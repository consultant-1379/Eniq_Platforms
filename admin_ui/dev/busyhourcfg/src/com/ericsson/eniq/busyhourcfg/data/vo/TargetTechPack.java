/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.List;

/**
 * @author eheijun
 * 
 */
public interface TargetTechPack {

  /**
   * Get the VersionId of the target techpack.
   * 
   * @return the name of the techpack
   */
  String getVersionId();

  // /**
  // * Checks if this target tp is selected in the UI
  // *
  // * @return true if the interface is selected.
  // */
  // boolean isSelected();
  //  
  // /**
  // * @param select
  // * the current tech pack.
  // */
  // void setSelected(boolean select);

  /**
   * Retrieves the busyhour supports available for this Target Tech Pack.
   * 
   * @return a vector of reports.
   */
  List<BusyhourSupport> getBusyhourSupports();

  /**
   * Adds new busyhour support for this Target Tech Pack
   * 
   * @param busyhourSupport
   */
  void addBusyhourSupport(BusyhourSupport busyhourSupport);

  /**
   * Gets bhsupport by bhlevel.
   * @param bhsupport
   * @return required bhsupport or null if none was found
   */
  BusyhourSupport getBusyhourSupportByBhlevel(String bhlevel);
  
  /**
   * Retrieves the counts of defined placeholders
   * @return
   */
  List<PlaceholderCount> getPlaceholderCounts();
  
  /**
   * Adds new placeholder count for this Target TP  
   * @param placeholderCount
   */
  void addPlaceholderCount(PlaceholderCount placeholderCount);
  
}
