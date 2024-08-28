package com.ericsson.eniq.techpacksdk.view.prompts;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
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
import tableTreeUtils.PairComponent;
import tableTreeUtils.ParameterPanel;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Prompt;
import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.distocraft.dc5000.repository.dwhrep.Promptoption;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Concrete factory class for prompt implementors.
 * 
 * @author eheijun
 * 
 */
public class PromptImplementorFactory extends TreeDataFactory {

	private Application application;

	/**
	 * The rock factory is the interface towards the DB.
	 */
	private RockFactory rockFactory = null;
	private PromptImplementorDataModel promptImplementorDataModel;

	/**
	 * The constructor initiates the renderer and DB interface for this specific
	 * type of dialogue.
	 */
	private PromptImplementorFactory(RockFactory rockFactory, boolean editable) {
		this.rockFactory = rockFactory;
		this.editable = editable;
		treeCellRenderer = new PromptImplementorRenderer();
		createTableInfoData(); // Collect info about the different table types used
		this.setMaximumTreeNodeNameLength(Promptimplementor.getPromptclassnameColumnSize());   
	}

	public PromptImplementorFactory(Application application,
			PromptImplementorDataModel promptImplementorDataModel,
			boolean editable) {
		this(promptImplementorDataModel.getRockFactory(), editable);
		this.application = application;
		this.promptImplementorDataModel = promptImplementorDataModel;
	}

	private Versioning versioning;

	private int implementorCount = 0;

