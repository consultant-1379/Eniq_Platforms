package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete factory class for ETL Sets.
 * 
 * @author ejarsav
 * 
 */
public class ETLFactory extends TreeDataFactory {

	private static final Logger logger = Logger.getLogger(ETLFactory.class
			.getName());

	public static int N_OF_TYPES = 10;

	public static int N_OF_COUNTERS = 40;

	public static int N_OF_KEYS = 40;

	private String setName = "";

	private String setVersion = "";

	private String type = "N/A";

  private String techpackType = "";

	private Vector<Meta_collections> removeList = new Vector<Meta_collections>();

	private final ETLSetHandlingDataModel etlSetHandlingDataModel;

	/**
	 * The rock factory is the interface towards the DB.
	 */
	private RockFactory rockFactory = null;

	/**
	 * The constructor initiates the renderer and DB interface for this specific
	 * type of dialogue.
	 */
  public ETLFactory(ETLSetHandlingDataModel etlSetHandlingDataModel) {
		createTableInfoData(); // Collect info about the different table types
		// used
		this.etlSetHandlingDataModel = etlSetHandlingDataModel;
		treeCellRenderer = new ETLRenderer();
		this.rockFactory = etlSetHandlingDataModel.getRockFactory();
	}

	/**
	 * Create a tree cell editor for the specific type of dialogue
	 * (MeasurementType).
	 * 
	 * @param theTree
	 * @return treeCellEditor.
	 */
	@Override
  public TTEditor getTreeCellEditor(JTree theTree) {
		if (treeCellEditor == null) {
			treeCellEditor = new ETLEditor(theTree);
		}
		return treeCellEditor;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public void setSetVersion(String setVersion) {
		this.setVersion = setVersion;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	private class cmpMetaCollections implements Comparator<Object> {

		@Override
    public int compare(Object o1, Object o2) {
			Meta_collections d1 = (Meta_collections) o1;
			Meta_collections d2 = (Meta_collections) o2;
			return d1.getCollection_name().compareTo(d2.getCollection_name());
		}
	}

	/**
	 * Create the tree model for the tree and initiate it with data from the
	 * database.
	 * 
	 * @return treeModel
	 */
	@Override
  public TreeModel createAndGetModel() {

		etlSetHandlingDataModel.setTechpack(setName, setVersion);

		// Create the root node
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(setName);
		DefaultTreeModel theModel = new DefaultTreeModel(root);

		// reads all the sets for one interface(techpack)

		Vector<Meta_collections> sets = etlSetHandlingDataModel.getAllSets();

		Collections.sort(sets, new cmpMetaCollections());

		// Loop through the measurement types, get their data and add them to
		// the model
		for (int i = 0; i < sets.size(); i++) {

			TreeMainNode mainNode = createSet(sets.elementAt(i));
			root.add(mainNode);
		}
		return theModel;
	}

	/**
	 * Method for collecting info of the different table types
	 */
	protected void createTableInfoData() {
		tableInformations.addElement(ETLActionTableModel.createTableTypeInfo());
		tableInformations.addElement(ETLSchedulerTableModel
				.createTableTypeInfo());
	}

	/**
	 * Helper method for creating the measurement type node and all its children
	 * 
	 * @param measurementtype
	 * @return
	 */
	private TreeMainNode createSet(Meta_collections mc) {

		// Create the child node data for all three nodes: sets, actions and
		// schedulings
		TTParameterModel parameterModel = new ETLSetModel(removeList, mc,
				rockFactory, editable);

		Vector<Object> myActions = etlSetHandlingDataModel.getAllActions(mc);
		Vector<Object> mySchedulings = etlSetHandlingDataModel
				.getAllSchedulings(mc);

		// Create the main node
		TreeMainNode mainNode = new TreeMainNode(parameterModel);

		// Create the child nodes
		DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
		DefaultMutableTreeNode actionNode = createTableNode(myActions,
				new ETLActionTableModel(mc, rockFactory, tableInformations,
						editable,techpackType));
		DefaultMutableTreeNode schedulingNode = createTableNode(mySchedulings,
				new ETLSchedulerTableModel(mc, rockFactory, tableInformations,
						editable));

		// Connect the nodes
		mainNode.add(parameterNode);
		mainNode.add(actionNode);
		mainNode.add(schedulingNode);

		return mainNode;
	}

	/**
	 * Overridden version of the corresponding TreeDataFactory method. Used for
	 * creating new empty nodes when the user inserts nodes in a tree.
	 */
	@Override
  public TreeMainNode createEmptyNode() {

		Meta_collection_sets mcSet = null;
		Meta_collections mcol = null;

		try {

			Meta_collection_sets mcs = new Meta_collection_sets(rockFactory);
			mcs.setCollection_set_name(setName);
			mcs.setVersion_number(setVersion);
			Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(
					rockFactory, mcs, true);
			mcSet = mcsF.getElementAt(0);

			if (mcSet != null) {

				mcol = new Meta_collections(rockFactory);
				mcol.setCollection_set_id(mcSet.getCollection_set_id());
				mcol.setVersion_number(mcSet.getVersion_number());
				mcol.setCollection_id(-1L);
				mcol.setMax_errors(0L);
				mcol.setMax_fk_errors(0l);
				mcol.setMax_col_limit_errors(0l);
				mcol.setCheck_fk_error_flag("N");
				mcol.setCheck_col_limits_flag("N");
				mcol.setPriority(0L);
				mcol.setQueue_time_limit(0L);
				mcol.setEnabled_flag("Y");
				mcol.setHold_flag("N");

			} else {

				long l = Utils.getTPMaxID(rockFactory) + 1;

				mcSet = new Meta_collection_sets(rockFactory);

				mcSet.setCollection_set_id(l);
				mcSet.setCollection_set_name(setName);
				mcSet.setDescription("");
				mcSet.setVersion_number(setVersion);
				mcSet.setEnabled_flag("Y");
				mcSet.setType(type);

				mcSet.saveDB();

				mcol = new Meta_collections(rockFactory);
				mcol.setCollection_set_id(l);
				mcol.setVersion_number(setVersion);
				mcol.setCollection_id(-1L);
				mcol.setMax_errors(0L);
				mcol.setMax_fk_errors(0l);
				mcol.setMax_col_limit_errors(0l);
				mcol.setCheck_fk_error_flag("N");
				mcol.setCheck_col_limits_flag("N");
				mcol.setPriority(0L);
				mcol.setQueue_time_limit(0L);
				mcol.setEnabled_flag("Y");
				mcol.setHold_flag("N");

			}

		} catch (Exception e) {
			logger.warning(e.getMessage());
		}

		// Create the child node data for all three nodes: sets, actions and
		// schedulings
		TTParameterModel parameterModel = new ETLSetModel(removeList, mcol,
				rockFactory, editable);

		parameterModel.setMainNodeName("NEW SET");

		Vector<Object> myActions = new Vector<Object>();
		Vector<Object> mySchedulings = new Vector<Object>();

		// Create the main node
		TreeMainNode mainNode = new TreeMainNode(parameterModel);

		// Create the child nodes
		DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);

		DefaultMutableTreeNode actionNode = createTableNode(myActions,
				new ETLActionTableModel(mcol, rockFactory, tableInformations,
						editable, techpackType));

		DefaultMutableTreeNode schedulingNode = createTableNode(mySchedulings,
				new ETLSchedulerTableModel(mcol, rockFactory,
						tableInformations, editable));

		// Connect the nodes
		mainNode.add(parameterNode);
		mainNode.add(actionNode);
		mainNode.add(schedulingNode);

		return mainNode;

	}

	public Vector<Meta_collections> getRemoveList() {
		return removeList;
	}

	public void setRemoveList(Vector<Meta_collections> removeList) {
		this.removeList = removeList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

  public void setTechpackType(final String techpackType) {
    this.techpackType = techpackType;
  }

	/**
	 * Duplicates an existing tree node data to the new tree node
	 */
	@Override
	public void duplicateExistingNode(TreeMainNode existingNode,
			TreeMainNode newNode) {

		// TODO: Implementation still missing for duplicate
		System.out.println("duplicateExistingNode(): Not implemented yet. "
				+ "Data is not copied to the new empty node.");
		JOptionPane
				.showMessageDialog(null,
						"Not implemented yet! Data is not copied to the new empty node.");

		//
		//
		// // First we need to get the new type id from the new node. This value
		// is
		// // needed when cloning the old Meta_schedulings rock objects.
		// // String newId = null;
		// // for (int i = 0; i < newNode.getChildCount(); i++) {
		// // Object child = ((DefaultMutableTreeNode) newNode.getChildAt(i)
		// // .getChildAt(0)).getUserObject();
		// // if (child instanceof ParameterPanel) {
		// // ETLSetModel model = (ETLSetModel) ((ParameterPanel) child)
		// // .getModel();
		// // newId = ((Meta_schedulings) model. getMeasurementtypeExt()
		// // .getMeasurementtype()).getTypeid();
		// // }
		// // }
		//
		// // Iterate through all the children.
		// for (int i = 0; i < existingNode.getChildCount(); i++) {
		// // Get both old and new children. The children must be in the same
		// // order under both nodes.
		// Object oldChild = ((DefaultMutableTreeNode) existingNode
		// .getChildAt(i).getChildAt(0)).getUserObject();
		// Object newChild = ((DefaultMutableTreeNode) newNode.getChildAt(i)
		// .getChildAt(0)).getUserObject();
		//
		// // Copy the data from old child to the new based on the type of the
		// // child.
		// if (oldChild instanceof ParameterPanel) {
		// // The node is a parameter panel
		//
		// // Get the old and new models
		// ETLSetModel oldParameterModel = (ETLSetModel) ((ParameterPanel)
		// oldChild)
		// .getModel();
		// ETLSetModel newParameterModel = (ETLSetModel) ((ParameterPanel)
		// newChild)
		// .getModel();
		//
		// // Get the values from old node.
		// String oldType = (String) oldParameterModel
		// .getValueAt(ETLSetModel.TYPE);
		// boolean oldStatus = (Boolean) oldParameterModel
		// .getValueAt(ETLSetModel.STATUS);
		// String oldPriority = String.valueOf(oldParameterModel
		// .getValueAt(ETLSetModel.PRIORITY));
		// String oldQtime = String.valueOf(oldParameterModel
		// .getValueAt(ETLSetModel.QTIME));
		//
		// // Set the values in the new node
		// //
		// // NOTE: If setValueAt is used, then the model is updated, but
		// // the combo box in the panel will show the old selection.
		// //
		//
		// // Update ComboBox: Type
		// int ind = -1;
		// for (int j = 0; j < ETLSetModel.TYPE_ITEMS.length; j++) {
		// if (ETLSetModel.TYPE_ITEMS[j].equals(oldType)) {
		// ind = j;
		// break;
		// }
		// }
		// PairComponent comp = (PairComponent) ((ParameterPanel) newChild)
		// .getComponent(0);
		// ((JComboBox) comp.getComponent()).setSelectedIndex(ind);
		//
		// // Update CheckBox: status
		// comp = (PairComponent) ((ParameterPanel) newChild)
		// .getComponent(1);
		// ((JCheckBox) comp.getComponent()).setSelected(oldStatus);
		// newParameterModel.setValueAt(oldStatus, ETLSetModel.STATUS);
		//
		// // Update TextField: priority
		// comp = (PairComponent) ((ParameterPanel) newChild)
		// .getComponent(2);
		// ((JTextField) comp.getComponent()).setText(oldPriority);
		// newParameterModel.setValueAt(oldPriority, ETLSetModel.PRIORITY);
		//
		// // Update TextField: qtime
		// comp = (PairComponent) ((ParameterPanel) newChild)
		// .getComponent(3);
		// ((JTextField) comp.getComponent()).setText(oldQtime);
		// newParameterModel.setValueAt(oldQtime, ETLSetModel.QTIME);
		//
		// } else if (oldChild instanceof TableContainer) {
		// // The node is a table
		//
		// // Get the table models for the old and new table
		// TTTableModel oldTableModel = ((TableContainer) oldChild)
		// .getTableModel();
		// TTTableModel newTableModel = ((TableContainer) newChild)
		// .getTableModel();
		//
		// // Copy all the rock objects in the table to the new table.
		//
		// Vector<Object> data = oldTableModel.getData();
		// for (int j = 0; j < data.size(); j++) {
		// if (oldTableModel instanceof ETLSchedulerTableModel) {
		//
		// Meta_schedulings oldObj = (Meta_schedulings) data
		// .elementAt(j);
		//
		// // Get the old rock object and clone it
		// Meta_schedulings newObj = (Meta_schedulings) oldTableModel
		// .copyOf(oldObj);
		// // Set the object as new
		// newObj.setNewItem(true);
		// // Insert the clone to the new table
		// newTableModel.insertDataLast(newObj);
		//
		// } else if (oldTableModel instanceof ETLActionTableModel) {
		// Meta_transfer_actions oldObj = (Meta_transfer_actions) data
		// .elementAt(j);
		//
		// // Get the old rock object and clone it
		// Meta_transfer_actions newObj = (Meta_transfer_actions) oldTableModel
		// .copyOf(oldObj);
		// // Set the object as new
		// newObj.setNewItem(true);
		// // Insert the clone to the new table
		// newTableModel.insertDataLast(newObj);
		// }
		// }
		//
		// // Refresh table so that it is correctly drawn after copying the
		// // data
		// ((TableContainer) newChild).tuneSize();
		// }
		// }
	}

}
