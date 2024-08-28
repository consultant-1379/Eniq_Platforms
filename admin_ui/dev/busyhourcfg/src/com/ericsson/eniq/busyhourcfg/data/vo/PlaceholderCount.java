/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;


/**
 * @author eheijun
 *
 */
public interface PlaceholderCount {
  
  /**
   * Get the level of the busyhour measurement type.
   * 
   * @return the bhlevel of the busyhour
   */
  String getBhlevel();
  
  /**
   * Return count of product placeholders
   * @return
   */
  Integer getProduct();
  
  /**
   * Return count of custom placeholders
   * @return
   */
  Integer getCustom();

}
