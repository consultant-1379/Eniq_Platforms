/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.verification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Verificationobject;
import com.distocraft.dc5000.repository.dwhrep.VerificationobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;

/**
 * @author eheijun
 * 
 */
public class VerificationObjectDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(VerificationObjectDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private TechPackDataModel techPackDataModel;

  private List<Verificationobject> verificationobjectData;

  private DataModelController dataModelController;

  public VerificationObjectDataModel(final RockFactory rockFactory) {
    this.rockFactory = rockFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.datamodel.DataModel#getRockFactory()
   */
  public RockFactory getRockFactory() {
    return rockFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.datamodel.DataModel#refresh()
   */
  public void refresh() {
    logger.finest("VerificationObjectDataModel starting refresh from DB");
    if (currentVersioning == null) {
      verificationobjectData = new ArrayList<Verificationobject>();
    } else {
      final Vector<Verificationobject> vo = getVerificationobjects(currentVersioning.getVersionid());
      verificationobjectData = new ArrayList<Verificationobject>(vo);
    }
    logger.info("VerificationObjectDataModel refreshed from DB");
  }

  private final static Comparator<Verificationobject> VERIFICATIONOBJECTCOMPARATOR = new Comparator<Verificationobject>() {

    public int compare(final Verificationobject d1, final Verificationobject d2) {

      final String s1 = Utils.replaceNull(d1.getObjectclass());
      final String s2 = Utils.replaceNull(d2.getObjectclass());
      return s1.compareTo(s2);
    }
  };

  /**
   * Returns a list of verification objects by versionId
   * 
   * @return results a list of verification objects
   */
  private Vector<Verificationobject> getVerificationobjects(final String versionId) {
    Vector<Verificationobject> results = new Vector<Verificationobject>();
    final Verificationobject whereVerificationobject = new Verificationobject(rockFactory);
    whereVerificationobject.setVersionid(versionId);
    try {
      final VerificationobjectFactory VerificationobjectFactory = new VerificationobjectFactory(rockFactory,
          whereVerificationobject, true);
      results = VerificationobjectFactory.get();
      Collections.sort(results, VERIFICATIONOBJECTCOMPARATOR);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getVerificationobjects", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getVerificationobjects", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getVerificationobjects", e);
    }

    return results;
  }

  public Versioning getCurrentVersioning() {
    return currentVersioning;
  }

  public void setCurrentVersioning(final Versioning versioning) {
    currentVersioning = versioning;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#delObj(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean delObj(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#modObj(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean modObj(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#modObj(ssc.rockfactory
   * .RockDBObject[])
   */
  public boolean modObj(final RockDBObject[] obj) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#newObj(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean newObj(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.datamodel.DataModel#save()
   */
  public void save() throws Exception {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#validateDel(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean validateDel(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#validateMod(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean validateMod(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#validateNew(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean validateNew(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public List<Verificationobject> getVerificationobjectData() {
    return verificationobjectData;
  }

  public void setVerificationobjectData(final List<Verificationobject> verificationobjectData) {
    this.verificationobjectData = verificationobjectData;
  }

  public boolean updated(final DataModel dataModel) {
    if (dataModel instanceof TechPackDataModel) {
      techPackDataModel = (TechPackDataModel) dataModel;
      if (techPackDataModel.getVersioning() != null) {
        this.setCurrentVersioning(techPackDataModel.getVersioning());
        refresh();
      }
      return true;
    }
    return false;
  }

  public DataModelController getDataModelController() {
    return dataModelController;
  }

  public void setDataModelController(final DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

}
