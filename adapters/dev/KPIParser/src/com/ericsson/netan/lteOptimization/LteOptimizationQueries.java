package com.ericsson.netan.lteOptimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.networkanalytics.KPIQueries;
import com.ericsson.networkanalytics.TopologyQueries;
import com.ericsson.networkanalytics.Utils;

public class LteOptimizationQueries {

	public String ERBS_Data_Tables = "'DC_E_ERBS_EUTRANCELLFDD:RAW' , 'DC_E_ERBS_EUTRANCELLTDD:RAW' , 'DC_E_ERBSG2_EUTRANCELLFDD:RAW' , 'DC_E_ERBSG2_EUTRANCELLTDD:RAW'";
	public String ERBS_Vector_Tables = "'DC_E_ERBS_EUTRANCELLFDD_V:RAW' , 'DC_E_ERBS_EUTRANCELLTDD_V:RAW' , 'DC_E_ERBSG2_EUTRANCELLFDD_V:RAW' , 'DC_E_ERBSG2_EUTRANCELLTDD_V:RAW'";
	public String Counters_Data_Tables = "'DC_E_ERBS_EUTRANCELLRELATION:RAW' , 'DC_E_ERBSG2_EUTRANCELLRELATION:RAW'";
	public String Counters_Avg_Data_Tables = "'DC_E_ERBS_SECTORCARRIER_V:RAW' , 'DC_E_ERBSG2_SECTORCARRIER_V:RAW'";
	public String ERBS_Day_Data_Tables = "'DC_E_ERBS_EUTRANCELLFDD:DAY' , 'DC_E_ERBS_EUTRANCELLTDD:DAY' , 'DC_E_ERBSG2_EUTRANCELLFDD:DAY' , 'DC_E_ERBSG2_EUTRANCELLTDD:DAY'";
	public String MobilityDayTables = "'DC_E_ERBS_EUTRANCELLRELATION:DAY' , 'DC_E_ERBS_UTRANCELLRELATION:DAY' , 'DC_E_ERBS_GERANCELLRELATION:DAY' , 'DC_E_ERBS_CDMA20001XRTTCELLRELATION:DAY' ," +
			"'DC_E_ERBSG2_EUTRANCELLRELATION:DAY' , 'DC_E_ERBSG2_UTRANCELLRELATION:DAY' , 'DC_E_ERBSG2_GERANCELLRELATION:DAY' , 'DC_E_ERBSG2_CDMA20001XRTTCELLRELATION:DAY'";
	public String MobilityRawTables = "'DC_E_ERBS_EUTRANCELLRELATION:RAW' , 'DC_E_ERBS_UTRANCELLRELATION:RAW' , 'DC_E_ERBS_GERANCELLRELATION:RAW' , 'DC_E_ERBS_CDMA20001XRTTCELLRELATION:RAW' ," +
			"'DC_E_ERBSG2_EUTRANCELLRELATION:RAW' , 'DC_E_ERBSG2_UTRANCELLRELATION:RAW' , 'DC_E_ERBSG2_GERANCELLRELATION:RAW' , 'DC_E_ERBSG2_CDMA20001XRTTCELLRELATION:RAW'";
	
	@TopologyQueries
	public String getERBSTopology(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String query = "select ERBS_FDN, ERBS_NAME, ERBS_VERSION, NEMIMVERSION, VENDOR from DIM_E_LTE_ERBS where STATUS='ACTIVE' and OSS_ID = '"+OSS_ID+"' order by ERBS_FDN";
		
		return query;
	}
	
	@TopologyQueries
	public String getCellTopology(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String query = "select CELL_TYPE, ERBS_ID, EUtranCellId, earfcn as FREQ_BAND from DIM_E_LTE_EUCELL_CELL where STATUS='ACTIVE' and OSS_ID = '"+OSS_ID+"' order by ERBS_ID";
		
		return query;
	}
	
