package com.ericsson.sim.engine.model;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.xml.sax.Attributes;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.constants.SIMEnvironment;

import com.ericsson.sim.engine.scheduler.RopIntervalScheduler;

import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.properties.RuntimeProperties;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

@RunWith(JMock.class)
public class TopologyHandlerTest {

	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString()
			+ "/test/";
	
	

	private TopologyHandler topHandler;
	private RopIntervalScheduler ropIntervalScheduler;

	Mockery context = new Mockery() {
		{ 
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private Attributes attributes = context.mock(Attributes.class);

	@Before
	public void setUp() throws Exception {
		ropIntervalScheduler = RopIntervalScheduler.getInstance();
		topHandler = new TopologyHandler(ropIntervalScheduler);
	}

	@After
	public void tearDown() throws Exception {
		topHandler.node = null; 
		topHandler.tagName = "";

		RopIntervalPool.getInstance().removeInterval(3373707);
		
		
		//ropIntervalScheduler.exit();
		
		

		SIMEnvironment.TOPOLOGYCONFIG = System.getProperty("TopologyConfig",
				SIMEnvironment.CONFIGPATH + "/topology.xml");
		
		System.out.println(SIMEnvironment.TOPOLOGYCONFIG);
		

	}

	@Test
	public void testStartElementWithNodeNotInPool() {

		context.checking(new Expectations() {
			{
				allowing(attributes).getValue("name");
				will(returnValue("name"));
				oneOf(attributes).getValue("IPAddress");
				will(returnValue("IPAddress"));
				oneOf(attributes).getValue("uniqueID");
				will(returnValue("uniqueID"));
			}
		});

		topHandler.startElement("", "", "node", attributes); 
	}

	@Test
	public void testStartElementWithNodeInPool() {

		Node node = new Node();
		node.setName("name");
		node.addProperty("IPAddress", "IPAddress");
		node.addProperty("uniqueID", "uniqueID");
		NodePool.getInstance().addNode(node);

		context.checking(new Expectations() {
			{
				exactly(1).of(attributes).getValue("name");
				will(returnValue("name"));
				oneOf(attributes).getValue("IPAddress");
				will(returnValue("IPAddress"));
				oneOf(attributes).getValue("uniqueID");
				will(returnValue("uniqueID"));

			}
		});

		topHandler.startElement("", "", "node", attributes);
		assertEquals(node.getID(), topHandler.node.getID());
	}

	@Test
	public void testStartElementWithNodeNameIncorrect() {

		topHandler.startElement("", "", "incorrect", attributes);
		assertEquals("incorrect", topHandler.tagName);
	}

	@Test
	public void testEndElement() {

		topHandler.node = new Node();
		topHandler.node.setName("name");
		topHandler.node.addProperty("IPAddress", "IPAddress");
		topHandler.node.addProperty("uniqueID", "uniqueID");

		Protocol protocol = new Protocol();
		protocol.setName("name");
		protocol.generateID();
		protocol.addInterval(3373707);
		topHandler.node.addProtocol(protocol.getID());

		RopInterval rop = new RopInterval();
		rop.setName("name");
		rop.generateID();
		protocol.addInterval(rop.getID());

		RopIntervalPool.getInstance().addInterval(rop);
		ProtocolPool.getInstance().addProtocol(protocol);

		topHandler.endElement("", "", "node");

		assertEquals("", topHandler.tagName);

	}

	@Test
	public void testCharacters() {
		topHandler.node = new Node();
		topHandler.tagName = "ProtocolsNot";
		char[] array = new char[1];
		array[0] = 'A';
		topHandler.accumulator = new StringBuffer();
		topHandler.characters(array, 0, 1);
		
		assertEquals('A', topHandler.accumulator.charAt(0));
		
	}

	@Test
	public void testCharactersTagnameProtocols() {
		topHandler.node = new Node();
		topHandler.tagName = "Protocols";
		char[] array = new char[4];
		array[0] = 'n';
		array[1] = 'a';
		array[2] = 'm';
		array[3] = 'e';

		SFTPproperties protocol = new SFTPproperties();
		protocol.setName("name");
		ProtocolPool.getInstance().addProtocol(protocol);
		topHandler.accumulator = new StringBuffer();
		topHandler.characters(array, 0, 4);
		topHandler.addProperty("Protocols", "name");
		String[] s = new String[1];
		s[0] = "name";
		topHandler.addProtocol(s);

		boolean result = false;
		for (Integer i : topHandler.node.getProtocols()) {
			if (i == "name".hashCode()) {
				result = true;
				break;
			}
		}
		assertTrue(result);
		
	}

	@Test
	public void testEndDocument() {
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
				"3");
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "2");

		NodePool.getInstance().addNode(new Node());
		topHandler.endDocument();
	}
	
	//@Test(expected = Exception.class)
	//public void testStartUpThrowsException() throws Exception {
	//	SIMEnvironment.TOPOLOGYCONFIG = "test/intervals.simc";
	//	topHandler.startup();
	//}

	@Test
	public void testStartUp() throws Exception {
		SIMEnvironment.TOPOLOGYCONFIG = FILE_PATH + "topology.xml";
		
		System.out.println("testStartUp: "+SIMEnvironment.TOPOLOGYCONFIG);
		System.out.println("Should not throw exception");
		
		try {
			topHandler.startup();
		} catch (Exception e) {
			fail("Exception thrown i.e. test did not pass");
		}
		
	}

	

	@Test
	public void testGetName() {
		assertEquals("com.ericsson.sim.engine.model.TopologyHandler",
				topHandler.getName());
		assertNotNull(topHandler.getName());
		assertNotSame("com.ericsson.sim.engine.model.TopologyHandlerNotHere",
				topHandler.getName());
	}
}
