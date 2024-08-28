package testTreesInTree;

import javax.swing.JTree;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import measurementType.MeasurementTypeFactory;

import tableTree.TableTreeComponent;
import tableTreeUtils.TreesInTreeListener;

/**
 * Example tree model for the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class MainTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param theTree
     * @param listener
     * @param documentListener
     */
    public MainTreeModel(JTree theTree, TreesInTreeListener listener,
	    DocumentListener documentListener) {
	super(null, true);
	DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("root");
	this.setRoot(treeRoot);
	theTree.addMouseListener(listener);

	DefaultMutableTreeNode node;
	DefaultMutableTreeNode treeNode;
	int firstLevelChildren = 2;
	TableTreeComponent TTComp;

	for (int i = 0; i < firstLevelChildren; i++) {
	    node = new DefaultMutableTreeNode("Child " + i);
	    TTComp = new TableTreeComponent(new MeasurementTypeFactory(true,
		    null), listener);
	    TTComp.addDocumentListener(documentListener);
	    // oneSubTree = new SubTree(listener);
	    treeNode = new DefaultMutableTreeNode(TTComp);
	    treeNode.setAllowsChildren(false);
	    node.add(treeNode);
	    treeRoot.add(node);
	}
    }
}
