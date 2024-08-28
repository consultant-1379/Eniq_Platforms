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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * class represents to read remote file fetch information for ALL CS node. file.
 * 
 * @author EPANJAG
 */

public class RemoteFileFetchDetailsReader {

	transient private List<ArrayList<String>> countersetRegexp = null;
	transient final private Map<String, ArrayList<CSRemoteElement>> ccMap = new HashMap<String, ArrayList<CSRemoteElement>>();

	transient final private List<ArrayList<CSElement>> csList = new ArrayList<ArrayList<CSElement>>();
	public static final Logger LOGGER = Logger.getLogger("ChargingSystem");
	transient private CSRemoteElement cs = null;
	transient private String valid = "";
	private final static String START = "start";
	private final static String REMOTESTARTTAG = "remoteStartTag";
	private final static String REMOTEENDTAG = "remoteEndTag";
	private final static String END = "end";
	private final static String CSTOPOLOGYMES = "CS PM topology cannot be created";

	public List<ArrayList<CSElement>> parseInputFile(
			final String inputFileName, final List<CSElement> csEle)
			throws IOException, UserInputException {
		final BufferedReader input = new BufferedReader(new FileReader(inputFileName));
		String line = input.readLine();

		// int lineCount = 1;

		while ((line != null)) {
			if ((!line.trim().equalsIgnoreCase("")) && (!(line.charAt(0) == '#'))) { // checking
				// for
				validateLine(line);
				if (START.equalsIgnoreCase(line)) {

					cs = new CSRemoteElement(); // NOPMD by epanjag on 19/04/10
					// 11:38

					line = input.readLine();
					continue;
				}

				if (REMOTESTARTTAG.equalsIgnoreCase(line)) {

					countersetRegexp = new ArrayList<ArrayList<String>>(); // NOPMD
					// by
					// epanjag
					// on
					// 19/04/10
					// 11:38

					line = input.readLine();
					continue;
				}
				if (REMOTEENDTAG.equalsIgnoreCase(line)) {

					cs.setRemoteCountersetRegexpPair(countersetRegexp);

				}
				csEndTagHelper(line);

				if (line.contains(":")) {
					loadCounterRegExp(line);
				}
				createCSObject(line, cs);

			}
			line = input.readLine();
		}
		checkForLastEndTag(line);
		loadCSObject(ccMap, csEle);
		
		return csList;
	}

	private void csEndTagHelper(final String line) {
		if (END.equalsIgnoreCase(line)) {
			if (ccMap.containsKey(cs.getCsNodeName().toLowerCase())) {
				final ArrayList<CSRemoteElement> al = ccMap.get(cs
						.getCsNodeName().toLowerCase());
				al.add(cs);
				ccMap.put(cs.getCsNodeName().toLowerCase(), al);
				cs.setCsr(al);
			} else {
				final ArrayList<CSRemoteElement> al1 = new ArrayList<CSRemoteElement>();//NOPMD
				al1.add(cs);
				ccMap.put(cs.getCsNodeName().toLowerCase(), al1);
				cs.setCsr(al1);
			}

		}
	}

	private void checkForLastEndTag(final String line)
			throws UserInputException {
		if ((line == null) && (!valid.equalsIgnoreCase(END))) {
			throw new UserInputException(
					"end  Tag is missing in the file NodeRemoteFileFetchDetails.txt."
					+CSTOPOLOGYMES);
		}
	}

	private void validateLine(final String line) throws UserInputException {
		checkForEndTag(line);
		if (line.equalsIgnoreCase(START)) {

			valid = line;
		}
		if ("".equalsIgnoreCase(valid)) {

			throw new UserInputException(
					"start Tag is missing in the file NodeRemoteFileFetchDetails.txt." +
					"CS PM topology cannot be created ");

		}
		if (line.equalsIgnoreCase(REMOTESTARTTAG)) {
			if (!valid.equalsIgnoreCase(START)) {
				throw new UserInputException(
						"start Tag is missing  in the file NodeRemoteFileFetchDetails.txt. " +
						CSTOPOLOGYMES);
			}
			valid = line;

		} else if (line.equalsIgnoreCase(REMOTEENDTAG)) {
			if (!valid.equalsIgnoreCase(REMOTESTARTTAG)) {
				throw new UserInputException(
						"remoteStartTag Tag is missing in the file NodeRemoteFileFetchDetails.txt. " +
						CSTOPOLOGYMES);
			}
			valid = line;

		} else if (line.equalsIgnoreCase(END)) {
			if (!valid.equalsIgnoreCase(REMOTEENDTAG)) {
				throw new UserInputException(
						"remoteEndTag Tag is missing in the file NodeRemoteFileFetchDetails.txt." +
						CSTOPOLOGYMES);
			}
			valid = line;
		}

	}

