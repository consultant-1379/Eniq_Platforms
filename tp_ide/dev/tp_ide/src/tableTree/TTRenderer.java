package tableTree;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import tableTreeUtils.TableContainer;

/**
 * Generic class for rendering tree cells. This can be extended if new types of
 * data are added.
 * 
 * @author ejeahei enaland
 * 
 */
public class TTRenderer implements TreeCellRenderer {

    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

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

	Object aUserObject = null;
	Object onlyChildUserObject = null;

	if (value != null) {
	    aUserObject = ((DefaultMutableTreeNode) value).getUserObject();

	    if (leaf) {
		// Special case for table data
		if (aUserObject instanceof TableContainer) {
		    TableContainer TC = (TableContainer) aUserObject;
		    return TC;
		} else if (aUserObject instanceof JPanel) {
		    JPanel thisPanel = (JPanel) aUserObject;
		    return thisPanel;
		} else {
		    return renderer.getTreeCellRendererComponent(tree, value,
			    selected, expanded, leaf, row, hasFocus);
		}
	    } else { // if (!leaf)
		// Get and display the number of rows for the parents of tables
		if (((DefaultMutableTreeNode) value).getChildCount() == 1) {
		    onlyChildUserObject = ((DefaultMutableTreeNode) value)
			    .getFirstLeaf().getUserObject();
		    if (onlyChildUserObject instanceof TableContainer) { // Is
			// parent
			// of
			// table?
			value = ((TableContainer) onlyChildUserObject)
				.getTable().getName();
			if (!expanded) {
			    int rows = ((TableContainer) onlyChildUserObject)
				    .getTable().getRowCount();
			    value = rows + " " + value;
			}
		    }
		}
		return renderer.getTreeCellRendererComponent(tree, value,
			selected, expanded, leaf, row, hasFocus);
	    }
	} else { // if (value == null)
	    return null;
	}
    }
}
