/**
 * 
 */
package com.ericsson.eniq.exception;

/**
 * Top level exception handling class for AFJ Manager.
 * @author esunbal
 *
 */
public class AFJException extends Exception {

  private static final long serialVersionUID = -2547566265524360140L;

  public AFJException(final String slogan, final Exception cause){
		super(slogan,cause);
	}
	
	public AFJException(final String slogan){
		super(slogan);
	}
}
