package com.ericsson.sim.configConversion;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class ConversionMapping {

	public HashMap<String, String> LoadMapping(){
		HashMap<String, String> mapping = new HashMap<String, String>();
		
		try{
			File mappingFile = new File("/eniq/sw/conf/sim/converionmapping");
			if(mappingFile.exists()){
				List<String> lines = Files.readAllLines(mappingFile.toPath(), Charset.forName("UTF-8"));
				
				for(String line : lines){
					String[] parts = line.split("=");
					mapping.put(parts[0], parts[1]);
				}
			}
		}catch(Exception e){
			System.err.println("Unable to load conversion mapping file");
		}
		
		
		return mapping;
	}
	
}
