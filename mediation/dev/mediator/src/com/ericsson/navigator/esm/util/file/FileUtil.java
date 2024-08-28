package com.ericsson.navigator.esm.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;


public abstract class FileUtil {

	private static final String classname = FileUtil.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	
	/**
	 * This will load a HashSet with values from a line separated file.
	 * @param valueFile The file with the values
	 * @param set
	 * @return
	 */
	public static boolean loadList(final File valueFile, final Set<String> set) {
		
		FileReader reader = null;
		BufferedReader breader = null;
		
		try {
			reader = new FileReader(valueFile);
			breader = new BufferedReader(reader);

			String line = null;
			try {

				while ((line = breader.readLine()) != null) {					
					set.add(line);
				}

			} catch (IOException e) {
				logger.error("Unable to read the FDN file " + valueFile, e);
				return false;
			}

		} catch (FileNotFoundException e) {
			logger.error("The file was not found " + valueFile);
			return false;
		}  finally {
			try {				
				if (breader != null) {
					breader.close();
				}
			} catch (IOException e) {
				logger.error("Unable to close stream for reading file " + valueFile);
			}
		}
		
		return true;
	}
}
