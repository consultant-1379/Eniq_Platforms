package com.distocraft.dc5000.etl.importexport;

/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.importexport.gpmgt.*;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtImportException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtTypeNotDefinedException;
import com.distocraft.dc5000.etl.importexport.groupmgttype.GroupMgtType;
import com.distocraft.dc5000.etl.importexport.groupmgttype.GroupMgtTypeFactory;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.cache.GroupTypeKeyDef;
import com.distocraft.dc5000.repository.cache.GroupTypesCache;
import ssc.rockfactory.RockException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class GroupMgtImporter {

    /**
     *
     * Replace the String NULL with the value NULL if seen in columns
     */
    private static final String NULL_OPTIONS = " NULL('NULL')";

    /**
     * Velocity template used to generate insert statement for an empty table
     */
    private final transient String emptyTemplate;

    /**
     * Velocity template used to generate insert statement for an populated
     * table
     */
    private final transient String appendTemplate;

    /**
     * Velocity template used to generate delete statement for a table
     */
    private final transient String deleteTemplate;

    /**
     * The action to take on the generated bcp load file, can be 'delete' or
     * 'none'
     */
    private final transient String afterAction;

    /**
     * The max number of groups for a group type
     */
    private final transient long maxGroups;

    /**
     * XML Parser instance, used JAXB.
     */
    private final transient GroupMgtXmlParser xmlParser;

    /**
     * Database helper instance.
     */
    protected transient DatabaseHelper dbHelper;

    /**
     * Map holding the colums each load file loads.
     */
    private final transient Map<File, String> fileColumnMap;

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger("gpmgt.import");

    private static final String DEF_MAX_GROUPS = "250";

    private static final String DIM_E_TABLE_FOR_3G_CELL = "DIM_E_SGEH_HIER321_CELL";

    private static final String GROUP_TYPE_HIER1 = "RAT_VEND_HIER321";

    private static final String GROUP_TYPE_HIER1_CELL = "RAT_VEND_HIER321_CELL";

    private static final String HIERARCHY_1_COLUMN_NAME = "HIERARCHY_1";

    private static final String HIERARCHY_3_COLUMN_NAME = "HIERARCHY_3";

    private static final String CELL_ID_COLUMN_NAME = "CELL_ID";

    private static final String RAT_COLUMN_NAME = "RAT";

    private static final String RAT_ID_FOR_3G = "1";

    /**
     * Helper class for converting the input xml format to an output bcp format
     * to get loaded into IQ
     */
    private final transient GroupXmlConverter xmlConverter;

    /**
     * Constructor.
     */
    public GroupMgtImporter() {
        final Properties gpMgtProperties = GroupMgtHelper.getGpMgtProperties();
        if (gpMgtProperties.isEmpty()) {
            log("No Properties Loaded, Using Default.", Level.WARNING);
        }

        emptyTemplate = gpMgtProperties.getProperty("merge.empty.template",
                "templates/merge_empty.vm");
        appendTemplate = gpMgtProperties.getProperty("merge.append.template",
                "templates/merge_append.vm");
        deleteTemplate = gpMgtProperties.getProperty("merge.delete.template",
                "templates/merge_delete.vm");
        afterAction = gpMgtProperties.getProperty("after.load", "delete"); // or
                                                                            // none,
                                                                            // move
        maxGroups = Long.parseLong(gpMgtProperties.getProperty(
                "events.maxgroups", DEF_MAX_GROUPS));
        fileColumnMap = new HashMap<File, String>();
        xmlParser = new GroupMgtXmlParser();
        dbHelper = initDbHelper(gpMgtProperties); // NOPMD : overridden in tests
        final String columnSeperator = gpMgtProperties.getProperty(
                "col.seperator", ",");
        final String rowSeperator = gpMgtProperties.getProperty(
                "row.seperator", "\n");
        final String colWrapperChar = gpMgtProperties.getProperty(
                "col.wrapper", "");
        xmlConverter = new GroupXmlConverter(columnSeperator, rowSeperator,
                colWrapperChar, dbHelper);
    }

    /**
     * Get an instance of the database helper class. Primarily used in tests.
     *
     * @param gpMgtProperties
     *            BCP load options to use
     * @return DatabaseHelper instance
     */
    protected DatabaseHelper initDbHelper(final Properties gpMgtProperties) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(gpMgtProperties);
        }
        return dbHelper;
    }

    /**
     * Log a message
     *
     * @param msg
     *            What to log
     * @param lvl
     *            Level to log it at
     */
    private static void log(final String msg, final Level lvl) {
        LOGGER.log(lvl, msg);
    }

    /**
     * Main Args examples : -i -add -f file.xml see gpmgt scrip for more
     * details.
     *
     * @param args
     *            Input args
     */
    public static void main(final String[] args) {
        final String tpVersionID = GroupMgtHelper.getArgValue(args, "-t");
        if (tpVersionID != null) { // need not be passed in
            final ExitCodes code = GroupMgtHelper.checkVersionArgs(tpVersionID);
            if (code != ExitCodes.EXIT_OK) {
                GroupMgtHelper.exitSystem(code);
            }
        }
        final String dataFile = GroupMgtHelper.getArgValue(args, "-f");
        final ExitCodes code = GroupMgtHelper.checkFileArgs(dataFile);
        if (code != ExitCodes.EXIT_OK) {
            GroupMgtHelper.exitSystem(code);
        }

        final ImportType type = GroupMgtHelper.getImportType(args);
        if (type == null) {
            GroupMgtHelper.exitSystem(ExitCodes.EXIT_NO_IMPORT_TYPE);
        }
        final GroupMgtImporter importer = new GroupMgtImporter();
        try {
            StaticProperties.reload();
            if (type == ImportType.Add) {
                importer.importDataFileAppend(dataFile, tpVersionID);
            } else /* if(type == ImportType.Delete) */{
                importer.importDataFileDelete(dataFile, tpVersionID);
            }
        } catch (final Throwable t) { // NOPMD : Catch all errors, check &
                                        // unchecked
            log(t.toString(), Level.SEVERE);
        } finally {
            importer.destroy();
        }
    }

    /**
     * Cleanup resources Database connections.
     */
    public final void destroy() {
        dbHelper.destroy();
        xmlConverter.destroy();
        fileColumnMap.clear();
    }

    /**
     * Import a file and delete entries in the Group tables that match data in
     * file.
     *
     * @param dataFile
     *            XML file
     * @param tpVersionId
     *            Tecxh Pack version, can be null, if it is the active version
     *            is used
     * @throws RockException
     *             Connection errors
     * @throws IOException
     *             Failed to read connection properties
     * @throws SQLException
     *             SQL Errors
     * @throws NoSuchAlgorithmException
     */
    public final void importDataFileDelete(final String dataFile,
            final String tpVersionId) throws RockException, IOException,
            SQLException, NoSuchAlgorithmException {
        String versionID = tpVersionId;
        if (versionID == null) {
            versionID = dbHelper.getActiveTechpackVersion();
        }
        log("Import Delete to " + versionID, Level.INFO);
        try {
            final List<GroupTypeDef> typesImported = convertAndLoadFile(
                    dataFile, versionID);
            deleteIfExists(typesImported);
        } finally {
            dbHelper.closeDwhdb();
        }
    }

    private void deleteIfExists(final List<GroupTypeDef> typesImported)
            throws SQLException, RockException, IOException {
        for (final GroupTypeDef gpType : typesImported) {
            final boolean isTableEmpty = dbHelper.isTableEmpty(
                    gpType.getTableName(), dbHelper.getDwhdb());
            if (isTableEmpty) {
                log("Nothing to delete.", Level.INFO);
            } else {
                final GroupMgtType groupMgtType = GroupMgtTypeFactory
                        .createGroupMgtType(gpType, maxGroups, dbHelper);
                groupMgtType.importDataFileDelete(deleteTemplate);
            }
            log("Dropping table " + gpType.getTempTableName(), Level.FINE);
            dbHelper.dropTmpTables(gpType.getTempTableName());
        }
        log("Import Complete.", Level.INFO);
    }

    /**
     * Import a file and delete entries in the Group tables that match data in
     * file.
     *
     * @param groupMgt
     *            groups to delete
     * @param tpVersionId
     *            Tecxh Pack version, can be null, if it is the active version
     *            is used
     * @throws RockException
     *             Connection errors
     * @throws IOException
     *             Failed to read connection properties
     * @throws SQLException
     *             SQL Errors
     * @throws NoSuchAlgorithmException
     */
    public final void delete(final Groupmgt groupMgt, final String tpVersionId)
            throws RockException, IOException, SQLException,
            NoSuchAlgorithmException {
        String versionID = tpVersionId;
        if (versionID == null) {
            versionID = dbHelper.getActiveTechpackVersion();
        }
        log("Import Delete to " + versionID, Level.INFO);
        changeGroupTypeFor_3G(groupMgt);
        try {
            final List<GroupTypeDef> typesImported = convertAndLoad(groupMgt,
                    versionID);
            deleteIfExists(typesImported);
        } finally {
            dbHelper.closeDwhdb();
        }
    }

    /**
     * Import a file and add entries top the Group tables that are in data in
     * file, no duplication
     *
     * @param dataFile
     *            XML file
     * @param tpVersionId
     *            Tech Pack version, can be null, if it is the active version is
     *            used
     * @throws RockException
     *             Connection errors
     * @throws IOException
     *             Failed to read connection properties
     * @throws SQLException
     *             SQL Errors
     * @throws NoSuchAlgorithmException
     */
    public final void importDataFileAppend(final String dataFile,
            final String tpVersionId) throws RockException, SQLException,
            IOException, NoSuchAlgorithmException {
        String versionId = tpVersionId;
        if (versionId == null) {
            versionId = dbHelper.getActiveTechpackVersion();
        }
        log("Import Add to " + versionId, Level.INFO);
        try {
            final List<GroupTypeDef> typesImported = convertAndLoadFile(
                    dataFile, versionId);
            mergeIfRequired(typesImported);
        } finally {
            dbHelper.closeDwhdb();
        }
    }

    private void mergeIfRequired(final List<GroupTypeDef> typesImported)
            throws SQLException, RockException, IOException {
        for (final GroupTypeDef gpType : typesImported) {
            final boolean isTableEmpty = dbHelper.isTableEmpty(
                    gpType.getTableName(), dbHelper.getDwhdb());
            String mergeTemplate;
            if (isTableEmpty) {
                mergeTemplate = emptyTemplate;
            } else {
                mergeTemplate = appendTemplate;
            }

            final GroupMgtType groupMgtType = GroupMgtTypeFactory
                    .createGroupMgtType(gpType, maxGroups, dbHelper);
            groupMgtType.importDataFileAppend(mergeTemplate);

            log("Dropping table " + gpType.getTempTableName(), Level.FINE);
            dbHelper.dropTmpTables(gpType.getTempTableName());
        }
    }

    /**
     * Import a file and add entries top the Group tables that are in data in
     * file, no duplication
     *
     * @param groupMgt
     *            Groups to insert
     * @param tpVersionId
     *            Tech Pack version, can be null, if it is the active version is
     *            used
     * @throws RockException
     *             Connection errors
     * @throws IOException
     *             Failed to read connection properties
     * @throws SQLException
     *             SQL Errors
     * @throws NoSuchAlgorithmException
     */
    public final void append(final Groupmgt groupMgt, final String tpVersionId)
            throws RockException, SQLException, IOException,
            NoSuchAlgorithmException {
        String versionId = tpVersionId;
        if (versionId == null) {
            versionId = dbHelper.getActiveTechpackVersion();
        }
        log("Import Add to " + versionId, Level.INFO);

        changeGroupTypeFor_3G(groupMgt);
        try {
            final List<GroupTypeDef> typesImported = convertAndLoad(groupMgt,
                    versionId);
            mergeIfRequired(typesImported);
        } finally {
            dbHelper.closeDwhdb();
        }
    }

    /**
     * Load the generated bcp file to their temp tables.
     *
     * @param dataFile
     *            The data xml file to convert and load
     * @param tpVersionId
     *            the techpack version id, can be null, if it is the active
     *            version is used.
     * @return List of GroupTypeDef that are being changed.
     * @throws RockException
     *             Connection errors
     * @throws IOException
     *             Failed to read connection properties
     * @throws SQLException
     *             SQL Errors
     * @throws NoSuchAlgorithmException
     */
    public final List<GroupTypeDef> convertAndLoadFile(final String dataFile,
            final String tpVersionId) throws RockException, SQLException,
            IOException, NoSuchAlgorithmException {
        // refresh group cache
        GroupTypesCache.init(dbHelper.getDwhrep());
        // are there any defined in Tech Pack
        if (!GroupTypesCache.areGroupsDefined(tpVersionId)) {
            throw new GroupMgtTypeNotDefinedException(dataFile, tpVersionId,
                    "*Any*");
        }
        // Start formatting the xml file to an sql load file + the load
        // command....
        final Groupmgt data = xmlParser.getGroupData(dataFile);
        return loadTempTables(dataFile, tpVersionId, data);
    }

    @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis",
            "PMD.CyclomaticComplexity" })
    private List<GroupTypeDef> loadTempTables(final String dataFile,
            final String tpVersionId, final Groupmgt data)
            throws RockException, SQLException, IOException,
            NoSuchAlgorithmException {
        final Map<String, String> tmpTables = new HashMap<String, String>();
        final Map<String, GroupTypeDef> typesImported = new HashMap<String, GroupTypeDef>();
        final Map<GroupTypeDef, String> loadColumsForType = new HashMap<GroupTypeDef, String>();
        for (final Group importGroupData : data.getGroup()) {
            final String importGroupType = importGroupData.getType()
                    .toUpperCase();
            final boolean isMgtType = GroupTypesCache.isGroupMgtType(
                    tpVersionId, importGroupType);
            if (!isMgtType) {
                throw new GroupMgtTypeNotDefinedException(dataFile,
                        tpVersionId, importGroupType);
            }
            final GroupTypeDef def = GroupTypesCache.getGrouptypesDef(
                    tpVersionId).get(importGroupType);
            String columnsToLoad;
            if (loadColumsForType.containsKey(def)) {
                columnsToLoad = loadColumsForType.get(def);
            } else {
                final Collection<GroupTypeKeyDef> sqlKeys = def.getKeys();
                final Iterator<GroupTypeKeyDef> iter = sqlKeys.iterator();
                final StringBuilder _columnsToLoad = new StringBuilder(); // NOPMD
                                                                            // :
                                                                            // How
                                                                            // else?
                while (iter.hasNext()) {
                    final GroupTypeKeyDef orderKey = iter.next();
                    final String columnName = orderKey.getKeyName()
                            .toUpperCase();
                    _columnsToLoad.append(columnName);
                    _columnsToLoad.append(getNullOptions());
                    if (iter.hasNext()) {
                        _columnsToLoad.append(", ");
                    }
                }
                columnsToLoad = _columnsToLoad.toString();
                loadColumsForType.put(def, columnsToLoad);
            }
            if (importGroupData.getName().equals("*")) {
                final String tmpTableName = def.getTempTableName();
                final String tmpCreateSql = dbHelper.getTempTableDDL(
                        tmpTableName, def);
                tmpTables.put(tmpTableName, tmpCreateSql);
            } else {
                final List<GroupElement> rowEntry = importGroupData
                        .getGroupElement();
                for (final GroupElement profile : rowEntry) {
                    xmlConverter.convertToSqlLoad(dataFile, tmpTables,
                            importGroupData, def, profile, columnsToLoad,
                            fileColumnMap);
                }
            }
            if (!typesImported.containsKey(def.getGroupType())) {
                typesImported.put(def.getGroupType(), def);
            }
        }
        xmlConverter.destroy();
        dbHelper.loadTempTables(tmpTables, fileColumnMap, afterAction);

        return new ArrayList<GroupTypeDef>(typesImported.values());
    }

    /**
     * Load the generated bcp file to their temp tables.
     *
     * @param tpVersionId
     *            the techpack version id, can be null, if it is the active
     *            version is used.
     * @return List of GroupTypeDef that are being changed.
     * @throws RockException
     *             Connection errors
     * @throws IOException
     *             Failed to read connection properties
     * @throws SQLException
     *             SQL Errors
     * @throws NoSuchAlgorithmException
     *
     *             convertAndLoadFile
     */

    public final List<GroupTypeDef> convertAndLoad(final Groupmgt data,
            final String tpVersionId) throws RockException, SQLException,
            IOException, NoSuchAlgorithmException {
        // refresh group cache
        GroupTypesCache.init(dbHelper.getDwhrep());
        if (data == null) {
            throw new GroupMgtImportException("Data can not be null.");
        }
        // are there any defined in Tech Pack
        if (!GroupTypesCache.areGroupsDefined(tpVersionId)) {
            throw new GroupMgtTypeNotDefinedException(tpVersionId, "*Any*");
        }
        return loadTempTables(null, tpVersionId, data);
    }

    protected String getNullOptions() {
        return NULL_OPTIONS;
    }

    /**
     * Add groups that are specified in groupMgt, no duplication
     *
     * @param groupMgt
     *            Groups to insert
     * @param tpVersionID
     *            Tech Pack version, can be null, if it is the active version is
     *            used
     * @throws IOException
     * @throws SQLException
     * @throws RockException
     * @throws NoSuchAlgorithmException
     *
     **/
    public static void appendGroups(final String tpVersionID,
            final Groupmgt groupMgt) throws NoSuchAlgorithmException,
            RockException, SQLException, IOException {
        GroupMgtHelper.groupLimitExceeded = false; // before operation , reset
                                                    // the flag to false.
        GroupMgtHelper.reloadProperties(); // load properties from system env
        final GroupMgtImporter importer = new GroupMgtImporter();
        try {
            StaticProperties.reload();
            importer.append(groupMgt, tpVersionID);

        } finally {
            importer.destroy();
        }
    }

    // fix for DEFTFTMIT-568
    // we will not get RAT_VEND_HIER321_CELL type of we create 3G access area
    // group from UI. So, we are doing things here
    public final void changeGroupTypeFor_3G(Groupmgt gmgt)
            throws RockException, IOException, SQLException {
        final Iterator<Group> groupIterator = gmgt.getGroup().iterator();
        while (groupIterator.hasNext()) {
            final Group group = groupIterator.next();
            if (GROUP_TYPE_HIER1.equalsIgnoreCase(group.getType())) {
                final Iterator<GroupElement> groupElementIterator = group
                        .getGroupElement().iterator();
                while (groupElementIterator.hasNext()) {
                    final GroupElement groupElement = groupElementIterator
                            .next();
                    final Iterator<Key> keyIterator = groupElement.getKey()
                            .iterator();
                    Key cell_Id = null;
                    boolean three_G = false;
                    boolean three_G_CELL = false;
                    String HIERARCHY_3 = null;
                    while (keyIterator.hasNext()) {
                        final Key key = keyIterator.next();
                        if (HIERARCHY_3_COLUMN_NAME.equalsIgnoreCase(key
                                .getName())) {
                            HIERARCHY_3 = key.getValue();
                            continue;
                        }
                        if (RAT_COLUMN_NAME.equalsIgnoreCase(key.getName())) {
                            if (RAT_ID_FOR_3G.equalsIgnoreCase(key.getValue())) {
                                log("3G group element found", Level.INFO);
                                three_G = true;
                            }
                        }
                    }
                    if (three_G == true) {
                        final Iterator<Key> keyIterator1 = groupElement
                                .getKey().iterator();

                        while (keyIterator1.hasNext()) {
                            final Key key1 = keyIterator1.next();
                            if (HIERARCHY_1_COLUMN_NAME.equalsIgnoreCase(key1
                                    .getName()))

                            {
                                String HIERARCHY_1 = key1.getValue();
                                String query = "SELECT "
                                        + HIERARCHY_1_COLUMN_NAME + " FROM "
                                        + DIM_E_TABLE_FOR_3G_CELL + " WHERE "
                                        + CELL_ID_COLUMN_NAME + "=" + "'"
                                        + HIERARCHY_1 + "'" + " AND "
                                        + HIERARCHY_3_COLUMN_NAME + "=" + "'"
                                        + HIERARCHY_3 + "'";
                                HIERARCHY_1 = dbHelper
                                        .getHierarchy_1For3GCell(query);
                                if (HIERARCHY_1 != null) {
                                    group.setType(GROUP_TYPE_HIER1_CELL);
                                    three_G_CELL = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (three_G_CELL == true) {
                        final Iterator<Key> keyIterator1 = groupElement
                                .getKey().iterator();
                        while (keyIterator1.hasNext()) {
                            final Key key = keyIterator1.next();
                            if (HIERARCHY_1_COLUMN_NAME.equalsIgnoreCase(key
                                    .getName())) {
                                String HIERARCHY_1 = key.getValue();
                                String query = "SELECT "
                                        + HIERARCHY_1_COLUMN_NAME + " FROM "
                                        + DIM_E_TABLE_FOR_3G_CELL + " WHERE "
                                        + CELL_ID_COLUMN_NAME + "=" + "'"
                                        + HIERARCHY_1 + "'" + " AND "
                                        + HIERARCHY_3_COLUMN_NAME + "=" + "'"
                                        + HIERARCHY_3 + "'";
                                cell_Id = new Key();
                                cell_Id.setName(CELL_ID_COLUMN_NAME);
                                cell_Id.setValue(HIERARCHY_1);
                                HIERARCHY_1 = dbHelper
                                        .getHierarchy_1For3GCell(query);
                                log("HIERARCHY_1 =" + HIERARCHY_1, Level.INFO);
                                key.setValue(HIERARCHY_1);
                            }
                        }
                        groupElement.getKey().add(cell_Id);
                    }
                }

            }
        }
    }

    /**
     * Delete groups from table, as specified in groupMgt
     *
     * @param groupMgt
     *            Groups to delete
     * @param tpVersionID
     *            Tech Pack version, can be null, if it is the active version is
     *            used
     * @throws SQLException
     * @throws IOException
     * @throws RockException
     * @throws NoSuchAlgorithmException
     *
     **/
    public static void deleteGroups(final String tpVersionID,
            final Groupmgt groupMgt) throws NoSuchAlgorithmException,
            RockException, IOException, SQLException {
        GroupMgtHelper.reloadProperties();
        final GroupMgtImporter importer = new GroupMgtImporter();
        try {
            StaticProperties.reload();
            importer.delete(groupMgt, tpVersionID);
        } finally {
            importer.destroy();
        }
    }
}
