package tableTree;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import tableTreeUtils.TableContainer;

/**
 * Generic class for rendering tree cells. This can be extended if new types of
 * data are added.
 * 
 * @author enaland ejeahei
 * 
 */
public class TTEditor extends DefaultTreeCellEditor implements TreeCellEditor {

    /**
     * Constructor, passes the tree to be edited and a default renderer to the
     * parent.
     * 
     * @param tree
     */
    public TTEditor(JTree tree) {
	super(tree, new DefaultTreeCellRenderer());
    }

    /**
     * Return the editor for the specific component. OVERRIDE IF there are new
     * types of nodes to be edited.
     * 
     * @param tree
     *                the tree to be edited
     * @param value
     *                the actual cell that is being edited
     * @param isSelected
     * @param expanded
     *                true if the edited node is a branch node that is expanded
     * @param leaf
     *                true if the edited node is a leaf node
     * @param row
     *                the row number being edited
     * @return component to be used to edit the node
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
	    boolean isSelected, boolean expanded, boolean leaf, int row) {
	Object aUserObject = null;

	if (value != null) {
	    aUserObject = ((DefaultMutableTreeNode) value).getUserObject();

	    if (leaf) {
		if (aUserObject instanceof TableContainer) {
		    TableContainer TC = (TableContainer) aUserObject;
		    return TC;
		}
		if (aUserObject instanceof JPanel) {
		    JPanel JP = (JPanel) aUserObject;
		    return JP;
		} else {
		    return super.getTreeCellEditorComponent(tree, value,
			    isSelected, expanded, leaf, row);
		}
	    }
	    else { // if (!leaf)
		return super.getTreeCellEditorComponent(tree, value,
			isSelected, expanded, leaf, row);
	    }
	} else {
	    return null;
	}
    }
    
}
