/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import java.sql.SQLException;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * 
 */
public class DatabaseConnectorFactory {

  private static final Logger log = Logger.getLogger(DatabaseConnectorFactory.class.getName());
	
  private static DatabaseConnector _instance;

  private static String applicationName;
  
  /**
   * Hide constructor
   */
  private DatabaseConnectorFactory() {}

  /**
   * 
   * @return
   */
  private static DatabaseConnector createDatabaseConnector(final ETLCServerProperties etlcServerProperties,
      final BusyhourProperties busyhourProperties) {
    
    try {
      
      if (etlcServerProperties == null) {
        throw new DatabaseException("Configuration failure: Invalid etlcServer properties");
      }

      final String dbUrl = etlcServerProperties.getProperty(ETLCServerProperties.DBURL).trim();
      final String username = etlcServerProperties.getProperty(ETLCServerProperties.DBUSERNAME).trim();
      final String password = etlcServerProperties.getProperty(ETLCServerProperties.DBPASSWORD).trim();
      final String dbDriver = etlcServerProperties.getProperty(ETLCServerProperties.DBDRIVERNAME).trim();

      if (busyhourProperties == null) {
        throw new DatabaseException("Configuration failure: Invalid busyhour properties");
      }
      
      applicationName = busyhourProperties.getProperty(BusyhourProperties.APPLICATIONNAME);

      if ((username == null) || (password == null)) {
        throw new DatabaseException("Configuration failure: Invalid username or password");
      } else {
        try {
          final RockFactory etlrepRock = new RockFactory(dbUrl, username, password, dbDriver, applicationName, true);
          log.info("Etlrep connection ready");
          return new RockDatabaseConnector(etlrepRock);
        } catch (SQLException e) {
          log.severe(e.getMessage());
          throw new DatabaseException("SQL Connection failure: Unable to connect etlrep database", e);
        } catch (RockException e) {
          log.severe(e.getMessage());
          throw new DatabaseException("Rock Connection failure: Unable to connect etlrep database", e);
        } catch (Exception e) {
          log.severe(e.getMessage());
          throw new DatabaseException("Fatal Connection failure: Unable to connect etlrep database", e);
        }
      }
    } catch (DatabaseException e) {
      log.severe("Could not create connector for the database. (" + e.getMessage() + ")");
    }
    return null;
  }

  /**
   * @return the applicationName
   */
  public static String getApplicationName() {
    if (applicationName != null) {
      return applicationName;
    }
    return "";
  }

  
  /**
   * Get (and create) connector class for dwhrep database
   * 
   * @param etlcServerProperties
   * @param busyhourProperties
   * @return DatabaseConnector or null if connector could not be created
   */
  public static DatabaseConnector getDatabaseConnector(final ETLCServerProperties etlcServerProperties,
      final BusyhourProperties busyhourProperties) {
    if (_instance == null) {
      _instance = createDatabaseConnector(etlcServerProperties, busyhourProperties); 
    }
    return _instance;
  }

  
  /**
   * Set alternative DatabaseConnector (for junit test use)
   *  
   * @param instance
   */
  public static void setDatabaseConnector(final DatabaseConnector instance) {
    _instance = instance;
  }
  
}
