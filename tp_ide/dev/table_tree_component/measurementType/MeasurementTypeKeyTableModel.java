package measurementType;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JTable;

import ssc.rockfactory.Measurementkey;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.ComboBoxTableCellEditor;
import tableTree.ComboBoxTableCellRenderer;
import tableTree.DescriptionCellEditor;
import tableTree.DescriptionCellRenderer;
import tableTree.LimitedRangeIntegerTableCellEditor;
import tableTree.LimitedRangeIntegerTableCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

/**
 * Concrete class that models the key table.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class MeasurementTypeKeyTableModel extends TTTableModel {

    private static final long serialVersionUID = -5150345618080149223L;

    /**
     * The table type/name
     */
    public static final String myTableName = "Keys";

    /**
     * Column names, used as headings for the columns.
     */
    public static final String[] myColumnNames = { "Name", "Description",
	    "Is Element", "Unique Key" };

    /**
     * Column widths, used to layout the table.
     */
    public static final int[] myColumnWidths = { 150, 150, 50, 50 };

    /**
     * Max number of rows shown before adding scroll bars
     */
    private static final int maxRowsShown = 25;

    /**
     * Static method that returns the table type and its corresponding column
     * names
     * 
     * @return table information
     */
    public static TableInformation createTableTypeInfo() {
	return new TableInformation(myTableName, myColumnNames, myColumnWidths,
		maxRowsShown);
    }

    /**
     * Constructor. Initializes the column names, widths and table name.
     * 
     * @param rockFactory
     * @param tableInfos
     * @param isTreeEditable
     */
    public MeasurementTypeKeyTableModel(RockFactory rockFactory,
	    Vector<TableInformation> tableInfos, boolean isTreeEditable) {
	super(rockFactory, tableInfos, isTreeEditable);
	this.rockFactory = rockFactory;
	this.setColumnNames(myColumnNames);
	this.setColumnWidths(myColumnWidths);
	this.setTableName(myTableName);
    }

    /**
     * Overridden method for getting the value at a certain position in the
     * table. Gets it from the corresponding RockDBObject.
     * 
     * @param row
     * @param col
     * @return the current value
     */
    public Object getValueAt(int row, int col) {
	Measurementkey theKey = (Measurementkey) displayData.elementAt(row);
	// Measurementkey theKey = (Measurementkey) data.elementAt(row);

	switch (col) {
	case 0:
	    return theKey.getDataname();

	case 1:
	    return theKey.getDescription();

	case 2:
	    return theKey.getIselement();

	case 3:
	    return theKey.getUniquekey();

	default:
	    break;
	}

	return data;
    }

    /**
     * Overridden method for getting the value at a certain position in the
     * original table data. Gets it from the corresponding RockDBObject.
     * 
     * @param row
     * @param col
     * @return the current value
     */
    public Object getOriginalValueAt(int row, int col) {
	Measurementkey theKey = (Measurementkey) data.elementAt(row);

	switch (col) {
	case 0:
	    return theKey.getDataname();

	case 1:
	    return theKey.getDescription();

	case 2:
	    return theKey.getIselement();

	case 3:
	    return theKey.getUniquekey();

	default:
	    System.out
		    .println(this.getClass()
			    + " getColumnValueAt(): Invalid column index: "
			    + col + ".");
	}

	return null;
    }

    /**
     * Overridden version of this method. Returns the value in a specified
     * column for the given data object. Returns null in case an invalid column
     * index.
     * 
     * @param dataObject
     * @param col
     * @return The data object in the cell
     */

    public Object getColumnValueAt(Object dataObject, int col) {
	Measurementkey theKey = (Measurementkey) dataObject;

	switch (col) {
	case 0:
	    return theKey.getDataname();

	case 1:
	    return theKey.getDescription();

	case 2:
	    return theKey.getIselement();

	case 3:
	    return theKey.getUniquekey();

	default:
	    System.out.println(this.getClass()
		    + "getColumnValueAt(): Invalid column index: " + col + ".");
	}

	return null;
    }

    /**
     * Overridden method for setting the value at a certain position in the
     * table Sets it in the corresponding RockDBObject.
     * 
     * @param value
     * @param row
     * @param col
     */
    public void setValueAt(Object value, int row, int col) {
	int index = data.indexOf((Measurementkey) displayData.elementAt(row));
	Measurementkey theKey = (Measurementkey) data.elementAt(index);

	switch (col) {
	case 0:
	    theKey.setDataname((String) value);
	    break;

	case 1:
	    theKey.setDescription((String) value);
	    break;

	case 2:
	    theKey.setIselement((Integer) value);
	    break;

	case 3:
	    if (value != null && !value.toString().equals("")) {
		theKey.setUniquekey(Integer.parseInt(value.toString()));
	    } else {
		// Here you could set the value to zero in case the edited value
		// is empty, or remove it from the DB completely (not
		// implemented).
		//
		// theKey.setUniquekey(0);
		// or
		// removeUniqueKey(theKey);
		return;
	    }
	    break;

	default:
	    System.out.println(this.getClass()
		    + "setValueAt(): Invalid column index: " + col + ".");
	}

	refreshTable();
	
	// We need to indicate that data has changed in the table. However, in
	// case the table is sorted or filtered, it is not enough to tell that
	// one cell changed.
	// fireTableCellUpdated(row, col);
	fireTableDataChanged();

    }

    /**
     * Overridden method for setting the column editor of the Description and
     * IsElement columns.
     * 
     * @param theTable
     *                The table
     */
    public void setColumnEditors(JTable theTable) {
	// Create the custom editor
	DescriptionCellEditor pairEdit = new DescriptionCellEditor(this
		.isTreeEditable());
	Object[] boxItems = { 30, 32, 34 };
	ComboBoxTableCellEditor comboEdit = new ComboBoxTableCellEditor(
		boxItems);
	LimitedRangeIntegerTableCellEditor limitedIntEditor = new LimitedRangeIntegerTableCellEditor(
		0, 100, true);
	LimitedSizeTextTableCellEditor limitedTextEditor = new LimitedSizeTextTableCellEditor(
		6, 32, true);

	// Set the editor for each column
	theTable.getColumnModel().getColumn(0).setCellEditor(limitedTextEditor);
	theTable.getColumnModel().getColumn(1).setCellEditor(pairEdit);
	theTable.getColumnModel().getColumn(2).setCellEditor(comboEdit);
	theTable.getColumnModel().getColumn(3).setCellEditor(limitedIntEditor);
    }

    /**
     * Overridden method for setting the column renderer. Used to set a renderer
     * for the description field and isElement combo box.
     * 
     * @param theTable
     *                The table
     */
    public void setColumnRenderers(JTable theTable) {
	// Create the custom renderers
	// TTDefaultTableCellRenderer defRend = new
	// TTDefaultTableCellRenderer();
	Object[] boxItems = { 30, 32, 34 };
	ComboBoxTableCellRenderer comboRend = new ComboBoxTableCellRenderer(
		boxItems, true);
	DescriptionCellRenderer pairRend = new DescriptionCellRenderer();
	LimitedSizeTextTableCellRenderer limitedTextRend = new LimitedSizeTextTableCellRenderer(
		6, 32, true);
	LimitedRangeIntegerTableCellRenderer limitedIntRend = new LimitedRangeIntegerTableCellRenderer(
		0, 100, true);

	// Set the renderer for each column
	theTable.getColumnModel().getColumn(0).setCellRenderer(limitedTextRend);
	theTable.getColumnModel().getColumn(1).setCellRenderer(pairRend);
	theTable.getColumnModel().getColumn(2).setCellRenderer(comboRend);
	theTable.getColumnModel().getColumn(3).setCellRenderer(limitedIntRend);
    }

    /**
     * Overridden version of this method for creating specifically
     * Measurementkeys.
     * 
     * @return the new rock object
     */
    public RockDBObject createNew() {
	Measurementkey newKey = new Measurementkey(rockFactory);
	return newKey;
    }

    /**
     * Overridden version of this method for saving specifically
     * Measurementkeys.
     * 
     * @throws RockException
     * @throws SQLException
     */
    protected void saveData(Object rockObject) throws SQLException,
	    RockException {
	((Measurementkey) rockObject).saveDB();
    }

    /**
     * Overridden version of this method for deleting specifically
     * Measurementkeys.
     * 
     * @throws RockException
     * @throws SQLException
     */
    protected void deleteData(Object rockObject) throws SQLException,
	    RockException {
	((Measurementkey) rockObject).deleteDB();
    }

    /**
     * Overridden version of this method for setting the order number of a
     * Measurementkey. In this example, we pretend that the UniqueKey field is
     * the order number.
     * 
     * @param currentData
     *                the Measurementkey to be updated
     * @param newOrderNumber
     *                the new value to set the order number of
     */
    protected void setOrderOf(Object currentData, int newOrderNumber) {
	((Measurementkey) currentData).setUniquekey(newOrderNumber);
    }

    /**
     * Overridden version of this method for retrieving the order number of a
     * Measurementkey. In this example, we pretend that the UniqueKey field is
     * the order number
     * 
     * @param currentData
     *                the Measurementkey whose order number we're querying.
     */
    protected int getOrderOf(Object currentData) {
	int orderNumber = ((Measurementkey) currentData).getUniquekey();
	return orderNumber;
    }

    /**
     * Overridden version of this method. Indicates that this table has an order
     * column
     * 
     * @return true
     */
    protected boolean dataHasOrder() {
	return true;
    }

    /**
     * Overridden version of this method.
     */
    public Object copyOf(Object toBeCopied) {
	Measurementkey newKey = new Measurementkey(rockFactory);
	Measurementkey oldKey = (Measurementkey) toBeCopied;

	newKey.setDataname(oldKey.getDataname());
	newKey.setDescription(oldKey.getDescription());
	newKey.setIselement(oldKey.getIselement());

	return newKey;
    }

    /**
     * Overridden version of this method. Checks is the column for this table
     * type is filtered.
     * 
     * @param column
     *                the selected column
     * @return true if the column is table type filtered
     */
    public boolean isColumnFilteredForTableType(int column) {
	if (column == -1)
	    return false;
	String filter = getTableInfo().getTableTypeColumnFilter(column);
	if (filter != null && filter != "")
	    return true;
	else
	    return false;
    }

    /**
     * Overridden version of this method. Returns the column filter for this
     * table type.
     * 
     * @param column
     *                the selected column
     * @return the table type filter for the column
     */
    public String getColumnFilterForTableType(int column) {
	return getTableInfo().getTableTypeColumnFilter(column);
    }

    /**
     * Overridden version of the method to show an example how the update method
     * can be used.
     * 
     * @param sourceObject
     * @param sourceArgument
     */

    public void update(Observable sourceObject, Object sourceArgument) {
	/*
	 * Example of how this functionality can be used
	 * 
	 * if(((String)sourceArgument).equals("setMainNodeName")){ String value =
	 * ((TTParameterModel)sourceObject).getMainNodeName();
	 * 
	 * for(int i=0;i<data.size();i++){
	 * ((Measurementkey)data.elementAt(i)).setDataname(value); } }
	 */

    }

    /**
     * Overridden version of the method to show that it is possible to not allow
     * the table rows to be added or removed.
     * 
     * @return true if the adding and removing of the rows is allowed.
     */
    public boolean isTableRowAddRemoveAllowed() {
	// By default, allow the table rows to be added or removed
    	return true;
    }

    /**
     * Overridden version of the method to allow description components to be
     * clicked even though the tree is not editable in the read-only mode.
     * 
     * @param row
     * @param col
     * @return true if the cell is editable
     */
    public boolean isCellEditable(int row, int col) {
	// Always allow the editing for the description column, so that the edit
	// button can be clicked also in the read-only mode. For all the other
	// columns, the editable value depends on the super class
	// implementation.
	if (col == 1)
	    return true;
	else
	    return super.isCellEditable(row, col);
    }

    /**
     * Overridden version of the method to implement validations for this table
     * model.
     * 
     * @see tableTree.TTTableModel#validateData()
     */
    @Override
    public Vector<String> validateData() {
	return new Vector<String>();
    }
    
    protected void clearData(Object elementAt) throws SQLException, RockException {
        // Do nothing.
      }
}
