/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eheijun
 *
 */
public class BusyhourConfigurationFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private BusyhourConfiguration mockBusyhourConfiguration;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockBusyhourConfiguration = context.mock(BusyhourConfiguration.class);
  }
  
	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.config.BusyhourConfigurationFactory#getBusyhourConfiguration()}.
	 */
	@Test
	public void testGetDefaultBusyhourConfiguration() {
    BusyhourConfigurationFactory.setBusyhourConfiguration(null);
	  final BusyhourConfiguration busyhourConfiguration = BusyhourConfigurationFactory.getBusyhourConfiguration();
    assertTrue(busyhourConfiguration instanceof  DefaultBusyhourConfiguration);
    assertTrue(busyhourConfiguration != null);
	}

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.config.BusyhourConfigurationFactory#getBusyhourConfiguration()}.
   */
  @Test
  public void testGetNonDefaultBusyhourConfiguration() {
    BusyhourConfigurationFactory.setBusyhourConfiguration(mockBusyhourConfiguration);
    final BusyhourConfiguration busyhourConfiguration = BusyhourConfigurationFactory.getBusyhourConfiguration();
    assertTrue(busyhourConfiguration instanceof  BusyhourConfiguration);
    assertTrue(busyhourConfiguration == mockBusyhourConfiguration);
  }

}
