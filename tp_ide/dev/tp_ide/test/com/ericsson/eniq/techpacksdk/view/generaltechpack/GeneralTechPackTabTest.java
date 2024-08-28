package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import static org.easymock.classextension.EasyMock.createNiceMock;

import java.awt.Component;


import javax.swing.*;


import junit.framework.TestCase;
import org.jdesktop.application.SingleFrameApplication;
import org.junit.After;
import org.junit.Before;


import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLSetHandlingView;
import java.lang.reflect.*;

/**
 * Test for GeneralTechPackTab.
 * 
 * @author eciacah
 */
public class GeneralTechPackTabTest extends TestCase {
	
	

  // Mocks:
  private SingleFrameApplication application;

  // Test instance:
  private GeneralTechPackTab testInstance = null;

  public GeneralTechPackTabTest() {
    super("GeneralTechPackTabTest");
  }

  @Override
  @Before
  protected void setUp() throws Exception {
    application = createNiceMock(SingleFrameApplication.class);
    
    JTabbedPane dummyTabbedPanel = new JTabbedPane();    
    dummyTabbedPanel.add("Tab1", null);
    dummyTabbedPanel.add("Tab2", null);
    dummyTabbedPanel.add("Tab3", null);
    
    //use javareflection to access tabFactory Field of CreateTabFactory.java
   Field tabFactoryField = GeneralTechPackTab.class.getDeclaredField("tabFactory");
   tabFactoryField.setAccessible(true);
    
    // Create new test instance:
    testInstance = new GeneralTechPackTab(application, dummyTabbedPanel);
    //set reflected field to dummyTabFactory.. so its overridden method is called instead of originals
    tabFactoryField.set(testInstance, new DummyTabFactory());       
    
  }

  @Override
  @After
  protected void tearDown() throws Exception {
    testInstance = null;
  }

  /**
   * Test that set type is 'TECHPACK' for different tech pack types.
   * Types are: "PM", "CM", "BASE", "SYSTEM", "Topology", "EVENT", "CUSTOM".
   * 
   * @throws Exception
   */
  public void testGetSetType() throws Exception {
    
    // PM
    String techPackName = "DC_E_BSS";
    String techPackType = "PM";
    int result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for product tech pack", ETLSetHandlingView.TECHPACK, result);
    
    // CM
    techPackName = "CM_TECH_PACK";
    techPackType = "CM";
    result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for CM tech pack",
        ETLSetHandlingView.TECHPACK, result);
    
    // BASE
    techPackName = "BASE_TP";
    techPackType = "BASE";
    result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for BASE tech pack",
        ETLSetHandlingView.TECHPACK, result);
    
    // SYSTEM
    techPackName = "DWH_MONITOR";
    techPackType = "SYSTEM";
    result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for system tech pack that is not ALARMINTERFACES",
        ETLSetHandlingView.TECHPACK, result);
    
    // Topology
    techPackName = "Topology_TP";
    techPackType = "Topology";
    result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for Topology tech pack",
        ETLSetHandlingView.TECHPACK, result);
    
    // EVENT
    techPackName = "An_Event_Tech_Pack";
    techPackType = "EVENT";
    result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for an event tech pack", ETLSetHandlingView.TECHPACK, result);
    
    // CUSTOM
    techPackName = "Custom_Tech_Pack";
    techPackType = "CUSTOM";
    result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'TECHPACK' for a custom tech pack", ETLSetHandlingView.TECHPACK, result);
  }

  /**
   * Test that set type is 'MAINTENANCE' for ALARMINTERFACES system tech pack.
   * 
   * @throws Exception
   */
  public void testGetSetTypeForAlarmIntfTP() throws Exception {
    final String techPackName = "ALARMINTERFACES";
    final String techPackType = "SYSTEM";

    final int result = testInstance.getSetType(techPackName, techPackType);
    assertEquals("Set type should be 'MAINTENANCE' for system tech pack that is ALARMINTERFACES",
        ETLSetHandlingView.MAINTENANCE, result);
  }
  
  //20110201,EANGUAN,HN44746: Test case for the fix for this TR
  //This Test case tests the condition that after closing the
  //View/Edit window, all the data structures of GeneralTechPackTab
  // class should be Null.
  public void testCloseDialog() throws Exception {
	  //Calling the closeDialog method of GeneralTechPackTab
	  testInstance.closeDialog();
	  assertNull("DataModelController:dataModelController of GeneralTechPackTab is not Null.",testInstance.dataModelController);
	  assertNull("JTable:tpT of GeneralTechPackTab is not Null.",testInstance.tpT);
	  assertNull("JTable:tppedencyT of GeneralTechPackTab is not Null.",testInstance.tppedencyT);
	  assertNull("JFrame:frame of GeneralTechPackTab is not Null.", testInstance.frame);
	  assertNull("JTabbedPane:tabbedPanel is not null.",testInstance.getJTabbedPane());
	  assertNull("ManageGeneralPropertiesView:generalTab is not Null.",testInstance.getManageGeneralPropertiesView());
  }
  
  // Create a dummy tabFactory class and override the method in it.
  class DummyTabFactory extends CreateTabFactory {

	public Component createTab(String tabName){
		  if(tabName.equals("Tab1")){
			  JPanel panel = new JPanel();
			//Component obj1 = new JComponent();
			  return panel;
		  }else if(tabName.equals("Tab2")){
			  JButton button =  new JButton();
			  return button;
		  }else if( tabName.equals("Tab3")){
			  JTable table =  new JTable();
			  return table;
		  }else
		return null;
		  
	  }
  }
  
  public void testCreateAllDataModelsIfNotCreated() throws Exception {
	  JTabbedPane tabbedPanel = testInstance.getJTabbedPane();
	  // Before the method is called components should be null.
	  for(int i=0;i<tabbedPanel.getTabCount();i++){
		  assertNull("Tab components should be null before this method call",tabbedPanel.getComponentAt(i));
	  }
	  //Now call the method to create tab components.
	  testInstance.createAllDataModelsIfNotCreated();
	  
	  //test if components are added to their relevant tabs.
	  assertTrue (" A JPanel should be added to Tab1 ",tabbedPanel.getComponentAt(0) instanceof JPanel && tabbedPanel.getTitleAt(0).equalsIgnoreCase("Tab1"));
	  assertTrue (" A JButton should be added to Tab2 ",tabbedPanel.getComponentAt(1) instanceof JButton && tabbedPanel.getTitleAt(1).equalsIgnoreCase("Tab2"));
	  assertTrue (" A JTable should be added to Tab3 ",tabbedPanel.getComponentAt(2) instanceof JTable && tabbedPanel.getTitleAt(2).equalsIgnoreCase("Tab3"));

	
	  
  }

  
}
