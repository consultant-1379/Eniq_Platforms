/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author eheijun
 *
 */
public class DatabaseExceptionTest {

	private static final String SOME_ERROR_MESSAGE = "some error message";

  /**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException#DatabaseException(java.lang.String, java.lang.Exception)}.
	 */
	@Test
	public void testDatabaseExceptionStringException() {
    final Exception originalException = new Exception(SOME_ERROR_MESSAGE); 
    try {
      throw new DatabaseException(SOME_ERROR_MESSAGE, originalException);      
    } catch (DatabaseException e) {
      assertTrue(SOME_ERROR_MESSAGE.equals(e.getMessage()));
      assertTrue(e.getCause().equals(originalException));
    }
	}

	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException#DatabaseException(java.lang.String)}.
	 */
	@Test
	public void testDatabaseExceptionString() {
	  try {
	    throw new DatabaseException(SOME_ERROR_MESSAGE);	    
	  } catch (DatabaseException e) {
	    assertTrue(SOME_ERROR_MESSAGE.equals(e.getMessage()));
    } catch (Exception e) {
      fail("wrong exception");
	  }
	}

}
