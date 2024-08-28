package com.ericsson.eniq.techpacksdk.view.aggregation;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the column table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class AggregationRuleTableModel extends TTTableModel {

  private static final Logger logger = Logger.getLogger(AggregationRuleTableModel.class.getName());

  /**
   * The table type/name
   */
  private static final String myTableName = "Rules";

  private final Aggregation aggregation;
  
  private final Application application;
  
  private static final int targettypeColumnIdx = 0;

  private static final int targetlevelColumnIdx = 1;

  private static final int targettableColumnIdx = 2;

  private static final int targetmtableidColumnIdx = 3;

  private static final int sourcetypeColumnIdx = 4;

  private static final int sourcelevelColumnIdx = 5;

  private static final int sourcetableColumnIdx = 6;

  private static final int sourcemtableidColumnIdx = 7;

  private static final int ruletypeColumnIdx = 8;

  private static final int aggrgationscopeColumnIdx = 9;

  private static final int bhtypeColumnIdx = 10;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "TargetType", "TargetLevel", "TargetTable", "TargetTableId",
    "SourceType", "SourceLevel", "SourceTable", "SourceTableId",
    "RuleType", "AggregationScope", "BHType" };
  
  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 120, 120, 120, 120, 120, 120, 120, 120, 120, 120, 120 };

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
   */
  public AggregationRuleTableModel(Application application, RockFactory rockFactory, Vector<TableInformation> tableInfos,
      boolean isTreeEditable, Aggregation aggregation) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.aggregation = aggregation;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Aggregationrule aggregationrule, final int col) {
    if (aggregationrule != null) {
      switch (col) {
      case targettypeColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_type());

      case targetlevelColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_level());

      case targettableColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_table());

      case targetmtableidColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_mtableid());

      case sourcetypeColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_type());

      case sourcelevelColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_level());

      case sourcetableColumnIdx:
        return Utils.replaceNull(aggregationrule.getTarget_table());

      case sourcemtableidColumnIdx:
        return Utils.replaceNull(aggregationrule.getSource_mtableid());

      case ruletypeColumnIdx:
        return Utils.replaceNull(aggregationrule.getRuletype());

      case aggrgationscopeColumnIdx:
        return Utils.replaceNull(aggregationrule.getAggregationscope());
        
      case bhtypeColumnIdx:
        return Utils.replaceNull(aggregationrule.getBhtype());

      default:
        break;
      }
    }
    return null;

  }

  private void setColumnValue(final Aggregationrule aggregationrule, final int col, final Object value) {
  }

  /**
   * Overridden method for getting the value at a certain position in the table.
   * Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getValueAt(final int row, final int col) {
    Object result = null;
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Aggregationrule)) {
        final Aggregationrule aggregationrule = (Aggregationrule) displayData.elementAt(row);
        result = getColumnValue(aggregationrule, col);
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
    if ((dataObject != null) && (dataObject instanceof Aggregationrule)) {
      final Aggregationrule aggregationrule = (Aggregationrule) dataObject;
      result = getColumnValue(aggregationrule, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Aggregationrule)) {
        final Aggregationrule aggregationrule = (Aggregationrule) data.elementAt(row);
        result = getColumnValue(aggregationrule, col);
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

    // Get the rule object in the specified "row" in the display data vector.
    // Get the index for that object in the real data vector. Set the column
    // value for the object in the data vector at the index.
    final int index = data.indexOf((Measurementkey) displayData.elementAt(row));
    final Aggregationrule aggregationrule = (Aggregationrule) data.elementAt(index);
    setColumnValue(aggregationrule, col, value);
    refreshTable();
    fireTableDataChanged();
  }

  /**
   * Overridden method for setting the column editor of the Description column.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
  }

  /**
   * Overridden method for creating specifically new Aggregationrules.
   */
  @Override
  public RockDBObject createNew() {
    final Aggregationrule aggregationrule = new Aggregationrule(rockFactory);
    aggregationrule.setAggregation(aggregation.getAggregation());
    aggregation.setAggregation(aggregation.getAggregation());
    return aggregationrule;
  }

  /**
   * Overridden version of this method for saving specifically Aggregationrules.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {

    final Aggregationrule rule = ((Aggregationrule) rockObject);
    try {
      if (rule.gimmeModifiedColumns().size() > 0) {
        rule.saveToDB();
        logger.info("save key " + rule.getRuleid() + " of " + rule.getAggregation());
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap()
          .getString("save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }

  }

  /**
   * Overridden version of this method for deleting specifically
   * Aggregationrules.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    try {
      ((Aggregationrule) rockObject).deleteDB();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap()
          .getString("delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Aggregationrule.
   * 
   * @param currentData
   *          the Aggregationrule to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    if ((currentData != null) && (currentData instanceof Aggregationrule)) {
      ((Aggregationrule) currentData).setRuleid(Integer.valueOf(newOrderNumber));
    }
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Aggregationrule.
   * 
   * @param currentData
   *          the Aggregationrule whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    int orderNumber = 1;
    if ((currentData != null) && (currentData instanceof Aggregationrule)) {
      orderNumber = Utils.replaceNull(((Aggregationrule) currentData).getRuleid()).intValue();
    }
    return orderNumber;
  }

  /**
   * Overridden version of this method. Indicates that this table has an order
   * column
   * 
   * @return true
   */
  @Override
  protected boolean dataHasOrder() {
    return true;
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    final Aggregationrule orig = (Aggregationrule) toBeCopied;
    final Aggregationrule copy = (Aggregationrule) orig.clone();
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
    if ((sourceArgument != null) && (((String) sourceArgument).equals(AggregationParameterModel.AGGREGATION))) {
      final String value = ((AggregationParameterModel)sourceObject).getAggregation().getAggregation();
      for(int i=0;i<data.size();i++){
          ((Aggregationrule) data.elementAt(i)).setAggregation(value);
      }
    }
  }

//  public boolean isCellEditable(final int row, final int col) {
//    return super.isCellEditable(row, col);
//  }
  
  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    return errorStrings;
  }
  
}
