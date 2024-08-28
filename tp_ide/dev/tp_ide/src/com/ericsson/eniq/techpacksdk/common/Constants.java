/**
 * 
 */
package com.ericsson.eniq.techpacksdk.common;

/**
 * @author eheijun
 * 
 */
public final class Constants {

    public static final String VOL3LARGE = "vol3large";

    public static final String VOL3MEDIUM = "vol3medium";

    public static final String VOL3 = "vol3";

    public static final String VOL3SMALL = "vol3small";

    public static final String VOL3EXTRASMALL = "vol3extrasmall";

    public static final String VOL2 = "vol2";

    public static final String VOL2SMALL = "vol2small";

    public static final String VOL2EXTRASMALL = "vol2extrasmall";

    public static final String VOLEXTRALARGE = "volextralarge";

    public static final String VOLLARGE = "vollarge";

    public static final String VOLMEDIUM = "volmedium";

    public static final String VOL = "vol";

    public static final String VOLSMALL = "volsmall";

    public static final String VOLEXTRASMALL = "volextrasmall";

    public static final String VOL4 = "vol4";

    public static final String VOL4SMALL = "vol4small";

    public static final String VOL3EXTRALARGE = "vol3extralarge";

    public static final String VOL4MEDIUM = "vol4medium";

    public static final String EXTRA_ZERO = "0";

    public static final String SONV = "SONV";

    // This is the ETLDATA directory for Events. Currently this is hard coded as
    // no environment property has been setup by integration for this yet.
    public static final String EVENTS_ETLDATA_DIR = "/eniq/data/etldata_";

    // Number of directories to create under the directory, /eniq/data/etldata_/
    // For example, directories would look like
    // /eniq/data/etldata_/00/...../eniq/data/etldata_/15/
    public final static int NUM_OF_DIRECTORIES = 16;

    public static final String EVENTS_BACKUP_DIR = "/eniq/backup";

    // This is the ETLDATA directory for STATS. It should be /eniq/data/eniq/. An
    // Environment property has been setup by integration for this
    public final static String STATS_ETLDATA_DIR = "${ETLDATA_DIR}";

    public final static String OSS_DIR = "${OSS}";

    public final static String ENIQ_EVENTS_TOPOLOGY_DIR = "eniq_events_topology";

    public static final String SGEH = "sgeh";

    public static final String SGEHSMALL = "sgehsmall";

    public static final String SGEHLARGE = "sgehlarge";

    public static final String SGEHMEDIUM = "sgehmedium";

    public static final String SGEHEXTRALARGE = "sgehextralarge";

    public static final String SGEH2EXTRALARGE = "sgeh2extralarge";

    public static final String SGEHEXTRASMALL = "sgehextrasmall";

    public static final String EVENT_E = "EVENT_E";

    public static final String DIM_E_SGEH = "DIM_E_SGEH";

    public static final String ENIQ_EVENT = "ENIQ_EVENT";

    public static final String SONAGG = com.ericsson.eniq.common.Constants.SONAGG;

    public static final String SON15AGG = com.ericsson.eniq.common.Constants.SON15AGG;

    public static final String ROPAGGSCOPE = com.ericsson.eniq.common.Constants.ROPAGGSCOPE;

    public static final String[] ROPGRPSUPPORTED_TP = com.ericsson.eniq.common.Constants.ROPGRPSUPPORTED_TP;

    public static final String SONAGGDAY = "SONDAY";

    // Different action types
    public static final String COUNTING_INTERVALS = "CountingIntervals";

    public static final String STORE_COUNTING_DATA = "StoreCountingData";

    public static final String COUNTING_ACTION = "CountingAction";

    public static final String COUNTING_DAY_ACTION = "CountingDayAction";

    public static final String COUNTING_TRIGGER = "CountingTrigger";

    public static final String COUNTING_DAY_TRIGGER = "CountingDayTrigger";

    public static final String CREATE_COLLECTED_DATA_FILES = "CreateCollectedData";

    public static final String UPDATE_COLLECTED_DATA_FILES = "UpdateCollectedData";

    public static final String BACKUP_TRIGGER = "BackupTrigger";

    public static final String BACKUP_COUNT_DAY = "BackupCountDay";

    public static final String BACKUP_LOADER = "BackupLoader";

    public static final String BACKUP_TABLES = "BackupTables";

    public static final String RESTORE = "Restore";

