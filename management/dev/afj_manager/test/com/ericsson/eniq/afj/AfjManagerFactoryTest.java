/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.exception.AFJConfiguationException;
import com.ericsson.eniq.exception.AFJException;



/**
 * @author eheijun
 *
 */
public class AfjManagerFactoryTest {
  
  private static class DummyAfjManager implements AFJManager {

    /*
     * (non-Javadoc)
     * @see com.ericsson.eniq.afj.AFJManager#getAFJDelta(com.ericsson.eniq.common.AFJTechPack)
     */
    @Override
    public AFJDelta getAFJDelta(final AFJTechPack arg0) throws AFJException {
      return null;
    }

    /*
     * (non-Javadoc)
     * @see com.ericsson.eniq.afj.AFJManager#getAFJDelta(com.ericsson.eniq.common.AFJTechPack)
     */
    @Override
    public List<AFJTechPack> getAFJTechPacks() throws AFJException {
      return null;
    }

    /*
     * (non-Javadoc)
     * @see com.ericsson.eniq.afj.AFJManager#getAFJTechPack(java.lang.String)
     */
    @Override
    public AFJTechPack getAFJTechPack(final String techPackName) throws AFJException, AFJConfiguationException {
      return null;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ericsson.eniq.afj.AFJManager#getAFJDelta(com.ericsson.eniq.common.AFJTechPack)
     */
    @Override
    public String upgradeAFJTechPack(final AFJDelta arg0) throws AFJException {
      return null;
    }

    /*
     * (non-Javadoc)
     * @see com.ericsson.eniq.afj.AFJManager#restoreAFJTechPack(com.ericsson.eniq.common.AFJTechPack)
     */
    @Override
    public Boolean restoreAFJTechPack(final AFJTechPack techPack) throws AFJException {
      return null;
    }

  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AfjManagerFactory#getInstance()}.
   */
  @Test
  public void testGetInstance() {
    try {
      
      AfjManagerFactory.setInstance(new DummyAfjManager());
      final AFJManager afjManager = AfjManagerFactory.getInstance();
      assertTrue(afjManager != null); 
      assertTrue(afjManager instanceof DummyAfjManager); 
    } catch (AFJException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AfjManagerFactory#getInstance()}.
   */
  @Test
  public void testGetNewInstance() {
    try {
      
      AfjManagerFactory.setInstance(null);
      final AFJManager afjManager = AfjManagerFactory.getInstance();
      assertTrue(afjManager != null); 
      assertTrue(afjManager instanceof AFJManagerImpl); 
    } catch (AFJException e) {
      fail(e.getMessage());
    }
  }

}
