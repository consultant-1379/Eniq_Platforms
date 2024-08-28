package com.ericsson.navigator.esm.model.pm;


public class RegexpCounterSetIdentification extends DefaultCounterSetIdentification {
	
	private final String regExp;

	public RegexpCounterSetIdentification(final String regExp, final String fileName) {
		super(fileName);
		this.regExp = regExp;
	}

	public String getRegularExpression() {
		return regExp;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj) && obj instanceof RegexpCounterSetIdentification && 
			regExp.equals(((RegexpCounterSetIdentification)obj).regExp);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append("regExp=\"");
		b.append(regExp);
		b.append("\",");
		b.append(super.toString());
		return b.toString();
	}
}
