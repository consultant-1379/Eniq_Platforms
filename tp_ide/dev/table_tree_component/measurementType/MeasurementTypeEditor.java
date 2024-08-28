package measurementType;

import java.awt.Component;
import javax.swing.JTree;

import tableTree.TTEditor;

/**
 * Concrete class for tree cell editors. This really doesn't do anything and is
 * superfluous, but included just to show that this type of classes can be
 * implemented.
 * 
 * @author enaland ejeahei
 * 
 */
public class MeasurementTypeEditor extends TTEditor {

    /**
     * Constructor.
     * 
     * @param tree
     */
    public MeasurementTypeEditor(JTree tree) {
	super(tree);
    }

    /**
     * Gets the editor component
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
	return super.getTreeCellEditorComponent(tree, value, isSelected,
		expanded, leaf, row);
    }
}
