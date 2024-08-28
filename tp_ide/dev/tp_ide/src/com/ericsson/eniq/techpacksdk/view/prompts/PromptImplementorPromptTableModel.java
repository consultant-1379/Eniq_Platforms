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
import tableTree.ComboBoxTableCellEditor;
import tableTree.ComboBoxTableCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Prompt;
import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for prompt table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class PromptImplementorPromptTableModel extends TTTableModel {

  private static final Logger logger = Logger.getLogger(PromptImplementorPromptTableModel.class.getName());

  protected final static int defaultWidth = 320;

  /**
   * The table type/name
   */
  public static final String myTableName = "Prompts";

  private final Promptimplementor promptimplementor;

  private final Application application;

  private static final int promptnameColumnIdx = 0;

  private static final int ordernumberColumnIdx = 1;

  private static final int unrefereshableColumnIdx = 2;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Name", "Order", "Unrefreshable" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 160, 100, 100 };

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
  public PromptImplementorPromptTableModel(Application application, RockFactory rockFactory,
      Vector<TableInformation> tableInfos, boolean isTreeEditable, Promptimplementor promptimplementor) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.promptimplementor = promptimplementor;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Prompt prompt, final int col) {
    if (prompt != null) {
      switch (col) {
      case promptnameColumnIdx:
        return Utils.replaceNull(prompt.getPromptname());

      case ordernumberColumnIdx:
        return Utils.replaceNull(prompt.getOrdernumber());

      case unrefereshableColumnIdx:
        return Utils.replaceNull(prompt.getUnrefreshable());

      default:
        break;
      }
    }
    return null;

  }

  private void setColumnValue(final Prompt prompt, final int col, final Object value) {
    switch (col) {
    case promptnameColumnIdx:
      prompt.setPromptname((String) value);
      break;

    case ordernumberColumnIdx:
      prompt.setOrdernumber((Integer) value);
      break;

    case unrefereshableColumnIdx:
      prompt.setUnrefreshable((String) value);
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
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Prompt)) {
        final Prompt Prompt = (Prompt) displayData.elementAt(row);
        result = getColumnValue(Prompt, col);
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
    if ((dataObject != null) && (dataObject instanceof Prompt)) {
      final Prompt Prompt = (Prompt) dataObject;
      result = getColumnValue(Prompt, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Prompt)) {
        final Prompt Prompt = (Prompt) data.elementAt(row);
        result = getColumnValue(Prompt, col);
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
    int index = data.indexOf((Prompt) displayData.elementAt(row));
    final Prompt Prompt = (Prompt) data.elementAt(index);
    setColumnValue(Prompt, col, value);
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
    final TableColumn promptnameColumn = theTable.getColumnModel().getColumn(promptnameColumnIdx);
    final LimitedSizeTextTableCellEditor limitedTextEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[promptnameColumnIdx], Prompt.getPromptnameColumnSize(), true);
    promptnameColumn.setCellEditor(limitedTextEditor);

    // Set editor for order
    final TableColumn ordernumberColumn = theTable.getColumnModel().getColumn(ordernumberColumnIdx);
    final ComboBoxTableCellEditor ordernumberColumnComboEditor = new ComboBoxTableCellEditor(Constants.PROMPTORDER);
    ordernumberColumn.setCellEditor(ordernumberColumnComboEditor);

    final TableColumn unrefreshableColumn = theTable.getColumnModel().getColumn(unrefereshableColumnIdx);
    final ComboBoxTableCellEditor unrefreshableColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.PROMPTUNREFRESHABLE);
    unrefreshableColumn.setCellEditor(unrefreshableColumnComboEditor);
  }

  /**
   * Overridden method for setting the column renderer.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set renderer for name
    final TableColumn promptnameColumn = theTable.getColumnModel().getColumn(promptnameColumnIdx);
    final LimitedSizeTextTableCellRenderer promptnameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[promptnameColumnIdx], Prompt.getPromptnameColumnSize(), true);
    promptnameColumn.setCellRenderer(promptnameComboRenderer);

    // Set renderer for order number
    final TableColumn ordernumberColumn = theTable.getColumnModel().getColumn(ordernumberColumnIdx);
    final ComboBoxTableCellRenderer ordernumberComboRenderer = new ComboBoxTableCellRenderer(Constants.PROMPTORDER);
    ordernumberColumn.setCellRenderer(ordernumberComboRenderer);

    final TableColumn unrefreshableColumn = theTable.getColumnModel().getColumn(unrefereshableColumnIdx);
    final ComboBoxTableCellRenderer unrefreshableColumnComboRenderer = new ComboBoxTableCellRenderer(
        Constants.PROMPTUNREFRESHABLE);
    unrefreshableColumn.setCellRenderer(unrefreshableColumnComboRenderer);
  }

  /**
   * Overridden method for creating specifically new Prompts.
   */
  @Override
  public RockDBObject createNew() {
    final Prompt prompt = new Prompt(rockFactory);
    prompt.setVersionid(promptimplementor.getVersionid());
    prompt.setPromptimplementorid(promptimplementor.getPromptimplementorid());
    prompt.setPromptname("");
    prompt.setOrdernumber(30);
    prompt.setUnrefreshable("");
    return prompt;
  }

  /**
   * Overridden version of this method for saving specifically Prompts.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {

    final Prompt column = ((Prompt) rockObject);
    try {
      if (column.gimmeModifiedColumns().size() > 0) {
        column.saveToDB();

        logger.info("save prompt " + column.getPromptname() + " of " + column.getPromptimplementorid());
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
      ((Prompt) rockObject).deleteDB();
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
   * @return false
   */
  @Override
  protected boolean dataHasOrder() {
    return false;
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    final Prompt orig = (Prompt) toBeCopied;
    final Prompt copy = (Prompt) orig.clone();
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
    // nothing to do
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      final Object obj = iter.next();
      if (obj instanceof Prompt) {
        final Prompt prompt = (Prompt) obj;
        if (Utils.replaceNull(prompt.getPromptname()).trim().equals("")) {
          errorStrings.add(this.promptimplementor.getPromptclassname() + ": " + myColumnNames[promptnameColumnIdx]
              + " for prompt is required");
        }
      }
    }
    return errorStrings;
  }

}
