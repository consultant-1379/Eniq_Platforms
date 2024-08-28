/**
 * 
 */
package com.ericsson.eniq.afj.common;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
//import com.ericsson.eniq.common.Utils;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.exception.AFJException;

/**
 * Class to generate the new loader sql in the loader action for the meas type. The new loader sql will have the new counters in it.
 * Common class - Base has been TPIDE's  CreateLoaderSet class. Needs to be refactored for common use by EBSManager and AFJManager.
 * @author esunbal
 *
 */
public class CommonSetGenerator {

    private final Logger log = Logger.getLogger(this.getClass().getName());

	  protected String loaderTemplateName;

	  protected String setTemplateName;

	  protected String name;

	  protected String version;

	  protected RockFactory dwhrep;

	  protected RockFactory etlrep;

	  protected int techPackID;

	  protected String templateName = "LoaderSQLXML.vm";

	  protected String postLoadSQLTemplate = "postLoadSQL.vm";

	  protected String updateDimSessionTemplate = "updateDimSession.vm";

	  protected String temporaryOptionTemplate = "temporaryOption.vm";

	  protected long maxSetID = 0;

	  protected long maxActionID = 0;

	  protected long maxSchedulingID = 0;

	  protected boolean doSchedulings = false;

	  protected String techPackName = "";

	  protected String templateDir;
	  
	  public CommonSetGenerator(final String templateDir, final String name, final String version, final RockFactory dwhrepRock,
		      final RockFactory rock, final int techPackID, final String techPackName, final boolean schedulings) throws Exception {

		    this.templateDir = templateDir;
		    this.name = name;
		    this.version = version;
		    this.dwhrep = dwhrepRock;
		    this.etlrep = rock;
		    this.techPackID = techPackID;

		    this.maxSetID = Utils.getSetMaxID(rock) + 1;
		    this.maxActionID = Utils.getActionMaxID(rock) + 1;
		    this.maxSchedulingID = Utils.getScheduleMaxID(rock) + 1;
		    this.doSchedulings = schedulings;
		    this.techPackName = techPackName;
		  }	

	
	/**
	 * Method to update the loader action for a given measurement type.
	 * @param mType
	 * @return
	 * @throws Exception
	 */
	public void updateLoaderAction(final String mType) throws Exception{
		
		/*
		 * 1.) Search for the meas type in the MeasurementType table to get the joinable field.
		 * 2.) Check if the joinable is null or empty - implies Loader else UnPartitioned_Loader.
		 * 3.) Get the setId from Utils.getSetId		 
		 * 4.) Create the loader sql query.
		 * 5.) Update the meta_transfer_actions record with the new loader query. 
		 */
		
		final Measurementtype measTypeWhere = new Measurementtype(dwhrep);
		measTypeWhere.setTypeid(mType);
		final MeasurementtypeFactory measTypeFactory = new MeasurementtypeFactory(dwhrep, measTypeWhere);
		final Vector<Measurementtype> measTypeList = measTypeFactory.get();
		final Measurementtype measTypeValue = (Measurementtype) measTypeList.get(0);
		
		final String joinable = measTypeValue.getJoinable();		
		String loaderType = null;
		if(joinable !=null && joinable.length() > 0){
			loaderType = "UnPartitioned_Loader";
		}
		else{
			loaderType = "Loader";
		}
		
		log.info("Joinable:"+joinable+" loaderType:"+loaderType);
        final long setid = Utils.getSetId("Loader" + "_" + measTypeValue.getTypename(), version, techPackID, etlrep);
        
        if(setid == -1){
        	log.info("We have a problem");
        	throw new Exception("Unable to find the set in the meta_collections for meas type:"+measTypeValue.getTypename()+" version:"+version+" techPackID:"+techPackID);
        }
        final long tpId = techPackID;
        
        final Meta_transfer_actions mta = new Meta_transfer_actions(etlrep);
	    mta.setTransfer_action_name(loaderType + "_" + measTypeValue.getTypename());
	    mta.setVersion_number(version);
	    mta.setCollection_set_id(tpId);
	    mta.setCollection_id(setid);
	    final Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(etlrep, mta);
	    if (mtaF.size()>0){
	    final Meta_transfer_actions transferActionsObject = ((Meta_transfer_actions) mtaF.getElementAt(0));
	    log.info("Existing action_content:"+transferActionsObject.getAction_contents());
	    final String sqlQuery = CreateSql(mType+":RAW"); // Need to change this call.
	    log.info("New action_content:"+sqlQuery);
	    transferActionsObject.setAction_contents(sqlQuery);
	    final int updateValue = transferActionsObject.updateDB();	    
	    log.info("Update Value:"+updateValue);
	    if(updateValue == 1){
	    	log.info("Updated the loader sql with updateValue:"+updateValue+" for set:"+setid);
	    }
	    }
	    else{
	    	log.info("Unable to find the meta_tranfer_actions record for tpId:"+techPackID+" setId:"+setid+" version:"+version);
	    	throw new AFJException("Unable to update the loader sql. Not able to find record in meta_transfer_actions");
	    }        
	}
	
