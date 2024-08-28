 alter table META_COLLECTIONS
 	drop constraint CON_CST_FK; 

 alter table META_COLUMN_CONSTRAINTS
 	drop constraint CCT_COMN_FK; 

 alter table META_COLUMNS
 	drop constraint COMN_TAE_FK; 

 alter table META_DATABASES
 	drop constraint DAE_VEN_FK; 

 alter table META_DATABASES
 	drop constraint DAE_CTE_FK; 

 alter table META_DEBUGS
 	drop constraint DEG_TAN_FK; 

 alter table META_ERRORS
 	drop constraint ERR_TAN_FK; 

 alter table META_EXECUTION_SLOT
 	drop constraint PAR_VEN_FK_1; 

 alter table META_FILES
 	drop constraint FILE_1_TAN_FK; 

 alter table META_FK_TABLE_JOINTS
 	drop constraint FTJT_FTE_FK; 

 alter table META_FK_TABLE_JOINTS
 	drop constraint FTJT_COMN_FK; 

 alter table META_FK_TABLES
 	drop constraint FTE_TATE_FK; 

 alter table META_FK_TABLES
 	drop constraint FTE_TAE_FK; 

 alter table META_JOINTS
 	drop constraint JOT_PLUG_FK; 

 alter table META_JOINTS
 	drop constraint JOT_STE_FK; 

 alter table META_JOINTS
 	drop constraint JOT_COMN_FK; 

 alter table META_JOINTS
 	drop constraint JOT_COMN_SOURCE_COLUMN_FK; 

 alter table META_JOINTS
 	drop constraint JOT_TRE_FK; 

 alter table META_JOINTS
 	drop constraint JOT_TTE_FK; 

 alter table META_JOINTS
 	drop constraint JOT_TATE_FK; 

 alter table META_JOINTS
 	drop constraint JOT_PTE_FK; 

 alter table META_JOINTS
 	drop constraint JOT_FILE_1_FK; 

 alter table META_PARAMETER_TABLES
 	drop constraint PTE_VEN_FK; 

 alter table META_PARAMETERS
 	drop constraint PAR_VEN_FK; 

 alter table META_PLUGINS
 	drop constraint PLUG_TAN_FK; 

 alter table META_SOURCE_TABLES
 	drop constraint STE_TAE_FK; 

 alter table META_SOURCE_TABLES
 	drop constraint STE_TAN_FK; 

 alter table META_SOURCE_TABLES
 	drop constraint STE_COMN_FK; 

 alter table META_SQL_LOADS
 	drop constraint SLD_TATE_FK; 

 alter table META_STATUSES
 	drop constraint STS_TAN_FK; 

 alter table META_SYSTEM_MONITORS
 	drop constraint FK_META_SYS_REFERENCE_META_SER; 

 alter table META_TABLES
 	drop constraint TAE_DAE_FK; 

 alter table META_TARGET_TABLES
 	drop constraint TATE_TAE_FK; 

 alter table META_TARGET_TABLES
 	drop constraint TATE_TAN_FK; 

 alter table META_TRANSF_TABLE_VALUES
 	drop constraint TTVE_TTE_FK; 

 alter table META_TRANSFER_ACTIONS
 	drop constraint TAN_CON_FK; 

 alter table META_TRANSFORMATION_RULES
 	drop constraint TRE_VEN_FK; 

 alter table META_TRANSFORMATION_TABLES
 	drop constraint TTE_VEN_FK; 

 alter table META_TRANSFORMATION_TABLES
 	drop constraint TTE_COMN_KEY_FK; 

 alter table META_TRANSFORMATION_TABLES
 	drop constraint TTE_COMN_VAL_FK; 

drop table META_COLLECTION_SETS;
drop table META_COLLECTIONS;
drop table META_COLUMN_CONSTRAINTS;
drop table META_COLUMNS;
drop table META_CONNECTION_TYPES;
drop table META_DATABASES;
drop table META_DEBUGS;
drop table META_ERRORS;
drop table META_EXECUTION_SLOT;
drop table META_EXECUTION_SLOT_PROFILE;
drop table META_FILES;
drop table META_FK_TABLE_JOINTS;
drop table META_FK_TABLES;
drop table META_JOINTS;
drop table META_PARAMETER_TABLES;
drop table META_PARAMETERS;
drop table META_PLUGINS;
drop table META_SCHEDULINGS;
drop table META_SERVERS;
drop table META_SOURCE_TABLES;
drop table META_SQL_LOADS;
drop table META_STATUSES;
drop table META_SYSTEM_MONITORS;
drop table META_TABLES;
drop table META_TARGET_TABLES;
drop table META_TRANSF_TABLE_VALUES;
drop table META_TRANSFER_ACTIONS;
drop table META_TRANSFER_BATCHES;
drop table META_TRANSFORMATION_RULES;
drop table META_TRANSFORMATION_TABLES;
drop table META_VERSIONS;
