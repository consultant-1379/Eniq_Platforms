package com.distocraft.dc5000.etl.importexport;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * Used to determine what to do with the generated sql file after loading.
 */
public enum AfterLoadAction {
	/**
	 * Generated load sql file gets delete
	 */
	delete,
	/**
	 * Generated load sql file gets moved to archive directory
	 * Not Implemented.
	 */
	move,
	/**
	 * Generated load file is left in place.
	 */
	none
}
