package com.ericsson.eniq.unittesting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Vector;

import com.ericsson.eniq.common.testutilities.DirectoryHelper;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class UnitTestDatabase {

  // Hibernate SQL DB Setup Information
  private String driver = "org.hsqldb.jdbcDriver";

  private String url = "jdbc:hsqldb:mem:";

  private String username = "sa";

  private String password = "";

  private String databaseType = "hsql";

  private String connectionName = "Test";

  private String portNumber = "";

  private String dbName;

  private Connection dbConn;

  private RockFactory dbRock;

  public static String sqlDir = DirectoryHelper
.getFullFolderName("tp_ide"
      + File.separator + "dev" + File.separator + "tp_ide_unittest"
      + File.separator)
      + "test" + File.separator + "UnitTesting" + File.separator
      + "SQLFiles"
      + File.separator;

  /**
   * Constructor Sets up the HSQL In Memory Database
   * 
   */
  public UnitTestDatabase(String dbName) {
    this.dbName = dbName.toLowerCase();
    System.out.println("sdfsdf");
    System.out.println("sqldir: " + sqlDir);

    getConnection();
  }

  /**
   * Sets up another type of database. Specifically used to connect to a Sybase
   * IQ database on a server.
   * 
   * @param driver
   * @param databaseType
   * @param dbName
   * @param url
   * @param username
   * @param password
   */
  public UnitTestDatabase(String driver, String databaseType, String dbName, String url, String portNumber,
      String username, String password) {
    this.driver = driver;
    this.databaseType = databaseType;
    this.dbName = dbName;
    this.url = url;
    this.portNumber = portNumber;
    this.username = username;
    this.password = password;

    getConnection();
  }

  /**
   * Creates the tables and adds test information to the database.
   * 
   */
  public void createCompleteDB() {
    this.runInsertCreateTable();
    this.insertDBData();
    this.runInsertForeignKeys();
  }

  /**
   * inserts the create table statements into the database
   * 
   */
  private void runInsertCreateTable() {
    String strCreateTable = sqlDir + "createTables_" + this.dbName + ".sql";
    File createTable = new File(strCreateTable);

    try {
      this.runSQLFile(createTable);
    }

    catch (Exception e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

  }

  /**
   * inserts the foreign keys into the database
   * 
   */
  private void runInsertForeignKeys() {
    String strForeignKeys = sqlDir + "foreignKeys_" + this.dbName + ".sql";
    File foreignKeys = new File(strForeignKeys);

    if (foreignKeys.exists()) {
      try {
        this.runSQLFile(foreignKeys);
      }

      catch (Exception e) {
        System.out.println("============================================");
        e.printStackTrace();
        System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
        System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
        System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
        System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
        System.out.println(" Message: " + e.getMessage());
        System.out.println("============================================");
      }

    } else {
      System.out.println(foreignKeys + " does not exist. If installing the Dataware house this is normal");
    }
  }

  /**
   * Creates a blank database. Only the structure of the Database is setup.
   * 
   */
  public void createBlankDB() {
    this.runInsertCreateTable();
    this.runInsertForeignKeys();
  }

  /**
   * Inserts the test data into the database
   * 
   */
  public void insertDBData() {
    String strInsertTestData = sqlDir + "insertTestData_" + this.dbName + ".sql";
    File insertTestData = new File(strInsertTestData);
    try {

      this.runSQLFile(insertTestData);
    }

    catch (Exception e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }
  }

  /**
   * Get Method that returns the Connection to the Database
   * 
   * @return Connection
   */
  public Connection getConnection() {
    try {
      this.dbRock = new RockFactory(this.url + this.dbName + this.portNumber, this.databaseType, this.username,
          this.password, this.driver, this.connectionName, false);
    }

    catch (SQLException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Error Code: " + e.getErrorCode() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    catch (RockException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    catch (Exception e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    this.dbConn = dbRock.getConnection();
    return this.dbConn;
  }

  /**
   * Get method that returns the RockFactory for this database
   * 
   * @return RockFactory for this database
   */
  public RockFactory getDBRockFactory() {
    return this.dbRock;
  }

  /**
   * Reinitialize Database Truncates the database first and then re-inserts the
   * tech pack information
   */
  public void reinitializeDB() {
    try {
      this.dropDBTables();
      this.createCompleteDB();
    }

    catch (Exception e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }
  }

  /**
   * Drops the tables in the Database
   * 
   * 
   */
  public void dropDBTables() {
    String strDropTables = sqlDir + "dropTables_" + this.dbName + ".sql";
    File dropTables = new File(strDropTables);

    try {
      this.runSQLFile(dropTables);
    }

    catch (Exception e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }
  }

  /**
   * Closes the Database Connection
   * 
   */
  public void closeDB() {
    this.dropDBTables();

    try {
      this.dbConn.close();
    } catch (SQLException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Error Code: " + e.getErrorCode() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }
  }

  /**
   * this method will take in the sql statement and then run it it returns a
   * ResultSet so that the user can run through the results
   * 
   * @param String
   *          strQuery - Contains the query to be run
   * @param Connection
   *          conn - Contains the connection to the database the query will be
   *          run against
   * 
   * @return ResultSet
   */
  public ResultSet runSQLQuery(String strQuery) throws Exception {
    try {
      Statement stmt = this.dbConn.createStatement();
      ResultSet rs = stmt.executeQuery(strQuery);
      stmt.close();

      return rs;
    }

    catch (SQLException e) {
      System.out.println("============================================");
      System.out.println("SQL Statement: " + strQuery);
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Error Code: " + e.getErrorCode() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    catch (Exception e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    return null;
  } // end runSQL

  /**
   * This method rill run a single query from a file and retun a ResultSet
   * 
   * @param String
   *          strQuery - Contains the query to be run
   * @param Connection
   *          conn - Contains the connection to the database the query will be
   *          run against
   * 
   *@return ResultSet
   */
  public ResultSet runSingleSQLQueryFile(File fileName) throws Exception {
    Statement stmt = this.dbConn.createStatement();

    String strQuery = readSingleSQLQueryFile(fileName);
    ResultSet rs = null;

    try {
      rs = stmt.executeQuery(strQuery);
      stmt.close();

      return rs;
    }

    catch (SQLException e) {
      System.out.println("============================================");
      System.out.println("SQL Statement: " + strQuery);
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Error Code: " + e.getErrorCode() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    catch (Exception e) {
      System.out.println("Cause" + e.getCause());
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

    return rs;
  }

  /**
   * 
   * This method will take in the sql file, send it to get all the sql
   * statements It will then run each individual sql statement
   * 
   * @param String
   *          strQuery - Contains the query to be run
   * @param Connection
   *          conn - Contains the connection to the database the query will be
   *          run against
   * 
   */
  public void runSQLFile(File fileName) throws Exception {
    System.out.println("In runSQLFile with: " + fileName);
    Statement stmt = this.dbConn.createStatement();

    Vector<String> vecSQLStatements = readSQLFile(fileName);

    String strCurrentStatement = null;

    // this loops through the SQL Statements in the Vector and then executes
    // them in the database.
    for (int i = 0; i < vecSQLStatements.size(); i++) {
      try {
        strCurrentStatement = (String) vecSQLStatements.get(i);
        stmt.execute(strCurrentStatement);
      }

      catch (SQLException e) {
	e.printStackTrace();
        System.out.println("============================================");
        System.out.println("SQL Statement: " + strCurrentStatement);
        e.printStackTrace();
        System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
        System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
        System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
        System.out.println("Error Code: " + e.getErrorCode() + "\n\n");
        System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
        System.out.println(" Message: " + e.getMessage());
        System.out.println("============================================");
      }

      catch (Exception e) {
        System.out.println("Cause" + e.getCause());
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }

    stmt.close();

  } // end runSQL

  /**
   * This method reads in a file that contains a single SQL Query
   * 
   * @param String
   *          strFileName
   * 
   * @return String
   * 
   */
  private static String readSingleSQLQueryFile(File fileName) {
    BufferedReader input = null;
    StringBuffer strBfrFileContents = new StringBuffer(); // holds the files
    // information
    // temporarly
    String query = null;
    try {
      input = new BufferedReader(new FileReader(fileName));
      String line = null;

      // Reading the File into a String Buffer. This will be used to separate
      // the SQL Statements
      while ((line = input.readLine()) != null) {
        // Checks to see if the line is a comment. if it is, it's not added to
        // tbe string buffer as they are not needed
        if (!line.startsWith("--")) {
          strBfrFileContents.append(line);
          strBfrFileContents.append(System.getProperty("line.separator"));
        }
      }

      query = strBfrFileContents.toString(); // converts the buffer to a string
      // for processing

      return query;
    }

    catch (FileNotFoundException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    catch (IOException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    finally {
      try {
        if (input != null) {
          input.close();
        }
      }

      catch (IOException e) {
        System.out.println("============================================");
        e.printStackTrace();
        System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
        System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
        System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
        System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
        System.out.println(" Message: " + e.getMessage());
        System.out.println("============================================");
      }
    }

    return query;
  }

  /**
   * This method takes in the SQL file, picks out each query and saves them in a
   * vector.
   * 
   * @param String
   *          strFileName
   * 
   * @return Vector - This will contain all the queries contained in the file
   */
  private static Vector<String> readSQLFile(File fileName) {
    BufferedReader input = null;

    StringBuffer strBfrFileContents = new StringBuffer(); // holds the files
    // information
    // temporarly
    String strFileContents = null; // holds the files information. this gets
    // searched for sql statementa.
    String sqlStatement = null; // temporary store for each sql statement found

    Vector<String> vecSQLStatement = new Vector<String>(); // stores the sql
    // statements

    int start = 0; // start of the sql statement
    int end = 0; // end of the sql statement

    try {
      input = new BufferedReader(new FileReader(fileName));
      String line = null;

      // Reading the File into a String Buffer. This will be used to separate
      // the SQL Statements
      while ((line = input.readLine()) != null) {
        // Checks to see if the line is a comment. if it is, it's not added to
        // tbe string buffer as they are not needed
        if (!line.startsWith("--")) {
          strBfrFileContents.append(line);
          strBfrFileContents.append(System.getProperty("line.separator"));
        }
      }

      strFileContents = strBfrFileContents.toString(); // converts the buffer to
      // a string for
      // processing
      strBfrFileContents = null;
      // extracts the sql statements and inserts them into a vector for further
      // processing.
      for (int i = 0; i < strFileContents.length(); i++) {

        // finds the point where the end of the sql statement is.
        if (strFileContents.charAt(i) == ';' && strFileContents.charAt(i + 1) == '\r') {
          end = i;

          // extracts the sql statement from the string
          sqlStatement = strFileContents.substring(start, end);

          // adds this to the vector
          vecSQLStatement.add(sqlStatement);

          // checks to see if the end variable is at the end of the string
          if (end++ != strFileContents.length()) {
            start = end;
          }
        }
      } // end for

      input.close();
      return vecSQLStatement;
    }

    catch (FileNotFoundException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    catch (IOException e) {
      System.out.println("============================================");
      e.printStackTrace();
      System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
      System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
      System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
      System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
      System.out.println(" Message: " + e.getMessage());
      System.out.println("============================================");
    }

    finally {
      try {
        if (input != null) {
          input.close();
        }
      }

      catch (IOException e) {
        System.out.println("============================================");
        e.printStackTrace();
        System.out.println("This is the cause of the problem: " + e.getCause() + "\n\n");
        System.out.println("This is the initial cause of the problem: " + e.initCause(e.getCause()) + "\n\n");
        System.out.println("Localised Message: " + e.getLocalizedMessage() + "\n\n");
        System.out.println("Fill In Stack Trace: " + e.fillInStackTrace());
        System.out.println(" Message: " + e.getMessage());
        System.out.println("============================================");
      }
    }

    return vecSQLStatement;

  } // end readSQLFile

  /**
   * Returns the existing database connection. May be null if the connection has
   * not been successfully initialized with getConnection().
   * 
   * @return the existing database connection
   */
  public Connection getExistingConnection() {
    return this.dbConn;
  }

  /**
   * Stores a savepoint for the current database.
   * 
   * @param savePointName
   */
  public void storeSavePoint(String savePointName) {
    System.out.println("Storing HSQLDB savepoint: " + savePointName);
    try {
      runSQLQuery("SAVEPOINT " + savePointName + ";");
    } catch (Exception e) {
      System.out.println(this.getClass() + " storeSavePoint(): Storing of HSQLDB savepoint failed!");
      e.printStackTrace();
    }
  }

  /**
   * Executes a rollback to a previously saved savepoint. This has not effect in
   * case the save point is not found.
   * 
   * @param savePointName
   */
  public void rollbackToSavePoint(String savePointName) {
    System.out.println("Rollback to HSQLDB savepoint: " + savePointName);
    try {
      runSQLQuery("ROLLBACK TO SAVEPOINT " + savePointName + ";");
    } catch (Exception e) {
      System.out.println(this.getClass() + " rollbackToSavePoint(): Rollback to HSQLDB savepoint failed!");
      e.printStackTrace();
    }
  }
}
