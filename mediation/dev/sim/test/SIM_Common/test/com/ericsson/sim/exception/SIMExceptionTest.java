package com.ericsson.sim.exception;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SIMExceptionTest {

	private SIMException exc;
	private final String EXCEPTION_MESSAGE = "SimException";
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testSIMException() throws SIMException {
		
		exc = new SIMException();
		assertEquals(null, exc.getMessage());
		
	} 
	
	@Test
	public void testSIMExceptionMessage() throws SIMException{
		
		exc = new SIMException(EXCEPTION_MESSAGE);
		assertEquals(EXCEPTION_MESSAGE, exc.getMessage());
		
	}
	
	@Test
	public void testSIMExceptionWithThrowable() throws SIMException{
		Throwable cause = new Throwable();
		
		exc= new SIMException(cause);
		assertEquals(null, exc.getMessage());

	}
	
	@Test
	public void testSIMExceptionMessageAndCause() throws SIMException{
		Exception e = new Exception();
		exc = new SIMException(EXCEPTION_MESSAGE, e);
		
		assertEquals(EXCEPTION_MESSAGE, exc.toString());
		
	}
	
}














