package com.ericsson.eniq.techpacksdk.common;

import junit.framework.TestCase;

public class ConstantsTest extends TestCase {

	public void testRSTATEPATTERN(){
		final String expPatter = "[prPR]{1}[123456789]\\d?[a-zA-Z]{1,2}" ;
		assertEquals(" RState pattern is not as expected. ", expPatter, Constants.RSTATEPATTERN);
	}

	public void testDATATYPES(){
		final String [] expDataTypes = { "varchar", "numeric", "integer", "int", "tinyint", "smallint",
			      "unsigned int", "long", "double", "datetime", "date", "char", "float", "bit"};
		for(String type : expDataTypes){
			boolean found = false ;
			for(String dataType : Constants.DATATYPES){
				if(dataType.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Data Type : " + type + " should be present in DATATYPES. ");
			}
		}
	}

	public void testDATATYPES_Events(){
		final String [] expDataTypes = { "varchar", "numeric", "integer", "int", "tinyint", "smallint",
			      "unsigned int", "long", "double", "datetime", "date", "char", "float", "binary", "timestamp",
			      "unsigned bigint", "bit" };
		for(String type : expDataTypes){
			boolean found = false ;
			for(String dataType : Constants.EVENTSDATATYPES){
				if(dataType.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Data Type : " + type + " should be present in DATATYPES. ");
			}
		}
	}

	public void testCOUNTER_TYPES(){
		final String [] expCounterTypes = { "GAUGE", "PEG", "VECTOR", "UNIQUEVECTOR", "CMVECTOR", "PMRESVECTOR", "COMPRESSEDVECTOR"};
		for(String type : expCounterTypes){
			boolean found = false ;
			for(String counterType : Constants.COUNTER_TYPES){
				if(counterType.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Counter Type : " + type + " should be present in COUNTER_TYPES. ");
			}
		}
	}

	public void testSIZINGITEMS(){
		final String [] expSizeItems = { "extrasmall", "small", "medium", "large", "extralarge" };
		for(String type : expSizeItems){
			boolean found = false ;
			for(String sizeItems : Constants.SIZINGITEMS){
				if(sizeItems.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Size Item : " + type + " should be present in SIZINGITEMS. ");
			}
		}
	}

	public void testACTIONTYPEITEMS(){
		final Object [] expActionTypeItems = { "Aggregation", "AggregationRuleCopy", "AlarmHandler", "AggRuleCacheRefresh",
			      "AlarmInterfaceUpdate", "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", "CreateDir",
			      "CreateAlarmFile", "DirectoryDiskmanager", "Diskmanager", "Distribute", "DuplicateCheck", "DWHMigrate", "EBSUpdate",
			      "ExecutionProfiler", "GateKeeper", "HistorySqlExecute", "JDBC Mediation", "Join", "JVMMonitor", "Load", "Loader",
			      "ManualReAggregation", "Mediation", "Parse", "PartitionAction", "Partitioned Loader", "PartitionedSQLExec",
			      "RefreshDBLookup", "ReloadDBLookups", "ReloadProperties", "ReloadTransformation", "SanityCheck",
			      "SessionLog Loader", "SetTypeTrigger", "SMTP Mediation", "SNMP Poller", "SQL Execute", "SQL Extract",
			      "SQLJoiner", "SQLLogResultSet", "StorageTimeAction", "System Call", "System Monitor", "TableCheck", "TableCleaner",
			      "TriggerScheduledSet", "Uncompress", "UnPartitioned Loader", "UpdateDimSession", "UpdateMonitoredTypes",
			      "UpdateMonitoring", "UpdatePlan", "VersionUpdate" };
		for(Object type : expActionTypeItems){
			boolean found = false ;
			for(Object actionTypeItems : Constants.ACTIONTYPEITEMS){
				if(actionTypeItems.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Action Type Item : " + type + " should be present in ACTIONTYPEITEMS. ");
			}
		}
	}

	public void testACTIONTYPEITEMS_FROM_EVENTS(){
		final String CREATE_COLLECTED_DATA_FILES = "CreateCollectedData";
		final String TOPOLOGY_SQL_EXECUTE = "TopologySqlExecute";
		final String UNKNOWN_TOPOLOGY = "UnknownTopology";
		final String UPDATE_COLLECTED_DATA_FILES = "UpdateCollectedData";
		final Object [] expActionTypeItems = { "Aggregation", "AggregationRuleCopy", "AlarmHandler",
		      "AlarmInterfaceUpdate", "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", "CreateDir",
		      CREATE_COLLECTED_DATA_FILES, "DirectoryDiskmanager", "Diskmanager", "Distribute", "DuplicateCheck", "DWHMigrate",
		      "EBSUpdate", "ExecutionProfiler", "GateKeeper", "JDBC Mediation", "Join", "JVMMonitor", "Load",
		      "Loader", "ManualReAggregation", "Mediation", "Parse", "PartitionAction", "Partitioned Loader",
		      "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups", "ReloadProperties", "ReloadTransformation",
		      "SanityCheck", "SessionLog Loader", "SetTypeTrigger", "SMTP Mediation", "SNMP Poller", "SQL Execute",
		      "SQL Extract", "SQLJoiner", "StorageTimeAction", "System Call", "System Monitor", "TableCheck", "TableCleaner",
		      TOPOLOGY_SQL_EXECUTE, "TriggerScheduledSet", "Uncompress", "UnPartitioned Loader", UNKNOWN_TOPOLOGY,
		      "UpdateDimSession", "UpdateMonitoredTypes", "UpdateMonitoring", UPDATE_COLLECTED_DATA_FILES, "UpdatePlan",
		      "VersionUpdate" };
		for(Object type : expActionTypeItems){
			boolean found = false ;
			for(Object actionTypeItems : Constants.EVENTSACTIONTYPEITEMS){
				if(actionTypeItems.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Action Type Item : " + type + " should be present in ACTIONTYPEITEMS. ");
			}
		}
	}

	public void testTRANFORMER_TYPES(){
		final String HASH_ID = "hashid";
		final String CURRENTTIME = "currenttime";
		final String FIXED = "fixed";
		final String LOOKUP = "lookup";
		final String COPY = "copy";
		final String CONDITION = "condition";
		final String POSTAPPENDER = "postappender";
		final String PROPERTYTOKENIZER = "propertytokenizer";
		final String PREAPPENDER = "preappender";
		final String DSTPARAMETERS = "dstparameters";
		final String DATABASELOOKUP = "databaselookup";
		final String CALCULATION = "calculation";
		final String DATEFORMAT = "dateformat";
		final String DEFAULTTIMEHANDLER = "defaulttimehandler";
		final String ALARM = "alarm";
		final String BITMAPLOOKUP = "bitmaplookup";
		final String REDUCEDATE = "reducedate";
		final String FIELDTOKENIZER = "fieldtokenizer";
		final String ROUNDTIME = "roundtime";
		final String SWITCH = "switch";
		final String RADIXCONVERTER = "radixconverter";
		final String ROPTIME = "roptime";

		final String [] expTranfTypes = new String[] { CURRENTTIME, FIXED, HASH_ID, LOOKUP, COPY, CONDITION,
			      POSTAPPENDER, PROPERTYTOKENIZER, PREAPPENDER, DSTPARAMETERS, DATABASELOOKUP, CALCULATION, DATEFORMAT,
			      DEFAULTTIMEHANDLER, ALARM, BITMAPLOOKUP, REDUCEDATE, FIELDTOKENIZER, ROUNDTIME, SWITCH, RADIXCONVERTER, ROPTIME };
		for(String type : expTranfTypes){
			boolean found = false ;
			for(String transTypes : Constants.TRANFORMER_TYPES){
				if(transTypes.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Transforamtion Type : " + type + " should be present in TRANFORMER_TYPES. ");
			}
		}
	}

	public void testALLTECHPACKTYPES(){
		final String PM_TECHPACK = "PM";
		final String CM_TECHPACK = "CM";
		final String BASE_TECHPACK = "BASE";
		final String SYSTEM_TECHPACK = "SYSTEM";
		final String TOPOLOGY_TECHPACK = "Topology";
		final String EVENT_TECHPACK = "EVENT";
		final String CUSTOM_TECHPACK = "CUSTOM";
		final String [] expTPTypes = { PM_TECHPACK, CM_TECHPACK, BASE_TECHPACK, SYSTEM_TECHPACK,
			    TOPOLOGY_TECHPACK, EVENT_TECHPACK, CUSTOM_TECHPACK };
		for(String type : expTPTypes){
			boolean found = false ;
			for(String allTPTypes : Constants.ALLTECHPACKTYPES){
				if(allTPTypes.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" TP TYPE : " + type + " should be present in ALLTECHPACKTYPES. ");
			}
		}
	}

	public void testBH_AGGREGATION_TYPPES_SHOW(){
		final String BH_TYPE_TL    = "Timelimited";
		final String BH_TYPE_SW    = "Slidingwindow";
		final String BH_TYPE_TL_TC = "Timelimited + Timeconsistent";
		final String BH_TYPE_SW_TC = "Slidingwindow + Timeconsistent";
		final String BH_TYPE_PR = "Peakrop";
		final String [] expBHAggType = {
			    BH_TYPE_TL,
			    BH_TYPE_SW,
			    BH_TYPE_TL_TC,
			    BH_TYPE_SW_TC,
			    BH_TYPE_PR };
		for(String type : expBHAggType){
			boolean found = false ;
			for(String BHAggTypes : Constants.BH_AGGREGATION_TYPPES_SHOW){
				if(BHAggTypes.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" BH Aggregation Type : " + type + " should be present in BH_AGGREGATION_TYPPES_SHOW. ");
			}
		}
	}

//	public void testBH_GROUPING_TYPES(){
//		final String[] expBHGroupTypes = new String[] { "None", "Time", "Node", "Time + Node" };
//		for(String type : expBHGroupTypes){
//			boolean found = false ;
//			for(String BHGroupTypes : Constants.BH_GROUPING_TYPES){
//				if(BHGroupTypes.equals(type)){
//					found = true ;
//					break;
//				}
//			}
//			if(found == false){
//				fail(" BH Grouping Type : " + type + " should be present in BH_GROUPING_TYPES. ");
//			}
//		}
//	}

	public void testCOUNTER_TYPES_Events(){
		final String [] expCounterTypes = { "GAUGE", "PEG", "VECTOR", "UNIQUEVECTOR", "CMVECTOR" };
		for(String type : expCounterTypes){
			boolean found = false ;
			for(String counterType : Constants.COUNTER_TYPES){
				if(counterType.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Counter Type : " + type + " should be present in COUNTER_TYPES. ");
			}
		}
	}

	public void testEVENTSACTIONTYPEITEMS(){
		final String BACKUP_COUNT_DAY = "BackupCountDay";
		final String BACKUP_LOADER = "BackupLoader";
		final String COUNTING_ACTION = "CountingAction";
		final String COUNTING_TRIGGER = "CountingTrigger";
		final String COUNTING_INTERVALS = "CountingIntervals";
		final String BACKUP_TRIGGER = "BackupTrigger";
		final String STORE_COUNTING_DATA = "StoreCountingData";
		final String COUNT_REAGG_ACTION = "CountReAggAction";
		final String CREATE_COLLECTED_DATA_FILES = "CreateCollectedData";
		final String EVENT_LOADER = "EventLoader";
		final String TOPOLOGY_SQL_EXECUTE = "TopologySqlExecute";
		final String UNKNOWN_TOPOLOGY = "UnknownTopology";
		final String UPDATE_COLLECTED_DATA_FILES = "UpdateCollectedData";
		final Object [] expActionTypeItems = { "Aggregation", "AggregationRuleCopy", "AlarmHandler",
			      "AlarmInterfaceUpdate", "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", BACKUP_TRIGGER,
			      BACKUP_COUNT_DAY, BACKUP_LOADER, COUNTING_ACTION, COUNTING_TRIGGER, COUNTING_INTERVALS, COUNT_REAGG_ACTION,
			      STORE_COUNTING_DATA, "CreateDir", CREATE_COLLECTED_DATA_FILES, "DirectoryDiskmanager", "Diskmanager",
			      "Distribute", "DuplicateCheck", "DWHMigrate", "EBSUpdate", "ExecutionProfiler", "GateKeeper",
			      "JDBC Mediation", "Join", "JVMMonitor", "Load", "Loader", EVENT_LOADER, "ManualReAggregation", "Mediation",
			      "Parse", "PartitionAction", "Partitioned Loader", "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups",
			      "ReloadProperties", "ReloadTransformation", "SanityCheck", "SessionLog Loader", "SetTypeTrigger",
			      "SMTP Mediation", "SNMP Poller", "SQL Execute", "SQL Extract", "SQLJoiner", "StorageTimeAction", "System Call",
			      "System Monitor", "TableCheck", "TableCleaner", TOPOLOGY_SQL_EXECUTE, "TriggerScheduledSet", "Uncompress",
			      "UnPartitioned Loader", UNKNOWN_TOPOLOGY, "UpdateDimSession", "UpdateMonitoredTypes", "UpdateMonitoring",
			      UPDATE_COLLECTED_DATA_FILES, "UpdatePlan", "VersionUpdate" };
		for(Object type : expActionTypeItems){
			boolean found = false ;
			for(Object eventActionTypeItems : Constants.EVENTSACTIONTYPEITEMS){
				if(eventActionTypeItems.equals(type)){
					found = true ;
					break;
				}
			}
			if(found == false){
				fail(" Event Action Type Item : " + type + " should be present in EVENTSACTIONTYPEITEMS. ");
			}
		}
	}


}//end of class
