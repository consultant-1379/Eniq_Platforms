package com.ericsson.navigator.esm.util.cssr.io;

/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2010
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.List;

/**
 * class represents a Charging System in the charging system topology file.
 * 
 * @author ejammor
 */
public class CSSystem extends Element {

	private List<CSElement> elements = null; // NOPMD
	private List<String> hierarchy = null; // NOPMD
  
	/**
	 * Creates a new instance of <code>CSSystem</code>
	 * 
	 */
	public CSSystem() {
		elements = new ArrayList<CSElement>();
		hierarchy = new ArrayList<String>();
		this.setProtocolType("TXF");
	}

	private int counter=0;
	
	/**
	 * System may have a hierarchy, this method lets user request levels
	 * as a sequence over multiple calls
	 * @return next level in this systems hierarchy or null if finished
	 */
	public String getNextHierarchy() {
		return nextHierarchyHelper(true);
	}
	
	public int getHierarchyLevel() {
		return counter;
	}
	
	public String peekNextHierarchy() {
		return nextHierarchyHelper(false);
	}
	
	//TODO probably better to implement hierarchy via Stack if we dont need
	//     to hold onto the levels, doing it this way for now to make debugging easier
	private String nextHierarchyHelper(final boolean increment) {
	String next = null;
		
		if(counter<hierarchy.size()) {
			next = hierarchy.get(counter);
			if(increment) {
				counter++;
			}
		}
		return next;
	}
	
	/**
	 * Adds the String to the system's hierarchy list.
	 * 
	 * @param String
	 */
	public void addHierarchy(final String level) {
		hierarchy.add(level);
	}
	
	/**
	 * Adds the element to the system's list of elements.
	 * 
	 * @param element
	 *            the elements to set
	 */
	public void addElement(final CSElement element) {
		elements.add(element);
	}

	/**
	 * Gets the elements that the system contains.
	 * 
	 * @return the elements
	 */
	public List<CSElement> getElements() {
		return elements;
	}
}