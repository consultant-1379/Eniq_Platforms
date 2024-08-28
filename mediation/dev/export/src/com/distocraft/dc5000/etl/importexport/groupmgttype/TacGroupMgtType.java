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

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.etl.importexport.DatabaseHelper;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtException;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;

/**
 * A Group Management Type for importing/appending/deleting specific groups that
 * contain the column TAC
 * 
 * @author epaujor
 * 
 */
public class TacGroupMgtType extends GroupMgtType {

  private static final String WHERE_GROUP_NAME_CLAUSE_FOR_EXCLUSIVE_TAC = " where GROUP_NAME='EXCLUSIVE_TAC' and ISNULL(TAC,-1) in "
      + "(select TAC from dc.GROUP_TYPE_E_TAC where GROUP_NAME<>'EXCLUSIVE_TAC')";

  private static final String EMPTY_STRING = " ";

  private static final String TAC = "TAC";

  private static final String WHERE_GROUP_NAME_CLAUSE_FOR_NON_EXCLUSIVE_TAC = " where GROUP_NAME<>'EXCLUSIVE_TAC' and ISNULL(TAC,-1) in "
      + "(select TAC from dc.GROUP_TYPE_E_TAC where GROUP_NAME='EXCLUSIVE_TAC')";

  private static final String SELECT_DISTINCT_TAC_FROM = "select distinct(TAC) from ";

  public TacGroupMgtType(final GroupTypeDef gpType, final long maxGroups, final DatabaseHelper dbHelper) {
    super(gpType, maxGroups, dbHelper);
  }

  @Override
  public void importDataFileAppend(final String mergeTemplate) throws SQLException, RockException, IOException {
    final Statement stmt = dbHelper.getDwhdb().getConnection().createStatement();
    ResultSet tacResult = null;
    try {
      // Check non-EXCLUSIVE_TAC groups to see that TACs in EXCLUSIVE_TAC are
      // not members
      tacResult = stmt.executeQuery(getSqlForNonExclTacGroup());
      final String tacValues = getTacValuesAsString(tacResult);

      if (tacValues.isEmpty()) {
        importTacs(mergeTemplate, stmt);
      } else {
        throw new GroupMgtException(
            "\nThese TAC value(s) are present in the group 'EXCLUSIVE_TAC' and cannot be present in other groups: "
                + tacValues + ".\nIMPORT FAILED.", null);
      }
    } finally {
      if (tacResult != null) {
        tacResult.close();
      }
      stmt.close();
    }
  }

  protected void importTacs(final String mergeTemplate, final Statement stmt) throws SQLException, RockException,
      IOException {
    ResultSet tacResult = null;
    try {
      String tacValues;
      // Check EXCLUSIVE_TAC groups to see that TACs in non-EXCLUSIVE_TAC are
      // not members
      tacResult = stmt.executeQuery(getSqlForExclTacGroup());
      tacValues = getTacValuesAsString(tacResult);
      if (tacValues.isEmpty()) {
        final long totalGroups = dbHelper.getNumberOfGroupsForGroupType(gpType.getTableName(),
            gpType.getTempTableName(), DEFAULT_FP_GROUP_NAME, dbHelper.getDwhdb());
        checkGroupLimits(mergeTemplate, totalGroups, "");
      } else {
        throw new GroupMgtException(
            "\nThese TAC value(s) are present in other groups and cannot be present in the group 'EXCLUSIVE_TAC': "
                + tacValues + ".\nIMPORT FAILED.", null);
      }
    } finally {
      if (tacResult != null) {
        tacResult.close();
      }
    }
  }

  private String getTacValuesAsString(final ResultSet tacResult) throws SQLException {
    final StringBuilder tacValues = new StringBuilder();
    while (tacResult.next()) {
      tacValues.append(EMPTY_STRING);
      tacValues.append(tacResult.getLong(TAC));
      tacValues.append(EMPTY_STRING);
    }

    return tacValues.toString();
  }

  private String getSqlForNonExclTacGroup() {
    final StringBuilder sqlQuery = new StringBuilder();
    sqlQuery.append(SELECT_DISTINCT_TAC_FROM);
    sqlQuery.append(dbHelper.getDcSchema());
    sqlQuery.append(gpType.getTempTableName());
    sqlQuery.append(WHERE_GROUP_NAME_CLAUSE_FOR_NON_EXCLUSIVE_TAC);
    return sqlQuery.toString();
  }

  private String getSqlForExclTacGroup() {
    final StringBuilder sqlQuery = new StringBuilder();
    sqlQuery.append(SELECT_DISTINCT_TAC_FROM);
    sqlQuery.append(dbHelper.getDcSchema());
    sqlQuery.append(gpType.getTempTableName());
    sqlQuery.append(WHERE_GROUP_NAME_CLAUSE_FOR_EXCLUSIVE_TAC);
    return sqlQuery.toString();
  }
}
