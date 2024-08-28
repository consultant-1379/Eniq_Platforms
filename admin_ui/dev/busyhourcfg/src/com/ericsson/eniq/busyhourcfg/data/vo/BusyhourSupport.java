/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.List;

/**
 * @author eheijun
 * 
 */
public interface BusyhourSupport {

  /**
   * Get the level of the busyhour measurement type.
   * 
   * @return the bhlevel of the busyhour
   */
  String getBhlevel();

  /**
   * Retrieves the busyhour placeholders available for this BHSupport.
   * 
   * @return a vector of reports.
   */
  List<Placeholder> getPlaceholders();

  /**
   * Adds new busyhour placeholder for this BHSupport.
   * 
   * @param placeholder
   */
  void addPlaceHolder(Placeholder placeholder);
  
  /**
   * Get placeholder by bhtype
   * @param bhtype
   * @return required placeholder or null if none was found
   */
  Placeholder getPlaceholderByBhtype(String bhtype);

//  /**
//   * Get currently selected placeholder or first one if none is selected
//   * @return
//   */
//  Placeholder getCurrentPlaceholder();
//
//  /**
//   * Sets given placeholder as selected.
//   * 
//   * @param selected
//   *          placeholder or null if no one is selected.
//   */
//  void setCurrentPlaceholder(Placeholder selected);
  
}

