package com.ericsson.sim.model.protocol.snmp;

import java.io.Serializable;
import java.util.HashMap;

public class Counter implements Serializable{

	private static final long serialVersionUID = 1L;
	private HashMap<String, String> properties;
	
	public Counter(){
		properties = new HashMap<String, String>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key, value);
	}
	
	public String getProperty(String key){
		return properties.get(key);
	}
	
	public HashMap<String, String> getProperties(){
		return properties;
	}
	
	public void printDetails(){
		for (String name : properties.keySet()) {
			String key = name.toString();
			String value = properties.get(name).toString();
			System.out.println(key + " " + value);

		}
	}
}
