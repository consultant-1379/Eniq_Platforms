package com.ericsson.eniq.techpacksdk.view.prompts;

import java.sql.SQLException;
import java.util.Iterator;
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
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.distocraft.dc5000.repository.dwhrep.Promptoption;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for prompt options table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class PromptImplementorPromptOptionTableModel extends TTTableModel {

  private static final Logger logger = Logger.getLogger(PromptImplementorPromptOptionTableModel.class.getName());

  /**
   * The table type/name
   */
  public static final String myTableName = "PromptOptions";

  private final Promptimplementor promptimplementor;

  private final Application application;

  private static final int optionnameColumnIdx = 0;

  private static final int optionvalueColumnIdx = 1;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Name", "Value" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 180, 180 };

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 25;

  /**
   * Static method that returns the table type and its corresponding column
   * names
   * 
   * @return
   */
  public static TableInformation createTableTypeInfo() {
    return new TableInformation(myTableName, myColumnNames, myColumnWidths, maxRowsShown);
  }

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  /**
   * Constructor. Initializes the column names, widths and table name.
   * 
   * @param promptimplementor
   */
  public PromptImplementorPromptOptionTableModel(Application application, RockFactory rockFactory,
      Vector<TableInformation> tableInfos, boolean isTreeEditable, Promptimplementor promptimplementor) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.promptimplementor = promptimplementor;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Promptoption promptOption, final int col) {
    if (promptOption != null) {
      switch (col) {
      case optionnameColumnIdx:
        return Utils.replaceNull(promptOption.getOptionname());

      case optionvalueColumnIdx:
        return Utils.replaceNull(promptOption.getOptionvalue());

      default:
        break;
      }
    }
    return null;

  }

  private void setColumnValue(final Promptoption promptOption, final int col, final Object value) {
    switch (col) {
    case optionnameColumnIdx:
      promptOption.setOptionname((String) value);
      break;

    case optionvalueColumnIdx:
      promptOption.setOptionvalue((String) value);
      break;

    default:
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
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Promptoption)) {
        final Promptoption promptOption = (Promptoption) displayData.elementAt(row);
        result = getColumnValue(promptOption, col);
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
    if ((dataObject != null) && (dataObject instanceof Promptoption)) {
      final Promptoption promptOption = (Promptoption) dataObject;
      result = getColumnValue(promptOption, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Promptoption)) {
        final Promptoption promptOption = (Promptoption) data.elementAt(row);
        result = getColumnValue(promptOption, col);
      }
    }
    return result;
  }

  /**
   * Overridden method for setting the value at a certain position in the table
   * Sets it in the corresponding RockDBObject.
   */
  @Override
  public void setValueAt(final Object value, final int row, final int col) {

    int index = data.indexOf((Promptoption) displayData.elementAt(row));
    Promptoption promptOption = (Promptoption) data.elementAt(index);
    setColumnValue(promptOption, col, value);
    // fireTableCellUpdated(row, col);
    refreshTable();
    fireTableDataChanged();
  }

  /**
   * Overridden method for setting the column editor of the columns.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {

    // Set editor for name
    final TableColumn optionnameColumn = theTable.getColumnModel().getColumn(optionnameColumnIdx);
    final LimitedSizeTextTableCellEditor limitedTextEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[optionnameColumnIdx], Promptoption.getOptionnameColumnSize(), true);
    optionnameColumn.setCellEditor(limitedTextEditor);

  }

  /**
   * Overridden method for setting the column renderers.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {

    // Set renderer for name
    final TableColumn optionnameColumn = theTable.getColumnModel().getColumn(optionnameColumnIdx);
    final LimitedSizeTextTableCellRenderer promptnameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[optionnameColumnIdx], Promptoption.getOptionnameColumnSize(), true);
    optionnameColumn.setCellRenderer(promptnameComboRenderer);

  }

  /**
   * Overridden method for creating specifically new Prompt option.
   */
  @Override
  public RockDBObject createNew() {
    final Promptoption promptoption = new Promptoption(rockFactory);
    promptoption.setVersionid(promptimplementor.getVersionid());
    promptoption.setPromptimplementorid(promptimplementor.getPromptimplementorid());
    promptoption.setOptionname("");
    promptoption.setOptionvalue("");
    return promptoption;
  }

  /**
   * Overridden version of this method for saving specifically Prompts.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {

    final Promptoption column = ((Promptoption) rockObject);
    try {
      if (column.gimmeModifiedColumns().size() > 0) {
        column.saveToDB();
        logger.info("save key " + column.getOptionname() + " of " + column.getPromptimplementorid());
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }

  }

  /**
   * Overridden version of this method for deleting specifically Prompts.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    try {
      ((Promptoption) rockObject).deleteDB();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method. Indicates that this table has an order
   * column
   * 
   * @return true
   */
  @Override
  protected boolean dataHasOrder() {
    return false;
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    final Promptoption orig = (Promptoption) toBeCopied;
    final Promptoption copy = (Promptoption) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  @Override
  public String getColumnFilterForTableType(final int column) {
    return getTableInfo().getTableTypeColumnFilter(column);
  }

  @Override
  public boolean isColumnFilteredForTableType(final int column) {
    if (column < 0) {
      return false;
    }
    final String filter = getTableInfo().getTableTypeColumnFilter(column);
    return (filter != null && filter != "");
  }

  @Override
  public void update(final Observable sourceObject, final Object sourceArgument) {
    // nothing TO DO
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      final Object obj = iter.next();
      if (obj instanceof Promptoption) {
        final Promptoption promptoption = (Promptoption) obj;
        if (Utils.replaceNull(promptoption.getOptionname()).trim().equals("")) {
          errorStrings.add(this.promptimplementor.getPromptclassname() + ": " + myColumnNames[optionnameColumnIdx]
              + " for promptoption is required");
        }
      }
    }
    return errorStrings;
  }

}
