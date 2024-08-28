/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import com.ericsson.eniq.busyhourcfg.data.vo.Placeholder;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;


/**
 * @author eheijun
 *
 */
public interface BusyhourUpdater {
  
  /**
   * Save all placeholder data into repository. Function does not peform validity checks
   * but validatePlaceholder method should be called before this.  
   * @param placeholder
   * @throws DatabaseException
   */
  void savePlaceholder(Placeholder placeholder) throws DatabaseException;

  /**
   * Validates given placeholder.  
   * @param placeholder
   * @return empty string if it is OK to save placeholder or error message if there is some problems
   */
  String validatePlaceholder(Placeholder placeholder);
  
  /**
   * Recreate placeholder views of the Techpack
   * @param techpack
   * @throws DatabaseException 
   */
  void recreateViews(TechPack techpack) throws DatabaseException;
  
  /**
   * Create automatically rankkey data for placeholder
   * @param placeholder
   * @throws DatabaseException 
   */
  void generateBusyhourrankkeys(Placeholder placeholder) throws DatabaseException;
  
}
