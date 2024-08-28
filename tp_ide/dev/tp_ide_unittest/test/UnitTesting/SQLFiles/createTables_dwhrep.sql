

--/***************************
--  Aggregation
--/***************************
create table Aggregation (
	AGGREGATION varchar(255) not null,
	VERSIONID varchar(128) not null,
	AGGREGATIONSET varchar(100) ,
	AGGREGATIONGROUP varchar(100) ,
	REAGGREGATIONSET varchar(100) ,
	REAGGREGATIONGROUP varchar(100) ,
	GROUPORDER integer ,
	AGGREGATIONORDER integer ,
	AGGREGATIONTYPE varchar(50) ,
	AGGREGATIONSCOPE varchar(50) 
);

 alter table Aggregation
       add primary key (AGGREGATION, VERSIONID);


--/***************************
--  AggregationRule
--/***************************
create table AggregationRule (
	AGGREGATION varchar(255) not null,
	VERSIONID varchar(128) not null,
	RULEID integer not null,
	TARGET_TYPE varchar(50) ,
	TARGET_LEVEL varchar(50) ,
	TARGET_TABLE varchar(255) ,
	TARGET_MTABLEID varchar(255) ,
	SOURCE_TYPE varchar(50) ,
	SOURCE_LEVEL varchar(50) ,
	SOURCE_TABLE varchar(255) ,
	SOURCE_MTABLEID varchar(255) ,
	RULETYPE varchar(50) not null,
	AGGREGATIONSCOPE varchar(50) ,
	BHTYPE varchar(50),
	ENABLE integer null
);

 alter table AggregationRule
       add primary key (AGGREGATION, VERSIONID, RULEID, RULETYPE);


--/***************************
--  AlarmInterface
--/***************************
create table AlarmInterface (
	INTERFACEID varchar(50) not null,
	DESCRIPTION varchar(200) not null,
	STATUS varchar(20) not null,
	COLLECTION_SET_ID numeric(31) not null,
	COLLECTION_ID numeric(31) not null,
	QUEUE_NUMBER numeric(31) not null
);

 alter table AlarmInterface
       add primary key (INTERFACEID);


--/***************************
--  AlarmReport
--/***************************
create table AlarmReport (
	INTERFACEID varchar(50) not null,
	REPORTID varchar(255) not null,
	REPORTNAME varchar(255) not null,
	URL varchar(32000) not null,
	STATUS varchar(10) not null
);

 alter table AlarmReport
       add primary key (REPORTID);


--/***************************
--  AlarmReportParameter
--/***************************
create table AlarmReportParameter (
	REPORTID varchar(255) not null,
	NAME varchar(255) not null,
	VALUE varchar(255) not null
);

 alter table AlarmReportParameter
       add primary key (REPORTID, NAME);


--/***************************
--  Busyhour
--/***************************
create table Busyhour (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(32) not null,
	BHTYPE varchar(32) not null,
	BHCRITERIA varchar(32000) ,
	WHERECLAUSE varchar(32000) ,
	DESCRIPTION varchar(32000) ,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null,
	BHELEMENT integer not null,
	ENABLE integer null,
	AGGREGATIONTYPE varchar(128) default 'RANKBH_TIMELIMITED' not null,
	OFFSET integer null,
	WINDOWSIZE integer null,
	LOOKBACK integer null,
	P_THRESHOLD integer null,
	N_THRESHOLD integer null,
	CLAUSE varchar(32000) null,
	PLACEHOLDERTYPE varchar(128) null,
	GROUPING varchar(32) default 'None' not null,
	REACTIVATEVIEWS integer default 0 not null
);

 alter table Busyhour 
       add primary key (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT);


--/***************************
--  BusyhourMapping
--/***************************
create table BusyhourMapping (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(32) not null,
	BHTYPE varchar(32) not null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null,
	TYPEID varchar(255) not null,
	BHTARGETTYPE varchar(128) not null,
	BHTARGETLEVEL varchar(128) not null,
	ENABLE integer not null
);

 alter table BusyhourMapping
       add primary key (VERSIONID, BHLEVEL, BHTYPE, TARGETVERSIONID, BHOBJECT, TYPEID);

--/***************************
--  BusyhourPlaceholders
--/***************************
create table BusyhourPlaceholders (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(32) not null,
	PRODUCTPLACEHOLDERS integer null,
	CUSTOMPLACEHOLDERS integer null
);

 alter table BusyhourPlaceholders
       add primary key (VERSIONID, BHLEVEL);
  
--/***************************
--  BusyhourRankkeys
--/***************************
create table BusyhourRankkeys (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(32) not null,
	BHTYPE varchar(32) not null,
	KEYNAME varchar(128) not null,
	KEYVALUE varchar(128) not null,
	ORDERNBR numeric(9) not null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null
);

 alter table BusyhourRankkeys
       add primary key (VERSIONID, BHLEVEL, BHTYPE, KEYNAME, TARGETVERSIONID, BHOBJECT);


--/***************************
--  BusyhourSource
--/***************************
create table BusyhourSource (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(32) not null,
	BHTYPE varchar(32) not null,
	TYPENAME varchar(255) not null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null
);

 alter table BusyhourSource
       add primary key (VERSIONID, BHLEVEL, BHTYPE, TYPENAME, TARGETVERSIONID, BHOBJECT);


--/***************************
--  Configuration
--/***************************
create table Configuration (
	PARAMNAME varchar(255) not null,
	PARAMVALUE varchar(32000) not null
);

 alter table Configuration
       add primary key (PARAMNAME);


--/***************************
--  DataFormat
--/***************************
create table DataFormat (
	DATAFORMATID varchar(100) not null,
	TYPEID varchar(255) not null,
	VERSIONID varchar(128) not null,
	OBJECTTYPE varchar(255) not null,
	FOLDERNAME varchar(50) not null,
	DATAFORMATTYPE varchar(50) not null
);

 alter table DataFormat
       add primary key (DATAFORMATID);


--/***************************
--  DataInterface
--/***************************
create table DataInterface (
	INTERFACENAME varchar(50) not null,
	STATUS numeric(9) not null,
	INTERFACETYPE varchar(50) not null,
	DESCRIPTION varchar(32000) ,
	DATAFORMATTYPE varchar(50) ,
	INTERFACEVERSION varchar(32) not null,
	LOCKEDBY varchar(255) ,
	LOCKDATE timestamp ,
	PRODUCTNUMBER varchar(255) ,
	ENIQ_LEVEL varchar(12) ,
	RSTATE varchar(255) ,
	INSTALLDESCRIPTION varchar(32000)
);

 alter table DataInterface
       add primary key (INTERFACENAME, INTERFACEVERSION);


--/***************************
--  DataItem
--/***************************
create table DataItem (
	DATAFORMATID varchar(100) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9) not null,
	DATAID varchar(255) not null,
	PROCESS_INSTRUCTION varchar(128),
	DATATYPE varchar(50) null,
	DATASIZE integer null,
	DATASCALE integer null 
);

 alter table DataItem
       add primary key (DATAFORMATID, DATANAME);


--/***************************
--  DefaultTags
--/***************************
create table DefaultTags (
	TAGID varchar(50) not null,
	DATAFORMATID varchar(100) not null,
	DESCRIPTION varchar(200) 
);

 alter table DefaultTags
       add primary key (TAGID, DATAFORMATID);


--/***************************
--  DWHColumn
--/***************************
create table DWHColumn (
	STORAGEID varchar(255) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9) not null,
	DATATYPE varchar(50) not null,
	DATASIZE integer not null,
	DATASCALE integer not null,
	UNIQUEVALUE numeric(9) not null,
	NULLABLE integer not null,
	INDEXES varchar(20) not null,
	UNIQUEKEY integer not null,
	STATUS varchar(10) ,
	INCLUDESQL integer 
);

 alter table DWHColumn
       add primary key (STORAGEID, DATANAME);


--/***************************
--  DWHPartition
--/***************************
create table DWHPartition (
	STORAGEID varchar(255) not null,
	TABLENAME varchar(255) not null,
	STARTTIME timestamp ,
	ENDTIME timestamp ,
	STATUS varchar(10) not null
);

 alter table DWHPartition
       add primary key (TABLENAME);


--/***************************
--  DWHTechPacks
--/***************************
create table DWHTechPacks (
	TECHPACK_NAME varchar(30) not null,
	VERSIONID varchar(128) not null,
	CREATIONDATE timestamp 
);

 alter table DWHTechPacks
       add primary key (TECHPACK_NAME);


--/***************************
--  DWHType
--/***************************
create table DWHType (
	TECHPACK_NAME varchar(30) not null,
	TYPENAME varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	STORAGEID varchar(255) not null,
	PARTITIONSIZE numeric(9) not null,
	PARTITIONCOUNT numeric(9) ,
	STATUS varchar(50) not null,
	TYPE varchar(50) not null,
	OWNER varchar(50) ,
	VIEWTEMPLATE varchar(255) not null,
	CREATETEMPLATE varchar(255) not null,
	NEXTPARTITIONTIME timestamp ,
	BASETABLENAME varchar(125) not null,
	DATADATECOLUMN varchar(128) ,
	PUBLICVIEWTEMPLATE varchar(255) ,
	PARTITIONPLAN varchar(128) 
);

 alter table DWHType
       add primary key (STORAGEID);


--/***************************
--  ExternalStatement
--/***************************
create table ExternalStatement (
	VERSIONID varchar(128) not null,
	STATEMENTNAME varchar(255) not null,
	EXECUTIONORDER numeric(9) not null,
	DBCONNECTION varchar(20) not null,
	STATEMENT varchar(32000),
    BASEDEF integer default 0 not null 
);

 alter table ExternalStatement
       add primary key (VERSIONID, STATEMENTNAME, BASEDEF);


--/***************************
--  ExternalStatementStatus
--/***************************
create table ExternalStatementStatus (
	TECHPACK_NAME varchar(30) not null,
	STATEMENTNAME varchar(255) not null,
	VERSIONID varchar(128) not null,
	STATUS varchar(10) not null,
	EXECTIME timestamp ,
	EXECSTATEMENT varchar(32000) 
);

 alter table ExternalStatementStatus
       add primary key (TECHPACK_NAME, STATEMENTNAME, VERSIONID);


--/***************************
--  InfoMessages
--/***************************
create table InfoMessages (
	MSGID integer not null,
	TITLE varchar(50) not null,
	MSGDATE timestamp not null,
	NAME varchar(50) not null,
	EMAIL varchar(50) not null,
	STATUS varchar(20) not null,
	MSG varchar(500) 
);

 alter table InfoMessages
       add primary key (MSGID);


--/***************************
--  InterfaceDependency
--/***************************
create table InterfaceDependency (
	INTERFACEVERSION varchar(32) not null,
	INTERFACENAME varchar(50) not null,
	TECHPACKNAME varchar(255) not null,
	TECHPACKVERSION varchar(128) not null
);

 alter table InterfaceDependency
       add primary key (INTERFACEVERSION, INTERFACENAME, TECHPACKNAME, TECHPACKVERSION);


--/***************************
--  InterfaceMeasurement
--/***************************
create table InterfaceMeasurement (
	TAGID varchar(50) not null,
	DATAFORMATID varchar(100) not null,
	INTERFACENAME varchar(50) not null,
	TRANSFORMERID varchar(255) ,
	STATUS numeric(9) not null,
	MODIFTIME timestamp ,
	DESCRIPTION varchar(32000) ,
	TECHPACKVERSION varchar(32) not null,
	INTERFACEVERSION varchar(32) not null
);

 alter table InterfaceMeasurement
       add primary key (TAGID, INTERFACENAME, INTERFACEVERSION);


--/***************************
--  InterfaceTechpacks
--/***************************
create table InterfaceTechpacks (
	INTERFACENAME varchar(50) not null,
	TECHPACKNAME varchar(30) not null,
	TECHPACKVERSION varchar(32) not null,
	INTERFACEVERSION varchar(32) not null
);

 alter table InterfaceTechpacks
       add primary key (INTERFACENAME, TECHPACKNAME, TECHPACKVERSION, INTERFACEVERSION);


--/***************************
--  MeasurementColumn
--/***************************
create table MeasurementColumn (
	MTABLEID varchar(255) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9) ,
	DATATYPE varchar(50) ,
	DATASIZE integer ,
	DATASCALE integer ,
	UNIQUEVALUE numeric(9) ,
	NULLABLE integer ,
	INDEXES varchar(20) ,
	DESCRIPTION varchar(32000) ,
	DATAID varchar(255) ,
	RELEASEID varchar(50) ,
	UNIQUEKEY integer ,
	INCLUDESQL integer ,
	COLTYPE varchar(16) ,
	FOLLOWJOHN integer
);

 alter table MeasurementColumn
       add primary key (MTABLEID, DATANAME);


--/***************************
--  MeasurementCounter
--/***************************
create table MeasurementCounter (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	DESCRIPTION varchar(32000) ,
	TIMEAGGREGATION varchar(50) ,
	GROUPAGGREGATION varchar(50) ,
	COUNTAGGREGATION varchar(50) ,
	COLNUMBER numeric(9) ,
	DATATYPE varchar(50) ,
	DATASIZE integer ,
	DATASCALE integer ,
	INCLUDESQL integer ,
	UNIVOBJECT varchar(128) ,
	UNIVCLASS varchar(35) ,
	COUNTERTYPE varchar(16) ,
	COUNTERPROCESS varchar(16) ,
	DATAID varchar(255) ,
	FOLLOWJOHN integer 
);

 alter table MeasurementCounter
       add primary key (TYPEID, DATANAME);


--/***************************
--  MeasurementDeltaCalcSupport
--/***************************
create table MeasurementDeltaCalcSupport (
	TYPEID varchar(255) not null,
	VENDORRELEASE varchar(16) not null,
	DELTACALCSUPPORT integer not null,
	VERSIONID varchar(128) not null
);

 alter table MeasurementDeltaCalcSupport
       add primary key (TYPEID, VENDORRELEASE, VERSIONID);


--/***************************
--  MeasurementKey
--/***************************
create table MeasurementKey (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	DESCRIPTION varchar(32000) ,
	ISELEMENT integer ,
	UNIQUEKEY integer ,
	COLNUMBER numeric(9) ,
	DATATYPE varchar(50) ,
	DATASIZE integer ,
	DATASCALE integer ,
	UNIQUEVALUE numeric(9) ,
	NULLABLE integer ,
	INDEXES varchar(20) ,
	INCLUDESQL integer ,
	UNIVOBJECT varchar(128) ,
	JOINABLE integer ,
	DATAID varchar(255) 
);

 alter table MeasurementKey
       add primary key (TYPEID, DATANAME);


--/***************************
--  MeasurementObjBHSupport
--/***************************
create table MeasurementObjBHSupport (
	TYPEID varchar(255) not null,
	OBJBHSUPPORT varchar(32) not null
);

 alter table MeasurementObjBHSupport
       add primary key (OBJBHSUPPORT, TYPEID);


--/***************************
--  MeasurementTable
--/***************************
create table MeasurementTable (
	MTABLEID varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	TYPEID varchar(255) ,
	BASETABLENAME varchar(255) ,
	DEFAULT_TEMPLATE varchar(50) ,
	PARTITIONPLAN varchar(128) 
);

 alter table MeasurementTable
       add primary key (MTABLEID);


--/***************************
--  MeasurementType
--/***************************
create table MeasurementType (
	TYPEID varchar(255) not null,
	TYPECLASSID varchar(50) not null,
	TYPENAME varchar(255) not null,
	VENDORID varchar(50) ,
	FOLDERNAME varchar(50) ,
	DESCRIPTION varchar(32000) ,
	STATUS numeric(9) ,
	VERSIONID varchar(128) not null,
	OBJECTID varchar(255) not null,
	OBJECTNAME varchar(255) ,
	OBJECTVERSION integer ,
	OBJECTTYPE varchar(255) ,
	JOINABLE varchar(255) ,
	SIZING varchar(16) ,
	TOTALAGG integer ,
	ELEMENTBHSUPPORT integer ,
	RANKINGTABLE integer ,
	DELTACALCSUPPORT integer ,
	PLAINTABLE integer ,
	UNIVERSEEXTENSION varchar(4) ,
	VECTORSUPPORT integer ,
	DATAFORMATSUPPORT integer ,
	FOLLOWJOHN integer ,
	ONEMINAGG integer ,
	FIFTEENMINAGG integer ,
	EVENTSCALCTABLE integer ,
	MIXEDPARTITIONSTABLE integer ,
	LOADFILE_DUP_CHECK integer	
	
);

 alter table MeasurementType
       add primary key (TYPEID);


--/***************************
--  MeasurementTypeClass
--/***************************
create table MeasurementTypeClass (
	TYPECLASSID varchar(50) not null,
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(32000) 
);

 alter table MeasurementTypeClass
       add primary key (TYPECLASSID);


--/***************************
--  MeasurementVector
--/***************************
create table MeasurementVector (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	VENDORRELEASE varchar(16) not null,
	VINDEX numeric(31) not null,
	VFROM varchar(255) not null,
	VTO varchar(255) not null,
	MEASURE varchar(255) not null,
	QUANTITY varchar(50) null
);

 alter table MeasurementVector
       add primary key (TYPEID, DATANAME, VENDORRELEASE, VINDEX);


--/***************************
--  PartitionPlan
--/***************************
create table PartitionPlan (
	PARTITIONPLAN varchar(128) not null,
	DEFAULTSTORAGETIME numeric(9) not null,
	DEFAULTPARTITIONSIZE numeric(9) not null,
	MAXSTORAGETIME numeric(9) 
);

 alter table PartitionPlan
       add primary key (PARTITIONPLAN);


--/***************************
--  Prompt
--/***************************
create table Prompt (
	VERSIONID varchar(128) not null,
	PROMPTIMPLEMENTORID integer not null,
	PROMPTNAME varchar(255) not null,
	ORDERNUMBER integer ,
	UNREFRESHABLE varchar(32) 
);

 alter table Prompt
       add primary key (VERSIONID, PROMPTIMPLEMENTORID, PROMPTNAME);


--/***************************
--  PromptImplementor
--/***************************
create table PromptImplementor (
	VERSIONID varchar(128) not null,
	PROMPTIMPLEMENTORID integer not null,
	PROMPTCLASSNAME varchar(255) ,
	PRIORITY integer not null
);

 alter table PromptImplementor
       add primary key (VERSIONID, PROMPTIMPLEMENTORID);


--/***************************
--  PromptOption
--/***************************
create table PromptOption (
	VERSIONID varchar(128) not null,
	PROMPTIMPLEMENTORID integer not null,
	OPTIONNAME varchar(255) not null,
	OPTIONVALUE varchar(255) not null
);

 alter table PromptOption
       add primary key (VERSIONID, PROMPTIMPLEMENTORID, OPTIONNAME);


--/***************************
--  ReferenceColumn
--/***************************
create table ReferenceColumn (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9) ,
	DATATYPE varchar(50) ,
	DATASIZE integer ,
	DATASCALE integer ,
	UNIQUEVALUE numeric(9) ,
	NULLABLE integer ,
	INDEXES varchar(20) ,
	UNIQUEKEY integer ,
	INCLUDESQL integer ,
	INCLUDEUPD integer ,
	COLTYPE varchar(16) ,
	DESCRIPTION varchar(32000) ,
	UNIVERSECLASS varchar(35) ,
	UNIVERSEOBJECT varchar(128) ,
	UNIVERSECONDITION varchar(255) ,
	DATAID varchar(255),
    BASEDEF integer default 0 not null	 
);

 alter table ReferenceColumn
       add primary key (TYPEID, DATANAME);


--/***************************
--  ReferenceTable
--/***************************
create table ReferenceTable (
	TYPEID varchar(255) not null,
	VERSIONID varchar(128) not null,
	TYPENAME varchar(255) ,
	OBJECTID varchar(255) ,
	OBJECTNAME varchar(255) ,
	OBJECTVERSION varchar(50) ,
	OBJECTTYPE varchar(255) ,
	DESCRIPTION varchar(32000) ,
	STATUS numeric(9) ,
	UPDATE_POLICY numeric(9) ,
	TABLE_TYPE varchar(12) ,
	DATAFORMATSUPPORT integer,
    BASEDEF integer default 0 not null 
);

 alter table ReferenceTable
       add primary key (TYPEID);


--/***************************
--  SupportedVendorRelease
--/***************************
create table SupportedVendorRelease (
	VERSIONID varchar(128) not null,
	VENDORRELEASE varchar(16) not null
);

 alter table SupportedVendorRelease
       add primary key (VERSIONID, VENDORRELEASE);


--/***************************
--  TechPackDependency
--/***************************
create table TechPackDependency (
	VERSIONID varchar(128) not null,
	TECHPACKNAME varchar(30) not null,
	VERSION varchar(32) not null
);

 alter table TechPackDependency
       add primary key (VERSIONID, TECHPACKNAME);


--/***************************
--  TPActivation
--/***************************
create table TPActivation (
	TECHPACK_NAME varchar(30) not null,
	STATUS varchar(10) not null,
	VERSIONID varchar(128) ,
	TYPE varchar(10) not null,
    MODIFIED integer default 0 not null
);

 alter table TPActivation
       add primary key (TECHPACK_NAME);


--/***************************
--  Transformation
--/***************************
create table Transformation (
	TRANSFORMERID varchar(255) not null,
	ORDERNO numeric(9) not null,
	TYPE varchar(128) not null,
	SOURCE varchar(128) ,
	TARGET varchar(128) ,
	CONFIG varchar(32000) ,
	DESCRIPTION varchar(32000) 
);

 alter table Transformation
       add primary key (TRANSFORMERID, ORDERNO);


--/***************************
--  Transformer
--/***************************
create table Transformer (
	TRANSFORMERID varchar(255) not null,
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(32000) ,
	TYPE varchar(50) default 'SPECIFIC' not null
);

 alter table Transformer
       add primary key (TRANSFORMERID);


--/***************************
--  TypeActivation
--/***************************
create table TypeActivation (
	TECHPACK_NAME varchar(30) not null,
	STATUS varchar(10) not null,
	TYPENAME varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	STORAGETIME numeric(9) ,
	TYPE varchar(12) not null,
	PARTITIONPLAN varchar(128) 
);

 alter table TypeActivation
       add primary key (TECHPACK_NAME, TYPENAME, TABLELEVEL);


--/***************************
--  UniverseClass
--/***************************
create table UniverseClass (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(35) not null,
	UNIVERSEEXTENSION varchar(4) not null,
	DESCRIPTION varchar(32000) ,
	PARENT varchar(35) ,
	OBJ_BH_REL integer ,
	ELEM_BH_REL integer ,
	INHERITANCE integer 
);

 alter table UniverseClass
       add primary key (VERSIONID, UNIVERSEEXTENSION, CLASSNAME);


--/***************************
--  UniverseComputedObject
--/***************************
create table UniverseComputedObject (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(35) not null,
	UNIVERSEEXTENSION varchar(4) not null,
	OBJECTNAME varchar(35) not null,
	DESCRIPTION varchar(32000) ,
	OBJECTTYPE varchar(16) ,
	QUALIFICATION varchar(16) ,
	AGGREGATION varchar(16) ,
	OBJSELECT varchar(32000) ,
	OBJWHERE varchar(32000) ,
	PROMPTHIERARCHY varchar(255) ,
	OBJ_BH_REL integer ,
	ELEM_BH_REL integer ,
	INHERITANCE integer 
);

 alter table UniverseComputedObject
       add primary key (VERSIONID, CLASSNAME, UNIVERSEEXTENSION, OBJECTNAME);


--/***************************
--  UniverseCondition
--/***************************
create table UniverseCondition (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(35) not null,
	UNIVERSEEXTENSION varchar(4) not null,
	UNIVERSECONDITION varchar(35) not null,
	DESCRIPTION varchar(32000) ,
	CONDWHERE varchar(32000) ,
	AUTOGENERATE integer ,
	CONDOBJCLASS varchar(35) ,
	CONDOBJECT varchar(35) ,
	PROMPTTEXT varchar(255) ,
	MULTISELECTION integer ,
	FREETEXT integer ,
	OBJ_BH_REL integer ,
	ELEM_BH_REL integer ,
	INHERITANCE integer 
);

 alter table UniverseCondition
       add primary key (VERSIONID, UNIVERSEEXTENSION, CLASSNAME, UNIVERSECONDITION);


--/***************************
--  UniverseFormulas
--/***************************
create table UniverseFormulas (
	VERSIONID varchar(128) not null,
	TECHPACK_TYPE varchar(32) ,
	NAME varchar(255) not null,
	FORMULA varchar(3200) ,
	OBJECTTYPE varchar(16) ,
	QUALIFICATION varchar(16) ,
	AGGREGATION varchar(16)
);

 alter table UniverseFormulas
       add primary key (VERSIONID, NAME);


--/***************************
--  UniverseJoin
--/***************************
create table UniverseJoin (
	VERSIONID varchar(128) not null,
	SOURCETABLE varchar(32000) not null,
	SOURCELEVEL varchar(255) not null,
	SOURCECOLUMN varchar(255) not null,
	TARGETTABLE varchar(32000) not null,
	TARGETLEVEL varchar(255) not null,
	TARGETCOLUMN varchar(255) not null,
	EXPRESSION varchar(255) not null,
	CARDINALITY varchar(255) ,
	CONTEXT varchar(32000) ,
	EXCLUDEDCONTEXTS varchar(32000) ,
	-- This should be defined as autoincrement. Tmp fix is to set it to default 0.
	TMPCOUNTER integer default 0 null
	--TMPCOUNTER integer
);
-- eeoidiv, 20110203, Commented out because TMPCOUNTER autoincrement doesnt work for HSQL dB.  Would force TMPCOUNTER to be not null. 
-- alter table UniverseJoin
--       add primary key (TMPCOUNTER, VERSIONID, SOURCETABLE, SOURCECOLUMN, TARGETTABLE, TARGETCOLUMN);


--/***************************
--  UniverseName
--/***************************
create table UniverseName (
	VERSIONID varchar(128) not null,
	UNIVERSENAME varchar(30) not null,
	UNIVERSEEXTENSION varchar(16) not null
);

 alter table UniverseName
       add primary key (UNIVERSEEXTENSION, VERSIONID);


--/***************************
--  UniverseObject
--/***************************
create table UniverseObject (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(35) not null,
	UNIVERSEEXTENSION varchar(4) not null,
	OBJECTNAME varchar(35) not null,
	DESCRIPTION varchar(32000) ,
	OBJECTTYPE varchar(16) ,
	QUALIFICATION varchar(16) ,
	AGGREGATION varchar(16) ,
	OBJSELECT varchar(32000) ,
	OBJWHERE varchar(32000) ,
	PROMPTHIERARCHY varchar(255) ,
	OBJ_BH_REL integer ,
	ELEM_BH_REL integer ,
	INHERITANCE integer 
);

 alter table UniverseObject
       add primary key (VERSIONID, UNIVERSEEXTENSION, CLASSNAME, OBJECTNAME);


--/***************************
--  UniverseParameters
--/***************************
create table UniverseParameters (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(35) not null,
	OBJECTNAME varchar(35) not null,
	UNIVERSEEXTENSION varchar(4) not null,
	ORDERNRO integer not null,
	NAME varchar(255),
	TYPENAME varchar(255) 
);

 alter table UniverseParameters
       add primary key (CLASSNAME, VERSIONID, OBJECTNAME, UNIVERSEEXTENSION, ORDERNRO);


--/***************************
--  UniverseTable
--/***************************
create table UniverseTable (
	VERSIONID varchar(128) not null,
	TABLENAME varchar(255) not null,
	UNIVERSEEXTENSION varchar(4) not null,
	OWNER varchar(255) not null,
	ALIAS varchar(255) ,
	OBJ_BH_REL integer ,
	ELEM_BH_REL integer ,
	INHERITANCE integer
);

 alter table UniverseTable
       add primary key (TABLENAME, VERSIONID, UNIVERSEEXTENSION);


--/***************************
--  UserAccount
--/***************************
create table UserAccount (
	NAME varchar(255) not null,
	PASSWORD varchar(16) not null,
	ROLE varchar(16) not null,
	LASTLOGIN timestamp 
);

 alter table UserAccount
       add primary key (NAME);


--/***************************
--  VerificationCondition
--/***************************
create table VerificationCondition (
	VERSIONID varchar(128) not null,
	FACTTABLE varchar(2560) not null,
	VERLEVEL varchar(32) not null,
	CONDITIONCLASS varchar(32) not null,
	VERCONDITION varchar(128) not null,
	PROMPTNAME1 varchar(255) ,
	PROMPTVALUE1 varchar(128) ,
	PROMPTNAME2 varchar(255) ,
	PROMPTVALUE2 varchar(128) ,
	OBJECTCONDITION varchar(255) ,
	PROMPTNAME3 varchar(255) ,
	PROMPTVALUE3 varchar(128) 
);

 alter table VerificationCondition
       add primary key (VERSIONID, VERLEVEL, VERCONDITION, CONDITIONCLASS);


--/***************************
--  VerificationObject
--/***************************
create table VerificationObject (
	VERSIONID varchar(128) not null,
	MEASTYPE varchar(128) not null,
	MEASLEVEL varchar(32) not null,
	OBJECTCLASS varchar(32) not null,
	OBJECTNAME varchar(32) not null
);

 alter table VerificationObject
       add primary key (VERSIONID, MEASLEVEL, OBJECTCLASS, OBJECTNAME);


     
--/***************************
--  Versioning
--/***************************
create table Versioning (
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(50) ,
	STATUS numeric(9) ,
	TECHPACK_NAME varchar(30) not null,
	TECHPACK_VERSION varchar(32) ,
	TECHPACK_TYPE varchar(10) ,
	PRODUCT_NUMBER varchar(255) ,
	LOCKEDBY varchar(255) ,
	LOCKDATE timestamp ,
	BASEDEFINITION varchar(128) ,
	BASEVERSION varchar(16) ,
	INSTALLDESCRIPTION varchar(32000) ,
	UNIVERSENAME varchar(30) ,
	UNIVERSEEXTENSION varchar(16) ,
	ENIQ_LEVEL varchar(12) default '1.0' not null,
	LICENSENAME varchar(255) 
);

 alter table Versioning
       add primary key (VERSIONID);

--/***************************
--  GroupTypes
--/***************************
create table Grouptypes	(
	  GROUPTYPE varchar(64) NOT NULL,
	  VERSIONID varchar(128) NOT NULL,
	  DATANAME varchar(128) NOT NULL,
	  DATATYPE varchar(50) NOT NULL,
	  DATASIZE integer,
	  DATASCALE integer DEFAULT 0,
	  NULLABLE smallint DEFAULT 0
);

 alter table Grouptypes
	  add primary key (GROUPTYPE, DATANAME, VERSIONID);
	   
-- Changes in repository 19.11.
alter table UniverseClass add ORDERNRO numeric null;
alter table UniverseComputedObject add ORDERNRO numeric null;
alter table UniverseCondition add ORDERNRO numeric null;
alter table UniverseFormulas add ORDERNRO numeric null;
alter table UniverseJoin add ORDERNRO numeric null;
alter table UniverseName add ORDERNRO numeric null;
alter table UniverseObject add ORDERNRO numeric null;
alter table UniverseTable add ORDERNRO numeric null;

-- #652 problem with the names of the Universes when you have splitted universe, 2009-06-08 eeoidiv
alter table UniverseName add UNIVERSEEXTENSIONNAME varchar(35) null;