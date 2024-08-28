package com.ericsson.eniq.glassfish;

/**
 * User: eeipca
 * Date: 09/01/12
 * Time: 08:58
 */
public class DomainParserException extends RuntimeException {
  public DomainParserException(final String message) {
    super(message);
  }

  public DomainParserException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
