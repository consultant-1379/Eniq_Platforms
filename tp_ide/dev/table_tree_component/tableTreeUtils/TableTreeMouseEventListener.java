package tableTreeUtils;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tableTree.TreeDataFactory;

/**
 * Mouse listener for table trees. This bypasses the normal mouse event
 * handling. If not used, very many clicks will be needed to activate inner
 * components for editing.
 * 
 * @author ejeahei enaland
 */
public class TableTreeMouseEventListener implements MouseListener {

    private JTree tree;
    private TreeDataFactory factory;
    private Vector<DefaultMutableTreeNode> selectedNodes;

    /**
     * Constructor, stores the tree and the factory.
     * 
     * @param tree
     * @param factory
     */
    public TableTreeMouseEventListener(JTree tree, TreeDataFactory factory) {
	this.tree = tree;
	this.factory = factory;
    }

    /**
     * Callback method for the mouseClicked event.
     * 
     * @param e
     *                the mouse event
     */
    public void mouseClicked(MouseEvent e) {
    	
    	final Vector<DefaultMutableTreeNode> selectedNodes = new Vector<DefaultMutableTreeNode>();
    	
	if (!tree.isEditable()) {
	    return;
	}

	// Mouse left clicked
	if (e.getButton() == 1) {
	    // Find the path to the selected component in the tree
	    TreePath pathToSelection = tree.getPathForLocation(e.getX(), e
		    .getY());
	    if (pathToSelection == null) {
		return;
	    }

	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathToSelection
		    .getLastPathComponent();
	    if (node == null) {
		// No selection
		return;
	    }

	    // Only leaves can be edited. Change this if other nodes should be
	    // editable too.
	    if (node.isLeaf()) {

		// Activate the component for editing
		tree.startEditingAtPath(pathToSelection);

		// The selected component may be nested: find the innermost
		// component that was clicked
		Component innerComponent = SwingUtilities
			.getDeepestComponentAt(tree, e.getX(), e.getY());

		// Dispatch the event to the innermost component
		Point newPoint = SwingUtilities.convertPoint(tree,
			e.getPoint(), innerComponent);

		innerComponent.dispatchEvent(new MouseEvent(innerComponent, e
			.getID(), e.getWhen(), e.getModifiers(), newPoint.x,
			newPoint.y, e.getClickCount(), e.isPopupTrigger()));
	    }

	}
	
	//Control Key or Shift Key pressed to delete multiple nodes 
	if (e.isControlDown() || e.isShiftDown()) {
		DefaultMutableTreeNode node;
		// Get the paths of all selected nodes
		TreePath[] tpEx = tree.getSelectionPaths();
		// Check so that the click location is a valid tree node
		if (tpEx != null) {
			boolean nodesInstanceOfTreeMainNode = true;
			int nodesLength = tpEx.length;
			for (int i = 0; i < nodesLength; i++) {
				node = (DefaultMutableTreeNode) tpEx[i].getLastPathComponent();
				if (node == null || node.isLeaf() == true) {
					// No selection
					return;
				}
				if (!(node instanceof TreeMainNode)) {
					nodesInstanceOfTreeMainNode = false;
				}
				selectedNodes.add(node);
			}
			// Mouse right clicked with Control Key or Shift Key pressed
			if (e.getButton() == 3) {
				// Launch the popup menu for removing multiple tree nodes
				if (nodesInstanceOfTreeMainNode) {
					tree.cancelEditing();
					JPopupMenu pop = factory.nodesRightClickMenu(selectedNodes);
					pop.show(tree, e.getX(), e.getY());
				}
			}
		} else {
			tree.stopEditing();
			JPopupMenu pop = factory.nodesRightClickMenu(null);
			pop.show(tree, e.getX(), e.getY());
		}
	}
	
	// Mouse right clicked
	else if (e.getButton() == 3) {
	    // Get the path location for the mouse click and set that location
	    // selected if it is a valid tree node
	    TreePath pathToMouseCoordinates = tree.getPathForLocation(e.getX(),
		    e.getY());
	    tree.setSelectionPath(pathToMouseCoordinates);

	    // Check so that the click location is a valid tree node
	    if (pathToMouseCoordinates != null) {

		// Get a hold of the deepest component at the click location
		tree.startEditingAtPath(pathToMouseCoordinates);
		Component rightClickedComponent = SwingUtilities
			.getDeepestComponentAt(tree, e.getX(), e.getY());

		// Check the type of the right clicked component and handle the
		// event accordingly
		if (rightClickedComponent instanceof JTable
			|| rightClickedComponent instanceof JTableHeader) {
		    Point newPoint = SwingUtilities.convertPoint(tree, e
			    .getPoint(), rightClickedComponent);

		    // Dispatch the event to the table's listener
		    rightClickedComponent.dispatchEvent(new MouseEvent(
			    rightClickedComponent, e.getID(), e.getWhen(), e
				    .getModifiers(), newPoint.x, newPoint.y, e
				    .getClickCount(), e.isPopupTrigger()));
		} else {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathToMouseCoordinates
			    .getLastPathComponent();
		    if (node == null || node.isLeaf() == true) {
			// No selection
			return;
		    }
		    // Launch the popup menu for adding/removing tree main nodes
		    if (node instanceof TreeMainNode) {
			// The editing is canceled (instead of stopped) and the
			// the pop-up is shown. Note: StopEditing would cause
			// the document listeners to be notified (even though
			// they shouldn't be).
			tree.cancelEditing();
			JPopupMenu pop = factory.nodeRightClickMenu(node);
			pop.show(tree, e.getX(), e.getY());
		    }
		}
	    } else {
		tree.stopEditing();
		JPopupMenu pop = factory.nodeRightClickMenu(null);
		pop.show(tree, e.getX(), e.getY());
	    }
	}
 }
    
 
    /**
     * Handles the mouse entered event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mouseEntered(MouseEvent e) {
	// No action
    }

    /**
     * Handles the mouse exited event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mouseExited(MouseEvent e) {
	// No action
    }

    /**
     * Handles the mouse pressed event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mousePressed(MouseEvent e) {
	// No action
    }

    /**
     * Handles the mouse released event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mouseReleased(MouseEvent e) {
	// No action
    }
}
