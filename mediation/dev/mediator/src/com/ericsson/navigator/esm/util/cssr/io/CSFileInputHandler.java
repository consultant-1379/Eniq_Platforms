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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.util.cssr.CSSR;

/**
 * Parser for the Charging System Node template files.
 * 
 * @author ejammor
 */
public class CSFileInputHandler {

	public static final Logger logger = Logger.getLogger("CSFileInputHandler");

	public CSComponents parseComponentsFile(final String inputFileName)
			throws IOException, UserInputException {

		final CSComponents components = new CSComponents();

		final BufferedReader in = new BufferedReader(new FileReader(
				inputFileName));

		final HashSet<String> names = new HashSet<String>();
		String line = in.readLine();
		String[] bits = null;
		String message = null;
		NameTypePair pair = null;
		boolean doingCompoments = false;

		while ((line != null)) {
			if (!line.startsWith("#") && !"".equals(line)) { // checking for
																// comment
				line = line.trim();

				if (line.indexOf("=") > 0) {
					bits = line.split("=");
					if (bits.length != 2) {
						message = "expected hierarchy line to be <SNOSTYPE>=<CIMNAME> got: "
								+ line;
						logger.error("CSFileInputHandler:parseFMComponentsFile() " + message);
						throw new UserInputException(message);
					}
					if (doingCompoments) {
						message = "unexpected hierarchy line in component section: "
								+ line;
						logger.error("CSFileInputHandler:parseFMComponentsFile() " + message);
						throw new UserInputException(message);
					}

					pair = new NameTypePair(bits);// NOPMD
					components.addHierarchy(pair);
				} else {
					doingCompoments = true;

					bits = line.split(",");
					if (bits.length != 2) {
						message = "expected component line to be <SNOSTYPE>=<CIMNAME> got: "
								+ line;
						logger.error("CSFileInputHandler:parseFMComponentsFile() " + message);
						throw new UserInputException(message);
					}
					pair = new NameTypePair(bits);// NOPMD
					if (names.contains(pair.getName())) {
						message = "component of name : '"
								+ pair.getName()
								+ " already created: "
								+ inputFileName;
						logger.error("CSFileInputHandler:parseFMComponentsFile() " + message);
						throw new UserInputException(message);
					} 
						names.add(pair.getName());
						components.addComponent(pair);
				}
			}
			line = in.readLine();
		}
		return components;
	}

	/**
	 * parses the template file to create the topology file elements.
	 * 
	 * @param pmComponents
	 * 
	 * @param String
	 *            name of input file
	 * @param String
	 *            node type to generate
	 * @return List of CSSystems
	 * @throws IOException
	 * @throws UserInputException
	 */
	public List<CSSystem> parseInputFile(final String inputFileName,
			final String nodeTypeToGenerate, final CSComponents fmComponents,
			final CSComponents pmComponents) throws IOException,
			UserInputException {

		final List<CSSystem> systems = parseInputFileHelper(inputFileName,
				nodeTypeToGenerate, fmComponents, pmComponents);

		// For creating pm topology elements and reading NodeRemoteFileFetchDetails.txt
		// file
		if (CSSR.pm_enable.equalsIgnoreCase("true")) {
			handlePmEnabled(nodeTypeToGenerate, systems);
		}

		return systems;
	}

	private void handlePmEnabled(final String nodeTypeToGenerate,
			final List<CSSystem> systems) throws IOException,
			UserInputException {
		
		final String remotePropFilePrefix = nodeTypeToGenerate;
		String remoteFileName = System.getProperty(
				remotePropFilePrefix + "_remoteFileDetails",
				"/nav/opt/si/" + remotePropFilePrefix.toLowerCase() + "/etc/NodeRemoteFileFetchDetails.txt");
		if(nodeTypeToGenerate.equalsIgnoreCase("OCMP")){
			remoteFileName = System.getProperty(
					remotePropFilePrefix + "_remoteFileDetails",
					"/nav/opt/si/ivr/etc/OCMP_NodeRemoteFileFetchDetails.txt");
		}
		
		for (int i = 0; i < systems.size(); i++) {
			new RemoteFileFetchDetailsReader().parseInputFile(// NOPMD
					remoteFileName, systems.get(i).getElements());
		}
	}

