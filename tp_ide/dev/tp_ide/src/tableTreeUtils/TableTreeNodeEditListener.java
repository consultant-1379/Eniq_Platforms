package tableTreeUtils;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * An implementation of TreeModelListener given to the tree in order to handle
 * renaming of tree main nodes
 * 
 * @author ejeahei enaland
 */

public class TableTreeNodeEditListener implements TreeModelListener {

    /**
     * Callback for modifications to tree main node
     * 
     * @param e
     *                tree model event
     */
    public void treeNodesChanged(TreeModelEvent e) {
	DefaultMutableTreeNode modifiedNode;

	modifiedNode = (DefaultMutableTreeNode) (e.getTreePath()
		.getLastPathComponent());

	try {
	    int index = e.getChildIndices()[0];
	    modifiedNode = (DefaultMutableTreeNode) (modifiedNode
		    .getChildAt(index));
	} catch (NullPointerException exc) {
	}

	// Modify main nodes only
	if (modifiedNode instanceof TreeMainNode) {
	    ((TreeMainNode) modifiedNode).setNodeName((String) modifiedNode
		    .getUserObject());
	}

    }

    /**
     * handles the tree nodes inserted event. No action at the moment.
     * 
     * @param e
     *                tree model event
     */
    public void treeNodesInserted(TreeModelEvent e) {
	// intentionally left blank
    }

    /**
     * handles the tree nodes removed event. No action at the moment.
     * 
     * @param e
     *                tree model event
     */
    public void treeNodesRemoved(TreeModelEvent e) {
	// intentionally left blank
    }

    /**
     * handles the tree nodes structure changed event. No action at the moment.
     * 
     * @param e
     *                tree model event
     */
    public void treeStructureChanged(TreeModelEvent e) {
	// intentionally left blank
    }

}
