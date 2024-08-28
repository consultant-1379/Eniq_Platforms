package com.ericsson.eniq.techpacksdk.view.aggregation;

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
import tableTree.TTTableModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * Concrete factory class for Aggregations.
 * 
 * @author eheijun
 * 
 */
public class AggregationDataFactory extends TreeDataFactory {

  private Application application;

  /**
   * The rock factory is the interface towards the DB.
   */
  private RockFactory rockFactory = null;

  private AggregationDataModel aggregationDataModel;

  /**
   * The constructor initiates the renderer and DB interface for this specific
   * type of dialogue.
   */
  private AggregationDataFactory(RockFactory rockFactory, boolean editable) {
    this.rockFactory = rockFactory;
    this.editable = editable;
    treeCellRenderer = new AggregationRenderer();
    createTableInfoData(); // Collect info about the different table types
    // used
    this.setMaximumTreeNodeNameLength(Aggregation.getAggregationColumnSize());
  }

  public AggregationDataFactory(Application application, AggregationDataModel aggregationDataModel, boolean editable) {
    this(aggregationDataModel.getRockFactory(), editable);
    this.application = application;
    this.aggregationDataModel = aggregationDataModel;
  }

  private Versioning versioning;

  private Versioning baseversioning;

  /**
   * Create a tree cell editor for the specific type of dialogue (Aggregation).
   * 
   * @param theTree
   * @return treeCellEditor.
   */
  public TTEditor getTreeCellEditor(final JTree theTree) {
    if (treeCellEditor == null) {
      treeCellEditor = new AggregationEditor(theTree);
    }
    return treeCellEditor;
  }

  /**
   * Create the tree model for the tree and initiate it with data from the
   * database.
   * 
   * @return treeModel
   */
  public TreeModel createAndGetModel() {

    // Create the root node
    final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    final DefaultTreeModel theModel = new DefaultTreeModel(root);

    aggregationDataModel.setCurrentVersioning(versioning);
    aggregationDataModel.setBaseVersioning(baseversioning);
    aggregationDataModel.refresh();
    final List<AggregationData> aggregations = aggregationDataModel.getAggregations();

    if (aggregations != null) {
      // Loop through the aggregations, get their data and add them to
      // the model
      for (final Iterator<AggregationData> iter = aggregations.iterator(); iter.hasNext();) {

        final TreeMainNode mainNode = createAggregation(iter.next());
        root.add(mainNode);
      }
    }
    return theModel;
  }

  /**
   * Method for collecting info of the different table types
   */
  protected void createTableInfoData() {
    tableInformations.addElement(AggregationRuleTableModel.createTableTypeInfo());
  }

  /**
   * Helper method for creating the reference type node and all its children
   * 
   * @param referencetype
   * @return
   */
  private TreeMainNode createAggregation(final AggregationData aggregationData) {

    final Aggregation aggregation = aggregationData.getAggregation();
    final Vector<Object> myColumns = aggregationData.getRules();

    // Create the child node data for all nodes: parameters and columns
    final TTParameterModel parameterModel = new AggregationParameterModel(application, aggregationData.getVersioning(),
        aggregation, rockFactory, editable);
    final TTTableModel aggregationRuleModel = new AggregationRuleTableModel(application, rockFactory,
        tableInformations, editable, aggregation);

    // Set the observer/observable relationships
    parameterModel.addObserver(aggregationRuleModel);

    // Create the main node
    final TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
    final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
    final DefaultMutableTreeNode columnNode = createTableNode(myColumns, aggregationRuleModel);

    // Connect the nodes
    mainNode.add(parameterNode);
    mainNode.add(columnNode);

    // Create deletion order
    final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
    nodeOrder.addElement(columnNode);
    nodeOrder.addElement(parameterNode);

    setDeletionOrder(mainNode, nodeOrder);

    return mainNode;
  }

  /**
   * Set deletion order, e.g. tables have to be deleted before the
   * parameterpanel due to references in the data. Nodes are deleted in the
   * order that they are in the vector sent to
   * mainNode.setDeletionOrder(vector). Nodes that aren't included in the vector
   * will be deleted last
   */
  private void setDeletionOrder(final TreeMainNode parentNode, final Vector<DefaultMutableTreeNode> nodes) {
    parentNode.setDeletionOrder(nodes);
  }

  /**
   * Overridden version of the corresponding TreeDataFactory method. Used for
   * creating new empty nodes when the user inserts nodes in a tree.
   */
  public TreeMainNode createEmptyNode() {
    // Create new type and table objects
    final AggregationData emptyAggregation = new AggregationData(versioning, rockFactory);
    final Aggregation aggregation = emptyAggregation.getAggregation();
    final Vector<Object> myColumns = emptyAggregation.getRules();

    // Create the empty data sets for the child nodes
    final TTParameterModel compositeData = new AggregationParameterModel(application, emptyAggregation.getVersioning(),
        aggregation, rockFactory, editable);
    final TTTableModel referenceColumnModel = new AggregationRuleTableModel(application, rockFactory,
        tableInformations, editable, aggregation);

    // Set the observer/observable relationships
    compositeData.addObserver(referenceColumnModel);

    // Create the main node and all the child nodes
    final TreeMainNode mainNode = new TreeMainNode(compositeData);
    final DefaultMutableTreeNode parameterNode = createParameterNode(compositeData);
    final DefaultMutableTreeNode columnNode = createTableNode(myColumns, referenceColumnModel);

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

  public Versioning getBaseVersioning() {
    return baseversioning;
  }

  public void setBaseVersioning(final Versioning baseversioning) {
    this.baseversioning = baseversioning;
  }

  /**
   * Duplicates an existing tree node data to the new tree node. The new node
   * must have already been created in the tree, so that it has the empty child
   * nodes with default values.
   */
  @Override
  public void duplicateExistingNode(TreeMainNode existingNode, TreeMainNode newNode) {
  }

}
