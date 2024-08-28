/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.io.File;
import java.io.IOException;


/**
 * @author eheijun
 *
 */
public interface FileArchiver {
  
  /**
   * Backs up given file into given dir and removes original.
   * @param srcFile backup file
   * @param dstDir destination dir 
   * @return destination file
   * @throws IOException
   */
  File backupFile(File srcFile, File dstDir) throws IOException;

}
