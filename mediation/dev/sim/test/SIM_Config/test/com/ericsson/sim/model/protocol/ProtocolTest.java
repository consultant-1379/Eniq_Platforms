package com.ericsson.sim.model.protocol;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ProtocolTest {
	
	private Protocol protocol;
	private final int intervalID = 5;
	private final int notIntervalID = 10;
	private final String name = "name";
	private final String executionThreadName = "ExecutionThreadName";

	@Before
	public void setUp() throws Exception {
		protocol = new Protocol();
		protocol.setName(name);

	}


	@Test
	public void testGetAndAddInterval() {
		
		protocol.addInterval(intervalID);
		assertEquals(intervalID, protocol.getInterval());
		assertNotSame(notIntervalID, protocol.getInterval());
	}
	
	@Test
	public void testRemoveInterval() {
		
		protocol.addInterval(intervalID);
		assertEquals(intervalID, protocol.getInterval());
		protocol.removeInterval(intervalID);
		assertEquals(0, protocol.getInterval());
		
	}
	
	
	@Test
	public void testGetAndGenerateID() {
		
		protocol.generateID();
		assertEquals(0, protocol.getID());
	}
	
	@Test
	public void testGetExecutionThreadName() {	
		assertEquals("", protocol.getExecutionThreadName());
		assertNotNull(protocol.getExecutionThreadName());
	}
	
	@Test
	public void testGetAndSetName() {
		//assertEquals(name, protocol.getName());
		protocol.setName("newName");
		assertNotSame(name, protocol.getName());
		assertEquals("", protocol.getName());
		
	}
	
}
