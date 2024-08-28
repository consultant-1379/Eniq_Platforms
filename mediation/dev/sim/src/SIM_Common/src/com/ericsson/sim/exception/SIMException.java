package com.ericsson.sim.exception;

public class SIMException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message = null;
 
    public SIMException() {
        super();
    }
 
    public SIMException(String message) {
        super(message);
        this.message = message;
    }
 
    public SIMException(Throwable cause) {
        super(cause);
    }
 
    public SIMException(String message, Exception e) {
    	super(message, e);
    	this.message = message;
	}

	@Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
