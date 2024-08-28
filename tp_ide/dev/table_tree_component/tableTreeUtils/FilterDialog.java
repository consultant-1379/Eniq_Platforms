/**
 * 
 */
package tableTreeUtils;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import tableTree.TTTableModel;
import tableTree.TableTreeComponent;

/**
 * Used for displaying a filter dialog window for modifying table column filters
 * for all the tables of the same type.
 * 
 * @author eheitur
 * 
 */
public class FilterDialog implements ActionListener {

    /**
     * Action: Set filter
     */
    public static final String SET_FILTER = "Set Column Filter";

    /**
     * Action: Edit filter
     */
    public static final String EDIT_FILTER_TEXT = "Edit Filter Text";

    /**
     * Action: Clear filter
     */
    public static final String CLEAR_FILTER = "Clear Column Filter";

    /**
     * Action: Clear all filters
     */
    public static final String CLEAR_ALL_FILTERS = "Clear Column Filters";

    /**
     * Action: Close
     */
    public static final String CLOSE = "Close";

    /**
     * The filter frame that is displayed when the filter action is triggered.
     */
    private JFrame filterFrame = null;

    /*
     * The components for the filter frame
     */
    protected JPanel panel = null;
    protected JComboBox tableCombo = null;
    protected JComboBox columnCombo = null;
    protected JTextField filterTextField = null;
    protected JButton clearButton = null;
    protected JButton setButton = null;
    protected JButton closeButton = null;

    /*
     * Selected table and column + the active filter for the table type.
     */
    protected int selectedTable = 0;
    protected int selectedColumn = 0;
    protected String activeFilter = null;

    /**
     * The table informations from the factory
     */
    private Vector<TableInformation> thisTableInfo = null;

    /**
     * The table tree component from the factory
     */
    private TableTreeComponent thisTree = null;

    /**
     * Constructor. Initializes the member variables and creates the filter
     * dialog frame.
     * 
     * @param inTree
     *                The table tree component
     * @param inTableInfo
     *                The list of table informations
     */
    public FilterDialog(TableTreeComponent inTree,
	    Vector<TableInformation> inTableInfo) {
	thisTree = inTree;
	thisTableInfo = inTableInfo;
	filterFrame = createFrame();
    }

    /**
     * Callback method for action events, i.e. for when one of the menu
     * alternatives have been selected.
     * 
     * @param ae
     *                action event
     */

    public void actionPerformed(ActionEvent ae) {

	if (ae.getActionCommand().equals(SET_FILTER)) {
	    setFilter();
	} else if (ae.getActionCommand().equals(EDIT_FILTER_TEXT)) {
	    setFilter();
	} else if (ae.getActionCommand().equals(CLEAR_FILTER)) {
	    clearFilter();
	} else if (ae.getActionCommand().equals(CLEAR_ALL_FILTERS)) {
	    clearAllTypeFilters();
	} else if (ae.getActionCommand().equals(CLOSE)) {
	    close();
	} else if (ae.getActionCommand().equals("comboBoxChanged")) {
	    updateFilterFrame(ae.getSource());
	} else {
	    System.out.println(this.getClass()
		    + " actionPerformed(): Received invalid action command: "
		    + ae.getActionCommand());
	}
    }

    /**
     * Helper method to create the filter dialog frame.
     * 
     * @return
     */
    private JFrame createFrame() {

	// Create frame
	JFrame thisFrame = new JFrame();
	thisFrame.setAlwaysOnTop(true);

	// Create the panel and add it to the frame
	panel = new JPanel(new GridLayout(2, 0));
	thisFrame.add(panel);

	// Get the table types
	Vector<String> tableTypes = new Vector<String>();
	for (int i = 0; i < thisTableInfo.size(); i++) {
	    tableTypes.add(thisTableInfo.elementAt(i).getType());
	}

	// Initialise the components
	//
	tableCombo = new JComboBox(tableTypes);
	selectedTable = 0;
	tableCombo.setSelectedIndex(selectedTable);
	tableCombo.addActionListener(this);
	panel.add(tableCombo);

	columnCombo = new JComboBox(thisTableInfo.elementAt(selectedTable)
		.getColumnNamesInOriginalOrder());
	selectedColumn = 0;
	columnCombo.setSelectedIndex(selectedColumn);
	columnCombo.addActionListener(this);
	panel.add(columnCombo);

	filterTextField = new JTextField(thisTableInfo.elementAt(selectedTable)
		.getTableTypeColumnFilter(selectedColumn));
	filterTextField.setActionCommand(FilterDialog.EDIT_FILTER_TEXT);
	filterTextField.addActionListener(this);
	panel.add(filterTextField);

	setButton = new JButton(FilterDialog.SET_FILTER);
	setButton.setActionCommand(FilterDialog.SET_FILTER);
	setButton.addActionListener(this);
	panel.add(setButton);

	clearButton = new JButton(FilterDialog.CLEAR_FILTER);
	clearButton.setActionCommand(FilterDialog.CLEAR_FILTER);
	clearButton.addActionListener(this);
	panel.add(clearButton);

	closeButton = new JButton(FilterDialog.CLOSE);
	closeButton.setActionCommand(FilterDialog.CLOSE);
	closeButton.addActionListener(this);
	panel.add(closeButton);

	thisFrame.add(panel);

	// Pack and return the frame.
	thisFrame.setTitle("Set Filters for a Table Type");
	thisFrame.pack();
	return thisFrame;
    }