	private List<CSSystem> parseInputFileHelper(final String inputFileName,
			final String nodeTypeToGenerate, final CSComponents fmcomponents,
			final CSComponents pmComponents) throws IOException,
			UserInputException {

		final BufferedReader in = new BufferedReader(new FileReader(
				inputFileName));

		final ArrayList<CSSystem> systems = new ArrayList<CSSystem>();
		final HashSet<String> names = new HashSet<String>();
		String line = in.readLine();
		CSSystem sys = null;

		// want to check all definitions belong to the same charging system
		String firstChargingSystem = null;

		// want to determine when switching from element def to system def
		boolean haveElements = false;

		while ((line != null)) {
			line = line.trim();
			
			if (!line.startsWith("#") && !"".equals(line)) { // checking for
																// comment

				if (line.indexOf("=") > 0) {

					if (haveElements || sys == null) {

						if (firstChargingSystem == null) {
							firstChargingSystem = line;
						} else {
							if (!firstChargingSystem.equals(line)) {
								final String msg = "multiple"
										+ " charging systems defined: "
										+ firstChargingSystem + ", " + line
										+ ". Check template file!";
								logger
										.error("CSFileInputHandler:parseInputFileHelper()"
												+ msg);
								throw new UserInputException(msg);
							}
						}

						final String name = line.substring(
								line.indexOf("=") + 1, line.length());
						sys = new CSSystem();// NOPMD
						sys.setName(name);
						systems.add(sys);
						haveElements = false;
						names.clear();
					}
					sys.addHierarchy(line);
				} else {
					haveElements = true;
					buildCSElement(line, nodeTypeToGenerate, sys, fmcomponents,
							pmComponents, names);
				}
			}
			line = in.readLine();
		}
		return systems;
	}

	private boolean validName(final String name, final Collection<String> names)
			throws UserInputException {
		if (names.contains(name)) {
			final String message = "node of name : '"
					+ name + " already created!";
			logger.error("CSUserInputHandler:userCreateSystem() " + message);
			throw new UserInputException(message);
		}
		return true;
	}
	
	private void validateAndSetSDPParams(final CSElement el,final String[] params, final String message)
			throws UserInputException {

		if (params.length == 3 || params.length == 5) {
			if (params.length == 3 && params[2].equalsIgnoreCase("cluster")) {
				el.setCluster(true);
			} else {
				el.setCluster(false);
			}
		} else {
			final String msg = message + " expected 3 or 5" + " got "
					+ params.length;
			logger.error(msg);
			throw new UserInputException(msg);
		}
	}
	
	private CSElement createSDPCSElement(final String[] params,
			final String nodeTypeToGenerate, final int offset, final CSComponents fmcomponents, final CSComponents pmComponents)
			throws UserInputException {

		CSElement el = new CSElement();
		final String message = "createSDPCSElement() validating params to create "
				+ nodeTypeToGenerate + " node,";
		validateAndSetSDPParams(el, params, message);
		if (el.isCluster()) {
			final String[] bitsFromSDP = { params[0], params[1] };
			el = createCSElement(bitsFromSDP, nodeTypeToGenerate, offset);
			setFMandPMComponents(fmcomponents, pmComponents, el);
		} else {
			el = createSNMPCSElement(params, 5, nodeTypeToGenerate, offset);
			el.setProtocolType("SNMP");
		}
		return el;
	}

