package parserDebugger;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTable;

/**
 * Output from the parser for one row in the database, including all the table
 * data (for each transformation step), and the list of the important columns.
 * 
 * @author eheitur
 * 
 */
public class ParsedRowData {
	private static final long serialVersionUID = 1L;

	private Vector<TableData> tables = null;
	private Vector<String> importantColumns = null;

	/**
	 * Constructor.
	 * 
	 * @param importantColumns
	 */
	public ParsedRowData(Vector<String> importantColumns) {
		tables = new Vector<TableData>();
		this.importantColumns = new Vector<String>();
		this.importantColumns.addAll(importantColumns);
	}

	/**
	 * @return all the tables for this row.
	 */
	public Vector<TableData> getAllTables() {
		return tables;
	}

	/**
	 * Adds the table and its transformations to the component. The new table
	 * will have the important columns first in the same order and the rest of
	 * the columns in alphabetical order after the important ones.
	 * 
	 * @param tableMap a hash map of the table columns and data
	 * @return the new table
	 */
	public TableData addTable(HashMap<String, Object> tableMap) {

		Vector<String> columnNames = new Vector<String>();
		Vector<String> unImportantColNames = new Vector<String>();
		Vector<Object> data = new Vector<Object>();

		// Iterate through the important columns vector. If the important column
		// is found from the input hash map, then that column name is added to
		// the
		// important column name vector for the new table.
		Iterator<String> impColIter = importantColumns.iterator();
		String currentColumn = null;
		while (impColIter.hasNext()) {
			currentColumn = impColIter.next();
			//if (tableMap.containsKey(currentColumn)) {
				// System.out.println("Important column: " + currentColumn
				// + " found.");
				columnNames.add(currentColumn);
			//}
		}

		// Iterate through the whole table hash map get unimportant column names
		// to a different vector.
		Iterator<String> mapIter = tableMap.keySet().iterator();
		while (mapIter.hasNext()) {
			String key = mapIter.next();
			if (!importantColumns.contains(key)) {
				// System.out.println("Unimportant column: " + key
				// + " found.");
				unImportantColNames.add(key);
			}
		}

		// Sort the unimportant column names alphabetically
		Collections.sort(unImportantColNames, Collator.getInstance());

		// Add the sorted unimportant column names to the end of the important
		// column names.
		columnNames.addAll(unImportantColNames);

		// Create the data vector by iterating through the column names
		Iterator<String> nameIter = columnNames.iterator();
		String currentName = null;
		while (nameIter.hasNext()) {
			currentName = nameIter.next();
			data.add(tableMap.get(currentName));
		}

		// Create the table
		Vector<Vector<Object>> tmpVect = new Vector<Vector<Object>>();
		tmpVect.add(data);
		TableData newTable = new TableData(columnNames, tmpVect);
		
		// Prevent automatic resizing of the tables
		newTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// Store the table
		tables.add(newTable);

		return newTable;
	}

	/**
	 * Sets the important columns for this row. The important columns will be
	 * displayed first (in order) and the other columns after the important ones
	 * in alphabetical order.
	 * 
	 * @param columns
	 */
	public void setImportantColumns(Vector<String> columns) {
		this.importantColumns.addAll(columns);
	}

}
