/**
 * 
 */
package com.ericsson.catalina.users;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.catalina.users.EniqUserDatabaseException;


/**
 * @author eheijun
 *
 */
public class EniqUserDatabaseExceptionTest {

  private class DummyException extends Exception {
    private static final long serialVersionUID = 1L;
  }

  /**
   * Test method for {@link com.ericsson.catalina.users.EniqUserDatabaseException#EniqUserDatabaseException(java.lang.Exception)}.
   */
  @Test
  public void testEniqUserDatabaseException() {
    DummyException de = new DummyException();
    try {
      throw new EniqUserDatabaseException(de);
    } catch (EniqUserDatabaseException e) {
      assertTrue(e.getCause().equals(de));
    }
  }
  
}
