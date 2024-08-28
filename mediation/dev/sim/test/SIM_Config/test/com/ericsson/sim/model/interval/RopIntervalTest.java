package com.ericsson.sim.model.interval;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.model.node.Node;

public class RopIntervalTest {
	
	private RopInterval interval;
	private Node node;
	private Node otherNode;
	private final String ADDED_KEY = "addedKey";
	private final String ADDED_VALUE = "addedValue";
	private final String NAME = "name";
	private final String OTHER_NAME = "error";
	

	@Before
	public void setUp() throws Exception {
		System.out.println("suyagddddddddddddddd");
		interval = new RopInterval();
		node = new Node();
		node.setName(NAME);
		node.addProperty("IPAddress", "1.2.3.");
		node.addProperty("uniqueID", "1");
		node.generateID();
	}
	
	@Test
	public void testSetAndGetName() {
		assertEquals(null, interval.getName());
		interval.setName(NAME);
		assertEquals(NAME, interval.getName());
		assertNotSame(OTHER_NAME, interval.getName());
	}
	
	@Test
	public void testGetProperty() {
		assertNull(interval.getProperty(ADDED_KEY));
		assertNotSame("Shouldnt be there", ADDED_VALUE, null);
	}
	
	@Test
	public void testAddProperty() {
		assertNull(interval.getProperty(ADDED_KEY));
		interval.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals(ADDED_VALUE, interval.getProperty(ADDED_KEY));
	}
	
	@Test
	public void testHasNode() {
		assertFalse(interval.hasNode(node.getID()));
	}
	
	@Test
	public void testaddNode() {
		interval.addNode(node.getID());
		assertTrue(interval.hasNode(node.getID()));
		otherNode = new Node();
		otherNode.setName(OTHER_NAME);
		otherNode.addProperty("IPAddress", "3.2.1");
		otherNode.addProperty("uniqueID", "2");
		otherNode.generateID();
		assertNotSame("Shouldnt be there", otherNode, null);
	}

	@Test
	public void testGetNodeIDs() {
		assertEquals(interval.getNodeIDs().size(), 0);
		interval.addNode(node.getID());
		assertEquals(interval.getNodeIDs().size(), 1);
		interval.addNode(node.getID());
		assertEquals("Should not add as already there",1, interval.getNodeIDs().size());
	}
	
	@Test
	public void testGetID() {
		assertEquals(interval.getID(), 0, 0);
	}
	
	@Test
	public void testGenerateID() {
		interval.setName(NAME);
		interval.generateID();
		assertEquals(interval.getID(), NAME.hashCode(), 0);
		assertNotSame(interval.getID(), OTHER_NAME.hashCode());
	}
	
	
	
}








