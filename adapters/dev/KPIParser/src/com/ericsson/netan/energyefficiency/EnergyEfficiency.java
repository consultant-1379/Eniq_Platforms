package com.ericsson.netan.energyefficiency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.networkanalytics.KPIParser;
import com.ericsson.networkanalytics.Utils;

import ssc.rockfactory.RockFactory;

public class EnergyEfficiency {
	
	private RockFactory dwhdb;
	private RockFactory repdb;
	private SourceFile sf;
	private String techPack;
	private String setType;
	private String setName;
	private String workerName;
	private Logger log;
	private MeasurementFile mFile;
	private MeasurementFile nodeFile;
	private MeasurementFile cellFile;
	private MeasurementFile energyMeasFile;
	private String OSS_ID;
	private int nodeidoffset;
	private String utcdatetime;
	private EnergyEfficiencyQueries energyEffQueries;
	private	ArrayList<String> activeMeasures;
	private Set<String> nodeFDNs;

	public EnergyEfficiency(){
		
	}
	
	public EnergyEfficiency(RockFactory dwhdb, RockFactory repdb, SourceFile sf, String techPack, String setType, String setName, String workerName, Logger log){
		this.dwhdb = dwhdb;
		this.repdb = repdb;
		this.sf = sf;
		this.techPack = techPack;
		this.setType = setType;
		this.setName = setName;
		this.workerName = workerName;
		this.log = log;
		
	}
	
