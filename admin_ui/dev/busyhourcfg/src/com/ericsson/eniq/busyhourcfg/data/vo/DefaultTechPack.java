/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eheijun
 * 
 */
public class DefaultTechPack implements TechPack {

  private final String versionid;

  private final String name;

  private final Integer modified;

  private final List<TargetTechPack> targetTechPacks;

  /**
   * @param name
   */
  public DefaultTechPack(final String versionid, final String name, final Integer modified) {
    this.versionid = versionid;
    this.name = name;
    this.modified = modified;
    this.targetTechPacks = new ArrayList<TargetTechPack>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TechPack#getVersionId()
   */
  @Override
  public String getVersionId() {
    return versionid;
  }
  
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TechPack#getModified()
   */
  @Override
  public Integer getModified() {
    return modified;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.TechPack#addTargetTechPack(com.ericsson
   * .eniq.busyhourcfg.data.vo.TargetTechPack)
   */
  @Override
  public void addTargetTechPack(final TargetTechPack targettp) {
    targetTechPacks.add(targettp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.TechPack#getTargetTechPackByVersionId(
   * java.lang.String)
   */
  @Override
  public TargetTechPack getTargetTechPackByVersionId(final String versionId) {
    for (final TargetTechPack item : targetTechPacks) {
      if (item.getVersionId().equals(versionId)) {
        return item;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.TechPack#getTargetTechPacks()
   */
  @Override
  public List<TargetTechPack> getTargetTechPacks() {
    return targetTechPacks;
  }

}
