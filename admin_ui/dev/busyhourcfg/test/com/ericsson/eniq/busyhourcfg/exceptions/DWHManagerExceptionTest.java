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
public class DWHManagerExceptionTest {

  private static final String SOME_ERROR_MESSAGE = "some error message";

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException#DWHManagerException(java.lang.String, java.lang.Exception)}.
   */
  @Test
  public void testDWHManagerExceptionStringException() {
    final Exception originalException = new Exception(SOME_ERROR_MESSAGE); 
    try {
      throw new DWHManagerException(SOME_ERROR_MESSAGE, originalException);      
    } catch (DWHManagerException e) {
      assertTrue(SOME_ERROR_MESSAGE.equals(e.getMessage()));
      assertTrue(e.getCause().equals(originalException));
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException#DWHManagerException(java.lang.String)}.
   */
  @Test
  public void testDWHManagerExceptionString() {
    try {
      throw new DWHManagerException(SOME_ERROR_MESSAGE);      
    } catch (DWHManagerException e) {
      assertTrue(SOME_ERROR_MESSAGE.equals(e.getMessage()));
    } catch (Exception e) {
      fail("wrong exception");
    }
  }
}
