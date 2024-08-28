/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.measurement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.BusyhourplaceholdersFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementdeltacalcsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementdeltacalcsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementvector;
import com.distocraft.dc5000.repository.dwhrep.MeasurementvectorFactory;
import com.distocraft.dc5000.repository.dwhrep.Supportedvendorrelease;
import com.distocraft.dc5000.repository.dwhrep.SupportedvendorreleaseFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.UniverseclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.UniversecomputedobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecondition;
import com.distocraft.dc5000.repository.dwhrep.UniverseconditionFactory;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeobject;
import com.distocraft.dc5000.repository.dwhrep.UniverseobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.UniverseparametersFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel;

/**
 * @author eheijun
 * @author eheitur
 * 
 */
public class MeasurementTypeDataModel implements DataModel {

	private static final Logger logger = Logger.getLogger(MeasurementTypeDataModel.class.getName());

	private final RockFactory rockFactory;

	private Versioning currentVersioning;

	private Versioning baseVersioning;

	private List<Measurementtypeclass> measurementtypeclasses;

	private List<MeasurementTypeData> measurements;

	private String[] vendorReleases;

	private String[] universeExtensions;

	private TechPackDataModel techPackDataModel;

	private DataModel universeDataModel;

	// private Vector<Measurementkey> publickeysPlain;
	//
	// private Vector<Measurementkey> publickeysRaw;
	//
	// private Vector<Measurementkey> publickeysDay;
	//
	// private Vector<Measurementkey> publickeysDaybh;
	//
	// private Vector<Measurementkey> publickeysCount;
	//
	// private Vector<Measurementkey> publickeysRankbh;
	//
	// private Vector<MeasurementcounterExt> publiccountersDay;
	//
	// private Vector<MeasurementcounterExt> publiccountersPlain;
	//
	// private Vector<MeasurementcounterExt> publiccountersRaw;
	//
	// private Vector<MeasurementcounterExt> publiccountersDaybh;
	//
	// private Vector<MeasurementcounterExt> publiccountersCount;
	//
	// private Vector<MeasurementcounterExt> publiccountersRankbh;

	private Vector<Measurementcolumn> publiccolumnsDay;

	private Vector<Measurementcolumn> publiccolumnsSonDayAgg;

	private Vector<Measurementcolumn> publiccolumnsSon15Agg;

	private Vector<Measurementcolumn> publiccolumnsPlain;

	private Vector<Measurementcolumn> publiccolumnsRaw;

	private Vector<Measurementcolumn> publiccolumnsDaybh;

	private Vector<Measurementcolumn> publiccolumnsCount;

	private Vector<Measurementcolumn> publiccolumnsRankbh;

	private Vector<Measurementcolumn> publiccolumnsRawLevel2;

	private Vector<Measurementcolumn> publiccolumnsOneMin;

	private Vector<Measurementcolumn> publiccolumnsFifteenMin;

	public boolean newDataCreated = false;

	// Vector for collecting the data formats for the current techpack. Used for
	// caching the data to speed up migrate.
	private Vector<Dataformat> dataFormats = null;

	// Map for collecting the data items for the data formats. The dataFormatId is
	// used as the key. Used for caching the data to speed up migrate.
	private Map<String, Vector<Dataitem>> dataItems = null;

	public MeasurementTypeDataModel(final RockFactory rockFactory) {
		this.rockFactory = rockFactory;
	}

	@Override
	public RockFactory getRockFactory() {
		return rockFactory;
	}

	public Versioning getCurrentVersioning() {
		return currentVersioning;
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

	public List<Measurementtypeclass> getMeasurementtypeclasses() {
		return measurementtypeclasses;
	}

	/**
	 * If selected Ranking table is exist in the same TechPack Check the ranking table has element busy hour support
	 * enabled or not
	 * 
	 */
	public MeasurementTypeData getMeasurement(final String mtypeid) {
		MeasurementTypeData result = null;
		for (final MeasurementTypeData item : measurements) {
			if (item.getMeasurementtypeExt().getTypeid().equals(mtypeid)) {

				result = item;
			}
		}
		return result;
	}

	/**
	 * If selected Ranking table has exist in other TP Check the ranking table has element busy hour support enabled or
	 * not for the selected Ranking table.
	 */

	public Integer gettargetMeasurement(final String targetVersioning, final String mtypeid) {

		Integer emBhsupport = 0;
		final Vector<Measurementtype> mt = getMeasurementTypes(targetVersioning);
		for (final Measurementtype measurementtype : mt) {
			final String typeid = measurementtype.getTypeid();
			if (typeid.equals(mtypeid)) {
				emBhsupport = measurementtype.getElementbhsupport();
			}
		}
		return emBhsupport;
	}

	public List<MeasurementTypeData> getMeasurements() {
		return measurements;
	}

	@Override
	public void refresh() {
		logger.finest("MeasurementTypeDataModel starting refresh from DB");
		final Vector<String> vr = this.getVendorReleases(currentVersioning.getVersionid());
		vendorReleases = new String[vr.size()];
		vr.toArray(vendorReleases);

		final Vector<String> ue = this.getUniverseExtensions(currentVersioning.getVersionid());
		universeExtensions = new String[ue.size()];
		ue.toArray(universeExtensions);

		measurementtypeclasses = new ArrayList<Measurementtypeclass>();
		measurements = new ArrayList<MeasurementTypeData>();

		if (currentVersioning != null) {

			measurementtypeclasses = getClassesForVersioning(currentVersioning);

			final Vector<Measurementtype> mt = getMeasurementTypes(currentVersioning.getVersionid());

			for (final Measurementtype measurementtype : mt) {
				final Vector<Measurementkey> keys = getKeysForMeasurementtype(measurementtype);
				final Vector<MeasurementcounterExt> counters = getCountersForMeasurementtype(measurementtype);
				final Vector<Object> dcsupports = getDeltaCalcSupportsForMeasurementtype(measurementtype);
				final Vector<Object> objbhsupports = getObjBHSupportsForMeasurementtype(measurementtype);
				final MeasurementtypeExt measurementtypeext = new MeasurementtypeExt(measurementtype, dcsupports,
						objbhsupports, getBusyhourPlaceholders(measurementtype));
				final MeasurementTypeData data = new MeasurementTypeData(currentVersioning, measurementtypeext, keys,
						counters, rockFactory);
				measurements.add(data);
			}
		}

		if (baseVersioning != null) {

			// publickeysPlain =
			// getPublicKeysForMeasurementtype(Constants.PLAINLEVEL);
			// publickeysRaw =
			// getPublicKeysForMeasurementtype(Constants.RAWLEVEL);
			// publickeysDay =
			// getPublicKeysForMeasurementtype(Constants.DAYLEVEL);
			// publickeysDaybh =
			// getPublicKeysForMeasurementtype(Constants.DAYBHLEVEL);
			// publickeysCount =
			// getPublicKeysForMeasurementtype(Constants.COUNTLEVEL);
			// publickeysRankbh =
			// getPublicKeysForMeasurementtype(Constants.RANKBHLEVEL);
			//
			// publiccountersPlain =
			// getPublicCountersForMeasurementtype(Constants.PLAINLEVEL);
			// publiccountersRaw =
			// getPublicCountersForMeasurementtype(Constants.RAWLEVEL);
			// publiccountersDay =
			// getPublicCountersForMeasurementtype(Constants.DAYLEVEL);
			// publiccountersDaybh =
			// getPublicCountersForMeasurementtype(Constants.DAYBHLEVEL);
			// publiccountersCount =
			// getPublicCountersForMeasurementtype(Constants.COUNTLEVEL);
			// publiccountersRankbh =
			// getPublicCountersForMeasurementtype(Constants.RANKBHLEVEL);

			publiccolumnsPlain = getPublicColumns(Constants.PLAINLEVEL);
			publiccolumnsRaw = getPublicColumns(Constants.RAWLEVEL);
			publiccolumnsDay = getPublicColumns(Constants.DAYLEVEL);
			publiccolumnsSonDayAgg = getPublicColumns(Constants.SONAGG);
			publiccolumnsSon15Agg = getPublicColumns(Constants.SON15AGG);
			publiccolumnsDaybh = getPublicColumns(Constants.DAYBHLEVEL);
			publiccolumnsCount = getPublicColumns(Constants.COUNTLEVEL);
			publiccolumnsRankbh = getPublicColumns(Constants.RANKBHLEVEL);
			publiccolumnsRawLevel2 = getPublicColumns(Constants.RAW_LEV2);
			publiccolumnsOneMin = getPublicColumns(Constants.ONEMIN);
			publiccolumnsFifteenMin = getPublicColumns(Constants.FIFTEENMIN);
		}

		logger.info("MeasurementTypeDataModel refreshed from DB");
	}

	@Override
	public void save() {
		// tabletree takes care of save
	}

	public void deleteGenerated(final MeasurementtypeExt measurementtypeExt) throws SQLException, RockException {
		deleteGenerated(true, measurementtypeExt);
	}

	/**
	 * Deletes all generated data from the database for the given measurement type.
	 * 
	 * @param truedelete
	 *            should be true if the measurement type will be deleted, false when updated.
	 * @param measurementtypeExt
	 * @throws SQLException
	 * @throws RockException
	 */
	public void deleteGenerated(final boolean truedelete, final MeasurementtypeExt measurementtypeExt)
			throws SQLException, RockException {

		// Get the measurement type name.
		final String measurementname = measurementtypeExt.getOriginal().getTypename();

		// Remove generated aggregation rules.
		final Aggregationrule whereAggregationrule = new Aggregationrule(rockFactory);
		whereAggregationrule.setVersionid(this.currentVersioning.getVersionid());
		whereAggregationrule.setTarget_type(measurementname);

		final List<String> aggregationsToDelete = new ArrayList<String>();
		final AggregationruleFactory aggregationruleFactory = new AggregationruleFactory(rockFactory,
				whereAggregationrule);
		final Vector<Aggregationrule> aggregationrules = aggregationruleFactory.get();
		for (final Aggregationrule aggregationrule : aggregationrules) {
			final String aggregationId = aggregationrule.getAggregation();
			aggregationrule.deleteDB();
			if (!aggregationsToDelete.contains(aggregationId)) {
				aggregationsToDelete.add(aggregationId);
			}
		}

		// Remove the generated aggregations.
		for (final String string : aggregationsToDelete) {
			final Aggregation whereAggregation = new Aggregation(rockFactory);
			whereAggregation.setVersionid(this.currentVersioning.getVersionid());
			whereAggregation.setAggregation(string);
			final AggregationFactory aggregationFactory = new AggregationFactory(rockFactory, whereAggregation);
			final Vector<Aggregation> aggregations = aggregationFactory.get();
			for (final Aggregation aggregation : aggregations) {
				aggregation.deleteDB();
			}
		}

		// Remove generated measurement columns and measurement tables.
		// NOTE: Vectorcounters are not removed.

		// final Vectorcounter whereVectorcounter = new Vectorcounter(rockFactory);
		final Measurementcolumn whereMeasurementcolumn = new Measurementcolumn(rockFactory);

		final Vector<Measurementtable> measurementtables = this.getTablesForMeasurementtype(measurementtypeExt
				.getOriginal());

		for (final Measurementtable measurementtable : measurementtables) {

			// whereVectorcounter.setMtableid(measurementtable.getMtableid());
			// whereVectorcounter.deleteDB(whereVectorcounter);

			whereMeasurementcolumn.setMtableid(measurementtable.getMtableid());
			whereMeasurementcolumn.deleteDB(whereMeasurementcolumn);

			measurementtable.deleteDB();
		}

		// If the measurement type will be deleted (not just updated), then remove
		// the referring universe classes and depending other objects.
		if (truedelete) {

			// Remove the "Keys" Universe Classes and depending objects.
			Universeclass unvclass = new Universeclass(rockFactory, true);
			unvclass.setVersionid(currentVersioning.getVersionid());
			unvclass.setClassname(measurementname + "_Keys");
			unvclass.setUniverseextension(measurementtypeExt.getMeasurementtype().getUniverseextension());
			// unvclass.setDescription("TPIDE Generated");
			// unvclass.setParent(measurementname);
			// unvclass.setObj_bh_rel(0);
			// unvclass.setElem_bh_rel(0);
			// unvclass.setInheritance(0);

			// Remove all universe stuff depending on the removed universe class
			UniverseclassFactory unvclassF = new UniverseclassFactory(rockFactory, unvclass);
			for (final Universeclass unvc : unvclassF.get()) {
				final Universecondition uc = new Universecondition(rockFactory);
				uc.setClassname(unvc.getClassname());
				final UniverseconditionFactory ucF = new UniverseconditionFactory(rockFactory, uc);
				for (final Universecondition ucFIA : ucF.get()) {
					ucFIA.deleteDB();
				}

				final Universecomputedobject uco = new Universecomputedobject(rockFactory);
				uco.setClassname(unvc.getClassname());
				final UniversecomputedobjectFactory ucoF = new UniversecomputedobjectFactory(rockFactory, uco);
				for (final Universecomputedobject ucoFIA : ucoF.get()) {
					final Universeparameters upa = new Universeparameters(rockFactory);
					upa.setClassname(unvc.getClassname());
					upa.setObjectname(ucoFIA.getObjectname());
					final UniverseparametersFactory upaF = new UniverseparametersFactory(rockFactory, upa);
					for (final Universeparameters upaFIA : upaF.get()) {
						upaFIA.deleteDB();
					}

					ucoFIA.deleteDB();
				}

				final Universeobject uob = new Universeobject(rockFactory);
				uob.setClassname(unvc.getClassname());
				final UniverseobjectFactory uobF = new UniverseobjectFactory(rockFactory, uob);
				for (final Universeobject uobFIA : uobF.get()) {
					uobFIA.deleteDB();
				}

				unvc.deleteDB();
			}

			// Remove the "RAW_Keys" universe classes.
			unvclass = new Universeclass(rockFactory, true);
			unvclass.setVersionid(currentVersioning.getVersionid());
			unvclass.setClassname(measurementname + "_RAW_Keys");
			unvclass.setUniverseextension(measurementtypeExt.getMeasurementtype().getUniverseextension());
			// unvclass.setDescription("TPIDE Generated");
			// unvclass.setParent(measurementname);
			// unvclass.setObj_bh_rel(0);
			// unvclass.setElem_bh_rel(0);
			// unvclass.setInheritance(0);

			unvclassF = new UniverseclassFactory(rockFactory, unvclass);
			for (final Universeclass unvc : unvclassF.get()) {
				unvc.deleteDB();
			}
		}
	}

	// private static Vector<Measurementkey>
	// joinMeasurementkeyVectors(Vector<Measurementkey> v1,
	// Vector<Measurementkey>
	// v2) {
	// final Vector<Measurementkey> allkeys = new Vector<Measurementkey>();
	// if (v1 != null) {
	// allkeys.addAll(v1);
	// }
	// if (v2 != null) {
	// allkeys.addAll(v2);
	// }
	// return allkeys;
	// }
	//
	// private static Vector<MeasurementcounterExt>
	// joinMeasurementcounterVectors(Vector<MeasurementcounterExt> v1,
	// Vector<MeasurementcounterExt> v2) {
	// final Vector<MeasurementcounterExt> allkeys = new
	// Vector<MeasurementcounterExt>();
	// if (v2 != null) {
	// allkeys.addAll(v2);
	// }
	// if (v1 != null) {
	// allkeys.addAll(v1);
	// }
	// return allkeys;
	// }

	/**
	 * Returns true if 'Calc Table' is selected in the parameter panel and if at least one of the defined 'Delta Calc
	 * Supports' (if any) is true.
	 * 
	 * @param measurementtypeExt
	 * @return
	 */
	private static boolean createCountTable(final MeasurementtypeExt measurementtypeExt) {
		boolean result = false;

		if (Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()).intValue() == 1) {
			result = true;
			if (measurementtypeExt.getDeltaCalcSupport().size() > 0) {
				// If all vendor releases are set as "false" then do not create count
				// table, but if there is at least one "true" then count table must be
				// created - eheijun 22.10.2008
				boolean test = false;
				for (final Object object : measurementtypeExt.getDeltaCalcSupport()) {
					final Measurementdeltacalcsupport tmp = (Measurementdeltacalcsupport) object;
					test = test || (tmp.getDeltacalcsupport().intValue() == 1);
				}
				result = test;
			}
		}
		return result;
	}

