package measurementType;

import java.awt.Component;

import javax.swing.JTree;

import tableTree.TTRenderer;

/**
 * Concrete class for tree cell renderers. This really doesn't do anything and
 * is superfluous, but included just to show that this type of classes can be
 * implemented.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class MeasurementTypeRenderer extends TTRenderer {

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
	return super.getTreeCellRendererComponent(tree, value, selected,
		expanded, leaf, row, hasFocus);
    }

}
