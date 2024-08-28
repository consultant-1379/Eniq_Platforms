package tableTreeUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;

import tableTree.TTTableModel;
import tableTree.TableTreeComponent;

/**
 * Listener class for the popup menu for tables.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class TablePopupMenuListener implements ActionListener {

  /*
   * Constants for the menu alternatives
   */
  /**
     * 
     */
  public static final String ADD = "Add Row";

  /**
     * 
     */
  public static final String DELETE = "Delete Row";

  /**
     * 
     */
  public static final String DELETE_NO_CONFIRM = "Delete Row (no confirm)";

  /**
     * 
     */
  public static final String MOVE_UP = "Move Up";

  /**
     * 
     */
  public static final String MOVE_DOWN = "Move Down";

  /**
     * 
     */
  public static final String ORDER = "Order";

  /**
     * 
     */
  public static final String ADD_FILTER = "Filter";

  /**
     * 
     */
  public static final String MODIFY_FILTER = "Modify Filter";

  /**
     * 
     */
  public static final String CLEAR_FILTER = "Clear Filter";

  /**
     * 
     */
  public static final String CLEAR_FILTERS_FOR_TABLE = "Clear Filters for Table";

  /**
     * 
     */
  public static final String CLEAR_FILTERS_FOR_ALL_TABLES = "Clear Filters for All Tables";

  /**
     * 
     */
  public static final String ADD_MULTIPLE_ROWS = "Add Multiple Rows";

  /**
     * 
     */
  public static final String DUPLICATE_ROW = "Duplicate Row";

  /**
     * 
     */
  public static final String RESET_COLUMNS = "Reset Columns for this Table Type";

  public static final String CLEAR_ROW = "Clear Row";

  /**
   * The table container that the listener refers to.
   */
  protected TableContainer thisCont = null;

  /**
   * The table of the table container that the listener refers to.
   */
  private JTable thisTable = null;

  /**
   * The header of the table of the container that the listener refers to.
   */
  JTableHeader tableHeader = null;

  /**
   * The model of the table of the container that the listener refers to.
   */
  private TTTableModel thisModel = null;

  /**
   * Constructor. Initializes the member variables based on the given table
   * container.
   * 
   * @param thisTableContainer
   */
  public TablePopupMenuListener(TableContainer thisTableContainer) {
    thisCont = thisTableContainer;
    thisTable = thisCont.getTable();
    tableHeader = thisTable.getTableHeader();
    thisModel = (TTTableModel) thisTable.getModel();
  }

  /**
   * Returns this TableContainer instance
   * 
   * @return the table container
   */
  public TableContainer getTableContainer() {
    return thisCont;
  }

  /**
   * Callback method for action events, i.e. for when one of the menu
   * alternatives have been selected.
   * 
   * @param ae
   *          the action event
   */
  public void actionPerformed(ActionEvent ae) {

    if (ae.getActionCommand().equals(ADD)) {
      addTableRow();
    } else if (ae.getActionCommand().equals(CLEAR_ROW)) {
      clearTableRow();
    } else if (ae.getActionCommand().equals(DELETE)) {
      deleteTableRow(); // Confirm is needed, default behavior
    } else if (ae.getActionCommand().equals(DELETE_NO_CONFIRM)) {
      deleteTableRow(false); // confirm needed?
    } else if (ae.getActionCommand().equals(MOVE_UP)) {
      moveRowsUp();
    } else if (ae.getActionCommand().equals(MOVE_DOWN)) {
      moveRowsDown();
    } else if ((ae.getActionCommand().equals(ADD_FILTER)) || (ae.getActionCommand().equals(MODIFY_FILTER))) {
      filter(thisModel.getLastSelectedColumn());
    } else if (ae.getActionCommand().equals(CLEAR_FILTER)) {
      clearColumnFilter(thisModel.getLastSelectedColumn());
    } else if (ae.getActionCommand().equals(CLEAR_FILTERS_FOR_TABLE)) {
      clearColumnFiltersForTable();
    } else if (ae.getActionCommand().equals(CLEAR_FILTERS_FOR_ALL_TABLES)) {
      clearColumnFiltersForAllTables();
    } else if (ae.getActionCommand().equals(ADD_MULTIPLE_ROWS)) {
      addMultipleRows();
    } else if (ae.getActionCommand().equals(DUPLICATE_ROW)) {
      duplicateRow();
    } else if (ae.getActionCommand().equals(RESET_COLUMNS)) {
      resetColumns();
    } else {
      System.out.println(this.getClass() + " actionPerformed(): ERROR: Received invalid action " + "command: "
          + ae.getActionCommand());
    }

  }

  /**
   * Callback method for adding table rows.
   */
  private void addTableRow() {
    int[] selectedRows = thisTable.getSelectedRows();

    // Selected row and table neither filtered nor sorted: Insert after
    if ((selectedRows.length > 0) && !(((TTTableModel) thisTable.getModel()).isTableFiltered())
        && (((TTTableModel) thisTable.getModel()).getCurrentSortColumn() == -1)) {
      Object toBeInserted = thisModel.createNew();
      thisModel.insertDataAtRow(toBeInserted, selectedRows[0]);
    }
    // No rows selected or sorted or filtered: add last.
    else {
      Object toBeInserted = thisModel.createNew();
      thisModel.insertDataLast(toBeInserted);
    }

    // Resize the table
    thisCont.tuneSize();
  }

  /**
   * Callback method for adding table rows.
   */
  private void clearTableRow() {
    // Prompt the user for confirmation based on the number of rows selected.
    String optionTitle = "Confirm Clearing of Row";
    if(thisTable.getSelectedRows().length > 1){
      optionTitle = "Confirm Clearing of Rows";
    }
    if ((JOptionPane.showConfirmDialog(null, "Are you sure?", optionTitle, JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

      // Remove confirmed. Remove the row(s).
      int[] selectedRows = thisTable.getSelectedRows();
      if (selectedRows.length > 0) {
        // Remove from the model
        thisModel.clearSelectedData(selectedRows);

        // Resize the table
        thisCont.tuneSize();
      }
    }
  }

  /**
   * Callback methods for deleting selected table rows.
   */
  private void deleteTableRow() {
    deleteTableRow(true);
  }

  private void deleteTableRow(boolean isConfirmNeeded) {
    // Prompt the user for confirmation
    if (!isConfirmNeeded
        || (JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm Remove", JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

      // Remove confirmed. Remove the row(s).
      int[] selectedRows = thisTable.getSelectedRows();
      if (selectedRows.length > 0) {
        // Remove from the model
        thisModel.removeSelectedData(selectedRows);

        // Resize the table
        thisCont.tuneSize();
      }
    }
  }

  /**
   * Callback method for moving rows up.
   */
  private void moveRowsUp() {
    int[] selectedRows = thisTable.getSelectedRows();
    if (selectedRows.length > 0) {
      selectedRows = thisModel.moveRowsUp(selectedRows);
      setRowSelection(selectedRows);
    }
  }

  /**
   * Callback method for moving rows down.
   */
  private void moveRowsDown() {
    int[] selectedRows = thisTable.getSelectedRows();
    if (selectedRows.length > 0) {
      selectedRows = thisModel.moveRowsDown(selectedRows);
      setRowSelection(selectedRows);
    }
  }

  /**
   * Method for updating the selected rows in a table after a move up/move down
   * request
   * 
   * @param numbers
   */
  private void setRowSelection(int[] numbers) {
    for (int i = 0; i < numbers.length; i++) {
      thisTable.addRowSelectionInterval(numbers[i], numbers[i]);
    }
  }

  /**
   * Callback method for filtering a column.
   */
  private void filter(int column) {
    // Filter a column
    thisModel.filterColumn(column);

    // Redraw the table
    // thisModel.fireTableDataChanged();
    // tableHeader.repaint();

    // Update the table size and the tree
    thisCont.tuneSize();

  }

  /**
   * Callback method for clearing a column filter.
   */
  private void clearColumnFilter(int column) {
    // Clear the column filter
    thisModel.clearColumnFilter(column);

    // Redraw the table
    // thisModel.fireTableDataChanged();
    // tableHeader.repaint();

    // Update the table size and the tree
    thisCont.tuneSize();
  }

  /**
   * Callback method for clearing column filters for the table.
   */
  private void clearColumnFiltersForTable() {
    // Clear all the column filters for the table.
    thisModel.clearFilters();

    // Redraw the table
    // thisModel.fireTableDataChanged();
    // tableHeader.repaint();

    // Update the table size and the tree
    thisCont.tuneSize();
  }

  /**
   * Callback method for clearing column filters for all the tables.
   */
  private void clearColumnFiltersForAllTables() {
    // Get the table tree component and call the clear all column filters
    // for all the table containers.
    TableTreeComponent theTree = (TableTreeComponent) SwingUtilities.getAncestorOfClass(TableTreeComponent.class,
        thisTable);
    if (theTree != null) {
      // Collect the table containers
      Vector<TableContainer> cont = theTree.collectTableContainers();

      // Loop through each table container
      for (int i = 0; i < cont.size(); i++) {
        // Get the table and the model
        JTable table = ((TableContainer) cont.elementAt(i)).getTable();
        TTTableModel model = (TTTableModel) table.getModel();

        // Clear all the column filters for the table.
        model.clearFilters();

        // Redraw the table
        // thisModel.fireTableDataChanged();
        // table.getTableHeader().repaint();

        // Update the table size and the tree
        ((TableContainer) cont.elementAt(i)).tuneSize();

      }
    }
  }

  /**
   * Callback method for adding multiple rows at the same time. The rows will be
   * added to the end of the table.
   * 
   */
  private void addMultipleRows() {
    String input = (String) JOptionPane.showInputDialog(null, "How many rows you want to add?", "Add Multiple Row",
        JOptionPane.QUESTION_MESSAGE, null, null, 1);
    if (input != null && !input.equals("")) {
      int times = 0;
      try {
        times = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid number of rows.");
      }

      if (times > 0) {
        thisModel.addMultipleRows(times);
      }

      // Update the table size and the tree
      thisCont.tuneSize();

    }
  }

  /**
   * Callback method for adding multiple rows at the same time
   * 
   */
  private void duplicateRow() {
    int[] selectedRows = thisTable.getSelectedRows();
    String input = (String) JOptionPane.showInputDialog(null, "How many times you want to duplicate the row?",
        "Duplicate Row", JOptionPane.QUESTION_MESSAGE, null, null, 1);
    if (input != null && !input.equals("")) {

      int times = 0;
      try {
        times = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid number of rows.");
      }

      if (times > 0) {
        thisModel.duplicateRow(selectedRows, times);
      }

      // Update the table size and the tree
      thisCont.tuneSize();

      // Keep the row selection
      setRowSelection(selectedRows);
    }
  }

  /**
   * Callback method for resetting the visual representation of the different
   * table types to their default looks
   */
  private void resetColumns() {
    ((TTTableModel) thisTable.getModel()).getTableInfo().resetColumns();
    thisCont.refreshAllTables();
  }
}
