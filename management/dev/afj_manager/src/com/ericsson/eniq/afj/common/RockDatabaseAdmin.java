/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.ericsson.eniq.repository.ETLCServerProperties;

import ssc.rockfactory.RockFactory;

/**
 * @author eheijun
 * 
 */
public class RockDatabaseAdmin implements DatabaseAdmin {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  private static final String SP_IQSTATUS = "sp_iqstatus";

  private static final String COLUMN_NAME = "Name";

  private static final String COLUMN_VALUE = "Value";

  private static final String TITLE_MAIN_IQ_BLOCKS_USED = "Main IQ Blocks Used:";

  private static final int MAINTENANCE_THRESHOLD = 90;

  private final RockFactory rockFactory;

  /**
   * Default constructor
   * 
   * @param rockFactory
   */
  public RockDatabaseAdmin(final RockFactory rockFactory) {
    super();
    this.rockFactory = rockFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.common.DatabaseAdmin#getDatabaseState()
   */
  @Override
  public DatabaseState getDatabaseState() {
    final Connection connection = rockFactory.getConnection();
    try {
      final Statement statement = connection.createStatement();
      try {
        String mainIq = "";
        final ResultSet resultSet = statement.executeQuery(SP_IQSTATUS);
        try {
          while (resultSet.next()) {
            if (resultSet.getString(COLUMN_NAME).trim().equalsIgnoreCase(TITLE_MAIN_IQ_BLOCKS_USED)) {
              mainIq = resultSet.getString(COLUMN_VALUE);
              break;
            }
          }
          if (checkAllowedSize(mainIq)) {
            return DatabaseState.NORMAL;
          } else {
            return DatabaseState.MAINTENANCE;
          }
        } finally {
          resultSet.close();
        }
      } finally {
        statement.close();
      }
    } catch (SQLException e) {
      log.severe("Database state query failed.");
      e.printStackTrace();
      return DatabaseState.MAINTENANCE;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.common.DatabaseAdmin#isState(com.ericsson.eniq.common.DatabaseState)
   */
  @Override
  public Boolean isState(final DatabaseState databaseState) {
    return getDatabaseState().equals(databaseState);
  }

  /**
   * 
   * Parses disc space usage information .
   * 
   * @param mainiqrow
   *          Main IQ Blocks Used value form sp_iqstatus (eg. 846912 of 10560000, 8%=25Gb, Max Block#: 37760613)
   */
  private Boolean checkAllowedSize(final String mainiqrow) {
    String[] splitted = mainiqrow.split(",");
    splitted = splitted[1].split("%");
    final int percentage = Integer.parseInt(splitted[0].trim());
    return percentage < MAINTENANCE_THRESHOLD;
  }

  /*
   * For code testing purposes only
   */
  /**/
  public static void main(final String[] args) {
    try {
       String conf_dir = System.getProperty("CONF_DIR");
       String propertiesFile;
    	if (conf_dir == null) {
    	      conf_dir = "/eniq/sw/conf";
    	      }

    	    if (!conf_dir.endsWith(File.separator)) {
    	        conf_dir += File.separator;
    	      }

    	    propertiesFile = conf_dir + "ETLCServer.properties";

    	ETLCServerProperties connProps =new ETLCServerProperties(propertiesFile);
      final RockFactory dwh = new RockFactory(connProps.getProperty("ENGINE_DB_URL"), 
    		  "DBA", "sql",    		  
    		  connProps.getProperty("ENGINE_DB_DRIVERNAME"), "TEST", true);
      final RockDatabaseAdmin rockDatabaseAdmin = new RockDatabaseAdmin(dwh);
      System.out.println(DatabaseState.NORMAL + " " + rockDatabaseAdmin.isState(DatabaseState.NORMAL));
      System.out.println(DatabaseState.MAINTENANCE + " " + rockDatabaseAdmin.isState(DatabaseState.MAINTENANCE));
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**/

}
