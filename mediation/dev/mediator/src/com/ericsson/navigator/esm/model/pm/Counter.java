package com.ericsson.navigator.esm.model.pm;

import java.io.Serializable;
import java.math.BigDecimal;

public interface Counter extends Serializable {

	enum CounterType {
		GAUGE, PEG, MAX, MIN, KPI, INDEX;
		
		@Override
		public String toString() {
			switch(ordinal()){
			case 1:
				return "Peg";
			case 2:
				return "Max";
			case 3:
				return "Min";
			case 4:
				return "Kpi";
			case 5:
				return "Index";
			default:
				return "Gauge";
			}
		}
	};
	
	/**
	 * Retrieve a serializable counter
	 * @return a serializable counter
	 */
	Counter getSerializable();

	/**
	 * Retrieve the counter type.
	 * 
	 * @return The type
	 */
	CounterType getType();

	/**
	 * For a PEG counter this will return false when a calculated delta value is
	 * available using getValue method.
	 * 
	 * @return True if PEG counter has been updated. True for all other
	 *         counters.
	 */
	boolean isValid();
	
	/**
	 * Reset counter values
	 */
	void reset();

	/**
	 * Retrieve the name of the counter.
	 * 
	 * @return The name
	 */
	String getName();

	/**
	 * Retrieve the value.
	 * 
	 * @return The value or if counter type is PEG null if this counter has not
	 *         been update once.
	 */
	BigDecimal getValue();
	
	/**
	 * Retrieve the value as a String
	 * 
	 * @return The value string if the counter is of type gauge.
	 */
	String getValueStr();

	/**
	 * The protocol identifier. For example this represents the OID for SNMP
	 * counters.
	 * 
	 * @return The id
	 */
	String getProtocolIdentifier();

	/**
	 * Used to update a counter with new counter value. If this is a PEG counter
	 * then the delta will be calculate from a previous poll. For GAUGE the
	 * value will simply be replaced.
	 * 
	 * @param The
	 *            counter to update with.
	 */
	void update(Counter c);
	
	/**
	* The data type for the counter. 
	*/
	String getDataType();
}
