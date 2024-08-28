

--/***************************
--  META_COLLECTION_SETS
--/***************************
create table META_COLLECTION_SETS (
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_SET_NAME varchar(128) not null,
	DESCRIPTION varchar(32000) ,
	VERSION_NUMBER varchar(32) not null,
	ENABLED_FLAG varchar(1) ,
	TYPE varchar(32) 
);

 alter table META_COLLECTION_SETS
       add primary key (COLLECTION_SET_ID, VERSION_NUMBER);


--/***************************
--  META_COLLECTIONS
--/***************************
create table META_COLLECTIONS (
	COLLECTION_ID numeric(31) not null,
	COLLECTION_NAME varchar(128) not null,
	COLLECTION varchar(200) ,
	MAIL_ERROR_ADDR varchar(100) ,
	MAIL_FAIL_ADDR varchar(100) ,
	MAIL_BUG_ADDR varchar(100) ,
	MAX_ERRORS numeric(31) not null,
	MAX_FK_ERRORS numeric(31) not null,
	MAX_COL_LIMIT_ERRORS numeric(31) not null,
	CHECK_FK_ERROR_FLAG varchar(1) not null,
	CHECK_COL_LIMITS_FLAG varchar(1) not null,
	LAST_TRANSFER_DATE timestamp ,
	VERSION_NUMBER varchar(32) not null,
	COLLECTION_SET_ID numeric(31) not null,
	USE_BATCH_ID varchar(1) ,
	PRIORITY numeric(3) ,
	QUEUE_TIME_LIMIT numeric(31) ,
	ENABLED_FLAG varchar(1) ,
	SETTYPE varchar(10) ,
	FOLDABLE_FLAG varchar(1) ,
	MEASTYPE varchar(30) ,
	HOLD_FLAG varchar(1) ,
	SCHEDULING_INFO varchar(2000) 
);

 alter table META_COLLECTIONS
       add primary key (COLLECTION_ID, VERSION_NUMBER, COLLECTION_SET_ID);


--/***************************
--  META_COLUMN_CONSTRAINTS
--/***************************
create table META_COLUMN_CONSTRAINTS (
	ID numeric(31) not null,
	LOW_VALUE varchar(30) not null,
	HIGH_VALUE varchar(30) ,
	VERSION_NUMBER varchar(32) not null,
	CONNECTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null,
	COLUMN_ID numeric(31) not null
);

 alter table META_COLUMN_CONSTRAINTS
       add primary key (ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID, COLUMN_ID);


--/***************************
--  META_COLUMNS
--/***************************
create table META_COLUMNS (
	COLUMN_ID numeric(31) not null,
	COLUMN_NAME varchar(30) not null,
	COLUMN_ALIAS_NAME varchar(60) ,
	COLUMN_TYPE varchar(30) not null,
	COLUMN_LENGTH numeric(31) not null,
	IS_PK_COLUMN varchar(1) not null,
	VERSION_NUMBER varchar(32) not null,
	CONNECTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null
);

 alter table META_COLUMNS
       add primary key (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID);


--/***************************
--  META_CONNECTION_TYPES
--/***************************
create table META_CONNECTION_TYPES (
	TYPE_NAME varchar(15) not null,
	DATABASE_TYPE_FLAG varchar(1) not null
);

 alter table META_CONNECTION_TYPES
       add primary key (TYPE_NAME);


--/***************************
--  META_DATABASES
--/***************************
create table META_DATABASES (
	USERNAME varchar(30) ,
	VERSION_NUMBER varchar(32) not null,
	TYPE_NAME varchar(15) not null,
	CONNECTION_ID numeric(31) not null,
	CONNECTION_NAME varchar(30) not null,
	CONNECTION_STRING varchar(200) not null,
	PASSWORD varchar(30) ,
	DESCRIPTION varchar(32000) ,
	DRIVER_NAME varchar(100) not null,
	DB_LINK_NAME varchar(128) 
);

 alter table META_DATABASES
       add primary key (VERSION_NUMBER, CONNECTION_ID);


--/***************************
--  META_DEBUGS
--/***************************
create table META_DEBUGS (
	ID numeric(31) not null,
	TEXT varchar(2000) not null,
	LAST_UPDATED timestamp not null,
	VERSION_NUMBER varchar(32) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	TRANSFER_BATCH_ID numeric(31) not null,
	TRANSFER_ACTION_ID numeric(31) 
);

 alter table META_DEBUGS
       add primary key (ID);


--/***************************
--  META_ERRORS
--/***************************
create table META_ERRORS (
	ID numeric(31) not null,
	TEXT varchar(2000) ,
	METHOD_NAME varchar(100) ,
	ERR_TYPE varchar(30) not null,
	LAST_UPDATED timestamp not null,
	VERSION_NUMBER varchar(32) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	TRANSFER_BATCH_ID numeric(31) not null,
	TRANSFER_ACTION_ID numeric(31) 
);

 alter table META_ERRORS
       add primary key (ID);


--/***************************
--  META_EXECUTION_SLOT
--/***************************
create table META_EXECUTION_SLOT (
	PROFILE_ID varchar(38) not null,
	SLOT_NAME varchar(15) not null,
	SLOT_ID varchar(38) not null,
	ACCEPTED_SET_TYPES varchar(2000) not null
);

 alter table META_EXECUTION_SLOT
       add primary key (SLOT_ID);


--/***************************
--  META_EXECUTION_SLOT_PROFILE
--/***************************
create table META_EXECUTION_SLOT_PROFILE (
	PROFILE_NAME varchar(15) not null,
	PROFILE_ID varchar(38) not null,
	ACTIVE_FLAG varchar(1) not null
);

 alter table META_EXECUTION_SLOT_PROFILE
       add primary key (PROFILE_ID);


--/***************************
--  META_FILES
--/***************************
create table META_FILES (
	FILE_ID numeric(31) not null,
	FILE_NAME varchar(100) not null,
	FILE_CONTENT_TYPE varchar(20) not null,
	ROW_DELIM varchar(5) ,
	COLUMN_DELIM varchar(5) ,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	COMMIT_AFTER_N_ROWS numeric(10) ,
	IS_SOURCE varchar(1) not null,
	VERSION_NUMBER varchar(32) not null,
	TRANSFER_ACTION_ID numeric(31) not null
);

 alter table META_FILES
       add primary key (FILE_ID, COLLECTION_SET_ID, COLLECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID);


--/***************************
--  META_FK_TABLE_JOINTS
--/***************************
create table META_FK_TABLE_JOINTS (
	VERSION_NUMBER varchar(32) not null,
	CONNECTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null,
	COLUMN_ID_FK_COLUMN numeric(31) not null,
	TARGET_TABLE_ID numeric(31) not null,
	COLUMN_ID numeric(31) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	TRANSFER_ACTION_ID numeric(31) not null
);

 alter table META_FK_TABLE_JOINTS
       add primary key (VERSION_NUMBER, CONNECTION_ID, TABLE_ID, COLUMN_ID_FK_COLUMN, TARGET_TABLE_ID, COLUMN_ID, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID);


--/***************************
--  META_FK_TABLES
--/***************************
create table META_FK_TABLES (
	MAX_ERRORS numeric(31) not null,
	VERSION_NUMBER varchar(32) not null,
	WHERE_CLAUSE varchar(2000) ,
	FILTER_ERRORS_FLAG varchar(1) not null,
	REPLACE_ERRORS_FLAG varchar(1) not null,
	REPLACE_ERRORS_WITH varchar(30) ,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	TRANSFER_ACTION_ID numeric(31) not null,
	CONNECTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null,
	TARGET_TABLE_ID numeric(31) not null
);

 alter table META_FK_TABLES
       add primary key (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID, TARGET_TABLE_ID);


--/***************************
--  META_JOINTS
--/***************************
create table META_JOINTS (
	ID numeric(31) not null,
	IS_PK_COLUMN varchar(1) not null,
	IS_SUM_COLUMN varchar(1) not null,
	IS_GROUP_BY_COLUMN varchar(1) not null,
	COLUMN_SPACE_AT_FILE numeric(10) ,
	FILE_ORDER_BY numeric(31) ,
	PLUGIN_METHOD_NAME varchar(100) ,
	VERSION_NUMBER varchar(32) ,
	COLLECTION_SET_ID numeric(31) ,
	COLLECTION_ID numeric(31) ,
	TRANSFER_ACTION_ID numeric(31) ,
	TARGET_CONNECTION_ID numeric(31) ,
	TARGET_TABLE_ID numeric(31) ,
	COLUMN_ID_TARGET_COLUMN numeric(31) ,
	SOURCE_CONNECTION_ID numeric(31) ,
	SOURCE_TABLE_ID numeric(31) ,
	COLUMN_ID_SOURCE_COLUMN numeric(31) ,
	TRANSFORMATION_ID numeric(31) ,
	TRANSF_TABLE_ID numeric(31) ,
	PAR_NAME varchar(30) ,
	FILE_ID numeric(31) ,
	PLUGIN_ID numeric(31) ,
	FREE_FORMAT_TRANSFORMAT varchar(2000) ,
	METHOD_PARAMETER varchar(200) 
);

 alter table META_JOINTS
       add primary key (ID);


--/***************************
--  META_PARAMETER_TABLES
--/***************************
create table META_PARAMETER_TABLES (
	PAR_NAME varchar(30) not null,
	PAR_VALUE varchar(200) not null,
	VERSION_NUMBER varchar(32) not null
);

 alter table META_PARAMETER_TABLES
       add primary key (PAR_NAME, VERSION_NUMBER);


--/***************************
--  META_PARAMETERS
--/***************************
create table META_PARAMETERS (
	RB_SEGMENT_NAME varchar(30) ,
	USE_RB_SEGMENT_FLAG char(1) not null,
	DEFAULT_ERROR_MAIL_ADDR char(100) ,
	DEFAULT_FAIL_MAIL_ADDR char(100) ,
	DEFAULT_BUG_ERROR_MAIL_ADDR char(100) ,
	DEFAULT_MAX_ERROR_VALUE numeric(31) not null,
	DEFAULT_MAX_FK_ERROR_VALUE numeric(31) not null,
	DEFAULT_MAX_COL_LIMIT_VALUE numeric(31) not null,
	TEMP_SUM_TABLESPACE varchar(30) ,
	USE_TEMP_SUM_TABLESPACE_FLAG varchar(1) not null,
	BATCH_COLUMN_NAME varchar(30) ,
	VERSION_NUMBER varchar(32) not null
);

 alter table META_PARAMETERS
       add primary key (VERSION_NUMBER);


--/***************************
--  META_PLUGINS
--/***************************
create table META_PLUGINS (
	PLUGIN_ID numeric(31) not null,
	PLUGIN_NAME varchar(30) not null,
	CONSTRUCTOR_PARAMETER varchar(200) ,
	IS_SOURCE varchar(1) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	COMMIT_AFTER_N_ROWS numeric(10) ,
	VERSION_NUMBER varchar(32) not null,
	TRANSFER_ACTION_ID numeric(31) not null
);

 alter table META_PLUGINS
       add primary key (PLUGIN_ID, COLLECTION_SET_ID, COLLECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID);


--/***************************
--  META_SCHEDULINGS
--/***************************
create table META_SCHEDULINGS (
	VERSION_NUMBER varchar(32) ,
	ID numeric(31) not null,
	EXECUTION_TYPE varchar(15) not null,
	OS_COMMAND varchar(2000) ,
	SCHEDULING_MONTH numeric(2) ,
	SCHEDULING_DAY numeric(2) ,
	SCHEDULING_HOUR numeric(2) ,
	SCHEDULING_MIN numeric(2) ,
	COLLECTION_SET_ID numeric(31) ,
	COLLECTION_ID numeric(31) ,
	MON_FLAG varchar(1) ,
	TUE_FLAG varchar(1) ,
	WED_FLAG varchar(1) ,
	THU_FLAG varchar(1) ,
	FRI_FLAG varchar(1) ,
	SAT_FLAG varchar(1) ,
	SUN_FLAG varchar(1) ,
	STATUS varchar(20) ,
	LAST_EXECUTION_TIME timestamp ,
	INTERVAL_HOUR numeric(2) ,
	INTERVAL_MIN numeric(2) ,
	NAME varchar(128) not null,
	HOLD_FLAG varchar(1) ,
	PRIORITY numeric(3) ,
	SCHEDULING_YEAR numeric(4) ,
	TRIGGER_COMMAND varchar(2000) ,
	LAST_EXEC_TIME_MS numeric(31) 
);

 alter table META_SCHEDULINGS
       add primary key (ID);


--/***************************
--  META_SERVERS
--/***************************
create table META_SERVERS (
	HOSTNAME varchar(255) not null,
	TYPE varchar(128) not null,
	STATUS_URL varchar(255) not null
);

 alter table META_SERVERS
       add primary key (HOSTNAME);


--/***************************
--  META_SOURCE_TABLES
--/***************************
create table META_SOURCE_TABLES (
	LAST_TRANSFER_DATE timestamp ,
	TRANSFER_ACTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null,
	USE_TR_DATE_IN_WHERE_FLAG varchar(1) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	CONNECTION_ID numeric(31) not null,
	DISTINCT_FLAG varchar(1) not null,
	AS_SELECT_OPTIONS varchar(200) ,
	AS_SELECT_TABLESPACE varchar(30) ,
	VERSION_NUMBER varchar(32) not null,
	TIMESTAMP_COLUMN_ID numeric(31) 
);

 alter table META_SOURCE_TABLES
       add primary key (TRANSFER_ACTION_ID, TABLE_ID, COLLECTION_SET_ID, COLLECTION_ID, CONNECTION_ID, VERSION_NUMBER);


--/***************************
--  META_SQL_LOADS
--/***************************
create table META_SQL_LOADS (
	INPUT_FILE varchar(200) not null,
	CTL_FILE varchar(200) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	CONNECTION_ID numeric(31) not null,
	DIS_FILE varchar(200) not null,
	BAD_FILE varchar(200) not null,
	LOAD_TYPE varchar(30) not null,
	TEXT varchar(2000) not null,
	DELIM varchar(1) ,
	SQLLDR_CMD varchar(200) ,
	LOAD_OPTION varchar(30) ,
	VERSION_NUMBER varchar(32) not null,
	TRANSFER_ACTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null
);

 alter table META_SQL_LOADS
       add primary key (COLLECTION_SET_ID, COLLECTION_ID, CONNECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID, TABLE_ID);


--/***************************
--  META_STATUSES
--/***************************
create table META_STATUSES (
	ID numeric(31) not null,
	STATUS_DESCRIPTION varchar(32000) not null,
	LAST_UPDATED timestamp not null,
	VERSION_NUMBER varchar(32) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	TRANSFER_BATCH_ID numeric(31) not null,
	TRANSFER_ACTION_ID numeric(31) 
);

 alter table META_STATUSES
       add primary key (ID);


--/***************************
--  META_SYSTEM_MONITORS
--/***************************
create table META_SYSTEM_MONITORS (
	MONITOR varchar(255) not null,
	HOSTNAME varchar(255) not null,
	TYPE varchar(32) not null,
	CONFIGURATION varchar(32000) ,
	EXECUTED timestamp not null,
	STATUS varchar(10) not null
);

 alter table META_SYSTEM_MONITORS
       add primary key (MONITOR, HOSTNAME);


--/***************************
--  META_TABLES
--/***************************
create table META_TABLES (
	TABLE_ID numeric(31) not null,
	TABLE_NAME varchar(60) not null,
	VERSION_NUMBER varchar(32) not null,
	IS_JOIN varchar(1) ,
	JOIN_CLAUSE varchar(2000) ,
	TABLES_AND_ALIASES varchar(2000) ,
	CONNECTION_ID numeric(31) not null
);

 alter table META_TABLES
       add primary key (TABLE_ID, VERSION_NUMBER, CONNECTION_ID);


--/***************************
--  META_TARGET_TABLES
--/***************************
create table META_TARGET_TABLES (
	VERSION_NUMBER varchar(32) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	TRANSFER_ACTION_ID numeric(31) not null,
	CONNECTION_ID numeric(31) not null,
	TABLE_ID numeric(31) not null
);

 alter table META_TARGET_TABLES
       add primary key (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID);


--/***************************
--  META_TRANSF_TABLE_VALUES
--/***************************
create table META_TRANSF_TABLE_VALUES (
	OLD_VALUE varchar(30) not null,
	NEW_VALUE varchar(30) ,
	VERSION_NUMBER varchar(32) not null,
	TRANSF_TABLE_ID numeric(31) not null
);

 alter table META_TRANSF_TABLE_VALUES
       add primary key (OLD_VALUE, VERSION_NUMBER, TRANSF_TABLE_ID);


--/***************************
--  META_TRANSFER_ACTIONS
--/***************************
create table META_TRANSFER_ACTIONS (
	VERSION_NUMBER varchar(32) not null,
	TRANSFER_ACTION_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	COLLECTION_SET_ID numeric(31) not null,
	ACTION_TYPE varchar(20) not null,
	TRANSFER_ACTION_NAME varchar(128) not null,
	ORDER_BY_NO numeric(31) not null,
	DESCRIPTION varchar(32000) ,
	WHERE_CLAUSE_01 varchar(32000) ,
	ACTION_CONTENTS_01 varchar(32000) ,
	ENABLED_FLAG varchar(1) not null,
	CONNECTION_ID numeric(31) ,
	WHERE_CLAUSE_02 varchar(32000) ,
	WHERE_CLAUSE_03 varchar(32000) ,
	ACTION_CONTENTS_02 varchar(32000) ,
	ACTION_CONTENTS_03 varchar(32000) 
);

 alter table META_TRANSFER_ACTIONS
       add primary key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID);


--/***************************
--  META_TRANSFER_BATCHES
--/***************************
create table META_TRANSFER_BATCHES (
	ID numeric(31) not null,
	START_DATE timestamp not null,
	END_DATE timestamp ,
	FAIL_FLAG varchar(1) not null,
	STATUS varchar(10) not null,
	VERSION_NUMBER varchar(32) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	META_COLLECTION_NAME varchar(128) not null,
	META_COLLECTION_SET_NAME varchar(128) not null,
	SETTYPE varchar(10) ,
	SLOT_ID integer 
);

 alter table META_TRANSFER_BATCHES
       add primary key (ID, VERSION_NUMBER, META_COLLECTION_NAME, META_COLLECTION_SET_NAME);


--/***************************
--  META_TRANSFORMATION_RULES
--/***************************
create table META_TRANSFORMATION_RULES (
	TRANSFORMATION_ID numeric(31) not null,
	TRANSFORMATION_NAME varchar(10) not null,
	CODE varchar(2000) not null,
	DESCRIPTION varchar(32000) ,
	VERSION_NUMBER varchar(32) not null
);

 alter table META_TRANSFORMATION_RULES
       add primary key (TRANSFORMATION_ID, VERSION_NUMBER);


--/***************************
--  META_TRANSFORMATION_TABLES
--/***************************
create table META_TRANSFORMATION_TABLES (
	TRANSF_TABLE_ID numeric(31) not null,
	TRANSF_TABLE_NAME varchar(30) not null,
	DESCRIPTION varchar(32000) ,
	VERSION_NUMBER varchar(32) not null,
	IS_LOOKUP varchar(1) ,
	CONNECTION_ID numeric(31) ,
	TABLE_ID numeric(31) ,
	KEY_COLUMN_ID numeric(31) ,
	VALUE_COLUMN_ID numeric(31) 
);

 alter table META_TRANSFORMATION_TABLES
       add primary key (TRANSF_TABLE_ID, VERSION_NUMBER);


--/***************************
--  META_VERSIONS
--/***************************
create table META_VERSIONS (
	VERSION_NUMBER varchar(32) not null,
	DESCRIPTION varchar(32000) ,
	CURRENT_FLAG varchar(1) not null,
	IS_PREDEFINED varchar(1) not null,
	ENGINE_SERVER varchar(50) ,
	MAIL_SERVER varchar(100) ,
	SCHEDULER_SERVER varchar(50) ,
	MAIL_SERVER_PORT numeric(5) 
);

 alter table META_VERSIONS
       add primary key (VERSION_NUMBER);
