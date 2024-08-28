package tableTreeUtils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;

import tableTree.TTTableModel;

/**
 * An implementation of the MouseListener used to catch mouse events performed
 * on a displayed table
 * 
 * @author enaland ejeahei eheitur
 */

public class TableMouseEventListener implements MouseListener,
	TableColumnModelListener {

    /**
     * A reference to the container containing the clicked table
     */
    private TableContainer passedContainer = null;

    /**
     * A boolean to trace whether we have had a margin resize before a mouse
     * released event
     */
    private boolean wereMariginsChanged = false;

    /**
     * A boolean to trace whether we have had a reordering before a mouse
     * released event
     */
    private boolean wereColumnsMoved = false;

    /**
     * A reference for the popup menu
     */
    private JPopupMenu menu = null;

    /**
     * A boolean when tracking mouse events
     */
    boolean mousePressed = false;

    /**
     * Constructor creating a MouseListener for a specific table container
     * 
     * @param TC
     */
    public TableMouseEventListener(TableContainer TC) {
	passedContainer = TC;
    }

    /**
     * This method handles the updating of the table model's last selected
     * column.
     * 
     * Important: the column model's column I, is not necessarily the same
     * column as the table model's column I. If, e.g., the user has drag/drop:ed
     * a column so that the column order in the visual table representation
     * differs.
     * 
     * @param x
     */
    private void updateLastSelectedColumnIndex(int x) {
	int index = passedContainer.getTable().getColumnModel()
		.getColumnIndexAtX(x);
	String colName = passedContainer.getTable().getColumnModel().getColumn(
		index).getHeaderValue().toString();
	((TTTableModel) passedContainer.getTable().getModel())
		.setLastSelectedColumn(colName);
    }

    private void sort(int column) {
	((TTTableModel) (passedContainer.getTable().getModel())).sort(column);
    }

    /**
     * Callback method for a mouse clicked event in the table. Left click on the
     * table header clears the row selection. Right click shows the pop-up menu.
     * The pop-up menu alternatives vary depending on whether a row in the table
     * is selected or not when the right click occurs
     * 
     * @param e
     *                mouse event
     */

    public void mouseClicked(MouseEvent e) {
	updateLastSelectedColumnIndex(e.getX()); // Update the column index

	// Mouse left clicked
	if (e.getButton() == 1) {
	    if (e.getComponent() instanceof JTableHeader) {
		// Clear possible selection (or update it according to the
		// sort?)
		passedContainer.getTable().clearSelection();

		// Sort the column
		sort(((TTTableModel) (passedContainer.getTable().getModel()))
			.getLastSelectedColumn());

		// Redraw the header and the table
		// ((TTTableModel) (passedContainer.getTable().getModel()))
		// .fireTableDataChanged();
		e.getComponent().repaint();
		wereMariginsChanged = false;
		wereColumnsMoved = false;
	    }
	}

	// Mouse right clicked
	if (e.getButton() == 3) {
	    menu = passedContainer.getPopupmenu(e.getComponent());
	    if (menu != null) {
		menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	    e.getComponent().repaint();
	    wereMariginsChanged = false;
	    wereColumnsMoved = false;
	}
    }

    /**
     * Method for handling the mouse entered event. No action needed.
     * 
     * @param e
     *                the mouse event
     */
    public void mouseEntered(MouseEvent e) {
	// intentionally left blank
    }

    /**
     * Method for handling the mouse exited event. No action needed.
     * 
     * @param e
     *                the mouse event
     */
    public void mouseExited(MouseEvent e) {
	// intentionally left blank
    }

    /**
     * Method for handling the mouse pressed event.
     * 
     * @param e
     *                the mouse event
     */
    public void mousePressed(MouseEvent e) {
	mousePressed = true;
    }

    /**
     * This method tracks whether a drag/drop has happened, which has resulted
     * in a margin resize or a column reordering. The corresponding methods for
     * the table container are then called if this is the case.
     * 
     * @param e
     *                the mouse event
     */
    public void mouseReleased(MouseEvent e) {
	mousePressed = false;
	if (e.getButton() == 1) {
	    if (wereMariginsChanged) {
		wereMariginsChanged = false;
		// System.out.println("Fix Margins");
		passedContainer.saveNewColumnSizes();
		passedContainer.refreshAllTables();
		passedContainer.reActivateForEditing();
	    } else if (wereColumnsMoved) {
		wereColumnsMoved = false;
		// System.out.println("Move Column");
		passedContainer.saveNewColumnOrder();
		passedContainer.refreshAllTables();
		passedContainer.reActivateForEditing();
	    }
	}
    }

    /**
     * Method for handling the column added model event. No action at the
     * moment.
     * 
     * @param e
     *                table column model event
     */
    public void columnAdded(TableColumnModelEvent e) {
	// intentionally left blank
    }

    /**
     * Tracks whether the margins changed due to mouse events or not
     * 
     * @param e
     *                table column model event
     */
    public void columnMarginChanged(ChangeEvent e) {
	if (mousePressed)
	    wereMariginsChanged = true;
    }

    /**
     * Tracks whether the column order changed due to mouse events or not
     * 
     * @param e
     *                table column model event
     */
    public void columnMoved(TableColumnModelEvent e) {
	if (e.getFromIndex() != e.getToIndex())
	    wereColumnsMoved = true;
    }

    /**
     * Method for handling the column removed model event. No action at the
     * moment.
     * 
     * @param e
     *                table column model event
     */
    public void columnRemoved(TableColumnModelEvent e) {
	// intentionally left blank
    }

    /**
     * Method for handling the column selection changed model event. No action
     * at the moment.
     * 
     * @param e
     *                table column model event
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
	// intentionally left blank
    }
}
