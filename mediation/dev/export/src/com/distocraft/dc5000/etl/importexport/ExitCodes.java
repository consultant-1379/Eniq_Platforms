package com.distocraft.dc5000.etl.importexport;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * Enum to hold all exit ccodes and respective exit messages.
 */
public enum ExitCodes {
	/**
	 * OK exit code : 0
	 */
	EXIT_OK("OK"),
	/**
	 * Usage error exit code
	 */
	EXIT_CODE_HELP("Wrong Usage"),
	/**
	 * Exit code to use of the input file was not found
	 */
	EXIT_NO_FILE("-f option not specified."),
	/**
	 * Exit code to use if the Tech Pack versionId string is not valid
	 */
	EXIT_INVALID_VER("TP VersionID not in the correct format"),
	/**
	 * Exit code to use if the import file cant be found
	 */
	EXIT_INVALID_FILE("Import file is not valid."),
	/**
	 * Exit code to use if the -add or -delete option isnt specified on an import
	 */
  EXIT_NO_IMPORT_TYPE("No import type : -add | -delete"),
  /**
   * Exit code to use if the input file or directory contains no .log extension
   */
  EXIT_INVALID_EXT("-f option not specified or file/directory contains no .log.XX extension"),
  /**
   * Exit code to use if the output file was not found
   */
  EXIT_NO_OUTPUT_FILE("-o option not specified.");
	private String exitMessage;

	ExitCodes(final String exitMessage) {
		this.exitMessage = exitMessage;
	}

	/**
	 * Get the message for the exit type
	 * @return Exit message.
	 */
	public final String getExistMessage() {
		return exitMessage;
	}
}
