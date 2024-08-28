package com.ericsson.eniq.glassfish;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomainParserHTTPS {

	private static final String LISTENER2_CERT_NODE = "//domain/configs/config[@name=\"server-config\"]/network-config/protocols/protocol[@name=\"http-listener-2\"]/ssl";
	private static final String ATTR_CERT="cert-nickname";

	private static final String LISTENER1_NETWORK_NODE = "//domain/configs/config[@name=\"server-config\"]/network-config/network-listeners/network-listener[@name=\"http-listener-1\"]";
	private static final String LISTENER2_NETWORK_NODE = "//domain/configs/config[@name=\"server-config\"]/network-config/network-listeners/network-listener[@name=\"http-listener-2\"]";
	private static final String ATTR_PROTOCOL="protocol";

	private static final Object HTTP_TO_HTTPS_REDIRECT = "pu-protocol-HTTP-HTTPS";

	private static final String HTTP_HTTPS_PROTOCOL_NODE = "//domain/configs/config[@name=\"server-config\"]/network-config/protocols/protocol[@name=\"" + HTTP_TO_HTTPS_REDIRECT + "\"]";

	private static String httpsFlagDirectory = "/eniq/home/dcuser/glassfish_domain_config_backup";
	private static final String HTTPS_SETUP_FILE_NAME = "/https_setup";
	private static final String HTTPS_ENABLED_FILE_NAME = "/https_enabled";
	private static final int HTTPS_CHECK_SUCCESS = 0;
	private static final int HTTPS_CHECK_FAIL = 1;

	private File domFile;
	private Document doc;
	
	private String certNickname;
	private String listener1ProtocolName = "";
	private String listener2ProtocolName = "";
	private boolean httpsSetup = false;
	private boolean httpsEnabled = false;
	
	public static void main(String[] args) {

		DomainParserHTTPS.setHttpsFlagDirectory(args[1]);
		DomainParserHTTPS glassfishDomain = new DomainParserHTTPS(args[0]);

		try {
			glassfishDomain.checkDomain();
		} catch (IOException e) {
			throw new DomainParserException("DomainParserHTTPS failed with error: "+e.getMessage(), e);
		}
	}

	public DomainParserHTTPS(String domainPath) {
		domFile = new File(domainPath);
		doc = parseDomainFile(domFile);
	}
	
	void checkDomain() throws IOException {
		setHttpsSetup(nodeExists(HTTP_HTTPS_PROTOCOL_NODE));

		if (isHttpsSetup()) {
			setCertNickname(getAttribute(LISTENER2_CERT_NODE, ATTR_CERT));
			setListener1ProtocolName(getAttribute(LISTENER1_NETWORK_NODE, ATTR_PROTOCOL));
			setListener2ProtocolName(getAttribute(LISTENER2_NETWORK_NODE, ATTR_PROTOCOL));
			
			if (listener1ProtocolName.equals(HTTP_TO_HTTPS_REDIRECT)) {
				setHttpsEnabled(true);
			} else {
				setHttpsEnabled(false);
			}
		} else {
			setHttpsEnabled(false);
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

	private String getAttribute(String xpath, String attribute) {
		Node node = getNodes(xpath).item(0);
		return node.getAttributes().getNamedItem(attribute).getNodeValue();
	}
	
	boolean nodeExists(String xpath) {
		if (getNodes(xpath).getLength()>0) {
			return true;
		} else {
			return false;
		}
	}

    NodeList getNodes(final String xPath) {
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

    private void checkHttpsFlagDirectory() throws IOException {
    	File dir = new File(httpsFlagDirectory);
    	if (!dir.exists()) {
    		dir.mkdirs();
    	} else {
    		if (!dir.isDirectory()) {
    			throw new IOException(httpsFlagDirectory + " is not a directory.");
    		}
    	}
    }

	private void createFlagFile(File flagFile) throws IOException {
		checkHttpsFlagDirectory();
		flagFile.createNewFile();
	}

	private void deleteFlagFile(File flagFile) throws IOException {
		if (flagFile.exists()) {
			if (flagFile.isFile()) {
				flagFile.delete();
			} else {
				throw new IOException(flagFile + " is not a file.");
			}
		}
	}
	
	public boolean isHttpsSetup() {
		return httpsSetup;
	}

	private void setHttpsSetup(boolean httpsSetup) throws IOException {
		this.httpsSetup = httpsSetup;
		
		// HTTPS_SETUP_FILE is used as a flag file to indicate that HTTPS has been set up
		File setupFile = new File(getHttpsSetupFileName());
		if (httpsSetup) {
			System.out.println("HTTPS is set up.");
			createFlagFile(setupFile);
		} else {
			System.out.println("HTTPS is not set up.");
			deleteFlagFile(setupFile);
		}
	}

	public boolean isHttpsEnabled() {
		return httpsEnabled;
	}

	private void setHttpsEnabled(boolean httpsEnabled) throws IOException {
		this.httpsEnabled = httpsEnabled;
		
		// HTTPS_ENABLED_FILE is used as a flag file to indicate that HTTPS is enabled
		File enabledFile = new File(getHttpsEnabledFileName());
		if (httpsEnabled) {
			System.out.println("HTTPS is enabled.");
			createFlagFile(enabledFile);
		} else {
			System.out.println("HTTPS is not enabled.");
			deleteFlagFile(enabledFile);
		}
	}

	public static String getHttpsFlagDirectory() {
		return httpsFlagDirectory;
	}

	public static void setHttpsFlagDirectory(String httpsFlagDirectory) {
		DomainParserHTTPS.httpsFlagDirectory = httpsFlagDirectory;
	}

	private static String getHttpsSetupFileName() {
		return getHttpsFlagDirectory() + HTTPS_SETUP_FILE_NAME;
	}

	private static String getHttpsEnabledFileName() {
		return getHttpsFlagDirectory() + HTTPS_ENABLED_FILE_NAME;
	}

	public String getCertNickname() {
		return certNickname;
	}

	private void setCertNickname(String certNickname) {
		this.certNickname = certNickname;
	}

	public String getListener1ProtocolName() {
		return listener1ProtocolName;
	}

	private void setListener1ProtocolName(String listener1Protocol) {
		this.listener1ProtocolName = listener1Protocol;
	}

	public String getListener2ProtocolName() {
		return listener2ProtocolName;
	}

	private void setListener2ProtocolName(String listener2Protocol) {
		this.listener2ProtocolName = listener2Protocol;
	}
}
