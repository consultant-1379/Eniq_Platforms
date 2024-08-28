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


/**
 * @author eheijun
 *
 */
@RunWith(JMock.class)
public class TechPackReaderFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.TechPackReaderFactory#getTechPackReader(com.ericsson.eniq.busyhourcfg.database.DatabaseSession)}.
   */
  @Test
  public void testGetTechPackReader() {
    final RockDatabaseSession mockDatabaseSession = context.mock(RockDatabaseSession.class);
    final TechPackReader techPackReader = TechPackReaderFactory.getTechPackReader(mockDatabaseSession);
    assertTrue(techPackReader instanceof RockTechPackReader);
    assertTrue(techPackReader != null);
  }
}
