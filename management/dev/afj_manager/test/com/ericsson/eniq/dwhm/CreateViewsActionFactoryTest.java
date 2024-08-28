/**
 * 
 */
package com.ericsson.eniq.dwhm;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import com.distocraft.dc5000.dwhm.CreateViewsAction;


/**
 * @author eheijun
 *
 */
public class CreateViewsActionFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  /**
   * Test method for {@link com.ericsson.eniq.dwhm.CreateViewsActionFactory#getInstance(ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, com.distocraft.dc5000.repository.dwhrep.Dwhtype, java.util.logging.Logger)}.
   * @throws Exception 
   */
  @Test
  public void testGetMockInstance() throws Exception {
	final CreateViewsAction mockCreateViewsAction = context.mock(CreateViewsAction.class);
    CreateViewsActionFactory.setInstance(mockCreateViewsAction);
    final CreateViewsAction createViewsAction = CreateViewsActionFactory.getInstance(null, null, null, null, null);
    assertEquals(createViewsAction, mockCreateViewsAction);
  }

}
