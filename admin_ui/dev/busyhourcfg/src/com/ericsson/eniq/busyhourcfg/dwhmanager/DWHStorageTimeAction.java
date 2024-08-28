/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.dwhmanager;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;


/**
 * @author eheijun
 *
 */
public interface DWHStorageTimeAction {

  /**
   * Call dropBhRankViews method of the StorageTimeAcrtion
   * @param busyhour
   */
  void performDropBhRankViews(Busyhour busyhour) throws DWHManagerException;

  /**
   * Call createBhRankViews method of the StorageTimeAcrtion
   * @param busyhour
   */
  void performCreateBhRankViews(Busyhour busyhour) throws DWHManagerException;
  
//  /**
//   * Create view creation SQL
//   * @param busyhour
//   * @return
//   * @throws DWHManagerException
//   */
//  String getCustomBhViewCreates(Busyhour busyhour) throws DWHManagerException;
//  
//  /**
//   * Create view creation SQL
//   * @param busyhour
//   * @return
//   * @throws DWHManagerException
//   */
//  String getPlaceholderCreateStatement(Busyhour busyhour, String techpackVersionId, String techpackName)
//      throws DWHManagerException;
  
  /**
   * Call updateBHCounters method of the StorageTimeAcrtion
   * @param dwhtechpack
   */
  void updateBHCounters(Dwhtechpacks dwhtechpack);
  
  /**
   * Call updateBHCountersForCustomTechpack method of the StorageTimeAcrtion
   * @param dwhtechpack
   */
  void updateBHCountersForCustomTechpack(Dwhtechpacks dwhtechpack);
  
}
