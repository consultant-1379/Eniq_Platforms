package tableTreeUtils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;
import tableTree.TableTreeComponent;

/**
 * Container component for tables. This is used to configure the graphical
 * aspects of the table.
 * 
 * @author enaland ejeahei eheitur
 */
public class TableContainer extends JScrollPane implements SavableTreeNode {

    private static final long serialVersionUID = 3693328340734160985L;
    private JTable theTable;
    private TTTableModel tableModel = null;
    private RowHeaderTable rowHeaderTable;
    private RockFactory rockFactory = null;
    private DefaultTreeModel treeModel = null;
    private DefaultMutableTreeNode tableNode = null;
    private TableTreeComponent tableTreeComponent = null;

    /**
     * Constructor, creates a container based on a given table model. Also adds
     * a TableMouseEventListener to catch right-clicks on both the table header
     * and the table itself
     * 
     * @param inModel
     */
    public TableContainer(TTTableModel inModel) {
	super();
	tableModel = inModel;

	// Create a new container with the model.
	theTable = new TableForContainer(tableModel);

	// Create a the scroll pane view port and set the table for its view.
	this.setViewportView(theTable);

	// Create the row header view port and table
	rowHeaderTable = new RowHeaderTable();
	this.setRowHeaderView(rowHeaderTable);

	// Set the name for the table.
	theTable.setName(tableModel.getTableName());

	// Get the rock factory
	rockFactory = tableModel.getRockFactory();

	// Add the mouse listeners
	TableMouseEventListener listener = new TableMouseEventListener(this);
	theTable.addMouseListener(listener);
	theTable.getTableHeader().addMouseListener(listener);
	theTable.getColumnModel().addColumnModelListener(listener);

	// The focus listener below is a trial for catching the clicks outside
	// the cell while editing, so that the editing could be stopped.
	// Otherwise it is misleading that value is shown in the renderer, but
	// the value has not been set.
	//
	// This solution is not working yet!!!!!
	//	
//	// Add focus listener to be able to catch clicks outside the cells when
//	// editing in still ongoing.
//	theTable.addFocusListener(new java.awt.event.FocusAdapter() {
//	    public void focusLost(java.awt.event.FocusEvent evt) {
//		// int col = theTable.getColumnCount();// this is the changes
//		// if (col == 0)// you must take care on each columns
//		// // differently since there type is different
//		// {
//		if (theTable.getCellEditor() != null) {
//		    System.out.println("editor (row: "
//			    + theTable.getEditingRow()
//			    + ", col: "
//			    + theTable.getEditingColumn()
//			    + "): editor value: "
//			    + theTable.getCellEditor().getCellEditorValue()
//			    + ", current value: "
//			    + theTable.getValueAt(theTable.getEditingRow(),
//				    theTable.getEditingColumn()));
//		    if (!theTable.getCellEditor().getCellEditorValue().equals(
//			    theTable.getValueAt(theTable.getEditingRow(),
//				    theTable.getEditingColumn()))) {
//			System.out
//				.println("in focus listener .... lost focus. Stop editing.");
//			theTable.getCellEditor().stopCellEditing();
//		    } else {
//			System.out
//				.println("in focus listener .... lost focus. Cancel editing.");
//			theTable.getCellEditor().cancelCellEditing();
//
//		    }
//
//		}
//		// } else
//		// ;// take care about the other rows if you need to.
//	    }
//	});

	// This listener is needed to be able to open pop-up menu on an empty
	// table
	this.addMouseListener(listener);

	// Set the renderer for the table header
	tableModel.setTableHeaderRenderers(theTable);

	// Set editor and renderer for the table columns
	tableModel.setColumnEditors(theTable);
	tableModel.setColumnRenderers(theTable);

	// Put the header of the rowHeader table to the corner of the scroll
	// pane.
	JTableHeader corner = rowHeaderTable.getTableHeader();
	corner.setReorderingAllowed(false);
	corner.setResizingAllowed(false);
	corner.setBackground(SystemColor.control);
	corner.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	this.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);

