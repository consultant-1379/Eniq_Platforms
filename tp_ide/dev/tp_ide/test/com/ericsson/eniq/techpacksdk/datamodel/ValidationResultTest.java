package com.ericsson.eniq.techpacksdk.datamodel;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
*
* @author eneacon
*
*/


public class ValidationResultTest{
  
  @Test
  public void testGetAction() {
    
    String testMsg = "test string";
    
    try {
      
      //new ValidationResult().setMessage(testMsg);
      
      //String returnMsg = new ValidationResult().getMessage();
      
      ValidationResult vr = new ValidationResult();
      
      vr.setMessage(testMsg);
      
      String returnMsg = vr.getMessage();
      
      assertEquals("Names should be the same", testMsg, returnMsg);
      
      
    } catch (Exception e) {

    }
  }
   
}