	private void checkForEndTag(final String line) throws UserInputException {
		if (line.equalsIgnoreCase(START) && !(valid.equalsIgnoreCase(END)) && !("".equalsIgnoreCase(valid))) {
			throw new UserInputException("end Tag is missing in the file NodeRemoteFileFetchDetails.txt." +
					CSTOPOLOGYMES);
		}
	}

	private void loadCounterRegExp(final String line) throws UserInputException {
		final String[] bits1 = line.split(":");
		final ArrayList<String> pair = new ArrayList<String>();
		pair.add(bits1[0]);
		pair.add(bits1[1]);
		if (countersetRegexp == null) {
			throw new UserInputException(
					"remoteStartTag  is missing in the file NodeRemoteFileFetchDetails.txt." +
					CSTOPOLOGYMES);
		}
		countersetRegexp.add(pair);
	}

	// for reducing cyclomatic complexity
	private void createCSObject(final String line, final CSRemoteElement cs)
			throws UserInputException {
		if (cs == null) {
			throw new UserInputException(
					"Start tag is missing in the file NodeRemoteFileFetchDetails.txt."
							+ CSTOPOLOGYMES);
		}
		if (line.contains("@")) {
			final String[] bits = line.split("@");
			// SDP_101@sdp1@15@SFTP@1@root@22@/var/opt/fds/statistics@.*PSC-TrafficHandler.*.stat@ccn1,sdp1@mainclass1
			// 0 1 2 3 4 5 6 7 8 9 10
			validatePMParams(bits);
			cs.setCsNodeName(bits[0]);
			cs.setpluginDir(bits[1]);
			cs.setROP(bits[2]);
			cs.setRemoteProtocolType(bits[3]);
			cs.setOffset(bits[4]);
			cs.setUserName(bits[5]);
			cs.setPort(Integer.parseInt(bits[6]));
			cs.setRemoteDir(bits[7]);
			cs.setFileNamePattern(bits[8]);
			
			/*
			  Change made to include new paraments - "filePerDay" and "MaxFileTransfersPerRop". 
			 */
			for (int i = 9; i < bits.length; i++) {
				final String[] remoteBits = bits[i].split("=");
				if (remoteBits[0].equalsIgnoreCase("FilePerDay")){
					cs.setFilePerDay(remoteBits[1]);
				} else if (remoteBits[0].equalsIgnoreCase("MaxFileTransfersPerRop")) {
					cs.setMaxFileTransfersPerRop(remoteBits[1]);
				} else if (remoteBits[0].equalsIgnoreCase("RemoteSubDir")) {
					cs.setSubDir(remoteBits[1]);
				} else if (remoteBits[0].equalsIgnoreCase("MainClass")) {
					cs.setMainClass(remoteBits[1]);
				}
			}
		}
	}

	private void validatePMParams(final String[] bits)
			throws UserInputException {
		if ((bits.length < 9) || (bits.length >13)){
			final String message = "RemoteFileFetchDetailsReader: loadToObject() "
					+ "Incorrect no of arguments .\n"
					+ "no of allowed parameters range is "
					+ "between 9 to 13 but received "
					+ bits.length
					+ " parameters from the NodeRemoteFileFetchDetails.txt file .\nCS PM topology cannot be created.";
			LOGGER.error(message);
			throw new UserInputException(message);

		}
	}

	// for reducing cyclo complexity
	private void loadCSObject(final Map<String, ArrayList<CSRemoteElement>> ccMap,
			final List<CSElement> csEle) throws UserInputException {
		if (ccMap.isEmpty()) {
			final String message = "RemoteFileFetchDetailsReader: loadCSObject() "
					+ "Unable to retrive the data from NodeRemoteFileFetchDetails.txt file."
					+ CSTOPOLOGYMES;
			LOGGER.error(message);
			throw new UserInputException(message);
		}
		for (int i = 0; i < csEle.size(); i++) {
			CSElement csElement;
			ArrayList<CSRemoteElement> cc1;
			csElement = (CSElement) csEle.get(i);
			cc1 = ccMap.get(csElement.getName().toLowerCase());
			if (cc1 == null) {
				final String message = "RemoteFileFetchDetailsReader: loadCSObject() "
						+ "PM info For the Node "
						+ csElement.getName()
						+ " is not available in the NodeRemoteFileFetchDetails.txt"
						+ " Hence PM support will not be provided .";
				LOGGER.info(message);
				csElement.setNodePMDisable(true);
				continue;
			}
				csElement.setCsr(cc1);
		}
	}
}
