package com.distocraft.dc5000.etl.symlink;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * 
 * @author xnagdas
 *
 */
class SymbolicLinkWriter{

    
    /**
     * Represents the link to be created by this object
     */
    final EniqSymbolicLink eniqSymbolicLink;

    /**
     * @param eniqSymbolicLink
     */
    public SymbolicLinkWriter(final EniqSymbolicLink eniqSymbolicLink) {
        this.eniqSymbolicLink = eniqSymbolicLink;
    } 
   
	/**
	 * Calls the NIO Files API to create Symbolic links
	 */
	public void createSymbolicLink() {
		try {

			/*
			 * Convert the string paths to Path using Paths utility and pass to
			 * NIO Files utility to create symbolic links
			 */						
			Files.createSymbolicLink(Paths.get(eniqSymbolicLink.getNewName()),Paths.get(eniqSymbolicLink.getOldName()));
			
		} catch (final FileAlreadyExistsException faee) {
			System.out.println("FileAlreadyExistsException: Wiil not be creating the Symbolic Link - "+ eniqSymbolicLink);
			System.out.println("faeee   ::"  + faee);
		} catch (final Exception ex) { 
			System.out.println(" Exception: Symbolic Link creation is failed ex ::: " + ex);
		}

	}

}
