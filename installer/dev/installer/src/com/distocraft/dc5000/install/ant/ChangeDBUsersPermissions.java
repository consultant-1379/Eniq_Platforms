package com.distocraft.dc5000.install.ant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.tools.ant.BuildException;

import ssc.rockfactory.RockFactory;


/**
 * This class is a custom made ANT-task that changes the permissions of dcbo and
 * dcpublic users. They can be either locked or unlocked. Copyright (c) 1999 -
 * 2008 AB LM Ericsson Oy All rights reserved.
 * 
 * @author ejannbe
 */
public class ChangeDBUsersPermissions extends CommonTask {

  RockFactory etlrepRockFactory = null;

  RockFactory dwhRockFactory = null;

  private String action = "";

  private String dbUser = "";

  /**
   * This function starts the execution of task.
   */
  @Override
  public void execute() throws BuildException {
    try{
    	// Create the connection to the etlrep.
        this.etlrepRockFactory = createDefaultEtlrepRockFactory(getClass().getSimpleName());
        // Create also the connection to dwhrep.
        this.dwhRockFactory = createDwhRockFactory(this.etlrepRockFactory, getClass().getSimpleName());

        // Do the actual change of the db users permissions.
        final int result = this.changeDBPermissions();
        
        if(result == 0) {
          System.out.println("Database user permission changed successfully.");
        } else {
          System.out.println("Changing database user permission failed. Please see log for more details.");
          System.exit(result);
        }
    }finally{
    	if(this.dwhRockFactory != null){
	    	try {
				this.dwhRockFactory.getConnection().close();
			} catch (SQLException e) {
			}
    	}
    	if(this.etlrepRockFactory != null){
	    	try {
				this.etlrepRockFactory.getConnection().close();
			} catch (SQLException e) {
			}
    	}
    }
    
  }

  /**
   * This function changes the permissions of users dcbo and dcpublic to the dwh
   * database. These users can be either locked or unlocked.
   * @return error code
   */
  private int changeDBPermissions() {

    Statement stmnt = null;
    ResultSet rs = null;

    try {
      if (this.action == null || this.action.equalsIgnoreCase("")) {
        System.out
            .println("Parameter action was empty or null. Please specify the action to be performed to the database users.");
        return 1;
      }
      if (this.dbUser == null || this.dbUser.equalsIgnoreCase("")) {
        System.out.println("Parameter databaseuser was empty or null. Please specify the database user.");
        return 2;
      }

      stmnt = this.dwhRockFactory.getConnection().createStatement();


      if (this.dbUser.equalsIgnoreCase("dcpublic") || this.dbUser.equalsIgnoreCase("dcbo")
          || this.dbUser.equalsIgnoreCase("all")) {
        if (this.action.equalsIgnoreCase("lock")) {
          // Lock users

          if (this.dbUser.equalsIgnoreCase("all")) {
            // Lock all
            rs = stmnt.executeQuery("CALL lock_user('dcbo');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            rs = stmnt.executeQuery("CALL lock_user('dcpublic');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            // Drop the existing connections
            rs = stmnt.executeQuery("CALL drop_user_connections('dcbo');");
            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            rs = stmnt.executeQuery("CALL drop_user_connections('dcpublic');");
            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

          } else {
            // Lock specific user
            rs = stmnt.executeQuery("CALL lock_user('" + this.dbUser + "');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            // Drop the existing connections
            rs = stmnt.executeQuery("CALL drop_user_connections('" + this.dbUser + "');");
            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();
          }

        } else if (this.action.equalsIgnoreCase("unlock")) {
          // Unlock users

          if (this.dbUser.equalsIgnoreCase("all")) {
            // Unlock all
            rs = stmnt.executeQuery("CALL unlock_user('dcbo');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

            rs = stmnt.executeQuery("CALL unlock_user('dcpublic');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();

          } else {
            // Unlock specific user
            rs = stmnt.executeQuery("CALL unlock_user('" + this.dbUser + "');");

            while (rs.next()) {
              if (rs.getString("msg") != null) {
                System.out.println(rs.getString("msg"));
              }
            }
            rs.close();
          }

        } else {
          System.out.println("Parameter action was unknown with value " + action);
          return 3;
        }

      } else {
        System.out
            .println("Incorrect database user parameter. Please use either dcpublic, dcbo or all as dbuser parameter.");
        return 4;
      }

      // All is fine, return 0.
      return 0;
      
    } catch (Exception e) {
      System.out.println("Changing database users permissions failed.");
      e.printStackTrace();
      return 5;
    } finally {

      if (stmnt != null) {
        try {
          while (stmnt.getMoreResults()) {
            stmnt.getResultSet().close();
          }
          stmnt.close();
        } catch (Exception e) {
          System.out.println("Error while closing SQL Statement object.");
          e.printStackTrace();
        }
      }
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          System.out.println("Error while closing SQL ResultSet object.");
          e.printStackTrace();
        }
      }

      // Close the database connections if they are still opened.
      if (this.dwhRockFactory != null) {
        if (this.dwhRockFactory.getConnection() != null) {
          try {
            this.dwhRockFactory.getConnection().close();
          } catch (Exception e) {
            // Don't mind if the connection is already closed.
          }

        }
      }

      if (this.etlrepRockFactory != null) {
        if (this.etlrepRockFactory.getConnection() != null) {
          try {
            this.etlrepRockFactory.getConnection().close();
          } catch (Exception e) {
            // Don't mind if the connection is already closed.
          }
        }
      }
    }

  }

  public String getAction() {
    return action;
  }

  public void setAction(final String action) {
    this.action = action;
  }

  public String getDbUser() {
    return dbUser;
  }

  public void setDbUser(final String dbUser) {
    this.dbUser = dbUser;
  }

}
