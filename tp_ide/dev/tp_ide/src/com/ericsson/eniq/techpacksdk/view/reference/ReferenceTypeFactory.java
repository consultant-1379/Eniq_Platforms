package com.ericsson.eniq.techpacksdk.view.reference;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
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
import tableTreeUtils.DescriptionComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.ParameterPanel;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Concrete factory class for ReferenceTypes.
 * 
 * @author eheijun
 * 
 */
public class ReferenceTypeFactory extends TreeDataFactory {

  private Application application;

  /**
   * The rock factory is the interface towards the DB.
   */
  private RockFactory rockFactory = null;

  private ReferenceTypeDataModel referenceTypeDataModel;

  /**
   * The constructor initiates the renderer and DB interface for this specific type of dialogue.
   * 
   * @param rockFactory
   *          Database connection
   * @param editable
   *          Boolean that checks if the reference types are editable or not
   * @param versioning
   * @param baseversioning
   */
  private ReferenceTypeFactory(final RockFactory rockFactory, final boolean editable, Versioning versioning,
      Versioning baseversioning) {
    this.rockFactory = rockFactory;
    this.editable = editable;
    this.versioning = versioning;
    this.baseversioning = baseversioning;
    treeCellRenderer = new ReferenceTypeRenderer();
    createTableInfoData(versioning.getTechpack_type()); // Collect info about
                                                        // the different table
    // types
    // used
    this.setMaximumTreeNodeNameLength(Referencetable.getTypenameColumnSize());
  }

  /**
   * Constructs a concrete factory class for ReferenceTypes.
   * 
   * @param application
   * @param referenceTypeDataModel
   * @param editable
   * @param versioning
   * @param baseversioning
   */
  public ReferenceTypeFactory(final Application application, final ReferenceTypeDataModel referenceTypeDataModel,
      final boolean editable, Versioning versioning, Versioning baseversioning) {
    this(referenceTypeDataModel.getRockFactory(), editable, versioning, baseversioning);
    this.application = application;
    this.referenceTypeDataModel = referenceTypeDataModel;
  }

  private Versioning versioning;

  private Versioning baseversioning;

