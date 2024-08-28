/**
 * 
 */
package com.ericsson.catalina.users;

import org.apache.catalina.UserDatabase;
import org.apache.catalina.users.MemoryUserDatabase;


/**
 * @author eheijun
 *
 */
public class EniqUserDatabaseSubjectFactory {
  
  private static UserDatabase _instance;
  
  /**
   * Hidden constructor.
   */
  private EniqUserDatabaseSubjectFactory() {
  }
  
  /**
   * Creates new UserDatabase instance. Default implementation class is MemoryUserDatabse
   * @param path
   * @param name
   * @param readonly
   * @return
   */
  public static UserDatabase getInstance(final String path, final String name, final Boolean readonly) {
    if (_instance == null) {
      final MemoryUserDatabase dataBase = new MemoryUserDatabase(name);
      if (path != null) {
        dataBase.setPathname(path);
      }
      if (readonly != null) {
        dataBase.setReadonly(readonly);
      }
      _instance = dataBase;
    }
    return _instance;
  }

  /**
   * Setter for alternative UserDatabase implementation
   */
  public static void setInstance(final UserDatabase instance) {
    _instance = instance;
  }

}
