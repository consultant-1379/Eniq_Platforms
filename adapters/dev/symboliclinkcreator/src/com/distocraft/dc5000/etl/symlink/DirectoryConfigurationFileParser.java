package com.distocraft.dc5000.etl.symlink;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class DirectoryConfigurationFileParser extends DefaultHandler {

    /**
     * Default value of eniq.xml file in the server
     * /eniq/sw/conf
     */
    private static final String ENIQ_DIRECTORY_CONFIGURATION_XML = "/eniq/sw/conf/eniq.xml";

    /**
     * Singleton instance of this class
     */
    private static DirectoryConfigurationFileParser instance = null;

    /**
     * An object which represents all details from the xml file
     * for a single neType
     */
    private SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration = null;

    /**
     * This entire map represents the whole XML file.
     * A map where NeType is the key and SymbolLinkSubDirConfiguration for that
     * NeType/configurationName as the value for easy and fast retrieval
     */
    static Map<String, SymbolicLinkSubDirConfiguration> symbolicLinkSubDirConfigurations = new HashMap<String, SymbolicLinkSubDirConfiguration>();

    /**
     * To hold the current XML tag @SupportedTags object
     */
    private SupportedTags currentTag = SupportedTags.noValue;

    /**
     * Value of the current tag
     */
    private String currentTagValue = "";

    /**
     * Supported XML tags by this parser
     */
    private enum SupportedTags {
        Interface, neType, maxNumLinks, nodeTypeDir, subdir, noValue;

        public static SupportedTags getTag(final String str) {
            try {
                return valueOf(str);
            } catch (final IllegalArgumentException e) {
            	System.out.println("SupportedTags  :: "  + e);
                return noValue;
            }
        }
    }

    /**
     * Used to indicated how Exceptions should be logged 
     */
    private enum ErrorSeverity {
        error, warning
    };

    /**
     * Obtain an instance to this singleton.
     * @return SymLinkDataSAXParser an instance to this singleton
     */
    public static DirectoryConfigurationFileParser getInstance() {
        if (instance == null) {
            instance = new DirectoryConfigurationFileParser();
        }
        return instance;
    }

    /**
     * Default constructor which parses the eniq.xml file
     */
    protected DirectoryConfigurationFileParser() {
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(new File(ENIQ_DIRECTORY_CONFIGURATION_XML), this);
        } catch (final FileNotFoundException e) {
        	System.out.println(e + "\n" + "DirectoryConfigurationFileParser  : " +e.getMessage());
            handleException(e, ErrorSeverity.warning,
                    ".ENIQ-M might not be installed on the server, ENIQ-S will not create the symbolic links");
        } catch (final SAXException e) {
        	System.out.println("DirectoryConfigurationFileParser  : " +e);
            handleException(e, ErrorSeverity.error, e.getMessage());
        } catch (final IOException e) {
        	System.out.println("DirectoryConfigurationFileParser  : " +e);
            handleException(e, ErrorSeverity.error, e.getMessage());
        } catch (final ParserConfigurationException e) {
        	System.out.println("DirectoryConfigurationFileParser  : " +e);
            handleException(e, ErrorSeverity.error, e.getMessage());
        } catch (final FactoryConfigurationError e) {
        	System.out.println("DirectoryConfigurationFileParser  : " +e);
            handleException(e, ErrorSeverity.error, e.getMessage());
        }
    }

    /**
     * Return the map of @SymbolicLinkSubDirConfiguration stored
     * per node type which is populated from the eniq.xml file
     * 
     * @return - the symbolicLinkSubDirConfigurations
     */
    public Map<String, SymbolicLinkSubDirConfiguration> getSymbolicLinkSubDirConfigurations() {
        return symbolicLinkSubDirConfigurations;
    }

    /**
     * Called when starting to parse an element. If the element is an
     * interface, i.e. the main object being parsed, then create a new instance
     * and add it to the list.
     * 
     * @param - qName String the XML tag for this element
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes inAttributes) {
        currentTag = SupportedTags.getTag(qName);
        if (currentTag.equals(SupportedTags.Interface)) {
            // data for new NeType
            symbolicLinkSubDirConfiguration = new SymbolicLinkSubDirConfiguration();
        }
    }

    /**
     * Called when finished parsing an element, i.e. the value of the element was
     * read by the characters() method. Set the appropriate attribute in the holder
     * to the value read by the characters() method.
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String namespaceURL, final String lName, final String qName) throws SAXException {
        switch (currentTag) {
        case neType:
            symbolicLinkSubDirConfiguration.setName(currentTagValue);
            symbolicLinkSubDirConfigurations.put(currentTagValue, symbolicLinkSubDirConfiguration);
            break;
        case maxNumLinks:
            try {
                symbolicLinkSubDirConfiguration.setMaxNumLinks(Integer.valueOf(currentTagValue));
            } catch (final NumberFormatException en) {
            	System.out.println("endElement   ::: " + en);
                //ignore it, for some ne types there is no max limit
            }
            break;
        case nodeTypeDir:
            symbolicLinkSubDirConfiguration.setNodeTypeDir(currentTagValue);
            break;
        case subdir:
            symbolicLinkSubDirConfiguration.addSubDir(currentTagValue);
            break;
        default:
            break;
        }
        currentTag = SupportedTags.noValue;
    }

    /**
     * Read the value from the XML file. This value is then set in the holder object
     * by the endElement() method.
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char buf[], final int offset, final int len) {
        currentTagValue = new String(buf, offset, len);
    }

    /**
     * Finished parsing the XML.
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() {
        symbolicLinkSubDirConfiguration = null;
    }

    /**
     * Handles the exceptions in this class
     * 
     * @param e - @Throwable object
     * @param type - "error" in case of major error
     * @param message - error message to be logged
     */
    private void handleException(final Throwable e, final ErrorSeverity errorSeverity, final String message) {
        if (errorSeverity.equals(ErrorSeverity.error)) {
        } else {
            System.out.println(e.getMessage() + message);
        }
        symbolicLinkSubDirConfigurations.clear();
        symbolicLinkSubDirConfiguration = null;
        currentTagValue = "";
        currentTag = SupportedTags.noValue;
    }
}
