package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.jdesktop.application.Application;

import ssc.rockfactory.RockException;
import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class BusyhourTreeModel extends DefaultTreeModel {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(BusyhourTreeModel.class.getName());

  private Map<String, TableTreeComponent> tableTreeComponentMap = null;

  private boolean editable;

  private Versioning versioning;

  private JTree theTree;

  private BusyhourTreeModelListener listener;

  private DocumentListener dl;

  public DataModelController dataModelController;

  private List<String> targettechpacks = new ArrayList<String>();

  private List<String> removedTargetTechpacks = new ArrayList<String>();

  private final Application application;

  public BusyhourTreeModel(Application application, Versioning versioning, DataModelController dataModelController,
      boolean editable, JTree theTree, BusyhourTreeModelListener listener, DocumentListener dl) {
    super(null, true);
    this.application = application;
    this.dl = dl;
    this.dataModelController = dataModelController;
    this.editable = editable;
    this.theTree = theTree;
    this.listener = listener;
    this.versioning = versioning;

    this.dataModelController.getBusyhourHandlingDataModel().setCurrentVersioning(versioning);
    this.dataModelController.getBusyhourHandlingDataModel().refreshAllBusyHourSourcesFromDb();
    this.dataModelController.getBusyhourHandlingDataModel().refresh();

    this.setRoot(refresh());

      dataModelController.setBusyHourTreeModel(this);

  }

  private DefaultMutableTreeNode refresh() {

    tableTreeComponentMap = new HashMap<String, TableTreeComponent>();

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    theTree.addMouseListener(listener);

    List<String> sort = new ArrayList<String>();

    sort.addAll(dataModelController.getBusyhourHandlingDataModel().getBusyHourTargetVersionIDs());

    for (String versionid : targettechpacks) {
      if (!sort.contains(versionid)) {
        sort.add(versionid);
      }
    }

    Collections.sort(sort);

    for (String targetversionid : sort) {

      if (removedTargetTechpacks.contains(targetversionid)) {
        continue;
      }

      if (!targettechpacks.contains(targetversionid)) {
        targettechpacks.add(targetversionid);
      }

      BusyhourHandlingFactory bhhf = new BusyhourHandlingFactory(application, this.dataModelController
          .getBusyhourHandlingDataModel());

      bhhf.setRemoveList(dataModelController.getBusyhourHandlingFactory().getRemoveList());
      bhhf.setDataModelController(dataModelController);
      bhhf.setVersioning(versioning);
      bhhf.setTargetVersionID(targetversionid);
      bhhf.setEditable(editable);

      TableTreeComponent TTComp;

      TTComp = new TableTreeComponent(bhhf, listener);
      TTComp.addDocumentListener(dl);
      TTComp.setName(targetversionid);
      tableTreeComponentMap.put(targetversionid, TTComp);

      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(targetversionid);
      treeNode.add(new DefaultMutableTreeNode(TTComp));

      root.add(treeNode);

    }

    return root;
  }

  public void addDocumentListener(DocumentListener dl) {

    for (String versionid : tableTreeComponentMap.keySet()) {
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(versionid);
      ttc.addDocumentListener(dl);
    }
  }

  public List<String> validateData() {

    // Call the validation for all the TTCs in the BH tree and return a combined
    // list of results.
    List<String> result = new ArrayList<String>();
    List<String> tmpResult;
    for (TableTreeComponent ttc : tableTreeComponentMap.values()) {
      tmpResult = new ArrayList<String>(ttc.validateData());
      if (!tmpResult.isEmpty()) {
        result.addAll(tmpResult);
      }
    }
    return result;
  }

  public void save() {

    logger.log(Level.INFO, "save()");

    try {

      dataModelController.getRockFactory().getConnection().setAutoCommit(false);

      // Delete busy hours data and aggregations for the target techpacks marked
      // for deletion.
      if (removedTargetTechpacks != null && removedTargetTechpacks.size() > 0) {
        logger.log(Level.FINE, "save(): Removing " + removedTargetTechpacks.size()
            + " target techpacks marked for deletion.");
        for (String versionid : removedTargetTechpacks) {
          for (BusyHourData bhd : dataModelController.getBusyhourHandlingDataModel().getBusyHourData(versionid)) {
            // Delete the aggregations and aggregation rules. The aggregator
            // sets are also removed.
            dataModelController.getBusyhourHandlingDataModel().deleteRankBhAggregations(bhd.getBusyhour(), true);
            // Delete the busy hour data.
            bhd.delete();
          }
        }
      }

      // Save the changes for target techpacks.
      for (String versionid : tableTreeComponentMap.keySet()) {
        if (!removedTargetTechpacks.contains(versionid)) {
          logger.log(Level.FINE, "save(): Saving changes for target techpack: " + versionid);
          TableTreeComponent ttc = tableTreeComponentMap.get(versionid);
          ttc.saveChanges();
        }
      }

      // Remove the busy hour data
      List<BusyHourData> removeList = dataModelController.getBusyhourHandlingFactory().getRemoveList();
      if (removeList != null && removeList.size() > 0) {
        logger.log(Level.FINE, "save(): Removing " + removeList.size() + " busy hour data marked for deletion.");
        for (BusyHourData busyhourdata : removeList) {
          busyhourdata.delete();
        }
        // Clear the remove list
        removeList.clear();
      }

  	
  	   dataModelController.getBusyhourHandlingDataModel().regenerateBhAggregationsForTechpack(dataModelController.getBusyhourHandlingDataModel().getCurrentVersioning().getVersionid());
      
  	   /*
      // Update the enabled value for all the aggregation rules.
      logger.log(Level.FINE, "save(): Updating enabled values for RANKBH aggregation rules.");
      for (BusyHourData bhd : dataModelController.getBusyhourHandlingDataModel().getBusyHourData(
          dataModelController.getBusyhourHandlingDataModel().getCurrentVersioning().getVersionid())) {

        updateBusyHourRankBhAggregationRulesEnable(bhd.getBusyhour());
      }
  	 */
    } catch (Exception e) {
      try {
        dataModelController.getRockFactory().getConnection().rollback();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    } finally {
      try {
        removedTargetTechpacks.clear();
        dataModelController.getBusyhourHandlingDataModel().refresh();
        // this.setRoot(refresh());
        dataModelController.getRockFactory().getConnection().setAutoCommit(true);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void discard() {
    logger.log(Level.INFO, "discard");

    Map<String, List<List<Object>>> m = new HashMap<String, List<List<Object>>>();

    for (String versionid : tableTreeComponentMap.keySet()) {
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(versionid);
      List<List<Object>> list = TreeState.saveExpansionState((JTree) ttc);
      m.put(versionid, list);
      ttc.discardChanges();
    }

    try {

      removedTargetTechpacks.clear();
      targettechpacks.clear();

      dataModelController.getBusyhourHandlingDataModel().refresh();
      this.setRoot(refresh());

    } catch (Exception e) {
      try {
        dataModelController.getBusyhourHandlingDataModel().refresh();
        this.setRoot(refresh());
      } catch (Exception ex) {
        logger.warning(ex.getMessage());
      }
    }

    for (String t : m.keySet()) {
      TableTreeComponent ttc = (TableTreeComponent) tableTreeComponentMap.get(t);
      if (ttc != null) {
        TreeState.loadExpansionState((JTree) ttc, m.get(t));
      }
    }

  }

  public void update() {
    try {
      this.setRoot(refresh());
    } catch (Exception e) {
      try {
        this.setRoot(refresh());
      } catch (Exception ex) {
        logger.warning("Unable to refresh Busyhour tree " + ex.getMessage());
      }
    }
  }

  public void addNew(String newversionid) {

    if (!targettechpacks.contains(newversionid)) {
      targettechpacks.add(newversionid);
      removedTargetTechpacks.remove(newversionid);
    }

    dataModelController.getBusyhourHandlingDataModel().refresh();
    this.setRoot(refresh());
    dl.changedUpdate(new DocEvent());
  }

  public void remove(MutableTreeNode node) {

    if (targettechpacks.contains(node.toString())) {
      targettechpacks.remove(node.toString());
      removedTargetTechpacks.add(node.toString());
    }

    dataModelController.getBusyhourHandlingDataModel().refresh();
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

  public List<String> getBusyhourTargetTechpacks() {
    return targettechpacks;
  }

  /**
   * Updates RANKBH aggregation rules enable value based on the enabled value
   * for the busy hour.
   * 
   * @param bh
   * @throws SQLException
   * @throws RockException
   */
  private void updateBusyHourRankBhAggregationRulesEnable(Busyhour bh) throws SQLException, RockException {

    // Busy hour is enabled if enable value is 1 or null, and disabled if value
    // is 0.
    Integer enable = 1;
    if (bh.getEnable() != null && bh.getEnable() == 0) {
      enable = 0;
    }

    // RANKBH Aggregation rule
    String atype = "RANKBH";
    Aggregationrule aggr = new Aggregationrule(dataModelController.getRockFactory());

    aggr.setAggregation(bh.getBhlevel() + "_" + atype + "_" + bh.getBhobject() + "_" + bh.getBhtype());
    AggregationruleFactory aggrF = new AggregationruleFactory(dataModelController.getRockFactory(), aggr);
    if (aggrF != null && aggrF.getElementAt(0) != null) {
      aggrF.getElementAt(0).setEnable(enable);
      aggrF.getElementAt(0).saveDB();
    }

    // WEEKRANKBH Aggregation rule
    atype = "WEEKRANKBH";
    aggr = new Aggregationrule(dataModelController.getRockFactory());
    aggr.setAggregation(bh.getBhlevel() + "_" + atype + "_" + bh.getBhobject() + "_" + bh.getBhtype());
    aggrF = new AggregationruleFactory(dataModelController.getRockFactory(), aggr);
    if (aggrF != null && aggrF.getElementAt(0) != null) {
      aggrF.getElementAt(0).setEnable(enable);
      aggrF.getElementAt(0).saveDB();
    }

    // MONTHRANKBH Aggregation rule
    atype = "MONTHRANKBH";
    aggr = new Aggregationrule(dataModelController.getRockFactory());
    aggr.setAggregation(bh.getBhlevel() + "_" + atype + "_" + bh.getBhobject() + "_" + bh.getBhtype());
    aggrF = new AggregationruleFactory(dataModelController.getRockFactory(), aggr);
    if (aggrF != null && aggrF.getElementAt(0) != null) {
      aggrF.getElementAt(0).setEnable(enable);
      aggrF.getElementAt(0).saveDB();
    }
  }
}
