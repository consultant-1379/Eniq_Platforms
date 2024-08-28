package tableTree;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import tableTreeUtils.ShowOnlyDescriptionComponent;

/**
 * This class is a customized table cell renderer for table columns of type
 * DescriptionComponent. It is used e.g. in the MeasurementTypeKeyTableModel for
 * the Description column.
 * 
 * @author eheitur
 */
public class ShowOnlyCellRenderer implements TableCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor, currently empty
     */
    public ShowOnlyCellRenderer() {
    }

    /**
     * Method for getting the renderer component. Sets the cell coloring and
     * the text value shown in the description components text box.
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

	// Create a new description component with the string value and the
	// number of characters in the text field. NOTE: The number of
	// characters does not affect anything when used in the table cell. That
	// is why a zero is used.
	ShowOnlyDescriptionComponent retComp = new ShowOnlyDescriptionComponent((String) value,
		0);

	// The colouring of the cell depends on the row selection, table type
	// filtering and table filtering.
	if (isSelected) {
	    retComp.setColors(table.getSelectionBackground(), table
		    .getSelectionForeground(), table.getSelectionBackground(),
		    table.getSelectionForeground());
	} else if (((TTTableModel) table.getModel())
		.isColumnFilteredForTableType(actualColumn)) {
	    retComp.setColors(table.getBackground(), table.getForeground(),
		    GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR,
		    table.getForeground());
	} else if (((TTTableModel) table.getModel())
		.isColumnFiltered(actualColumn)) {
	    retComp.setColors(table.getBackground(), table.getForeground(),
		    GlobalTableTreeConstants.FILTERED_CELL_COLOR, table
			    .getForeground());
	} else {
	    retComp.setColors(table.getBackground(), table.getForeground(),
		    table.getBackground(), table.getForeground());
	}

	return retComp;
    }
}
