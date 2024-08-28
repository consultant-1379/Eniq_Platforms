/**
 * 
 */
package com.ericsson.eniq.techpacksdk.datamodel;

import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.ExceptionHandler;

/**
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class TechPackDataModel extends DefaultTableModel implements DataModel {

  private static final Logger logger = Logger.getLogger(TechPackDataModel.class.getName());

  private RockFactory rockFactory;

  private DataModelController dataModelController = null;

  private TechPackTreeDataModel techPackTreeDataModel;

  private Versioning baseversioning;

  private Versioning versioning;

  public TechPackDataModel(RockFactory rockFactory2) {
    this.rockFactory = rockFactory2;
  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public boolean delObj(RockDBObject obj) {
    return false;
  }

  public RockFactory getRockFactory() {
    return null;
  }

  public boolean modObj(RockDBObject obj) {
    return false;
  }

  public boolean modObj(RockDBObject[] obj) {
    return false;
  }

  public boolean newObj(RockDBObject obj) {
    return false;
  }

  public void refresh() {
    logger.info(versioning.getVersionid() + " refreshed from db");
  }

  public void save() {
  }

  public boolean updated(DataModel dataModel) {
    if (dataModel instanceof TechPackTreeDataModel) {
      techPackTreeDataModel = (TechPackTreeDataModel) dataModel;
      setVersioning(techPackTreeDataModel.getSelected());
      setBaseversioning(techPackTreeDataModel.getVersionByVersionId(this.versioning.getBasedefinition()));
      return true;
    }
    return false;
  }

  public boolean validateDel(RockDBObject obj) {
    return false;
  }

  public boolean validateMod(RockDBObject obj) {
    return false;
  }

  public boolean validateNew(RockDBObject obj) {
    return false;
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public void setVersioning(Versioning versioning) {
    try {
      this.versioning = versioning;
      refresh();
      dataModelController.rockObjectsModified(this);
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  public String getVersionId() {
    if (versioning == null) {
      return "";
    }
    return versioning.getVersionid();
  }

  public void migrate(String versionid, String migratedEniqLevel) throws Exception {

    Versioning v = new Versioning(rockFactory, true);
    v.setVersionid(versionid);
    VersioningFactory vF = new VersioningFactory(rockFactory, v, true);
    Versioning theTp = (Versioning) vF.get().elementAt(0);

    theTp.setEniq_level(migratedEniqLevel);
    String oldVersion = theTp.getTechpack_version();

    if (oldVersion.indexOf("_") > 0) {
      oldVersion = oldVersion.substring(0, oldVersion.lastIndexOf("_"));
    }

    theTp.setTechpack_version(oldVersion);
    theTp.updateDB();

  }

  public Versioning getBaseversioning() {
    return baseversioning;
  }

  public void setBaseversioning(Versioning baseversioning) {
    this.baseversioning = baseversioning;
  }

}