    public static final String TOPOLOGY_SQL_EXECUTE = "TopologySqlExecute";

    public static final String UNKNOWN_TOPOLOGY = "UnknownTopology";

    public static final String GATE_KEEPER_PROPERTY = "GateKeeperProperty";

    public static final String UPDATE_COUNT_INTERVALS = "UpdateCountIntervals";

    public static final String IMSI_TO_IMEI = "IMSItoIMEI";

    public static final String TIMEBASE_PARTITION_LOADER = "TimeBase EventLoader";

    public static final String EVENT_LOADER = "EventLoader";

    public static final String COUNT_REAGG_ACTION = "CountReAggAction";

    public static final String UPDATE_HASH_IDS = "UpdateHashIds";

    public static final String HISTORY_SQL_EXECUTE = "HistorySqlExecute";

    private Constants() {
        // never construct this
    }

    /**
     * Regular expression pattern for Ericsson R-States. Accepts "normal" R-States according to the corporate basic standards (e.g. R1A --> R99ZZ),
     * including preliminary R-states (e.g. P1A --> P99ZZ), excluding R-states with verification level (amendment level) (e.g. R1A01, P99ZZ99,
     * R1AB123) and special R-states (e.g. R1A/1, R1A/A).
     */
    public static final String RSTATEPATTERN = "[prPR]{1}[123456789]\\d?[a-zA-Z]{1,2}";

    /**
     * The latest ENIQ database version for techpacks.
     */
    public static final String CURRENT_TECHPACK_ENIQ_LEVEL = "11";

    /**
     * The ENIQ database version where Busy Hour Improvements were introduced.
     */
    public static final String BH_IMPROVEMENT_ENIQ_LEVEL = "11";

    /**
     * The latest ENIQ database version for interfaces.
     */
    public static final String CURRENT_INTERFACE_ENIQ_LEVEL = "2.0";

    /**
     * returns ":" separator for measurement type id usage
     */
    public static final String TYPESEPARATOR = ":";

    /**
     * returns "_" separator for measurement type name usage
     */
    public static final String TYPENAMESEPARATOR = "_";

    /**
     * returns "1MIN"
     */
    public static final String ONEMIN = "1MIN";

    /**
     * returns "15MIN"
     */
    public static final String FIFTEENMIN = "15MIN";

    /**
     * returns "BIG_RAW"
     */
    public static final String RAW_LEV2 = "RAW_LEV2";

    /**
     * returns "PLAIN"
     */
    public static final String PLAINLEVEL = "PLAIN";

    /**
     * returns "RAW"
     */
    public static final String RAWLEVEL = "RAW";

    /**
     * returns "DAY"
     */
    public static final String DAYLEVEL = "DAY";

    /**
     * returns "DAYBH"
     */
    public static final String DAYBHLEVEL = "DAYBH";

    /**
     * returns "COUNT"
     */
    public static final String COUNTLEVEL = "COUNT";

    /**
     * returns "RANKBH"
     */
    public static final String RANKBHLEVEL = "RANKBH";

    public static final String RANKBHCLASS = "RANKBHCLASS";

    public static final String MONTHRANKBHLEVEL = "MONTHRANKBH";

    public static final String WEEKRANKBHLEVEL = "WEEKRANKBH";

    // public static final String RANKBHLEVEL_TIMECONSISTENT =
    // "RANKBH_TIMECONSISTENT";
    // public static final String RANKBHLEVEL_TIMELIMITED = "RANKBH_TIMELIMITED";
    // public static final String RANKBHLEVEL_SLIDINGWINDOW =
    // "RANKBH_SLIDINGWINDOW";

    /**
     * returns "RANKBHCLASS"
     */
    public static final String RANKBHCLASSLEVEL = "RANKBHCLASS";

    /**
     * returns "DAY"
     */
    public static final String DAYSCOPE = "DAY";
    public static final String BHSCOPE = "BH";
    /**
     * returns scope "WEEK"
     */
    public static final String WEEKSCOPE = "WEEK";

    /**
     * returns type "BHSRC"
     */
    public static final String BHSRC = "BHSRC";

    /**
     * returns type "RANKSRC"
     */
    public static final String RANKSRC = "RANKSRC";

    /**
     * returns type "DAYBHCLASS"
     */
    public static final String DAYBHCLASS = "DAYBHCLASS";

