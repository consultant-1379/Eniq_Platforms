package com.ericsson.sim.sftp.serialization;

import java.io.File;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.exception.SIMException;

public class SFTPserFile {
	
	private Logger log = LogManager.getLogger(this.getClass().getName());
	
	public synchronized void writeserFile(String path, ArrayList<String> collection)throws SIMException{
		try{
			File loc = new File(path);
			if(!loc.getParentFile().exists()){
				loc.getParentFile().mkdirs();
			}
			
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream output = new ObjectOutputStream(fout);
			output.writeObject(collection);
			
			output.flush();
			output.close();

			log.debug("Updated file: " + loc.getName());
		}catch(Exception e){
			throw new SIMException("Unable to write ser file: " + path, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized ArrayList<String> loadPersistedFile(String path) throws SIMException{
		try{
			File propertiesFile = new File(path);
			if(propertiesFile.exists()){
				FileInputStream fin = new FileInputStream(propertiesFile);
				ObjectInputStream input = new ObjectInputStream(fin);
				ArrayList<String> collection = (ArrayList<String>) input.readObject();
				input.close();
				return collection;
			}
		}catch(Exception e){
			throw new SIMException("Unable to load ser file: " + path, e);
		}
		return new ArrayList<String>();
	}
	
	public boolean containsSERFile(String serPath){
		Path path = Paths.get(serPath);
		try {
			if(Files.exists(path) && Files.size(path)> 0){
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return false;
	}

}
