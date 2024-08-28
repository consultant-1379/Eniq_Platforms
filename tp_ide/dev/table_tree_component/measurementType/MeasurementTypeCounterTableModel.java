package measurementType;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JTable;

import ssc.rockfactory.Measurementcounter;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.DescriptionCellEditor;
import tableTree.DescriptionCellRenderer;
import tableTree.TTDefaultTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

/**
 * Concrete class that models the counter table.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class MeasurementTypeCounterTableModel extends TTTableModel {

    private static final long serialVersionUID = -5150345618080149223L;

    /**
     * The table type/name
     */
    private static final String myTableName = "Counters";

    /**
     * Column names, used as headings for the columns.
     */
    private static final String[] myColumnNames = { "Name", "Description",
	    "Time Agg", "Group Agg", "Count Agg", "YesNo Value" };

    /**
     * Column widths, used to graphically layout the columns.
     */
    private static final int[] myColumnWidths = { 150, 150, 50, 50, 50, 100 };

    /**
     * Max number of rows shown before adding scrollbars
     */
    private static final int maxRowsShown = 20;

    /**
     * Static method that returns the table type and its corresponding column
     * names
     * 
     * @return the table information object
     */
    public static TableInformation createTableTypeInfo() {
	return new TableInformation(myTableName, myColumnNames, myColumnWidths,
		maxRowsShown);
    }

    /**
     * Rock factory, used to connect to the database.
     */
    private RockFactory rockFactory = null;

    /**
     * Constructor. Initializes the column names, widths and table name.
     * 
     * @param rockFactory
     * @param tableInfos
     * @param isTreeEditable
     */
    public MeasurementTypeCounterTableModel(RockFactory rockFactory,
	    Vector<TableInformation> tableInfos, boolean isTreeEditable) {
	super(rockFactory, tableInfos, isTreeEditable);
	this.rockFactory = rockFactory;
	this.setColumnNames(myColumnNames);
	this.setTableName(myTableName);
	this.setColumnWidths(myColumnWidths);
    }

    /**
     * Overridden method for getting the value at a certain position in the
     * table. Gets it from the corresponding RockDBObject. *
     * 
     * @param row
     * @param col
     * @return the object
     */
    public Object getValueAt(int row, int col) {
	Measurementcounter theCounter = (Measurementcounter) displayData
		.elementAt(row);
	// Measurementcounter theCounter =
	// (Measurementcounter)data.elementAt(row);

	switch (col) {
	case 0:
	    return theCounter.getDataname();

	case 1:
	    return theCounter.getDescription();

	case 2:
	    return theCounter.getTimeaggregation();

	case 3:
	    return theCounter.getGroupaggregation();

	case 4:
	    return theCounter.getCountaggregation();

	case 5:
	    // return theCounter.getYesNoValue();
	    if (theCounter.getYesNoValue().equals(1)) {
		return true;
	    } else
		return false;

	default:
	    break;
	}

	return data;
    }

    /**
     * Overridden method for getting the order value at a certain position in
     * the table. Gets it from the corresponding RockDBObject. *
     * 
     * @param row
     * @param col
     * @return the object
     */
    public Object getOriginalValueAt(int row, int col) {
	Measurementcounter theCounter = (Measurementcounter) data
		.elementAt(row);

	switch (col) {
	case 0:
	    return theCounter.getDataname();

	case 1:
	    return theCounter.getDescription();

	case 2:
	    return theCounter.getTimeaggregation();

	case 3:
	    return theCounter.getGroupaggregation();

	case 4:
	    return theCounter.getCountaggregation();

	case 5:
	    // return theCounter.getYesNoValue();
	    if (theCounter.getYesNoValue().equals(1)) {
		return true;
	    } else
		return false;

	default:
	    break;
	}

	return data;
    }

    /**
     * Overridden version of this method. Returns the value in a specified
     * column for the given data object. Returns null in case an invalid column
     * index.
     * 
     * @param dataObject
     * @param col
     * 
     * @return The data object in the cell
     */
    public Object getColumnValueAt(Object dataObject, int col) {
	Measurementcounter theCounter = (Measurementcounter) dataObject;

	switch (col) {
	case 0:
	    return theCounter.getDataname();

	case 1:
	    return theCounter.getDescription();

	case 2:
	    return theCounter.getTimeaggregation();

	case 3:
	    return theCounter.getGroupaggregation();

	case 4:
	    return theCounter.getCountaggregation();

	case 5:
	    // return theCounter.getYesNoValue();
	    if (theCounter.getYesNoValue().equals(1)) {
		return true;
	    } else
		return false;

	default:
	    break;
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

	// Debug:
	// System.out.println(this.getClass() + " setValueAt()");

	int index = data.indexOf((Measurementcounter) displayData
		.elementAt(row));
	Measurementcounter theCounter = (Measurementcounter) data
		.elementAt(index);

	switch (col) {
	case 0:
	    theCounter.setDataname((String) value);
	    break;

	case 1:
	    theCounter.setDescription((String) value);
	    break;

	case 2:
	    theCounter.setTimeaggregation((String) value);
	    break;

	case 3:
	    theCounter.setGroupaggregation((String) value);
	    break;

	case 4:
	    theCounter.setCountaggregation((String) value);
	    break;

	case 5:
	    if ((Boolean) value) {
		theCounter.setYesNoValue(new Integer(1));
	    } else
		theCounter.setYesNoValue(new Integer(0));
	    break;

	default:
	    break;
	}

	// We need to indicate that data has changed in the table. However, in
	// case the table is sorted or filtered, it is not enough to tell that
	// one cell changed.
	// fireTableCellUpdated(row, col);
	fireTableDataChanged();

	refreshTable();

	// In case the table is filtered, then re-filter the table so that a
	// correct number of rows is displayed.
	// if (this.isTableFiltered()) {
	// // Debug:
	// // System.out.println(this.getClass() + " setValueAt(): table is
	// // filtered. Refilter it.");
	// this.filterTable();
	// }
    }

    /**
     * Overridden method for setting the column editor of the Description
     * column.
     * 
     * @param theTable
     *                the table
     */
    public void setColumnEditors(JTable theTable) {

	// Create the custom editor
	DescriptionCellEditor pairEdit = new DescriptionCellEditor(this
		.isTreeEditable());

	// Set the editor for each column
	// theTable.getColumnModel().getColumn(0).setCellEditor(defEdit); use
	// default
	theTable.getColumnModel().getColumn(1).setCellEditor(pairEdit);
	// theTable.getColumnModel().getColumn(2).setCellEditor(defEdit); use
	// default
	// theTable.getColumnModel().getColumn(3).setCellEditor(defEdit); use
	// default
	// theTable.getColumnModel().getColumn(4).setCellEditor(defEdit); use
	// default
	// theTable.getColumnModel().getColumn(5).setCellEditor(defEdit); use
	// default
    }

    /**
     * Overridden method for setting the column renderer.
     * 
     * @param theTable
     *                the table
     */
    public void setColumnRenderers(JTable theTable) {

	// Create the custom renderers
	TTDefaultTableCellRenderer defRend = new TTDefaultTableCellRenderer();
	DescriptionCellRenderer pairRend = new DescriptionCellRenderer();

	// Set the renderer for each column
	theTable.getColumnModel().getColumn(0).setCellRenderer(defRend);
	theTable.getColumnModel().getColumn(1).setCellRenderer(pairRend);
	theTable.getColumnModel().getColumn(2).setCellRenderer(defRend);
	theTable.getColumnModel().getColumn(3).setCellRenderer(defRend);
	theTable.getColumnModel().getColumn(4).setCellRenderer(defRend);
	// theTable.getColumnModel().getColumn(5).setCellRenderer(); we use the
	// default

    }

    /**
     * Overridden method for creating specifically new Measurementcounters.
     * 
     * @return the new RockObject
     */
    public RockDBObject createNew() {
	Measurementcounter newCounter = new Measurementcounter(rockFactory);
	return newCounter;
    }

    /**
     * Overridden version of this method for saving specifically
     * Measurementcounters.
     * 
     * @throws RockException
     * @throws SQLException
     */
    protected void saveData(Object rockObject) throws SQLException,
	    RockException {
	((Measurementcounter) rockObject).saveDB();
    }

    /**
     * Overridden version of this method for deleting specifically
     * Measurementcounters.
     * 
     * @throws RockException
     * @throws SQLException
     */
    protected void deleteData(Object rockObject) throws SQLException,
	    RockException {
	((Measurementcounter) rockObject).deleteDB();
    }

    /**
     * Overridden version of this method.
     */
    public Object copyOf(Object toBeCopied) {
	Measurementcounter newCounter = new Measurementcounter(rockFactory);
	Measurementcounter oldCounter = (Measurementcounter) toBeCopied;

	newCounter.setDataname(oldCounter.getDataname());
	newCounter.setDescription(oldCounter.getDescription());
	newCounter.setTimeaggregation(oldCounter.getTimeaggregation());
	newCounter.setGroupaggregation(oldCounter.getGroupaggregation());
	newCounter.setCountaggregation(oldCounter.getCountaggregation());
	newCounter.setYesNoValue(oldCounter.getYesNoValue());

	return newCounter;
    }

    /**
     * Overridden version of this method. Checks is the column for this table
     * type is filtered.
     * 
     * @param column
     *                the selected column
     * @return true if the column is filtered in the column type level
     */
    public boolean isColumnFilteredForTableType(int column) {
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
     * @return the regex filter for the table type for this column
     */
    public String getColumnFilterForTableType(int column) {
	return getTableInfo().getTableTypeColumnFilter(column);
    }

    /**
     * Overridden version of the method to be used as an example how to use the
     * observable.
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
	 * ((Measurementcounter)data.elementAt(i)).setDataname(value); } }
	 */
	// System.out.println("Notification received to
	// MesurementTypeCounterTableModel, with argument:
	// "+(String)sourceArgument);
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
    
    protected void setOrderOf(Object currentData, int newOrderNumber) {
        // intentionally left blank
      }
    
    protected int getOrderOf(Object currentData) {
        return 0;
      }
    
    protected boolean dataHasOrder() {
        return false;
      }
    
    public boolean isTableRowAddRemoveAllowed() {
        // By default the table rows are allowed to be added and removed.
        return true;
      }
}
