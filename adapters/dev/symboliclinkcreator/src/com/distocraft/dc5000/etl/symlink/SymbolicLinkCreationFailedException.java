package com.distocraft.dc5000.etl.symlink;

public class SymbolicLinkCreationFailedException extends Exception{
	
	public SymbolicLinkCreationFailedException(final String errorMessage) {
		super(errorMessage);
		System.out.println("exception ::: " + errorMessage);
	}
	
}