	public static String getCellRelationsTopology(String datetime_id, HashMap<String, String> queryProperties){
		String query = "SELECT GeranCellRelation as REL_NAME, TRIM(ISNULL(EUTRANCELLTDD,'') + ISNULL(EUTRANCELLFDD,'')) as CELL_NAME, adjacentCell as ADJ_CELL, '' as CELLREF, GeranFreqGroupRelation as FREQ_REL, ERBS_ID, OSS_ID FROM DIM_E_LTE_GERANCELLRELATION where STATUS='ACTIVE'  and CELL_NAME in ($CELL_LIST) " + 
				"union all " + 
				"SELECT UtranCellRelation as REL_NAME, TRIM(ISNULL(EUTRANCELLTDD,'') + ISNULL(EUTRANCELLFDD,'')) as CELL_NAME, adjacentCell as ADJ_CELL, '' as CELLREF, UtranFreqRelation as FREQ_REL, ERBS_ID, OSS_ID FROM DIM_E_LTE_UTRANCELLRELATION where STATUS='ACTIVE' and CELL_NAME in ($CELL_LIST) " + 
				"union all " + 
				"SELECT EUCELLRELATION as REL_NAME, TRIM(ISNULL(EUTRANCELLTDD,'') + ISNULL(EUTRANCELLFDD,'')) as CELL_NAME, ADJACENTCELL_ID as ADJ_CELL, neighborCellRef as CELLREF, EUtranFreqRelation as FREQ_REL, ERBS_ID, OSS_ID FROM DIM_E_LTE_EUREL where STATUS='ACTIVE'  and CELL_NAME in ($CELL_LIST)";
		
		query = query.replace("$CELL_LIST", queryProperties.get("CELL_LIST"));
		
		return query;
	}
	
	public List<String> getBulk_CM(String OSS_ID, String currentTime, String oneHourBack,HashMap<String, String> queryProperties){
		String ERBSnames = queryProperties.get("ERBSNames");
		List<String> queries = new ArrayList<>();
		String query1 = "configuredOutputPower&Total Transmit Power#SELECT ELEMENT,DATETIME_ID, UTC_DATETIME_ID, 'NotValid' as CELL_NAME, configuredOutputPower , NULL as AntennaUnitGroup from DC_E_BULK_CM_SECTOREQUIPMENTFUNCTION_RAW where OSS_ID = '"+OSS_ID+"' and UTC_DATETIME_ID<='"+currentTime+"' and UTC_DATETIME_ID>='"+oneHourBack+"' and ELEMENT in ("+ERBSnames+")";			
		String query2 =	"iuantAntennaBearing&Antenna Azimuth - TMA Subunit,iuantAntennaOperatingBand&Frequency Band - TMA Subunit#SELECT ELEMENT,DATETIME_ID, UTC_DATETIME_ID, 'NotValid' as CELL_NAME, iuantAntennaBearing, iuantAntennaOperatingBand, AntennaUnitGroup from DC_E_BULK_CM_TMASUBUNIT_RAW where OSS_ID = '"+OSS_ID+"' and UTC_DATETIME_ID<='"+currentTime+"' and UTC_DATETIME_ID>='"+oneHourBack+"' and ELEMENT in ("+ERBSnames+")";				
		String query3 =	"iuantAntennaBearing&Antenna Azimuth - RET Subunit,iuantAntennaOperatingBand&Frequency Band - RET Subunit,electricalAntennaTilt&Antenna Tilt - Electrical#SELECT ELEMENT,DATETIME_ID, UTC_DATETIME_ID, 'NotValid' as CELL_NAME, iuantAntennaBearing, iuantAntennaOperatingBand, electricalAntennaTilt, AntennaUnitGroup from DC_E_BULK_CM_RETSUBUNIT_RAW where OSS_ID = '"+OSS_ID+"' and UTC_DATETIME_ID<='"+currentTime+"' and UTC_DATETIME_ID>='"+oneHourBack+"' and ELEMENT in ("+ERBSnames+")";				
		String query4 =	"mechanicalAntennaTilt&Antenna Tilt - Mechanical#SELECT ELEMENT,DATETIME_ID, UTC_DATETIME_ID, 'NotValid' as CELL_NAME, mechanicalAntennaTilt , AntennaUnitGroup from DC_E_BULK_CM_ANTENNAUNIT_RAW where OSS_ID = '"+OSS_ID+"' and UTC_DATETIME_ID<='"+currentTime+"' and UTC_DATETIME_ID>='"+oneHourBack+"' and ELEMENT in ("+ERBSnames+")"; 			
		String query5 =	"altitude&Antenna Height,pMaxServingCell&UE Transmit Power#SELECT ELEMENT,DATETIME_ID, UTC_DATETIME_ID, EUtranCellFDDId as CELL_NAME, altitude, pMaxServingCell, NULL as AntennaUnitGroup  from DC_E_BULK_CM_EUTRANCELLFDD_RAW where OSS_ID = '"+OSS_ID+"' and UTC_DATETIME_ID<='"+currentTime+"' and UTC_DATETIME_ID>='"+oneHourBack+"' and ELEMENT in ("+ERBSnames+")" +			
		"union all SELECT ELEMENT, DATETIME_ID, UTC_DATETIME_ID, EUtranCellTDDId as CELL_NAME, altitude, pMaxServingCell, NULL as AntennaUnitGroup  from DC_E_BULK_CM_EUTRANCELLTDD_RAW where OSS_ID = '"+OSS_ID+ "'and UTC_DATETIME_ID<='"+currentTime+"' and UTC_DATETIME_ID>='"+oneHourBack+"' and ELEMENT in ("+ERBSnames+")";	
		
		queries.add(query1);	
		queries.add(query2);
		queries.add(query3);
		queries.add(query4);
		queries.add(query5);

		return queries;		
	}
	
