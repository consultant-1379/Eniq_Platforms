--/***************************
--  TPActivation
--/***************************
create table TPActivation (
	TECHPACK_NAME varchar(30) not null,
	STATUS varchar(10) not null,
	VERSIONID varchar(128),
	TYPE varchar(10) not null,
    MODIFIED integer default 0 not null
);

alter table TPActivation
	add primary key (TECHPACK_NAME);
	
--/***************************
--  Versioning
--/***************************
create table Versioning (
	VERSIONID varchar(128) not null,
	DESCRIPTION varchar(50),
	STATUS numeric(9),
	TECHPACK_NAME varchar(30) not null,
	TECHPACK_VERSION varchar(32),
	TECHPACK_TYPE varchar(10),
	PRODUCT_NUMBER varchar(255),
	LOCKEDBY varchar(255),
	LOCKDATE timestamp,
	BASEDEFINITION varchar(128),
	BASEVERSION varchar(16),
	INSTALLDESCRIPTION varchar(32000),
	UNIVERSENAME varchar(30),
	UNIVERSEEXTENSION varchar(16),
	ENIQ_LEVEL varchar(12) default '1.0' not null,
	LICENSENAME varchar(255) 
);

alter table Versioning
	add primary key (VERSIONID);

--/***************************
--  MeasurementTypeClass
--/***************************
create table MeasurementTypeClass (
   TYPECLASSID          varchar(50)  not null,
   VERSIONID            varchar(128) not null,
   DESCRIPTION          varchar(32000)  null
);

alter table MeasurementTypeClass
   add primary key (TYPECLASSID);


--/***************************
--  MeasurementType
--/***************************
create table MeasurementType (
	TYPEID varchar(255) not null,
	TYPECLASSID varchar(50) not null,
	TYPENAME varchar(255) not null,
	VENDORID varchar(50),
	FOLDERNAME varchar(50),
	DESCRIPTION varchar(32000),
	STATUS numeric(9),
	VERSIONID varchar(128) not null,
	OBJECTID varchar(255) not null,
	OBJECTNAME varchar(255),
	OBJECTVERSION integer,
	OBJECTTYPE varchar(255),
	JOINABLE varchar(255),
	SIZING varchar(16),
	TOTALAGG integer,
	ELEMENTBHSUPPORT integer,
	RANKINGTABLE integer,
	DELTACALCSUPPORT integer,
	PLAINTABLE integer,
	UNIVERSEEXTENSION varchar(4),
	VECTORSUPPORT integer,
	DATAFORMATSUPPORT integer,
	FOLLOWJOHN integer
);

alter table MeasurementType
	add primary key (TYPEID);
       
--/***************************
--  MeasurementKey
--/***************************
create table MeasurementKey (
	TYPEID varchar(255) not null,
	DATANAME varchar(128) not null,
	DESCRIPTION varchar(32000),
	ISELEMENT integer,
	UNIQUEKEY integer,
	COLNUMBER numeric(9),
	DATATYPE varchar(50),
	DATASIZE integer,
	DATASCALE integer,
	UNIQUEVALUE numeric(9),
	NULLABLE integer,
	INDEXES varchar(20),
	INCLUDESQL integer,
	UNIVOBJECT varchar(128),
	JOINABLE integer,
	DATAID varchar(255) 
);

 alter table MeasurementKey
       add primary key (TYPEID, DATANAME);

--/***************************
--  MeasurementTable
--/***************************
create table MeasurementTable (
	MTABLEID varchar(255) not null,
	TABLELEVEL varchar(50) not null,
	TYPEID varchar(255),
	BASETABLENAME varchar(255),
	DEFAULT_TEMPLATE varchar(50),
	PARTITIONPLAN varchar(128) 
);

alter table MeasurementTable
	add primary key (MTABLEID);
	

--/***************************
--  MeasurementColumn
--/***************************
create table MeasurementColumn (
	MTABLEID VARCHAR(255),
	DATANAME VARCHAR(128),
	COLNUMBER BIGINT,
	DATATYPE VARCHAR(50),
	DATASIZE INTEGER,
	DATASCALE INTEGER,
	UNIQUEVALUE BIGINT,
	NULLABLE INTEGER,
	INDEXES VARCHAR(20),
	DESCRIPTION VARCHAR(32000),
	DATAID VARCHAR(255),
	RELEASEID VARCHAR(50),
	UNIQUEKEY INTEGER,
	INCLUDESQL INTEGER,
	COLTYPE VARCHAR(16),
	FOLLOWJOHN INTEGER
);

alter table MeasurementColumn
	add primary key (MTABLEID, DATANAME);
	
--/***************************
--  MeasurementCounter
--/***************************
create table MeasurementCounter (
	TYPEID VARCHAR(255),
	DATANAME VARCHAR(128),
	DESCRIPTION VARCHAR(32000),
	TIMEAGGREGATION VARCHAR(50),
	GROUPAGGREGATION VARCHAR(50),
	COUNTAGGREGATION VARCHAR(32000),
	COLNUMBER BIGINT,
	DATATYPE VARCHAR(50),
	DATASIZE INTEGER,
	DATASCALE INTEGER,
	INCLUDESQL INTEGER,
	UNIVOBJECT VARCHAR(128),
	UNIVCLASS VARCHAR(35),
	COUNTERTYPE VARCHAR(16),
	COUNTERPROCESS VARCHAR(16),
	DATAID VARCHAR(255),
	FOLLOWJOHN INTEGER
);

alter table MeasurementCounter
	add primary key (TYPEID, DATANAME);
	
--/***************************
--  DefaultTags
--/***************************
create table DefaultTags (
	TAGID varchar(50) not null, 
	DATAFORMATID varchar(100) not null, 
	DESCRIPTION varchar(200) null
);

alter table DefaultTags 
	add primary key (TAGID, DATAFORMATID);

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
--  DataFormat
--/***************************
create table DataFormat (
   DATAFORMATID    varchar(100) not null,
   TYPEID          varchar(255) not null,
   VERSIONID       varchar(128) not null,
   OBJECTTYPE      varchar(255) not null,
   FOLDERNAME      varchar(50)  not null,
   DATAFORMATTYPE  varchar(50)  not null
);

alter table DataFormat
   add primary key (DATAFORMATID);
   
--/***************************
--  DataItem
--/***************************
create table DataItem (
   DATAFORMATID  varchar(100) not null,
   DATANAME      varchar(128) not null,
   COLNUMBER     numeric(9)   not null,
   DATAID        varchar(255) not null,
   PROCESS_INSTRUCTION varchar(128) null,
   DATATYPE varchar(50) null,
   DATASIZE INTEGER,
   DATASCALE INTEGER
);

alter table DataItem
   add primary key (DATAFORMATID, DATANAME);

--/***************************
--  InterfaceMeasurement
--/***************************
create table InterfaceMeasurement (
   TAGID          varchar(50)  not null,
   DATAFORMATID   varchar(100) not null,
   INTERFACENAME  varchar(50)  not null,
   TRANSFORMERID  varchar(255) null,
   STATUS         numeric(9)   not null,
   MODIFTIME      datetime     null,
   DESCRIPTION    varchar(32000) null,
   TECHPACKVERSION varchar(32) not null,
   INTERFACEVERSION varchar(32) not null 
);

alter table InterfaceMeasurement
   add primary key (TAGID,INTERFACENAME,INTERFACEVERSION);
   
--/***************************
--  Transformer
--/***************************
create table Transformer (
   TRANSFORMERID varchar(255) not null,
   VERSIONID     varchar(128) not null,
   DESCRIPTION   varchar(32000) null,
   TYPE          varchar(50)   not null
);

alter table Transformer
   add primary key (TRANSFORMERID);
   
--/***************************
--  Transformation
--/***************************
create table Transformation (
   TRANSFORMERID varchar(255)   not null,
   ORDERNO       numeric(9)     not null,
   TYPE          varchar(128)   not null,
   SOURCE        varchar(128)   null,
   TARGET        varchar(128)   null,
   CONFIG        varchar(32000) null,
   DESCRIPTION   varchar(32000) null
);

alter table Transformation
   add primary key (TRANSFORMERID,ORDERNO);

--/***************************
--  Typeactivation
--/***************************
create table Typeactivation (
   TECHPACK_NAME	varchar(30)   not null,
   STATUS			varchar(10)   not null,
   TYPENAME			varchar(255)   not null,
   TABLELEVEL		varchar(50)   not null,
   STORAGETIME		integer,
   TYPE				varchar(12)   not null,
   PARTITIONPLAN	varchar(128)
);

alter table Typeactivation
   add primary key (TECHPACK_NAME, TYPENAME, TABLELEVEL);
   
--/***************************
--  DWHtype
--/***************************
create table DWHtype (
   TECHPACK_NAME		varchar(30)   not null,
   TYPENAME				varchar(255)   not null,
   TABLELEVEL			varchar(50)   not null,
   STORAGEID			varchar(255)   not null,
   PARTITIONSIZE		integer   not null,
   PARTITIONCOUNT		integer,
   STATUS				varchar(50)   not null,
   TYPE					varchar(50)   not null,
   OWNER				varchar(50),
   VIEWTEMPLATE			varchar(255)   not null,
   CREATETEMPLATE		varchar(255)   not null,
   NEXTPARTITIONTIME	datetime,
   BASETABLENAME		varchar(125)   not null,
   DATADATECOLUMN		varchar(128),
   PUBLICVIEWTEMPLATE	varchar(255),
   PARTITIONPLAN		varchar(128)
);

alter table DWHtype
   add primary key (STORAGEID);
