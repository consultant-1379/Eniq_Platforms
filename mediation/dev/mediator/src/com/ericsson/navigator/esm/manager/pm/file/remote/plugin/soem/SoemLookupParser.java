package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.soem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVMEnvironment;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.ParserException;

public class SoemLookupParser implements CounterSetFileParser {

	public static final Logger logger = Logger.getLogger("SoemLookupParser");
	private static final String classname = SoemLookupParser.class.getName();
	private String NBDirectory;
	private File inputFile;
	
	@Override
	public File parseFile(String fdn, String filePath,
			CounterSetCallback callback, boolean doLookup) throws ParserException {
		NBDirectory = MVMEnvironment.SOEMFAILEDDIR;
		boolean foundMatch = false;
		inputFile = new File(filePath);
		String[] filename = inputFile.getName().split("-");
		try {
			FileInputStream fstream = new FileInputStream(MVMEnvironment.SFTP_LISTEDFILES_STORAGE_PATH+"SOEMLookupTable");
			
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			while ((strLine = br.readLine()) != null && !foundMatch) {
				String[] lookupLine = strLine.split(",");
				if(lookupLine[0].equals(filename[0]) && lookupLine[1].equals(filename[3])){
					NBDirectory = lookupLine[2];
					foundMatch=true;
				}
			}
			
			in.close();
			
			} catch (FileNotFoundException e) {
				logger.error(classname+": Lookup file could not be found. "+inputFile.getName()+" moved to /nav/var/esm/pm/local/soemFailed");
				throw new ParserException("Could not find SOEM lookup file");
			} catch (IOException e) {
				logger.error(classname+": Error opening lookup file. "+inputFile.getName()+" moved to /nav/var/esm/pm/local/soemFailed");
				throw new ParserException("Error opening SOEM lookup file");
			}catch (Exception e) {
				logger.error(classname+": Error reading lookup file. "+inputFile.getName()+" moved to /nav/var/esm/pm/local/soemFailed");
				throw new ParserException("Error reading SOEM lookup file");
			}

			if(!foundMatch){
				throw new ParserException("No match was found in lookup table");
			}
			
		return inputFile;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser#getContactInformation()
	 */
	@Override
	public String getContactInformation() {
		return "ENIQ product development team";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser#getDescription()
	 */
	@Override
	public String getDescription() {
		return "SOEM lookup table parser";
	}


	@Override
	public String getDirectory() {
		return NBDirectory;
	}

}
