/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

/**
 * @author eheijun
 * 
 */
public interface MappedType {
 	
  String getTypeid();
  void setTypeid(String typeid);
  
  String getBhTargettype();
  
  /**
   * TODO
   * @param bhtargettype
   */
  void setBhTargettype(String bhtargettype);
  
  /**
   * TODO
   * @return
   */
  String getBhTargetlevel();

  /**
   * TODO
   * @param bhtargetlevel
   */
  void setBhTargetlevel(String bhtargetlevel);
  
  /**
   * TODO
   * @return
   */
  Boolean getEnabled();
  
  /**
   * TODO
   * @param enabled
   */
  void setEnabled(Boolean enabled);

  /**
   * Get true if type is created
   */
  Boolean isNew();

  /**
   * Get true if type is modified
   */
  Boolean isModified();

}
