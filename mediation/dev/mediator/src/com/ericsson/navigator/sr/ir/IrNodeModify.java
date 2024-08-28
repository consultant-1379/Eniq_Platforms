/**
 * 
 */
package com.ericsson.navigator.sr.ir;

import java.util.List;

import com.ericsson.navigator.sr.util.Status;

/**
 * @author epkfjo
 *
 */
public interface IrNodeModify {
	
	Status addSystems(List<SNOSNE> systems);
	void sortSystems();
}
