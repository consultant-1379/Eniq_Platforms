package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
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

import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.BusyhourrankkeysFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.BusyhoursourceFactory;
import com.distocraft.dc5000.repository.dwhrep.Externalstatement;
import com.distocraft.dc5000.repository.dwhrep.ExternalstatementFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
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
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeData;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementtypeExt;

/**
 * The data model for the Busy Hour calculation objects storing the condition
 * and criteria used when aggregating and calculating the actual busy hour data.
 * 
 * @author eheijun
 * @author eheitur
 * @author ejarsav
 * 
 */
public class BusyhourHandlingDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(BusyhourHandlingDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private List<BusyHourData> busyHourData;

  private TechPackDataModel techPackDataModel;

  private DataModelController dataModelController;

  private List<BusyHourSourceTables> busyHourSources;

  private String[] rankingTechpacks;

  public boolean dataUpdated = false;

  public BusyhourHandlingDataModel(RockFactory rockFactory, DataModelController dataModelController) {
    this.rockFactory = rockFactory;
    this.dataModelController = dataModelController;
  }

  /**
   * Create the busy hour mappings for the given object busy hour support.
   * 
   * @param support
   */
  public void createMappings(final List<Measurementobjbhsupport> supports) {

    for (BusyHourData bhData : busyHourData) {
      Busyhour bh = bhData.getBusyhour();

      // Get existing mappings
      List<Busyhourmapping> existingMappings = bhData.getBusyhourmapping();

      // Loop all supports and add mapping for each one with objBhSupport value
      // matching the bhObject.
      for (Measurementobjbhsupport sup : supports) {

        if (bh.getBhobject().equalsIgnoreCase(sup.getObjbhsupport())) {

          // Skip the busy hour support itself
          if (sup.getTypeid().equalsIgnoreCase(bh.getVersionid() + Constants.TYPESEPARATOR + bh.getBhlevel())) {
            continue;
          }

          // Create a new mapping.
          // NOTE: New mappings will be enabled by default.
          final Busyhourmapping newBHM = new Busyhourmapping(rockFactory);
          newBHM.setVersionid(bh.getVersionid());
          newBHM.setTargetversionid(bh.getTargetversionid());
          newBHM.setBhlevel(bh.getBhlevel());
          newBHM.setBhobject(sup.getObjbhsupport());
          newBHM.setBhtype(bh.getBhtype());
          newBHM.setTypeid(sup.getTypeid());
          newBHM.setBhtargettype(sup.getTypeid().substring(sup.getTypeid().lastIndexOf(":") + 1));
          newBHM.setBhtargetlevel(Utils.replaceNull(bh.getBhobject()).trim() + Constants.TYPENAMESEPARATOR
              + Utils.replaceNull(bh.getBhtype()));
          newBHM.setEnable(1);

          // Check if the new mapping already exists (not caring about enabled).
          boolean match = false;
          for (Busyhourmapping oldBHM : existingMappings) {
            if (oldBHM.getVersionid().equalsIgnoreCase(newBHM.getVersionid())
                && oldBHM.getTargetversionid().equalsIgnoreCase(newBHM.getTargetversionid())
                && oldBHM.getBhlevel().equalsIgnoreCase(newBHM.getBhlevel())
                && oldBHM.getBhobject().equalsIgnoreCase(sup.getObjbhsupport())
                && oldBHM.getBhtype().equalsIgnoreCase(newBHM.getBhtype())
                && oldBHM.getTypeid().equalsIgnoreCase(newBHM.getTypeid())
                && oldBHM.getBhtargettype().equalsIgnoreCase(newBHM.getBhtargettype())
                && oldBHM.getBhtargetlevel().equalsIgnoreCase(newBHM.getBhtargetlevel())) {
              // Match found, no need to create it.
              match = true;
              break;
            }
          }

          // If no match, then create it.
          if (!match) {
            try {
              // System.out.println("createMappings(): create: " +
              // newBHM.toSQLInsert());
              newBHM.insertDB();
            } catch (SQLException e) {
              e.printStackTrace();
            } catch (RockException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  /**
   * Deletes the aggregations for a busy hour. Also the aggregator set is
   * deleted for all the aggregations.
   * 
   * @param bh
   *          the busy hour
   * @param deleteAggregatorSets
   *          if true, then the aggregator sets for the aggregations are also
   *          deleted.
   * @throws SQLException
   * @throws RockException
   */
  public void deleteRankBhAggregations(Busyhour bh, boolean deleteAggregatorSets) throws SQLException, RockException {

    // Get the target techpack (without the version number).
    // For example: 'DC_X_RADIO'
    String targetTechpack = Utils.replaceNull(bh.getTargetversionid()
        .substring(0, bh.getTargetversionid().indexOf(":")));

    // Get the ranking value:
    // For example: 'Cell_TCHTRAF'
    String ranking = Utils.replaceNull(bh.getBhobject()).trim() + Constants.TYPENAMESEPARATOR
        + Utils.replaceNull(bh.getBhtype());

    // Check if this is an object or element busy hour and delete the
    // aggregations and rules accordingly.
    if (Utils.replaceNull(bh.getBhelement()).intValue() == 0) {
      // This is an object busy hour.

      // Get ranking table value from the bhlevel.
      // For example: DC_X_RADIO_BSS_CELLBH
      String rankingTable = bh.getBhlevel();

      deleteAggregation(rankingTable + "_RANKBH_" + ranking, deleteAggregatorSets);
      deleteAggregation(rankingTable + "_WEEKRANKBH_" + ranking, deleteAggregatorSets);
      deleteAggregation(rankingTable + "_MONTHRANKBH_" + ranking, deleteAggregatorSets);

      // There might be also incorrectly named aggregations from in the DB.
      // These must be deleted as well.
      // NOTE: For some tehcpacks the rankingTable statement below gives the
      // same result as above. In this case, there are no invalid ones to
      // remove.
      rankingTable = targetTechpack + Constants.TYPENAMESEPARATOR
          + Utils.replaceNull(bh.getBhobject()).trim().toUpperCase() + "BH";
      deleteAggregation(rankingTable + "_RANKBH_" + ranking, deleteAggregatorSets);
      deleteAggregation(rankingTable + "_WEEKRANKBH_" + ranking, deleteAggregatorSets);
      deleteAggregation(rankingTable + "_MONTHRANKBH_" + ranking, deleteAggregatorSets);

    } else {
      // This is an element busy hour.

      // Get the ranking table for element busy hours.
      String rankingTable = targetTechpack + Constants.TYPENAMESEPARATOR + "ELEMBH";

      // Delete the aggregations.
      deleteAggregation(rankingTable + "_RANKBH_" + ranking, deleteAggregatorSets);
      deleteAggregation(rankingTable + "_WEEKRANKBH_" + ranking, deleteAggregatorSets);
      deleteAggregation(rankingTable + "_MONTHRANKBH_" + ranking, deleteAggregatorSets);

    }
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
    logger.info("BusyHourDataModel starting refresh from DB");
    busyHourData = new ArrayList<BusyHourData>();
    if (currentVersioning != null) {
        final List<Busyhour> bhs = getBusyhours(currentVersioning.getVersionid());
      for (Busyhour bh : bhs) {
        final List<Busyhourrankkeys> rks = getRankkeys(bh);
        final List<Busyhoursource> srcs = getSources(bh);
        final List<Busyhourmapping> maps = getMappings(bh);
        final BusyHourData data = new BusyHourData(currentVersioning, bh, rks, srcs, maps, dataModelController);
        busyHourData.add(data);
      }

      getRankingTechpacks();
      // dataUpdated = false;
    }
    logger.info("BusyHourDataModel refreshed from DB");
  }

  public void refreshAllBusyHourSourcesFromDb() {
    busyHourSources = getAllBusyHourSources_db();
  }

  private final static Comparator<Busyhour> BUSYHOURCOMPARATOR = new Comparator<Busyhour>() {

    public int compare(final Busyhour d1, final Busyhour d2) {

      // The comparison between two busy hours based on the bhLevel, and
      // if they match, then on the bhType.
      final String s1 = Utils.replaceNull(d1.getBhlevel());
      final String s2 = Utils.replaceNull(d2.getBhlevel());
      if (s1.equalsIgnoreCase(s2)) {
        final String s3 = Utils.replaceNull(d1.getBhtype());
        final String s4 = Utils.replaceNull(d2.getBhtype());

        Integer insS3 = Utils.extractDigitsFromEndOfString(s3);
        Integer insS4 = Utils.extractDigitsFromEndOfString(s4);
        return insS3.compareTo(insS4);        
      }
      return s1.compareTo(s2);
    }
  };

  private final static Comparator<Busyhourmapping> BUSYHOURMAPPINGCOMPARATOR = new Comparator<Busyhourmapping>() {

    public int compare(final Busyhourmapping d1, final Busyhourmapping d2) {

      final String s1 = Utils.replaceNull(d1.getTypeid());
      final String s2 = Utils.replaceNull(d2.getTypeid());
      return s1.compareTo(s2);
    }
  };

  private final static Comparator<Busyhoursource> BUSYHOURSOURCECOMPARATOR = new Comparator<Busyhoursource>() {

    public int compare(final Busyhoursource d1, final Busyhoursource d2) {

      final String s1 = Utils.replaceNull(d1.getTypename());
      final String s2 = Utils.replaceNull(d2.getTypename());
      return s1.compareTo(s2);
    }
  };

  private final static Comparator<Busyhourrankkeys> BUSYHOURRANKKEYCOMPARATOR = new Comparator<Busyhourrankkeys>() {

    public int compare(final Busyhourrankkeys d1, final Busyhourrankkeys d2) {

      final Long l1 = Utils.replaceNull(d1.getOrdernbr());
      final Long l2 = Utils.replaceNull(d2.getOrdernbr());
      return l1.compareTo(l2);
    }
  };

  private List<Busyhourmapping> getMappings(final Busyhour busyhour) {
    List<Busyhourmapping> results = new Vector<Busyhourmapping>();

    final Busyhourmapping whereBusyhourmapping = new Busyhourmapping(rockFactory);
    whereBusyhourmapping.setVersionid(busyhour.getVersionid());
    whereBusyhourmapping.setTargetversionid(busyhour.getTargetversionid());
    whereBusyhourmapping.setBhlevel(busyhour.getBhlevel());
    whereBusyhourmapping.setBhobject(busyhour.getBhobject());
    whereBusyhourmapping.setBhtype(busyhour.getBhtype());
    try {
      final BusyhourmappingFactory busyhourmappingFactory = new BusyhourmappingFactory(rockFactory,
          whereBusyhourmapping, true);
      results = busyhourmappingFactory.get();
      Collections.sort(results, BUSYHOURMAPPINGCOMPARATOR);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in BusyhourMappings", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in BusyhourMappings", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in BusyhourMappings", e);
    }
    return results;
  }

  private List<Busyhoursource> getSources(final Busyhour busyhour) {
    List<Busyhoursource> results = new Vector<Busyhoursource>();

    final Busyhoursource whereBusyhoursource = new Busyhoursource(rockFactory);
    whereBusyhoursource.setVersionid(busyhour.getVersionid());
    whereBusyhoursource.setTargetversionid(busyhour.getTargetversionid());
    whereBusyhoursource.setBhlevel(busyhour.getBhlevel());
    whereBusyhoursource.setBhobject(busyhour.getBhobject());
    whereBusyhoursource.setBhtype(busyhour.getBhtype());
    try {
      final BusyhoursourceFactory busyhoursourceFactory = new BusyhoursourceFactory(rockFactory, whereBusyhoursource,
          true);
      results = busyhoursourceFactory.get();
      Collections.sort(results, BUSYHOURSOURCECOMPARATOR);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getSources", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getSources", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getSources", e);
    }
    return results;
  }

  private List<Busyhourrankkeys> getRankkeys(final Busyhour busyhour) {
    List<Busyhourrankkeys> results = new Vector<Busyhourrankkeys>();

    final Busyhourrankkeys whereBusyhourrankkeys = new Busyhourrankkeys(rockFactory);
    whereBusyhourrankkeys.setVersionid(busyhour.getVersionid());
    whereBusyhourrankkeys.setTargetversionid(busyhour.getTargetversionid());
    whereBusyhourrankkeys.setBhlevel(busyhour.getBhlevel());
    whereBusyhourrankkeys.setBhobject(busyhour.getBhobject());
    whereBusyhourrankkeys.setBhtype(busyhour.getBhtype());
    try {
      final BusyhourrankkeysFactory busyhourrankkeysFactory = new BusyhourrankkeysFactory(rockFactory,
          whereBusyhourrankkeys, true);
      results = busyhourrankkeysFactory.get();
      Collections.sort(results, BUSYHOURRANKKEYCOMPARATOR);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getRankkeys", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getRankkeys", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getRankkeys", e);
    }
    return results;
  }

  /**
   * Returns a list of busy hours by versionId
   * 
   * @return results a list of busy hours
   */
  protected List<Busyhour> getBusyhours(final String versionId) {
    List<Busyhour> results = new Vector<Busyhour>();
    final Busyhour whereBusyhour = new Busyhour(rockFactory);
    whereBusyhour.setVersionid(versionId);
    try {
      final BusyhourFactory busyhourFactory = new BusyhourFactory(rockFactory, whereBusyhour, true);
      results = busyhourFactory.get();
      Collections.sort(results, BUSYHOURCOMPARATOR);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getBusyhours", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getBusyhours", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getBusyhours", e);
    }

    return results;
  }

  private final static Comparator<Measurementtype> MEASUREMENTTYPECOMPARATOR = new Comparator<Measurementtype>() {

    public int compare(Measurementtype o1, Measurementtype o2) {
      final String s1 = Utils.replaceNull(o1.getVersionid());
      final String s2 = Utils.replaceNull(o2.getVersionid());
      int res1 = s1.compareTo(s2);
      if (res1 != 0) {
        return res1;
      }
      final String s3 = Utils.replaceNull(o1.getTypename());
      final String s4 = Utils.replaceNull(o2.getTypename());
      return s3.compareTo(s4);
    }

  };

  // private final static Comparator<Measurementobjbhsupport>
  // MEASUREMENTOBJBHSUPPORTCOMPARATOR = new
  // Comparator<Measurementobjbhsupport>() {
  //
  // public int compare(final Measurementobjbhsupport d1, final
  // Measurementobjbhsupport d2) {
  //
  // final String s1 = Utils.replaceNull(d1.getTypeid());
  // final String s2 = Utils.replaceNull(d2.getTypeid());
  // int res1 = s1.compareTo(s2);
  // if (res1 != 0) {
  // return res1;
  // }
  // final String s3 = Utils.replaceNull(d1.getObjbhsupport());
  // final String s4 = Utils.replaceNull(d2.getObjbhsupport());
  // return s3.compareTo(s4);
  // }
  // };
  //
  // private static final Comparator<Dwhtype> DWHTYPECOMPARATOR = new
  // Comparator<Dwhtype>() {
  //
  // public int compare(final Dwhtype d1, final Dwhtype d2) {
  //
  // final String s1 = Utils.replaceNull(d1.getTechpack_name());
  // final String s2 = Utils.replaceNull(d2.getTechpack_name());
  // int res1 = s1.compareTo(s2);
  // if (res1 != 0) {
  // return res1;
  // }
  // final String s3 = Utils.replaceNull(d1.getBasetablename());
  // final String s4 = Utils.replaceNull(d2.getBasetablename());
  // return s3.compareTo(s4);
  // }
  // };

  private static final Comparator<Versioning> VERSIONINGCOMPARATOR = new Comparator<Versioning>() {

    public int compare(final Versioning d1, final Versioning d2) {

      final String s1 = Utils.replaceNull(d1.getTechpack_name());
      final String s2 = Utils.replaceNull(d2.getTechpack_name());
      int res1 = s1.compareTo(s2);
      if (res1 != 0) {
        return res1;
      }
      final String s3 = Utils.replaceNull(d1.getTechpack_name());
      final String s4 = Utils.replaceNull(d2.getTechpack_name());
      return s3.compareTo(s4);
    }
  };

  /**
   * Sorter for Tpactivation
   */
  private final static Comparator<Tpactivation> TPACTIVATIONCOMPARATOR = new Comparator<Tpactivation>() {

    public int compare(final Tpactivation d1, final Tpactivation d2) {
      return d1.getVersionid().compareTo(d2.getVersionid());
    }
  };

  public List<BusyHourRankTables> getAllBusyHourRankTables() {

    List<BusyHourRankTables> result = new Vector<BusyHourRankTables>();

    try {

      final Measurementtype whereMeasurementType = new Measurementtype(rockFactory);
      // Measurementobjbhsupport whereMeasurementobjbhsupport = new
      // Measurementobjbhsupport(rockFactory);
      whereMeasurementType.setRankingtable(1);
      final MeasurementtypeFactory measurementtypeFactory = new MeasurementtypeFactory(rockFactory,
          whereMeasurementType, true);
      final Vector<Measurementtype> measurementTypes = measurementtypeFactory.get();
      Collections.sort(measurementTypes, MEASUREMENTTYPECOMPARATOR);
      String exversionid = null;
      Vector<String> ranktables = new Vector<String>();
      for (Measurementtype item : measurementTypes) {
        if ((exversionid != null) && !item.getVersionid().equals(exversionid)) {
          result.add(new BusyHourRankTables(exversionid, ranktables));
          ranktables = new Vector<String>();
        }

        ranktables.add(item.getTypename());

        /*
         * if (item.getElementbhsupport().equals(1)) {
         * ranktables.add(item.getTypename() + "_" + "Element"); }
         * whereMeasurementobjbhsupport.setTypeid(item.getTypeid());
         * MeasurementobjbhsupportFactory measurementobjbhsupportFactory = new
         * MeasurementobjbhsupportFactory(rockFactory,
         * whereMeasurementobjbhsupport, true); Vector<Measurementobjbhsupport>
         * temp = measurementobjbhsupportFactory.get(); Collections.sort(temp,
         * MEASUREMENTOBJBHSUPPORTCOMPARATOR); for
         * (Iterator<Measurementobjbhsupport> iter2 = temp.iterator();
         * iter2.hasNext();) { Measurementobjbhsupport item2 = iter2.next();
         * ranktables.add(item.getTypename() + "_" + item2.getObjbhsupport()); }
         */
        exversionid = item.getVersionid();
      }
      if (exversionid != null) {
        if (ranktables != null) {
          Collections.sort(ranktables);
        }
        result.add(new BusyHourRankTables(exversionid, ranktables));
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return result;

  }

  public List<BusyHourSourceTables> getAllBusyHourSources() {
    return busyHourSources;
  }

  public List<BusyHourSourceTables> getAllBusyHourSources_db() {
    logger.finest("BusyHourDataModel get All Busy Hour Sources from DB started");
    List<BusyHourSourceTables> result = new Vector<BusyHourSourceTables>();

    try {

      // TR: HL78530 Choosing source table, IDE should only show active Tech Packs 
      final Tpactivation a = new Tpactivation(rockFactory);
      a.setStatus("ACTIVE");
      final TpactivationFactory aF = new TpactivationFactory(rockFactory, a, false);
      final Vector<Tpactivation> tpactivations = aF.get();
      Collections.sort(tpactivations, TPACTIVATIONCOMPARATOR);

      for (Tpactivation activation : tpactivations) {

        Vector<String> basetables = new Vector<String>();
        
        //TR: HL76709
        //Add in the DIM tables from the REFERENCETABLE:
        Referencetable referenceTable = new Referencetable(rockFactory);
        referenceTable.setVersionid(activation.getVersionid());
        
        ReferencetableFactory referenceTableFactory = new ReferencetableFactory(rockFactory, referenceTable);
        Vector<Referencetable> tables = referenceTableFactory.get();
        for (Referencetable refT:tables){
          //Don't put in tables that have a "null" name or have tableType = VIEW.
          if(refT.getObjectname() != null && !"VIEW".equals(refT.getTable_type())){
            basetables.add(refT.getObjectname());
          }
        }
        //END TR: HL76709

        Measurementtype mt = new Measurementtype(rockFactory);
        mt.setVersionid(activation.getVersionid());
        MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);

        Vector<Measurementtype> mtypes = mtF.get();
        for (Measurementtype mts : mtypes) {

          Measurementtable mta = new Measurementtable(rockFactory);
          mta.setTypeid(mts.getTypeid());

          MeasurementtableFactory mtaF = new MeasurementtableFactory(rockFactory, mta);
          Vector<Measurementtable> mtab = mtaF.get();
          for (Measurementtable mtable : mtab) {
            if (mtable.getTablelevel().equalsIgnoreCase("RAW") || mtable.getTablelevel().equalsIgnoreCase("COUNT")) {
              if (mtable.getBasetablename() != null) {
                basetables.add(mtable.getBasetablename());
              }
            }
          }
        }

        if (basetables != null) {
          Collections.sort(basetables);
        }

        result.add(new BusyHourSourceTables(activation.getVersionid(), basetables));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
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
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.datamodel.DataModel#save()
   */
  public void save() throws Exception {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.techpacksdk.datamodel.DataModel#validateDel(ssc.rockfactory
   * .RockDBObject)
   */
  public boolean validateDel(final RockDBObject obj) {
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
    return false;
  }

  public boolean updated(final DataModel dataModel) {
    if (dataModel instanceof TechPackDataModel) {
      logger.log(Level.FINE, "Updating BusyHourDataModel due to changes in the TechPackDataModel.");
      techPackDataModel = (TechPackDataModel) dataModel;
      if (techPackDataModel.getVersioning() != null) {

        // Set the current versioning object.
        this.setCurrentVersioning(techPackDataModel.getVersioning());

        // Refresh busy hour data model from the DB.
        refresh();

        // Refresh all possible busy hour source tables from the DB.
        this.refreshAllBusyHourSourcesFromDb();
      }
      return true;
    } else if (dataModel instanceof MeasurementTypeDataModel) {
      logger.log(Level.FINE, "Updating BusyHourDataModel due to changes in the MeasurementTypeModel.");

      // Mark data as updated so that the busy hour tree will be refreshed the
      // next time it is shown.
      dataUpdated = true;

      // Refresh all possible busy hour source tables from the DB.
      this.refreshAllBusyHourSourcesFromDb();

      // Refresh busy hour data model from the DB.
      refresh();

      // Check all the busy hour supports (i.e. ranking table measurement types)
      // and create the missing busy hour place holders or remove the extra
      // place holders.
      //
      // Collect all ranking tables to a list.
      List<MeasurementtypeExt> rankingTableList = new ArrayList<MeasurementtypeExt>();
      for (MeasurementTypeData mtData : ((MeasurementTypeDataModel) dataModel).getMeasurements()) {
        if (Utils.replaceNull(mtData.getMeasurementtypeExt().getRankingtable()).equals(1)) {
          rankingTableList.add(mtData.getMeasurementtypeExt());
        }
      }

      // Add/remove the busy hour place holders.
      updateBusyHourPlaceHolders(rankingTableList);

      // Refresh busy hour data model from the DB.Needed because otherwise the
      // newly created place holders are not in the model yet.
      refresh();

      // Iterate through busy hour data and regenerate the busy hour rank keys,
      // because the keys might have changed when measurement types were
      // modified.
      logger.log(Level.FINE, "Regenerating busy hour rank keys.");
      for (BusyHourData bhData : busyHourData) {

        try {

          bhData.removeBusyHourRankKeysFromDB();
          bhData.generateBusyHourRankKeys(true);
          bhData.save();
          bhData.removeUnusedBHs();

          dataUpdated = true;

        } catch (Exception e) {
          logger.warning("Error while changing BysyHour Rank Keys " + e);
        }
      }

      // Collect all the measurement object busy hour supports for this techpack
      // from the measurement type model. This is needed both in creating the
      // aggregations and the busy hour mappings.

      // Delete and regenerate all aggregations for the busy hours. This is due
      // to the fact that when a rank table measurement type has been changed,
      // all the aggregations and rules have been removed. Since there is no
      // information available which measurement types have changed, all the
      // busy hour aggregations and rules have to be regenerated.
      logger.log(Level.FINE, "Regenerating busy hour RANKBH aggregations.");
      for (BusyHourData bhData : busyHourData) {

        try {
          deleteRankBhAggregations(bhData.getBusyhour(), false);
          StorageTimeAction
              .createRankbhAggregationsForBusyhour(bhData.getBusyhour(), bhData.getBusyhourSource(), currentVersioning.getVersionid(), rockFactory);
        } catch (Exception e) {
          logger.warning("Error while regenerating aggregations and aggregation rules " + e);
        }
      }

      // Create the busy hour mappings.
      logger.log(Level.FINE, "Regenerating busy hour mappings.");
      List<Measurementobjbhsupport> supports = new ArrayList<Measurementobjbhsupport>();
      for (MeasurementTypeData mtData : ((MeasurementTypeDataModel) dataModel).getMeasurements()) {
        for (Object obj : mtData.getMeasurementtypeExt().getObjBHSupport()) {
          if (obj instanceof Measurementobjbhsupport) {
            Measurementobjbhsupport support = (Measurementobjbhsupport) obj;
            supports.add(support);
          }
        }
      }
      createMappings(supports);

      // TODO: Is this refresh really needed???
      refresh();

      return true;
    }
    return false;
  }

  public List<BusyHourData> getBusyHourData() {
    return busyHourData;
  }

  public List<BusyHourData> getBusyHourData(String tid) {
    List<BusyHourData> result = new Vector<BusyHourData>();
    //20120202 efaigha eanguan :: Refreshing the BusyHourDataModel. Needed if calling activation without using BusyHour Tab in edit window 
    refresh();
    for (BusyHourData bd : busyHourData) {
    	if(bd.getBusyhour().getVersionid().equals(tid)){
    		result.add(bd);
    	}else if(bd.getBusyhour().getTargetversionid().equals(tid)){
    		result.add(bd);
    	}
    }
    return result;
  }
  
  public List<BusyHourData> getBusyHourDataByPlaceholderType(String targetversionId, String bhLevel,
      String placeholderType) {
    List<BusyHourData> result = new Vector<BusyHourData>();
    for (BusyHourData bd : busyHourData) {
      if (bd.getBusyhour().getTargetversionid().equals(targetversionId)
          && bd.getBusyhour().getBhlevel().equals(bhLevel)
          && bd.getBusyhour().getPlaceholdertype().equals(placeholderType)) {
        result.add(bd);
      }
    }
    return result;
  }

  /**
   * Method to get the BHLevel of Busy Hours matching targetVersionId.
   * 
   * @param tid
   * @return
   */
  public List<String> getBusyHourDataNames(String tid) {
    List<String> result = new Vector<String>();
    for (BusyHourData bd : busyHourData) {
      if (bd.getBusyhour().getTargetversionid().equals(tid) && !result.contains(bd.getBusyhour().getBhlevel())) {
        result.add(bd.getBusyhour().getBhlevel());
      }
    }
    return result;
  }

  public List<String> getBusyHourVersionIDs() {
    List<String> result = new Vector<String>();
    for (BusyHourData bd : busyHourData) {
      if (!result.contains(bd.getBusyhour().getVersionid())) {
        result.add(bd.getBusyhour().getVersionid());
      }
    }
    return result;
  }

  public List<String> getBusyHourTargetVersionIDs() {
    List<String> result = new Vector<String>();
    for (BusyHourData bd : busyHourData) {
      if (!result.contains(bd.getBusyhour().getTargetversionid())) {
        result.add(bd.getBusyhour().getTargetversionid());
      }
    }
    return result;
  }

  public void setBusyHourData(final List<BusyHourData> busyHourData) {
    this.busyHourData = busyHourData;
  }

  public class BusyHourRankTables {

    private String versionid;

    private Vector<String> ranktables;

    /**
     * @param versionid
     * @param ranktables
     */
    public BusyHourRankTables(String versionid, Vector<String> ranktables) {
      super();
      this.versionid = versionid;
      this.ranktables = ranktables;
    }

    public String getVersionid() {
      return versionid;
    }

    public void setVersionid(String versionid) {
      this.versionid = versionid;
    }

    public Vector<String> getRanktables() {
      return ranktables;
    }

    public void setRanktables(Vector<String> ranktables) {
      this.ranktables = ranktables;
    }
  }

  public class BusyHourSourceTables {

    private String versionid;

    private Vector<String> basetables;

    /**
     * @param versionid
     * @param basetables
     */
    public BusyHourSourceTables(String versionid, Vector<String> basetables) {
      super();
      this.versionid = versionid;
      this.basetables = basetables;
    }

    public String getVersionid() {
      return versionid;
    }

    public void setVersionid(String versionid) {
      this.versionid = versionid;
    }

    public Vector<String> getBasetables() {
      return basetables;
    }

    public void setBasetables(Vector<String> basetables) {
      this.basetables = basetables;
    }
  }

  public static String replaceLast(String find, String replace, String source) {
    String result = "";

    if (source.indexOf(find) >= 0) {
      result += source.substring(0, source.lastIndexOf(find)) + replace;
      source = source.substring(source.lastIndexOf(find) + find.length());
    }
    result += source;

    return result;
  }

  /**
   * Migrates the busy hour data to the latest DB version. The current migrate
   * goes to ENIQ level 2.0, which does not include the Busy Hour Improvements.
   * The step from 2.0 to Busy Hour Improvements will be made in the mandatory
   * copying of the techpack after migrate.
   * 
   * @param versioning
   * @param fromEniqLevel
   * @throws Exception
   */
  public void migrate(Versioning versioning, String fromEniqLevel) throws Exception {

    // Check the from version.
    if (!fromEniqLevel.equals("1.0")) {
      // No need for migrate.
      return;

    } else {
      // Migrating from ENIQ1.0 to ENIQ2.0

      List<String> createdBusyhourNames = new ArrayList<String>();
      String versionid = versioning.getVersionid();
      // remove all RANKBH aggregation rules
      Aggregationrule aggr = new Aggregationrule(this.rockFactory);
      aggr.setVersionid(versionid);
      aggr.setTarget_level("RANKBH");
      AggregationruleFactory aggrF = new AggregationruleFactory(this.rockFactory, aggr);
      for (Aggregationrule agreru : aggrF.get()) {

        if (createdBusyhourNames.contains(agreru.getTarget_type() + "_" + agreru.getBhtype())) {
          agreru.deleteDB();
          continue;
        }

        createdBusyhourNames.add(agreru.getTarget_type() + "_" + agreru.getBhtype());

        // create Busy hours

        String whereclause = "";
        String criteria = "";

        Busyhour bh = new Busyhour(this.rockFactory);
        bh.setVersionid(versionid);
        bh.setTargetversionid(versionid);
        bh.setBhlevel(agreru.getTarget_type().trim());
        bh.setBhobject(agreru.getBhtype().substring(0, agreru.getBhtype().indexOf("_")).trim());
        bh.setBhtype(agreru.getBhtype().substring(agreru.getBhtype().indexOf("_") + 1).trim());
        bh.setBhelement(agreru.getTarget_type().substring(agreru.getTarget_type().lastIndexOf("_") + 1)
            .equals("ELEMBH") ? 1 : 0);

        // Description is set to the type. This is a workaround to get unique
        // descriptions so that all values can be selected in the reports.
        bh.setDescription(agreru.getBhtype().substring(agreru.getBhtype().indexOf("_") + 1).trim());

        bh.setWhereclause("");
        bh.setBhcriteria("");
        bh.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);
        bh.setGrouping(Constants.BH_GROUPING_TYPES[0]);
        bh.setReactivateviews(0);
        bh.saveDB();

        Externalstatement exts = new Externalstatement(this.rockFactory);
        exts.setVersionid(versionid);
        ExternalstatementFactory extsF = new ExternalstatementFactory(this.rockFactory, exts);

        // Iterate through all external statements (create statements) and
        // extract the where, criteria and description values for busy hours.
        for (Externalstatement exstaem : extsF.get()) {

          // Search for create statements
          String createStatementName = "CREATE VIEW " + agreru.getTarget_type() + "_RANKBH_" + agreru.getBhtype();
          if (exstaem.getStatementname().equalsIgnoreCase(createStatementName)) {

            // CRITERIA
            //
            // Example: 'AtmPort_RxAtmP',
            // cast((sum(ifnull(pmReceivedAtmCells,0,pmReceivedAtmCells))) as
            // numeric(18,8)), sum(PERIOD_DURATION)

            // The start tag for the criteria: For example:
            // "'AtmPort_RxAtmP', ". Calculate the position in the statement
            // where it ends.
            int criteriaStartPosition = exstaem.getStatement().indexOf("'" + agreru.getBhtype() + "', ")
                + agreru.getBhtype().length() + 4;

            // The end tag for the criteria: For example:
            // ", sum(src1.PERIOD_DURATION)" or ", sum(PERIOD_DURATION)"
            // Calculate the position in the statement where it starts.
            String tmpStr = exstaem.getStatement().substring(criteriaStartPosition,
                exstaem.getStatement().indexOf("PERIOD_DURATION)") + 16);
            tmpStr = tmpStr.substring(tmpStr.lastIndexOf(", sum("));
            int criteriaEndPosition = exstaem.getStatement().indexOf(tmpStr);

            // Extract the criteria from the statement
            criteria = exstaem.getStatement().substring(criteriaStartPosition, criteriaEndPosition);

            // SOURCES

            String select = exstaem.getStatement().substring(exstaem.getStatement().indexOf(" SELECT "));

            // FROM DC_E_MGW_ATMPORT_COUNT GROUP BY
            String sources_groupby = "";
            try {
              sources_groupby = select.substring(select.indexOf(" FROM ") + 6, (select.indexOf(" GROUP BY")));
            } catch (Exception e) {

            }

            // FROM DC_E_MGW_ATMPORT_COUNT WHERE
            String sources_where = "";
            try {
              sources_where = select.substring(select.indexOf(" FROM ") + 6, (select.indexOf(" where")));

            } catch (Exception e) {

            }

            // if there is both where and group by strings then take the where
            // string one
            String[] source = sources_groupby.split(",");
            if (sources_where.length() > 0) {

              source = sources_where.split(",");

              // WHERECALUSE

              // Parse the where clause. ' where ' is stripped away.
              whereclause = sources_groupby.substring(sources_groupby.indexOf(" where ") + 7);

            }

            for (int i = 0; i < source.length; i++) {

              // Sources
              Busyhoursource bhs = new Busyhoursource(this.rockFactory);
              bhs.setVersionid(versionid);
              bhs.setTargetversionid(bh.getTargetversionid());
              bhs.setBhlevel(bh.getBhlevel());
              bhs.setBhobject(bh.getBhobject());
              bhs.setBhtype(bh.getBhtype());
              bhs.setTypename(source[i].trim());

              bhs.saveDB();

            }

            // KEYS

            // CREATE VIEW DC_E_MGW_ATMPORTBH_RANKBH_AtmPort_RxAtmP (
            // OSS_ID,MGW,TRANSPORTNETWORK
            // ,ATMPORT,DATE_ID,HOUR_ID,ROWSTATUS,BHTYPE

            String keynames = exstaem.getStatement().substring(
                exstaem.getStatement().indexOf(createStatementName + " ( ") + createStatementName.length() + 3,
                (exstaem.getStatement().indexOf(",DATE_ID")));
            String keyname[] = keynames.split(",");

            // SELECT OSS_ID,MGW,TRANSPORTNETWORK,ATMPORT, DATE_ID, HOUR_ID,
            // ROWSTATUS, 'AtmPort_RxAtmP'

            String keyvalues = select.substring(select.indexOf("SELECT ") + 7, (select.indexOf(", DATE_ID")));
            String keyvalue[] = keyvalues.split(",");

            if (keyvalue.length == keyname.length) {
              for (int i = 0; i < keyvalue.length; i++) {

                // Keys
                Busyhourrankkeys bhk = new Busyhourrankkeys(this.rockFactory);
                bhk.setVersionid(versionid);
                bhk.setTargetversionid(bh.getTargetversionid());
                bhk.setBhlevel(bh.getBhlevel());
                bhk.setBhobject(bh.getBhobject());
                bhk.setBhtype(bh.getBhtype());
                bhk.setKeyname(keyname[i].trim());
                bhk.setKeyvalue(keyvalue[i].trim());
                bhk.setOrdernbr(new Long(i));
                bhk.saveDB();

              }
            }
          }
        }

        // Update the busy hour data based on the information extracted from
        // external statements.
        Busyhour bhu = new Busyhour(this.rockFactory);
        bhu.setVersionid(versionid);
        bhu.setTargetversionid(bh.getTargetversionid());
        bhu.setBhlevel(bh.getBhlevel());
        bhu.setBhobject(bh.getBhobject());
        bhu.setBhtype(bh.getBhtype());
        bhu.setAggregationtype(bh.getAggregationtype());

        BusyhourFactory bhuF = new BusyhourFactory(this.rockFactory, bhu);
        for (Busyhour b : bhuF.get()) {
          b.setWhereclause(whereclause);
          b.setBhcriteria(criteria);
          b.saveDB();
        }

        // remove aggregation
        agreru.deleteDB();
      }

      // remove all RANKBH aggregations
      Aggregation agg = new Aggregation(this.rockFactory);
      agg.setVersionid(versionid);
      agg.setAggregationtype("RANKBH");
      AggregationFactory aggF = new AggregationFactory(this.rockFactory, agg);
      for (Aggregation agre : aggF.get()) {
        agre.deleteDB();
      }

      this.setCurrentVersioning(versioning);

      // reload the data...
      refresh();

      // create the aggregations....
      for (BusyHourData bh : busyHourData) {
        StorageTimeAction.createRankbhAggregationsForBusyhour(bh.getBusyhour(), bh.getBusyhourSource(), currentVersioning.getVersionid(), rockFactory);
      }
    }
  }

  /**
   * Deletes the aggregator set for the BH aggregation.
   * 
   * @param aggregation
   * @throws Exception
   */
  public void deleteAggregatorSet(Aggregation aggregation) {

    logger.info("Removing aggregator set for aggregation: " + aggregation.getAggregation());

    // Get the ETL rock factory.
    RockFactory etlRockFactory = dataModelController.getEtlRockFactory();

    String version = currentVersioning.getVersionid().substring(currentVersioning.getVersionid().indexOf(":") + 1);

    // Get the meta collection set name from the DB.
    Meta_collection_sets mwhere = new Meta_collection_sets(dataModelController.getEtlRockFactory());
    mwhere.setCollection_set_name(currentVersioning.getTechpack_name());
    mwhere.setVersion_number(version);

    try {
      Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
          mwhere);
      Vector<Meta_collection_sets> tps = mcsf.get();

      Meta_collection_sets mcs = null;
      int mcs_id = 0;
      if (tps.size() > 0) {
        mcs = (Meta_collection_sets) tps.get(0);
        mcs_id = (int) mcs.getCollection_set_id().longValue();
      } else {
        logger.info("No meta collection set found. No aggregator sets to be removed.");
        return;
      }

      // Set the aggregator set name
      String name = "Aggregator_" + aggregation.getAggregation();

      long setid = Utils.getSetId(name, version, mcs_id, etlRockFactory);
      if (setid == -1) {
        logger.info("No set found, no need to remove");
        return;
      }
      logger.fine("Removing action: GateKeeper");
      Utils.removeAction("GateKeeper", version, mcs_id, setid, etlRockFactory);

      logger.fine("Removing action: " + name);
      Utils.removeAction(name, version, mcs_id, setid, etlRockFactory);

      logger.fine("Removing scheduling " + name);
      Utils.removeScheduling(name, version, mcs_id, setid, etlRockFactory);

      logger.fine("Removing aggregator set " + name);
      Utils.removeSet(name, version, mcs_id, etlRockFactory);
    } catch (Exception e) {
      logger.severe("Error in removing the aggregator set for aggregation.");
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

  }

  /**
   * Removes the aggregation with the specified name and its aggregation rules
   * from the DB. Removes also the aggregator sets if specified with the
   * parameter.
   * 
   * @param aggregationName
   *          The name if the aggregation
   * @param deleteAggregatorSets
   *          True if the aggregator sets are also removed from the DB.
   * @throws SQLException
   * @throws RockException
   */
  private void deleteAggregation(String aggregationName, boolean deleteAggregatorSets) throws SQLException,
      RockException {

    // Get the aggregation(s) matching the name from the DB.
    final Aggregation aggCondition = new Aggregation(rockFactory, false);
    aggCondition.setAggregation(aggregationName);
    aggCondition.setVersionid(currentVersioning.getVersionid());
    AggregationFactory aggregationFactory = new AggregationFactory(rockFactory, aggCondition);
    Vector<Aggregation> aggregations = aggregationFactory.get();

    // Loop through all matches and delete the aggregation, rules, and sets.
    for (Aggregation aggregation : aggregations) {

      // Get the aggregation rules.
      final Aggregationrule aggRuleCondition = new Aggregationrule(rockFactory);
      aggRuleCondition.setVersionid(this.currentVersioning.getVersionid());
      aggRuleCondition.setAggregation(aggregation.getAggregation());
      final AggregationruleFactory aggregationruleFactory = new AggregationruleFactory(rockFactory, aggRuleCondition);
      final Vector<Aggregationrule> aggRules = aggregationruleFactory.get();

      // Delete the rules.
      for (Aggregationrule aggRule : aggRules) {
        aggRule.deleteDB();
      }

      // Delete the aggreagation set for this aggregation.
      if (deleteAggregatorSets)
        deleteAggregatorSet(aggregation);

      // Delete the aggreagation.
      aggregation.deleteDB();
    }
  }

  /**
   * @param tid
   * @return
   */
  public Vector<String> getRankingMeasurementtypenames(String tid) {

    Vector<String> result = new Vector<String>();

    try {
      Measurementtype mt = new Measurementtype(this.rockFactory);
      mt.setVersionid(tid);
      mt.setRankingtable(1);
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(this.rockFactory, mt);

      for (Measurementtype m : mtF.get()) {
        result.add(m.getTypename());
      }
    } catch (Exception e) {

    }

    return result;
  }

  /**
   * Gets all the type names for ranking table measurement types.
   * 
   * @return an array of names.
   */
  public String[] getRankingMeasurementtypenames() {

    Vector<String> measurementTypes = new Vector<String>();

    try {
      Measurementtype mt = new Measurementtype(this.rockFactory);
      mt.setRankingtable(1);
      MeasurementtypeFactory mtF = new MeasurementtypeFactory(this.rockFactory, mt);
      for (Measurementtype m : mtF.get()) {
        measurementTypes.add(m.getTypename());
      }
    } catch (Exception e) {

    }
    String[] result = new String[measurementTypes.size()];
    result = measurementTypes.toArray(result);
    return result;
  }

  /**
   * Gets a list of versionIds for techpacks with one or more ranking tables.
   * 
   * @return
   */
  public String[] getRankingTechpacks() {

    // Get the list of ranking techpacks. If the list is already collected, then
    // return it, otherwise collect the list.

    if (rankingTechpacks != null) {
      return rankingTechpacks;
    }

    Vector<String> rankingVec = new Vector<String>();

    try {
      Versioning ver = new Versioning(this.rockFactory);
      VersioningFactory verF = new VersioningFactory(this.rockFactory, ver);
      for (Versioning v : verF.get()) {
        Measurementtype mt = new Measurementtype(this.rockFactory);
        mt.setVersionid(v.getVersionid());
        mt.setRankingtable(1);
        MeasurementtypeFactory mtF = new MeasurementtypeFactory(this.rockFactory, mt);
        if (!mtF.get().isEmpty()) {
          rankingVec.add(v.getVersionid());
        }
      }
    } catch (Exception e) {

    }

    String[] rankingTechpacks = new String[rankingVec.size()];
    rankingTechpacks = rankingVec.toArray(rankingTechpacks);
    return rankingTechpacks;
  }

  /**
   * Regenerates all the busy hour (RANKBH) aggregations and aggregation rules
   * for a techpack. All existing busy hour (RANKBH) aggregations and rules are
   * first removed. The model does not need to be refreshed from the DB for this
   * method to work.
   * 
   * @param versionId
   * @throws Exception
   */
  public void regenerateBhAggregationsForTechpack(String versionId) throws Exception {

    // First remove all the RANKBH busy hour aggregations and aggregation rules
    // from the DB for this techpack.
    Aggregation aCond = new Aggregation(rockFactory);
    aCond.setVersionid(versionId);
    AggregationFactory af = new AggregationFactory(rockFactory, aCond);

    for (Aggregation a : af.get()) {

      if (a.getAggregationtype().equals("RANKBH")) {
        // Aggregation is a busy hour aggregation.

        // Remove the aggregation rules for this aggregation.
        Aggregationrule agr = new Aggregationrule(rockFactory);
        agr.setVersionid(versionId);
        agr.setAggregation(a.getAggregation());
        agr.deleteDB();

        // Remove the aggregation.
        a.deleteDB();
      }
    }

    
    // Iterate through all the busy hour for this techpack and generate the
    // RANKBH aggregations and rules.
    for (Busyhour bh : getBusyhours(versionId)) {
    	
         StorageTimeAction.createRankbhAggregationsForBusyhour(bh, getSources(bh), versionId, rockFactory);
         
         // update log_aggregationRules
         if (!bh.getBhcriteria().trim().equals("") ){
                 createDWHAggregationRules(bh);
         }
    }
    
  }

  /**
   * Check the the number of product and custom place holders for each busy hour
   * support (ranking table) and create missing or remove extra busy hour place
   * holders.
   * 
   * @param rankingTables
   */
  private void updateBusyHourPlaceHolders(List<MeasurementtypeExt> rankingTables) {

    // Get the rock connection
    RockFactory rock = dataModelController.getRockFactory();

    // Iterate through the measurement data from the updated measurement type
    // data model. Create the busy hour place holders for all busy hour
    // supports (ranking tables) if missing.
    logger.log(Level.FINE, "Creating new busy hour place holders if needed.");

    for (MeasurementtypeExt rankingTable : rankingTables) {
      try {
        final Measurementtype mtype = rankingTable.getMeasurementtype();

        int numberOfProductPHs = rankingTable.getBHProductPlaceholders();
        int numberOfCustomPHs = rankingTable.getBHCustomPlaceholders();

        // Check missing product place holders
        for (int i = 0; i < numberOfProductPHs; i++) {

          boolean match = false;
          for (BusyHourData bhData : busyHourData) {
            Busyhour bh = bhData.getBusyhour();
            if (bh.getVersionid().equalsIgnoreCase(mtype.getVersionid())
                && (bh.getBhlevel().equalsIgnoreCase(mtype.getTypename()))
                && (bh.getBhtype().equalsIgnoreCase(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX + i))
                && (bh.getTargetversionid().equalsIgnoreCase(mtype.getVersionid()))) {
              match = true;

              break;
            }
          }
          if (!match) {
            // A place holder is missing, create it.

            logger.finest("Creating new busy hour place holder " + Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX + i
                + " for busy hour support " + mtype.getTypename() + ".");

            Busyhour newBH = new Busyhour(rock);

            newBH.setVersionid(mtype.getVersionid());
            newBH.setBhlevel(mtype.getTypename());
            newBH.setBhtype(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX + i);
            newBH.setTargetversionid(mtype.getVersionid());
            newBH.setBhobject(mtype.getTypename().substring(mtype.getTypename().lastIndexOf("_") + 1,
                mtype.getTypename().indexOf("BH")));
            newBH.setAggregationtype((Constants.BH_AGGREGATION_TYPPES[0]));

            if (Utils.replaceNull(mtype.getElementbhsupport()).equals(1)) {
              newBH.setBhelement(1);
            } else {
              newBH.setBhelement(0);
            }
            newBH.setEnable(0);
            newBH.setDescription("");
            newBH.setWhereclause("");
            newBH.setBhcriteria("");
            newBH.setOffset(0);
            newBH.setWindowsize(60);
            newBH.setLookback(0);
            newBH.setP_threshold(0);
            newBH.setN_threshold(0);
            newBH.setClause("");
            newBH.setPlaceholdertype(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX);
            newBH.setGrouping(Constants.BH_GROUPING_TYPES[0]);
            newBH.setReactivateviews(0);

            newBH.saveDB();
          }
        }
        // Check missing custom place holders
        for (int i = 0; i < numberOfCustomPHs; i++) {

          boolean match = false;
          for (BusyHourData bhData : busyHourData) {
            Busyhour bh = bhData.getBusyhour();
            if (bh.getVersionid().equalsIgnoreCase(mtype.getVersionid())
                && (bh.getBhlevel().equalsIgnoreCase(mtype.getTypename()))
                && (bh.getBhtype().equalsIgnoreCase(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX + i))
                && (bh.getTargetversionid().equalsIgnoreCase(mtype.getVersionid()))) {
              match = true;

              break;
            }
          }
          if (!match) {
            // A place holder is missing, create it.

            logger.finest("Creating new busy hour place holder " + Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX + i
                + " for busy hour support " + mtype.getTypename() + ".");

            Busyhour newBH = new Busyhour(rock);

            newBH.setVersionid(mtype.getVersionid());
            newBH.setBhlevel(mtype.getTypename());
            newBH.setBhtype(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX + i);
            newBH.setTargetversionid(mtype.getVersionid());
            newBH.setBhobject(mtype.getTypename().substring(mtype.getTypename().lastIndexOf("_") + 1,
                mtype.getTypename().indexOf("BH")));
            newBH.setAggregationtype((Constants.BH_AGGREGATION_TYPPES[0]));

            if (Utils.replaceNull(mtype.getElementbhsupport()).equals(1)) {
              newBH.setBhelement(1);
            } else {
              newBH.setBhelement(0);
            }
            newBH.setEnable(0);
            newBH.setDescription("");
            newBH.setWhereclause("");
            newBH.setBhcriteria("");
            newBH.setOffset(0);
            newBH.setWindowsize(60);
            newBH.setLookback(0);
            newBH.setP_threshold(0);
            newBH.setN_threshold(0);
            newBH.setClause("");
            newBH.setPlaceholdertype(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX);
            newBH.setGrouping(Constants.BH_GROUPING_TYPES[0]);
            newBH.setReactivateviews(0);

            newBH.saveDB();
          }
        }

        // Check the number of existing place holders against the configured
        // number. If there are too many, then remove the extra ones.
        //
        // NOTE: Remove fails in case the place holder is marked as enabled.
        // In this case, a warning is logged and save will fail.
        List<BusyHourData> removeList = new ArrayList<BusyHourData>();

        // Check extra product place holders.
        int existingProdPHs = 0;
        for (BusyHourData bhData : busyHourData) {
          Busyhour bh = bhData.getBusyhour();
          if (bh.getVersionid().equalsIgnoreCase(mtype.getVersionid())
              && (bh.getBhlevel().equalsIgnoreCase(mtype.getTypename()))
              && (bh.getPlaceholdertype().equalsIgnoreCase(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX))
              && (bh.getTargetversionid().equalsIgnoreCase(mtype.getVersionid()))) {
            existingProdPHs++;

            // If there are too many, add the busy hour to the remove list.
            if (existingProdPHs > numberOfProductPHs) {

              removeList.add(bhData);
            }
          }
        }

        if (existingProdPHs > numberOfProductPHs) {
          logger.log(Level.FINE, "The number of existing product place holders (" + existingProdPHs
              + ") exeeds the number of configured (" + numberOfProductPHs + ") for ranking table "
              + mtype.getTypename() + ". Removing " + (existingProdPHs - numberOfProductPHs) + " place holders.");
        }

        // Check extra product place holders.
        int existingCustPHs = 0;
        for (BusyHourData bhData : busyHourData) {
          Busyhour bh = bhData.getBusyhour();
          if (bh.getVersionid().equalsIgnoreCase(mtype.getVersionid())
              && (bh.getBhlevel().equalsIgnoreCase(mtype.getTypename()))
              && (bh.getPlaceholdertype().equalsIgnoreCase(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX))
              && (bh.getTargetversionid().equalsIgnoreCase(mtype.getVersionid()))) {
            existingCustPHs++;

            // If there are too many, add the busy hour to the remove list.
            if (existingCustPHs > numberOfCustomPHs) {

              removeList.add(bhData);
            }
          }
        }
        if (existingCustPHs > numberOfCustomPHs) {
          logger.log(Level.FINE, "The number of existing custom place holders (" + existingCustPHs
              + ") exeeds the number of configured (" + numberOfCustomPHs + ") for ranking table "
              + mtype.getTypename() + ". Removing " + (existingCustPHs - numberOfCustomPHs) + " place holders.");
        }

        // Remove the extra place holders from DB.
        // If the place holder is enable, there will be an exception!
        for (BusyHourData bhData : removeList) {
          if (Utils.replaceNull(bhData.getBusyhour().getEnable()).equals(0)) {
            // Remove the busy hour data from the DB.
            bhData.delete();
          } else {
            logger.log(Level.SEVERE, "Unable to remove an enabled busy hour for ranking table " + mtype.getTypename()
                + ". Disable the busy hour or increase the number of place holders.");
            throw new RuntimeException("Unable to remove an enabled busy hour for ranking table " + mtype.getTypename()
                + ". Disable the busy hour or increase the number of place holders.");
          }
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      } catch (RockException e1) {
        e1.printStackTrace();
      }
    }
  }
  public void createDWHAggregationRules(final Busyhour bh) throws SQLException, RockException {
	  
	    if (rockFactory != null) {
    	  final StringBuffer sql = new StringBuffer();
	      try {
	        
	        // get meastypes that are interesting to us
	        final Vector<String> meastypes = new Vector<String>();
	        meastypes.add(bh.getBhlevel());
	        Aggregationrule aggr = new Aggregationrule(rockFactory);
	        aggr.setVersionid(bh.getVersionid());
	        AggregationruleFactory aggrF = new AggregationruleFactory(rockFactory, aggr);
	        for (Aggregationrule a : aggrF.get()) {
	        	
	          if (meastypes.contains(a.getTarget_type())) {
	        	// In IDE tests HSQL dB does not understand SQL IF.
	        	// Skip "IF (SELECT count(*) FROM LOG_AggregationRules WHERE aggregation='" as deleting none-existing rows does not cause an error.
	            // remove	    			  	    			  
	            sql.append(" DELETE FROM LOG_AggregationRules WHERE aggregation='");
	            sql.append(a.getAggregation()).append("'; \n");
	          }
	        }
	        aggr = new Aggregationrule(rockFactory);
	        aggr.setVersionid(bh.getVersionid());
	        aggr.setEnable(1);
	        aggrF = new AggregationruleFactory(rockFactory, aggr);
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
	          final Connection rockConn = dataModelController.getDwhRockFactory().getConnection();
	          final Statement stmt = rockConn.createStatement();
	          try {
	        	  //count++;
	            stmt.executeUpdate(sql.toString());
	          } finally {
	            stmt.close();
	          }
	        }
	      } catch (RuntimeException e) {
	        throw e;
	      } catch (Exception e) {
	    	  logger.severe("Rock Error:" + e.getMessage()+", sql:"+sql.toString());
	    	  throw new RockException("Failed to create DWHAggregationRules");
	      }
	    }
	    
	  }
}
