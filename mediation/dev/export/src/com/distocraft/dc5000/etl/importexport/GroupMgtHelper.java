package com.distocraft.dc5000.etl.importexport;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import com.distocraft.dc5000.etl.importexport.gpmgt.ImportType;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtVelocityException;
import com.ericsson.eniq.repository.ETLCServerProperties;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

/**
 * General helper class for the Group Management Stuff.
 * Handles stuff like parsing/.validating input arguements, getting platform properties etc.
 */
public final class GroupMgtHelper {

    /**
     * Indicates wheather or not group has exceeds the number limit maxGroup
     */

    public static boolean groupLimitExceeded= false;
	/**
	 * XML Schema package.
	 */
	public static final String SCHEMA_PACKAGE = "com.distocraft.dc5000.etl.importexport.gpmgt";
	/**
	 * XSD file as found on the classpath
	 */
	public static final String SCHEMA_FILE = "xsd/group_management.xsd";
	/**
	 * Indicates whether or not Velocity has been initialised or not.
	 */
	private static boolean velocityInit = false;
	/**
	 * Singletone instance used in the import/export classes
	 */
	private static GroupMgtHelper sTHIS = new GroupMgtHelper();
	/**
	 * Used with JUnits to set specific properties/
	 */
	private static Properties testEtlcProps = null;
	/**
	 * Used with JUnits to set specific properties/
	 */
	private static Properties testGpmgtProps = null;
	/**
	 * ETLC server property used to get the etlc properties file.
	 * Can be used to override the {@link#etlcProperties} value.
	 */
	private static final String ETLC_PROPERTIES = "etlc.properties";
	/**
	 * Default ETLC Server properties file
	 */
	private static String etlcProperties = File.separatorChar+"eniq"+File.separatorChar+"sw"+File.separatorChar+
		 "conf"+File.separatorChar+"ETLCServer.properties";
	/**
	 * Group Management properties source used in the common utilityies Properties helper
	 */
	public static final String GPMGT_PROP_SRC = "gpmgt";


    //name of system properties.
    private static final String CONF_DIR = "CONF_DIR";

    private static final String DC5000_CONFIG_DIR = "dc5000.config.directory";

    private static final String LOADER_DIR = "loader.dir";

    private static final String PWD = "PWD";

    private static final String PWD_ENV = "pwd";


	/**
	 * Singleton
	 */
	private GroupMgtHelper(){/**/}

	/**
	 * Method used during test to override the default location of the ETLC server properties file.
	 * @param pFile Proxy etlc file
	 */
	static void setPropertiesFile(final String pFile){ //NOPMD : Used in tests
		etlcProperties = pFile;
	}

	/**
	 * Test method to override the default etlc properties
	 * @param testProps The new set of etlc properties
	 */
	static void setEtlcProperties(final Properties testProps){ //NOPMD : Used in tests
		testEtlcProps = testProps;
	}
	/**
	 * Test method to override the default group mgt properties
	 * @param testProps The new set of etlc properties
	 */
	static void setGpmgtProperties(final Properties testProps){ //NOPMD : Used in tests
		testGpmgtProps = testProps;
	}

	/**
	 * Get an instance of the GroupMgtHelper
	 * @return Singleton instance of GroupMgtHelper
	 */
	public static GroupMgtHelper getInstance(){
			return sTHIS;
	}

	/**
	 * Utility method to get the gpmgt properties
	 * @return Properties object containing gpmgt properties
	 */
	@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.DefaultPackage"})
	static Properties getGpMgtProperties() {
		com.distocraft.dc5000.common.Properties toReturn;
		try{
			if(testGpmgtProps == null){
				toReturn = new com.distocraft.dc5000.common.Properties(GPMGT_PROP_SRC);
			} else {
				toReturn = new com.distocraft.dc5000.common.Properties(GPMGT_PROP_SRC, new Hashtable(), testGpmgtProps);
			}
		} catch (Exception e){
			throw new GroupMgtException("Failed to get Properties", e);
		}
		return toReturn;
	}

	/**
	 * Utility method to get the ETLC server properties
	 * @return Properties object containing the ETLC properties
	 * @throws IOException If the property file cant be read.
	 */
	static Properties getEtlcProperties() throws IOException { //NOPMD : Used in tests
		Properties toReturn;
		if(testEtlcProps == null){
			final String etlcFile = System.getProperty(ETLC_PROPERTIES, etlcProperties);
			toReturn = new ETLCServerProperties(etlcFile);
		} else {
			toReturn = testEtlcProps;
		}
		return toReturn;
	}

