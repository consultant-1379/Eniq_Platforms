/**------------------------------------------------------------------------
 *
 *
 *      COPYRIGHT (C)                   ERICSSON RADIO SYSTEMS AB, Sweden
 *
 *      The  copyright  to  the document(s) herein  is  the property of
 *      Ericsson Radio Systems AB, Sweden.
 *
 *      The document(s) may be used  and/or copied only with the written
 *      permission from Ericsson Radio Systems AB  or in accordance with
 *      the terms  and conditions  stipulated in the  agreement/contract
 *      under which the document(s) have been supplied.
 *
 *------------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.importexport.groupmgttype;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.etl.importexport.DatabaseHelper;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;

/**
 * A Group Management Type for importing/appending/deleting specific groups that
 * contain the column RAT
 *
 * @author epaujor
 *
 */
public class RatGroupMgtType extends GroupMgtType {

    private static final String GROUP_TYPE_E_RAT_VEND_HIER321 = "GROUP_TYPE_E_RAT_VEND_HIER321";

    private static final String GROUP_TYPE_E_RAT_VEND_HIER321_CELL = "GROUP_TYPE_E_RAT_VEND_HIER321_CELL";

    public RatGroupMgtType(final GroupTypeDef gpType, final long maxGroups,
            final DatabaseHelper dbHelper) {
        super(gpType, maxGroups, dbHelper);
    }

    @Override
    public void importDataFileAppend(final String mergeTemplate)
            throws SQLException, RockException, IOException {
        if (checkOnlyOneRatExistsInGroup()) {
            if (checkOnlySACOrCELLExistsIn3GGroup()) {
                if (checkNoDuplicateGroupNames()) {
                    final List<Integer> listOfRatValues = dbHelper
                            .getRatValuesFromTempTable(
                                    gpType.getTempTableName(),
                                    dbHelper.getDwhdb());
                    for (int ratValue : listOfRatValues) {
                        importDataFileAppendForRatValue(mergeTemplate, ratValue);
                    }
                }
            }
        }
    }

    private void importDataFileAppendForRatValue(final String mergeTemplate,
            final int ratValue) throws SQLException, RockException, IOException {
        final String infoMessage = " with RAT = " + ratValue;
        final int totalGroups = dbHelper.getNumberOfGroupsForRatValue(
                gpType.getTableName(), gpType.getTempTableName(),
                dbHelper.getDwhdb(), ratValue);
        checkGroupLimits(mergeTemplate, totalGroups, infoMessage);
    }

    /**
     * Check that one RAT value exists in the group
     *
     * @return
     * @throws SQLException
     * @throws RockException
     * @throws IOException
     */
    private boolean checkOnlyOneRatExistsInGroup() throws SQLException,
            RockException, IOException {
        boolean isOneRatValueInGroup = true;
        final Statement stmt = dbHelper.getDwhdb().getConnection()
                .createStatement();
        ResultSet ratResult = null;
        final String tempTableName = gpType.getTempTableName();
        try {
            if (tempTableName.contains(GROUP_TYPE_E_RAT_VEND_HIER321)) {
                ratResult = stmt
                        .executeQuery("select tmp.GROUP_NAME, count(distinct(tmp.RAT)) as cnt from "
                                + "(SELECT GROUP_NAME, RAT FROM "
                                + dbHelper.getDcSchema()
                                + "GROUP_TYPE_E_RAT_VEND_HIER321 union all "
                                + "SELECT GROUP_NAME, RAT FROM "
                                + dbHelper.getDcSchema()
                                + "GROUP_TYPE_E_RAT_VEND_HIER321_CELL union all "
                                + "SELECT GROUP_NAME, RAT FROM "
                                + dbHelper.getDcSchema()
                                + tempTableName
                                + ") as tmp group by tmp.GROUP_NAME");
            } else {
                ratResult = stmt
                        .executeQuery("select tmp.GROUP_NAME, count(distinct(tmp.RAT)) as cnt from "
                                + "(SELECT GROUP_NAME, RAT FROM "
                                + dbHelper.getDcSchema()
                                + gpType.getTableName()
                                + " union all "
                                + "SELECT GROUP_NAME, RAT FROM "
                                + dbHelper.getDcSchema()
                                + tempTableName
                                + ") as tmp group by tmp.GROUP_NAME");
            }
            while (ratResult.next()) {
                if (ratResult.getInt("cnt") > 1) {
                    log("More than one RAT value exists in the group '"
                            + ratResult.getString("GROUP_NAME")
                            + "'. This is not allowed.\nIMPORT FAILED.",
                            Level.WARNING);
                    isOneRatValueInGroup = false;
                    break;
                }
            }
        } finally {
            stmt.close();
            if (ratResult != null) {
                ratResult.close();
            }
        }
        return isOneRatValueInGroup;
    }

