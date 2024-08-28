package com.ericsson.sim.constants;

public class SIMEnvironment {

	//Parent directory structure
	public static String RUNTIMEPATH =  System.getProperty("RuntimePath", "/eniq/data/pmdata_sim");
	public static String CONFIGPATH =  System.getProperty("ConfigPath", "/eniq/sw/conf/sim");
	
	//App sub directories
	public static String MIBSPATH = System.getProperty("MibsPath", CONFIGPATH + "/mibs");	
	
	//Runtime sub directories, using config path for the optimizer
	public static String OPTPATH = System.getProperty("OptPath",RUNTIMEPATH + "/optimizer");
	public static String LOGPATH =  System.getProperty("LogPath", "/eniq/log/sw_log/sim");
	public static String PERSPATH = System.getProperty("PersPath", CONFIGPATH + "/pers");
	public static String PARSINGPATH = System.getProperty("ParsingPath", RUNTIMEPATH + "/parsing");

	//Config file paths
	public static String TOPOLOGYCONFIG = System.getProperty("TopologyConfig", CONFIGPATH + "/topology.xml");
	public static String TOPOLOGYEXTRACONFIG = System.getProperty("TopologyExtraConfig", CONFIGPATH + "/topology.simc");
	public static String ROPINTERVALCONFIG = System.getProperty("RopIntervalConfig", CONFIGPATH + "/intervals.simc");
	public static String PROTOCOLCONFIG = System.getProperty("ProtocolConfig", CONFIGPATH + "/protocols.simc");
	public static String PROCUSPROTOCOLCONFIG = System.getProperty("ProtocolConfig", CONFIGPATH + "/procusProtocols.simc");
	public static String DISABLEDCOUNTERSCONFIG = System.getProperty("DisabledCountersConfig", CONFIGPATH + "/DisabledSNMPCounters.xml");
	public static String RUNPROPSCONFIG = System.getProperty("RunPropsConfig", CONFIGPATH + "/properties.simc");
	public static String LOGGERCONFIG = System.getProperty("LoggerConfig", CONFIGPATH + "/log.properties");
	public static String MAVERICKLICENSE = System.getProperty("MaverickLicense", CONFIGPATH + "/maverickLicense.txt");
	public static String OPTIMIZERRULES = System.getProperty("OptimizerRules", CONFIGPATH + "/optimizerRules.txt");
	
	//SSH keys path
	public static String SSHKEYSPATH = System.getProperty("SSHKeysPath", "/eniq/home/dcuser/.ssh/id_rsa");
	
	//Variables
	public static long STARTTIME;
}