	@KPIQueries
	public String ERBSKPIs(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String KPI_ID = "1,2,3,4,5,6,7,8,9,10,12";
		String KPI = "select " + 
				"	ERBS as NODE_NAME, " + 
				"	$EUTRANCELL as CELL_NAME," + 
				"	TIMELEVEL, DC_RELEASE, DATETIME_ID, " + 
				"	100*((pmRrcConnEstabSucc/ (pmRrcConnEstabAtt - pmRrcConnEstabAttReatt - (pmRrcConnEstabFailMmeOvlMos + pmRrcConnEstabFailMmeOvlMod)))" + 
				"	* (pmS1SigConnEstabSucc / (pmS1SigConnEstabAtt - pmS1SigConnEstabFailMmeOvlMos))" + 
				"	* (pmErabEstabSuccInit/ pmErabEstabAttInit)) as KPI_1," + 
				"	100 * (pmErabEstabSuccAdded/ (pmErabEstabAttAdded - pmErabEstabAttAddedHoOngoing)) as KPI_2," + 
				"	100 * ((pmErabRelAbnormalEnbAct + pmErabRelAbnormalMmeAct) /  (pmErabRelAbnormalEnb + pmErabRelMme + pmErabRelNormalEnb))  as KPI_3," + 
				"	pmPdcpVolDlDrb/(pmSchedActivityCellDl/ 1000) as KPI_4," + 
				"	pmPdcpVolDlDrb/(pmMacCellThpTimeDl/ 1000) as KPI_5," + 
				"	pmPdcpVolUlDrb/(pmSchedActivityCellUl/ 1000) as KPI_6," + 
				"	pmPdcpVolUlDrb/pmMacCellThpTimeUl as KPI_7, " + 
				"	(pmPdcpVolDlDrb - pmPdcpVolDlDrbLastTTI)/(pmUeThpTimeDl / 1000) as KPI_8, " +
				"	pmPdcpVolDlDrb/ (pmMacUeThpTimeDl /1000) as KPI_9, " +
				"	pmUeThpVolUl/ (PmUeThpTimeUl / 1000) as KPI_10, " +
				"	pmPdcpLatTimeDl/ pmPdcpLatPktTransDl as KPI_12 " +
				"from $TABLENAME " + 
				"where " + 
				"	UTC_DATETIME_ID = '$DATETIMEID' and " + 
				"	OSS_ID = '$OSS' and " + 
				"	ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and " + 
				"	ERBS in (  $ERBSLIST  )";
		
		String query = populateSQL(KPI, queryProperties.get("ERBSTables"), queryProperties.get("ERBSNames"), datetime_id, OSS_ID);
		
		return KPI_ID+"-"+query;
	}
	
