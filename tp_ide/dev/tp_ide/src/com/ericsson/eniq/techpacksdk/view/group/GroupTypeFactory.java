package com.ericsson.eniq.techpacksdk.view.group;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TTRenderer;
import tableTree.TTTableModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class GroupTypeFactory extends TreeDataFactory {

  private Application application;

  /**
   * The rock factory is the interface towards the DB.
   */
  private RockFactory rockFactory = null;

  private GroupTypeDataModel groupTypeDataModel;

  private Versioning versioning;

  /**
   * The constructor initiates the renderer and DB interface for this specific
   * type of dialogue.
   * 
   * @param rockFactory
   *          Database connection
   * @param editable
   *          Boolean that checks if the group types are editable or not
   * @param versioning
   * @param baseversioning
   */
  private GroupTypeFactory(final RockFactory rockFactory, final boolean editable, final Versioning versioning) {
    this.rockFactory = rockFactory;
    this.editable = editable;
    this.versioning = versioning;
    treeCellRenderer = new TTRenderer();
    createTableInfoData();
    this.setMaximumTreeNodeNameLength(Grouptypes.getGrouptypeColumnSize());
  }

  /**
   * Constructs a concrete factory class for groupTypes.
   * 
   * @param application
   * @param groupTypeDataModel
   * @param editable
   * @param versioning
   * @param baseversioning
   */
  public GroupTypeFactory(final Application application, final GroupTypeDataModel groupTypeDataModel,
      final boolean editable, final Versioning versioning) {
    this(groupTypeDataModel.getRockFactory(), editable, versioning);
    this.application = application;
    this.groupTypeDataModel = groupTypeDataModel;
  }

  /**
   * Create a tree cell editor for the specific type of dialogue (groupType).
   * 
   * @param theTree
   * @return treeCellEditor.
   */
  @Override
  public TTEditor getTreeCellEditor(final JTree theTree) {
    if (treeCellEditor == null) {
      treeCellEditor = new TTEditor(theTree);
    }
    return treeCellEditor;
  }

  /**
   * Create the tree model for the tree and initiate it with data from the
   * database.
   * 
   * @return treeModel
   */
  @Override
  public TreeModel createAndGetModel() {

    // Create the root node
    final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    final DefaultTreeModel theModel = new DefaultTreeModel(root);

    groupTypeDataModel.setCurrentVersioning(versioning);
    groupTypeDataModel.refresh();
    final List<GroupTypeData> groups = groupTypeDataModel.getGroups();

    if (groups != null) {
      // Loop through the group types, get their data and add them to
      // the model
      for (final Iterator<GroupTypeData> iter = groups.iterator(); iter.hasNext();) {

        final TreeMainNode mainNode = createGrouptype(iter.next());
        root.add(mainNode);
      }
    }
    return theModel;
  }

  /**
   * Method for collecting info of the different table types
   */
  private void createTableInfoData() {
    tableInformations.addElement(GroupTypeColumnTableModel.createTableTypeInfo());
  }

  /**
   * Helper method for creating the group type node and all its children
   * 
   * @param grouptype
   * @return
   */
  private TreeMainNode createGrouptype(final GroupTypeData groupTypeData) {
    final GroupTable grouptable = groupTypeData.getGroupTable();
    final Vector<Object> myCols = new Vector<Object>();
    myCols.addAll(grouptable.getGroupTypeColumns());

    // Create the child node data for all nodes: parameters and columns
    final TTParameterModel parameterModel = new GroupTypeParameterModel(rockFactory, grouptable, editable);

    // Create the column model.
    final TTTableModel groupColumnModel = new GroupTypeColumnTableModel(application, rockFactory, tableInformations,
        editable, grouptable);

    // Set the observer/observable relationships
    parameterModel.addObserver(groupColumnModel);

    // Create the main node
    final TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
    final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
    final DefaultMutableTreeNode columnNode = createTableNode(myCols, groupColumnModel);

    // Connect the nodes
    mainNode.add(parameterNode);
    mainNode.add(columnNode);

    // Create deletion order
    final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
    nodeOrder.add(columnNode);
    nodeOrder.add(parameterNode);
    setDeletionOrder(mainNode, nodeOrder);

    return mainNode;
  }

  /**
   * Set deletion order, e.g. tables have to be deleted before the
   * parameterpanel due to groups in the data. Nodes are deleted in the order
   * that they are in the vector sent to mainNode.setDeletionOrder(vector).
   * Nodes that aren't included in the vector will be deleted last
   */
  private void setDeletionOrder(final TreeMainNode parentNode, final Vector<DefaultMutableTreeNode> nodes) {// NOPMD Leave as vector as abstract returns vector
    parentNode.setDeletionOrder(nodes);
  }

  /**
   * Overridden version of the corresponding TreeDataFactory method. Used for
   * creating new empty nodes when the user inserts nodes in a tree.
   */
  @Override
  public TreeMainNode createEmptyNode() {

    // Create new type and table objects
    final GroupTypeData emptyGroupType = new GroupTypeData(versioning);
    final GroupTable emptygrouptable = emptyGroupType.getGroupTable();
    final Vector<Object> myCols = new Vector<Object>();
    myCols.addAll(emptygrouptable.getGroupTypeColumns());

    final TTParameterModel parameterModel = new GroupTypeParameterModel(rockFactory, emptygrouptable, editable);

    final TTTableModel groupColumnModel = new GroupTypeColumnTableModel(application, rockFactory, tableInformations,
        editable, emptygrouptable);

    // Set the observer/observable relationships
    parameterModel.addObserver(groupColumnModel);

    // Create the main node and all the child nodes
    final TreeMainNode mainNode = new TreeMainNode(parameterModel);
    final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
    final DefaultMutableTreeNode columnNode = createTableNode(myCols, groupColumnModel);

    // Connect all the nodes
    mainNode.add(parameterNode);
    mainNode.add(columnNode);

    // Create deletion order
    final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
    nodeOrder.addElement(columnNode);
    nodeOrder.addElement(parameterNode);

    setDeletionOrder(mainNode, nodeOrder);

    return mainNode;
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }

  /**
   * Duplicates an existing tree node data to the new tree node. The new node
   * must have already been created in the tree, so that it has the empty child
   * nodes with default values.
   */
  @Override
  public void duplicateExistingNode(final TreeMainNode existingNode, final TreeMainNode newNode) {

    // Iterate through all the children.
    for (int i = 0; i < existingNode.getChildCount(); i++) {
      // Get both old and new children. The children must be in the same
      // order under both nodes.
      final Object oldChild = ((DefaultMutableTreeNode) existingNode.getChildAt(i).getChildAt(0)).getUserObject();
      final Object newChild = ((DefaultMutableTreeNode) newNode.getChildAt(i).getChildAt(0)).getUserObject();
      if (oldChild instanceof TableContainer) {
        // The node is a table

        // Get the table models for the old and new table
        final TTTableModel oldTableModel = ((TableContainer) oldChild).getTableModel();
        final TTTableModel newTableModel = ((TableContainer) newChild).getTableModel();

        // Copy all the rock objects in the table to the new table.

        final Vector<Object> data = oldTableModel.getData();
        for (int j = 0; j < data.size(); j++) {
          if (oldTableModel instanceof GroupTypeColumnTableModel) {

            final Grouptypes oldObj = (Grouptypes) data.elementAt(j);

            // Get the old rock object and clone it
            final Grouptypes newObj = (Grouptypes) oldTableModel.copyOf(oldObj);
            // Modify the keys of the clone to match the new
            newObj.setGrouptype(oldObj.getGrouptype());
            // Insert the clone to the new table
            newTableModel.insertDataLast(newObj);
          }
        }

        ((TableContainer) newChild).tuneSize();
      }
    }
  }
}
