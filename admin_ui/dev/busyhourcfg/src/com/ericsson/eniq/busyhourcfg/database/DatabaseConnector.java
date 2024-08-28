/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public interface DatabaseConnector {
  
  /**
   * Creates database session 
   *   
   * @return DatabaseSession instance of the created session 
   * @throws DatabaseException
   */
  DatabaseSession createDatabaseSession(String application) throws DatabaseException;

}
