package com.ericsson.eniq.afj.xml;

import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.exception.AFJException;

public class AFJParserFactory {  
	  
	  private static AFJParser _instance;

	  private AFJParserFactory() {
	  }

	  /**
	   * Get (and create if necessary) parser.
	   * @param techPack
	   * @return
	   */
	  public static AFJParser getParser(final AFJTechPack techPack) throws AFJException{
		  if(_instance == null){
			  _instance = new AFJParser(techPack);
		  }
		  else{
		  _instance.setAfjTechPack(techPack);
		  }
		  return _instance;
	  }

	  /**
	   * Setter for non-default parser
	   * @param instance
	   */
	  public static void setParser(final AFJParser instance) {
	    _instance = instance;
	  }



}
