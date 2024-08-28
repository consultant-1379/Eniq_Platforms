/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun 
 * 
 */
public interface DatabaseSession {

  /**
   * Clear database session 
   */
  void clear() throws DatabaseException ;
  

}
