/**
 * 
 */
package com.ericsson.eniq.dwhm;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.distocraft.dc5000.dwhm.VersionUpdateAction;


/**
 * @author eheijun
 *
 */
public class VersionUpdateActionFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for {@link com.ericsson.eniq.dwhm.VersionUpdateActionFactory#getInstance(ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, java.lang.String, java.util.logging.Logger)}.
   * @throws Exception 
   */
  @Test
  public void testGetInstance() throws Exception {
	final VersionUpdateAction mockVersionUpdateAction = context.mock(VersionUpdateAction.class);
    VersionUpdateActionFactory.setInstance(mockVersionUpdateAction);
    final VersionUpdateAction versionUpdateAction = VersionUpdateActionFactory.getInstance(null, null, null, null);
    assertEquals(versionUpdateAction, mockVersionUpdateAction);
  }

}
