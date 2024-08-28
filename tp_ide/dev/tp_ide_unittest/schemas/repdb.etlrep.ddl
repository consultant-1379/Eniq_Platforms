<?xml version="1.0"?>
<!DOCTYPE database SYSTEM "http://db.apache.org/torque/dtd/database">
  <database name="repdb">
    <table name="META_COLLECTION_SETS">
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_SET_NAME" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="ENABLED_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
      <unique name="CST_NAME_UI">
        <unique-column name="VERSION_NUMBER"/>
        <unique-column name="COLLECTION_SET_NAME"/>
      </unique>
    </table>
    <table name="META_COLLECTIONS">
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_NAME" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="COLLECTION" primaryKey="false" required="false" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="MAIL_ERROR_ADDR" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="MAIL_FAIL_ADDR" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="MAIL_BUG_ADDR" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="MAX_ERRORS" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="MAX_FK_ERRORS" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="MAX_COL_LIMIT_ERRORS" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="CHECK_FK_ERROR_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="CHECK_COL_LIMITS_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="LAST_TRANSFER_DATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="USE_BATCH_ID" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="PRIORITY" primaryKey="false" required="false" type="NUMERIC" size="3" autoIncrement="false"/>
      <column name="QUEUE_TIME_LIMIT" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="ENABLED_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="SETTYPE" primaryKey="false" required="false" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="FOLDABLE_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="MEASTYPE" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="HOLD_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="SCHEDULING_INFO" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <unique name="CON_NAME_UI">
        <unique-column name="COLLECTION_NAME"/>
        <unique-column name="COLLECTION_SET_ID"/>
        <unique-column name="VERSION_NUMBER"/>
      </unique>
    </table>
    <table name="META_COLUMN_CONSTRAINTS">
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="LOW_VALUE" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="HIGH_VALUE" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLUMN_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_COLUMNS">
      <column name="COLUMN_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLUMN_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="COLUMN_ALIAS_NAME" primaryKey="false" required="false" type="VARCHAR" size="60" autoIncrement="false"/>
      <column name="COLUMN_TYPE" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="COLUMN_LENGTH" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="IS_PK_COLUMN" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_CONNECTION_TYPES">
      <column name="TYPE_NAME" primaryKey="true" required="true" type="VARCHAR" size="15" autoIncrement="false"/>
      <column name="DATABASE_TYPE_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
    </table>
    <table name="META_DATABASES">
      <column name="USERNAME" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TYPE_NAME" primaryKey="false" required="true" type="VARCHAR" size="15" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="CONNECTION_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="CONNECTION_STRING" primaryKey="false" required="true" type="VARCHAR" size="400" autoIncrement="false"/>
      <column name="PASSWORD" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="DRIVER_NAME" primaryKey="false" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="DB_LINK_NAME" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="META_DEBUGS">
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TEXT" primaryKey="false" required="true" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="LAST_UPDATED" primaryKey="false" required="true" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="false" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_BATCH_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_ERRORS">
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TEXT" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="METHOD_NAME" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="ERR_TYPE" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="LAST_UPDATED" primaryKey="false" required="true" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="false" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_BATCH_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_EXECUTION_SLOT">
      <column name="PROFILE_ID" primaryKey="false" required="true" type="VARCHAR" size="38" autoIncrement="false"/>
      <column name="SLOT_NAME" primaryKey="false" required="true" type="VARCHAR" size="15" autoIncrement="false"/>
      <column name="SLOT_ID" primaryKey="true" required="true" type="VARCHAR" size="38" autoIncrement="false"/>
      <column name="ACCEPTED_SET_TYPES" primaryKey="false" required="true" type="VARCHAR" size="2000" autoIncrement="false"/>
    </table>
    <table name="META_EXECUTION_SLOT_PROFILE">
      <column name="PROFILE_NAME" primaryKey="false" required="true" type="VARCHAR" size="15" autoIncrement="false"/>
      <column name="PROFILE_ID" primaryKey="true" required="true" type="VARCHAR" size="38" autoIncrement="false"/>
      <column name="ACTIVE_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
    </table>
    <table name="META_FILES">
      <column name="FILE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="FILE_NAME" primaryKey="false" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="FILE_CONTENT_TYPE" primaryKey="false" required="true" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="ROW_DELIM" primaryKey="false" required="false" type="VARCHAR" size="5" autoIncrement="false"/>
      <column name="COLUMN_DELIM" primaryKey="false" required="false" type="VARCHAR" size="5" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COMMIT_AFTER_N_ROWS" primaryKey="false" required="false" type="NUMERIC" size="10" autoIncrement="false"/>
      <column name="IS_SOURCE" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_FK_TABLE_JOINTS">
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLUMN_ID_FK_COLUMN" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TARGET_TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLUMN_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_FK_TABLES">
      <column name="MAX_ERRORS" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="WHERE_CLAUSE" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="FILTER_ERRORS_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="REPLACE_ERRORS_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="REPLACE_ERRORS_WITH" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TARGET_TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_JOINTS">
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="IS_PK_COLUMN" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="IS_SUM_COLUMN" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="IS_GROUP_BY_COLUMN" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="COLUMN_SPACE_AT_FILE" primaryKey="false" required="false" type="NUMERIC" size="10" autoIncrement="false"/>
      <column name="FILE_ORDER_BY" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="PLUGIN_METHOD_NAME" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TARGET_CONNECTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TARGET_TABLE_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLUMN_ID_TARGET_COLUMN" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="SOURCE_CONNECTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="SOURCE_TABLE_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLUMN_ID_SOURCE_COLUMN" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFORMATION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSF_TABLE_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="PAR_NAME" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="FILE_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="PLUGIN_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="FREE_FORMAT_TRANSFORMAT" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="METHOD_PARAMETER" primaryKey="false" required="false" type="VARCHAR" size="200" autoIncrement="false"/>
    </table>
    <table name="META_PARAMETER_TABLES">
      <column name="PAR_NAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="PAR_VALUE" primaryKey="false" required="true" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="META_PARAMETERS">
      <column name="RB_SEGMENT_NAME" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="USE_RB_SEGMENT_FLAG" primaryKey="false" required="true" type="CHAR" size="1" autoIncrement="false"/>
      <column name="DEFAULT_ERROR_MAIL_ADDR" primaryKey="false" required="false" type="CHAR" size="100" autoIncrement="false"/>
      <column name="DEFAULT_FAIL_MAIL_ADDR" primaryKey="false" required="false" type="CHAR" size="100" autoIncrement="false"/>
      <column name="DEFAULT_BUG_ERROR_MAIL_ADDR" primaryKey="false" required="false" type="CHAR" size="100" autoIncrement="false"/>
      <column name="DEFAULT_MAX_ERROR_VALUE" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="DEFAULT_MAX_FK_ERROR_VALUE" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="DEFAULT_MAX_COL_LIMIT_VALUE" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TEMP_SUM_TABLESPACE" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="USE_TEMP_SUM_TABLESPACE_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="BATCH_COLUMN_NAME" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="META_PLUGINS">
      <column name="PLUGIN_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="PLUGIN_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="CONSTRUCTOR_PARAMETER" primaryKey="false" required="false" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="IS_SOURCE" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COMMIT_AFTER_N_ROWS" primaryKey="false" required="false" type="NUMERIC" size="10" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_SCHEDULINGS">
      <column name="VERSION_NUMBER" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="EXECUTION_TYPE" primaryKey="false" required="true" type="VARCHAR" size="15" autoIncrement="false"/>
      <column name="OS_COMMAND" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="SCHEDULING_MONTH" primaryKey="false" required="false" type="NUMERIC" size="2" autoIncrement="false"/>
      <column name="SCHEDULING_DAY" primaryKey="false" required="false" type="NUMERIC" size="2" autoIncrement="false"/>
      <column name="SCHEDULING_HOUR" primaryKey="false" required="false" type="NUMERIC" size="2" autoIncrement="false"/>
      <column name="SCHEDULING_MIN" primaryKey="false" required="false" type="NUMERIC" size="2" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="MON_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="TUE_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="WED_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="THU_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="FRI_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="SAT_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="SUN_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="false" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="LAST_EXECUTION_TIME" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="INTERVAL_HOUR" primaryKey="false" required="false" type="NUMERIC" size="2" autoIncrement="false"/>
      <column name="INTERVAL_MIN" primaryKey="false" required="false" type="NUMERIC" size="2" autoIncrement="false"/>
      <column name="NAME" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="HOLD_FLAG" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="PRIORITY" primaryKey="false" required="false" type="NUMERIC" size="3" autoIncrement="false"/>
      <column name="SCHEDULING_YEAR" primaryKey="false" required="false" type="NUMERIC" size="4" autoIncrement="false"/>
      <column name="TRIGGER_COMMAND" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="LAST_EXEC_TIME_MS" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_SERVERS">
      <column name="HOSTNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="STATUS_URL" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
    <table name="META_SOURCE_TABLES">
      <column name="LAST_TRANSFER_DATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="USE_TR_DATE_IN_WHERE_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="DISTINCT_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="AS_SELECT_OPTIONS" primaryKey="false" required="false" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="AS_SELECT_TABLESPACE" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TIMESTAMP_COLUMN_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_SQL_LOADS">
      <column name="INPUT_FILE" primaryKey="false" required="true" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="CTL_FILE" primaryKey="false" required="true" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="DIS_FILE" primaryKey="false" required="true" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="BAD_FILE" primaryKey="false" required="true" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="LOAD_TYPE" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="TEXT" primaryKey="false" required="true" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="DELIM" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="SQLLDR_CMD" primaryKey="false" required="false" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="LOAD_OPTION" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_STATUSES">
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="STATUS_DESCRIPTION" primaryKey="false" required="true" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="LAST_UPDATED" primaryKey="false" required="true" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="false" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_BATCH_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_SYSTEM_MONITORS">
      <column name="MONITOR" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="HOSTNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="CONFIGURATION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="EXECUTED" primaryKey="false" required="true" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
    </table>
    <table name="META_TABLES">
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_NAME" primaryKey="false" required="true" type="VARCHAR" size="60" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="IS_JOIN" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="JOIN_CLAUSE" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="TABLES_AND_ALIASES" primaryKey="false" required="false" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_TARGET_TABLES">
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_TRANSF_TABLE_VALUES">
      <column name="OLD_VALUE" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="NEW_VALUE" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TRANSF_TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_TRANSFER_ACTIONS">
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="ACTION_TYPE" primaryKey="false" required="true" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="TRANSFER_ACTION_NAME" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="ORDER_BY_NO" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="WHERE_CLAUSE_01" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="ACTION_CONTENTS_01" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="ENABLED_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="WHERE_CLAUSE_02" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="WHERE_CLAUSE_03" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="ACTION_CONTENTS_02" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="ACTION_CONTENTS_03" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
    <table name="META_TRANSFER_BATCHES">
      <column name="ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="START_DATE" primaryKey="false" required="true" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="END_DATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="FAIL_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="META_COLLECTION_NAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="META_COLLECTION_SET_NAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="SETTYPE" primaryKey="false" required="false" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="SLOT_ID" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="SCHEDULING_INFO" primaryKey="false" required="false" type="VARCHAR" size="16000" autoIncrement="false"/>
      <index name="mt_tr_bat_end">
        <index-column name="END_DATE"/>
      </index>
      <index name="mt_tr_bat_start">
        <index-column name="START_DATE"/>
      </index>
    </table>
    <table name="META_TRANSFORMATION_RULES">
      <column name="TRANSFORMATION_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSFORMATION_NAME" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="CODE" primaryKey="false" required="true" type="VARCHAR" size="2000" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="META_TRANSFORMATION_TABLES">
      <column name="TRANSF_TABLE_ID" primaryKey="true" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TRANSF_TABLE_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="IS_LOOKUP" primaryKey="false" required="false" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="CONNECTION_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="TABLE_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="KEY_COLUMN_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="VALUE_COLUMN_ID" primaryKey="false" required="false" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="META_VERSIONS">
      <column name="VERSION_NUMBER" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="CURRENT_FLAG" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="IS_PREDEFINED" primaryKey="false" required="true" type="VARCHAR" size="1" autoIncrement="false"/>
      <column name="ENGINE_SERVER" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="MAIL_SERVER" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="SCHEDULER_SERVER" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="MAIL_SERVER_PORT" primaryKey="false" required="false" type="NUMERIC" size="5" autoIncrement="false"/>
    </table>
    <table name="PriorityQueue">
      <column name="queueid" primaryKey="true" required="true" type="BIGINT" size="19" autoIncrement="false"/>
      <column name="obj" primaryKey="false" required="true" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
  </database>
