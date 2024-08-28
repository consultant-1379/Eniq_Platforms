/**
 * 
 */
package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.afj.common.DatabaseLockAction;
import com.ericsson.eniq.afj.common.RockDatabaseLockAction;
import com.ericsson.eniq.afj.common.RockDatabaseLockActionFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author eheijun
 * 
 */
public class RockDatabaseLockActionFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private AFJDatabaseHandler mockDatabaseHandler;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockDatabaseHandler = context.mock(AFJDatabaseHandler.class);
    AFJDatabaseHandler.setInstance(mockDatabaseHandler);
  }
  
  /**
   * @throws Exception
   */
  @After
  public void tearDownAfterClass() throws Exception {
    AFJDatabaseHandler.setInstance(null);
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.common.RockDatabaseLockActionFactory#getInstance()}.
   * 
   * @throws AFJException
   */
  @Test
  public void testGetInstanceInitialization() throws AFJException {
    final RockFactory mockDbaDwhRock = context.mock(RockFactory.class);

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwhRock));
      }
    });

    final DatabaseLockAction databaseLockAction = RockDatabaseLockActionFactory.getDWHInstance();
    assertTrue(databaseLockAction != null);
    assertTrue(databaseLockAction instanceof RockDatabaseLockAction);
  }

}