    /**
     * Check that only SAC or CELL values exist in the group
     *
     * @return
     * @throws SQLException
     * @throws RockException
     * @throws IOException
     */
    private boolean checkOnlySACOrCELLExistsIn3GGroup() throws SQLException,
            RockException, IOException {
        boolean isOnlyCellValuesInGroup = true;
        boolean is3G = true;
        final Statement stmt = dbHelper.getDwhdb().getConnection()
                .createStatement();
        ResultSet ratResult = null;
        final String tempTableName = gpType.getTempTableName();
        try {
            if (tempTableName
                    .equalsIgnoreCase("TMP_GROUP_TYPE_E_RAT_VEND_HIER321_CELL")) {
                ratResult = stmt
                        .executeQuery("select tmp.GROUP_NAME, tmp.CELL_ID from "
                                + "(SELECT GROUP_NAME, CELL_ID  FROM "
                                + dbHelper.getDcSchema()
                                + "GROUP_TYPE_E_RAT_VEND_HIER321_CELL union all "
                                + "SELECT GROUP_NAME,CELL_ID FROM "
                                + dbHelper.getDcSchema()
                                + tempTableName
                                + ") as tmp group by tmp.GROUP_NAME ,tmp.CELL_ID ");
                while (ratResult.next()) {
                    if (ratResult.getString("CELL_ID").equals("null")) {
                        log("SAC and CELL value exists in the group '"
                                + ratResult.getString("GROUP_NAME")
                                + "'. This is not allowed.\nIMPORT FAILED.",
                                Level.WARNING);
                        isOnlyCellValuesInGroup = false;
                        break;

                    }
                }
            }
        } finally {
            stmt.close();
            if (ratResult != null) {
                ratResult.close();
            }

        }

        return isOnlyCellValuesInGroup;
    }

    /**
     * Check that there is no duplicate groups names between 2G Cell groups, 3G
     * SAC groups and/or 3G Cell groups
     *
     * @return
     * @throws SQLException
     * @throws RockException
     * @throws IOException
     */
    private boolean checkNoDuplicateGroupNames() throws SQLException,
            RockException, IOException {
        boolean isOneGroupName = true;
        final Statement stmt = dbHelper.getDwhdb().getConnection()
                .createStatement();
        ResultSet duplicateGrpNameResult = null;
        final String tempTableName = gpType.getTempTableName();
        try {
            if (tempTableName.contains(GROUP_TYPE_E_RAT_VEND_HIER321)) {
                duplicateGrpNameResult = stmt
                        .executeQuery("select tmp.GROUP_NAME, count(*) as noOfGroups from "
                                + "(SELECT distinct GROUP_NAME FROM "
                                + dbHelper.getDcSchema()
                                + "GROUP_TYPE_E_RAT_VEND_HIER321 union all "
                                + "SELECT distinct GROUP_NAME FROM "
                                + dbHelper.getDcSchema()
                                + "GROUP_TYPE_E_RAT_VEND_HIER321_CELL union all "
                                + "SELECT distinct GROUP_NAME FROM "
                                + dbHelper.getDcSchema()
                                + tempTableName
                                + " where GROUP_NAME not in (SELECT distinct(GROUP_NAME) from "
                                + dbHelper.getDcSchema()
                                + gpType.getTableName()
                                + ")"
                                + ") as tmp group by tmp.GROUP_NAME");

                while (duplicateGrpNameResult.next()) {
                    if (duplicateGrpNameResult.getInt("noOfGroups") > 1) {
                        log("The group '"
                                + duplicateGrpNameResult
                                        .getString("GROUP_NAME")
                                + "' cannot have a combinatition of 2G Cells, 3G SACs and/or 3G Cells. This is not allowed.\nIMPORT FAILED.",
                                Level.WARNING);
                        isOneGroupName = false;
                        break;
                    }
                }
            }
        } finally {
            stmt.close();
            if (duplicateGrpNameResult != null) {
                duplicateGrpNameResult.close();
            }
        }
        return isOneGroupName;
    }

}