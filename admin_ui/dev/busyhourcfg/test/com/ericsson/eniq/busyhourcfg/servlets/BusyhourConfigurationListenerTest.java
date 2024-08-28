/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.servlets;

import static org.junit.Assert.*;

import javax.servlet.ServletContextEvent;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.common.StaticProperties;

/**
 * @author eheijun
 * 
 */
public class BusyhourConfigurationListenerTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String SOME_KEY = "some_key";

  private static final String SOME_VALUE = "some_value";

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    StaticProperties.giveProperties(null);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourConfigurationListener#contextInitialized(javax.servlet.ServletContextEvent)}
   * .
   */
  @Test
  public void testContextInitialized() {
    final ServletContextEvent event = context.mock(ServletContextEvent.class);
    final BusyhourConfigurationListener listener = new BusyhourConfigurationListener();
    try {
      listener.contextInitialized(event);
      assertTrue(StaticProperties.getProperty(SOME_KEY).equals(SOME_VALUE));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.servlets.BusyhourConfigurationListener#contextDestroyed(javax.servlet.ServletContextEvent)}
   * .
   */
  @Test
  public void testContextDestroyed() {
    final ServletContextEvent event = context.mock(ServletContextEvent.class);
    final BusyhourConfigurationListener listener = new BusyhourConfigurationListener();
    try {
      listener.contextDestroyed(event);
      try {
        StaticProperties.getProperty(SOME_KEY).equals(SOME_VALUE);
      } catch (NullPointerException e) {
        // this should happen
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
