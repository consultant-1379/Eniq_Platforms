package tableTreeUtils;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import tableTree.TableTreeComponent;
import testTreesInTree.MainTreeModel;

/**
 * This listener is needed for implementing a tree with TableTreeComponent
 * (tree) instances as leafs. The same listener must be used in the tree and in
 * the creation of the TableTreeComponent instances.
 * 
 * @author enaland eheitur
 * 
 */
public class TreesInTreeListener implements MouseListener,
	TreeExpansionListener, ActionListener {

    static JTree parentTree = null;
    private final String ADD = "Add Node";
    private final String REMOVE = "Remove Node";
    private DefaultMutableTreeNode nodeToDelete = null;

    /**
     * Constructor. Stores the parent tree given as parameter.
     * 
     * @param inTree
     */
    public TreesInTreeListener(JTree inTree) {
	parentTree = inTree;
    }

    /**
     * Handles the mouse clicked event
     * 
     * @param e
     *                the mouse event
     */
    public void mouseClicked(MouseEvent e) {
	// Get the clicked path
	Point clickedPoint = new Point(e.getX(), e.getY());
	SwingUtilities.convertPointToScreen(clickedPoint, (Component) e
		.getSource());
	SwingUtilities.convertPointFromScreen(clickedPoint, parentTree);
	TreePath path = parentTree.getPathForLocation(clickedPoint.x,
		clickedPoint.y);
	parentTree.setSelectionPath(path);

	// Get the clicked node, if any
	DefaultMutableTreeNode clickedNode = null;
	if (path != null)
	    clickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();

	// Check for mouse right or left click
	if (e.getButton() == 3
		&& ((clickedNode != null && !clickedNode.isLeaf()) || path == null)) {
	    // Mouse right click: Something (not a leaf) was clicked.
	    // Show the pop-up menu.
	    JPopupMenu menu = getPopupMenu(clickedNode);
	    menu.show(parentTree, (int) clickedPoint.getX(), (int) clickedPoint
		    .getY());
	} else if (e.getButton() == 1 && clickedNode != null
		&& clickedNode.isLeaf()) {
	    // Mouse left click: a leaf node was clicked.
	    // If a TableTreeComponent was clicked, the start editing it.
	    if (path != null
		    && clickedNode.getUserObject() instanceof TableTreeComponent) {
		// Start editing the parent tree so that the actual
		// TableTreeComponent is shown.
		if (parentTree.isEditing() == false) {
		    parentTree.startEditingAtPath(path);
		}
		// Get the inner tree and indicate that the tree changed, so
		// that the tree is repainted.
		// DefaultMutableTreeNode theNode = (DefaultMutableTreeNode)
		// path
		// .getLastPathComponent();
		// TableTreeComponent innerTree = (TableTreeComponent) theNode
		// .getUserObject();
		TableTreeComponent innerTree = (TableTreeComponent) clickedNode
			.getUserObject();
		innerTree.treeDidChange();

		// Indicate that the parent node changed.
		((DefaultTreeModel) parentTree.getModel())
			.nodeChanged(clickedNode);

		// Cancel the editing of the parent tree and start editing at
		// the TableTreeComponent
		parentTree.cancelEditing();
		parentTree.startEditingAtPath(path);
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
	// Intentionally left blank.
    }

    /**
     * Handles the mouse exited event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mouseExited(MouseEvent e) {
	// Intentionally left blank.
    }

    /**
     * Handles the mouse pressed event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mousePressed(MouseEvent e) {
	// Intentionally left blank.
    }

    /**
     * Handles the mouse released event. No action at the moment.
     * 
     * @param e
     *                mouse event
     */
    public void mouseReleased(MouseEvent e) {
	// Intentionally left blank.
    }

    /**
     * Handles the tree collapsed event. No action at the moment.
     * 
     * @param event
     *                tree expansion event
     */
    public void treeCollapsed(TreeExpansionEvent event) {
	// Intentionally left blank.
    }

    /**
     * Handles the expansion of the parent tree
     * 
     * @param event
     *                tree expansion event
     */
    public void treeExpanded(TreeExpansionEvent event) {
	// In case the tree is any other than TableTreeComponent, editing is
	// started for the tree.
	if (!(event.getSource() instanceof TableTreeComponent)) {
	    int row = parentTree.getRowForPath(event.getPath()) + 1;
	    TreePath newPath = parentTree.getPathForRow(row);
	    parentTree.startEditingAtPath(newPath);
	}
    }

    /**
     * Gets the right-click pop-up menu for the parent tree
     * 
     * @param node
     * @return the pop-up menu
     */
    public JPopupMenu getPopupMenu(DefaultMutableTreeNode node) {
	JPopupMenu retMenu = new JPopupMenu();

	JMenuItem item;
	item = new JMenuItem(ADD);
	item.setActionCommand(ADD);
	item.addActionListener(this);
	retMenu.add(item);

	if (node != null) {
	    item = new JMenuItem(REMOVE);
	    item.setActionCommand(REMOVE);
	    item.addActionListener(this);
	    retMenu.add(item);
	    nodeToDelete = node;
	}
	return retMenu;
    }

    /**
     * Callback method example for the action events from the pop-up menu.
     * 
     * @param e
     *                the action event
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equals(ADD)) {
	    System.out.println("Code for adding a new node here");
	    nodeToDelete = null;
	} else if (e.getActionCommand().equals(REMOVE)) {
	    System.out.println("Code for removing the selected node here");
	    ((MainTreeModel) parentTree.getModel())
		    .removeNodeFromParent(nodeToDelete);
	} else {
	    // Nothing
	}
    }
}
