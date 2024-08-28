package com.ericsson.eniq.techpacksdk.unittest;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	com.ericsson.eniq.techpacksdk.unittest.GeneralIDETests.IdeGeneralTest.class,
	com.ericsson.eniq.techpacksdk.unittest.ManageTechPackTabTest.ManageTechPackViewTest.class,
//	//com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests.TechPackActivationTest.class,	
//	//com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests.TechPackDeActivationTest.class,
    com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.MeasurementTypesViewTest.class,    
	com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.SetsActionsSchedulingsViewTest.class,
	com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.BusyHoursViewTest.class,
	com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.DataFormatsViewTest.class,
    com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.ReferenceTypesViewTest.class,   
    com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.TransformersViewTest.class,
    com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.ExternalStatementsViewTest.class, 
    com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests.TechPackActivationTest.class,
    com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests.ActivateBHCriteriaTest.class,
    com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests.TechPackDeActivationTest.class,
    com.ericsson.eniq.techpacksdk.unittest.InterfaceGUITests.ManageInterfaceViewTest.class,    
    com.ericsson.eniq.techpacksdk.unittest.UniverseTests.UniverseViewTest.class,   
    com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests.TechPackUpgradeTest.class
   
  })

/*
 * This class in used for collecting all the TechPachIDE unit test cases into
 * one collection for running the entire set of GUI test cases in a specified
 * order.
 */
public class AllTests {
}
