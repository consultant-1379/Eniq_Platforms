package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import org.junit.*;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.etl.importexport.gpmgt.*;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.*;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.dwhrep.*;
import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;

public class GroupMgtImporterTest {

    private GroupMgtImporter tInstance = null;

    private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";

    private static final String DB_URL = "jdbc:hsqldb:mem:gpmgt";

    private static final String FP_GROUPS_NAME_PREFIX = "TopDataConsumers_";

    private static RockFactory testFactory = null;

    private static final String versionId = "EVENT_E_SGEH:((11))";

    private final static String loaderOptions = "QUOTES OFF ESCAPES OFF FORMAT bcp DELIMITED BY ',' IGNORE CONSTRAINT UNIQUE 1 STRIP RTRIM";

    private final Properties gpmgt = new Properties();

    private static Map<String, String> env = System.getenv();

    private transient ObjectFactory xmlObjectFactory;

    @Before
    public void setUp() throws Exception {
        StaticProperties.giveProperties(new Properties());
        System.setProperty("loader.dir", env.get("WORKSPACE"));
        final Properties p = new Properties();
        p.setProperty("ENGINE_DB_URL", DB_URL);
        p.setProperty("ENGINE_DB_DRIVERNAME", TESTDB_DRIVER);
        p.setProperty("ENGINE_DB_USERNAME", "SA");
        p.setProperty("ENGINE_DB_PASSWORD", "");
        GroupMgtHelper.setEtlcProperties(p);

        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.append.template", "templates/merge_append.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.empty.template", "templates/delete_table.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".loader.options", loaderOptions);
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.delete.template", "templates/merge_delete.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".row.seperator", "\n");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".col.wrapper", "");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".events.tpname", "EVENT_E_SGEH");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".col.seperator", ",");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.empty.template", "templates/merge_empty.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".etlc.properties", "/eniq/sw/conf/ETLCServer.properties");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".after.load", "delete");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".events.maxgroups", "2");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".events.fpgroupsname", "TopDataConsumers_%");
        GroupMgtHelper.setGpmgtProperties(gpmgt);
        testFactory = new RockFactory(DB_URL, "SA", "", TESTDB_DRIVER, "", true);
        DatabaseTestUtils.loadSetup(testFactory, "gpmgt");
        tInstance = new GroupMgtImporter();
        xmlObjectFactory = new ObjectFactory();
    }

    @After
    public void tearDown() throws Exception {
        tInstance.destroy();
        tInstance = null;
        gpmgt.clear();
        shutdown();
    }

    private void shutdown() {
        try {
            testFactory.getConnection().createStatement().execute("SHUTDOWN");
        } catch (final SQLException e) {/**/
        }
    }

    @Test
    public void testParseInvalidFile() throws Exception {
        final File testData = getDataFile("xml/invalid_tac.xml");
        try {
            tInstance.importDataFileAppend(testData.getAbsolutePath(), versionId);
            fail("GroupMgtInvalidDataException should have been throws");
        } catch (final GroupMgtParseException e) {
            assertTrue(e.getMessage().contains("Error parsing Group Management Data file"));
            final Map<String, String> errors = e.getAdditionalInfo();
            assertFalse("Expecting errors in the additional info field", errors.isEmpty());
        }
    }

    @Test
    public void testLoadNoFile() throws Exception {
        try {
            tInstance.importDataFileAppend(null, null);
            fail("GroupMgtFileNotFoundException should have been throws");
        } catch (final GroupMgtFileNotFoundException e) {
            // expected this
        }
        try {
            tInstance.importDataFileAppend("some_file" + System.currentTimeMillis() + ".txt", null);
            fail("GroupMgtFileNotFoundException should have been throws");
        } catch (final GroupMgtFileNotFoundException e) {
            // expected this
        }
    }

    @Test
    public void testLoadGroupsInvalidKey() throws Exception {
        final File testData = getDataFile("xml/imsi_2.xml");
        try {
            tInstance.importDataFileAppend(testData.getAbsolutePath(), versionId);
            fail("GroupMgtInvalidDataException should have been throws");
        } catch (final GroupMgtInvalidDataException e) {
            assertEquals("wrong_key", e.getAdditionalInfo("keyname:"));
        }
    }

    @Test
    public void testLoadNoGroupsInTechPack() throws Exception {
        final File testData = getDataFile("xml/imsi.xml");
        try {
            tInstance.importDataFileAppend(testData.getAbsolutePath(), versionId + "hh");
            fail("GroupMgtTypeNotDefinedException should have been throws");
        } catch (final GroupMgtTypeNotDefinedException e) {
            assertEquals("*Any*", e.getAdditionalInfo("grouptype:"));
        }
    }

    @Test
    public void testLoadNoGroupInTechPack() throws Exception {
        final File testData = getDataFile("xml/imsi_3.xml");
        try {
            tInstance.importDataFileAppend(testData.getAbsolutePath(), versionId);
            fail("GroupMgtTypeNotDefinedException should have been throws");
        } catch (final GroupMgtTypeNotDefinedException e) {
            assertEquals("THIS_DOES_NOT_EXIST", e.getAdditionalInfo("grouptype:"));
        }
    }

    private File getDataFile(final String uri) throws URISyntaxException {
        final URL url = ClassLoader.getSystemResource(uri);
        assertNotNull("Couldnt load test data", url);
        return new File(url.toURI().getRawPath());
    }

    private void doImport(final String loadData, final int type, final String vId) throws URISyntaxException, SQLException, RockException,
            IOException, NoSuchAlgorithmException {
        final File testData = getDataFile(loadData);
        final StorageTimeAction sta = new StorageTimeAction(Logger.getAnonymousLogger()) {
            @Override
            public String getPartitionCreateStatement(final Dwhtype type, final Dwhpartition partition, final Vector<Dwhcolumn> columns)
                    throws Exception {
                String sql = super.getPartitionCreateStatement(type, partition, columns);
                sql = sql.replace("NOT NULL IQ UNIQUE (255)", "");
                sql = sql.replace("NULL IQ UNIQUE (255)", "");
                sql = sql.replace("unsigned bigint", "int");
                sql = sql.replace("unsigned int", "bigint");
                return sql.substring(0, sql.indexOf(';') + 1);
            }
        };
        final DatabaseHelper dbProxy = new DatabaseHelper(gpmgt) {
            @Override
            protected StorageTimeAction getStorageTimeAction() {
                return sta;
            }

            @Override
            public String getTempTableDDL(final String tmpTableName, final GroupTypeDef gpType) throws RockException, SQLException, IOException {
                final String sql = super.getTempTableDDL(tmpTableName, gpType);
                return sql.substring(0, sql.indexOf(");") + 2);
            }

            @Override
            protected String getDeleteTableSql(final String tableName) {
                return "";
            }

            @Override
            protected int doLoad(final Statement stmt, final String loadStmt, final File fileToLoad, final String afterAction) throws SQLException,
                    IOException {
                // !!!!!!!!!! HSQL doesnt like the IQ 'LOAD TABLE....' statements.....
                String newLoadStmt = loadStmt.substring(0, loadStmt.indexOf(')') + 1);
                newLoadStmt = newLoadStmt.replace("LOAD TABLE ", "insert into ");
                final BufferedReader br = new BufferedReader(new FileReader(fileToLoad));
                int totalLoaded = 0;
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String sql;
                        if (type == 1) {
                            final String dummy = "'" + line.replace(",", "', '").replace("''", "null") + "'";
                            sql = newLoadStmt + " values (" + dummy + ")";
                        } else {
                            final StringBuilder sb = new StringBuilder();
                            final StringTokenizer st = new StringTokenizer(line, ",");
                            sb.append("'").append(st.nextToken()).append("',");
                            String val = null;
                            while (st.hasMoreTokens()) {
                                val = st.nextToken();
                            }
                            sb.append("'2000-01-01 12:00:00', '2000-01-01 12:00:00','");
                            sb.append(val).append("'");
                            sql = newLoadStmt + " values (" + sb.toString() + ")";
                        }
                        totalLoaded += stmt.executeUpdate(sql);
                    }
                } finally {
                    br.close();
                    afterLoadAction(fileToLoad, AfterLoadAction.none.name());
                }
                System.out.println("Loaded " + totalLoaded + " rows");
                return totalLoaded;
            }
        };
        final StubbedGroupMgtImporter tester = new StubbedGroupMgtImporter() {
            @Override
            protected DatabaseHelper initDbHelper(final Properties props) {
                return dbProxy;
            }
        };
        if (type == 1) {
            tester.importDataFileAppend(testData.getAbsolutePath(), vId);
        } else if (type == 2) {
            tester.importDataFileDelete(testData.getAbsolutePath(), vId);
        }
    }

    @Test
    public void testImportDelete() throws Exception {
        tearDown();
        /**
         * Using H2 as HSQL can handle the delete statement that used on IQ................
         */
        final String h2Driver = "org.h2.Driver";
        final String h2Url = "jdbc:h2:mem:h2gptests";
        testFactory = new RockFactory(h2Url, "SA", "", h2Driver, "", true);

        Statement stmt = testFactory.getConnection().createStatement();
        stmt.execute("create user DBA password '' ADMIN");
        stmt.execute("CREATE user etlrep password 'etlrep' ADMIN");
        stmt.execute("CREATE user dwhrep password 'dwhrep' ADMIN");
        stmt.execute("CREATE user dc password 'dc' ADMIN");
        stmt.execute("CREATE SCHEMA etlrep AUTHORIZATION etlrep");
        stmt.execute("CREATE SCHEMA dwhrep AUTHORIZATION dwhrep");
        stmt.execute("SET SCHEMA_SEARCH_PATH etlrep, dwhrep");
        stmt.close();
        DatabaseTestUtils.loadSetup(testFactory, "gpmgtDelete");

        final Properties p = new Properties();
        p.setProperty("ENGINE_DB_URL", h2Url + ";SCHEMA_SEARCH_PATH=etlrep,dwhrep,dc");
        p.setProperty("ENGINE_DB_DRIVERNAME", h2Driver);
        p.setProperty("ENGINE_DB_USERNAME", "etlrep");
        p.setProperty("ENGINE_DB_PASSWORD", "etlrep");
        GroupMgtHelper.setEtlcProperties(p);

        tInstance = new GroupMgtImporter();

        final String tableName = "dc.GROUP_TYPE_E_APN";
        stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,APN int \n" + ");";
        stmt.executeUpdate(cTable);
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, APN) values ('apn-1', '1')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, APN) values ('apn-2', '2')");
        stmt.close();
        final String expectedName = "apn-2";
        final String expectedValue = "2";
        try {
            doImport("xml/apn.xml", 2, versionId);
            stmt = testFactory.getConnection().createStatement();
            final ResultSet rs = stmt.executeQuery("select GROUP_NAME, APN from " + tableName);
            final Map<String, String> results = new HashMap<String, String>();
            while (rs.next()) {
                final String name = rs.getString("GROUP_NAME");
                final String value = rs.getString("APN");
                results.put(name, value);
            }
            rs.close();
            assertEquals(1, results.size());
            assertEquals(expectedName, results.keySet().iterator().next());
            assertEquals(expectedValue, results.get(expectedName));
        } catch (final Throwable t) {
            t.printStackTrace();
        } finally {
            shutdown();
        }
    }

    @Test
    public void testImportInvalidData() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        final Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('imsi-4', '901')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('someGroup', '902')");
        stmt.close();
        try {
            doImport("xml/imsi_invalid_data.xml", 1, versionId);
        } catch (final GroupMgtImportException e) {
            final Map<String, String> errors = e.getAdditionalInfo();
            assertFalse(errors.isEmpty());
            assertTrue(errors.get("error:").equals("data exception: invalid character value for cast"));
        }
    }

    @Test
    public void testImportAppendPopulatedTable() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('imsi-4', '901')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('someGroup', '902')");
        stmt.close();

        stmt.close();
        doImport("xml/imsi.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("93", "42", "901", "902");
        final List<String> expectedNames = Arrays.asList("imsi-4", "someGroup");

        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String imsi = rs.getString("IMSI");
                assertTrue("Unexpected group value", expectedValues.contains(imsi));
            }
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name ", recordedNames.contains(eName));
        }
    }

    @Test
    public void testImportAppendPopulatedTableForTac() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_TAC";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,TAC int \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, TAC) values ('tac-group-1', '901')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, TAC) values ('someGroup', '902')");
        stmt.close();

        stmt.close();
        doImport("xml/tac.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("123", "456", "901", "902");
        final List<String> expectedNames = Arrays.asList("tac-group-1", "someGroup");

        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String imsi = rs.getString("TAC");
                assertTrue("Unexpected group value", expectedValues.contains(imsi));
            }
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name ", recordedNames.contains(eName));
        }
    }

    @Test
    public void testExceptionThrownWhenAddExclusiveTacElementToOtherTacGroup() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_TAC";
        final Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,TAC int \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, TAC) values ('EXCLUSIVE_TAC', '901')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, TAC) values ('EXCLUSIVE_TAC', '123')");
        stmt.close();

        stmt.close();

        try {
            doImport("xml/tac.xml", 1, versionId);
            fail("Should not get here..");
        } catch (final Exception e) {
            assertEquals("\nThese TAC value(s) are present in the group 'EXCLUSIVE_TAC' "
                    + "and cannot be present in other groups:  123  901 .\nIMPORT FAILED.", e.getMessage());
        }
    }

    @Test
    public void testImportAppendPopulatedTableForForRat() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_RAT_VEND_HIER3";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + ", HIER3_ID BIGINT, HIERARCHY_3 varchar (64), RAT int, VENDOR varchar (64) \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName
                + " (GROUP_NAME, HIER3_ID, HIERARCHY_3, RAT, VENDOR) values ('someGroup', '901', 'RNC1', '1','Ericsson')");
        stmt.close();

        stmt.close();
        doImport("xml/rat_vend_hier3.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("RNC1", "ONRM_RootMo_R:RNC01:RNC01");
        final List<String> expectedNames = Arrays.asList("someGroup", "RNC_Group1");

        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String hier3 = rs.getString("HIERARCHY_3");
                assertTrue("Unexpected group value", expectedValues.contains(hier3));
            }
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name ", recordedNames.contains(eName));
        }
    }

    @Test
    public void testImportEmptyGroupsForRatDoesNotContainDuplicates() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_RAT_VEND_HIER3";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + ", HIER3_ID BIGINT, HIERARCHY_3 varchar (64), RAT int, VENDOR varchar (64) \n" + ");";
        stmt.executeUpdate(cTable);
        stmt.close();

        stmt.close();
        doImport("xml/rat_vend_hier3_with_multiple_rats.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();

        final ResultSet numberOfRows = stmt.executeQuery("select count(*) from " + tableName);

        while (numberOfRows.next()) {
            assertEquals(2, numberOfRows.getInt(1));
        }

        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("ONRM_RootMo_R:RNC01:RNC01");
        final List<String> expectedNames = Arrays.asList("RNC_Group_2", "RNC_Group1");

        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String hier3 = rs.getString("HIERARCHY_3");
                assertTrue("Unexpected group value", expectedValues.contains(hier3));
            }
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name ", recordedNames.contains(eName));
        }
    }

    @Test
    public void checkGroupIsNotImportedWhenGroupLimitIsExceededForRat() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_RAT_VEND_HIER3";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + ", HIER3_ID BIGINT, HIERARCHY_3 varchar (64), RAT int, VENDOR varchar (64) \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName
                + " (GROUP_NAME, HIER3_ID, HIERARCHY_3, RAT, VENDOR) values ('someGroup1', '901', 'RNC3', '1','Ericsson')");
        stmt.executeUpdate("insert into " + tableName
                + " (GROUP_NAME, HIER3_ID, HIERARCHY_3, RAT, VENDOR) values ('someGroup', '901', 'RNC1', '1','Ericsson')");
        stmt.close();

        stmt.close();
        doImport("xml/rat_vend_hier3.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("901", "902");
        final List<String> expectedNames = Arrays.asList("someGroup1", "someGroup");

        int expectedNumberOfResults = 0;
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (gName.equalsIgnoreCase("imsi-4")) {
                    fail("Group Type = 'imsi-4' should not have been added");
                }
                final String hier3_id = rs.getString("HIER3_ID");
                assertTrue("Unexpected group value", expectedValues.contains(hier3_id));
                expectedNumberOfResults++;
            }
        } finally {
            stmt.close();
        }
        assertEquals(expectedNumberOfResults, 2);
    }

    @Test
    public void checkGroupIsNotImportedWhenGroupLimitIsExceeded() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('imsi-5', '901')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('someGroup', '902')");
        stmt.close();

        stmt.close();
        doImport("xml/imsi.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("93", "42", "901", "902");
        final List<String> expectedNames = Arrays.asList("imsi-5", "someGroup");

        int expectedNumberOfResults = 0;
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (gName.equalsIgnoreCase("imsi-4")) {
                    fail("Group Type = 'imsi-4' should not have been added");
                }
                final String imsi = rs.getString("IMSI");
                assertTrue("Unexpected group value", expectedValues.contains(imsi));
                expectedNumberOfResults++;
            }
        } finally {
            stmt.close();
        }
        assertEquals(expectedNumberOfResults, 2);
    }

    @Test
    public void checkGroupIsNotImportedWhenGroupLimitIsExceededAndFpGroupsExist() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);

        // Simulate FeaturePack-specific non-manual updates (adding the
        // FeaturePack-specific groups with IMSIs)
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('" + FP_GROUPS_NAME_PREFIX + "010101" + "', '111')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('" + FP_GROUPS_NAME_PREFIX + "010102" + "', '222')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('" + FP_GROUPS_NAME_PREFIX + "010103" + "', '333')");

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('imsi-5', '901')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('someGroup', '902')");

        stmt.close();

        stmt.close();
        doImport("xml/imsi.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedFpValues = Arrays.asList("111", "222", "333");
        final List<String> expectedFpNames = Arrays.asList(FP_GROUPS_NAME_PREFIX + "010101", FP_GROUPS_NAME_PREFIX + "010102", FP_GROUPS_NAME_PREFIX
                + "010103");
        final List<String> expectedValues = Arrays.asList("93", "42", "901", "902");
        final List<String> expectedNames = Arrays.asList("imsi-5", "someGroup");

        int expectedNumberOfResults = 0;
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedFpNames.contains(gName) || expectedNames.contains(gName));
                if (gName.equalsIgnoreCase("imsi-4")) {
                    fail("Group Type = 'imsi-4' should not have been added");
                }
                final String imsi = rs.getString("IMSI");
                assertTrue("Unexpected group value", expectedFpValues.contains(imsi) || expectedValues.contains(imsi));
                expectedNumberOfResults++;
            }
        } finally {
            stmt.close();
        }
        // expected result = all existing FeaturePack-specific groups(3) + all
        // existing non-FeaturePack-specific groups(2)
        // (without any imported group and without any imported value of existing
        // group) = 5
        assertEquals(expectedNumberOfResults, 5);
    }

    @Test
    public void checkGroupIsNotImportedWhenGroupLimitIsExceededWithFpAndNonFpGroups() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);

        // Simulate FeaturePack-specific non-manual updates (adding the
        // FeaturePack-specific groups with IMSIs)
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('" + FP_GROUPS_NAME_PREFIX + "010101" + "', '111')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('" + FP_GROUPS_NAME_PREFIX + "010102" + "', '222')");
        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('" + FP_GROUPS_NAME_PREFIX + "010103" + "', '333')");

        stmt.executeUpdate("insert into " + tableName + " (GROUP_NAME, IMSI) values ('imsi-5', '901')");

        stmt.close();

        stmt.close();
        doImport("xml/imsi_fpgroups.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedFpValues = Arrays.asList("111", "222", "333");
        final List<String> expectedFpNames = Arrays.asList(FP_GROUPS_NAME_PREFIX + "010101", FP_GROUPS_NAME_PREFIX + "010102", FP_GROUPS_NAME_PREFIX
                + "010103");
        final List<String> expectedNonFpValues = Arrays.asList("901");
        final List<String> expectedNonFpNames = Arrays.asList("imsi-5");

        int expectedNumberOfResults = 0;
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedFpNames.contains(gName) || expectedNonFpNames.contains(gName));
                if (gName.equalsIgnoreCase("imsi-4") || gName.equalsIgnoreCase("TopDataConsumers_010104")) {
                    fail("Group Type = 'imsi-4' and 'TopDataConsumers_010104' should not have been added");
                }
                final String imsi = rs.getString("IMSI");
                assertTrue("Unexpected group value", expectedFpValues.contains(imsi) || expectedNonFpValues.contains(imsi));
                expectedNumberOfResults++;
            }
        } finally {
            stmt.close();
        }
        // expected result = all existing FeaturePack-specific groups(3) + all
        // existing non-FeaturePack-specific groups(1)
        // (without any imported group and without any imported value of existing
        // group) = 4
        System.out.println("res=" + expectedNumberOfResults);
        assertEquals(expectedNumberOfResults, 4);
    }

    @Test
    public void testImportAppendDefaultVersionId() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);
        stmt.close();
        doImport("xml/imsi.xml", 1, null);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("93", "42");
        final List<String> expectedNames = Arrays.asList("imsi-4");

        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String imsi = rs.getString("IMSI");
                assertTrue("Unexpected group value", expectedValues.contains(imsi));
            }
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name '" + eName + "'", recordedNames.contains(eName));
        }
    }

    @Test
    public void testImportAppendEmptyTable() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_IMSI";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + " ,IMSI int \n" + ");";
        stmt.executeUpdate(cTable);
        stmt.close();
        doImport("xml/imsi.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("93", "42");
        final List<String> expectedNames = Arrays.asList("imsi-4");

        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                final String gName = rs.getString("GROUP_NAME");
                assertTrue("Unexpected group name", expectedNames.contains(gName));
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String imsi = rs.getString("IMSI");
                assertTrue("Unexpected group value", expectedValues.contains(imsi));
            }
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name '" + eName + "'", recordedNames.contains(eName));
        }
    }

    /** test cases for append and delete ***/
    @Test
    public void testAppendLoadGroupsInvalidKey() throws Exception {
        try {
            //    copied data from imsi_2.xml
            final Groupmgt groupMgt = xmlObjectFactory.createGroupmgt();

            final List<Group> groups = groupMgt.getGroup();
            //create group
            final Group group = createGroup("imsi-1", "IMSI");
            groups.add(group);
            //adding group element
            final List<GroupElement> groupElements = group.getGroupElement();
            final GroupElement groupElement = xmlObjectFactory.createGroupElement();
            groupElements.add(groupElement);
            //adding key to group element
            groupElement.getKey().add(createKey("wrong_key", "11"));

            tInstance.append(groupMgt, versionId);
            fail("GroupMgtInvalidDataException should have been throws");
        } catch (final GroupMgtInvalidDataException e) {
            assertEquals("wrong_key", e.getAdditionalInfo("keyname:"));
        }
    }

    @Test
    public void testAppendLoadNoGroupsInTechPack() throws Exception {
        try {
            // copied data from imsi.xml
            final Groupmgt groupMgt = xmlObjectFactory.createGroupmgt();

            final List<Group> groups = groupMgt.getGroup();
            //create group
            final Group group = createGroup("imsi-4", "imsi");
            groups.add(group);
            //adding group element1
            final List<GroupElement> groupElements = group.getGroupElement();
            final GroupElement groupElement1 = xmlObjectFactory.createGroupElement();
            groupElements.add(groupElement1);
            //adding key to group element1
            groupElement1.getKey().add(createKey("imsi", "93"));

            //adding group element2
            final GroupElement groupElement2 = xmlObjectFactory.createGroupElement();
            groupElements.add(groupElement2);
            //adding key to group element2
            groupElement2.getKey().add(createKey("imsi", "43"));
            tInstance.append(groupMgt, versionId + "hh");
            fail("GroupMgtTypeNotDefinedException should have been throws");
        } catch (final GroupMgtTypeNotDefinedException e) {
            assertEquals("*Any*", e.getAdditionalInfo("grouptype:"));
        }
    }

    @Test
    public void test3G_SAC_CELL() throws Exception {
        try {

            final Groupmgt groupMgt = xmlObjectFactory.createGroupmgt();

            final List<Group> groups = groupMgt.getGroup();
            // create group
            final Group group = createGroup("core_3g", "RAT_VEND_HIER321");
            groups.add(group);
            // adding group element1
            final List<GroupElement> groupElements = group.getGroupElement();
            final GroupElement groupElement1 = xmlObjectFactory.createGroupElement();
            groupElements.add(groupElement1);
            // adding key to group element1
            groupElement1.getKey().add(createKey("RAT", "1"));
            groupElement1.getKey().add(createKey("VENDOR", "ERICSSON"));
            groupElement1.getKey().add(createKey("HIERARCHY_3", "ONRM_RootMo_R:RNC01:RNC01"));
            groupElement1.getKey().add(createKey("HIERARCHY_1", "RNC01-00-2"));

            // adding group element2
            final GroupElement groupElement2 = xmlObjectFactory.createGroupElement();
            groupElements.add(groupElement2);
            // adding key to group element2
            groupElement1.getKey().add(createKey("RAT", "1"));
            groupElement1.getKey().add(createKey("VENDOR", "ERICSSON"));
            groupElement1.getKey().add(createKey("HIERARCHY_3", "ONRM_RootMo_R:RNC01:RNC01"));
            groupElement1.getKey().add(createKey("HIERARCHY_1", "RNC01-00-2"));
            tInstance.append(groupMgt, versionId + "hh");
            fail("GroupMgtTypeNotDefinedException should have been throws");
        } catch (final GroupMgtTypeNotDefinedException e) {
            assertEquals("*Any*", e.getAdditionalInfo("grouptype:"));
        }
    }

    @Test
    public void testExceptionThrownWhenAddDiffRatInOneGroup() throws Exception {
        final String tableName = "dc.GROUP_TYPE_E_RAT_VEND_HIER3";
        Statement stmt = testFactory.getConnection().createStatement();
        final String cTable = "CREATE TABLE " + tableName + " (\n" + "GROUP_NAME varchar (64)     \n" + " ,START_TIME datetime \n"
                + " ,STOP_TIME datetime \n" + ", HIER3_ID BIGINT, HIERARCHY_3 varchar (64), RAT int, VENDOR varchar (64) \n" + ");";
        stmt.executeUpdate(cTable);
        stmt.executeUpdate("insert into " + tableName
                + " (GROUP_NAME, HIER3_ID, HIERARCHY_3, RAT, VENDOR) values ('RNC_Group1', '901', 'RNC1', '2','Ericsson')");
        stmt.close();
        stmt.close();
        doImport("xml/rat_vend_hier3.xml", 1, versionId);
        stmt = testFactory.getConnection().createStatement();
        final ResultSet rs = stmt.executeQuery("select * from " + tableName);
        final List<String> expectedValues = Arrays.asList("RNC1");
        final List<String> expectedNames = Arrays.asList("RNC_Group1");
        int group_count = 0;
        final List<String> recordedNames = new ArrayList<String>();
        try {
            while (rs.next()) {
                if (rs.getString("GROUP_NAME").equals("RNC_Group1")) {
                    group_count = group_count + 1;
                }
                final String gName = rs.getString("GROUP_NAME");
                if (group_count == 1) {
                    assertTrue("Unexpected group name", expectedNames.contains(gName));
                    assertSame(1, group_count);
                } else {
                    fail("GroupMgtTypeNotDefinedException should have been throws");
                }
                if (!recordedNames.contains(gName)) {
                    recordedNames.add(gName);
                }
                final String hier3 = rs.getString("HIERARCHY_3");
                assertTrue("Unexpected group value", expectedValues.contains(hier3));
            }
        } catch (final GroupMgtTypeNotDefinedException e) {
            assertEquals("It should not have two group of same name", e.getAdditionalInfo("grouptype:"));
        } finally {
            stmt.close();
        }
        for (final String eName : expectedNames) {
            assertTrue("Unrecorded group name ", recordedNames.contains(eName));
        }
    }

    @Test
    public void testAppendLoadNoGroupInTechPack() throws Exception {
        try {
            //  copied data from imsi3.xml
            final Groupmgt groupMgt = xmlObjectFactory.createGroupmgt();

            final List<Group> groups = groupMgt.getGroup();
            //create group
            final Group group = createGroup("imsi-4", "THIS_DOES_NOT_EXIST");
            groups.add(group);
            //adding group element
            final GroupElement groupElement = xmlObjectFactory.createGroupElement();
            group.getGroupElement().add(groupElement);
            groupElement.getKey().add(createKey("THIS_DOES_NOT_EXIST", "93"));

            tInstance.append(groupMgt, versionId);
            fail("GroupMgtTypeNotDefinedException should have been throws");
        } catch (final GroupMgtTypeNotDefinedException e) {
            assertEquals("THIS_DOES_NOT_EXIST", e.getAdditionalInfo("grouptype:"));
        }
    }

    private Key createKey(final String name, final String value) {
        final Key key = xmlObjectFactory.createKey();
        key.setName(name);
        key.setValue(value);
        return key;
    }

    private Group createGroup(final String name, final String type) {
        final Group group = xmlObjectFactory.createGroup();
        group.setName(name);
        group.setType(type);
        return group;
    }

    private class StubbedGroupMgtImporter extends GroupMgtImporter {

        @Override
        protected String getNullOptions() {
            return "";
        }
    }
}
