/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.List;

/**
 * @author eheijun
 * 
 */
public interface TechPack {

  /**
   * Return version of the TechPack
   * 
   * @return the version id
   */
  String getVersionId();
  
  /**
   * Return name of the tp
   * @return
   */
  String getName();

  /**
   * Return modified value of the TechPack
   * 
   * @return 0, 1 or 2
   */
  Integer getModified(); 

  /**
   * Add a new target tech pack to this container.
   * 
   * @param tp
   *          the target tech pack to add.
   */
  void addTargetTechPack(TargetTechPack targettp);

  /**
   * Get an target tech pack given by versionid.
   * 
   * @param name
   *          the name to search for
   * @return the target tech pack or null if none was found
   */
  TargetTechPack getTargetTechPackByVersionId(String versionId);

  /**
   * @return an enumeration of all the target tech packs.
   */
  List<TargetTechPack> getTargetTechPacks();

}
