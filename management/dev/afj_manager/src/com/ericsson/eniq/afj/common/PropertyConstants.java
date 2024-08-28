/**
 * 
 */
package com.ericsson.eniq.afj.common;

/**
 * Class to list all the properties required by afj manager.
 * @author esunbal
 *
 */
public class PropertyConstants {

	public static final String PROP_IQLOADER_DIR = "iqloader.dir";

	public static final String PROP_REJECTED_DIR = "rejected.dir";

	public static final String PROP_ETLDATA_DIR = "etldata.dir";

	public static final String PROP_ETLDATA_JOINED_DIR = "joined.dir";	

	public static final String DEF_STATIC_PROP_NAME = "dc5000.config.directory";

	public static final String PROP_IDE_TEMPLATES = "default.ide.templates";

	public static final String PROP_DEF_PARTITIONTYPE = "default.partitiontype";

	public static final String PROP_DEF_PROCESSINSTRUCTION = "default.dataformat.processinstruction";

	public static final String PROP_DEF_TECHPACKVERSION = "default.interfacemeasurement.techpackversion";

	public static final String PROP_DEF_STATUS = "default.interfacemeasurement.status";

	public static final String PROP_DEF_TYPE = "default.transformer.type";

	public static final String PROP_DEF_DESCRIPTION = "default.transformer.description";

	public static final String PROP_DEF_PARTITIONPLAN = "default.measurementtable.partitionplan";

	public static final String PROP_DEF_TABLELEVEL = "default.measurementtable.tablelevel";

	public static final String PROP_DEF_DATAFORMATSUPPORT = "default.measurementtype.dataformatsupport";

	public static final String PROP_DEF_VECTORSUPPORT = "default.measurementtype.vectorsupport";

	public static final String PROP_DEF_UNIVERSEEXTENSION = "default.measurementtype.universeextension";

	public static final String PROP_DEF_PLAINTABLE = "default.measurementtype.plaintable";

	public static final String PROP_DEF_DELTACALCSUPPORT = "default.measurementtype.deltacalcsupport";

	public static final String PROP_DEF_RANKINGTABLE = "default.measurementtype.rankingtable";

	public static final String PROP_DEF_ELEMENTBHSUPPORT = "default.measurementtype.elementbhsupport";

	public static final String PROP_DEF_TOTALAGG = "default.measurementtype.totalagg";

	public static final String PROP_DEF_SIZING = "default.measurementtype.sizing";

	public static final String PROP_DEF_JOINABLE = "default.measurementtype.joinable";

	public static final String PROP_DEF_OBJECTTYPE = "default.dataformat.objecttype";	

	public static final String PROP_IDENTITY_COUNTERTYPE = "countertype.identity";

	public static final String PROP_AFJTECHPACKS = "afj.supported.tps";	

	public static final int MEASTYPE_TOKEN = 2;	

	public static final String DATAFORMAT_DELIMITER = ":";

	public static final String PROPERTY_DELIMITER = ".";
	
	public static final String PROP_PARSER = "parser";
	
	public static final String PROP_DATASIZE= "datasize";
	
	public static final String PROP_DATASCALE = "datascale";
	
	public static final String PROP_KEYS = "keys";
	
	public static final String PROP_MEASNAME = "measname";
	
	public static final String PROP_INTERFACE = "interface";
	
	public static final String PROP_DEFAULT_TABLE_LEVEL = "default.tablelevel";
	
	public static final String PROP_DEFAULT_STORAGE_TIME = "default.storagetime";
	
	public static final String PROP_DEFAULT_TYPE = "default.type";
	
	public static final String PROP_DEFAULT_STATUS = "default.status";
	
	public static final String PROP_DEFAULT_PARTITIONPLAN = "default.partitionplan";
	
	public static final String PROP_DEFAULT_DIRECTORYCHECKER = "default.directorychecker";

	public static final String PROPERTY_UNDERSCORE = "_";

	public static final String ENGINE_NO_LOADS = "NoLoads";

	public static final String ENGINE_NORMAL = "Normal";

	public static final String DEFAULT_CONF_DIR = "/eniq/sw/conf";

	public static final String CONF_DIR = "CONF_DIR";

	public static final String AFJMANAGER_PROPERTIES = "AFJManager.properties";

	public static final String AFJ_LOGGER_NAME = "com.ericsson.eniq.afj";

	public static final String PROP_DWH_TYPE = "dwh_type";
	public static final String DEFAULT_DWH_TYPE = "USER";

	public static final String PROP_DWHDBA_TYPE = "dwhdba_type";
	public static final String DEFAULT_DWHDBA_TYPE = "DBA";

	public static final String PROP_DWHREP_TYPE = "dwhrep_type";
	public static final String DEFAULT_DWHREP_TYPE = "USER";

