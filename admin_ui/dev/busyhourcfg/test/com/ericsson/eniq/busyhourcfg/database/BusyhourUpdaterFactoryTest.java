/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;


/**
 * @author eheijun
 *
 */
@RunWith(JMock.class)
public class BusyhourUpdaterFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.BusyhourUpdaterFactory#getBusyhourUpdater(com.ericsson.eniq.busyhourcfg.database.DatabaseSession, com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction)}.
   */
  @Test
  public void testGetBusyhourUpdater() {
    final RockDatabaseSession mockDatabaseSession = context.mock(RockDatabaseSession.class);
    final DWHStorageTimeAction mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
    final BusyhourUpdater busyhourUpdater = BusyhourUpdaterFactory.getBusyhourUpdater(mockDatabaseSession, mockStorageTimeAction);
    assertTrue(busyhourUpdater instanceof  RockBusyhourUpdater);
    assertTrue(busyhourUpdater != null);
  }
}
