/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.util.List;

/**
 * @author eheijun
 * 
 */
public class AFJMeasurementTag {

  private String tagName;

  private String dataformattype;

  private List<AFJMeasurementCounter> newCounters;

  /**
   * @return the tagName
   */
  public String getTagName() {
    return tagName;
  }

  /**
   * @param tagName
   *          the tagName to set
   */
  public void setTagName(final String tagName) {
    this.tagName = tagName;
  }

  /**
   * @return the newCounters
   */
  public List<AFJMeasurementCounter> getNewCounters() {
    return newCounters;
  }

  /**
   * @param newCounters
   *          the newCounters to set
   */
  public void setNewCounters(final List<AFJMeasurementCounter> newCounters) {
    this.newCounters = newCounters;
  }

  /**
   * Add new counter
   * 
   * @param newCounter
   */
  public void addNewCounter(final AFJMeasurementCounter newCounter) {
    this.newCounters.add(newCounter);
  }

  /**
   * Get parsertype
   * 
   * @return the dataformattype
   */
  public String getDataformattype() {
    return dataformattype;
  }

  /**
   * Set parsertype
   * 
   * @param dataformattype
   */
  public void setDataformattype(final String dataformattype) {
    this.dataformattype = dataformattype;
  }

}
