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

import com.ericsson.eniq.common.setWizards.StatsCreateTPDirCheckerSet;


/**
 * @author eheijun
 *
 */
public class CreateTPDirCheckerSetFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private StatsCreateTPDirCheckerSet mockInstance;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockInstance = context.mock(StatsCreateTPDirCheckerSet.class);
  }

  /**
   * Test method for {@link com.ericsson.eniq.techpacksdk.CreateTPDirCheckerSetFactory#getInstance(java.lang.String, java.lang.String, java.lang.String, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, java.lang.Long, java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testGetInstance() throws Exception {
    CreateTPDirCheckerSetFactory.setInstance(mockInstance);
    final StatsCreateTPDirCheckerSet instance = CreateTPDirCheckerSetFactory.getInstance(null, null, null, null, null, null, null);
    assertEquals(instance, mockInstance);
  }

}
