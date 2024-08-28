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

import com.distocraft.dc5000.repository.dwhrep.Verificationcondition;
import com.distocraft.dc5000.repository.dwhrep.VerificationconditionFactory;
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
public class VerificationConditionDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(VerificationConditionDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private TechPackDataModel techPackDataModel;

  private List<Verificationcondition> verificationconditionData;

  private DataModelController dataModelController;

  public VerificationConditionDataModel(final RockFactory rockFactory) {
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
    logger.finest("VerificationConditionDataModel starting refresh from DB");
    if (currentVersioning == null) {
      verificationconditionData = new ArrayList<Verificationcondition>();
    } else {
      final Vector<Verificationcondition> vo = getVerificationconditions(currentVersioning.getVersionid());
      verificationconditionData = new ArrayList<Verificationcondition>(vo);
    }
    logger.info("VerificationConditionDataModel refreshed from DB");
  }

  private final static Comparator<Verificationcondition> VERIFICATIONOBJECTCOMPARATOR = new Comparator<Verificationcondition>() {

    public int compare(final Verificationcondition d1, final Verificationcondition d2) {

      final String s1 = Utils.replaceNull(d1.getFacttable());
      final String s2 = Utils.replaceNull(d2.getFacttable());
      return s1.compareTo(s2);
    }
  };

  /**
   * Returns a list of verification objects by versionId
   * 
   * @return results a list of verification objects
   */
  private Vector<Verificationcondition> getVerificationconditions(final String versionId) {
    Vector<Verificationcondition> results = new Vector<Verificationcondition>();
    final Verificationcondition whereVerificationcondition = new Verificationcondition(rockFactory);
    whereVerificationcondition.setVersionid(versionId);
    try {
      final VerificationconditionFactory VerificationconditionFactory = new VerificationconditionFactory(rockFactory,
          whereVerificationcondition, true);
      results = VerificationconditionFactory.get();
      Collections.sort(results, VERIFICATIONOBJECTCOMPARATOR);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getVerificationconditions", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getVerificationconditions", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getVerificationconditions", e);
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

  public List<Verificationcondition> getVerificationconditionData() {
    return verificationconditionData;
  }

  public void setVerificationconditionData(final List<Verificationcondition> verificationconditionData) {
    this.verificationconditionData = verificationconditionData;
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
