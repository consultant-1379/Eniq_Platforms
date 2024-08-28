package com.ericsson.sim.model.properties;

import static org.junit.Assert.*;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


public class RuntimePropertiesTest {
	
	private RuntimeProperties runtimeProperties;
	private final String key = "key";
	private final String value = "value";

	@Before
	public void setUp() throws Exception {
		runtimeProperties = RuntimeProperties.getInstance();
		
	}
	
	@Test
	public void testAddAndGetProperty(){
		assertNotSame(value, runtimeProperties.getProperty(key));
		runtimeProperties.addProperty(key, value);
		assertEquals(value, runtimeProperties.getProperty(key));
			
	}	

}
