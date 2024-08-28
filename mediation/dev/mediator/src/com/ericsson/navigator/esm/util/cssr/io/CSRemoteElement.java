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

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;



/**
 * class represents a Charging System element in the charging system topology
 * file.
 * 
 * @author ejammor
 */
public class CSRemoteElement extends Element {
	private static final String BASEDIR = "/nav/etc/custom/esm/plugins";
	private String csNodeName = null;
	transient private String pluginDir = null;
	transient private String rop;
	transient private String rProtocolType;
	private String offset;
	private String userName = null;
	private int port = 22;
	private String remoteDir = null;
	private String subDir = null;
	private String fileNamePattern = null;
	private String mainClass = "";
	transient private List<ArrayList<String>> countersetRegexp = new ArrayList<ArrayList<String>>();
	final static private transient String EMPTYSTR="";
	private boolean nodePMDisable;
	private String filePerDay = null;
	private String maxFileTransfersPerRop = null;
	private List<CSRemoteElement> csr = new ArrayList<CSRemoteElement>();
	/**
	 * @return the filePerDay
	 */
	public String getFilePerDay() {
		return filePerDay;
	}

	/**
	 * @param filePerDay
	 *            the filePerDay to set
	 */
	public void setFilePerDay(final String filePerDay) {
		this.filePerDay = filePerDay;
	}

	/**
	 * @return the maxFileTransfersPerRop
	 */
	public String getMaxFileTransfersPerRop() {
		return maxFileTransfersPerRop;
	}

	/**
	 * @param maxFileTransfersPerRop
	 *            the maxFileTransfersPerRop to set
	 */
	public void setMaxFileTransfersPerRop(final String maxFileTransfersPerRop) {
		this.maxFileTransfersPerRop = maxFileTransfersPerRop;
	}

		public String toString() {
		return "type: " + getType() + "\tname: " + getName() + "\tip: "
				+ getIp() + "\tcomm: " + getCommunity() + "\tport: "
				+ getSnmsPort() + "\tprotocol type: " + getProtocolType()
				+ "\tother: " + getOtherIndentifyingInfo() + "\thostname: "
				+ getHostname() + "\tcsNodeName: " + csNodeName
				+ "\tpluginDir: " + pluginDir + "\tROP: " + rop
				+ "\tProtocolType: " + rProtocolType + "\tOffset: " + offset
				+ "\tuser: " + userName + "\tport: "
				+ port + "\tremoteDir: " + remoteDir
				+ "\tremoteSubDirectories: " + subDir + "\tfileNamePattern: "
				+ fileNamePattern + "\tMainClass: " + mainClass
				+ "\tremoteFileCounterSet: " + countersetRegexp
				+ "\tfilePerDay: " + filePerDay
				+ "\tmaxFileTransfersPerRop: " + maxFileTransfersPerRop;

	}

	/**
	 * @param components
	 *            the components to set
	 */
	/*public void setComponents(final CSComponents components) {
		this.components = components;
	}*/

	/**
	 * @return the components
	 */
	/*public CSComponents getComponents() {
		return components;
	}*/

	public void setCsNodeName(final String csNodeName) {
		this.csNodeName = csNodeName;
	}

	public String getCsNodeName() {
		return csNodeName;
	}

	public void setpluginDir(final String pluginDir) {
		this.pluginDir = pluginDir;
	}

	public String getpluginDir() {
		return pluginDir;
	}

	public void setROP(final String rop) {
		this.rop = rop;
	}

	public String getROP() {
		return rop;
	}

	public String getRemoteProtocolType() {
		return rProtocolType;
	}

	public void setRemoteProtocolType(final String rProtocolType) {
		this.rProtocolType = rProtocolType;
	}

	public void setOffset(final String offset) {
		this.offset = offset;
	}

	public String getOffset() {
		return offset;
	}
	public void setRemoteCountersetRegexpPair(
			final List<ArrayList<String>> countersetRegexp) {

		this.countersetRegexp = countersetRegexp;
	}

