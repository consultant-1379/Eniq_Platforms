package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * Concrete factory class for ETL Sets.
 * 
 * @author ejarsav
 * 
 */
public class BusyhourHandlingFactory extends TreeDataFactory {

  // private static final Logger logger =
  // Logger.getLogger(BusyhourHandlingFactory.class.getName());

  public static int N_OF_TYPES = 10;

  public static int N_OF_COUNTERS = 40;

  public static int N_OF_KEYS = 40;

  private Versioning versioning;

  private String targetversionid;

  private DataModelController dataModelController;

  private String type = "N/A";

  private Vector<BusyHourData> removeList = new Vector<BusyHourData>();

  private BusyhourHandlingDataModel busyHourHandlingDataModel;

  private final Application application;

  /**
   * The rock factory is the interface towards the DB.
   */
  private RockFactory rockFactory = null;

  /**
   * The constructor initiates the renderer and DB interface for this specific
   * type of dialogue.
   */
  public BusyhourHandlingFactory(Application application, BusyhourHandlingDataModel busyHourHandlingDataModel) {
    createTableInfoData(); // Collect info about the different table types
    // used
    this.application = application;
    this.busyHourHandlingDataModel = busyHourHandlingDataModel;
    treeCellRenderer = new BusyhourHandlingRenderer();
    this.rockFactory = busyHourHandlingDataModel.getRockFactory();
  }

  /**
   * Create a tree cell editor for the specific type of dialogue
   * (MeasurementType).
   * 
   * @param theTree
   * @return treeCellEditor.
   */
  public TTEditor getTreeCellEditor(JTree theTree) {
    if (treeCellEditor == null) {
      treeCellEditor = new BusyhourHandlingEditor(theTree);
    }
    return treeCellEditor;
  }

  public void setVersioning(Versioning versioning) {
    this.versioning = versioning;
    busyHourHandlingDataModel.setCurrentVersioning(versioning);
  }

