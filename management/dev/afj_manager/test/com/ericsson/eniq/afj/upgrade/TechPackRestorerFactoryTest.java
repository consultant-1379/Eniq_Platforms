/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJTechPack;

/**
 * @author eheijun
 * 
 */
public class TechPackRestorerFactoryTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private TechPackRestorer mockTechPackRestorer;
  
  private AFJTechPack mockTechPack;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockTechPack = context.mock(AFJTechPack.class);
    mockTechPackRestorer = context.mock(TechPackRestorer.class);
  }
  
  @After
  public void tearDown() throws Exception {
    TechPackRestorerFactory.setInstance(null);
  }

	/**
	 * Test method for
	 * {@link com.ericsson.eniq.afj.upgrade.TechPackRestorerFactory#getRestorer()}
	 * .
	 */
	@Test
	public void testGetMockInstance() {
    TechPackRestorerFactory.setInstance(mockTechPackRestorer);
		final TechPackRestorer techPackRestorer = TechPackRestorerFactory.getInstance(mockTechPack);
		assertTrue(techPackRestorer != null);
		assertFalse(techPackRestorer instanceof DefaultTechPackRestorer);
	}

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.upgrade.TechPackRestorerFactory#getRestorer()}
   * .
   */
  @Test
  public void testGetDefaultInstance() {
    TechPackRestorerFactory.setInstance(null);
    final TechPackRestorer techPackRestorer = TechPackRestorerFactory.getInstance(mockTechPack);
    assertTrue(techPackRestorer != null);
    assertTrue(techPackRestorer instanceof DefaultTechPackRestorer);
  }

}
