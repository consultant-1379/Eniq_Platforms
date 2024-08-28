package com.ericsson.eniq.techpacksdk.view.reference;

import java.util.Vector;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class ReferenceTypeColumnTableModelFactory {

  /**
   * The table type/name
   */
  private static final String MY_TABLE_NAME = "Columns";

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int MAX_ROWS_SHOWN = 25;

  private static final String IQ_INDEX = "IQIndex";

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] MY_COLUMN_NAMES = { "Name", "DataType", "DataSize", "DataScale",
      "DuplicateConstraint",
      "Mandatory", "Universe Class", "Universe Object", "Universe Condition", "SQL Interface Support",
      "Topology Update Use", "Description" };

  /**
   * Column names, used as headings for the columns when techpack type is ENIQ EVENTS.
   */
  private static final String[] MY_EVENT_COLUMN_NAMES = { "Name", "DataType", "DataSize", "DataScale",
      "DuplicateConstraint", "Mandatory", "Universe Class", "Universe Object", "Universe Condition",
      "SQL Interface Support", "Topology Update Use", "Description", IQ_INDEX };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] MY_COLUMN_WIDTHS = { 128, 120, 60, 60, 120, 120, 120, 90, 90, 90, 60, 200 };

  /**
   * Column widths, used to graphically layout the columns when techpack type is ENIQ EVENTS.
   */
  private static final int[] MY_EVENT_COLUMN_WIDTHS = { 128, 120, 60, 60, 120, 120, 120, 90, 90, 90, 60, 200, 120 };

  public static ReferenceTypeColumnTableModel createReferenceTypeColumnTableModel(final Application application,
      final RockFactory rockFactory, final Vector<TableInformation> tableInfos, final boolean isTreeEditable,
      final Referencetable referencetable, final boolean isBaseTechPack, final Vector<Referencecolumn> publiccolumns,
      final String techpackType) {
    if (techpackType.equals(Constants.ENIQ_EVENT)) {
      return new EventsReferenceTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
          referencetable, isBaseTechPack, publiccolumns, techpackType, MY_EVENT_COLUMN_NAMES, MY_EVENT_COLUMN_WIDTHS,
          MY_TABLE_NAME, Constants.EVENTSDATATYPES);
    }

    return new ReferenceTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable, referencetable,
        isBaseTechPack, publiccolumns, techpackType, MY_COLUMN_NAMES, MY_COLUMN_WIDTHS, MY_TABLE_NAME, Constants.DATATYPES);
  }

  /**
   * Static method that returns the table type and its corresponding column names
   * 
   * @return
   */
  public static TableInformation createTableTypeInfo(final String techpackType) {
    if (techpackType.equals(Constants.ENIQ_EVENT)) {
      return new TableInformation(MY_TABLE_NAME, MY_EVENT_COLUMN_NAMES, MY_EVENT_COLUMN_WIDTHS, MAX_ROWS_SHOWN);
    }
    return new TableInformation(MY_TABLE_NAME, MY_COLUMN_NAMES, MY_COLUMN_WIDTHS, MAX_ROWS_SHOWN);

  }
}
