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

public class SoemPmFileParser implements CounterSetFileParser {

	public static final Logger logger = Logger.getLogger("SoemPmFileParser");
	private static final String classname = SoemPmFileParser.class.getName();
	private String NBDirectory = MVMEnvironment.SOEMFAILEDDIR;
	private File inputFile;
	private String neType = "";
	
	@Override
	public File parseFile(String fdn, String filePath,
			CounterSetCallback callback, boolean doLookup) throws ParserException {
		
		inputFile = new File(filePath);
		parseFile(inputFile);
		
		if(doLookup){
			sortNeType();
		}
			
		return inputFile;
	}
	
	private void parseFile(File inputFile) throws ParserException{
		boolean foundMatch = false;
		try{
			FileInputStream fstream = new FileInputStream(inputFile);
			
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			while ((strLine = br.readLine()) != null && !foundMatch) {
				if(strLine.contains("NEType")){
					int start = strLine.indexOf(">")+1;
					int end = strLine.indexOf("</");
					neType = strLine.substring(start, end);
					foundMatch = true;
				}
				
			}
			
			in.close();
			fstream.close();
			
		} catch (FileNotFoundException e) {
			logger.error(classname+": Could not find input file "+inputFile.getName()+e);
			//throw new ParserException("Could not find input file");
		} catch (IOException e) {
			logger.error(classname+": Error opening file. "+inputFile.getName()+e);
			//throw new ParserException("Error opening SOEM lookup file");
		}catch (Exception e) {
			logger.error(classname+": Error reading file. "+inputFile.getName()+e);
			//throw new ParserException("Error reading SOEM lookup file");
		}
	}
	
	private void sortNeType() throws ParserException{
		boolean foundMatch = false;
		try {
			FileInputStream fstream = new FileInputStream(MVMEnvironment.PROPERTIESDIR + "/SOEMLookupTable");
			
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			while ((strLine = br.readLine()) != null && !foundMatch) {
				String[] lookupLine = strLine.split(",");
				if(lookupLine[0].equals(neType)){
					NBDirectory = lookupLine[1];
					foundMatch=true;
				}
			}
			
			in.close();
			fstream.close();
			
			} catch (FileNotFoundException e) {
				logger.error(classname+": Lookup file could not be found. "+inputFile.getName()+" moved to /eniq/mediator/runtime/soemFailed"+e);
				throw new ParserException("Could not find SOEM lookup file");
			} catch (IOException e) {
				logger.error(classname+": Error opening lookup file. "+inputFile.getName()+" moved to /eniq/mediator/runtime/soemFailed"+e);
				throw new ParserException("Error opening SOEM lookup file");
			}catch (Exception e) {
				logger.error(classname+": Error reading lookup file. "+inputFile.getName()+" moved to /eniq/mediator/runtime/soemFailed"+e);
				throw new ParserException("Error reading SOEM lookup file");
			}

			if(!foundMatch){
				throw new ParserException("No match was found in lookup table");
			}
	}
	

	@Override
	public String getContactInformation() {
		return "ENIQ product development team";
	}
	
	@Override
	public String getDescription() {
		return "SOEM File parser";
	}

	@Override
	public String getDirectory() {
		// TODO Auto-generated method stub
		return NBDirectory;
	}

}
