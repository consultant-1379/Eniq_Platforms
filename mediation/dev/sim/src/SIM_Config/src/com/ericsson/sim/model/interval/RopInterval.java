package com.ericsson.sim.model.interval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RopInterval implements Serializable{

	private static final long serialVersionUID = 1L;
	private int ID;
	private String name;
	private HashMap<String, String> properties;
	private ArrayList<Integer> nodes;
	
	public RopInterval(){
		properties = new HashMap<String, String>();
		nodes = new ArrayList<Integer>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key.toUpperCase(), value);
	}
	
	public void addNode(Integer ID){
		if(!nodes.contains(ID)){
			nodes.add(ID);
		}
	}
	
	public String getProperty(String key){
		return properties.get(key.toUpperCase());
	}
	
	public boolean hasNode(Integer ID){
		return nodes.contains(ID);
	}
	
	public ArrayList<Integer> getNodeIDs(){
		return nodes;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public Integer getID() {
		return ID;
	}

	public void generateID() {
		int hash = name.hashCode();
		if(hash<0){
			hash = hash * -1;
		}
		this.ID = hash;
	}
	
	public void removeNodes(){
		nodes.clear();
	}
	
	public void printDetails(){
		for (String name : properties.keySet()) {
			String key = name.toString();
			String value = properties.get(name).toString();
			System.out.println(key + " " + value);

		}
		for(Integer c : nodes){
			System.out.println("protocols");
			System.out.println(c);
			System.out.println("\n\n");
		}
		
	}
	
	
}
