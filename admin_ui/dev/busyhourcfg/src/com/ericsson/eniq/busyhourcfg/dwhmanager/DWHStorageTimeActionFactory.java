/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.dwhmanager;

import com.ericsson.eniq.busyhourcfg.database.DatabaseSession;
import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;

/**
 * @author eheijun
 *
 */
public class DWHStorageTimeActionFactory {
  
  private static DWHStorageTimeAction _instance;

  /**
   * Hide constructor
   */
  private DWHStorageTimeActionFactory() {
    super();
  }
  
  /**
   * Create default DWHManager connector
   * @param databaseSession
   * @return
   * @throws DWHManagerException
   */
  private static DWHStorageTimeAction createDWHStorageTimeAction(final DatabaseSession databaseSession) throws DWHManagerException {
    if (databaseSession instanceof RockDatabaseSession) {
      final RockDatabaseSession rockDatabaseSession = (RockDatabaseSession) databaseSession;
      return new DefaultDWHStorageTimeAction(rockDatabaseSession.getDwhRockFactory(), rockDatabaseSession.getDwhrepRockFactory());
    }
    return null;
  }

  
  /**
   * Get (and create) DWHManager connector
   * @return the DWHStorageTimeAction instance
   * @throws DWHManagerException 
   */
  public static DWHStorageTimeAction getDWHStorageTimeAction(final DatabaseSession databaseSession) throws DWHManagerException {
    if (_instance == null) {
      _instance = createDWHStorageTimeAction(databaseSession);
    }
    return _instance;
  }

  
  /**
   * Set DWHManager instance (for junit test use)
   * @param instance the _instance to set
   */
  public static void setDWHStorageTimeAction(final DWHStorageTimeAction instance) {
    _instance = instance;
  }
  
}
