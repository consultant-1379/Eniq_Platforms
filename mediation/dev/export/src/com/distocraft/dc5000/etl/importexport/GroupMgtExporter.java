package com.distocraft.dc5000.etl.importexport;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import com.distocraft.dc5000.etl.importexport.gpmgt.*;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtTypeNotDefinedException;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.cache.GroupTypeKeyDef;
import com.distocraft.dc5000.repository.cache.GroupTypesCache;
import com.ericsson.eniq.common.HashIdCreator;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to export the contents of a Group table stored in DWHDB to a
 * well defined XML format that can be reimported using the import finction.
 * <p/>
 * It can export all Group instances, Group instances of a certain type e.g. APN
 * or specific instances of a group type e.g The group instance called VIP of type TAC
 */
public class GroupMgtExporter {
    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger("gpmgt.export");
    /**
     * Group filter option identifier
     */
    private static final String GROUP_FOPTION = "-g";
    /**
     * Database helper/util class
     */
    private transient final DatabaseHelper dbHelper;
    /**
     * Factory object to used to create the XML JABX Objects
     */
    private transient final ObjectFactory xmlObjectFactory;
    
    private static final String GROUP_TABLE_NAME_FOR_2G4G = "GROUP_TYPE_E_RAT_VEND_HIER321";
    
    private static final String GROUP_TABLE_NAME_FOR_3G = "GROUP_TYPE_E_RAT_VEND_HIER321_CELL";
    
    private static final String OPEN_PARENTHESES=" ( ";
    
    private static final String CLOSE_PARENTHESES=" ) ";
    
    private static final String UNION_KEYWORD=" UNION ";
    
    private static final String AS_TMP="AS TMP";

    /**
     * Constructor
     */
    public GroupMgtExporter() {
        dbHelper = new DatabaseHelper();
        xmlObjectFactory = new ObjectFactory();
    }

    /**
     * Log method
     *
     * @param msg Text to log, level defaults to INFO
     */
    private void log(final String msg) {
        log(msg, Level.INFO);
    }

    /**
     * Log method
     *
     * @param msg Text to log;
     * @param lvl Level to log at
     */
    private static void log(final String msg, final Level lvl) {
        LOGGER.log(lvl, msg);
    }

    /**
     * Log an error
     *
     * @param msg   Message to display
     * @param error Exception to log
     */
    private static void log(final String msg, final Throwable error) {
        LOGGER.log(Level.SEVERE, msg, error);
    }

