package com.ericsson.eniq.glassfish;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomainParserHTTPSTest {


	private static final String BAD_DOMAIN_XML = "test/xml/domain-bad.xml";
	private static final String BASIC_DOMAIN_XML = "test/xml/domain-1.xml";
	private static final String HTTP_DOMAIN_XML = "test/xml/domain-http.xml";
	private static final String HTTPS_DOMAIN_XML = "test/xml/domain-https.xml";

	private static final String PROTOCOLS_NODE = "//domain/configs/config/network-config/protocols/protocol";
	
	private static final String LISTENER2_CERT_NODE = "//domain/configs/config[@name=\"server-config\"]/network-config/protocols/protocol[@name=\"http-listener-2\"]/ssl";

	private static final Object HTTP_TO_HTTPS_REDIRECT = "pu-protocol-HTTP-HTTPS";

	private static final String HTTP_HTTPS_PROTOCOL_NODE = "//domain/configs/config[@name=\"server-config\"]/network-config/protocols/protocol[@name=\"" + HTTP_TO_HTTPS_REDIRECT + "\"]";
	
	private static final String BAD_NODE = "//thisNodeShouldNotExist";

	private String flagFileDirectory = "H:\\Documents\\glassfish";
	private String badFlagFileDirectory = "BAD:\\Documents\\glassfish";
	private static final String HTTPS_SETUP_FILE_NAME = "/https_setup";
	private static final String HTTPS_ENABLED_FILE_NAME = "/https_enabled";
	
	DomainParserHTTPS testDomHTTPS;
	DomainParserHTTPS testDomHTTP;
	DomainParserHTTPS testDomNone;

	@Before
	public void setUp() throws Exception {
		DomainParserHTTPS.setHttpsFlagDirectory(flagFileDirectory);
		testDomHTTPS = new DomainParserHTTPS(HTTPS_DOMAIN_XML);
		testDomHTTP = new DomainParserHTTPS(HTTP_DOMAIN_XML);
		testDomNone = new DomainParserHTTPS(BASIC_DOMAIN_XML);
	}

	@Test
	public void testNodeExists() {
		boolean httpRedirectNodeExists = testDomHTTPS.nodeExists(HTTP_HTTPS_PROTOCOL_NODE);
		assertTrue("HTTP-HTTPS redirect protocol wasn't found.", httpRedirectNodeExists);

		boolean badNodeExists = testDomHTTPS.nodeExists(BAD_NODE);
		assertFalse("The node " + BAD_NODE + " was found in the test document when it doesn't exist.", badNodeExists);
	}

	@Test(expected = NullPointerException.class)
	public void testNodeExistsNull() {
		boolean nullNodeExists = testDomHTTPS.nodeExists(null);
	}
	
	@Test
	public void testGetNodes() {
		NodeList testNodeList = testDomHTTPS.getNodes(LISTENER2_CERT_NODE);
		assertEquals(1, testNodeList.getLength());

		Node testNode = testNodeList.item(0);
		assertEquals("ssl", testNode.getNodeName());

		NodeList protocolsNodeList = testDomHTTPS.getNodes(PROTOCOLS_NODE);
		assertEquals(16, protocolsNodeList.getLength());

		NodeList badNodeList = testDomHTTPS.getNodes(BAD_NODE);
		assertEquals(0, badNodeList.getLength());
	}

	@Test(expected = NullPointerException.class)
	public void testGetNodesNull() {
		NodeList testNodeList = testDomHTTPS.getNodes(null);
	}
	
	@Test
	public void testCheckDomainHTTPS() {
		try {
			testDomHTTPS.checkDomain();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error running checkDomain(): " + e.getMessage());
		}
		
		assertEquals("eniq", testDomHTTPS.getCertNickname());
		assertEquals("pu-protocol-HTTP-HTTPS", testDomHTTPS.getListener1ProtocolName());
		assertEquals("http-listener-2", testDomHTTPS.getListener2ProtocolName());
		assertTrue(testDomHTTPS.isHttpsSetup());
		assertTrue(testDomHTTPS.isHttpsEnabled());
		
		assertTrue("File " + flagFileDirectory + HTTPS_SETUP_FILE_NAME + " does not exist.", flagFileExists(HTTPS_SETUP_FILE_NAME));
		assertTrue("File " + flagFileDirectory + HTTPS_ENABLED_FILE_NAME + " does not exist.", flagFileExists(HTTPS_ENABLED_FILE_NAME));
	}
	
	@Test
	public void testCheckDomainHTTP() {
		try {
			testDomHTTP.checkDomain();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error running checkDomain(): " + e.getMessage());
		}
		
		assertEquals("eniq", testDomHTTP.getCertNickname());
		assertEquals("http-listener-1", testDomHTTP.getListener1ProtocolName());
		assertEquals("pu-protocol-HTTPS-HTTP", testDomHTTP.getListener2ProtocolName());
		assertTrue(testDomHTTP.isHttpsSetup());
		assertFalse(testDomHTTP.isHttpsEnabled());

		assertTrue("File " + flagFileDirectory + HTTPS_SETUP_FILE_NAME + " does not exist.", flagFileExists(HTTPS_SETUP_FILE_NAME));
		assertFalse("File " + flagFileDirectory + HTTPS_ENABLED_FILE_NAME + " should not exist.", flagFileExists(HTTPS_ENABLED_FILE_NAME));
	}

	@Test
	public void testCheckDomainNone() {
		try {
			testDomNone.checkDomain();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Error running checkDomain(): " + e.getMessage());
		}
		
		assertNull(testDomNone.getCertNickname());
		assertEquals("", testDomHTTP.getListener1ProtocolName());
		assertEquals("", testDomHTTP.getListener2ProtocolName());
		assertFalse(testDomNone.isHttpsSetup());
		assertFalse(testDomNone.isHttpsEnabled());

		assertFalse("File " + flagFileDirectory + HTTPS_SETUP_FILE_NAME + " should not exist.", flagFileExists(HTTPS_SETUP_FILE_NAME));
		assertFalse("File " + flagFileDirectory + HTTPS_ENABLED_FILE_NAME + " should not exist.", flagFileExists(HTTPS_ENABLED_FILE_NAME));
	}

	@Test(expected = DomainParserException.class)
	public void testMainBadDomain() {
		String[] args = {BAD_DOMAIN_XML, flagFileDirectory};
		DomainParserHTTPS.main(args);
	}

	@Test(expected = DomainParserException.class)
	public void testMainBadFlagFileDir() {
		String[] args = {HTTP_DOMAIN_XML, badFlagFileDirectory};
		DomainParserHTTPS.main(args);
	}

	
	@Test
	public void testMainBasic() {
		String[] args = {BASIC_DOMAIN_XML, flagFileDirectory};
		DomainParserHTTPS.main(args);
		assertFalse("File " + flagFileDirectory + HTTPS_SETUP_FILE_NAME + " should not exist.", flagFileExists(HTTPS_SETUP_FILE_NAME));
		assertFalse("File " + flagFileDirectory + HTTPS_ENABLED_FILE_NAME + " should not exist.", flagFileExists(HTTPS_ENABLED_FILE_NAME));
	}

	@Test
	public void testMainHTTP() {
		String[] args = {HTTP_DOMAIN_XML, flagFileDirectory};
		DomainParserHTTPS.main(args);
		assertTrue("File " + flagFileDirectory + HTTPS_SETUP_FILE_NAME + " does not exist.", flagFileExists(HTTPS_SETUP_FILE_NAME));
		assertFalse("File " + flagFileDirectory + HTTPS_ENABLED_FILE_NAME + " should not exist.", flagFileExists(HTTPS_ENABLED_FILE_NAME));
	}
	
	@Test
	public void testMainHTTPS() {
		String[] args = {HTTPS_DOMAIN_XML, flagFileDirectory};
		DomainParserHTTPS.main(args);
		assertTrue("File " + flagFileDirectory + HTTPS_SETUP_FILE_NAME + " does not exist.", flagFileExists(HTTPS_SETUP_FILE_NAME));
		assertTrue("File " + flagFileDirectory + HTTPS_ENABLED_FILE_NAME + " does not exist.", flagFileExists(HTTPS_ENABLED_FILE_NAME));
	}

	public boolean flagFileExists(String fileName) {
		File checkFile = new File(flagFileDirectory + fileName);
		return checkFile.isFile();
	}

}
