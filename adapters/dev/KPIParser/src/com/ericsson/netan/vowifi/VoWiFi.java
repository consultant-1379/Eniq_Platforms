package com.ericsson.netan.vowifi;

import java.sql.ResultSet;
import java.sql.SQLException;
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

public class VoWiFi {
	
	private RockFactory dwhdb;
	private RockFactory repdb;
	private SourceFile sf;
	private String techPack;
	private String setType;
	private String setName;
	private String workerName;
	private Logger log;
	private MeasurementFile mFile;
	private int maxID;
	private String utcdatetime;
	private HashMap<String, String> topologyInfo;
	private	HashMap<String, String> activeKpiThresholds;
	private String KPI_VALUE;
	private Set<String> nodeFDNs;

	public VoWiFi(RockFactory dwhdb, RockFactory repdb, SourceFile sf, String techPack, String setType, String setName, String workerName, Logger log){
		this.dwhdb = dwhdb;
		this.repdb = repdb;
		this.sf = sf;
		this.techPack = techPack;
		this.setType = setType;
		this.setName = setName;
		this.workerName = workerName;
		this.log = log;
	}
	
	public VoWiFi() {

	}

	 
	public void init(Utils utils){
		try{
			utils.loadParameters(dwhdb, "DIM_E_VOWIFI_CONFIG");
			int nodeidoffset = Integer.parseInt(utils.etlcserverprops.getProperty("NODEIDOFFSET"));
			
			Class<VoWiFiQueries> vowifi = VoWiFiQueries.class;
			VoWiFiQueries vowifiQueries = vowifi.newInstance();
			String OSS_ID = techPack.split("-")[1];
			if(techPack.contains("DIM")){
				mFile = Main.createMeasurementFile(sf, "nodes", techPack, setType, setName, workerName, log);
				nodeFDNs = new HashSet<String>();
				log.config("nodeidoffset - "+nodeidoffset);
				/*int nodeidcount = utils.getNodeId(dwhdb, "DIM_E_VOWIFI_NODE", "NODE_ID");
				if(nodeidcount < nodeidoffset){
					nodeidcount = nodeidcount + nodeidoffset;
				}
				
				maxID = nodeidcount;*/
				
				List<String> queries = KPIParser.getTopologyQueries(vowifiQueries, OSS_ID, vowifi.getDeclaredMethods());
				
				for(String query : queries){
					createTopology(query);
				}
			}else if(techPack.contains("DC")){
				mFile = Main.createMeasurementFile(sf, "vowifi", techPack, setType, setName, workerName, log);
				topologyInfo = utils.getNodeTopologyInfo(dwhdb, "DIM_E_VOWIFI_NODE");
				utcdatetime = utils.calculatePreviousRopTime();

				List<String> queries = KPIParser.getKPIQueries(vowifiQueries, OSS_ID, utcdatetime, vowifi.getDeclaredMethods());
				
				HashMap<String, Set<String>> partitions = utils.getPartitions(repdb, queries);

				activeKpiThresholds = utils.getActiveThresholds(dwhdb, "DIM_E_VOWIFI_KPI");
				activeKpiThresholds = utils.checkRequiredThresholds(utcdatetime,activeKpiThresholds );
				
				if(activeKpiThresholds.keySet().size() > 0){
					log.info("Running "+activeKpiThresholds.keySet().size()+" KPIs for datetime = "+utcdatetime);
					
					for(String query : queries){
						
						String[] KPI_IDs = query.substring(0, query.indexOf("-", 0)).trim().split(",");
						query = query.substring(query.indexOf("-", 0)+1).trim();
						
						query = utils.modifyQueryWithPartitions(query, partitions);
						
						if(KPI_IDs.length == 1){
							if(activeKpiThresholds.containsKey(KPI_IDs[0])){
								createPMFiles(KPI_IDs[0], query);
							}
						}else if (KPI_IDs.length > 1){
							calculateMultipleKpi(query, KPI_IDs);
						}
					}
				}else{
					log.warning("No active KPIs");
				}
			}
		} catch (NumberFormatException e){
			log.log(Level.SEVERE, "NODEIDOFFSET not defined.", e);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Unable to retrieve information" ,e);
		} catch (Exception e) {
			log.severe("Parsing failed. "+e.getMessage());
		}finally {
			if (mFile != null)
				try {
					mFile.close();
				} catch (Exception e) {
					log.warning("Unable to close measurement file.");
				}
		}
	}
	
