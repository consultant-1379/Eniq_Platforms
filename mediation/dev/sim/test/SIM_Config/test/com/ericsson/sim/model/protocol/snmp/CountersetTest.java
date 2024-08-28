package com.ericsson.sim.model.protocol.snmp;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jmock.*;

public class CountersetTest {
	private CounterSet counterSet;
	private final String ADDED_KEY = "addedKey";
	private final String ADDED_VALUE = "addedValue";
	private final String ANOTHER_KEY = "anotherKey";
	private final String ANOTHER_VALUE = "anotherValue";
	private final String NAME = "name";
	private final String OTHER_NAME = "error";
	

	@Before
	public void setUp() throws Exception {
		counterSet = new CounterSet();
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetProperty() {
		assertEquals(null, counterSet.getProperty(ADDED_KEY));
		assertNotSame("Shouldnt be there", ADDED_VALUE, null);
	}

	@Test
	public void testAddProperty() {
		assertEquals(null, counterSet.getProperty(ADDED_KEY));
		counterSet.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals(ADDED_VALUE, counterSet.getProperty(ADDED_KEY));
		
	}
	
	
	@Test
	public void testGetProperties() {
		assertEquals(0, counterSet.getProperties().size());
		counterSet.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals(1, counterSet.getProperties().size());
		counterSet.addProperty(ANOTHER_KEY, ANOTHER_VALUE);
		assertEquals(2, counterSet.getProperties().size());
		counterSet.addProperty(ANOTHER_KEY, ANOTHER_VALUE);
		assertEquals("Should not add as already there",2, counterSet.getProperties().size());
		
	}
	
	@Test
	public void testGetCounters() {
		assertEquals(0, counterSet.getCounters().size());
		counterSet.addCounter(new Counter());
		assertEquals(1, counterSet.getCounters().size());
	}
	
	@Test
	public void testAddCounter() {
		Counter counter = new Counter();
		counterSet.addCounter(counter);
		assertEquals(1, counterSet.getCounters().size());
		assertEquals(counter , counterSet.getCounters().get(0));
	}
	
	
	@Test
	public void testGetName() {
		assertEquals(null, counterSet.getName());
	}
	
	@Test
	public void testSetName() {
		counterSet.setName(NAME);
		assertEquals(NAME, counterSet.getName());
		assertNotSame(OTHER_NAME, counterSet.getName());
	}
	

}
