package com.ericsson.navigator.esm.manager.pm.file.remote.plugin;

import java.io.File;

/**
 * A counter set file parser interface to be implemented by a adaptation for a
 * specific type of PM counter set file.
 */
public interface CounterSetFileParser {	
	
	File parseFile(String fdn, String filePath, CounterSetCallback callback, boolean doLookup) throws ParserException;

	String getDescription();

	String getContactInformation();
	
	String getDirectory();
}
