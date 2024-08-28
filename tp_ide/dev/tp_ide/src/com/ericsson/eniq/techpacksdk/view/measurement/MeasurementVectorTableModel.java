package com.ericsson.eniq.techpacksdk.view.measurement;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.ComboBoxTableCellEditor;
import tableTree.ComboBoxTableCellRenderer;
import tableTreeUtils.TCTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Measurementvector;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the counter table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class MeasurementVectorTableModel extends TCTableModel implements Observer {

  private static final Logger logger = Logger.getLogger(MeasurementVectorTableModel.class.getName());

  /**
   * The table type/name
   */
  private static final String myTableName = "MeasurementVector";

  private final MeasurementcounterExt measurementcounterExt;

  private String[] vendorReleases;

  private static final int vendorreleaseColumnIdx = 0;

  private static final int vindexColumnIdx = 1;

  private static final int vfromColumnIdx = 2;

  private static final int vtoColumnIdx = 3;

  private static final int measureColumnIdx = 4;
  
  private static final int measurementQuantityColumnIdx = 5;

  /**
   * Column names, used as headings for the columns.
   */
  private static String[] myColumnNames = { "VendorRelease", "Index", "From", "To", "Measure", "Quantity" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static int[] myColumnWidths = { 120, 120, 120, 120, 120, 120 };

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

  /**
   * Constructor. Initializes the column names, widths and table name.
   * 
   * @param tableInformations
   */
public MeasurementVectorTableModel(RockFactory rockFactory, Vector<TableInformation> tableInformations,
      boolean isTreeEditable, MeasurementcounterExt measurementcounterExt, String[] vendorReleases) {
    super(rockFactory, tableInformations, isTreeEditable);
    this.rockFactory = rockFactory;
    this.measurementcounterExt = measurementcounterExt;
    this.vendorReleases = vendorReleases;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Measurementvector measurementvector, final int col) {
    switch (col) {
    case vendorreleaseColumnIdx:
      return Utils.replaceNull(measurementvector.getVendorrelease());
    case vindexColumnIdx:
      return Utils.replaceNull(measurementvector.getVindex());
    case vfromColumnIdx:
      return Utils.replaceNull(measurementvector.getVfrom());
    case vtoColumnIdx:
      return Utils.replaceNull(measurementvector.getVto());
    case measureColumnIdx:
      return Utils.replaceNull(measurementvector.getMeasure());
    case measurementQuantityColumnIdx:
        return Utils.replaceNull(measurementvector.getQuantity());
    default:
      break;
    }
    return null;

  }

  private void setColumnValue(final Measurementvector measurementvector, final int col, final Object value) {
    switch (col) {
    case vendorreleaseColumnIdx:
      measurementvector.setVendorrelease(Utils.replaceNull((String) value));
      break;
    case vindexColumnIdx:
      measurementvector.setVindex(Utils.replaceNull((Long) value));
      break;
    case vfromColumnIdx:
      measurementvector.setVfrom(Utils.replaceNull((String) value));
      break;
    case vtoColumnIdx:
      measurementvector.setVto(Utils.replaceNull((String) value));
      break;
    case measureColumnIdx:
      measurementvector.setMeasure(Utils.replaceNull((String) value));
      break;
    case measurementQuantityColumnIdx:
        measurementvector.setQuantity(Utils.replaceNull((Long) value));
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
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Measurementvector)) {
        final Measurementvector measurementvector = (Measurementvector) displayData.elementAt(row);
        result = getColumnValue(measurementvector, col);
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
    if ((dataObject != null) && (dataObject instanceof Measurementvector)) {
      final Measurementvector measurementvector = (Measurementvector) dataObject;
      result = getColumnValue(measurementvector, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementvector)) {
        final Measurementvector measurementvector = (Measurementvector) data.elementAt(row);
        result = getColumnValue(measurementvector, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementvector)) {
        final Measurementvector measurementvector = (Measurementvector) data.elementAt(row);
        setColumnValue(measurementvector, col, value);
        // fireTableCellUpdated(row, col);
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
    // Set editor for vendor releases
    final TableColumn vendorreleaseColumn = theTable.getColumnModel().getColumn(vendorreleaseColumnIdx);
    final ComboBoxTableCellEditor vendorreleaseColumnComboEditor = new ComboBoxTableCellEditor(vendorReleases);
    vendorreleaseColumn.setCellEditor(vendorreleaseColumnComboEditor);
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set renderer for vendor release
    final TableColumn vendorreleaseColumn = theTable.getColumnModel().getColumn(vendorreleaseColumnIdx);
    final ComboBoxTableCellRenderer vendorreleaseComboRenderer = new ComboBoxTableCellRenderer(vendorReleases, true);
    vendorreleaseColumn.setCellRenderer(vendorreleaseComboRenderer);
  }

  private long getLatestVindex() {
    long latestVindex = -1L;
    if (data.size() > 0) {
      final Measurementvector latestsVector = (Measurementvector) data.get(data.size() - 1);
      if (latestsVector.getVindex() == null) {
        latestVindex = -1;
      } else {
        latestVindex = latestsVector.getVindex();
      }
    }
    return latestVindex;
  }

  /**
   * Overridden method for creating specifically new measurementobjbhsupports.
   */
  @Override
  public RockDBObject createNew() {
    final Measurementvector measurementvector = new Measurementvector(rockFactory);
    measurementvector.setTypeid(measurementcounterExt.getTypeid());
    measurementvector.setDataname(measurementcounterExt.getDataname());
    measurementvector.setVindex(getLatestVindex() + 1);
    measurementvector.setVfrom("");
    measurementvector.setVto("");
    measurementvector.setMeasure("");
	if(measurementvector.getQuantity() == null){
		logger.finest("Quantity was null.Setting it to 0");
		measurementvector.setQuantity((long) 0);
	}
    return measurementvector;
  }

  /**
   * Overridden version of this method for saving specifically
   * measurementobjbhsupports.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {
    final Measurementvector mvector = ((Measurementvector) rockObject);
    try {
      if (!measurementcounterExt.getDataname().equals(mvector.getDataname())) {
        mvector.setDataname(measurementcounterExt.getDataname());
      }
      if (mvector.gimmeModifiedColumns().size() > 0) {
        mvector.saveToDB();
        logger.info("save mvector " + mvector.getDataname() + " of " + mvector.getTypeid());
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }

  }

  /**
   * Overridden version of this method for deleting specifically
   * Measurementobjbhsupports.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    final Measurementvector mvector = ((Measurementvector) rockObject);
    try {
      mvector.deleteDB();
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Measurementvector.
   * 
   * @param currentData
   *          the Measurementvector to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    // nothing TO DO
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Measurementvector.
   * 
   * @param currentData
   *          the Measurementvector whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    final Measurementvector mvector = ((Measurementvector) currentData);
    int pos = 0;
    int counter = 0;
    for (Iterator<Object> it = data.iterator(); it.hasNext();) {
      final Measurementvector item = ((Measurementvector) it.next());
      if (mvector.equals(item)) {
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
    Measurementvector orig = (Measurementvector) toBeCopied;
    Measurementvector copy = (Measurementvector) orig.clone();
    copy.setNewItem(true);
    // copy.setVindex(getLatestVindex() + 1); -- maybe not good idea?
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
    if ((sourceArgument != null) && (((String) sourceArgument).equals(MeasurementTypeParameterModel.TYPE_ID))) {
      final String value = ((MeasurementTypeParameterModel) sourceObject).getMeasurementtypeExt().getMeasurementtype()
          .getTypeid();
      Vector<Object> temp = new Vector<Object>();
      for (int i = 0; i < data.size(); i++) {
        if (data.elementAt(i) instanceof Measurementvector) {
          try {
            Measurementvector original = (Measurementvector) data.elementAt(i);
            Measurementvector copy = (Measurementvector) original.clone();
            copy.setTypeid(value);
            temp.add(copy);
            deleteData(original);
          } catch (Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
      for (int i = 0; i < temp.size(); i++) {
        if (data.elementAt(i) instanceof Measurementvector) {
          try {
            Measurementvector copy = (Measurementvector) temp.elementAt(i);
            insertDataLast(copy);
          } catch (Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
    }
  }

  public boolean isCellEditable(int row, int col) {
    return super.isCellEditable(row, col);
  }

  @Override
  public void startEditing() {
    // Create a copy of the current vector counters, so that the data can be
    // restored if the user cancels the editing.
    copyOfTheData = new Vector<Object>();
    for (Iterator<Object> it = data.iterator(); it.hasNext();) {
      Object obj = it.next();
      if (obj instanceof Measurementvector) {
        Measurementvector original = (Measurementvector) obj;
        Measurementvector copy = (Measurementvector) original.clone();
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
    // Discard the changes in the model by overwriting the current data with the
    // copy of the original data.
    setData(copyOfTheData);

    // Set the vector counter references in the measurement counter extension to
    // keep the models in sync.
    measurementcounterExt.setVectorcounters(this.getData());
  }

  @Override
  public void stopEditing() {
    // Discard the data copy
    copyOfTheData = null;
  }

  @Override
  public JPopupMenu getPopUpMenu(TableComponentListener listener, Component component) {
    JPopupMenu menu = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem(TableComponentListener.ADD_ROW);
    menuItem.addActionListener(listener);
    menu.add(menuItem);

    menuItem = new JMenuItem(TableComponentListener.REMOVE_ROW);
    menuItem.setEnabled(component instanceof JTable);
    menuItem.addActionListener(listener);
    menu.add(menuItem);

    menu.addSeparator();
    JMenu submenu = new JMenu("Advanced");

    menuItem = new JMenuItem(TableComponentListener.ADD_MULTIPLE_ROWS);
    menuItem.addActionListener(listener);
    submenu.add(menuItem);

    menuItem = new JMenuItem(TableComponentListener.DUPLICATE_ROW);
    menuItem.setEnabled(false);
    if (component instanceof JTable) {
      if (((JTable) component).getSelectedRow() > -1) {
        menuItem.setEnabled(true);
      }
    }
    menuItem.addActionListener(listener);
    submenu.add(menuItem);

    menu.add(submenu);

    return menu;
  }

  @Override
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      Measurementvector item = (Measurementvector) iter.next();
      if (Utils.replaceNull(item.getVendorrelease()).trim().equals("")) {
        errorStrings.add(columnNames[vendorreleaseColumnIdx] + " for vector counter is required");
      }
    }
    return errorStrings;
  }

  public TableModel getTableModel() {
	  // Show or hide additional Measurement Quantity column, added for PMRESVECTOR 
	  /**
	   * NEO WP01110 Increase WRAN Counter Capacity in ENIQ 
	   * IP: 263/159 41-FCP 103 8147
	   * Work Item 1.11: ENIQ TP IDE to support pmRes
	   * Add PMRESVECTOR and Quantity column.
	   */
	  if(this.measurementcounterExt.getCountertype().equals("PMRESVECTOR")) {
		  // Show all columns
		  this.setColumnNames(MeasurementVectorTableModel.myColumnNames);
		  this.setColumnWidths(MeasurementVectorTableModel.myColumnWidths);
	  } else {
		  // Hide column
		  String[] myColumnNames2 = { "VendorRelease", "Index", "From", "To", "Measure" };
		  int[] myColumnWidths2 = { 120, 120, 120, 120, 120 };
		  this.setColumnNames(myColumnNames2);
		  this.setColumnWidths(myColumnWidths2);
	  }
    return this;
  }

}