	private void buildCSElement(final String line,
			final String nodeTypeToGenerate, final CSSystem sys,
			final CSComponents fmcomponents, final CSComponents pmComponents,
			final Collection<String> names) throws UserInputException {
		String[] bits = null;
		CSElement el = null;

		if ("CCN".equals(nodeTypeToGenerate) || "IVR".equals(nodeTypeToGenerate) || "OCMP".equals(nodeTypeToGenerate)) {
			// adding fdn in the template file. [fdn removed sprint 9]
			bits = line.split(",", 5);
			el = createSNMPCSElement(bits, 4, nodeTypeToGenerate, 0);
			setFMandPMComponents(fmcomponents, pmComponents, el);
		}else if("SDP".equals(nodeTypeToGenerate)){
			bits = line.split(",");
			el = createSDPCSElement(bits, nodeTypeToGenerate, 0,fmcomponents, pmComponents);				
		} else { // assuming generating a simple node
			bits = line.split(",");
			el = createCSElement(bits, nodeTypeToGenerate, 0);
			setFMandPMComponents(fmcomponents, pmComponents, el);
		}		

		if (sys == null) {
			final String message = "template file failed to define at least one system.";
			logger.error("CSInputFileReader:parseInputFile(): " + message);
			throw new UserInputException(message);
		}
		final String name = el.getName();
		if (validName(name, names)) {
			names.add(name);
			sys.addElement(el);
		}
	}

	private void setFMandPMComponents(final CSComponents fmcomponents,
			final CSComponents pmComponents, final CSElement el) {
		el.setFmComponents(fmcomponents);
		el.setPmComponents(pmComponents);
	}

	/**
	 * helper method to validate the user's input to do it in a method
	 * 
	 * @param params
	 */
	private void validateUserInput(final String[] params, final int expected,
			final String message) throws UserInputException {
		if (params.length != expected) {
			final String msg = message + " expected " + expected
			+ " got " + params.length;
			logger.error(msg);
			throw new UserInputException(msg);
		}
	}

	/**
	 * Creates a CSElement from user supplied parameters. node type is the first
	 * param passed.
	 * 
	 * @param params
	 * @param node
	 *            type
	 * @param offset
	 *            into params to start accessing values
	 * @return CSElement
	 * @throws UserInputException
	 */
	private CSElement createCSElement(final String[] params,
			final String nodeTypeToGenerate, final int offset)
			throws UserInputException {

		String message = "createCSElement() validating params to create "
				+ nodeTypeToGenerate + " node,";
		validateUserInput(params, 2 + offset, message);

		final CSElement el = new CSElement();

		el.setType(nodeTypeToGenerate);
		el.setName(params[0 + offset]);
		el.setProtocolType(CSTopologyUtilities.getProtocolType(el.getType()));

		final String ip = params[1 + offset];
		if (!"".equals(ip) && !CSTopologyUtilities.validateIpString(ip)) {
			message = "invalid ip recieved: " + ip;
			logger.error("CSFileInputHandler:createCSElement() " + message);
			throw new UserInputException(message);
		}
		el.setIp(ip);

		return el;
	}

	/**
	 * Creates a SNMP node element from user supplied parameters.
	 * 
	 * @param params
	 * @param expected
	 * @param offset
	 *            into params arra
	 * @return CSElement
	 * @throws UserInputException
	 */
	private CSElement createSNMPCSElement(final String[] params, final int expected, final String nodeTypeToGenerate, final int offset)
			throws UserInputException {
		
		String message = "createSNMPCSElement(): validating params to create " + nodeTypeToGenerate + " node, ";
		validateUserInput(params, expected + offset, message);

		final CSElement el = new CSElement();
		el.setType(nodeTypeToGenerate);
		el.setSnosType(params[0 + offset]);
		el.setName(params[0 + offset]);
		el.setProtocolType(CSTopologyUtilities.getProtocolType(el.getType()));
		final String ip = params[1 + offset];
		if (!"".equals(ip) && !CSTopologyUtilities.validateIpString(ip)) {
			message = "invalid ip recieved: " + ip;
			logger.error("createSNMPCSElement()  " + message);
			throw new UserInputException(message);
		}

		el.setIp(ip);

		el.setCommunity(params[2 + offset]);
		final String port = params[3 + offset];
		if (!"".equals(port) && !CSTopologyUtilities.validateSnmsPort(port)) {
			message = "invalid snmsport recieved: " + port;
			logger.error("createSNMPCSElement()  " + message);
			throw new UserInputException(message);
		}
		el.setSnmsPort(port);
		
		//need to store the name of the virtual node
		if(expected == 5) {
			el.setCimIdentifyingDescription(params[4 + offset]);
		}
		return el;
	}

}