	private void calculateMultipleKpi(String query, String... KPI_IDs) throws SQLException {
		int indicatorForKPI1 = 0;
		log.finest("Query for multiple KPIs ---- "+query);
		ResultSet result = Utils.executeQuery(dwhdb, query);
		try{
			while(result.next()){
				if(query.contains("DC_E_WMG")){
					for(int i = 0; i < KPI_IDs.length; i++){
						if(activeKpiThresholds.containsKey(KPI_IDs[i])){
							createPMFiles(KPI_IDs[i], result);
							indicatorForKPI1++;
						}
					}
					
					if(indicatorForKPI1 == 3){
						if(activeKpiThresholds.containsKey("1")){
							createPMFiles("1", result);
							
						}
					}
				}else if(query.contains("DC_E_IMS") || query.contains("DC_E_CSCF")){
					for(int i = 0; i < KPI_IDs.length; i++){
						if(activeKpiThresholds.containsKey(KPI_IDs[i])){
							calculateCSCFKpis(KPI_IDs[i],result);
						}
					}
				}
				
				indicatorForKPI1 = 0;
			}
		}catch(Exception e){
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(result != null)
				result.close();
		}
	}
	
	private void calculateCSCFKpis(String KPI_ID, ResultSet result) throws SQLException {
		HashMap<String, HashMap<String, String>> counterPerNode = new HashMap<>();
		HashMap<String, String> counterDataPerNode = null;
		
		if(KPI_ID.equals("6")){
			Double scscfSuccessfulRegistrationPerAccess = 0.0;
			Double scscfFailedRegistrationPerAccess_400_403 = 0.0;
			Double scscfFailedRegistrationPerAccess_401 = 0.0;
			Double scscfAttemptedRegistrationPerAccess = 0.0;
			
			do{
				
				String moid = result.getString("MOID");
				String nodeName = result.getString("NODE_NAME");
				if(counterPerNode.containsKey(nodeName)){
					if(moid.contains("400") || moid.contains("403")){
						scscfFailedRegistrationPerAccess_400_403 += result.getDouble("scscfFailedRegistrationPerAccess");
					}else if(moid.contains("401")){
						scscfFailedRegistrationPerAccess_401 += result.getDouble("scscfFailedRegistrationPerAccess");
					}
					
					scscfSuccessfulRegistrationPerAccess += result.getDouble("scscfSuccessfulRegistrationPerAccess");
					scscfAttemptedRegistrationPerAccess += result.getDouble("scscfAttemptedRegistrationPerAccess");
					
					counterDataPerNode.put("scscfSuccessfulRegistrationPerAccess", scscfSuccessfulRegistrationPerAccess.toString());
					counterDataPerNode.put("scscfFailedRegistrationPerAccess_400_403", scscfFailedRegistrationPerAccess_400_403.toString());
					counterDataPerNode.put("scscfAttemptedRegistrationPerAccess", scscfAttemptedRegistrationPerAccess.toString());
					counterDataPerNode.put("scscfFailedRegistrationPerAccess_401", scscfFailedRegistrationPerAccess_401.toString());
					
					counterPerNode.put(nodeName, counterDataPerNode);
				}else{
					counterDataPerNode = new HashMap<>();
					
					scscfSuccessfulRegistrationPerAccess = 0.0;
					scscfFailedRegistrationPerAccess_400_403 = 0.0;
					scscfFailedRegistrationPerAccess_401 = 0.0;
					scscfAttemptedRegistrationPerAccess = 0.0;
					
					if(moid.contains("400") || moid.contains("403")){
						scscfFailedRegistrationPerAccess_400_403 = result.getDouble("scscfFailedRegistrationPerAccess");
					}else if(moid.contains("401")){
						scscfFailedRegistrationPerAccess_401 = result.getDouble("scscfFailedRegistrationPerAccess");
					}
					
					scscfSuccessfulRegistrationPerAccess = result.getDouble("scscfSuccessfulRegistrationPerAccess");
					scscfAttemptedRegistrationPerAccess = result.getDouble("scscfAttemptedRegistrationPerAccess");
					
					counterDataPerNode.put("UTC_DATETIME_ID", utcdatetime);
					counterDataPerNode.put("DATETIME_ID", result.getString("DATETIME_ID"));
					counterDataPerNode.put("NODE_ID", getNodeID(result));
					counterDataPerNode.put("KPI_ID",KPI_ID);
					counterDataPerNode.put("TIMELEVEL", result.getString("TIMELEVEL"));
					counterDataPerNode.put("DC_RELEASE", result.getString("DC_RELEASE"));
					
					counterDataPerNode.put("scscfSuccessfulRegistrationPerAccess", scscfSuccessfulRegistrationPerAccess.toString());
					counterDataPerNode.put("scscfFailedRegistrationPerAccess_400_403", scscfFailedRegistrationPerAccess_400_403.toString());
					counterDataPerNode.put("scscfAttemptedRegistrationPerAccess", scscfAttemptedRegistrationPerAccess.toString());
					counterDataPerNode.put("scscfFailedRegistrationPerAccess_401", scscfFailedRegistrationPerAccess_401.toString());
					
					counterPerNode.put(nodeName, counterDataPerNode);
				}			
			}while(result.next());
		}else if (KPI_ID.equals("7")){
			
			Double scscfOrigSuccessfulEstablishedInvitePerAccess = 0.0;
            Double scscfOrigFailedInvitePerAccess = 0.0;
            Double scscfOrigAttemptedInvitePerAccess = 0.0;
            result.first();
            do{

				String moid = result.getString("MOID");
				String nodeName = result.getString("NODE_NAME");
				if(counterPerNode.containsKey(nodeName)){
					if(moid.contains("403") || moid.contains("404") || moid.contains("407") || moid.contains("484") || moid.contains("486") || moid.contains("600")){
						scscfOrigFailedInvitePerAccess += result.getDouble("scscfOrigFailedInvitePerAccess");
					}
					scscfOrigSuccessfulEstablishedInvitePerAccess += result.getDouble("scscfOrigSuccessfulEstablishedInvitePerAccess");		
					scscfOrigAttemptedInvitePerAccess += result.getDouble("scscfOrigAttemptedInvitePerAccess"); 
				
					counterDataPerNode.put("scscfOrigFailedInvitePerAccess", scscfOrigFailedInvitePerAccess.toString());
					counterDataPerNode.put("scscfOrigSuccessfulEstablishedInvitePerAccess", scscfOrigSuccessfulEstablishedInvitePerAccess.toString());
					counterDataPerNode.put("scscfOrigAttemptedInvitePerAccess", scscfOrigAttemptedInvitePerAccess.toString());
					
					counterPerNode.put(nodeName, counterDataPerNode);
					
				}else{
					counterDataPerNode = new HashMap<>();
					
					scscfOrigSuccessfulEstablishedInvitePerAccess = 0.0;
		            scscfOrigFailedInvitePerAccess = 0.0;
		            scscfOrigAttemptedInvitePerAccess = 0.0;
		            
					if(moid.contains("403") || moid.contains("404") || moid.contains("407") || moid.contains("484") || moid.contains("486") || moid.contains("600")){
						scscfOrigFailedInvitePerAccess = result.getDouble("scscfOrigFailedInvitePerAccess");
					}
					scscfOrigSuccessfulEstablishedInvitePerAccess = result.getDouble("scscfOrigSuccessfulEstablishedInvitePerAccess");
					scscfOrigAttemptedInvitePerAccess = result.getDouble("scscfOrigAttemptedInvitePerAccess");
					
					counterDataPerNode.put("UTC_DATETIME_ID", utcdatetime);
					counterDataPerNode.put("DATETIME_ID", result.getString("DATETIME_ID"));
					counterDataPerNode.put("NODE_ID", getNodeID(result));
					counterDataPerNode.put("KPI_ID",KPI_ID);
					counterDataPerNode.put("TIMELEVEL", result.getString("TIMELEVEL"));
					counterDataPerNode.put("DC_RELEASE", result.getString("DC_RELEASE"));
					
					counterDataPerNode.put("scscfOrigFailedInvitePerAccess", scscfOrigFailedInvitePerAccess.toString());
					counterDataPerNode.put("scscfOrigSuccessfulEstablishedInvitePerAccess", scscfOrigSuccessfulEstablishedInvitePerAccess.toString());
					counterDataPerNode.put("scscfOrigAttemptedInvitePerAccess", scscfOrigAttemptedInvitePerAccess.toString());
					
					counterPerNode.put(nodeName, counterDataPerNode);
				}
            }while(result.next());
			
		}else if (KPI_ID.equals("8")){
            Double scscfTermSuccessfulEstablishedInvitePerAccess = 0.0;
            Double scscfTermFailedInvitePerAccess = 0.0;
            Double scscfTermAttemptedInvitePerAccess = 0.0;
            result.first();
            do{

            	String moid = result.getString("MOID");
            	String nodeName = result.getString("NODE_NAME");
            	if(counterPerNode.containsKey(nodeName)){
            		if(moid.contains("403") || moid.contains("404") || moid.contains("407") || moid.contains("484") || moid.contains("486") || moid.contains("600")){
            			scscfTermFailedInvitePerAccess += result.getDouble("scscfTermFailedInvitePerAccess");
            		}
            		scscfTermSuccessfulEstablishedInvitePerAccess += result.getDouble("scscfTermSuccessfulEstablishedInvitePerAccess");
            		scscfTermAttemptedInvitePerAccess += result.getDouble("scscfTermAttemptedInvitePerAccess"); 

            		counterDataPerNode.put("scscfTermFailedInvitePerAccess", scscfTermFailedInvitePerAccess.toString());
            		counterDataPerNode.put("scscfTermSuccessfulEstablishedInvitePerAccess", scscfTermSuccessfulEstablishedInvitePerAccess.toString());
            		counterDataPerNode.put("scscfTermAttemptedInvitePerAccess", scscfTermAttemptedInvitePerAccess.toString());
            		
            		counterPerNode.put(nodeName, counterDataPerNode);
            		
            	}else{
            		counterDataPerNode = new HashMap<>();
            		
            		scscfTermSuccessfulEstablishedInvitePerAccess = 0.0;
                    scscfTermFailedInvitePerAccess = 0.0;
                    scscfTermAttemptedInvitePerAccess = 0.0;
                    
            		if(moid.contains("403") || moid.contains("404") || moid.contains("407") || moid.contains("484") || moid.contains("486") || moid.contains("600")){
            			scscfTermFailedInvitePerAccess = result.getDouble("scscfTermFailedInvitePerAccess");
            		}
            		scscfTermSuccessfulEstablishedInvitePerAccess = result.getDouble("scscfTermSuccessfulEstablishedInvitePerAccess");
            		scscfTermAttemptedInvitePerAccess = result.getDouble("scscfTermAttemptedInvitePerAccess");
            		
            		counterDataPerNode.put("UTC_DATETIME_ID", utcdatetime);
					counterDataPerNode.put("DATETIME_ID", result.getString("DATETIME_ID"));
					counterDataPerNode.put("NODE_ID", getNodeID(result));
					counterDataPerNode.put("KPI_ID",KPI_ID);
					counterDataPerNode.put("TIMELEVEL", result.getString("TIMELEVEL"));
					counterDataPerNode.put("DC_RELEASE", result.getString("DC_RELEASE"));

            		counterDataPerNode.put("scscfTermFailedInvitePerAccess", scscfTermFailedInvitePerAccess.toString());
            		counterDataPerNode.put("scscfTermSuccessfulEstablishedInvitePerAccess", scscfTermSuccessfulEstablishedInvitePerAccess.toString());
            		counterDataPerNode.put("scscfTermAttemptedInvitePerAccess", scscfTermAttemptedInvitePerAccess.toString());
            		
            		counterPerNode.put(nodeName, counterDataPerNode);
            	}
            	
            }while(result.next());
		}
		
		for(Map.Entry<String, HashMap<String, String>> entry : counterPerNode.entrySet()){
			
			try{
				mFile.addData(entry.getValue());
				Double KPI_VALUES = getKPIValue(Integer.parseInt(KPI_ID), entry.getValue());
				mFile.addData("DIRNAME", sf.getDir());

				String breach = null;
				if (KPI_VALUES != null){
					KPI_VALUE = KPI_VALUES.toString();
					mFile.addData("KPI_VALUE", KPI_VALUE);
		            breach = String.valueOf(Utils.isBreach(Integer.parseInt(activeKpiThresholds.get(KPI_ID).split(":")[1]), 
							Double.parseDouble(activeKpiThresholds.get(KPI_ID).split(":")[0]), 
							Double.parseDouble(KPI_VALUE)));
					mFile.addData("BREACH_INDICATION",breach );
				}

				mFile.saveData();
			}catch(Exception e){
				log.log(Level.WARNING, "Unable to get breach for KPI "+KPI_ID+" for "+result.getString("NODE_NAME"));
			}
		}
	}
	
