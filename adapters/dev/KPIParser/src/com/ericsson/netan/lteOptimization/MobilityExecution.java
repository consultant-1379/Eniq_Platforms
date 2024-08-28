package com.ericsson.netan.lteOptimization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.ericsson.networkanalytics.Utils;




public class MobilityExecution {
	
	private static MobilityExecution instance = null;
	
	String datetime;

	private Logger log;

	
	private MobilityExecution(Logger log){		
		instance = this;
        this.log =log;
	}
	
	public void calculateKPIs(RockFactory dwhdb,String query, LteOptimization lteOptimization, Utils util){

		ResultSet result = Utils.executeQuery(dwhdb, query);
		calculateMobilityKpi(dwhdb, result, lteOptimization, util);
	}
	
	public void calculateCellRelations(RockFactory dwhdb, LteOptimization lteOptimization, Utils util) throws SQLException{
		datetime = util.calculatePreviousRopTime();
		String previousDay =util.calculatePreviousDay(datetime);
		ArrayList<String> cellList = getCellList(dwhdb, previousDay);
		if(!cellList.isEmpty()){
			lteOptimization.getQueryProperties().put("CELL_LIST", cellList.toString().replace("[", "'").replace("]", "'").replace(", ", "','"));
			String SQLquery = LteOptimizationQueries.mobilityCellRelationKPI(datetime, lteOptimization.getQueryProperties());
			log.log(Level.FINEST, "cell relations " + SQLquery);
			createCellRelationsMobility(Utils.executeQuery(dwhdb, SQLquery), lteOptimization, util);
		}else{
			log.log(Level.INFO, "No cells returned from cellHO. relationsHO  will not be run.");
		}
	}
	
	public static MobilityExecution getInstance(Logger log){
		if(instance ==null){
		
			return new MobilityExecution(log);
		}
		
		return null;
	}
	
	public void destroyInstance(){
		instance = null;
	}
	
	
	public void calculateMobilityKpi(RockFactory dwhdb, ResultSet results, LteOptimization lteOpt, Utils util){
		ArrayList<String> CellNames = new ArrayList<String>();
		datetime = util.calculatePreviousRopTime();
		try{
			MeasurementFile mobilityFile = Main.createMeasurementFile(lteOpt.getSf(), "cellHO", lteOpt.getTechPack(), lteOpt.getSetType(), lteOpt.getSetName(), lteOpt.getWorkerName(), log);
			while(results.next()){
				String nodeID=lteOpt.getNodeID("NODES", results.getString("NODE_NAME"));
				String cellID=lteOpt.getCellID("CELLS", results.getString("CELL_NAME"), nodeID);
				mobilityFile.addData("DIRNAME", lteOpt.getSf().getDir());
				mobilityFile.addData("NODE_ID", nodeID);
				mobilityFile.addData("CELL_ID", cellID);
				mobilityFile.addData("HO_FAIL_RATE", results.getString("KPI_VALUE"));
				mobilityFile.addData("PREPSUCC", results.getString("PrepSucc"));
				mobilityFile.addData("PREPATT", results.getString("PrepAtt"));
				mobilityFile.addData("EXESUCC", results.getString("ExeSucc"));
				mobilityFile.addData("EXEATT", results.getString("ExeAtt"));
				mobilityFile.addData("UTC_DATETIME_ID", datetime);
				mobilityFile.addData("OSS_ID", results.getString("OSS_ID"));
				mobilityFile.addData("DATE_ID", results.getString("DATE_ID"));
				mobilityFile.addData("TIMELEVEL", results.getString("TIMELEVEL"));
				mobilityFile.addData("DC_RELEASE", results.getString("DC_RELEASE"));
				mobilityFile.saveData();
				
				String Cell_Name = results.getString("CELL_NAME");
				CellNames.add(Cell_Name);

			}
			lteOpt.closeMeasFile(mobilityFile);			
			
			if(mobilityFile.getRowCount()!=0){
					//Get Cell Relations DIM info based on Top 100 worst Cells
					lteOpt.getQueryProperties().put("CELL_LIST", CellNames.toString().replace("[", "'").replace("]", "'").replace(", ", "','"));
					String SQLquery = LteOptimizationQueries.getCellRelationsTopology( datetime, lteOpt.getQueryProperties());	
					log.log(Level.FINEST, "Top_relations " + SQLquery);			
					createCellRelationsTopology(Utils.executeQuery(dwhdb, SQLquery), lteOpt, util);
										
			}else{
				log.log(Level.INFO, "No data returned for cellHO. relations  will not be run.");
			}
			
		}catch(Exception e){
			log.log(Level.WARNING, "Unable to calculate Mobility KPI.", e);
		}
				
	}
	
