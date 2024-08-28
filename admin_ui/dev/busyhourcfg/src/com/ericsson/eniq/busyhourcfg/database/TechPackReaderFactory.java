/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;


/**
 * @author eheijun
 *
 */
public class TechPackReaderFactory {
  
  private static TechPackReader reader;
  
  private TechPackReaderFactory() {}

  /**
   * Get techpack database reader  
   * @param databaseSession
   * @return
   */
  public static TechPackReader getTechPackReader(final DatabaseSession databaseSession) {
    if (reader == null) {
      return new RockTechPackReader(databaseSession);
    }
    return reader;
  }
  
  /**
   * @return the factory
   */
  public static TechPackReader getReader() {
    return reader;
  }

  
  /**
   * @param factory the factory to set
   */
  public static void setReader(final TechPackReader reader) {
    TechPackReaderFactory.reader = reader;
  }

  
}
