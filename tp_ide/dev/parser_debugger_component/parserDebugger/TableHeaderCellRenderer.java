package parserDebugger;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This class is used to replace the default table header cell renderer, so that
 * and different coloring can be added according to if the column is involved in
 * a transformation.
 * 
 * @author eheitur
 */

public class TableHeaderCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The colors for the normal and filtered table header cells
	Color normalBackground = SystemColor.control;
	Color normalForeground = Color.BLACK;
	Color transformationBackground = new Color(0x00, 0xEB, 0x00); // "EB-Green"
	Color importantForeground = Color.BLUE;

	/**
	 * Constructor.
	 * 
	 * @param columnCount
	 */
	public TableHeaderCellRenderer(int columnCount) {
		super();
	}

	/**
	 * Method for getting the renderer component. Sets the selected item and the
	 * coloring according to the table.
	 * 
	 * @param table
	 *            the table to be rendered
	 * @param value
	 *            the actual cell that is being rendered
	 * @param isSelected
	 *            true if the cell is selected
	 * @param hasFocus
	 *            true if the cell has focus
	 * @param row
	 *            the row being rendered
	 * @param column
	 *            the column being rendered
	 * 
	 * @return component to be used to render the cell
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component comp = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		// Get the component from tables component hierarchy: JTable (this) -->
		// JViewport --> JScrollpane --> JPanel (tablePane) --> JPanel
		// (bottomPane) --> JViewport --> JScrollpane -->
		// ParserDebuggerComponent
		ParserDebuggerComponent pdc = (ParserDebuggerComponent) table
				.getParent().getParent().getParent().getParent().getParent()
				.getParent().getParent();

		// TODO: Move the isColumnInTransformation method from the component to
		// the TableData.
		
		// Check if the column is involved in a transformation and set the
		// colors accordingly.
		if (pdc.isColumnInTransformation((TableData) table, table
				.getColumnName(column))) {
			// System.out.println("Column:" + table.getColumnName(column)
			// + " is in trans!");
			comp.setBackground(transformationBackground);
		} else {
			// System.out.println("Column:" + table.getColumnName(column)
			// + " not in trans!");
			comp.setBackground(normalBackground);
		}

		// Update the font based on if the column is important or not
		if (pdc.isColumnImportant(table
				.getColumnName(column))){
			comp.setForeground(importantForeground);
		} else {
			comp.setForeground(normalForeground);
		}
		
		// Set the border for the header cell
		((JLabel) comp).setBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED));

		return comp;
	}
}
