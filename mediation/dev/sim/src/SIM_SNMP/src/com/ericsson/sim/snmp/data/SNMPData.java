package com.ericsson.sim.snmp.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SNMPData {

	private String header = null;
	private String measurement = null;
	private String ROP = null;
	private String timeOfRop = null;
	private ArrayList<String> data = null;
	
	public SNMPData(){
		data = new ArrayList<String>();
	}
	
	public void setHeader(String header){
		this.header = header;
	}
	
	public String getHeader(){
		return header;
	}
	
	public void setMeasurement(String measurement){
		this.measurement = measurement;
	}
	
	public String getMeasurement(){
		return measurement;
	}
	
	public void setROP(String ROP){
		this.ROP = ROP;
	}
	
	public String getROP(){
		return ROP;
	}
	
	public void setTimeOfRop(String RopTime){
		this.timeOfRop = RopTime;
	}
	
	public String getTimeOfRop(){
		return timeOfRop;
	}
	
	
	public void addToData(String line){
		data.add(line);
	}
	
	public ArrayList<String> getData(){
		return data;
	}
	
}