	@KPIQueries
	public String KPI_11(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String KPI_ID = "11";
		String KPIBody = "select NODE_NAME, CELL_NAME, (sum(pmPdcpVolUlDrbQci)/pmMacUeThpTimeUl)/1000 as KPI_11, TIMELEVEL, DC_RELEASE, DATETIME_ID" + 
				" from( 	" + 
				"	select  	" + 
				"	FACTOR_1.ERBS as NODE_NAME,  	" + 
				"	FACTOR_1.CELL_NAME, " + 
				"	FACTOR_1.TIMELEVEL, " + 
				"	FACTOR_1.DC_RELEASE, " + 
				"	FACTOR_1.DATETIME_ID, " +
				"	pmPdcpVolUlDrbQci, pmMacUeThpTimeUl " + 
				"	from( 		" + 
				"		$VECTOR  	" + 
				"	) as FACTOR_1, 	" + 
				"	( 		" + 
				"		$COUNTER	" + 
				"	) as FACTOR_2" + 
				"	where FACTOR_1.ERBS=FACTOR_2.ERBS   	" + 
				"	and  FACTOR_1.CELL_NAME=FACTOR_2.CELL_NAME " + 
				") as KPI " + 
				"group by NODE_NAME, CELL_NAME, TIMELEVEL, DATETIME_ID, DC_RELEASE, pmMacUeThpTimeUl";
		
		String counter = "select ERBS, $EUTRANCELL as CELL_NAME," + 
				"	TIMELEVEL, DC_RELEASE, DATETIME_ID, pmMacUeThpTimeUl " + 
				"from $TABLENAME " + 
				"where " + 
				"	UTC_DATETIME_ID = '$DATETIMEID' and " + 
				"	OSS_ID = '$OSS' and " + 
				"	ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and " + 
				"	ERBS in (  $ERBSLIST  ) ";
		
		String vector = "select ERBS, $EUTRANCELL as CELL_NAME, " + 
				"	TIMELEVEL, DC_RELEASE, DATETIME_ID, pmPdcpVolUlDrbQci " + 
				"from $TABLENAME " + 
				"where " + 
				"	UTC_DATETIME_ID ='$DATETIMEID' and " + 
				"	OSS_ID = '$OSS' and " + 
				"	ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and " + 
				"	ERBS in (  $ERBSLIST  ) ";
		
		String CounterSQL = populateSQL(counter, queryProperties.get("ERBSTables"), queryProperties.get("ERBSNames"), datetime_id, OSS_ID);
		String VectorSQL = populateSQL(vector, queryProperties.get("ERBS_Vector_Tables"), queryProperties.get("ERBSNames"), datetime_id, OSS_ID);
		
		String query = KPIBody.replace("$VECTOR", VectorSQL);
		query = query.replace("$COUNTER", CounterSQL);
		
		
		return KPI_ID+"-"+query;
	}	
	
