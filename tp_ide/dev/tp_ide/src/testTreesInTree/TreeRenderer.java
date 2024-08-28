package testTreesInTree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import tableTree.TableTreeComponent;

/**
 * Sample renderer for the tree nodes in the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class TreeRenderer implements TreeCellRenderer {

    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    /**
     * Return the renderer for the specific component. OVERRIDE IF there are new
     * types of nodes to be rendered.
     * 
     * @param tree
     *                the tree to be rendered
     * @param value
     *                the actual cell that is being rendered
     * @param selected
     *                true if the tree cell is selected
     * @param expanded
     *                true if the rendered node is a branch node that is
     *                expanded
     * @param leaf
     *                true if the rendered node is a leaf node
     * @param row
     *                the row number being rendered
     * @param hasFocus
     *                true if the tree cell has focus
     * @return component to be used to render the node
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
	    boolean selected, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	Object obj = node.getUserObject();
	// JLabel retLab;
	TableTreeComponent retTree;

	if (obj instanceof TableTreeComponent) {
	    retTree = (TableTreeComponent) obj;
	    return retTree;
	} else {
	    return defaultRenderer.getTreeCellRendererComponent(tree, value,
		    selected, expanded, leaf, row, hasFocus);
	}

    }

}