	/**
	 * Create a tree cell editor for the specific type of dialogue
	 * (PromptImplementor).
	 * 
	 * @param theTree
	 * @return treeCellEditor.
	 */
	public TTEditor getTreeCellEditor(final JTree theTree) {
		if (treeCellEditor == null) {
			treeCellEditor = new PromptImplementorEditor(theTree);
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

		promptImplementorDataModel.setCurrentVersioning(versioning);
		promptImplementorDataModel.refresh();
		final List<PromptImplementorData> implementorData = promptImplementorDataModel
				.getPromptimplementors();
		implementorCount = 0;

		if (implementorData != null) {
			// Loop through the implementors, get their data and add them to
			// the model
			for (final Iterator<PromptImplementorData> iter = implementorData
					.iterator(); iter.hasNext();) {

				final TreeMainNode mainNode = createPromptImplementor(iter
						.next());
				implementorCount++;
				root.add(mainNode);
			}
		}
		return theModel;
	}

	/**
	 * Method for collecting info of the different table types
	 */
	protected void createTableInfoData() {
		tableInformations.addElement(PromptImplementorPromptTableModel
				.createTableTypeInfo());
		tableInformations.addElement(PromptImplementorPromptOptionTableModel
				.createTableTypeInfo());
	}

	/**
	 * Helper method for creating the reference type node and all its children
	 * 
	 * @param PromptImplementor
	 * @return
	 */
	private TreeMainNode createPromptImplementor(
			final PromptImplementorData promptImplementorData) {

		// Create the child node data for all three nodes: parameters, prompts
		// and prompt options
		final TTParameterModel parameterModel = new PromptImplementorParameterModel(
				application, promptImplementorData, rockFactory, editable);
		final Promptimplementor promptimplementor = promptImplementorData
				.getPromptimplementor();
		final Vector<Object> prompts = promptImplementorData.getPrompts();
		final Vector<Object> promptoptions = promptImplementorData
				.getPromptoptions();

		// Create the main node
		final TreeMainNode mainNode = new TreeMainNode(parameterModel);

		// Create the child nodes
		final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
		final DefaultMutableTreeNode promptNode = createTableNode(prompts,
				new PromptImplementorPromptTableModel(application, rockFactory,
						tableInformations, editable, promptimplementor));
		final DefaultMutableTreeNode promptOptionNode = createTableNode(
				promptoptions, new PromptImplementorPromptOptionTableModel(
						application, rockFactory, tableInformations, editable,
						promptimplementor));

		// Connect the nodes
		mainNode.add(parameterNode);
		mainNode.add(promptNode);
		mainNode.add(promptOptionNode);

		// Create deletion order
		final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
		nodeOrder.addElement(promptOptionNode);
		nodeOrder.addElement(promptNode);
		nodeOrder.addElement(parameterNode);

		setDeletionOrder(mainNode, nodeOrder);

		implementorCount++;

		return mainNode;
	}

	/**
	 * Overridden version of the corresponding TreeDataFactory method. Used for
	 * creating new empty nodes when the user inserts nodes in a tree.
	 */
	public TreeMainNode createEmptyNode() {
		// Create new type and table objects
		final PromptImplementorData emptyPromptImplementor = new PromptImplementorData(
				versioning, rockFactory, Integer.valueOf(implementorCount++));

		// Create the empty data sets for the child nodes
		final TTParameterModel compositeData = new PromptImplementorParameterModel(
				application, emptyPromptImplementor, rockFactory, editable);
		final Promptimplementor promptimplementor = emptyPromptImplementor
				.getPromptimplementor();
		final Vector<Object> prompts = emptyPromptImplementor.getPrompts();
		final Vector<Object> promptoptions = emptyPromptImplementor
				.getPromptoptions();

		// Create the main node and all the child nodes
		final TreeMainNode mainNode = new TreeMainNode(compositeData);
		final DefaultMutableTreeNode parameterNode = createParameterNode(compositeData);
		final DefaultMutableTreeNode promptNode = createTableNode(prompts,
				new PromptImplementorPromptTableModel(application, rockFactory,
						tableInformations, editable, promptimplementor));
		final DefaultMutableTreeNode promptOptionNode = createTableNode(
				promptoptions, new PromptImplementorPromptOptionTableModel(
						application, rockFactory, tableInformations, editable,
						promptimplementor));

		// Connect all the nodes
		mainNode.add(parameterNode);
		mainNode.add(promptNode);
		mainNode.add(promptOptionNode);

		// Create deletion order
		final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
		nodeOrder.addElement(promptOptionNode);
		nodeOrder.addElement(promptNode);
		nodeOrder.addElement(parameterNode);

		setDeletionOrder(mainNode, nodeOrder);

		return mainNode;
	}

	/**
	 * Set deletion order, e.g. tables have to be deleted before the parameter
	 * panel due to references in the data. Nodes are deleted in the order that
	 * they are in the vector sent to mainNode.setDeletionOrder(vector). Nodes
	 * that aren't included in the vector will be deleted last
	 */
	private void setDeletionOrder(final TreeMainNode parentNode,
			final Vector<DefaultMutableTreeNode> nodes) {
		parentNode.setDeletionOrder(nodes);
	}

	/**
	 * @return the versioning RockDBObject
	 */
	public Versioning getVersioning() {
		return versioning;
	}

	/**
	 * Sets the versioning RockDBObject
	 * 
	 * @param versioning
	 */
	public void setVersioning(final Versioning versioning) {
		this.versioning = versioning;
	}

	/**
	 * Duplicates an existing tree node data to the new tree node. The new node
	 * must have already been created in the tree, so that it has the empty
	 * child nodes with default values.
	 */
	@Override
	public void duplicateExistingNode(TreeMainNode existingNode,
			TreeMainNode newNode) {

		// First we need to get the new prompt implementor id from the new node.
		// This value is needed when cloning the old prompt and prompt option
		// rock objects.
		int promptImplementorId = -1;
		for (int i = 0; i < newNode.getChildCount(); i++) {
			Object child = ((DefaultMutableTreeNode) newNode.getChildAt(i)
					.getChildAt(0)).getUserObject();
			if (child instanceof ParameterPanel) {
				PromptImplementorParameterModel model = (PromptImplementorParameterModel) ((ParameterPanel) child)
						.getModel();
				promptImplementorId = ((Promptimplementor) model
						.getPromptimplementor()).getPromptimplementorid();
			}
		}

		// Iterate through all the children.
		for (int i = 0; i < existingNode.getChildCount(); i++) {
			// Get both old and new children. The children must be in the same
			// order under both nodes.
			Object oldChild = ((DefaultMutableTreeNode) existingNode
					.getChildAt(i).getChildAt(0)).getUserObject();
			Object newChild = ((DefaultMutableTreeNode) newNode.getChildAt(i)
					.getChildAt(0)).getUserObject();

			// Copy the data from old child to the new based on the type of the
			// child.
			if (oldChild instanceof ParameterPanel) {
				// The node is a parameter panel
				PromptImplementorParameterModel oldParameterModel = (PromptImplementorParameterModel) ((ParameterPanel) oldChild)
						.getModel();
        // PromptImplementorParameterModel newParameterModel =
        // (PromptImplementorParameterModel) ((ParameterPanel) newChild)
        // .getModel();

				// Get the priority value from old node.
				String priority = (String) oldParameterModel
						.getValueAt(PromptImplementorParameterModel.PRIORITY);

				// Set the value in the new node
				//
				// NOTE: If setValueAt is used, then the model is updated, but
				// the combo box in the panel will show the old selection.
				// newParameterModel.setValueAt(priority,
				// PromptImplementorParameterModel.PRIORITY);
				//
				// Update the selected value in the combobox
				int ind = 0;
				for (int j = 0; j < Constants.PROMPTPRIORITY_TEXT.length; j++) {
					if (Constants.PROMPTPRIORITY_TEXT[j] == (priority)) {
						ind = j;
						break;
					}
				}
				PairComponent comp = (PairComponent) ((ParameterPanel) newChild)
						.getComponent(0);
				((JComboBox) comp.getComponent()).setSelectedIndex(ind);

			} else if (oldChild instanceof TableContainer) {
				// The node is a table

				// Get the table models for the old and new table
				TTTableModel oldTableModel = ((TableContainer) oldChild)
						.getTableModel();
				TTTableModel newTableModel = ((TableContainer) newChild)
						.getTableModel();

				// Copy all the rock objects in the table to the new table.

				Vector<Object> data = oldTableModel.getData();
				for (int j = 0; j < data.size(); j++) {
					if (oldTableModel instanceof PromptImplementorPromptTableModel) {
						// Get the old rock object and clone it
						Prompt oldObj = (Prompt) data.elementAt(j);
						Prompt newObj = (Prompt) oldTableModel.copyOf(oldObj);
						// Modify the id of the clone to match the new
						newObj.setPromptimplementorid(promptImplementorId);
						// Insert the clone to the new table
						newTableModel.insertDataLast(newObj);
					} else if (oldTableModel instanceof PromptImplementorPromptOptionTableModel) {
						// Get the old rock object and clone it
						Promptoption oldObj = (Promptoption) data.elementAt(j);
						Promptoption newObj = (Promptoption) oldTableModel
								.copyOf(oldObj);
						// Modify the id of the clone to match the new
						newObj.setPromptimplementorid(promptImplementorId);
						// Insert the clone to the new table
						newTableModel.insertDataLast(newObj);
					}
				}

				// Refresh table so that it is correctly drawn after copying the
				// data
				((TableContainer) newChild).tuneSize();
			}
		}
	}
	
}
