package com.ericsson.sim.model.protocol.snmp;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CounterTest {

	private Counter counter;
	private final String KEY = "testKey";
	private final String VALUE = "testValue";
	private final String ERROR_VALUE = "error";
	private final String ADDED_KEY = "addedKey";
	private final String ADDED_VALUE = "addedValue";

	@Before
	public void setUp() throws Exception {
		counter = new Counter();
		counter.addProperty("", "");
		
	}

	@Test
	public void testGetProperty() {
		assertEquals(null, counter.getProperty(KEY));
		counter.addProperty(KEY, VALUE);
		assertEquals(VALUE, counter.getProperty(KEY));
		assertNotSame(ERROR_VALUE, counter.getProperty(KEY));

	}

	@Test
	public void testAddProperty() {
		counter.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals("addedValue", counter.getProperty(ADDED_KEY));

	}

	@Test
	public void testGetProperties() {
		counter.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals(2, counter.getProperties().size());
		assertNotSame(0, counter.getProperties().size());
		assertNotNull(counter.getProperties().size());
		assertEquals(true, counter.getProperties().containsKey(ADDED_KEY));
		
		counter.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals("Should not add as already there",2, counter.getProperties().size());

	}


}
