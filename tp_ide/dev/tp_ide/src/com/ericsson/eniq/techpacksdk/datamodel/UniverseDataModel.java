package com.ericsson.eniq.techpacksdk.datamodel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;


public class UniverseDataModel implements DataModel {

  private RockFactory rockFactory;
  
  private DataModel measurementDataModel;

  public UniverseDataModel(RockFactory rockFactory) {
    this.rockFactory = rockFactory;
  }
  
  public DataModel getMeasurementDataModel() {
    return measurementDataModel;
  }

  
  public void setMeasurementDataModel(DataModel measurementDataModel) {
    this.measurementDataModel = measurementDataModel;
  }

  public boolean delObj(RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public RockFactory getRockFactory() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean modObj(RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean newObj(RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public void refresh() {
    // TODO Auto-generated method stub
    
  }

  public void save() {
    // TODO Auto-generated method stub
    
  }

  public boolean validateDel(RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean validateMod(RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean validateNew(RockDBObject obj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean modObj(RockDBObject[] rObj) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean updated(DataModel dataModel) {
    // TODO Auto-generated method stub
    return false;
  }

  
}
