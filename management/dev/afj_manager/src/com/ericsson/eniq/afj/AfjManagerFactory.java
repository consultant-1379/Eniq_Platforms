/**
 * 
 */
package com.ericsson.eniq.afj;


import com.ericsson.eniq.exception.AFJException;

/**
 * @author eheijun
 *
 */
public class AfjManagerFactory {
  
  private static AFJManager _instance;
  
  private AfjManagerFactory() {
  }
  
  /**
   * @return the afjManager instance
   * @throws AFJException 
   */
  public static AFJManager getInstance() throws AFJException {
    if (_instance == null) {
        _instance = new AFJManagerImpl();
    }
    return _instance;
  }
  
  /**
   * 
   * @param afjManager the afjManager to set
   */
  public static void setInstance(final AFJManager afjManager) {
    _instance = afjManager;
  }

}
