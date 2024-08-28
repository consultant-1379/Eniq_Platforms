/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;


/**
 * @author eheijun
 *
 */
public interface Source {
  
  /**
   * Get typename of the Source table
   * @return the typename
   */
  String getTypename();
  
  /**
   * Set new typename for the Source 
   * @param typename the typename to set
   */
  void setTypename(String typename);
  
  /**
   * Get true if source is created
   */
  Boolean isNew();

  /**
   * Get true if source is modified
   */
  Boolean isModified();

}