  /**
   * Create a tree cell editor for the specific type of dialogue
   * (ReferenceType).
   * 
   * @param theTree
   * @return treeCellEditor.
   */
  @Override
  public TTEditor getTreeCellEditor(final JTree theTree) {
    if (treeCellEditor == null) {
      treeCellEditor = new ReferenceTypeEditor(theTree);
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

    referenceTypeDataModel.setCurrentVersioning(versioning);
    referenceTypeDataModel.setBaseVersioning(baseversioning);
    referenceTypeDataModel.refresh();
    final List<ReferenceTypeData> references = referenceTypeDataModel.getReferences();

    if (references != null) {
      // Loop through the reference types, get their data and add them to
      // the model
      for (final Iterator<ReferenceTypeData> iter = references.iterator(); iter.hasNext();) {

        final TreeMainNode mainNode = createReferencetype(iter.next());
        root.add(mainNode);
      }
    }
    return theModel;
  }

  /**
   * Method for collecting info of the different table types
   */
  protected void createTableInfoData(String techpackType) {
    tableInformations.addElement(ReferenceTypeColumnTableModelFactory.createTableTypeInfo(techpackType));
  }

  /**
   * Helper method for creating the reference type node and all its children
   * 
   * @param referencetype
   * @return
   */
  private TreeMainNode createReferencetype(final ReferenceTypeData referenceTypeData) {

    final Referencetable referencetable = referenceTypeData.getReferencetable();
    final Vector<Referencecolumn> myPublicColumns = referenceTypeData.getPubliccolumns();
    final Vector<Object> myColumns = referenceTypeData.getReferencecolumns();

    // Create the child node data for all nodes: parameters and columns
    final TTParameterModel parameterModel = new ReferenceTypeParameterModel(application, referenceTypeData
        .getVersioning(), referencetable, rockFactory, editable);
    // final TTTableModel referenceColumnModel = new
    // ReferenceTypeColumnTableModel(application, rockFactory,
    // tableInformations, editable, referencetable, myPublicColumns);

    // Check if this techpack is a base techpack.
    boolean isBaseTechPack = false;
    if (versioning.getTechpack_type().equalsIgnoreCase("base")) {
      isBaseTechPack = true;
    }

    // Create the column model.
    final TTTableModel referenceColumnModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInformations, editable, referencetable, isBaseTechPack, myPublicColumns,
        versioning.getTechpack_type());

    // Set the observer/observable relationships
    parameterModel.addObserver(referenceColumnModel);

    // Create the main node
    final TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
    final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
    final DefaultMutableTreeNode columnNode = createTableNode(myColumns, referenceColumnModel);

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
  @Override
  public TreeMainNode createEmptyNode() {
    // Create new type and table objects
    final ReferenceTypeData emptyReferenceType = new ReferenceTypeData(versioning, rockFactory);
    final Referencetable referencetable = emptyReferenceType.getReferencetable();

    // Get the public columns for the reference type from the base techpack.
    // If this is a base techpack, then public columns is set to null.
    Vector<Referencecolumn> myPublicColumns = null;
    if (!versioning.getTechpack_type().equalsIgnoreCase("base")) {
      myPublicColumns = new Vector<Referencecolumn>(referenceTypeDataModel.getPublicColumnsForReferencetype());
    }

    final Vector<Object> myColumns = emptyReferenceType.getReferencecolumns();

    // Check if this techpack is a base techpack.
    boolean isBaseTechPack = false;
    if (versioning.getTechpack_type().equalsIgnoreCase("base")) {
      isBaseTechPack = true;
    }

    // Create the empty data sets for the child nodes
    final TTParameterModel compositeData = new ReferenceTypeParameterModel(application, emptyReferenceType
        .getVersioning(), referencetable, rockFactory, editable);
    // final TTTableModel referenceColumnModel = new
    // ReferenceTypeColumnTableModel(application, rockFactory,
    // tableInformations, editable, referencetable, myPublicColumns);
    final TTTableModel referenceColumnModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInformations, editable, referencetable, isBaseTechPack, myPublicColumns,
        versioning.getTechpack_type());

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
  public void duplicateExistingNode(final TreeMainNode existingNode, final TreeMainNode newNode) {

    // Vector for storing a list of public columns for reference tables.
    // Vector<Referencecolumn> publicColumns = null;

    // First we need to get the new type id from the new node.
    // This value is needed when cloning the old reference table and
    // reference column rock objects.
    String newTypeId = null;
    for (int i = 0; i < newNode.getChildCount(); i++) {
      final Object child = ((DefaultMutableTreeNode) newNode.getChildAt(i).getChildAt(0)).getUserObject();
      if (child instanceof ParameterPanel) {
        final ReferenceTypeParameterModel model = (ReferenceTypeParameterModel) ((ParameterPanel) child).getModel();
        newTypeId = (model.getReferencetable()).getTypeid();
      }
    }

    // Iterate through all the children.
    for (int i = 0; i < existingNode.getChildCount(); i++) {
      // Get both old and new children. The children must be in the same
      // order under both nodes.
      final Object oldChild = ((DefaultMutableTreeNode) existingNode.getChildAt(i).getChildAt(0)).getUserObject();
      final Object newChild = ((DefaultMutableTreeNode) newNode.getChildAt(i).getChildAt(0)).getUserObject();

      // Copy the data from old child to the new based on the type of the
      // child.
      if (oldChild instanceof ParameterPanel) {
        // The node is a parameter panel

        // Get the old model
        final ReferenceTypeParameterModel oldParameterModel = (ReferenceTypeParameterModel) ((ParameterPanel) oldChild)
            .getModel();

        // Get the public columns from the reference data model
        // publicColumns =
        // referenceTypeDataModel.getPublicColumnsForReferencetype();

        // Get the values from old node.
        final String oldUpdateMethod = (String) oldParameterModel.getValueAt(ReferenceTypeParameterModel.UPDATEMETHOD);
        final int oldDataFormatSupport = (Integer) oldParameterModel
            .getValueAt(ReferenceTypeParameterModel.DATAFORMATSUPPORT);
        final String oldDescription = (String) oldParameterModel.getValueAt(ReferenceTypeParameterModel.DESCRIPTION);

        // Set the values in the new node
        //
        // NOTE: If setValueAt is used, then the model is updated, but
        // the combo box in the panel will show the old selection.
        // newParameterModel.setValueAt(priority,
        // PromptImplementorParameterModel.PRIORITY);
        //
        // Update the selected value in the update method combo box
        int ind = 0;
        for (int j = 0; j < Constants.UPDATE_METHODS_TEXT.length; j++) {
          if (Constants.UPDATE_METHODS_TEXT[j] == (oldUpdateMethod)) {
            ind = j;
            break;
          }
        }
        PairComponent comp = (PairComponent) ((ParameterPanel) newChild).getComponent(0);
        ((JComboBox) comp.getComponent()).setSelectedIndex(ind);

        // Update the value in the data format support check box
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(1);
        ((JCheckBox) comp.getComponent()).setSelected(oldDataFormatSupport == 1);

        // Update the description field
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(2);
        (((DescriptionComponent) comp.getComponent()).getTextField()).setText(oldDescription);

      } else if (oldChild instanceof TableContainer) {
        // The node is a table

        // Get the table models for the old and new table
        final TTTableModel oldTableModel = ((TableContainer) oldChild).getTableModel();
        final TTTableModel newTableModel = ((TableContainer) newChild).getTableModel();

        // Copy all the rock objects in the table to the new table.

        final Vector<Object> data = oldTableModel.getData();
        for (int j = 0; j < data.size(); j++) {
          if (oldTableModel instanceof ReferenceTypeColumnTableModel) {

            final Referencecolumn oldObj = (Referencecolumn) data.elementAt(j);

            // // Check if the old reference column is a public column
            // boolean isPublic = false;
            // Iterator<Referencecolumn> iter = publicColumns.iterator();
            // while (iter.hasNext()) {
            // Referencecolumn currentRefColumn = iter.next();
            // if (currentRefColumn.getTypeid().equals(oldObj.getTypeid())) {
            // isPublic = true;
            // System.out.println("Column with TypeId: " + oldObj.getTypeid() +
            // " is public.");
            // break;
            // }
            // }
            //
            // // If the reference column is a public column, then it
            // // will not be cloned, as it will automatically be added
            // // when changes are saved to the DB. If it is not
            // // public, then a clone will be added to the new table.
            // if (!isPublic) {
            // System.out.println("Column with TypeId: " + oldObj.getTypeid() +
            // " is not public. Cloning.");

            // Get the old rock object and clone it
            final Referencecolumn newObj = (Referencecolumn) oldTableModel.copyOf(oldObj);
            // Modify the keys of the clone to match the new
            newObj.setTypeid(newTypeId);
            // Insert the clone to the new table
            newTableModel.insertDataLast(newObj);

            // }
          }
        }

        // Refresh table so that it is correctly drawn after copying the
        // data
        ((TableContainer) newChild).tuneSize();
      }
    }
  }

}
