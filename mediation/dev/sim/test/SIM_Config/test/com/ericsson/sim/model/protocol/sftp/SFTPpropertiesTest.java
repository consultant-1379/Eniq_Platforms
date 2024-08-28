package com.ericsson.sim.model.protocol.sftp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SFTPpropertiesTest {

	private SFTPproperties sftpProperties;
	private final String KEY = "key";
	private final String VALUE = "value";
	private final String ADDED_KEY = "addedKey";
	private final String ADDED_VALUE = "addedValue";
	private final String NAME = "name";
	private final String EXECUTION_THREAD_NAME = "ExecutionThreadName";

	@Before
	public void setUp() throws Exception {
		sftpProperties = new SFTPproperties();
		sftpProperties.setName(NAME);
		sftpProperties.addProperty(KEY, VALUE);

	}

	@Test
	public void testAddProperty() {

		sftpProperties.addProperty(ADDED_KEY, ADDED_VALUE);
		assertNotSame(VALUE, ADDED_KEY);
		assertEquals(ADDED_VALUE, sftpProperties.getProperty(ADDED_KEY));
		assertEquals(2, sftpProperties.getProperties().size());

	}

	@Test
	public void testGetProperty() {
		sftpProperties.addProperty(ADDED_KEY, ADDED_VALUE);
		assertEquals(VALUE, sftpProperties.getProperty(KEY));

	}

	@Test
	public void testGetProperties() {
		assertEquals(VALUE, sftpProperties.getProperty(KEY));
		assertNotSame(ADDED_VALUE, KEY);

	}

	@Test
	public void testContainsProperty() {
		assertTrue(sftpProperties.containsProperty(KEY));
		assertFalse(sftpProperties.containsProperty(ADDED_KEY));
	}

	@Test
	public void testGetAndGenerateID() {
		int code = NAME.hashCode();
		sftpProperties.generateID();
		assertEquals(code, sftpProperties.getID());
	}

	@Test
	public void testGetAndSetName() {
		assertEquals(NAME, sftpProperties.getName());
		sftpProperties.setName("newName");
		assertNotSame(NAME, sftpProperties.getName());
		assertEquals("newName", sftpProperties.getName());
	}

	@Test
	public void testGetExecutionThreadName() {
		sftpProperties.addProperty(EXECUTION_THREAD_NAME, "etn");
		assertEquals("etn", sftpProperties.getExecutionThreadName());
		
	}

}
