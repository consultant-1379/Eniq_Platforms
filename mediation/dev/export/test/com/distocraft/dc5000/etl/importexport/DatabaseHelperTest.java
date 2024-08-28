package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;

public class DatabaseHelperTest {

  private DatabaseHelper testInstance = null;

  private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";

  private static final String DB_URL = "jdbc:hsqldb:mem:gpmgt";

  private static RockFactory testFactory = null;

  @Before
  public void setUp() throws Exception {
    StaticProperties.giveProperties(new Properties());
    testFactory = new RockFactory(DB_URL, "SA", "", TESTDB_DRIVER, "", true);
    DatabaseTestUtils.loadSetup(testFactory, "gpmgt");
    final Properties p = new Properties();
    p.setProperty("ENGINE_DB_URL", DB_URL);
    p.setProperty("ENGINE_DB_DRIVERNAME", TESTDB_DRIVER);
    p.setProperty("ENGINE_DB_USERNAME", "SA");
    p.setProperty("ENGINE_DB_PASSWORD", "");
    GroupMgtHelper.setEtlcProperties(p);
    final Properties gpmgt = new Properties();
    gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".events.tpname", "EVENT_E_SGEH");
    GroupMgtHelper.setGpmgtProperties(gpmgt);
    testInstance = new DatabaseHelper(gpmgt);
  }

  @After
  public void tearDown() throws Exception {
    if (testInstance != null) {
      testInstance.destroy();
    }
    testInstance = null;
    try {
      testFactory.getConnection().createStatement().execute("SHUTDOWN");
    } catch (SQLException e) {/**/
    }
  }

  @Test
  public void testDestroy() throws Exception {
    RockFactory test = testInstance.getDwhdb();
    testInstance.closeDwhdb();
    assertTrue(test.getConnection().isClosed());
    test = testInstance.getDwhrep();
    testInstance.closeDwhrep();
    assertTrue(test.getConnection().isClosed());
  }

  @Test
  public void testGetDwhrep() throws Exception {
    final RockFactory rf = testInstance.getDwhrep();
    assertFalse(rf.getConnection().isClosed());
    final RockFactory copy = testInstance.getDwhrep();
    assertEquals(rf, copy);
    testInstance.destroy();
    assertTrue(rf.getConnection().isClosed());
  }

  @Test
  public void testGetDwhdb() throws Exception {
    final RockFactory rf = testInstance.getDwhdb();
    assertFalse(rf.getConnection().isClosed());
    final RockFactory copy = testInstance.getDwhdb();
    assertEquals(rf, copy);
    testInstance.destroy();
    assertTrue(rf.getConnection().isClosed());
  }

  @Test
  public void testGetVersionId() throws Exception {
    final String vId = testInstance.getActiveTechpackVersion();
    assertEquals("EVENT_E_SGEH:((11))", vId);
  }

  @Test
  public void testIsTableEmpty() throws Exception {
    final Statement stmt = testFactory.getConnection().createStatement();
    String tableName = "some_test_table";
    stmt.executeUpdate("create table "+testInstance.getDcSchema()+tableName+" ( cola varchar(32))");
    assertTrue("Table is empty, TRUE should have been returned", testInstance.isTableEmpty(tableName, testFactory));
    stmt.executeUpdate("insert into " + testInstance.getDcSchema() + tableName + " values ('a')");
    assertFalse("Table has one row in it, FALSE should have been returned",
        testInstance.isTableEmpty(tableName, testFactory));
  }

  @Test
  public void testAfterLoadAction() throws IOException {
    final File f = new File(System.getProperty("java.io.tmpdir"), "someFile.txt");
    // no errors should be thrown about non exiting file....
    testInstance.afterLoadAction(null, AfterLoadAction.delete.name());
    testInstance.afterLoadAction(f, AfterLoadAction.delete.name());
    // noinspection ResultOfMethodCallIgnored
    f.createNewFile();
    f.deleteOnExit();
    testInstance.afterLoadAction(f, AfterLoadAction.none.name());
    assertTrue(f.exists());
    testInstance.afterLoadAction(f, AfterLoadAction.delete.name());
    assertFalse(f.exists());
  }

  @Test
  public void testDropTables() throws Exception {
    final Statement stmt = testFactory.getConnection().createStatement();
    String tableName = "someTable";
    stmt.executeUpdate("create table dc." + tableName + " (cola varchar(32));");
    stmt.close();
    testInstance.dropTmpTables(tableName);
    final Statement stmt1 = testFactory.getConnection().createStatement();
    try {
      stmt1.executeQuery("select * from " + tableName);
      fail("Table should have been deleted.");
    } catch (SQLException e) {
    	 final List<String> l = Arrays.asList("S0002", "42501");
         assertTrue(l.contains(e.getSQLState()));

    }
  }

  @Test
  public void testDropTableNonExisting() throws Exception {
    String tableName = "someTable";
    testInstance.dropTmpTables(tableName);// no error should be throw....
  }

  @Test
  public void testgetNumberOfGroupsForRatValue() throws Exception {
    final Statement stmt = testFactory.getConnection().createStatement();
    String tableName = "testGroupTable";
    String tmpTableName = "TMP_testGroupTable";
    stmt.executeUpdate("create table dc."
        + tableName
        + " (GROUP_NAME varchar(64),START_TIME timestamp,STOP_TIME timestamp,HIERARCHY_1 varchar(128),HIERARCHY_2 varchar(128),HIERARCHY_3 varchar(128),RAT tinyint,VENDOR varchar(20))");
    stmt.executeUpdate("create table dc."
        + tmpTableName
        + " (GROUP_NAME varchar(64),START_TIME timestamp,STOP_TIME timestamp,HIERARCHY_1 varchar(128),HIERARCHY_2 varchar(128),HIERARCHY_3 varchar(128),RAT tinyint,VENDOR varchar(20))");
    stmt.executeUpdate("insert into dc." + tableName
        + " values ('DG_GroupNameRATVENDHIER321_250',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',1,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tableName
        + " values ('DG_GroupNameRATVENDHIER321_249',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',1,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tableName
        + " values ('DG_GroupNameRATVENDHIER321_248',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',0,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tableName
        + " values ('DG_GroupNameRATVENDHIER321_247',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',0,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tableName
        + " values ('DG_GroupNameRATVENDHIER321_246',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',0,'ERICSSON')");

    stmt.executeUpdate("insert into dc." + tmpTableName
        + " values ('DG_GroupNameRATVENDHIER321_251',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',1,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tmpTableName
        + " values ('DG_GroupNameRATVENDHIER321_252',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',1,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tmpTableName
        + " values ('DG_GroupNameRATVENDHIER321_253',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',0,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tmpTableName
        + " values ('DG_GroupNameRATVENDHIER321_254',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',0,'ERICSSON')");
    stmt.executeUpdate("insert into dc." + tmpTableName
        + " values ('DG_GroupNameRATVENDHIER321_255',null,null,'1055',null,'ONRM_RootMo_R:RNC02:RNC02',0,'ERICSSON')");

    stmt.close();
    assertEquals(4, testInstance.getNumberOfGroupsForRatValue(tableName, tmpTableName, testFactory, 1));
    assertEquals(6, testInstance.getNumberOfGroupsForRatValue(tableName, tmpTableName, testFactory, 0));
    testInstance.dropTmpTables(tableName);
    testInstance.dropTmpTables(tmpTableName);
  }

  @Test
  public void testGetNumberOfGroupsForGroupType() throws Exception {
    final Statement stmt = testFactory.getConnection().createStatement();
    final String tableName = "testGroupTable";
    final String tmpTableName = "TMP_testGroupTable";
    final String fpDefaultGroupName = "TopDataConsumers_%";

    // table to import to
    stmt.executeUpdate("create table dc." + tableName
        + " (GROUP_NAME varchar(64),START_TIME timestamp,STOP_TIME timestamp, IMSI varchar(64))");
    // temporary table to import from
    stmt.executeUpdate("create table dc." + tmpTableName
        + " (GROUP_NAME varchar(64),START_TIME timestamp,STOP_TIME timestamp, IMSI varchar(64))");

    // insert FeaturePack-specific groups with some IMSIs
    stmt.executeUpdate("insert into dc." + tableName + " values ('TopDataConsumers_010101',null,null,'111')");
    stmt.executeUpdate("insert into dc." + tableName + " values ('TopDataConsumers_010102',null,null,'222')");
    // insert non-FeaturePack-specific groups with some IMSIs
    stmt.executeUpdate("insert into dc." + tableName + " values ('non-TopDataConsumers_1',null,null,'1')");
    stmt.executeUpdate("insert into dc." + tableName + " values ('non-TopDataConsumers_2',null,null,'2')");

    // import existing FeaturePack-specific groups with some new IMSIs
    stmt.executeUpdate("insert into dc." + tmpTableName + " values ('TopDataConsumers_010101',null,null,'111111')");
    stmt.executeUpdate("insert into dc." + tmpTableName + " values ('TopDataConsumers_010102',null,null,'222222')");
    // import new FeaturePack-specific groups with some IMSIs
    stmt.executeUpdate("insert into dc." + tmpTableName + " values ('TopDataConsumers_010104',null,null,'444')");
    stmt.executeUpdate("insert into dc." + tmpTableName + " values ('TopDataConsumers_010105',null,null,'555')");
    // import existing non-FeaturePack-specific groups with some new IMSIs
    stmt.executeUpdate("insert into dc." + tableName + " values ('non-TopDataConsumers_1',null,null,'11')");
    stmt.executeUpdate("insert into dc." + tableName + " values ('non-TopDataConsumers_2',null,null,'22')");
    // import new non-FeaturePack-specific groups with some IMSIs
    stmt.executeUpdate("insert into dc." + tableName + " values ('non-TopDataConsumers_3',null,null,'3')");
    stmt.executeUpdate("insert into dc." + tableName + " values ('non-TopDataConsumers_4',null,null,'4')");

    stmt.close();
    // check a number of:
    // existing non-FeaturePack-specific groups + new non-FeaturePack-specific
    // groups + imported FeaturePack-specific groups
    assertEquals(6,
        testInstance.getNumberOfGroupsForGroupType(tableName, tmpTableName, fpDefaultGroupName, testFactory));

    testInstance.dropTmpTables(tableName);
    testInstance.dropTmpTables(tmpTableName);
  }
}
