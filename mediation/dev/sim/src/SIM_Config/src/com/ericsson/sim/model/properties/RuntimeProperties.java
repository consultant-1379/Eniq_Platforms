package com.ericsson.sim.model.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class RuntimeProperties {

	private static RuntimeProperties obj;
	private static int instance;
	private HashMap<String, String> properties;
	
	private RuntimeProperties(){
		obj = this;
		properties = new HashMap<String, String>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key.toUpperCase(), value);
	}
	
	public String getProperty(String key){
		return properties.get(key.toUpperCase());
	}
	
	
	public void writePersistedFile(String filepath) throws Exception{		
			File loc = new File(filepath);
			if(!loc.exists()){
				loc.mkdirs();
			}
			
			FileOutputStream fout = new FileOutputStream(new File(filepath+"/properties.simc"));
			ObjectOutputStream output = new ObjectOutputStream(fout);
			output.writeObject(properties);
			
			output.flush();
			output.close();
	}
	
	@SuppressWarnings("unchecked")
	public void loadPersistedFile(String filepath) throws Exception{
		File propertiesFile = new File(filepath);
		if(propertiesFile.exists()){
			FileInputStream fin = new FileInputStream(propertiesFile);
			ObjectInputStream input = new ObjectInputStream(fin);				
			properties = (HashMap<String, String>)input.readObject();
			input.close();
		}
	}
	
	public static RuntimeProperties getInstance(){
		if(instance ==0){
			instance = 1;
			return new RuntimeProperties();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
}
