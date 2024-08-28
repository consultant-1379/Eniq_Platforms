package com.distocraft.dc5000.etl.symlink;

import java.io.File;

/**
 * 
 * @author xnagdas
 *
 */
public class EniqSymbolicLink {

    private final String oldName;

    private final String newName;

    private static String pmicStorageDir = "/ericsson";

    /**
     * All directory names should end with @File.separator
     * 
     */
    public EniqSymbolicLink(final String symbolicLinkFolder, final String linkNodeTypeDir, final String linkSubDir,
            final String segment1OnEniq, final String absoluteFileName) {
        final String relativeFileName = getRelativeFileName(absoluteFileName);
        // Use StringBuilder for Maximum Efficiency to create oldName (i.e. the file the Symbolic Link will point to)
        StringBuilder builder = new StringBuilder();
        builder.append(segment1OnEniq);
        builder.append(relativeFileName);
        this.oldName = builder.toString();

        // Create newName (i.e. the Symbolic Link)
        builder = new StringBuilder();
        builder.append(symbolicLinkFolder);
        builder.append(linkNodeTypeDir);
        builder.append(linkSubDir);
        builder.append(getFilename(absoluteFileName));
        this.newName = builder.toString();
        
        System.out.println("EniqSymbolicLink  ::  " +"\n "+ " this.oldName  ::  " + this.oldName  + " \n "  + "this.newName  :::  "+ this.newName);
    }

    /**
     * @return - the absolute location of the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @return - the absolute location of the oldName
     */
    public String getOldName() {
        return oldName;
    }

    /**
     * Returns XML/FDN/FILE_NAME
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