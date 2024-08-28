/**
 * 
 */
package com.ericsson.eniq.afj.common;


/**
 * @author eheijun
 *
 */
public class AFJMeasurementCounter {
  
  private String counterName;

  private Boolean counterNew;
  
  private String subType;
  
  private String type;
  
  private String resultType;
  
  private String description;

  /**
   * @return the measurementCounterName
   */
  public String getCounterName() {
    return counterName;
  }

  /**
   * @param counterName the counterName to set
   */
  public void setCounterName(final String counterName) {
    this.counterName = counterName;
  }

  
  /**
   * @return the counterNew
   */
  public Boolean isCounterNew() {
    return counterNew;
  }

  
  /**
   * @param counterNew the counterNew to set
   */
  public void setCounterNew(final Boolean counterNew) {
    this.counterNew = counterNew;
  }

/**
 * @return the subType
 */
public String getSubType() {
	return subType;
}

/**
 * @param subType the subType to set
 */
public void setSubType(final String subType) {
	this.subType = subType;
}

/**
 * @return the type
 */
public String getType() {
	return type;
}

/**
 * @param type the type to set
 */
public void setType(final String type) {
	this.type = type;
}

/**
 * @return the resultType
 */
public String getResultType() {
	return resultType;
}

/**
 * @param resultType the resultType to set
 */
public void setResultType(final String resultType) {
	this.resultType = resultType;
}

/**
 * @return the description
 */
public String getDescription() {
	return description;
}

/**
 * @param description the description to set
 */
public void setDescription(final String description) {
	this.description = description;
} 
  
}
