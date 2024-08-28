package tableTreeUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import tableTree.TableTreeComponent;
import tableTree.TreeDataFactory;

/**
 * Listener class for the popup menu for tree nodes.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class TreePopupMenuListener extends Observable implements ActionListener {

  /**
   * The node of the tree that the listener is attached to.
   */
  private DefaultMutableTreeNode thisNode = null;
  
  private Vector<DefaultMutableTreeNode> thisNodes = null;

  /**
   * The factory that generated the model of the tree that the listener is
   * attached to.
   */
  private TreeDataFactory thisFactory = null;

  /*
   * Constants for the menu alternatives
   */
  /**
     * 
     */
  public static final String ADD_NODE = "Add Element";

  /**
     * 
     */
  public static final String REMOVE_NODE = "Remove Element";
  
  /**
     * 
     */
  public static final String REMOVE_NODE_NO_CONFIRM = "Remove Element (No confirm)";
  
  /**
   * 
   */
  public static final String REMOVE_NODES = "Remove Elements";
  
  /**
   * 
   */
  public static final String REMOVE_NODES_NO_CONFIRM = "Remove Elements (No confirm)";

  /**
     * 
     */
  // public static final String RENAME_NODE = "Rename Element";
  /**
     * 
     */
  public static final String DUPLICATE_NODE = "Duplicate Element";

  /**
     * 
     */
  public static final String SET_TYPE_FILTERS = "Set Filters for Table Types";

  /**
     * 
     */
  public static final String CLEAR_ALL_TYPE_FILTERS = "Clear Type Filters for All Table Types";

  /**
     * 
     */
  public static final String EXPAND_NODE = "Expand Node";

  /**
     * 
     */
  public static final String EXPAND_ALL_NODES = "Expand All Nodes";

  /**
     * 
     */
  public static final String COLLAPSE_NODE = "Collapse Node";

  /**
     * 
     */
  public static final String COLLAPSE_ALL_NODES = "Collapse All Nodes";

  /**
   * Constructor. Initializes the member variables based on the given node and
   * factory.
   * 
   * @param clickedNode
   * @param inFactory
   */
  public TreePopupMenuListener(DefaultMutableTreeNode clickedNode, TreeDataFactory inFactory) {
    thisNode = clickedNode;
    thisFactory = inFactory;
    // thisFactoryTablesInfo = thisFactory.tableInformations;
    addObserver(inFactory.getTree());
  }
  
  /**
   * Constructor. Initializes the member variables based on the given nodes and
   * factory.
   * 
   * @param clickedNodes
   * @param inFactory
   */
  public TreePopupMenuListener(Vector<DefaultMutableTreeNode> clickedNodes, TreeDataFactory inFactory) {
    thisNodes = clickedNodes;
    thisFactory = inFactory;
    addObserver(inFactory.getTree());
  }

  /**
   * Callback method for action events, i.e. for when one of the menu
   * alternatives have been selected.
   * 
   * @param ae
   *          the action event
   */
  public void actionPerformed(ActionEvent ae) {

    if (ae.getActionCommand().equals(ADD_NODE)) {
      addNode();
    } else if (ae.getActionCommand().equals(REMOVE_NODE)) {
      removeNode(true);
    } else if (ae.getActionCommand().equals(REMOVE_NODE_NO_CONFIRM)) {
      removeNode(false);
      // } else if (ae.getActionCommand().equals(RENAME_NODE)) {
      // renameNode();
    } else if (ae.getActionCommand().equals(REMOVE_NODES)) {
      removeNodes(true);
    } else if (ae.getActionCommand().equals(REMOVE_NODES_NO_CONFIRM)) {
      removeNodes(false);
    }else if (ae.getActionCommand().equals(DUPLICATE_NODE)) {
      duplicateNode();
    } else if (ae.getActionCommand().equals(SET_TYPE_FILTERS)) {
      thisFactory.getFilterDialog().showDialog();
    } else if (ae.getActionCommand().equals(CLEAR_ALL_TYPE_FILTERS)) {
      thisFactory.getFilterDialog().clearAllTypeFilters();
    } else if (ae.getActionCommand().equals(EXPAND_NODE)) {
      expandNode(true);
    } else if (ae.getActionCommand().equals(EXPAND_ALL_NODES)) {
      expandAllNodes(true);
    } else if (ae.getActionCommand().equals(COLLAPSE_NODE)) {
      expandNode(false);
    } else if (ae.getActionCommand().equals(COLLAPSE_ALL_NODES)) {
      expandAllNodes(false);
    } else {
      System.out.println(this.getClass() + " actionPerformed(): Received invalid action command: "
          + ae.getActionCommand());
    }
  }

  /**
   * Callback method for adding a new node.
   */
  private void addNode() {

    // Get the tree and the tree model
    DefaultMutableTreeNode parentNode;
    TableTreeComponent theTree = thisFactory.getTree();
    DefaultTreeModel treeModel = (DefaultTreeModel) theTree.getModel();

    // Get the parent of the new node
    parentNode = (DefaultMutableTreeNode) treeModel.getRoot();

    // Prompt the user for the new name
    String name = promptNewName("", parentNode);

    // If a valid name was given, then create the node.
    if (name != null && name != "") {
      // Create a new empty node, rename it according to the user input
      // and
      // insert data in the model
      TreeMainNode newNode = (TreeMainNode) thisFactory.createEmptyNode();
      newNode.setNodeName(name);
      newNode.setUserObject(name);
      treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());

      // Mark the new node as new and unsaved
      theTree.markAsNewAndUnsaved(newNode);

      // Update the references for the table container, so that the tune
      // size
      // will work properly when the nodes are resized.
      setReferencesForNewTreeNodes(theTree, treeModel, newNode);

      // Indicate the change in the tree nodes.
      treeModel.nodeStructureChanged(parentNode);

      // Notify observers that the tree has changed
      this.setChanged();
      this.notifyObservers();

      // Get the tree path to the newly created node, scroll to the node
      // and
      // start editing the node so that the user can type the new name.
      TreeNode[] nodes = treeModel.getPathToRoot(newNode);
      TreePath path = new TreePath(nodes);
      theTree.scrollPathToVisible(path);
      theTree.setSelectionPath(path);
      // theTree.startEditingAtPath(path);
    }
  }

  /**
   * Callback method for renaming a node.
   */
  // private void renameNode() {
  //
  // // Get the tree and the tree model
  // DefaultMutableTreeNode parentNode;
  // TableTreeComponent theTree = thisFactory.getTree();
  // DefaultTreeModel treeModel = (DefaultTreeModel) theTree.getModel();
  // TreeMainNode currentNode = (TreeMainNode) thisNode;
  //
  // // Get the Root
  // parentNode = (DefaultMutableTreeNode) treeModel.getRoot();
  //
  // // Prompt the user for the new name
  // String name = promptNewName((String) currentNode.getUserObject(),
  // parentNode);
  //
  // // If a valid name was given, then create the node.
  // if (name != null && name != "") {
  //
  // // Rename the node
  // currentNode.setNodeName(name);
  // currentNode.setUserObject(name);
  //
  // // Mark the new node as new and unsaved
  // theTree.markAsNewAndUnsaved(currentNode);
  //
  // // Update the references for the table container, so that the tune
  // // size
  // // will work properly when the nodes are resized.
  // setReferencesForNewTreeNodes(theTree, treeModel, currentNode);
  //
  // // Indicate the change in the tree nodes.
  // treeModel.nodeStructureChanged(parentNode);
  //
  // // Notify observers that the tree has changed
  // this.setChanged();
  // this.notifyObservers();
  //
  // // Get the tree path to the newly created node, scroll to the node
  // // and
  // // start editing the node so that the user can type the new name.
  // TreeNode[] nodes = treeModel.getPathToRoot(currentNode);
  // TreePath path = new TreePath(nodes);
  // theTree.scrollPathToVisible(path);
  // theTree.setSelectionPath(path);
  // // theTree.startEditingAtPath(path);
  // }
  // }
  /**
   * Method for setting the references in the TableContainer for a new node. The
   * references are needed in order for the container to update its size
   * correctly in the tree.
   */
  @SuppressWarnings("unchecked")
  private void setReferencesForNewTreeNodes(TableTreeComponent theTree, DefaultTreeModel theModel, TreeMainNode aNode) {
    Enumeration<DefaultMutableTreeNode> childNodes = aNode.breadthFirstEnumeration();

    DefaultMutableTreeNode oneNode;
    Object userObj;

    while (childNodes.hasMoreElements()) {
      oneNode = childNodes.nextElement();
      userObj = oneNode.getUserObject();

      if (userObj instanceof TableContainer) {
        ((TableContainer) userObj).setTreeUpdateReferences(theTree, theModel, oneNode);
      } else if (userObj instanceof ParameterPanel) {
        (((ParameterPanel) userObj).getModel()).addTableTreeComponentAsObserver(theTree);
      }
    }
  }
  
  /**
   * Callback method for removing nodes. The node will be marked for deletion,
   * but actual deletion happens when the changes are saved.
   */
  private void removeNode(boolean isConfirmNeeded) {
    TableTreeComponent theTree = thisFactory.getTree();
    DefaultTreeModel treeModel = (DefaultTreeModel) theTree.getModel();
    if (!isConfirmNeeded
        || (JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirm Remove", JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

      // Remove the node
      treeModel.removeNodeFromParent(thisNode);

      // Mark the given node for deletion
      theTree.markForDeletion((TreeMainNode) thisNode);

      // Notify observers that the tree has changed
      this.setChanged();
      this.notifyObservers();
    }
  }
  
    
  /**
   * Callback method for removing multiple nodes. The nodes will be marked for deletion,
   * but actual deletion happens when the changes are saved.
   */
  private void removeNodes(boolean isConfirmNeeded) {
    TableTreeComponent theTree = thisFactory.getTree();
    DefaultTreeModel treeModel = (DefaultTreeModel) theTree.getModel();
    DefaultMutableTreeNode currentNode = null;
    
    if (!isConfirmNeeded
        || (JOptionPane.showConfirmDialog(null, "Are you sure that you want to remove all selected nodes?", "Confirm Remove", JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

      Iterator<DefaultMutableTreeNode> itr = thisNodes.iterator();
      
      while(itr.hasNext()) {
    	  currentNode = itr.next();
    	  // Remove the node
    	  treeModel.removeNodeFromParent(currentNode);
    	  // Mark the given node for deletion
          theTree.markForDeletion((TreeMainNode) currentNode);
      }
      
      // Notify observers that the tree has changed
      this.setChanged();
      this.notifyObservers();
     }
  }

  /**
   * Callback method for expanding or collapsing a node in a tree.
   * 
   * @param expand
   *          boolean for expanding (or collapsing)
   */
  private void expandNode(boolean expand) {
    // Get the tree model
    TableTreeComponent theTree = thisFactory.getTree();

    // Expand/collapse the selected node in the tree
    theTree.expandAll(theTree, theTree.getSelectionPath(), expand);
  }

  /**
   * Callback method for expanding or collapsing all nodes in a tree.
   * 
   * @param expand
   *          boolean for expanding (or collapsing)
   * 
   */
  private void expandAllNodes(boolean expand) {
    // Get the tree model
    TableTreeComponent theTree = thisFactory.getTree();

    // Expand/Collapse all nodes in the tree.
    theTree.expandAll(theTree, expand);
  }

  /**
   * Prompts the user for the new name for the node. Input is accepted if the
   * name is unique and contains the forced name prefix or the user cancels the
   * operation.
   * 
   * @param oldName
   *          the old name of the node. This can be null or empty when adding a
   *          new node.
   * @param parentNode
   *          the parent node for the node
   */
  @SuppressWarnings("unchecked")
  private String promptNewName(String oldName, DefaultMutableTreeNode parentNode) {

    boolean accepted = false;
    String name = oldName;

    // Get the forced name prefix for the tree main node.
    String prefix = thisFactory.getForcedNamePrefix();

    // Set the initial value for the new name. If the name is not empty,
    // then force the prefix to the beginning if not already there. Else
    // just create a new name with the prefix.
    if (name != null && name != "") {
      if (prefix != null && !name.startsWith(prefix)) {
        name = prefix + name;
      }
    } else {
      if (prefix != null) {
        name = prefix + "NewName";
      } else {
        name = "NewName";
      }
    }

    while (!accepted) {

      // Get the maximmun length for the node name from the factory.
      int limit = thisFactory.getMaximumTreeNodeNameLength();

      int fieldWidth = 20;

      LimitedSizeTextDialog myDialog = new LimitedSizeTextDialog(null, "Element Name", null, fieldWidth, name, limit);
      name = myDialog.getAnswer();

      // Check for empty input or canceling
      if (name != null && !name.equals("")) {
        // User gave some input. Check for duplicate.

        // Check if the new name (child) already exists. Accept the
        // name in case the value is unique.
        Enumeration<DefaultMutableTreeNode> children = parentNode.children();
        DefaultMutableTreeNode node = null;
        accepted = true;
        while (children.hasMoreElements()) {
          node = children.nextElement();
          // System.out.println("Child: " + node.getUserObject());
          if (node.getUserObject().equals(name)) {
            // System.out.println("Name: '" + name + "' already
            // exists!!!");
            // A duplicate was found. The value is not accepted.
            JOptionPane.showMessageDialog(null, "The element name '" + name + "' already exists", "Invalid name",
                JOptionPane.ERROR_MESSAGE);
            accepted = false;
            break;
          }
        }
        // Check if the forced prefix is as it should be
        //
        // NOTE: Forcing of the prefix has been commented out since
        // forcing was not wanted.
        //
        // if (thisFactory.getForcedNamePrefix() != null
        // && thisFactory.getForcedNamePrefix() != "") {
        // if (!name.startsWith(prefix)) {
        // JOptionPane.showMessageDialog(null,
        // "The element name must start with the prefix: '"
        // + prefix + "'!", "Invalid name",
        // JOptionPane.ERROR_MESSAGE);
        // accepted = false;
        // }
        // }
      } else {
        // User canceled or entered an empty string.
        // Node addition is aborted.
        return "";
      }
    }
    return name;
  }

  /**
   * Callback method for duplicating an existing node.
   */
  private void duplicateNode() {

    // Get the tree and the tree model
    DefaultMutableTreeNode parentNode;
    TableTreeComponent theTree = thisFactory.getTree();
    DefaultTreeModel treeModel = (DefaultTreeModel) theTree.getModel();

    // Get the parent of the new node
    parentNode = (DefaultMutableTreeNode) treeModel.getRoot();

    // Prompt the user for the new name
    String name = promptNewName("", parentNode);

    // If a valid name was given, then create the node.
    if (name != null && name != "") {

      // Create a new empty node, rename it according to the user input
      // and insert data in the model
      TreeMainNode newNode = (TreeMainNode) thisFactory.createEmptyNode();
      newNode.setNodeName(name);
      newNode.setUserObject(name);
      treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());

      // Duplicate the existing data in the tree node to the new node
      thisFactory.duplicateExistingNode((TreeMainNode) thisNode, newNode);

      // Mark the new node as new and unsaved
      theTree.markAsNewAndUnsaved(newNode);

      // Update the references for the table container, so that the tune
      // size will work properly when the nodes are resized.
      setReferencesForNewTreeNodes(theTree, treeModel, newNode);

      // Indicate the change in the tree nodes.
      treeModel.nodeStructureChanged(parentNode);

      // Notify observers that the tree has changed
      this.setChanged();
      this.notifyObservers();

      // Get the tree path to the newly created node, scroll to the node
      // and start editing the node so that the user can type the new
      // name.
      TreeNode[] nodes = treeModel.getPathToRoot(newNode);
      TreePath path = new TreePath(nodes);
      theTree.scrollPathToVisible(path);
      theTree.setSelectionPath(path);
      // theTree.startEditingAtPath(path);
    }
  }

}
