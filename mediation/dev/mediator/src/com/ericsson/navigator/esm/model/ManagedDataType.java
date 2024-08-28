package com.ericsson.navigator.esm.model;

public enum ManagedDataType {
	Alarm, CounterSet, Marker;
	
	@Override
	public String toString() {
		switch(ordinal()){
		case 0:
			return "Alarm";
		case 1:
			return "CounterSet";
		case 2:
			return "Marker";
		default:
			return "Marker";
		}
	}	
}
