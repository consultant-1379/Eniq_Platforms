/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import com.ericsson.eniq.afj.common.AFJTechPack;

/**
 * @author eheijun
 * 
 */
public class TechPackRestorerFactory {
  
  private static TechPackRestorer _instance;

	private TechPackRestorerFactory() {
	}

	/**
	 * Get and create new default restorer instance. 
	 * @param techPack name of the Tech Pack to be restored 
	 * @return the instance
	 */
	public static TechPackRestorer getInstance(final AFJTechPack techPack) {
	  if (_instance == null) {
	    return new DefaultTechPackRestorer(techPack);
	  }
    return _instance;
	}

	/**
	 * Set non default restorer instance
	 * @param instance
	 */
  public static void setInstance(final TechPackRestorer instance) {
    _instance = instance;
  }

}
