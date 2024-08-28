/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.util.ArrayList;
import java.util.Arrays;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author eheijun
 *
 */
public class RockDatabaseLockActionFactory {
  
  private static final String DCPUBLIC = "dcpublic";
  
  private static final String DCBO = "dcbo";
  
  private static DatabaseLockAction _instance;
  
  private RockDatabaseLockActionFactory() {}

  /**
   * Method to return singleton instance of the DatabaseLockAction. 
   * @return the _instance
   */
  public static DatabaseLockAction getDWHInstance() throws AFJException {
    if (_instance != null) {
      return _instance;
    }
    final AFJDatabaseHandler connection = AFJDatabaseHandler.getInstance();    
    try {
      final RockFactory dbaDwhRock = connection.getDbaDwh();
      final ArrayList<String> userList = new ArrayList<String>(Arrays.asList(new String[] { DCBO, DCPUBLIC }));
      return new RockDatabaseLockAction(dbaDwhRock, userList);
    } catch (AFJException e) {
      throw e;
    }
  }

  /**
   * Method to set singleton instance of the DatabaseLockAction. 
   */
  public static void setDWHInstance(final DatabaseLockAction instance) {
    _instance = instance;
  }
  
  /**
   * For internal use.
   * @param args
   */
//  public static void main(final String[] args) {
//    try {
//      try {
//        final int result = callDwhAsDcboUser();
//        System.out.println("SQL executed, result is " + result);
//      } catch (Exception e) {
//        System.out.println("Unexpected Error: " + e.getMessage());
//      }
//      final DatabaseLockAction lockAction = RockDatabaseLockActionFactory.getInstance();
//      if (lockAction.performLock()) {
//        try {
//          try {
//            final int result = callDwhAsDcboUser();
//            System.out.println("SQL executed, result is " + result);
//          } catch (Exception e) {
//            System.out.println("Expected Error: " + e.getMessage());
//          }
//        } finally {
//          lockAction.performUnlock();
//        }
//        try {
//          final int result = callDwhAsDcboUser();
//          System.out.println("SQL executed, result is " + result);
//        } catch (Exception e) {
//          System.out.println("Unexpected Error: " + e.getMessage());
//        }
//      } else {
//        System.out.println("ERROR: could not lock user.");
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }

//  private static int callDwhAsDcboUser() throws SQLException, RockException {
//    int result = -1;
//    final RockFactory dwh = new RockFactory("jdbc:sybase:Tds:dwhdb:2640", "dcbo", "dcbo", "com.sybase.jdbc3.jdbc.SybDriver", "HEIKKI_TEST", true);
//    final Connection conn = dwh.getConnection();
//    try {
//      final Statement stmt = conn.createStatement();
//      try {
//        final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM DC.DIM_DATE;");
//        try {
//          if (rs.next()) {
//            result = rs.getInt(1);
//          } else {
//            result = 0;
//          }
//        } finally {
//          rs.close();
//        }
//      } finally {
//        stmt.close();
//      }
//    } finally {
//      conn.close();
//    }
//    return result;
//  }
  
  
}
