package com.ericsson.eniq.glassfish;

/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * User: eeipca Date: 03/01/12 Time: 13:57
 */
public class DomainParserDOM {

    private static final String XPATH_JCONFIG = "//domain/configs/config[@name=\"server-config\"]/java-config";

    private static final String XPATH_KVM_PROFILER = XPATH_JCONFIG + "/profiler";

    private static final String XPATH_JVMOPTS = XPATH_JCONFIG + "/jvm-options";

    private static final String XPATH_EVENTS_CUSTOM_RESOURCE_PROPERTY = "//domain/resources/custom-resource[@res-type=\"java.util.Properties\"][@jndi-name=\"Eniq_Event_Properties\"][@factory-class=\"org.glassfish.resources.custom.factory.PropertiesFactory\"]/property";
    
    private static final String XPATH_KPI_CUSTOM_RESOURCE_PROPERTY = "//domain/resources/custom-resource[@res-type=\"java.util.Properties\"][@jndi-name=\"Kpi_Service_Properties\"][@factory-class=\"org.glassfish.resources.custom.factory.PropertiesFactory\"]/property";

    private static final List<String> VM_TYPES = Arrays.asList("-server", "-client", "-hotspot");

    private static final String ARG_MARKER = "--";

    private static final String ARG_DOMAIN_NAME = ARG_MARKER + "d";

    private static final String ARG_GF_BASE = ARG_MARKER + "b";

    private static final String ARG_UPDATE_JVM_OPTIONS = ARG_MARKER + "o";

    private static final String ARG_LIST_JVM_OPTIONS = ARG_MARKER + "l";

    private static final String ARG_COPY_CUSTOM_RESOURCES = ARG_MARKER + "p";

    private static final String ENIQ_EVENTS_ONE_MINUTE_AGGREGATION = "ENIQ_EVENTS_ONE_MINUTE_AGGREGATION";

    private static final String ENIQ_EVENTS_UI_VERSION = "ENIQ_EVENTS_UI_VERSION";
    
    private static final String ENIQ_EVENTS_UI_COPYRIGHT = "ENIQ_EVENTS_UI_COPYRIGHT";

    private static final String ENIQ_EVENTS_DEFAULT_DATA_SOURCE = "ENIQ_EVENTS_DEFAULT_DATA_SOURCE";
    
    private static final String ENIQ_EVENTS_ADDITIONAL_DATA_SOURCES = "ENIQ_EVENTS_ADDITIONAL_DATA_SOURCES";
    
    private static final String ENIQ_EVENTS_EXPORT_CSV_DATA_SOURCE = "ENIQ_EVENTS_EXPORT_CSV_DATA_SOURCE";

    private static final String MAXIMUM_QUEUE_LENGTH = "MAXIMUM.QUEUE.LENGTH";
	
	private static final String EVENT_SGEH_DIRECTORY_VERSION = "EVENT.E.SGEH.DIRECTORY.VERSION";
	
	private static final String EVENT_LTE_DIRECTORY_VERSION = "EVENT.E.LTE.DIRECTORY.VERSION";

    private final static Map<String, String> jndiParametersExclusion = new HashMap<String, String>();

    static {
    	jndiParametersExclusion.put(ENIQ_EVENTS_ONE_MINUTE_AGGREGATION,"");
    	jndiParametersExclusion.put(ENIQ_EVENTS_UI_VERSION,"");
    	jndiParametersExclusion.put(ENIQ_EVENTS_UI_COPYRIGHT,"");
    	jndiParametersExclusion.put(ENIQ_EVENTS_DEFAULT_DATA_SOURCE,"");
    	jndiParametersExclusion.put(ENIQ_EVENTS_ADDITIONAL_DATA_SOURCES,"");
    	jndiParametersExclusion.put(ENIQ_EVENTS_EXPORT_CSV_DATA_SOURCE,"");    	
    	jndiParametersExclusion.put(MAXIMUM_QUEUE_LENGTH,"");
		jndiParametersExclusion.put(EVENT_SGEH_DIRECTORY_VERSION,"");  
		jndiParametersExclusion.put(EVENT_LTE_DIRECTORY_VERSION,"");  
    	}

