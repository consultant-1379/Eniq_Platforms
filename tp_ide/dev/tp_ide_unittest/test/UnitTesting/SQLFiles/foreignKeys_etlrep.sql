
 
alter table META_COLLECTIONS
       add constraint CON_CST_FK foreign key (COLLECTION_SET_ID, VERSION_NUMBER)
       references META_COLLECTION_SETS (COLLECTION_SET_ID, VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_COLUMN_CONSTRAINTS
       add constraint CCT_COMN_FK foreign key (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_COLUMNS
       add constraint COMN_TAE_FK foreign key (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       references META_TABLES (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       on delete restrict on update restrict;

 
alter table META_DATABASES
       add constraint DAE_VEN_FK foreign key (VERSION_NUMBER)
       references META_VERSIONS (VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_DATABASES
       add constraint DAE_CTE_FK foreign key (TYPE_NAME)
       references META_CONNECTION_TYPES (TYPE_NAME)
       on delete restrict on update restrict;

 
alter table META_DEBUGS
       add constraint DEG_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_ERRORS
       add constraint ERR_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_EXECUTION_SLOT
       add constraint PAR_VEN_FK_1 foreign key (PROFILE_ID)
       references META_EXECUTION_SLOT_PROFILE (PROFILE_ID)
       on delete restrict on update restrict;

 
alter table META_FILES
       add constraint FILE_1_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_FK_TABLE_JOINTS
       add constraint FTJT_FTE_FK foreign key (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID, TARGET_TABLE_ID)
       references META_FK_TABLES (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID, TARGET_TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_FK_TABLE_JOINTS
       add constraint FTJT_COMN_FK foreign key (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TARGET_TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_FK_TABLES
       add constraint FTE_TATE_FK foreign key (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TARGET_TABLE_ID)
       references META_TARGET_TABLES (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_FK_TABLES
       add constraint FTE_TAE_FK foreign key (TARGET_TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       references META_TABLES (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_PLUG_FK foreign key (PLUGIN_ID, COLLECTION_SET_ID, COLLECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID)
       references META_PLUGINS (PLUGIN_ID, COLLECTION_SET_ID, COLLECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_STE_FK foreign key (TRANSFER_ACTION_ID, SOURCE_TABLE_ID, COLLECTION_SET_ID, COLLECTION_ID, SOURCE_CONNECTION_ID, VERSION_NUMBER)
       references META_SOURCE_TABLES (TRANSFER_ACTION_ID, TABLE_ID, COLLECTION_SET_ID, COLLECTION_ID, CONNECTION_ID, VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_COMN_FK foreign key (COLUMN_ID_TARGET_COLUMN, VERSION_NUMBER, TARGET_CONNECTION_ID, TARGET_TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_COMN_SOURCE_COLUMN_FK foreign key (COLUMN_ID_SOURCE_COLUMN, VERSION_NUMBER, SOURCE_CONNECTION_ID, SOURCE_TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_TRE_FK foreign key (TRANSFORMATION_ID, VERSION_NUMBER)
       references META_TRANSFORMATION_RULES (TRANSFORMATION_ID, VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_TTE_FK foreign key (TRANSF_TABLE_ID, VERSION_NUMBER)
       references META_TRANSFORMATION_TABLES (TRANSF_TABLE_ID, VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_TATE_FK foreign key (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, TARGET_CONNECTION_ID, TARGET_TABLE_ID)
       references META_TARGET_TABLES (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_PTE_FK foreign key (PAR_NAME, VERSION_NUMBER)
       references META_PARAMETER_TABLES (PAR_NAME, VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_JOINTS
       add constraint JOT_FILE_1_FK foreign key (FILE_ID, COLLECTION_SET_ID, COLLECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID)
       references META_FILES (FILE_ID, COLLECTION_SET_ID, COLLECTION_ID, VERSION_NUMBER, TRANSFER_ACTION_ID)
       on delete restrict on update restrict;

 
alter table META_PARAMETER_TABLES
       add constraint PTE_VEN_FK foreign key (VERSION_NUMBER)
       references META_VERSIONS (VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_PARAMETERS
       add constraint PAR_VEN_FK foreign key (VERSION_NUMBER)
       references META_VERSIONS (VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_PLUGINS
       add constraint PLUG_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_SOURCE_TABLES
       add constraint STE_TAE_FK foreign key (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       references META_TABLES (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       on delete restrict on update restrict;

 
alter table META_SOURCE_TABLES
       add constraint STE_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_SOURCE_TABLES
       add constraint STE_COMN_FK foreign key (TIMESTAMP_COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_SQL_LOADS
       add constraint SLD_TATE_FK foreign key (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID)
       references META_TARGET_TABLES (VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID, TRANSFER_ACTION_ID, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_STATUSES
       add constraint STS_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_SYSTEM_MONITORS
       add constraint FK_META_SYS_REFERENCE_META_SER foreign key (HOSTNAME)
       references META_SERVERS (HOSTNAME)
       on delete restrict on update restrict;

 
alter table META_TABLES
       add constraint TAE_DAE_FK foreign key (VERSION_NUMBER, CONNECTION_ID)
       references META_DATABASES (VERSION_NUMBER, CONNECTION_ID)
       on delete restrict on update restrict;

 
alter table META_TARGET_TABLES
       add constraint TATE_TAE_FK foreign key (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       references META_TABLES (TABLE_ID, VERSION_NUMBER, CONNECTION_ID)
       on delete restrict on update restrict;

 
alter table META_TARGET_TABLES
       add constraint TATE_TAN_FK foreign key (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       references META_TRANSFER_ACTIONS (VERSION_NUMBER, TRANSFER_ACTION_ID, COLLECTION_ID, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_TRANSF_TABLE_VALUES
       add constraint TTVE_TTE_FK foreign key (TRANSF_TABLE_ID, VERSION_NUMBER)
       references META_TRANSFORMATION_TABLES (TRANSF_TABLE_ID, VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_TRANSFER_ACTIONS
       add constraint TAN_CON_FK foreign key (COLLECTION_ID, VERSION_NUMBER, COLLECTION_SET_ID)
       references META_COLLECTIONS (COLLECTION_ID, VERSION_NUMBER, COLLECTION_SET_ID)
       on delete restrict on update restrict;

 
alter table META_TRANSFORMATION_RULES
       add constraint TRE_VEN_FK foreign key (VERSION_NUMBER)
       references META_VERSIONS (VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_TRANSFORMATION_TABLES
       add constraint TTE_VEN_FK foreign key (VERSION_NUMBER)
       references META_VERSIONS (VERSION_NUMBER)
       on delete restrict on update restrict;

 
alter table META_TRANSFORMATION_TABLES
       add constraint TTE_COMN_KEY_FK foreign key (KEY_COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;

 
alter table META_TRANSFORMATION_TABLES
       add constraint TTE_COMN_VAL_FK foreign key (VALUE_COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       references META_COLUMNS (COLUMN_ID, VERSION_NUMBER, CONNECTION_ID, TABLE_ID)
       on delete restrict on update restrict;
