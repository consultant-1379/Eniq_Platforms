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

package com.ericsson.navigator.esm.util.cssr.io;


import java.util.List;



/**
 * class represents a Charging System element in the charging system topology
 * file.
 * 
 * @author ejammor
 */
public class CSElement extends Element {
	private CSComponents fmComponents = null;
	private CSComponents  pmComponents=null;
	private String snosType = "";
	private boolean nodePMDisable;
	private List<CSRemoteElement> csr = null;
	
	
	/**
	 * @param components
	 *            the components to set
	 */
	public void setFmComponents(final CSComponents fmComponents) {
		this.fmComponents = fmComponents;
	}

	/**
	 * @return the components
	 */
	public CSComponents getFmComponents() {
		return fmComponents;
	}

	
	public String toString() {
		return "type: " + getType() + "\tname: " + getName() + "\tip: "
				+ getIp() + "\tcomm: " + getCommunity() + "\tport: "
				+ getSnmsPort() + "\tprotocol type: " + getProtocolType()
				+ "\tother: " + getOtherIndentifyingInfo() + "\thostname: "
				+ getHostname() + "\tcsNodeName: " ;

	}

	public void setSnosType(final String snosType) {
		// TODO Auto-generated method stub
		this.snosType = snosType;
		
	}
	
	public String getSnosType() {
		// TODO Auto-generated method stub
		return snosType;
		
	}

	public void setPmComponents(final CSComponents pmComponents) {
		this.pmComponents = pmComponents;
	}

	/**
	 * @return the components
	 */
	public CSComponents getPmComponents() {
		return pmComponents;
	}

	public void setNodePMDisable(final boolean nodePMDisable) {
		this.nodePMDisable = nodePMDisable;
		
	}
	public boolean getNodePMDisable() {
		return nodePMDisable;
		
	}
	public List<CSRemoteElement> getCsr() {
		return csr;
	}

	public void setCsr(final List<CSRemoteElement> csr) {
		this.csr = csr;
	}

}