	private Double getKPIValue(int KPI_ID, HashMap<String, String> counterDataPerNode) throws NumberFormatException{
		Double KPI_VALUE = null;
		switch(KPI_ID){
		case 6:
			String scscfSuccessfulRegistrationPerAccess = counterDataPerNode.get("scscfSuccessfulRegistrationPerAccess");
			String scscfAttemptedRegistrationPerAccess = counterDataPerNode.get("scscfAttemptedRegistrationPerAccess");
			String scscfFailedRegistrationPerAccess_401 = counterDataPerNode.get("scscfFailedRegistrationPerAccess_401");
			String scscfFailedRegistrationPerAccess_400_403 =  counterDataPerNode.get("scscfFailedRegistrationPerAccess_400_403");
			
			if((scscfSuccessfulRegistrationPerAccess == null) && (scscfAttemptedRegistrationPerAccess == null) && (scscfFailedRegistrationPerAccess_401 == null) && (scscfFailedRegistrationPerAccess_400_403 == null) ) {
				KPI_VALUE = null;
			}else if(( (scscfSuccessfulRegistrationPerAccess != null) || (scscfFailedRegistrationPerAccess_400_403 != null)) && ( (scscfAttemptedRegistrationPerAccess != null ) || (scscfFailedRegistrationPerAccess_401 != null) ) ){
				KPI_VALUE =  100*((Double.parseDouble(scscfSuccessfulRegistrationPerAccess != null ? scscfSuccessfulRegistrationPerAccess :"0.00")+Double.parseDouble(scscfFailedRegistrationPerAccess_400_403 != null ? scscfFailedRegistrationPerAccess_400_403 : "0.00"))/(Double.parseDouble(scscfAttemptedRegistrationPerAccess != null ? scscfAttemptedRegistrationPerAccess : "0.00")-Double.parseDouble(scscfFailedRegistrationPerAccess_401 != null ? scscfFailedRegistrationPerAccess_401 : "0.00"))); 
			}else	
				KPI_VALUE = null;
			
			break;			
			
		case 7:
			String scscfOrigSuccessfulEstablishedInvitePerAccess = counterDataPerNode.get("scscfOrigSuccessfulEstablishedInvitePerAccess"); 
			String scscfOrigFailedInvitePerAccess = counterDataPerNode.get("scscfOrigFailedInvitePerAccess");
			String scscfOrigAttemptedInvitePerAccess = counterDataPerNode.get("scscfOrigAttemptedInvitePerAccess");
			
			if((scscfOrigSuccessfulEstablishedInvitePerAccess == null )&& (scscfOrigFailedInvitePerAccess == null )&& (scscfOrigAttemptedInvitePerAccess == null))
				KPI_VALUE = null;
			else if( ((scscfOrigSuccessfulEstablishedInvitePerAccess != null ) || (scscfOrigFailedInvitePerAccess != null))  &&  (scscfOrigAttemptedInvitePerAccess != null)){
				KPI_VALUE = 100*((Double.parseDouble(scscfOrigSuccessfulEstablishedInvitePerAccess != null ? scscfOrigSuccessfulEstablishedInvitePerAccess : "0.00")+Double.parseDouble(scscfOrigFailedInvitePerAccess != null ? scscfOrigFailedInvitePerAccess : "0.00"))/(Double.parseDouble(scscfOrigAttemptedInvitePerAccess != null ? scscfOrigAttemptedInvitePerAccess : "0.00")));
			}else
				KPI_VALUE = null;
				
			break;
		case 8: 
			String scscfTermSuccessfulEstablishedInvitePerAccess = counterDataPerNode.get("scscfTermSuccessfulEstablishedInvitePerAccess");
			String scscfTermFailedInvitePerAccess = counterDataPerNode.get("scscfTermFailedInvitePerAccess") ;
			String scscfTermAttemptedInvitePerAccess = counterDataPerNode.get("scscfTermAttemptedInvitePerAccess") ; 
			if((scscfTermSuccessfulEstablishedInvitePerAccess == null) && (scscfTermFailedInvitePerAccess == null) && (scscfTermAttemptedInvitePerAccess == null) )
				KPI_VALUE = null;
			else if ( ((scscfTermSuccessfulEstablishedInvitePerAccess != null ) || (scscfTermFailedInvitePerAccess != null) ) && (scscfTermAttemptedInvitePerAccess != null ) )
				KPI_VALUE = 100*((Double.parseDouble(scscfTermSuccessfulEstablishedInvitePerAccess != null ? scscfTermSuccessfulEstablishedInvitePerAccess : "0.00")+Double.parseDouble(scscfTermFailedInvitePerAccess != null ? scscfTermFailedInvitePerAccess : "0.00"))/(Double.parseDouble(scscfTermAttemptedInvitePerAccess != null ? scscfTermAttemptedInvitePerAccess : "0.00")));
			else 
				KPI_VALUE = null;
			break;
		}
		
		return KPI_VALUE;
	}
	
