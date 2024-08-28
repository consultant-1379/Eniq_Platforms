/**
 * 
 */
package com.ericsson.eniq.dwhm;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.distocraft.dc5000.dwhm.PartitionAction;


/**
 * @author eheijun
 *
 */
public class PartitionActionFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for {@link com.ericsson.eniq.dwhm.PartitionActionFactory#getInstance(ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, java.lang.String, java.util.logging.Logger)}.
   * @throws Exception 
   */
  @Test
  public void testGetInstance() throws Exception {
	final PartitionAction mockPartitionAction = context.mock(PartitionAction.class);
    PartitionActionFactory.setInstance(mockPartitionAction);
    final PartitionAction partitionAction = PartitionActionFactory.getInstance(null, null, null, null,true);
    assertEquals(partitionAction, mockPartitionAction);
  }

}
