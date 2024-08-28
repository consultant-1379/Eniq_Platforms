package com.ericsson.eniq.flssymlink.symlink;

import java.io.File;
import java.util.logging.Logger;

import com.ericsson.eniq.flssymlink.StaticProperties;

/**
 * 
 * @author xnagdas
 *
 */
public class EniqSymbolicLink {

    private final String eniqHardLinkPath;

    private final String eniqSimlinkPath;
    
    private final String eniqPMDataDirectory;

    private static String pmicStorageDir = StaticProperties.getProperty("PMIC_STORAGE_PATH", "/ericsson" ) ;
    
    Logger log;

    /**
     * All directory names should end with @File.separator
     * 
     */
    public EniqSymbolicLink(final String eniqPMDataDirectory, final String linkNodeTypeDir, final String linkSubDir,
            final String eniqImportDataDirectory, final String absoluteFileName, Logger log ) {
    	this.log = log;
    	this.eniqPMDataDirectory = eniqPMDataDirectory;
        final String relativeFileName = getRelativeFileName(absoluteFileName);
        // Use StringBuilder for Maximum Efficiency to create oldName (i.e. the file the Symbolic Link will point to)
        StringBuilder builder = new StringBuilder();
        builder.append(eniqImportDataDirectory);
        builder.append(relativeFileName);
        this.eniqHardLinkPath = builder.toString();

        // Create newName (i.e. the Symbolic Link)
        builder = new StringBuilder();
        builder.append(eniqPMDataDirectory);
        builder.append(linkNodeTypeDir);
        builder.append(linkSubDir);
        builder.append(getFilename(absoluteFileName));
        this.eniqSimlinkPath = builder.toString();
        
       /* log.fine("EniqSymbolicLink  ::  " +"\n "+ " ImportData Path  ::  " + this.eniqHardLinkPath  
        		+ " \n "  + " Simlink Path  :::  "+ this.eniqSimlinkPath);*/
    }

    /**
     * @return - the absolute location of the eniqSimlinkPath
     */
    public String getNewName() {
        return eniqSimlinkPath;
    }

    /**
     * @return - the absolute location of the eniqHardLinkPath
     */
    public String getOldName() {
        return eniqHardLinkPath;
    }

    public String getEniqPMDataDirectory() {
		return eniqPMDataDirectory;
	}

	/**
     * Returns pmic1/XML/FDN/FILE_NAME
     * from /ericsson/pmic1/XML/FDN/FILE_NAME
     * 
     * Note: pmicStorageDir should not end with File.separator
     * @param absoluteFileName
     * @return
     */
    private String getRelativeFileName(final String absoluteFileName) {
        return absoluteFileName.substring(pmicStorageDir.length() + 1);
    }

    /**Returns only the fileName from 
     * from /ericsson/pmic1/XML/FDN/FILE_NAME
     * @param absoluteFileName
     * @return
     */
    private String getFilename(final String absoluteFileName) {
        return absoluteFileName.substring(absoluteFileName.lastIndexOf(File.separator) + 1);

    }
}