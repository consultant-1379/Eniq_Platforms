--START:dwhrep
CREATE TABLE Aggregation (
	AGGREGATION varchar(255) not null,
	VERSIONID varchar(128) not null,
	AGGREGATIONSET varchar(100) null,
	AGGREGATIONGROUP varchar(100) null,
	REAGGREGATIONSET varchar(100) null,
	REAGGREGATIONGROUP varchar(100) null,
	GROUPORDER int null,
	AGGREGATIONORDER int null,
	AGGREGATIONTYPE varchar(50) null,
	AGGREGATIONSCOPE varchar(50) null
);
CREATE TABLE AggregationRule (
	AGGREGATION varchar(255) not null,
	VERSIONID varchar(128) not null,
	RULEID int not null,
	TARGET_TYPE varchar(50) null,
	TARGET_LEVEL varchar(50) null,
	TARGET_TABLE varchar(255) null,
	TARGET_MTABLEID varchar(255) null,
	SOURCE_TYPE varchar(50) null,
	SOURCE_LEVEL varchar(50) null,
	SOURCE_TABLE varchar(255) null,
	SOURCE_MTABLEID varchar(255) null,
	RULETYPE varchar(50) not null,
	AGGREGATIONSCOPE varchar(50) null,
	BHTYPE varchar(50) null,
	ENABLE int null
);
CREATE TABLE AlarmInterface (
	INTERFACEID varchar(50) not null,
	DESCRIPTION varchar(200) not null,
	STATUS varchar(20) not null,
	COLLECTION_SET_ID numeric(38, 0) not null,
	COLLECTION_ID numeric(38, 0) not null,
	QUEUE_NUMBER numeric(38, 0) not null
);
CREATE TABLE AlarmReport (
	INTERFACEID varchar(50) not null,
	REPORTID varchar(255) not null,
	REPORTNAME varchar(255) not null,
	URL varchar(32000) not null,
	STATUS varchar(10) not null,
	SIMULTANEOUS int null
);
CREATE TABLE AlarmReportParameter (
	REPORTID varchar(255) not null,
	NAME varchar(255) not null,
	VALUE varchar(255) not null
);
CREATE TABLE Busyhour (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(255) not null,
	BHTYPE varchar(32) not null,
	BHCRITERIA varchar(32000) null,
	WHERECLAUSE varchar(32000) null,
	DESCRIPTION varchar(32000) null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null,
	BHELEMENT int not null,
	ENABLE int not null,
	AGGREGATIONTYPE varchar(128) not null,
	OFFSET int not null,
	WINDOWSIZE int not null,
	LOOKBACK int not null,
	P_THRESHOLD int not null,
	N_THRESHOLD int not null,
	CLAUSE varchar(32000) not null,
	PLACEHOLDERTYPE varchar(128) null,
	GROUPING varchar(32) not null,
	REACTIVATEVIEWS int not null
);
CREATE TABLE BusyhourMapping (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(255) not null,
	BHTYPE varchar(32) not null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null,
	TYPEID varchar(255) not null,
	BHTARGETTYPE varchar(128) not null,
	BHTARGETLEVEL varchar(128) not null,
	ENABLE int not null
);
CREATE TABLE BusyhourPlaceholders (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(255) not null,
	PRODUCTPLACEHOLDERS int null,
	CUSTOMPLACEHOLDERS int null
);
CREATE TABLE BusyhourRankkeys (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(255) not null,
	BHTYPE varchar(32) not null,
	KEYNAME varchar(128) not null,
	KEYVALUE varchar(128) not null,
	ORDERNBR numeric(9, 0) not null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null
);
CREATE TABLE BusyhourSource (
	VERSIONID varchar(128) not null,
	BHLEVEL varchar(255) not null,
	BHTYPE varchar(32) not null,
	TYPENAME varchar(255) not null,
	TARGETVERSIONID varchar(128) not null,
	BHOBJECT varchar(32) not null
);
CREATE TABLE Configuration (
	PARAMNAME varchar(255) not null,
	PARAMVALUE varchar(32000) not null
);
CREATE TABLE CountingInterval (
	INTERVAL int not null,
	INTERVALNAME varchar(16) not null,
	INTERVALDESCRIPTION varchar(255) not null
);
CREATE TABLE CountingManagement (
	STORAGEID varchar(255) not null,
	TABLENAME varchar(255) not null,
	LASTAGGREGATEDROW bigint not null
);
CREATE TABLE DataFormat (
	DATAFORMATID varchar(100) not null,
	TYPEID varchar(255) not null,
	VERSIONID varchar(128) not null,
	OBJECTTYPE varchar(255) not null,
	FOLDERNAME varchar(128) not null,
	DATAFORMATTYPE varchar(50) not null
);
CREATE TABLE DataInterface (
	INTERFACENAME varchar(255) not null,
	STATUS numeric(9, 0) not null,
	INTERFACETYPE varchar(50) not null,
	DESCRIPTION varchar(32000) null,
	DATAFORMATTYPE varchar(50) null,
	INTERFACEVERSION varchar(32) not null,
	LOCKEDBY varchar(255) null,
	LOCKDATE timestamp null,
	PRODUCTNUMBER varchar(255) null,
	ENIQ_LEVEL varchar(12) null,
	RSTATE varchar(255) null,
	INSTALLDESCRIPTION varchar(32000) null
);
CREATE TABLE DataItem (
	DATAFORMATID varchar(100) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9, 0) not null,
	DATAID varchar(255) not null,
	PROCESS_INSTRUCTION varchar(128) null,
	DATATYPE varchar(50) null,
	DATASIZE int null,
	DATASCALE int null
);
CREATE TABLE DefaultTags (
	TAGID varchar(128) not null,
	DATAFORMATID varchar(100) not null,
	DESCRIPTION varchar(200) null
);
CREATE TABLE DWHColumn (
	STORAGEID varchar(255) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9, 0) not null,
	DATATYPE varchar(50) not null,
	DATASIZE int not null,
	DATASCALE int not null,
	UNIQUEVALUE numeric(9, 0) not null,
	NULLABLE int not null,
	INDEXES varchar(20) not null,
	UNIQUEKEY int not null,
	STATUS varchar(10) null,
	INCLUDESQL int null
);
CREATE TABLE DWHPartition (
	STORAGEID varchar(255) not null,
	TABLENAME varchar(255) not null,
	STARTTIME timestamp null,
	ENDTIME timestamp null,
	STATUS varchar(10) not null,
	LOADORDER int null
);
CREATE TABLE DWHTechPacks (
	TECHPACK_NAME varchar(30) not null,
	VERSIONID varchar(128) not null,
	CREATIONDATE timestamp null
);
CREATE TABLE DWHType (
	TECHPACK_NAME varchar(30) not null,
	TYPENAME varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	STORAGEID varchar(255) not null,
	PARTITIONSIZE numeric(9, 0) not null,
	PARTITIONCOUNT numeric(9, 0) null,
	STATUS varchar(50) not null,
	TYPE varchar(50) not null,
	OWNER varchar(50) null,
	VIEWTEMPLATE varchar(255) not null,
	CREATETEMPLATE varchar(255) not null,
	NEXTPARTITIONTIME timestamp null,
	BASETABLENAME varchar(125) not null,
	DATADATECOLUMN varchar(128) null,
	PUBLICVIEWTEMPLATE varchar(255) null,
	PARTITIONPLAN varchar(128) null
);
CREATE TABLE ENIQ_EVENTS_ADMIN_PROPERTIES (
	PARAM_NAME varchar(100) not null,
	PARAM_VALUE varchar(1000) not null,
	DATE_MODIFIED timestamp not null,
	MODIFIED_BY varchar(100) not null
);
CREATE TABLE ExternalStatement (
	VERSIONID varchar(128) not null,
	STATEMENTNAME varchar(255) not null,
	EXECUTIONORDER numeric(9, 0) not null,
	DBCONNECTION varchar(20) not null,
	STATEMENT varchar(32000) null,
	BASEDEF int not null
);
CREATE TABLE ExternalStatementStatus (
	TECHPACK_NAME varchar(30) not null,
	STATEMENTNAME varchar(255) not null,
	VERSIONID varchar(128) not null,
	STATUS varchar(10) not null,
	EXECTIME timestamp null,
	EXECSTATEMENT varchar(32000) null
);
CREATE TABLE GroupTypes (
	GROUPTYPE varchar(64) not null,
	VERSIONID varchar(128) not null,
	DATANAME varchar(128) not null,
	DATATYPE varchar(50) not null,
	DATASIZE int not null,
	DATASCALE int not null,
	NULLABLE smallint not null
);
CREATE TABLE InfoMessages (
	MSGID int not null,
	TITLE varchar(50) not null,
	MSGDATE timestamp not null,
	NAME varchar(50) not null,
	EMAIL varchar(50) not null,
	STATUS varchar(20) not null,
	MSG varchar(500) null
);
CREATE TABLE InterfaceDependency (
	INTERFACEVERSION varchar(32) not null,
	INTERFACENAME varchar(255) not null,
	TECHPACKNAME varchar(255) not null,
	TECHPACKVERSION varchar(128) not null
);
CREATE TABLE InterfaceMeasurement (
	TAGID varchar(128) not null,
	DATAFORMATID varchar(100) not null,
	INTERFACENAME varchar(255) not null,
	TRANSFORMERID varchar(255) null,
	STATUS numeric(9, 0) not null,
	MODIFTIME timestamp null,
	DESCRIPTION varchar(32000) null,
	TECHPACKVERSION varchar(32) not null,
	INTERFACEVERSION varchar(32) not null
);
CREATE TABLE InterfaceTechpacks (
	INTERFACENAME varchar(255) not null,
	TECHPACKNAME varchar(30) not null,
	TECHPACKVERSION varchar(32) not null,
	INTERFACEVERSION varchar(32) not null
);
CREATE TABLE MeasurementColumn (
	MTABLEID varchar(255) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9, 0) null,
	DATATYPE varchar(50) null,
	DATASIZE int null,
	DATASCALE int null,
	UNIQUEVALUE numeric(9, 0) null,
	NULLABLE int null,
	INDEXES varchar(20) null,
	DESCRIPTION varchar(32000) null,
	DATAID varchar(255) null,
	RELEASEID varchar(50) null,
	UNIQUEKEY int null,
	INCLUDESQL int null,
	COLTYPE varchar(16) null,
	FOLLOWJOHN int null
);
CREATE TABLE MeasurementCounter (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	DESCRIPTION varchar(32000) null,
	TIMEAGGREGATION varchar(50) null,
	GROUPAGGREGATION varchar(50) null,
	COUNTAGGREGATION varchar(32000) null,
	COLNUMBER numeric(9, 0) null,
	DATATYPE varchar(50) null,
	DATASIZE int null,
	DATASCALE int null,
	INCLUDESQL int null,
	UNIVOBJECT varchar(128) null,
	UNIVCLASS varchar(35) null,
	COUNTERTYPE varchar(16) null,
	COUNTERPROCESS varchar(16) null,
	DATAID varchar(255) null,
	FOLLOWJOHN int null
);
CREATE TABLE MeasurementDeltaCalcSupport (
	TYPEID varchar(255) not null,
	VENDORRELEASE varchar(16) not null,
	DELTACALCSUPPORT int not null,
	VERSIONID varchar(128) not null
);
CREATE TABLE MeasurementKey (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	DESCRIPTION varchar(32000) null,
	ISELEMENT int null,
	UNIQUEKEY int null,
	COLNUMBER numeric(9, 0) null,
	DATATYPE varchar(50) null,
	DATASIZE int null,
	DATASCALE int null,
	UNIQUEVALUE numeric(9, 0) null,
	NULLABLE int null,
	INDEXES varchar(20) null,
	INCLUDESQL int null,
	UNIVOBJECT varchar(128) null,
	JOINABLE int null,
	DATAID varchar(255) null,
	ROPGRPCELL INT null
);
CREATE TABLE MeasurementObjBHSupport (
	TYPEID varchar(255) not null,
	OBJBHSUPPORT varchar(32) not null
);
CREATE TABLE MeasurementTable (
	MTABLEID varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	TYPEID varchar(255) null,
	BASETABLENAME varchar(255) null,
	DEFAULT_TEMPLATE varchar(50) null,
	PARTITIONPLAN varchar(128) null
);
CREATE TABLE MeasurementType (
	TYPEID varchar(255) not null,
	TYPECLASSID varchar(255) not null,
	TYPENAME varchar(255) not null,
	VENDORID varchar(128) null,
	FOLDERNAME varchar(128) null,
	DESCRIPTION varchar(32000) null,
	STATUS numeric(9, 0) null,
	VERSIONID varchar(128) not null,
	OBJECTID varchar(255) not null,
	OBJECTNAME varchar(255) null,
	OBJECTVERSION int null,
	OBJECTTYPE varchar(255) null,
	JOINABLE varchar(255) null,
	SIZING varchar(16) null,
	TOTALAGG int null,
	ELEMENTBHSUPPORT int null,
	RANKINGTABLE int null,
	DELTACALCSUPPORT int null,
	PLAINTABLE int null,
	UNIVERSEEXTENSION varchar(12) null,
	VECTORSUPPORT int null,
	DATAFORMATSUPPORT int null,
	FOLLOWJOHN int null,
	ONEMINAGG int null,
	FIFTEENMINAGG int null,
	EVENTSCALCTABLE int null,
	MIXEDPARTITIONSTABLE int null,
	LOADFILE_DUP_CHECK int null,
	SONAGG int null,
	SONFIFTEENMINAGG int null,
	ROPGRPCELL varchar(255) null
);
CREATE TABLE MeasurementTypeClass (
	TYPECLASSID varchar(255) not null,
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(32000) null
);
CREATE TABLE MeasurementVector (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	VENDORRELEASE varchar(16) not null,
	VINDEX numeric(30, 6) not null,
	VFROM varchar(255) not null,
	VTO varchar(255) not null,
	MEASURE varchar(255) not null,
	QUANTITY numeric(30, 6) null
);
CREATE TABLE MZTechPacks (
	VERSIONID varchar(128) not null,
	TECHPACK_NAME varchar(30) not null,
	STATUS varchar(10) null,
	CREATIONDATE timestamp null,
	PRODUCT_NUMBER varchar(255) null,
	TYPE varchar(10) not null,
	TECHPACK_VERSION varchar(32) null
);
CREATE TABLE PartitionPlan (
	PARTITIONPLAN varchar(128) not null,
	DEFAULTSTORAGETIME numeric(20, 0) not null,
	DEFAULTPARTITIONSIZE numeric(20, 0) not null,
	MAXSTORAGETIME numeric(20, 0) null,
	PARTITIONTYPE tinyint not null
);
CREATE TABLE Prompt (
	VERSIONID varchar(128) not null,
	PROMPTIMPLEMENTORID int not null,
	PROMPTNAME varchar(255) not null,
	ORDERNUMBER int null,
	UNREFRESHABLE varchar(32) null
);
CREATE TABLE PromptImplementor (
	VERSIONID varchar(128) not null,
	PROMPTIMPLEMENTORID int not null,
	PROMPTCLASSNAME varchar(255) null,
	PRIORITY int not null
);
CREATE TABLE PromptOption (
	VERSIONID varchar(128) not null,
	PROMPTIMPLEMENTORID int not null,
	OPTIONNAME varchar(255) not null,
	OPTIONVALUE varchar(255) not null
);
CREATE TABLE ReferenceColumn (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	COLNUMBER numeric(9, 0) null,
	DATATYPE varchar(50) null,
	DATASIZE int null,
	DATASCALE int null,
	UNIQUEVALUE numeric(9, 0) null,
	NULLABLE int null,
	INDEXES varchar(20) null,
	UNIQUEKEY int null,
	INCLUDESQL int null,
	INCLUDEUPD int null,
	COLTYPE varchar(16) null,
	DESCRIPTION varchar(32000) null,
	UNIVERSECLASS varchar(35) null,
	UNIVERSEOBJECT varchar(128) null,
	UNIVERSECONDITION varchar(255) null,
	DATAID varchar(255) null,
	BASEDEF int not null
);
CREATE TABLE ReferenceTable (
	TYPEID varchar(255) not null,
	VERSIONID varchar(128) not null,
	TYPENAME varchar(255) null,
	OBJECTID varchar(255) null,
	OBJECTNAME varchar(255) null,
	OBJECTVERSION varchar(50) null,
	OBJECTTYPE varchar(255) null,
	DESCRIPTION varchar(32000) null,
	STATUS numeric(9, 0) null,
	UPDATE_POLICY numeric(9, 0) null,
	TABLE_TYPE varchar(12) null,
	DATAFORMATSUPPORT int null,
	BASEDEF int not null
);
CREATE TABLE SupportedVendorRelease (
	VERSIONID varchar(128) not null,
	VENDORRELEASE varchar(16) not null
);
CREATE TABLE TechPackDependency (
	VERSIONID varchar(128) not null,
	TECHPACKNAME varchar(30) not null,
	VERSION varchar(32) not null
);
CREATE TABLE TPActivation (
	TECHPACK_NAME varchar(30) not null,
	STATUS varchar(10) not null,
	VERSIONID varchar(128) null,
	TYPE varchar(10) not null,
	MODIFIED int not null
);
CREATE TABLE Transformation (
	TRANSFORMERID varchar(255) not null,
	ORDERNO numeric(9, 0) not null,
	TYPE varchar(128) not null,
	SOURCE varchar(128) null,
	TARGET varchar(128) null,
	CONFIG varchar(32000) null,
	DESCRIPTION varchar(32000) null
);
CREATE TABLE Transformer (
	TRANSFORMERID varchar(255) not null,
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(32000) null,
	TYPE varchar(50) not null
);
CREATE TABLE TypeActivation (
	TECHPACK_NAME varchar(30) not null,
	STATUS varchar(10) not null,
	TYPENAME varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	STORAGETIME numeric(15, 0) null,
	TYPE varchar(12) not null,
	PARTITIONPLAN varchar(128) null
);
CREATE TABLE UniverseClass (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(128) not null,
	UNIVERSEEXTENSION varchar(12) not null,
	DESCRIPTION varchar(32000) null,
	PARENT varchar(128) null,
	OBJ_BH_REL int null,
	ELEM_BH_REL int null,
	INHERITANCE int null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UniverseComputedObject (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(128) not null,
	UNIVERSEEXTENSION varchar(12) not null,
	OBJECTNAME varchar(128) not null,
	DESCRIPTION varchar(32000) null,
	OBJECTTYPE varchar(16) null,
	QUALIFICATION varchar(16) null,
	AGGREGATION varchar(16) null,
	OBJSELECT varchar(32000) null,
	OBJWHERE varchar(32000) null,
	PROMPTHIERARCHY varchar(255) null,
	OBJ_BH_REL int null,
	ELEM_BH_REL int null,
	INHERITANCE int null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UniverseCondition (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(128) not null,
	UNIVERSEEXTENSION varchar(12) not null,
	UNIVERSECONDITION varchar(128) not null,
	DESCRIPTION varchar(32000) null,
	CONDWHERE varchar(32000) null,
	AUTOGENERATE int null,
	CONDOBJCLASS varchar(128) null,
	CONDOBJECT varchar(128) null,
	PROMPTTEXT varchar(255) null,
	MULTISELECTION int null,
	FREETEXT int null,
	OBJ_BH_REL int null,
	ELEM_BH_REL int null,
	INHERITANCE int null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UniverseFormulas (
	VERSIONID varchar(128) not null,
	TECHPACK_TYPE varchar(32) null,
	NAME varchar(255) not null,
	FORMULA varchar(32000) null,
	OBJECTTYPE varchar(16) null,
	QUALIFICATION varchar(16) null,
	AGGREGATION varchar(16) null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UniverseJoin (
	VERSIONID varchar(128) not null,
	SOURCETABLE varchar(32000) not null,
	SOURCELEVEL varchar(255) null,
	SOURCECOLUMN varchar(255) not null,
	TARGETTABLE varchar(32000) not null,
	TARGETLEVEL varchar(255) null,
	TARGETCOLUMN varchar(255) not null,
	EXPRESSION varchar(255) null,
	CARDINALITY varchar(255) null,
	CONTEXT varchar(32000) null,
	EXCLUDEDCONTEXTS varchar(32000) null,
	TMPCOUNTER int not null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UniverseName (
	VERSIONID varchar(128) not null,
	UNIVERSENAME varchar(30) not null,
	UNIVERSEEXTENSION varchar(16) not null,
	ORDERNRO numeric(30, 6) null,
	UNIVERSEEXTENSIONNAME varchar(35) null
);
CREATE TABLE UniverseObject (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(128) not null,
	UNIVERSEEXTENSION varchar(12) not null,
	OBJECTNAME varchar(128) not null,
	DESCRIPTION varchar(32000) null,
	OBJECTTYPE varchar(16) null,
	QUALIFICATION varchar(16) null,
	AGGREGATION varchar(16) null,
	OBJSELECT varchar(32000) null,
	OBJWHERE varchar(32000) null,
	PROMPTHIERARCHY varchar(255) null,
	OBJ_BH_REL int null,
	ELEM_BH_REL int null,
	INHERITANCE int null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UniverseParameters (
	VERSIONID varchar(128) not null,
	CLASSNAME varchar(128) not null,
	OBJECTNAME varchar(128) not null,
	UNIVERSEEXTENSION varchar(12) not null,
	ORDERNRO int not null,
	NAME varchar(255) null,
	TYPENAME varchar(255) null
);
CREATE TABLE UniverseTable (
	VERSIONID varchar(128) not null,
	TABLENAME varchar(255) not null,
	UNIVERSEEXTENSION varchar(12) not null,
	OWNER varchar(255) not null,
	ALIAS varchar(255) null,
	OBJ_BH_REL int null,
	ELEM_BH_REL int null,
	INHERITANCE int null,
	ORDERNRO numeric(30, 6) null
);
CREATE TABLE UserAccount (
	NAME varchar(255) not null,
	PASSWORD varchar(16) not null,
	ROLE varchar(16) not null,
	LASTLOGIN timestamp null
);
CREATE TABLE VerificationCondition (
	VERSIONID varchar(128) not null,
	FACTTABLE varchar(2560) not null,
	VERLEVEL varchar(32) not null,
	CONDITIONCLASS varchar(32) not null,
	VERCONDITION varchar(128) not null,
	PROMPTNAME1 varchar(255) null,
	PROMPTVALUE1 varchar(128) null,
	PROMPTNAME2 varchar(255) null,
	PROMPTVALUE2 varchar(128) null,
	OBJECTCONDITION varchar(255) null,
	PROMPTNAME3 varchar(255) null,
	PROMPTVALUE3 varchar(128) null
);
CREATE TABLE VerificationObject (
	VERSIONID varchar(128) not null,
	MEASTYPE varchar(128) not null,
	MEASLEVEL varchar(32) not null,
	OBJECTCLASS varchar(32) not null,
	OBJECTNAME varchar(32) not null
);
CREATE TABLE Versioning (
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(50) null,
	STATUS numeric(9, 0) null,
	TECHPACK_NAME varchar(30) not null,
	TECHPACK_VERSION varchar(32) null,
	TECHPACK_TYPE varchar(10) null,
	PRODUCT_NUMBER varchar(255) null,
	LOCKEDBY varchar(255) null,
	LOCKDATE timestamp null,
	BASEDEFINITION varchar(128) null,
	BASEVERSION varchar(16) null,
	INSTALLDESCRIPTION varchar(32000) null,
	UNIVERSENAME varchar(30) null,
	UNIVERSEEXTENSION varchar(16) null,
	ENIQ_LEVEL varchar(12) not null,
	LICENSENAME varchar(255) null
);
--END:dwhrep

--START:etlrep
CREATE TABLE META_DATABASES (
	USERNAME varchar(30),
	VERSION_NUMBER varchar(32),
	TYPE_NAME varchar(15),
	CONNECTION_ID numeric(38, 0),
	CONNECTION_NAME varchar(30),
	CONNECTION_STRING varchar(400),
	PASSWORD varchar(30),
	DESCRIPTION varchar(32000),
	DRIVER_NAME varchar(100),
	DB_LINK_NAME varchar(128) null
);
--END:etlrep

--START:dc

--END:dc