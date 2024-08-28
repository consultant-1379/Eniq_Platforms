package com.distocraft.dc5000.etl.importexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.etl.importexport.gpmgt.Group;
import com.distocraft.dc5000.etl.importexport.gpmgt.GroupElement;
import com.distocraft.dc5000.etl.importexport.gpmgt.Key;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtImportException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtInvalidDataException;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.cache.GroupTypeKeyDef;
import com.ericsson.eniq.common.HashIdCreator;

public class GroupXmlConverter {
  
    private static final String NULL_STRING = "null";

    private static final String PIPE = "|";

    private static final String EMPTY_STRING = "";

    /**
     * Directory the load files get generated too
     */
    private final transient String loadFileDir = System.getProperty("loader.dir", File.separator + "tmp");

    /**
     * BCP column delimiter character
     */
    private final transient String columnSeperator;

    /**
     * BCP row delimiting character
     */
    private final transient String rowSeperator;

    /**
     * Column wrapper
     */
    private final transient String colWrapperChar;

    /**
     * Map to hold the writers for each load file generated. Saves having to
     * reopen new writers as the groups can ge in any order in the xml file.
     */
    private final transient Map<String, BufferedWriter> writerMap;

    /**
     * Database helper
     */
    private final transient DatabaseHelper dbHelper;

    /**
     * Constructor
     * 
     * @param colSererator
     *          Seperator character to use between column
     * @param rowSeperator
     *          Seperator character to use between rows
     * @param rowWrapper
     *          Column wrapper character
     * @param helper
     *          Database helper
     */
    public GroupXmlConverter(final String colSererator, final String rowSeperator, final String rowWrapper,
            final DatabaseHelper helper) {
        this.columnSeperator = colSererator;
        this.rowSeperator = rowSeperator;
        this.colWrapperChar = rowWrapper;
        writerMap = new Hashtable<String, BufferedWriter>();
        dbHelper = helper;
    }

    /**
     * Conver the xml to bcp
     * 
     * @param dataFile
     *          XML file
     * @param tmpTablesToLoad
     *          List of temp table that are to be loaded
     * @param importGroupData
     *          XML data from file
     * @param groupDef
     *          The group definition
     * @param groupEntry
     *          Group element i.e. a row in the table
     * @param columnsToLoad
     *          Columns that will be loaded via the bcp file
     * @param fileColumnMap
     *          Map of the colums that are loaded for eac file being loaded
     * @throws RockException
     *           Connection errors
     * @throws IOException
     *           Failed to read connection properties
     * @throws SQLException
     *           SQL Errors
     * @throws NoSuchAlgorithmException
     */
    public void convertToSqlLoad(final String dataFile, final Map<String, String> tmpTablesToLoad,
            final Group importGroupData, final GroupTypeDef groupDef, final GroupElement groupEntry,
            final String columnsToLoad, final Map<File, String> fileColumnMap) throws RockException, SQLException,
            IOException, NoSuchAlgorithmException {
        // NOPMD : Avoid long names :Descriptive naming
        final List<Key> dataKeyListFromFile = groupEntry.getKey();
        // the columns on the table are naturally ordered..
        Collections.sort(dataKeyListFromFile, new Comparator<Key>() {

            @Override
            public int compare(final Key dataKeyFromFile1, final Key dataKeyFromFile2) {
                return dataKeyFromFile1.getName().toUpperCase().compareTo(dataKeyFromFile2.getName().toUpperCase());
            }
        });

        // the load file command is in the format 'LOAD TABLE $TABLE <columns> from
        // $FILE'
        // so the data in the file needs to be in the index order as the <columns>
        // are so
        // pull the values from the xml file, store then in a tmp map and iterate
        // over the
        // GroupTypeDef for the Grouptype, the GroupTypeDef.getKeys() method orders
        // by natural String comparisons
        // and wheather or not the key is a datatype or not i.e. a Key/Column on the
        // MeasurementType.
        // NOPMD : Avoid long names : Descriptive naming
        final Map<String, String> unorderedSourceData = new HashMap<String, String>();
        unorderedSourceData.put(GroupTypeDef.KEY_NAME_GROUP_NAME, importGroupData.getName());
        unorderedSourceData.put(GroupTypeDef.KEY_NAME_START_TIME, GroupMgtHelper.getCurrentTime());
        unorderedSourceData.put(GroupTypeDef.KEY_NAME_STOP_TIME, GroupMgtHelper.getMaxTime());
        for (final Key aKeyFromFile : dataKeyListFromFile) {
            if (!groupDef.isValidKey(aKeyFromFile.getName())) {
                throw new GroupMgtInvalidDataException(dataFile, importGroupData.getType(), importGroupData.getName(),
                        aKeyFromFile.getName(), aKeyFromFile.getValue());
            }
            unorderedSourceData.put(aKeyFromFile.getName().toUpperCase(), aKeyFromFile.getValue());
        }
        final Collection<GroupTypeKeyDef> orderedSqlKeys = groupDef.getKeys();
        final StringBuilder sqlBuffer = new StringBuilder();
        // NOPMD : Avoid long names : Descriptive naming
        final Iterator<GroupTypeKeyDef> orderKeyDefIterator = orderedSqlKeys.iterator();
        while (orderKeyDefIterator.hasNext()) {
            final GroupTypeKeyDef orderKey = orderKeyDefIterator.next();
            final String nextKeyInSequence = orderKey.getKeyName().toUpperCase();
            sqlBuffer.append(colWrapperChar);

            String dataForColumn = unorderedSourceData.get(nextKeyInSequence);
            // DataForColumn will be null if this is one of the hashId columns
            if (dataForColumn == null) {
                dataForColumn = getHashId(unorderedSourceData, nextKeyInSequence);
            }
            sqlBuffer.append(dataForColumn);

            sqlBuffer.append(colWrapperChar);
            if (orderKeyDefIterator.hasNext()) {
                sqlBuffer.append(columnSeperator);
            }
        }
        final String tmpTableName = groupDef.getTempTableName();
        // we've got one line to append to the load file...
        writeToFile(loadFileDir, tmpTableName, sqlBuffer.toString(), columnsToLoad, fileColumnMap);
        if (!tmpTablesToLoad.containsKey(tmpTableName)) {
            final String tmpTableCreateDDL = dbHelper.getTempTableDDL(tmpTableName, groupDef);
            tmpTablesToLoad.put(tmpTableName, tmpTableCreateDDL);
        }
    }

