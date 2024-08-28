package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.UniverseclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeobject;
import com.distocraft.dc5000.repository.dwhrep.UniverseobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class UniverseObjectDataModel implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(UniverseObjectView.class.getName());

  private Versioning versioning;

  RockFactory etlRock;

  private UniverseObjectTableModel uotm;

  DataModelController dataModelController = null;

  Universeobject universeobject;

  public UniverseObjectDataModel(RockFactory etlRock) {

    this.etlRock = etlRock;
    try {
      this.uotm = createTableModel(null);
    } catch (Exception e) {
      logger.warning("Could not set TableModel for universeObject.");
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public UniverseObjectTableModel getTableModel() {
    return uotm;
  }

  private UniverseObjectTableModel createTableModel(Versioning v) throws RockException, SQLException {

    Vector<Universeobject> u = null;
    if (v != null) {
      Universeobject U = new Universeobject(etlRock);
      U.setVersionid(v.getVersionid());
      UniverseobjectFactory UF = new UniverseobjectFactory(etlRock, U, "order by ordernro");
      u = UF.get();
    }

    UniverseObjectTableModel uotm = new UniverseObjectTableModel(u, etlRock);

    return uotm;
  }

  public void refresh(Versioning v) {
    try {
      this.versioning = v;
      this.uotm = createTableModel(v);
    } catch (Exception e) {
      logger.warning("Could not refresh UniverseObject.");
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  public void refresh() {
    refresh(null);
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  public void save() throws Exception {
    // Get TP Versionid and throw if it is empty
    String vrVersionid = versioning.getVersionid();
    if (vrVersionid == null || vrVersionid.equals(""))
      throw new Exception("Trying to save without versionID");

    // Remove with this versionId
    try {
      Universeobject delU = new Universeobject(etlRock);
      delU.setVersionid(vrVersionid);

      UniverseobjectFactory delUF = new UniverseobjectFactory(etlRock, delU);
      Iterator deliter = delUF.get().iterator();

      while (deliter.hasNext()) {
        Universeobject tmpdelU = (Universeobject) deliter.next();
        tmpdelU.deleteDB();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    // Then add the ones on the Screen
    try {
      UniverseObjectTableModel tableModel = getTableModel();
      for (Universeobject u : tableModel.data) {

        if (!vrVersionid.equals(u.getVersionid()))
          throw new Exception("Trying to save for wrong versionId");

        u.insertDB();
      }
    } catch (Exception e) {
      System.out.println(e);
      throw e;
    }
  }

  /**
   * Data validation
   * 
   * @return errormessages
   */
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    UniverseObjectTableModel tm = getTableModel();
    if (tm == null || tm.data == null || tm.data.isEmpty())
      return errorStrings;

    boolean fkIsOK = true;

    // Get the list of existing universe classes for foreign key verification.
    List<Universeclass> ucList = null;
    try {
      Universeclass UC = new Universeclass(etlRock);
      UC.setVersionid(versioning.getVersionid());
      UniverseclassFactory UF = new UniverseclassFactory(etlRock, UC);
      ucList = UF.get();
      if (ucList.isEmpty()) {
        fkIsOK = false;
      }
    } catch (Exception ex) {
      fkIsOK = false;
    }

    // Iterate through the table data.
    List<Universeobject> us = new ArrayList<Universeobject>();
    for (Universeobject u1 : tm.data) {

      // Validate foreign key: a universe class with the matching class name,
      // version id, and universe extension must exist for this universe object.
      for (Universeclass uc : ucList) {

        if (uc.getVersionid().equals(u1.getVersionid()) && uc.getUniverseextension().equals(u1.getUniverseextension())
            && uc.getClassname().equals(u1.getClassname())) {
          fkIsOK = true;
          break;
        }
        fkIsOK = false;
      }
      if (!fkIsOK)
        errorStrings.add("Universe class (with Class name: " + u1.getClassname() + " and Universe extension: "
            + u1.getUniverseextension() + ") is missing for Object name: " + u1.getObjectname() + ".");

      // Replace null values with empty values.
      u1.removeNulls();

      // Verify that mandatory parameters are not empty
      if (u1.getClassname().equals("") || u1.getUniverseextension().equals("") || u1.getVersionid().equals("")
          || u1.getObjectname().equals("")) {
        errorStrings.add("No empty values allowed for Class name, Object name or Universe extension.");
      }

      // Verify that there are no duplicate primary keys. A duplicate value is
      // detected if all the primary key column values are the same.
      for (Universeobject u2 : us) {
        if (u1.dbEquals(u2)) {
          errorStrings.add("Duplicate entries found with Object Name: " + u1.getObjectname());
        }
      }

      us.add(u1);
    }

    return errorStrings;
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
    return true;
  }

  public boolean delObj(RockDBObject rObj) {
    return true;
  }

  public boolean modObj(RockDBObject rObj) {
    return true;
  }

  public boolean modObj(RockDBObject rObj[]) {
    return true;
  }

  public boolean updated(DataModel dataModel) {
    return false;
  }

  public String[] getUniverseExtensions(String versionId) {

    if (versionId == null || versionId.equals(""))
      return null;

    List<String> rowData = new ArrayList<String>();

    for (String defaultStr : Constants.UNIVERSEEXTENSIONTYPES)
      rowData.add(defaultStr);

    try {

      Universename unvname = new Universename(etlRock);
      unvname.setVersionid(versionId);

      UniversenameFactory unvnameF = new UniversenameFactory(etlRock, unvname);

      Iterator unvnameIter = unvnameF.get().iterator();

      while (unvnameIter.hasNext()) {
        Universename tmpunivversename = (Universename) unvnameIter.next();
        rowData.add(tmpunivversename.getUniverseextension());
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    String[] retVal = new String[rowData.size()];
    for (int i = 0; i < retVal.length; i++)
      retVal[i] = rowData.get(i);

    return retVal;
  }
}
