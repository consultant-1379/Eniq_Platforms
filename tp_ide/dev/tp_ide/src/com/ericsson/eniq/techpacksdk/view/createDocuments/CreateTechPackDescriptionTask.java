package com.ericsson.eniq.techpacksdk.view.createDocuments;

import com.distocraft.dc5000.etl.engine.sql.SQLExecute;
import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.distocraft.dc5000.repository.dwhrep.GrouptypesFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.BusyhoursourceFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Supportedvendorrelease;
import com.distocraft.dc5000.repository.dwhrep.SupportedvendorreleaseFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.BusyhourrankkeysFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Task Class for creating TP Description document(s) of the given TechPack
 * 
 * @author etogust
 * 
 */
public class CreateTechPackDescriptionTask extends Task<Void, Void> {

  /**
   * logger
   */
  private final Logger logger;

  /**
   * Given TeckPack versioning object
   */
  private final Versioning versioning;

  /**
   * Rockfactory
   */
  private final RockFactory rock;

  /**
   * Output directory
   */
  private final String outputDir;

  private StringBuilder errorBuilder = new StringBuilder();

	private static final List<String> placeTypeOrder = Arrays.asList("PP", "CP");
  /**
   * This member stores the measurementtypes, so there is no need to get them
   * twice from database.
   */
  private final List<Measurementtype> measurementTypes = new ArrayList<Measurementtype>();

  /**
   * This member stores the measurementtypes, so there is no need to get them
   * twice from database.
   */
  private List<Referencetable> referenceTabels = new ArrayList<Referencetable>();
	private static final Map<String, String> bhTypeToString = new HashMap<String, String>();
	static {
		bhTypeToString.put("RANKBH_TIMELIMITED", "Timelimited");
		bhTypeToString.put("RANKBH_SLIDINGWINDOW", "Slidingwindow");
		bhTypeToString.put("RANKBH_TIMECONSISTENT", "Timelimited + Timeconsistent");
		bhTypeToString.put("RANKBH_TIMECONSISTENT_SLIDINGWINDOW", "Slidingwindow + Timeconsistent");
		bhTypeToString.put("RANKBH_PEAKROP", "Peakrop");
	}

  public CreateTechPackDescriptionTask(final String dir, final Versioning versioning, final RockFactory dwhRF,
																			 final Application app, final Logger logger) {
    super(app);
    this.outputDir = dir;
    this.logger = logger;
    this.versioning = versioning;
    this.rock = dwhRF;

  }

  /**
   * Overridden method from Task, where the action begins from
   */
  protected Void doInBackground() throws Exception {

    errorBuilder = new StringBuilder();

    createSDIF();

    if (errorBuilder.length() == 0) {
      errorBuilder.append("TP Description document created OK.");
    }
    JOptionPane.showMessageDialog(null, errorBuilder.toString());

    return null;
  }

  /**
   * Creates temp.xml document, which is one of the required TP Description
   * documents
   *
	 * @throws Exception If errors on opening outputfile
	 */
  private void createSDIF() throws Exception {

    final Map<String, Object> objects = getObjectsToVelocity();
    final String desc = ((String) objects.get("description")).trim();
    final String docName = "TP Description " + desc + ".sdif";
    final BufferedOutputStream bos;
    try {
      final File file = new File(this.outputDir, docName);
      final FileOutputStream fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos);
    } catch (Exception e) {
      final String msg = "Could not open stream for sdif file";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
      throw e;
    }