    /**
     * Get hash ID value from columns that are associated with this hash ID
     * 
     * @param unorderedSourceData
     * @param hashIdName
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    protected String getHashId(final Map<String, String> unorderedSourceData, final String hashIdName)
            throws NoSuchAlgorithmException, IOException {
        String hashIdValue = null;
        final HashIdCreator hashIdCreator = new HashIdCreator();
        final List<String> columns = hashIdCreator.getColsForHashId(hashIdName);

        if (columns != null) {
            final Iterator<String> colsIterator = columns.iterator();
            final StringBuilder strBuilder = new StringBuilder();
            while (colsIterator.hasNext()) {
                final String column = colsIterator.next();
                final String dataForColumn = unorderedSourceData.get(column);
                if (dataForColumn == null || dataForColumn.equalsIgnoreCase(NULL_STRING)) {
                    strBuilder.append(EMPTY_STRING);
                } else {
                    strBuilder.append(dataForColumn);
                }

                if (colsIterator.hasNext()) {
                    strBuilder.append(PIPE);
                }
            }
            final long id = hashIdCreator.hashStringToLongId(strBuilder.toString());
            hashIdValue = Long.toString(id);
        }
        return hashIdValue;
    }

    /**
     * Write a line to file
     * 
     * @param outputDir
     *          The directory containing the bcp load files.
     * @param tableName
     *          The table the row will be writtin into.
     * @param dataRow
     *          The row of data
     * @param columns
     *          The columns being loaded.
     * @param fileColumnMap
     *          Map of the colums that are loaded for eac file being loaded
     */
    protected final void writeToFile(final String outputDir, final String tableName, final String dataRow,
            final String columns, final Map<File, String> fileColumnMap) {
        BufferedWriter writer;
        final File fileName = new File(outputDir + File.separatorChar + tableName + ".sql");
        try {
            if (writerMap.containsKey(tableName)) {
                writer = writerMap.get(tableName);
            } else {
                writer = new BufferedWriter(new FileWriter(fileName, false));
                writerMap.put(tableName, writer);
                fileColumnMap.put(fileName, columns);
            }
            writer.write(dataRow);
            writer.write(rowSeperator);
            writer.flush();
        } catch (final IOException e) {
            throw new GroupMgtImportException("Failed to write to file " + fileName, e);
        }
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        for (final BufferedWriter w : writerMap.values()) {
            try {
                w.flush();
                w.close();
            } catch (final IOException e) {/**/
            } // NOPMD : Close all possible
        }
        writerMap.clear();
    }
}
