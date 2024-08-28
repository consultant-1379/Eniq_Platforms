/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

/**
 * @author eheijun
 * 
 */
public class UpdateResult {

  public enum ResultType { INFO, ERROR  };

  final private ResultType type;

  final private String text;

  public UpdateResult(final String message, final ResultType messageType) {
    this.text = message;
    this.type = messageType;
  }

  /**
   * @return the messageType
   */
  public ResultType getType() {
    return type;
  }

  /**
   * @return the message
   */
  public String getText() {
    return text;
  }

  /**
   * Check if message type is ERROR
   * @return
   */
  public Boolean isError() {
    return Boolean.valueOf(getType().equals(ResultType.ERROR));
  }

  /**
   * Check if message type is INFO
   * @return
   */
  public Boolean isInfo() {
    return Boolean.valueOf(getType().equals(ResultType.INFO));
  }

}
