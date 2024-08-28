/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ecarbjo
 * 
 */
public class LicensingSettingsTest {

  private static LicensingSettings settings = null;

  /**
	 */
  @BeforeClass
  public static void setUpBeforeClass() {
    settings = new LicensingSettings();
    settings.setServerHostName("serverHost");
    settings.setServerPort(3030);
    settings.setServerRefName("serverRefName");
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getServerHostName()}
   * .
   */
  @Test
  public void testGetServerHostName() {
    final String server = settings.getServerHostName();
    assertTrue(server != null);
    assertTrue(!server.equals(""));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getServerPort()}
   * .
   */
  @Test
  public void testGetServerPort() {
    assertTrue(settings.getServerPort() > 0);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getServerRefName()}
   * .
   */
  @Test
  public void testGetServerRefName() {
    final String ref = settings.getServerRefName();
    assertTrue(ref != null);
    assertTrue(!ref.equals(""));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getLicensingServers()}
   * .
   */
  @Test
  public void testGetLicensingServers() {
    final String[] servers = settings.getLicensingServers();
    assertTrue(servers != null);
    assertTrue(servers.length > 0);
    for (String server : servers) {
      assertTrue(server != null);
      assertTrue(!server.equals(""));
      assertTrue(server.trim().equals(server));
    }
  }

}
