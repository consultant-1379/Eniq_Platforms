<?xml version="1.0"?>
<!DOCTYPE database SYSTEM "http://db.apache.org/torque/dtd/database">
  <database name="repdb">
    <table name="Aggregation">
      <column name="AGGREGATION" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="AGGREGATIONSET" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="AGGREGATIONGROUP" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="REAGGREGATIONSET" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="REAGGREGATIONGROUP" primaryKey="false" required="false" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="GROUPORDER" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="AGGREGATIONORDER" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="AGGREGATIONTYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="AGGREGATIONSCOPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
    </table>
    <table name="AggregationRule">
      <column name="AGGREGATION" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="RULEID" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="TARGET_TYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="TARGET_LEVEL" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="TARGET_TABLE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TARGET_MTABLEID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="SOURCE_TYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="SOURCE_LEVEL" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="SOURCE_TABLE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="SOURCE_MTABLEID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="RULETYPE" primaryKey="true" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="AGGREGATIONSCOPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="BHTYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="ENABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="AlarmInterface">
      <column name="INTERFACEID" primaryKey="true" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="true" type="VARCHAR" size="200" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="COLLECTION_SET_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="COLLECTION_ID" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
      <column name="QUEUE_NUMBER" primaryKey="false" required="true" type="NUMERIC" size="31" autoIncrement="false"/>
    </table>
    <table name="AlarmReport">
      <column name="INTERFACEID" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="REPORTID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="REPORTNAME" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="URL" primaryKey="false" required="true" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="SIMULTANEOUS" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="AlarmReportParameter">
      <column name="REPORTID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="NAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VALUE" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
    <table name="Busyhour">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHLEVEL" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BHTYPE" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="BHCRITERIA" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="WHERECLAUSE" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="TARGETVERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHOBJECT" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="BHELEMENT" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ENABLE" primaryKey="false" required="true" type="INTEGER" size="10" default="1" autoIncrement="false"/>
      <column name="AGGREGATIONTYPE" primaryKey="false" required="true" type="VARCHAR" size="128" default="RANKBH_TIMELIMITED" autoIncrement="false"/>
      <column name="OFFSET" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
      <column name="WINDOWSIZE" primaryKey="false" required="true" type="INTEGER" size="10" default="60" autoIncrement="false"/>
      <column name="LOOKBACK" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
      <column name="P_THRESHOLD" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
      <column name="N_THRESHOLD" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
      <column name="CLAUSE" primaryKey="false" required="true" type="VARCHAR" size="32000" default="&apos;" autoIncrement="false"/>
      <column name="PLACEHOLDERTYPE" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="GROUPING" primaryKey="false" required="true" type="VARCHAR" size="32" default="None" autoIncrement="false"/>
      <column name="REACTIVATEVIEWS" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
    </table>
    <table name="BusyhourMapping">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHLEVEL" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BHTYPE" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TARGETVERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHOBJECT" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BHTARGETTYPE" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHTARGETLEVEL" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="ENABLE" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="BusyhourPlaceholders">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHLEVEL" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PRODUCTPLACEHOLDERS" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="CUSTOMPLACEHOLDERS" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="BusyhourRankkeys">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHLEVEL" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BHTYPE" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="KEYNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="KEYVALUE" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="ORDERNBR" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="TARGETVERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHOBJECT" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="BusyhourSource">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHLEVEL" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BHTYPE" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TYPENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TARGETVERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BHOBJECT" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="Configuration">
      <column name="PARAMNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PARAMVALUE" primaryKey="false" required="true" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
    <table name="CountingInterval">
      <column name="INTERVAL" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INTERVALNAME" primaryKey="false" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="INTERVALDESCRIPTION" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
    <table name="CountingManagement">
      <column name="STORAGEID" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TABLENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="LASTAGGREGATEDROW" primaryKey="false" required="true" type="BIGINT" size="19" autoIncrement="false"/>
    </table>
    <table name="DataFormat">
      <column name="DATAFORMATID" primaryKey="true" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="TYPEID" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="OBJECTTYPE" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="FOLDERNAME" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DATAFORMATTYPE" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
    </table>
    <table name="DataInterface">
      <column name="INTERFACENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="INTERFACETYPE" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="DATAFORMATTYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="INTERFACEVERSION" primaryKey="true" required="true" type="VARCHAR" size="32" default="N/A" autoIncrement="false"/>
      <column name="LOCKEDBY" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="LOCKDATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="PRODUCTNUMBER" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="ENIQ_LEVEL" primaryKey="false" required="false" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="RSTATE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="INSTALLDESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
    <table name="DataItem">
      <column name="DATAFORMATID" primaryKey="true" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="COLNUMBER" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DATAID" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PROCESS_INSTRUCTION" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="DefaultTags">
      <column name="TAGID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DATAFORMATID" primaryKey="true" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="200" autoIncrement="false"/>
    </table>
    <table name="DWHColumn">
      <column name="STORAGEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="COLNUMBER" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIQUEVALUE" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="NULLABLE" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INDEXES" primaryKey="false" required="true" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="UNIQUEKEY" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="false" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="INCLUDESQL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="DWHPartition">
      <column name="STORAGEID" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TABLENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="STARTTIME" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="ENDTIME" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="LOADORDER" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="DWHTechPacks">
      <column name="TECHPACK_NAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CREATIONDATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
    </table>
    <table name="DWHType">
      <column name="TECHPACK_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="TYPENAME" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TABLELEVEL" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="STORAGEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PARTITIONSIZE" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="PARTITIONCOUNT" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="OWNER" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="VIEWTEMPLATE" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="CREATETEMPLATE" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="NEXTPARTITIONTIME" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="BASETABLENAME" primaryKey="false" required="true" type="VARCHAR" size="125" autoIncrement="false"/>
      <column name="DATADATECOLUMN" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PUBLICVIEWTEMPLATE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PARTITIONPLAN" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="ENIQ_EVENTS_ADMIN_PROPERTIES">
      <column name="PARAM_NAME" primaryKey="true" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="PARAM_VALUE" primaryKey="false" required="true" type="VARCHAR" size="1000" autoIncrement="false"/>
      <column name="DATE_MODIFIED" primaryKey="false" required="true" type="TIMESTAMP" size="23" default="getdate()" autoIncrement="false"/>
      <column name="MODIFIED_BY" primaryKey="false" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
    </table>
    <table name="ExternalStatement">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="STATEMENTNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="EXECUTIONORDER" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DBCONNECTION" primaryKey="false" required="true" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="STATEMENT" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="BASEDEF" primaryKey="true" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
    </table>
    <table name="ExternalStatementStatus">
      <column name="TECHPACK_NAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="STATEMENTNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="EXECTIME" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="EXECSTATEMENT" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
    <table name="GroupTypes">
      <column name="GROUPTYPE" primaryKey="true" required="true" type="VARCHAR" size="64" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
      <column name="NULLABLE" primaryKey="false" required="true" type="SMALLINT" size="5" default="0" autoIncrement="false"/>
    </table>
    <table name="InfoMessages">
      <column name="MSGID" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="TITLE" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="MSGDATE" primaryKey="false" required="true" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="NAME" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="EMAIL" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="MSG" primaryKey="false" required="false" type="VARCHAR" size="500" autoIncrement="false"/>
    </table>
    <table name="InterfaceDependency">
      <column name="INTERFACEVERSION" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="INTERFACENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TECHPACKNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TECHPACKVERSION" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="InterfaceMeasurement">
      <column name="TAGID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DATAFORMATID" primaryKey="false" required="true" type="VARCHAR" size="100" autoIncrement="false"/>
      <column name="INTERFACENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TRANSFORMERID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="MODIFTIME" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="TECHPACKVERSION" primaryKey="false" required="true" type="VARCHAR" size="32" default="N/A" autoIncrement="false"/>
      <column name="INTERFACEVERSION" primaryKey="true" required="true" type="VARCHAR" size="32" default="N/A" autoIncrement="false"/>
    </table>
    <table name="InterfaceTechpacks">
      <column name="INTERFACENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TECHPACKNAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="TECHPACKVERSION" primaryKey="true" required="true" type="VARCHAR" size="32" default="N/A" autoIncrement="false"/>
      <column name="INTERFACEVERSION" primaryKey="true" required="true" type="VARCHAR" size="32" default="N/A" autoIncrement="false"/>
    </table>
    <table name="MeasurementColumn">
      <column name="MTABLEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="COLNUMBER" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIQUEVALUE" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="NULLABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INDEXES" primaryKey="false" required="false" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="DATAID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="RELEASEID" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="UNIQUEKEY" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INCLUDESQL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="COLTYPE" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="FOLLOWJOHN" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="MeasurementCounter">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="TIMEAGGREGATION" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="GROUPAGGREGATION" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="COUNTAGGREGATION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="COLNUMBER" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INCLUDESQL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIVOBJECT" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVCLASS" primaryKey="false" required="false" type="VARCHAR" size="35" autoIncrement="false"/>
      <column name="COUNTERTYPE" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="COUNTERPROCESS" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="DATAID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="FOLLOWJOHN" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="MeasurementDeltaCalcSupport">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VENDORRELEASE" primaryKey="true" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="DELTACALCSUPPORT" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="MeasurementKey">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="ISELEMENT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIQUEKEY" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="COLNUMBER" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIQUEVALUE" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="NULLABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INDEXES" primaryKey="false" required="false" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="INCLUDESQL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIVOBJECT" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="JOINABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATAID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
    <table name="MeasurementObjBHSupport">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJBHSUPPORT" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="MeasurementTable">
      <column name="MTABLEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TABLELEVEL" primaryKey="false" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="TYPEID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BASETABLENAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DEFAULT_TEMPLATE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="PARTITIONPLAN" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="MeasurementType">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TYPECLASSID" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TYPENAME" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VENDORID" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="FOLDERNAME" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="OBJECTID" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJECTNAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJECTVERSION" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="OBJECTTYPE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="JOINABLE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="SIZING" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="TOTALAGG" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ELEMENTBHSUPPORT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="RANKINGTABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DELTACALCSUPPORT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="PLAINTABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="false" required="false" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="VECTORSUPPORT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATAFORMATSUPPORT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="FOLLOWJOHN" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ONEMINAGG" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="FIFTEENMINAGG" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="EVENTSCALCTABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="MIXEDPARTITIONSTABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="LOADFILE_DUP_CHECK" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="MeasurementTypeClass">
      <column name="TYPECLASSID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
    <table name="MeasurementVector">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="VENDORRELEASE" primaryKey="true" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="VINDEX" primaryKey="true" required="true" type="NUMERIC" size="30,6" autoIncrement="false"/>
      <column name="VFROM" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VTO" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="MEASURE" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="QUANTITY" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="MZTechPacks">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TECHPACK_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="false" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="CREATIONDATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="PRODUCT_NUMBER" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="TECHPACK_VERSION" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="PartitionPlan">
      <column name="PARTITIONPLAN" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DEFAULTSTORAGETIME" primaryKey="false" required="true" type="NUMERIC" size="20" autoIncrement="false"/>
      <column name="DEFAULTPARTITIONSIZE" primaryKey="false" required="true" type="NUMERIC" size="20" autoIncrement="false"/>
      <column name="MAXSTORAGETIME" primaryKey="false" required="false" type="NUMERIC" size="20" autoIncrement="false"/>
      <column name="PARTITIONTYPE" primaryKey="false" required="true" type="TINYINT" size="3" autoIncrement="false"/>
    </table>
    <table name="Prompt">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PROMPTIMPLEMENTORID" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="PROMPTNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="ORDERNUMBER" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNREFRESHABLE" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="PromptImplementor">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PROMPTIMPLEMENTORID" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="PROMPTCLASSNAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PRIORITY" primaryKey="false" required="true" type="INTEGER" size="10" autoIncrement="false"/>
    </table>
    <table name="PromptOption">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PROMPTIMPLEMENTORID" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="OPTIONNAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OPTIONVALUE" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
    <table name="ReferenceColumn">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATANAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="COLNUMBER" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="DATATYPE" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="DATASIZE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="DATASCALE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="UNIQUEVALUE" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="NULLABLE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INDEXES" primaryKey="false" required="false" type="VARCHAR" size="20" autoIncrement="false"/>
      <column name="UNIQUEKEY" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INCLUDESQL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INCLUDEUPD" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="COLTYPE" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="UNIVERSECLASS" primaryKey="false" required="false" type="VARCHAR" size="35" autoIncrement="false"/>
      <column name="UNIVERSEOBJECT" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSECONDITION" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DATAID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="BASEDEF" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
    </table>
    <table name="ReferenceTable">
      <column name="TYPEID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TYPENAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJECTID" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJECTNAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJECTVERSION" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="OBJECTTYPE" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="UPDATE_POLICY" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="TABLE_TYPE" primaryKey="false" required="false" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="DATAFORMATSUPPORT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="BASEDEF" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
    </table>
    <table name="SupportedVendorRelease">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="VENDORRELEASE" primaryKey="true" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
    </table>
    <table name="TechPackDependency">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TECHPACKNAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="VERSION" primaryKey="false" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="TPActivation">
      <column name="TECHPACK_NAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="MODIFIED" primaryKey="false" required="true" type="INTEGER" size="10" default="0" autoIncrement="false"/>
    </table>
    <table name="Transformation">
      <column name="TRANSFORMERID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="ORDERNO" primaryKey="true" required="true" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="SOURCE" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TARGET" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CONFIG" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
    </table>
    <table name="Transformer">
      <column name="TRANSFORMERID" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="VERSIONID" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="50" default="SPECIFIC" autoIncrement="false"/>
    </table>
    <table name="TypeActivation">
      <column name="TECHPACK_NAME" primaryKey="true" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="true" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="TYPENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TABLELEVEL" primaryKey="true" required="true" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="STORAGETIME" primaryKey="false" required="false" type="NUMERIC" size="15" autoIncrement="false"/>
      <column name="TYPE" primaryKey="false" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="PARTITIONPLAN" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="UniverseClass">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CLASSNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="PARENT" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="OBJ_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ELEM_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INHERITANCE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="UniverseComputedObject">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CLASSNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="OBJECTNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="OBJECTTYPE" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="QUALIFICATION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="AGGREGATION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="OBJSELECT" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="OBJWHERE" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="PROMPTHIERARCHY" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJ_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ELEM_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INHERITANCE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="UniverseCondition">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CLASSNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="UNIVERSECONDITION" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="CONDWHERE" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="AUTOGENERATE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="CONDOBJCLASS" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CONDOBJECT" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PROMPTTEXT" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="MULTISELECTION" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="FREETEXT" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="OBJ_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ELEM_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INHERITANCE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="UniverseFormulas">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TECHPACK_TYPE" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="NAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="FORMULA" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="OBJECTTYPE" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="QUALIFICATION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="AGGREGATION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="UniverseJoin">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="SOURCETABLE" primaryKey="true" required="true" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="SOURCELEVEL" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="SOURCECOLUMN" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TARGETTABLE" primaryKey="true" required="true" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="TARGETLEVEL" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TARGETCOLUMN" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="EXPRESSION" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="CARDINALITY" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="CONTEXT" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="EXCLUDEDCONTEXTS" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="TMPCOUNTER" primaryKey="true" required="true" type="INTEGER" size="10" default="autoincrement" autoIncrement="true"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="false" required="false" type="VARCHAR" size="12" autoIncrement="false"/>
    </table>
    <table name="UniverseName">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSENAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSIONNAME" primaryKey="false" required="false" type="VARCHAR" size="35" autoIncrement="false"/>
    </table>
    <table name="UniverseObject">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CLASSNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="OBJECTNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="OBJECTTYPE" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="QUALIFICATION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="AGGREGATION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="OBJSELECT" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="OBJWHERE" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="PROMPTHIERARCHY" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJ_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ELEM_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INHERITANCE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="UniverseParameters">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="CLASSNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="OBJECTNAME" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="true" required="true" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="NAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="TYPENAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
    <table name="UniverseTable">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="TABLENAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="true" required="true" type="VARCHAR" size="12" autoIncrement="false"/>
      <column name="OWNER" primaryKey="false" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="ALIAS" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="OBJ_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ELEM_BH_REL" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="INHERITANCE" primaryKey="false" required="false" type="INTEGER" size="10" autoIncrement="false"/>
      <column name="ORDERNRO" primaryKey="false" required="false" type="NUMERIC" size="30,6" autoIncrement="false"/>
    </table>
    <table name="UserAccount">
      <column name="NAME" primaryKey="true" required="true" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PASSWORD" primaryKey="false" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="ROLE" primaryKey="false" required="true" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="LASTLOGIN" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
    </table>
    <table name="VerificationCondition">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="FACTTABLE" primaryKey="false" required="true" type="VARCHAR" size="2560" autoIncrement="false"/>
      <column name="VERLEVEL" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="CONDITIONCLASS" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="VERCONDITION" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PROMPTNAME1" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PROMPTVALUE1" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="PROMPTNAME2" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PROMPTVALUE2" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="OBJECTCONDITION" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PROMPTNAME3" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="PROMPTVALUE3" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
    </table>
    <table name="VerificationObject">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="MEASTYPE" primaryKey="false" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="MEASLEVEL" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="OBJECTCLASS" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="OBJECTNAME" primaryKey="true" required="true" type="VARCHAR" size="32" autoIncrement="false"/>
    </table>
    <table name="Versioning">
      <column name="VERSIONID" primaryKey="true" required="true" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="DESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="50" autoIncrement="false"/>
      <column name="STATUS" primaryKey="false" required="false" type="NUMERIC" size="9" autoIncrement="false"/>
      <column name="TECHPACK_NAME" primaryKey="false" required="true" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="TECHPACK_VERSION" primaryKey="false" required="false" type="VARCHAR" size="32" autoIncrement="false"/>
      <column name="TECHPACK_TYPE" primaryKey="false" required="false" type="VARCHAR" size="10" autoIncrement="false"/>
      <column name="PRODUCT_NUMBER" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="LOCKEDBY" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
      <column name="LOCKDATE" primaryKey="false" required="false" type="TIMESTAMP" size="23" autoIncrement="false"/>
      <column name="BASEDEFINITION" primaryKey="false" required="false" type="VARCHAR" size="128" autoIncrement="false"/>
      <column name="BASEVERSION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="INSTALLDESCRIPTION" primaryKey="false" required="false" type="VARCHAR" size="32000" autoIncrement="false"/>
      <column name="UNIVERSENAME" primaryKey="false" required="false" type="VARCHAR" size="30" autoIncrement="false"/>
      <column name="UNIVERSEEXTENSION" primaryKey="false" required="false" type="VARCHAR" size="16" autoIncrement="false"/>
      <column name="ENIQ_LEVEL" primaryKey="false" required="true" type="VARCHAR" size="12" default="1.0" autoIncrement="false"/>
      <column name="LICENSENAME" primaryKey="false" required="false" type="VARCHAR" size="255" autoIncrement="false"/>
    </table>
  </database>