	private void createPMFiles(String KPI_ID, ResultSet result) throws Exception{
		try{
			mFile.addData("DIRNAME", sf.getDir());
			mFile.addData("NODE_ID", getNodeID(result));
			mFile.addData("KPI_ID", KPI_ID);
			mFile.addData("UTC_DATETIME_ID", utcdatetime);
			mFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
			mFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
			mFile.addData("DC_RELEASE", result.getString("DC_RELEASE"));
			if(KPI_ID == "1"){
				Double value = getKPIValue(result);
				String breach = null;
				if (value != null){
					KPI_VALUE = value.toString();
					mFile.addData("KPI_VALUE", KPI_VALUE);
		            breach = String.valueOf(Utils.isBreach(Integer.parseInt(activeKpiThresholds.get(KPI_ID).split(":")[1]), 
							Double.parseDouble(activeKpiThresholds.get(KPI_ID).split(":")[0]), 
							Double.parseDouble(KPI_VALUE)));
					mFile.addData("BREACH_INDICATION",breach );
				}
			}else{
				String breach = null;
				KPI_VALUE = result.getString("KPI_VALUE_"+KPI_ID);
				if (KPI_VALUE != null){
					mFile.addData("KPI_VALUE", KPI_VALUE);
		            breach = String.valueOf(Utils.isBreach(Integer.parseInt(activeKpiThresholds.get(KPI_ID).split(":")[1]), 
							Double.parseDouble(activeKpiThresholds.get(KPI_ID).split(":")[0]), 
							Double.parseDouble(KPI_VALUE)));
					mFile.addData("BREACH_INDICATION",breach );
				}
			}
			mFile.saveData();
		}catch(Exception e){
			log.log(Level.WARNING, "Unable to write measurement file  pm file", e);
		}
	}
	
