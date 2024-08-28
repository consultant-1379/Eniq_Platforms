package com.ericsson.eniq.techpacksdk.view.group;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.ComboBoxTableCellEditor;
import tableTree.ComboBoxTableCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the group column table.
 * 
 * @author eheijun
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class GroupTypeColumnTableModel extends TTTableModel {

  private static final Logger logger = Logger.getLogger(GroupTypeColumnTableModel.class.getName());

  private final GroupTable groupTable;

  private final Application application;

  private static final int datanameColumnIdx = 0;

  private static final int datatypeColumnIdx = 1;

  private static final int datasizeColumnIdx = 2;

  private static final int datascaleColumnIdx = 3;

  private static final int nullableColumnIdx = 4;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Name", "DataType", "DataSize", "DataScale", "Nullable" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 128, 120, 60, 60, 120 };

  /**
   * The table type/name
   */
  private static final String myTableName = "GroupColumns";

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 20;

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;


  /**
   * Constructor. Initializes the column names, widths and table name.
   */
  public GroupTypeColumnTableModel(final Application application, final RockFactory rockFactory,
      final List<TableInformation> tableInfos, final boolean isTreeEditable, final GroupTable groupTable) {
    super(rockFactory, (Vector<TableInformation>) tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.groupTable = groupTable;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  protected Object getColumnValue(final Grouptypes groupColumn, final int col) {
    if (groupColumn != null) {
      switch (col) {
      case datanameColumnIdx:
        return Utils.replaceNull(groupColumn.getDataname());

      case datatypeColumnIdx:
        return Utils.replaceNull(groupColumn.getDatatype());

      case datasizeColumnIdx:
        return Utils.replaceNull(groupColumn.getDatasize());

      case datascaleColumnIdx:
        return Utils.replaceNull(groupColumn.getDatascale());

      case nullableColumnIdx:
        return (Utils.integerToBoolean(groupColumn.getNullable()));

      default:
        logger.info("Column with index: " + col + " is undefined.");
        break;
      }
    }
    return null;

  }

  protected void setColumnValue(final Grouptypes groupColumn, final int col, final Object value) {
    switch (col) {
    case datanameColumnIdx:
      groupColumn.setDataname((String) value);
      break;

    case datatypeColumnIdx:
      groupColumn.setDatatype((String) value);
      break;

    case datasizeColumnIdx:
      groupColumn.setDatasize((Integer) value);
      break;

    case datascaleColumnIdx:
      groupColumn.setDatascale((Integer) value);
      break;

    case nullableColumnIdx:
      if ((Boolean) value) {
        groupColumn.setNullable(1);
      } else {
        groupColumn.setNullable(0);
      }
      break;

    default:
      logger.info("Column with index: " + col + " is undefined.");
      break;
    }

  }

  /**
   * Overridden method for getting the value at a certain position in the table.
   * Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getValueAt(final int row, final int col) {
    Object result = null;
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Grouptypes)) {
        final Grouptypes groupColumn = (Grouptypes) displayData.elementAt(row);
        result = getColumnValue(groupColumn, col);
      }
    }
    return result;
  }

  /**
   * Overridden method for getting the value at a certain position in the
   * original table data. Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getOriginalValueAt(final int row, final int col) {
    Object result = null;
    if (data.size() >= row) {
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Grouptypes)) {
        final Grouptypes groupcolumn = (Grouptypes) data.elementAt(row);
        result = getColumnValue(groupcolumn, col);
      }
    }
    return result;
  }

  /**
   * Overridden version of this method. Returns the value in a specified column
   * for the given data object. Returns null in case an invalid column index.
   * 
   * @return The data object in the cell
   */
  @Override
  public Object getColumnValueAt(final Object dataObject, final int col) {
    Object result = null;
    if ((dataObject != null) && (dataObject instanceof Grouptypes)) {
      final Grouptypes groupColumn = (Grouptypes) dataObject;
      result = getColumnValue(groupColumn, col);
    }
    return result;
  }

  /**
   * Overridden method for setting the value at a certain position in the table
   * Sets it in the corresponding RockDBObject.
   */
  @Override
  public void setValueAt(final Object value, final int row, final int col) {
    final int index = data.indexOf(displayData.elementAt(row));
    final Grouptypes groupcolumn = (Grouptypes) data.elementAt(index);
    setColumnValue(groupcolumn, col, value);
    // fireTableCellUpdated(row, col);
    refreshTable();
    fireTableDataChanged();
  }


  /**
   * Overridden method for setting the column editor of the Description column.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {
    // Set editor for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellEditor limitedTextEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[datanameColumnIdx], Grouptypes.getDatanameColumnSize(), true);
    datanameColumn.setCellEditor(limitedTextEditor);

    // Set editor for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellEditor datatypeColumnComboEditor = new ComboBoxTableCellEditor(Constants.EVENTSDATATYPES);
    datatypeColumn.setCellEditor(datatypeColumnComboEditor);
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[datanameColumnIdx], Grouptypes.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);

    // Set renderer for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellRenderer datatypeComboRenderer = new ComboBoxTableCellRenderer(Constants.EVENTSDATATYPES,
        true);
    datatypeColumn.setCellRenderer(datatypeComboRenderer);
  }

  /**
   * Overridden method for creating specifically new groupcolumns.
   */
  @Override
  public RockDBObject createNew() {
    final Grouptypes groupcolumn = new Grouptypes(rockFactory);
    groupcolumn.setGrouptype(groupTable.getTypeName());
    logger.finest("VersionId = " + groupTable.getVersionid());
    groupcolumn.setVersionid(groupTable.getVersionid());
    groupcolumn.setDataname("");
    groupcolumn.setDatasize(0);
    groupcolumn.setDatascale(0);
    groupcolumn.setNullable(0);
    return groupcolumn;
  }

  /**
   * Overridden version of this method for saving specifically groupcolumns.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {
    final Grouptypes column = ((Grouptypes) rockObject);
    try {
      if (column.gimmeModifiedColumns().size() > 0) {
        logger.finest("VersionId = " + groupTable.getVersionid());
        column.saveToDB();
        logger.info("save key " + column.getDataname() + " of " + column.getGrouptype());
      }
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(),
          application.getContext().getResourceMap().getString("save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }

  }

  /**
   * Overridden version of this method for deleting specifically groupcolumns.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    try {
      ((Grouptypes) rockObject).deleteDB();
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(),
          application.getContext().getResourceMap().getString("delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    final Grouptypes orig = (Grouptypes) toBeCopied;
    final Grouptypes copy = (Grouptypes) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  @Override
  public Vector<String> validateData() {// NOPMD Leave as vector as abstract returns vector
    final Vector<String> errorStrings = new Vector<String>();
    for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      final Object obj = iter.next();
      if (obj instanceof Grouptypes) {
        final Grouptypes groupcolumn = (Grouptypes) obj;

        errorStrings.addAll(validateDataName(groupcolumn));
        errorStrings.addAll(validateDataType(groupcolumn));
      }
    }

    return errorStrings;
  }

  private List<String> validateDataType(final Grouptypes groupcolumn) {
    final List<String> errorStrings = new Vector<String>();

    if (Utils.replaceNull(groupcolumn.getDatatype()).trim().equals("")) {
      errorStrings.add(groupTable.getTypeName() + ": " + myColumnNames[datatypeColumnIdx] + " for column is required");
    }
    final Vector<String> datatypeCheck = Utils.checkDatatype(groupcolumn.getDatatype(), groupcolumn.getDatasize(),
        groupcolumn.getDatascale());
    for (final Iterator<String> iter2 = datatypeCheck.iterator(); iter2.hasNext();) {
      errorStrings.add(groupTable.getTypeName() + " " + groupcolumn.getDataname() + " Column: "
          + iter2.next());
    }

    return errorStrings;
  }

  private List<String> validateDataName(final Grouptypes groupcolumn) {
    final List<String> errorStrings = new Vector<String>();
    if (Utils.replaceNull(groupcolumn.getDataname()).trim().equals("")) {
      errorStrings.add(groupTable.getTypeName() + ": " + myColumnNames[datanameColumnIdx]
          + " for column is required");
    } else {
      for (final Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
        final Object obj2 = iter2.next();
        if (obj2 instanceof Grouptypes) {
          final Grouptypes groupcolumn2 = (Grouptypes) obj2;
          if (groupcolumn2 != groupcolumn) {
            if (groupcolumn2.getDataname().equals(groupcolumn.getDataname())) {
              errorStrings.add(groupTable.getTypeName() + " Key: " + myColumnNames[datanameColumnIdx] + " ("
                  + groupcolumn.getDataname() + ") is not unique");
            }
          }
        }
      }
    }
    return errorStrings;
  }

  public static TableInformation createTableTypeInfo() {
    return new TableInformation(myTableName, myColumnNames, myColumnWidths, maxRowsShown);
  }

  @Override
  public void update(final Observable sourceObject, final Object sourceArgument) {

  }

  @Override
  public String getColumnFilterForTableType(final int column) {
    return null;
  }

  @Override
  public boolean isColumnFilteredForTableType(final int column) {
    return false;
  }
}