	public static void initVelocity() {
		if (!velocityInit) {
			Velocity.setProperty("resource.loader", "class");
			Velocity.setProperty("class.resource.loader.class",
				 "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			try{
				Velocity.init();
			} catch (Exception e){
				throw new GroupMgtVelocityException("_init_", e);
			}
			velocityInit = true;
		}
	}

	/**
	 * Utility method to check the validity of the version id format
	 * @param tpVersionID The Version ID to check
	 * @return ExitCodes.EXIT_OK if the format it ok, ExitCodes.EXIT_INVALID_VER otherwise
	 */
	static ExitCodes checkVersionArgs(final String tpVersionID) { //NOPMD : Used in tests
		ExitCodes exitCode = ExitCodes.EXIT_OK; //NOPMD
		if (tpVersionID != null && tpVersionID.length() > 0 && tpVersionID.indexOf(':') == -1) {
				exitCode = ExitCodes.EXIT_INVALID_VER; //NOPMD
		}
		return exitCode;
	}
	/**
	 * Utility method to check if a file exist.
	 * @param dataFile The file to check
	 * @return ExitCodes.EXIT_OK if the file is found,  EXIT_NO_FILE is the arg is null or EXIT_INVALID_FILE if not found.
	 */
	static ExitCodes checkFileArgs(final String dataFile) { //NOPMD : Used in tests
		ExitCodes exitCode = ExitCodes.EXIT_OK; //NOPMD
		if(dataFile == null){
			exitCode = ExitCodes.EXIT_NO_FILE; //NOPMD
		} else {
			final File importFile = new File(dataFile);
			if (!importFile.exists()) {
				exitCode = ExitCodes.EXIT_INVALID_FILE; //NOPMD
			}
		}
		return exitCode;
	}

	/**
	 * Get an arg value form an array,
	 * @param args The alist of args
	 * @param opt the option to get.
	 * @return The options value.
	 */
	static String getArgValue(final String[] args, final String opt){ //NOPMD : Uses in tests
		String lReturn = null; //NOPMD
		if(args != null && args.length > 0){
			for (int i = 0; i < args.length; i++) {
				if (opt.equalsIgnoreCase(args[i])) {
					if(i < args.length-1){
						lReturn = args[++i]; //NOPMD
						break;
					}
					break;
				}
			}
		}
		return lReturn; 
	}

	/**
	 * Get the current Time
	 * @return Empty String for now
	 */
	public static String getCurrentTime() {
		return "";
	}

	/**
	 * Get the max system time
	 * @return Empty String for now
	 */
	public static String getMaxTime() {
		return "";
	}

	/**
	 * Function to exit the JVM. If exitCode != ExitCodes.EXIT_OK the codes message is printed to stderr
	 * @param exitCode Exit codes to halt the JVM with.
	 */
	public static void exitSystem(final ExitCodes exitCode){
		if(exitCode != ExitCodes.EXIT_OK){
			System.err.println(exitCode.getExistMessage()); // NOPMD : To stderr
		}
		System.exit(exitCode.ordinal()); // NOPMD No in a J2EE/JEE application.
	}
	
	/**
	 * Get the import type i.e. add or delete
	 * @param args Main args
	 * @return ImportType.Delete is -delete exists, ImportType.add is -add exists, which ever first.
	 */
	public static ImportType getImportType(final String[] args) {
		ImportType type = null; //NOPMD : its the point of the method....
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-add")) {
				type = ImportType.Add; //NOPMD : its the point of the method....
				break;
			} else if (arg.equalsIgnoreCase("-delete")) {
				type = ImportType.Delete; //NOPMD : its the point of the method....
				break;
			}
		}
		return type;
	}

    /**
     * load system properties when using the importGroup & exportGroup function directly from eniq-events-services.
     * Command line is skiped.
     * @throws GroupMgtException
     */
      static void reloadProperties() throws GroupMgtException {
        if (System.getProperty(DC5000_CONFIG_DIR) == null) {
            final String configDir = System.getenv(CONF_DIR);
            if (configDir == null) {
                throw new GroupMgtException("System property " + CONF_DIR + " must be defined.", null);
            }
            System.setProperty(DC5000_CONFIG_DIR, configDir);
        }
        if (System.getProperty(LOADER_DIR) == null) {
            final String pwd = System.getenv(PWD);
            if (pwd == null) {
                System.setProperty(LOADER_DIR, "");
                System.setProperty(PWD_ENV, "");

            } else {
                System.setProperty(LOADER_DIR, pwd);
                System.setProperty(PWD_ENV, pwd);
            }

        }
    }
}
