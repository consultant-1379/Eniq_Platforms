package com.ericsson.sim.model.protocol;

import java.io.Serializable;

public class Protocol implements Serializable{

	private static final long serialVersionUID = 1L;
	private int intervalID;
		
	public void addInterval(int id){
		intervalID = id;
	}
	
	public void removeInterval(Integer id){
		intervalID = 0;
	}
	
	public int getInterval(){
		return intervalID;
	}
	
	
	public int getID() {
		return 0;
	}

	public void generateID() {}
	
	public String getExecutionThreadName() {
		return "";
	}

	public void setName(String name) {}

	public String getName() {
		return "";
	}
	
	public void printDetails(){}
	
	
}
