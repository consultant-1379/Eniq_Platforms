package com.ericsson.navigator.esm.model.conversion;

import java.util.Map;
import java.io.Serializable;

public interface Conversion {
	/**
	 *  Implementations of the Conversion interface converts data for an alarm or counterset.
	 *  <p>
	 *  Add new properties, change existing properties and return the properties list 
	 *  either as the complete list or just a list of changed/added properties. 
	 *  To suppress an alarm or counterset return null. 
	 */
	Map<String,Serializable> convert(Map<String,Serializable> properties);
}
