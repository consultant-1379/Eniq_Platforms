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

/**
 * class represents the most basic element in the charging system topology file.
 * 
 * @author ejammor
 */
public class Element {
	private String name = "";
	private String type = "";
	private String protocolType = "";
	private String ip = "";
	private String otherIdInfo = "";
	private String community = "";
	private String snmsPort = "";
	private String hostname = "";
	private boolean isCluster = false;
	private String cimIdentifyingDescription = "";
	
	/**
	 * @return the isCluster
	 */
	public boolean isCluster() {
		return isCluster;
	}

	/**
	 * @param isCluster
	 *            the isCluster to set
	 */
	public void setCluster(final boolean isCluster) {
		this.isCluster = isCluster;
	}

	/**
	 * Sets element's name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets element's name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the elements type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets the element's type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set's the element's ip
	 * 
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(final String ip) {
		this.ip = ip;
	}

	/**
	 * Get's the element's ip.
	 * 
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * sets the otherIndentifyingInfo for this element. *
	 * 
	 * @param otherIndentifyingInfo
	 *            the otherIndentifyingInfo to set
	 */
	public void setOtherIndentifyingInfo(final String otherIndentifyingInfo) {
		this.otherIdInfo = otherIndentifyingInfo;
	}

	/**
	 * gets the otherIndentifyingInfo for this element.
	 * 
	 * @return the otherIndentifyingInfo
	 */
	public String getOtherIndentifyingInfo() {
		return otherIdInfo;
	}

	/**
	 * sets the community for this element.
	 * 
	 * @param community
	 *            the community to set
	 */
	public void setCommunity(final String community) {
		this.community = community;
	}

	/**
	 * gets the community for this element.
	 * 
	 * @return the community
	 */
	public String getCommunity() {
		return community;
	}

	/**
	 * sets the snmsport for this element.
	 * 
	 * @param snmsPort
	 *            the snmsPort to set
	 */
	public void setSnmsPort(final String snmsPort) {
		this.snmsPort = snmsPort;
	}

	/**
	 * gets the snmsport for this element.
	 * 
	 * @return the snmsPort
	 */
	public String getSnmsPort() {
		return snmsPort;
	}

	/**
	 * Set's the element's protocol type.
	 * 
	 * @param protocolType
	 *            the protocolType to set
	 */
	public void setProtocolType(final String protocolType) {
		this.protocolType = protocolType;
	}

	/**
	 * Get's the element's protocol type.
	 * 
	 * @return the protocolType
	 */
	public String getProtocolType() {
		return protocolType;
	}

	/**
	 * Set's the elements hostname
	 * 
	 * @param hostname
	 *            the hostname to set
	 */
	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Get's the elements hostname.
	 * 
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	public void setCimIdentifyingDescription(final String cimIdentifyingDescription) {
		this.cimIdentifyingDescription = cimIdentifyingDescription;
	}

	public String getCimIdentifyingDescription() {
		return cimIdentifyingDescription;
	}
}
