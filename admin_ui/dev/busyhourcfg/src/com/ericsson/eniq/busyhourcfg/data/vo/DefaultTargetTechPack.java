package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.ArrayList;
import java.util.List;

public class DefaultTargetTechPack implements TargetTechPack {

  private final String versionid;

  private final List<BusyhourSupport> busyhourSupports;

  private final List<PlaceholderCount> placeholderCounts;

  public DefaultTargetTechPack(final String versionid) {
    this.versionid = versionid;
    this.busyhourSupports = new ArrayList<BusyhourSupport>();
    this.placeholderCounts = new ArrayList<PlaceholderCount>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack#getVersionId()
   */
  @Override
  public String getVersionId() {
    return versionid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack#getBusyhourSupports()
   */
  @Override
  public List<BusyhourSupport> getBusyhourSupports() {
    return busyhourSupports;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack#addBusyhourSupport
   * (com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport)
   */
  @Override
  public void addBusyhourSupport(final BusyhourSupport busyhourSupport) {
    busyhourSupports.add(busyhourSupport);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack#getBusyhourSupportByBhlevel(java.lang.String)
   */
  @Override
  public BusyhourSupport getBusyhourSupportByBhlevel(final String bhlevel) {
    for (BusyhourSupport busyhourSupport : busyhourSupports) {
      if (busyhourSupport.getBhlevel().equals(bhlevel)) {
        return busyhourSupport;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack#getPlaceholderCounts()
   */
  @Override
  public List<PlaceholderCount> getPlaceholderCounts() {
    return placeholderCounts;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TargetTechPack#addPlaceholderCount(com.ericsson.eniq.busyhourcfg.data.vo.PlaceholderCount)
   */
  @Override
  public void addPlaceholderCount(final PlaceholderCount placeholderCount) {
    placeholderCounts.add(placeholderCount);
  }

}
