package com.ericsson.sim.model.protocol.snmp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.ericsson.sim.model.protocol.Protocol;

public class SNMPproperties extends Protocol implements Serializable{

	private static final long serialVersionUID = 1L;
	private int ID;
	private String name;
	private ArrayList<CounterSet> countersets;
	private HashMap<String, String> properties;
	
	public SNMPproperties(){
		countersets = new ArrayList<CounterSet>();
		properties = new HashMap<String, String>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key.toUpperCase(), value);
	}
	
	public String getProperty(String key){
		return properties.get(key.toUpperCase());
	}
	
	public boolean containsProperty(String key){
		return properties.containsKey(key.toUpperCase());
	}
	
	public void addCounterSet(CounterSet counterset){
		countersets.add(counterset);
	}
	
	public HashMap<String, String> getProperties(){
		return properties;
	}
	
	public ArrayList<CounterSet> getCounterSets(){
		return countersets;
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
		return name;
	}
	
	
	@Override
	public void printDetails(){
		System.out.println("----------- " + ID + " -----------");
		for (String name : properties.keySet()) {
			String key = name.toString();
			String value = properties.get(name).toString();
			System.out.println(key + " " + value);

		}
		for(CounterSet c : countersets){
			System.out.println("----------- CounterSet -----------");
			c.printDetails();
			System.out.println("----------- -- -- -----------");
		}
		System.out.println("----------- -- -- -----------");
	}
	
	
}
