package com.ericsson.sim.sftp.serialization.appended;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;

public class FilePropertyManager {

	Logger log = LogManager.getLogger(this.getClass().getName());
	private static FilePropertyManager obj;
	private static int instance;
	protected ConcurrentHashMap<String, FileProperties> files;
	protected static int numberofInstances;

	private FilePropertyManager(){
		obj = this;
		files = new ConcurrentHashMap<String, FileProperties>();
		loadPersistedFile();
		numberofInstances = 0;
	}
	
	public synchronized FileProperties getProperties(String id){
		FileProperties props = new FileProperties();
		if(files.containsKey(id)){
			props = files.get(id);
		}else{
			files.put(id, props);
		}
		return props;
	}
	
	public void addProperties(String id, FileProperties props){
		files.put(id, props);
	}
	
	public void removeProperties(String id){
		files.remove(id);
	}
	
	public synchronized void writePerFile(){
		File loc = new File(SIMEnvironment.CONFIGPATH,"appendedFiles.simc");
		try{
			if(!loc.getParentFile().exists()){
				loc.mkdirs();
			}
			
			FileOutputStream fout = new FileOutputStream(loc);
			ObjectOutputStream output = new ObjectOutputStream(fout);
			output.writeObject(files);
			
			output.flush();
			output.close();
		}catch(Exception e){
			log.error("Unable to write ser file: " + loc.getAbsolutePath(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadPersistedFile(){
		File propertiesFile = new File(SIMEnvironment.CONFIGPATH,"appendedFiles.simc");
		try{
			if(propertiesFile.exists()){
				FileInputStream fin = new FileInputStream(propertiesFile);
				ObjectInputStream input = new ObjectInputStream(fin);
				files.putAll((ConcurrentHashMap<String, FileProperties>) input.readObject());
				input.close();
			}
		}catch(Exception e){
			log.error("Unable to load ser file: " + propertiesFile.getAbsolutePath(), e);
		}
	}
	
	public synchronized void destroyInstance(){
		numberofInstances--;
		if(numberofInstances == 0){
			writePerFile();
		}
	}
	
	public static synchronized FilePropertyManager getInstance(){
		numberofInstances++;
		if(instance ==0){
			instance = 1;
			return new FilePropertyManager();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
}
