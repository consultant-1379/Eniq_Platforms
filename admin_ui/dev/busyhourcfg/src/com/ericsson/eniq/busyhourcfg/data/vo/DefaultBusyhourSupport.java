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
public class DefaultBusyhourSupport implements BusyhourSupport {

  private final String bhlevel;

  private final List<Placeholder> placeholders;

//  private Placeholder currentPlaceholder;

  public DefaultBusyhourSupport(final String bhlevel) {
    this.bhlevel = bhlevel;
    this.placeholders = new ArrayList<Placeholder>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport#getBhlevel()
   */
  @Override
  public String getBhlevel() {
    return bhlevel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport#getPlaceholders()
   */
  @Override
  public List<Placeholder> getPlaceholders() {
    return placeholders;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport#addPlaceHolder(com
   * .ericsson.eniq.busyhourcfg.data.vo.Placeholder)
   */
  @Override
  public void addPlaceHolder(final Placeholder placeholder) {
    placeholders.add(placeholder);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport#getPlaceholderByBhtype(java.lang.String)
   */
  @Override
  public Placeholder getPlaceholderByBhtype(final String bhtype) {
    for (Placeholder placeholder : placeholders) {
      if (placeholder.getBhtype().equals(bhtype)) {
        return placeholder;
      }
    }
    return null;
  }

//  /*
//   * (non-Javadoc)
//   * @see com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport#getCurrentPlaceholder()
//   */
//  @Override
//  public Placeholder getCurrentPlaceholder() {
////    if (currentPlaceholder == null) {
////      if (placeholders.size() > 0) {
////        return placeholders.get(0);
////      }
////    }
//    return currentPlaceholder;
//  }
//
//  /*
//   * (non-Javadoc)
//   * @see com.ericsson.eniq.busyhourcfg.data.vo.BusyhourSupport#setCurrentPlaceholder(com.ericsson.eniq.busyhourcfg.data.vo.Placeholder)
//   */
//  @Override
//  public void setCurrentPlaceholder(final Placeholder selected) {
//    currentPlaceholder = selected;
//  }

}
