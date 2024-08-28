/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;


/**
 * @author eheijun
 *
 */
public class DefaultPlaceholderCount implements PlaceholderCount {

  private final String bhlevel;
  private final Integer custom;
  private final Integer product;

  /**
   * @param bhlevel
   * @param custom
   * @param product
   */
  public DefaultPlaceholderCount(final String bhlevel, final Integer custom, final Integer product) {
    super();
    this.bhlevel = bhlevel;
    this.custom = custom;
    this.product = product;
  }

  /* (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceholderCount#getBhlevel()
   */
  @Override
  public String getBhlevel() {
    return bhlevel;
  }

  /* (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceholderCount#getCustom()
   */
  @Override
  public Integer getCustom() {
    return custom;
  }

  /* (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceholderCount#getProduct()
   */
  @Override
  public Integer getProduct() {
    return product;
  }

}