    /**
     * returns "MONTH"
     */
    public static final String MONTHSCOPE = "MONTH";

    /**
     * returns "COUNT"
     */
    public static final String COUNTSCOPE = "COUNT";

    /**
     * returns "TOTAL"
     */
    public static final String TOTALTYPE = "TOTAL";

    /**
     * returns WEEKBH
     */
    public static final String WEEKBH = "WEEKBH";

    /**
     * returns MONTHBH
     */
    public static final String MONTHBH = "MONTHBH";

    /**
     * "varchar", "numeric", "integer", "int", "tinyint", "smallint", "unsigned int", "long", "double", "datetime", "date", "char", "float",
     * "unsigned bigint", "bit"
     */
    public final static String[] DATATYPES = { "varchar", "numeric", "integer", "int", "tinyint", "smallint", "unsigned int", "long", "double",
            "datetime", "date", "char", "float", "unsigned bigint", "bit" }; // 20110906
                                                                             // eanguan
                                                                             // ::
                                                                             // Adding
                                                                             // Datatype
                                                                             // "unsigned bigint"
                                                                             // for
                                                                             // STATS
                                                                             // also
                                                                             // on
                                                                             // SON
                                                                             // team
                                                                             // request

    /**
     * "varchar", "numeric", "integer", "int", "tinyint", "smallint", "unsigned int", "long", "double", "datetime", "date", "char", "float", "binary",
     * "timestamp", "unsigned bigint", "bit"
     */
    public final static String[] EVENTSDATATYPES = { "varchar", "numeric", "integer", "int", "tinyint", "smallint", "unsigned int", "long", "double",
            "datetime", "date", "char", "float", "binary", "timestamp", "unsigned bigint", "bit" };

    /**
     * "extrasmall", "small", "medium", "large", "extralarge"
     */
    public final static String[] SIZINGITEMS = { "extrasmall", "small", "medium", "large", "extralarge", "bulk_cm" };

    /**
     * "extrasmall", "small", "medium", "large", "extralarge", "sgeh"
     */
    public final static String[] EVENTSIZINGITEMS = { "extrasmall", "small", "medium", "large", "extralarge", SGEH, SGEHLARGE, SGEHMEDIUM, SGEHSMALL,
            SGEHEXTRALARGE, SGEH2EXTRALARGE, SGEHEXTRASMALL, VOLEXTRASMALL, VOLSMALL, VOL, VOLMEDIUM, VOLLARGE, VOLEXTRALARGE, VOL2EXTRASMALL,
            VOL2SMALL, VOL2, VOL3EXTRASMALL, VOL3SMALL, VOL3, VOL3MEDIUM, VOL3LARGE, VOL4, VOL4SMALL, VOL3EXTRALARGE, VOL4MEDIUM };

    /**
     * 30, 20, 10
     */
    public final static int[] PROMPTPRIORITY = { 30, 20, 10 };

    /**
     * "DEFAULT", "MEDIUM", "HIGH"
     */
    public final static String[] PROMPTPRIORITY_TEXT = { "DEFAULT", "MEDIUM", "HIGH" };

    /**
     * 10, 20, 30, 40, 50
     */
    public final static Integer[] PROMPTORDER = { 10, 20, 30, 40, 50 };

    /**
     * "", "YES", "NO"
     */
    public final static String[] PROMPTUNREFRESHABLE = { "", "YES", "NO" };

    /**
     * 0L, 1L, 2L, 3L
     */
    public final static long[] UPDATE_METHODS = { 0L, 1L, 2L, 3L, 4L };

    /**
     * "Static", "Predefined", "Dynamic", "Timed Dynamic", "History Dynamic"
     */
    public final static String[] UPDATE_METHODS_TEXT = { "Static", "Predefined", "Dynamic", "Timed Dynamic", "History Dynamic" };

    /**
     * "", "TABLE", "VIEW"
     */
    public final static String[] TABLE_TYPES = { "", "TABLE", "VIEW" };

    /**
     * "GAUGE", "PEG", "VECTOR", "UNIQUEVECTOR"
     */
    public final static String[] COUNTER_TYPES = { "GAUGE", "PEG", "VECTOR", "UNIQUEVECTOR", "CMVECTOR", "PMRESVECTOR", "COMPRESSEDVECTOR" };

