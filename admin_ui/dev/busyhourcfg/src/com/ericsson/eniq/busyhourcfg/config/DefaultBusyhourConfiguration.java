/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

import java.io.File;

import javax.servlet.ServletContext;

import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public class DefaultBusyhourConfiguration implements BusyhourConfiguration {

  private static final String ETLCSERVER_PROPERTIES = "ETLCServer.properties";

  private static final String CONF_DIR = "CONF_DIR";

  private static final String CONFFILEFOLDER = "conffilefolder";

  private static final String CONFFILE = "conffile";

  public BusyhourProperties getBusyhourProperties(final ServletContext context, final BusyhourEnvironment environment) throws BusyhourConfigurationException {
    try {
      final String folder = environment.getEnvEntryString(CONFFILEFOLDER);
      final String filename = environment.getEnvEntryString(CONFFILE);
      final String filepath = context.getRealPath(File.separator + folder + File.separator + filename);
      final BusyhourProperties busyhourProperties = new BusyhourProperties(filepath);
      return busyhourProperties;
    } catch (Exception e) {
      throw new BusyhourConfigurationException(e.getMessage());
    }
  }

  public ETLCServerProperties getETLCServerProperties(final ServletContext context, final BusyhourEnvironment environment) throws BusyhourConfigurationException {
    try {
      final String folder = System.getProperty(CONF_DIR);
      final String filename = ETLCSERVER_PROPERTIES;
      final String filepath = File.separator + folder + File.separator + filename;
      final ETLCServerProperties etlcserverProperties = new ETLCServerProperties(filepath);
      return etlcserverProperties;
    } catch (Exception e) {
      throw new BusyhourConfigurationException(e.getMessage());
    }
  }

}
