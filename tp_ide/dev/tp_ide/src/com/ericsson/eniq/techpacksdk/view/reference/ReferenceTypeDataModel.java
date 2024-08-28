/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.reference;

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

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;

/**
 * @author eheijun
 * @author eheitur
 * 
 */
public class ReferenceTypeDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(ReferenceTypeDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private Versioning baseVersioning;

  private TechPackDataModel techPackDataModel;

  private DataModel universeDataModel;

  private List<ReferenceTypeData> references;

  private Vector<Referencecolumn> publiccolumns;

  public ReferenceTypeDataModel(RockFactory rockFactory) {
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

  public DataModel getUniverseDataModel() {
    return universeDataModel;
  }

  public void setUniverseDataModel(final DataModel universeDataModel) {
    this.universeDataModel = universeDataModel;
  }

  public List<ReferenceTypeData> getReferences() {
    return references;
  }

  public void refresh() {
    logger.finest("ReferenceTableDataModel starting refresh from DB");
    references = new ArrayList<ReferenceTypeData>();

    if (baseVersioning != null) {
      publiccolumns = getPublicColumnsForReferencetype();
    }

    if (currentVersioning != null) {
      final Vector<Referencetable> mt = getReferenceTables(currentVersioning.getVersionid());

      for (final Iterator<Referencetable> it = mt.iterator(); it.hasNext();) {
        final Referencetable referencetable = it.next();
        // final Vector<Referencecolumn> columns =
        // getColumnsForReferenceTable(referencetable, (currentVersioning
        // .getTechpack_type().equalsIgnoreCase("BASE") ? 0 : 1));
        final Vector<Referencecolumn> columns = getColumnsForReferenceTable(referencetable);

        // Add data to the references list. Skip the data from base techpack.
        if (referencetable.getBasedef() == null
            || (referencetable.getBasedef() != null && referencetable.getBasedef().intValue() != 1)) {
          final ReferenceTypeData data = new ReferenceTypeData(currentVersioning, referencetable, columns,
              publiccolumns, rockFactory);
          references.add(data);
        }
      }
    }

    logger.info("ReferenceTableDataModel refreshed from DB");
  }

  public void save() {

  }

  /**
   * Deletes the generated data for reference types, i.e. the reference table
   * and columns entries fetched from the base techpack. Nothing is done in case
   * the current techpack is a base techpack or a techpack without a base
   * techpack.
   */
  public void deleteGenerated() {
    try {
      // Remove generated reference table and column entries fetched from the
      // base techpack (not for a base techpack or techpack without a base).
      if (hasBaseTechPack()) {

        // Iterate through all reference table entries. Delete the base tables
        // and columns.
        Referencetable rt = new Referencetable(rockFactory);
        rt.setVersionid(currentVersioning.getVersionid());
        ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
        Iterator tableIter = rtF.get().iterator();
        while (tableIter.hasNext()) {
          Referencetable tmpRT = (Referencetable) tableIter.next();

          // Remove all base defined columns for this reference table entry.
          // These are either public columns for "normal" reference table or
          // columns for a base defined table.
          Referencecolumn rc = new Referencecolumn(rockFactory);
          rc.setTypeid(tmpRT.getTypeid());
          rc.setBasedef(1);
          ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rc);
          Iterator colIter = rcF.get().iterator();
          while (colIter.hasNext()) {
            Referencecolumn tmpRC = (Referencecolumn) colIter.next();
            tmpRC.deleteDB();

            // System.out.println("deleteGenerated(): DEBUG: removed column: " +
            // tmpRC.getDataname() + " for table: "
            // + tmpRC.getTypeid());

          }

          // Remove the table itself if it is a base definition.
          if (tmpRT.getBasedef() != null && tmpRT.getBasedef() == 1) {
            tmpRT.deleteDB();

            // System.out.println("deleteGenerated(): DEBUG: removed table: " +
            // tmpRT.getTypeid());
            //
          }
        }
      }
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in deleteGenerated", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in deleteGenerated", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in deleteGenerated", e);
    }
  }

  /**
   * Creates the generated data for reference types, i.e. the reference table
   * and columns entries fetched from the base techpack. Nothing is done in case
   * the current techpack is a base techpack or a techpack without a base
   * techpack.
   * 
   * NOTE: Currently only hard coded reference tables 'DIM_(MTNAME)BH_BHTYPE',
   * 'SELECT_(TPNAME)_AGGLEVEL', and 'PUBLIC_REFTYPE' are supported. All other
   * tables are ignored.
   * 
   */
  public void createGenerated() {
    try {

      // Create generated reference table and column entries fetched from the
      // base techpack (except for a base techpack or techpack without a base).
      if (hasBaseTechPack()) {

        // Iterate through all reference table entries in the base techpack and
        // create the matching entries to the database for this techpack.
        Referencetable rt = new Referencetable(rockFactory);
        rt.setVersionid(baseVersioning.getVersionid());
        ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
        Iterator tableIter = rtF.get().iterator();
        while (tableIter.hasNext()) {
          Referencetable tmpRT = (Referencetable) tableIter.next();

          // Check if the current base reference table is the special reference
          // type meant for ranking table measurement types, i.e. object name is
          // '(DIM_RANKMT)_BHTYPE'. If yes, then generate the reference tables
          // and columns for ranking table measurement types.
          if (tmpRT.getObjectname().equals("(DIM_RANKMT)_BHTYPE")) {
            createReferenceTypesForRankingTables(tmpRT);
            continue;
          }

          // Check if the current base reference table is the special reference
          // type meant for AGGLEVEL, i.e. object name is
          // 'SELECT_(TPNAME)_AGGLEVEL'. If yes, then copy the reference table
          // entry to the current techpack.
          if (tmpRT.getObjectname().equals("SELECT_(TPNAME)_AGGLEVEL")) {
            createReferenceTypesForAggLevel(tmpRT);
            continue;
          }

          // Check if the current base reference table is the special reference
          // type meant for public columns, i.e. object name is
          // 'PUBLIC_REFTYPE'. If yes, then copy all the columns from this base
          // reference type to the reference types in the current techpack.
          if (tmpRT.getObjectname().equals("PUBLIC_REFTYPE")) {
            createPublicReferenceColumns(tmpRT);
            continue;
          }

          // TODO: Anything else defined in the base techpack is now ignored.
          // If other reference types should be possible to be defined in the
          // base, then they must be separately handled here.
          logger.log(Level.WARNING, "Reference type: " + tmpRT.getTypename()
              + " defined in the base techpack is ignored when generating types from the base techpack.");

        }
      }
    } catch (SQLException e) {

      logger.log(Level.SEVERE, "SQL error in createGenerated", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in createGenerated", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in createGenerated", e);
    }
  }

  /**
   * Creates the generated reference table and column entries to the database
   * based on the special base techpack reference table entry.
   * 
   * @param baseReferenceTable
   * @throws Exception
   */
  private void createReferenceTypesForRankingTables(Referencetable baseReferenceTable) throws Exception {

    // Iterate through all ranking table measurement types and generate
    // the "BHTYPE" reference table and column entries to the database.
    Measurementtype mt = new Measurementtype(rockFactory);
    mt.setVersionid(currentVersioning.getVersionid());
    MeasurementtypeFactory mtF = new MeasurementtypeFactory(rockFactory, mt);
    Iterator mtIter = mtF.get().iterator();
    while (mtIter.hasNext()) {
      Measurementtype tmpMT = (Measurementtype) mtIter.next();
      if (tmpMT.getRankingtable().intValue() == 1) {

        // Create the type name by replacing the (DIM_RANKMT) in the
        // measurement type Object Name of the base reference table. For
        // example: "(DIM_RANKMT)_BHTYPE" for DC_E_MGW_AAL2APBH measurement type
        // will be "DIM_E_MGW_AAL2APBH_BHTYPE".
    	  
    	  //code changes for TR HQ87381
    	    String typeName = baseReferenceTable.getTypename().replace("(DIM_RANKMT)",
            "DIM_" + tmpMT.getObjectname().replaceFirst("DC_", ""));
            String typeId = currentVersioning.getVersionid() + ":" + typeName;

        // Create the reference table entry
        Referencetable newRT = new Referencetable(rockFactory);
        newRT.setTypeid(typeId);
        newRT.setVersionid(currentVersioning.getVersionid());
        newRT.setTypename(typeName);
        newRT.setObjectid(typeId);
        newRT.setObjectname(typeName);
        newRT.setObjectversion(baseReferenceTable.getObjectversion());
        newRT.setObjecttype(baseReferenceTable.getObjecttype());
        newRT.setDescription(baseReferenceTable.getDescription());
        newRT.setStatus(baseReferenceTable.getStatus());
        newRT.setUpdate_policy(baseReferenceTable.getUpdate_policy());
        newRT.setTable_type(baseReferenceTable.getTable_type());
        newRT.setDataformatsupport(baseReferenceTable.getDataformatsupport());
        newRT.setBasedef(1);
        newRT.saveDB();

        // System.out.println("createReferenceTypesForRankingTables(): DEBUG: created reftype: "
        // + typeId);

        // Create the reference columns for this reference table.
        Referencecolumn rc = new Referencecolumn(rockFactory);
        rc.setTypeid(baseReferenceTable.getTypeid());
        ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rc);
        Iterator rcIter = rcF.get().iterator();
        while (rcIter.hasNext()) {
          Referencecolumn tmpRC = (Referencecolumn) rcIter.next();

          // Create the reference column entry
          Referencecolumn newRC = new Referencecolumn(rockFactory);
          newRC.setTypeid(typeId);
          newRC.setDataname(tmpRC.getDataname());
          newRC.setColnumber(tmpRC.getColnumber());
          newRC.setDatatype(tmpRC.getDatatype());
          newRC.setDatasize(tmpRC.getDatasize());
          newRC.setDatascale(tmpRC.getDatascale());
          newRC.setUniquevalue(tmpRC.getUniquevalue());
          newRC.setNullable(tmpRC.getNullable());
          newRC.setIndexes(tmpRC.getIndexes());
          newRC.setUniquekey(tmpRC.getUniquekey());
          newRC.setIncludesql(tmpRC.getIncludesql());
          newRC.setIncludeupd(tmpRC.getIncludeupd());
          newRC.setColtype("PUBLICCOL");
          newRC.setDescription(tmpRC.getDescription());
          newRC.setUniverseclass(tmpRC.getUniverseclass());
          newRC.setUniverseobject(tmpRC.getUniverseobject());
          newRC.setUniversecondition(tmpRC.getUniversecondition());
          newRC.setDataid(tmpRC.getDataid());
          newRC.setBasedef(1);
          newRC.saveDB();

          // System.out.println("createReferenceTypesForRankingTables(): DEBUG: created refcol: "
          // + newRC.getDataname());

        }
      }
    }
  }

  /**
   * Creates the generated reference table and column entries to the database
   * based on the special base techpack reference table entry.
   * 
   * @param baseReferenceTable
   * @throws Exception
   */
  private void createReferenceTypesForAggLevel(Referencetable baseReferenceTable) throws Exception {

    // Copy the "AGGLEVEL" reference table to the database.

    // Create the type name by replacing the (TPNAME) in the base reference type
    // typeName. For example: "SELECT_(TPNAME)_AGGLEVEL" for DC_E_MGW techpack
    // "SELECT_E_MGW_AGGLEVEL".
    String typeName = baseReferenceTable.getTypename().replace("(TPNAME)",
        currentVersioning.getTechpack_name().replace("DC_", ""));
    String typeId = currentVersioning.getVersionid() + ":" + typeName;

    // Create the reference table entry
    Referencetable newRT = new Referencetable(rockFactory);
    newRT.setTypeid(typeId);
    newRT.setVersionid(currentVersioning.getVersionid());
    newRT.setTypename(typeName);
    newRT.setObjectid(typeId);
    newRT.setObjectname(typeName);
    newRT.setObjectversion(baseReferenceTable.getObjectversion());
    newRT.setObjecttype(baseReferenceTable.getObjecttype());
    newRT.setDescription(baseReferenceTable.getDescription());
    newRT.setStatus(baseReferenceTable.getStatus());
    newRT.setUpdate_policy(baseReferenceTable.getUpdate_policy());
    newRT.setTable_type(baseReferenceTable.getTable_type());
    newRT.setDataformatsupport(baseReferenceTable.getDataformatsupport());
    newRT.setBasedef(1);
    newRT.saveDB();

    // System.out.println("createReferenceTypesForAggLevel(): DEBUG: created reftype: "
    // + typeId);

  }

  /**
   * Creates the public reference column entries for the reference table entries
   * in the techpack based on the special base techpack reference table entry.
   * 
   * @param baseReferenceTable
   * @throws Exception
   */
  private void createPublicReferenceColumns(Referencetable baseReferenceTable) throws Exception {

    // Create a vector for the public columns
    Vector<Referencecolumn> refCols = new Vector<Referencecolumn>();

    // Get the public columns from the base reference table.
    Referencecolumn rc = new Referencecolumn(rockFactory);
    rc.setTypeid(baseReferenceTable.getTypeid());
    ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rc, "ORDER BY COLNUMBER");
    Iterator rcIter = rcF.get().iterator();
    while (rcIter.hasNext()) {
      Referencecolumn tmpRC = (Referencecolumn) rcIter.next();
      refCols.add(tmpRC);
    }

    // Iterate through all "non-base" reference tables in the techpack and
    // generate the public column entries to the database.
    Referencetable rt = new Referencetable(rockFactory);
    rt.setVersionid(currentVersioning.getVersionid());
    ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);
    Iterator rtIter = rtF.get().iterator();
    while (rtIter.hasNext()) {
      Referencetable tmpRT = (Referencetable) rtIter.next();
      // Include only reference tables from current techpack, not from base.
      if (tmpRT.getBasedef() == null || (tmpRT.getBasedef() != null && tmpRT.getBasedef().intValue() != 1)) {
        // Fix for IDE #674, HK79489 TechPack IDE is adding columns to topology
        // tables, eeoidiv, 20090727
        // Public Reference Columns should only be added to topology tables that
        // have type Dynamic.
        // Topology tables that are type Predefined and Static should not have
        // those extra columns in any case.
        // Update_policy 2 is dynamic [Static=0,Predefined=1,Dynamic=2,TimedDynamic=3]]
        // Update_policy 3 is Timed Dynamic, assume above 2 like Dynamic.
        // eeoidiv 20091203 : Timed Dynamic topology handling in ENIQ, WI 6.1.2, (284/159 41-FCP 103 8147) Improved WRAN Topology in ENIQ
    	// 20110830 EANGUAN :: Adding comparison for policy number 4 for History Dynamic (for SON)
        if ((tmpRT.getUpdate_policy() == 2) || (tmpRT.getUpdate_policy() == 3) || (tmpRT.getUpdate_policy() == 4)) {
          // Count the number of existing columns for this reference table. This
          // is needed for counting the column numbers for the public columns.
          long colNumber = 0;
          Referencecolumn rc2 = new Referencecolumn(rockFactory);
          rc2.setTypeid(tmpRT.getTypeid());
          ReferencecolumnFactory rcF2 = new ReferencecolumnFactory(rockFactory, rc2);
          colNumber = rcF2.get().size();

          colNumber = colNumber + 100L;

          // Create the reference columns for this reference table.
          Iterator rcIter3 = refCols.iterator();
          while (rcIter3.hasNext()) {
            Referencecolumn tmpRC = (Referencecolumn) rcIter3.next();

            // Increment the column number.
            colNumber++;

            // Create the public reference column entry
            Referencecolumn newRC = new Referencecolumn(rockFactory);
            newRC.setTypeid(tmpRT.getTypeid());
            newRC.setDataname(tmpRC.getDataname());
            newRC.setColnumber(colNumber);
            newRC.setDatatype(tmpRC.getDatatype());
            newRC.setDatasize(tmpRC.getDatasize());
            newRC.setDatascale(tmpRC.getDatascale());
            newRC.setUniquevalue(tmpRC.getUniquevalue());
            newRC.setNullable(tmpRC.getNullable());
            newRC.setIndexes(tmpRC.getIndexes());
            newRC.setUniquekey(tmpRC.getUniquekey());
            newRC.setIncludesql(tmpRC.getIncludesql());
            newRC.setIncludeupd(tmpRC.getIncludeupd());
            newRC.setColtype("PUBLICCOL");
            newRC.setDescription(tmpRC.getDescription());
            newRC.setUniverseclass(tmpRC.getUniverseclass());
            newRC.setUniverseobject(tmpRC.getUniverseobject());
            newRC.setUniversecondition(tmpRC.getUniversecondition());
            newRC.setDataid(tmpRC.getDataid());
            newRC.setBasedef(1);
            newRC.saveDB();
            // System.out.println("createReferenceTypesForRankingTables(): DEBUG: created public refcol: "
            // + tmpRC.getDataname() + " for table: " + tmpRT.getTypeid());
          } // end while (rcIter3.hasNext()) {
        } // end if (tmpRT.getUpdate_policy()) {
      } // end if (tmpRT.getBasedef() == null || (tmpRT.getBasedef() != null &&
      // tmpRT.getBasedef().intValue() != 1)) {
    }
  }

  /**
   * Returns the dataId from a DataItem with the specified dataName belonging to
   * a DataFormat with the specified typeId.
   * 
   * @param dataname
   *          The dataName of a DataItem
   * @param typeid
   *          The typeId of a DataFormat
   * @return A dataId for the matching data item. Null in case the are no
   *         matches.
   * @throws Exception
   */
  private String getDataIdFromDataFormat(String dataname, String typeid) throws Exception {
    // Create a new DataFormat object and set the type id.
    Dataformat df = new Dataformat(rockFactory);
    df.setTypeid(typeid);

    // Create a factory and iterate through all matching data formats
    DataformatFactory dfF = new DataformatFactory(rockFactory, df);
    Iterator<Dataformat> dfFI = dfF.get().iterator();
    while (dfFI.hasNext()) {
      Dataformat dform = (Dataformat) dfFI.next();

      // Create a DataItem object and set the dataFormatId and dataName.
      Dataitem di = new Dataitem(rockFactory);
      di.setDataformatid(dform.getDataformatid());
      di.setDataname(dataname);

      // Create a factory and iterate through all data items. If there
      // is a data item found, then return the dataId from it.
      DataitemFactory diF = new DataitemFactory(rockFactory, di);
      Iterator<Dataitem> diFI = diF.get().iterator();
      while (diFI.hasNext()) {
        Dataitem ditem = (Dataitem) diFI.next();
        return ditem.getDataid();
      }
    }
    return null;
  }

  /**
   * Migrates the reference type data to match the latest database structure.
   * 
   * @param versionid
   * @param fromEniqLevel
   * @throws Exception
   */
  public void migrate(String versionid, String fromEniqLevel) throws Exception {

    // Check the from version.
    if (!fromEniqLevel.equals("1.0")) {
      // No need for migrate.
      logger.log(Level.FINEST, "No need to migrate reference types.");
      return;
    } else {
      // Iterate through all the reference tables
      Vector<Referencetable> rtables = this.getReferenceTables(versionid);
      for (Iterator<Referencetable> iter = rtables.iterator(); iter.hasNext();) {
        Referencetable rtable = iter.next();

        // Iterate through all reference columns for this reference table
        // Vector<Referencecolumn> rcolumns =
        // this.getColumnsForReferenceTable(rtable, 2);
        Vector<Referencecolumn> rcolumns = this.getColumnsForReferenceTable(rtable);
        for (Iterator<Referencecolumn> iter2 = rcolumns.iterator(); iter2.hasNext();) {
          Referencecolumn rcolumn = iter2.next();

          // Set the values for the columns
          // rcolumn.setTypeid();
          // rcolumn.setDataname();
          rcolumn.setColnumber(Utils.replaceNull(rcolumn.getColnumber()) + 100);
          // rcolumn.setDatatype();
          // rcolumn.setDatasize();
          // rcolumn.setDatascale();
          // rcolumn.setUniquevalue();
          // rcolumn.setNullable();
          // rcolumn.setIndexes();
          // rcolumn.setUniquekey();
          // rcolumn.setIncludesql();
          // rcolumn.setIncludeupd();
          rcolumn.setColtype("COLUMN");
          rcolumn.setDescription("");
          // rcolumn.setUniverseclass();
          // rcolumn.setUniverseobject();
          // rcolumn.setUniversecondition();
          rcolumn.setDataid(getDataIdFromDataFormat(rcolumn.getDataname(), rtable.getTypeid()));
          rcolumn.setBasedef(0);

          try {
            rcolumn.saveToDB();
          } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error in migrate", e);
            ExceptionHandler.instance().handle(e);
            throw e;
          } catch (RockException e) {
            logger.log(Level.SEVERE, "ROCK error in migrate", e);
            throw e;
          } catch (Exception e) {
            logger.log(Level.SEVERE, "FATAL error in migrate", e);
            throw e;
          }
        }
      }
    }
  }

  private final static Comparator<Referencecolumn> REFERENCECOLUMNCOMPARATOR = new Comparator<Referencecolumn>() {

    public int compare(final Referencecolumn d1, final Referencecolumn d2) {

      final Long i1 = Utils.replaceNull(d1.getColnumber()).longValue();
      final Long i2 = Utils.replaceNull(d2.getColnumber()).longValue();
      return i1.compareTo(i2);
    }
  };

  /**
   * Returns a list of public reference columns
   * 
   * @return results a list of Referencecolumns
   */
  public Vector<Referencecolumn> getPublicColumnsForReferencetype() {

    Vector<Referencecolumn> results = new Vector<Referencecolumn>();
    try {
      // Get the public reference type from the base and return its columns.
      Referencetable rt = new Referencetable(rockFactory);
      rt.setVersionid(baseVersioning.getVersionid());
      rt.setTypename("PUBLIC_REFTYPE");
      ReferencetableFactory rtF = new ReferencetableFactory(rockFactory, rt);

      if (rtF.get().size() > 0) {
        Referencetable publicRT = rtF.get().elementAt(0);
        Referencecolumn rc = new Referencecolumn(rockFactory);
        rc.setTypeid(publicRT.getTypeid());
        ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rc);
        results = rcF.get();
      }

    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getPublicColumnsForReferencetype", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getPublicColumnsForReferencetype", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getPublicColumnsForReferencetype", e);
    }

    // Sort the results based on the colnumber.
    Collections.sort(results, REFERENCECOLUMNCOMPARATOR);

    return results;

  }

  /**
   * Returns a list of reference columns for referencetable (without public
   * columns)
   * 
   * @param referencetable
   *          the parent of the columns
   * @param filter
   *          0 = only public columns 1 = only specific columns 2 = all columns
   * @return results a list of Referencecolumns
   */
  /*
   * private Vector<Referencecolumn> getColumnsForReferenceTable(final
   * Referencetable referencetable, int filter) { final Vector<Referencecolumn>
   * results = new Vector<Referencecolumn>();
   * 
   * final Referencecolumn whereReferencecolumn = new
   * Referencecolumn(rockFactory);
   * whereReferencecolumn.setTypeid(referencetable.getTypeid()); try { final
   * ReferencecolumnFactory referencecolumnFactory = new
   * ReferencecolumnFactory(rockFactory, whereReferencecolumn, true); final
   * Vector<Referencecolumn> referencecolumns = referencecolumnFactory.get();
   * for (final Iterator<Referencecolumn> rciter = referencecolumns.iterator();
   * rciter.hasNext();) { final Referencecolumn referencecolumn = rciter.next();
   * switch (filter) { case 0: if (referencecolumn.getColnumber() <= 100) {
   * results.add(referencecolumn); } break; case 1: if
   * (referencecolumn.getColnumber() > 100) { results.add(referencecolumn); }
   * break; default: results.add(referencecolumn); } } } catch (SQLException e)
   * { logger.log(Level.SEVERE, "SQL error in getColumnsForReferenceTable", e);
   * ExceptionHandler.instance().handle(e); } catch (RockException e) {
   * logger.log(Level.SEVERE, "ROCK error in getColumnsForReferenceTable", e); }
   * catch (Exception e) { logger.log(Level.SEVERE,
   * "FATAL error in getColumnsForReferenceTable", e); }
   * Collections.sort(results, REFERENCECOLUMNCOMPARATOR); return results; }
   */

  /**
   * Returns a list of reference columns for referencetable.
   * 
   * @param referencetable
   *          the parent of the columns
   * @return results a list of Referencecolumns
   */
  private Vector<Referencecolumn> getColumnsForReferenceTable(final Referencetable referencetable) {
    final Vector<Referencecolumn> results = new Vector<Referencecolumn>();

    final Referencecolumn whereReferencecolumn = new Referencecolumn(rockFactory);
    whereReferencecolumn.setTypeid(referencetable.getTypeid());
    whereReferencecolumn.setBasedef(referencetable.getBasedef());
    try {
      final ReferencecolumnFactory referencecolumnFactory = new ReferencecolumnFactory(rockFactory,
          whereReferencecolumn, true);
      final Vector<Referencecolumn> referencecolumns = referencecolumnFactory.get();
      for (final Iterator<Referencecolumn> rciter = referencecolumns.iterator(); rciter.hasNext();) {
        final Referencecolumn referencecolumn = rciter.next();
        //referencecolumn.setDatatype(referencecolumn.getDatatype().toLowerCase());
        results.add(referencecolumn);
      }
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getColumnsForReferenceTable", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getColumnsForReferenceTable", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getColumnsForReferenceTable", e);
    }
    Collections.sort(results, REFERENCECOLUMNCOMPARATOR);
    return results;
  }

  /**
   * Returns a list of reference types by versionId
   * 
   * @return results a list of Referencetables
   */
  private Vector<Referencetable> getReferenceTables(final String versionId) {

    final Vector<Referencetable> results = new Vector<Referencetable>();

    final Referencetable whereReferenceTable = new Referencetable(rockFactory);
    whereReferenceTable.setVersionid(versionId);
    try {
      final ReferencetableFactory referencetypeFactory = new ReferencetableFactory(rockFactory, whereReferenceTable,
          true);
      final Vector<Referencetable> targetReferenceTables = referencetypeFactory.get();

      for (final Iterator<Referencetable> iter = targetReferenceTables.iterator(); iter.hasNext();) {
        final Referencetable mt = iter.next();
        results.add(mt);
      }
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "SQL error in getReferenceTables", e);
      ExceptionHandler.instance().handle(e);
    } catch (RockException e) {
      logger.log(Level.SEVERE, "ROCK error in getReferenceTables", e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "FATAL error in getReferenceTables", e);
    }

    return results;
  }

  public boolean delObj(final RockDBObject obj) {
    return false;
  }

  public boolean modObj(final RockDBObject obj) {
    return false;
  }

  public boolean modObj(final RockDBObject[] obj) {
    return false;
  }

  public boolean newObj(final RockDBObject obj) {
    return false;
  }

  public boolean validateDel(final RockDBObject obj) {
    return false;
  }

  public boolean validateMod(final RockDBObject obj) {
    return false;
  }

  public boolean validateNew(final RockDBObject obj) {
    return false;
  }

  public boolean updated(final DataModel dataModel) {

    // System.out.println("updated(): DEBUG: RefTypeDataModel is updated!");

    if (dataModel instanceof TechPackDataModel) {
      techPackDataModel = (TechPackDataModel) dataModel;
      if (techPackDataModel.getVersioning() != null) {
        this.setCurrentVersioning(techPackDataModel.getVersioning());
        this.setBaseVersioning(techPackDataModel.getBaseversioning());
        refresh();
      }
      return true;
    } else if (dataModel instanceof MeasurementTypeDataModel) {
      // Update the reference types from the base techpack. This is needed since
      // there might be changes in the measurement types for ranking tables.
      deleteGenerated();
      createGenerated();
    }
    return false;
  }

  /**
   * @return true if this techpack has a base techpack.
   */
  private boolean hasBaseTechPack() {
    boolean hasBase = true;
    for (int i = 0; i < Constants.TYPES_NOBASE.length; i++) {
      if (currentVersioning.getTechpack_type().equalsIgnoreCase(Constants.TYPES_NOBASE[i])) {
        hasBase = false;
        break;
      }
    }
    return hasBase;
  }
}
