package tableTree;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TableHeaderCellRenderer;
import tableTreeUtils.TableInformation;
import tableTreeUtils.TablePopupMenuListener;

/**
 * Abstract class for table models. Implement this and override the required
 * methods for concrete table models.
 * 
 * @author ejeahei enaland eheitur
 * 
 */
public abstract class TTTableModel extends AbstractTableModel implements Observer {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  /**
   * Holds the names of the columns. Set these with setColumnNames()
   */
  protected String[] columnNames = null;

  /**
   * Holds the actual data of the table. Set this with setData()
   */
  protected Vector<Object> data = null;

  /**
   * Holds the references to the actual data in a sorted order, but the
   * invisible items are not removed. This vector is used by prepareData() to
   * make the displayData vector
   */
  protected Vector<DisplayDataInfo> preparationData = null;

  /**
   * Holds the references to the items in the actual data that should be
   * displayed. The references are prepared by calling the prepareData() method,
   * which orders them accordingly to the current sort and hides filtered items
   */
  protected Vector<Object> displayData = null;

  /**
   * Should be updated with the current column index when a sort click event
   * occurs.
   */
  protected int currentSortColumnIndex = -1;

  /**
   * Keeps track of the sorting order of the sort column
   */
  public enum availableSortOrders {
    /**
     * No sorting
     */
    NO_SORT,
    /**
     * Ascending sort order
     */
    ASCENDING,
    /**
     * Descending sort order
     */
    DESCENDING
  }

  /**
   * Keeps track of the current sorting order for the column currently being
   * sorted. It is initialized to availableSortOrders.NO_SORT
   */
  protected availableSortOrders currentSortOrder = availableSortOrders.NO_SORT;

  /**
   * Keeps track of table data that should be deleted from the DB if a save is
   * performed
   */
  protected Vector<Object> markedForDeletion = null;

  /**
   * Keeps track of table data that should be cleared from the row in the DB if
   * a save is performed
   */
  protected Vector<Object> markedForClearing = null;

  /**
   * Keeps track of newly added table data that has yet to be saved to the DB
   */
  protected Vector<Object> markedAsNew = null;

  /**
   * This index is used to indicate the column that the user wishes to filter or
   * order
   */
  protected int lastSelectedColumn = -1;

  /**
   * This method is used for updating the lastSelectedColumn value. If a table
   * has two columns with the same header value, this will not work.
   * 
   * Future improvement is to redo the chain of events to use the table header's
   * column indexes and table model's column indexes instead of using the header
   * values to get a hold of the indexes.
   * 
   * @param colName
   */
  public void setLastSelectedColumn(String colName) {
    lastSelectedColumn = this.getTableInfo().getOriginalColumnIndexOfColumnName(colName);
  }

  /**
   * Returns the last selected column index for the table model column indexes
   * 
   * @return the last selected column index
   */
  public int getLastSelectedColumn() {
    return lastSelectedColumn;
  }

  /**
   * Holds the preferred width for each of the columns. Set using
   * setColumnWidths()
   */
  protected int[] columnWidths = null;

  /**
   * Holds the table name. Set this with setTableName()
   */
  protected String tableName;

  /**
   * Holds the rockFactory that is the connection to the database.
   */
  protected RockFactory rockFactory = null;

  /**
   * A vector of all the different table types used in this factory creation
   */
  public Vector<TableInformation> tableInformations = null;

  /**
   * Holds the regular expression used for filtering each of the columns. Set
   * using setColumnFilter()
   */
  protected String[] columnFilters = null;

  /**
   * A boolean used to indicate whether the tree is in read-only mode or not
   */
  private boolean editable;

  /**
   * A variable holding the reference to the object handling the table model's
   * observable functionality
   */
  private ObservableHelper obsHelper;
  
  /** A boolean to check if duplicate rows option should be enabled in pop-up menu. */
  private boolean enableDuplicateRows;

  /**
   * Constructor, setting the RockFactory, the different available table type
   * info references and the boolean telling whether the table is editable or
   * not
   * 
   * @param RF
   * @param tableInfos
   * @param editable
   */
  public TTTableModel(RockFactory RF, Vector<TableInformation> tableInfos, boolean editable) {
    rockFactory = RF;
    tableInformations = tableInfos;
    markedForDeletion = new Vector<Object>();
    markedAsNew = new Vector<Object>();
    markedForClearing = new Vector<Object>();
    this.editable = editable;
    addTableModelListener(new TableModelListener() {

      public void tableChanged(TableModelEvent e) {
        if (obsHelper != null)
          obsHelper.notifyTableTreeComponent();
      }
    });
    enableDuplicateRows = true;
  }

  /**
   * Set the tree instance to be observer of the observable functionality of the
   * table model
   * 
   * @param comp
   */
  public void setTableTreeComponentAsObserver(TableTreeComponent comp) {
    if (obsHelper == null)
      obsHelper = new ObservableHelper(comp);
  }

  /**
   * Return whether the table is editable, based on if the tree is editable.
   * 
   * @return true if the table is editable
   */
  public boolean isTreeEditable() {
    return editable;
  }

  /**
   * Return the value that should be displayed in position (rowIndex,
   * columnIndex). OVERRIDE this. Use this method to access the fields in the
   * corresponding RockDBObject for the row.
   * 
   * @param rowIndex
   * @param columnIndex
   * @return the object at position (rowIndex, columnIndex)
   */
  public abstract Object getValueAt(int rowIndex, int columnIndex);

  /**
   * Return the value that is used to decide the order. OVERRIDE this so that it
   * returns the data at rowIndex, columnIndex from the data Vector.
   * 
   * @param rowIndex
   * @param columnIndex
   * @return the value object
   */
  public abstract Object getOriginalValueAt(int rowIndex, int columnIndex);

  /**
   * Return the value for a specific column of an object. OVERRIDE this so that
   * it returns the data at columnIndex for the data object
   * 
   * @param dataObject
   * @param columnIndex
   * @return the column value object
   */
  public abstract Object getColumnValueAt(Object dataObject, int columnIndex);