    /**
     * "", "SUM", "AVG", "MAX", "MIN"
     */
    public final static String[] AGGREGATION_FORMULAS = { "", "NONE", "SUM", "AVG", "MAX", "MIN" };

    /**
     * Transformer_types start here
     */
    public final static String CONVERTIPADDRESS = "convertipaddress";

    public final static String HASH_ID = "hashid";

    public final static String CURRENTTIME = "currenttime";

    public final static String FIXED = "fixed";

    public final static String LOOKUP = "lookup";

    public final static String COPY = "copy";

    public final static String CONDITION = "condition";

    public final static String POSTAPPENDER = "postappender";

    public final static String PROPERTYTOKENIZER = "propertytokenizer";

    public final static String PREAPPENDER = "preappender";

    public final static String DSTPARAMETERS = "dstparameters";

    public final static String DATABASELOOKUP = "databaselookup";

    public final static String CALCULATION = "calculation";

    public final static String DATEFORMAT = "dateformat";

    public final static String DEFAULTTIMEHANDLER = "defaulttimehandler";

    public final static String ALARM = "alarm";

    public final static String BITMAPLOOKUP = "bitmaplookup";

    public final static String REDUCEDATE = "reducedate";

    public final static String FIELDTOKENIZER = "fieldtokenizer";

    public final static String ROUNDTIME = "roundtime";

    public final static String SWITCH = "switch";

    public final static String RADIXCONVERTER = "radixconverter";

    public final static String ROPTIME = "roptime";

    public static final String UNDERSCORE = "_";

    /**
     * Returns "_ERR_". This is used to check it table name contains "_ERR_"
     */
    public static final String ERR = "_ERR_";

    /**
     * Returns "_SUC_". This is used to check it table name contains "_SUC_"
     */
    public static final String SUC = "_SUC_";

    // public final static Object[] ACTIONTYPEITEMS = { "Aggregation",
    // "AggregationRuleCopy", "AlarmHandler",
    // "AlarmInterfaceUpdate", "AlarmMarkup", "AutomaticAggregation",
    // "AutomaticREAggregati", "CreateDir",
    // CREATE_COLLECTED_DATA_FILES, "DirectoryDiskmanager", "Diskmanager",
    // "Distribute", "DuplicateCheck", "DWHMigrate",
    // "EBSUpdate", "ExecutionProfiler", "GateKeeper", "Grouping",
    // "JDBC Mediation", "Join", "JVMMonitor", "Load",
    // "Loader", "ManualReAggregation", "Mediation", "Parse", "PartitionAction",
    // "Partitioned Loader",
    // "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups",
    // "ReloadProperties", "ReloadTransformation",
    // "SanityCheck", "SessionLog Loader", "SetTypeTrigger", "SMTP Mediation",
    // "SNMP Poller", "SQL Execute",
    // "SQL Extract", "SQLJoiner", "StorageTimeAction", "System Call",
    // "System Monitor", "TableCheck", "TableCleaner",
    // TOPOLOGY_SQL_EXECUTE, "TriggerScheduledSet", "Uncompress",
    // "UnPartitioned Loader", UNKNOWN_TOPOLOGY,
    // "UpdateDimSession", "UpdateMonitoredTypes", "UpdateMonitoring",
    // UPDATE_COLLECTED_DATA_FILES, "UpdatePlan",
    // "VersionUpdate" };

    public final static Object[] ACTIONTYPEITEMS = { "Aggregation", "AggregationRuleCopy", "AlarmHandler", "AggRuleCacheRefresh",
            "AlarmInterfaceUpdate", "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", "CreateDir", "CreateAlarmFile", "BackupTopologyData","BackupAggregationData",
            "DirectoryDiskmanager", "Diskmanager", "Distribute", "DuplicateCheck", "DWHMigrate", "EBSUpdate", "ExecutionProfiler", "GateKeeper",
            HISTORY_SQL_EXECUTE, "JDBC Mediation", "Join", "JVMMonitor", "Load", "Loader", "ManualReAggregation", "Mediation", "Parse",
            "PartitionAction", "Partitioned Loader", "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups", "ReloadProperties",
            "ReloadTransformation", "SanityCheck", "SessionLog Loader", "SetTypeTrigger", "SMTP Mediation", "SNMP Poller", "SQL Execute",
            "SQL Extract", "SQLJoiner", "SQLLogResultSet", "StorageTimeAction", "System Call", "System Monitor", "TableCheck", "TableCleaner", "TriggerDeltaView",
            "TriggerScheduledSet", "Uncompress", "UnPartitioned Loader", UPDATE_HASH_IDS, "UpdateDimSession", "UpdateMonitoredTypes",
            "UpdateMonitoring", "UpdatePlan", "VersionUpdate" };