  public void setTargetVersionID(String versionid) {
    this.targetversionid = versionid;
  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  // private class cmpMType implements Comparator<Object> {
  //
  // public int compare(Object o1, Object o2) {
  // Measurementtype d1 = (Measurementtype) o1;
  // Measurementtype d2 = (Measurementtype) o2;
  // return d1.getTypename().compareTo(d2.getTypename());
  // }
  // }

  /**
   * Create the tree model for the tree and initiate it with data from the
   * database.
   * 
   * @return treeModel
   */
  public TreeModel createAndGetModel() {

    busyHourHandlingDataModel.setCurrentVersioning(versioning);

    // Create the root node
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
    DefaultTreeModel theModel = new DefaultTreeModel(root);

    List<String> bhDataNames = busyHourHandlingDataModel.getBusyHourDataNames(targetversionid);

    for (String typeName : busyHourHandlingDataModel.getRankingMeasurementtypenames(targetversionid)) {
      if (!bhDataNames.contains(typeName)) {
        bhDataNames.add(typeName);
      }
    }

    Collections.sort(bhDataNames);

    for (String name : bhDataNames) {
      TreeMainNode mainNode = createSet(name);
      root.add(mainNode);
    }

    return theModel;
  }

  /**
   * Method for collecting info of the different table types
   */
  protected void createTableInfoData() {
    tableInformations.addElement(BusyhourProductPlaceholderTableModel.createTableTypeInfo());
    tableInformations.addElement(BusyhourCustomPlaceholderTableModel.createTableTypeInfo());
  }

  /**
	 */
  private TreeMainNode createSet(String typename) {

    TTParameterModel parameterModel = new BusyhourHandlingModel(typename, removeList, rockFactory, editable);

    DefaultMutableTreeNode productPlaceholderNode;
    DefaultMutableTreeNode customPlaceholderNode;
    TreeMainNode mainNode = new TreeMainNode(parameterModel);
    Vector<Object> myProductPlaceholders = new Vector<Object>();
    Vector<Object> myCustomPlaceholders = new Vector<Object>();

    for (BusyHourData bhd : busyHourHandlingDataModel.getBusyHourData(targetversionid)) {

      if (bhd.getBusyhour().getBhlevel().equals(typename)
          && bhd.getBusyhour().getTargetversionid().equals(targetversionid)) {
        if (bhd.getBusyhour().getPlaceholdertype() != null
            && bhd.getBusyhour().getPlaceholdertype().equals(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX)) {
          myProductPlaceholders.add(bhd);
        } else if (bhd.getBusyhour().getPlaceholdertype() != null
            && bhd.getBusyhour().getPlaceholdertype().equals(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX)) {
          myCustomPlaceholders.add(bhd);
        }
      }
    }
    
	 final BusyHourData emptyBusyHourDataPP = new BusyHourData(dataModelController, versioning, targetversionid, typename, Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX);
	 // Create the child nodes
	 final BusyhourProductPlaceholderTableModel pHolder = new BusyhourProductPlaceholderTableModel(
			 application, rockFactory, tableInformations, dataModelController, editable, emptyBusyHourDataPP);
	 productPlaceholderNode = createTableNode(myProductPlaceholders, pHolder);
	 
	 final BusyHourData emptyBusyHourDataCP = new BusyHourData(dataModelController, versioning, targetversionid, typename, Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX);
	 customPlaceholderNode = createTableNode(myCustomPlaceholders, new BusyhourCustomPlaceholderTableModel(application,
			 rockFactory, tableInformations, dataModelController, editable, emptyBusyHourDataCP, pHolder));


    // Connect the nodes
    mainNode.add(productPlaceholderNode);
    mainNode.add(customPlaceholderNode);

    return mainNode;
  }

  /**
   * Overridden version of the corresponding TreeDataFactory method. Used for
   * creating new empty nodes when the user inserts nodes in a tree.
   */
  public TreeMainNode createEmptyNode() {

    String typename = "";

    TTParameterModel parameterModel = new BusyhourHandlingModel("", removeList, rockFactory, editable);

    Vector<Object> myProductPlaceholders = new Vector<Object>();
    Vector<Object> myCustomPlaceholders = new Vector<Object>();

    for (BusyHourData bhd : busyHourHandlingDataModel.getBusyHourData(targetversionid)) {

      if (bhd.getBusyhour().getBhlevel().equals(typename)) {
        if (bhd.getBusyhour().getPlaceholdertype().equals(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX)) {
          myProductPlaceholders.add(bhd);
        } else if (bhd.getBusyhour().getPlaceholdertype().equals(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX)) {
          myCustomPlaceholders.add(bhd);
        }
      }
    }

    // Create the main node
    TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
		final BusyhourProductPlaceholderTableModel pHolder = new BusyhourProductPlaceholderTableModel(
			 application, rockFactory, tableInformations, dataModelController, editable, null);
    final DefaultMutableTreeNode productPlaceholderNode = createTableNode(myProductPlaceholders, pHolder);
		
    final DefaultMutableTreeNode customPlaceholderNode = createTableNode(myCustomPlaceholders,
        new BusyhourCustomPlaceholderTableModel(application, rockFactory, tableInformations, dataModelController, editable, null, pHolder));

    // Connect the nodes
    mainNode.add(productPlaceholderNode);
    mainNode.add(customPlaceholderNode);

    return mainNode;

  }

  public Vector<BusyHourData> getRemoveList() {
    return removeList;
  }

  public void setRemoveList(Vector<BusyHourData> removeList) {
    this.removeList = removeList;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

	/**
	 * Override the default implementation to always return <code>false</code>. This will disable the
	 * Add/Remove/Duplicate element in the popup menu for the busy hour types but still allow the model (re placeholders)
	 * to be edited.
	 * The Add/Remove Target techpacks will still be enabled based on wheather or not the techpacks is
	 * being edited or viewed.
	 *
	 * @return <code>false</code>
	 */
	@Override
	public boolean isTreeEditable() {
		return false;
	}

	/**
   * Duplicates an existing tree node data to the new tree node
   */
  @Override
  public void duplicateExistingNode(TreeMainNode existingNode, TreeMainNode newNode) {

    // TODO: Implementation still missing for duplicate
    System.out.println("duplicateExistingNode(): Not implemented yet. " + "Data is not copied to the new empty node.");
    JOptionPane.showMessageDialog(null, "Not implemented yet! Data is not copied to the new empty node.");

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
