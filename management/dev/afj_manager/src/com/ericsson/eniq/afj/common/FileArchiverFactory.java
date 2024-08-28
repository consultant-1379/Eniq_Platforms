/**
 * 
 */
package com.ericsson.eniq.afj.common;


/**
 * @author eheijun
 *
 */
public class FileArchiverFactory {
  
  public static FileArchiver _fileArchiver;
  
  /**
   * Hidden constructor
   */
  private FileArchiverFactory() {}
  
  /**
   * get instance (and create default instance if it does not exists)
   * @return
   */
  public static FileArchiver getInstance() {
    if (_fileArchiver == null) {
      _fileArchiver = new MIMFileArchiver();
    }
    return _fileArchiver;
  }
  
  /**
   * Method to replace default instance
   * @param fileArchiver
   */
  public static void setInstance(final FileArchiver fileArchiver) {
    _fileArchiver = fileArchiver;
  }
  

}