    try {
      final VelocityContext context = new VelocityContext();
      final Set<String> keys = objects.keySet();
      for (String key : keys) {
        context.put(key, objects.get(key));
			}
      final StringWriter strWriter = new StringWriter();
      Velocity.mergeTemplate("/SDIFDescTemplate.vm", Velocity.ENCODING_DEFAULT, context, strWriter);
      bos.write(strWriter.toString().getBytes());
    } catch (Exception e) {
      final String msg = "Velocity error, Could not create sdif document";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
      throw e;
    } finally {
      try {
        bos.close();

        // clear all the data which you want to fetch again when you
        // generate
        // doc again
        this.measurementTypes.clear();
        this.referenceTabels.clear();

      } catch (Exception e) {
        // ignore on a close
      }
    }

  }

  /**
   * Gets all the rock objects, which are needed in generating temp.xml
   * document. The map will be used in Velocity template.
	 * @return Objects all of em
	 */
  private Map<String, Object> getObjectsToVelocity() {
    final Map<String, Object> objects = new HashMap<String, Object>();

    objects.putAll(getIntroductionStrings());

    objects.put("factTableData", getFactTableData());

    objects.put("busyHourData", getBusyHourData());

    objects.put("dimensionTableData", getDimensionTableData());

    objects.put("interfaceData", getInterfaceData());

    objects.put("sqlInterfaceData", getSqlInterfaceData());

		objects.put("groupDefData", getGroupData());

    return objects;
  }

	private Map<String, List<Grouptypes>> getGroupData() {
		final TreeMap<String, List<Grouptypes>> data = new TreeMap<String, List<Grouptypes>>();
		try{
			final Grouptypes where = new Grouptypes(this.rock);
			where.setVersionid(this.versioning.getVersionid());
			final GrouptypesFactory fac = new GrouptypesFactory(this.rock, where);
			final List<Grouptypes> groups = fac.get();
			for(Grouptypes group : groups){
				final List<Grouptypes> gdata;
				if(data.containsKey(group.getGrouptype())){
					gdata = data.get(group.getGrouptype());
				} else {
					gdata = new ArrayList<Grouptypes>();
					data.put(group.getGrouptype(), gdata);
				}
				gdata.add(group);
			}
		} catch (SQLException e) {
			Exception cause;
			if(e.getNextException() != null){
				cause = e.getNextException();
			} else {
				cause = e;
			}
			error("Could not get Grouptypes information due to an error while fetching from table Grouptypes", cause);
		} catch (RockException e){
			error("Could not get Grouptypes information due to an error while fetching from table Grouptypes", e);
		}
		return data;
	}

	private void error(final String msg, final Exception cause){
		errorBuilder.append(msg).append("\n");
		logger.log(Level.WARNING, msg, cause);
	}

  /**
   * Returns All Strings which introduction chapter will need. description is
   * also used at filename, so at least that one should be included.
   * 
   * @return List of Strings
   */
  protected Map<String, Object> getIntroductionStrings() {
    final Map<String, Object> map = new HashMap<String, Object>();

    // Get techpack name and version id.
    // Set version as version number without the techpack name. The double
    // Parentheses are also removed.
    final String versID = versioning.getVersionid();
    final String name = versioning.getTechpack_name();
    String version = versID.startsWith(name) ? versID.substring(name.length() + 1) : versID;
    version = version.replace("((", "");
    version = version.replace("))", "");

    // Get date information
    final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    final Date dte = new Date();
    final String day = dayFormat.format(dte);
    final String year = yearFormat.format(dte);
    final String month = monthFormat.format(dte);

    map.put("year", year);
    map.put("day", day);
    map.put("month", month);

    // Get the techpack description.
    // If the trimmed description has the version number in the end, the version
    // number will be removed.
    String description = versioning.getDescription().trim();
    description = description.endsWith(version) ? description.substring(0, description.length() - version.length())
        : description;

    // Get the techpack R-State.
    // If the R-State has the version number in the end (old format), remove the
    // underscore and the number.
    // Set the number and release as the product number + r-state.
    String tpVers = versioning.getTechpack_version();
    tpVers = tpVers.endsWith(version) ? tpVers.replaceAll("_" + version, "") : tpVers;
    final String numberAndRelease = versioning.getProduct_number() + " " + tpVers;

    map.put("revision", tpVers);
    map.put("productNumber", versioning.getProduct_number());
    map.put("versioning", versioning);
    map.put("version", version);
    map.put("description", description.trim());
    map.put("productNumberAndRelease", numberAndRelease);

    final StringBuilder releases = new StringBuilder("");
    final Supportedvendorrelease where = new Supportedvendorrelease(this.rock);
    where.setVersionid(this.versioning.getVersionid());
    try {
      final SupportedvendorreleaseFactory svrF = new SupportedvendorreleaseFactory(this.rock, where);
      final Iterator<Supportedvendorrelease> iter = svrF.get().iterator();
      while (iter.hasNext()) {
        releases.append(iter.next().getVendorrelease());
        if (iter.hasNext()) {
          releases.append(",");
        }
      }
    } catch (Exception ex) {
      final String msg = "Could not get supportedversionrelease information to TP description due to an error";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, ex);
    }
    map.put("releases", releases.toString());

    return map;
  }

  /**
   * Creates list where each element describes one fact table and all its
   * information. Element is a map where each key is name of the fact table.
   * With the name there will be a map of all the inforamtions as key value
   * pairs
   * 
   * @return List of fact tables
   */
  protected Map<String, Map<String, Object>> getFactTableData() {

    final Map<String, Map<String, Object>> factTableList = new LinkedHashMap<String, Map<String, Object>>();

    // First get all the facttables and put them to set. Also put all the
    // data which can be found from the measurementtype in place.
    // NOTE: Ranking Tables are skipped.
    try {
      final Measurementtype where = new Measurementtype(this.rock);
      where.setVersionid(versioning.getVersionid());
      final MeasurementtypeFactory mtf = new MeasurementtypeFactory(this.rock, where);

      this.measurementTypes.addAll(mtf.get());

      Iterator<Measurementtype> iter = this.measurementTypes.iterator();
      while (iter.hasNext()) {
        Measurementtype mt = iter.next();

        // Include only non ranking tables
        if (mt.getRankingtable().intValue() == 0) {
          Map<String, Object> factTableData = new LinkedHashMap<String, Object>();
          factTableList.put(mt.getTypename(), factTableData);

          // here add all the data which can be found from measurementtype
          factTableData.put("factTable", mt.getTypename());
          factTableData.put("typeID", mt.getTypeid());
          factTableData.put("OneMinuteAggregation", Utils.replaceNull(mt.getOneminagg()) == 1 ? "Yes" : "No");
          factTableData.put("FifteenMinuteAggregation", Utils.replaceNull(mt.getFifteenminagg()) == 1 ? "Yes" : "No");
          factTableData.put("totalAggregation", Utils.replaceNull(mt.getTotalagg()) == 1 ? "Yes" : "No");
          factTableData.put("SONAggregation", Utils.replaceNull(mt.getSonagg()) == 1 ? "Yes" : "No");
          factTableData.put("size", mt.getSizing());
          factTableData.put("elementBusyHourSupport", mt.getElementbhsupport() == 1 ? "Yes" : "No");
          factTableData.put("deltaCalculation", mt.getDeltacalcsupport() == 1 ? "Yes" : "No");
          factTableData.put("Load File Duplicate Check", Utils.replaceNull(mt.getLoadfile_dup_check()) == 1 ? "Yes"
              : "No");
        }
      }
    } catch (Exception e) {
      String msg = "Could not get fact table data to TP description due to an error";
      errorBuilder.append(msg + "\n");
      logger.log(Level.WARNING, msg, e);
    }

    // second phase is to iterate over the facttables and to add object busy
    // hour support information
    Iterator<String> tableIter = factTableList.keySet().iterator();
    while (tableIter.hasNext()) {
      final Map<String, Object> factTableData = factTableList.get(tableIter.next());
      try {
        final Measurementobjbhsupport where = new Measurementobjbhsupport(this.rock);
        where.setTypeid((String) factTableData.get("typeID"));
        final MeasurementobjbhsupportFactory mobhsF = new MeasurementobjbhsupportFactory(this.rock, where);
        final Iterator<Measurementobjbhsupport> iter = mobhsF.get().iterator();
        final StringBuilder sb = new StringBuilder("");
        while (iter.hasNext()) {
          sb.append(iter.next().getObjbhsupport());
          if (iter.hasNext()) {
            sb.append(", ");
          }
        }
        factTableData.put("objectBusyHourSupport", sb.length() > 0 ? sb.toString() : "None");
      } catch (Exception e) {
        final String msg = "Could not get MeasurementObjBHSupport information to TP description due to an error";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }
    }

    // Then add key data for each factTable
    tableIter = factTableList.keySet().iterator();
    while (tableIter.hasNext()) {
      final Map<String, Object> factTableData = factTableList.get(tableIter.next());
      try {
        final Measurementkey where = new Measurementkey(this.rock);
        where.setTypeid((String) factTableData.get("typeID"));
        final MeasurementkeyFactory mkF = new MeasurementkeyFactory(this.rock, where);
        final Iterator<Measurementkey> iter = mkF.get().iterator();
        final List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        factTableData.put(factTableData.get("factTable") + "_keys", rows);

        while (iter.hasNext()) {
          final Measurementkey mk = iter.next();
          final Map<String, String> row = new LinkedHashMap<String, String>();
          row.put("name", mk.getDataname());
          row.put("dataType", mk.getDatatype() + "(" + mk.getDatasize() + ")");
          row.put("duplicateConstraint", mk.getUniquekey() == 1 ? "Yes" : "No");
          rows.add(row);
        }

      } catch (Exception e) {
        final String msg = "Could not get Measurement key information to TP description due to an error";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }

    }

    // And counters next
    tableIter = factTableList.keySet().iterator();
    while (tableIter.hasNext()) {
      final Map<String, Object> factTableData = factTableList.get(tableIter.next());
      try {
        final Measurementcounter where = new Measurementcounter(this.rock);
        where.setTypeid((String) factTableData.get("typeID"));
        final MeasurementcounterFactory mcF = new MeasurementcounterFactory(this.rock, where);
        final Iterator<Measurementcounter> iter = mcF.get().iterator();
        final List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        factTableData.put(factTableData.get("factTable") + "_counters", rows);

        while (iter.hasNext()) {
          final Measurementcounter mc = iter.next();
          final Map<String, String> row = new LinkedHashMap<String, String>();
          row.put("name", mc.getDataname());
          final int Datascale;
          if(null==mc.getDatascale()){
        	  Datascale = -1;
          } else{
        	  Datascale = mc.getDatascale();
          }
          if (Datascale > 0){
          	row.put("dataType", mc.getDatatype() + "(" + mc.getDatasize() + "," + mc.getDatascale()+ ")");
          }else {
        	  row.put("dataType", mc.getDatatype() + "(" + mc.getDatasize() + ")");
          }
          row.put("timeAggregation", mc.getTimeaggregation());
          row.put("groupAggregation", mc.getGroupaggregation());
          row.put("type", mc.getCountertype());
          rows.add(row);
        }

      } catch (Exception e) {
        final String msg = "Could not get Counter information to TP description due to an error";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }

    }

    return factTableList;
  }


	private String getDisplayableAggType(final String dbType){
		if(bhTypeToString.containsKey(dbType)){
			return bhTypeToString.get(dbType);
		}
		return dbType;
	}
