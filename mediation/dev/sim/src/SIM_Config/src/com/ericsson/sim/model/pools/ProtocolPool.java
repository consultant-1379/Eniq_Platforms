package com.ericsson.sim.model.pools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.ericsson.sim.model.protocol.Protocol;

public class ProtocolPool {
	
	private static ProtocolPool obj;
	private static int instance;
	private HashMap<Integer, Protocol> protocols;
	
	private ProtocolPool(){
		obj = this;
		protocols = new HashMap<Integer, Protocol>();
	}
	
	public Protocol getProtocol(Integer ID){
		Protocol protocol = null;
		if(protocols.containsKey(ID)){
			protocol = protocols.get(ID);
		}
		return protocol;
	}
	
	public void addProtocol(Protocol protocol){
		if(protocols.containsKey(protocol.getID())){
			protocols.remove(protocol.getID());
		}
		protocol.generateID();
		protocols.put(protocol.getID(), protocol);
	}
	
	public boolean containsProtocol(Integer ID){
		return protocols.containsKey(ID);
	}
	
	public boolean containsProtocol(String name){
		return protocols.containsKey(name.hashCode());
	}
	
	public void removeProtocol(Integer ID){
		protocols.remove(ID);
	}
	
	public HashMap<Integer, Protocol> getProtocols(){
		return protocols;
	}
	
	public void writePersistedFile(String filepath) throws Exception {			
		File loc = new File(filepath);
		if(!loc.exists()){
			loc.getParentFile().mkdirs();
			loc.createNewFile();
		}


		for (Integer i : protocols.keySet()) {
			System.out.println(protocols.get(i).getName());;

		}
		
		FileOutputStream fout = new FileOutputStream(new File(filepath));
		ObjectOutputStream output = new ObjectOutputStream(fout);
		output.writeObject(protocols);
		
		output.flush();
		output.close();
	}
	
	@SuppressWarnings("unchecked")
	public void loadPersistedFile(String filepath) throws Exception{
		File propertiesFile = new File(filepath);
		if(propertiesFile.exists()){
			FileInputStream fin = new FileInputStream(propertiesFile);
			ObjectInputStream input = new ObjectInputStream(fin);				
			protocols.putAll((HashMap<Integer, Protocol>)input.readObject());
			input.close();
		}
	}
	
	
	public static ProtocolPool getInstance(){
		if(instance ==0){
			instance = 1;
			return new ProtocolPool();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
	
	public void printDetails(){
		for (Integer name : protocols.keySet()) {
			System.out.println("----------- Protocol " + name + " -----------");
			protocols.get(name).printDetails();
			System.out.println("\n\n");
		}
	}
	
	
}

