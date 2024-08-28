package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class TagTreeModel extends DefaultTreeModel {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(TagTreeModel.class.getName());

  private Map<String, TableTreeComponent> tableTreeComponentMap = null;

  private boolean editable;

  private JTree theTree;

  private DataformatTreeModelListener listener;

  private String versionid;

  private DocumentListener dl;

  public DataModelController dataModelController;

  public TagTreeModel(String versionid, DataModelController dataModelController, boolean editable, JTree theTree,
      DataformatTreeModelListener listener, DocumentListener dl) {
    super(null, true);
    this.dl = dl;
    this.dataModelController = dataModelController;
    this.editable = editable;
    this.theTree = theTree;
    this.listener = listener;
    this.versionid = versionid;

    dataModelController.getDataformatDataModel().setVersionid(versionid);
    dataModelController.getDataformatDataModel().refresh();

    this.setRoot(refresh());

  }

  private DefaultMutableTreeNode refresh() {

    tableTreeComponentMap = new HashMap<String, TableTreeComponent>();

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    theTree.addMouseListener(listener);

    Vector<String> sort = new Vector<String>();
    sort.addAll(dataModelController.getDataformatDataModel().getAllDataFormats().keySet());
    Collections.sort(sort);
    Iterator<String> iter = sort.iterator();

    while (iter.hasNext()) {

      String dataformatType = (String) iter.next();

      DataformatFactory dff = new DataformatFactory(dataModelController.getDataformatDataModel(), editable);
      dff.setSetName(dataformatType);
      dff.setVersionid(versionid);
      dff.setDataformattype(dataformatType);
      dff.setEditable(editable);

      TableTreeComponent TTComp;

      TTComp = new TableTreeComponent(dff, listener);
      TTComp.addDocumentListener(dl);
      tableTreeComponentMap.put(dataformatType, TTComp);

      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(dataformatType);
      treeNode.add(new DefaultMutableTreeNode(TTComp));

      root.add(treeNode);

    }

    return root;
  }

  public void addDocumentListener(DocumentListener dl) {

    Iterator<String> iter = tableTreeComponentMap.keySet().iterator();
    while (iter.hasNext()) {
      String dataformatType = (String) iter.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);
      ttc.addDocumentListener(dl);
    }
  }

  /**
   * Checks the Data Tags are unique within a Data Format. 
   * @return
   */
  public Vector<String> validateData() {
    Vector<String> result = new Vector<String>();
    Iterator<String> iter = tableTreeComponentMap.keySet().iterator();
    while (iter.hasNext()) { // Loop through DataFormats, E.g. [ascii, csexport]
      String dataformatType = (String) iter.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);
      Vector<String> duplicateCheck = new Vector<String>();
      Iterator<String> i = dataModelController.getDataformatDataModel().getAllDefaultTagsMap().keySet().iterator();

      while (i.hasNext()) { // Loop through All, list includes the DataFormats we are not looking at in this iteration.
        String key = (String) i.next();
        if (key.endsWith(":"+dataformatType)) { // Ignore other DataFormats
	        Vector<Defaulttags> v = (Vector<Defaulttags>) dataModelController.getDataformatDataModel()
	            .getAllDefaultTagsMap().get(key);
	        Iterator<Defaulttags> ii = v.iterator();
	
	        while (ii.hasNext()) {
	          Defaulttags d = (Defaulttags) ii.next();
	          if (duplicateCheck.contains(d.getTagid())) {
	            result.add("Tag " + d.getTagid() + " is not unique in all dataformats"+" for "+dataformatType);
	            // Removing this setting of letSaveButton
	            //((DataFormatTree)theTree).letSaveButtEnable = true ;
	          } else {
	        	  duplicateCheck.add(d.getTagid());
	          }
	        }
        } // end if
      }

      result.addAll(ttc.validateData());
    } // // Loop through DataFormats

    return result;
  }

  public void save() {
    logger.log(Level.INFO, "save");
    Iterator<String> iter = tableTreeComponentMap.keySet().iterator();

    while (iter.hasNext()) {

      String dataformatType = (String) iter.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);

      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);

        dataModelController.getDataformatDataModel().saveDataformats();
        dataModelController.getDataformatDataModel().removeMarkedDataformats();

        ttc.saveChanges();

        dataModelController.rockObjectsModified(dataModelController.getDataformatDataModel());

        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {
        try {
          dataModelController.getRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {
        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
      }

    }
  }

  public void discard() {
    logger.log(Level.INFO, "discard");

    Map<String, List<List<Object>>> m = new HashMap<String, List<List<Object>>>();

    Iterator<String> iter = tableTreeComponentMap.keySet().iterator();
    while (iter.hasNext()) {

      String dataformatType = (String) iter.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(dataformatType);
      List<List<Object>> list = TreeState.saveExpansionState((JTree) ttc);
      m.put(dataformatType, list);
      ttc.discardChanges();
    }

    try {

      dataModelController.getDataformatDataModel().refresh();
      this.setRoot(refresh());

    } catch (Exception e) {
      try {
        dataModelController.getDataformatDataModel().refresh();
        this.setRoot(refresh());
      } catch (Exception ex) {
        logger.warning(ex.getMessage());
      }
    }

    Iterator<String> iter2 = m.keySet().iterator();
    while (iter2.hasNext()) {
      String t = (String) iter2.next();
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(t);
      TreeState.loadExpansionState((JTree) ttc, m.get(t));
    }

  }

  public void addNew() {
    addNew("NEW DATATYPE");
  }

  public void update() {
    try {
      if (dataModelController.getDataformatDataModel().newDataCreated) {
        this.setRoot(refresh());
        dataModelController.getDataformatDataModel().newDataCreated = false;
      }
    } catch (Exception e) {
      try {
        this.setRoot(refresh());
        dataModelController.getDataformatDataModel().newDataCreated = false;
      } catch (Exception ex) {
        logger.warning("Unable to refresh Dataformt tree " + ex.getMessage());
      }
    }
  }

  public Set<String> getDataformats() {
    return dataModelController.getDataformatDataModel().getAllDataFormats().keySet();
  }

  public void addNew(String name) {
    dataModelController.getDataformatDataModel().addDataType(name);
    this.setRoot(refresh());
    dl.changedUpdate(new DocEvent());
  }

  public void rename(String oldname, String newname) {

    dataModelController.getDataformatDataModel().renameDataType(oldname, newname);
    this.setRoot(refresh());
    dl.changedUpdate(new DocEvent());
  }

  public void remove(MutableTreeNode node) {

    dataModelController.getDataformatDataModel().removeDataType(node.toString());
    this.setRoot(refresh());
    dl.changedUpdate(new DocEvent());
  }

  private class DocEvent implements DocumentEvent {

    public ElementChange getChange(Element elem) {
      return null;
    }

    public Document getDocument() {
      return null;
    }

    public int getLength() {
      return 0;
    }

    public int getOffset() {
      return 0;
    }

    public EventType getType() {
      return DocumentEvent.EventType.CHANGE;
    }

  }

}
