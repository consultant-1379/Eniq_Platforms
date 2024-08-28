/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;


/**
 * @author eheijun
 *
 */
public class DefaultSource implements Source {
  
  private String typename;
  private final String originalname;
  
  /**
   * @param typename
   */
  public DefaultSource(final String typename) {
    super();
    this.typename = typename;
    this.originalname = typename;
  }


  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Source#getTypename()
   */
  @Override
  public String getTypename() {
    return typename;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Source#setTypename(java.lang.String)
   */
  @Override
  public void setTypename(final String typename) {
    if (!this.typename.equals(typename)) {
      this.typename = typename;
    }

  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Source#isModified()
   */
  @Override
  public Boolean isModified() {
    return !originalname.equals(typename);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Source#isNew()
   */
  @Override
  public Boolean isNew() {
    return originalname.trim().equals("");
  }

}
