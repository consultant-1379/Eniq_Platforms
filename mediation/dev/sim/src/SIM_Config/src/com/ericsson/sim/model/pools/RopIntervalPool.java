package com.ericsson.sim.model.pools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.ericsson.sim.model.interval.RopInterval;

public class RopIntervalPool {

	private static RopIntervalPool obj;
	private static int instance = 0;
	private HashMap<Integer, RopInterval> intervals;
	
	private RopIntervalPool(){
		obj = this;
		intervals = new HashMap<Integer, RopInterval>();
	}
	
	public RopInterval getInterval(Integer ID){
		RopInterval conn = null;
		if(intervals.containsKey(ID)){
			conn = intervals.get(ID);
		}
		return conn;
	}
	
	public void addInterval(RopInterval conn){
		if(intervals.containsKey(conn.getID())){
			intervals.remove(conn.getID());
		}
		conn.generateID();
		intervals.put(conn.getID(), conn);
	}
	
	public void removeInterval(Integer ID){
		intervals.remove(ID);
	}
	
	public HashMap<Integer, RopInterval> getRopIntervals(){
		return intervals;
	}
	
	public int getNumberofRopIntervals(){
		return intervals.size();
	}
	
	public void writePersistedFile(String filepath) throws Exception {		
		File loc = new File(filepath);
		if(!loc.exists()){
			loc.mkdirs();
		}
		
		FileOutputStream fout = new FileOutputStream(new File(filepath+"/intervals.simc"));
		ObjectOutputStream output = new ObjectOutputStream(fout);
		output.writeObject(intervals);
		
		output.flush();
		output.close();
	}
	
	@SuppressWarnings("unchecked")
	public void loadPersistedFile(String filepath) throws Exception{
		File propertiesFile = new File(filepath);
		if(propertiesFile.exists()){
			FileInputStream fin = new FileInputStream(propertiesFile);
			ObjectInputStream input = new ObjectInputStream(fin);				
			intervals.putAll((HashMap<Integer, RopInterval>)input.readObject());
			input.close();
		}
	}
	
	public static RopIntervalPool getInstance(){
		if(instance ==0){
			instance = 1;
			return new RopIntervalPool();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
	
	public void printDetails(){
		for (Integer name : intervals.keySet()) {
			System.out.println("----------- Conneciton " + name + " -----------");
			intervals.get(name).printDetails();
			System.out.println("\n\n");
		}
	}
	
}
