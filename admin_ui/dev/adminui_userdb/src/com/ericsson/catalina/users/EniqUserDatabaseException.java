/**
 * 
 */
package com.ericsson.catalina.users;

/**
 * @author eheijun
 * 
 */
public class EniqUserDatabaseException extends Exception {

  private static final long serialVersionUID = 1L;

  public EniqUserDatabaseException(final Exception e) {
    initCause(e);
  }

}
