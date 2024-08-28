package tableTreeUtils;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import tableTree.TTParameterModel;

/**
 * A class for handling main nodes in the tree, where a main node represents
 * e.g. a measurement type and contains that type's parameters and tables as
 * child nodes
 * 
 * @author ejeahei enaland
 */

public class TreeMainNode extends DefaultMutableTreeNode implements
	SavableTreeNode {

    private static final long serialVersionUID = 1L;

    /**
     * A reference to the main node's parameter model
     */
    private TTParameterModel parameterModel;

    /**
     * A vector containing the specific deletion order of child nodes, if there
     * is such a requirement
     * 
     * e.g. tables needs to be deleted before parameter panels
     */
    private Vector<DefaultMutableTreeNode> deletionOrder = null;

    /**
     * Constructor for creating a main node, where the parameter model is used
     * to obtain the main node's name
     * 
     * @param parameterModel
     */
    public TreeMainNode(TTParameterModel parameterModel) {
	super(parameterModel.getMainNodeName());
	this.parameterModel = parameterModel;
    }

    /**
     * A method for saving the main node and all its children to the DB
     */
    public void saveChanges() {
	DefaultMutableTreeNode currentChildNode = null;
	Object currentUserObject = null;

	int children = getChildCount();
	for (int i = 0; i < children; i++) {
	    // Get the "heading" node
	    currentChildNode = (DefaultMutableTreeNode) getChildAt(i);

	    // Get the actual child node (the one that contains data)
	    currentChildNode = (DefaultMutableTreeNode) currentChildNode
		    .getFirstChild();
	    currentUserObject = currentChildNode.getUserObject();

	    if (currentUserObject instanceof SavableTreeNode) {
		((SavableTreeNode) currentUserObject).saveChanges();
	    }
	}
    }

    /**
     * Returns a vector containing references to the data objects that the node
     * was built up from, consisting of TTParameterModel and TTTableModel object
     * 
     * @return data object references
     */
    @SuppressWarnings("unchecked")
    public Vector<Object> getMainNodeData() {
	Vector<Object> retVector = new Vector<Object>();
	DefaultMutableTreeNode aNode = null;
	Object userObj = null;

	Enumeration<DefaultMutableTreeNode> childNodes = this
		.breadthFirstEnumeration();

	while (childNodes.hasMoreElements()) {
	    aNode = childNodes.nextElement();
	    userObj = aNode.getUserObject();
	    if (userObj instanceof ParameterPanel) {
		retVector.add(((ParameterPanel) userObj).getParameterModel());
	    } else if (userObj instanceof TableContainer) {
		retVector.add(((TableContainer) userObj).getTableModel());
	    }
	}

	return retVector;
    }

    /**
     * A method used to set the main node's name
     * 
     * @param nodeName
     */
    public void setNodeName(String nodeName) {
	parameterModel.setMainNodeName(nodeName);
    }

    /**
     * This method should be called from the factory, where the TreeMainNode is
     * created. The nodes-vector should contain references to the TreeMainNode's
     * actual children
     * 
     * @param nodes
     */
    public void setDeletionOrder(Vector<DefaultMutableTreeNode> nodes) {
	deletionOrder = nodes;
    }

    /**
     * Delete this node and all its children from the database
     */
    public void removeFromDB() {
	DefaultMutableTreeNode currentChildNode = null;
	Object currentUserObject = null;

	if (deletionOrder != null) {
	    for (int i = 0; i < deletionOrder.size(); i++) {
		currentChildNode = deletionOrder.elementAt(i);
		if (!currentChildNode.isLeaf())
		    currentChildNode = (DefaultMutableTreeNode) currentChildNode
			    .getFirstChild();
		currentUserObject = currentChildNode.getUserObject();
		if (currentUserObject instanceof SavableTreeNode) {
		    ((SavableTreeNode) currentUserObject).removeFromDB();
		    ((DefaultMutableTreeNode) deletionOrder.elementAt(i))
			    .removeFromParent();
		}
	    }
	}

	for (int i = 0; i < getChildCount(); i++) {
	    // Get the "heading" node
	    currentChildNode = (DefaultMutableTreeNode) getChildAt(i);

	    // Get the actual child node (the one that contains data)
	    currentChildNode = (DefaultMutableTreeNode) currentChildNode
		    .getFirstChild();
	    currentUserObject = currentChildNode.getUserObject();

	    if (currentUserObject instanceof SavableTreeNode) {
		((SavableTreeNode) currentUserObject).removeFromDB();
	    }
	}
    }
}
