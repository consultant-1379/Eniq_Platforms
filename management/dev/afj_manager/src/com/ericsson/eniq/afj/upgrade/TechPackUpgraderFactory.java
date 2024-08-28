/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_STN_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IMS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_HSS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MTAS_NAME;

import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author eheijun
 * 
 */
public class TechPackUpgraderFactory {  
  
  private static TechPackUpgrader _instance;

  private TechPackUpgraderFactory() {
  }

  /**
   * Get (and create if necessary) upgrader.
   * @param techPack
   * @return
   */
  public static TechPackUpgrader getUpgrader(final String techpackName) throws AFJException{
	  System.out.println("Inside the TechPackUpgrader");
	  if (techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_STN_NAME))) {
		  _instance = new STNUpgrader();
		  System.out.println("STNUpgrader object returned");
	  }
	  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_BSS_NAME)))){
		  _instance = new BSSUpgrader();
		  System.out.println("BSSUpgrader object returned");
	  }
	  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_IMS_NAME)))){
		  _instance = new IMSUpgrader();
		  System.out.println("IMSUpgrader object returned");
	  }
	  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_HSS_NAME)))){
		  _instance = new HSSUpgrader();
		  System.out.println("HSSUpgrader object returned");
	  }
	  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_MTAS_NAME)))){
		  _instance = new MTASUpgrader();
		  System.out.println("MTASUpgrader object returned");
	  }
	  return _instance;
  }

  /**
   * Setter for non-default upgrader
   * @param upgrader
   */
  public static void setUpgrader(final TechPackUpgrader instance) {
    _instance = instance;
  }

}
