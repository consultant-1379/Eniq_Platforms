package com.ericsson.navigator.esm.model.pm;

import java.util.List;

public class DefaultCounterSetScheduling<CSI extends CounterSetIdentification> implements CounterSetScheduling<CSI> {

	private static final long serialVersionUID = 1L;
	private final String fdn;
	private final int rop;
	private final List<CSI> identifications;
	
	public DefaultCounterSetScheduling(final String fdn, final int rop, final List<CSI> identifications){
		this.fdn = fdn;
		this.rop = rop;
		this.identifications = identifications;
	}
	
	public String getFdn(){
		return fdn;
	}

	public int getRop() {
		return rop;
	}
	
	@Override
	public int getOffset() {
		//Returns zero for all CounterSetSchedulings
		return 0;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		final CounterSetScheduling<CSI> c = (CounterSetScheduling<CSI>)obj;
		return getFdn().equals(c.getFdn()) && getRop() == c.getRop() && 
			getIdentifications().equals(c.getIdentifications());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append("FDN=\"");
		b.append(fdn);
		b.append("\",ROP=\"");
		b.append(rop);
		b.append("\"");
		if(!identifications.isEmpty()){
			b.append(",identifications=[");
			boolean first = true;
			for(CounterSetIdentification identification : identifications){
				if(first){
					first = false;
				} else {
					b.append(",");
				}
				b.append(identification.toString());
			}
			b.append("]");
		}
		return b.toString();
	}

	@Override
	public List<CSI> getIdentifications() {
		return identifications;
	}
}
