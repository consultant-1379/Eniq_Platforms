package com.ericsson.eniq.busyhourcfg.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.distocraft.dc5000.common.StaticProperties;


public class BusyhourConfigurationListener implements ServletContextListener {

  private static final String STATIC_PROPERTIES = "static.properties";

  private final Logger log = Logger.getLogger(BusyhourConfigurationListener.class.getName());
  
  /*
   * (non-Javadoc)
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    try {
      final Properties props = new Properties();
      final InputStream in = getClass().getClassLoader().getResourceAsStream(STATIC_PROPERTIES);
      if (in == null) {
        log.warning("Unable to find " + STATIC_PROPERTIES);
      } else {
          props.load(in);
      }
      StaticProperties.giveProperties(props);
      log.info("StaticProperties initialized");
    } catch (IOException e) {
      log.warning("Unable to read " + STATIC_PROPERTIES);
    } catch (Exception e) {
      log.warning("Unable to initialize " + STATIC_PROPERTIES);
    }
    log.info("application initialized");
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    try {
      StaticProperties.giveProperties(null);
    } catch (Exception e) {
      log.warning("Unable to finalize " + STATIC_PROPERTIES);
    }
    // this.context = null;
    log.info("application finalized");
  }

}