	/**
	 * Create the generated data for a measurement type.
	 * 
	 * @param measurementtypeExt
	 * @param allmeasurementtypes
	 * @throws Exception
	 */
	public void createGenerated(final MeasurementtypeExt measurementtypeExt,
			final Vector<MeasurementtypeExt> allmeasurementtypes) throws Exception {

		// Get the measurement keys for this measurement type.
		final Vector<Measurementkey> measurementkeys = getKeysForMeasurementtype(measurementtypeExt
				.getMeasurementtype());

		// Get the measurement counters for this measurement type.
		final Vector<MeasurementcounterExt> measurementcounters = getCountersForMeasurementtype(measurementtypeExt
				.getMeasurementtype());

		// Check if "count" table should be created or not.
		final boolean createCountTable = createCountTable(measurementtypeExt);

		// Create the measurement information (Measurementtable and
		// Measurementcolumn objects) to the database.
		//
		// NOTE: the measurement keys/counters are now separated so that public keys
		// are in an own list compared to the keys for this techpack. Public
		// counters are not supported at the moment, since the the measurement
		// columns in the base techpack are all considered to be "public keys".
		if (Utils.replaceNull(measurementtypeExt.getRankingtable()).intValue() == 0) {
			createMeasurementInfoForTableType(measurementtypeExt, measurementkeys, measurementcounters);

			if (Utils.replaceNull(measurementtypeExt.getTotalagg()).intValue() == 1) {
				createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
						publiccolumnsDay, Constants.DAYLEVEL);
			}
			if (Utils.replaceNull(measurementtypeExt.getSonAgg()).intValue() == 1) {
				createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
						publiccolumnsSonDayAgg, Constants.SONAGG);
			}
			if (Utils.replaceNull(measurementtypeExt.getSonFifteenMinAgg()).intValue() == 1) {

				createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
						publiccolumnsSon15Agg, Constants.SON15AGG);
			}
			if (measurementtypeExt.getObjBHSupport().size() > 0) {
				createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
						publiccolumnsDaybh, Constants.DAYBHLEVEL);
			}
			if (createCountTable) {
				createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
						publiccolumnsCount, Constants.COUNTLEVEL);
			}
		} else {
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsRankbh,
					Constants.RANKBHLEVEL);
		}

		createAggregationInformation(measurementtypeExt, measurementkeys, measurementcounters, allmeasurementtypes);

		final String measurementname = measurementtypeExt.getTypename();

		if (createCountTable(measurementtypeExt)) {

			// Create/Update the universe classes
			updateUnivExtInUnivClass(measurementtypeExt, measurementname, "_Keys", measurementname);
			updateUnivExtInUnivClass(measurementtypeExt, measurementname, "_RAW_Keys", measurementname);

		} else {

			// Remove extra RAW_keys universe class and depending objects if found.
			removeRawKeysInUnivClass(measurementname);

			// Create/Update the universe classes
			updateUnivExtInUnivClass(measurementtypeExt, measurementname, "_Keys", measurementname);

		}

	}

	private void createMeasurementInfoForTableType(final MeasurementtypeExt measurementtypeExt,
			final Vector<Measurementkey> measurementkeys, final Vector<MeasurementcounterExt> measurementcounters)
			throws Exception {
		if (Utils.replaceNull(measurementtypeExt.getPlaintable()).intValue() == 1) {
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsPlain,
					Constants.PLAINLEVEL);
		} else if (Utils.replaceNull(measurementtypeExt.getSonFifteenMinAgg()).intValue() == 1) {
			// createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
			// publiccolumnsPlain,
			// Constants.PLAINLEVEL);
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsRaw,
					Constants.RAWLEVEL);
		} else if (Utils.replaceNull(measurementtypeExt.getMixedpartitionstable()).intValue() == 1) {
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
					publiccolumnsRawLevel2, Constants.RAW_LEV2);
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsRaw,
					Constants.RAWLEVEL);
		} else if (Utils.replaceNull(measurementtypeExt.getEventscalctable()).intValue() == 1) {
			createMeasurementInfoForExtraAggForEvents(measurementtypeExt, measurementkeys, measurementcounters);
    } else if (measurementtypeExt.getTypename().equalsIgnoreCase("EVENT_E_LTE_IMSI_SUC")
        || measurementtypeExt.getTypename().equalsIgnoreCase("EVENT_E_SGEH_IMSI_SUC")) {
      // Special case for EVENT_E_SGEH_IMSI_SUC and EVENT_E_LTE_IMSI_SUC. 
      // This is a RAW table but we only want the 15MIN base definition. We do not want the RAW base definition.
      createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsFifteenMin,
          Constants.RAWLEVEL);
    } else {
      createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsRaw,
          Constants.RAWLEVEL);
    }
	}

	private void createMeasurementInfoForExtraAggForEvents(final MeasurementtypeExt measurementtypeExt,
			final Vector<Measurementkey> measurementkeys, final Vector<MeasurementcounterExt> measurementcounters)
			throws Exception {
		if (Utils.replaceNull(measurementtypeExt.getOneminagg()).intValue() == 1) {
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters, publiccolumnsOneMin,
					Constants.ONEMIN);
		}
		if (Utils.replaceNull(measurementtypeExt.getFifteenminagg()).intValue() == 1) {
			createMeasurementInformation(measurementtypeExt, measurementkeys, measurementcounters,
					publiccolumnsFifteenMin, Constants.FIFTEENMIN);
		}
	}

	/**
	 * Removes the Universe Class and depending objects for the generated "_RAW_Keys" universe class.
	 * 
	 * @param measurementname
	 *            Type name of the measurement type.
	 * @throws Exception
	 */
	private void removeRawKeysInUnivClass(final String measurementname) throws Exception {

		// Get the universe class.
		final Universeclass unvclass = new Universeclass(rockFactory, true);
		unvclass.setVersionid(currentVersioning.getVersionid());
		unvclass.setClassname(measurementname + "_RAW_Keys");
		unvclass.setParent(measurementname);
		unvclass.setObj_bh_rel(0);
		unvclass.setElem_bh_rel(0);
		unvclass.setInheritance(0);
		unvclass.setOrdernro(Long.MAX_VALUE);
		final UniverseclassFactory unvclassF = new UniverseclassFactory(rockFactory, unvclass);

		if ((unvclassF != null) && (unvclassF.get() != null) && !unvclassF.get().isEmpty()) {

			final Universeclass oldClass = unvclassF.getElementAt(0);

			// Get the list of universe computed objects, universe objects, and
			// universe conditions related to the universe class.
			final List<Universecomputedobject> computedobjects = new ArrayList<Universecomputedobject>();
			final List<Universeobject> objects = new ArrayList<Universeobject>();
			final List<Universecondition> conditions = new ArrayList<Universecondition>();

			final List<Universecomputedobject> colist = getUniverseComputedObjectList(oldClass);
			final List<Universeobject> olist = getUniverseObjectList(oldClass);
			final List<Universecondition> clist = getUniverseConditionList(oldClass);

			// Remove the related objects
			for (final Universecomputedobject computed : colist) {
				computedobjects.add((Universecomputedobject) computed.clone());
				computed.deleteDB();
			}
			for (final Universeobject object : olist) {
				objects.add((Universeobject) object.clone());
				object.deleteDB();
			}
			for (final Universecondition condition : clist) {
				conditions.add((Universecondition) condition.clone());
				condition.deleteDB();
			}

			// Remove the universe class itself
			oldClass.deleteDB();
		}
	}

	/**
	 * Get the universe class from the database and update the universe extension and description values. If not class
	 * does not exist yet, it will be created.
	 * 
	 * @param measTypeExt
	 * @param measName
	 * @param measExt
	 * @param parent
	 * @throws Exception
	 */
	private void updateUnivExtInUnivClass(final MeasurementtypeExt measTypeExt, final String measName,
			final String measExt, final String parent) throws Exception {

		// Get the new universe extension value.
		final String univExt = measTypeExt.getMeasurementtype().getUniverseextension();

		// Create the description. A new value is needed, since there might still
		// exist the old values "TPIDE Generated".
		String description = "";
		if (measExt == "_RAW_Keys") {
			description = "RAW keys for " + measName;
		} else {
			description = "Keys for " + measName;
		}

		// Get the universe class from the DB
		Universeclass unvclass = new Universeclass(rockFactory, true);
		unvclass = new Universeclass(rockFactory, true);
		unvclass.setVersionid(currentVersioning.getVersionid());
		unvclass.setClassname(measName + measExt);
		unvclass.setParent(parent);
		unvclass.setObj_bh_rel(0);
		unvclass.setElem_bh_rel(0);
		unvclass.setInheritance(0);
		unvclass.setOrdernro(Long.MAX_VALUE);

		final UniverseclassFactory unvclassF = new UniverseclassFactory(rockFactory, unvclass);

		// Create the universe class in case it does not exist yet, otherwise update
		// the universe extension value. In case the value is updated, also all
		// referenced universe objects, computed objects, and conditions are
		// updated.
		if ((unvclassF != null) && (unvclassF.get() != null)) {
			if (unvclassF.get().isEmpty()) {
				unvclass.setUniverseextension(univExt);
				unvclass.setDescription(description);
				// Only set ordernro to max_value if we are created a new universe class (otherwise it might not be
				// found):
				unvclass.setOrdernro(Long.MAX_VALUE);
				unvclass.saveDB();
			} else {

				// The class needs to be updated. First all the referring objects and
				// the class itself are removed from the database, and then inserted
				// with the updated universe extension value.

				// Get the class object and clone it.
				final Universeclass oldClass = unvclassF.getElementAt(0);
				final Universeclass newClass = (Universeclass) unvclassF.getElementAt(0).clone();

				// Get the list of universe computed objects, universe objects, and
				// universe conditions related to the universe class.
				final List<Universecomputedobject> computedobjects = new ArrayList<Universecomputedobject>();
				final List<Universeobject> objects = new ArrayList<Universeobject>();
				final List<Universecondition> conditions = new ArrayList<Universecondition>();

				final List<Universecomputedobject> colist = getUniverseComputedObjectList(oldClass);
				final List<Universeobject> olist = getUniverseObjectList(oldClass);
				final List<Universecondition> clist = getUniverseConditionList(oldClass);

				// Remove the related objects
				for (final Universecomputedobject computed : colist) {
					computedobjects.add((Universecomputedobject) computed.clone());
					computed.deleteDB();
				}
				for (final Universeobject object : olist) {
					objects.add((Universeobject) object.clone());
					object.deleteDB();
				}
				for (final Universecondition condition : clist) {
					conditions.add((Universecondition) condition.clone());
					condition.deleteDB();
				}

				// Remove the universe class itself
				oldClass.deleteDB();

				// Add the universe class with updated universe extension value.
				newClass.setUniverseextension(univExt);
				newClass.setDescription(description);
				newClass.insertDB();

				// Add the referenced universe computed objects.
				for (final Universecomputedobject computed : computedobjects) {
					computed.setUniverseextension(univExt);
					computed.insertDB();
				}

				// Add the referenced universe objects.
				for (final Universeobject object : objects) {
					object.setUniverseextension(univExt);
					object.insertDB();
				}

				// Add the referenced universe conditions.
				for (final Universecondition condition : conditions) {
					condition.setUniverseextension(univExt);
					condition.insertDB();
				}
			}
		}
	}

	/**
	 * Gets list of universe conditions related to a universe class. NOTE: The universe extension value is ignored, so
	 * it is assumed that there is only one match for the class versionId and className.
	 * 
	 * @param uc
	 * @return
	 * @throws Exception
	 */
	List<Universecondition> getUniverseConditionList(final Universeclass uc) throws Exception {
		List<Universecondition> condList = new ArrayList<Universecondition>();
		try {
			final Universecondition U = new Universecondition(rockFactory);
			U.setVersionid(uc.getVersionid());
			U.setClassname(uc.getClassname());
			// U.setUniverseextension(uc.getUniverseextension());
			final UniverseconditionFactory F = new UniverseconditionFactory(rockFactory, U);
			condList = F.get();
		} catch (final Exception e) {
			throw e;
		}
		return condList;
	}

	/**
	 * Gets list of universe object related to a universe class. NOTE: The universe extension value is ignored, so it is
	 * assumed that there is only one match for the class versionId and className.
	 * 
	 * @param uc
	 * @return
	 * @throws Exception
	 */
	List<Universeobject> getUniverseObjectList(final Universeclass uc) throws Exception {
		List<Universeobject> objList = new ArrayList<Universeobject>();
		try {
			final Universeobject U = new Universeobject(rockFactory);
			U.setVersionid(uc.getVersionid());
			U.setClassname(uc.getClassname());
			// U.setUniverseextension(uc.getUniverseextension());
			final UniverseobjectFactory F = new UniverseobjectFactory(rockFactory, U);
			objList = F.get();
		} catch (final Exception e) {
			throw e;
		}
		return objList;
	}

	/**
	 * Gets list of universe computed objects related to a universe class. NOTE: The universe extension value is
	 * ignored, so it is assumed that there is only one match for the class versionId and className.
	 * 
	 * @param uc
	 * @return
	 * @throws Exception
	 */
	List<Universecomputedobject> getUniverseComputedObjectList(final Universeclass uc) throws Exception {
		List<Universecomputedobject> compObjList = new ArrayList<Universecomputedobject>();
		try {
			final Universecomputedobject U = new Universecomputedobject(rockFactory);
			U.setVersionid(uc.getVersionid());
			U.setClassname(uc.getClassname());
			// U.setUniverseextension(uc.getUniverseextension());
			final UniversecomputedobjectFactory F = new UniversecomputedobjectFactory(rockFactory, U);
			compObjList = F.get();
		} catch (final Exception e) {
			throw e;
		}
		return compObjList;
	}

	/**
	 * 
	 * Crate the measurement table and measurement column information to the database for the defined table level.
	 * 
	 * @param measurementtypeExt
	 *            Measurement type for which the data is created for.
	 * @param measurementkeys
	 *            List of measurement keys in this techpack (not public keys).
	 * @param measurementcounters
	 *            List of measurement counters in this techpack (not public counters).
	 * @param publicColumns
	 *            List of public measurement columns from the base techpack.
	 * @param tablelevel
	 *            The table level.
	 * @throws Exception
	 */
	private void createMeasurementInformation(final MeasurementtypeExt measurementtypeExt,
			final Vector<Measurementkey> measurementkeys, final Vector<MeasurementcounterExt> measurementcounters,
			final Vector<Measurementcolumn> publicColumns, final String tablelevel) throws Exception {

		try {

			// Loop through all the measurement keys and collect the joinable
			// information for the measurement type entry.
			String joinables = "";
			boolean isFirst = true;
			for (final Measurementkey measurementkey : measurementkeys) {
				if ((measurementkey.getJoinable() != null) && (measurementkey.getJoinable().intValue() == 1)) {
					if (isFirst) {
						joinables = measurementkey.getDataname();
						isFirst = false;
					} else {
						joinables = joinables + "," + measurementkey.getDataname();
					}
				}
			}
			measurementtypeExt.setJoinable(joinables);

			// Save the measurement type
			measurementtypeExt.saveToDB();

			// Create a new measurement table entry for the given table level.
			final Measurementtable measurementtable = new Measurementtable(rockFactory, true);
			final String mtableid = measurementtypeExt.getTypeid() + ":" + tablelevel;
			measurementtable.setMtableid(mtableid);
			measurementtable.setTypeid(measurementtypeExt.getTypeid());
			measurementtable.setTablelevel(tablelevel);
			if (tablelevel.equals(Constants.SONAGG)) {
				measurementtable.setPartitionplan(measurementtypeExt.getSizing().toLowerCase() + "_day");
			} else if (tablelevel.equals(Constants.SON15AGG)) {
				measurementtable.setPartitionplan(measurementtypeExt.getSizing().toLowerCase() + "_day");
			} else {
				measurementtable.setPartitionplan(measurementtypeExt.getSizing().toLowerCase() + "_"
						+ tablelevel.toLowerCase());
			}
			if (tablelevel.equals("PLAIN")) {
				// if(measurementtypeExt.getSonFifteenMinAgg() == 1){
				// measurementtable.setBasetablename(measurementtypeExt.getTypename() + "_PREVSON");
				// }else{
				measurementtable.setBasetablename(measurementtypeExt.getTypename());
				// }
			} else if (tablelevel.equals(Constants.SONAGG)) {
				measurementtable.setBasetablename(measurementtypeExt.getTypename() + "_" + Constants.SONAGG);
			} else if (tablelevel.equals(Constants.SON15AGG)) {
				measurementtable.setBasetablename(measurementtypeExt.getTypename() + "_" + Constants.SON15AGG);
			} else {
				measurementtable.setBasetablename(measurementtypeExt.getTypename() + "_" + tablelevel);
			}

			// Save the measurement table
			measurementtable.saveToDB();

			// Create the columns for this measurement table.
			// NOTE: The column number will be set to a running number instead of old
			// way: 1-50 key, 51-100: public key, 100-: counters.

			long col = 0L;

			// Create the measurement columns for the measurement keys in this
			// techpack (not public columns).
			for (final Measurementkey measurementkey : measurementkeys) {
				final Measurementcolumn measurementcolumn = new Measurementcolumn(rockFactory);
				col++;
				measurementcolumn.setMtableid(mtableid);
				measurementcolumn.setDataname(Utils.replaceNull(measurementkey.getDataname()));
				measurementcolumn.setColnumber(col);
				measurementcolumn.setDatatype(Utils.replaceNull(measurementkey.getDatatype()));
				measurementcolumn.setDatasize(Utils.replaceNull(measurementkey.getDatasize()));
				measurementcolumn.setDatascale(Utils.replaceNull(measurementkey.getDatascale()));
				measurementcolumn.setUniquevalue(Utils.replaceNull(measurementkey.getUniquevalue()));
				measurementcolumn.setNullable(Utils.replaceNull(measurementkey.getNullable()));
				measurementcolumn.setIndexes(Utils.replaceNull(measurementkey.getIndexes()));
				measurementcolumn.setDescription(Utils.replaceNull(measurementkey.getDescription()));
				if ((measurementkey.getDataid() == null) || (measurementkey.getDataid().length() < 1)) {
					measurementcolumn.setDataid(Utils.replaceNull(measurementkey.getDataname()));
				} else {
					measurementcolumn.setDataid(Utils.replaceNull(measurementkey.getDataid()));
				}
				measurementcolumn.setReleaseid(Utils.replaceNull(currentVersioning.getVersionid()));
				measurementcolumn.setUniquekey(Utils.replaceNull(measurementkey.getUniquekey()));
				measurementcolumn.setIncludesql(Utils.replaceNull(measurementkey.getIncludesql()));
				measurementcolumn.setColtype("KEY");
				measurementcolumn.saveToDB();
			}

			// Create the public measurement columns for this techpack.
			// Note: There are no public columns for techpacks without a base
			// techpack.
			// Note: The current column number in the base techpack is ignored. It was
			// used only for internal ordering when the columns were fetched from
			// base.
			if (publicColumns != null) {
				for (final Measurementcolumn publicColumn : publicColumns) {
					final Measurementcolumn measurementcolumn = new Measurementcolumn(rockFactory);
					col++;
					measurementcolumn.setMtableid(mtableid);
					measurementcolumn.setDataname(Utils.replaceNull(publicColumn.getDataname()));
					// measurementcolumn.setColnumber(Utils.replaceNull(publicColumn.getColnumber()));
					measurementcolumn.setColnumber(col);
					measurementcolumn.setDatatype(Utils.replaceNull(publicColumn.getDatatype()));
					measurementcolumn.setDatasize(Utils.replaceNull(publicColumn.getDatasize()));
					measurementcolumn.setDatascale(Utils.replaceNull(publicColumn.getDatascale()));
					measurementcolumn.setUniquevalue(Utils.replaceNull(publicColumn.getUniquevalue()));
					measurementcolumn.setNullable(Utils.replaceNull(publicColumn.getNullable()));
					measurementcolumn.setIndexes(Utils.replaceNull(publicColumn.getIndexes()));
					measurementcolumn.setDescription(Utils.replaceNull(publicColumn.getDescription()));
					measurementcolumn.setDataid(Utils.replaceNull(publicColumn.getDataid()));
					measurementcolumn.setReleaseid(Utils.replaceNull(publicColumn.getReleaseid()));
					measurementcolumn.setUniquekey(Utils.replaceNull(publicColumn.getUniquekey()));
					measurementcolumn.setIncludesql(Utils.replaceNull(publicColumn.getIncludesql()));
					measurementcolumn.setColtype("PUBLICKEY");
					measurementcolumn.saveToDB();
				}
			}

			// If the table level is not RANKBH, then create the measurement columns
			// from the measurement counters in this techpack (not public columns).
			if (!tablelevel.equals("RANKBH")) {

				// col = 100L;

				for (final MeasurementcounterExt measurementcounterext : measurementcounters) {
					final Measurementcolumn measurementcolumn = new Measurementcolumn(rockFactory);
					col++;
					measurementcolumn.setMtableid(mtableid);
					measurementcolumn.setDataname(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
							.getDataname()));
					measurementcolumn.setColnumber(col);
					measurementcolumn.setDatatype(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
							.getDatatype()));
					measurementcolumn.setDatasize(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
							.getDatasize()));
					measurementcolumn.setDatascale(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
							.getDatascale()));
					measurementcolumn.setUniquevalue(255L);
					measurementcolumn.setNullable(1);
					measurementcolumn.setIndexes("");
					measurementcolumn.setDescription(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
							.getDescription()));
					if ((measurementcounterext.getMeasurementcounter().getDataid() == null)
							|| (measurementcounterext.getMeasurementcounter().getDataid().length() < 1)) {
						measurementcolumn.setDataid(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
								.getDataname()));
					} else {
						measurementcolumn.setDataid(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
								.getDataid()));
					}
					measurementcolumn.setReleaseid(Utils.replaceNull(currentVersioning.getVersionid()));
					measurementcolumn.setUniquekey(0);
					measurementcolumn.setIncludesql(Utils.replaceNull(measurementcounterext.getMeasurementcounter()
							.getIncludesql()));
					measurementcolumn.setColtype("COUNTER");
					measurementcolumn.saveToDB();

					// for (Iterator<Object> iterator2 =
					// measurementcounterext.getVectorcounters().iterator();
					// iterator2.hasNext();) {
					// Object obj = iterator2.next();
					// if (obj instanceof Measurementvector) {
					// Measurementvector measurementvector = (Measurementvector)
					// obj;
					// Vectorcounter vectorcounter = new
					// Vectorcounter(rockFactory);
					// vectorcounter.setMtableid(mtableid);
					// vectorcounter.setDataname(Utils.replaceNull(measurementvector.getDataname()));
					// vectorcounter.setVendorrelease(Utils.replaceNull(measurementvector.getVendorrelease()));
					// vectorcounter.setVindex(Utils.replaceNull(measurementvector.getVindex()));
					// vectorcounter.setVfrom(Utils.replaceNull(measurementvector.getVfrom()));
					// vectorcounter.setVto(Utils.replaceNull(measurementvector.getVto()));
					// vectorcounter.setMeasure(Utils.replaceNull(measurementvector.getMeasure()));
					// vectorcounter.saveToDB();
					// }
					// }

				}
			}

		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in createMeasurementInformation");
			ExceptionHandler.instance().handle(e);
			throw e;
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in createMeasurementInformation");
			throw e;
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in createMeasurementInformation");
			throw e;
		}
	}

	private void createAggregationInformation(final MeasurementtypeExt measurementtypeExt,
			final Vector<Measurementkey> measurementkeys, final Vector<MeasurementcounterExt> measurementcounters,
			final Vector<MeasurementtypeExt> allmeasurementtypes) throws Exception {

		try {
			logger.log(Level.FINEST, "Creating Aggregation Information");
			final String measurementname = measurementtypeExt.getTypename();
			final String vendorid = measurementtypeExt.getVendorid();

			Integer ruleID = 0;

			final boolean createCountTable = createCountTable(measurementtypeExt);

			// Check if 'Ranking Table' is set.
			if ((Utils.replaceNull(measurementtypeExt.getRankingtable()).intValue() == 0)
					&& (Utils.replaceNull(measurementtypeExt.getEventscalctable()).intValue() == 0)) {

				// 'Ranking Table' is not set.
				// Check that 'Total Aggregation' is set.
				if (Utils.replaceNull(measurementtypeExt.getTotalagg()).intValue() == 1) {

					// FD: If fact table includes incremental counters, insert clause for
					// delta aggregation to table Aggregation is added.
					//
					// Create COUNT delta aggregation for 'Calc Table'.
					if (createCountTable) {
						final Aggregation countAggregation = new Aggregation(rockFactory, true);
						countAggregation.setAggregation(measurementname + "_" + Constants.COUNTLEVEL);
						countAggregation.setVersionid(currentVersioning.getVersionid());
						countAggregation.setAggregationtype(Constants.TOTALTYPE);
						countAggregation.setAggregationscope(Constants.COUNTSCOPE);
						countAggregation.saveToDB();

					}

					// FD: If fact table includes total aggregation, insert clause for day
					// total aggregation to table Aggregation is added.
					//
					// Create a DAY total aggregation
					final Aggregation dayAggregation = new Aggregation(rockFactory, true);
					dayAggregation.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + Constants.DAYLEVEL);
					dayAggregation.setVersionid(currentVersioning.getVersionid());
					dayAggregation.setAggregationtype(Constants.TOTALTYPE);
					dayAggregation.setAggregationscope(Constants.DAYSCOPE);
					dayAggregation.saveToDB();

					// Check if table is a 'Calc Table' or other. Create Day-Count rule
					// for 'Calc Table' and Day-Raw for others.
					if (createCountTable) {
						// FD: If fact table includes incremental counters, insert clause
						// from delta to day for table AggregationRule is added.
						//
						// Create Day-Count aggregation rules for 'Calc Table' (TOTAL)
						final Aggregationrule countAggregationRule = new Aggregationrule(rockFactory, true);
						countAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYLEVEL);
						countAggregationRule.setVersionid(currentVersioning.getVersionid());
						countAggregationRule.setRuleid(ruleID++);
						countAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYLEVEL);
						countAggregationRule.setTarget_type(measurementname);
						countAggregationRule.setTarget_level(Constants.DAYLEVEL);
						countAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYLEVEL);
						countAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.COUNTLEVEL);
						countAggregationRule.setSource_type(measurementname);
						countAggregationRule.setSource_level(Constants.COUNTLEVEL);
						countAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.COUNTLEVEL);
						countAggregationRule.setRuletype(Constants.TOTALTYPE);
						countAggregationRule.setAggregationscope(Constants.DAYSCOPE);
						countAggregationRule.saveToDB();

					} else {
						// FD: If fact table does not include incremental counters, insert
						// clause from raw to day for table AggregationRule is added.
						//
						// Create Day-Raw aggregation rule (TOTAL)
						final Aggregationrule dayAggregationRule = new Aggregationrule(rockFactory, true);
						dayAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYLEVEL);
						dayAggregationRule.setVersionid(currentVersioning.getVersionid());
						dayAggregationRule.setRuleid(ruleID++);
						dayAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
								+ Constants.DAYLEVEL);
						dayAggregationRule.setTarget_type(measurementname);
						dayAggregationRule.setTarget_level(Constants.DAYLEVEL);
						dayAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYLEVEL);
						dayAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
								+ Constants.RAWLEVEL);
						dayAggregationRule.setSource_type(measurementname);
						dayAggregationRule.setSource_level(Constants.RAWLEVEL);
						dayAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.RAWLEVEL);
						dayAggregationRule.setRuletype(Constants.TOTALTYPE);
						dayAggregationRule.setAggregationscope(Constants.DAYSCOPE);
						dayAggregationRule.saveToDB();

					}
				}

				final String SONAGG = Constants.SONAGG;
				final String SON15AGG = Constants.SON15AGG;

				if (Utils.replaceNull(measurementtypeExt.getSonAgg()).intValue() == 1) {
					if (createCountTable) {
						final Aggregation countAggregation = new Aggregation(rockFactory, true);
						countAggregation.setAggregation(measurementname + "_" + Constants.COUNTLEVEL);
						countAggregation.setVersionid(currentVersioning.getVersionid());
						countAggregation.setAggregationtype(Constants.SONAGG);
						countAggregation.setAggregationscope(Constants.COUNTSCOPE);
						countAggregation.saveToDB();

					}

					final Aggregation dayAggregation = new Aggregation(rockFactory, true);
					dayAggregation.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + SONAGG);
					dayAggregation.setVersionid(currentVersioning.getVersionid());
					dayAggregation.setAggregationtype(SONAGG);
					dayAggregation.setAggregationscope(Constants.DAYSCOPE);
					dayAggregation.saveToDB();

					if (createCountTable) {
						final Aggregationrule countAggregationRule = new Aggregationrule(rockFactory, true);
						countAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + SONAGG);
						countAggregationRule.setVersionid(currentVersioning.getVersionid());
						countAggregationRule.setRuleid(ruleID++);
						countAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + SONAGG);
						countAggregationRule.setTarget_type(measurementname);
						countAggregationRule.setTarget_level(Constants.SONAGG);
						countAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR + SONAGG);
						countAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.COUNTLEVEL);
						countAggregationRule.setSource_type(measurementname);
						countAggregationRule.setSource_level(Constants.COUNTLEVEL);
						countAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.COUNTLEVEL);
						countAggregationRule.setRuletype(Constants.SONAGG);
						countAggregationRule.setAggregationscope(Constants.DAYSCOPE);
						countAggregationRule.saveToDB();
					} else {
						final Aggregationrule dayAggregationRule = new Aggregationrule(rockFactory, true);
						dayAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + SONAGG);
						dayAggregationRule.setVersionid(currentVersioning.getVersionid());
						dayAggregationRule.setRuleid(ruleID++);
						dayAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
								+ SONAGG);
						dayAggregationRule.setTarget_type(measurementname);
						dayAggregationRule.setTarget_level(Constants.SONAGG);
						dayAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR + SONAGG);
						dayAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
								+ Constants.RAWLEVEL);
						dayAggregationRule.setSource_type(measurementname);
						dayAggregationRule.setSource_level(Constants.RAWLEVEL);
						dayAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.RAWLEVEL);
						dayAggregationRule.setRuletype(Constants.SONAGG);
						dayAggregationRule.setAggregationscope(Constants.DAYSCOPE);
						dayAggregationRule.saveToDB();
					}

				}

				if (Utils.replaceNull(measurementtypeExt.getSonFifteenMinAgg()).intValue() == 1) {
					if (createCountTable) {
						final Aggregation countAggregation = new Aggregation(rockFactory, true);
						countAggregation.setAggregation(measurementname + "_" + Constants.COUNTLEVEL);
						countAggregation.setVersionid(currentVersioning.getVersionid());
						countAggregation.setAggregationtype(Constants.SON15AGG);
						countAggregation.setAggregationscope(Constants.COUNTSCOPE);
						countAggregation.saveToDB();

					}

					final Aggregation dayAggregation = new Aggregation(rockFactory, true);
					dayAggregation.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + SON15AGG);
					dayAggregation.setVersionid(currentVersioning.getVersionid());
					dayAggregation.setAggregationtype(SON15AGG);
					dayAggregation.setAggregationscope(Constants.ROPAGGSCOPE);
					dayAggregation.saveToDB();

					if (createCountTable) {
						final Aggregationrule countAggregationRule = new Aggregationrule(rockFactory, true);
						countAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + SON15AGG);
						countAggregationRule.setVersionid(currentVersioning.getVersionid());
						countAggregationRule.setRuleid(ruleID++);
						countAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + SONAGG);
						countAggregationRule.setTarget_type(measurementname);
						countAggregationRule.setTarget_level(Constants.SON15AGG);
						countAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR + SON15AGG);
						countAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.COUNTLEVEL);
						countAggregationRule.setSource_type(measurementname);
						countAggregationRule.setSource_level(Constants.COUNTLEVEL);
						countAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.COUNTLEVEL);
						countAggregationRule.setRuletype(Constants.SON15AGG);
						countAggregationRule.setAggregationscope(Constants.ROPAGGSCOPE);
						countAggregationRule.saveToDB();
					} else {
						final Aggregationrule dayAggregationRule = new Aggregationrule(rockFactory, true);
						dayAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR + SON15AGG);
						dayAggregationRule.setVersionid(currentVersioning.getVersionid());
						dayAggregationRule.setRuleid(ruleID++);
						dayAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
								+ SON15AGG);
						dayAggregationRule.setTarget_type(measurementname);
						dayAggregationRule.setTarget_level(Constants.SON15AGG);
						dayAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR + SON15AGG);
						dayAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
								+ Constants.PLAINLEVEL);
						dayAggregationRule.setSource_type(measurementname);
						dayAggregationRule.setSource_level(Constants.PLAINLEVEL);
						dayAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ com.ericsson.eniq.common.Constants.SONVISTEMPTABLE);
						dayAggregationRule.setRuletype(Constants.SON15AGG);
						dayAggregationRule.setAggregationscope(Constants.ROPAGGSCOPE);
						dayAggregationRule.saveToDB();
					}

				}

				// Create aggregations and rules for busy hour support. Iterate through
				// all busy hour support objects and create a rules for them.
				//
				// FD: If fact table includes busy hour aggregation, insert clause for
				// busy hour aggregations to table Aggregation in day, week and month
				// level are added for each busy hour object.
				//
				for (final Object tmpobject : measurementtypeExt.getObjBHSupport()) {
					if (tmpobject instanceof Measurementobjbhsupport) {
						final Measurementobjbhsupport measurementobjbhsupport = (Measurementobjbhsupport) tmpobject;
						final String bhtype = measurementobjbhsupport.getObjbhsupport();

						// Create DayBH aggregation
						final Aggregation dayBHAggregation = new Aggregation(rockFactory, true);
						dayBHAggregation.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL + Constants.TYPENAMESEPARATOR + bhtype);
						dayBHAggregation.setVersionid(currentVersioning.getVersionid());
						dayBHAggregation.setAggregationtype(Constants.DAYBHLEVEL);
						dayBHAggregation.setAggregationscope(Constants.DAYSCOPE);
						dayBHAggregation.saveToDB();

						// Check if table is a 'Calc Table' or other. Create DayBH-Count
						// rule for 'Calc Table' and DayBH-Raw for others.
						if (createCountTable) {
							// FD: If fact table includes incremental counters and object busy
							// hour support, insert clause from delta to day busy hour for
							// table AggregationRule is added.
							//
							// Create DayBH-Count aggregations rule (BHSRC)
							final Aggregationrule dayBHAggregationRule = new Aggregationrule(rockFactory, true);
							dayBHAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
									+ Constants.DAYBHLEVEL + Constants.TYPENAMESEPARATOR + bhtype);
							dayBHAggregationRule.setVersionid(currentVersioning.getVersionid());
							dayBHAggregationRule.setRuleid(ruleID++);
							dayBHAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
									+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
							dayBHAggregationRule.setTarget_type(measurementname);
							dayBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
							dayBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
									+ Constants.DAYBHLEVEL);
							dayBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
									+ Constants.TYPESEPARATOR + Constants.COUNTLEVEL);
							dayBHAggregationRule.setSource_type(measurementname);
							dayBHAggregationRule.setSource_level(Constants.COUNTLEVEL);
							dayBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
									+ Constants.COUNTLEVEL);
							dayBHAggregationRule.setRuletype(Constants.BHSRC);
							dayBHAggregationRule.setAggregationscope(Constants.DAYSCOPE);
							dayBHAggregationRule.saveToDB();
						} else {
							// FD: If fact table does not include incremental counters but
							// includes object busy hour support, insert clause from delta to
							// day busy hour for table AggregationRule is added.
							//
							// DayBH-Raw aggregation rule (BHSRC)
							final Aggregationrule dayBHAggregationRule = new Aggregationrule(rockFactory, true);
							dayBHAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
									+ Constants.DAYBHLEVEL + Constants.TYPENAMESEPARATOR + bhtype);
							dayBHAggregationRule.setVersionid(currentVersioning.getVersionid());
							dayBHAggregationRule.setRuleid(ruleID++);
							dayBHAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
									+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
							dayBHAggregationRule.setTarget_type(measurementname);
							dayBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
							dayBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
									+ Constants.DAYBHLEVEL);
							dayBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
									+ Constants.TYPESEPARATOR + Constants.RAWLEVEL);
							dayBHAggregationRule.setSource_type(measurementname);
							dayBHAggregationRule.setSource_level(Constants.RAWLEVEL);
							dayBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
									+ Constants.RAWLEVEL);
							dayBHAggregationRule.setRuletype(Constants.BHSRC);
							dayBHAggregationRule.setAggregationscope(Constants.DAYSCOPE);
							dayBHAggregationRule.saveToDB();
						}

						// Create WeekBH aggregation
						final Aggregation weekBHAggregation = new Aggregation(rockFactory, true);
						weekBHAggregation.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.WEEKBH + Constants.TYPENAMESEPARATOR + bhtype);
						weekBHAggregation.setVersionid(currentVersioning.getVersionid());
						weekBHAggregation.setAggregationtype(Constants.DAYBHLEVEL);
						weekBHAggregation.setAggregationscope(Constants.WEEKSCOPE);
						weekBHAggregation.saveToDB();

						// FD: If fact table includes object busy hour support, insert
						// clause from day busy hour to week busy hour for table
						// AggregationRule is added.
						//
						// Create WeekBH-DayBH aggregation rules (DAYBHCLASS)
						final Aggregationrule weekBHAggregationRule = new Aggregationrule(rockFactory, true);
						weekBHAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.WEEKBH + Constants.TYPENAMESEPARATOR + bhtype);
						weekBHAggregationRule.setVersionid(currentVersioning.getVersionid());
						weekBHAggregationRule.setRuleid(ruleID++);
						weekBHAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						weekBHAggregationRule.setTarget_type(measurementname);
						weekBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
						weekBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						weekBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						weekBHAggregationRule.setSource_type(measurementname);
						weekBHAggregationRule.setSource_level(Constants.DAYBHLEVEL);
						weekBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						weekBHAggregationRule.setRuletype(Constants.DAYBHCLASS + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						weekBHAggregationRule.setAggregationscope(Constants.WEEKSCOPE);
						weekBHAggregationRule.saveToDB();

						// Create MonthBH aggregation
						final Aggregation monthBHAggregation = new Aggregation(rockFactory, true);
						monthBHAggregation.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.MONTHBH + Constants.TYPENAMESEPARATOR + bhtype);
						monthBHAggregation.setVersionid(currentVersioning.getVersionid());
						monthBHAggregation.setAggregationtype(Constants.DAYBHLEVEL);
						monthBHAggregation.setAggregationscope(Constants.MONTHSCOPE);
						monthBHAggregation.saveToDB();

						// FD: If fact table includes object busy hour support, insert
						// clause from day busy hour to month busy hour for table
						// AggregationRule is added.
						//
						// Create MonthBH-DayBH aggregation rule (DAYBHCLASS)
						final Aggregationrule monthBHAggregationRule = new Aggregationrule(rockFactory, true);
						monthBHAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.MONTHBH + Constants.TYPENAMESEPARATOR + bhtype);
						monthBHAggregationRule.setVersionid(currentVersioning.getVersionid());
						monthBHAggregationRule.setRuleid(ruleID++);
						monthBHAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						monthBHAggregationRule.setTarget_type(measurementname);
						monthBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
						monthBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						monthBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						monthBHAggregationRule.setSource_type(measurementname);
						monthBHAggregationRule.setSource_level(Constants.DAYBHLEVEL);
						monthBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						monthBHAggregationRule.setRuletype(Constants.DAYBHCLASS + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						monthBHAggregationRule.setAggregationscope(Constants.MONTHSCOPE);
						monthBHAggregationRule.saveToDB();

						// TR HQ28244 Fix - Start
						// Create DayBH-RankBH aggregation
						// rule (RANKSRC)
						final Aggregationrule dayBHAggregationRule = new Aggregationrule(rockFactory, true);
						dayBHAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL + Constants.TYPENAMESEPARATOR + bhtype);
						dayBHAggregationRule.setVersionid(currentVersioning.getVersionid());
						dayBHAggregationRule.setRuleid(ruleID++);
						dayBHAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						dayBHAggregationRule.setTarget_type(measurementname);
						dayBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
						dayBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						dayBHAggregationRule.setSource_mtableid(currentVersioning.getVersionid()+ Constants.TYPESEPARATOR +vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE
								+ Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
						dayBHAggregationRule.setSource_type(vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE);
						dayBHAggregationRule.setSource_level(Constants.RANKBHLEVEL);
						dayBHAggregationRule.setSource_table(vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE + Constants.TYPENAMESEPARATOR
								+ Constants.RANKBHLEVEL);
						dayBHAggregationRule.setRuletype(Constants.RANKSRC);
						dayBHAggregationRule.setAggregationscope(Constants.DAYSCOPE);
						dayBHAggregationRule.saveToDB();

						// Create Week-RankBH aggregation
						// rule (DAYBHCLASS)
						final Aggregationrule weekBHAggregationRule1 = new Aggregationrule(rockFactory, true);
						weekBHAggregationRule1.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.WEEKBH + Constants.TYPENAMESEPARATOR + bhtype);
						weekBHAggregationRule1.setVersionid(currentVersioning.getVersionid());
						weekBHAggregationRule1.setRuleid(ruleID++);
						weekBHAggregationRule1.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						weekBHAggregationRule1.setTarget_type(measurementname);
						weekBHAggregationRule1.setTarget_level(Constants.DAYBHLEVEL);
						weekBHAggregationRule1.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						weekBHAggregationRule1.setSource_mtableid(currentVersioning.getVersionid()+ Constants.TYPESEPARATOR +vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE
								+ Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
						weekBHAggregationRule1.setSource_type(vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE);
						weekBHAggregationRule1.setSource_level(Constants.RANKBHLEVEL);
						weekBHAggregationRule1.setSource_table(vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE + Constants.TYPENAMESEPARATOR
								+ Constants.RANKBHLEVEL);
						weekBHAggregationRule1.setRuletype(Constants.DAYBHCLASS);
						weekBHAggregationRule1.setAggregationscope(Constants.WEEKSCOPE);
						weekBHAggregationRule1.saveToDB();

						// Create MonthBH-RankBH aggregation
						// rule (DAYBHCLASS)
						final Aggregationrule monthBHAggregationRule1 = new Aggregationrule(rockFactory, true);
						monthBHAggregationRule1.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.MONTHBH + Constants.TYPENAMESEPARATOR + bhtype);
						monthBHAggregationRule1.setVersionid(currentVersioning.getVersionid());
						monthBHAggregationRule1.setRuleid(ruleID++);
						monthBHAggregationRule1.setTarget_mtableid(measurementtypeExt.getTypeid()
								+ Constants.TYPESEPARATOR + Constants.DAYBHLEVEL);
						monthBHAggregationRule1.setTarget_type(measurementname);
						monthBHAggregationRule1.setTarget_level(Constants.DAYBHLEVEL);
						monthBHAggregationRule1.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
								+ Constants.DAYBHLEVEL);
						monthBHAggregationRule1.setSource_mtableid(currentVersioning.getVersionid()+ Constants.TYPESEPARATOR +vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE
								+ Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
						monthBHAggregationRule1.setSource_type(vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE);
						monthBHAggregationRule1.setSource_level(Constants.RANKBHLEVEL);
						monthBHAggregationRule1.setSource_table(vendorid + Constants.TYPENAMESEPARATOR + bhtype + Constants.BHSCOPE + Constants.TYPENAMESEPARATOR
								+ Constants.RANKBHLEVEL);
						monthBHAggregationRule1.setRuletype(Constants.DAYBHCLASS);
						monthBHAggregationRule1.setAggregationscope(Constants.MONTHSCOPE);
						monthBHAggregationRule1.saveToDB();
						// TR HQ28244 Fix - End
					}
				}
			}/*
			 * else { // Ranking Table is set. // // FD: If fact table includes object busy hour support, insert clause
			 * // from ranking table to busy hour table for table AggregationRule is // added. // // Create busy hour
			 * support aggregations rules for 'Ranking Table'. // // NOTE: The aggregations for these (ranking table)
			 * aggregation rules // are created by other measurement types with the same // "Object BH Support" values.
			 * for (final Iterator<Object> origiter = measurementtypeExt.getObjBHSupport().iterator();
			 * origiter.hasNext();) { final Object tmpobject0 = origiter.next(); if (tmpobject0 instanceof
			 * Measurementobjbhsupport) { final Measurementobjbhsupport measurementobjbhsupport1 =
			 * (Measurementobjbhsupport) tmpobject0; final String bhtype = measurementobjbhsupport1.getObjbhsupport();
			 * for (final Iterator<MeasurementtypeExt> alliter = allmeasurementtypes.iterator(); alliter.hasNext();) {
			 * final MeasurementtypeExt measurementtypeExt2 = alliter.next(); final String measurementname2 =
			 * measurementtypeExt2.getTypename(); if
			 * (Utils.replaceNull(measurementtypeExt2.getRankingtable()).intValue() == 0) { for (final Iterator<Object>
			 * compiter = measurementtypeExt2.getObjBHSupport().iterator(); compiter .hasNext();) { final Object
			 * tmpobject2 = compiter.next(); if (tmpobject2 instanceof Measurementobjbhsupport) { final
			 * Measurementobjbhsupport measurementobjbhsupport2 = (Measurementobjbhsupport) tmpobject2; if
			 * (measurementobjbhsupport2.getObjbhsupport().equals(measurementobjbhsupport1.getObjbhsupport())) {
			 * 
			 * // Create DayBH-RankBH aggregation rule (RANKSRC) final Aggregationrule dayBHAggregationRule = new
			 * Aggregationrule(rockFactory, true); dayBHAggregationRule.setAggregation(measurementname2 +
			 * Constants.TYPENAMESEPARATOR + Constants.DAYBHLEVEL + Constants.TYPENAMESEPARATOR + bhtype);
			 * dayBHAggregationRule.setVersionid(currentVersioning.getVersionid());
			 * dayBHAggregationRule.setRuleid(ruleID++);
			 * dayBHAggregationRule.setTarget_mtableid(measurementtypeExt2.getTypeid() + Constants.TYPESEPARATOR +
			 * Constants.DAYBHLEVEL); dayBHAggregationRule.setTarget_type(measurementname2);
			 * dayBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
			 * dayBHAggregationRule.setTarget_table(measurementname2 + Constants.TYPENAMESEPARATOR +
			 * Constants.DAYBHLEVEL); dayBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() +
			 * Constants.TYPESEPARATOR + Constants.RANKBHLEVEL); dayBHAggregationRule.setSource_type(measurementname);
			 * dayBHAggregationRule.setSource_level(Constants.RANKBHLEVEL);
			 * dayBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); dayBHAggregationRule.setRuletype(Constants.RANKSRC);
			 * dayBHAggregationRule.setAggregationscope(Constants.DAYSCOPE); dayBHAggregationRule.saveToDB();
			 * 
			 * // Create Week-RankBH aggregation rule (DAYBHCLASS) final Aggregationrule weekBHAggregationRule = new
			 * Aggregationrule(rockFactory, true); weekBHAggregationRule.setAggregation(measurementname2 +
			 * Constants.TYPENAMESEPARATOR + Constants.WEEKBH + Constants.TYPENAMESEPARATOR + bhtype);
			 * weekBHAggregationRule.setVersionid(currentVersioning.getVersionid());
			 * weekBHAggregationRule.setRuleid(ruleID++);
			 * weekBHAggregationRule.setTarget_mtableid(measurementtypeExt2.getTypeid() + Constants.TYPESEPARATOR +
			 * Constants.DAYBHLEVEL); weekBHAggregationRule.setTarget_type(measurementname2);
			 * weekBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
			 * weekBHAggregationRule.setTarget_table(measurementname2 + Constants.TYPENAMESEPARATOR +
			 * Constants.DAYBHLEVEL); weekBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() +
			 * Constants.TYPESEPARATOR + Constants.RANKBHLEVEL); weekBHAggregationRule.setSource_type(measurementname);
			 * weekBHAggregationRule.setSource_level(Constants.RANKBHLEVEL);
			 * weekBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); weekBHAggregationRule.setRuletype(Constants.DAYBHCLASS);
			 * weekBHAggregationRule.setAggregationscope(Constants.WEEKSCOPE); weekBHAggregationRule.saveToDB();
			 * 
			 * // Create MonthBH-RankBH aggregation rule (DAYBHCLASS) final Aggregationrule monthBHAggregationRule = new
			 * Aggregationrule(rockFactory, true); monthBHAggregationRule.setAggregation(measurementname2 +
			 * Constants.TYPENAMESEPARATOR + Constants.MONTHBH + Constants.TYPENAMESEPARATOR + bhtype);
			 * monthBHAggregationRule.setVersionid(currentVersioning.getVersionid());
			 * monthBHAggregationRule.setRuleid(ruleID++);
			 * monthBHAggregationRule.setTarget_mtableid(measurementtypeExt2.getTypeid() + Constants.TYPESEPARATOR +
			 * Constants.DAYBHLEVEL); monthBHAggregationRule.setTarget_type(measurementname2);
			 * monthBHAggregationRule.setTarget_level(Constants.DAYBHLEVEL);
			 * monthBHAggregationRule.setTarget_table(measurementname2 + Constants.TYPENAMESEPARATOR +
			 * Constants.DAYBHLEVEL); monthBHAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() +
			 * Constants.TYPESEPARATOR + Constants.RANKBHLEVEL); monthBHAggregationRule.setSource_type(measurementname);
			 * monthBHAggregationRule.setSource_level(Constants.RANKBHLEVEL);
			 * monthBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); monthBHAggregationRule.setRuletype(Constants.DAYBHCLASS);
			 * monthBHAggregationRule.setAggregationscope(Constants.MONTHSCOPE); monthBHAggregationRule.saveToDB(); } }
			 * } } }
			 * 
			 * Commented out... No, No, No! We should not create the RANBH aggregations and rules here. They are created
			 * in the BH model, when measurement type model has been saved.
			 * 
			 * 
			 * // Generate RANKBH empty place holders List<String> list = new ArrayList<String>();
			 * 
			 * // product place holders for (int i = 0; i < measurementtypeExt.getBHProductPlaceholders(); i++) {
			 * list.add(Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX + i); }
			 * 
			 * // Custom place holders for (int i = 0; i < measurementtypeExt.getBHCustomPlaceholders(); i++) {
			 * list.add(Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX + i); }
			 * 
			 * for (int i = 0; i < list.size(); i++) {
			 * 
			 * // Create Rank place holders (Aggregation) final Aggregation rankBHAggregation = new
			 * Aggregation(rockFactory, true); rankBHAggregation.setAggregation(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.RANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * rankBHAggregation.setVersionid(currentVersioning.getVersionid());
			 * rankBHAggregation.setAggregationtype(Constants.DAYBHLEVEL);
			 * rankBHAggregation.setAggregationscope(Constants.DAYSCOPE); rankBHAggregation.saveToDB();
			 * 
			 * // Create WeekRankBH place holders (Aggregation) final Aggregation weekRankBHAggregation = new
			 * Aggregation(rockFactory, true); weekRankBHAggregation.setAggregation(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.WEEKRANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * weekRankBHAggregation. setVersionid(currentVersioning.getVersionid());
			 * weekRankBHAggregation.setAggregationtype(Constants.DAYBHLEVEL);
			 * weekRankBHAggregation.setAggregationscope(Constants.WEEKSCOPE); weekRankBHAggregation.saveToDB();
			 * 
			 * // Create MonthRankBH place holders (Aggregation) final Aggregation monthRankBHAggregation = new
			 * Aggregation(rockFactory, true); monthRankBHAggregation.setAggregation(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.MONTHRANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * monthRankBHAggregation .setVersionid(currentVersioning.getVersionid());
			 * monthRankBHAggregation.setAggregationtype(Constants.DAYBHLEVEL);
			 * monthRankBHAggregation.setAggregationscope(Constants.MONTHSCOPE); monthRankBHAggregation.saveToDB();
			 * 
			 * // Create Rank place holders (Aggregationrule) final Aggregationrule rankBHAggregationRule = new
			 * Aggregationrule(rockFactory, true); rankBHAggregationRule.setAggregation(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.RANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * rankBHAggregationRule. setVersionid(currentVersioning.getVersionid());
			 * rankBHAggregationRule.setRuleid(ruleID++); rankBHAggregationRule.setTarget_mtableid
			 * (measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
			 * rankBHAggregationRule.setTarget_type(measurementname);
			 * rankBHAggregationRule.setTarget_level(Constants.RANKBHLEVEL);
			 * rankBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); rankBHAggregationRule.setSource_mtableid(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.RANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * rankBHAggregationRule.setSource_type(measurementname);
			 * rankBHAggregationRule.setSource_level(Constants.RAWLEVEL);
			 * rankBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); rankBHAggregationRule.setRuletype(Constants.RANKBHLEVEL);
			 * rankBHAggregationRule.setAggregationscope(Constants.DAYSCOPE); rankBHAggregationRule.saveToDB();
			 * 
			 * // Create WeekRankBH place holders (Aggregationrule) final Aggregationrule weekRankBHAggregationRule =
			 * new Aggregationrule(rockFactory, true); weekRankBHAggregationRule.setAggregation(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.WEEKRANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * weekRankBHAggregationRule .setVersionid(currentVersioning.getVersionid());
			 * weekRankBHAggregationRule.setRuleid(ruleID++); weekRankBHAggregationRule
			 * .setTarget_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
			 * weekRankBHAggregationRule.setTarget_type(measurementname);
			 * weekRankBHAggregationRule.setTarget_level(Constants.RANKBHLEVEL);
			 * weekRankBHAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); weekRankBHAggregationRule .setSource_mtableid(measurementtypeExt.getTypeid() +
			 * Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
			 * weekRankBHAggregationRule.setSource_type(measurementname);
			 * weekRankBHAggregationRule.setSource_level(Constants.RANKBHLEVEL);
			 * weekRankBHAggregationRule.setSource_table(measurementname + Constants.TYPENAMESEPARATOR +
			 * Constants.RANKBHLEVEL); weekRankBHAggregationRule.setRuletype(Constants.RANKBHCLASS);
			 * weekRankBHAggregationRule .setAggregationscope(Constants.WEEKSCOPE);
			 * weekRankBHAggregationRule.saveToDB();
			 * 
			 * // Create MonthRankBH place holders // (Aggregationrule) final Aggregationrule monthRankBHAggregationRule
			 * = new Aggregationrule(rockFactory, true); monthRankBHAggregationRule.setAggregation(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.MONTHRANKBHLEVEL + Constants.TYPENAMESEPARATOR + list.get(i));
			 * monthRankBHAggregationRule .setVersionid(currentVersioning.getVersionid());
			 * monthRankBHAggregationRule.setRuleid(ruleID++); monthRankBHAggregationRule
			 * .setTarget_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
			 * monthRankBHAggregationRule.setTarget_type(measurementname); monthRankBHAggregationRule
			 * .setTarget_level(Constants.RANKBHLEVEL); monthRankBHAggregationRule.setTarget_table(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.RANKBHLEVEL); monthRankBHAggregationRule
			 * .setSource_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR + Constants.RANKBHLEVEL);
			 * monthRankBHAggregationRule.setSource_type(measurementname); monthRankBHAggregationRule
			 * .setSource_level(Constants.RANKBHLEVEL); monthRankBHAggregationRule.setSource_table(measurementname +
			 * Constants.TYPENAMESEPARATOR + Constants.RANKBHLEVEL);
			 * monthRankBHAggregationRule.setRuletype(Constants.RANKBHCLASS); monthRankBHAggregationRule
			 * .setAggregationscope(Constants.MONTHSCOPE); monthRankBHAggregationRule.saveToDB();
			 * 
			 * } } } }
			 */

			// FD: If fact table includes incremental counters, insert clause from raw
			// to delta for table AggregationRule is added.
			//
			// Create Count-Raw aggregation rule for 'Calc Table'.
			if (createCountTable) {
				final Aggregationrule countAggregationRule = new Aggregationrule(rockFactory, true);
				countAggregationRule.setAggregation(measurementname + Constants.TYPENAMESEPARATOR
						+ Constants.COUNTLEVEL);
				countAggregationRule.setVersionid(currentVersioning.getVersionid());
				countAggregationRule.setRuleid(ruleID++);
				countAggregationRule.setTarget_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
						+ Constants.COUNTLEVEL);
				countAggregationRule.setTarget_type(measurementname);
				countAggregationRule.setTarget_level(Constants.COUNTLEVEL);
				countAggregationRule.setTarget_table(measurementname + Constants.TYPENAMESEPARATOR
						+ Constants.COUNTLEVEL);
				countAggregationRule.setSource_mtableid(measurementtypeExt.getTypeid() + Constants.TYPESEPARATOR
						+ Constants.RAWLEVEL);
				countAggregationRule.setSource_type(measurementname);
				countAggregationRule.setSource_level(Constants.RAWLEVEL);
				countAggregationRule
						.setSource_table(measurementname + Constants.TYPENAMESEPARATOR + Constants.RAWLEVEL);
				countAggregationRule.setRuletype(Constants.COUNTLEVEL);
				countAggregationRule.setAggregationscope(Constants.DAYSCOPE);
				countAggregationRule.saveToDB();
			}

		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in createAggregationInformation: " + e.getMessage());
			ExceptionHandler.instance().handle(e);
			throw e;
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in createAggregationInformation");
			throw e;
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in createAggregationInformation");
			throw e;
		}

	}

	/**
	 * Returns the process instructions from a DataItem with the specified dataName belonging to a DataFormat with the
	 * specified typeId.
	 * 
	 * @param dataname
	 *            The dataName of the DataItem
	 * @param typeid
	 *            The typeId of the dataFormat
	 * @return process instruction(s) for the matching data item. Empty vector in case the are no matches.
	 * @throws Exception
	 */
	private Vector<String> checkProcessInstructions(final String dataname, final String typeid) throws Exception {

		final Vector<String> result = new Vector<String>();

		final Dataformat df = new Dataformat(rockFactory);
		df.setTypeid(typeid);
		final DataformatFactory dfF = new DataformatFactory(rockFactory, df);
		final Iterator<Dataformat> dfFI = dfF.get().iterator();
		while (dfFI.hasNext()) {
			final Dataformat dform = dfFI.next();

			final Dataitem di = new Dataitem(rockFactory);
			di.setDataformatid(dform.getDataformatid());
			di.setDataname(dataname);
			final DataitemFactory diF = new DataitemFactory(rockFactory, di);

			final Iterator<Dataitem> diFI = diF.get().iterator();
			while (diFI.hasNext()) {
				final Dataitem ditem = diFI.next();
				result.add(ditem.getProcess_instruction());
			}

		}

		return result;
	}

	/**
	 * Returns the dataId from a DataItem with the specified dataName belonging to a DataFormat with the specified
	 * typeId. This method is used in migrate when updating the dataId values for measurement keys, counters, and
	 * columns.
	 * 
	 * The data format and data item information is read only once from the DB to speed up migration.
	 * 
	 * @param dataname
	 *            The dataName of a DataItem
	 * @param typeid
	 *            The typeId of a DataFormat
	 * @param versionId
	 *            The versionId of the techpack.
	 * @return A dataId for the matching data item. Null in case the is no match.
	 * @throws Exception
	 */
	private String getDataIdFromDataFormat(final String dataname, final String typeid, final String versionId)
			throws Exception {

		// Check if the data formats and data items have already been read from the
		// database for this techpack. If not, then they are read.
		if (dataFormats == null) {
			logger.log(Level.FINEST, "Reading data format and data item information from the DB.");

			this.dataFormats = new Vector<Dataformat>();
			this.dataItems = new HashMap<String, Vector<Dataitem>>();

			final Dataformat dfCond = new Dataformat(rockFactory);
			dfCond.setVersionid(versionId);
			final DataformatFactory mcF = new DataformatFactory(rockFactory, dfCond);
			final Vector<Dataformat> dfVec = mcF.get();
			for (final Dataformat df : dfVec) {
				this.dataFormats.add(df);

				// Get the data items for this data format.
				final Dataitem di = new Dataitem(rockFactory);
				di.setDataformatid(df.getDataformatid());
				final DataitemFactory diF = new DataitemFactory(rockFactory, di, true);
				final Vector<Dataitem> dItems = diF.get();

				// Add the data items to the map.
				this.dataItems.put(df.getDataformatid(), dItems);

			}
		}

		// The data formats and data items have been cached from the DB.

		// Find the matching data id.
		//
		// Find the data format matching the given type id.
		final Dataformat df = new Dataformat(rockFactory);
		df.setTypeid(typeid);
		final DataformatFactory dfF = new DataformatFactory(rockFactory, df);
		final Iterator<Dataformat> dfFI = dfF.get().iterator();
		while (dfFI.hasNext()) {
			final Dataformat dform = dfFI.next();

			// Find the data item object with the given data name.
			final Dataitem di = new Dataitem(rockFactory);
			di.setDataformatid(dform.getDataformatid());
			di.setDataname(dataname);
			final DataitemFactory diF = new DataitemFactory(rockFactory, di);
			final Iterator<Dataitem> diFI = diF.get().iterator();
			while (diFI.hasNext()) {
				final Dataitem ditem = diFI.next();

				// Matching data item found. Return it.
				return ditem.getDataid();
			}
		}

		// No matching data item found. Return null.
		return null;
	}

	/**
	 * Migrates the measurement type data to match the latest database structure.
	 * 
	 * @param versionid
	 * @param fromEniqLevel
	 * @throws Exception
	 */
	public void migrate(final String versionid, final String techpackType, final String fromEniqLevel) throws Exception {

		// Check the from version.
		if (!fromEniqLevel.equals("1.0")) {
			// No need for migrate.
			logger.log(Level.FINEST, "No need to migrate measurement types.");
			return;
		} else {
			try {
				// Iterate through all measurement types.
				final Vector<Measurementtype> mtypes = this.getMeasurementTypes(versionid);
				for (final Measurementtype mtype : mtypes) {
					final String typeid = mtype.getTypeid();

					logger.log(Level.FINEST, "Migrating measurement type: " + typeid);

					String sizing = "";
					int rankTable = 0;
					int plainTable = 0;
					int calcTable = 0;
					int vectorTable = 0;
					int totalAgg = 0;
					int sonAgg = 0;
					int elembh = 0;
					final Vector<String> mdcs = new Vector<String>();

					// Iterate through all measurement tables for this measurement type
					final Vector<Measurementtable> mtables = this.getTablesForMeasurementtype(mtype);
					for (final Measurementtable mtable : mtables) {
						// Get the table type (vector, rank, plain, or calc)
						if (mtable.getTypeid().endsWith("_V")) {
							vectorTable = 1;
						} else if (mtable.getTablelevel().equals(Constants.RANKBHLEVEL)) {
							rankTable = 1;
						} else if (mtable.getTablelevel().equals(Constants.PLAINLEVEL)) {
							plainTable = 1;
						} else if (mtable.getTablelevel().equals(Constants.COUNTLEVEL)) {
							calcTable = 1;
						}

						// Get the Total Aggregation
						if (mtable.getTablelevel().equals(Constants.DAYLEVEL)) {
							totalAgg = 1;
						}

						// Get the Total Aggregation
						if (mtable.getTablelevel().equals(Constants.SONAGG)) {
							sonAgg = 1;
						}

						// Get the Element BH Support
						if (mtable.getTablelevel().equals(Constants.RANKBHLEVEL)
								&& mtable.getTypeid().endsWith("_ELEMBH")) {
							elembh = 1;
						}

						// Get Sizing
						final String[] sizingItems = getSizingItems(techpackType);

						for (final String sizingItem : sizingItems) {
							if (mtable.getPartitionplan() != null) {
								if (mtable.getPartitionplan().startsWith(sizingItem)) {
									sizing = sizingItem;
									break;
								}
							}
						}

						// Iterate through all measurement columns for this measurement
						// table
						final Vector<Measurementcolumn> mcolumns = getColumnsForMeasurementtable(mtable);
						for (final Measurementcolumn column : mcolumns) {
							// Initialize the column type value to public key.
							String coltype = "PUBLICKEY";

							// Initialize the dataId to be the same as the dataName. It will
							// be set to a correct value for keys or counters from this
							// techpack (i.e. excluding the public keys)
							String dataId = column.getDataname();

							// Create a new measurement key object and set the TypeId and
							// Dataname values.
							final Measurementkey whereMeasurementkey = new Measurementkey(rockFactory);
							whereMeasurementkey.setTypeid(typeid);
							whereMeasurementkey.setDataname(column.getDataname());

							// Create a measurement key factory object for the key and get the
							// key(s) from it. If there is a key returned, then the current
							// measurement column is a measurement key column and the key data
							// will be set and then saved to the database.
							final MeasurementkeyFactory measurementkeyFactory = new MeasurementkeyFactory(rockFactory,
									whereMeasurementkey, true);
							final Vector<Measurementkey> mkeys = measurementkeyFactory.get();
							if (mkeys.size() > 0) {
								coltype = "KEY";
								final Measurementkey mkey = mkeys.get(0);
								// mkey.setDescription();
								// mkey.setIselement();
								// mkey.setUniquekey();
								mkey.setColnumber(Utils.replaceNull(column.getColnumber()));
								mkey.setDatatype(Utils.replaceNull(column.getDatatype()));
								mkey.setDatasize(Utils.replaceNull(column.getDatasize()));
								mkey.setDatascale(Utils.replaceNull(column.getDatascale()));
								mkey.setUniquevalue(Utils.replaceNull(column.getUniquevalue()));
								mkey.setNullable(Utils.replaceNull(column.getNullable()));
								mkey.setIndexes(Utils.replaceNull(column.getIndexes()));
								mkey.setIncludesql(Utils.replaceNull(column.getIncludesql()));
								// The univobject is intentionally initialized to be the same as
								// the dataname, since the value is not available in the
								// non-migrated techpacks.
								mkey.setUnivobject(Utils.replaceNull(column.getDataname()));
								// mkey.setJoinable();

								// Get the dataId for the key. If there is not matching dataId
								// and the table is not a ranking table, then log a warning.
								dataId = getDataIdFromDataFormat(column.getDataname(), mtable.getTypeid(), versionid);
								if ((dataId == null) && !mtable.getMtableid().endsWith("RANKBH")) {
									logger.log(Level.WARNING, "Migrating measurement type: " + typeid + ", table: "
											+ mtable.getMtableid() + ", column: " + column.getDataname()
											+ ": No matching dataId found from Dataitem table!");
								}
								mkey.setDataid(dataId);

								// Save measurement key data
								mkey.saveDB();
							}

							// Create a new measurement counter object and set the TypeId and
							// Dataname values.
							final Measurementcounter whereMeasurementcounter = new Measurementcounter(rockFactory);
							whereMeasurementcounter.setTypeid(typeid);
							whereMeasurementcounter.setDataname(column.getDataname());

							// Create a measurement counter factory object for the counter and
							// get the counter(s) from it. If there is a counter returned,
							// then the current measurement column is a measurement counter
							// column and the counter data will be set and then saved to the
							// database.
							final MeasurementcounterFactory measurementcounterFactory = new MeasurementcounterFactory(
									rockFactory, whereMeasurementcounter, true);
							final Vector<Measurementcounter> mcounters = measurementcounterFactory.get();
							if (mcounters.size() > 0) {
								coltype = "COUNTER";
								final Measurementcounter mcounter = mcounters.get(0);
								// mcounter.setDescription();
								// mcounter.setTimeaggregation();
								// mcounter.setGroupaggregation();
								// mcounter.setCountaggregation();
								mcounter.setColnumber(Utils.replaceNull(column.getColnumber()));
								mcounter.setDatatype(Utils.replaceNull(column.getDatatype()));
								mcounter.setDatasize(Utils.replaceNull(column.getDatasize()));
								mcounter.setDatascale(Utils.replaceNull(column.getDatascale()));
								mcounter.setIncludesql(Utils.replaceNull(column.getIncludesql()));
								// The univobject is intentionally initialized to be the same as
								// the dataname, since the value is not available in the
								// non-migrated techpacks.
								mcounter.setUnivobject(Utils.replaceNull(column.getDataname()));
								// mcounter.setUnivclass();

								// Get the dataId for the key. If there is not matching dataId
								// and the table is not a ranking table, then log a warning.
								dataId = getDataIdFromDataFormat(column.getDataname(), mtable.getTypeid(), versionid);
								if ((dataId == null) && !mtable.getMtableid().endsWith("RANKBH")) {
									logger.log(Level.WARNING, "Migrating measurement type: " + typeid + ", table: "
											+ mtable.getMtableid() + ", column: " + column.getDataname()
											+ ": No matching dataId found from Dataitem table!");
								}
								mcounter.setDataid(dataId);

								// Set the counter process and counter type values
								if (checkProcessInstructions(column.getDataname(), mtable.getTypeid()).contains(
										"UNIQUEVECTOR")) {
									mcounter.setCounterprocess("UNIQUEVECTOR");
									mcounter.setCountertype("UNIQUEVECTOR");
								
								} else if (checkProcessInstructions(column.getDataname(), mtable.getTypeid()).contains(
										"CMVECTOR")) {
									mcounter.setCounterprocess("CMVECTOR");
									mcounter.setCountertype("CMVECTOR");
								} else if (checkProcessInstructions(column.getDataname(), mtable.getTypeid()).contains(
										"VECTOR")) {
									mcounter.setCounterprocess("VECTOR");
									mcounter.setCountertype("VECTOR");
								} else if (vectorTable == 1) {
									mcounter.setCounterprocess("VECTOR");
									mcounter.setCountertype("VECTOR");
								} else if ("PEG".equals(mcounter.getCountaggregation())) {
									mcounter.setCounterprocess(mcounter.getCountaggregation());
									mcounter.setCountertype(mcounter.getCountaggregation());
								} else if ("GAUGE".equals(mcounter.getCountaggregation())) {
									mcounter.setCounterprocess(mcounter.getCountaggregation());
									mcounter.setCountertype(mcounter.getCountaggregation());
								} else {
									try {
										if (mcounter.getCountaggregation().length() > 0) {
											final List<CountAggregationVendorReleases> cas = decodeCounteragg(mcounter
													.getCountaggregation());
											mcounter.setCounterprocess("PEG");
											mcounter.setCountertype("PEG");
											final Vector<String> tpvr = this.getVendorReleases(versionid);
											final List<String> mcvr = new ArrayList<String>();
											final List<String> calcIsOff = cas.get(1).getVendorReleases();
											mcvr.addAll(cas.get(0).getVendorReleases());
											mcvr.addAll(cas.get(1).getVendorReleases());
											for (final String vendorrelease : mcvr) {
												if (!tpvr.contains(vendorrelease)) {
													final Supportedvendorrelease svr = new Supportedvendorrelease(
															rockFactory, true);
													svr.setVersionid(versionid);
													svr.setVendorrelease(vendorrelease);
													svr.saveToDB();
													tpvr.add(vendorrelease);
												}
												if (calcTable == 1) {
													// update deltacalcsupport table with non default
													// values
													if (calcIsOff.contains(vendorrelease)) {
														// all counters contain same values so only one row
														// vendor release need to be done
														if (!mdcs.contains(vendorrelease)) {
															final Measurementdeltacalcsupport mvr = new Measurementdeltacalcsupport(
																	rockFactory, true);
															mvr.setTypeid(mtype.getTypeid());
															mvr.setVersionid(versionid);
															mvr.setVendorrelease(vendorrelease);
															mvr.setDeltacalcsupport(new Integer(0));
															mvr.saveToDB();
															mdcs.add(vendorrelease);
														}
													}
												}
											}
										}
									} catch (final InvalidCountAggregationFormat e) {
										logger.warning(mtype.getTypename() + " " + mcounter.getDataname()
												+ " counter type can not be resolved");
									}
								}

								// Save measurement counter data
								mcounter.saveDB();
							}

							// Update the dataId for the column
							column.setDataid(dataId);

							// Set the column type (key/counter) for the measurement column
							column.setColtype(coltype);

							// Save the measurement column data.
							column.saveDB();
						}
					}

					final String measurementname = mtype.getFoldername();
					if (calcTable == 1) {

						// UniverseClass add-on
						Universeclass unvclass = new Universeclass(rockFactory, true);
						unvclass.setVersionid(mtype.getVersionid());
						unvclass.setClassname(measurementname + "_Keys");
						unvclass.setUniverseextension("ALL");
						unvclass.setDescription("Keys for " + measurementname);
						unvclass.setParent(measurementname);
						unvclass.setObj_bh_rel(0);
						unvclass.setElem_bh_rel(0);
						unvclass.setInheritance(0);
						unvclass.setOrdernro(Long.MAX_VALUE);
						unvclass.saveDB();

						unvclass = new Universeclass(rockFactory, true);
						unvclass.setVersionid(mtype.getVersionid());
						unvclass.setClassname(measurementname + "_RAW_Keys");
						unvclass.setUniverseextension("ALL");
						unvclass.setDescription("RAW keys for " + measurementname);
						unvclass.setParent(measurementname);
						unvclass.setObj_bh_rel(0);
						unvclass.setElem_bh_rel(0);
						unvclass.setInheritance(0);
						unvclass.setOrdernro(Long.MAX_VALUE);
						unvclass.saveDB();
					} else {
						// UniverseClass add-on
						final Universeclass unvclass = new Universeclass(rockFactory, true);
						unvclass.setVersionid(mtype.getVersionid());
						unvclass.setClassname(measurementname + "_Keys");
						unvclass.setUniverseextension("ALL");
						unvclass.setDescription("Keys for " + measurementname);
						unvclass.setParent(measurementname);
						unvclass.setObj_bh_rel(0);
						unvclass.setElem_bh_rel(0);
						unvclass.setInheritance(0);
						unvclass.setOrdernro(Long.MAX_VALUE);
						unvclass.saveDB();
					}

					// Set the measurement type values
					mtype.setRankingtable(new Integer(rankTable));
					mtype.setPlaintable(new Integer(plainTable));
					mtype.setDeltacalcsupport(new Integer(calcTable));
					mtype.setVectorsupport(new Integer(vectorTable));
					mtype.setTotalagg(new Integer(totalAgg));
					mtype.setSonagg(new Integer(sonAgg));
					mtype.setElementbhsupport(new Integer(elembh));
					mtype.setDataformatsupport(new Integer(1));
					mtype.setUniverseextension("ALL");
					if (sizing.length() > 0) {
						mtype.setSizing(sizing);
					} else {
						logger.warning(mtype.getTypename() + " sizing can not be resolved");
					}

					// Save measurement type data
					mtype.saveDB();
				}
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, "SQL error in migrate");
				ExceptionHandler.instance().handle(e);
				throw e;
			} catch (final RockException e) {
				logger.log(Level.SEVERE, "ROCK error in migrate");
				throw e;
			} catch (final Exception e) {
				logger.log(Level.SEVERE, "FATAL error in migrate");
				throw e;
			}
		}
	}

	protected String[] getSizingItems(final String techpackType) {
		if (techpackType.equals(Constants.ENIQ_EVENT)) {
			return Constants.EVENTSIZINGITEMS;
		}
		return Constants.SIZINGITEMS;
	}

	private final static Comparator<Measurementtype> MEASUREMENTTYPECOMPARATOR = new Comparator<Measurementtype>() {

		@Override
		public int compare(final Measurementtype d1, final Measurementtype d2) {

			final String s1 = Utils.replaceNull(d1.getTypename());
			final String s2 = Utils.replaceNull(d2.getTypename());
			return s1.compareTo(s2);
		}
	};

	private final static Comparator<Measurementkey> MEASUREMENTKEYCOMPARATOR = new Comparator<Measurementkey>() {

		@Override
		public int compare(final Measurementkey d1, final Measurementkey d2) {

			final Long i1 = Utils.replaceNull(d1.getColnumber()).longValue();
			final Long i2 = Utils.replaceNull(d2.getColnumber()).longValue();
			return i1.compareTo(i2);
		}
	};

	private final static Comparator<MeasurementcounterExt> MEASUREMENTCOUNTEREXTCOMPARATOR = new Comparator<MeasurementcounterExt>() {

		@Override
		public int compare(final MeasurementcounterExt d1, final MeasurementcounterExt d2) {

			final Long i1 = Utils.replaceNull(d1.getColnumber()).longValue();
			final Long i2 = Utils.replaceNull(d2.getColnumber()).longValue();
			return i1.compareTo(i2);
		}
	};

	private final static Comparator<Measurementcolumn> MEASUREMENTCOLUMNCOMPARATOR = new Comparator<Measurementcolumn>() {

		@Override
		public int compare(final Measurementcolumn d1, final Measurementcolumn d2) {

			final Long i1 = Utils.replaceNull(d1.getColnumber()).longValue();
			final Long i2 = Utils.replaceNull(d2.getColnumber()).longValue();
			return i1.compareTo(i2);
		}
	};

	private final static Comparator<Measurementdeltacalcsupport> MEASUREMENTDELTACALCSUPPORTCOMPARATOR = new Comparator<Measurementdeltacalcsupport>() {

		@Override
		public int compare(final Measurementdeltacalcsupport d1, final Measurementdeltacalcsupport d2) {

			final String s1 = Utils.replaceNull(d1.getVendorrelease());
			final String s2 = Utils.replaceNull(d2.getVendorrelease());
			return s1.compareTo(s2);
		}
	};

	private final static Comparator<Measurementvector> MEASUREMENTVECTORCOMPARATOR = new Comparator<Measurementvector>() {

		@Override
		public int compare(final Measurementvector d1, final Measurementvector d2) {

			final String s1 = Utils.replaceNull(d1.getVendorrelease());
			final String s2 = Utils.replaceNull(d2.getVendorrelease());
			int result = s1.compareTo(s2);
			if (result == 0) {
				final Long i1 = Utils.replaceNull(d1.getVindex()).longValue();
				final Long i2 = Utils.replaceNull(d2.getVindex()).longValue();
				result = i1.compareTo(i2);
			}
			return result;
		}
	};

	/**
	 * Returns a list of measurement keys for measurementtype
	 * 
	 * @param parentType
	 *            the parent of the keys
	 * @return results a list of Measurementkeys
	 */
	private Vector<Measurementkey> getKeysForMeasurementtype(final Measurementtype measurementtype) {
		final Vector<Measurementkey> results = new Vector<Measurementkey>();

		final Measurementkey whereMeasurementkey = new Measurementkey(rockFactory);
		whereMeasurementkey.setTypeid(measurementtype.getTypeid());
		try {
			final MeasurementkeyFactory measurementkeyFactory = new MeasurementkeyFactory(rockFactory,
					whereMeasurementkey, true);
			final Vector<Measurementkey> sortMeasurementkey = measurementkeyFactory.get();
			Collections.sort(sortMeasurementkey, MEASUREMENTKEYCOMPARATOR);
			for (final Measurementkey measurementkey : sortMeasurementkey) {
				results.add(measurementkey);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getKeysForMeasurementtype", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getKeysForMeasurementtype", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getKeysForMeasurementtype", e);
		}
		return results;
	}

	/**
	 * Returns a list of public keys for measurementtype
	 * 
	 * @param tablelevel
	 * 
	 * @param parentType
	 *            the parent of the keys
	 * @return results a list of Measurementkeys
	 */
	public Vector<Measurementkey> getPublicKeysForMeasurementtype(final String tablelevel) {
		final Vector<Measurementkey> results = new Vector<Measurementkey>();

		final String typename = tablelevel;
		final String mtbaleid = Utils.encodeTypeid(typename, baseVersioning.getVersionid());
		final Measurementcolumn whereMeasurementcolumn = new Measurementcolumn(rockFactory);
		whereMeasurementcolumn.setMtableid(mtbaleid);
		try {
			final MeasurementcolumnFactory measurementcolumnFactory = new MeasurementcolumnFactory(rockFactory,
					whereMeasurementcolumn, true);

			final Vector<Measurementkey> sortMeasurementkey = new Vector<Measurementkey>();

			final Iterator<Measurementcolumn> colI = measurementcolumnFactory.get().iterator();
			while (colI.hasNext()) {
				final Measurementcolumn mcol = colI.next();

				final Measurementkey newKey = new Measurementkey(rockFactory);

				newKey.setTypeid("");
				newKey.setDataname(mcol.getDataname());
				newKey.setDescription(mcol.getDescription());
				newKey.setUniquekey(mcol.getUniquekey());
				newKey.setIselement(0);
				newKey.setColnumber(mcol.getColnumber());
				newKey.setDatatype(mcol.getDatatype());
				newKey.setDatasize(mcol.getDatasize());
				newKey.setDatascale(mcol.getDatascale());
				newKey.setUniquevalue(mcol.getUniquevalue());
				newKey.setNullable(mcol.getNullable());
				newKey.setIndexes(mcol.getIndexes());
				newKey.setIncludesql(0);
				newKey.setUnivobject("");
				newKey.setJoinable(0);

				sortMeasurementkey.add(newKey);
			}

			Collections.sort(sortMeasurementkey, MEASUREMENTKEYCOMPARATOR);
			for (final Measurementkey measurementkey : sortMeasurementkey) {
				results.add(measurementkey);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getPublicKeysForMeasurementtype", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getPublicKeysForMeasurementtype", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getPublicKeysForMeasurementtype", e);
		}
		return results;
	}

	/*
	 * private Vector<Measurementkey> getPublicKeysForMeasurementtype(final String tablelevel) { final
	 * Vector<Measurementkey> results = new Vector<Measurementkey>();
	 * 
	 * final String typename = baseVersioning.getTechpack_name() + Constants.TYPENAMESEPARATOR + tablelevel; final
	 * String typeid = Utils.encodeTypeid(typename, baseVersioning.getVersionid()); final Measurementkey
	 * whereMeasurementkey = new Measurementkey(rockFactory); whereMeasurementkey.setTypeid(typeid); try { final
	 * MeasurementkeyFactory measurementkeyFactory = new MeasurementkeyFactory(rockFactory, whereMeasurementkey, true);
	 * final Vector<Measurementkey> sortMeasurementkey = measurementkeyFactory.get();
	 * Collections.sort(sortMeasurementkey, MEASUREMENTKEYCOMPARATOR); for (final Iterator<Measurementkey> sortiter =
	 * sortMeasurementkey.iterator(); sortiter.hasNext();) { results.add(sortiter.next()); } } catch (SQLException e) {
	 * logger.log(Level.SEVERE, "SQL error in getPublicKeysForMeasurementtype", e);
	 * ExceptionHandler.instance().handle(e); } catch (RockException e) { logger.log(Level.SEVERE,
	 * "ROCK error in getPublicKeysForMeasurementtype", e); } catch (Exception e) { logger.log(Level.SEVERE,
	 * "FATAL error in getPublicKeysForMeasurementtype", e); } return results; }
	 */

	/**
	 * Returns a list of measurement counters for measurementtype
	 * 
	 * @param measurementtype
	 *            the parent of the counters
	 * @return results a list of Measurementcounters
	 */
	private Vector<MeasurementcounterExt> getCountersForMeasurementtype(final Measurementtype measurementtype) {
		final Vector<MeasurementcounterExt> results = new Vector<MeasurementcounterExt>();

		final Measurementcounter whereMeasurementcounter = new Measurementcounter(rockFactory);
		whereMeasurementcounter.setTypeid(measurementtype.getTypeid());
		try {
			final MeasurementcounterFactory measurementcounterFactory = new MeasurementcounterFactory(rockFactory,
					whereMeasurementcounter, true);
			final Vector<Measurementcounter> measurementcounters = measurementcounterFactory.get();
			for (final Measurementcounter mc : measurementcounters) {
				final Vector<Object> mv = getVectorsForMeasurementcounter(mc);
				final MeasurementcounterExt mce = new MeasurementcounterExt(mc, mv);
				results.add(mce);
			}
			Collections.sort(results, MEASUREMENTCOUNTEREXTCOMPARATOR);
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getCountersForMeasurementtype (counter)", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getCountersForMeasurementtype (counter)", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getCountersForMeasurementtype (counter)", e);
		}
		return results;
	}

	/**
	 * Returns a list of public counters for measurementtype
	 * 
	 * @param measurementtype
	 *            the parent of the counters
	 * @param tablelevel
	 *            level of the public measurementtype
	 * @return results a list of Measurementcounters
	 */
	public Vector<MeasurementcounterExt> getPublicCountersForMeasurementtype(final String tablelevel) {
		final Vector<MeasurementcounterExt> results = new Vector<MeasurementcounterExt>();

		final String typename = baseVersioning.getTechpack_name() + Constants.TYPENAMESEPARATOR + tablelevel;
		final String typeid = Utils.encodeTypeid(typename, baseVersioning.getVersionid());
		final Measurementcounter whereMeasurementcounter = new Measurementcounter(rockFactory);
		whereMeasurementcounter.setTypeid(typeid);
		try {
			final MeasurementcounterFactory measurementcounterFactory = new MeasurementcounterFactory(rockFactory,
					whereMeasurementcounter, true);
			final Vector<Measurementcounter> measurementcounters = measurementcounterFactory.get();
			for (final Measurementcounter mc : measurementcounters) {
				final Vector<Object> mv = getVectorsForMeasurementcounter(mc);
				final MeasurementcounterExt mce = new MeasurementcounterExt(mc, mv);
				results.add(mce);
			}
			Collections.sort(results, MEASUREMENTCOUNTEREXTCOMPARATOR);
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getCountersForMeasurementtype (counter)", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getCountersForMeasurementtype (counter)", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getCountersForMeasurementtype (counter)", e);
		}
		return results;
	}

	/**
	 * Returns a list of public measurement columns for the defined table level.
	 * 
	 * This method is needed, since there are no public keys/counters defined in the base techpack yet. Instead, there
	 * are only measurement columns available, and if the data is transferred via measurement key/counter objects, then
	 * some information is not available anymore when columns should be created.
	 * 
	 * @param tablelevel
	 *            level of the public measurementtype
	 * @return results a list of Measurementcolumns
	 */
	public Vector<Measurementcolumn> getPublicColumns(final String tablelevel) {
		final Vector<Measurementcolumn> results = new Vector<Measurementcolumn>();

		// Get all the measurement columns from the base techpack for the given
		// table level
		final Measurementcolumn whereMeasurementcolumn = new Measurementcolumn(rockFactory);
		whereMeasurementcolumn.setMtableid(baseVersioning.getVersionid() + Constants.TYPESEPARATOR + tablelevel);
		try {
			final MeasurementcolumnFactory mcF = new MeasurementcolumnFactory(rockFactory, whereMeasurementcolumn, true);
			final Vector<Measurementcolumn> measurementcolumns = mcF.get();
			final Iterator<Measurementcolumn> iter = measurementcolumns.iterator();
			while (iter.hasNext()) {
				results.add(iter.next());
			}

			// Sort the results
			Collections.sort(results, MEASUREMENTCOLUMNCOMPARATOR);

		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getCountersForMeasurementtype (counter)", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getCountersForMeasurementtype (counter)", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getCountersForMeasurementtype (counter)", e);
		}
		return results;
	}

	/**
	 * Returns a list of measurement deltacalcsupports for measurementtype. If measurementtype is null then default
	 * values will be returned.
	 * 
	 * @param measurementtype
	 *            the parent of the deltacalcsupports
	 * @return results a list of Measurementdeltacalcsupports
	 */
	public Vector<Object> getDeltaCalcSupportsForMeasurementtype(final Measurementtype measurementtype) {
		final Vector<Measurementdeltacalcsupport> results = new Vector<Measurementdeltacalcsupport>();
		String typeid = "";
		if (measurementtype != null) {
			typeid = measurementtype.getTypeid();
		}
		final Measurementdeltacalcsupport whereMeasurementdeltacalcsupport = new Measurementdeltacalcsupport(
				rockFactory);
		whereMeasurementdeltacalcsupport.setTypeid(typeid);
		for (final String vendorRelease : vendorReleases) {
			try {
				whereMeasurementdeltacalcsupport.setVendorrelease(vendorRelease);
				final MeasurementdeltacalcsupportFactory measurementdeltacalcsupportFactory = new MeasurementdeltacalcsupportFactory(
						rockFactory, whereMeasurementdeltacalcsupport, true);
				final Vector<Measurementdeltacalcsupport> measurementdeltacalcsupports = measurementdeltacalcsupportFactory
						.get();
				if (measurementdeltacalcsupports.size() > 0) {
					final Measurementdeltacalcsupport measurementdeltacalcsupport = measurementdeltacalcsupports
							.elementAt(0);
					results.add(measurementdeltacalcsupport);
				} else {
					final Measurementdeltacalcsupport measurementdeltacalcsupport = new Measurementdeltacalcsupport(
							rockFactory);
					measurementdeltacalcsupport.setTypeid(typeid);
					measurementdeltacalcsupport.setVendorrelease(vendorRelease);
					// false (=0) is default
					measurementdeltacalcsupport.setDeltacalcsupport(1);
					measurementdeltacalcsupport.setVersionid(currentVersioning.getVersionid());
					results.add(measurementdeltacalcsupport);
				}
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, "SQL error in getDeltaCalcSupportsForMeasurementtype", e);
				ExceptionHandler.instance().handle(e);
			} catch (final RockException e) {
				logger.log(Level.SEVERE, "ROCK error in getDeltaCalcSupportsForMeasurementtype", e);
			} catch (final Exception e) {
				logger.log(Level.SEVERE, "FATAL error in getDeltaCalcSupportsForMeasurementtype", e);
			}
		}
		Collections.sort(results, MEASUREMENTDELTACALCSUPPORTCOMPARATOR);
		final Vector<Object> sortedresults = new Vector<Object>();
		for (final Measurementdeltacalcsupport measurementdeltacalcsupport : results) {
			sortedresults.add(measurementdeltacalcsupport);
		}
		return sortedresults;
	}

	/**
	 * Returns a list of objbhsupports for measurementtype
	 * 
	 * @param measurementtype
	 *            the parent of the objbhsupports
	 * @return results a list of Measurementobjbhsupports
	 */
	private Vector<Object> getObjBHSupportsForMeasurementtype(final Measurementtype measurementtype) {
		final Vector<Object> results = new Vector<Object>();

		final Measurementobjbhsupport whereMeasurementobjbhsupport = new Measurementobjbhsupport(rockFactory);
		whereMeasurementobjbhsupport.setTypeid(measurementtype.getTypeid());
		try {
			final MeasurementobjbhsupportFactory measurementobjbhsupportFactory = new MeasurementobjbhsupportFactory(
					rockFactory, whereMeasurementobjbhsupport, true);
			final Vector<Measurementobjbhsupport> measurementobjbhsupport = measurementobjbhsupportFactory.get();
			for (final Measurementobjbhsupport measurementobjbhsupport2 : measurementobjbhsupport) {
				results.add(measurementobjbhsupport2);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getObjBHSupportsForMeasurementtype", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getObjBHSupportsForMeasurementtype", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getObjBHSupportsForMeasurementtype", e);
		}

		return results;
	}

	// private int getBHProductPlaceholdersForMeasurementtype(final
	// Measurementtype measurementtype) {
	//
	// int result = Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS;
	//
	// try {
	//
	// // String bhobj =
	// //
	// measurementtype.getTypename().substring(measurementtype.getVendorid().length()
	// // + 1);
	//
	// Busyhourplaceholders bph = new Busyhourplaceholders(rockFactory);
	// bph.setVersionid(measurementtype.getVersionid());
	// bph.setBhlevel(measurementtype.getTypename());
	// BusyhourplaceholdersFactory bphF = new
	// BusyhourplaceholdersFactory(rockFactory, bph);
	// result = bphF.getElementAt(0).getProductplaceholders();
	//
	// } catch (SQLException e) {
	// logger.log(Level.SEVERE,
	// "SQL error in getBHProductPlaceholdersForMeasurementtype", e);
	// ExceptionHandler.instance().handle(e);
	// } catch (RockException e) {
	// logger.log(Level.SEVERE,
	// "ROCK error in getBHProductPlaceholdersForMeasurementtype", e);
	// } catch (Exception e) {
	// // logger.log(Level.SEVERE,
	// // "FATAL error in getBHProductPlaceholdersForMeasurementtype", e);
	// }
	//
	// return result;
	// }

	// private int getBHCustomPlaceholdersForMeasurementtype(final Measurementtype
	// measurementtype) {
	//
	// int result = Constants.DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS;
	//
	// try {
	//
	// // String bhobj =
	// //
	// measurementtype.getTypename().substring(measurementtype.getVendorid().length()
	// // + 1);
	//
	// Busyhourplaceholders bph = new Busyhourplaceholders(rockFactory);
	// bph.setVersionid(measurementtype.getVersionid());
	// bph.setBhlevel(measurementtype.getTypename());
	// BusyhourplaceholdersFactory bphF = new
	// BusyhourplaceholdersFactory(rockFactory, bph);
	// result = bphF.getElementAt(0).getCustomplaceholders();
	//
	// } catch (SQLException e) {
	// logger.log(Level.SEVERE,
	// "SQL error in getBHCustomPlaceholdersForMeasurementtype", e);
	// ExceptionHandler.instance().handle(e);
	// } catch (RockException e) {
	// logger.log(Level.SEVERE,
	// "ROCK error in getBHCustomPlaceholdersForMeasurementtype", e);
	// } catch (Exception e) {
	// // logger.log(Level.SEVERE,
	// // "FATAL error in getBHCustomPlaceholdersForMeasurementtype", e);
	// }
	//
	// return result;
	// }

	/**
	 * Returns a list of tables for measurementtype
	 * 
	 * @param measurementtype
	 *            the parent of the tables
	 * @return results a list of Measurementtables
	 */
	private Vector<Measurementtable> getTablesForMeasurementtype(final Measurementtype measurementtype) {
		final Vector<Measurementtable> results = new Vector<Measurementtable>();

		final Measurementtable whereMeasurementtable = new Measurementtable(rockFactory);
		whereMeasurementtable.setTypeid(measurementtype.getTypeid());
		try {
			final MeasurementtableFactory measurementtableFactory = new MeasurementtableFactory(rockFactory,
					whereMeasurementtable, true);
			final Vector<Measurementtable> measurementtable = measurementtableFactory.get();
			for (final Measurementtable measurementtable2 : measurementtable) {
				results.add(measurementtable2);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getTablesForMeasurementtype", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getTablesForMeasurementtype", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getTablesForMeasurementtype", e);
		}

		return results;
	}

	/**
	 * Returns a list of columns for measurementtable
	 * 
	 * @param measurementtable
	 *            the parent of the columns
	 * @return results a list of Measurementcolumns
	 */
	private Vector<Measurementcolumn> getColumnsForMeasurementtable(final Measurementtable measurementtable) {
		final Vector<Measurementcolumn> results = new Vector<Measurementcolumn>();

		final Measurementcolumn whereMeasurementcolumn = new Measurementcolumn(rockFactory);
		whereMeasurementcolumn.setMtableid(measurementtable.getMtableid());
		try {
			final MeasurementcolumnFactory measurementcolumnFactory = new MeasurementcolumnFactory(rockFactory,
					whereMeasurementcolumn, true);
			final Vector<Measurementcolumn> measurementcolumn = measurementcolumnFactory.get();
			for (final Measurementcolumn measurementcolumn2 : measurementcolumn) {
				results.add(measurementcolumn2);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getColumnsForMeasurementtable", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getColumnsForMeasurementtable", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getColumnsForMeasurementtable", e);
		}

		return results;
	}

	/**
	 * Returns a list of measurement vectors for measurementcounter
	 * 
	 * @param measurementcounter
	 *            the parent of the vectors
	 * @return results a list of Measurementvectors
	 */
	private Vector<Object> getVectorsForMeasurementcounter(final Measurementcounter measurementcounter) {
		final Vector<Object> sortedresults = new Vector<Object>();
		final Measurementvector whereMeasurementvector = new Measurementvector(rockFactory);
		whereMeasurementvector.setTypeid(measurementcounter.getTypeid());
		whereMeasurementvector.setDataname(measurementcounter.getDataname());
		try {
			final MeasurementvectorFactory measurementvectorFactory = new MeasurementvectorFactory(rockFactory,
					whereMeasurementvector, true);
			final Vector<Measurementvector> measurementvector = measurementvectorFactory.get();
			Collections.sort(measurementvector, MEASUREMENTVECTORCOMPARATOR);
			for (final Measurementvector measurementvector2 : measurementvector) {
				sortedresults.add(measurementvector2);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getVectorsForMeasurementcounter", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getVectorsForMeasurementcounter", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getVectorsForMeasurementcounter", e);
		}
		return sortedresults;
	}

	/**
	 * Returns a measurement types class of the current TP
	 * 
	 * @return TypeClass Measurementtypeclass
	 */
	private List<Measurementtypeclass> getClassesForVersioning(final Versioning versioning) {

		final List<Measurementtypeclass> results = new ArrayList<Measurementtypeclass>();
		final Measurementtypeclass whereMeasurementtypeclass = new Measurementtypeclass(rockFactory);
		whereMeasurementtypeclass.setVersionid(versioning.getVersionid());
		try {
			final MeasurementtypeclassFactory measurementtypeclassFactory = new MeasurementtypeclassFactory(
					rockFactory, whereMeasurementtypeclass, true);
			final Vector<Measurementtypeclass> mtc = measurementtypeclassFactory.get();
			for (final Measurementtypeclass measurementtypeclass : mtc) {
				results.add(measurementtypeclass);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getClassesForVersioning", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getClassesForVersioning", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getClassesForVersioning", e);
		}
		return results;
	}

	/**
	 * Returns a list of measurement types by versionId
	 * 
	 * @return results a list of Measurementtypes
	 */
	private Vector<Measurementtype> getMeasurementTypes(final String versionId) {

		final Vector<Measurementtype> results = new Vector<Measurementtype>();

		final Measurementtype whereMeasurementType = new Measurementtype(rockFactory);
		whereMeasurementType.setVersionid(versionId);
		try {
			final MeasurementtypeFactory measurementtypeFactory = new MeasurementtypeFactory(rockFactory,
					whereMeasurementType, true);
			final Vector<Measurementtype> sortMeasurementTypes = measurementtypeFactory.get();
			Collections.sort(sortMeasurementTypes, MEASUREMENTTYPECOMPARATOR);

			for (final Measurementtype mt : sortMeasurementTypes) {
				results.add(mt);
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getMeasurementTypes", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getMeasurementTypes", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getMeasurementTypes", e);
		}

		return results;
	}

	private Busyhourplaceholders getBusyhourPlaceholders(final Measurementtype meastype) {
		Busyhourplaceholders result = null;
		// String bhobj =
		// meastype.getTypename().substring(meastype.getVendorid().length() + 1);
		try {
			final Busyhourplaceholders b = new Busyhourplaceholders(rockFactory);
			b.setVersionid(meastype.getVersionid());
			b.setBhlevel(meastype.getTypename());
			final BusyhourplaceholdersFactory bF = new BusyhourplaceholdersFactory(rockFactory, b);
			result = bF.getElementAt(0);

			// this is done because there is a chance that there is no rows in the
			// table...
			if (result == null) {
				result = new Busyhourplaceholders(rockFactory);
				result.setVersionid(meastype.getVersionid());
				result.setBhlevel(meastype.getTypename());
				result.setProductplaceholders(Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS);
				result.setCustomplaceholders(Constants.DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS);
			}

		} catch (final Exception e) {
		}
		return result;
	}

	/**
	 * Returns a list of universe extensions by versionId
	 * 
	 * @return results a list of universe extensions
	 */
	private Vector<String> getUniverseExtensions(final String versionId) {
		final Vector<String> results = new Vector<String>();
		final Universename whereUniversename = new Universename(rockFactory);
		whereUniversename.setVersionid(versionId);
		try {
			final UniversenameFactory vendorreleaseFactory = new UniversenameFactory(rockFactory, whereUniversename,
					true);
			final Iterator<Universename> iter = vendorreleaseFactory.get().iterator();

			results.add("ALL");
			while (iter.hasNext()) {
				final Universename tmpUn = iter.next();
				results.add(tmpUn.getUniverseextension());
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getUniverseExtensions", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getUniverseExtensions", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getUniverseExtensions", e);
		}

		return results;
	}

	/**
	 * Returns a list of vendorreleases by versionId
	 * 
	 * @return results a list of vendorreleases
	 */
	private Vector<String> getVendorReleases(final String versionId) {
		final Vector<String> results = new Vector<String>();
		final Supportedvendorrelease whereSupportedvendorrelease = new Supportedvendorrelease(rockFactory);
		whereSupportedvendorrelease.setVersionid(versionId);
		try {
			final SupportedvendorreleaseFactory vendorreleaseFactory = new SupportedvendorreleaseFactory(rockFactory,
					whereSupportedvendorrelease, true);
			final Iterator<Supportedvendorrelease> iter = vendorreleaseFactory.get().iterator();

			while (iter.hasNext()) {
				final Supportedvendorrelease tmpVr = iter.next();
				results.add(tmpVr.getVendorrelease());
			}
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, "SQL error in getVendorReleases", e);
			ExceptionHandler.instance().handle(e);
		} catch (final RockException e) {
			logger.log(Level.SEVERE, "ROCK error in getVendorReleases", e);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "FATAL error in getVendorReleases", e);
		}

		return results;
	}

	@Override
	public boolean delObj(final RockDBObject obj) {
		return false;
	}

	@Override
	public boolean modObj(final RockDBObject obj) {
		return false;
	}

	@Override
	public boolean modObj(final RockDBObject[] obj) {
		return false;
	}

	@Override
	public boolean newObj(final RockDBObject obj) {
		return false;
	}

	@Override
	public boolean validateDel(final RockDBObject obj) {
		return false;
	}

	@Override
	public boolean validateMod(final RockDBObject obj) {
		return false;
	}

	@Override
	public boolean validateNew(final RockDBObject obj) {
		return false;
	}

	@Override
	public boolean updated(final DataModel dataModel) {

		if (dataModel instanceof TechPackDataModel) {
			logger.log(Level.FINE, "Updating MeasurementTypeDataModel due to changes in the TechPackDataModel.");
			techPackDataModel = (TechPackDataModel) dataModel;
			if (techPackDataModel.getVersioning() != null) {
				this.setCurrentVersioning(techPackDataModel.getVersioning());
				this.setBaseVersioning(techPackDataModel.getBaseversioning());
				refresh();
			}

			return true;
		} else if (dataModel instanceof GeneralTechPackDataModel) {
			logger.log(Level.FINE, "Updating MeasurementTypeDataModel due to changes in the GeneralTechPackDataModel.");

			try {

				newDataCreated = true;

				final GeneralTechPackDataModel generalTechPackDataModel = (GeneralTechPackDataModel) dataModel;

				if (generalTechPackDataModel.getVersioning() != null) {
					this.setCurrentVersioning(generalTechPackDataModel.getVersioning());

					final Versioning v = new Versioning(generalTechPackDataModel.getRockFactory());
					v.setVersionid(generalTechPackDataModel.getVersioning().getBaseversion());
					final VersioningFactory vF = new VersioningFactory(generalTechPackDataModel.getRockFactory(), v);

					this.setBaseVersioning(vF.getElementAt(0));

					refresh();
				}

			} catch (final Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	public String[] getVendorReleases() {
		return vendorReleases;
	}

	public void setVendorReleases(final String[] vendorReleases) {
		this.vendorReleases = vendorReleases;
	}

	public String[] getUniverseExtensions() {
		return universeExtensions;
	}

	public void setUniverseExtensions(final String[] universeExtensions) {
		this.universeExtensions = universeExtensions;
	}

	@SuppressWarnings("serial")
	class InvalidCountAggregationFormat extends Exception {

		public InvalidCountAggregationFormat() {
			super("Invalid Countaggregation string");
		}

	}

	class CountAggregationVendorReleases {

		private String countAggregation;

		private List<String> vendorReleases;

		public CountAggregationVendorReleases(final String countAggregation, final List<String> vendorReleases) {
			super();
			this.countAggregation = countAggregation;
			this.vendorReleases = vendorReleases;
		}

		public String getCountAggregation() {
			return countAggregation;
		}

		public void setCountAggregation(final String countAggregation) {
			this.countAggregation = countAggregation;
		}

		public List<String> getVendorReleases() {
			return vendorReleases;
		}

		public void setVendorReleases(final List<String> vendorReleases) {
			this.vendorReleases = vendorReleases;
		}

	}

	private List<CountAggregationVendorReleases> decodeCounteragg(final String counterAgg)
			throws InvalidCountAggregationFormat {

		if (counterAgg.length() <= 0) {
			return new ArrayList<CountAggregationVendorReleases>();
		}

		final String[] cas = counterAgg.split("/");

		final List<CountAggregationVendorReleases> result = new ArrayList<CountAggregationVendorReleases>(cas.length);

		for (int ind = 0; ind < cas.length; ind++) {
			if (ind > 1) {
				throw new InvalidCountAggregationFormat();
			}
			final String[] splitted = cas[ind].split(";");
			if (splitted.length > 1) {
				String vrs = "";
				String type = "";
				for (int jnd = 0; jnd < splitted.length; jnd++) {
					if (jnd == 0) {
						vrs = splitted[jnd];
					} else if (jnd == 1) {
						type = splitted[jnd];
					}
				}
				if (vrs.equals("") || type.equals("")) {
					throw new InvalidCountAggregationFormat();
				} else {
					final String[] onoff = vrs.split(",");
					final List<String> releases = new ArrayList<String>(onoff.length);
					for (final String element : onoff) {
						releases.add(element);
					}
					result.add(new CountAggregationVendorReleases(type, releases));
				}
			} else {
				throw new InvalidCountAggregationFormat();
			}
		}

		return result;
	}

}