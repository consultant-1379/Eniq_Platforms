package com.ericsson.navigator.esm.util.file;

import java.io.File;

/**
 * Interface for directory listeners
 * @author qbacfre
 *
 */
public interface DirectoryListener {

	void directoryChanged(File directory);
	
}
