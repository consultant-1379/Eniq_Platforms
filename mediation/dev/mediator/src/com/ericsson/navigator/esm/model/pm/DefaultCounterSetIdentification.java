package com.ericsson.navigator.esm.model.pm;


public class DefaultCounterSetIdentification implements CounterSetIdentification {
 
	private final String fileName;

	public DefaultCounterSetIdentification(final String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public String getCounterSetId() {
		return fileName.replace(".xml", "");
	}
	
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof DefaultCounterSetIdentification && 
			fileName.equals(((DefaultCounterSetIdentification)obj).fileName);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append("fileName=\"");
		b.append(fileName);
		b.append("\"");
		return b.toString();
	}
}
