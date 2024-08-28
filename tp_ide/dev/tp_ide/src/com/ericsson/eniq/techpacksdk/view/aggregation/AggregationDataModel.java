/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.aggregation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;

/**
 * @author eheijun
 * 
 */
public class AggregationDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(AggregationDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private Versioning baseVersioning;

  private TechPackDataModel techPackDataModel;

  private List<AggregationData> aggregations;

  public AggregationDataModel(RockFactory rockFactory) {
    this.rockFactory = rockFactory;
  }

  public RockFactory getRockFactory() {
    return rockFactory;
  }

  public Versioning getCurrentVersioning() {
    return techPackDataModel.getVersioning();
  }

  public void setCurrentVersioning(final Versioning versioning) {
    currentVersioning = versioning;
  }

  public Versioning getBaseVersioning() {
    return baseVersioning;
  }

  public void setBaseVersioning(final Versioning versioning) {
    baseVersioning = versioning;
  }

  public List<AggregationData> getAggregations() {
    return aggregations;
  }

  public void refresh() {

    aggregations = new ArrayList<AggregationData>();

    if (currentVersioning != null) {
      final Vector<Aggregation> mt = getAggregations(currentVersioning.getVersionid());

      for (final Iterator<Aggregation> it = mt.iterator(); it.hasNext();) {
        final Aggregation aggregation = it.next();
        final Vector<Aggregationrule> rules = getRulesForAggregation(aggregation);
        final AggregationData data = new AggregationData(currentVersioning, aggregation, rules, rockFactory);
        aggregations.add(data);
      }
    }

    logger.info("AggregationDataModel refreshed from DB");
  }

  public void save() {

  }

  public void deleteGenerated(final Aggregation aggregation) throws SQLException, RockException {
  }
  
  public void createGenerated(final Aggregation aggregation) {
  }

  public void migrate(String versionid) throws Exception {
  }

  private final static Comparator<Aggregation> AGGREGATIONCOMPARATOR = new Comparator<Aggregation>() {

    public int compare(final Aggregation d1, final Aggregation d2) {

      final String s1 = Utils.replaceNull(d1.getAggregation());
      final String s2 = Utils.replaceNull(d2.getAggregation());
      return s1.compareTo(s2);
    }
  };

  private final static Comparator<Aggregationrule> AGGREGATIONRULECOMPARATOR = new Comparator<Aggregationrule>() {

    public int compare(final Aggregationrule d1, final Aggregationrule d2) {

      final Long i1 = Utils.replaceNull(d1.getRuleid()).longValue();
      final Long i2 = Utils.replaceNull(d2.getRuleid()).longValue();
      return i1.compareTo(i2);
    }
  };

  /**
   * Returns a list of rules for aggregation 
   * 
   * @param aggregation
   *          the parent of the rules
   * @return results a list of aggregation rules
   */
  private Vector<Aggregationrule> getRulesForAggregation(final Aggregation aggregation) {
    Vector<Aggregationrule> results = new Vector<Aggregationrule>();

    final Aggregationrule whereAggregationrule = new Aggregationrule(rockFactory);
    whereAggregationrule.setAggregation(aggregation.getAggregation());
    try {
      final AggregationruleFactory aggregationruleFactory = new AggregationruleFactory(rockFactory,
          whereAggregationrule, true);
      results = aggregationruleFactory.get();
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getRulesForAggregation", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getRulesForAggregation", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getRulesForAggregation", e);
    }
    Collections.sort(results, AGGREGATIONRULECOMPARATOR);
    return results;
  }

  /**
   * Returns a list of aggregations by versionId
   * 
   * @return results a list of aggregations
   */
  private Vector<Aggregation> getAggregations(final String versionId) {

    Vector<Aggregation> results = new Vector<Aggregation>();

    final Aggregation whereAggregation = new Aggregation(rockFactory);
    whereAggregation.setVersionid(versionId);
    try {
      final AggregationFactory aggregationFactory = new AggregationFactory(rockFactory, whereAggregation,
          true);
      results = aggregationFactory.get();
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getAggregations", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getAggregations", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getAggregations", e);
    }
    Collections.sort(results, AGGREGATIONCOMPARATOR);
    return results;
  }

  public boolean delObj(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean modObj(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean modObj(final RockDBObject[] obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean newObj(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean validateDel(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean validateMod(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean validateNew(final RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean updated(final DataModel dataModel) {
    if (dataModel instanceof TechPackDataModel) {
      techPackDataModel = (TechPackDataModel) dataModel;
      if (techPackDataModel.getVersioning() != null) {
        this.setCurrentVersioning(techPackDataModel.getVersioning());
        this.setBaseVersioning(techPackDataModel.getBaseversioning());
        refresh();
      }
      return true;
    } else if (dataModel instanceof MeasurementTypeDataModel) {
      refresh();
      return true;
    }
    return false;
  }

}