    public final static Object[] EVENTSACTIONTYPEITEMS = { "Aggregation", "AggregationRuleCopy", "AlarmHandler", "AlarmInterfaceUpdate",
            "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", BACKUP_TRIGGER, BACKUP_COUNT_DAY, BACKUP_LOADER, BACKUP_TABLES, "BackupTopologyData","BackupAggregationData",
            COUNTING_ACTION, COUNTING_DAY_ACTION, COUNTING_TRIGGER, COUNTING_DAY_TRIGGER, COUNTING_INTERVALS, COUNT_REAGG_ACTION,
            STORE_COUNTING_DATA, "CreateDir", CREATE_COLLECTED_DATA_FILES, "DirectoryDiskmanager", "Diskmanager", "Distribute", "DuplicateCheck",
            "DWHMigrate", "EBSUpdate", "ExecutionProfiler", "GateKeeper", GATE_KEEPER_PROPERTY, HISTORY_SQL_EXECUTE, IMSI_TO_IMEI, "JDBC Mediation",
            "Join", "JVMMonitor", "Load", "Loader", EVENT_LOADER, "ManualReAggregation", "Mediation", "Parse", "PartitionAction",
            "Partitioned Loader", "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups", RESTORE, "ReloadProperties", "ReloadTransformation",
            "SanityCheck", "SessionLog Loader", "SetTypeTrigger", "SMTP Mediation", "SNMP Poller", "SQL Execute", "SQL Extract", "SQLJoiner",
            "StorageTimeAction", "System Call", "System Monitor", "TableCheck", "TableCleaner", TIMEBASE_PARTITION_LOADER, TOPOLOGY_SQL_EXECUTE,"TriggerDeltaView",
            "TriggerScheduledSet", "Uncompress", "UnPartitioned Loader", UNKNOWN_TOPOLOGY, UPDATE_COUNT_INTERVALS, UPDATE_HASH_IDS,
            "UpdateDimSession", "UpdateMonitoredTypes", "UpdateMonitoring", UPDATE_COLLECTED_DATA_FILES, "UpdatePlan", "VersionUpdate" };

    public final static String[] TRANFORMER_TYPES = new String[] { CONVERTIPADDRESS, CURRENTTIME, FIXED, HASH_ID, LOOKUP, COPY, CONDITION,
            POSTAPPENDER, PROPERTYTOKENIZER, PREAPPENDER, DSTPARAMETERS, DATABASELOOKUP, CALCULATION, DATEFORMAT, DEFAULTTIMEHANDLER, ALARM,
            BITMAPLOOKUP, REDUCEDATE, FIELDTOKENIZER, ROUNDTIME, SWITCH, RADIXCONVERTER, ROPTIME };

    public final static String[] UNIVERSEEXTENSIONTYPES = new String[] { "ALL" };

    public final static String[] UNIVERSEOBJECTTYPES = new String[] { "Character", "Number", "Date" };

    public final static String[] UNIVERSEQUALIFICATIONTYPES = new String[] { "Dimension", "Measure" };

    public final static String[] UNIVERSEOWNERTYPES = new String[] { "DC", "DWH" };

    public final static String PM_TECHPACK = "PM";

    public final static String CM_TECHPACK = "CM";

    public final static String BASE_TECHPACK = "BASE";

    public final static String SYSTEM_TECHPACK = "SYSTEM";

    public final static String TOPOLOGY_TECHPACK = "Topology";

    public final static String EVENT_TECHPACK = "EVENT";

    public final static String CUSTOM_TECHPACK = "CUSTOM";

    // 20110615 eanguan :: Separating the various Types to differentiate between
    // stats and events
    public final static String STATSTECHPACKTYPES[] = { "PM", "CM", BASE_TECHPACK, SYSTEM_TECHPACK, "Topology", "EVENT", CUSTOM_TECHPACK };

    public final static String EVENTSTECHPACKTYPES[] = { ENIQ_EVENT };