  public void removeLoaderAction(final String mType) throws Exception {
    
    final Measurementtype mt = new Measurementtype(dwhrep);
    mt.setTypeid(mType);
    final MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrep, mt);

    final Measurementtype measType = mtf.getElementAt(0);

    if (measType == null) {
      log.info("Measurement type not found, no need to remove sets");
      return;
    }

    boolean join = false;

    if (measType.getJoinable() != null && measType.getJoinable().length() > 0) {
      join = true;
    }

    final long setid = Utils.getSetId("Loader" + "_" + measType.getTypename(), version, techPackID, etlrep);
    if (setid == -1) {
      log.info("No sets found, no need to remove");
      return;
    }

    // Temporary option
    log.info("Removing action SQL_Execute_" + measType.getTypename());
    Utils.removeAction("SQL_Execute_" + measType.getTypename(), version, techPackID, setid, etlrep);

    // Remove loader, based on joinable value
    if (join) {
      // Remove unpartitioned loader
      log.info("Removing action UnPartitioned_Loader_" + measType.getTypename());
      Utils.removeAction("UnPartitioned_Loader_" + measType.getTypename(), version, techPackID, setid, etlrep);
    } else {
      // Remove loader
      log.info("Removing action Loader_" + measType.getTypename());
      Utils.removeAction("Loader_" + measType.getTypename(), version, techPackID, setid, etlrep);
    }

    // Remove joiner action
    if (join) {
      log.info("Removing action SQLJoiner_" + measType.getTypename());
      Utils.removeAction("SQLJoiner_" + measType.getTypename(), version, techPackID, setid, etlrep);
    }

    log.info("  Removing action DuplicateCheck_" + measType.getTypename());
    Utils.removeAction("DuplicateCheck_" + measType.getTypename(), version, techPackID, setid, etlrep);

    // Remove UpdateDimSession
    log.info("  Removing action UpdateDimSession_" + measType.getTypename());
    Utils.removeAction("UpdateDimSession_" + measType.getTypename(), version, techPackID, setid, etlrep);

    // Remove scheduling
    final String name = "Loader_" + measType.getTypename();
    log.info("Removing Scheduling " + name);
    Utils.removeScheduling(name, version, techPackID, setid, etlrep);

    // Remove set
    log.info("Removing set Loader_" + measType.getTypename());
    Utils.removeSet("Loader" + "_" + measType.getTypename(), version, techPackID, etlrep);
    
    
  }

	/**
	 * Method to create the loader sql.
	 * @param mTableID
	 * @return
	 * @throws Exception
	 */
	private String CreateSql(final String mTableID) throws Exception {
		final List<List<Object>> measurementColumnList = new ArrayList<List<Object>>();
		final Measurementcolumn mc = new Measurementcolumn(dwhrep);
		mc.setMtableid(mTableID);
		final MeasurementcolumnFactory mcf = new MeasurementcolumnFactory(dwhrep, mc);
		final Vector<Measurementcolumn> measColumnList = mcf.get();

		final Iterator<Measurementcolumn> iTable = measColumnList.iterator();
		List<Object> sortList = null;
		while (iTable.hasNext()) {
			final Measurementcolumn columns = iTable.next();
			sortList = new ArrayList<Object>();
			sortList.add(0, columns.getDataname());
			sortList.add(1, columns.getColnumber());
			measurementColumnList.add(sortList);
		}

		class comp implements Comparator<List<Object>> {
			 
			public int compare(final List<Object> d1, final List<Object> d2) {

				final Long i1 = (Long) d1.get(1);
				final Long i2 = (Long) d2.get(1);
				return i1.compareTo(i2);
			}
		}

		Collections.sort(measurementColumnList, new comp());

		final Vector<Object> vec = new Vector<Object>();
		final Iterator<List<Object>> iColl = measurementColumnList.iterator();
		while (iColl.hasNext()) {
		  final Object tmp = iColl.next().get(0);
			vec.add(tmp);
		}

		final VelocityContext context = new VelocityContext();
		context.put("measurementColumn", vec);

		return merge(context);
	}
	  
	  /**
	   * 
	   * Merges template and context
	   * 
	   * @param templateName
	   * @param context
	   * @return string contains the output of the merge
	   * @throws Exception
	   */
	  private String merge(final VelocityContext context) throws Exception {	
		Velocity.init();
	    final StringWriter strWriter = new StringWriter();
	    log.info("File Name:"+templateDir + "/" + this.templateName);	    
	    log.info("TemplateExists:"+Velocity.templateExists(this.templateDir+File.separator+this.templateName));	    

	    final boolean isMergeOk = Velocity.mergeTemplate(this.templateDir+File.separator+this.templateName, Velocity.ENCODING_DEFAULT, context,
		        strWriter);	    
	    
	    log.info("Velocity Merge OK: " + isMergeOk);

	    return strWriter.toString();

	  }
}
