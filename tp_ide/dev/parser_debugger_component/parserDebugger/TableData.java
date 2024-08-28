package parserDebugger;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;


public class TableData extends JTable {

	private static final long serialVersionUID = 7140851450301212965L;
	private Vector<String> columnNames = null;
	private Vector<Object> data = null;
	private Vector<Transformation> transformations = null;

	private static int numberOfTables = 0;
	private String tableName = null;

	public TableData(Vector<String> columnNames, Vector<Vector<Object>> data) {
		super(data, columnNames);

		this.columnNames = new Vector<String>(columnNames);
		this.data = new Vector<Object>(data.get(0));
		this.transformations = new Vector<Transformation>();

		// Set the name for the table
		numberOfTables++;
		tableName = "Table-" + numberOfTables;

        /*
		System.out.println("TableData(): Table: " + tableName + ", Names: "
				+ this.columnNames + ", data: " + this.data + ".");
		System.out.println("TableData(): Table: " + tableName + ", Model: "
				+ ((JTable) this).getModel());
		*/
		// Set the column model for the table
		this.setColumnModel(new XTableColumnModel());
		this.createDefaultColumnsFromModel();
		
		// Set the column header renderer to be able to show different colors
		// when column is involved in a transformation.
		for (int i = 0; i < this.getColumnCount(); i++) {
			this.getTableHeader().getColumnModel().getColumn(i)
					.setHeaderRenderer(
							new TableHeaderCellRenderer(this.getColumnCount()));
		}


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#toString()
	 */
	@Override
	public String toString() {
		return tableName;
	}

	public void addTransformation(Transformation trans) {
		this.transformations.add(trans);
		// System.out.println("addTransformation(): Added transformation: " +
		// trans);
	}

	public void clearTransformations() {
		this.transformations.clear();
		// System.out.println("cleatTransformations(): Cleared all
		// transformations");
	}

	public Iterator<Transformation> getTransformations() {
		return this.transformations.iterator();
	}

	/**
	 * @return the column index of the column name
	 */
	public int getColumnId(String columnName) {
		return columnNames.indexOf(columnName);
	}

	/**
	 * Sets the width for the column.
	 * 
	 * @param columnName
	 * @param columnWidth
	 */
	public void setColumnWidth(String columnName, int columnWidth) {
		int columnIndex = getColumnId(columnName);
		TableColumnModel colModel = this.getColumnModel();
		colModel.getColumn(columnIndex).setPreferredWidth(columnWidth);
	}
	
	
	
}
