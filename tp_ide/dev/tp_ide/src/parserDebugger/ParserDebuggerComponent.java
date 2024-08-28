package parserDebugger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * This class is used as a component for visually representing database table
 * rows before and after a transformation actions made by a parser. Affected
 * columns for each transformation are shown. It is also possible to define
 * which column are the important ones (shown first).
 * 
 * @author eheitur
 * 
 */
public class ParserDebuggerComponent extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	// Labels for the options, used also as action commands
	private static final String SHOW_ALL_COLUMNS = "Show all columns";
	private static final String ALWAYS_SHOW_IMPORTANT_COLUMNS = "Show only affected columns";

	/**
	 * Preferred height for tables. This is the height that each scroll pane
	 * will use so that there is enough space between the tables to draw the
	 * transformation connection lines.
	 */
	private static final int PREFERRED_TABLE_HEIGHT = 60;

	// A container, an iterator and a current object for a parsed database rows
	private Vector<ParsedRowData> parsedRowData = null;
	private Iterator<ParsedRowData> rowIter = null;
	private ParsedRowData currentRow = null;

	// An iterator and current object for a parsed database rows
	private Iterator<TableData> tableIter = null;
	private TableData currentTable = null;

	/**
	 * A container for the currently visible tables in the components table
	 * pane.
	 */
	private Vector<TableData> visibleTables = null;

	/**
	 * A container for the columns marked as important columns.
	 */
	private Vector<String> importantColumns = null;

	/**
	 * A container for transformations executed for each database row.
	 */
	private Vector<Transformation> transformations = null;

	// Container panes for drawing the graphical objects
	private JPanel topPane = null;
	private JPanel optionPane = null;
	private JPanel tablePane = null;
	private JPanel drawingPane = null;
	private JPanel bottomPane = null;

	/**
	 * Check box for showing all columns / hiding the ones not involved in any
	 * transformation.
	 */
	private JCheckBox cbShowAllColumns = null;

	/**
	 * Check box for showing important columns.
	 */
	private JCheckBox cbAlwaysShowImportantColumns = null;

	/**
	 * The list for showing all the transformations + highlighting the currently
	 * active one.
	 */
	private JList transformationList = null;

	/**
	 * The offset of the latest visible table in the table pane. Used for
	 * calculating the position for the next table.
	 */
	private int tableOffset = 0;
    
    
    private int tableSize = 0;
    private int rowSize = 0;

	/**
	 * Constructor.
	 */
	public ParserDebuggerComponent() {
		super(new BorderLayout());

		// Initialize the vectors
		parsedRowData = new Vector<ParsedRowData>();
		visibleTables = new Vector<TableData>();
		importantColumns = new Vector<String>();
		transformations = new Vector<Transformation>();

		// Content panes must be opaque
		setOpaque(true);

		// Create the top panel for showing the transformation list and the
		// options
		topPane = new JPanel(new GridLayout(0, 2));

		// Create the list of transformations
		DefaultListModel listModel = new DefaultListModel();
		Iterator<Transformation> iter = getTransformations();
		Transformation currentTrans = null;
		listModel.addElement("-");
		while (iter.hasNext()) {
			currentTrans = iter.next();
			listModel.addElement(currentTrans.getTransformationId());
		}
		transformationList = new JList(listModel);
		transformationList.setEnabled(false);
		JScrollPane listScroller = new JScrollPane(transformationList);
		listScroller.setPreferredSize(new Dimension(150, 70));

		// Create the option pane
		optionPane = new JPanel(new GridLayout(0, 1));

		cbShowAllColumns = new JCheckBox(SHOW_ALL_COLUMNS, false);
		cbShowAllColumns.setActionCommand(SHOW_ALL_COLUMNS);
		cbShowAllColumns.addActionListener(this);
		optionPane.add(cbShowAllColumns);

		cbAlwaysShowImportantColumns = new JCheckBox(
				ALWAYS_SHOW_IMPORTANT_COLUMNS, false);
		cbAlwaysShowImportantColumns
				.setActionCommand(ALWAYS_SHOW_IMPORTANT_COLUMNS);
		cbAlwaysShowImportantColumns.addActionListener(this);
		optionPane.add(cbAlwaysShowImportantColumns);
		//cbAlwaysShowImportantColumns.setEnabled(false);

		// Add the list pane and the option pane to the top pane.
		topPane.add(listScroller);
		topPane.add(optionPane);
		topPane.setPreferredSize(new Dimension(300, 80));
		topPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Add the top pane to the component
		this.add(topPane, BorderLayout.PAGE_START);

		// Create a bottomPane as a container for the table and drawing panes.
		// No layout manager will be set so the panes can be positioned on top
		// of each other. NOTE: This also means the panes will not automatically
		// resize when the component is resized
		bottomPane = new JPanel(null);

		// Create a scroll pane for the bottom pane. The preferred size is set,
		// so
		// the scroll bars are visible if the component is or gets larger.
		JScrollPane scrollPane = new JScrollPane(bottomPane);
		// scrollPane.getViewport().setPreferredSize(new Dimension(600, 600));
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x00,
				0xEB, 0x00)));

		// Create the drawing pane and add it to the bottom pane
		drawingPane = new ParserDebuggerDrawingPane(this);
		bottomPane.add(drawingPane);
		drawingPane.setLocation(0, 0);

		// Create the table pane and add it to the bottom pane
		tablePane = new JPanel();
		//tablePane.setBorder(BorderFactory.createLineBorder(Color.PINK));
		tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.Y_AXIS));
		bottomPane.add(tablePane);
		tablePane.setLocation(0, 0);

		// Add the bottom pane to the component
		// this.add(bottomPane, BorderLayout.CENTER);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(SHOW_ALL_COLUMNS)) {
			if (((JCheckBox) e.getSource()).isSelected()) {
				// Disable the "Always show important columns" option.
				cbAlwaysShowImportantColumns.setEnabled(false);
			} else {
				// Enable the "Always show important columns" option.
				cbAlwaysShowImportantColumns.setEnabled(true);
			}
		}
		// Show and hide columns based on the options
		showAndHideColumns(cbShowAllColumns.isSelected(),
				cbAlwaysShowImportantColumns.isSelected());
	}

	/**
	 * Adds a new transformation
	 * 
	 * @param transformationId
	 * @param fromColumns
	 * @param toColumns
	 */
	public void addTransformation(String transformationId,
			String[] fromColumns, String[] toColumns) {
		// Create a new transformation
		Transformation newTrans = new Transformation(transformationId,
				fromColumns, toColumns);

		// Store transformation
		transformations.add(newTrans);

		// Add new transformation to the transformation list.
		((DefaultListModel) transformationList.getModel()).addElement(newTrans
				.getTransformationId());
	}

    
	/**
	 * Removes the currently shown tables from the component.
	 */
	private void clearVisibletables() {
		// Clear the visible tables vector
		visibleTables.clear();

		// Remove the tables from the table pane
		Component[] comps = tablePane.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JScrollPane) {
				//System.out.println("Removing a table scrollpane:" + comps[i]);
				tablePane.remove(comps[i]);
			} else {
				//System.out.println("Not a scroll pane: " + comps[i]);
            }
		}

		// Clear the selection of the transformation list
		transformationList.clearSelection();

		// Clear the table offset used for table location in the layered pane.
		tableOffset = 0;

		// Validate the containers after removing the components
		drawingPane.validate();
		tablePane.validate();
		// Repaint the the table pane to get rid of the tables.
		tablePane.repaint();

	}

	/**
	 * Creates a new parsed row object.
	 * 
	 * @return the new parsed row object
	 */
	public ParsedRowData createRow() {

		//System.out.println("createRow()");

		ParsedRowData newRow = new ParsedRowData(importantColumns);
		parsedRowData.add(newRow);

		return newRow;
	}

    private void clearRow() {
        parsedRowData.clear();
    }
    
	/**
	 * Gets all transformations
	 * 
	 * @return an iterator of all transformations
	 */
	public Iterator<Transformation> getTransformations() {
		return transformations.iterator();
	}
    
    private void clearTransformations() {
        transformations.clear();
        ((DefaultListModel) transformationList.getModel()).removeAllElements();
        
    }

	/**
	 * Gets all tables that are currently visible in the component
	 * 
	 * @return visible tables
	 */
	public Iterator<TableData> getVisibleTables() {
		return visibleTables.iterator();
	}

	/**
	 * Returns true if the table column is involved in an incoming and/or
	 * outgoing transformation.
	 * 
	 * @param table
	 * @param columnName
	 * @return true when column is in a transformation
	 */
	public boolean isColumnInTransformation(TableData table, String columnName) {
		// Get table index in the tables vector
		for (int i = 0; i < visibleTables.size(); i++) {
			if (table.equals(visibleTables.elementAt(i))) {
				if (i > 0) {
					// We might have an incoming transformation to this table
					String[] toColumns = transformations.elementAt(i - 1)
							.getToColumns();
					for (int j = 0; j < toColumns.length; j++) {
						if (toColumns[j].equals(columnName)) {
							// System.out
							// .println("isColumnInTransformation(): "
							// + "Found incoming transformation match for "
							// + "column " + columnName + ".");
							return true;
						}
					}
				}
				if (i < visibleTables.size() - 1) {
					// We might have an outgoing transformation to this table
					String[] fromColumns = transformations.elementAt(i)
							.getFromColumns();
					for (int j = 0; j < fromColumns.length; j++) {
						if (fromColumns[j].equals(columnName)) {
							// System.out
							// .println("isColumnInTransformation(): "
							// + "Found outgoing transformation match for "
							// + "column " + columnName + ".");
							return true;
						}
					}

				}
			}
		}
		return false;
	}

	/**
	 * Shows all the tables for the last row
	 */
	public void moveEnd() {

		// Check if a row is already being processed or not.
		if (currentRow == null) {
			// This is the first step. Create a row iterator.
			rowIter = parsedRowData.iterator();
            rowSize = parsedRowData.size();
			// if (rowIter.hasNext()) {
			// currentRow = rowIter.next();
			// System.out.println("moveEnd(): First row: " + currentRow);
			// tableIter = currentRow.getAllTables();
			// // Move to the last table in the row
			// moveLast();
			// } else {
			// System.out.println("moveEnd(): No rows.");
			// return;
			// }
		} else {
			// A row is already being processed.
			// Move to the last table in the row
			moveLast();
		}

		// Loop through all the tables for this row and show them
		while (rowIter.hasNext()) {
			clearVisibletables();
			currentRow = rowIter.next();
            rowSize--;
			//System.out.println("moveEnd(): Next row: " + currentRow);
			tableIter = currentRow.getAllTables().iterator();
            tableSize = currentRow.getAllTables().size();
			moveLast();
		}
	}

	/**
	 * Shows all the tables for the current row.
	 */
	public void moveLast() {

		// Check if a row is already being processed or not.
		if (currentRow == null) {
			// This is the first step. Create a row iterator and move the the
			// first row and create a table iterator.
            rowIter = parsedRowData.iterator();
            rowSize = parsedRowData.size();
			if (rowIter.hasNext()) {
				currentRow = rowIter.next();
                rowSize--;
				//System.out.println("moveLast(): First row: " + currentRow);
                tableIter = currentRow.getAllTables().iterator();
                tableSize = currentRow.getAllTables().size();
			} else {
				System.out.println("moveLast(): No rows.");
				return;
			}
		} else {
			// A row is already being processed.
			// If there are no more tables left, then the next row is selected.
			if (!tableIter.hasNext()) {
				if (rowIter.hasNext()) {
					clearVisibletables();
					currentRow = rowIter.next();
                    rowSize--;
					//System.out.println("moveLast(): First row: " + currentRow);
                    tableIter = currentRow.getAllTables().iterator();
                    tableSize = currentRow.getAllTables().size();
				} else {
					//System.out.println("moveLast(): No more rows.");
					return;
				}
			}
		}

		// Loop through all the tables for this row and show them
		while (tableIter.hasNext()) {
			moveNext();
		}
	}

	/**
	 * Shows the next available table in the component.
	 */
	public void moveNext() {

		// Check if a row is already being processed or not.
		if (currentRow != null) {
			// We are handling a row already.
			// Move to the next table, or if this is the last table then move to
			// the next row and the first table there.
			if (tableIter.hasNext()) {
				currentTable = tableIter.next();
				//System.out.println("moveNext(): Next table: " + currentTable);
			} else {
				//System.out.println("moveNext(): No more tables in the row.");
				if (rowIter.hasNext()) {
					clearVisibletables();
					currentRow = rowIter.next();
                    rowSize--;
					System.out.println("moveNext(): Next row: " + currentRow);
                    tableIter = currentRow.getAllTables().iterator();
                    tableSize = currentRow.getAllTables().size();
					if (tableIter.hasNext()) {
						currentTable = tableIter.next();
						System.out.println("moveNext(): Next table: "
								+ currentTable);
					} else {
						//System.out.println("moveNext(): No tables for this row.");
						return;
					}

				} else {
					// No more rows
					//System.out.println("moveNext(): No more rows.");
					return;
				}
			}
		} else {
			// This is the first step. Create a row iterator and move the the
			// first row and table.
			clearVisibletables();
            rowIter = parsedRowData.iterator();
            rowSize = parsedRowData.size();
			if (rowIter.hasNext()) {
				currentRow = rowIter.next();
                rowSize--;
				System.out.println("moveNext(): First row: " + currentRow);
                tableIter = currentRow.getAllTables().iterator();
                tableSize = currentRow.getAllTables().size();
				if (tableIter.hasNext()) {
					currentTable = tableIter.next();
					//System.out.println("moveNext(): First table: "+ currentTable);
				} else {
					//System.out.println("moveNext(): No tables for this row.");
					return;
				}
			} else {
				//System.out.println("moveNext(): No rows.");
				return;
			}
		}

		// Now the row and table iterators are set, so the table is shown in the
		// frame.

		if (visibleTables.contains(currentTable)) {
			// System.out.println("Found");
		} else {
			// System.out.println("Not found. Adding.");
			visibleTables.add(currentTable);

			// Add a listener to the table to catch column resize events
			currentTable.getColumnModel().addColumnModelListener(
					new TableColumnModelListener() {

						public void columnMarginChanged(ChangeEvent e) {
							refreshAll();
						}

						public void columnSelectionChanged(ListSelectionEvent e) {
						}

						public void columnAdded(TableColumnModelEvent e) {
						}

						public void columnMoved(TableColumnModelEvent e) {
							refreshAll();
						}

						public void columnRemoved(TableColumnModelEvent e) {
						}

						private void refreshAll() {
							// Update the sizes for the table and drawing panes
							updatePaneSizes();
							// Validate the scroll pane for the bottom pane to
							// get the scroll bar visible in case the resizing
							// of the table grows bigger than the current width.
							bottomPane.getParent().getParent().validate();
							// Redraw the component after showing/hiding
							// columns.
							redrawComponent();
						}
					});
			// Create scroll panes and add the tables to them.
			JScrollPane scrollPane = new JScrollPane(currentTable);
			scrollPane.setPreferredSize(new Dimension(currentTable
					.getPreferredSize().width, PREFERRED_TABLE_HEIGHT));
			scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());

			// Add the scroll pane to the table pane.
			scrollPane.setBounds(0, this.tableOffset, scrollPane
					.getPreferredSize().width,
					scrollPane.getPreferredSize().height);
			this.tableOffset = this.tableOffset + 80;
			tablePane.add(scrollPane);

		}

		// Validate the container after adding a component
		tablePane.validate();

		// Temporarily show all columns, because some hidden columns in the
		// visible tables might be needed due to the new table and
		// transformation.
		showAndHideColumns(true, true);

		// Update the transformation list selection
		transformationList.setSelectedIndex(transformationList
				.getSelectedIndex() + 1);
		transformationList.ensureIndexIsVisible(transformationList
				.getSelectedIndex());

		// Update the sizes for the table and drawing panes
		updatePaneSizes();

		// Update the shown/hidden columns based on the show all columns option.
		showAndHideColumns(cbShowAllColumns.isSelected(),
				cbAlwaysShowImportantColumns.isSelected());

	}

	/**
	 * Resets the current iteration for rows and tables. The current shown
	 * tables are removed from the component.
	 */
	public void moveReset() {
		clearVisibletables();
		rowIter = null;
		currentRow = null;
		tableIter = null;
		currentTable = null;
        tableSize = 0;
        rowSize = 0;
		updatePaneSizes();
	}
    
    public void dataReset() {
      moveReset();
      this.clearImportantColumns();
      this.clearTransformations();
      this.clearRow();
      
    }

	public void redrawComponent() {
		// System.out.println("TablePane preferred size: "
		// + tablePane.getPreferredSize());

		tablePane.validate();
		((ParserDebuggerDrawingPane) drawingPane).reCalculateRoutes();
	}

	/**
	 * Sets the important columns for parsing the rows. These columns will be
	 * shown first (in order) before all the other columns in the parser input
	 * data.
	 * 
	 * @param columns
	 */
	public void setImportantColumns(Vector<String> columns) {
		this.importantColumns.addAll(columns);
	}

    private void clearImportantColumns() {
        this.importantColumns.clear();
    }
    
	/**
	 * @param showAll
	 * @param alwaysShowImportant
	 */
	private void showAndHideColumns(boolean showAll, boolean alwaysShowImportant) {

		Iterator<TableData> iter = visibleTables.iterator();
		TableData currentTable = null;
		XTableColumnModel currentModel = null;
		while (iter.hasNext()) {
			currentTable = iter.next();
			currentModel = ((XTableColumnModel) currentTable.getColumnModel());

            /*
			System.out.println("showAndHideColumns(): Current table: "
					+ currentTable + " with "
					+ currentModel.getColumnCount(false) + " total and "
					+ currentModel.getColumnCount(true) + " visible columns.");
             */
			// Show all columns
			((XTableColumnModel) currentTable.getColumnModel())
					.setAllColumnsVisible();

			// Check if all columns should be shown or not
			if (!showAll) {
				// Hide columns not involved in any transformations. Loop
				// through the visible columns in descending order to avoid
				// invalid column indexes when columns are hidden (=removed)
				// between iterations.
				for (int i = currentModel.getColumnCount(true) - 1; i >= 0; i--) {
					// Get column identifier matching the index (of all columns)
					String columnId = (String) currentModel.getColumn(i, true)
							.getIdentifier();

                    /*
					System.out.println("showAndHideColumns(): Current table: "
							+ currentTable + " with "
							+ currentModel.getColumnCount(false)
							+ " total and " + currentModel.getColumnCount(true)
							+ " visible columns: " + columnId + " index "
							+ currentModel.getColumnIndex(columnId, false)
							+ " (total), "
							+ currentModel.getColumnIndex(columnId, true)
							+ " (visible)");
                    */
					// Check is the current column identifier for this table is
					// involved in any transformation
					if (!isColumnInTransformation(currentTable, columnId)) {
						// The column is not included in any transformation, so
						// it must be checked if the column is important.
						if (!isColumnImportant(columnId)
								|| alwaysShowImportant) {
							// Not important or important will not always be
							// shown. Hide it.
                          /*
							System.out
									.println("showAndHideColumns(): Current table: "
											+ currentTable
											+ ", column: "
											+ columnId
											+ " not in transformation and "
											+ "not shown as important: Hide");
                           */
							// Get column index (from visible columns)
							int columnIndex = currentModel.getColumnIndex(
									columnId, true);
							// Set the column visible to false
							currentModel.setColumnVisible(currentModel
									.getColumn(columnIndex, true), false);
						} else {
							// The column is important and important columns are
							// always shown.
                          /*
							System.out
									.println("showAndHideColumns(): Current table: "
											+ currentTable
											+ ", column: "
											+ columnId
											+ " is not is trans, but is "
											+ "important: Show");
                                            
                          */
						}
					} else {
						// The column is included in a transformation.
                      /*
						System.out
								.println("showAndHideColumns(): Current table: "
										+ currentTable
										+ ", column: "
										+ columnId
										+ " is in transformation: Show");
                     */
					}
				}
			}

			// Check if there are zero columns to be shown (due to no columns
			// involved in transformations). If yes, then show all columns to
			// avoid zero column table.
			if (currentModel.getColumnCount(true) == 0) {
				// Show all columns
              /*
				System.out
						.println("showAndHideColumns(): All columns would be "
								+ "hidden. Showing all columns!");
              */
				((XTableColumnModel) currentTable.getColumnModel())
						.setAllColumnsVisible();
			}

		}

		// Table pane must be validated here. Otherwise there are problems when
		// adding tables to the pane or clearing it.
		tablePane.validate();

		// The pane sizes are recalculated
		updatePaneSizes();

		// Redraw the component after showing/hiding columns.
		redrawComponent();

	}

	/**
	 * Since the bottom pane in the component has no layout manager, the sizes
	 * for the drawing and table panes must be manually updated. This method
	 * calculates the sizes based on the needed space for the currently visible
	 * tables.
	 */
	private void updatePaneSizes() {
		// Get maximum table width
		int maxWidth = 0;
		Component[] comps = tablePane.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JScrollPane) {

				// Get the width of the table in the scroll pane
				TableData table = (TableData) ((JViewport) ((JScrollPane) comps[i])
						.getComponent(0)).getView();
                /*
				System.out.println("Scrollpane prefSize: "
						+ comps[i].getPreferredSize().width + ", Table: "
						+ table + ", prefWidth: "
						+ table.getPreferredSize().width + ", prefCVPWidth: "
						+ table.getPreferredScrollableViewportSize().width);
                        
               */
				if (table.getPreferredSize().width > maxWidth) {
					maxWidth = table.getPreferredSize().width;
				}
			}
		}
		// Calculate the height of the visible tables based on the table offset
		int maxHeight = tableOffset + 50;

		//System.out.println("Setting table and drawing pane pref size: "+ maxWidth + ", height: " + maxHeight);
		tablePane.setBounds(0, 0, maxWidth, maxHeight);
		drawingPane.setBounds(0, 0, maxWidth, maxHeight);
		bottomPane.setPreferredSize(new Dimension(maxWidth, maxHeight));
	}

	/**
	 * Returns true if the column is an important column.
	 * 
	 * @param column
	 * @return true if the column is an important column
	 */
	boolean isColumnImportant(String column) {
		Iterator<String> colIter = this.importantColumns.iterator();
		while (colIter.hasNext()) {
			if (colIter.next().equals(column))
				return true;
		}
		return false;
	}

  public int getTableSize() {
    return tableSize;
  }

  public int getRowSize() {
    return rowSize;
  }
  
}
