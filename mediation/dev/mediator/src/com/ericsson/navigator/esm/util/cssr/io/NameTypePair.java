package com.ericsson.navigator.esm.util.cssr.io;

/**
 * Stores two Strings representing cimname and snostype
 * @author ejammor
 */
public class NameTypePair {
	
	private String name = null;
	private String type = null;
	
	public NameTypePair(final String[] bits) {
		setName(bits[1]);
		setType(bits[0]);
	}

	private void setName(final String n) {
		this.name = n;
	}
	
	private void setType(final String t) {
		this.type = t;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		   return "name: " + getName() + "\ttype: " + getType();
	   }
}
