package tableTree;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 * This is a default cell renderer for the tables. The background of the cell is
 * colored based row selection and filtering of the column.
 * 
 * @author eheitur
 * 
 */
public class TTDefaultTableCellRenderer extends JTextField implements
	TableCellRenderer {

    private static final long serialVersionUID = 1L;

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
	} else {
	    // Cell is not selected and column is not filtered
	    setForeground(table.getForeground());
	    setBackground(table.getBackground());

	    this.setForeground(table.getForeground());
	    this.setBackground(table.getBackground());
	}
	this.setBorder(null);
	if (value != null) {
	    this.setText(value.toString());
	} else {
	    this.setText("");
	}
	return this;

    }

}
