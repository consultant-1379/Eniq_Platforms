package com.ericsson.sim.model.pools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.model.node.Node;

public class NodePool {

	private static NodePool obj;
	private static int instance;
	private HashMap<Integer, Node> nodes;
	
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	private NodePool(){
		obj = this;
		nodes = new HashMap<Integer, Node>();
	}
	
	public void addNode(Node node){
		node.generateID();
		nodes.put(node.getID(), node);
	}
	
	public Node getNode(Integer ID){
		return nodes.get(ID);
	}
	
	public boolean hasNode(Integer ID){
		if(ID<0){
			ID = ID * -1;
		}
		return nodes.containsKey(ID);
	}
	
	public HashMap<Integer, Node> getNodes(){
		return nodes;
	}
	
	public void removeNodes(){
		nodes.clear();
	}
	
	public void writePersistedFile(String filepath) throws Exception {			
		File path = new File(filepath);
		if(!path.exists()){
			path.mkdirs();
		}
		
		path = new File(path,"topology.simc");
		if(!path.exists()){
			path.createNewFile();
		}
		
		FileOutputStream fout = new FileOutputStream(path);
		ObjectOutputStream output = new ObjectOutputStream(fout);
		output.writeObject(nodes);
		
		output.flush();
		output.close();
	}
	
	@SuppressWarnings("unchecked")
	public void loadPersistedFile(String filepath) throws Exception{
		File propertiesFile = new File(filepath);		
		if(propertiesFile.exists()){
			
			FileInputStream fin = new FileInputStream(propertiesFile);
			ObjectInputStream input = new ObjectInputStream(fin);
			nodes.clear();
			nodes.putAll((HashMap<Integer, Node>)input.readObject());
			input.close();
		}
	}
	
	public static NodePool getInstance(){
		if(instance ==0){
			instance = 1;
			return new NodePool();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
	
	public void printDetails(){
		for (Integer name : nodes.keySet()) {
			log.error("----------- Node " + name + " -----------");
			log.error(nodes.get(name).printDetails());
		}
	}
	
}
