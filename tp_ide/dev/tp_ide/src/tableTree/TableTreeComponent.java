package tableTree;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import tableTreeUtils.ParameterPanel;
import tableTreeUtils.SavableTreeNode;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TableTreeMouseEventListener;
import tableTreeUtils.TableTreeNodeEditListener;
import tableTreeUtils.TreeMainNode;
import tableTreeUtils.TreesInTreeListener;

/**
 * Class for trees that can contain tables and panels as nodes. Initialize this
 * with a concrete TreeDataFactory.
 * 
 * @author ejeahei enaland
 */
public class TableTreeComponent extends JTree implements Observer {

  private static final long serialVersionUID = 1L;

  /**
   * Abstract factory pattern used here. This will be instantiated to the
   * concrete factory of the implemented dialogue.
   */
  private TreeDataFactory theFactory = null;

  /**
   * Vector containing nodes that will be deleted when the changes are
   * committed.
   */
  private Vector<TreeMainNode> markedForDeletion = null;

  /**
   * Vector containing newly added nodes. This vector is emptied every time a
   * save occurs, saving the new data represented in the nodes to the DB
   */
  private Vector<TreeMainNode> newlyAdded = null;

  /**
   * Vector for holding all the Table Containers in the tree. This is used e.g.
   * when clearing the filters in all the tables in the tree.
   */
  private Vector<TableContainer> tableContainers = null;

  /**
   * Basic constructor, gets the dataModel from the given factory and configures
   * the tree.
   * 
   * @param aFactory
   *          the tree data factory
   */
  public TableTreeComponent(TreeDataFactory aFactory) {
    this(aFactory, null);
  }

  /**
   * Modified constructor, gets the dataModel from the given factory, configures
   * the tree and registers a listener passed on from the class instance that
   * creates this instance. This is used, e.g. where we have a JTree that have
   * instances of this class as nodes.
   * 
   * @param aFactory
   *          the tree data factory
   * @param treesInTreeListener
   *          the listener for the change events from the component
   */
  public TableTreeComponent(TreeDataFactory aFactory, TreesInTreeListener treesInTreeListener) {
    // Store the factory and get the data model
    theFactory = aFactory;
    theFactory.setTree(this);
    // TreeModel aModel = theFactory.createAndGetModel();
    // setModel(aModel);

    setModelForTree();
    setRootVisible(false);
    setShowsRootHandles(true);

    // Add listener for when nodes are edited
    // TableTreeNodeEditListener editListener = new
    // TableTreeNodeEditListener();
    // aModel.addTreeModelListener(editListener);

    // Set renderer, editor and mouse listener
    setCellRenderer(theFactory.getTreeCellRenderer());
    setCellEditor(theFactory.getTreeCellEditor(this));
    addMouseListener(new TableTreeMouseEventListener(this, theFactory));
    if (treesInTreeListener != null)
      addMouseListener(treesInTreeListener);

    // Always needs to be true
    setEditable(true);

    // Remove some of the auto scrolling in the tree
    setAutoscrolls(false);
    setScrollsOnExpand(false);

    // Double-click should edit, not expand
    setToggleClickCount(0);

    // Set tree row height to zero to avoid problems with Windows and Motif
    // Look and Feels. If not set, only a default row eight of pixels is
    // shown of the tree leaf nodes, i.e. the preferred size does not work
    // for height.
    setRowHeight(0);
  }

  /**
   * Return the stored factory.
   * 
   * @return theFactory
   */
  public TreeDataFactory getFactory() {
    return theFactory;
  }

  /**
   * Save all the changes made in the tree.
   */
  public void saveChanges() {
    DefaultMutableTreeNode currentChildNode = null;
    TreeMainNode deletedNode = null;

    // Delete all the nodes marked for deletion
    if (markedForDeletion != null && markedForDeletion.size() > 0) {
      for (int i = 0; i < markedForDeletion.size(); i++) {
        deletedNode = markedForDeletion.elementAt(i);
        deletedNode.removeFromDB();
        deletedNode = null;
      }
      markedForDeletion.clear();
    }
    // Save all other nodes
    DefaultTreeModel theModel = (DefaultTreeModel) getModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) theModel.getRoot();
    int numberOfChildren = root.getChildCount();

    // Get the children of the root node and save them
    for (int i = 0; i < numberOfChildren; i++) {
      currentChildNode = (DefaultMutableTreeNode) root.getChildAt(i);

      if (currentChildNode instanceof SavableTreeNode) {
        ((SavableTreeNode) currentChildNode).saveChanges();
      }
    }

