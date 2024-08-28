package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import java.util.Hashtable;

/**
 * A TrapMap provides information on howto map pdu values into user defined values.
 * 
 * @author qhapers, qagnkuc
 * 
 */
public class TrapMap {
	
	private final String id;
	private Hashtable<String, String> entries;
	private String defaultValue;
	
	/**
	 * Constructor.
	 * 
	 * @param id Id the name of the map.
	 */
	public TrapMap(final String id) {
		this.id = id;
	}
	
	/**
	 * Returns the map id.
	 * 
	 * @return id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Adds an entry describing map behavior.
	 * 
	 * @param key value pair.
	 */
	public void setEntry(final String key, final String value) {
		if(entries == null) {
			entries = new Hashtable<String, String>();
		}
		entries.put(key, value);
	}
	
	/**
	 * Finds a matching value.
	 * 
	 * @param key.
	 * @return value Value that matches key else default.
	 */
	public String getEntry(final String key) {
		if(entries.containsKey(key)) {
			return entries.get(key);
		} else {
			return getDefaultValue();
		}
	}
	
	/**
	 * Adds a default map behavior.
	 */
	public void setDefaultValue(final String value) {
		defaultValue = value;
	}
	
	/**
	 * Gets the default value for the map.
	 * 
	 * @return defaultValue.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
}
