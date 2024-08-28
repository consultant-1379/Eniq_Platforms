/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.BusyhourplaceholdersFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.BusyhourrankkeysFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.BusyhoursourceFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.common.Utils;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.Key;
import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;
import com.ericsson.eniq.common.setWizards.CreateAggregatorSet;

/**
 * @author eheijun
 * 
 */
public class RockDatabaseSession implements DatabaseSession {

  private final Logger log = Logger.getLogger(RockDatabaseSession.class.getName());

  private final RockFactory dwhrepRockFactory;

  private final RockFactory etlrepRockFactory;

  private final RockFactory dwhRockFactory;

  /**
   * Creates session for dwhrep & dwh rock databases
   * 
   * @param dwhrepRockFactory
   *          for dwhrep database
   * @param dwhRockFactory
   *          for dwh database
   */
  public RockDatabaseSession(final RockFactory dwhrepRockFactory, final RockFactory dwhRockFactory,
      final RockFactory etlrepRockFactory) {
    super();
    if ((dwhrepRockFactory == null) || (dwhRockFactory == null)) {
      log.warning("RockFactories has not been initialized");
    }
    this.dwhrepRockFactory = dwhrepRockFactory;
    this.dwhRockFactory = dwhRockFactory;
    this.etlrepRockFactory = etlrepRockFactory;
  }

  /**
   * @return the dwhrepRockFactory
   */
  public RockFactory getDwhrepRockFactory() {
    return dwhrepRockFactory;
  }