	public List<ArrayList<String>> getRemoteCountersetRegexpPair() {
		return countersetRegexp;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * @param remoteDir
	 *            the remoteDir to set
	 */
	public void setRemoteDir(final String remoteDir) {
		this.remoteDir = remoteDir;
	}

	/**
	 * @param subDir
	 *            the subDir to set
	 */
	public void setSubDir(final String subDir) {
		this.subDir = subDir;
	}

	/**
	 * @param fileNamePattern
	 *            the fileNamePattern to set
	 */
	public void setFileNamePattern(final String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	/**
	 * @return
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * @return
	 */
	public String setMainClass(final String mainClass) {
		return this.mainClass = mainClass;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the subDir
	 */
	public String getSubDir() {
		return subDir;
	}

	/**
	 * @return the fileNamePattern
	 */
	public String getFileNamePattern() {
		return fileNamePattern;
	}

	/**
	 * @return the remoteDir
	 */
	public String getRemoteDir() {
		return remoteDir;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public boolean isValid() {
		return (userName != null || remoteDir != null || fileNamePattern != null);
	}

	public void createPropertiesFile()  {
		try {
			if (isValid()) {
				final Properties prop = new Properties();
				final String parserClass = getMainClass();
				final String subDirectory = getSubDir();
				final String filePerDayProp = getFilePerDay();
				final String maxFileTransfersPerRopProp = getMaxFileTransfersPerRop();
				prop.setProperty("user", getUserName());
				prop.setProperty("port", EMPTYSTR + getPort());
				prop.setProperty("remoteDirectory", getRemoteDir());
				prop.setProperty("fileNamePattern", getFileNamePattern());
				if (parserClass != null) {
					prop.setProperty("mainClass", parserClass);
				}
				if (subDirectory != null) {
					prop.setProperty("remoteSubDirectory", getSubDir());
				}
				if(filePerDayProp !=null){
					prop.setProperty("filePerDay", getFilePerDay());
				}
				if(maxFileTransfersPerRopProp !=null){
					prop.setProperty("MaxFileTransfersPerRop", getMaxFileTransfersPerRop());
				}

				createPluginDirectoryIfNeeded(BASEDIR + File.separator
						+ getpluginDir());
				final String message = "CSElement: createPropertiesFile() "
						+ "Creating config.xml file  : '" + BASEDIR
						+ File.separator + getpluginDir() + File.separator
						+ "config.xml";
				System.out.println(message);

				/*
				 * System.out.println("Creating config.xml file :" + BASEDIR +
				 * File.separator + getpluginDir() + File.separator +
				 * "config.xml");
				 */
				final FileOutputStream fos = new FileOutputStream(BASEDIR
						+ File.separator + getpluginDir() + File.separator
						+ "config.xml");
				prop.storeToXML(fos, "Charging System Integration");
				fos.close();
			} else {
				/*
				 * System.out.println(
				 * "Cannot create config.xml file RemoteFetchElement is not valid. Mandatory parameters are missing!"
				 * );
				 */

				final String message = "CSElement: createPropertiesFile() "
						+ "Cannot create config.xml file RemoteFetchElement is not valid. Mandatory parameters are missing!";
				System.out.println(message);
			}
		} catch (final IOException e) {
			// e.printStackTrace();
			final String message = "CSElement: createPropertiesFile() "
					+ "Cannot create config.xml file " + e.getMessage();
			System.out.println(message);
		}
	}

	private void createPluginDirectoryIfNeeded(final String configDirName) {
		final File configDir = new File(configDirName);

		// if the directory does not exist, create it
		if (!configDir.exists()) {
			// System.out.println("Creating plugin directory: " +
			// configDirName);
			final String message = "CSElement: createPluginDirectoryIfNeeded() "
					+ "Creating plugin directory: " + configDirName;
			System.out.println(message);
			configDir.mkdir();
		}
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
