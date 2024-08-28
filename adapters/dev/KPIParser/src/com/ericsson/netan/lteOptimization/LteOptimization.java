package com.ericsson.netan.lteOptimization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.networkanalytics.KPIParser;
import com.ericsson.networkanalytics.Utils;

public class LteOptimization {
	
	private RockFactory dwhdb;
	private RockFactory repdb;
	private SourceFile sf;
	private String techPack;
	private String setType;
	private String setName;
	private String workerName;
	private Logger log;
	private MeasurementFile mFile;
	private MeasurementFile cellmFile;
	private int maxID;
	private String OSS_ID;
	private int nodeidoffset;
	private String datetime;
	private LteOptimizationQueries LteOptQueries;
	private HashMap<String, HashMap<String, String>> topologyInfo;
	private	HashMap<String, String> queryProperties;
	private	ArrayList<String> activeKPIs;
	
	public LteOptimization(RockFactory dwhdb, RockFactory repdb, SourceFile sf, String techPack, String setType, String setName, String workerName, Logger log){
		this.dwhdb = dwhdb;
		this.repdb = repdb;
		this.setSf(sf);
		this.setTechPack(techPack);
		this.setSetType(setType);
		this.setSetName(setName);
		this.setWorkerName(workerName);





		this.log = log;
		this.setQueryProperties(new HashMap<String, String>());
		this.topologyInfo = new HashMap<String, HashMap<String, String>>();
	}
	
	

	public LteOptimization() {

	}
	