  /**
   * @return the dwhRockFactory
   */
  public RockFactory getDwhRockFactory() {
    return dwhRockFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.database.DatabaseSession#dropConnection()
   */
  @Override
  public void clear() throws DatabaseException {
    String error = "";
    try {
      if (dwhRockFactory != null) {
        if (dwhRockFactory.getConnection() != null) {
          if (!dwhRockFactory.getConnection().isClosed()) {
            dwhRockFactory.getConnection().close();
          }
        }
      }
    } catch (Exception e) {
      log.severe("Rock Error:" + e.getMessage());
      error += e.getMessage() + "\n";
    }
    try {
      if (dwhrepRockFactory != null) {
        if (dwhrepRockFactory.getConnection() != null) {
          if (!dwhrepRockFactory.getConnection().isClosed()) {
            dwhrepRockFactory.getConnection().close();
          }
        }
      }
    } catch (Exception e) {
      log.severe("Rock Error:" + e.getMessage());
      error += e.getMessage() + "\n";
    }
    try {
      if (etlrepRockFactory != null) {
        if (etlrepRockFactory.getConnection() != null) {
          if (!etlrepRockFactory.getConnection().isClosed()) {
            etlrepRockFactory.getConnection().close();
          }
        }
      }
    } catch (Exception e) {
      log.severe("Rock Error:" + e.getMessage());
      error += e.getMessage() + "\n";
    }
    if (!error.equals("")) {
      throw new DatabaseException(error);
    }
  }

  /**
   * Sorter for Tpactivation
   */
  private final static Comparator<Tpactivation> TPACTIVATIONCOMPARATOR = new Comparator<Tpactivation>() {

    public int compare(final Tpactivation d1, final Tpactivation d2) {
      return d1.getVersionid().compareTo(d2.getVersionid());
    }
  };

  /**
   * Sorter for Busyhours
   */
  private static final Comparator<Busyhour> BUSYHOURCOMPARATOR = new Comparator<Busyhour>() {

    public int compare(final Busyhour d1, final Busyhour d2) {
      int result = d1.getTargetversionid().compareTo(d2.getTargetversionid());
      if (result == 0) {
        result = d1.getBhlevel().compareTo(d2.getBhlevel());
        if (result == 0) {
          if ((d2.getPlaceholdertype() != null) && (d1.getPlaceholdertype() != null)) {
            result = d2.getPlaceholdertype().compareTo(d1.getPlaceholdertype());
          }
          if (result == 0) {
        	  final String s3 = Utils.replaceNull(d1.getBhtype());
              final String s4 = Utils.replaceNull(d2.getBhtype());
        	  final Integer insS3 = Utils.extractDigitsFromEndOfString(s3);
              final Integer insS4 = Utils.extractDigitsFromEndOfString(s4);
              
              result = insS3.compareTo(insS4);   
          }
        }
      }
      return result;
    }
  };

  /**
   * Sorter for Busyhour Sources
   */
  private final static Comparator<Busyhoursource> BUSYHOURSOURCECOMPARATOR = new Comparator<Busyhoursource>() {

    public int compare(final Busyhoursource d1, final Busyhoursource d2) {
      return d1.getTypename().compareTo(d2.getTypename());
    }
  };

  /**
   * Sorter for Busyhour Mappings
   */
  private final static Comparator<Busyhourmapping> BUSYHOURMAPPINGCOMPARATOR = new Comparator<Busyhourmapping>() {

    public int compare(final Busyhourmapping d1, final Busyhourmapping d2) {
      return d1.getBhtargettype().compareTo(d2.getBhtargettype());
    }
  };

  /**
   * Sorter for Busyhour Keys
   */
  private final static Comparator<Busyhourrankkeys> BUSYHOURRANKKEYCOMPARATOR = new Comparator<Busyhourrankkeys>() {

    public int compare(final Busyhourrankkeys d1, final Busyhourrankkeys d2) {
      return d1.getKeyname().compareTo(d2.getKeyname());
    }
  };

  /**
   * Sorter for Measurement tables
   */
  private static final Comparator<Measurementtable> MTABLECOMPARATOR = new Comparator<Measurementtable>() {

    public int compare(final Measurementtable mt1, final Measurementtable mt2) {
      return mt1.getBasetablename().compareTo(mt2.getBasetablename());
    }
  };

  private static final Comparator<Referencetable> RTABLECOMPARATOR = new Comparator<Referencetable>() {

    public int compare(final Referencetable rt1, final Referencetable rt2) {
      return rt1.getTypename().compareTo(rt2.getTypename());
    }
  };

  private static final Comparator<Busyhourplaceholders> BUSYHOURPLACEHOLDERCOMPARATOR = new Comparator<Busyhourplaceholders>() {

    public int compare(final Busyhourplaceholders d1, final Busyhourplaceholders d2) {
      int result = d1.getVersionid().compareTo(d2.getVersionid());
      if (result == 0) {
        result = d1.getBhlevel().compareTo(d2.getBhlevel());
      }
      return result;
    }
  };

  private static final Comparator<Measurementkey> MEASUREMENTKEYCOMPARATOR = new Comparator<Measurementkey>() {

    public int compare(final Measurementkey mk1, final Measurementkey mk2) {
      return mk1.getTableName().compareTo(mk2.getTableName());
    }
  };

  /**
   * Get all activated Versionings from the repository sorted by VersionId.
   * @return vector of Versioning objects
   * @throws DatabaseException
   * @author ejohabd added method for HM56886 
   */
  public Vector<Tpactivation> getActivatedVersionings() throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Tpactivation a = new Tpactivation(dwhrepRockFactory);
        a.setStatus("ACTIVE");
        final TpactivationFactory aF = new TpactivationFactory(dwhrepRockFactory, a, false);
        final Vector<Tpactivation> tpactivations = aF.get();
        Collections.sort(tpactivations, TPACTIVATIONCOMPARATOR);
        return tpactivations;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Tpactivations");
      }
    }
    return null;
  }
  
  /**
   * Get all activated PM TechPack Type Versionings from the repository sorted by VersionId.
   * 
   * @return vector of Versioning objects
   * @throws DatabaseException
   */
  public Vector<Tpactivation> getActivatedPMTypeVersionings() throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Tpactivation a = new Tpactivation(dwhrepRockFactory);
        a.setStatus("ACTIVE");
        a.setType("PM");
        final TpactivationFactory aF = new TpactivationFactory(dwhrepRockFactory, a, false);
        final Vector<Tpactivation> tpactivations = aF.get();
        Collections.sort(tpactivations, TPACTIVATIONCOMPARATOR);
        return tpactivations;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Tpactivations");
      }
    }
    return null;
  }

  /**
   * Get all existing busyhour sourcetables from the repository
   * 
   * @param versionid
   * @return
   * @throws DatabaseException
   */
  public Vector<Measurementtable> getBusyhourSourceTables(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Vector<Measurementtable> basetables = new Vector<Measurementtable>();
        final Measurementtype mt = new Measurementtype(dwhrepRockFactory);
        mt.setVersionid(versionid);
        final MeasurementtypeFactory mtF = new MeasurementtypeFactory(dwhrepRockFactory, mt, false);
        final Vector<Measurementtype> mtypes = mtF.get();
        for (Measurementtype mtype : mtypes) {
          final Measurementtable mta = new Measurementtable(dwhrepRockFactory);
          mta.setTypeid(mtype.getTypeid());
          mta.setTablelevel("RAW");
          final MeasurementtableFactory mtaFraw = new MeasurementtableFactory(dwhrepRockFactory, mta, false);
          final Vector<Measurementtable> rawmtables = mtaFraw.get();
          for (Measurementtable mtable : rawmtables) {
            if (mtable.getBasetablename() != null) {
              basetables.add(mtable);
            }
          }
          mta.setTablelevel("COUNT");
          final MeasurementtableFactory mtaFcount = new MeasurementtableFactory(dwhrepRockFactory, mta, false);
          final Vector<Measurementtable> countmtables = mtaFcount.get();
          for (Measurementtable mtable : countmtables) {
            if (mtable.getBasetablename() != null) {
              basetables.add(mtable);
            }
          }
        }
        if (basetables != null) {
          Collections.sort(basetables, MTABLECOMPARATOR);
          return basetables;
        }
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Basetables");
      }
    }
    return null;
  }

  /**
   * Get all existing busyhour referencetables from the repository
   * 
   * @param versionid
   * @return
   * @throws DatabaseException
   */
  public Vector<Referencetable> getBusyhourReferenceTables(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Vector<Referencetable> referencetables = new Vector<Referencetable>();
        final Referencetable rt = new Referencetable(dwhrepRockFactory);
        rt.setVersionid(versionid);
        final ReferencetableFactory rtF = new ReferencetableFactory(dwhrepRockFactory, rt, false);
        final Vector<Referencetable> rtables = rtF.get();
        for (Referencetable rtable : rtables) {
          if (!"VIEW".equals(rtable.getTable_type())) {
            referencetables.add(rtable);
          }
        }
        if (referencetables != null) {
          Collections.sort(referencetables, RTABLECOMPARATOR);
          return referencetables;
        }
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Basetables");
      }
    }
    return null;
  }

  /**
   * Get all busyhours of the tp form the repository sorted by TargetVersionId
   * and BHLevel and BHType.
   * 
   * @param versionid
   *          condition to get busyhours
   * @return sorted vector of busyhour objects for given TP
   * @throws DatabaseException
   */
  public Vector<Busyhour> getBusyhours(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhour bh = new Busyhour(dwhrepRockFactory);
        bh.setVersionid(versionid);
        final BusyhourFactory bhF = new BusyhourFactory(dwhrepRockFactory, bh, false);
        final Vector<Busyhour> busyhours = bhF.get();
        Collections.sort(busyhours, BUSYHOURCOMPARATOR);
        return busyhours;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Busyhours");
      }
    }
    return null;
  }

  /**
   * Get all busyhour placeholders from the repository
   * 
   * @param versionid
   * @return
   * @throws DatabaseException
   */
  public Vector<Busyhourplaceholders> getBusyhourplaceholders(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhourplaceholders bhph = new Busyhourplaceholders(dwhrepRockFactory);
        bhph.setVersionid(versionid);
        final BusyhourplaceholdersFactory bhphF = new BusyhourplaceholdersFactory(dwhrepRockFactory, bhph, false);
        final Vector<Busyhourplaceholders> busyhourplaceholders = bhphF.get();
        Collections.sort(busyhourplaceholders, BUSYHOURPLACEHOLDERCOMPARATOR);
        return busyhourplaceholders;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Busyhourplaceholders");
      }
    }
    return null;
  }

  /**
   * Get tpactivation from repository
   * 
   * @param versionid
   * @return
   * @throws DatabaseException
   */
  public Tpactivation getActivatedVersioning(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Tpactivation whereTpactivation = new Tpactivation(dwhrepRockFactory);
        whereTpactivation.setVersionid(versionid);
        final TpactivationFactory taF = new TpactivationFactory(dwhrepRockFactory, whereTpactivation, false);
        final Vector<Tpactivation> tpactivations = taF.get();
        if (!tpactivations.isEmpty()) {
          return tpactivations.firstElement();
        }
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Versioning information");
      }
    }
    return null;
  }

  /**
   * Save busyhour into repository
   * 
   * @param busyhour
   *          object to save
   * @throws DatabaseException
   */
  public void setPlaceholder(final DefaultPlaceholder placeholder) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        dwhrepRockFactory.getConnection().setAutoCommit(false);
        final Tpactivation whereTpactivation = new Tpactivation(dwhrepRockFactory);
        whereTpactivation.setVersionid(placeholder.getVersionid());
        final TpactivationFactory taF = new TpactivationFactory(dwhrepRockFactory, whereTpactivation, true);
        final Vector<Tpactivation> tpactivations = taF.get();
        Tpactivation tpactivation = null;
        Integer modified = 0;
        if (!tpactivations.isEmpty()) {
          tpactivation = tpactivations.firstElement();
          modified = tpactivation.getModified();
        }
        final Busyhour whereBusyhour = new Busyhour(dwhrepRockFactory);
        whereBusyhour.setVersionid(placeholder.getVersionid());
        whereBusyhour.setBhlevel(placeholder.getBhlevel());
        whereBusyhour.setBhtype(placeholder.getBhtype());
        whereBusyhour.setTargetversionid(placeholder.getTargetversionid());
        whereBusyhour.setBhobject(placeholder.getBhobject());
        final BusyhourFactory bhF = new BusyhourFactory(dwhrepRockFactory, whereBusyhour, true);
        final Vector<Busyhour> busyhours = bhF.get();
        if (busyhours.size() > 0) {
          final Busyhour busyhour = busyhours.firstElement();
          // delete aggregations and rules
          StorageTimeAction.deleteRankbhAggregationsForBusyhour(busyhour, busyhour.getVersionid(), dwhrepRockFactory);
          // delete sources first
          deleteBusyhourSources(busyhour);
          // delete keys
          deleteBusyhourKeys(busyhour);
          // delete mapped types
          deleteBusyhourMappedTypes(busyhour);
          Boolean reactivate = Utils.integerToBoolean(busyhour.getReactivateviews());
          // update placeholder
          if (!Utils.replaceNull(busyhour.getDescription()).equals(placeholder.getDescription())) {
            busyhour.setDescription(placeholder.getDescription());
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.replaceNull(busyhour.getWhereclause()).equals(placeholder.getWhere())) {
            busyhour.setWhereclause(placeholder.getWhere());
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.replaceNull(busyhour.getBhcriteria()).equals(placeholder.getCriteria())) {
            busyhour.setBhcriteria(placeholder.getCriteria());
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!busyhour.getGrouping().equals(placeholder.getGrouping())) {
            busyhour.setGrouping(placeholder.getGrouping());
          }
          if (!busyhour.getEnable().equals(Utils.booleanToInteger(placeholder.getEnabled()))) {
            busyhour.setEnable(Utils.booleanToInteger(placeholder.getEnabled()));
          }
          if (!busyhour.getAggregationtype().equals(placeholder.getAggregationType())) {
            busyhour.setAggregationtype(placeholder.getAggregationType());
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!busyhour.getWindowsize().equals(placeholder.getWindowsize())) {
            busyhour.setWindowsize(placeholder.getWindowsize());
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.replaceNull(placeholder.getOffset()).equals(busyhour.getOffset())) {
            busyhour.setOffset(Utils.replaceNull(placeholder.getOffset()));
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.replaceNull(placeholder.getLookback()).equals(busyhour.getLookback())) {
            busyhour.setLookback(Utils.replaceNull(placeholder.getLookback()));
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.replaceNull(placeholder.getPThreshold()).equals(busyhour.getP_threshold())) {
            busyhour.setP_threshold(Utils.replaceNull(placeholder.getPThreshold()));
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.replaceNull(placeholder.getNThreshold()).equals(busyhour.getN_threshold())) {
            busyhour.setN_threshold(Utils.replaceNull(placeholder.getNThreshold()));
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          // busyhour.setClause(null);
          // check if sources has been changed
          if (sourcesChanged(placeholder)) {
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          // check if keys has been changed
          if (keysChanged(placeholder)) {
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          // check if mappedtypes has been changed
          if (mappedtypesChanged(placeholder)) {
            reactivate = Boolean.valueOf(true);
            modified = Integer.valueOf(1);
          }
          if (!Utils.booleanToInteger(reactivate).equals(busyhour.getReactivateviews())) {
            busyhour.setReactivateviews(Utils.booleanToInteger(reactivate));
          }
          busyhour.saveToDB();
          // create sources
          createBusyhourSources(placeholder);
          // create keys
          createBusyhourKeys(placeholder);
          // create MappedTypes
          createBusyhourMappedtypes(placeholder);
          // create aggregations and rules
          StorageTimeAction.createRankbhAggregationsForBusyhour(busyhour, getBusyhourSources(busyhour), busyhour
              .getVersionid(), dwhrepRockFactory);
          // update log_aggregationRules
          createDWHAggregationRules(placeholder);
          if (modified.intValue() > tpactivation.getModified().intValue()) {
            tpactivation.setModified(modified);
            tpactivation.saveToDB();
          }
        }
        dwhrepRockFactory.getConnection().commit();
      } catch (Exception e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Busyhour update failed (" + e.getMessage() + ")");
      } finally {
      }
    }
  }

  /*
   * Check if any changes has been made in sources
   */
  private boolean sourcesChanged(final DefaultPlaceholder placeholder) {
    if (placeholder.getRemovedSources().size() > 0) {
      return true;
    }
    for (Source source : placeholder.getSources()) {
      if (source.isNew() || source.isModified()) {
        return true;
      }
    }
    return false;
  }

  /*
   * Check if any changes has been made in keys
   */
  private boolean keysChanged(final DefaultPlaceholder placeholder) {
    if (placeholder.getRemovedKeys().size() > 0) {
      return true;
    }
    for (Key key : placeholder.getKeys()) {
      if (key.isNew() || key.isModified()) {
        return true;
      }
    }
    return false;
  }

  /*
   * Check if any changes has been made in keys
   */
  private boolean mappedtypesChanged(final DefaultPlaceholder placeholder) {
    for (MappedType mappedType : placeholder.getMappedTypes()) {
      if (mappedType.isNew() || mappedType.isModified()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get all busyhoursources of the busyhour from repository
   * 
   * @param whereBusyhour
   * @return
   * @throws DatabaseException
   */
  public Vector<Busyhoursource> getBusyhourSources(final Busyhour whereBusyhour) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhoursource bhs = new Busyhoursource(dwhrepRockFactory);
        bhs.setVersionid(whereBusyhour.getVersionid());
        bhs.setBhlevel(whereBusyhour.getBhlevel());
        bhs.setBhtype(whereBusyhour.getBhtype());
        bhs.setTargetversionid(whereBusyhour.getTargetversionid());
        bhs.setBhobject(whereBusyhour.getBhobject());
        final BusyhoursourceFactory bhsF = new BusyhoursourceFactory(dwhrepRockFactory, bhs, false);
        final Vector<Busyhoursource> busyhoursources = bhsF.get();
        Collections.sort(busyhoursources, BUSYHOURSOURCECOMPARATOR);
        return busyhoursources;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Busyhours");
      }
    }
    return null;
  }

  /**
   * Get all mappings of the busyhour from repository 
   * @param whereBusyhour
   * @return
   * @throws DatabaseException
   */
  public Vector<Busyhourmapping> getBusyhourmappings(final Busyhour whereBusyhour) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhourmapping bhm = new Busyhourmapping(dwhrepRockFactory);
        bhm.setVersionid(whereBusyhour.getVersionid());
        bhm.setBhlevel(whereBusyhour.getBhlevel());
        bhm.setBhtype(whereBusyhour.getBhtype());
        bhm.setTargetversionid(whereBusyhour.getTargetversionid());
        bhm.setBhobject(whereBusyhour.getBhobject());
        final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(dwhrepRockFactory, bhm, false);
        final Vector<Busyhourmapping> busyhourmapping = bhmF.get();
        Collections.sort(busyhourmapping, BUSYHOURMAPPINGCOMPARATOR);
        return busyhourmapping;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Busyhours");
      }
    }
    return null;
  }

  /**
   * Get rankkeys of the busyhour from repository
   * @param whereBusyhour
   * @return
   * @throws DatabaseException
   */
  public Vector<Busyhourrankkeys> getBusyhourrankkeys(final Busyhour whereBusyhour) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhourrankkeys bhrk = new Busyhourrankkeys(dwhrepRockFactory);
        bhrk.setVersionid(whereBusyhour.getVersionid());
        bhrk.setBhlevel(whereBusyhour.getBhlevel());
        bhrk.setBhtype(whereBusyhour.getBhtype());
        bhrk.setTargetversionid(whereBusyhour.getTargetversionid());
        bhrk.setBhobject(whereBusyhour.getBhobject());
        final BusyhourrankkeysFactory bhrkF = new BusyhourrankkeysFactory(dwhrepRockFactory, bhrk, true);
        final Vector<Busyhourrankkeys> busyhourrankkeys = bhrkF.get();
        Collections.sort(busyhourrankkeys, BUSYHOURRANKKEYCOMPARATOR);
        return busyhourrankkeys;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Busyhours");
      }
    }
    return null;
  }

  /**
   * Get versioning by versionid
   * @param versionid
   * @return
   * @throws DatabaseException
   */
  protected Versioning getVersioning(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Versioning v = new Versioning(dwhrepRockFactory);
        v.setVersionid(versionid);
        final VersioningFactory vF = new VersioningFactory(dwhrepRockFactory, v);
        if (vF.size() != 1) {
          throw new Exception("Can not find TP " + versionid);
        }
        final Versioning versioning = vF.getElementAt(0);
        return versioning;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Versioning");
      }
    }
    return null;
  }

  /**
   * Get measurementtypes by versionid
   * @param versionid
   * @return
   * @throws DatabaseException
   */
  protected Vector<Measurementtype> getMeasurementtypes(final String versionid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Measurementtype mt = new Measurementtype(dwhrepRockFactory);
        mt.setVersionid(versionid);
        final MeasurementtypeFactory mtF = new MeasurementtypeFactory(dwhrepRockFactory, mt);
        return mtF.get();
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Measurementtypes");
      }
    }
    return null;
  }
  
  /**
   * Get meta_collection_sets by name and version
   * @param setName
   * @param setVersion
   * @return
   * @throws DatabaseException
   */
  protected Meta_collection_sets getMeta_collection_sets(final String setName, final String setVersion) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Meta_collection_sets mwhere = new Meta_collection_sets(etlrepRockFactory);
        mwhere.setCollection_set_name(setName);
        mwhere.setVersion_number(setVersion);
        final Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(etlrepRockFactory, mwhere);
        if (mcsf.size() != 1) {
          throw new Exception("Can not find Meta_collection_sets " + setName + ":" + setVersion);
        }
        final Meta_collection_sets mcs = (Meta_collection_sets) mcsf.get().get(0);
        return mcs;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get Meta_collection_sets");
      }
    }
    return null;
  }

  /**
   * Gets keys of the Busyhoursupport measurement type
   * 
   * @param typeid
   * @return
   * @throws DatabaseException
   */
  public Vector<Measurementkey> getBusyhourSupportKeys(final String typeid) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Measurementkey whereMeasurementkey = new Measurementkey(dwhrepRockFactory);
        whereMeasurementkey.setTypeid(typeid);
        final MeasurementkeyFactory mkF = new MeasurementkeyFactory(dwhrepRockFactory, whereMeasurementkey);
        final Vector<Measurementkey> measurementkeys = mkF.get();
        Collections.sort(measurementkeys, MEASUREMENTKEYCOMPARATOR);
        return measurementkeys;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to get source keys");
      }
    }
    return null;
  }
  
  /**
   * Create Busyhoursources into repository
   * 
   * @param placeholder
   * @throws DatabaseException
   */
  private void createBusyhourSources(final DefaultPlaceholder placeholder) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        for (Source source : placeholder.getSources()) {
          final Busyhoursource bhs = new Busyhoursource(dwhrepRockFactory, true);
          bhs.setVersionid(placeholder.getVersionid());
          bhs.setBhlevel(placeholder.getBhlevel());
          bhs.setBhtype(placeholder.getBhtype());
          bhs.setTargetversionid(placeholder.getTargetversionid());
          bhs.setBhobject(placeholder.getBhobject());
          bhs.setTypename(source.getTypename());
          bhs.saveDB();
        }
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create Busyhour Sources");
      }
    }
  }

  /**
   * Create Busyhourkeys into repository
   * 
   * @param placeholder
   * @throws DatabaseException
   */
  private void createBusyhourKeys(final DefaultPlaceholder placeholder) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        Long ordernro = 0l;
        for (Key key : placeholder.getKeys()) {
          final Busyhourrankkeys bhs = new Busyhourrankkeys(dwhrepRockFactory, true);
          bhs.setVersionid(placeholder.getVersionid());
          bhs.setBhlevel(placeholder.getBhlevel());
          bhs.setBhtype(placeholder.getBhtype());
          bhs.setTargetversionid(placeholder.getTargetversionid());
          bhs.setBhobject(placeholder.getBhobject());
          bhs.setKeyname(key.getKeyName());
          String value = key.getKeyValue();
          if (key.getSource().trim().length() > 0) {
            value = key.getSource() + "." + key.getKeyValue();
          }
          bhs.setKeyvalue(value);
          bhs.setOrdernbr(ordernro);
          bhs.saveDB();
          ordernro++;
        }
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create Busyhour Keys");
      }
    }
  }

  /**
   * Create Busyhourmappedtypes into repository
   * 
   * @param placeholder
   * @throws DatabaseException
   */
  protected void createBusyhourMappedtypes(final DefaultPlaceholder placeholder) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        for (MappedType mappedType : placeholder.getMappedTypes()) {
          final Busyhourmapping bhs = new Busyhourmapping(dwhrepRockFactory, true);
          bhs.setVersionid(placeholder.getVersionid());
          bhs.setBhlevel(placeholder.getBhlevel());
          bhs.setBhtype(placeholder.getBhtype());
          bhs.setTargetversionid(placeholder.getTargetversionid());
          bhs.setBhobject(placeholder.getBhobject());
          final String typeId = placeholder.getTargetversionid() + ":" + mappedType.getBhTargettype();
          bhs.setTypeid(typeId);
          bhs.setBhtargettype(mappedType.getBhTargettype());
          bhs.setBhtargetlevel(mappedType.getBhTargetlevel());
          bhs.setEnable((mappedType.getEnabled() ? 1 : 0));
          bhs.saveDB();
        }
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create Busyhour MappedTypes");
      }
    }
  }

  /**
   * Create ETL sets for busyhour
   * @param busyhour
   * @throws DatabaseException
   */
  private void createETLSets(final Busyhour busyhour) throws DatabaseException {
    if (dwhrepRockFactory != null && etlrepRockFactory != null) {
      try {
        final Vector<String> skiplist = new Vector<String>();
        final Vector<String> meastypes = new Vector<String>();
        meastypes.add(busyhour.getBhlevel());
        final Vector<Busyhoursource> busyhourSources = getBusyhourSources(busyhour);
        for (Busyhoursource busyhourSource : busyhourSources) {
          meastypes.add(busyhourSource.getTypename().substring(0, busyhourSource.getTypename().lastIndexOf("_")));
        }
        final Vector<Measurementtype> measurementtypes = getMeasurementtypes(busyhour.getVersionid());
        for (Measurementtype m : measurementtypes) {
          if (!meastypes.contains(m.getTypename())) {
            skiplist.add(m.getTypename());
          }
        }
        final Versioning v = getVersioning(busyhour.getVersionid());
        final String setName = v.getTechpack_name();
        final String setVersion = busyhour.getVersionid().substring(busyhour.getVersionid().lastIndexOf(":") + 1);
        final Meta_collection_sets mcs = getMeta_collection_sets(setName, setVersion);        
        final CreateAggregatorSet cas = new CreateAggregatorSet("5.2", setName, setVersion, busyhour.getVersionid(),
            dwhrepRockFactory, etlrepRockFactory, (int) mcs.getCollection_set_id().longValue(), true);
        // remove existing ones
        final List<String> skipAggTypes = new ArrayList<String> ();
        // Always skip day and count aggregations. Never skip DAYBH and RANKBH,
        // those are the ones bhconfig needs to recreate.
        skipAggTypes.add(com.ericsson.eniq.common.Constants.DAYLEVEL);
        skipAggTypes.add(com.ericsson.eniq.common.Constants.COUNTLEVEL);
        cas.removeSets(skiplist, skipAggTypes);
        cas.create(false, skiplist, skipAggTypes);
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create ETL Sets");
      }
    }
  }
  
  /**
   * Create busyhour mappings in dwh database
   * @param versionid
   * @throws DatabaseException
   */
  private void createDWHBusyhourMappings(final DefaultTechPack techPack) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final StringBuffer sql = new StringBuffer();
        final Busyhourmapping bhm = new Busyhourmapping(dwhrepRockFactory);
        bhm.setVersionid(techPack.getVersionId());
        final BusyhourmappingFactory bhmF = new BusyhourmappingFactory(dwhrepRockFactory, bhm);
        for (Busyhourmapping b : bhmF.get()) {
          // update
          sql.append("UPDATE LOG_BusyhourMapping SET ENABLE = " + b.getEnable() + " WHERE BHTARGETTYPE = '");
          sql.append(b.getBhtargettype()).append("' and BHTARGETLEVEL = '");
          sql.append(b.getBhtargetlevel()).append("'; \n");
        }
        if (sql.length() > 0) {
          final Connection rockConn = dwhRockFactory.getConnection();
          final Statement stmt = rockConn.createStatement();
          try {
            stmt.executeUpdate(sql.toString());
          } finally {
            stmt.close();
          }
        }
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create Busyhour Mappings");
      }
    }
  }

  private void createDWHAggregationRules(final DefaultPlaceholder placeholder) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final StringBuffer sql = new StringBuffer();
        // get meastypes that are interesting to us
        final Vector<String> meastypes = new Vector<String>();
        meastypes.add(placeholder.getBhlevel());
        Aggregationrule aggr = new Aggregationrule(dwhrepRockFactory);
        aggr.setVersionid(placeholder.getVersionid());
        AggregationruleFactory aggrF = new AggregationruleFactory(dwhrepRockFactory, aggr);
        for (Aggregationrule a : aggrF.get()) {
          if (meastypes.contains(a.getTarget_type())) {
            // remove	    			  	    			  
            sql.append("IF (SELECT count(*) FROM LOG_AggregationRules WHERE aggregation='");
            sql.append(a.getAggregation()).append("'");
            sql.append(") > 0 THEN \n");
            sql.append(" DELETE FROM LOG_AggregationRules WHERE aggregation='");
            sql.append(a.getAggregation()).append("' \n");
            sql.append("END IF; \n");
          }
        }
        aggr = new Aggregationrule(dwhrepRockFactory);
        aggr.setVersionid(placeholder.getVersionid());
        aggr.setEnable(1);
        aggrF = new AggregationruleFactory(dwhrepRockFactory, aggr);
        for (Aggregationrule a : aggrF.get()) {
          if (meastypes.contains(a.getTarget_type())) {
            // create new
            sql
                .append(" INSERT INTO LOG_AggregationRules (AGGREGATION, RULEID, TARGET_TYPE, TARGET_LEVEL, TARGET_TABLE, SOURCE_TYPE, SOURCE_LEVEL, SOURCE_TABLE, RULETYPE, AGGREGATIONSCOPE, STATUS, MODIFIED, BHTYPE) VALUES('");
            sql.append(a.getAggregation()).append("','");
            sql.append(a.getRuleid()).append("','");
            sql.append(a.getTarget_type()).append("','");
            sql.append(a.getTarget_level()).append("','");
            sql.append(a.getTarget_table()).append("','");
            sql.append(a.getSource_type()).append("','");
            sql.append(a.getSource_level()).append("','");
            sql.append(a.getSource_table()).append("','");
            sql.append(a.getRuletype()).append("','");
            sql.append(a.getAggregationscope()).append("',null,null,");
            if (a.getBhtype() == null) {
              sql.append("null");
            } else {
              sql.append("'").append(a.getBhtype()).append("'");
            }
            sql.append("); \n");
          }
        }
        if (sql.length() > 0) {
          final Connection rockConn = dwhRockFactory.getConnection();
          final Statement stmt = rockConn.createStatement();
          try {
            stmt.executeUpdate(sql.toString());
          } finally {
            stmt.close();
          }
        }
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to create ETL Sets");
      }
    }
  }

  /**
   * Remove busyhoursources from repository
   * 
   * @param whereBusyhour
   *          Busyhour object which sources will be removed
   * @throws DatabaseException
   */
  private void deleteBusyhourSources(final Busyhour whereBusyhour) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhoursource delbhs = new Busyhoursource(dwhrepRockFactory);
        delbhs.setVersionid(whereBusyhour.getVersionid());
        delbhs.setBhlevel(whereBusyhour.getBhlevel());
        delbhs.setBhtype(whereBusyhour.getBhtype());
        delbhs.setTargetversionid(whereBusyhour.getTargetversionid());
        delbhs.setBhobject(whereBusyhour.getBhobject());
        delbhs.deleteDB(delbhs);
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to delete Busyhour Sources");
      }
    }
  }

  /**
   * Remove Busyhourrankkeys from repository
   * 
   * @param whereBusyhour
   *          Busyhour object which sources will be removed
   * @throws DatabaseException
   */
  private void deleteBusyhourKeys(final Busyhour whereBusyhour) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhourrankkeys delbhs = new Busyhourrankkeys(dwhrepRockFactory);
        delbhs.setVersionid(whereBusyhour.getVersionid());
        delbhs.setBhlevel(whereBusyhour.getBhlevel());
        delbhs.setBhtype(whereBusyhour.getBhtype());
        delbhs.setTargetversionid(whereBusyhour.getTargetversionid());
        delbhs.setBhobject(whereBusyhour.getBhobject());
        delbhs.deleteDB(delbhs);
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to delete Busyhour Keys");
      }
    }
  }

  /**
   * Remove Busyhourmapping from repository
   * 
   * @param whereBusyhour
   *          Busyhour object which sources will be removed
   * @throws DatabaseException
   */
  protected void deleteBusyhourMappedTypes(final Busyhour whereBusyhour) throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        final Busyhourmapping delbhs = new Busyhourmapping(dwhrepRockFactory);
        delbhs.setVersionid(whereBusyhour.getVersionid());
        delbhs.setBhlevel(whereBusyhour.getBhlevel());
        delbhs.setBhtype(whereBusyhour.getBhtype());
        delbhs.setTargetversionid(whereBusyhour.getTargetversionid());
        delbhs.setBhobject(whereBusyhour.getBhobject());
        delbhs.deleteDB(delbhs);
      } catch (Exception e) {
        log.severe("Rock Error:" + e.getMessage());
        throw new DatabaseException("Failed to delete Busyhour Mappings");
      }
    }
  }

  /**
   * Activate changed placeholders for all busyhours of the TechPack
   * 
   * @param versionid
   * @throws DatabaseException
   */
  public void performBusyhourActivation(final DefaultTechPack techpack, final DWHStorageTimeAction sta)
      throws DatabaseException {
    if (dwhrepRockFactory != null) {
      try {
        dwhrepRockFactory.getConnection().setAutoCommit(false);
        final Busyhour where = new Busyhour(dwhrepRockFactory);
        where.setVersionid(techpack.getVersionId());
        where.setPlaceholdertype(Constants.CUSTOM); // only custom placeholders can be recreated from GUI
        where.setReactivateviews(1); // only redo modified placeholders
        final BusyhourFactory bhF = new BusyhourFactory(dwhrepRockFactory, where);
        final Vector<Busyhour> busyhours = bhF.get();
        log.fine("Reactivating " + busyhours.size() + " placeholders");
        int viewsRecreated = 0;
        for (Busyhour busyhour : busyhours) {
          try {
            if (busyhour.getBhcriteria() == null || busyhour.getBhcriteria().length() == 0) {
              log.fine("Dropping bhrank views");
              sta.performDropBhRankViews(busyhour);
            } else {
              log.fine("Create bhrank views");
              sta.performCreateBhRankViews(busyhour);
            }
            viewsRecreated++;
            busyhour.setReactivateviews(0);
            busyhour.saveToDB();
            log.fine("placeholder #" + viewsRecreated + " saved");
            // create ETL sets
            log.info("Creating ETL sets for busyhour");
            createETLSets(busyhour);
          } catch (DWHManagerException e) {
            log.severe("DWHManager Error:" + e.getMessage());
            throw e;
          }
        }
        // update log_busyhourMappings
        log.info("Creating DWH busyhour mappings");
        createDWHBusyhourMappings(techpack);
        // Update the busy hour counter data.
        final Dwhtechpacks dwhtechpack = new Dwhtechpacks(dwhrepRockFactory);
        dwhtechpack.setVersionid(techpack.getVersionId());
        dwhtechpack.setTechpack_name(techpack.getName());
        sta.updateBHCounters(dwhtechpack);
        sta.updateBHCountersForCustomTechpack(dwhtechpack);
        log.info("Saving " + busyhours.size() + " techpack activations");
        final Tpactivation whereTpactivation = new Tpactivation(dwhrepRockFactory);
        whereTpactivation.setVersionid(techpack.getVersionId());
        final TpactivationFactory taF = new TpactivationFactory(dwhrepRockFactory, whereTpactivation, true);
        final Vector<Tpactivation> tpactivations = taF.get();
        Tpactivation tpactivation = null;
        if (!tpactivations.isEmpty()) {
          tpactivation = tpactivations.firstElement();
          if (tpactivation.getModified().intValue() == 1) {
            tpactivation.setModified(0);
            tpactivation.saveToDB();
          }
        }
        dwhrepRockFactory.getConnection().commit();
        log.info(viewsRecreated + " views modified");
      } catch (RuntimeException e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Error:" + e.getMessage());
        throw e;
      } catch (Exception e) {
        try {
          dwhrepRockFactory.getConnection().rollback();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        log.severe("Error:" + e.getMessage());
        throw new DatabaseException("Failed to recreate busyhour views");
      }
    }
  }

}