	private void createCellRelationsTopology(ResultSet result, LteOptimization lteOpt, Utils util) {
		MeasurementFile RelmFile = null;
		try {
			RelmFile = Main.createMeasurementFile(lteOpt.getSf(), "relations", lteOpt.getTechPack(), lteOpt.getSetType(), lteOpt.getSetName(), lteOpt.getWorkerName(), log);
			
			while(result.next()){
			    
				String nodeID=lteOpt.getNodeID("NODES", result.getString("ERBS_ID"));
				String cellID=lteOpt.getCellID("CELLS", result.getString("CELL_NAME"), nodeID);
				String targetCell=util.calculateAdjCell(result.getString("ADJ_CELL"), result.getString("CELLREF"));			
				RelmFile.addData("CELL_RELATION_NAME",  result.getString("REL_NAME"));
				RelmFile.addData("SOURCE_CELL", cellID);			
				RelmFile.addData("TARGET_CELL", targetCell );				
				RelmFile.addData("FREQUENCY_REL", result.getString("FREQ_REL"));
				RelmFile.addData("ERBS", nodeID);
				RelmFile.addData("OSS_ID", result.getString("OSS_ID"));
				RelmFile.addData("DIRNAME", lteOpt.getSf().getDir());
				RelmFile.saveData();
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
			lteOpt.closeMeasFile(RelmFile);
		}
	}

	private void createCellRelationsMobility(ResultSet results, LteOptimization lteOpt, Utils util) {
		MeasurementFile MobmFile = null;
		try {
			MobmFile = Main.createMeasurementFile(lteOpt.getSf(), "relationHO", lteOpt.getTechPack(), lteOpt.getSetType(), lteOpt.getSetName(), lteOpt.getWorkerName(), log);			
			while(results.next()){
				String nodeID = lteOpt.getNodeID("NODES", results.getString("NODE_NAME"));
				String cellID = lteOpt.getCellID("CELLS", results.getString("CELL_NAME"), nodeID);
				MobmFile.addData("DIRNAME", lteOpt.getSf().getDir());
				MobmFile.addData("NODE_ID", nodeID);
				MobmFile.addData("CELL_ID", cellID);
				MobmFile.addData("RELATION_ID", lteOpt.getRelationID("RELATIONS", results.getString("REL_NAME"), cellID,results.getString("FREQ_REL")));
				MobmFile.addData("HO_FAIL_RATE", results.getString("KPI_VALUE"));
				MobmFile.addData("PREPSUCC", results.getString("PrepSucc"));
				MobmFile.addData("PREPATT", results.getString("PrepAtt"));
				MobmFile.addData("EXESUCC", results.getString("ExeSucc"));
				MobmFile.addData("EXEATT", results.getString("ExeAtt"));
				MobmFile.addData("OSS_ID",  results.getString("OSS_ID") );
				MobmFile.addData("DATE_ID", results.getString("DATE_ID"));
				MobmFile.addData("UTC_DATETIME_ID", datetime);
				MobmFile.addData("TIMELEVEL", results.getString("TIMELEVEL"));
				MobmFile.addData("DC_RELEASE", results.getString("DC_RELEASE"));
				MobmFile.saveData();

			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Unable to write measurement file.", e);
		}finally{
			if(results != null)
				try {
					results.close();
				} catch (SQLException e) {
					log.log(Level.WARNING, "Unable to close results file.", e);
				}
			lteOpt.closeMeasFile(MobmFile);
		}
	}
	
	private ArrayList<String> getCellList(RockFactory dwhdb,String date) throws SQLException {

		String sqlCell = "select a.CELL_ID, b.CELL_NAME from DC_E_LTE_OPTIMIZATION_CELL_HO_DAILY_RAW a left join DIM_E_LTE_OPTIMIZATION_CELL b ON a.CELL_ID = b.CELL_ID where a.date_id='"+date+"'";
		log.log(Level.FINEST, "cell relations " + sqlCell);
		
		ResultSet result = Utils.executeQuery(dwhdb, sqlCell);

		ArrayList<String> cellList = new ArrayList<String>();
			while(result.next()){
				String cell = result.getString("CELL_NAME");
				if(!cell.equals("") && !cell.equals("Unknown") ){
					cellList.add(cell);
				}
			}					
			return cellList;		
	}
	

}