    public static void main(final String[] args) {
        // --b C:\eniq\glassfish\glassfishv3\glassfish --d domain1 --o -d64
        // -server -Xms512m -Xmx1024M -XX:MaxPermSize=256m
        final DomainParserDOM parser = new DomainParserDOM();
        try {
            parser.execute(args);
        } catch (final DomainParserException e) {
            e.printStackTrace();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    public void execute(final String[] args) throws DomainParserException {
        if (args == null || args.length == 0) {
            throw new DomainParserException("No Arguements!");
        }
        String domainName = getArgValue(ARG_DOMAIN_NAME, args);
        if (domainName == null) {
            domainName = "domain1";
        }

        final String gfHome = getArgValue(ARG_GF_BASE, args);// e.g.
                                                             // /eniq/glassfish/glassfishv3/glassfish/
        if (gfHome == null) {
            throw new DomainParserException("Glassfish Home not specified (" + ARG_GF_BASE + ")");
        }

        final File domainXml = new File(gfHome, "domains/" + domainName + "/config/domain.xml");
        if (!domainXml.exists()) {
            throw new DomainParserException("No domain file found in " + domainXml.getPath());
        }

        final DomainParserDOM parser = new DomainParserDOM();

        if (getArgIndex(ARG_LIST_JVM_OPTIONS, args) >= 0) {
            final String[] options = parser.listJvmOptions(domainXml);
            for (final String s : options) {
                System.out.printf("%s\n", s);
            }
        } else if (getArgIndex(ARG_UPDATE_JVM_OPTIONS, args) >= 0) {
            final int startPosition = getArgIndex(ARG_UPDATE_JVM_OPTIONS, args);
            if (startPosition < 0) {
                throw new DomainParserException("No VM options flag " + ARG_UPDATE_JVM_OPTIONS);
            }
            final List<String> jvmargs = new ArrayList<String>();
            jvmargs.addAll(Arrays.asList(args).subList(startPosition + 1, args.length));
            parser.updateJvmOptions(domainXml, jvmargs.toArray(new String[jvmargs.size()]));
        } else if (getArgIndex(ARG_COPY_CUSTOM_RESOURCES, args) > 0) {
            System.out.println("JNDI Backup : " + args[5]);
            System.out.println("JNDI Backup : " + args[4]);
            final String backedUpDomainFileName = getArgValue(ARG_COPY_CUSTOM_RESOURCES, args);
            if (backedUpDomainFileName == null) {
                throw new DomainParserException("No backup domain file supplied.");
            }
            final File backedUpDomainFile = new File(backedUpDomainFileName);
            copyCustomResources(backedUpDomainFile, domainXml);
        }
    }

    private String[] listJvmOptions(final File domainFile) throws DomainParserException {
        final Document doc = parseDomainFile(domainFile);
        final Map<String, Object> jvmOptions = getJvmOptions(doc);
        return listJvmOptions(jvmOptions);
    }

    private void updateJvmOptions(final File domainFile, final String[] newOptions) {
        final Document doc = parseDomainFile(domainFile);
        final RecordedHashMap jvmOptions = getJvmOptions(doc);

        // Get the current VM type
        String originalVmType = null;
        for (final String key : jvmOptions.keySet()) {
            if (VM_TYPES.contains(key)) {
                originalVmType = key;
            }
        }

        if (originalVmType != null) {
            deleteJvmOption(originalVmType, jvmOptions);
        }
        // Delete any current Heap settings
        deleteJvmOption("-Xmx.*", jvmOptions);
        deleteJvmOption("-Xms.*", jvmOptions);
        deleteJvmOption("-d(32|64)", jvmOptions);

        for (final String jvmOption : newOptions) {
            final int splitIndex = jvmOption.indexOf('=');
            if (splitIndex < 0) {
                setJvmOption(jvmOption, jvmOptions);
            } else {
                setJvmOption(jvmOption.substring(0, splitIndex), jvmOption.substring(splitIndex + 1), jvmOptions);
            }
        }

        // Check the updated jvm option list to see if a VM type is set
        boolean vmTypeSet = false;
        for (final String key : jvmOptions.keySet()) {
            if (VM_TYPES.contains(key)) {
                vmTypeSet = true;
                break;
            }
        }

        // If no VM type is set and the original JVM settings had a VM type,
        // reset it.
        if (!vmTypeSet && originalVmType != null) {
            setJvmOption(originalVmType, jvmOptions);
        }

        if (!jvmOptions.hasChanges()) {
            System.out.printf("No changes needed.\n");
            return;
        } else {
            for (final String change : jvmOptions.getChanges()) {
                System.out.printf("%s\n", change);
            }
        }

        final NodeList profilerNodes = getNodes(doc, XPATH_KVM_PROFILER);
        final NodeList configNodes = getNodes(doc, XPATH_JCONFIG);
        for (int i = 0; i < configNodes.getLength(); i++) {
            final Node javaConfig = configNodes.item(i);
            while (javaConfig.hasChildNodes()) {
                final Node n = javaConfig.getFirstChild();
                javaConfig.removeChild(n);
            }
        }

        final String INDENATION = "\n\t\t";
        final Node parent = configNodes.item(0);
        for (final String nodeName : jvmOptions.keySet()) {
            final Element jvm_options = doc.createElement("jvm-options");
            String textContent = nodeName;
            final Object propValue = jvmOptions.get(nodeName);

            if (propValue != null) {
                textContent += "=" + propValue;
            }
            jvm_options.setTextContent(textContent);
            parent.appendChild(doc.createTextNode(INDENATION)); // formatting
                                                                // only!!
            parent.appendChild(jvm_options);
        }
        if (profilerNodes.getLength() > 0) {
            for (int i = 0; i < profilerNodes.getLength(); i++) {
                parent.appendChild(doc.createTextNode(INDENATION));
                parent.appendChild(profilerNodes.item(i));
                parent.appendChild(doc.createTextNode(INDENATION));
            }
        }
        rewriteDomainFile(domainFile, doc);
    }

    private void copyCustomResources(final File backedUpDomainFile, final File domainFile) {
        if (!backedUpDomainFile.exists()) {
            return;
        }

        // Get custom resources from backed up domain.xml
        final Document originalDoc = parseDomainFile(backedUpDomainFile);
        final RecordedHashMap originalCustomResources = getCustomResources(originalDoc);

        // Overwrite custom resource properties with original values
        final Document doc = parseDomainFile(domainFile);
        overrideProperties(originalCustomResources, doc, XPATH_EVENTS_CUSTOM_RESOURCE_PROPERTY);
        overrideProperties(originalCustomResources, doc, XPATH_KPI_CUSTOM_RESOURCE_PROPERTY);

        rewriteDomainFile(domainFile, doc);
    }
    
    private void overrideProperties(final RecordedHashMap originalCustomResources, final Document doc , final String xPathToCustomResourceProperty){
        final NodeList customResources = getNodes(doc, xPathToCustomResourceProperty);
        for (int i = 0; i < customResources.getLength(); i++) {
            final Node property = customResources.item(i);
            final String key = property.getAttributes().getNamedItem("name").getNodeValue();
            if (originalCustomResources.containsKey(key)) {
                final String value = (String) originalCustomResources.get(key);
                //Do not retain old value for JNDI property ENIQ_EVENTS_ONE_MINUTE_AGGREGATION OR ENIQ_EVENTS_UI_VERSION
                if (!jndiParametersExclusion.containsKey(key)) {
                    property.getAttributes().getNamedItem("value").setNodeValue(value);
                }
            }
        }
    }

    private void rewriteDomainFile(final File domainFile, final Document updatedModel) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_ww'T'kk_mm_ss");
        final File backupFile = new File(domainFile.getParentFile(), domainFile.getName() + "_"
                + dateFormat.format(new Date()));
        try {
            if (!backupFile.createNewFile()) {
                throw new DomainParserException("Failed(1) to create backup file " + backupFile.getPath());
            }
        } catch (final IOException e) {
            throw new DomainParserException("Failed(2) to create backup file " + backupFile.getPath(), e);
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            try {
                source = new FileInputStream(domainFile).getChannel();
                destination = new FileOutputStream(backupFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
            final FileOutputStream fileWriter = new FileOutputStream(domainFile, false);
            try {
                updatedModel.setTextContent("NOTHING");
                writeDomToFile(updatedModel, new PrintWriter(fileWriter));
            } finally {
                fileWriter.close();
            }
        } catch (final IOException e) {
            throw new DomainParserException("Failed to write backup file " + backupFile.getPath(), e);
        }
    }

    private void writeDomToFile(final Document doc, final PrintWriter out) {
        final TransformerFactory transfac = TransformerFactory.newInstance();
        final Transformer trans;
        try {
            trans = transfac.newTransformer();
        } catch (final TransformerConfigurationException e) {
            throw new DomainParserException("Failed to create Transformer?!?", e);
        }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        final StreamResult result = new StreamResult(out);
        final DOMSource source = new DOMSource(doc.getFirstChild());
        try {
            trans.transform(source, result);
        } catch (final TransformerException e) {
            throw new DomainParserException("Failed to transform out DOM document?!?", e);
        }
    }

    private void setJvmOption(final String name, final Map<String, Object> jvmOptions) {
        setJvmOption(name, null, jvmOptions);
    }

    /**
     * Add a VM arg to the arg map.
     * 
     * @param optName
     *            The JVM option name e.g. -Dabc
     * @param value
     *            The JVM option value e.g. 123
     * @param jvmOptions
     *            List of current JVM options
     */
    private void setJvmOption(final String optName, final String value, final Map<String, Object> jvmOptions) {
        jvmOptions.put(optName, value);
    }

    /**
     * Delete an option from the map
     * 
     * @param optName
     *            The JVM option name e.g. -Dabc
     * @param jvmOptions
     *            List of current JVM options
     */
    private void deleteJvmOption(final String optName, final Map<String, Object> jvmOptions) {
        final Iterator<Map.Entry<String, Object>> mapIterator = jvmOptions.entrySet().iterator();
        while (mapIterator.hasNext()) {
            final Map.Entry<String, Object> entry = mapIterator.next();
            if (entry.getKey().matches(optName)) {
                mapIterator.remove();
                entry.getValue();
                break;
            }
        }
    }

    private Document parseDomainFile(final File domainFile) {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);
        final Document doc;
        try {
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(domainFile);
        } catch (final ParserConfigurationException e) {
            throw new DomainParserException("Failed to parse file " + domainFile.getPath(), e);
        } catch (final SAXException e) {
            throw new DomainParserException("Failed to parse file " + domainFile.getPath(), e);
        } catch (final IOException e) {
            throw new DomainParserException("Failed to parse file " + domainFile.getPath(), e);
        }
        doc.getDocumentElement().normalize();
        return doc;
    }

    private RecordedHashMap getJvmOptions(final Document doc) {
        final NodeList nodes = getNodes(doc, XPATH_JVMOPTS);
        final RecordedHashMap options = new RecordedHashMap();
        for (int i = 0; i < nodes.getLength(); i++) {
            final String jvmOption = nodes.item(i).getTextContent().trim();
            final String[] tokens = jvmOption.split("=");
            final String name = tokens[0];
            String value = null;
            if (tokens.length == 2) {
                value = tokens[1];
            }
            options.put(name, value);
        }
        options.clearChanges();
        return options;
    }

    private RecordedHashMap getCustomResources(final Document doc) {
        final RecordedHashMap properties = new RecordedHashMap();
        setProperties(properties, getNodes(doc, XPATH_EVENTS_CUSTOM_RESOURCE_PROPERTY));
        setProperties(properties, getNodes(doc, XPATH_KPI_CUSTOM_RESOURCE_PROPERTY));
        properties.clearChanges();
        return properties;
    }
    
    private void setProperties(final RecordedHashMap properties, final NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            final NamedNodeMap attributes = nodes.item(i).getAttributes();
            final Node nameAttribute = attributes.getNamedItem("name");
            final String name = nameAttribute.getTextContent();
            final Node valueAttribute = attributes.getNamedItem("value");
            final String value = valueAttribute.getTextContent();
            properties.put(name, value);
        }

    }

    private NodeList getNodes(final Document doc, final String xPath) {
        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xpath = xPathFactory.newXPath();
        try {
            final XPathExpression expr = xpath.compile(xPath);
            final Object result = expr.evaluate(doc, XPathConstants.NODESET);
            return (NodeList) result;
        } catch (final XPathExpressionException e) {
            throw new DomainParserException("Error compiling XPATH[" + xPath + "]", e);
        }
    }

    private String[] listJvmOptions(final Map<String, Object> jvmOptions) {
        final List<String> sets = new ArrayList<String>();
        for (final String name : jvmOptions.keySet()) {
            final Object value = jvmOptions.get(name);
            if (value == null) {
                sets.add(name);
            } else {
                sets.add(name + " -> " + value);
            }
        }
        return sets.toArray(new String[sets.size()]);
    }

    private String getArgValue(final String argName, final String[] args) {
        final int index = getArgIndex(argName, args);
        if (index >= 0) {
            return getArgValue(index, args);
        }
        return null;
    }

    private int getArgIndex(final String argName, final String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (argName.equalsIgnoreCase(args[i])) {
                return i;
            }
        }
        return -1;
    }

    private String getArgValue(final int argIndex, final String[] args) {
        if (argIndex + 1 < args.length && !args[argIndex + 1].startsWith(ARG_MARKER)) {
            return args[argIndex + 1];
        }
        return null;
    }

}