    /**
     * Shows the filter dialog. The previously selected table and column, and
     * the active filter are shown by default.
     */
    protected JFrame showDialog() {

	// Store selected column locally, because changing the table
	// combo box selection will cause the selectedColumn to change.
	int currentColumn = selectedColumn;

	// Set the table selection based on the earlier selection
	tableCombo.setSelectedIndex(selectedTable);

	// Refresh the contents of the column combo box.
	DefaultComboBoxModel model = new DefaultComboBoxModel(thisTableInfo
		.elementAt(selectedTable).getColumnNamesInOriginalOrder());
	columnCombo.setModel(model);

	// Set the column selection based on the earlier selection
	columnCombo.setSelectedIndex(currentColumn);

	// Refresh the filter text field.
	activeFilter = thisTableInfo.elementAt(selectedTable)
		.getTableTypeColumnFilter(currentColumn);
	filterTextField.setText(activeFilter);

	// Show the dialog
	filterFrame.setVisible(true);

	return filterFrame;
    }

    /**
     * Callback method for clearing all the table type level filters.
     */
    public void clearAllTypeFilters() {

	selectedTable = 0;
	selectedColumn = 0;
	activeFilter = "";

	// Go through the table types in table informations vector and clear the
	// column filters.
	for (int i = 0; i < thisTableInfo.size(); i++) {
	    thisTableInfo.elementAt(i).clearAllTableTypeColumnFilters();
	}

	// Update the tables according to the new filtering
	updateAllTables();
    }

    /**
     * Callback method for setting a table type level filter.
     */
    protected void setFilter() {
	// Update the active filter;
	activeFilter = filterTextField.getText();

	// Set the filter
	thisTableInfo.elementAt(selectedTable).setTableTypeColumnFilter(
		selectedColumn, activeFilter);

	// Update the tables according to the new filtering
	updateAllTables();
    }

    /**
     * Callback method for setting the table type level filters.
     */
    protected void clearFilter() {
	// Update the active filter;
	activeFilter = "";

	// Clear the filter
	thisTableInfo.elementAt(selectedTable).clearTableTypeColumnFilter(
		selectedColumn);

	// Refresh the filter text field.
	filterTextField.setText(activeFilter);

	// Update the tables according to the new filtering
	updateAllTables();
    }

    /**
     * Callback method for closing the dialog.
     */
    protected void close() {
	// Hide and dispose the frame.
	filterFrame.setVisible(false);
	filterFrame.dispose();
    }

    /**
     * Callback method for updating the contents in the filter frame components
     * when e.g. a combo box selection changes.
     * 
     * @param source
     *                component which caused the event
     */
    protected void updateFilterFrame(Object source) {
	if (source instanceof JComboBox) {
	    if (source.equals(tableCombo)) {
		// Set the table selection
		selectedTable = tableCombo.getSelectedIndex();

		// Refresh the contents of the column combo box.
		// columnCombo.removeAllItems();
		DefaultComboBoxModel model = new DefaultComboBoxModel(
			thisTableInfo.elementAt(selectedTable)
				.getColumnNamesInOriginalOrder());
		columnCombo.setModel(model);

		// Reset the column selection
		selectedColumn = 0;

		// Refresh the filter text field.
		activeFilter = thisTableInfo.elementAt(selectedTable)
			.getTableTypeColumnFilter(selectedColumn);
		filterTextField.setText(activeFilter);

	    } else if (source.equals(columnCombo)) {
		// Set the column selection
		selectedColumn = columnCombo.getSelectedIndex();

		// Refresh the filter text field.
		activeFilter = thisTableInfo.elementAt(selectedTable)
			.getTableTypeColumnFilter(selectedColumn);
		filterTextField.setText(activeFilter);
	    }

	} else if (source instanceof JTextField) {

	    // Debug:
	    // System.out.println("FilterTextField changed.");

	} else {
	    // Debug:
	    System.out
		    .println("updateFilterFrame(): Unexpected change event from source: "
			    + source);
	}
    }

    /**
     * Used for updating all the tables in the tree after modifying the filters
     * for the table types. Each table is filtered again the size is tuned.
     */
    protected void updateAllTables() {
	if (thisTree != null) {
	    // Collect the table containers
	    Vector<TableContainer> cont = thisTree.collectTableContainers();

	    // Loop through each table container
	    for (int i = 0; i < cont.size(); i++) {
		// Get the table and the model
		JTable table = ((TableContainer) cont.elementAt(i)).getTable();
		TTTableModel model = (TTTableModel) table.getModel();

		// Clear all the column filters for the table.
		model.filterTable();

		// Update the table size and the tree
		((TableContainer) cont.elementAt(i)).tuneSize();
	    }
	}
    }

}
