package com.distocraft.dc5000.etl.importexport.gpmgt.exception;

/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * This exception will be thrown during the import/export if the Group type is
 * not defined in the Tech Pack
 */
public class GroupMgtTypeNotDefinedException extends GroupMgtException {
    /**
     * Error message
     */
    private static final String SLOGAN = "GroupType is not defined in TechPack";

    /**
     * Additional info key for Tech Pack VersionID
     */
    private static final String AINFO_VERSIONID = "versionid:";

    /**
     * Additional info key for input data file
     */
    private static final String AINFO_DFILE = "datafile:";

    /**
     * Additional info key for Group type not found 
     */
    private static final String AINFO_GPTYPE = "grouptype:";

    /**
     * Constructor
     * @param importFile Input datafile being imported
     * @param tpVersionId The Tech Pack version the groups belong too
     * @param undefinedType the Group type that's not defined.
     */
    public GroupMgtTypeNotDefinedException(final String importFile, final String tpVersionId, final String undefinedType) {
        super(SLOGAN, null);
        addAdditionalInfo(AINFO_VERSIONID, tpVersionId);
        if (null != importFile) {
            addAdditionalInfo(AINFO_DFILE, importFile);
        }
        addAdditionalInfo(AINFO_GPTYPE, undefinedType);
    }

    /**
     * Constructor
     * @param tpVersionId The Tech Pack version the groups belong too
     * @param undefinedType the Group type that's not defined.
     */
    public GroupMgtTypeNotDefinedException(final String tpVersionId, final String undefinedType) {
        this(null, tpVersionId, undefinedType);
    }
}
