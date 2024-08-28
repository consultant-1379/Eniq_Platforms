/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.exceptions;

/**
 * @author eheijun
 * 
 */
public class DWHManagerException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 7961786870317338297L;

  public DWHManagerException(final String message, final Exception cause) {
    super(message, cause);
  }

  public DWHManagerException(final String message) {
    super(message);
  }
}
