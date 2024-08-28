package com.ericsson.eniq.techpacksdk.datamodel;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.DwhtechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel;

@SuppressWarnings("serial")
public class DWHTreeDataModel extends DefaultTreeModel implements DataModel {

  RockFactory etlRock;

  Vector activeTechpacks = new Vector();
  
  public DWHTreeDataModel(RockFactory etlRock) {
    super(null);
    this.etlRock = etlRock;
    this.refresh();

  }

  public void refresh() {
    activeTechpacks.clear();
    this.setRoot(createTree());
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  private DefaultMutableTreeNode createTree() {

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    Vector<String> versions = getAllUniqueTechpackNames();

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

  private Vector<String> getTypes(String tpname) {

    Vector<String> tps = new Vector<String>();

    try {

      Tpactivation m = new Tpactivation(etlRock);
      m.setTechpack_name(tpname);
      TpactivationFactory mF = new TpactivationFactory(etlRock, m);
      Iterator iter = mF.get().iterator();
      while(iter.hasNext()){
        Tpactivation tpa = (Tpactivation)iter.next();
        if (!tps.contains(tpa.getVersionid())){
          tps.add(tpa.getVersionid());
        }
      }

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return tps;
  }
  
  private Vector<String> getAllUniqueTechpackNames() {

    Vector<String> tps = new Vector<String>();

    try {

      Versioning m = new Versioning(etlRock);
      VersioningFactory mF = new VersioningFactory(etlRock, m);
      Iterator iter = mF.get().iterator();
      
      while(iter.hasNext()){
        Versioning v = (Versioning)iter.next();
        if (!tps.contains(v.getTechpack_name())){
          tps.add(v.getTechpack_name());
        }
      }
      
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);      
      e.printStackTrace();
    }

    return tps;
  }
  
  public Tpactivation getActiveVersion(String techpackName) {

    try {

      Tpactivation tpa = new Tpactivation(etlRock);
      tpa.setTechpack_name(techpackName);
           
      TpactivationFactory tpaF = new TpactivationFactory(etlRock, tpa);
      
      if (tpaF==null || tpaF.size()==0){
        return null;
      }  
      
      Tpactivation activeTpa = tpaF.getElementAt(0);
         return activeTpa;  
      
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);      
      e.printStackTrace();
    }

    return null;
  }
  
  private Vector<Versioning> getAllVersions(String tpName) {

    Vector<Versioning> theCounters = new Vector<Versioning>();

    try {

      Versioning m = new Versioning(etlRock, true);
      m.setTechpack_name(tpName);
      VersioningFactory mF = new VersioningFactory(etlRock, m);
      theCounters = mF.get();

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return theCounters;
  }

  private DefaultMutableTreeNode createTechPack(String tpname) {
    return new DefaultMutableTreeNode(tpname);
  }

  
  private Vector<Object> createVersion(String tpname) {

    Vector<Versioning> vec = getAllVersions(tpname);
    Vector<String> tpas = getTypes(tpname);
    Vector<Object> result = new Vector();

    DefaultMutableTreeNode version = createTechPack(tpname);

    Iterator iter = vec.iterator();
    while (iter.hasNext()) {

      Versioning v = (Versioning) iter.next();
     
      boolean active = false;
      String locked = null;

      try {

        
        
        Dwhtechpacks atp = new Dwhtechpacks(etlRock);
        atp.setVersionid(v.getVersionid());
        DwhtechpacksFactory atpF = new DwhtechpacksFactory(etlRock, atp);
        active = (atpF != null && atpF.get().size() > 0);

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }

      if (v != null && v.getLockedby() != null) {
        locked = v.getLockedby().trim();
      }

      // remove double parenthesis from buildnumber
      String show = v.getVersionid();
      show = show.replace("((", "");
      show = show.replace("))", "");
      
      if (active) {
        activeTechpacks.add(show);
      }
      
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

  public boolean modObj(RockDBObject rObj){
    try {

        rObj.updateDB();

    } catch (Exception e){
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;   
  }
  
  public boolean modObj(RockDBObject rObj[]) {
    try {
      for (int i = 0; i < rObj.length ; i++){
        rObj[i].updateDB();
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean updated(DataModel dataModel) {
    if (dataModel instanceof TechPackDataModel || dataModel instanceof NewTechPackDataModel) {
      this.setRoot(createTree());
      return true;
    }
  return false;
}

  public Vector getActiveTechpacks() {
    return activeTechpacks;
  }

}
