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
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.importexport.GroupMgtHelper;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.etl.importexport.DatabaseHelper;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtImportException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtVelocityException;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.cache.GroupTypeKeyDef;

/**
 * A Group Management Type for importing/appending/deleting specific groups
 * 
 * @author epaujor
 * 
 */
public class GroupMgtType {

  /**
   * Velocity Context object for the DC schema
   */
  private static final String VCTX_SCHEMA = "SCH";

  /**
   * Velocity Context object for the destination table name
   */
  private static final String VCTX_DEST = "DEST";

  /**
   * Velocity Context object for the temporary table name
   */
  private static final String VCTX_TMP = "TMP";

  /**
   * Velocity Context object for the Group Name column
   */
  private static final String VCTX_GPN_COL = "GPN_COL";

  /**
   * Velocity Context object for the data keys being loaded
   */
  private static final String VCTX_GP_KEY = "GP_KEY";

  /**
   * Velocity Context object for the all keys being loaded
   */
  private static final String VCTX_ALL_GP_COLS = "ALL_GP_COLS";

  /**
   * The FeaturePack-specific condition for group names
   */
  protected static final String DEFAULT_FP_GROUP_NAME = "TopDataConsumers_%";

  final GroupTypeDef gpType;

  DatabaseHelper dbHelper;

  private final long maxGroups;

  /**
   * Logger
   */
  private static final Logger LOGGER = Logger.getLogger("gpmgt.import");

  public GroupMgtType(final GroupTypeDef gpType, final long maxGroups, final DatabaseHelper dbHelper) {
    this.gpType = gpType;
    this.maxGroups = maxGroups;
    this.dbHelper = dbHelper;
  }

  /**
   * Used to add/append to a group
   * 
   * @param mergeTemplate
   * @throws SQLException
   * @throws RockException
   * @throws IOException
   */
  public void importDataFileAppend(final String mergeTemplate) throws SQLException, RockException,
      IOException {
    final long totalGroups = dbHelper.getNumberOfGroupsForGroupType(gpType.getTableName(), gpType.getTempTableName(),
        DEFAULT_FP_GROUP_NAME, dbHelper.getDwhdb());
    checkGroupLimits(mergeTemplate, totalGroups, "");
  }
  
  /**
   * Used to delete a group
   * 
   * @param deleteTemplate
   * @throws SQLException
   * @throws RockException
   * @throws IOException
   */
  public void importDataFileDelete(final String deleteTemplate) throws SQLException, RockException, IOException {
    final int rowsDeleted = mergeTables(deleteTemplate);
    log(rowsDeleted + " entries deleted for Group Type " + gpType.getGroupType(), Level.INFO);
  }

  protected void checkGroupLimits(final String mergeTemplate, final long totalGroups,
      final String infoMessage) throws SQLException, RockException, IOException {
    if (totalGroups <= maxGroups) {
      final int rowsAdded = mergeTables(mergeTemplate);
      log(rowsAdded + " entries added for Group Type " + gpType.getGroupType() + infoMessage, Level.INFO);
      log("Import Complete.", Level.INFO);
    } else {
        GroupMgtHelper.groupLimitExceeded=true;  //group limit exceeded .
      log("Maximum number of groups exceeded. \nNumber of groups after import would be '" + totalGroups + "'"
              + infoMessage + ".\nMaximum number is '" + maxGroups + "'.\nIMPORT FAILED.", Level.WARNING);
    }
  }

  /**
   * Merge the temp table with the data table
   * 
   * @param gpType
   *          Group type being imported
   * @param template
   *          The template to use to generate the join statement
   * @throws RockException
   *           Connection errors
   * @throws IOException
   *           Failed to read connection properties
   * @throws SQLException
   *           SQL Errors
   * @return The number of rows added/deleted from the group table
   */
  private int mergeTables(final String template) throws SQLException, RockException,
      IOException {
    // tmp tables are loaded, now merge with real tables...
    final StringWriter sqlWriter = new StringWriter();
    final VelocityContext vctx = new VelocityContext();
    vctx.put(VCTX_SCHEMA, dbHelper.getDcSchema()); // dc.
    vctx.put(VCTX_DEST, gpType.getTableName()); // GROUP_TYPE_E_IMSI
    vctx.put(VCTX_TMP, gpType.getTempTableName()); // #GROUP_TYPE_E_IMSI
    vctx.put(VCTX_GPN_COL, GroupTypeDef.KEY_NAME_GROUP_NAME); // GROUP_NAME
    // Columns of group (including name, start and end time)
    vctx.put(VCTX_ALL_GP_COLS, getListOfKeyNames(gpType.getKeys()));
    // Columns of group (excluding name, start and end time)
    vctx.put(VCTX_GP_KEY, getListOfKeyNames(gpType.getDataKeys()));
    boolean mergedOk;
    try {
      mergedOk = Velocity.mergeTemplate(template, Velocity.ENCODING_DEFAULT, vctx, sqlWriter);
      if (mergedOk) {
        log(sqlWriter.toString(), Level.FINE);
        return dbHelper.executeUpdateDwhdb(sqlWriter.toString());
      }
      throw new GroupMgtImportException("Velocity Merge failed for an unknown reason..");
    } catch (VelocityException e) {
      throw new GroupMgtVelocityException(template, e);
    } catch (Exception e) {
      throw new GroupMgtVelocityException(template, e);
    }
  }

  private List<String> getListOfKeyNames(final Collection<GroupTypeKeyDef> keys) {
    final List<String> keyNames = new ArrayList<String>();
    for (GroupTypeKeyDef key : keys) {
      keyNames.add(key.getKeyName());
    }
    return keyNames;
  }

  /**
   * Log a message
   * 
   * @param msg
   *          What to log
   * @param lvl
   *          Level to log it at
   */
  protected static void log(final String msg, final Level lvl) {
    LOGGER.log(lvl, msg);
  }
}
