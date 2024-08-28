package com.ericsson.eniq.techpacksdk.datamodel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
*
* @author eneacon
*
*/


public class UniverseDataModelTest{
  
  @Test
  public void testModObj() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockDBObject rocky = null;
      
      boolean bool = udm.modObj(rocky);
      
     
      
      assertEquals("Should return false", false, bool);
      
      
    } catch (Exception e) {

    }
  }
   
  
  
  @Test
  public void testSetGetModel() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      DataModel measModel = null;
      
      udm.setMeasurementDataModel(measModel);
      
      DataModel newModel = udm.getMeasurementDataModel();
      
      assertEquals("The 2 DataModels should be the same", newModel, measModel);
      
      
    } catch (Exception e) {

    }
  }
  
  
  
  
  @Test
  public void testDelObj() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockDBObject rocky = null;
      
      boolean bool = udm.delObj(rocky);
      
     
      
      assertEquals("Should return false", false, bool);
      
      
    } catch (Exception e) {

    }
  }
  
  
  
  
  @Test
  public void testNewObj() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockDBObject rocky = null;
      
      boolean bool = udm.newObj(rocky);
      
     
      
      assertEquals("Should return false", false, bool);
      
      
    } catch (Exception e) {

    }
  }
  
  
  
  @Test
  public void testValidateDel() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockDBObject rocky = null;
      
      boolean bool = udm.validateDel(rocky);
      
     
      
      assertEquals("Should return false", false, bool);
      
      
    } catch (Exception e) {

    }
  }
  
  
  @Test
  public void testValidateMod() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockDBObject rocky = null;
      
      boolean bool = udm.validateMod(rocky);
      
     
      
      assertEquals("Should return false", false, bool);
      
      
    } catch (Exception e) {

    }
  }
  
  
  @Test
  public void testValidateNew() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockDBObject rocky = null;
      
      boolean bool = udm.validateNew(rocky);
      
     
      
      assertEquals("Should return false", false, bool);
      
      
    } catch (Exception e) {

    }
  }
  
  
  @Test
  public void testUpdated() {
    
    
    
    try {
      
UniverseDataModel udm = new UniverseDataModel(null);
      
      DataModel measModel = null;
      
      boolean bool = udm.updated(measModel);
      
      
      assertEquals("Should return false", false, bool);
      
    } catch (Exception e) {

    }
  }
  
  
  @Test
  public void testgetRockFactory() {
    
    
    
    try {
      
      UniverseDataModel udm = new UniverseDataModel(null);
      
      RockFactory rock = udm.getRockFactory();
      
      
      assertNull("Should return null", rock);
   
      
      
    } catch (Exception e) {

    }
  }
  
  
}