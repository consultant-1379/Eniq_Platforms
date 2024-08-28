package com.ericsson.eniq.techpacksdk.view.measurement;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TCTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Measurementdeltacalcsupport;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the counter table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class MeasurementDeltaCalcSupportTableModel extends TCTableModel implements Observer  {

  private static final Logger logger = Logger.getLogger(MeasurementDeltaCalcSupportTableModel.class.getName());

  /**
   * The table type/name
   */
  private static final String myTableName = "DeltaCalcSupport";

  private final Measurementtype measurementtype;
  
  private static final int vendorreleaseColumnIdx = 0;

  private static final int deltacalcsupportColumnIdx = 1;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "VendorRelease", "DeltaCalcSupport" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 120, 120 };

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 20;

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

  private Vector<Object> copyOfTheData;

  private Application application;

  /**
   * Constructor. Initializes the column names, widths and table name.
   * @param application 
   */
  public MeasurementDeltaCalcSupportTableModel(Application application, RockFactory rockFactory, Vector<TableInformation> tableInfos, boolean isTreeEditable, Measurementtype measurementtype) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.measurementtype = measurementtype;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Measurementdeltacalcsupport measurementdeltacalcsupport, final int col) {
    switch (col) {
    case vendorreleaseColumnIdx:
      return Utils.replaceNull(measurementdeltacalcsupport.getVendorrelease());

    case deltacalcsupportColumnIdx:
      return Utils.integerToBoolean(measurementdeltacalcsupport.getDeltacalcsupport());

    default:
      break;
    }
    return null;

  }

  private void setColumnValue(final Measurementdeltacalcsupport measurementdeltacalcsupport, final int col, final Object value) {
    switch (col) {
    case vendorreleaseColumnIdx:
      measurementdeltacalcsupport.setVendorrelease(Utils.replaceNull((String) value));
      break;

    case deltacalcsupportColumnIdx:
      measurementdeltacalcsupport.setDeltacalcsupport(Utils.booleanToInteger((Boolean) value));
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
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Measurementdeltacalcsupport)) {
        final Measurementdeltacalcsupport measurementdeltacalcsupport = (Measurementdeltacalcsupport) displayData.elementAt(row);
        result = getColumnValue(measurementdeltacalcsupport, col);
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
    if ((dataObject != null) && (dataObject instanceof Measurementdeltacalcsupport)) {
      final Measurementdeltacalcsupport measurementdeltacalcsupport = (Measurementdeltacalcsupport) dataObject;
      result = getColumnValue(measurementdeltacalcsupport, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementdeltacalcsupport)) {
        final Measurementdeltacalcsupport measurementdeltacalcsupport = (Measurementdeltacalcsupport) data.elementAt(row);
        result = getColumnValue(measurementdeltacalcsupport, col);
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
    if (data.size() >= row) {
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementdeltacalcsupport)) {
        final Measurementdeltacalcsupport measurementdeltacalcsupport = (Measurementdeltacalcsupport) data.elementAt(row);
        setColumnValue(measurementdeltacalcsupport, col, value);
        //fireTableCellUpdated(row, col);
        refreshTable();
        fireTableDataChanged();
      }
    }
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
   * Overridden method for creating specifically new Measurementdeltacalcsupports.
   */
  @Override
  public RockDBObject createNew() {
    final Measurementdeltacalcsupport measurementdeltacalcsupport = new Measurementdeltacalcsupport(rockFactory);
    measurementdeltacalcsupport.setTypeid(measurementtype.getTypeid());
    measurementdeltacalcsupport.setVendorrelease("");
    measurementdeltacalcsupport.setDeltacalcsupport(0);
    measurementdeltacalcsupport.setVersionid(measurementtype.getVersionid());
    return measurementdeltacalcsupport;
  }

  /**
   * Overridden version of this method for saving specifically
   * Measurementdeltacalcsupports.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {
    final Measurementdeltacalcsupport dcsupport = ((Measurementdeltacalcsupport) rockObject);
    try {
      if (dcsupport.gimmeModifiedColumns().size() > 0) {
        if (dcsupport.getDeltacalcsupport().equals(1)) {
          // true (=1) is default, no need to save 
          dcsupport.deleteDB();
        } else {
          if (dcsupport.getTypeid().equals("")) {
            dcsupport.setTypeid(measurementtype.getTypeid());
          }
          dcsupport.saveToDB();
        }
        logger.info("save dcsupport " + dcsupport.getVendorrelease() + " of " + dcsupport.getTypeid());
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap()
          .getString("save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }

  }

  /**
   * Overridden version of this method for deleting specifically
   * Measurementdeltacalcsupports.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    final Measurementdeltacalcsupport dcsupport = ((Measurementdeltacalcsupport) rockObject);
    try {
      dcsupport.deleteDB();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap()
          .getString("delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Measurementdeltacalcsupport. 
   * 
   * @param currentData
   *          the Measurementdeltacalcsupport to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    // nothing TO DO
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Measurementdeltacalcsupport. 
   * 
   * @param currentData
   *          the Measurementdeltacalcsupport whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    final Measurementdeltacalcsupport dcsupport = ((Measurementdeltacalcsupport) currentData);
    int pos = 0;
    int counter = 0;
    for (Iterator<Object> it = data.iterator(); it.hasNext(); ) {
      final Measurementdeltacalcsupport item = ((Measurementdeltacalcsupport) it.next());
      if (dcsupport.equals(item)) {
        pos = counter; 
      }
      counter++;
    }
    return pos;
  }

  /**
   * Overridden version of this method. Indicates that this table has an order
   * column
   * 
   * @return false
   */
  @Override
  protected boolean dataHasOrder() {
    return true;
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    Measurementdeltacalcsupport orig = (Measurementdeltacalcsupport) toBeCopied;
    Measurementdeltacalcsupport copy = (Measurementdeltacalcsupport) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  @Override
  public String getColumnFilterForTableType(final int column) {
    return "";
  }

  @Override
  public boolean isColumnFilteredForTableType(final int column) {
    return false;
  }

  @Override
  public void update(final Observable sourceObject, final Object sourceArgument) {
    if ((sourceArgument != null) && (((String)sourceArgument).equals(MeasurementTypeParameterModel.TYPE_ID))) {
      final String value = ((MeasurementTypeParameterModel) sourceObject).getMeasurementtypeExt().getMeasurementtype().getTypeid();
      Vector<Object> copies = new Vector<Object>(); 
      for(int i = 0; i < data.size(); i++){
        if (data.elementAt(i) instanceof Measurementdeltacalcsupport) {
          try {
            Measurementdeltacalcsupport orig = (Measurementdeltacalcsupport) data.elementAt(i);  
            Measurementdeltacalcsupport copy = (Measurementdeltacalcsupport) orig.clone();
            copy.setTypeid(value);
            copies.add(copy);
          } catch (Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
      for(int i = data.size(); data.size() > 0; i--){
        if (data.elementAt(i - 1) instanceof Measurementdeltacalcsupport) {
          Measurementdeltacalcsupport orig = (Measurementdeltacalcsupport) data.elementAt(i - 1);  
          markedForDeletion.add(orig);
          data.remove(orig);
        }
      }
      for (int i = 0; i < copies.size(); i++) {
        if (copies.elementAt(i) instanceof Measurementdeltacalcsupport) {
          try {
            Measurementdeltacalcsupport copy = (Measurementdeltacalcsupport) copies.elementAt(i);
            insertDataLast(copy);
          } catch (Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
      refreshTable();
    }
  }

  public boolean isCellEditable(int row, int col) {
    return super.isCellEditable(row, col);
  }
  
  @Override
  public void setData(Vector<Object> inData) {
    super.setData(inData);
  }
  
  @Override
  public void startEditing() {
    copyOfTheData = new Vector<Object>();
    for (Iterator<Object> it = data.iterator(); it.hasNext(); ) {
      Object obj = it.next();
      if (obj instanceof Measurementdeltacalcsupport) {
        Measurementdeltacalcsupport original = (Measurementdeltacalcsupport) obj;
        Measurementdeltacalcsupport copy = (Measurementdeltacalcsupport) original.clone();
        copyOfTheData.add(copy);
      }
    }
  }

  @Override
  public Vector<Object> getData() {
    return data;
  }

  @Override
  public void cancelEditing() {
    setData(copyOfTheData);
  }

  @Override
  public void stopEditing() {
    copyOfTheData = null;
  }

  @Override
  public JPopupMenu getPopUpMenu(final TableComponentListener listener, Component component) {
    // popup menu not needed
    return null;
  }

  public TableModel getTableModel() {
    return this;
  }

  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      Measurementdeltacalcsupport item = (Measurementdeltacalcsupport) iter.next();
      if (Utils.replaceNull(item.getVendorrelease()).trim().equals("")) {
        errorStrings.add(columnNames[vendorreleaseColumnIdx] + " for objectbhsupport is required");
      }
    }
    return errorStrings;
  }

}
