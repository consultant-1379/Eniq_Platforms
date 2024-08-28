package com.ericsson.eniq.afj.xml;

import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_HSS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_IMS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_MTAS_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_STN_NAME;

import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.exception.AFJException;

public class AFJComparatorFactory {  
	  
	  private static CompareInterface _instance;

	  private AFJComparatorFactory() {
	  }

	  /**
	   * Get (and create if necessary) comparator.
	   * @param techPack
	   * @return
	   */
	  public static CompareInterface getComparator(final String techpackName) throws AFJException{
		  System.out.println("Inside the AFJComparatorFactory");
		  if (techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_STN_NAME))) {
			  _instance = new STNComparator();
			  System.out.println("STNComparator object returned");
		  }
		  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_BSS_NAME)))){
			  _instance = new BSSComparator();
			  System.out.println("BSSComparator object returned");
		  }
		  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_IMS_NAME)))){
			  _instance = new IMSComparator();
			  System.out.println("IMSComparator object returned");
		  }
		  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_HSS_NAME)))){
			  _instance = new HSSComparator();
			  System.out.println("HSSComparator object returned");
		  }
		  else if((techpackName.equalsIgnoreCase(PropertiesUtility.getProperty(PROP_MTAS_NAME)))){
			  _instance = new MTASComparator();
			  System.out.println("MTASComparator object returned");
		  }
		  return _instance;
	  }

	  /**
	   * Setter for non-default comparator
	   * @param upgrader
	   */
	  public static void setComparator(final CompareInterface instance) {
	    _instance = instance;
	  }

}