  /**
   * Returns the number of columns. No need to override this.
   * 
   * @return number of columns
   */
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * Return the preferred column width for the given column. No need to override
   * this.
   * 
   * @param col
   *          index
   * @return preferredWidth or 0 if "don't care"
   */
  public int getPreferredColumnWidth(int col) {
    if (columnWidths == null || col >= columnWidths.length) {
      return 0;
    } else {
      return columnWidths[col];
    }
  }

  /**
   * Return the column name for the given column. No need to override this.
   * 
   * @param column
   *          index
   * @return the name. Defaults to "A", "B", ... etc
   */
  public String getColumnName(int column) {
    if (columnNames != null) {
      return columnNames[column];
    } else {
      return super.getColumnName(column);
    }
  }

  /**
   * Overridden method for getting the number of rows in the table. Returns the
   * number of visible rows in the table (defined by filters).
   * 
   * No need to override this.
   * 
   * @return number of visible rows
   */
  public int getRowCount() {
    return displayData.size();
  }

  /**
   * Return the name of the table. No need to override this.
   * 
   * @return tableName
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * Returns the table information for this specific table model type, where the
   * type is identified by the table name
   * 
   * @return table information
   */
  public TableInformation getTableInfo() {
    Iterator<TableInformation> iter = tableInformations.iterator();
    TableInformation thisInfo = null;
    while (iter.hasNext()) {
      thisInfo = iter.next();
      if (thisInfo.getType().equals(tableName)) {
        return thisInfo;
      }
    }
    return null;
  }

  /**
   * Return the class for the given column. No need to override this.
   * 
   * @param c
   *          column index
   * @return the class of the column
   */
  public Class<?> getColumnClass(int c) {
    return getOriginalValueAt(0, c).getClass();
  }

