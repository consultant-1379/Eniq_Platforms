package com.ericsson.eniq.flssymlink.fls;

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
	Map<String,Long> tokenMap;

	public PersistedToken(Map<String,Long>  tokenMap) {
		super();
		this.tokenMap = tokenMap;
	}

	public Map<String,Long>  getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(Map<String,Long>  tokenMap) {
		this.tokenMap = tokenMap;
	}

	public PersistedToken() {
		super();
	}
	

}
