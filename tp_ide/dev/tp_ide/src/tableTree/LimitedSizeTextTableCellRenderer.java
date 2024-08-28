package tableTree;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

//import tableTreeUtils.LimitedSizeTextField;
//import tableTreeUtils.LimitedSizeTextField.RequiredWatch;

/**
 * Table cell renderer for text fields with limited size.
 * 
 * @author eheitur
 * 
 */
public class LimitedSizeTextTableCellRenderer implements TableCellRenderer {

//    private int columns;
    private int limit;
    private boolean required;

    /**
     * @param columns
     * @param limit
     * @param required
     */
    public LimitedSizeTextTableCellRenderer(int columns, int limit,
	    boolean required) {
	super();
//	this.columns = columns;
	this.limit = limit;
	this.required = required;
    }

    /**
     * Method for getting the renderer component. Creates component and sets the
     * text value shown.
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return the renderer component
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {

	// The actualColumn represents the column index in the table model, not
	// the view
	int actualColumn = ((TTTableModel) table.getModel()).getTableInfo()
		.getOriginalColumnIndexOfColumnName(
			table.getColumnModel().getColumn(column)
				.getHeaderValue().toString());

	String text = "";
	if (value != null) {
	    text = (String) value;
	}

	// LimitedSizeTextField retComp = new LimitedSizeTextField(columns,
	// limit,
	// required);
	JTextField retComp = new JTextField();
	retComp.setText(text);

	// The coloring of the cell depends on the row selection, table type
	// filtering and table filtering.
	if (isSelected) {
	    // Cell is selected
	    retComp.setForeground(table.getSelectionForeground());
	    retComp.setBackground(table.getSelectionBackground());
	} else if (((TTTableModel) table.getModel())
		.isColumnFilteredForTableType(actualColumn)) {
	    // Cell is not selected, but the column is filtered in table type
	    // level.
	    retComp.setForeground(table.getForeground());
	    retComp
		    .setBackground(GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR);
	} else if (((TTTableModel) table.getModel())
		.isColumnFiltered(actualColumn)) {
	    // Cell is not selected, but the column is filtered
	    retComp.setForeground(table.getForeground());
	    retComp.setBackground(GlobalTableTreeConstants.FILTERED_CELL_COLOR);
	    // this.setBackground(TableTreeConstants.FILTERED_CELL_BACKGROUND_COLOR);
	} else {
	    // Cell is not selected and column is not filtered
	    retComp.setForeground(table.getForeground());
	    retComp.setBackground(table.getBackground());
	}

	// Validate the length
	if (required && (text.length() <= 0)) {
	    retComp.setBackground(GlobalTableTreeConstants.ERROR_BG);
	    retComp.setToolTipText("Empty value is not allowed.");

	} else if (text.length() > limit) {
	    retComp.setBackground(GlobalTableTreeConstants.ERROR_BG);
	    retComp.setToolTipText("Current length " + text.length()
		    + " is over the maximum of " + limit + " characters.");
	} else {
	    retComp.setToolTipText(null);
	}

	return retComp;
    }
}
