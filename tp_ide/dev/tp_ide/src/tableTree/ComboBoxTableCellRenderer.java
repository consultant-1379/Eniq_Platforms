package tableTree;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Table cell renderer for combo boxes.
 * 
 * @author ejeahei enaland eheitur
 * 
 */
public class ComboBoxTableCellRenderer extends JComboBox implements
	TableCellRenderer {

    private boolean required;

    private static final long serialVersionUID = -434957203118072023L;

    /**
     * Constructor for initializing the combo box with items
     * 
     * @param items
     */
    public ComboBoxTableCellRenderer(Object[] items) {
	super(items);
	this.required = false;
    }

    /**
     * Constructor for initializing the combo box with items and required flag.
     * 
     * @param items
     * @param required
     */
    public ComboBoxTableCellRenderer(Object[] items, boolean required) {
	super(items);
	this.required = required;
    }

    /**
     * Method for getting the renderer component. Sets the selected item and the
     * coloring according to the table.
     * 
     * @param table
     *                the table to be rendered
     * @param value
     *                the actual cell that is being rendered
     * @param isSelected
     *                true if the cell is selected
     * @param hasFocus
     *                true if the cell has focus
     * @param row
     *                the row being rendered
     * @param column
     *                the column being rendered
     * 
     * @return component to be used to render the cell
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {

	int actualColumn = ((TTTableModel) table.getModel()).getTableInfo()
		.getOriginalColumnIndexOfColumnName(
			table.getColumnModel().getColumn(column)
				.getHeaderValue().toString());

	if (isSelected) {
	    // Cell is selected
	    setForeground(table.getSelectionForeground());
	    super.setBackground(table.getSelectionBackground());

	    this.setForeground(table.getSelectionForeground());
	    this.setBackground(table.getSelectionBackground());
	} else if (((TTTableModel) table.getModel())
		.isColumnFilteredForTableType(actualColumn)) {
	    // Cell is not selected, but the column is filtered in table type
	    // level.
	    setForeground(table.getForeground());
	    setBackground(table.getBackground());

	    this.setForeground(table.getForeground());
	    this
		    .setBackground(GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR);
	} else if (((TTTableModel) table.getModel())
		.isColumnFiltered(actualColumn)) {
	    // Cell is not selected, but the column is filtered
	    setForeground(table.getForeground());
	    setBackground(table.getBackground());

	    this.setForeground(table.getForeground());
	    this.setBackground(GlobalTableTreeConstants.FILTERED_CELL_COLOR);
	    // this.setBackground(TableTreeConstants.FILTERED_CELL_BACKGROUND_COLOR);
	} else {
	    // Cell is not selected and column is not filtered
	    setForeground(table.getForeground());
	    setBackground(table.getBackground());

	    this.setForeground(table.getForeground());
	    this.setBackground(table.getBackground());

	}

	// Set the selected item in the list. The value can be null in the
	// database, so the selection match against the combo box items is
	// checked first.
	this.setSelectedIndex(-1);
	for (int ind = 0; ind < this.getItemCount(); ind++) {
	    if (this.getItemAt(ind).equals(value)) {
		setSelectedItem(value);
		break;
	    }
	}

	// Validate the value. If the combo box is required to have a value, but
	// none is selected, then indicate an error.
	if (required && (this.getSelectedIndex()==-1)) {
	    this.setBackground(GlobalTableTreeConstants.ERROR_BG);
	    this.setToolTipText("Empty value is not allowed.");

	} else {
	    this.setToolTipText(null);
	}

	return this;
    }
}
