package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;

/**
 * Concrete factory class for Data Format.
 * 
 * @author ejarsav
 * 
 */
public class DataformatFactory extends TreeDataFactory {

  private String setName = "";

  private String versionid = "";

  private String dataformattype = "";

  /**
   * The rock factory is the interface towards the DB.
   */
  private RockFactory rockFactory = null;

  private DataformatDataModel dataFormatDataModel;

  /**
   * The constructor initiates the renderer and DB interface for this specific
   * type of dialogue.
   */
  public DataformatFactory(RockFactory rockFactory, boolean editable) {
    createTableInfoData(); // Collect info about the different table types
    // used
    treeCellRenderer = new DataformatRenderer();
    this.rockFactory = rockFactory;
    this.editable = editable;
  }

  /**
   * @param dataFormatDataModel
   * @param editable
   */
  public DataformatFactory(DataformatDataModel dataFormatDataModel, boolean editable) {
    this(dataFormatDataModel.getRockFactory(), editable);
    this.dataFormatDataModel = dataFormatDataModel;
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
      treeCellEditor = new DataformatEditor(theTree);
    }
    return treeCellEditor;
  }

  /**
   * @param setName
   */
  public void setSetName(String setName) {
    this.setName = setName;
  }

  /**
   * @param versionid
   */
  public void setVersionid(String versionid) {
    this.versionid = versionid;
  }

  /**
   * @param dataformattype
   */
  public void setDataformattype(String dataformattype) {
    this.dataformattype = dataformattype;
  }

  /**
   * @param editable
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  /**
   * @author ejarsav
   * 
   */
  private class cmpDataformats implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
      Dataformat d1 = (Dataformat) o1;
      Dataformat d2 = (Dataformat) o2;
      return d1.getFoldername().compareTo(d2.getFoldername());
    }
  }

  /**
   * @see tableTree.TreeDataFactory#createAndGetModel()
   */
  public TreeModel createAndGetModel() {

    // Create the root node
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(setName);
    DefaultTreeModel theModel = new DefaultTreeModel(root);

    dataFormatDataModel.setVersionid(versionid);
    Vector<Dataformat> dataformats = dataFormatDataModel.getDataFormats(dataformattype);

    Collections.sort(dataformats, new cmpDataformats());

    Iterator<Dataformat> iter = dataformats.iterator();
    while (iter.hasNext()) {

      Dataformat dataformat = (Dataformat) iter.next();
      TreeMainNode mainNode = createSet(dataformat);
      root.add(mainNode);

    }
    return theModel;
  }

  /**
	 * 
	 */
  protected void createTableInfoData() {
    tableInformations.addElement(DataformatTableModel.createTableTypeInfo());
    tableInformations.addElement(TagTableModel.createTableTypeInfo());
  }

  /**
   * @author ejarsav
   * 
   */
  private class cmpDataitems implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
      Dataitem d1 = (Dataitem) o1;
      Dataitem d2 = (Dataitem) o2;
      return d1.getDataname().compareTo(d2.getDataname());
    }
  }

  /**
   * @param df
   * @return
   */
  private TreeMainNode createSet(Dataformat df) {

    TTParameterModel parameterModel = new DataformatParameterModel(df.getFoldername(), rockFactory, editable);

    Vector<Defaulttags> defaultTags = dataFormatDataModel.getDefaultTags(df.getDataformatid());
    Vector<Dataitem> dataItems = dataFormatDataModel.getDataItems(df.getDataformatid());

    Vector<Object> tmpTags = new Vector<Object>();
    for (int i = 0; i < defaultTags.size(); i++) {
      tmpTags.add((Object) defaultTags.elementAt(i));
    }

    Vector<Object> tmpItems = new Vector<Object>();
    for (int i = 0; i < dataItems.size(); i++) {
      tmpItems.add((Object) dataItems.elementAt(i));
    }

    Collections.sort(dataItems, new cmpDataitems());

    // Create the main node
    TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
    DefaultMutableTreeNode tagNode = createTableNode(tmpTags, new TagTableModel(df.getDataformatid(), rockFactory,
        tableInformations, editable));
    DefaultMutableTreeNode dataFormatNode = createTableNode(tmpItems, new DataformatTableModel(rockFactory,
        tableInformations, editable));

    // Connect the nodes
    mainNode.add(tagNode);
    mainNode.add(dataFormatNode);

    return mainNode;
  }

  /**
   * Overridden version of the corresponding TreeDataFactory method. Used for
   * creating new empty nodes when the user inserts nodes in a tree.
   */
  public TreeMainNode createEmptyNode() {

    Dataformat mc = new Dataformat(rockFactory);

    // Create the child node data for all three nodes: sets, actions and
    // schedulings
    TTParameterModel parameterModel = new DataformatParameterModel("NEW DATAFORMAT", rockFactory, editable);

    parameterModel.setMainNodeName("NEW SET");

    Vector<Object> myActions = new Vector<Object>();

    // Create the main node
    TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
    DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);

    DefaultMutableTreeNode tagNode = createTableNode(myActions, new TagTableModel(mc.getDataformatid(), rockFactory,
        tableInformations, editable));
    DefaultMutableTreeNode actionNode = createTableNode(myActions, new DataformatTableModel(rockFactory,
        tableInformations, editable));

    // Connect the nodes
    mainNode.add(parameterNode);
    mainNode.add(tagNode);
    mainNode.add(actionNode);
    return mainNode;

  }

  /**
   * Duplicates an existing tree node data to the new tree node
   */
  @Override
  public void duplicateExistingNode(TreeMainNode existingNode, TreeMainNode newNode) {
    // Duplicate is not available for transformers.
  }

  /**
   * Overridden version of the method. Does not allow add/remove/duplicate of
   * three main nodes.
   * 
   * @return false
   */
  @Override
  public boolean isTreeNodeAddRemoveAllowed() {
    return false;
  }
}
