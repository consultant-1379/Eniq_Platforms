package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer;

/**
 * A class that holds constants that the composer is dependent on.
 * 
 * @author qbacfre
 * 
 */
public class ComposerConstants {

	/**
	 * Variables that exist in the composer.
	 * 
	 * @author qbacfre
	 * 
	 */
	public enum Variables {
		AGENTADDRESS, ENTERPRISE, GENERICTYPE, SPECIFICTYPE, UPTIME, OID
	};

	public static final String VARIABLE_CHARACTER = "@";
	public static final String INDEX_CHARACTER = "$";
	public static final String EXPRESSION_START = "{";
	public static final String EXPRESSION_END = "}";
}
