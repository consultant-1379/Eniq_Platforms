/**
 * ETL Repository access library.<br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved.<br>
 * 
 * @author lemminkainen
 */
package ssc.rockfactory;

/**
 * Exception for RockFactory and rock elements
 */
public class RockException extends Exception {

  private Throwable nestedException = null;

  public RockException(String message) {
    super(message);
  }

  public RockException(String message, Throwable nestedException) {
    super(message);
    this.nestedException = nestedException;
  }

  /**
   * Returns the nested exception or null if there is no nested exception
   * 
   * @return Throwable Nested exception
   */
  public Throwable getNestedException() {
    return this.nestedException;
  }

}