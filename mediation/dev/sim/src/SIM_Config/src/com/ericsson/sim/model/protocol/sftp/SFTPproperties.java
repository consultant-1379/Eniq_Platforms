package com.ericsson.sim.model.protocol.sftp;

import java.io.Serializable;
import java.util.HashMap;

import com.ericsson.sim.model.protocol.Protocol;

public class SFTPproperties extends Protocol implements Serializable{

	private static final long serialVersionUID = 1L;
	private int ID;
	private String name;
	private HashMap<String, String> properties;
	
	public SFTPproperties(){
		properties = new HashMap<String, String>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key.toUpperCase(), value);
	}

	public String getProperty(String key){
		return properties.get(key.toUpperCase());
	}
	
	public HashMap<String, String> getProperties(){
		return properties;
	}
	
	public boolean containsProperty(String key){
		return properties.containsKey(key.toUpperCase());
	}
	
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void generateID() {
		int hash = name.hashCode();
		if(hash < 0){
			hash = hash * -1;
		}
		this.ID = hash;
	}

	@Override
	public String getExecutionThreadName() {
		return getProperty("ExecutionThreadName");
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	@Override
	public void printDetails(){
		System.out.println("----------- SFTP " + ID + " -----------");
		for (String name : properties.keySet()) {
			String key = name.toString();
			String value = properties.get(name).toString();
			System.out.println(key + " " + value);

		}
		System.out.println("\n\n");
	}
	
}

