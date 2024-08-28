/**
 * 
 */
package com.ericsson.eniq.dwhm;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.distocraft.dc5000.dwhm.StorageTimeAction;


/**
 * @author eheijun
 *
 */
public class StorageTimeActionFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for {@link com.ericsson.eniq.dwhm.StorageTimeActionFactory#getInstance(ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, java.lang.String, java.util.logging.Logger, java.lang.String)}.
   * @throws Exception 
   */
  @Test
  public void testGetInstance() throws Exception {
	final StorageTimeAction mockStorageTimeAction = context.mock(StorageTimeAction.class);
    StorageTimeActionFactory.setInstance(mockStorageTimeAction);
    final StorageTimeAction storageTimeAction = StorageTimeActionFactory.getInstance(null, null, null, null, null, null, null,true);
    assertEquals(storageTimeAction, mockStorageTimeAction);
  }

}
