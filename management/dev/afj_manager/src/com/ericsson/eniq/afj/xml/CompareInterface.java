/**
 * 
 */
package com.ericsson.eniq.afj.xml;

import java.util.List;

import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author esunbal
 *
 */
public interface CompareInterface {

	List<AFJMeasurementType> getMeasTypeDelta(Object object) throws AFJException;
}
