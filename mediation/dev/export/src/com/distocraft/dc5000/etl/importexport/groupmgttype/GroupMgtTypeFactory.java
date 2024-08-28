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

import com.distocraft.dc5000.etl.importexport.DatabaseHelper;
import com.distocraft.dc5000.repository.cache.GroupTypeDef;

/**
 * This factory class is used for creating a Group Management Type base on the
 * group table name
 * 
 * @author epaujor
 * 
 */
public class GroupMgtTypeFactory {

  private static final String GROUP_TYPE_E_RAT = "GROUP_TYPE_E_RAT";
  private static final String GROUP_TYPE_E_TAC = "GROUP_TYPE_E_TAC";

  private GroupMgtTypeFactory() {
  }

  /**
   * Creates the specific Group Management Type base on the group table name
   * 
   * @param gpType
   * @param maxGroups
   * @param dbHelper
   * @return
   */
  public static GroupMgtType createGroupMgtType(final GroupTypeDef gpType, final long maxGroups,
      final DatabaseHelper dbHelper) {
    if (gpType.getTableName().startsWith(GROUP_TYPE_E_TAC)) {
      return new TacGroupMgtType(gpType, maxGroups, dbHelper);
    } else if (gpType.getTableName().startsWith(GROUP_TYPE_E_RAT)) {
      return new RatGroupMgtType(gpType, maxGroups, dbHelper);
    } else {
      return new GroupMgtType(gpType, maxGroups, dbHelper);
    }
  }
}
