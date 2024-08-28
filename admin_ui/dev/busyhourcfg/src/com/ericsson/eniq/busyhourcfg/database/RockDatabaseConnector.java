/**
 *   
 */
package com.ericsson.eniq.busyhourcfg.database;

import java.sql.SQLException;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 */
public class RockDatabaseConnector implements DatabaseConnector {

  private static final String DWHREP_CONNECTIONNAME = "dwhrep";

  private static final String DWH_CONNECTIONNAME = "dwh";

  private static final String USER = "USER";

  private final Logger log = Logger.getLogger(RockDatabaseConnector.class.getName());

  private final RockFactory etlrepRock;

  public RockDatabaseConnector(final RockFactory etlrepRock) {
    this.etlrepRock = etlrepRock;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.database.DatabaseConnector#getDatabaseSession()
   */
  @Override
  public DatabaseSession createDatabaseSession(final String application) throws DatabaseException {
    RockFactory dwhrepRock = null;
    RockFactory dwhRock = null;
    if (etlrepRock == null) {
      throw new DatabaseException("Configuration failure: Unable to connect dwhrep database");
    } else {
      try {
        final Meta_databases mdbDwhrep = new Meta_databases(etlrepRock);
        mdbDwhrep.setConnection_name(DWHREP_CONNECTIONNAME);
        mdbDwhrep.setType_name(USER);
        final Meta_databasesFactory mdbfrep = new Meta_databasesFactory(etlrepRock, mdbDwhrep);
        if (mdbfrep.size() > 0) {
          final Meta_databases dwhrepmdb = (Meta_databases) mdbfrep.get().get(0);
          dwhrepRock = new RockFactory(dwhrepmdb.getConnection_string(), dwhrepmdb.getUsername(), dwhrepmdb
              .getPassword(), dwhrepmdb.getDriver_name(), application, true);
          log.info("Dwhrep connection ready");
          final Meta_databases mdbDwh = new Meta_databases(etlrepRock);
          mdbDwh.setConnection_name(DWH_CONNECTIONNAME);
          mdbDwh.setType_name(USER);
          final Meta_databasesFactory mdbfdwh = new Meta_databasesFactory(etlrepRock, mdbDwh);
          final Meta_databases dwhmdb = (Meta_databases) mdbfdwh.get().get(0);
          dwhRock = new RockFactory(dwhmdb.getConnection_string(), dwhmdb.getUsername(), dwhmdb.getPassword(), dwhmdb
              .getDriver_name(), application, true);
          log.info("Dwh connection ready");
        }
      } catch (SQLException e) {
        log.severe(e.getMessage());
        throw new DatabaseException("SQL Connection failure: Unable to connect dwhrep/dwh database", e);
      } catch (RockException e) {
        log.severe(e.getMessage());
        throw new DatabaseException("Rock Connection failure: Unable to connect dwhrep/dwh database", e);
      } catch (Exception e) {
        log.severe(e.getMessage());
        throw new DatabaseException("Fatal Connection failure: Unable to connect dwhrep database", e);
      }
    }
    return new RockDatabaseSession(dwhrepRock, dwhRock, etlrepRock);
  }

  /** 
   * @return the etlrepRock 
   */
  public RockFactory getEtlrepRock() {
    return etlrepRock;
  }
}
