/**
 * 
 */
package com.ericsson.eniq.exception;

/**
 * @author esunbal
 *
 */
public class AFJConfiguationException extends Exception{
	
  private static final long serialVersionUID = 9154580701571288285L;

  public AFJConfiguationException(final String slogan, final Exception cause){
		super(slogan,cause);
	}

	public AFJConfiguationException(final String slogan){
		super(slogan);
	}
	
}
