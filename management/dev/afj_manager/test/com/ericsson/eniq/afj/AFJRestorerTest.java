/**
 * 
 */
package com.ericsson.eniq.afj;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.exception.AFJConfiguationException;
import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class AFJRestorerTest {
  
  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  private AFJManager mockAfjManager;

  private AFJTechPack mockAFJTechPack;

  @Before
  public void setUp() throws AFJException, AFJConfiguationException {
    mockAfjManager = context.mock(AFJManager.class);
    mockAFJTechPack = context.mock(AFJTechPack.class);
    AfjManagerFactory.setInstance(mockAfjManager);
  }

  @After
  public void tearDown() {
    AfjManagerFactory.setInstance(null);    
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJRestorer#main(java.lang.String[])}.
   * @throws AFJConfiguationException 
   * @throws AFJException 
   */
  @Test
  public void testMain() throws AFJException, AFJConfiguationException {
    context.checking(new Expectations() {
      {
        allowing(mockAfjManager).getAFJTechPack(with(any(String.class)));
        will(returnValue(mockAFJTechPack));
        allowing(mockAfjManager).restoreAFJTechPack(mockAFJTechPack);
        will(returnValue(true));
      }
    });
    
    final String[] args = {"DC_E_DUMMY"};
    AFJRestorer.main(args);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJRestorer#main(java.lang.String[])}.
   * @throws AFJConfiguationException 
   * @throws AFJException 
   */
  @Test
  public void testMainFail() throws AFJException, AFJConfiguationException {
    context.checking(new Expectations() {
      {
        allowing(mockAfjManager).getAFJTechPack(with(any(String.class)));
        will(returnValue(mockAFJTechPack));
        allowing(mockAfjManager).restoreAFJTechPack(mockAFJTechPack);
        will(returnValue(false));
      }
    });
    
    final String[] args = {"DC_E_DUMMY"};
    AFJRestorer.main(args);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJRestorer#main(java.lang.String[])}.
   * @throws AFJConfiguationException 
   * @throws AFJException 
   */
  @Test
  public void testMainNonAFJTechPack() throws AFJException, AFJConfiguationException {
    context.checking(new Expectations() {
      {
        allowing(mockAfjManager).getAFJTechPack(with(any(String.class)));
        will(throwException(new AFJException("SOME ERROR MESSAGE")));
      }
    });
    final String[] args = {"DC_E_DUMMY"};
    AFJRestorer.main(args);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJRestorer#main(java.lang.String[])}.
   */
  @Test
  public void testMainArgMissing() {
	  final String[] args = {};
    AFJRestorer.main(args);
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJRestorer#main(java.lang.String[])}.
   */
  @Test
  public void testMainTooManyArgs() {
	  final String[] args = {"-t", "DC_E_DUMMY"};
    AFJRestorer.main(args);
  }
  

}
