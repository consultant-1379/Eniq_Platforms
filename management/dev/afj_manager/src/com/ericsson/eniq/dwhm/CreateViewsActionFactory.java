/**
 * 
 */
package com.ericsson.eniq.dwhm;

import com.ericsson.eniq.common.TechPackType;
import com.ericsson.eniq.common.Utils;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.CreateViewsAction;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;

/**
 * @author eheijun
 * 
 */
public class CreateViewsActionFactory {

  private static CreateViewsAction _instance;

  private CreateViewsActionFactory() {
  }

  /**
   * Get or create instance of the CreateViewsAction
   * 
   * @param dbadwh
   * @param dwh
   * @param dwhrep
   * @param dwhType
   * @param log
   * @return
   * @throws Exception
   */
  public static CreateViewsAction getInstance(final RockFactory dbadwh, final RockFactory dwh,
      final RockFactory dwhrep, final Dwhtype dwhType, final Logger log) throws Exception {
    if (_instance != null) {
      return _instance;
    }
    final TechPackType tpt = Utils.getTechPackType(dwhrep, dwhType);
    return new CreateViewsAction(dbadwh, dwh, dwhrep, dwhType, log, tpt);
  }

  /**
   * Set instance of the CreateViewsAction
   * 
   * @param instance
   *          the _instance to set
   */
  public static void setInstance(final CreateViewsAction instance) {
    _instance = instance;
  }

}
