/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.common.setWizards.StatsCreateLoaderSet;


/**
 * @author eheijun
 *
 */
public class CreateLoaderSetFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private StatsCreateLoaderSet mockInstance;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockInstance = context.mock(StatsCreateLoaderSet.class);
  }

  /**
   * Test method for {@link com.ericsson.eniq.techpacksdk.CreateLoaderSetFactory#getInstance(java.lang.String, java.lang.String, java.lang.String, java.lang.String, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, int, java.lang.String, boolean)}.
   * @throws Exception 
   */
  @Test
  public void testGetInstance() throws Exception {
    CreateLoaderSetFactory.setInstance(mockInstance);
    final StatsCreateLoaderSet instance = CreateLoaderSetFactory.getInstance(null, null, null, null, null, null, null, null, null);
    assertEquals(instance, mockInstance);
  }

}
