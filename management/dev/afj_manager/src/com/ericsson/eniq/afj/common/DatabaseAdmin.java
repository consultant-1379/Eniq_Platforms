/**
 * 
 */
package com.ericsson.eniq.afj.common;


/**
 * @author eheijun
 *
 */
public interface DatabaseAdmin {
  
  /**
   * Get state of the database 
   * @return DatabaseState state
   */
  DatabaseState getDatabaseState();
  
  /**
   * Check if database is in specific state
   * @return
   */
  Boolean isState(DatabaseState databaseState);

}