    public final static String ALLTECHPACKTYPES[] = { "PM", "CM", BASE_TECHPACK, SYSTEM_TECHPACK, "Topology", "EVENT", ENIQ_EVENT, CUSTOM_TECHPACK };

    public final static String PARSERFORMATS[] = { "alarm", "ascii", "axd", "ebs", "csexport", "ct", "eniqasn1", "mdc", "nascii", "nossdb", "omes",
            "omes2", "raml", "redback", "sasn", "separator", "spf", "stfiop", "wifi", "wifiinventory", "xml", "3gpp32435", "bcd", "twampM",
            "twampPT", "twampST", "mlXmlParser", CUSTOM_TECHPACK };

    public final static String CUSTOMTECHPACKTYPES[] = { CUSTOM_TECHPACK };

    public final static String TYPES_NOBASE[] = { BASE_TECHPACK, SYSTEM_TECHPACK };

    public static final String BOXIAUTHENTICATIONS[] = { "ENTERPRISE" };

    public static final String REPORTOBJECTLEVELS[] = { "TOTAL_RAW", "TOTAL_DAY", "DAYBH_RAW", "DAYBH_DAY", "ELEM_RAW", "ELEM_DAY" };

    public static final String REPORTCONDITIONLEVELS[] = { "TOTAL_RAW", "TOPOLOGY", "KEYTOPOLOGY", "TOTAL_DAY", "DAYBH_RAW", "DAYBH_DAY", "DAYBH",
            "ELEMBH_RAW", "ELEMBH_DAY" };

    /**
     * The default number of busy hour product place holder.
     */
    public static final int DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS = 5;

    /**
     * The default number of busy hour product place holder.
     */
    public static final int DEFAULT_NUMBER_OF_BH_CUSTOM_PLACE_HOLDERS = 5;

    /**
     * The busy hour product place holder prefix.
     */
    public static final String BH_PRODUCT_PLACE_HOLDER_PREFIX = "PP";

    /**
     * The busy hour custom place holder prefix.
     */
    public static final String BH_CUSTOM_PLACE_HOLDER_PREFIX = "CP";

    /**
     * Custom TechPack holder prefix.
     */
    public static final String BH_CUSTOM_TP_PREFIX = "CTP";

    /**
     * The default value for Sliding Window Offset.
     */
    public static final int SLIDING_WINDOW_OFFSET = 15;

    /**
     * The default value for Peakrop Window Offset.
     */
    public static final int PEAKROP_OFFSET = 15;

    /**
     * The maximum number of busy hour place holders (including both product and custom place holders).
     */
    public static final int MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS = 30;

    public static final String BH_TYPE_TL = "Timelimited";

    public static final String BH_TYPE_SW = "Slidingwindow";

    public static final String BH_TYPE_TL_TC = "Timelimited + Timeconsistent";

    public static final String BH_TYPE_SW_TC = "Slidingwindow + Timeconsistent";

    public static final String BH_TYPE_PR = "Peakrop";

    /**
     * The values for the busy hour aggregation types shown in the GUI.
     */
    public final static String[] BH_AGGREGATION_TYPPES_SHOW = { BH_TYPE_TL, BH_TYPE_SW, BH_TYPE_TL_TC, BH_TYPE_SW_TC, BH_TYPE_PR };

    /**
     * The values for the busy hour aggregation types stored to the database.
     */
    public final static String[] BH_AGGREGATION_TYPPES = { "RANKBH_TIMELIMITED", "RANKBH_SLIDINGWINDOW", "RANKBH_TIMECONSISTENT",
            "RANKBH_TIMECONSISTENT_SLIDINGWINDOW", "RANKBH_PEAKROP" };

    /**
     * The values for the busy hour grouping options.
     */
    public final static String[] BH_GROUPING_TYPES = new String[] { "None", "Time", "Node", "Time + Node" };

    /**
     * Events counting levels
     */
    public static final int[] COUNTING_LEVELS = { 1, 15, 1440 };

    /**
     * Events counting level descriptions
     */
    public static final String[] COUNTING_DESCRIPTIONS = { "1 minute", "15 minutes", "Daily" };

    /**
     * Name of the Alarm Interfaces techpack.
     */
    public final static String ALARM_INTERFACES_TECHPACK_NAME = "AlarmInterfaces";

    /**
     * Name of the DC_Z_ALARM techpack.
     */
    public final static String DC_Z_ALARM_TECHPACK_NAME = "DC_Z_ALARM";

}
