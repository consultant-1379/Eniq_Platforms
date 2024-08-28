package com.ericsson.navigator.esm;

import java.io.ObjectInputStream.GetField;

public class MVMEnvironment {

	/**
	 * ESM properties
	 */
	
	public static final String PROPERTIESDIR = System.getProperty("PropertiesDir","/eniq/mediator/conf/properties");
	
	public static final String LOGGERCONFIGFILE = System.getProperty(
			"LoggerConfigFile", "/eniq/mediator/conf/properties/mediator.log4j.properties");

	public static final String MAVERICK_LICENSE_PATH = System.getProperty(
			"Maverick.licensePath", "/eniq/mediator/conf/properties/mavericklicense.txt");
	
	public static final String CONVERSIONS_PATH = System.getProperty(
"Conversions.path", "/eniq/mediator/runtime/conversions");
	/**
	 * ESM Alarm properties
	 */
	public static final String SNMPBIND_HOSTNAME = System.getProperty(
			"Alarm.snmpBindHostName", "0.0.0.0");
	public static final Integer SNMPBIND_PORT = Integer.getInteger(
			"Alarm.snmpBindPort", new Integer(162));
	public static final String MSTTRANSLATIONDIR = System.getProperty(
			"Alarm.mst.translationPath", "/eniq/mediator/runtime/mst");	
	public static final String MSTCUSTOMERTRANSLATIONSDIR = System.getProperty(
			"Alarm.mst.customTranslationPath", "/eniq/mediator/runtime/mst");
	public static final String MIBFILESDIR = System.getProperty("Alarm.mibFilesPath",
			"/eniq/mediator/runtime/mibs");
	public static final String USERMIBFILESDIR = System.getProperty("Alarm.userMibFilesPath",
			"/eniq/mediator/runtime/mibs");
	public static final long TXFSYNCTIMEOUT = Long.getLong("Alarm.txf.syncTimeOut", 30000);
	public static final String TXFALARMFILEPATH = System.getProperty("Alarm.txf.alarmFilePath", "/eniq/mediator/runtime/alarmfiles/");
	public static final long TXFALLOWEDFILESIZE = Long.getLong("Alarm.txf.maxAlarmFileSize", 5000) * 1024; //bytes
	public static final String TXFTRANSLATIONMAPPATH = System.getProperty("Alarm.txf.translationMapPath", "/eniq/mediator/runtime/txf/maps");
	
	/**
	 * ESM PM properties
	 */
	public static final String LOCALFILE_PMIRPINPUT = System.getProperty("PM.irpLocalInputPath",
			"/eniq/mediator/runtime/input");
	public static final String LOCALFILE_PMIRPARCHIVE = System.getProperty("PM.irpLocalArchivePath",
			"/eniq/mediator/runtime/archive");
	public static final String LOCALFILE_PMIRPCORRUPTED = System.getProperty("PM.irpLocalCorruptedPath",
			"/eniq/mediator/runtime/corrupted");
	public static final String REMOTEFILE_PMDOWNLOADDIR = System.getProperty("PM.remoteDownloadDirectory",
			"/eniq/mediator/runtime/");
	public static final String COUNTERSETDIR = System.getProperty("PM.counterSetPath",
 "/eniq/mediator/runtime/countersets");
	public static final String PARSERPLUGINDIR = System.getProperty("PM.parserPluginPath",
			"/eniq/mediator/conf/plugins/");
	public static final String PARSERPLUGINPROPERTYFILE = System.getProperty("PM.parserPluginPropertyFile",
			"config.xml");
	public static final String SOEMSORTDIR = System.getProperty("MVM.soemSortingDirectory","/eniq/mediator/runtime/sorting/");
	public static final String SOEMFAILEDDIR = System.getProperty("MVM.soemSortingDirectory","/eniq/mediator/runtime/failed/");
	
	public static final boolean PMAGENT3GPP = Boolean.getBoolean("PM.enable3GPPFileAgent");
	public static final boolean PMAGENTDATABASE = Boolean.getBoolean("PM.enableDataBase");
	public static final boolean PMDBKPIONLY = Boolean.getBoolean("PM.enableKPIOnly");
	public static final boolean NAME_NBDIRECTORY = Boolean.getBoolean("enableNBDirectoryByName");
	public static final boolean ENABLEDOWNLOAD = Boolean.getBoolean("MVM.enableDownload");
	public static final boolean ENABLESNMP = Boolean.getBoolean("MVM.enableSNMP");
	public static final boolean ENABLEPARSER = Boolean.getBoolean("MVM.enableParser");
	public static final boolean PM3GPPMOIDRETENTION = Boolean.getBoolean("PM.retainNEDNFrom3GPPDataFile");
	public static final int REMOTEFILE_COREPOOLSIZE = Integer.getInteger("PM.remoteFileFetchExecutor.corePoolSize", 10);
	public static final int REMOTEFILE_MAXPOOLSIZE = Integer.getInteger("PM.remoteFileFetchExecutor.maximumPoolSize", 100);
	public static final int REMOTEFILE_KEEPALIVETIME = Integer.getInteger("PM.remoteFileFetchExecutor.keepAliveTime", 30);
	public static final int REMOTEFILE_POOLQUEUESIZE = Integer.getInteger("PM.remoteFileFetchExecutor.queueSize", 1000);
	public static final String SFTP_PRIVATEKEY_PATH = System.getProperty("PM.sftp.privateKeyPath", "/eniq/home/dcuser/.ssh/id_rsa");
	public static final String SFTP_LISTEDFILES_STORAGE_PATH = System.getProperty("PM.sftp.listedFilesStoragePath", "/eniq/mediator/runtime/");
	public static final String NB_DIRECTORY = System.getProperty("PM.northBoundDirectory", "/eniq/mediator/runtime/raw");	
	public static final int THREAD_KEEPALIVETIME = Integer.getInteger("PM.remoteFileFetchExecutor.keepAliveTime", 5);
	public static final int NUMBEROFWRITERS = Integer.getInteger("MVM.enableNumberOfSNMPWriters", 1);
	public static final int HOUSEKEEPINGDIRLIMIT = Integer.getInteger("MVM.corruptStorageLength",24);
	public static final int STORAGESPACE = Integer.getInteger("MVM.storageSpace",40);
}
