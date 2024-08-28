/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.util.List;

/**
 * @author esunbal
 * 
 */
public class AFJDelta {

  private String techPackName;

  private String techPackVersion;

  private List<AFJMeasurementType> measurementTypes;

  private String momFileName;

  /**
   * @return the techPackName
   */
  public String getTechPackName() {
    return techPackName;
  }

  /**
   * @param techPackName
   *          the techPackName to set
   */
  public void setTechPackName(final String techPackName) {
    this.techPackName = techPackName;
  }

  /**
   * @return the techPackVersion
   */
  public String getTechPackVersion() {
    return techPackVersion;
  }

  /**
   * @param techPackVersion
   *          the techPackVersion to set
   */
  public void setTechPackVersion(final String techPackVersion) {
    this.techPackVersion = techPackVersion;
  }

  /**
   * @return the measurmentTypes
   */
  public List<AFJMeasurementType> getMeasurementTypes() {
    return measurementTypes;
  }

  /**
   * @param measurmentTypes
   *          the measurmentTypes to set
   */
  public void setMeasurementTypes(final List<AFJMeasurementType> measurementTypes) {
    this.measurementTypes = measurementTypes;
  }

  /**
   * @return the momFileName
   */
  public String getMomFileName() {
    return momFileName;
  }

  /**
   * @param momFileName
   *          the momFileName to set
   */
  public void setMomFileName(final String momFileName) {
    this.momFileName = momFileName;
  }

}
