package testTreesInTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Example implementation of a sub-tree model in the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class SubTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public SubTreeModel() {
	super(null, true);
	DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode("subRoot");
	this.setRoot(subRoot);

	DefaultMutableTreeNode node;
	DefaultMutableTreeNode subNode;

	node = new DefaultMutableTreeNode("Uno");
	subNode = new DefaultMutableTreeNode("subUno");
	node.add(subNode);
	subRoot.add(node);
	node = new DefaultMutableTreeNode("Dos");
	subNode = new DefaultMutableTreeNode("subDos");
	node.add(subNode);
	subRoot.add(node);
    }

}