//
	private Map<String, List<Map<String, String>>> getOldFormatBusyHourData(final List<Busyhour> busyHourList) {
			final Map<String, List<Map<String, String>>> busyHourFormattedList = new LinkedHashMap<String, List<Map<String, String>>>();
			// first get all the busyHours
			try {
				for(Busyhour bh : busyHourList){
					final List<Map<String, String>> splitList;
					if(busyHourFormattedList.containsKey(bh.getBhlevel())){
						splitList = busyHourFormattedList.get(bh.getBhlevel());
					} else {
						splitList = new ArrayList<Map<String, String>>();
						busyHourFormattedList.put(bh.getBhlevel(), splitList);
					}
					final Map<String, String> datamap = new HashMap<String, String>();
					datamap.put("description", bh.getDescription());
					datamap.put("criteria", bh.getBhcriteria());
					datamap.put("whereCondition", bh.getWhereclause());
					datamap.put("targetVersionId", bh.getTargetversionid());
					datamap.put("versionId", bh.getVersionid());
					datamap.put("bhType", bh.getBhtype());
					datamap.put("bhLevel", bh.getBhlevel());
					datamap.put("aggregationType", getDisplayableAggType(bh.getAggregationtype()));
					datamap.put("bhVersion", "0");
					final Busyhoursource where = new Busyhoursource(this.rock);
					where.setVersionid(datamap.get("versionId"));
					where.setTargetversionid(datamap.get("targetVersionId"));
					where.setBhtype(datamap.get("bhType"));
					where.setBhlevel(datamap.get("bhLevel"));
					final BusyhoursourceFactory bhsF = new BusyhoursourceFactory(this.rock, where);
					final Iterator<Busyhoursource> bhsIter = bhsF.get().iterator();
					final StringBuilder source = new StringBuilder("");
					while (bhsIter.hasNext()) {
						final Busyhoursource bhs = bhsIter.next();
						source.append(bhs.getTypename());
						if (bhsIter.hasNext()) {
							source.append(",");
						}
					}
					datamap.put("source", source.toString());
					splitList.add(datamap);
				}
			} catch (Exception e) {
				final String msg = "Could not get busy hour information TP description due to an error while fetching from table bysyhour";
				errorBuilder.append(msg + "\n");
				logger.log(Level.WARNING, msg, e);
			}
			return busyHourFormattedList;
		}


	protected Map<String, List<Map<String, String>>> getBusyHourData(){
		try{
			final Busyhour bhWhere = new Busyhour(this.rock);
			bhWhere.setVersionid(versioning.getVersionid());
			final BusyhourFactory bhf = new BusyhourFactory(this.rock, bhWhere, "ORDER BY BHLEVEL, BHTYPE");
			final List<Busyhour> busyHourList = bhf.get();
			boolean newFormatBusyhours = false;
			if(!busyHourList.isEmpty()){
				newFormatBusyhours = placeTypeOrder.contains(busyHourList.get(0).getPlaceholdertype());
			}
			if(newFormatBusyhours){
				return getNewFormatBusyHourData(busyHourList);
			} else {
				return getOldFormatBusyHourData(busyHourList);
			}
		} catch (Exception e) {
      String msg = "Could not get busy hour information TP description due to an error while fetching from table bysyhour";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
    }
		return new HashMap<String, List<Map<String, String>>>(0);
	}
  /**
   * Gets all the information about busyhours to the TP description document
   * 
   * @param busyHourList The Busyhour info from the db
	 * @return list of busy hour information
   */
  protected Map<String, List<Map<String, String>>> getNewFormatBusyHourData(final List<Busyhour> busyHourList) {
    // first get all the busyHours


		final Map<String, Map<String, List<Map<String, String>>>> bhLevelList = new HashMap<String, Map<String, List<Map<String, String>>>>();
    try {
			for (Busyhour bh : busyHourList) {
				final Map<String, List<Map<String, String>>> splitList;
				if(bhLevelList.containsKey(bh.getBhlevel())){
					splitList = bhLevelList.get(bh.getBhlevel());
				} else {
					splitList = new HashMap<String, List<Map<String, String>>>();
					bhLevelList.put(bh.getBhlevel(), splitList);
				}
				final List<Map<String, String>> orderPlaceholderList;
				if(splitList.containsKey(bh.getPlaceholdertype())){
					orderPlaceholderList = splitList.get(bh.getPlaceholdertype());
				} else {
					orderPlaceholderList = new ArrayList<Map<String, String>>();
					splitList.put(bh.getPlaceholdertype(), orderPlaceholderList);
				}
				final Map<String, String> datamap = new HashMap<String, String>();

				datamap.put("description", bh.getDescription());
				datamap.put("criteria", bh.getBhcriteria());
				datamap.put("whereCondition", bh.getWhereclause());
				datamap.put("targetVersionId", bh.getTargetversionid());
				datamap.put("versionId", bh.getVersionid());
				datamap.put("bhType", bh.getBhtype());
				datamap.put("bhLevel", bh.getBhlevel());
				datamap.put("aggregationType", getDisplayableAggType(bh.getAggregationtype()));
				// 20110727 eanguan :: For Remove grouping CR
				//datamap.put("grouping", bh.getGrouping());
				datamap.put("bhVersion", "1");

				final Busyhoursource sWhere = new Busyhoursource(this.rock);
        sWhere.setVersionid(bh.getVersionid());
        sWhere.setTargetversionid(bh.getTargetversionid());
        sWhere.setBhtype(bh.getBhtype());
        sWhere.setBhlevel(bh.getBhlevel());
        final BusyhoursourceFactory bhsF = new BusyhoursourceFactory(this.rock, sWhere);
				final Iterator<Busyhoursource> bhsIter = bhsF.get().iterator();
        StringBuilder source = new StringBuilder("");
        while (bhsIter.hasNext()) {
          final Busyhoursource bhs = bhsIter.next();
          source.append(bhs.getTypename());
          if (bhsIter.hasNext()) {
            source.append(",");
          }
        }
        datamap.put("source", source.toString());

				final Busyhourrankkeys kWhere = new Busyhourrankkeys(this.rock);
				kWhere.setBhtype(bh.getBhtype());
				kWhere.setBhlevel(bh.getBhlevel());
				kWhere.setVersionid(bh.getVersionid());
				final BusyhourrankkeysFactory kFac = new BusyhourrankkeysFactory(this.rock, kWhere);
				final List<Busyhourrankkeys> kList = kFac.get();
				final Iterator<Busyhourrankkeys> kIter = kList.iterator();
				final StringBuilder sb = new StringBuilder();
				while(kIter.hasNext()){
					final Busyhourrankkeys rkey = kIter.next();
					sb.append(rkey.getKeyname());
					if(kIter.hasNext()){
						sb.append(", ");
					}
				}
				datamap.put("rankKeys", sb.toString());

				final Busyhourmapping mWhere = new Busyhourmapping(this.rock);
				mWhere.setBhtype(bh.getBhtype());
				mWhere.setBhlevel(bh.getBhlevel());
				mWhere.setVersionid(bh.getVersionid());
				final BusyhourmappingFactory mFac = new BusyhourmappingFactory(this.rock, mWhere);
				final List<Busyhourmapping> mList = mFac.get();
				final Iterator<Busyhourmapping> mIter = mList.iterator();
				final StringBuilder msb = new StringBuilder();
				while(mIter.hasNext()){
					final Busyhourmapping mapping = mIter.next();
					msb.append(mapping.getBhtargettype());
					if(mIter.hasNext()){
						msb.append(", ");
					}
				}
				datamap.put("bhMappings", msb.toString());
				datamap.put("bhType", bh.getBhtype());
				if(bh.getBhcriteria() == null || bh.getBhcriteria().length()==0){
					datamap.put("defined", "false");
				} else {
					datamap.put("defined", "true");
				}
				orderPlaceholderList.add(datamap);
			}
    } catch (Exception e) {
      String msg = "Could not get busy hour information TP description due to an error while fetching from table bysyhour";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
    }
		final Map<String, List<Map<String, String>>> bhList = new HashMap<String, List<Map<String, String>>>();
		// Now reorder stuff.....
		final List<String> levels = new ArrayList<String>(bhLevelList.keySet());
		Collections.sort(levels);
		for(String bhLevel : levels){
			final Map<String, List<Map<String, String>>> tmpMap = bhLevelList.get(bhLevel);
			final Comparator<Map<String, String>> dataComp = new Comparator<Map<String, String>>(){
				public int compare(Map<String, String> m1, Map<String, String> m2) {
					return m1.get("bhType").compareTo(m2.get("bhType"));
				}
			};
			final List<Map<String, String>> lList;
			if(bhList.containsKey(bhLevel)){
				lList = bhList.get(bhLevel);
			} else {
				lList = new ArrayList<Map<String, String>>();
				bhList.put(bhLevel, lList);
			}
			for(String pType : placeTypeOrder){
				final List<Map<String, String>> bb = tmpMap.get(pType);
				if(bb != null){
					Collections.sort(bb, dataComp);
					lList.addAll(bb);
				}
			}
		}
		return bhList;
  }

  /**
   * Creates list where each element describes one dimension table and all its
   * information. Element is a map where each key is name of the dimension
   * table. With the name there will be a map of all the information as key
   * value pairs.
   * 
   * @return Table data
   */
  protected Map<String, Map<String, Object>> getDimensionTableData() {

    final Map<String, Map<String, Object>> dimensionTableList = new LinkedHashMap<String, Map<String, Object>>();

    // first get all the dimensiontables and put them to set. Also put all
    // the data which can be found from the referencetable-table in place
    try {
      final Referencetable where = new Referencetable(this.rock);
      where.setVersionid(versioning.getVersionid());
      final ReferencetableFactory rtF = new ReferencetableFactory(this.rock, where);
      this.referenceTabels = rtF.get();
			for (Referencetable rt : this.referenceTabels) {
				final Map<String, Object> dimensionTableData = new LinkedHashMap<String, Object>();
				dimensionTableList.put(rt.getTypename(), dimensionTableData);
				// here add all the data which can be found from referencetable
				dimensionTableData.put("dimensionTable", rt.getTypename());
				dimensionTableData.put("typeID", rt.getTypeid());
				dimensionTableData.put("updateMethod", Constants.UPDATE_METHODS_TEXT[(rt.getUpdate_policy()).intValue()]);

				// Set the type for the reference table. The value will be set to
				// "table" for all types, except a special handling is implemented for
				// the AGGLEVEL reference type (For example: type name
				// "SELECT_E_MGW_AGGLEVEL"), which will be set as "view".
				if (rt.getTypename().contains("SELECT_") && rt.getTypename().contains("_AGGLEVEL")) {
					dimensionTableData.put("type", "view");
				} else {
					dimensionTableData.put("type", "table");
				}
			}
    } catch (Exception e) {
      final String msg = "Could not get dimension table data to TP description due to an error";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
    }

    // second phase is to iterate over the dimensiontables and to add
    // referencecolumn information to list
		for (String s : dimensionTableList.keySet()) {
			final Map<String, Object> dimensionTableData = dimensionTableList.get(s);
			try {
				final Referencecolumn where = new Referencecolumn(this.rock);
				where.setTypeid((String) dimensionTableData.get("typeID"));
				final ReferencecolumnFactory rcF = new ReferencecolumnFactory(this.rock, where);
				final Iterator<Referencecolumn> iter = rcF.get().iterator();
				final List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
				dimensionTableData.put(dimensionTableData.get("dimensionTable") + "_columns", rows);

				while (iter.hasNext()) {
					final Referencecolumn rc = iter.next();
					final Map<String, String> row = new LinkedHashMap<String, String>();
					row.put("name", rc.getDataname());
					row.put("dataType", buildType(rc));
					row.put("includedInUpdates", rc.getIncludeupd() == 1 ? "Yes" : "No");
					rows.add(row);
				}
			} catch (Exception e) {
				final String msg = "Could not get ReferenceColumn information to TP description due to an error";
				errorBuilder.append(msg).append("\n");
				logger.log(Level.WARNING, "Could not get ReferenceColumn information to TP description due to an error", e);
			}
		}

    return dimensionTableList;

  }

  /**
   * Creates list where each element describes one interface and all its
   * information. Element is a map where each key is name of the interface. With
   * the name there will be a map of all the information as key value pairs.
   * 
   * @return list of interfaces with all the information that TP description
   *         needs
   */
  protected Map<String, Map<String, Object>> getInterfaceData() {
    final Map<String, Map<String, Object>> interfaceList = new LinkedHashMap<String, Map<String, Object>>();

    // first get the dataformats of this techpack, because they are needed
    // to
    // access interfacemeasurements and datainterface
    final List<Dataformat> dataformatList = new ArrayList<Dataformat>();
    try {
      final Dataformat where = new Dataformat(this.rock);
      where.setVersionid(versioning.getVersionid());
      final DataformatFactory dfF = new DataformatFactory(this.rock, where);
      dataformatList.addAll(dfF.get());
    } catch (Exception e) {
      final String msg = "Could not get interface data to TP description due to an error while trying to get dataformats.";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
    }

    // Then get the interfacemeasurements related to these dataformats
    final List<Interfacemeasurement> ifMeasList = new ArrayList<Interfacemeasurement>();
    for (Dataformat df : dataformatList) {
      try {
        final Interfacemeasurement where = new Interfacemeasurement(this.rock);
        where.setDataformatid(df.getDataformatid());
        final InterfacemeasurementFactory ifmF = new InterfacemeasurementFactory(this.rock, where);
        ifMeasList.addAll(ifmF.get());
      } catch (Exception e) {
        final String msg = "TP description generation->error while trying to get interfaceMeasurements.";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }
    }

    // get the datainterfaces
    final Set<String> names = new HashSet<String>();
    for (Interfacemeasurement ifm : ifMeasList) {
      names.add(ifm.getInterfacename());
		}
    for (String interfaceName : names) {
      try {
        final Map<String, Object> interfaceData = new LinkedHashMap<String, Object>();

        final Datainterface where = new Datainterface(this.rock);
        where.setInterfacename(interfaceName);
        final DatainterfaceFactory diF = new DatainterfaceFactory(this.rock, where);

        // We got if with interfacename, so we must have one and only
        // one interface here
        final Datainterface di = diF.get().firstElement();
        interfaceList.put(interfaceName, interfaceData);
        interfaceData.put("name", di.getInterfacename() + "_" + di.getInterfacetype());
        interfaceData.put("type", di.getInterfacetype());

      } catch (Exception e) {
        final String msg = "TP description generation->error while trying to get datainterfaces.";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }
    }

    return interfaceList;
  }

  /**
   * Creates list where each element describes one sqlinterface and all its
   * information. Element is a map where each key is name of the interface. With
   * the name there will be a map of all the information as key value pairs.
   * sqlinterface means the views and some tables at DWH database.
   * 
   * The list of sqlinterfaces is constructed from tables measurementtable and
   * referencetables
   * 
   * @return list of sqlinterfaces with all the information that TP description
   *         needs
   */
  protected Map<String, List<Map<String, String>>> getSqlInterfaceData() {

    final Map<String, List<Map<String, String>>> sqlInterfaceList = new LinkedHashMap<String, List<Map<String, String>>>();

    // first get all the needed data from measurementtable-table
    final List<Measurementtable> measurementTables = new ArrayList<Measurementtable>();
    try {
      for (Measurementtype mt : this.measurementTypes) {
        final Measurementtable where = new Measurementtable(this.rock);
        where.setTypeid(mt.getTypeid());
        final MeasurementtableFactory mtf = new MeasurementtableFactory(this.rock, where);
        measurementTables.addAll(mtf.get());
      }
    } catch (Exception e) {
      final String msg = "TP description generation->error while trying to get measurementtables.";
			errorBuilder.append(msg).append("\n");
      logger.log(Level.WARNING, msg, e);
    }

    // Then add measurementtables to map and get the info about the columns
    for (Measurementtable mt : measurementTables) {

      final List<Map<String, String>> columns = new ArrayList<Map<String, String>>();

      try {
        final Measurementcolumn where = new Measurementcolumn(this.rock);
        where.setMtableid(mt.getMtableid());
        where.setIncludesql(1);

        final MeasurementcolumnFactory mcF = new MeasurementcolumnFactory(this.rock, where);
				for (Measurementcolumn mc : mcF.get()) {
					// add here, so that only views with valid columns are added
					sqlInterfaceList.put(mt.getBasetablename(), columns);

					final StringBuilder sb = new StringBuilder("");
					sb.append(mc.getDatatype());
					final Map<String, String> row = new HashMap<String, String>();
					columns.add(row);
					row.put("name", mc.getDataname());
					row.put("type", buildType(mc));
				}
      } catch (Exception e) {
        final String msg = "TP description generation->error while trying to get measurementcolumns.";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }
    }

    // Then add referencetables to map and get the info about the columns
    for (Referencetable rt : this.referenceTabels) {
      // second phase is to iterate over the dimensiontables and to add
      // referencecolumn information to list
      final List<Map<String, String>> columns = new ArrayList<Map<String, String>>();
      try {
        final Referencecolumn where = new Referencecolumn(this.rock);
        where.setTypeid(rt.getTypeid());
        where.setIncludesql(1);
        final ReferencecolumnFactory rcF = new ReferencecolumnFactory(this.rock, where);
				for (Referencecolumn rc : rcF.get()) {
					final Map<String, String> row = new HashMap<String, String>();
					sqlInterfaceList.put(rt.getTableName(), columns);
					columns.add(row);
					row.put("name", rc.getDataname());
					row.put("type", buildType(rc));
				}
      } catch (Exception e) {
        final String msg = "TP description generation->error while trying to get referencecolumns.";
				errorBuilder.append(msg).append("\n");
        logger.log(Level.WARNING, msg, e);
      }
    }

    return sqlInterfaceList;
  }

  /**
   * Builds the type string from given column
   * 
   * @param column Measurementcolumn or Referencecolumn
   * @return String representing column
   * @throws Exception <code>column></code> wasnt Measurementcolumn or Referencecolumn
   */
  private String buildType(Object column) throws Exception {
    final StringBuilder sb = new StringBuilder("");

    if (column instanceof Measurementcolumn) {
      final Measurementcolumn mc = (Measurementcolumn) column;
      sb.append(mc.getDatatype());
      if (mc.getDatatype().equalsIgnoreCase("tinyint") || mc.getDatatype().equalsIgnoreCase("smallint")
          || mc.getDatatype().equalsIgnoreCase("int") || mc.getDatatype().equalsIgnoreCase("integer")
          || mc.getDatatype().equalsIgnoreCase("date") || mc.getDatatype().equalsIgnoreCase("datetime")
          || mc.getDatatype().equalsIgnoreCase("unsigned int")) {
        // this is fine. only name needed
      } else if (mc.getDatatype().equalsIgnoreCase("numeric")) {
        sb.append("(");
        sb.append(mc.getDatasize());
        sb.append(",");
        sb.append(mc.getDatascale());
        sb.append(")");
      } else {
        sb.append("(");
        sb.append(mc.getDatasize());
        sb.append(")");
      }
    } else if (column instanceof Referencecolumn) {
      final Referencecolumn mc = (Referencecolumn) column;
      sb.append(mc.getDatatype());
      if (mc.getDatatype().equalsIgnoreCase("tinyint") || mc.getDatatype().equalsIgnoreCase("smallint")
          || mc.getDatatype().equalsIgnoreCase("int") || mc.getDatatype().equalsIgnoreCase("integer")
          || mc.getDatatype().equalsIgnoreCase("date") || mc.getDatatype().equalsIgnoreCase("datetime")
          || mc.getDatatype().equalsIgnoreCase("unsigned int")) {
        // this is fine. only name needed
      } else if (mc.getDatatype().equalsIgnoreCase("numeric")) {
        sb.append("(");
        sb.append(mc.getDatasize());
        sb.append(",");
        sb.append(mc.getDatascale());
        sb.append(")");
      } else {
        sb.append("(");
        sb.append(mc.getDatasize());
        sb.append(")");
      }
    } else {
      final String msg = "Trying to get column information from a object which is something else than a column.";
			errorBuilder.append(msg).append("\n");
      throw new Exception(msg);
    }
    return sb.toString();
  }
}