	public static final String PROP_DWH_USER = "dwh_user";
	public static final String DEFAULT_DWH_USER = "dc";

	public static final String PROP_DWH_NAME = "dwh_name";
	public static final String DEFAULT_DWH_NAME = "dwh";

	public static final String PROP_DWH_REDIRECT = "afj.redirect.dwh";
	public static final String DEFAULT_DWH_REDIRECT = null;

	public static final String PROP_DEFAULT_NAMESPACEAWARE = "default.namespaceaware";

	public static final String PROP_IMS_NAMESPACEWARE = "ims.namespaceaware";

	public static final String PROP_TEMPLATE_DIR = "afj.template.dir";

	public static final String PROP_VELOCITY_LOADER = "class.resource.loader.class";

	public static final String DEFAULT_VELOCITY_LOADER = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

	public static final String PROP_MOM = "mom.";

	public static final String PROP_INCLUDESQL = "default.includesql";

	public static final String PROP_FOLLOWJOHN = "default.followjohn";

	public static final String PROP_UNIQUEVALUE = "default.uniquevalue";

	public static final String PROP_NULLABLE = "default.nullable";

	public static final String PROP_INDEXES = "default.indexes";

	public static final String PROP_RELEASEID = "default.releaseid";

	public static final String PROP_UNIQUEKEY = "default.uniquekey";

	public static final String PROP_COLTYPE = "default.coltype";

	public static final String PROP_UNIVERSEOBJECT = "default.universeobject";

	public static final String PROP_UNIVERSECLASS = "default.universeclass";		

	public static final String PROP_DATA_SIZE = "datasize.";

	public static final String PROP_DATA_SCALE = "datascale.";

	public static final String PROP_HSS = "hss.";

	public static final String PROP_STN_NAME = "stn.name";

	public static final String PROP_BSS_NAME = "bss.name";

	public static final String PROP_STS_NAME = "sts.name";

	public static final String PROP_IMS_NAME = "ims.name";

	public static final String PROP_MTAS_NAME = "mtas.name";

	public static final String PROP_HSS_NAME = "hss.name";	

	public static final String PROP_BSS_ACTIONTYPE = "bss.actiontype";

	public static final String PROP_BSS_ADDVENDORIDTO = "bss.addvendoridto";

	public static final String PROP_MOCLEVEL_BSS = "bss.moclevel";

	public static final String PROP_MOCLEVEL_STS = "sts.moclevel";	

	public static final String ETLCSERVER_PROPERTIES = "ETLCServer.properties";

	public static final String PROP_AFJ_BASE_DIR = "afjBasePath";

	public static final String PROP_AFJ_ARCHIVE_DIR = "afjArchivePath";

	public static final String PROP_AFJ_STATUS = "STATUS";

	public static final String PROP_AFJ_STATUS_FILE = "afj_status_file";

	public static final String PROP_AFJ_SCHEMA_FILE = "afj.schema.file";

	public static final String PROP_AFJ_SCHEMA_FILE_IMS = "afj.schema.file.ims";

	public static final String PROP_AFJ_SCHEMA_PACKAGE = "afj.schema.package";

	public static final String PROP_AFJ_SCHEMA_PACKAGE_IMS = "afj.schema.package.ims";

	public static final String PROP_AFJ_SCHEMA_DIR = "dtd";	

	public static final String PROP_ENGINE_DB_USERNAME = "ENGINE_DB_USERNAME";
	public static final String DEFAULT_ENGINE_DB_USERNAME = "etlrep";

	public static final String PROP_ENGINE_PORT = "ENGINE_PORT";
	public static final String DEFAULT_ENGINE_PORT = "1200";

	public static final String PROP_ENGINE_HOSTNAME = "ENGINE_HOSTNAME";
	public static final String DEFAULT_ENGINE_HOSTNAME = "localhost";

	public static final String PROP_ENGINE_DB_PASSWORD = "ENGINE_DB_PASSWORD";
	public static final String DEFAULT_ENGINE_DB_PASSWORD = "etlrep";

	public static final String PROP_ENGINE_DB_URL = "ENGINE_DB_URL";

	
	public static final String PROP_ENGINE_DB_DRIVERNAME = "ENGINE_DB_DRIVERNAME";


	public static final String PROP_TRANSFORMATIONTYPE = "type";

	public static final String PROP_TRANSFORMATIONTARGET = "target";

	public static final String PROP_TRANSFORMATIONSOURCE = "source";

	public static final String PROP_TRANSFORMATIONCONFIG = "config";

	public static final String PROP_MAXCOUNTERS = "max.counters";

	public static final String PROP_MAXMEASTYPES = "max.meastypes";

	public static final String PROP_SPECIALMULTIPLEMOCS = "bss.specialmultiplemocs";


}
