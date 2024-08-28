package testTreesInTree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import tableTree.TableTreeComponent;

/**
 * Sample editor for the tree nodes in the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class TreeEditor extends DefaultTreeCellEditor implements TreeCellEditor {

    /**
     * @param inTree
     */
    public TreeEditor(JTree inTree) {
	super(inTree, new DefaultTreeCellRenderer());
    }

    /**
     * Return the editor for the specific component.
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

	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	Object obj = node.getUserObject();
	TableTreeComponent retTree;

	if (obj instanceof TableTreeComponent) {
	    retTree = (TableTreeComponent) obj;
	    return retTree;
	} else {
	    return super.getTreeCellEditorComponent(tree, value, isSelected,
		    expanded, leaf, row);
	}
    }

}
