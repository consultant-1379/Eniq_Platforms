/**
 * 
 */
package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.CommonSetGeneratorFactory;


/**
 * @author eheijun
 *
 */
public class CommonSetGeneratorFactoryTest {

  private final Mockery context = new JUnit4Mockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  private CommonSetGenerator mockInstance;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockInstance = context.mock(CommonSetGenerator.class);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.CommonSetGeneratorFactory#getInstance(java.lang.String, java.lang.String, java.lang.String, java.lang.String, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, java.lang.Integer, java.lang.String, java.lang.Boolean)}.
   * @throws Exception 
   */
  @Test
  public void testGetInstance() throws Exception {
    CommonSetGeneratorFactory.setInstance(mockInstance);
    final CommonSetGenerator testInstance = CommonSetGeneratorFactory.getInstance(null, null, null, null, null, null, null, null);
    assertEquals(testInstance, mockInstance);
  }

}
