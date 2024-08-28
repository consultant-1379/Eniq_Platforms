/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import com.ericsson.eniq.exception.AFJException;

/**
 * @author eheijun
 * 
 */
public interface TechPackRestorer {

	/**
	 * Restores TechPack to it's original state by removing all AFJ(=NVU)
	 * changes.
	 * 
	 * @return
	 * @throws AFJException
	 */
	Boolean restore() throws AFJException;

}
