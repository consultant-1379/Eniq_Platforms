/**
 * Class handles comparing data in db and in xml-file
 */
package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;

import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;


/**
 * @author epetrmi
 * 
 */
public abstract class DatabaseTester {

  // ##TODO## Get these from somewhere else
  // ##TODO## Consider making this more configurable (dir/file-patterns etc.)
  /**
   * Base directory where "expected" xml-files are stored/searched
   */
  final static String expectedFilesBaseDir = "test/expectedXmls";

  /**
   * Name of the test case (used in filename)
   */
  final static String testName = "testCreateNewTeckPack";

  /**
   * Delimeter used in filename
   */
  final static String DELIM = "_";

  /**
   * Fail postfix of filename
   */
  final static String FILE_EXPECTED = "_expected";

  /**
   * Fail postfix of filename
   */
  final static String FILE_FAIL_ID = "_FAILED";

  /**
   * Postfix of filename
   */
  final static String FILE_POSTFIX = ".xml";

  /**
   * A flag that creates a file representing actual db data beside the expected
   * file.
   */
  private static boolean createActualFileWhenTestFails = true;

  /**
   * Stores dbunit database connections in a map (Allow all tests use once
   * created connections again)
   */
  private static Map<String, IDatabaseConnection> conns = null;

  // Constructors
  /**
   * Class is now implemented to be used static way so the basic constructor is
   * not allowed.
   */
  private DatabaseTester() {
  }

  /**
   *
   * The method takes data out from db and checks if a file exist for this test.
   * If file does not exist, a new file is created and it is used as "expected"
   * data next time.
   * 
   * 
   * @param testCaseName
   *          - name of the test case
   * @param dbName
   *          - name of the db to use
   * @param tableName
   *          - table name to check
   * @param ignoredCols
   *          -
   * @throws Exception
   */
  public static void testTable(String className, String testCaseName, String dbName, String tableName,
      String[] ignoredCols) throws Exception {

    // Validate input
    if (className == null || testCaseName == null || tableName == null || dbName == null) {
      throw new IllegalArgumentException("classname/testcasename/tablename/dbname cannot be null. " + "testCaseName="
          + testCaseName + ", tableName=" + tableName + ", dbname=" + dbName);
    }
    if (ignoredCols == null) {
      ignoredCols = new String[0];
    }
    
    System.out.println("Verifying table [" +tableName +"]");
    // ACTUAL TEST STARTS...
    final String[] checkedTable = new String[] { tableName };
    final String[] ignoredColsOfCheckedTable = ignoredCols;

    // Read actual/current data from database
    final IDataSet actualDSFromDb = getConnection(dbName).createDataSet(checkedTable);
    final ITable actualTableFromDb = actualDSFromDb.getTable(checkedTable[0]);


    // We generate the filename and directories
    final String parsedClassName = getClassNameAsFilename(className);
    final String directory = handleDirectory(expectedFilesBaseDir + "/" + parsedClassName);
    final String baseFile = testCaseName + DELIM + checkedTable[0];
    final String fullFileName = directory + "/" + baseFile + FILE_EXPECTED + FILE_POSTFIX;
    final String failedFileName = directory + "/" + baseFile + FILE_FAIL_ID + FILE_POSTFIX;

    
    // Check if such file exist
    final File expectedFile = new File(fullFileName);
    if (expectedFile.exists()) {
      
    } else {
      // File does not exist so we store the current db-state as expected state.
      // Normally this happens when the test is run the first time
      System.out.println("WARNING: The previous file (" + fullFileName + ") did NOT exist "
          + "so the current state is stored to expected-file");
      writeData(actualDSFromDb,  new FileOutputStream(expectedFile));
    }

    // Read expected data from file (actually there's info only from one table)
    final IDataSet expectedDSFromFile = readData(new FileInputStream(expectedFile));
    final ITable expectedTableFromFile = expectedDSFromFile.getTable(checkedTable[0]);

    // Check if the db-table-state matches with file-table-state
    if (createActualFileWhenTestFails) {
      // ##TODO## Check if there's more efficient way to do this
      try {
        Assertion.assertEqualsIgnoreCols(expectedTableFromFile, actualTableFromDb, ignoredColsOfCheckedTable);
      } catch (AssertionError e) {
        // When error happens we want to write out failed xml-file
        writeData(actualDSFromDb,  new FileOutputStream(failedFileName));
        throw e;// Re-throw the exception
      }
    } else {
      // Don't write failed (actual) data on disk
      Assertion.assertEqualsIgnoreCols(expectedTableFromFile, actualTableFromDb, ignoredColsOfCheckedTable);
    }

  }

  
  
  /**
   * Creates expected files, if they dont already exist. (Does not compare)
   * 
   * @param tables - list of testTableItems
   * @throws Exception
   */
  public static void createExpected(List<TestTableItem> tables) throws Exception {
    createExpected(tables, false);
  }
  
  /**
   * Creates expected files (Does not compare)
   * 
   * @param tables
   * @param forceOverwrite - if true, overwrites old file if it exists
   * @throws Exception
   */
  public static void createExpected(List<TestTableItem> tables, boolean forceOverwrite) throws Exception {
    for (TestTableItem i : tables) {
      
      final String parsedClassName = getClassNameAsFilename(i.getClassName());
      final String directory = handleDirectory(expectedFilesBaseDir + "/" + parsedClassName);
      final String baseFile = i.getMethodName() + DELIM + i.getTableName();
      final String fullFileName = directory + "/" + baseFile + FILE_EXPECTED + FILE_POSTFIX;
      File f = new File(fullFileName);

      if(forceOverwrite || !f.exists()) {
         // Read actual/current data from database
        final IDataSet actualDSFromDb = getConnection(i.getDbName()).createDataSet(new String[] {i.getTableName() } );
        writeData(actualDSFromDb, new FileOutputStream(f));
        System.out.println("INFO: Writed file="+fullFileName+" succesfully");
      }else{
        System.out.println("INFO: File="+fullFileName+" NOT created. File exist and it's not overwrited.");
      }
    }
  }
  
  


