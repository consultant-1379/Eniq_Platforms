package com.ericsson.eniq.techpacksdk.view.measurement;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTreeUtils.TCTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the counter table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class MeasurementObjBHTableModel extends TCTableModel implements Observer {

  private static final Logger logger = Logger.getLogger(MeasurementObjBHTableModel.class.getName());

  /**
   * The table type/name
   */
  private static final String myTableName = "ObjectBHSupport";

  private final Measurementtype measurementtype;

  private static final int objbhsupportColumnIdx = 0;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "ObjectBHSupport" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 120 };

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
   * 
   * @param application
   */
  public MeasurementObjBHTableModel(Application application, RockFactory rockFactory,
      Vector<TableInformation> tableInfos, boolean isTreeEditable, Measurementtype measurementtype) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.measurementtype = measurementtype;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Measurementobjbhsupport measurementobjbhsupport, final int col) {
    switch (col) {
    case objbhsupportColumnIdx:
      return Utils.replaceNull(measurementobjbhsupport.getObjbhsupport());

    default:
      break;
    }
    return null;

  }

  private void setColumnValue(final Measurementobjbhsupport measurementobjbhsupport, final int col, final Object value) {
    switch (col) {
    case objbhsupportColumnIdx:
      measurementobjbhsupport.setObjbhsupport(Utils.replaceNull((String) value));
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
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Measurementobjbhsupport)) {
        final Measurementobjbhsupport measurementobjbhsupport = (Measurementobjbhsupport) displayData.elementAt(row);
        result = getColumnValue(measurementobjbhsupport, col);
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
    if ((dataObject != null) && (dataObject instanceof Measurementobjbhsupport)) {
      final Measurementobjbhsupport measurementobjbhsupport = (Measurementobjbhsupport) dataObject;
      result = getColumnValue(measurementobjbhsupport, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementobjbhsupport)) {
        final Measurementobjbhsupport measurementobjbhsupport = (Measurementobjbhsupport) data.elementAt(row);
        result = getColumnValue(measurementobjbhsupport, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementobjbhsupport)) {
        final Measurementobjbhsupport measurementobjbhsupport = (Measurementobjbhsupport) data.elementAt(row);
        setColumnValue(measurementobjbhsupport, col, value);
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
    // Set editor for the ObjectBHSupport
    final TableColumn objbhColumn = theTable.getColumnModel().getColumn(objbhsupportColumnIdx);
    // LimitedSizeTextTableCellEditor limitedTextEditor = new
    // LimitedSizeTextTableCellEditor(myColumnWidths[objbhsupportColumnIdx],
    // Measurementobjbhsupport.getObjbhsupportColumnSize(), true);
    // objbhColumn.setCellEditor(limitedTextEditor);

    JComboBox objbhComboBox = new JComboBox(getObjectBHSupportNames());
    objbhComboBox.setEditable(true);
    DefaultCellEditor extColumnEditor = new DefaultCellEditor(objbhComboBox);
    objbhColumn.setCellEditor(extColumnEditor);

  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set renderer for name
    final TableColumn nameColumn = theTable.getColumnModel().getColumn(objbhsupportColumnIdx);
    final LimitedSizeTextTableCellRenderer nameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[objbhsupportColumnIdx], Measurementobjbhsupport.getObjbhsupportColumnSize(), true);
    nameColumn.setCellRenderer(nameComboRenderer);
  }

  /**
   * Overridden method for creating specifically new measurementobjbhsupports.
   */
  @Override
  public RockDBObject createNew() {
    final Measurementobjbhsupport measurementobjbhsupport = new Measurementobjbhsupport(rockFactory);
    measurementobjbhsupport.setTypeid(measurementtype.getTypeid());
    measurementobjbhsupport.setObjbhsupport("");
    return measurementobjbhsupport;
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
    final Measurementobjbhsupport objbhsupport = ((Measurementobjbhsupport) rockObject);
    try {
      if (objbhsupport.gimmeModifiedColumns().size() > 0) {
        objbhsupport.saveToDB();
        logger.info("save objbhsupport " + objbhsupport.getObjbhsupport() + " of " + objbhsupport.getTypeid());
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
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
    final Measurementobjbhsupport objbhsupport = ((Measurementobjbhsupport) rockObject);
    try {
      // Delete the busy hour mappings for this measurement object bh support (if exists).
      final Busyhourmapping bhmCond = new Busyhourmapping(rockFactory);
      bhmCond.setBhobject(objbhsupport.getObjbhsupport());
      bhmCond.setTypeid(objbhsupport.getTypeid());
      final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(rockFactory, bhmCond, true);
      for (Busyhourmapping bhm : bhmF.get()) {
        bhm.deleteDB();
      }
      
      objbhsupport.deleteDB();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Measurementobjbhsupport.
   * 
   * @param currentData
   *          the Measurementobjbhsupport to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    // nothing TO DO
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Measurementobjbhsupport.
   * 
   * @param currentData
   *          the Measurementobjbhsupport whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    final Measurementobjbhsupport objsupport = ((Measurementobjbhsupport) currentData);
    int pos = 0;
    int counter = 0;
    for (Iterator<Object> it = data.iterator(); it.hasNext();) {
      final Measurementobjbhsupport item = ((Measurementobjbhsupport) it.next());
      if (objsupport.equals(item)) {
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
    Measurementobjbhsupport orig = (Measurementobjbhsupport) toBeCopied;
    Measurementobjbhsupport copy = (Measurementobjbhsupport) orig.clone();
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
    if ((sourceArgument != null) && (((String) sourceArgument).equals(MeasurementTypeParameterModel.TYPE_ID))) {
      final String value = ((MeasurementTypeParameterModel) sourceObject).getMeasurementtypeExt().getMeasurementtype()
          .getTypeid();
      Vector<Object> temp = new Vector<Object>();
      for (int i = 0; i < data.size(); i++) {
        if (data.elementAt(i) instanceof Measurementobjbhsupport) {
          try {
            Measurementobjbhsupport original = (Measurementobjbhsupport) data.elementAt(i);
            Measurementobjbhsupport copy = (Measurementobjbhsupport) original.clone();
            copy.setTypeid(value);
            temp.add(copy);
          } catch (Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
      for (int i = data.size(); data.size() > 0; i--) {
        if (data.elementAt(i - 1) instanceof Measurementobjbhsupport) {
          Measurementobjbhsupport orig = (Measurementobjbhsupport) data.elementAt(i - 1);
          markedForDeletion.add(orig);
          data.remove(orig);
        }
      }
      for (int i = 0; i < temp.size(); i++) {
        if (temp.elementAt(i) instanceof Measurementobjbhsupport) {
          try {
            Measurementobjbhsupport copy = (Measurementobjbhsupport) temp.elementAt(i);
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
    copyOfTheData = new Vector<Object>();
    for (Iterator<Object> it = data.iterator(); it.hasNext();) {
      Object obj = it.next();
      if (obj instanceof Measurementobjbhsupport) {
        Measurementobjbhsupport original = (Measurementobjbhsupport) obj;
        Measurementobjbhsupport copy = (Measurementobjbhsupport) original.clone();
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

  public JPopupMenu getPopUpMenu(final TableComponentListener listener, Component component) {

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

  public TableModel getTableModel() {
    return this;
  }

  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      Measurementobjbhsupport item = (Measurementobjbhsupport) iter.next();
      if (Utils.replaceNull(item.getObjbhsupport()).trim().equals("")) {
        errorStrings.add(columnNames[objbhsupportColumnIdx] + " for objectbhsupport is required");
      }
    }
    return errorStrings;
  }

  /**
   * Gets all the Object BH Support values from all rank tables in all
   * techpacks.
   * 
   * @return A sorted string array of the results. Null in case a DB error
   *         occurs.
   */
  public String[] getObjectBHSupportNames() {
    String[] result = null;
    // TODO: Should improve and only get this list once when loading, as called many times.
    Vector<String> resultVector = new Vector<String>();
    try {
      // Get all the Measurement Object BH Supports from the database. With the
      // typeId, check from the measurement types, which have Ranking Table set.
      // Add the matching ones to the results.
      Measurementobjbhsupport measObjBHSupp = new Measurementobjbhsupport(rockFactory);
      MeasurementobjbhsupportFactory mF = new MeasurementobjbhsupportFactory(rockFactory, measObjBHSupp, true);
      // Get Measurement types from the database.
      // IDE improvement #1041 Speed up viewing a TechPack by reducing sql over network, eeoidiv, 20090804
      // To help performance, want to reduce network traffic by getting as much the data at once as possible.
      // In Measurementtype table, only want where RANKINGTABLE=1.
      Measurementtype measType = new Measurementtype(rockFactory);
      measType.setRankingtable(1);
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, measType, true);

      Iterator<Measurementobjbhsupport> queryResults = mF.get().iterator();
      while (queryResults.hasNext()) {
        Measurementobjbhsupport queryResult = queryResults.next();

        // Get the Object BH Support name
        String objBHSupportName = queryResult.getObjbhsupport();
        Iterator<Measurementtype> mTableResults = mtF.get().iterator();
        while (mTableResults.hasNext()) {
          Measurementtype mType = mTableResults.next();
          if (mType.getTypeid().equals(queryResult.getTypeid())) {
            // If the name is not already in the list, then it is added to the list.
            if (!resultVector.contains(objBHSupportName)) {
              resultVector.add(objBHSupportName);
            }
          }
        }
      }
    } catch (SQLException e) {
      logger.warning("Unable to retrieve objectbhsupports.\n" + e);
    } catch (RockException e) {
      logger.warning("Unable to retrieve objectbhsupports.\n" + e);
    }
    // Store the results as a sorted string array.
    result = new String[resultVector.size()];
    result = resultVector.toArray(result);
    Arrays.sort(result);
    
    return result;
  }

}