  /**
   * Returns true if the table cell at (row, col) is editable. This default
   * implementation allows all cells to be edited in case the tree is editable.
   * OVERRIDE IF this needs to be restricted.
   * 
   * @param row
   * @param col
   * @return true if (row, col) is editable
   */
  public boolean isCellEditable(int row, int col) {
    if (this.isTreeEditable()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Set the value of the table cell (row, col). OVERRIDE this so that the field
   * corresponding to the column of the RockDBObject corresponding to the row.
   * 
   * @param value
   *          of the cell
   * @param row
   * @param col
   */
  public abstract void setValueAt(Object value, int row, int col);

  /**
   * Set the column editors for the columns of the table. OVERRIDE IF there are
   * columns that need custom editors.
   * 
   * @param theTable
   */
  public void setColumnEditors(JTable theTable) {
    // No editors by default
  }

  /**
   * Set the column renderers for the columns of the table. OVERRIDE IF there
   * are columns that need custom renderers.
   * 
   * @param theTable
   */
  public void setColumnRenderers(JTable theTable) {
    // No renderers by default
  }

  /**
   * Set the column header renderers for the table. This allows us to make
   * changes to the individual column header representations
   * 
   * @param theTable
   */
  public void setTableHeaderRenderers(JTable theTable) {
    for (int i = 0; i < getColumnCount(); i++) {
      theTable.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(
          new TableHeaderCellRenderer(getColumnCount()));
    }
  }

  /**
   * It is read by the TableHeaderCellRenderer:s in order to correctly update
   * the header look.
   * 
   * @return sort column index
   */
  public int getCurrentSortColumnIndex() {
    return currentSortColumnIndex;
  }

  /**
   * Method for saving one element in the data array. OVERRIDE this to cast the
   * element to the right type and call the right method for saving this
   * particular data.
   * 
   * @param rockObject
   */
  protected abstract void saveData(Object rockObject) throws SQLException, RockException;

  /**
   * Method for deleting one element in the deltaData array. OVERRIDE this to
   * cast the element to the right type and call the right method for deleting
   * this particular data.
   */
  protected abstract void deleteData(Object rockObject) throws SQLException, RockException;

  /**
   * Method for clearing the contents one element in the deltaData array.
   * OVERRIDE this to cast the element to the right type and call the right
   * method for clearing this particular data.
   */
  protected void clearData(Object elementAt) throws SQLException, RockException {
    // Do nothing.
  }

  /**
   * Sets the order number for the given object. OVERRIDE this if the data has
   * an order.If so, you also have to override {@code dataHasOrder} and {@code
   * getOrderOf}.
   * 
   * @param currentData
   *          the data object for which the order number is set
   * @param newOrderNumber
   *          the new number to be set as the order number for the data
   */
  protected void setOrderOf(Object currentData, int newOrderNumber) {
    // intentionally left blank
  }

  /**
   * Returns the order number for the given object. OVERRIDE this if the data
   * has an order. If so, you also have to override {@code dataHasOrder} and
   * {@code setOrderOf}.
   * 
   * @param currentData
   *          the data object for which the order is fetched
   * @return the order number of the given data
   */
  protected int getOrderOf(Object currentData) {
    return 0;
  }

  /**
   * Tells whether the data in the table has an order or not. OVERRIDE if the
   * data has order. If so, you also have to override {@code setOrderOf} and
   * {@code getOrderOf}.
   * 
   * @return true if the data is ordered
   */
  protected boolean dataHasOrder() {
    return false;
  }

  /**
   * Set the preferred column widths of the table. No need to override this.
   * 
   * @param inColumnWidths
   *          array of column widths
   */
  protected void setColumnWidths(int[] inColumnWidths) {
    columnWidths = inColumnWidths;
  }

  /**
   * Set the RockDBObject data of the table. No need to override this.
   * 
   * @param inData
   *          vector of RockDBObjects
   */
  public void setData(Vector<Object> inData) {
    data = inData;
    preparationData = new Vector<DisplayDataInfo>();
    prepareData();

    // Initialise the column filter array
    columnFilters = new String[this.getColumnCount()];
  }

  /**
   * Returns the RockDBObject data of the table. No need to override this.
   * 
   * @return vector of RockDBObjects
   */
  public Vector<Object> getData() {
    return data;
  }

  /**
   * Set the column names of the table. No need to override this.
   * 
   * @param inNames
   *          array of names for the columns
   */
  public void setColumnNames(String[] inNames) {
    columnNames = inNames;
  }

  /**
   * Set the name of the table. This name will be displayed as the parent name
   * in the tree. No need to override this.
   * 
   * @param newName
   */
  public void setTableName(String newName) {
    tableName = newName;
  }

  /**
   * Create a new data object corresponding to a row in the table. OVERRIDE this
   * to specify how the data object is created.
   * 
   * @return the rock object
   */
  public abstract RockDBObject createNew();

  /**
   * Callback method for adding a row at a specific point in the table.
   * 
   * @param datum
   *          the object to add
   * @param index
   *          the place to add it
   */
  public void insertDataAtRow(Object datum, int index) {
    int orderOfFirstElement = getOrderOf(data.elementAt(0));
    data.insertElementAt(datum, index);

    markedAsNew.add(datum);

    reorderDataChanged(orderOfFirstElement);
    refreshTable();
    this.fireTableDataChanged();
  }

  /**
   * Callback method for adding a row at the end of the table.
   * 
   * @param datum
   *          the object to add
   */
  public void insertDataLast(Object datum) {
    if (data.isEmpty()) {
      data.insertElementAt(datum, data.size());
    } else {
      int orderOfFirstElement = getOrderOf(data.elementAt(0));
      data.insertElementAt(datum, data.size());
      reorderDataChanged(orderOfFirstElement);
    }

    markedAsNew.add(datum);

    refreshTable();
    this.fireTableDataChanged();
  }

  public void setDataAt(Object datum, int index) {
    data.setElementAt(datum, index);
    markedAsNew.add(datum);
    refreshTable();
    this.fireTableDataChanged();
  }

  /**
   * Callback method for duplicating a row at the end of the table.
   * 
   * @param selectedRows
   *          selected rows
   * @param times
   *          number of times to duplicate the rows
   */
  public void duplicateRow(int[] selectedRows, int times) {
    Object sourceData = null;
    for (int i = 0; i < times; i++) {
      for (int j = 0; j < selectedRows.length; j++) {
        sourceData = displayData.elementAt(selectedRows[j]);
        insertDataLast(copyOf(sourceData));
      }
    }

    refreshTable();
    this.fireTableDataChanged();
  }

  /**
   * Callback method for adding multiple rows to the end of the table.
   * 
   * @param times
   *          number of rows to add
   */
  public void addMultipleRows(int times) {
    for (int i = 0; i < times; i++) {
      Object toBeInserted = createNew();
      if (toBeInserted != null) {
        insertDataLast(toBeInserted);
      }
    }
    refreshTable();
    this.fireTableDataChanged();
  }

  /**
   * Clones the table model.
   * 
   * @param toBeCopied
   * @return the copy
   */
  public abstract Object copyOf(Object toBeCopied);

  /**
   * Callback method for clearing selected rows from the table.
   * 
   * @param indeces
   *          the rows to be cleared
   */
  public void clearSelectedData(int[] indeces) {

    for (int i = indeces.length - 1; i >= 0; i--) {

      // Add the row to the list of to be deleted items.
      Object tempObject = displayData.elementAt(indeces[i]);
      try {
        clearData(tempObject);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (RockException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    refreshTable();
    this.fireTableDataChanged();
  }

  /**
   * Callback method for removing selected rows from the table.
   * 
   * @param indeces
   *          the rows to be deleted
   */
  public void removeSelectedData(int[] indeces) {

    // Get the order of the first element before deletion (in case the first on
    // is one of the deleted ones).
    int orderOfFirstElement = getOrderOf(data.elementAt(0));

    for (int i = indeces.length - 1; i >= 0; i--) {

      // Make a dummy set operation before remove causing the affected
      // RockDBObject to be showed as updated. This is needed, since only
      // the deleted tree main nodes (for example: measurement types) are
      // included in the deleted data vector in the TableTreeComponent.
      this.setValueAt(this.getValueAt(indeces[i], 0), indeces[i], 0);

      // Add the row to the list of to be deleted items.
      markedForDeletion.add(displayData.elementAt(indeces[i]));

      // Remove the element from the data vector.
      data.remove(displayData.elementAt(indeces[i]));

      // Refresh the table.
      refreshTable();
    }

    // Reorder the data, so that the order values are updated.
    reorderDataChanged(orderOfFirstElement);

    this.fireTableDataChanged();
  }

  /**
   * Callback method for moving selected rows up in the table.
   * 
   * @param indeces
   *          the indeces of rows to be moved up
   * @return the updated row indeces
   */
  public int[] moveRowsUp(int[] indeces) {
    // NOTE: The int array given as a parameter (as a reference) is updated
    // by this method. Returning the same array is just symbolic to show
    // that it has been changed.

    if (currentSortOrder == availableSortOrders.NO_SORT && !isTableFiltered()) {
      int orderOfFirstElement = getOrderOf(data.elementAt(0));
      int thisElement = 1; // Can't move the first element up
      int indexElement = 0;
      boolean isChanged = false;

      while (thisElement < data.size() && indexElement < indeces.length) {
        if (thisElement == indeces[indexElement]) {
          // Swap the elements and update the index to the moved
          // position.
          swap(thisElement, thisElement - 1);
          indeces[indexElement]--;

          // Move to the next index
          indexElement++;

          // Mark the table as changed
          isChanged = true;

        }
        thisElement++;
      }

      // If at least one row was moved, then reorder the table and
      // refresh the table. Also a dummy change operation is made for
      // all affected rows causing all corresponding RockDBObjects to
      // be showed as updated.
      if (isChanged) {
        reorderDataUnchanged(orderOfFirstElement);
        preparationData.removeAllElements();
        prepareData();

        // Make dummy set operations.
        for (int index : indeces) {
          this.setValueAt(this.getOriginalValueAt(index, 0), index, 0);
          this.setValueAt(this.getOriginalValueAt(index + 1, 0), index + 1, 0);
        }

        this.fireTableDataChanged();
      }
    }
    return indeces;
  }

  /**
   * Callback method for moving selected down up in the table.
   * 
   * @param indeces
   *          the rows to be moved down
   * @return the rows
   */
  public int[] moveRowsDown(int[] indeces) {
    if (currentSortOrder == availableSortOrders.NO_SORT && !isTableFiltered()) {

      // Get the order of the first element
      int orderOfFirstElement = getOrderOf(data.elementAt(0));

      // Set the current element iteration number as the last data
      // element, excluding the last row (as you cannot move that one
      // down).
      int thisElement = data.size() - 2;

      // Set the iteration value for the row indexes.
      int indexElement = indeces.length - 1;

      // Initialize the change flag.
      boolean isChanged = false;

      // Iterate the rows (excluding the last row) and the row indexes in
      // descending order.
      while (thisElement >= 0 && indexElement >= 0) {
        // If the current row matches the current index row, then the
        // data is swapped with the row below.
        if (thisElement == indeces[indexElement]) {

          // Swap the elements and update the index to the moved
          // position.
          swap(thisElement, thisElement + 1);
          indeces[indexElement]++;

          // Move to the next index
          indexElement--;

          // Mark the table as changed
          isChanged = true;
        }
        thisElement--;
      }

      // If at least one row was moved, then reorder the table and refresh
      // the table. Also a dummy change operation is made for all affected
      // rows causing all corresponding RockDBObjects to be showed as
      // updated.
      if (isChanged) {
        reorderDataUnchanged(orderOfFirstElement);
        preparationData.removeAllElements();
        prepareData();

        // Make dummy set operations.
        for (int index : indeces) {
          this.setValueAt(this.getOriginalValueAt(index - 1, 0), index - 1, 0);
          this.setValueAt(this.getOriginalValueAt(index, 0), index, 0);
        }

        this.fireTableDataChanged();
      }
    }

    return indeces;
  }

  /**
   * Helper method for swapping the position of two elements in a table
   * 
   * @param element1
   * @param element2
   */
  private void swap(int element1, int element2) {
    Object tmpObject = data.elementAt(element1);
    data.setElementAt(data.elementAt(element2), element1);
    data.setElementAt(tmpObject, element2);
  }

  /**
   * Method for returning the rock factory of the table.
   * 
   * @return rockFactory
   */
  public RockFactory getRockFactory() {
    return rockFactory;
  }

  /**
   * Save the changed table data in the DB
   */
  public void saveChanges() {
    // Remove deleted rows
    for (int i = 0; i < markedForDeletion.size(); i++) {
      try {
        deleteData(markedForDeletion.elementAt(i));
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (RockException e) {
        e.printStackTrace();
      }
    }
    markedForDeletion.clear();

    // // Remove deleted rows
    // for (int i = 0; i < markedForClearing.size(); i++) {
    // try {
    // clearData(markedForClearing.elementAt(i));
    // } catch (SQLException e) {
    // e.printStackTrace();
    // } catch (RockException e) {
    // e.printStackTrace();
    // }
    // }
    // markedForClearing.clear();

    // saveData will save changes and create new objects
    for (int i = 0; i < data.size(); i++) {
      try {
        saveData(data.elementAt(i));
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (RockException e) {
        e.printStackTrace();
      }
    }

    // The elements in markedAsNew are now saved and can be removed
    if (markedAsNew.size() > 0)
      markedAsNew.clear();
  }

  /**
   * Remove all the data from the database
   */
  public void removeFromDB() {
    for (int i = 0; i < data.size(); i++) {
      try {
        deleteData(data.elementAt(i));
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (RockException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns a popup menu, where the menu alternatives are available, disabled
   * etc depending on the current state of the tree
   * 
   * @param targetComponent
   * @param tableContainer
   * @return the pop-up menu
   */
  public JPopupMenu getPopupMenu(Component targetComponent, TableContainer tableContainer) {
    JPopupMenu menu = new JPopupMenu();

    TablePopupMenuListener listener = new TablePopupMenuListener(tableContainer);

    menu.add(newMenuItem(TablePopupMenuListener.ADD, listener, true, false, true, true, false));
    menu.add(newMenuItem(TablePopupMenuListener.DELETE, listener, true, true, true, true, false));

    menu.add(newMenuItem(TablePopupMenuListener.MOVE_UP, listener, true, true, true, false, true));
    menu.add(newMenuItem(TablePopupMenuListener.MOVE_DOWN, listener, true, true, true, false, true));

    JMenuItem item = newMenuItem(TablePopupMenuListener.CLEAR_FILTER, listener, false, false, false, false, false);
    if (isColumnFiltered(getLastSelectedColumn())) {
      menu.add(newMenuItem(TablePopupMenuListener.MODIFY_FILTER, listener, false, false, false, false, false));
      menu.add(item);
    } else {
      menu.add(newMenuItem(TablePopupMenuListener.ADD_FILTER, listener, false, false, false, false, false));
      item.setEnabled(false);
      menu.add(item);
    }

    menu.addSeparator();
    JMenu submenu = new JMenu("Advanced");

    JMenuItem clear_item = submenu.add(newMenuItem(TablePopupMenuListener.CLEAR_FILTERS_FOR_TABLE, listener, false,
        false, false, false, false));
    if (!isTableFiltered())
      clear_item.setEnabled(false);
    submenu.add(newMenuItem(TablePopupMenuListener.CLEAR_FILTERS_FOR_ALL_TABLES, listener, false, false, false, false,
        false));

    submenu.add(newMenuItem(TablePopupMenuListener.ADD_MULTIPLE_ROWS, listener, true, false, true, true, false));
    JMenuItem duplicateRow = newMenuItem(TablePopupMenuListener.DUPLICATE_ROW, listener, true, true, true, true, false);    
    duplicateRow.setEnabled(enableDuplicateRows);    
    submenu.add(duplicateRow);              
    // 20110519 eanguan Merge IP : Decision made to hide the Reset_columns sub-menu
    //submenu.add(newMenuItem(TablePopupMenuListener.RESET_COLUMNS, listener, false, false, false, false, false));

    menu.add(submenu);

    return menu;
  }

  /**
   * Use this method to create popup menu items. The five booleans are used to
   * decide whether the menu item is disabled or not depending on the tree's and
   * the tables current state
   * 
   * @param actionCommand
   * @param listener
   * @param disableWhenSortOrFilter
   * @param disableIfNoRowsSelected
   * @param disableWhenReadOnly
   * @param checkIfRowAddRemoveAllowed
   * @param disableWhenDataNotOrdered
   * @return
   */
  protected JMenuItem newMenuItem(String actionCommand, TablePopupMenuListener listener,
      boolean disableWhenSortOrFilter, boolean disableIfNoRowsSelected, boolean disableWhenReadOnly,
      boolean checkIfRowAddRemoveAllowed, boolean disableWhenDataNotOrdered) {
    JMenuItem retItem;

    retItem = new JMenuItem(actionCommand);
    // 20110519 eanguan Merge IP : To disable the Add/Remove sub-menu in View mode
    if(!(actionCommand.equals(TablePopupMenuListener.CLEAR_FILTER)
        || actionCommand.equals(TablePopupMenuListener.ADD_FILTER)
        || actionCommand.equals(TablePopupMenuListener.CLEAR_FILTERS_FOR_ALL_TABLES)
        || actionCommand.equals(TablePopupMenuListener.CLEAR_FILTERS_FOR_TABLE)
        || actionCommand.equals(TablePopupMenuListener.MODIFY_FILTER)
        || actionCommand.equals(TablePopupMenuListener.RESET_COLUMNS)
        )){
      if(!this.editable){
          retItem.setEnabled(false);
          retItem.addActionListener(listener);
            return retItem;
        }
    }
    if (disableWhenSortOrFilter && (currentSortOrder != availableSortOrders.NO_SORT || isTableFiltered())) {
      retItem.setEnabled(false);
    }
    if (disableIfNoRowsSelected && listener.getTableContainer().getTable().getSelectedRow() == -1) {
      retItem.setEnabled(false);
    }
    if (disableWhenReadOnly && !isTreeEditable()) {
      retItem.setEnabled(false);
    }

    if (disableWhenDataNotOrdered && !dataHasOrder()) {
      retItem.setEnabled(false);
    }

    if (checkIfRowAddRemoveAllowed) {
      if (actionCommand.equalsIgnoreCase(TablePopupMenuListener.DELETE)) {
        retItem.setEnabled(isTableRemoveAllowed());
      } else if (actionCommand.equalsIgnoreCase(TablePopupMenuListener.DUPLICATE_ROW)) {
        retItem.setEnabled(isTableDuplicateRowAllowed());
      } else if (actionCommand.equalsIgnoreCase(TablePopupMenuListener.ADD)
          || actionCommand.equalsIgnoreCase(TablePopupMenuListener.ADD_MULTIPLE_ROWS)) {
        retItem.setEnabled(isTableAddAllowed());
      } else {
        if (!this.isTableRowAddRemoveAllowed()) {
          retItem.setEnabled(false);
        }
      }
    }

    retItem.addActionListener(listener);
    return retItem;
  }

  /**
   * Help function for reordering the data when the data has changed, e.g. data
   * added.
   * 
   * @param oldStartIndex
   *          The index of the first element before the reorder
   */
  private void reorderDataChanged(int oldStartIndex) {
    if (dataHasOrder()) {
      if (oldStartIndex < 0) {
        reorder(oldStartIndex - 1);
      } else {
        reorder(oldStartIndex);
      }
    }
  }

  /**
   * Help function for reordering the data when the data hasn't changed, e.g.
   * rows moved.
   * 
   * @param oldStartIndex
   */
  private void reorderDataUnchanged(int oldStartIndex) {
    if (dataHasOrder()) {
      reorder(oldStartIndex);
    }
  }

  /**
   * Help function for reordering the data. Assumes that the start index has
   * been calculated using either reorderDataChanged or reorderDataUnchanged.
   * 
   * @param newStartIndex
   */
  private void reorder(int newStartIndex) {
    Object currentData = null;
    int runningIndex = newStartIndex;

    for (int i = 0; i < data.size(); i++) {
      currentData = data.elementAt(i);
      if (getOrderOf(currentData) != runningIndex) {
        setOrderOf(currentData, runningIndex);
      }
      runningIndex++;
    }
  }

  /**
   * This method can be used to refresh a table (resort, refilter, etc). Usually
   * called when table rows have been added or removed while the table is sorted
   * or filtered
   */
  public void refreshTable() {
    // System.out.println("refreshTable()");

    sort(-1);

    // No need to prepare the data as it will be called from the
    // sort-filter-preparedata chain.
    // prepareData();
  }

  /**
   * Sort the table according to the given column and the current active sort
   * order. If the column index is less than zero, the sort order is not updated
   * (stepped). Otherwise, the sort order will be stepped.
   * 
   * The preparation data is updated based on the sorting.
   * 
   * @param column
   *          index of the column to sort by
   */
  public void sort(int column) {
    // System.out.println("sort()");

    // Step the sort order to the next order in sequence
    // NOSORT-ASCENDING-DESCENDING-
    // If the sorted column is less than zero, then the sort order is not
    // stepped.
    if (column < 0) {
      // don't change the sort order
    } else if (currentSortColumnIndex == column) {
      // change the sort order
      if (currentSortOrder == availableSortOrders.NO_SORT) {
        currentSortOrder = availableSortOrders.ASCENDING;
      } else if (currentSortOrder == availableSortOrders.ASCENDING) {
        currentSortOrder = availableSortOrders.DESCENDING;
      } else if (currentSortOrder == availableSortOrders.DESCENDING) {
        currentSortOrder = availableSortOrders.NO_SORT;
      } else {
        // should never get here
      }
    } else if (currentSortColumnIndex != column && currentSortColumnIndex <= getColumnCount()) {
      currentSortOrder = availableSortOrders.ASCENDING;
      currentSortColumnIndex = column;
    } else {
      // should never get here
    }

    // Sort the table based on the current sort order
    if (currentSortOrder == availableSortOrders.NO_SORT) {
      // No sorting: preparation data is not used.
      preparationData.removeAllElements();
      // Reset current sort column index to none (-1):
      currentSortColumnIndex = -1;
    } else if (currentSortOrder == availableSortOrders.ASCENDING) {
      // Ascending sort: preparation data uses real data in ascending
      // order.
      preparationData.removeAllElements();
      preparationData = sortDataAscending(currentSortColumnIndex);
    } else if (currentSortOrder == availableSortOrders.DESCENDING) {
      // Descending sort: preparation data uses real data in descending
      // order.
      preparationData.removeAllElements();
      preparationData = sortDataDescending(currentSortColumnIndex);

      // NOTE: The code below switches the order of the ascending sorted
      // vector. However, this caused an unnecessary switch when table was
      // resorted during descending order. The order remained as
      // descending, but the actual data order was swithed. This has been
      // replaced by using a separate sortDataDescending method.
      //
      // Vector<DisplayDataInfo> tmpData = (Vector<DisplayDataInfo>)
      // preparationData
      // .clone();
      // int size = tmpData.size() - 1;
      // preparationData.removeAllElements();
      // for (int i = size; i >= 0; i--) {
      // preparationData.add(tmpData.elementAt(i));
      // }

    } else {
      preparationData.removeAllElements();
      // should never get here
    }
    filterTableFromSort();
    // prepareData();
  }

  /**
   * Sorts the actual data in ascending order based on the given column. Returns
   * a sorted vector of DisplayDataInfo objects.
   * 
   * @param columnIndex
   *          index of the column to be sorted by
   * @return the result vector
   */
  private Vector<DisplayDataInfo> sortDataAscending(int columnIndex) {
    Vector<DisplayDataInfo> resultVector = new Vector<DisplayDataInfo>();
    Object[] resultVectorData = null;
    Object currentElement = null;
    Object sortValue = null;

    Iterator<Object> iter = data.iterator();
    while (iter.hasNext()) {
      currentElement = iter.next();
      sortValue = getColumnValueAt(currentElement, columnIndex);
      resultVector.add(new DisplayDataInfo(currentElement, true, sortValue));
    }
    resultVectorData = resultVector.toArray();
    Arrays.sort(resultVectorData);

    for (int i = 0; i < resultVectorData.length; i++) {
      resultVector.setElementAt((DisplayDataInfo) resultVectorData[i], i);
    }

    return resultVector;
  }

  /**
   * Sorts the actual data in descending order based on the given column.
   * Returns a sorted vector of DisplayDataInfo objects.
   * 
   * @param columnIndex
   *          index of the column to be sorted by
   * @return the result vector
   */
  private Vector<DisplayDataInfo> sortDataDescending(int columnIndex) {
    Vector<DisplayDataInfo> resultVector = new Vector<DisplayDataInfo>();
    Object[] resultVectorData = null;
    Object currentElement = null;
    Object sortValue = null;

    Iterator<Object> iter = data.iterator();
    while (iter.hasNext()) {
      currentElement = iter.next();
      sortValue = getColumnValueAt(currentElement, columnIndex);
      resultVector.add(new DisplayDataInfo(currentElement, true, sortValue));
    }
    resultVectorData = resultVector.toArray();
    Arrays.sort(resultVectorData);

    for (int i = 0; i < resultVectorData.length; i++) {
      resultVector.setElementAt((DisplayDataInfo) resultVectorData[resultVectorData.length - i - 1], i);
    }

    return resultVector;
  }

  /**
   * Returns the current sort order for the table
   * 
   * @return the sort order
   */
  public availableSortOrders getCurrentSortOrder() {
    return currentSortOrder;
  }

  /**
   * Returns the current active sort column for the table model. Returns -1 in
   * case the sorting is not active.
   * 
   * @return the current sort column
   */
  public int getCurrentSortColumn() {
    return currentSortColumnIndex;
  }

  /**
   * This method is called when the user selects the filter option from the
   * pop-up menu after right clicking a column header.
   * 
   * @param column
   *          the column index
   */
  public void filterColumn(int column) {

    // Get the current filter regex for the column.
    String regex = getColumnFilter(column);

    // Get the input regexp from the user. The current regex is shown as the
    // default value.
    regex = (String) JOptionPane.showInputDialog(null, "Enter the regexp for filtering column: '"
        + getColumnName(column) + "'", "Column Filter", JOptionPane.QUESTION_MESSAGE, null, null, regex);

    // If a string (not null) was returned, use that as the
    // filter. If null is returned, do nothing. If an empty
    // string is returned, clear the filter for this column
    if ((regex != null) && (regex.length() > 0)) {

      // Activate the filtering for this column
      setColumnFilter(column, regex);

      // Filter the table according to active filters
      filterTable();
    } else if (regex == null) {
      // Cancel clicked, do nothing
    } else {
      // input string is empty
      // Remove the filtering (set null filter)
      setColumnFilter(column, null);

      // Filter the table according to active filters
      filterTable();
    }
  }

  /**
   * Calls the filerTable in order to complete update of preparationData for a
   * sort event
   */
  private void filterTableFromSort() {
    // No special actions are currently implemented when the table is
    // filtered after sorting, so filter table is just called.
    filterTable();
  }

  /**
   * Scans through each column in the table row by row checking if the current
   * value in the cell matches the filter for the column and the filter for the
   * table type. If the value does not match, the current row will be marked as
   * hidden.
   */
  public void filterTable() {

    // Check if there are any filters active.
    // In case there are no filters active, the isVisble values will be
    // reset (and no filtering will be executed) and the visual data is
    // prepared (based on the actual data).
    /*
     * if (!isTableFiltered()) { // Debug: System.out .println("filterTable():
     * There are no active filters. Only reset the isVisible to true and prepare
     * the visual data."); // In case the preparation data is not empty, the
     * isVisible values // are reset so that all the objects become visible. if
     * (preparationData.size() <= 0) { Iterator<DisplayDataInfo> iter =
     * preparationData.iterator(); DisplayDataInfo elem = null; while
     * (iter.hasNext()) { elem = iter.next(); elem.isVisible = true; } }
     * 
     * prepareData(); return; }
     */

    // There is a filter set for at least one of the columns in the table,
    // so there is a need to re-filter the table.
    // 
    // Check if the preparation data is empty. The data could be empty if
    // the re-filtering is done after changing the sort order to no-sort. In
    // case the preparation data is empty, a new preparation data is copied
    // from the actual data.
    //
    if (preparationData.size() <= 0) {

      // Debug:
      // System.out
      // .println("filterTable(): Preparation data empty. Copy new from
      // actual data.");

      Iterator<Object> dataIterator = data.iterator();
      while (dataIterator.hasNext()) {
        DisplayDataInfo insertionObject = new DisplayDataInfo(dataIterator.next());
        preparationData.add(insertionObject);
      }
    }

    // Debug:
    // System.out.println("filterTable(): Filtering.");

    // Create an iterator for the preparation data
    Iterator<DisplayDataInfo> iter = preparationData.iterator();
    DisplayDataInfo elem = null;

    // Reset the isVisible values in the preparation data to true
    while (iter.hasNext()) {
      elem = iter.next();
      elem.isVisible = true;
    }

    // Go through each column and set preparation data isVisible value to
    // false when the pattern for the column does not match.

    for (int columnId = 0; columnId < this.getColumnCount(); columnId++) {

      // Get the column filter
      String tableRegex = getColumnFilter(columnId);
      String tableTypeRegex = getColumnFilterForTableType(columnId);

      // Create the pattern for matching the value with the filter
      Pattern pattern = null;
      Matcher matcher = null;

      // If either one of the filters is not null, then go through the
      // column and check for matches.
      if (tableTypeRegex != null || tableRegex != null) {

        // Create a new preparation data iterator
        iter = preparationData.iterator();
        elem = null;

        // Scan through the preparation data and check if the
        // corresponding actual data in the specified column matches the
        // pattern. If there is no match it will be set to hidden.
        while (iter.hasNext()) {
          elem = iter.next();

          // Get the actual data item from the filtered column
          String value = getColumnValueAt(elem.dataRef, columnId).toString();

          // Check for match in the table type level filter. If no
          // match, then the visual data is hidden.
          if (tableTypeRegex != null) {
            pattern = Pattern.compile(tableTypeRegex);
            matcher = pattern.matcher("");

            matcher.reset(value);
            if (!matcher.find()) {
              elem.isVisible = false;
            }
          }

          // Check for match in the table column filter. If no
          // match, then the visual data is hidden.
          if (tableRegex != null) {
            pattern = Pattern.compile(tableRegex);
            matcher = pattern.matcher("");

            matcher.reset(value);
            if (!matcher.find()) {
              elem.isVisible = false;
            }
          }
        }
      }
    }

    // Prepare the data to be shown visually
    prepareData();
  }

  /**
   * Prepare the visual data objects based on the preparation data objects. In
   * case there is no preparation data (no sorting or filtering activated), then
   * the actual data objects are used as the source.
   */
  private void prepareData() {
    // System.out.println("prepareData()");

    // Create a new display data object
    displayData = new Vector<Object>();

    // In case we have preparation data, copy the visible marked objects in
    // the order to the display data. If the preparation data is empty, then
    // all the actual data objects are copied.
    if (!preparationData.isEmpty()) {

      // DEBUG:
      // System.out.println("prepareData(): Non-empty preparation data:  ");

      for (int i = 0; i < preparationData.size(); i++) {
        // If the current preparation data element is visible, then it
        // is copied into the display data.
        if (preparationData.elementAt(i).isVisible) {
          displayData.add(preparationData.elementAt(i).dataRef);
        }
      }
    } else {

      // DEBUG:
      // System.out.println("prepareData(): empty preparation data. "
      // + "Copying from real data.");

      displayData = data;
    }

    // Notify all listeners that table data may have changed.
    // NOTE: If we enable this one, then just sorting or filtering will
    // cause the change notification to fire.
    // fireTableDataChanged();
  }

  /**
   * Gets the filter (regex) for the given table column
   * 
   * @param column
   *          the selected column
   * @return the column filter regex string
   */
  public String getColumnFilter(int column) {
    if (columnNames != null) {
      return (String) columnFilters[column];
    } else {
      return null;
    }
  }

  /**
   * Sets the filter (regex) for the given table column
   * 
   * @param column
   *          the selected column
   * @param filter
   *          the filter regexp for the column
   */
  public void setColumnFilter(int column, String filter) {
    if (columnNames != null) {
      columnFilters[column] = filter;
    }
  }

  /**
   * Checks if filtering is active for at least one column in the table.
   * 
   * @return true if the table is filtered
   */
  public boolean isTableFiltered() {
    for (int i = 0; i < columnFilters.length; i++) {
      if (columnFilters[i] != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Clears the filter for the column and re-filters the table.
   * 
   * @param column
   *          the selected column
   */
  public void clearColumnFilter(int column) {
    // Clear the filter for the column
    columnFilters[column] = null;

    // Re-filter the table with the updated filters.
    filterTable();
  }

  /**
   * Clears all column filters for the table and re-filters it.
   * 
   */
  public void clearFilters() {
    // Clear the filters for all the columns
    for (int i = 0; i < columnFilters.length; i++) {
      columnFilters[i] = null;
    }
    // Re-filter the table with the empty filters.
    filterTable();
  }

  /**
   * Checks if filtering is active for the given column in the table.
   * 
   * @param column
   *          the selected column
   * @return true if the column is filtered
   */
  public boolean isColumnFiltered(int column) {
    // In case the column is less than zero, then return false
    if (column < 0)
      return false;

    if (columnFilters[column] != null) {
      return true;
    } else
      return false;
  }

  /**
   * Check if the column is filtered in table type level. OVERRIDE this.
   * 
   * @param column
   * @return if the column is filtered
   */
  public abstract boolean isColumnFilteredForTableType(int column);

  /**
   * Return the table type level filter for this column. OVERRIDE this.
   * 
   * @param column
   * @return the filter
   */
  public abstract String getColumnFilterForTableType(int column);

  /**
   * This method catches NotifyObservers events from objects that have set this
   * instance as its observer
   * 
   * @param sourceObject
   * @param sourceArgument
   */

  public abstract void update(Observable sourceObject, Object sourceArgument);

  /**
   * Gets if it is allowed to add or remove table rows. OVERRIDE this if it
   * should not be possible to add/remove table rows. If the menu items add and
   * delete should be handled separately, then override the methods
   * isTableAddAllowed and isTableRemoveAllowed.
   * 
   * @return whether the adding and removing of table rows is allowed
   */
  public boolean isTableRowAddRemoveAllowed() {
    // By default the table rows are allowed to be added and removed.
    return true;
  }

  /**
   * Gets if it is allowed to remove table rows. OVERRIDE this if it should be
   * possible to separately handle the delete menu item. Otherwise the default,
   * isTalbeRowAddRemoveAllowed method, is called
   * 
   * @return whether the removing of table rows is allowed
   */
  public boolean isTableRemoveAllowed() {
    return isTableRowAddRemoveAllowed();
  }

  /**
   * Gets if it is allowed to add table rows. OVERRIDE this if it should be
   * possible to separately handle the add menu item. Otherwise the default,
   * isTalbeRowAddRemoveAllowed method, is called
   * 
   * @return whether the adding of table rows is allowed
   */
  public boolean isTableAddAllowed() {
    return isTableRowAddRemoveAllowed();
  }
  
  /**
   * Sets enableDuplicateRows to enable/disable duplicate rows option
   * in pop-up menu.
   * @return True if option is enabled.
   */
  public boolean isEnableDuplicateRows() {
    return enableDuplicateRows;
  }

  
  /**
   * Sets enableDuplicateRows to enable/disable duplicate rows option
   * in pop-up menu.
   * @param enableDuplicateRows True if option should be enabled.
   */
  public void setEnableDuplicateRows(boolean enableDuplicateRows) {
    this.enableDuplicateRows = enableDuplicateRows;
  }

  /**
   * Gets if it is allowed to duplicate table rows. OVERRIDE this if it should
   * be possible to separately handle the add menu item. Otherwise the default,
   * isTalbeRowAddRemoveAllowed method, is called
   * 
   * @return whether the duplicating of table rows is allowed
   */
  public boolean isTableDuplicateRowAllowed() {
    return isTableRowAddRemoveAllowed();
  }

  /**
   * Data used for supporting sorting and filtering of the actual data objects.
   * In addition to a reference to the actual data, there are a sort value and a
   * visibility value stored for each object.
   * 
   */
  public class DisplayDataInfo implements Comparable<Object> {

    /**
     * The reference the the actual data object
     */
    public Object dataRef;

    /**
     * True if the data should be shown in the table
     */
    public boolean isVisible;

    /**
     * Sort value for this object
     */
    public Object sortValue;

    /**
     * Constructor.
     * 
     * @param dataRef
     *          reference to the actual data object
     */
    public DisplayDataInfo(Object dataRef) {
      this.dataRef = dataRef;
      this.isVisible = true;
      this.sortValue = null;
    }

    /**
     * Constructor.
     * 
     * @param dataRef
     * @param isVisible
     * @param sortValue
     */
    public DisplayDataInfo(Object dataRef, boolean isVisible, Object sortValue) {
      this.dataRef = dataRef;
      this.isVisible = isVisible;
      this.sortValue = sortValue;
    }

    /**
     * method for comparing to display data info objects by the sort value.
     * 
     * @param anotherDisplayDataInfo
     * @throws ClassCastException
     * @return a negative integer, zero, or a positive integer as the specified
     *         object is greater than, equal to, or less than this object,
     *         ignoring case considerations.
     */
    public int compareTo(Object anotherDisplayDataInfo) throws ClassCastException {
      if (!(anotherDisplayDataInfo instanceof DisplayDataInfo))
        throw new ClassCastException("A DisplayDataInfo object expected.");
      Object anotherSortValue = ((DisplayDataInfo) anotherDisplayDataInfo).sortValue;

      int returnValue = 0;

      if (sortValue instanceof String) {
        returnValue = (sortValue.toString().compareToIgnoreCase(anotherSortValue.toString()));
      } else if (sortValue instanceof Integer) {
        returnValue = (Integer) sortValue - (Integer) anotherSortValue;
      }

      return returnValue;
    }
  }

  /**
   * A helper class used for the observable implementation
   * 
   * @author enaland
   * 
   */
  private class ObservableHelper extends Observable {

    /**
     * Constructor, sets the in-parameter as the observer of this instance
     * 
     * @param theObserver
     */
    public ObservableHelper(Observer theObserver) {
      addObserver(theObserver);
    }

    /**
     * This method should be called by the table model when the tree should be
     * informed that the table data has changed
     */
    public void notifyTableTreeComponent() {
      setChanged();
      notifyObservers();
    }

  }

  /**
   * Validates the table models data. OVERRIDE this so that all necessary
   * validations are executed for this table model.
   * 
   * @return validation error messages. Empty vector in case validation was
   *         successful.
   */
  public abstract Vector<String> validateData();

}
