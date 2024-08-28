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

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class holds utility methods of use to the classes manipulating the CS
 * topology file.
 * 
 * @author ejammor
 */
//see comment on method getProtocolType(), its the only offending method in the class
@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class CSTopologyUtilities {

	public static final Logger logger = Logger.getLogger("CSSR");
	
	public static final String NO_PARENT = "";

	private static final int SEGMENTS_IN_COIINFO = 4;

	/**
	 * Creates a new instance of <code>CSTopologyUtilities</code> Constructor is
	 * private to silence PMD warnings about all static methods.
	 */
	private CSTopologyUtilities() {

	}

	/**
	 * returns the protocol type for a given node type;
	 * 
	 * @param nodeType
	 * @return
	 */
	//ejammor suppressing the warning as the method is very simple and a fix is not justified
	@SuppressWarnings( "PMD.CyclomaticComplexity" )
	public static String getProtocolType(final String nodeType) {
		if ("CRS".equals(nodeType)) {
			return "TXF";
		} else if ("MINSAT".equals(nodeType)) {
			return "TXF";
		} else if ("SDP".equals(nodeType)) {
			return "TXF";
		} else if ("VS".equals(nodeType)) {
			return "TXF";
		} else if ("AIR".equals(nodeType)) {
			return "TXF";
		} else if ("CCN".equals(nodeType)) {
			return "IRP";
		} else if ("SNF".equals(nodeType)) {
			return "SNF";
		} else if ("MINSAT".equals(nodeType)) {
			return "TXF";
		}else if ("CRS".equals(nodeType)) {
			return "TXF";
		} else if ("IVR".equals(nodeType)) {
			return "MST";
		} else if ("OCMP".equals(nodeType)) {
			return "MST";
		} else {
			return null;
		}
	}

	/**
	 * method creates the default cimotheridentifyinginfo attribute for elements
	 * in an ordered element list.
	 * 
	 * @param elements
	 */
	public static void createDefaultCOIInfo(final List<CSElement> elements) {
		for (final CSElement el : elements) {
			createDefaultCOIInfo(el);
		}
	}

	/**
	 * method creates the default cimotheridentifyinginfo attribute for elements
	 * in an ordered element list.
	 * 
	 * @param elements
	 */
	public static void createDefaultCOIInfo(final CSElement element) {
		final int digits = SEGMENTS_IN_COIINFO;
		String info = null;
		if ((element != null) && "".equals(element.getOtherIndentifyingInfo())) {
			info = makeCOIInfoString(digits);
			element.setOtherIndentifyingInfo(info);
		}
	}

	/**
	 * changes the last quad of the cimotherindentifyinginfo to the given value.
	 * 
	 * @param oii
	 * @param quad
	 * @return String
	 */
	public static String adjustOtherIndentifyingInfo(final String oii,
			final int quad) {
		final int lastIndex = oii.lastIndexOf(".") + 1;
		return oii.substring(0, lastIndex) + quad;
	}

	protected static String makeCOIInfoString(final int digits) {
		final StringBuffer string = new StringBuffer();

		for (int i = 0; i < digits; i++) {
			string.append("1");
			if (i < digits - 1) {
				string.append(".");
			}
		}
		return string.toString();
	}

	/**
	 * compares the top level of hierarchy for the CSSystem objects in the list if they differ throw an
	 * exception.
	 * 
	 * @param systems
	 * @throws UserInputException
	 */
//	public static void checkAllBelongToSameChargingSystem(final List<CSSystem> systems) throws UserInputException {
//		
//		if(systems == null || systems.size() == 0) {
//			return;
//		}
//		
//		CSSystem sys = systems.get(0);
//		final String chargingSystemName = sys.peekNextHierarchy();
//		String sysName = null;
//		for(int i=1;i<systems.size();i++) {
//			sys = systems.get(i);
//			sysName = sys.peekNextHierarchy();
//			if(!chargingSystemName.equals(sysName)) {
//				final String msg = "multiple" +
//						" charging systems defined: " + chargingSystemName + ", " +sysName + ". Check template file!";
//				logger.error("CSTopologyUtilities:checkAllBelongToSameChargingSystem()" + msg);
//				throw new UserInputException(msg);
//			}
//		}
//	}
	
	/**
	 * validates an ip string.
	 * 
	 * @param ip
	 * @return true if string is valid ip
	 */
	public static boolean validateIpString(final String ip) {
		final String[] quads = ip.split("[.]");
		if (quads.length != 4) {
			return false;
		}

		try {
			int quad = 0;
			quad = Integer.parseInt(quads[0]);
			if (!validateQuad(quad, 0, 255)) {
				return false;
			}
			quad = Integer.parseInt(quads[1]);
			if (!validateQuad(quad, 0, 255)) {
				return false;
			}
			quad = Integer.parseInt(quads[2]);
			if (!validateQuad(quad, 0, 255)) {
				return false;
			}
			quad = Integer.parseInt(quads[3]);
			if (!validateQuad(quad, 0, 255)) {
				return false;
			}

		} catch (final NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	
	
	/**
	 * checks if the quad value lies in the given range inclusively.
	 * 
	 * @param quad
	 * @param min
	 * @param max
	 * @return boolean
	 */
	private static boolean validateQuad(final int quad, final int min,
			final int max) {
		return ((quad >= min) && (quad <= max));
	}

	/**
	 * validate a snmsport value.
	 * 
	 * @param String
	 *            port
	 * @return boolean
	 */
	public static boolean validateSnmsPort(final String port) {
		boolean valid = true;
		try {
			//accepting any valid positive integer greater than or equal to zero
			final int portVal = Integer.parseInt(port);
			//if ((portVal < 1025) || (portVal > 65355)) {
			if (portVal < 0) {
				valid = false;
			}
		} catch (final NumberFormatException e) {
			valid = false;
		}
		return valid;
	}
}