	// Update the sizes of the table
	initializeColumnWidths();
	tuneSize();
    }

    /**
     * Returns the table within the component.
     * 
     * @return theTable
     */
    public JTable getTable() {
	return theTable;
    }

    /**
     * Returns the table model
     * 
     * @return the table model
     */
    public TTTableModel getTableModel() {
	return tableModel;
    }

    /**
     * Returns the correct popup menu for a right click
     * 
     * @param targetComponent
     * @return the pop-up menu
     */
    public JPopupMenu getPopupmenu(Component targetComponent) {
	return ((TTTableModel) theTable.getModel()).getPopupMenu(
		targetComponent, this);
    }

    /**
     * Returns the RockFactory that is the connection to the DB
     * 
     * @return the factory
     */
    public RockFactory getRockFactory() {
	return rockFactory;
    }

    /**
     * Save the changed parameters in the DB
     */
    public void saveChanges() {
	TTTableModel tableModel = (TTTableModel) theTable.getModel();
	tableModel.saveChanges();
    }

    /**
     * Remove the objects completely from the DB.
     */
    public void removeFromDB() {
	TTTableModel tableModel = (TTTableModel) theTable.getModel();
	tableModel.removeFromDB();
    }

    /**
     * Helper function for tuning the size of the table. Uses the tableModel's
     * preferences for column width.
     * 
     */
    public void tuneSize() {

	int maxRowsShown = tableModel.getTableInfo().getRowsShown();
	int totBorderSize = 3;
	Dimension headerDim;
	Dimension tableDim;
	int tableRowHeight;
	int tableWidth;
	Dimension finalDim;
	int rowTableWidth = 0;

	// Set the row height for both the actual table and the row header
	// table.
	tableRowHeight = getTableRowMaxHeight();
	theTable.setRowHeight(tableRowHeight);
	rowHeaderTable.setRowHeight(tableRowHeight);

	// Set the width for the rowHeader table based on the number of
	// rows in the table.
	if (theTable.getRowCount() > 999) {
	    rowTableWidth = 40;
	} else if (theTable.getRowCount() > 99) {
	    rowTableWidth = 30;
	} else {
	    rowTableWidth = 20;
	}
	rowHeaderTable.setPreferredScrollableViewportSize(new Dimension(
		rowTableWidth, rowHeaderTable
			.getPreferredScrollableViewportSize().height));

	// Get the sizes from the table and the table model (in the method
	// setColumnSizes). The width of the rowHeader is added to the table
	// width.
	headerDim = theTable.getTableHeader().getPreferredSize();
	tableDim = theTable.getPreferredSize();
	// tableWidth = tableModel.getTableInfo().getTotalColumnWidth() +
	// rowTableWidth;
	tableWidth = tableModel.getTableInfo().getTableWidth() + rowTableWidth;

	// Check if the table height exceeds the maximum number of rows to be
	// shown. If yes, the height is set according to the max number of rows.
	// If no, the real height is used.
	if (tableDim.height > (tableRowHeight * maxRowsShown)) {
	    finalDim = new Dimension(tableWidth, headerDim.height
		    + tableRowHeight * maxRowsShown + totBorderSize);
	} else {
	    finalDim = new Dimension(tableWidth, headerDim.height
		    + tableDim.height + totBorderSize);
	}

	// Set the new preferred size for the container
	this.setPreferredSize(finalDim);

	// Update column order and margin sizes, if needed
	this.updateColumnOrderAndWidths();

	// Update the tree location for this TableContainer, if the references
	// have been set.
	if (tableTreeComponent != null && treeModel != null
		&& tableNode != null) {
	    tableTreeComponent.cancelEditing();
	    treeModel.nodeChanged(tableNode);
	}
    }

    /**
     * This method is used to refresh the table's visual representation
     * according to earlier set preferences, if these exsist. If not, the use
     * default values
     */
    private void updateColumnOrderAndWidths() {
	// System.out.println("Refresh table: "+getTable().getName());
	TableColumnModel colModel = theTable.getColumnModel();
	String[] wantedOrder = tableModel.getTableInfo()
		.getColumnNamesInOrderFromProperties();
	int[] wantedOrderWidths = tableModel.getTableInfo()
		.getColumnWidthsInOrderFromProperties();
	String[] currentOrder = new String[wantedOrder.length];

	int index = 0;
	while (index < wantedOrder.length) {
	    for (int i = 0; i < wantedOrder.length; i++) {
		currentOrder[i] = theTable.getColumnModel().getColumn(i)
			.getHeaderValue().toString();
	    }

	    if (!wantedOrder[index].equals(currentOrder[index])) {
		colModel.moveColumn(stringArrayIndexOf(wantedOrder[index],
			currentOrder), index);
		index = 0;
	    } else {
		index++;
	    }

	}

	for (int i = 0; i < colModel.getColumnCount(); i++) {
	    colModel.getColumn(i).setPreferredWidth(wantedOrderWidths[i]);
	}

    }

    /**
     * This method is used to save the new column order to the properties file
     */
    public void saveNewColumnOrder() {
	String[] currentOrder = new String[theTable.getColumnModel()
		.getColumnCount()];
	for (int i = 0; i < currentOrder.length; i++) {
	    currentOrder[i] = theTable.getColumnName(i);
	}
	tableModel.getTableInfo().setColumnNamesInOrderToProperties(
		currentOrder);
    }

    /**
     * This method is used to save the new column widths to the properties file
     */
    public void saveNewColumnSizes() {
	String[] colNames = new String[theTable.getColumnCount()];
	int[] widths = new int[colNames.length];
	for (int i = 0; i < colNames.length; i++) {
	    colNames[i] = theTable.getColumnName(i);
	    widths[i] = theTable.getColumnModel().getColumn(i)
		    .getPreferredWidth();
	}
	tableModel.getTableInfo().setColumnWidths(colNames, widths);
    }

    /**
     * This method gets a hold of the index of a certain string in a string
     * array
     * 
     * @param value
     * @param array
     * @return
     */
    private int stringArrayIndexOf(String value, String[] array) {
	int index = -1;

	for (int i = 0; i < array.length; i++) {
	    if (value.equals(array[i])) {
		index = i;
		break;
	    }
	}

	return index;
    }

    /**
     * This method is used to initialize the column widths when a table is
     * created
     * 
     */
    private void initializeColumnWidths() {
	for (int i = 0; i < theTable.getColumnCount(); i++) {
	    theTable.getColumnModel().getColumn(i).setPreferredWidth(
		    tableModel.getPreferredColumnWidth(i));
	}
    }

    /**
     * Calculate the row height based on the largest contained component
     * 
     * @return the maximum row height
     */
    private int getTableRowMaxHeight() {

	int maxHeight = 0;

	if (theTable.getRowCount() <= 0)
	    return theTable.getRowHeight();

	for (int i = 0; i < theTable.getColumnCount(); i++) {
	    TableCellRenderer renderer = theTable.getCellRenderer(0, i);
	    Component comp = theTable.prepareRenderer(renderer, 0, i);
	    int thisHeight = comp.getPreferredSize().height;
	    maxHeight = Math.max(maxHeight, thisHeight);
	}
	return maxHeight;
    }

    /**
     * This method is used to set the needed references for being able to update
     * the tree node that the table is contained in whenever rows are added or
     * removed
     * 
     * @param comp
     * @param model
     * @param node
     */
    public void setTreeUpdateReferences(TableTreeComponent comp,
	    TreeModel model, DefaultMutableTreeNode node) {
	tableTreeComponent = comp;
	treeModel = (DefaultTreeModel) model;
	tableNode = node;
	tableModel.setTableTreeComponentAsObserver(comp);
    }

    /**
     * This method forces a tuneSize() on all the tables in the tree, usually as
     * a result from adding or removing table type filters
     */
    public void refreshAllTables() {
	// System.out.println("Refresh all tables");
	Iterator<TableContainer> iter = tableTreeComponent
		.collectTableContainers().iterator();
	while (iter.hasNext()) {
	    iter.next().tuneSize();
	}
    }

    /**
     * This is a helper method for enchaning the look and feel of the table in
     * the tree
     */
    public void reActivateForEditing() {
	if (tableTreeComponent != null) {
	    TreePath path = tableTreeComponent.getPathForLocation((int) this
		    .getLocation().getX(), (int) this.getLocation().getY());
	    tableTreeComponent.startEditingAtPath(path);
	}
    }

    /**
     * This is, as it is now, obsolete. It can, however, be used to override
     * default behavior of the JTable whenever needed
     * 
     * @author enaland
     */
    protected class TableForContainer extends JTable {

	private static final long serialVersionUID = 1676114662786483592L;

	/**
	 * Constructor.
	 * 
	 * @param inModel
	 */
	public TableForContainer(TTTableModel inModel) {
	    super(inModel);
	}

	/*
	 * NOTE: This one is now commented out, because it caused problems when
	 * clicking a combo box after editing another combo box cell or a
	 * description cell. Exception in thread "AWT-EventQueue-0"
	 * java.awt.IllegalComponentStateException: component must be showing on
	 * the screen to determine its location
	 * 
	 * 
	 * 
	 * @Override public void editingStopped(ChangeEvent e) { // Debug:
	 * System.out.println(this.getClass() + " editingStopped()");
	 * 
	 * super.editingStopped(e); TableContainer.this.tuneSize(); }
	 */

    }

    /**
     * This class is used for a table row header displaying the number of rows
     * in the actual data table.
     * 
     * @author enaland
     * 
     */
    protected class RowHeaderTable extends JTable {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public RowHeaderTable() {
	    super();
	    TableModel rowHeaderData = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public int getColumnCount() {
		    return 1;
		}

		public int getRowCount() {
		    return TableContainer.this.theTable.getRowCount();
		}

		public Object getValueAt(int row, int column) {
		    return row + 1;
		}

		public String getColumnName(int column) {
		    return new String("#");
		}
	    };
	    this.setModel(rowHeaderData);
	    setPreferredScrollableViewportSize(this.getPreferredSize());
	    setBackground(SystemColor.control);
	    this.setEnabled(false);
	    this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	}

	/**
	 * Set the size of the row table header (a.k.a the running number shown
	 * as the first column)
	 * 
	 * @param newSize
	 */
	public void setNewRowHeaderSize(Dimension newSize) {
	    setPreferredScrollableViewportSize(newSize);
	}

    }
}
