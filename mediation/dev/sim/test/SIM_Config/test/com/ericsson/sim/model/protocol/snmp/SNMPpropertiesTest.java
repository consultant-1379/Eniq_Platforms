package com.ericsson.sim.model.protocol.snmp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SNMPpropertiesTest {

	private SNMPproperties snmpProperties;
	private final String KEY = "testKey";
	private final String VALUE = "testValue";
	private final String ADDED_KEY = "addedKey";
	private final String ADDED_VALUE = "addedValue";
	private final String CONTAINS_KEY = "containsKey";
	private final String EXECUTION_THREAD_NAME = "ExecutionThreadName";
	private final String NAME = "name";

	@Before
	public void setUp() throws Exception {
		snmpProperties = new SNMPproperties();
	}

	@Test
	public void testAddProperty() {
		assertNotSame(VALUE, snmpProperties.getProperty(KEY));
		snmpProperties.addProperty(KEY, VALUE);
		assertEquals(VALUE, snmpProperties.getProperty(KEY));
	}

	@Test
	public void testGetProperty() {
		snmpProperties.addProperty(ADDED_KEY, ADDED_VALUE);
		assertNotSame(VALUE, snmpProperties.getProperty(KEY));
		assertEquals(ADDED_VALUE, snmpProperties.getProperty(ADDED_KEY));
	}

	@Test
	public void testContainsProperty() {
		assertFalse(snmpProperties.containsProperty(CONTAINS_KEY));
		snmpProperties.addProperty(CONTAINS_KEY, VALUE);
		assertTrue(snmpProperties.containsProperty(CONTAINS_KEY));
	}

	@Test
	public void testAddCounterSet() {

		assertEquals(0, snmpProperties.getCounterSets().size());
		CounterSet counterSet = new CounterSet();
		snmpProperties.addCounterSet(counterSet);
		assertEquals(1, snmpProperties.getCounterSets().size());
		assertEquals(counterSet, snmpProperties.getCounterSets().get(0));
	}

	@Test
	public void testGetProperties() {
		assertEquals(0, snmpProperties.getProperties().size());
		snmpProperties.addProperty(KEY, VALUE);
		assertEquals(1, snmpProperties.getProperties().size());

	}

	@Test
	public void testGetCounterSets() {
		CounterSet counterSet = new CounterSet();
		snmpProperties.addCounterSet(counterSet);
		assertEquals(1, snmpProperties.getCounterSets().size());
		assertEquals(counterSet, snmpProperties.getCounterSets().get(0));
	}

	@Test
	public void testGenerateAndGetId() {
		int code = NAME.hashCode();
		snmpProperties.setName(NAME);
		snmpProperties.generateID();
		assertEquals(code, snmpProperties.getID());
	}

	@Test
	public void testGetExecutionThreadName() {
		assertNull("Has to be null", snmpProperties.getExecutionThreadName());
		snmpProperties.addProperty(EXECUTION_THREAD_NAME, "threadName");
		assertEquals("threadName", snmpProperties.getExecutionThreadName());

	}

	@Test
	public void testSetName() {
		snmpProperties.setName(NAME);
		assertEquals(NAME, snmpProperties.getName());

		snmpProperties.setName("nameTwo");
		assertEquals("nameTwo", snmpProperties.getName());
	}

	@Test
	public void testGetName() {
		assertNull(snmpProperties.getName());
		snmpProperties.setName(NAME);
		assertEquals(NAME, snmpProperties.getName());
	}

}
