/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author eheijun
 * 
 */
public class DefaultBusyhourEnvironment implements BusyhourEnvironment {

  private final Logger log = Logger.getLogger(DefaultBusyhourEnvironment.class.getName());
  /**
   * Hide constructor
   */
  protected DefaultBusyhourEnvironment() {
    super();
  }

  private static DefaultBusyhourEnvironment _instance;

  public static BusyhourEnvironment getInstance() {
    if (_instance == null) {
      _instance = new DefaultBusyhourEnvironment();
    }
    return _instance;
  }

  /**
   * Gets env entry String value from web.xml
   * 
   * @param entry
   * @return value
   */
  public String getEnvEntryString(final String entry) {
    String value = "";

    try {
      final InitialContext context = new InitialContext();
      final Context envCtx = (Context) context.lookup("java:comp/env");
      value = (String) envCtx.lookup(entry);
    } catch (NamingException e) {
      log.severe(e.getMessage());
    } catch (Exception e) {
      log.severe(e.getMessage());
    }

    return value;
  }
}
