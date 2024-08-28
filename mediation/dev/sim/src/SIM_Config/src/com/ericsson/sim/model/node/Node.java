package com.ericsson.sim.model.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Node implements Serializable{

	private static final long serialVersionUID = 1L;
	private int ID;
	private String name;
	private HashMap<String, String> properties;
	private ArrayList<Integer> protocols;

	
	public Node(){
		properties = new HashMap<String, String>();
		protocols = new ArrayList<Integer>();
	}
	
	public void addProperty(String key, String value){
		properties.put(key.toUpperCase(), value);
	}

	public String getProperty(String key){ 
		return properties.get(key.toUpperCase());
	}
	
	public void addProtocol(Integer ID){
		if(!protocols.contains(ID)){
			protocols.add(ID);
		}
	}
	
	public boolean hasProperty(String key){
		return properties.containsKey(key.toUpperCase());
	}

	public ArrayList<Integer> getProtocols(){
		return protocols;
	}
	
	public void clearProtocols(){
		protocols.clear();
	}
	
	public synchronized void updateFirstRop(Integer ID){
		String firstRop = (String) getProperty("firstRop");
		if(firstRop == null){
			firstRop = ""+ID;
		}else{
			firstRop = firstRop + "," + ID;
		}
		addProperty("firstRop", firstRop);
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getID() {
		return ID;
	}
	
	public void generateID() {
		String idstring = name + ":" + getProperty("IPAddress") + ":" + getProperty("uniqueID");
		int hash = idstring.hashCode();
		if(hash<0){
			hash = hash * -1;
		}
		this.ID = hash;
	}
	
	
	public String printDetails(){
		String s = "";
		
		for (String name : properties.keySet()) {
			String key = name.toString();
			String value = properties.get(name).toString();
			//log.error(key + " " + value);
			s= s.concat(key + ":" + value);
			s=s.concat("\n");

		}
		for(Integer c : protocols){
			s=s.concat("----------- supportedTypes -----------\n");
			s=s.concat(c.toString());
			s=s.concat("\n----------- -- -- -----------");
			//log.error("----------- supportedTypes -----------");
			//log.error(c);
			//log.error("----------- -- -- -----------");
		}
		return s;
		
	}
	
	public void validate() throws Exception{
		boolean valid = true;
		String errorMessage = "";
		
		//validate that the SFTP port is an integer
		if(hasProperty("sftpport")){
			valid = validateInteger(getProperty("sftpport"));
			errorMessage.concat("SFTP port is not a valid integer value. ");
		}
		
		//validate that the SNMP port is an integer
		if(hasProperty("snmpport")){
			valid = validateInteger(getProperty("sftpport"));
			errorMessage.concat("SNMP port is not a valid integer value. ");
		}
		
		//validate the IP address
		String[] ipParts = getProperty("IPAddress").split(".");
		if(ipParts.length == 4){
			for(String part : ipParts){
				
				if(validateInteger(part)){
					int number = Integer.parseInt(part);
					if(number > 255){
						valid = false;
						errorMessage.concat("IP address contains a value over 255. ");
					}
				}else{
					valid = false;
					errorMessage.concat("IP address contains a value that could not be parsed to an integer. ");
				}
				
				
			}
		}else{
			valid = false;
			errorMessage.concat("IP address is not valid. ");
		}

		if(!valid){
			throw new Exception(errorMessage);
		}
		
		
		
	}
	
	private boolean validateInteger(String value){
		try{
			Integer.parseInt(value);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
}