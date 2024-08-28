/**
 * 
 */
package com.ericsson.eniq.afj.common;

import static com.ericsson.eniq.afj.common.PropertyConstants.AFJMANAGER_PROPERTIES;
import static com.ericsson.eniq.afj.common.PropertyConstants.CONF_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.DEFAULT_CONF_DIR;
import static com.ericsson.eniq.afj.common.PropertyConstants.DEF_STATIC_PROP_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.ETLCSERVER_PROPERTIES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.velocity.app.Velocity;

import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * Class to get the afj manager properties loaded.
 * 
 * @author esunbal
 * 
 */
public class PropertiesUtility {

  private static PropertiesUtility propsUtil;

  private static Properties afjProperties;

  private PropertiesUtility() {
  }

  public static void setPropertiesUtility(final PropertiesUtility anotherPropsUtil) {
    propsUtil = anotherPropsUtil;
  }

  public static void setAfjProperties(final Properties anotherProps) {
    afjProperties = anotherProps;
  }

  private static void initialise() throws AFJException {
    if (afjProperties == null) {
      afjProperties = new Properties();
    }
    if (propsUtil == null) {
      propsUtil = new PropertiesUtility();
      initVelocity();
      loadEngineProperties();
      loadAFJManagerProperties();
      initStaticProperties();
    }
  }

  private static void initStaticProperties() throws AFJException {
    String conf = System.getProperty(DEF_STATIC_PROP_NAME);
    if (conf == null) {
      conf = System.getProperty(CONF_DIR, DEFAULT_CONF_DIR);
      System.setProperty(DEF_STATIC_PROP_NAME, conf);
    }
    try {
      StaticProperties.reload();
    } catch (Exception e) {
      throw new AFJException("Exception in reloading StaticProperties:" + e.getMessage());

    }
  }

  private static void initVelocity() throws AFJException {
    try {
      // Velocity.setProperty("resource.loader", "class");
      // final String velLoader = PropertiesUtility.getProperty(PROP_VELOCITY_LOADER, DEFAULT_VELOCITY_LOADER);
      // Velocity.setProperty(PROP_VELOCITY_LOADER, velLoader);
      // Velocity.init();
      Velocity.setProperty("resource.loader", "class");
      Velocity.setProperty("class.resource.loader.class",
          "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
      // Velocity
      // .setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
      Velocity.init();
    } catch (Exception e) {
      throw new AFJException(e.getMessage());
    }
  }

  private static void loadEngineProperties() throws AFJException {
    final String etlcPropertiesFile = System.getProperty(CONF_DIR, DEFAULT_CONF_DIR) + File.separator
        + ETLCSERVER_PROPERTIES;
    try {
      final ETLCServerProperties props = new ETLCServerProperties(etlcPropertiesFile);
      for (final Enumeration<?> propertyNames = props.propertyNames(); propertyNames.hasMoreElements(); ) {
        final Object key = propertyNames.nextElement();
        afjProperties.put(key, props.get(key));
      }    
    } catch (IOException e) {
      throw new AFJException("IO exception when loading the properties file " + etlcPropertiesFile + ":" + e.getMessage());
    }
  }

  private static void loadAFJManagerProperties() throws AFJException {
    final String afjPropertiesFile = System.getProperty(CONF_DIR) + File.separator + AFJMANAGER_PROPERTIES;
    FileInputStream fis;
    try {
      fis = new FileInputStream(afjPropertiesFile);
      try {
        afjProperties.load(fis);
      } finally {
        fis.close();
      }
    } catch (FileNotFoundException e) {
      throw new AFJException("No file found for loading the properties file " + afjPropertiesFile + ":" + e.getMessage());
    } catch (IOException e) {
      throw new AFJException("IO exception when loading the properties file " + afjPropertiesFile + ":" + e.getMessage());
    }
  }

  public static String getProperty(final String key, final String defaultValue) throws AFJException {
    initialise();
    return afjProperties.getProperty(key, defaultValue);
  }

  public static String getProperty(final String key) throws AFJException {
    return getProperty(key, null);
  }

}
