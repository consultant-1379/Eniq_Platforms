/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.navigator.esm.util.cssr.io;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.log4j.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * <p>
 * This class parses the SystemTopolgy xml file to identify the Charging Systems installed in ESM, the relevant cimname
 * values are then written to a log file.
 * 
 * </p>
 * 
 * <p>
 * Using the default constructor in this class will look for a file called SystemTopology.xml specified in the
 * Environment.TOPOLOGYFILE and write the output to a file called CS_uninstall_log.txt. These values can be altered
 * using the appropriate getter/setters.
 * </p>
 * 
 * @author etonayr
 * 
 */
public class CSUninstallUtility extends DefaultHandler {

	private static final Logger LOGGER = Logger.getLogger("CSSR");
	private static final String classname = CSUninstallUtility.class.getName();
	
	/*
	 * Used as the search key during parsing.
	 */
	private String idDescriptionSearchKey;

	/*
	 * Output log file path
	 */
	private String logFilePath;

	/*
	 * Topology File
	 */
	private String topologyFile;

	/*
	 * Will hold the list of CS installed systems found in the topology file
	 */
	private final List<String> installedSystems = new Vector<String>();

	/**
	 * Create a new UnIntall utility with the default settings.
	 */
	public CSUninstallUtility() {
		idDescriptionSearchKey = "ChargingSystemNetwork";
		topologyFile = "/nav/var/esm/repository/SystemTopology.xml";
		logFilePath = "/nav/var/esm/repository/CS_uninstall.txt";
	}

	/**
	 * Parses the system topology xml file.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseXmlFile(final InputStream source) throws ParserConfigurationException, SAXException, IOException {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(source, this);
		exportToLogFile(installedSystems);
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
	        throws SAXException {

		final String cimIdDesc = attributes.getValue(uri, "cimidentifyingdescription");

		if (idDescriptionSearchKey.equals(cimIdDesc)) {
			final String cimname = attributes.getValue(uri, "cimname");
			installedSystems.add(cimname);
		}
	}

	/*
	 * Writes a List of Strings to a text file.
	 */
	private void exportToLogFile(final List<String> systems) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(logFilePath));
			for (final String cimName : systems) {
				writer.println(cimName);
			}
		} catch (final FileNotFoundException e) {
			LOGGER.fatal(classname+ ":exportToLogFile()   Error occurred while exporting log file. Details: " + e.getMessage());
			LOGGER.debug(classname+ ":exportToLogFile()   Caused by: ", e);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	public static void main(final String args[]) {

//		if (args.length != 1 || args[0].equals("")) {
//			System.out.println("Path to system topology path not specified.");
//			System.exit(1);
//		}
		if(args.length != 1 && args.length !=2 || "".equals(args[0])) {
			
			final String message = classname + ":   Expected 1 or 2 arguments got: " + args.length + ", first arg is System Registeration topology file, second (optional) is log file path";
			System.out.println(message);
			LOGGER.error(message);
			System.exit(1);
		}
		final CSUninstallUtility uninstaller = new CSUninstallUtility();
		uninstaller.setTopologyFile(args[0]);
		
		if(args.length == 2) {
			uninstaller.setLogFilePath(args[1]);
		}
		
		try {
			uninstaller.parseXmlFile(new FileInputStream(new File(args[0])));
		} catch (final ParserConfigurationException e) {
			//e.printStackTrace();
			LOGGER.error(classname + ":main() ParserConfigurationException: " + e.getMessage());
		} catch (final SAXException e) {
			//e.printStackTrace();
			LOGGER.error(classname + ":main() SAXException: " + e.getMessage());
		} catch (final IOException e) {
			//e.printStackTrace();
			LOGGER.error(classname + ":main() IOException: " + e.getMessage());
		}
	}

	public void setIdDescriptionSearchKey(final String searchKey) {
		this.idDescriptionSearchKey = searchKey;
	}

	public String getIdDescriptionSearchKey() {
		return idDescriptionSearchKey;
	}

	public void setLogFilePath(final String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setTopologyFile(final String topologyFile) {
		this.topologyFile = topologyFile;
	}

	public String getTopologyFile() {
		return topologyFile;
	}
}