	public void init(Utils utils){
		try{
			utils.loadParameters(dwhdb, "DIM_E_ENERGY_CONFIG");
			nodeidoffset = Integer.parseInt(utils.etlcserverprops.getProperty("NODEIDOFFSET"));
			log.config("nodeidoffset - "+nodeidoffset);
			
			OSS_ID = techPack.split("-")[1];
			
			Class<EnergyEfficiencyQueries> energyEff = EnergyEfficiencyQueries.class;
			energyEffQueries = energyEff.newInstance();
			
			if(techPack.contains("DIM")){
				mFile = Main.createMeasurementFile(sf, "Nodes", techPack, setType, setName, workerName, log);
				nodeFDNs = new HashSet<String>();
				List<String> queries = KPIParser.getTopologyQueries(energyEffQueries,  OSS_ID, energyEff.getDeclaredMethods());
				
				for(String query : queries){
					createTopology(query);
				}
			}else if (techPack.contains("DC")){
				mFile = Main.createMeasurementFile(sf, "energyNetwork", techPack, setType, setName, workerName, log);
				nodeFile = Main.createMeasurementFile(sf, "energyNode", techPack, setType, setName, workerName, log);
				cellFile = Main.createMeasurementFile(sf, "energyCell", techPack, setType, setName, workerName, log);
				energyMeasFile = Main.createMeasurementFile(sf, "energyMeasurement", techPack, setType, setName, workerName, log);
				activeMeasures = utils.getActiveKPIs(dwhdb, "DIM_E_ENERGY_MEASURES");
				
				utcdatetime = utils.calculatePreviousRopTime();
				log.info("Calculating "+activeMeasures.size()+" Measures for UTC Timestamp "+utcdatetime);
				
				List<String> queries = KPIParser.getKPIQueries(energyEffQueries, OSS_ID, utcdatetime, energyEff.getDeclaredMethods());
				
				HashMap<String, Set<String>> partitions = utils.getPartitions(repdb, queries);
				
				for(String query : queries){
					
					query = utils.modifyQueryWithPartitions(query, partitions);

					createPMData(query);
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
			closeMeasFile(nodeFile);
			closeMeasFile(cellFile);
			closeMeasFile(energyMeasFile);
			
		}
	}
		
	public void createPMData(String query){
		
		
		if(query.contains("ENERGY_NETWORK")){
			ResultSet result = Utils.executeQuery(dwhdb, query);
			
			try{
				while(result.next()){
					mFile.addData("NODE_TYPE", result.getString("NODE_TYPE"));
					mFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
					mFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
					mFile.addData("UTC_DATETIME_ID", utcdatetime);
					mFile.addData("TOTALENERGY", result.getString("TOTALENERGY"));
					mFile.addData("TOTALDATAVOLUMEDL", result.getString("TOTALDATADOWN"));
					mFile.addData("TOTALDATAVOLUMEUL", result.getString("TOTALDATAUP"));
					mFile.addData("TOTALREPORTINGNODES", result.getString("TOTALREPORTINGNODES"));
					mFile.addData("TOTALNODES", result.getString("TOTALNODES"));
					mFile.addData("DIRNAME", sf.getDir());
					mFile.saveData();
					
				}
			}catch(Exception e){
				log.log(Level.WARNING, "Unable to write measurement file for Energy Network", e);
			}
		}else if(query.contains("ENERGYPERNODE")){
			try{
				String measureID = query.split("-", 2)[0];
				query = query.split("-", 2)[1];
				if(activeMeasures.contains(measureID)){
					ResultSet result = Utils.executeQuery(dwhdb, query);
					
					while(result.next()){
						nodeFile.addData("NODE_ID", result.getString("NODE_ID"));
						nodeFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
						nodeFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
						nodeFile.addData("MEASURE_ID", measureID);
						nodeFile.addData("MEASURE_VALUE", result.getString("MEASUREVALUE"));
						nodeFile.addData("UTC_DATETIME_ID", utcdatetime);
						nodeFile.addData("DIRNAME", sf.getDir());
						nodeFile.saveData();
					}
				}
				
			}catch(Exception e){
				log.log(Level.WARNING, "Unable to write measurement file for Energy Node", e);
			}
		}else if (query.contains("ENERGYPERCELL")){
			try{
				String[] measureIDs = query.split("-", 2)[0].split(",");
				query = query.split("-", 2)[1];
				ResultSet result = Utils.executeQuery(dwhdb, query);
				while(result.next()){
					Map<String, String> cellFileMap = new HashMap<String, String>();
					
					cellFileMap.put("NODE_ID", result.getString("NODE_ID"));
					cellFileMap.put("CELL_ID", result.getString("CELL_ID"));
					cellFileMap.put("DATETIME_ID", result.getString("DATETIME_ID"));
					cellFileMap.put("TIMELEVEL", result.getString("TIMELEVEL"));
					cellFileMap.put("UTC_DATETIME_ID", utcdatetime);
					cellFileMap.put("DIRNAME", sf.getDir());
					
					for(String measureID : measureIDs){
						if(activeMeasures.contains(measureID)){
							cellFileMap.put("MEASURE_ID", measureID);
							cellFileMap.put("MEASURE_VALUE", result.getString("MEASUREVALUE_"+measureID));
							cellFile.addData(cellFileMap);
							cellFile.saveData();
						}
					}
				}

				
			}catch(Exception e){
				log.log(Level.WARNING, "Unable to write measurement file for Energy Cell", e);
			}
		}else{
			ResultSet result = Utils.executeQuery(dwhdb, query);
			try{
				while(result.next()){
					energyMeasFile.addData("NODE_ID", result.getString("NODE_ID"));
					energyMeasFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
					energyMeasFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
					energyMeasFile.addData("SAMPLE_INDEX", result.getString("DCVECTOR_INDEX"));
					energyMeasFile.addData("UTC_DATETIME_ID", utcdatetime);
					energyMeasFile.addData("EQUIPMENTSUPPORTFUNCTION", result.getString("EQUIPMENTSUPPORTFUNCTION"));
					energyMeasFile.addData("ENERGYMEASUREMENT", result.getString("ENERGYMEASUREMENT"));
					energyMeasFile.addData("PMPOWERCONSUMPTION", result.getString("POWERCONSUMPTION"));
					energyMeasFile.addData("DIRNAME", sf.getDir());
					energyMeasFile.saveData();
					
				}
			}catch(Exception e){
				log.log(Level.WARNING, "Unable to write measurement file for Energy Measurement", e);
			}
		}
		
		
	}
	
	public void createTopology(String query) {
		
		ResultSet result = Utils.executeQuery(dwhdb, query);
		try {
			while(result.next()){
				if(!nodeFDNs.contains(result.getString("NODE_FDN"))){
					if(query.contains("DIM_E_RAN")){
						mFile.addData("RNC_ID", result.getString("RNC_ID"));
						mFile.addData("RNC_NAME", result.getString("RNC_NAME"));
						mFile.addData("RNC_FDN", result.getString("RNC_FDN"));
					}
					mFile.addData("NODE_FDN", result.getString("NODE_FDN"));
					nodeFDNs.add(result.getString("NODE_FDN"));
					mFile.addData("NODE_NAME", result.getString("NODE_NAME"));
					mFile.addData("NODE_VERSION", result.getString("NODE_VERSION"));
					mFile.addData("NODE_TYPE", result.getString("NODE_TYPE"));
					if(!query.contains("DIM_E_GRAN_RADIONODE")){
						mFile.addData("NE_MIM_VERSION", result.getString("NE_MIM_VERSION"));
					}
					
					mFile.addData("DIRNAME", sf.getDir());
					mFile.saveData();
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to write measurement file for Topology", e);
		}finally{
			if(result != null)
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}


	private void closeMeasFile(MeasurementFile mFile){
		if (mFile != null){
			try {
				mFile.close();
			} catch (Exception e) {
				log.warning("Unable to close " + mFile.getTagID() + " measurement file.");
				
			}
		}
	}	
}
