package com.ericsson.eniq.techpacksdk.datamodel;

//import general.DataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel;

@SuppressWarnings("serial")
public class TechPackTreeDataModel extends DefaultTreeModel implements DataModel {

  RockFactory etlRock;
  
  String serverName;
  private static final Logger LOGGER = Logger.getLogger(TechPackTreeDataModel.class.getName());
  DataModelController dataModelController = null;

  private Versioning selected;

  public TechPackTreeDataModel(RockFactory etlRock, String serverName) {
    super(null);
    this.etlRock = etlRock;
    this.serverName = serverName;
    this.refresh();

  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public void refresh() {
    this.setRoot(createTree());
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  private DefaultMutableTreeNode createTree() {

    Vector<String> versions = null;

    DefaultMutableTreeNode root = new DefaultMutableTreeNode(serverName);

    versions = getAllTPNames();

    Collections.sort(versions);
    
    for (int i = 0; i < versions.size(); i++) {

      Iterator iter = createVersion(versions.elementAt(i)).iterator();

      while (iter.hasNext()) {

        DefaultMutableTreeNode t = (DefaultMutableTreeNode) iter.next();
        root.add(t);
      }

    }

    return root;
  }

  private Vector<String> getAllTPNames() {

    Vector<String> theNames = new Vector<String>();

    try {

      Versioning m = new Versioning(etlRock);
      VersioningFactory mF = new VersioningFactory(etlRock, m, true);
      Iterator iter = mF.get().iterator();

      while(iter.hasNext()){
        Versioning ver = (Versioning)iter.next();
        if (!theNames.contains(ver.getTechpack_name())){
          theNames.add(ver.getTechpack_name());
        }
      }
      
      return theNames;
      
    } catch (Exception e) { 
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return theNames;
  }

 
  private Vector<Versioning> getAllVersions(String tpName) {

    Vector<Versioning> versions = new Vector<Versioning>();

    try {

      Versioning m = new Versioning(etlRock, true);
      m.setTechpack_name(tpName);
      VersioningFactory mF = new VersioningFactory(etlRock, m, true);
      versions = mF.get();

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return versions;
  }

  private Versioning getVersion(String versionid) {

    Versioning m = new Versioning(etlRock, true);
    m.setVersionid(versionid);
    
    try {
      
      if (versionid==null || versionid.length()<1){
        return null;
      }
      
      VersioningFactory mF = new VersioningFactory(etlRock, m, true);
      Vector<Versioning> versions = mF.get();
      if (versions.size() > 0) {
        return versions.get(0);
      }
      
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return null;
  }

  private DefaultMutableTreeNode createTechPack(String tpName) {
    return new DefaultMutableTreeNode(tpName);
  }

  private Vector<Object> createVersion(String tpName) {

    Vector<Versioning> vec = getAllVersions(tpName);
    Vector<Object> result = new Vector<Object>();

    DefaultMutableTreeNode version = createTechPack(tpName);

    Iterator iter = vec.iterator();
    while (iter.hasNext()) {

      Versioning v = (Versioning) iter.next();

      boolean active = false;
      String locked = null;

      try {

        Tpactivation atp = new Tpactivation(etlRock);
        atp.setVersionid(v.getVersionid());
        TpactivationFactory atpF = new TpactivationFactory(etlRock, atp, true);
        active = (atpF != null && ((String) ((Tpactivation) atpF.getElementAt(0)).getStatus())
            .equalsIgnoreCase("active"));

      } catch (Exception e) {

      }

      if (v != null && v.getLockedby() != null) {
        locked = v.getLockedby().trim();
      }

      // remove double parenthesis from buildnumber
      String show = v.getVersionid();
      show = show.replace("((", "");
      show = show.replace("))", "");
      
      // Create the child nodes
      DefaultMutableTreeNode parameterNode = new DefaultMutableTreeNode(new DataTreeNode(show, v, active,
          locked));

      // Connect the nodes
      version.add(parameterNode);

    }

    result.add(version);

    return result;
  }

  public void save() {

  }

  public boolean validateNew(RockDBObject rObj) {
    return true;
  }

  public boolean validateDel(RockDBObject rObj) {
    return true;
  }

  public boolean validateMod(RockDBObject rObj) {
    return true;
  }

  public boolean newObj(RockDBObject rObj) {
    try {
      rObj.insertDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean delObj(RockDBObject rObj) {
    try {
      rObj.deleteDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean modObj(RockDBObject rObj) {
    try {

      rObj.updateDB();

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean modObj(RockDBObject rObj[]) {
    try {
      for (int i = 0; i < rObj.length; i++) {
        rObj[i].updateDB();
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean updated(DataModel dataModel) {
    if (dataModel instanceof NewTechPackDataModel) {
      refresh();
      return true;
    }
    return false;
  }

  public Versioning getSelected() {
    return selected;
  }

  public void setSelected(Versioning selected) {
    try {
      this.selected = selected;
//      dataModelController.rockObjectsModified(this);
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  public Versioning getVersionByVersionId(String versionid) {
    return getVersion(versionid);
  }
  
  //20110112,EANGUAN,HN54220:Function to count the number of Locked Technology Packages
  public List<String> listOfLockedTechPacks(){
	  List<String> lockedTPList = new ArrayList<String>();
	  Enumeration<DefaultMutableTreeNode> tpNames = ((DefaultMutableTreeNode)this.getRoot()).children();
	  while(tpNames.hasMoreElements()){
		  DefaultMutableTreeNode tpName = (DefaultMutableTreeNode)tpNames.nextElement() ;
		  Enumeration<DefaultMutableTreeNode> tpVerList =  tpName.children();
		  while(tpVerList.hasMoreElements()){
			  DefaultMutableTreeNode tpVer = (DefaultMutableTreeNode)tpVerList.nextElement() ;
			  DataTreeNode node = (DataTreeNode)tpVer.getUserObject();
			  // node.toString() = name of node (may be null)
			  if(node != null && node.toString() != null && node.toString().length() > 0){
				  // node.locked = name of the user who locked the node ( may be null )
				  if(node.locked != null && node.locked.length() > 0){
					  if(node.locked.trim().equals(dataModelController.getUserName().trim())){
						  LOGGER.log(Level.INFO, " Locked TechPack: " + node.toString());
						  lockedTPList.add(node.toString());
					  }
				  } 
			  }
		  }
	  }
	  return lockedTPList ;	  
  }
  
  //20110112,EANGUAN Merge Improvements:: Function to unlock the locked TPs by the user
  public void unlockAllLockedTPs(){
	  Enumeration<DefaultMutableTreeNode> tpNames = ((DefaultMutableTreeNode)this.getRoot()).children();
	  while(tpNames.hasMoreElements()){
		  DefaultMutableTreeNode tpName = (DefaultMutableTreeNode)tpNames.nextElement() ;
		  Enumeration<DefaultMutableTreeNode> tpVerList =  tpName.children();
		  while(tpVerList.hasMoreElements()){
			  DefaultMutableTreeNode tpVer = (DefaultMutableTreeNode)tpVerList.nextElement() ;
			  DataTreeNode node = (DataTreeNode)tpVer.getUserObject();
			  // node.toString() = name of node (may be null)
			  if(node != null && node.toString() != null && node.toString().length() > 0){
				  // node.locked = name of the user who locked the node ( may be null )
				  if(node.locked != null && node.locked.length() > 0){
					  if(node.locked.trim().equals(dataModelController.getUserName().trim())){
				            final Versioning v = (Versioning) node.getRockDBObject();
				            v.setLockedby("");
				            v.setLockdate(null);
				            LOGGER.log(Level.INFO, " Unlocking TechPack: " + node.toString());
				            dataModelController.getTechPackTreeDataModel().modObj(v);
				            dataModelController.getTechPackTreeDataModel().refresh();
					  }
				  } 
			  }
		  }
	  }
  }
  
}
