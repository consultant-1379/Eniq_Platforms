package com.ericsson.networkanalytics;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.ericsson.netan.energyefficiency.EnergyEfficiency;
import com.ericsson.netan.lteOptimization.LteOptimization;
import com.ericsson.netan.vowifi.VoWiFi;

public class KPIParser implements Parser {

	private Utils utils;
	private Logger log;
	private String techPack;
    private String setType;
    private String setName;
    private int status = 0;
    private Main mainParserObject = null;
    private String workerName = "";
    private boolean completed = false;
    private RockFactory dwhdb;
    private RockFactory repdb;
    
    
	@Override
	public void run() {
		try {

            this.status = 2;
            SourceFile sf = null;
            completed = false;
                  
            while ((sf = mainParserObject.nextSourceFile()) != null) {

                try {
                    mainParserObject.preParse(sf);
                    if(!completed){
                    	parse(sf, techPack, setType, setName);
                    }
                    mainParserObject.postParse(sf);
                } catch (final Exception e) {
                    mainParserObject.errorParse(e, sf);
                } finally {
                    mainParserObject.finallyParse(sf);
                }
            }
        } catch (final Exception e) {
            log.log(Level.WARNING, "Worker parser failed to exception", e);
        } finally {
            this.status = 3;
        }
		
	}

	@Override
	public void init(final Main main, final String techPack, final String setType, final String setName, final String workerName) {
		this.mainParserObject = main;
        this.techPack = techPack;
        this.setType = setType;
        this.setName = setName;
        this.status = 1;
        this.workerName = workerName;
		
        String logWorkerName = "";
        if (workerName.length() > 0) {
            logWorkerName = "." + workerName;
        }
        
        log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.KPIParser" + logWorkerName);
	}

	@Override
	public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
			throws Exception {
		
		TransformerCache.setCheckTransformations(false);
		completed = true;
		utils = new Utils(log);
		utils.createDataFile(sf.getDir());
		utils.loadProperties();
		try{
			dwhdb = utils.getDBConn("dwhdb");
			repdb = utils.getDBConn("repdb");
			
			if(techPack.toLowerCase().contains("vowifi")){
				VoWiFi vowifi = new VoWiFi(dwhdb, repdb, sf, techPack, setType, setName, workerName, log);
				
				vowifi.init(utils);

			}else if(techPack.toLowerCase().contains("lte_optimization")){
				LteOptimization LteOpt = new LteOptimization(dwhdb, repdb, sf, techPack, setType, setName, workerName, log);
				
				LteOpt.init(utils);
			}else if(techPack.toLowerCase().contains("energy")){
				EnergyEfficiency energyEff = new EnergyEfficiency(dwhdb, repdb, sf, techPack, setType, setName, workerName, log);
				
				energyEff.init(utils);
			}
		}catch(Exception e){
			log.log(Level.SEVERE, "Parser initiation failed", e);
		}finally{
			try{
				if(dwhdb!=null){
					if((dwhdb.getConnection()!=null ) && (!dwhdb.getConnection().isClosed())){
						dwhdb.getConnection().close();
						log.info("Dwhdb connection is closed "+dwhdb.getConnection().isClosed());
					}
				}
			}catch(Exception e){
				log.warning("Exception while closing the dwhdb connection "+e.getMessage());
			}
			try{
				if(repdb!=null){
					if((repdb.getConnection()!=null ) && (!repdb.getConnection().isClosed())){
						repdb.getConnection().close();
						log.info("repdb connection is closed "+repdb.getConnection().isClosed());
					}
				}
			}catch(Exception e){
				log.warning("Exception while closing the repdb connection "+e.getMessage());
			}
		}
	}
	
	public static List<String> getTopologyQueries(Object feature, String techPack, Method... methods) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<String> queries = new ArrayList<>();
		for(Method method : methods){
			
			if(method.isAnnotationPresent(TopologyQueries.class)){
				
				String query = (String) method.invoke(feature, techPack);
				queries.add(query);
			}
		}
		return queries;
	}
	
	public static List<String> getTopologyQueries(Object feature, String techPack, String datetime, HashMap<String, String> queryProperties, Method... methods) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<String> queries = new ArrayList<>();
		for(Method method : methods){
			
			if(method.isAnnotationPresent(TopologyQueries.class)){
				
				String query = (String) method.invoke(feature, techPack, datetime, queryProperties);
				queries.add(query);
			}
		}
		return queries;
	}
	
	public static List<String> getKPIQueries(Object feature, String techPack, String datetime, Method... methods) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<String> queries = new ArrayList<>();
		for(Method method : methods){
			
			if(method.isAnnotationPresent(KPIQueries.class)){
				
				String query = (String) method.invoke(feature, techPack, datetime);
				queries.add(query);
			}
		}
		return queries;
	}
	
	public static List<String> getKPIQueries(Object feature, String techPack, String datetime, HashMap<String, String> queryProperties, Method... methods) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<String> queries = new ArrayList<>();
		for(Method method : methods){
			
			if(method.isAnnotationPresent(KPIQueries.class)){
				
				String query = (String) method.invoke(feature, techPack, datetime, queryProperties);
				queries.add(query);
			}
		}
		return queries;
	}

	@Override
	 public int status() {
        return status;
    }
	
	
}