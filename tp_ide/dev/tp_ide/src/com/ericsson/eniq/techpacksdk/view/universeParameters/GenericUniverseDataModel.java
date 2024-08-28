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
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

abstract public class GenericUniverseDataModel<E extends RockDBObject> implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(GenericUniverseDataModel.class.getName());

  public Versioning versioning;

  public RockFactory etlRock;

  private GenericUniverseTableModel tablemodel;

  DataModelController dataModelController = null;
  
  protected GenericUniverseDataModel() {
    
  }

  /**
   * Constructor
   * 
   * @param etlRock
   */
  public GenericUniverseDataModel(RockFactory etlRock) {
    this.etlRock = etlRock;
    try {
      this.tablemodel = createTableModel(null);
    } catch (Exception e) {
      logger.warning("Could not set TableModel.");
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  /**
   * Sets the datamodelcontroller
   * 
   * @param dataModelController
   */
  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  /**
   * Get related tablemodel
   * 
   * @return
   */
  public GenericUniverseTableModel getTableModel() {
    return tablemodel;
  }

  /**
   * Creates tablemodel
   * 
   * @param v
   * @return
   * @throws RockException
   * @throws SQLException
   */
  abstract public GenericUniverseTableModel createTableModel(Versioning v) throws RockException, SQLException;

  /**
   * Refresh with versioning
   * 
   * @param v
   */
  public void refresh(Versioning v) {
    try {
      this.versioning = v;
      this.tablemodel = createTableModel(v);
    } catch (Exception e) {
      logger.warning("Could not refresh.");
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

  /**
   * Save to database
   */
  abstract public void save() throws Exception;

  /**
   * Data validation
   * 
   * @return errormessages
   */
  abstract public Vector<String> validateData();

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

      UniversenameFactory unvnameF = new UniversenameFactory(etlRock, unvname, "order by ordernro");

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
