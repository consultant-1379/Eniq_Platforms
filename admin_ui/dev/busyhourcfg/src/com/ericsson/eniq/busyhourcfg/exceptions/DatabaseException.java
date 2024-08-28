/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.exceptions;

/**
 * @author eheijun
 * 
 */
public class DatabaseException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -5599884061950661242L;

  public DatabaseException(final String message, final Exception cause) {
    super(message, cause);
  }

  public DatabaseException(final String message) {
    super(message);
  }
}
