package com.ericsson.sim.model.node;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

public class NodeTest {

	private Node node;
	private final String NAME = "name";
	private final String OTHER_NAME = "otherName";
	private final String ADDED_KEY = "addedKey";
	private final String ADDED_VALUE = "addedValue";
	
	@Before
	public void setUp() throws Exception {
		node = new Node();
	}

	@Test
	public void testGetName() {
		assertNull(node.getName());
	}

	@Test
	public void testSetAndGetName() {
		node.setName(NAME);
		assertEquals(NAME, node.getName());
		assertNotSame(OTHER_NAME, node.getName());
	}
	
	@Test
	public void testGetProperty() {
		assertNull(node.getProperty(ADDED_KEY));
		assertNotSame("Shouldnt be there", null, ADDED_VALUE);
	}
	
	@Test
	public void testAddProperty() {
		node.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals(ADDED_VALUE, node.getProperty(ADDED_KEY));
	}
	
	@Test
	public void testGetID() {
		assertEquals(0, node.getID());
	}
	
	@Test
	public void testGenerateID() {
		node.setName(NAME);
		node.addProperty("IPAddress", "1.2.3.");
		node.addProperty("uniqueID", "1");
		node.generateID();
		String id = node.getName() + ":" + node.getProperty("IPAddress") + ":" + node.getProperty("uniqueID");
		assertEquals(id.hashCode(), node.getID());
	}
	
	@Test
	public void testGetProtocols() {
		assertEquals(0, node.getProtocols().size());
		node.addProtocol(1);
		assertEquals(1, node.getProtocols().size());
		node.addProtocol(2);
		assertEquals(2, node.getProtocols().size());
		node.addProtocol(2);
		assertEquals("Should not add as already there",2, node.getProtocols().size());
	}
	
	@Test
	public void testUpdateFirstRop() {
		node.addProperty("firstRop", null);
		node.updateFirstRop(1);
		assertEquals(""+1, node.getProperty("firstRop"));
		node.updateFirstRop(2);
		assertEquals(""+1+","+2, node.getProperty("firstRop"));
	}
	
	
}