    /**
     * Convert all the -g values to a usable structure
     * Key --> Group Type
     * Values --> Grou names for the type, this may be empty i.e. all group instance of a type are to be exported.
     *
     * @param args Arguements to parse
     * @return Map of types to names
     */
    static Map<String, List<String>> getGroupFilter(final String[] args) { // NOPMD : USes in tests
        final Map<String, List<String>> toExport = new HashMap<String, List<String>>(); //NOPMD
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (GROUP_FOPTION.equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        final String filter = args[++i];
                        final int pre = filter.indexOf(':');
                        String type;
                        final List<String> gpNames = new ArrayList<String>(); //NOPMD
                        if (pre == -1) {
                            type = filter.toUpperCase(Locale.getDefault());
                        } else {
                            type = filter.substring(0, pre).toUpperCase(Locale.getDefault());
                            final String names = filter.substring(pre + 1);
                            final StringTokenizer st = new StringTokenizer(names, ","); //NOPMD
                            while (st.hasMoreTokens()) {
                                gpNames.add(st.nextToken());
                            }
                        }
                        if (toExport.containsKey(type)) {
                            toExport.get(type).addAll(gpNames);
                        } else {
                            toExport.put(type, gpNames);
                        }
                    } else {
                        throw new IndexOutOfBoundsException("No Value given for -g option.");
                    }
                }
            }
        }
        return toExport;
    }

    /**
     * Main
     * Usage: -e -f <file> {-g <filter>}
     * Seet gpmgt script for
     *
     * @param args e.g. -e -f file.xml -g imsi
     */
    public static void main(final String[] args) {
        final String tpVersionID = GroupMgtHelper.getArgValue(args, "-t");
        if (tpVersionID != null) { // need not be passed in.
            final ExitCodes code = GroupMgtHelper.checkVersionArgs(tpVersionID);
            if (code != ExitCodes.EXIT_OK) {
                GroupMgtHelper.exitSystem(code);
            }
        }
        final String exportFile = GroupMgtHelper.getArgValue(args, "-f");
        final Map<String, List<String>> toExport = getGroupFilter(args);
        final GroupMgtExporter exporter = new GroupMgtExporter();
        try {
            exporter.export(exportFile, tpVersionID, toExport);
        } catch (Throwable t) { //NOPMD : Catch all errors, check & unchecked
            log("Export failed : ", t);
        } finally {
            exporter.destroy();
        }
    }

    /**
     * Export groups to an Xml file
     *
     * @param tpVersionId The tech pack to look up the group definitions from. If this is
     *                    null the current active techpacks is used.
     * @param toExport    List of group types and names to export, if empty all data is
     *                    exported. e.g  { {"IMSI" : ["VIP", "NORTH", ...]}, {"TAC" : ["EXC", ...]}, {"APN" : []} }
     * @throws RockException            Connecion creation errors
     * @throws SQLException             Select errors
     * @throws IOException              File write errors
     * @throws JAXBException            XML creation errors.
     * @throws NoSuchAlgorithmException
     */
    public final Groupmgt export(final String tpVersionId, final Map<String, List<String>> toExport) throws JAXBException, RockException, IOException, SQLException, NoSuchAlgorithmException {
        try {
            final RockFactory dwhrep = dbHelper.getDwhrep();
            GroupTypesCache.init(dwhrep);
            String versionId = tpVersionId;
            if (versionId == null) {
                versionId = dbHelper.getActiveTechpackVersion();
            }
            if (!GroupTypesCache.areGroupsDefined(versionId)) {
                throw new GroupMgtTypeNotDefinedException(versionId, "*Any*");
            }
            return getFilteredGroups(toExport, versionId);
        } finally {
            dbHelper.destroy();
        }
    }

    /**
     * this function is used by eniq-events service to get an object of group definition.
     * @param tpVersionID   can be null
     * @param toExport      group to export
     * @return an Groupmgt object contain the definition of groups
     * @throws RockException   Connecion creation errors
     * @throws JAXBException   XML creation errors
     * @throws IOException    File write errors
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     */
    public static final Groupmgt exportGroups(final String tpVersionID, final Map<String, List<String>> toExport) throws RockException, JAXBException, IOException, NoSuchAlgorithmException, SQLException {
        GroupMgtHelper.reloadProperties();
        final GroupMgtExporter exporter = new GroupMgtExporter();
        Groupmgt groupMgt = null;
        try {
            groupMgt = exporter.export(tpVersionID, toExport);
        } catch (Throwable t) { //NOPMD : Catch all errors, check & unchecked
            log("Export failed : ", t);
        } finally {
            exporter.destroy();
        }
        return groupMgt;
    }

    /**
     * Cleanup method, closes all db connections
     */
    public final void destroy() {
        dbHelper.destroy();
    }

    /**
     * Export groups to an Xml file
     *
     * @param toFile      The file to write the groups too
     * @param tpVersionId The tech pack to look up the group definitions from. If this is
     *                    null the current active techpacks is used.
     * @param toExport    List of group types and names to export, if empty all data is
     *                    exported.
     * @throws RockException            Connecion creation errors
     * @throws SQLException             Select errors
     * @throws IOException              File write errors
     * @throws JAXBException            XML creation errors.
     * @throws NoSuchAlgorithmException
     */
    public final void export(final String toFile, final String tpVersionId, final Map<String, List<String>> toExport)
            throws RockException, SQLException, IOException, JAXBException, NoSuchAlgorithmException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(GroupMgtHelper.SCHEMA_PACKAGE);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            final RockFactory dwhrep = dbHelper.getDwhrep();
            GroupTypesCache.init(dwhrep);
            String versionId = tpVersionId;
            if (versionId == null) {
                versionId = dbHelper.getActiveTechpackVersion();
            }
            if (!GroupTypesCache.areGroupsDefined(versionId)) {
                throw new GroupMgtTypeNotDefinedException(toFile, versionId, "*Any*");
            }
            final Groupmgt groupMgt = getFilteredGroups(toExport, versionId);
            final File outputFile = new File(toFile);
            marshaller.marshal(groupMgt, outputFile);
        } finally {
            dbHelper.destroy();
        }
    }

    private Groupmgt getFilteredGroups(final  Map<String, List<String>> toExport,final  String versionId) throws SQLException, RockException, IOException, NoSuchAlgorithmException {
        final Map<String, GroupTypeDef> allDefinedGps = GroupTypesCache.getGrouptypesDef(versionId);
        final Groupmgt groupMgt = xmlObjectFactory.createGroupmgt();

        for (GroupTypeDef gpType : allDefinedGps.values()) {
            if (toExport == null || toExport.isEmpty()) {
                extractGroups(groupMgt, gpType, null);                
            } else {
                if (toExport.containsKey(gpType.getGroupType())) {
                    final List<String> names = toExport.get(gpType.getGroupType());
                    extractGroups(groupMgt, gpType, names);
                }
            }
        }
        if (groupMgt.getGroup().isEmpty()) {
            log("No Groups Found.", Level.INFO);
        }

        return groupMgt;
    }

    /**
     * @param groupMgt
     * @param gpType
     * @param names
     * @throws SQLException
     * @throws RockException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private void extractGroups(final Groupmgt groupMgt, GroupTypeDef gpType, final List<String> names) throws SQLException, RockException,
            IOException, NoSuchAlgorithmException {
        SortedMap<String, Group> gpMap = exportGroupType(gpType, names);
        for (Group gp : gpMap.values()) {
            groupMgt.getGroup().add(gp);
        }
    }

    /**
     * This method extract the contents of the Group table in DWHDB and populate
     * the SortedMap args with all available groups matching the filter provided
     * by the {@link GroupTypeDef} and List parameters.
     *
     * @param gpType  The Group type to export
     * @param gpNames List of Group instances to export, if empty all groups of type
     *                gpType are extracted
     * @param gpMap   Map that gets populated with found Group instances
     * @throws SQLException                  Select errors
     * @throws java.io.IOException           If properties file cant be read
     * @throws ssc.rockfactory.RockException Db connection errors
     * @throws NoSuchAlgorithmException
     */
    private SortedMap<String, Group> exportGroupType(final GroupTypeDef gpType, final List<String> gpNames) throws SQLException, RockException, IOException, NoSuchAlgorithmException {
        final StringBuilder selectSQL = new StringBuilder();
        final StringBuilder whereSQL = new StringBuilder();
        if (null != gpNames && !gpNames.isEmpty()) {
        	whereSQL.append(" where ");
            final Iterator<String> iter = gpNames.iterator();
            while (iter.hasNext()) {
            	whereSQL.append(GroupTypeDef.KEY_NAME_GROUP_NAME).append(" = '").append(iter.next()).append("'");
                if (iter.hasNext()) {
                	whereSQL.append(" or ");
                }
            }
        }
        selectSQL.append("select * from ");
        // fix for DEFTFTMIT-568
    	// we don't have RAT_VEND_HIER321_CELL group type for 3G access area groups separate(for 2G3G4G we will have type as RAT_VEND_HIER321) if we create it from UI.
        //so if we get RAT_VEND_HIER321 type also we need to search in GROUP_TYPE_E_RAT_VEND_HIER321_CELL table
        if (GROUP_TABLE_NAME_FOR_2G4G.equalsIgnoreCase(gpType.getTableName())) {
        	selectSQL.append(OPEN_PARENTHESES);
        	selectSQL.append("select GROUP_NAME,START_TIME,STOP_TIME,HIER321_ID,HIER32_ID,HIER3_ID,HIERARCHY_1,HIERARCHY_2,HIERARCHY_3,RAT,VENDOR from ").append(GROUP_TABLE_NAME_FOR_2G4G).append(whereSQL.toString());
        	selectSQL.append(UNION_KEYWORD);
        	selectSQL.append("select GROUP_NAME,START_TIME,STOP_TIME,HIER321_ID,HIER32_ID,HIER3_ID,CELL_ID as HIERARCHY_1,HIERARCHY_2,HIERARCHY_3,RAT,VENDOR from ").append(GROUP_TABLE_NAME_FOR_3G).append(whereSQL.toString());
        	selectSQL.append(CLOSE_PARENTHESES);
        	selectSQL.append(AS_TMP);
        }
        else {
        	selectSQL.append(gpType.getTableName()).append(whereSQL.toString());
        }
        selectSQL.append(" ORDER BY ").append(GroupTypeDef.KEY_NAME_GROUP_NAME);
        return extractGroup(gpType, selectSQL.toString());
    }

    /**
     * This method extract the contents of the Group table in DWHDB and populate
     * the SortedMap args with all available groups matching the filter provided
     * by the {@link GroupTypeDef} and List parameters.
     *
     * @param gpType    The Group type to export
     * @param gpMap     Map that gets populated with found Group instances
     * @param selectSQL SQL statement to get group data
     * @throws SQLException                  Select errors
     * @throws java.io.IOException           If properties file cant be read
     * @throws ssc.rockfactory.RockException Db connection errors
     * @throws NoSuchAlgorithmException
     */
    private SortedMap<String, Group> extractGroup(final GroupTypeDef gpType, final String selectSQL)
            throws RockException, SQLException, IOException, NoSuchAlgorithmException {
        SortedMap<String, Group> gpMap = new TreeMap<String, Group>();

        final RockFactory dwhdb = dbHelper.getDwhdb();
        final Statement stmt = dwhdb.getConnection().createStatement();
        ResultSet exportResults = null; //NOPMD

        try {
            exportResults = stmt.executeQuery(selectSQL);
            if (exportResults.next()) {
                final Collection<GroupTypeKeyDef> keysToExport = gpType.getDataKeys();
                log("Exporting " + gpType.getGroupType());
                do {
                    final String gpName = exportResults.getString(GroupTypeDef.KEY_NAME_GROUP_NAME);
                    Group group;
                    if (gpMap.containsKey(gpName)) {
                        group = gpMap.get(gpName);
                    } else {
                        group = new Group(); //NOPMD
                        group.setName(gpName);
                        group.setType(gpType.getGroupType());
                        gpMap.put(gpName, group);
                    }
                    final GroupElement groupElement = xmlObjectFactory.createGroupElement();
                    group.getGroupElement().add(groupElement);
                    for (GroupTypeKeyDef key : keysToExport) {
                        final String keyName = key.getKeyName();
                        // Do not add the Hash IDs to the extracted data
                        if (new HashIdCreator().getColsForHashId(keyName) == null) {
                            final String kValue = exportResults.getString(keyName);
                            if (kValue != null) {
                                final Key k = xmlObjectFactory.createKey(); // NOPMD
                                k.setName(key.getKeyName());
                                k.setValue(kValue);
                                groupElement.getKey().add(k);
                            }
                        }
                    }
                } while (exportResults.next());
            }
        } finally {
            if (exportResults != null) {
                exportResults.close();
            }
            while (stmt.getMoreResults()) {
                stmt.getResultSet().close();
            }
            stmt.close();
        }
        return gpMap;
    }

}
