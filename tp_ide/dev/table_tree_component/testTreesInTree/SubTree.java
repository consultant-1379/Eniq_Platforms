package testTreesInTree;

import javax.swing.JTree;

import tableTree.TTEditor;
import tableTreeUtils.TreesInTreeListener;

/**
 * Example implementation of a sub-tree in the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class SubTree extends JTree {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param listener
     */
    public SubTree(TreesInTreeListener listener) {
	this.setModel(new SubTreeModel());
	this.addMouseListener(listener);
	this.setEditable(true);
	this.setCellRenderer(new TreeRenderer());
	this.setCellEditor(new TTEditor(this));
	this.setRootVisible(true);
    }
}