    if (newlyAdded != null && newlyAdded.size() > 0) {
      newlyAdded.clear();
    }
  }

  /**
   * Discard all the changes made in the tree. This is done by re-reading the
   * model from the database.
   */
  public void discardChanges() {

    // Set the model for the tree
    setModelForTree();

    // Clear possible "to be deleted" marks for tree nodes.
    if (markedForDeletion != null) {
      markedForDeletion.clear();
    }
  }

  /**
   * This metod returns a vector containing a vector for each TreeMainNode shown
   * in the tree (each MeasurementType in our example). Each TreeMainNode vector
   * contain objects of the classes used to create the TreeMainNode's children
   * (in our example, each TreeMainNode vector contain: {TTParameterModel,
   * TTTableModel, TTTableModel})
   * 
   * @return retVector
   */
  @SuppressWarnings("unchecked")
  public Vector<Vector<Object>> getAllData() {
    Vector<Vector<Object>> retVector = new Vector<Vector<Object>>();

    DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
    DefaultMutableTreeNode node = null;
    Enumeration<DefaultMutableTreeNode> nodes = root.breadthFirstEnumeration();

    while (nodes.hasMoreElements()) {
      node = nodes.nextElement();
      if (node instanceof TreeMainNode) {
        retVector.add(createDataVectorOfTreeMainNode((TreeMainNode) node));
      }
    }
    return retVector;
  }

  /**
   * This metod returns a vector containing a vector for each new (still unsaved
   * to the DB)TreeMainNode shown in the tree (each MeasurementType in our
   * example). Each TreeMainNode vector contain objects of the classes used to
   * create the TreeMainNode's children (in our example, each TreeMainNode
   * vector contain: {TTParameterModel, TTTableModel, TTTableModel})
   * 
   * @return retVector
   */
  public Vector<Vector<Object>> getNewUnsavedData() {
    Vector<Vector<Object>> retVector = new Vector<Vector<Object>>();
    DefaultMutableTreeNode node = null;

    if (newlyAdded != null && newlyAdded.size() > 0) {
      for (int i = 0; i < newlyAdded.size(); i++) {
        node = newlyAdded.elementAt(i);
        if (node instanceof TreeMainNode) {
          retVector.add(createDataVectorOfTreeMainNode((TreeMainNode) node));
        }
      }
    }
    return retVector;
  }

  /**
   * This metod returns a vector containing a vector for each deleted, but still
   * not removed from the DB, TreeMainNode no longer visible in the tree. Each
   * TreeMainNode vector contain objects of the classes used to create the
   * TreeMainNode's children (in our example, each TreeMainNode vector contain:
   * {TTParameterModel, TTTableModel, TTTableModel})
   * 
   * @return retVector
   */
  public Vector<Vector<Object>> getDeletedData() {
    Vector<Vector<Object>> retVector = new Vector<Vector<Object>>();
    DefaultMutableTreeNode node = null;

    if (markedForDeletion != null && markedForDeletion.size() > 0) {
      for (int i = 0; i < markedForDeletion.size(); i++) {
        node = markedForDeletion.elementAt(i);
        if (node instanceof TreeMainNode) {
          retVector.add(createDataVectorOfTreeMainNode((TreeMainNode) node));
        }
      }
    }
    return retVector;
  }

  /**
   * This method takes a TreeMainNode as input and gets a hold of the data that
   * the node was built up from and returns this data in the form of a Vector
   * 
   * @param node
   * @return retVector
   */
  private Vector<Object> createDataVectorOfTreeMainNode(TreeMainNode node) {
    Vector<Object> retVector = node.getMainNodeData();
    return retVector;
  }

  /**
   * Helper function for repainting the tree. Used when a table is updated.
   * 
   * @param point
   *          the position of the node that was changed
   */
  public void updateTree(Point point) {
    DefaultTreeModel treeModel = (DefaultTreeModel) this.getModel();
    TreePath pathToSelection = this.getPathForLocation((int) point.getX(), (int) point.getY());
    if (pathToSelection == null) {
      return;
    }

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathToSelection.getLastPathComponent();
    if (node == null) {
      // No selection
      return;
    }
    treeModel.nodeChanged(node);
  }

  /**
   * Store the given node for deletion. It will be truly deleted when
   * saveChanges is called.
   * 
   * @param deletedNode
   *          the node to be deleted
   */
  public void markForDeletion(TreeMainNode deletedNode) {
    // Create the vector only if needed
    if (markedForDeletion == null) {
      markedForDeletion = new Vector<TreeMainNode>();
    }

    markedForDeletion.add(deletedNode);
  }

  /**
   * Store the given node as one of the newly added and unsaved nodes.
   * 
   * @param newNode
   *          the node that was added
   */
  public void markAsNewAndUnsaved(TreeMainNode newNode) {
    // Create the vector only if needed
    if (newlyAdded == null) {
      newlyAdded = new Vector<TreeMainNode>();
    }
    newlyAdded.add(newNode);
  }

  /**
   * Hide or display the root node. Overrides the JTree version of the method to
   * change the click count.
   * 
   * @param visible
   */
  public void setRootVisible(boolean visible) {
    if (visible) {
      super.setRootVisible(true);

      // Double-click should edit, not expand
      setToggleClickCount(0);
    } else {
      super.setRootVisible(false);

      // Double-click should expand
      setToggleClickCount(2);
    }
  }

  /**
   * This metod calls the current factory's createAndGetModel(), which returns
   * the model for the tree, and perform the nessecary setting of references for
   * all TableContainers and ParameterPanels. These references are needed for,
   * among other things, updates of the tree.
   */
  @SuppressWarnings("unchecked")
  public void setModelForTree() {
    TreeModel aModel = theFactory.createAndGetModel();

    // Add listener for when nodes are edited
    TableTreeNodeEditListener editListener = new TableTreeNodeEditListener();
    aModel.addTreeModelListener(editListener);

    super.setModel(aModel);

    // Give the tree, model and node reference to each node having a
    // TableContainer as the UserObject. This is done so that the
    // TableContainer can update the tree when the table is resized
    Enumeration<DefaultMutableTreeNode> treeNodes = ((DefaultMutableTreeNode) (aModel.getRoot()))
        .depthFirstEnumeration();
    DefaultMutableTreeNode currentNode = null;
    TableContainer currentTableContainer = null;
    ParameterPanel currentParameterPanel = null;
    while (treeNodes.hasMoreElements()) {
      currentNode = treeNodes.nextElement();
      if (currentNode.getUserObject() instanceof TableContainer) {
        currentTableContainer = (TableContainer) currentNode.getUserObject();
        currentTableContainer.setTreeUpdateReferences(this, aModel, currentNode);
      } else if (currentNode.getUserObject() instanceof ParameterPanel) {
        currentParameterPanel = (ParameterPanel) currentNode.getUserObject();
        currentParameterPanel.getModel().addTableTreeComponentAsObserver(this);
      } else if (!(currentNode instanceof TreeMainNode)) {
        // System.out.println(this.getUI().getRowForPath(this, new
        // TreePath(currentNode.getPath())));
      }
    }

  }

  /**
   * Collects all TableContainers in the tree to a vector
   * 
   * @return a vector with all the table containers
   */
  @SuppressWarnings("unchecked")
  public Vector<TableContainer> collectTableContainers() {
    tableContainers = new Vector<TableContainer>();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel().getRoot();
    Enumeration<DefaultMutableTreeNode> nodes = root.breadthFirstEnumeration();
    DefaultMutableTreeNode node = null;

    while (nodes.hasMoreElements()) {
      node = nodes.nextElement();

      // If the node is a leaf and is a TableContainer, then it is added
      // to the list of containers. Else the same method is called for the
      // child.
      if (node.isLeaf()) {
        if (node.getUserObject() instanceof TableContainer) {
          tableContainers.add((TableContainer) node.getUserObject());
        }
      }
    }
    return tableContainers;
  }

  /**
   * This method has been overridden in order to prevent the auto scroll as
   * editing is started on a node
   */
  @Override
  public void scrollPathToVisible(TreePath path) {
    // If the new tree main node has just been created, then scroll to it.
    // Otherwise the scrolling is not done to prevent unnecessary jumping of
    // the tree
    // when node editing is started.
    if (path.getLastPathComponent() instanceof TreeMainNode) {
      TreeMainNode node = (TreeMainNode) path.getLastPathComponent();

      // In case there are new nodes (not saved yet), then iterate through
      // them. If the node is in the list, then scroll to it and return.
      if (newlyAdded != null) {
        TreeMainNode thisNode = null;
        Iterator<TreeMainNode> iter = newlyAdded.iterator();
        while (iter.hasNext()) {
          thisNode = iter.next();
          if (thisNode.equals(node)) {
            super.scrollPathToVisible(path);
            return;
          }
        }
      }
    }
  }

  /**
   * This method is used for expanding and collapsing all the nodes in the tree.
   * If expand is true, expands all nodes in the tree. Otherwise, collapses all
   * nodes in the tree.
   * 
   * @param tree
   * @param expand
   */
  public void expandAll(JTree tree, boolean expand) {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

    // Traverse tree from root
    expandAll(tree, new TreePath(root), expand);
  }

  /**
   * Method for expanding or collapsing all nodes in a tree under the parent
   * node.
   * 
   * @param tree
   * @param parent
   * @param expand
   */
  public void expandAll(JTree tree, TreePath parent, boolean expand) {
    // Traverse children all recursively expand/collapse
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path, expand);
      }
    }

    // Expansion or collapse the node.
    // Note: since the root is hidden, it is not collapsed.
    if (expand) {
      tree.expandPath(parent);
    } else {
      if (!node.isRoot()) {
        tree.collapsePath(parent);
      }
    }
  }

  /**
   * This method catches observer events from the tree's table and parameter
   * models when these models' data is changed. It then fires the event for the
   * data change.
   * 
   * @param o
   *          the observable object
   * @param arg
   *          arguments
   */
  public void update(Observable o, Object arg) {
    fireTableTreeComponentDataChanged();
  }

  /**
   * This vector keeps track of all registered DocumentListeners for this
   * component
   */
  private Vector<DocumentListener> docListeners = new Vector<DocumentListener>();

  /**
   * Call this method to register a DocumentListener to this component
   * 
   * @param docList
   */
  public void addDocumentListener(DocumentListener docList) {
    if (!docListeners.contains(docList)) {
      docListeners.add(docList);
    }
  }

  /**
   * Call this method to unregister a DocumentListener from this component
   * 
   * @param docList
   */
  public void removeDocumentListener(DocumentListener docList) {
    if (docListeners.contains(docList)) {
      docListeners.remove(docList);
    }
  }

  /**
   * Gets the document listeners for added this component.
   * 
   * @return a vector of document listeners
   */
  public Vector<DocumentListener> getDocumentListeners() {
    return docListeners;
  }

  /**
   * This method raises an event, telling the registered DocumentListeners that
   * a table model's or parameter model's data has changed
   */
  public void fireTableTreeComponentDataChanged() {

    Iterator<DocumentListener> iter = docListeners.iterator();

    while (iter.hasNext()) {
      iter.next().changedUpdate(new DocEvent());
    }
  }

  /**
   * This is an implementation of the DocumentEvent, used to create the
   * DocumentListener event used to inform the listeners about the data change
   * 
   * @author enaland
   */
  private class DocEvent implements DocumentEvent {

    /**
     * @param elem
     * @return the element change. Will always be null.
     */
    public ElementChange getChange(Element elem) {
      return null;
    }

    /**
     * @return the document
     */
    public Document getDocument() {
      return null;
    }

    /**
     * @return the length. Will always be zero.
     */
    public int getLength() {
      return 0;
    }

    /**
     * @return the offset. Will always be zero.
     */
    public int getOffset() {
      return 0;
    }

    /**
     * @return the event type. Will always be "Change".
     */
    public EventType getType() {
      return DocumentEvent.EventType.CHANGE;
    }

  }

  /**
   * Overridden version of the method for disabling the editing of some nodes in
   * the tree.
   * 
   * @param path
   *          the tree path
   * @return true if the path is editable
   */
  public boolean isPathEditable(TreePath path) {
    DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) path.getLastPathComponent();
    // Editing is now not allowed for tree main nodes, only leafs.
    // if (!(lastNode.isLeaf() || lastNode instanceof TreeMainNode)) {
    if (!(lastNode.isLeaf())) {
      return false;
    } else {
      return super.editable;
    }
  }

  /**
   * Calls validators for all tables in the tree.
   * 
   * @return Validation error messages. Empty vector in case validation was
   *         successful.
   */
  @SuppressWarnings("unchecked")
  public Vector<String> validateData() {

    Vector<String> retStrings = new Vector<String>();

    // Iterate through all tree nodes and check is the data is valid for all
    // table containers and parameter nodes.

    // TreeModel aModel = theFactory.createAndGetModel();
    TreeModel aModel = theFactory.getTree().getModel();

    Enumeration<DefaultMutableTreeNode> treeNodes = ((DefaultMutableTreeNode) (aModel.getRoot()))
        .depthFirstEnumeration();
    DefaultMutableTreeNode currentNode = null;
    TableContainer currentTableContainer = null;
    ParameterPanel currentParameterPanel = null;
    Vector<String> errorStrings = new Vector<String>();
    while (treeNodes.hasMoreElements()) {
      currentNode = treeNodes.nextElement();

      if (currentNode.getUserObject() instanceof TableContainer) {
        currentTableContainer = (TableContainer) currentNode.getUserObject();
        errorStrings = ((TTTableModel) currentTableContainer.getTable().getModel()).validateData();

        retStrings.addAll(errorStrings);

        // if (errorStrings.size() > 0) {
        // System.out.println("isDataValid(): Table container: "
        // + currentTableContainer.getName()
        // + " failed validation.");
        // }

      } else if (currentNode.getUserObject() instanceof ParameterPanel) {
        currentParameterPanel = (ParameterPanel) currentNode.getUserObject();
        errorStrings = ((TTParameterModel) currentParameterPanel.getModel()).validateData();

        retStrings.addAll(errorStrings);

        // if (errorStrings.size() > 0) {
        // System.out.println("isDataValid(): Parameter panel: "
        // + currentParameterPanel.getName()
        // + " failed validation.");
        // }
      }
    }

    // Return all the collected validation error messages
    return retStrings;
  }
}
