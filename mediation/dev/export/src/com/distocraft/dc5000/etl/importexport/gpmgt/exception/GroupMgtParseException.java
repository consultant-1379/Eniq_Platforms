package com.distocraft.dc5000.etl.importexport.gpmgt.exception;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
import java.util.List;

/**
 * This excpetion will be thrown if there any any error (schema validation) during
 * the XML parse.
 */
public class GroupMgtParseException extends GroupMgtException {
	/**
	 * Error message
	 */
	private static final String SLOGAN = "Error parsing Group Management Data file.";
	/**
	 * Additional info key for the xml file being parsed
	 */
	private static final String ADD_INFO_DFILE = "datafile:";
	/**
	 * Additional info key for the parser error.
	 */
	private static final String ADD_INFO_EPREFIX = "parsererror_";

	/**
	 * Constructor
	 * @param dataFile The input XML file
	 * @param parserErrors Any XML parser errors
	 */
	public GroupMgtParseException(final String dataFile, final List<String> parserErrors) {
		super(SLOGAN, null);
		addAdditionalInfo(ADD_INFO_DFILE, dataFile);
		if(parserErrors != null && !parserErrors.isEmpty()){
			for(int i=1;i<=parserErrors.size();i++){
				final String addInfo = ADD_INFO_EPREFIX + i + ":";
				addAdditionalInfo(addInfo, parserErrors.get(i-1));
			}
		}
	}
}
