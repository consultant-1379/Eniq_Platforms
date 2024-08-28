/**
 * 
 */
package com.ericsson.eniq.afj.database;

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
public class AFJDatabaseCommonUtilsFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  private AFJDatabaseCommonUtils mockInstance;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockInstance = context.mock(AFJDatabaseCommonUtils.class);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory#getInstance()}.
   */
  @Test
  public void testGetDefaultInstance() {
    AFJDatabaseCommonUtilsFactory.setInstance(null);
    final AFJDatabaseCommonUtils utils = AFJDatabaseCommonUtilsFactory.getInstance();
    assertFalse(utils == null);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory#getInstance()}.
   */
  @Test
  public void testGetMockInstance() {
    AFJDatabaseCommonUtilsFactory.setInstance(mockInstance);
    final AFJDatabaseCommonUtils utils = AFJDatabaseCommonUtilsFactory.getInstance();
    assertEquals(utils, mockInstance);
  }

}