	@KPIQueries
	public String countersKPI(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String KPI_ID = "15,16,17,18";
		String KPI = "select" + 
				"	ERBS as NODE_NAME, " + 
				"	TRIM(ISNULL(EUTRANCELLFDD,'') + ISNULL(EUTRANCELLTDD,'')) as CELL_NAME, " +  
				"	TIMELEVEL, DC_RELEASE, DATETIME_ID, " + 
				"	sum(pmHoTooEarlyHoIntraF) as KPI_15, sum(pmHoTooEarlyHoInterF) as KPI_16," + 
				"	sum(pmHoTooLateHoIntraF) as KPI_17, sum(pmHoTooLateHoInterF) as KPI_18 " + 
				"from $TABLENAME " + 
				"where " + 
				"	UTC_DATETIME_ID = '$DATETIMEID' and " + 
				"	OSS_ID = '$OSS' and " + 
				"	ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and " + 
				"	ERBS in (  $ERBSLIST  )" +
				"   GROUP BY ERBS, EUTRANCELLTDD, EUTRANCELLFDD, TIMELEVEL, DC_RELEASE, DATETIME_ID ";

		String query = populateSQL(KPI, queryProperties.get("CountersTables"), queryProperties.get("ERBSNames"), datetime_id, OSS_ID);
		
		return KPI_ID+"-"+query;
	}
	
	@KPIQueries
	public String SNIRcounters(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String KPI_ID = "19";
		String KPI = "SELECT ERBS, SectorCarrier, TIMELEVEL, DC_RELEASE, DCVECTOR_INDEX, DATETIME_ID,  " + 
				"	pmBranchDeltaSinrDistr0 as '0' , pmBranchDeltaSinrDistr1 as '1' , " + 
				"	pmBranchDeltaSinrDistr2 as '2' , pmBranchDeltaSinrDistr3 as '3' , " + 
				"	pmBranchDeltaSinrDistr4 as '4' , pmBranchDeltaSinrDistr5 as '5' , " + 
				"	pmBranchDeltaSinrDistr6 as '6'  " + 
				"FROM $TABLENAME " + 
				"WHERE 	 " + 
				"	UTC_DATETIME_ID = '$DATETIMEID'  " + 
				"	AND 	OSS_ID = '$OSS'  " + 
				"	AND 	ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED')  " + 
				"	AND 	ERBS IN (  $ERBSLIST  ) ";
		
		String query = populateSQL(KPI, queryProperties.get("CountersAvgTables"), queryProperties.get("ERBSNames"), datetime_id, OSS_ID);
		
		return KPI_ID+"-"+query;
	}
	
	
	@KPIQueries
	public String mobilityCellKPI(String OSS_ID, String datetime_id, HashMap<String, String> queryProperties){
		String KPI_ID = "13";
		String KPI = LteOptimizationMobility.MobilityCellKPI;
		
		String query = populateMobilityRawSQL(KPI, queryProperties.get("ERBSTables"), "", datetime_id, OSS_ID);
		query = populateMobilityRawSQL(query, queryProperties.get("MobilityRawTables"), "", datetime_id, OSS_ID);
		query = query.replace("$ERBSLIST", queryProperties.get("ERBSNames"));
		
		return KPI_ID+"-"+query;
	}
	
	private static String populateMobilityRawSQL(String SQL, String partitionNames, String ERBSNames, String datetime_id, String OSS_ID){
		HashMap<String,Set<String>> tablePartitions = new HashMap<>();
		Set<String> partitions;
		Pattern p = Pattern.compile("DC_E_.*?_RAW");
		Matcher m = p.matcher(SQL);
		
		while(m.find()){
			tablePartitions.put(m.group(), new HashSet<String>());
		}
		
		for(Entry<String, Set<String>> entry : tablePartitions.entrySet()){
			for(String partition : partitionNames.split(",")){
				if(!partition.equalsIgnoreCase("")){
					partitions = entry.getValue();
					if(partition.contains(entry.getKey())){
						partitions.add(partition);
					}
					
				}
			}
		}
		
		for(Entry<String, Set<String>> entry : tablePartitions.entrySet()){
			String key = entry.getKey();
			Object[] partitionTables = entry.getValue().toArray();
			String partitionQuery = "";
			if(partitionTables.length > 0){
				for(int i = 0; i < partitionTables.length; i++){
					partitionQuery += "select * from "+partitionTables[i];					
					for(int j = i; j < partitionTables.length - 1 ; j++){
						partitionQuery += " union all ";
					}
				}
				
				SQL = SQL.replace(key, "("+partitionQuery+")");
			}
			
		}
		SQL = SQL.replace("$OSS", OSS_ID);
		SQL = SQL.replace("$DATETIMEID", datetime_id);
			
		return SQL;
	}
	
