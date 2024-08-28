package com.ericsson.eniq.enminterworking.automaticNAT;

import java.util.ArrayList;
import java.util.Map;

public class Eniq_Role {

	private String eniq_identifier = null;

	private String ip_address = null;

	private String role = null;
	
	private Map<String, ArrayList<String>> policyCriteriaMap = null;
	
	public Eniq_Role (String eniq_identifier, String ip_address, String role,Map<String, ArrayList<String>> policyCriteriaMap ){
		this.eniq_identifier = eniq_identifier;
		this.ip_address = ip_address;
		this.role = role;
		this.policyCriteriaMap = policyCriteriaMap;
	}

	public String getEniq_identifier() {
		return eniq_identifier;
	}

	public String getIp_address() {
		return ip_address;
	}

	public String getRole() {
		return role;
	}
	
	public Map<String, ArrayList<String>> getPolicyCriteriaMap() {
		return policyCriteriaMap;
	}

	public String toString() {
		return eniq_identifier;
	}
}
