/**
 * 
 */
package com.ericsson.eniq.afj.common;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class RockDatabaseAdminFactory {
  
  private static DatabaseAdmin _instance;
  
  private RockDatabaseAdminFactory() {}
  
  /**
   * Returns DatabaseAdmin interface for DWH database
   * @return
   * @throws AFJException
   */
  public static DatabaseAdmin getDWHinstance() throws AFJException {
    if (_instance != null) {
      return _instance;
    }
    final AFJDatabaseHandler connection = AFJDatabaseHandler.getInstance();    
    try {
      final RockFactory dbaDwhRock = connection.getDbaDwh();
      return new RockDatabaseAdmin(dbaDwhRock);
    } catch (AFJException e) {
      throw e;
    }
  }

  /**
   * Method to set singleton instance of the DatabaseAdmin. 
   */
  public static void setDWHInstance(final DatabaseAdmin instance) {
    _instance = instance;
  }
  
}
