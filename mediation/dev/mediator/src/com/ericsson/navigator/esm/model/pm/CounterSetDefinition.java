package com.ericsson.navigator.esm.model.pm;

import java.util.List;
import java.util.Map;

public class CounterSetDefinition {

	public static final String MANAGED_SYSTEM = "<managedSystem>";
	//final private List<String> transformationNames;
	
	public CounterSetDefinition(final String definitionFile, final String northBound, 
			final List<String> moidStrings, final Map<String, CounterDefinition> definitions) {
		
		fileName = definitionFile;
		moid = concatenateMoidString(moidStrings);
		this.definitions = definitions;
		this.northBound = northBound;
		//this.transformationNames=transformationNames;
		
	}
	
	public String getMoidString(final String newReservedStr) {
		return moid.replace(MANAGED_SYSTEM, newReservedStr);
	}
	
	public String getCounterSetId() {
		return fileName.replace(".xml", "");
	}

	public String getFileName() {
		return fileName;
	}
	
	public Map<String, CounterDefinition> getDefinitions() {
		return definitions;
	}
	
	public String getNorthBoundLocation(){
		return northBound;
	}
	
	//public List<String> getTransformationNames(){
		//return transformationNames;
	//}
	
	/*PRIVATE*/
	private final String fileName;
	private final String moid;
	private final Map<String, CounterDefinition> definitions;
	private final String northBound;
	
	private String concatenateMoidString(final List<String> moidStrings) {
		final StringBuilder sb = new StringBuilder();
		for(String s: moidStrings) {
			sb.append(s);
		}
		return sb.toString();
	}
}
