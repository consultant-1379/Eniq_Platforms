/**
 * Settings for the LicensingCache. Reads the data from the default .properties file.
 */
package com.ericsson.eniq.licensing.cache;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.ServicenamesHelper;
import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author ecarbjo
 */
public class LicensingSettings {

  private String serverHostName;

  private int serverPort;

  private String serverRefName;

  private String etlcServerPropertiesFile;

  private String[] licensingServers;

  private String[] mappingFiles;

  private static Logger log;

  /**
   * @throws IOException
   * 
   */
  public LicensingSettings() {
    // get the properties file, with a default of /eniq/sw/conf
    String propertiesFile = System.getProperty("CONF_DIR", "/eniq/sw/conf");
    if (!propertiesFile.endsWith(File.separator)) {
      propertiesFile += File.separator;
    }

    propertiesFile += "ETLCServer.properties";

    init(propertiesFile);
  }

  /**
   * @param propertiesFile
   * @throws IOException
   */
  public LicensingSettings(final String propertiesFile) {
    init(propertiesFile);
  }

  /**
   * initialize the class and read the properties file.
   * 
   * @param propertiesFile
   * @throws IOException
   */
  public void init(final String propertiesFile) {
    log = Logger.getLogger("licensing.cache.LicenseCacheSettings");

    log.fine("Reading properties from " + propertiesFile);
    this.etlcServerPropertiesFile = propertiesFile;

    this.getProperties();
  }

  /**
   * Read the properties file to get the host and port for the RMI server.
   * 
   * @throws IOException
   */
  protected void getProperties() {
    log.finest("enter getProperties()");
    try {
      // read properties
      Properties appProps = null;

      try {
        appProps  = new ETLCServerProperties(etlcServerPropertiesFile);
      } catch (IOException e) {
        log.warning("Could not load the file " + etlcServerPropertiesFile + ". Using defaults.");
      } 
      
      // 	get the hostname by service name and default to localhost.
      String hostNameByServiceName = null ;
      try{
    	  hostNameByServiceName = ServicenamesHelper.getServiceHost("licenceservice", "localhost");
      }catch(final Exception e){
    	  hostNameByServiceName = "localhost" ;
      }
      
      this.serverHostName = appProps.getProperty("LICENCE_HOSTNAME", hostNameByServiceName);
      log.finer("Server set to: " + this.serverHostName);

      // read the port, default to 1200
      this.serverPort = 1200;
      final String serverPortStr = appProps.getProperty("LICENCE_PORT", "1200");
      try {
        this.serverPort = Integer.parseInt(serverPortStr);
      } catch (NumberFormatException nfe) {
        log.config("Server port could not be parsed");
      }

      log.finer("Server port set to: " + this.serverPort);

      // red the ref name, default to LicensingCache. This is the
      // reference that is bound to the RMI registry.
      this.serverRefName = appProps.getProperty("LICENCE_REFNAME", "LicensingCache");

      log.finer("Server refname set to: " + this.serverRefName);

      // read the mapping files settings. These files will be used by the
      // feature mapper to map feature names to FAJ numbers, descriptions, and
      // interface names.
      final String mapperFile = appProps.getProperty("MAPPING_FILES",
          "/eniq/sw/conf/feature_descriptions:/eniq/sw/conf/feature_techpacks:/eniq/sw/conf/feature_report_packages");
      final String delimiter = System.getProperty("mapping.files.delimiter", ":");
      this.mappingFiles = mapperFile.split("\\s*"+delimiter+"\\s*");

      log.finer("Using " + mappingFiles.length + "mapping file(s): " + mapperFile);
    } catch (Exception e) {
      log.warning("Could not read properties from " + this.etlcServerPropertiesFile);
    }

    // read the LSHOST from a environment variable. This means that it needs
    // to be set from the starting script or command line.
    final String lshost = System.getProperty("LSHOST", "localhost");
    log.finer("License server set to: " + lshost);

    // split the string on colon to get all servers if many have been
    // specified.
    this.licensingServers = lshost.split("\\s*:\\s*");

    log.finer("Using " + licensingServers.length + " license server(s): " + lshost);
    log.finest("exit getProperties()");
  }

  /* Setters and getters */

  public String getServerHostName() {
    log.finest("getServerHostName()");
    return serverHostName;
  }

  public void setServerHostName(final String serverHostName) {
    log.finest("setServerHostName()");
    this.serverHostName = serverHostName;
  }

  public int getServerPort() {
    log.finest("getServerPort()");
    return serverPort;
  }

  public void setServerPort(final int serverPort) {
    log.finest("setServerPort()");
    this.serverPort = serverPort;
  }

  public String getServerRefName() {
    log.finest("getServerRefName()");
    return serverRefName;
  }

  public void setServerRefName(final String serverRefName) {
    log.finest("setServerRefName()");
    this.serverRefName = serverRefName;
  }

  public String[] getLicensingServers() {
    log.finest("getLicensingServer(): " + licensingServers.length);
    return licensingServers;
  }

  public void setLicensingServer(final String[] servers) {
    log.finest("setLicensingServer()");
    if (servers == null) {
      this.licensingServers = null;
    } else {
      this.licensingServers = new String[servers.length];
      for (int i = 0; i < servers.length; i++) {
        this.licensingServers[i] = servers[i];
      }
    }
  }

  public void setMappingFiles(final String[] mappingFiles) {
    log.finest("enter setMappingFiles()");
    if (mappingFiles == null) {
      this.mappingFiles = null;
    } else {
      this.mappingFiles = new String[mappingFiles.length];
      for (int i = 0; i < mappingFiles.length; i++) {
        this.mappingFiles[i] = mappingFiles[i];
      }
    }
  }

  public String[] getMappingFiles() {
    log.finest("enter getMappingFiles()");
    return this.mappingFiles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public LicensingSettings clone() {
    log.finest("enter clone()");
    final LicensingSettings clone = new LicensingSettings();
    clone.setServerHostName(serverHostName);
    clone.setServerPort(serverPort);
    clone.setServerRefName(serverRefName);
    clone.setLicensingServer(licensingServers);

    log.finest("exit clone()");
    return clone;
  }
}