	public void init(Utils utils){
		try{
			utils.loadParameters(dwhdb, "DIM_E_LTE_OPTIMIZATION_CONFIG");
			nodeidoffset = Integer.parseInt(utils.etlcserverprops.getProperty("NODEIDOFFSET"));
			log.config("nodeidoffset - "+nodeidoffset);
			
			setOSS_ID(getTechPack().split("-")[1]);
			
			topologyInfo.put("NODES", utils.getNodeTopologyInfo(dwhdb, "DIM_E_LTE_OPTIMIZATION_NODE"));
			topologyInfo.put("CELLS", utils.getTopologyInfo(dwhdb, "CELL_NAME", "CELL_ID","NODE_ID","DIM_E_LTE_OPTIMIZATION_CELL"));						
			topologyInfo.put("ATTRIBUTE", utils.getTopologyInfo(dwhdb, "ATTRIBUTE_NAME", "ATTRIBUTE_ID", "ATTRIBUTE_SLOGAN", "DIM_E_LTE_OPTIMIZATION_CM_ATTRIBUTES"));
			topologyInfo.put("RELATIONS", utils.getRelationsTopologyInfo(dwhdb, "CELL_RELATION_NAME", "CELL_RELATION_ID","SOURCE_CELL", "FREQUENCY_REL", "DIM_E_LTE_OPTIMIZATION_CELL_RELATION"));
			
			
		

			Class<LteOptimizationQueries> c = LteOptimizationQueries.class;
			LteOptQueries = c.newInstance();
			
			if(getTechPack().contains("DIM")){
				mFile = Main.createMeasurementFile(getSf(), "nodes", getTechPack(), getSetType(), getSetName(), getWorkerName(), log);
				cellmFile = Main.createMeasurementFile(getSf(), "cells", getTechPack(), getSetType(), getSetName(), getWorkerName(), log);
				
				String ERBSnames = getERBSSegmentInfo();
				if(!ERBSnames.equals("")){
					getQueryProperties().put("ERBSNames", ERBSnames);
					
					List<String> queries = KPIParser.getTopologyQueries(LteOptQueries, getOSS_ID(), datetime, getQueryProperties(), c.getDeclaredMethods());
					
					for(String query : queries){
						ResultSet result = Utils.executeQuery(dwhdb, query);
						log.log(Level.FINEST, query);
						if(query.contains("DIM_E_LTE_ERBS")){						
							createTopology(result);
							
						}else if(query.contains("DIM_E_LTE_EUCELL_CELL")){							
							createCellTopology(result); 
							
						}
					}
					
						String currentTime = utils.calculatePreviousRopTime();						
					    String onehourback = utils.calculatePreviousHour(currentTime);
						
						List<String> bulkQueries = LteOptQueries.getBulk_CM(getOSS_ID(), currentTime, onehourback,getQueryProperties());
						MeasurementFile bulk_cm_File = null;
						ResultSet results = null;
						try{
							bulk_cm_File = Main.createMeasurementFile(getSf(), "cmData", getTechPack(), getSetType(), getSetName(), getWorkerName(), log);
					
							for(String query : bulkQueries){
								String[] q1 = query.split("#");
								String[] counterArray = q1[0].split(",");
								
								results = Utils.executeQuery(dwhdb, q1[1]);
								log.log(Level.FINEST, q1[1]);
							
									while(results.next()){
									for(String counter: counterArray){
										String nodeID=getNodeID("NODES", results.getString("ELEMENT"));
										String cellID=getCellID("CELLS", results.getString("CELL_NAME"), nodeID);
										String[] counterSloganArray = counter.split("&");
									    bulk_cm_File.addData("OSS_ID",getOSS_ID());
										bulk_cm_File.addData("CELL_ID", cellID);
										bulk_cm_File.addData("NODE_ID",nodeID);
										bulk_cm_File.addData("ATTRIBUTE_ID", getCellID("ATTRIBUTE", counterSloganArray[0], counterSloganArray[1]));
										bulk_cm_File.addData("ATTRIBUTE_VALUE",results.getString(counterSloganArray[0]));
										bulk_cm_File.addData("DATETIME_ID",results.getString("DATETIME_ID") );
										bulk_cm_File.addData("UTC_DATETIME_ID",results.getString("UTC_DATETIME_ID") );
										bulk_cm_File.addData("AntennaUnitGroup",results.getString("AntennaUnitGroup"));
										bulk_cm_File.saveData();														
									}
								}
			


						}
						}catch(Exception e){
							log.log(Level.WARNING, "Unable to write to measurement file." ,e);
						}finally {
							if(results != null){
								results.close();
							}
							closeMeasFile(bulk_cm_File);
						}
						
				}
				
			}else if(getTechPack().contains("DC")){
				mFile = Main.createMeasurementFile(getSf(), "kpi", getTechPack(), getSetType(), getSetName(), getWorkerName(), log);
				activeKPIs = utils.getActiveKPIs(dwhdb, "DIM_E_LTE_OPTIMIZATION_KPI");
				datetime = utils.calculatePreviousRopTime();
				
				String ERBSnames = getERBSSegmentInfo();
				if(!ERBSnames.equals("")){
					getQueryProperties().put("ERBSTables", LteOptQueries.ERBS_Data_Tables);
					getQueryProperties().put("CountersTables", LteOptQueries.Counters_Data_Tables);
					getQueryProperties().put("CountersAvgTables", LteOptQueries.Counters_Avg_Data_Tables);
					getQueryProperties().put("ERBS_Vector_Tables", LteOptQueries.ERBS_Vector_Tables);
					getQueryProperties().put("MobilityDayTables", LteOptQueries.MobilityDayTables);
					getQueryProperties().put("MobilityRawTables", LteOptQueries.MobilityRawTables);
					getQueryProperties().put("ERBSDayTables", LteOptQueries.ERBS_Day_Data_Tables);
					getQueryProperties().putAll(utils.getParitionTableNames(repdb, datetime, getQueryProperties()));
					getQueryProperties().put("ERBSNames", ERBSnames);
					
					List<String> queries = KPIParser.getKPIQueries(LteOptQueries, getOSS_ID(), datetime, getQueryProperties(), c.getDeclaredMethods());
					for(String query : queries){
						log.log(Level.FINEST, query);
						String[] KPI_IDs = query.substring(0, query.indexOf("-", 0)).trim().split(",");
						query = query.substring(query.indexOf("-", 0)+1).trim();
	
						
						
						if(KPI_IDs.length > 1){
							boolean needToRunSQL = false;
							for(int i = 0; i < KPI_IDs.length; i++){
								if(activeKPIs.contains(KPI_IDs[i])){
									needToRunSQL = true;
								}
							}
							if(needToRunSQL){
								ResultSet result = Utils.executeQuery(dwhdb, query);
								calculateMultipleKpi(result, KPI_IDs);
							}
							
							
						}else{
							if(activeKPIs.contains(KPI_IDs[0])){
								if(KPI_IDs[0].equalsIgnoreCase("14")){
									
									String trigger = System.getProperty("mobilityTrig");
									log.log(Level.FINEST, "trigger "+ trigger);
									
									String[] date=datetime.split(":");
							
									log.log(Level.FINEST, "datetime "+ date[0]);									
							
									if (!date[0].equalsIgnoreCase(trigger)){									
										if(isDayDataAvailable()){ 
											MobilityExecution mobexc = MobilityExecution.getInstance(log);
											
											if (mobexc !=null){										
												mobexc.calculateKPIs(dwhdb, query, this, utils);
												mobexc.destroyInstance();
											}
																			
											System.setProperty("mobilityTrig", date[0]);
								
										}										 
									}
									
									if(isCellHandoverDataAvailable()){
										MobilityExecution mobexc = MobilityExecution.getInstance(log);
										if (mobexc !=null){		
											mobexc.calculateCellRelations(dwhdb, this, utils);
											mobexc.destroyInstance();
										}										
									}	
									
								}
								
								else if(KPI_IDs[0].equalsIgnoreCase("19")){
									ResultSet result = Utils.executeQuery(dwhdb, query);
									createSINRMeasFile(result);
								}
								
								else{
									ResultSet result = Utils.executeQuery(dwhdb, query);
									calculateSingleKpi(result, KPI_IDs[0]);
								}
							}
						}
						
					}
	
				}
			}
			
		} catch (NumberFormatException e){
			log.log(Level.SEVERE, "NODEIDOFFSET not defined.", e);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Unable to retrieve information" ,e);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Parsing failed." ,e);
		}finally {
			closeMeasFile(mFile);
			closeMeasFile(cellmFile);
		}
	}
	
	
	private void calculateMultipleKpi(ResultSet result, String... KPI_IDs) throws SQLException {
		try{
			while(result.next()){
				for(int i = 0; i < KPI_IDs.length; i++){
					if(activeKPIs.contains(KPI_IDs[i])){
						createPMFiles(result, KPI_IDs[i]);
					}
				}
			}
		}catch(Exception e){
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(result != null)
				result.close();
		}
	}
	
	private void calculateSingleKpi(ResultSet result, String KPI_ID) throws SQLException{
		try{
			while(result.next()){
				createPMFiles(result, KPI_ID);
			}
		}catch(Exception e){
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(result != null)
				result.close();
		}
	}
	
	
	private void createPMFiles(ResultSet result, String KPI_ID) throws Exception{
		String KPIvalue = result.getString("KPI_" + KPI_ID);
		if(KPIvalue != null || KPIvalue != ""){
			String nodeID=getNodeID("NODES", result.getString("NODE_NAME"));
			mFile.addData("DIRNAME", getSf().getDir());
			mFile.addData("NODE_ID", nodeID);
			mFile.addData("CELL_ID",getCellID("CELLS", result.getString("CELL_NAME"), nodeID));
			mFile.addData("KPI_ID", KPI_ID);
			mFile.addData("OSS_ID", getOSS_ID());
			mFile.addData("UTC_DATETIME_ID", datetime);
			mFile.addData("DATE_ID", datetime.split(" ")[0]);
			mFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
			mFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
			mFile.addData("DC_RELEASE", result.getString("DC_RELEASE"));
			mFile.addData("KPI_VALUE", result.getString("KPI_" + KPI_ID));
	
			mFile.saveData();
		}
	}
	
	private void createTopology(ResultSet result) {
		try {
			while(result.next()){
				mFile.addData("NODE_TYPE", "eNB");
				mFile.addData("NODE_NAME", result.getString("ERBS_NAME"));
				mFile.addData("FDN", result.getString("ERBS_FDN"));
				mFile.addData("NODE_VERSION", result.getString("ERBS_VERSION"));
				mFile.addData("VENDOR", result.getString("VENDOR"));
				mFile.addData("SW_VERSION", result.getString("NEMIMVERSION"));
				mFile.addData("DIRNAME", getSf().getDir());
				mFile.addData("OSS_ID", getOSS_ID());
				mFile.saveData();
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(result != null)
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void createCellTopology(ResultSet result) {
		try {
			while(result.next()){
				cellmFile.addData("NODE_ID", getNodeID("NODES", result.getString("ERBS_ID")));
				cellmFile.addData("CELL_TYPE", result.getString("CELL_TYPE"));
				cellmFile.addData("CELL_NAME", result.getString("EUtranCellId"));
				cellmFile.addData("FREQ_BAND", result.getString("FREQ_BAND"));
				cellmFile.addData("DIRNAME", getSf().getDir());
				cellmFile.addData("OSS_ID", getOSS_ID());
				cellmFile.saveData();
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(result != null)
				try {
					result.close();
				} catch (SQLException e) {
					log.log(Level.WARNING, "Unable to write measurement file.", e);
				}
		}
	}
	
	
	
	private void createSINRMeasFile(ResultSet result) {
		MeasurementFile SINRmFile = null;
		try {
			SINRmFile = Main.createMeasurementFile(getSf(), "SINR", getTechPack(), getSetType(), getSetName(), getWorkerName(), log);
			
			while(result.next()){
				
				for(int ant_pair = 0; ant_pair <= 6; ant_pair++){
					SINRmFile.addData("DIRNAME", getSf().getDir());
					SINRmFile.addData("NODE_ID", getNodeID("NODES", result.getString("ERBS")));
					SINRmFile.addData("OSS_ID", getOSS_ID());
					SINRmFile.addData("SECTOR_CARRIER", result.getString("SectorCarrier"));
					SINRmFile.addData("RANGE_ID", result.getString("DCVECTOR_INDEX"));
					SINRmFile.addData("UTC_DATETIME_ID", datetime);
					SINRmFile.addData("DATE_ID", datetime.split(" ")[0]);
					SINRmFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
					SINRmFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
					SINRmFile.addData("DC_RELEASE", result.getString("DC_RELEASE"));
					SINRmFile.addData("ANTENNA_PAIR", ""+ant_pair);
					SINRmFile.addData("COUNT", result.getString(""+ant_pair));
					
					SINRmFile.saveData();
				}
				
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(result != null)
				try {
					result.close();
				} catch (SQLException e) {
					log.log(Level.WARNING, "Unable to write measurement file.", e);
				}
			closeMeasFile(SINRmFile);
		}
	}

	
	public String getERBSSegmentInfo() throws SQLException{
		String ERBSnames = ""; 
		String sql = "SELECT DIM_E_LTE_OPTIMIZATION_NODE.NODE_NAME FROM DIM_E_LTE_OPTIMIZATION_NODE JOIN DIM_E_LTE_OPTIMIZATION_SEGMENT " + 
				"ON DIM_E_LTE_OPTIMIZATION_SEGMENT.NODE_ID=DIM_E_LTE_OPTIMIZATION_NODE.NODE_ID";
		
		ResultSet result = Utils.executeQuery(dwhdb, sql);
		try{
			ArrayList<String> ERBSList = new ArrayList<String>();
			while(result.next()){
				String ERBS = result.getString("NODE_NAME");
				if(!ERBS.equals("")){
					ERBSList.add(ERBS);
				}
			}
			
			ERBSnames = ERBSList.toString().replace("[", "'").replace("]", "'").replace(", ", "','");
		}finally{
			if(result != null)
				result.close();
		}
		return ERBSnames;
	}
	
	String getNodeID(String collectionName, String ElementName) throws SQLException{
		String nodeID = null;
		
		nodeID = topologyInfo.get(collectionName).get(ElementName);
		if(!(nodeID != null) || nodeID == ""){
			nodeID = "0";
		}
		
		return nodeID;
		
	}
	
	String getCellID(String collectionName, String cellName, String nodeID) throws SQLException{
		String cellID = null;
		String strCellNode = cellName+nodeID;
		
		if(cellName.equalsIgnoreCase("NotValid")){
			return "";
		}
		
		cellID = topologyInfo.get(collectionName).get(strCellNode);
		
		if((cellID == null) || cellID == ""){
			cellID = "0";
		}		
		return cellID;		
	}
	
	private boolean isCellHandoverDataAvailable() throws  SQLException, ParseException {

		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateCell= null;
		Date dateCellRel = null;
		ResultSet resultCell = Utils.executeQuery(dwhdb, "select max(UTC_DATETIME_ID) as maxDatetime from DC_E_LTE_OPTIMIZATION_CELL_HO_DAILY_RAW");
		ResultSet resultCellRel =  Utils.executeQuery(dwhdb, "select max(UTC_DATETIME_ID) as maxDatetime from DC_E_LTE_OPTIMIZATION_CELL_RELATION_HO_DAILY_RAW");

		resultCell.next();
		resultCellRel.next();
		
		if(resultCell.getString("maxDatetime")==null){
			return false;
		}
		
		if(resultCellRel.getString("maxDatetime")==null){
			return true;
		}
		
		dateCell = dateTimeFormat.parse(resultCell.getString("maxDatetime"));
	
		dateCellRel = dateTimeFormat.parse(resultCellRel.getString("maxDatetime"));
		
		if(dateCell!=null && dateCellRel!=null){
			if(dateCell.after(dateCellRel)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isDayDataAvailable() throws SQLException{
		ResultSet result = null;
		boolean okToRun = false;		
		try{

			String date_id = Utils.calculatePreviousDay(datetime);
			
			result = Utils.executeQuery(dwhdb, "select max(DATE_ID) as maxDatetime from DC_E_LTE_OPTIMIZATION_CELL_HO_DAILY_RAW");
                     result.next();
                     String max= result.getString("maxDatetime");

                    if(max==null){
                         okToRun=true;
                    }else if(result.getString("maxDatetime").equals(date_id)){
                          return false;
                    }


		
			String sql = "select STATUS from LOG_AggregationStatus where aggregation in ($tablenames) and DATE_ID='" + date_id + "'";
			String tablenames = LteOptQueries.ERBS_Day_Data_Tables + " , " + LteOptQueries.MobilityDayTables;
			sql = sql.replace("$tablenames", tablenames.replace(":", "_"));
		
		
			result = Utils.executeQuery(dwhdb, sql);
			while(result.next()){
				String status = result.getString("STATUS");
				if( !status.equalsIgnoreCase("AGGREGATED")&& !status.equalsIgnoreCase("NOT_LOADED")){
					return false;
				}else{
					okToRun = true;
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to determine day aggregation status. ", e);
			okToRun = false;
		}finally{
			if(result != null)
				result.close();
		}
		
		return okToRun;
	}
	
	void closeMeasFile(MeasurementFile mFile){
		if (mFile != null){
			try {
				mFile.close();
			} catch (Exception e) {
				log.warning("Unable to close " + mFile.getTagID() + " measurement file.");
				
			}
		}
	}

		public String getRelationID(String collectionName, String relName, String cellID,
			String freqRel) {
		String relationID = null;
		String strRelCellFreq = relName+cellID+freqRel;
		relationID = topologyInfo.get(collectionName).get(strRelCellFreq);
		
		if((relationID == null) || relationID == ""){
			relationID = "0";
		}	
		
		return relationID;
	}

	public SourceFile getSf() {
		return sf;
	}



	public void setSf(SourceFile sf) {
		this.sf = sf;
	}



	public String getTechPack() {
		return techPack;
	}



	public void setTechPack(String techPack) {
		this.techPack = techPack;
	}



	public String getSetType() {
		return setType;
	}



	public void setSetType(String setType) {
		this.setType = setType;
	}



	public String getSetName() {
		return setName;
	}



	public void setSetName(String setName) {
		this.setName = setName;
	}



	public String getWorkerName() {
		return workerName;
	}



	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}



	public String getOSS_ID() {
		return OSS_ID;
	}



	public void setOSS_ID(String oSS_ID) {
		OSS_ID = oSS_ID;
	}



	public HashMap<String, String> getQueryProperties() {
		return queryProperties;
	}



	public void setQueryProperties(HashMap<String, String> queryProperties) {
		this.queryProperties = queryProperties;
	}
}
