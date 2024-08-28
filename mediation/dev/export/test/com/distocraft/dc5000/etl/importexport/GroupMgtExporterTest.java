package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

import org.junit.*;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.importexport.gpmgt.*;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtTypeNotDefinedException;
import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;

public class GroupMgtExporterTest {

    private static Map<String, String> env = System.getenv();

    final String exportFile = env.get("WORKSPACE") + File.separator + "exported.xml";

    private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";

    private static final String DB_URL = "jdbc:hsqldb:mem:gpmgt";

    private static final String versionId = "EVENT_E_SGEH:((11))";

    private RockFactory testFactory = null;

    @Before
    public void setUp() throws Exception {
        StaticProperties.giveProperties(new Properties());
        final Properties etlc = new Properties();
        etlc.setProperty("ENGINE_DB_URL", DB_URL);
        etlc.setProperty("ENGINE_DB_DRIVERNAME", TESTDB_DRIVER);
        etlc.setProperty("ENGINE_DB_USERNAME", "SA");
        etlc.setProperty("ENGINE_DB_PASSWORD", "");
        GroupMgtHelper.setEtlcProperties(etlc);
        final Properties gpmgt = new Properties();
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.append.template", "templates/merge_append.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".loader.options",
                "QUOTES OFF ESCAPES OFF FORMAT bcp DELIMITED BY ',' IGNORE CONSTRAINT UNIQUE 1 STRIP RTRIM");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.delete.template", "templates/merge_delete.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".row.seperator", "\n");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".col.wrapper", "");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".events.tpname", "EVENT_E_SGEH");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".col.seperator", ",");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".merge.empty.template", "templates/merge_empty.vm");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".etlc.properties", "/eniq/sw/conf/ETLCServer.properties");
        gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".after.load", "delete");
        GroupMgtHelper.setGpmgtProperties(gpmgt);

        testFactory = new RockFactory(DB_URL, "SA", "", TESTDB_DRIVER, "", true);
        DatabaseTestUtils.loadSetup(testFactory, "gpmgtExport");
    }

    @After
    public void tearDown() {
        try {
            testFactory.getConnection().createStatement().executeUpdate("SHUTDOWN");
        } catch (final SQLException e) {/**/
        }
    }

    @Test
    public void testExportAll() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        exporter.export(exportFile, versionId, null);
        final GroupMgtXmlParser parser = new GroupMgtXmlParser();
        final Groupmgt gps = parser.getGroupData(exportFile);
        assertEquals(12, gps.getGroup().size());
    }

    @Test
    public void testExportApn() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Map<String, List<String>> names = GroupMgtExporter.getGroupFilter(new String[] { "-g", "APn:apn-1,apn-2" });
        exporter.export(exportFile, versionId, names);
        final GroupMgtXmlParser parser = new GroupMgtXmlParser();
        final Groupmgt gps = parser.getGroupData(exportFile);
        assertEquals(2, gps.getGroup().size());
        {
            final Group g = gps.getGroup().get(0);
            assertEquals("apn-1", g.getName());
            final List<GroupElement> e = g.getGroupElement();
            assertEquals(2, e.size());
            {
                final List<Key> k = e.get(0).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("11", k.get(0).getValue());
            }
            {
                final List<Key> k = e.get(1).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("12", k.get(0).getValue());
            }
        }
        {
            final Group g = gps.getGroup().get(1);
            assertEquals("apn-2", g.getName());
            final List<GroupElement> e = g.getGroupElement();
            assertEquals(2, e.size());
            {
                final List<Key> k = e.get(0).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("22", k.get(0).getValue());
            }
            {
                final List<Key> k = e.get(1).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("23", k.get(0).getValue());
            }
        }
    }

    @Test
    public void testExportTac() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Map<String, List<String>> names = GroupMgtExporter.getGroupFilter(new String[] { "-g", "Tac" });
        exporter.export(exportFile, versionId, names);
        final GroupMgtXmlParser parser = new GroupMgtXmlParser();
        final Groupmgt gps = parser.getGroupData(exportFile);
        assertEquals(2, gps.getGroup().size());
        final Group g = gps.getGroup().get(0);
        assertEquals("tac-1", g.getName());
        final List<GroupElement> e = g.getGroupElement();
        assertEquals(1, e.size());
        final List<Key> k = e.get(0).getKey();
        assertEquals(1, k.size());
        assertEquals("TAC", k.get(0).getName());
        assertEquals("99991919", k.get(0).getValue());

        final Group g1 = gps.getGroup().get(1);
        assertEquals("test-duplicate-group", g1.getName());
        final List<GroupElement> e1 = g1.getGroupElement();
        assertEquals(1, e1.size());
        final List<Key> k1 = e1.get(0).getKey();
        assertEquals(1, k1.size());
        assertEquals("TAC", k1.get(0).getName());
        assertEquals("10999111", k1.get(0).getValue());
    }

    @Test
    public void testExportTacNoVersionId() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Map<String, List<String>> names = GroupMgtExporter.getGroupFilter(new String[] { "-g", "Tac" });
        exporter.export(exportFile, null, names);
        final GroupMgtXmlParser parser = new GroupMgtXmlParser();
        final Groupmgt gps = parser.getGroupData(exportFile);
        assertEquals(2, gps.getGroup().size());
        final Group g = gps.getGroup().get(0);
        assertEquals("tac-1", g.getName());
        final List<GroupElement> e = g.getGroupElement();
        assertEquals(1, e.size());
        final List<Key> k = e.get(0).getKey();
        assertEquals(1, k.size());
        assertEquals("TAC", k.get(0).getName());
        assertEquals("99991919", k.get(0).getValue());

        final Group g1 = gps.getGroup().get(1);
        assertEquals("test-duplicate-group", g1.getName());
        final List<GroupElement> e1 = g1.getGroupElement();
        assertEquals(1, e1.size());
        final List<Key> k1 = e1.get(0).getKey();
        assertEquals(1, k1.size());
        assertEquals("TAC", k1.get(0).getName());
        assertEquals("10999111", k1.get(0).getValue());
    }

    @Test
    public void testExportWrongVersionId() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        try {
            exporter.export("abc!", "does_not_work", null);
            fail("GroupMgtTypeNotDefinedException should have been throw.");
        } catch (final GroupMgtTypeNotDefinedException e) {
            // Expected...
        }
    }

    @Test
    public void testGetGroupFilter() {
        Map<String, List<String>> m1 = GroupMgtExporter.getGroupFilter(null);
        assertNotNull(m1);
        assertTrue(m1.isEmpty());

        m1 = GroupMgtExporter.getGroupFilter(new String[0]);
        assertNotNull(m1);
        assertTrue(m1.isEmpty());

        try {
            GroupMgtExporter.getGroupFilter(new String[] { "-g" });
            fail("IndexOutOfBoundsException should have been thrown as there's no value given");
        } catch (final IndexOutOfBoundsException e) {
            // Expected this.
        }

        String keyApn = "apn";
        m1 = GroupMgtExporter.getGroupFilter(new String[] { "-g", keyApn });
        assertFalse(m1.isEmpty());
        assertTrue(m1.containsKey(keyApn.toUpperCase()));
        assertTrue(m1.get(keyApn.toUpperCase()).isEmpty());

        keyApn = "apn:";
        m1 = GroupMgtExporter.getGroupFilter(new String[] { "-g", keyApn });
        assertFalse(m1.isEmpty());
        assertTrue(m1.containsKey("APN"));
        assertTrue(m1.get("APN").isEmpty());

        keyApn = "apn:a1,a2";
        m1 = GroupMgtExporter.getGroupFilter(new String[] { "-g", keyApn });
        assertFalse(m1.isEmpty());
        assertTrue(m1.containsKey("APN"));
        List<String> names = m1.get("APN");
        assertFalse(names.isEmpty());
        assertEquals(2, names.size());
        assertTrue(names.contains("a1"));
        assertTrue(names.contains("a2"));

        m1 = GroupMgtExporter.getGroupFilter(new String[] { "-g", "apn:a,b", "-g", "TAC", "-g", "TAC:", "-g", "apn:c,d", "-g", "imsi:xyz" });
        assertTrue(m1.containsKey("APN"));
        names = m1.get("APN");
        assertEquals(4, names.size());
        assertTrue(names.contains("a"));
        assertTrue(names.contains("b"));
        assertTrue(names.contains("c"));
        assertTrue(names.contains("d"));

        assertTrue(m1.containsKey("IMSI"));
        names = m1.get("IMSI");
        assertEquals(1, names.size());
        assertTrue(names.contains("xyz"));

        assertTrue(m1.containsKey("TAC"));
        names = m1.get("TAC");
        assertTrue(names.isEmpty());

    }

    /** testing export function **/
    @Test
    public void testExportAllGroups() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Groupmgt grps = exporter.export(versionId, null);
        List<Group> groupList = grps.getGroup();
        assertEquals(12, groupList.size());

        for (Group group : groupList) {
            if (group.getType().equalsIgnoreCase("APN")) {
                checkApnGroups(group);
            } else if (group.getType().equalsIgnoreCase("IMSI")) {
                checkImsiGroups(group);
            } else if (group.getType().equalsIgnoreCase("TAC")) {
                checkTacGroups(group);
            } else {
                fail("Unknown group type: " + group.getType());
            }
        }
    }

    @Test
    public void testExportApnGroups() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Map<String, List<String>> toExport = new HashMap<String, List<String>>();
        final List<String> names = new ArrayList<String>();
        names.add("apn-1");
        names.add("apn-2");
        toExport.put("APN", names);
        final Groupmgt gps = exporter.export(versionId, toExport);
        assertEquals(2, gps.getGroup().size());
        {
            final Group g = gps.getGroup().get(0);
            assertEquals("apn-1", g.getName());
            final List<GroupElement> e = g.getGroupElement();
            assertEquals(2, e.size());
            {
                final List<Key> k = e.get(0).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("11", k.get(0).getValue());
            }
            {
                final List<Key> k = e.get(1).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("12", k.get(0).getValue());
            }
        }
        {
            final Group g = gps.getGroup().get(1);
            assertEquals("apn-2", g.getName());
            final List<GroupElement> e = g.getGroupElement();
            assertEquals(2, e.size());
            {
                final List<Key> k = e.get(0).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("22", k.get(0).getValue());
            }
            {
                final List<Key> k = e.get(1).getKey();
                assertEquals(1, k.size());
                assertEquals("APN", k.get(0).getName());
                assertEquals("23", k.get(0).getValue());
            }
        }
    }

    @Test
    public void testExportTacGroups() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Map<String, List<String>> toExport = new HashMap<String, List<String>>();
        toExport.put("TAC", new ArrayList<String>());
        final Groupmgt gps = exporter.export(versionId, toExport);
        assertEquals(2, gps.getGroup().size());
        final Group g = gps.getGroup().get(0);
        assertEquals("tac-1", g.getName());
        final List<GroupElement> e = g.getGroupElement();
        assertEquals(1, e.size());
        final List<Key> k = e.get(0).getKey();
        assertEquals(1, k.size());
        assertEquals("TAC", k.get(0).getName());
        assertEquals("99991919", k.get(0).getValue());

        final Group g1 = gps.getGroup().get(1);
        assertEquals("test-duplicate-group", g1.getName());
        final List<GroupElement> e1 = g1.getGroupElement();
        assertEquals(1, e1.size());
        final List<Key> k1 = e1.get(0).getKey();
        assertEquals(1, k1.size());
        assertEquals("TAC", k1.get(0).getName());
        assertEquals("10999111", k1.get(0).getValue());
    }

    @Test
    public void testExportTacGroupsNoVersionId() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        final Map<String, List<String>> toExport = new HashMap<String, List<String>>();
        toExport.put("TAC", new ArrayList<String>());
        final Groupmgt gps = exporter.export(null, toExport);
        assertEquals(2, gps.getGroup().size());
        final Group g = gps.getGroup().get(0);
        assertEquals("tac-1", g.getName());
        final List<GroupElement> e = g.getGroupElement();
        assertEquals(1, e.size());
        final List<Key> k = e.get(0).getKey();
        assertEquals(1, k.size());
        assertEquals("TAC", k.get(0).getName());
        assertEquals("99991919", k.get(0).getValue());

        final Group g1 = gps.getGroup().get(1);
        assertEquals("test-duplicate-group", g1.getName());
        final List<GroupElement> e1 = g1.getGroupElement();
        assertEquals(1, e1.size());
        final List<Key> k1 = e1.get(0).getKey();
        assertEquals(1, k1.size());
        assertEquals("TAC", k1.get(0).getName());
        assertEquals("10999111", k1.get(0).getValue());
    }

    @Test
    public void testExportGroupsWrongVersionId() throws Exception {
        final GroupMgtExporter exporter = new GroupMgtExporter();
        try {
            exporter.export("does_not_work", null);
            fail("GroupMgtTypeNotDefinedException should have been throw.");
        } catch (final GroupMgtTypeNotDefinedException e) {
            // Expected...
        }
    }

    private void checkApnGroups(Group group) {
        if (group.getName().equalsIgnoreCase("apn-1")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("11") && !key.getValue().equals("12")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("apn-2")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("22") && !key.getValue().equals("23")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("test-duplicate-group")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("2311")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else {
            fail("Unknown group name: " + group.getName());
        }
    }

    private void checkImsiGroups(Group group) {
        if (group.getName().equalsIgnoreCase("imsi-4")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("93") && !key.getValue().equals("42")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("imsi-5")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("93") && !key.getValue().equals("42")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("imsi-6")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("93") && !key.getValue().equals("42")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("imsi-7")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("93") && !key.getValue().equals("42")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("imsi-8")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("93") && !key.getValue().equals("42")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("imsi-9")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("93") && !key.getValue().equals("42")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("test-duplicate-group")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("10111")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else {
            fail("Unknown group name: " + group.getName());
        }
    }

    private void checkTacGroups(Group group) {
        if (group.getName().equalsIgnoreCase("tac-1")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("99991919")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else if (group.getName().equalsIgnoreCase("test-duplicate-group")) {
            for (GroupElement groupElement : group.getGroupElement()) {
                for (Key key : groupElement.getKey()) {
                    if (!key.getValue().equals("10999111")) {
                        fail("Unknown value: " + key.getValue());
                    }
                }
            }
        } else {
            fail("Unknown group name: " + group.getName());
        }
    }

}
