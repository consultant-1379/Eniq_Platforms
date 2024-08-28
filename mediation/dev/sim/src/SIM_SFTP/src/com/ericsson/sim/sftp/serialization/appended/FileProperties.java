package com.ericsson.sim.sftp.serialization.appended;

import java.io.Serializable;
import java.util.Date;


public class FileProperties implements Serializable{

	private static final long serialVersionUID = 1L;
	private Date lastCollectedTime;
	private String lastParsedLine;
	private int RopsUnmodified;

	public FileProperties(){
		lastCollectedTime = new Date(0);
		lastParsedLine = "";
	}
	
	public Date getLastCollectedTime() {
		return lastCollectedTime;
	}
	public void setLastCollectedTime(Date lastCollectedTime) {
		this.lastCollectedTime = lastCollectedTime;
	}
	public String getLastParsedLine() {
		return lastParsedLine;
	}
	public void setLastParsedLine(String lastParsedLine) {
		this.lastParsedLine = lastParsedLine;
	}
	
	public int getRopsUnmodified() {
		return RopsUnmodified;
	}

	public void incrementRopsUnmodified() {
		RopsUnmodified++;
	}
	
	public void clearUnmodified(){
		RopsUnmodified=0;
	}
}
