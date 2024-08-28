package com.distocraft.dc5000.etl.importexport.gpmgt.exception;

/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * This exception is thrown if any of the data in the xml file is not
 * valie i.e. values not within database constraints
 */
public class GroupMgtInvalidDataException extends GroupMgtException {
    /**
     * Error message
     */
    private static final String SLOGAN = "Invalid Group Management Data.";

    /**
     * Additional info key for the input xml file
     */
    private static final String AINFO_DATA_FILE = "datafile:";

    /**
     * Additional info key for group type being imported
     */
    private static final String AINFO_GROUP_TYPE = "grouptype:";

    /**
     * Additional info key for group instance name being imported
     */
    private static final String AINFO_GROUP_NAME = "groupname:";

    /**
     * Additional info key for data key name
     */
    private static final String AINFO_GROUP_EKEY = "keyname:";

    /**
     * Additional info key for data value
     */
    private static final String AINFO_GROUP_EVAL = "value:";

    /**
     * Constructor
     * @param inFile Input data xml file
     * @param groupType The group type being imported
     * @param groupName The group instance name
     * @param keyName The data key name
     * @param keyValue The data value
     */
    public GroupMgtInvalidDataException(final String inFile, final String groupType, final String groupName,
            final String keyName, final String keyValue) {
        super(SLOGAN, null);
        if (null != inFile) {
            addAdditionalInfo(AINFO_DATA_FILE, inFile);
        }
        addAdditionalInfo(AINFO_GROUP_TYPE, groupType);
        addAdditionalInfo(AINFO_GROUP_NAME, groupName);
        addAdditionalInfo(AINFO_GROUP_EKEY, keyName);
        addAdditionalInfo(AINFO_GROUP_EVAL, keyValue);
    }
}
