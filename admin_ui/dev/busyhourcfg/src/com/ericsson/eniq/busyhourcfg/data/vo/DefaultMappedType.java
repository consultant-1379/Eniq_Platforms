/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

/**
 * @author eheijun
 * 
 */
public class DefaultMappedType implements MappedType {

  private String typeid;

  private String bhtargetlevel;

  private String bhtargettype;

  private Boolean enabled;

  private Boolean modified;

  /**
   * @param typeid
   * @param bhtargetlevel
   * @param bhtargettype
   * @param enabled
   */
  public DefaultMappedType(final String typeid, final String bhtargetlevel, final String bhtargettype, final Boolean enabled) {
    super();
    this.typeid = typeid;
    this.bhtargetlevel = bhtargetlevel;
    this.bhtargettype = bhtargettype;
    this.enabled = enabled;
    this.modified = false;
  }

  @Override
  public String getTypeid() {
    return typeid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.MappedType#getBhTargetlevel()
   */
  @Override
  public String getBhTargetlevel() {
    return bhtargetlevel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.MappedType#getBhTargettype()
   */
  @Override
  public String getBhTargettype() {
    return bhtargettype;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.MappedType#getEnabled()
   */
  @Override
  public Boolean getEnabled() {
    return enabled;
  }

  @Override
  public void setTypeid(final String typeid) {
    if (!this.typeid.equals(typeid)) {
      this.typeid = typeid;
      this.modified = true;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.MappedType#setBhTargetlevel(java.
   * lang.String)
   */
  @Override
  public void setBhTargetlevel(final String bhtargetlevel) {
    if (!this.bhtargetlevel.equals(bhtargetlevel)) {
      this.bhtargetlevel = bhtargetlevel;
      this.modified = true;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.MappedType#setBhTargettype(java.lang
   * .String)
   */
  @Override
  public void setBhTargettype(final String bhtargettype) {
    if (!this.bhtargettype.equals(bhtargettype)) {
      this.bhtargettype = bhtargettype;
      this.modified = true;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.MappedType#setEnabled(java.lang.Boolean
   * )
   */
  @Override
  public void setEnabled(final Boolean enabled) {
    if (!this.enabled.equals(enabled)) {
      this.enabled = enabled;
      this.modified = true;
    }
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.MappedType#isModified()
   */
  @Override
  public Boolean isModified() {
    return modified;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.MappedType#isNew()
   */
  @Override
  public Boolean isNew() {
    // mapped types can not be created in GUI so always false
    return false;
  }

}
