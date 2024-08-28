package tableTree;

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import tableTreeUtils.FilterDialog;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TableInformation;
import tableTreeUtils.TreeMainNode;
import tableTreeUtils.TreePopupMenuListener;

/**
 * Generic superclass for factories. Inherit this per dialogue type. Override
 * the constructor to initiate myDB and treeCellRenderer.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public abstract class TreeDataFactory {

    /**
     * Boolean to tell whether the tree is editable or not
     */
    protected boolean editable;

    /**
     * The tree cell renderer for the specific dialogue.
     */
    protected TTRenderer treeCellRenderer = null;

    /**
     * The tree cell editor for the specific dialogue.
     */
    protected TTEditor treeCellEditor = null;

    /**
     * The tree itself.
     */
    protected TableTreeComponent myTree = null;

    /**
     * The filter dialog window
     */
    protected FilterDialog myFilterDialog = null;

    /**
     * The forced name suffix, which must be in the beginning of each tree main
     * node name. This will be checked when adding a new tree main node or when
     * renaming it.
     */
    private String forcedNamePrefix = null;

    /**
     * Defines the maximum length for the name of a tree node. The length
     * depends on length of the name of the technology package and its revision,
     * so that the total length does not exceed the limit of the database field.
     */
    private int maximumTreeNodeNameLength = 256;

    /**
     * Return the editor for tree cells. OVERRIDE IF the type has special need
     * for cell editing. If only normal tree nodes, tables or panes are used,
     * there is no need to override.
     * 
     * @param theTree
     *            to be edited
     * @return tree cell editor to be used to edit the tree nodes
     */
    public TTEditor getTreeCellEditor(JTree theTree) {
  if (treeCellEditor == null) {
      treeCellEditor = new TTEditor(theTree);
  }
  return treeCellEditor;
    }

    /**
     * Return the renderer for tree cells. OVERRIDE IF the type has special need
     * for cell rendering. If only normal tree nodes, tables or panes are used,
     * there is no need to override.
     * 
     * @return tree cell renderer to be used to edit the tree nodes
     */
    public TTRenderer getTreeCellRenderer() {
  if (treeCellRenderer == null) {
      treeCellRenderer = new TTRenderer();
  }
  return treeCellRenderer;
    }

    /**
     * Returns a boolean telling whether the tree is editable or not
     * 
     * @return true if the tree is editable
     */
    public boolean isTreeEditable() {
  return editable;
    }

    /**
     * Build and return the tree model. OVERRIDE this method.
     * 
     * @return treeModel representing the data
     */
    public abstract TreeModel createAndGetModel();

    /**
     * A public vector that is used to keep track of all different table types
     * for each factory instance
     */
    public Vector<TableInformation> tableInformations = new Vector<TableInformation>();

    /**
     * Build an empty node, including all its sub nodes. Used when the user
     * inserts new nodes in the tree. OVERRIDE this method.
     * 
     * @return the new node
     */
    public abstract Object createEmptyNode();

    /**
     * Copies an existing node, including all its sub nodes. Used when the user
     * duplicates a node in the tree. OVERRIDE this method.
     * 
     * @param existingNode
     * @param newNode
     */
    public abstract void duplicateExistingNode(TreeMainNode existingNode,
      TreeMainNode newNode);
    
  /**
   * Helper function for creating table nodes. No need to override this.
   * 
   * @param rockData
   *          the data to be displayed in the table
   * @param tableModel
   *          the model describing the table
   * @return treeNode containing the name of the table and one child containing
   *         the table
   */
  protected DefaultMutableTreeNode createTableNode(Vector<Object> rockData, TTTableModel tableModel) {
    return createTableNode(rockData, tableModel, true);
  }

    /**
     * Helper function for creating table nodes. No need to override this.
     * 
     * @param rockData
     *            the data to be displayed in the table
     * @param tableModel
     *            the model describing the table
     * @param enableDuplicateRows
     *            True if the option to duplicate rows is enabled.
     * @return treeNode containing the name of the table and one child
     *         containing the table
     */
    protected DefaultMutableTreeNode createTableNode(Vector<Object> rockData,
      TTTableModel tableModel, final boolean enableDuplicateRows) {
  // Create the parent node with the heading
  DefaultMutableTreeNode theNode = new DefaultMutableTreeNode(tableModel
    .getTableName());

  // Create the table
  tableModel.setData(rockData);
  TableContainer table = new TableContainer(tableModel);
  tableModel.setEnableDuplicateRows(enableDuplicateRows);

  // Create the node for the table
  DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table);

  // Connect the heading node and the table node
  theNode.add(tableNode);

  return theNode;
    }

    /**
     * Helper function for creating parameter nodes. A parameter node contains
     * the specific parameters for the main level node. This may correspond to
     * one or more RockDBObjects. No need to override this.
     * 
     * @param parameterModel
     *            the model for the main node
     * @return treeNode containing the name of the node and one child with the
     *         parameters
     */
    protected DefaultMutableTreeNode createParameterNode(
      TTParameterModel parameterModel) {
  // Create the parameters heading node
  DefaultMutableTreeNode parameters = new DefaultMutableTreeNode(
    "Parameters");

  // Create the panel
  JPanel parametersPanel = parameterModel.createParametersPanel();

  // Create a node for the panel
  DefaultMutableTreeNode parametersNode = new DefaultMutableTreeNode(
    parametersPanel);

  // Connect all three nodes
  parameters.add(parametersNode);
  return parameters;
    }

    /**
     * Helper function for creating main nodes. A main node contains the heading
     * for the node, e.g. the name of the measurement type and a sub node with
     * the parameters. No need to override this.
     * 
     * @param parameterModel
     *            the model for the main node
     * @return treeNode containing the name of the node and one child with the
     *         parameters
     */
    protected DefaultMutableTreeNode createMainNode(
      TTParameterModel parameterModel) {
  // Create the parent node with the main node name as the heading
  DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(
    parameterModel.getMainNodeName());

  // Create a node for the panel
  DefaultMutableTreeNode parametersNode = createParameterNode(parameterModel);

  // Connect the nodes
  mainNode.add(parametersNode);

  return mainNode;
    }

    /**
     * Set the reference to the tree. No need to override this.
     * 
     * @param tableTreeComponent
     *            the new tree to be stored
     */
    public void setTree(TableTreeComponent tableTreeComponent) {
  this.myTree = tableTreeComponent;
    }

    /**
     * Retrieve the reference to the tree. No need to override this.
     * 
     * @return myTree
     */
    public TableTreeComponent getTree() {
  return this.myTree;
    }

    /**
     * Create and return a right click menu for tree nodes.
     * 
     * @param node
     *            the node to which the right click menu should be connected
     * @return the menu
     */
    public JPopupMenu nodeRightClickMenu(DefaultMutableTreeNode node) {

  JPopupMenu thisMenu = new JPopupMenu();
  JMenuItem menuItem;
  TreePopupMenuListener theListener = new TreePopupMenuListener(node,
    this);

  menuItem = new JMenuItem(TreePopupMenuListener.ADD_NODE);
  menuItem.addActionListener(theListener);
  thisMenu.add(menuItem);
  if (!isTreeEditable() || !isTreeNodeAddRemoveAllowed()) {
      menuItem.setEnabled(false);
  }

  menuItem = new JMenuItem(TreePopupMenuListener.REMOVE_NODE);
  menuItem.addActionListener(theListener);
  thisMenu.add(menuItem);
  if (node == null || !isTreeEditable() || !isTreeNodeAddRemoveAllowed()) {
      menuItem.setEnabled(false);
  }

  // menuItem = new JMenuItem(TreePopupMenuListener.RENAME_NODE);
  // menuItem.addActionListener(theListener);
  // thisMenu.add(menuItem);
  // if (!isTreeEditable() || !(node instanceof TreeMainNode)) {
  // menuItem.setEnabled(false);
  // }

  menuItem = new JMenuItem(TreePopupMenuListener.DUPLICATE_NODE);
  menuItem.addActionListener(theListener);
  thisMenu.add(menuItem);
  if (node == null || !isTreeEditable() || !isTreeNodeAddRemoveAllowed()
    || !(node instanceof TreeMainNode)) {
      menuItem.setEnabled(false);
  }

  menuItem = new JMenuItem(TreePopupMenuListener.SET_TYPE_FILTERS);
  menuItem.addActionListener(theListener);
  thisMenu.add(menuItem);

  menuItem = new JMenuItem(TreePopupMenuListener.CLEAR_ALL_TYPE_FILTERS);
  menuItem.addActionListener(theListener);
  thisMenu.add(menuItem);

  thisMenu.addSeparator();
  JMenu submenu = new JMenu("Expand/Collapse");

  menuItem = new JMenuItem(TreePopupMenuListener.EXPAND_NODE);
  menuItem.addActionListener(theListener);
  submenu.add(menuItem);
  if (node == null) {
      menuItem.setEnabled(false);
  }

  menuItem = new JMenuItem(TreePopupMenuListener.EXPAND_ALL_NODES);
  menuItem.addActionListener(theListener);
  submenu.add(menuItem);

  menuItem = new JMenuItem(TreePopupMenuListener.COLLAPSE_NODE);
  menuItem.addActionListener(theListener);
  submenu.add(menuItem);
  if (node == null || getTree().isCollapsed(getTree().getSelectionPath())) {
      menuItem.setEnabled(false);
  }

  menuItem = new JMenuItem(TreePopupMenuListener.COLLAPSE_ALL_NODES);
  menuItem.addActionListener(theListener);
  submenu.add(menuItem);

  thisMenu.add(submenu);

  return thisMenu;
    }
    
    /**
     * Create and return a right click menu for tree nodes.
     * 
     * @param multiple nodes
     *            the nodes to which the right click menu should be connected
     * @return the menu
     */
    public JPopupMenu nodesRightClickMenu(Vector<DefaultMutableTreeNode> node) {

  JPopupMenu thisMenu = new JPopupMenu();
  JMenuItem menuItem;
  TreePopupMenuListener theListener = new TreePopupMenuListener(node,
    this);

  menuItem = new JMenuItem(TreePopupMenuListener.REMOVE_NODES);
  menuItem.addActionListener(theListener);
  thisMenu.add(menuItem);
  if (node == null || !isTreeEditable() || !isTreeNodeAddRemoveAllowed()) {
      menuItem.setEnabled(false);
  }

  return thisMenu;
    }
    
    
    

    /**
     * Returns the filter dialog instance for this factory. A new instance is
     * created if it does not exist yet.
     * 
     * @return the filter dialog
     */
    public FilterDialog getFilterDialog() {
  if (myFilterDialog == null) {
      myFilterDialog = new FilterDialog(myTree, tableInformations);
  }
  return myFilterDialog;

    }

    /**
     * @return the forcedNamePrefix
     */
    public String getForcedNamePrefix() {
  return forcedNamePrefix;
    }

    /**
     * @param forcedNamePrefix
     *            the forcedNamePrefix to set
     */
    public void setForcedNamePrefix(String forcedNamePrefix) {
  this.forcedNamePrefix = forcedNamePrefix;
    }

    /**
     * @return the maximum length of the tree node name
     */
    public int getMaximumTreeNodeNameLength() {
  return maximumTreeNodeNameLength;
    }

    /**
     * Sets the maximum length for a tree node name
     * 
     * @param maximumTreeNodeNameLength
     */
    public void setMaximumTreeNodeNameLength(int maximumTreeNodeNameLength) {
  this.maximumTreeNodeNameLength = maximumTreeNodeNameLength;
    }

    /**
     * Gets if it is allowed to add/remove/duplicate tree main nodes. OVERRIDE
     * this if it should not be possible.
     * 
     * @return whether the adding and removing of tree main nodes is allowed
     */
    public boolean isTreeNodeAddRemoveAllowed() {
  // By default the tree main nodes are allowed to be added and removed.
  return true;
    }

}