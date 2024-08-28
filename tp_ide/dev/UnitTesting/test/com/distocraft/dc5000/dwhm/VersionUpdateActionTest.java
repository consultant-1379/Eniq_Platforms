package com.distocraft.dc5000.dwhm;

import java.io.File;
import java.sql.SQLException;

import junit.framework.AssertionFailedError;

import org.dbunit.Assertion;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.unittesting.DWHMemDB;
import com.ericsson.eniq.unittesting.DWHRepMemDB;
import com.ericsson.eniq.unittesting.ETLRepMemDB;

public class VersionUpdateActionTest extends DatabaseTestCase {

  // Database Objects
  private DWHRepMemDB dbDwhRep = new DWHRepMemDB();

  private ETLRepMemDB dbEtlRep = new ETLRepMemDB();

  private DWHMemDB dbDwh = new DWHMemDB();

  // Files Directories
  private String xmlDir = "test/XMLFiles/VersionUpdateActionTest/";

  // Rock Factories
  private RockFactory rckDwhRep;

  private RockFactory rckEtlRep;

  private RockFactory rckDwh;

  protected void setUp() throws Exception {
    super.setUp();

    try {
      // this.dbDwhRep.createCompleteDB();
      // this.dbDwhRep.createTables();
      // dbDwhRep.readJarFile();
      this.dbDwhRep.insertDBData();
      this.rckDwhRep = this.dbDwhRep.getDBRockFactory();

      this.dbEtlRep.createCompleteDB();
      this.rckEtlRep = this.dbEtlRep.getDBRockFactory();

      this.dbDwh.createCompleteDB();
      this.rckDwh = this.dbDwh.getDBRockFactory();

      /*
       * Uncomment this when you need to create XML versions of the tables in
       * the Databases
       * 
       * this.extractTables( "systemtables", this.dbEtlRep.getDWHRepTableNames()
       * ); this.extractTables( "systemtables",
       * this.dbEtlRep.getEtlRepTableNames() ); this.extractTables(
       * "systemtables", this.dbDwh.getDwhTableNames() ); this.extractTables(
       * "systemtables", this.dbDwh.getDwhSystemTableNames() );
       */
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void tearDown() {

    // Dropping the Tables
    try {
      this.dbDwhRep.dropDBTables();
      this.dbEtlRep.dropDBTables();
      this.dbDwh.dropDBTables();
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected IDatabaseConnection getConnection() throws Exception {
    return new DatabaseConnection(this.dbDwhRep.getConnection());
  }

  protected IDatabaseConnection getETLConnection() throws Exception {
    return new DatabaseConnection(this.dbEtlRep.getConnection());
  }

  protected IDatabaseConnection getDWHConnection() throws Exception {
    return new DatabaseConnection(this.dbDwh.getConnection());
  }

  @Override
  protected IDataSet getDataSet() throws Exception {
    IDataSet loadedDataSet = null;

    try {
      loadedDataSet = getConnection().createDataSet(this.dbDwhRep.getDwhRepTableNames());
      return loadedDataSet;
    }

    catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getCause());
      System.out.println(e.getMessage());
      System.out.println(e.getClass());
      System.out.println();
    }

    return loadedDataSet;
  }

  /*
   * Uncomment this when you need to create XML versions of the tables in the
   * Databases
   * 
   * public void extractTables(String targetDirectory, String[] tableNames)
   * throws Exception { IDatabaseConnection connection = getETLConnection();
   * 
   * for (int i = 0; i < tableNames.length; i++) { String tableName =
   * tableNames[i];
   * 
   * IDataSet partialDataSet = connection.createDataSet (new String[] {
   * tableName }); FlatXmlDataSet.write( partialDataSet, new
   * FileOutputStream(targetDirectory + "/" + tableName + ".xml") ); } }
   */
  public void testRowCount() {
    try {
      ITable expectedTable = getConnection().createQueryTable("VERSIONING", "Select * From VERSIONING");

      rckDwhRep
          .executeSql("insert into Versioning (versionid, description, status, techpack_name, techpack_version, techpack_type, product_number) values ('DC_E_PT:b(build)1','Ericsson Platform Test b(build)1',10,'DC_E_PT1','R1A_b(build)1','PM1','')");
      rckDwhRep.commit();

      // dbDwhRep.createTables();
      // dbDwhRep.readJarFile();

      ITable actualTable = getConnection().createQueryTable("VERSIONING", "Select * From VERSIONING");

      Assertion.assertEquals(expectedTable, actualTable);
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testDataEnteredIntoVersioning() {
    try {
      rckDwhRep
          .executeSql("insert into Versioning (versionid, description, status, techpack_name, techpack_version, techpack_type, product_number, basedefinition, "
              + "installdescription, universename, universeextension, lockedby, lockdate, baseversion) values "
              + "('DC_E_PT:c(build)','Ericsson Platform Test c(build)',1,'DC_E_PT_BSS','R1A_c(build)','PM', '', '', '', '', '', '', '2008-04-12 12:23:22.0', '');");

      rckDwhRep.commit();

      IDataSet actualDataSet = getConnection().createDataSet();
      ITable actualTable = actualDataSet.getTable("VERSIONING");

      IDataSet expectedDataSet = new FlatXmlDataSet(new File(this.xmlDir
          + "VERSIONING_testDataEnteredIntoVersioning.xml"));
      ITable expectedTable = expectedDataSet.getTable("VERSIONING");

      Assertion.assertEquals(expectedTable, actualTable);
    }

    catch (SQLException e) {
      e.printStackTrace();
    }

    catch (RockException e) {
      e.printStackTrace();
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testTwoDatabases() {
    try {

      rckDwhRep
          .executeSql("insert into Versioning (versionid, description, status, techpack_name, techpack_version, techpack_type, product_number, basedefinition, "
              + "installdescription, universename, universeextension, lockedby, lockdate, baseversion) values "
              + "('DC_E_PT:c(build)','Ericsson Platform Test c(build)',1,'DC_E_PT_BSS','R1A_c(build)','PM', '', '', '', '', '', '', '2008-04-12 12:23:22.0', '');");

      rckDwhRep.commit();

      this.rckEtlRep
          .executeSql("insert into META_COLLECTION_SETS (COLLECTION_SET_ID, COLLECTION_SET_NAME, DESCRIPTION, VERSION_NUMBER, ENABLED_FLAG, TYPE) values "
              + "(213, 'DWH_BASE', '', 'R1K_b49', 'Y', 'Maintenance')");

      IDataSet actualDataSet = getConnection().createDataSet();
      ITable actualTable = actualDataSet.getTable("VERSIONING");

      IDataSet expectedDataSet = new FlatXmlDataSet(new File(this.xmlDir + "VERSIONING_testTwoDatabases.xml"));
      ITable expectedTable = expectedDataSet.getTable("VERSIONING");

      IDataSet actualDataSetETL = this.getETLConnection().createDataSet();
      ITable actualTableETL = actualDataSetETL.getTable("META_COLLECTION_SETS");

      IDataSet expectedDataSetETL = new FlatXmlDataSet(new File(this.xmlDir
          + "META_COLLECTION_SETS_testTwoDatabases.xml"));
      ITable expectedTableETL = expectedDataSetETL.getTable("META_COLLECTION_SETS");

      Assertion.assertEquals(expectedTable, actualTable);
      Assertion.assertEquals(expectedTableETL, actualTableETL);
    }

    catch (SQLException e) {
      e.printStackTrace();
    }

    catch (RockException e) {
      e.printStackTrace();
    }

    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void testMulitpleTables() {
    try {

      rckDwhRep
          .executeSql("insert into Versioning (versionid, description, status, techpack_name, techpack_version, techpack_type, product_number, basedefinition, "
              + "installdescription, universename, universeextension, lockedby, lockdate, baseversion) values "
              + "('DC_E_PT:c(build)','Ericsson Platform Test c(build)',1,'DC_E_PT_BSS','R1A_c(build)','PM', '', '', '', '', '', '', '2008-04-12 12:23:22.0', '');");
      rckDwhRep.commit();

      rckDwhRep
          .executeSql("insert into TPActivation (Techpack_Name, Status, VersionID, Type) values ('DC_E_PT1', 'ACTIVE', 'DC_E_PT:b(build)1', 'PM');");
      rckDwhRep.commit();

      String[] testTables = { "VERSIONING", "TPACTIVATION" };
      String[] testTablesXMLFileNames = { "VERSIONING_testMulitpleTables.xml", "TPACTIVATION_testMulitpleTables.xml" };
      IDataSet actualDataSet = getConnection().createDataSet(testTables);

      for (int i = 0; i < testTables.length; i++) {
        ITable actualTable = actualDataSet.getTable(testTables[i]);

        IDataSet expectedDataSet = new FlatXmlDataSet(new File(this.xmlDir + testTablesXMLFileNames[i]));
        ITable expectedTable = expectedDataSet.getTable(testTables[i]);

        Assertion.assertEquals(expectedTable, actualTable);
      }

    }

    catch (SQLException e) {
      e.printStackTrace();
    }

    catch (RockException e) {
      e.printStackTrace();
    }

    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void testNum() {
    boolean testFailed = false;

    try {
      assertEquals(2, 1);
    }

    catch (AssertionFailedError e) {
      System.out.println(e);
      System.out.println(e.getStackTrace());
      System.out.println("Cause: " + e.getCause());
      System.out.println("Class: " + e.getClass());
      System.out.println(e.getLocalizedMessage());
      e.printStackTrace();
      // System.out.print("1st Failed");
      testFailed = true;
    }

    try {
      assertEquals(5, 5);
    }

    catch (AssertionFailedError e) {
      System.out.print("2nd Failed");
      testFailed = true;
    }

    try {
      assertEquals(4, 4);
    }

    catch (AssertionFailedError e) {
      System.out.print("3rd Failed");
      testFailed = true;
    }

    if (testFailed) {
      assertEquals("All Should Suceed", "But Some Failed, Check the System.out");
    }
  }

  /*
   * public void testTwoTables() { Logger log = Logger.getLogger( "etlengine" );
   * 
   * try { VersionUpdateAction vua = new VersionUpdateAction(rckDwhRep, rckDwh,
   * "DC_E_PT", log); vua.execute();
   * 
   * IDataSet actualDataSet = getConnection().createDataSet(); ITable
   * actualTable = actualDataSet.getTable( "DWHTYPE" );
   * 
   * IDataSet expectedDataSet = new FlatXmlDataSet(new File(this.xmlDir +
   * "DWHTYPE_testTwoTables.xml")); ITable expectedTable =
   * expectedDataSet.getTable( "DWHTYPE" );
   * 
   * Assertion.assertEquals( expectedTable, actualTable ); } catch ( Exception e
   * ) { e.printStackTrace(); } }
   */

  /*
   * public void testVerifyVersions() { try {
   * 
   * ITable expectedTable = getConnection().createQueryTable( "Deck",
   * "Select * From " + "VERSIONING" );
   * 
   * int rowCount = expectedTable.getRowCount();
   * 
   * rckDwhRep.executeSql(
   * "insert into Versioning (versionid, description, status, techpack_name, techpack_version, techpack_type, product_number) values ('DC_E_PT:b(build)1','Ericsson Platform Test b(build)1',10,'DC_E_PT1','R1A_b(build)1','PM1','')"
   * ); rckDwhRep.commit();
   * 
   * // dbConn.reinitializeDB();
   * 
   * ITable actualTable = getConnection().createQueryTable( "Deck",
   * "Select * From " + "VERSIONING" );
   * 
   * //assertEquals( (rowCount + 1) , actualTable.getRowCount());
   * //assertNotSame( (rowCount) , actualTable.getRowCount()); assertEquals(
   * "DC_E_PT:b(build)1", actualTable.getValue( actualTable.getRowCount(),
   * "versionid" )); //assertEquals(1,1); }
   * 
   * catch ( Exception e ) { e.printStackTrace(); }
   * 
   * }
   */
}
