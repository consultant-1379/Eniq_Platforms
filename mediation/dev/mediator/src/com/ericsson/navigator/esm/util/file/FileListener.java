
package com.ericsson.navigator.esm.util.file;


import java.io.File;



/**
 * This is the interface for listening for file updates.
 * @author qnilsje
 */
public interface FileListener
{
    /** 
     * <p>Title: getAlarmDefinition</p>
     * <p>Called when one of the monitored files are created, deleted or modified.</p>
     * 
     * @param File  
     */ 
	void fileChanged (File file);
}
