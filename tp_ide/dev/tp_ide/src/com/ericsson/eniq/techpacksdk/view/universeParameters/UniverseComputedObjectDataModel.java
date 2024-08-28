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

import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.UniverseclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.UniversecomputedobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;

public class UniverseComputedObjectDataModel implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(UniverseComputedObjectDataModel.class.getName());

  private Versioning versioning;

  RockFactory etlRock;

  private UniverseComputedObjectTableModel uotm;

  DataModelController dataModelController = null;

  UniverseParametersDataModel universeParametersDataModel;

  Universecomputedobject universeobject;

  public boolean dataRefreshed = false;;

  public UniverseComputedObjectDataModel(RockFactory etlRock) {

    this.etlRock = etlRock;
    this.universeParametersDataModel = new UniverseParametersDataModel(this.etlRock);
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

  public UniverseComputedObjectTableModel getTableModel() {
    return uotm;
  }

  private UniverseComputedObjectTableModel createTableModel(Versioning v) throws RockException, SQLException {

    Vector<Universecomputedobject> u = null;
    if (v != null) {
      Universecomputedobject U = new Universecomputedobject(etlRock);
      U.setVersionid(v.getVersionid());
      UniversecomputedobjectFactory UF = new UniversecomputedobjectFactory(etlRock, U, "order by ordernro");
      u = UF.get();
    }

    GenericUniverseTableModel<Universeparameters> parametersTableModel = this.universeParametersDataModel
        .getTableModel();
    UniverseComputedObjectTableModel uotm = new UniverseComputedObjectTableModel(u, parametersTableModel, this.etlRock);

    return uotm;
  }

  public void refresh(Versioning v) {
    try {
      this.versioning = v;
      this.universeParametersDataModel.refresh(v);
      this.uotm = createTableModel(v);
    } catch (Exception e) {
      logger.warning("Could not refresh UniverseComputedObject.");
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

    logger.info("Saving Universecomputedobjects.");

    // Delete the items with current versionid from Universeparameters table.
    // The deletion must be performed, since there is a dependency between the
    // Universecomputedobject and Universeparameters tables.
    logger.info("Deleting items from Universeparameters table.");
    this.universeParametersDataModel.deleteItemsWithCurrentVersionId();

    // Remove with this versionId
    logger.info("Deleting items from Universecomputedobject table.");
    try {
      Universecomputedobject delU = new Universecomputedobject(etlRock);
      delU.setVersionid(vrVersionid);

      UniversecomputedobjectFactory delUF = new UniversecomputedobjectFactory(etlRock, delU);
      Iterator deliter = delUF.get().iterator();

      while (deliter.hasNext()) {
        Universecomputedobject tmpdelU = (Universecomputedobject) deliter.next();
        tmpdelU.deleteDB();
      }
    } catch (Exception e) {
      logger.warning("Exception while deleting Universecomputedobjects items:\n" + e.toString());
      throw e;
    }

    // Then add the ones on the Screen
    logger.info("Adding items to Universecomputedobject table.");
    try {
      UniverseComputedObjectTableModel tableModel = getTableModel();
      for (Universecomputedobject u : tableModel.data) {

        if (!vrVersionid.equals(u.getVersionid()))
          throw new Exception("Trying to save for wrong versionId");

        u.insertDB();
      }
    } catch (Exception e) {
      logger.warning("Exception while adding Universecomputedobjects items:\n" + e.toString());
      throw e;
    }

    // Finally, save the UniverseParametersDataModel.
    logger.info("Adding items to Universeparameters table.");
    this.universeParametersDataModel.save();
  }

  /**
   * Data validation
   * 
   * @return errormessages
   */
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    UniverseComputedObjectTableModel tm = getTableModel();
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
    List<Universecomputedobject> us = new ArrayList<Universecomputedobject>();
    for (Universecomputedobject u1 : tm.data) {

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
      for (Universecomputedobject u2 : us) {
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
    if (dataModel instanceof UniverseFormulasDataModel) {
      dataRefreshed = true;
      refresh(versioning);
      return true;
    }
    if (dataModel instanceof MeasurementTypeDataModel) {
      dataRefreshed = true;
      refresh(versioning);
      return true;
    }
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

  public String[] getUniverseFormulas(String versionId) {
    String[] result;

    UniverseObjectFormulas formulas = new UniverseObjectFormulas(this.etlRock, versionId);
    result = formulas.getNames();

    return result;
  }

  public String[] getMeasurementTypeNames(String versionId) {
    String[] result = null;

    try {
      Measurementtype m = new Measurementtype(this.etlRock);
      m.setVersionid(versionId);
      MeasurementtypeFactory mF = new MeasurementtypeFactory(this.etlRock, m, true);
      Vector<Measurementtype> resultingMeasurementTypes = mF.get();
      result = new String[resultingMeasurementTypes.size()];
      for (int i = 0; i < result.length; ++i) {
        result[i] = resultingMeasurementTypes.get(i).getTypename();
      }
    } catch (SQLException e) {
      logger.warning("Unable to retrieve measurement types.\n" + e);
    } catch (RockException e) {
      logger.warning("Unable to retrieve measurement types.\n" + e);
    }

    return result;
  }

  public String[] getMeasurementKeyDataNames(String versionId) {
    String[] result = null;
    Vector<String> resultVector = new Vector<String>();

    try {
      String[] typeIds = getTypeIds(versionId);
      for (int i = 0; i < typeIds.length; ++i) {
        String typeId = typeIds[i];
        Measurementkey measurementKey = new Measurementkey(this.etlRock);
        measurementKey.setTypeid(typeId);
        MeasurementkeyFactory mF = new MeasurementkeyFactory(this.etlRock, measurementKey, true);
        Iterator<Measurementkey> queryResults = mF.get().iterator();
        while (queryResults.hasNext()) {
          Measurementkey queryResult = queryResults.next();
          String dataName = queryResult.getDataname();
          if (!resultVector.contains(dataName)) {
            resultVector.add(dataName);
          }
        }
      }
    } catch (SQLException e) {
      logger.warning("Unable to retrieve measurement keys.\n" + e);
    } catch (RockException e) {
      logger.warning("Unable to retrieve measurement keys.\n" + e);
    }

    result = new String[resultVector.size()];
    result = resultVector.toArray(result);
    return result;
  }

  public String[] getMeasurementCounterDataNames(String versionId) {
    String[] result = null;
    Vector<String> resultVector = new Vector<String>();

    try {
      String[] typeIds = getTypeIds(versionId);
      for (int i = 0; i < typeIds.length; ++i) {
        String typeId = typeIds[i];
        Measurementcounter measurementCounter = new Measurementcounter(this.etlRock);
        measurementCounter.setTypeid(typeId);
        MeasurementcounterFactory mF = new MeasurementcounterFactory(this.etlRock, measurementCounter, true);
        Iterator<Measurementcounter> queryResults = mF.get().iterator();
        while (queryResults.hasNext()) {
          Measurementcounter queryResult = queryResults.next();
          String dataName = queryResult.getDataname();
          if (!resultVector.contains(dataName)) {
            resultVector.add(dataName);
          }
        }
      }
    } catch (SQLException e) {
      logger.warning("Unable to retrieve measurement counters.\n" + e);
    } catch (RockException e) {
      logger.warning("Unable to retrieve measurement counters.\n" + e);
    }

    result = new String[resultVector.size()];
    result = resultVector.toArray(result);
    return result;
  }

  private String[] getTypeIds(String versionId) {
    String[] result = null;

    try {
      Measurementtype m = new Measurementtype(this.etlRock);
      m.setVersionid(versionId);
      MeasurementtypeFactory mF = new MeasurementtypeFactory(this.etlRock, m, true);
      Vector<Measurementtype> resultingMeasurementTypes = mF.get();
      result = new String[resultingMeasurementTypes.size()];
      for (int i = 0; i < result.length; ++i) {
        result[i] = resultingMeasurementTypes.get(i).getTypeid();
      }
    } catch (SQLException e) {
      logger.warning("Unable to retrieve measurement types.\n" + e);
    } catch (RockException e) {
      logger.warning("Unable to retrieve measurement types.\n" + e);
    }

    return result;
  }

  /**
   * 
   * 
   * @return An instance of universe parameters data model.
   */
  public UniverseParametersDataModel getUniverseParametersDataModel() {
    return this.universeParametersDataModel;
  }

}
