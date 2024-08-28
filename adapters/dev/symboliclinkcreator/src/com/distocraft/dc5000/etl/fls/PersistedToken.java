package com.distocraft.dc5000.etl.fls;

import java.io.Serializable;
import java.util.Map;


/*
 * Class for creating persisted token map for FLS Querying
 * 
 * @author xdhanac
 */
public class PersistedToken implements Serializable{
	/**
	 * serial id
	 */
	private static final long serialVersionUID = 1L;
	
	//tokenMap to be serialized
	Map<String,Integer> tokenMap;

	public PersistedToken(Map<String,Integer>  tokenMap) {
		super();
		this.tokenMap = tokenMap;
	}

	public Map<String,Integer>  getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(Map<String,Integer>  tokenMap) {
		this.tokenMap = tokenMap;
	}
	

}
