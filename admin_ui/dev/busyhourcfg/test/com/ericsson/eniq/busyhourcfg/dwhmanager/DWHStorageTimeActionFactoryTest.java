/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.dwhmanager;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;


/**
 * @author eheijun
 *
 */
@RunWith(JMock.class)
public class DWHStorageTimeActionFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private RockDatabaseSession mockDatabaseSession;

  private DWHStorageTimeAction mockDWHStorageTimeAction;
  
  /**
   * 
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    final RockFactory mockDwhRockFactory = context.mock(RockFactory.class, "DWH");
    final RockFactory mockDwhrepRockFactory = context.mock(RockFactory.class, "DWHREP");
    mockDatabaseSession = context.mock(RockDatabaseSession.class);
    mockDWHStorageTimeAction = context.mock(DWHStorageTimeAction.class);
    
    context.checking(new Expectations() {
      {
        // set values for database session mock 
        allowing(mockDatabaseSession).getDwhRockFactory();
        will(returnValue(mockDwhRockFactory));
        allowing(mockDatabaseSession).getDwhrepRockFactory();
        will(returnValue(mockDwhrepRockFactory));
      }
    });
  }
  

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeActionFactory#createDWHStorageTimeAction(ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory)}.
   */
  @Test
  public void testCreateDefaultDWHManagerConnector() {
    
    DWHStorageTimeActionFactory.setDWHStorageTimeAction(null);
    
    try {
      final DWHStorageTimeAction connector = DWHStorageTimeActionFactory.getDWHStorageTimeAction(mockDatabaseSession);
      assertTrue(connector instanceof DefaultDWHStorageTimeAction);
      assertTrue(connector != null);
    } catch (DWHManagerException e) {
      fail("Creating dwhmanager connector failed: " + e.getMessage());
    }
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeActionFactory#createDWHStorageTimeAction(ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory)}.
   */
  @Test
  public void testCreateNonDefaultDWHManagerConnector() {
    
    DWHStorageTimeActionFactory.setDWHStorageTimeAction(mockDWHStorageTimeAction);
    
    try {
      final DWHStorageTimeAction connector = DWHStorageTimeActionFactory.getDWHStorageTimeAction(mockDatabaseSession);
      assertFalse(connector instanceof DefaultDWHStorageTimeAction);
      assertTrue(connector != null);
    } catch (DWHManagerException e) {
      fail("Creating dwhmanager connector failed: " + e.getMessage());
    }
  }
  
}
