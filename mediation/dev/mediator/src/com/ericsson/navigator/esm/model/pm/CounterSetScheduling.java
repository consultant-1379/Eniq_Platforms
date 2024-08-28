package com.ericsson.navigator.esm.model.pm;

import java.util.List;


public interface CounterSetScheduling<CSI extends CounterSetIdentification>{
	
	String getFdn();
	int getRop();
	int getOffset();
	boolean equals(final Object obj);
	List<CSI> getIdentifications();
	int hashCode();
	String toString();
}