	@KPIQueries
	public String mobilityCellHOKPI(String OSS_ID, String date_id, HashMap<String, String> queryProperties){
		String KPI_ID = "14";
		String KPI = LteOptimizationMobility.MobilityCellHOKPI;
		
		String query = populateMobilityDaySQL(KPI, queryProperties.get("ERBSDayTables"), "", date_id);
		query = populateMobilityDaySQL(query, queryProperties.get("MobilityDayTables"), "", date_id);
		
		return KPI_ID+"-"+query;
	}
	
	public static String mobilityCellRelationKPI( String date_id, HashMap<String, String> queryProperties){
		String KPI = LteOptimizationMobility.MobilityCellRelationsHOKPI;
		
		String query = populateMobilityDaySQL(KPI, queryProperties.get("ERBSDayTables"), "", date_id);
		query = populateMobilityDaySQL(query, queryProperties.get("MobilityDayTables"), "", date_id);
		query = query.replace("$CELL_LIST", queryProperties.get("CELL_LIST"));
		
		return query;
	}
	
	private static String populateMobilityDaySQL(String SQL, String partitionNames, String ERBSNames, String datetime_id){
		/*for(String tablename : partitionNames.split(",")){
			if(!tablename.equalsIgnoreCase("")){
				String swapTable = tablename.split("_DAY")[0] + "_DAY";
				SQL = SQL.replace(swapTable, tablename);
			}
		}*/
		HashMap<String,Set<String>> tablePartitions = new HashMap<>();
		Set<String> partitions;
		Pattern p = Pattern.compile("DC_E_.*?_DAY");
		Matcher m = p.matcher(SQL);
		
		while(m.find()){
			tablePartitions.put(m.group(), new HashSet<String>());
		}
		for(Entry<String, Set<String>> entry : tablePartitions.entrySet()){
			for(String partition : partitionNames.split(",")){
				if(!partition.equalsIgnoreCase("")){
					partitions = entry.getValue();
					if(partition.contains(entry.getKey())){
						partitions.add(partition);
					} 
				}
			}
		}
		for(Entry<String, Set<String>> entry : tablePartitions.entrySet()){
			String key = entry.getKey();
			Object[] partitionTables = entry.getValue().toArray();
			String partitionQuery = "";
			if(partitionTables.length > 0){
				for(int i = 0; i < partitionTables.length; i++){
					partitionQuery += "select * from "+partitionTables[i];					
					for(int j = i; j < partitionTables.length - 1 ; j++){
						partitionQuery += " union all ";
					}
				}
				SQL = SQL.replace(key, "("+partitionQuery+")");
			}
	}
		SQL = SQL.replace("$DATEID", Utils.calculatePreviousDay(datetime_id));
		return SQL;
	}
	
	private String populateSQL(String SQL, String partitionNames, String ERBSNames, String datetime_id, String OSS_ID){
		String query = "";
		for(String tablename : partitionNames.split(",")){
			if(!tablename.equalsIgnoreCase("")){
				if(query != ""){
					query = query + " union all ";
				}
				
				query = query + SQL;
				query = query.replace("$DATETIMEID", datetime_id);
				query = query.replace("$OSS", OSS_ID);
				query = query.replace("$ERBSLIST", ERBSNames);
				query = query.replace("$TABLENAME", tablename);
				if(tablename.contains("EUTRANCELLTDD")){
					query = query.replace("$EUTRANCELL", "EUTRANCELLTDD");
				}else{
					query = query.replace("$EUTRANCELL", "EUTRANCELLFDD");
				}
			}
			
		}
		
		return query;
	}
	
}