  /**
   * Tests a given list of tables and collects assertion error messages together
   * into one assertion exception
   * 
   * This methods purpose is to gather many db-assertion failures during one
   * test run
   * 
   * @param list
   * @return array of tested tables. Item format is "db:tablename" e.g. "etlrep:MEASUREMENTTYPE".
   * @throws Exception
   */
  public static String[] testTableList(List<TestTableItem> list) throws Exception {
    
    // Return StringArray containing tablenames ("db:tablename" e.g. "etlrep:MEASUREMENTTYPE")
    String[] ret = new String[list.size()];
    int index = 0;
    
    StringBuffer sbErrors = new StringBuffer();
    // Iterate given tables
    for (TestTableItem i : list) {
      try {
        testTable(i.getClassName(), i.getMethodName(), i.getDbName(), i.getTableName(), i.getIgnoredCols());
         System.out.println("testTableList(): Verification successful for table: "
         + i.getTableName() + ".");
        ret[index] = i.getDbName()+":"+i.getTableName();
        index++;
      } catch (AssertionError e) {
        // Catch exception and add error message to sB
        System.out.println("testTableList(): Verification failed for table: " + i.getTableName() + ".");
        sbErrors.append("\n" + e.getMessage());
      } catch (Throwable e) {
        // Let's print also other problems
        System.out.println("testTableList(): Error in verification of table: " + i.getTableName() + ".");
        e.printStackTrace();
      }
    }

    // Check if any exceptions existed
    if (sbErrors.length() > 0) {
      // At least one exception is thrown
      // We throw new AssertionError which holds the
      // display messages of all exceptions
      throw new AssertionError(sbErrors.toString());
    }
    return ret;
  }

  
  /**
   * Reads data 
   * 
   * @param inputStream
   * @return dataset or null
   */
  private static IDataSet readData(FileInputStream inputStream) {
    IDataSet ret = null;
    try {
      ret = new XmlDataSet(inputStream);
    } catch (DataSetException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * Writes data
   * 
   * @param actualDSFromDb
   * @param outputStream
   */
  private static void writeData(IDataSet actualDSFromDb, OutputStream outputStream) {
    try {
      XmlDataSet.write(actualDSFromDb, outputStream);
    } catch (DataSetException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }    
  }

  /**
   * Returns a directory structure as a String value based on
   * given class name
   * 
   * @param className
   * @return - directory structure (eg. "com.foo.bar.ClassName" as "com/foo/bar/ClassName")
   */
  private static String getClassNameAsFilename(String className) {
    return className.replace(".", "/");
  }
  
  
  /**
   * Checks if given directory path exist If exists returns given directory
   * path. If does not exist creates the dir and returns the given directory
   * path.
   * 
   * @return - directory path
   */
  private static String handleDirectory(String dirPath) {
    File f = new File(dirPath);
    if (!f.exists()) {
      f.mkdirs();
    }
    return dirPath;
  }

  private static IDatabaseConnection getConnection(String dbName) {
    if (conns == null) {
      initializeConnections();
    }
    return conns.get(dbName);
  }

  /**
   * Initialize database connections and store references to static map from
   * where they can be used again
   */
  private static void initializeConnections() {
    try {
      // System.out.println("Initialize db connections");
      conns = new HashMap<String, IDatabaseConnection>(3);
      // conns.put(DB_ETLREP, new
      // DatabaseConnection(TechPackIdeStarter.getEtlRepDbConnection()));
      // conns.put(DB_DWHREP, new
      // DatabaseConnection(TechPackIdeStarter.getDwhRepDbConnection()));

      conns.put(TestSetupConstants.DB_ETLREP, getConn(TestSetupConstants.DB_ETLREP));
      conns.put(TestSetupConstants.DB_DWHREP, getConn(TestSetupConstants.DB_DWHREP));

      // conns.put(DB_DWH, new
      // DatabaseConnection(TechPackIdeStarter.getDwhDbConnection()));
      // System.out.println("Initialize db connection succesfully. conns.size="
      // + conns.size() + ", conns="
      // + conns.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves a database connection to repdb
   * @return database connection or null
   */
  private static IDatabaseConnection getConn(String dbName) throws SQLException {	  
    IDatabaseConnection ret = null;
    try {
      Connection c = null;
      if (dbName.equalsIgnoreCase("dwhrep")){
    	  c = SybaseDBSetup.getRockFactory(Schema.dwhrep).getConnection();
      }else if(dbName.equalsIgnoreCase("etlrep")){
    	  c = SybaseDBSetup.getRockFactory(Schema.etlrep).getConnection();
      }else if(dbName.equalsIgnoreCase("dwh")){
    	  c = SybaseDBSetup.getRockFactory(Schema.dc).getConnection();
      }else{
    	  System.out.println("DatabaseTester.getConn(): Check the DbName passed here");
      }
      ret = new DatabaseConnection(c);
    } catch (DatabaseUnitException e) {
      e.printStackTrace();
    }
    return ret;
  }

}
