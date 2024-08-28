/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

/**
 * @author eheijun
 * 
 */
public interface Key {
  
  /**
   * Get name of the Key
   * @return keyName
   */
  String getKeyName();
  
  /**
   * Set name for the Key
   * @param keyName
   */
  void setKeyName(String keyName);
  
  /**
   * Get value of the Key
   * @return keyValue
   */
  String getKeyValue();
  
  /**
   * Set value for the key
   * @param keyValue
   */
  void setKeyValue(String keyValue);

  /**
   * Get source of the key
   * @return source or null if not exists
   */
  String getSource();
  
  void setSource(String source);
  
  /**
   * Get true if key is created
   */
  Boolean isNew();

  /**
   * Get true if key is modified
   */
  Boolean isModified();

  

}
