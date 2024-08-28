package com.ericsson.eniq.teststubs;

import java.util.Vector;

/**
 * This abstract class can be used as a super class for stub classes used in 
 * unit testing. The class contains methods for storing information on the 
 * method calls performed on the sub class.
 * 
 * The inheriting stub class must store the method call information by using
 * addMethodCall(String methodCall) method in its own methods. The method
 * getMethodCalls() can then be used by the unit test class to examine which
 * of the stub classes methods have been called, and in what order. 
 * 
 * @author epiituo
 */
public abstract class AbstractTestStub {
	
	private Vector<String> methodCalls = new Vector<String>();

	protected void addMethodCall(String methodCall) {
		this.methodCalls.addElement(methodCall);
	}
	
	public String[] getMethodCalls() {
		String[] resultArray = new String[this.methodCalls.size()];
		this.methodCalls.toArray(resultArray);
		
		return resultArray;
	}
	
}
