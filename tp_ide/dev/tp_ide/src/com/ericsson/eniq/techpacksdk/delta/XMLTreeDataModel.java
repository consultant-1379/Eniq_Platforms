//package com.ericsson.eniq.techpacksdk.delta;
//
////import general.DataModel;
//
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import ssc.rockfactory.RockDBObject;
//import ssc.rockfactory.RockFactory;
//
//import com.distocraft.dc5000.repository.dwhrep.Versioning;
//import com.ericsson.eniq.component.ExceptionHandler;
//import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
//import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel;
//
//@SuppressWarnings("serial")
//public class XMLTreeDataModel extends DefaultTreeModel implements DataModel {
//
//  private Versioning selected;
//
//  private Document XMLRootNode;
//
//  private String rootName;
//
//  public XMLTreeDataModel(String rootName, Document n) {
//    super(null);
//    this.XMLRootNode = n;
//    this.refresh();
//    this.rootName = rootName;
//  }
//
//  public void setDataModelController(Document n) {
//    this.XMLRootNode = n;
//  }
//
//  public void refresh() {
//    this.setRoot(createTree());
//  }
//
//  private void travelDocument(Node n, DefaultMutableTreeNode root) {
//    NodeList nl = n.getChildNodes();
//
//    for (int i = 0; i < nl.getLength(); i++) {
//      if (nl.item(i).getNodeType() != 3) {
//        // Create the child nodes
//        DefaultMutableTreeNode parameterNode = new DefaultMutableTreeNode(nl.item(i).getNodeName());
//        travelDocument(nl.item(i), parameterNode);
//        root.add(parameterNode);
//      }
//
//    }
//  }
//
//  private DefaultMutableTreeNode createTree() {
//
//    DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(rootName);
//
//    travelDocument(XMLRootNode, treeRoot);
//
//    return treeRoot;
//  }
//
//  public void save() {
//
//  }
//
//  public boolean validateNew(RockDBObject rObj) {
//    return true;
//  }
//
//  public boolean validateDel(RockDBObject rObj) {
//    return true;
//  }
//
//  public boolean validateMod(RockDBObject rObj) {
//    return true;
//  }
//
//  public boolean newObj(RockDBObject rObj) {
//    try {
//      rObj.insertDB();
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//    return true;
//  }
//
//  public boolean delObj(RockDBObject rObj) {
//    try {
//      rObj.deleteDB();
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//    return true;
//  }
//
//  public boolean modObj(RockDBObject rObj) {
//    try {
//
//      rObj.updateDB();
//
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//    return true;
//  }
//
//  public boolean modObj(RockDBObject rObj[]) {
//    try {
//      for (int i = 0; i < rObj.length; i++) {
//        rObj[i].updateDB();
//      }
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//    return true;
//  }
//
//  public boolean updated(DataModel dataModel) {
//    if (dataModel instanceof NewTechPackDataModel) {
//      refresh();
//      return true;
//    }
//    return false;
//  }
//
//  public Versioning getSelected() {
//    return selected;
//  }
//
//  public void setSelected(Versioning selected) {
//    try {
//      this.selected = selected;
//      // dataModelController.rockObjectsModified(this);
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//  }
//
//  public RockFactory getRockFactory() {
//    return null;
//  }
//
//}
