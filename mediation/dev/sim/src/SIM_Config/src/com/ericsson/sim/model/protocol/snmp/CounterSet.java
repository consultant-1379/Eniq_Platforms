package com.ericsson.sim.model.protocol.snmp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class CounterSet implements Serializable{

	private static final long serialVersionUID = 1L;
	private HashMap<String, String> properties;
	private ArrayList<Counter> counters;
	private String name;
	
	public CounterSet(){
		properties = new HashMap<String, String>();
		counters = new ArrayList<Counter>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key, value);
	}
	
	public void addCounter(Counter counter){
		counters.add(counter);
	}
	
	public String getProperty(String key){
		return properties.get(key);
	}
	
	public HashMap<String, String> getProperties(){
		return properties;
	}
	
	public ArrayList<Counter> getCounters(){
		return counters;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public void printDetails(){
		for (String name : properties.keySet()) {
			String key = name.toString();
			String value = properties.get(name).toString();
			System.out.println(key + " " + value);

		}
		for(Counter c : counters){
			System.out.println("----------- Counter -----------");
			c.printDetails();
			System.out.println("----------- -- -- -----------");
		}
		
	}
	
	
}
