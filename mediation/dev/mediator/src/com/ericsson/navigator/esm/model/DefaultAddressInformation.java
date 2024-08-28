package com.ericsson.navigator.esm.model;



public class DefaultAddressInformation implements AddressInformation {

	private final String address;
	private final String type;
	private final String backupIP;
	
	public DefaultAddressInformation(final String address, final String type){
		this.address = address;
		this.type = type;
		this.backupIP = "";
	}
	
	public DefaultAddressInformation(final String address, final String type, final String backupIP){
		this.address = address;
		this.type = type;
		this.backupIP = backupIP;
	}
	
	public String getAddress() {
		return address;
	}

	public String getType() {
		return type;
	}
	
	public String getBackupIp() {
		return backupIP;
	}
	
	@Override
	public boolean equals(final Object o) {
		final DefaultAddressInformation addressInfo = (DefaultAddressInformation) o;
		return getAddress().equals(addressInfo.getAddress()) && 
			getType().equals(addressInfo.getType());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append("Address: ");
		b.append(address);
		b.append(", Type: ");
		b.append(type);
		return b.toString();
	}

}
