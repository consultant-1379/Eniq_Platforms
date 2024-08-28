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

import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Universetable;
import com.distocraft.dc5000.repository.dwhrep.UniversetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class UniverseTablesDataModel implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(UniverseTablesView.class.getName());

  private Versioning versioning;

  RockFactory etlRock;

  private UniverseTablesTableModel uttm;

  DataModelController dataModelController = null;

  Universetable universetable;

  public UniverseTablesDataModel(RockFactory etlRock) {

    this.etlRock = etlRock;
    try {
      this.uttm = createUTTableModel(null);
    } catch (Exception e) {
      logger.warning("Could not set TableModel for universeTable.");
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

  }

  public void setUniverseTable(DataTreeNode dataTreeNode) {
    universetable = (Universetable) dataTreeNode.getRockDBObject();
  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public UniverseTablesTableModel getUTTableModel() {
    return uttm;
  }

  public Object[] getTechPacks() {

    try {

      Versioning ver = new Versioning(etlRock);
      VersioningFactory verF = new VersioningFactory(etlRock, ver);

      Object[] result = new Object[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
        Versioning v = (Versioning) verF.get().get(i);
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version());
      }

      return result;

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  private UniverseTablesTableModel createUTTableModel(Versioning v) throws RockException, SQLException {

    Vector<Universetable> uv = null;
    if (v != null) {
      Universetable U = new Universetable(etlRock);
      U.setVersionid(v.getVersionid());
      UniversetableFactory UCF = new UniversetableFactory(etlRock, U, "order by ordernro");
      uv = UCF.get();
    }

    UniverseTablesTableModel uttm = new UniverseTablesTableModel(uv, etlRock);

    return uttm;
  }

  public void refresh(Versioning v) {
    try {
      this.versioning = v;
      this.uttm = createUTTableModel(v);
    } catch (Exception e) {
      logger.warning("Could not refresh UniverseTables.");
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

  public String getVersionid() {

    if (universetable == null) {
      return "";
    }
    return universetable.getVersionid();
  }

  public String getUniverseextension() {

    if (universetable == null) {
      return "";
    }
    return universetable.getUniverseextension();
  }

  public String getOwner() {

    if (universetable == null) {
      return "";
    }
    return universetable.getOwner();
  }

  public String getAlias() {

    if (universetable == null) {
      return "";
    }
    return universetable.getAlias();
  }

  public void save() throws Exception {
    // Get TP Versionid and throw if it is empty
    String vrVersionid = versioning.getVersionid();
    if (vrVersionid == null || vrVersionid.equals(""))
      throw new Exception("Trying to save without versionID");

    // Remove with this versionId
    try {
      Universetable delU = new Universetable(etlRock);
      delU.setVersionid(vrVersionid);

      UniversetableFactory delUF = new UniversetableFactory(etlRock, delU);
      Iterator deliter = delUF.get().iterator();

      while (deliter.hasNext()) {
        Universetable tmpdelU = (Universetable) deliter.next();
        tmpdelU.deleteDB();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    // Then add the ones on the Screen
    try {
      UniverseTablesTableModel tableModel = getUTTableModel();
      for (Universetable u : tableModel.data) {

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
    UniverseTablesTableModel tableModel = getUTTableModel();
    if (tableModel == null)
      return null;

    // Iterate through the table data.
    List<Universetable> us = new ArrayList<Universetable>();
    for (Universetable u1 : tableModel.data) {

      // Replace null values with empty values.
      u1.removeNulls();

      // Verify that mandatory parameters are not empty
      if (u1.getOwner().equals("") || u1.getUniverseextension().equals("") || u1.getVersionid().equals("")
          || u1.getTablename().equals("")) {
        errorStrings.add("No empty values allowed for Owner, Name or Universe extension.");
      }

      // Verify that there are no duplicate primary keys. A duplicate value is
      // detected if 1) all the primary key column values are the same or 2)
      // when other primary key column values are the same and the universe
      // extension is "ALL" in the other one.
      for (Universetable u2 : us) {

        // Check for the duplicate
        if (u1.dbEquals(u2)) {
          // All primary keys match
          errorStrings.add("Duplicate entries found with Name " + u1.getTablename() + ".");
        } else if (u1.getTablename().equals(u2.getTablename()) && u1.getVersionid().equals(u2.getVersionid())
            && (u1.getUniverseextension().equals("ALL") || u2.getUniverseextension().equals("ALL"))) {
          // TableName and VersionId match and one of the tables has extension
          // "ALL"
          errorStrings.add("Duplicate entries found with Name " + u1.getTablename()
              + ". Universe extension 'ALL' is used!");
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
    RockDBObject[] r = new RockDBObject[1];
    r[0] = rObj;
    return modObj(r);
  }

  public boolean modObj(RockDBObject rObj[]) {

    try {

      etlRock.getConnection().setAutoCommit(false);

      for (int i = 0; i < rObj.length; i++) {
        rObj[i].updateDB();
      }

      dataModelController.rockObjectsModified(this);

      etlRock.getConnection().commit();

    } catch (Exception e) {
      try {
        etlRock.getConnection().rollback();
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        etlRock.getConnection().setAutoCommit(true);
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
    }

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
