package com.ericsson.eniq.busyhourcfg.database;

import java.util.List;

import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;

/**
 * @author eheijun 
 */

/**
 * 
 */
public interface TechPackReader {

  /**
   * Get list of techpack version ids 
   * @return a list containing all TechPack Version Ids
   */
  List<String> getAllActivatedPMTypeVersionIds();
  
  /**
   * Get active tech pach by versionId
   * @param VersionId to get
   * @return a TechPack 
   */
  TechPack getTechPackByVersionId(String versionId);
  
  /**
   * Get all possible sourcetable techpacks for busyhour
   * @return
   */
  List<String> getAllBusyhourSourceTechpacks();

  /**
   * Get all possible basetables for busyhour source techpack
   * @param versionid
   * @return list of measurement tables
   */
  List<String> getAllBusyhourBasetables(String versionid);

}
