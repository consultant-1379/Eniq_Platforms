package tableTreeUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.ericsson.eniq.techpacksdk.TechPackIDE;

import tableTree.GlobalTableTreeConstants;
import tableTree.TTTableModel;
import tableTree.TTTableModel.availableSortOrders;

/**
 * This class is used to replace the default table header cell renderer, so that icons for sorting
 * and different colorings can be added
 * 
 * @author enaland
 */

public class TableHeaderCellRenderer extends DefaultTableCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(TableHeaderCellRenderer.class.getName());

    // The colors for the normal and filtered table header cells
    Color normalBackground = SystemColor.control;
    Color normalForeground = Color.BLACK;
    Color filteredBackground = GlobalTableTreeConstants.FILTERED_HEADER_COLOR;
    Color filteredForeground = Color.BLACK;
    Color typeFilteredBackground = GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR;
    Color typeFilteredForeground = Color.BLACK;

    protected int sortInfoTracker;

    /**
     * Constructor.
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

	Component comp = super.getTableCellRendererComponent(table, value,
		isSelected, hasFocus, row, column);

	int actualColumn = ((TTTableModel) table.getModel()).getTableInfo()
		.getOriginalColumnIndexOfColumnName(
			table.getColumnModel().getColumn(column)
				.getHeaderValue().toString());

	// Create a JLabel so that we can set the sort icon next to the heading
	JLabel lab = (JLabel) comp;
	lab.setHorizontalTextPosition(LEFT);

	// Create the image icons (up/down arrows)
	ImageIcon upIcon = createImageIcon("up.icon");
	ImageIcon downIcon = createImageIcon("down.icon");

	// Set the border for the header cell
	lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	// Set the colors and the tool tip according to if the column is
	// filtered or not.
	// if (((TTTableModel) table.getModel()).isColumnFiltered(column)
	// || ((TTTableModel) table.getModel())
	// .isColumnFilteredForTableType(column)) {
	if (((TTTableModel) table.getModel()).isColumnFiltered(actualColumn)
		|| ((TTTableModel) table.getModel())
			.isColumnFilteredForTableType(actualColumn)) {
	    // Filtering is active either for table type or for the current
	    // table.

	    // The tooltip text
	    String tooltip = "Active filters: ";

	    if (((TTTableModel) table.getModel())
		    .isColumnFilteredForTableType(actualColumn)) {
		// Append the tooltip text
		tooltip = tooltip.concat("Table Type filter: '"
			+ ((TTTableModel) table.getModel())
				.getColumnFilterForTableType(actualColumn)
			+ "' ");
		// Set the colours
		comp.setBackground(typeFilteredBackground);
		comp.setForeground(typeFilteredForeground);
	    }

	    if (((TTTableModel) table.getModel())
		    .isColumnFiltered(actualColumn)) {
		// Append the tooltip text
		tooltip = tooltip.concat("Filter for this table: '"
			+ ((TTTableModel) table.getModel())
				.getColumnFilter(actualColumn) + "'");
		// Set the colours
		comp.setBackground(filteredBackground);
		comp.setForeground(filteredForeground);

	    }
	    // Set the tooltip
	    super.setToolTipText(tooltip);

	} else {
	    // Filtering is not active.
	    // Set the colours.
	    comp.setBackground(normalBackground);
	    comp.setForeground(normalForeground);

	    // Clear the tool tip.
	    super.setToolTipText(null);
	}

	// Check if the current column is sorted. If yes, the add the arrow icon
	// according to the current sort order.
	if (((TTTableModel) table.getModel()).getCurrentSortColumn() == actualColumn) {
	    // This column is sorted.
	    availableSortOrders sortOrder = ((TTTableModel) table.getModel())
		    .getCurrentSortOrder();

	    if (sortOrder == availableSortOrders.NO_SORT) {
		lab.setIcon(null);
	    } else if (sortOrder == availableSortOrders.ASCENDING) {
		lab.setIcon(downIcon);
	    } else if (sortOrder == availableSortOrders.DESCENDING) {
		lab.setIcon(upIcon);
	    } else {
		System.out.println(this.getClass()
			+ " getTableCellRendererComponent(): ERROR: "
			+ "Incorrect sort order specified: " + sortOrder + ".");
		lab.setIcon(null);
	    }

	} else {
	    // This column is not sorted. Set no icon.
	    lab.setIcon(null);
	}
	return lab;

    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected ImageIcon createImageIcon(String iconName) {
    	ImageIcon upIcon = null;
    	if(TechPackIDE.getResourceMap() != null){
    		upIcon = TechPackIDE.getResourceMap().getImageIcon(iconName);
    		if(upIcon == null){
        		//System.err.println("Couldn't find image in resource map: " + iconName);
        		logger.warning("Couldn't find image in resource map: " + iconName);
        		return null;
        	}else{
        		return upIcon ;
        	}
    	}else{
    		//System.err.println("Couldn't find image in resource map: " + iconName);
    		//logger.warning(" Resource Map of  TechPackIDE is NULL. ");
    		return null;
    	}
    }
}
