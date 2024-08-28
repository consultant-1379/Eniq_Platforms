/**
 * 
 */
package com.ericsson.eniq.engine;

import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class EngineRestarterWrapper {
  
  private static boolean _executionAllowed = true;;
  
  private EngineRestarterWrapper() {
  }

  /**
   * 
   * @throws AFJException
   */
  public static void execute() throws AFJException {
    if (_executionAllowed && !isWindows()) {
      EngineRestarter.execute();
    }
  }
  
  /**
   * @return the _executionAllowed
   */
  public static boolean isExecutionAllowed() {
    return _executionAllowed;
  }
  
  /**
   * @param executionAllowed the _executionAllowed to set
   */
  public static void setExecutionAllowed(final boolean executionAllowed) {
    _executionAllowed = executionAllowed;
  }

  /**
   * Detect if running in Windows
   * @return
   */
  private static boolean isWindows() {
    final String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("win") >= 0); 
  }
}
