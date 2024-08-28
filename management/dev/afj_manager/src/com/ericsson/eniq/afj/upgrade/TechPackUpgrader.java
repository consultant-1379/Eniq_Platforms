/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public interface TechPackUpgrader {
  
  String upgrade(AFJDelta delta) throws AFJException;

}
