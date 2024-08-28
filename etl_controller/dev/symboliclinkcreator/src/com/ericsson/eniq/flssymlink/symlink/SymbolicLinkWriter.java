package com.ericsson.eniq.flssymlink.symlink;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;


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
    
    Logger log;

    /**
     * @param eniqSymbolicLink
     */
    public SymbolicLinkWriter(final EniqSymbolicLink eniqSymbolicLink, Logger log) {
        this.eniqSymbolicLink = eniqSymbolicLink;
        this.log = log;
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
			log.info("Successfully created symbolic link :: " + eniqSymbolicLink.getNewName().substring( eniqSymbolicLink.getNewName().lastIndexOf(File.separator) +1 ));
			log.finest("Successfully created symbolic link!!" + "     \n   " + "  ImportData Path : " + eniqSymbolicLink.getOldName() + "  \n  " +"Simlink Path    : " + eniqSymbolicLink.getNewName() );
			
		} catch (final FileAlreadyExistsException faee) {
			log.warning("FileAlreadyExistsException: Wiil not be creating the Symbolic Link - "+ eniqSymbolicLink);
		} catch (final Exception ex) { 
			log.warning(" Exception: Symbolic Link creation is failed ex ::: " + ex);
		}

	}

}
