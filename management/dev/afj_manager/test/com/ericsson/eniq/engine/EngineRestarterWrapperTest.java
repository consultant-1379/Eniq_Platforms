/**
 * 
 */
package com.ericsson.eniq.engine;

import org.junit.Test;

import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class EngineRestarterWrapperTest {

  /**
   * Test method for {@link com.ericsson.eniq.engine.EngineRestarterWrapper#execute()}.
   * @throws AFJException 
   */
  @Test
  public void testExecute() throws AFJException {
    if (EngineRestarterWrapper.isExecutionAllowed()) {
      EngineRestarterWrapper.setExecutionAllowed(false);
    }
    EngineRestarterWrapper.execute();
  }

}