	private Double getKPIValue(ResultSet result) throws NumberFormatException, SQLException {
		Double KPI_VALUE = null;
		String KPI_VALUE_2 = result.getString("KPI_VALUE_2");
		String KPI_VALUE_3 = result.getString("KPI_VALUE_3") ;
		String KPI_VALUE_4 = result.getString("KPI_VALUE_4") ;
		if((KPI_VALUE_2 == null)|| (KPI_VALUE_3 == null) || (KPI_VALUE_4 == null) )
			KPI_VALUE = null;
		else
			KPI_VALUE = ((Double.parseDouble(KPI_VALUE_2))/100)*(Double.parseDouble((KPI_VALUE_3))/100)*(Double.parseDouble((KPI_VALUE_4))/100)*100;
		
		return KPI_VALUE;
		
	}
	
	private void createPMFiles(String KPI_ID, String query) throws SQLException{
		log.finest("Query for "+KPI_ID+ " ---- "+query);
		ResultSet result = Utils.executeQuery(dwhdb, query);
		
		try{
			while(result.next()){
				if(KPI_ID.equals("6")){
					calculateCSCFKpis(KPI_ID, result);
				}else{
					try{
						mFile.addData("DIRNAME", sf.getDir());
						mFile.addData("NODE_ID", getNodeID(result));
						mFile.addData("KPI_ID", KPI_ID);
						mFile.addData("UTC_DATETIME_ID", utcdatetime);
						mFile.addData("DATETIME_ID", result.getString("DATETIME_ID"));
						mFile.addData("TIMELEVEL", result.getString("TIMELEVEL"));
						mFile.addData("DC_RELEASE", result.getString("DC_RELEASE"));
						String KPI_VALUE = result.getString("KPI_VALUE_"+KPI_ID);
						String breach ;
						if (KPI_VALUE != null){
							mFile.addData("KPI_VALUE", KPI_VALUE);
				            breach = String.valueOf(Utils.isBreach(Integer.parseInt(activeKpiThresholds.get(KPI_ID).split(":")[1]), 
									Double.parseDouble(activeKpiThresholds.get(KPI_ID).split(":")[0]), 
									Double.parseDouble(KPI_VALUE)));
							mFile.addData("BREACH_INDICATION",breach );
						}
						mFile.saveData();
					}catch(Exception e){
						log.log(Level.WARNING, "Unable to get breach for KPI "+KPI_ID+" for "+result.getString("NODE_NAME"));
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
	
	private void createTopology(String query) {
		
		ResultSet result = Utils.executeQuery(dwhdb, query);
		try {
			while(result.next()){
				//maxID = maxID+1;
				//mFile.addData("NODE_ID", Integer.toString(maxID));
				if(query.contains("WMG") || query.contains("GGSN")){
					mFile.addData("SYSTEM_AREA", "EPC");
				}else if(query.contains("IMS") || query.contains("CSCF")){
					mFile.addData("SYSTEM_AREA", "IMS");
				}
				mFile.addData("NODE_TYPE", result.getString("NE_TYPE"));
				mFile.addData("NODE_NAME", result.getString("NE_NAME"));
				mFile.addData("FDN", result.getString("NE_FDN"));
				nodeFDNs.add(result.getString("NE_FDN"));
				mFile.addData("NODE_VERSION", result.getString("NE_VERSION"));
				mFile.addData("DIRNAME", sf.getDir());
				mFile.saveData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(result != null)
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	private String getNodeID(ResultSet result) throws SQLException{
		String nodeID = null;
		
		nodeID = topologyInfo.get(result.getString("NODE_NAME"));
		if(!(nodeID != null) || nodeID == ""){
			nodeID = "0";
		}
		
		return nodeID;
		
	}
	

}
