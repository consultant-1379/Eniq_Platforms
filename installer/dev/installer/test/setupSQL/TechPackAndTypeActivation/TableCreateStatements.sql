CREATE TABLE TPActivation (
	TECHPACK_NAME VARCHAR(30),
	STATUS VARCHAR(10),
	VERSIONID VARCHAR(128),
	TYPE VARCHAR(10),
	MODIFIED INTEGER
);
CREATE TABLE Versioning (
	VERSIONID VARCHAR(128),
	DESCRIPTION VARCHAR(50),
	STATUS NUMERIC(9),
	TECHPACK_NAME VARCHAR(30),
	TECHPACK_VERSION VARCHAR(32),
	TECHPACK_TYPE VARCHAR(10),
	PRODUCT_NUMBER VARCHAR(255),
	LOCKEDBY VARCHAR(255),
	LOCKDATE TIMESTAMP,
	BASEDEFINITION VARCHAR(128),
	BASEVERSION VARCHAR(16),
	INSTALLDESCRIPTION VARCHAR(32000),
	UNIVERSENAME VARCHAR(30),
	UNIVERSEEXTENSION VARCHAR(16),
	ENIQ_LEVEL VARCHAR(12),
	LICENSENAME VARCHAR(1023)
);
CREATE TABLE MeasurementType (
	TYPEID VARCHAR(255),
	TYPECLASSID VARCHAR(255),
	TYPENAME VARCHAR(255),
	VENDORID VARCHAR(128),
	FOLDERNAME VARCHAR(128),
	DESCRIPTION VARCHAR(32000),
	STATUS NUMERIC(9),
	VERSIONID VARCHAR(128),
	OBJECTID VARCHAR(255),
	OBJECTNAME VARCHAR(255),
	OBJECTVERSION INTEGER,
	OBJECTTYPE VARCHAR(255),
	JOINABLE VARCHAR(255),
	SIZING VARCHAR(16),
	TOTALAGG INTEGER,
	ELEMENTBHSUPPORT INTEGER,
	RANKINGTABLE INTEGER,
	DELTACALCSUPPORT INTEGER,
	PLAINTABLE INTEGER,
	UNIVERSEEXTENSION VARCHAR(12),
	VECTORSUPPORT INTEGER,
	DATAFORMATSUPPORT INTEGER,
	FOLLOWJOHN INTEGER,
	ONEMINAGG INTEGER,
	FIFTEENMINAGG INTEGER,
	EVENTSCALCTABLE INTEGER,
	MIXEDPARTITIONSTABLE INTEGER,
	LOADFILE_DUP_CHECK INTEGER,	SONAGG int,	SONFIFTEENMINAGG int,	ROPGRPCELL varchar(255)
);
CREATE TABLE ReferenceTable (
	TYPEID VARCHAR(255),
	VERSIONID VARCHAR(128),
	TYPENAME VARCHAR(255),
	OBJECTID VARCHAR(255),
	OBJECTNAME VARCHAR(255),
	OBJECTVERSION VARCHAR(50),
	OBJECTTYPE VARCHAR(255),
	DESCRIPTION VARCHAR(32000),
	STATUS NUMERIC(9),
	UPDATE_POLICY NUMERIC(9),
	TABLE_TYPE VARCHAR(12),
	DATAFORMATSUPPORT INTEGER,
	BASEDEF INTEGER
);
CREATE TABLE TypeActivation (
	TECHPACK_NAME VARCHAR(30),
	STATUS VARCHAR(10),
	TYPENAME VARCHAR(255),
	TABLELEVEL VARCHAR(50),
	STORAGETIME NUMERIC(15),
	TYPE VARCHAR(12),
	PARTITIONPLAN VARCHAR(128)
);
CREATE TABLE MeasurementObjBHSupport (
	TYPEID VARCHAR(255),
	OBJBHSUPPORT VARCHAR(32)
);
CREATE TABLE MeasurementVector (
	TYPEID VARCHAR(255),
	DATANAME VARCHAR(128),
	VENDORRELEASE VARCHAR(16),
	VINDEX NUMERIC(30,6),
	VFROM VARCHAR(255),
	VTO VARCHAR(255),
	MEASURE VARCHAR(255),
	QUANTITY NUMERIC(30,6)
);
CREATE TABLE MeasurementTable (
	MTABLEID VARCHAR(255),
	TABLELEVEL VARCHAR(50),
	TYPEID VARCHAR(255),
	BASETABLENAME VARCHAR(255),
	DEFAULT_TEMPLATE VARCHAR(50),
	PARTITIONPLAN VARCHAR(128)
);
CREATE TABLE ReferenceTable (
	TYPEID VARCHAR(255),
	VERSIONID VARCHAR(128),
	TYPENAME VARCHAR(255),
	OBJECTID VARCHAR(255),
	OBJECTNAME VARCHAR(255),
	OBJECTVERSION VARCHAR(50),
	OBJECTTYPE VARCHAR(255),
	DESCRIPTION VARCHAR(32000),
	STATUS NUMERIC(9),
	UPDATE_POLICY NUMERIC(9),
	TABLE_TYPE VARCHAR(12),
	DATAFORMATSUPPORT INTEGER,
	BASEDEF INTEGER
);
