package com.ericsson.sim.model.protocol.snmp.disable;

import java.util.ArrayList;
import java.util.HashMap;

public class DisabledCollection {

	private String affectedPluginName;
	private String collectionName;
	private ArrayList<Integer> affectedNodes;
	private HashMap<String, ArrayList<String>> affectedCountersets;
	
	public DisabledCollection(){
		affectedNodes = new ArrayList<Integer>();
		affectedCountersets = new HashMap<String, ArrayList<String>>();
	}
	
	public void setcollectionName(String collectionName){
		this.collectionName = collectionName;
	}
	
	public String getcollectionName(){
		return collectionName;
	}
	
	public void setPluginName(String pluginName){
		affectedPluginName = pluginName;
	}
	
	public String getPluginName(){
		return affectedPluginName;
	}
	
	public void addNodeToList(Integer nodeID){
		affectedNodes.add(nodeID);
	}
	
	public boolean containsNode(Integer nodeID){
		if(affectedNodes.contains(nodeID)){
			return true;
		}
		
		return false;
	}
	
	public void addCounterSet(String CounterSetName, ArrayList<String> CounterNames){
		affectedCountersets.put(CounterSetName, CounterNames);
	}
	
	public boolean containsCounterset(String CounterSetName){
		if(affectedCountersets.containsKey(CounterSetName)){
			return true;
		}
		return false;
	}
	
	public ArrayList<String> getCounterNames(String CounterSetName){
		return affectedCountersets.get(CounterSetName);
	}
	
}
