package com.ericsson.eniq.techpacksdk.view.newTechPack;

import javax.swing.JComboBox;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;
import org.junit.After;
import org.junit.Before;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Test for ManageNewTechPackView.
 * @author eciacah
 */
public class ManageNewTechPackViewTest extends TestCase {

  // Test instance:
  private ManageNewTechPackView testInstance = null;
  
  // Mocks:
  private LimitedSizeTextField versionTextField;
  private LimitedSizeTextField rStateTextField;
  private LimitedSizeTextField nameTextField;
  private LimitedSizeTextField universeNameField;
  private LimitedSizeTextField productTextField;
  private JComboBox typeComboBox;

  private static boolean SETTINGS_FALSE = false;
  
  public ManageNewTechPackViewTest() {
    super("ManageNewTechPackViewTest");
  }

  @Override
  @Before
  protected void setUp() throws Exception {
    versionTextField = createNiceMock(LimitedSizeTextField.class);
    rStateTextField = createNiceMock(LimitedSizeTextField.class);
    nameTextField = createNiceMock(LimitedSizeTextField.class);
    universeNameField = createNiceMock(LimitedSizeTextField.class);
    productTextField = createNiceMock(LimitedSizeTextField.class);
    typeComboBox = createNiceMock(JComboBox.class);
       
    // Create new test instance:
    final String tempVersionId = "new version";
    testInstance = new ManageNewTechPackView(tempVersionId, versionTextField, rStateTextField, nameTextField, 
        universeNameField, productTextField, typeComboBox);
  }

  @Override
  @After
  protected void tearDown() throws Exception {
    testInstance = null;
  }
    
  /**
   * Test that getTechPackName() does not return a string in upper case.
   * @throws Exception
   */
  public void testGetTechPackName() {    
    final String TECH_PCK_NM = "Tech Pack";
    
    // Set up expectation:
    nameTextField.getText();    
    expectLastCall().andReturn(TECH_PCK_NM);
   
    replay(nameTextField);
    
    final String result = testInstance.getTechPackName();
    final String techPackNameInUpperCase = result.toUpperCase();    
    final boolean checkName = techPackNameInUpperCase.equals(result);
    assertFalse("Tech pack name should not be converted to upper case", checkName);    
  }
 
  /**
   * Test that isCreateEnabledCustomSystemEvent() returns true for system, event and custom tech packs.
   * @throws Exception
   */
  public void testIsCreateEnabledCustomSystemEvent() throws Exception {
    // expectations:
    expectIsCreateEnabledCustomSystemEvent();

    boolean result = testInstance.isCreateEnabledCustomSystemEvent(true, Constants.SYSTEM_TECHPACK);
    assertEquals("isCreateEnabledCustomSystemEvent() should return true for system tech packs", true, result);
    
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    expectIsCreateEnabledCustomSystemEvent();
    
    result = testInstance.isCreateEnabledCustomSystemEvent(true, Constants.CUSTOM_TECHPACK);
    assertEquals("isCreateEnabledCustomSystemEvent() should return true for custom tech packs", true, result);
    
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    expectIsCreateEnabledCustomSystemEvent();
    
    result = testInstance.isCreateEnabledCustomSystemEvent(true, Constants.EVENT_TECHPACK);
    assertEquals("isCreateEnabledCustomSystemEvent() should return true for event tech packs", true, result);
    
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
  }
  
  /**
   * Test that isCreateEnabledCustomSystemEvent() returns false for PM tech packs.
   * @throws Exception
   */
  public void testIsCreateEnabledCustomSystemEventWrongTP() throws Exception {
    // expectations:
    expectIsCreateEnabledCustomSystemEvent();

    boolean result = testInstance.isCreateEnabledCustomSystemEvent(true, Constants.PM_TECHPACK);
    assertEquals("isCreateEnabledCustomSystemEvent should return false for PM tech packs", false, result);
        
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
  }
  
  /**
   * Test that isCreateEnabledCustomSystemEvent() returns false if settings are incorrect.
   * @throws Exception
   */
  public void testIsCreateEnabledCustomSystemEventWrongSettings() throws Exception {
    // expectations:
    expectIsCreateEnabledCustomSystemEvent();
    
    boolean result = testInstance.isCreateEnabledCustomSystemEvent(SETTINGS_FALSE, Constants.PM_TECHPACK);
    assertEquals("isCreateEnabledCustomSystemEvent should return false if settings are wrong", false, result);
        
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
  }
  
  /**
   * Set up common expectations for a call to isCreateEnabledCustomSystemEvent().
   */
  private void expectIsCreateEnabledCustomSystemEvent() {
    // Set up expectations:
    rStateTextField.getText();    
    expectLastCall().andReturn("rState");
    replay(rStateTextField);
    
    versionTextField.getText();    
    expectLastCall().andReturn("version").times(2);
    replay(versionTextField);
            
    nameTextField.getText();    
    expectLastCall().andReturn("name");
    replay(nameTextField);        
  }
    
  // ============================================
  /**
   * Test that isCreateEnabledPMCM() returns true for PM and CM tech packs.
   * @throws Exception
   */
  public void testIsCreateEnabledPMCM() throws Exception {
    // expectations:
    expectIsCreateEnabledPMCM();

    boolean result = testInstance.isCreateEnabledPMCM(true, Constants.PM_TECHPACK);
    assertEquals("isCreateEnabledPMCM() should return true for PM tech packs", true, result);
    
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    reset(universeNameField);
    reset(productTextField);
    expectIsCreateEnabledPMCM();
    
    result = testInstance.isCreateEnabledPMCM(true, Constants.CM_TECHPACK);
    assertEquals("isCreateEnabledPMCM() should return true for CM tech packs", true, result);
    
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
    verify(universeNameField);
    verify(productTextField);
  }
  
  /**
   * Test that isCreateEnabledPMCM() returns false for tech packs that are not PM and CM .
   * @throws Exception
   */
  public void testIsCreateEnabledPMCMWrongTP() throws Exception {
    // expectations:
    expectIsCreateEnabledPMCM();

    boolean result = testInstance.isCreateEnabledPMCM(true, Constants.SYSTEM_TECHPACK);
    assertEquals("isCreateEnabledPMCM() should return false for wrong tech packs", false, result);
    
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    reset(universeNameField);
    reset(productTextField);
    expectIsCreateEnabledPMCM();
    
    result = testInstance.isCreateEnabledPMCM(true, Constants.BASE_TECHPACK);
    assertEquals("isCreateEnabledPMCM() should return false for wrong tech packs", false, result);
    
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
    verify(universeNameField);
    verify(productTextField);
  }
  
  /**
   * Test that isCreateEnabledPMCM() returns false if settings are incorrect.
   * @throws Exception
   */
  public void testIsCreateEnabledPMCMWrongSettings() throws Exception {
    // expectations:
    expectIsCreateEnabledPMCM();

    boolean result = testInstance.isCreateEnabledPMCM(SETTINGS_FALSE, Constants.SYSTEM_TECHPACK);
    assertEquals("isCreateEnabledPMCM() should return false if settings are wrong", false, result);
    
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
    verify(universeNameField);
    verify(productTextField);
  }
  
  /**
   * Set up common expectations for a call to isCreateEnabledPMCM().
   */
  private void expectIsCreateEnabledPMCM() {
    // Set up expectations:
    rStateTextField.getText();    
    expectLastCall().andReturn("rState");
    replay(rStateTextField);
    
    versionTextField.getText();    
    expectLastCall().andReturn("version").times(2);
    replay(versionTextField);
            
    nameTextField.getText();    
    expectLastCall().andReturn("name");
    replay(nameTextField);
    
    universeNameField.getText();    
    expectLastCall().andReturn("universe name");
    replay(universeNameField);   
    
    productTextField.getText();    
    expectLastCall().andReturn("product");
    replay(productTextField);     
  }
  
  //============================================
  /**
   * Test that isCreateEnabledBaseSystemTopology() returns true for Base, System and Topology tech packs.
   * @throws Exception
   */
  public void testIsCreateEnabledBaseSystemTopology() throws Exception {
    // expectations:
    expectIsCreateEnabledBaseSystemTopology();

    boolean result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(true, Constants.BASE_TECHPACK);
    assertEquals("Universe creation should be enabled for system tech packs", true, result);
    
    reset(productTextField);
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    expectIsCreateEnabledBaseSystemTopology();
    
    result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(true, Constants.SYSTEM_TECHPACK);
    assertEquals("Universe creation should be enabled for system tech packs", true, result);
    
    reset(productTextField);
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    expectIsCreateEnabledBaseSystemTopology();
    
    result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(true, Constants.TOPOLOGY_TECHPACK);
    assertEquals("Universe creation should be enabled for system tech packs", true, result);
    
    verify(productTextField);
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
  }
    
  /**
   * Test that isCreateEnabledBaseSystemTopology() returns true for PM and CM tech packs.
   * @throws Exception
   */
  public void testIsCreateEnabledBaseSystemTopologyWrongTP() throws Exception {
    // expectations:
    expectIsCreateEnabledBaseSystemTopology();

    boolean result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(true, Constants.PM_TECHPACK);
    assertEquals("isCreateEnabledBaseSystemTopologyWrongTP() should return false for wrong tech packs", false, result);
    
    reset(productTextField);
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    expectIsCreateEnabledBaseSystemTopology();
    
    result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(true, Constants.CM_TECHPACK);
    assertEquals("isCreateEnabledBaseSystemTopologyWrongTP() should return false for wrong tech packs", false, result);
    
    reset(productTextField);
    reset(rStateTextField);
    reset(versionTextField);
    reset(nameTextField);
    expectIsCreateEnabledBaseSystemTopology();
    
    result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(true, Constants.EVENT_TECHPACK);
    assertEquals("isCreateEnabledBaseSystemTopologyWrongTP() should return false for wrong tech packs", false, result);
    
    verify(productTextField);
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
  }
  
  /**
   * Test that isCreateEnabledPMCMWrongSettings() returns false if settings are incorrect.
   * @throws Exception
   */
  public void testIsCreateEnabledBaseSystemTopologyWrongSettings() throws Exception {
    // expectations:
    expectIsCreateEnabledPMCM();

    boolean result = testInstance.isCreateEnabledBaseSystemTopologyEniqEvent(SETTINGS_FALSE, Constants.SYSTEM_TECHPACK);
    assertEquals("isCreateEnabledBaseSystemTopology() should return false if settings are wrong", false, result);
    
    verify(productTextField);
    verify(rStateTextField);
    verify(versionTextField);
    verify(nameTextField);
  }
  
  /**
   * Set up common expectations for a call to isCreateEnabledBaseSystemTopology().
   */
  private void expectIsCreateEnabledBaseSystemTopology() {
    // Set up expectations:
    productTextField.getText();    
    expectLastCall().andReturn("product");
    replay(productTextField);
    
    rStateTextField.getText();    
    expectLastCall().andReturn("rState");
    replay(rStateTextField);
    
    versionTextField.getText();    
    expectLastCall().andReturn("version").times(2);
    replay(versionTextField);
            
    nameTextField.getText();    
    expectLastCall().andReturn("name");
    replay(nameTextField);    
  }
//Testing of validation for invalid characters in Techpack name for EQEV-3659 (start)
  /**
   * Test that  doInBackground() that the new techpack name addded will having invalid characters as 
   * %,.,- and space
   * @throws Exception
   */
  public void testdoInBackground1() throws Exception
  {
        final String TECH_PACK_NAME = "TechPack.";
          
          // Set up expectation:
          nameTextField.getText();    
          expectLastCall().andReturn(TECH_PACK_NAME);
          replay(nameTextField);
        final String result = testInstance.getTechPackName();
        
        boolean flagstatus = true;
        if( result.contains("."))
        {
              flagstatus=false;
              
        }
        assertFalse(flagstatus);
  }
  
  public void testdoInBackground2() throws Exception
  {
        final String TECH_PACK_NAME = "TechPack%";
          
          // Set up expectation:
          nameTextField.getText();    
          expectLastCall().andReturn(TECH_PACK_NAME);
          replay(nameTextField);
        final String result = testInstance.getTechPackName();
        
        boolean flagstatus = true;
        if(result.contains("%"))
        {
              flagstatus=false;
              
        }
        assertFalse(flagstatus);
  }
  public void testdoInBackground3() throws Exception
  {
        final String TECH_PACK_NAME = "TechPack abc";
          
          // Set up expectation:
          nameTextField.getText();    
          expectLastCall().andReturn(TECH_PACK_NAME);
          replay(nameTextField);
        final String result = testInstance.getTechPackName();
        
        boolean flagstatus = true;
        if(result.contains(" "))
        {
              flagstatus=false;
              
        }
        assertFalse(flagstatus);
  }
  public void testdoInBackground4() throws Exception
  {
        final String TECH_PACK_NAME = "TechPack-";
          
          // Set up expectation:
          nameTextField.getText();    
          expectLastCall().andReturn(TECH_PACK_NAME);
          replay(nameTextField);
        final String result = testInstance.getTechPackName();
        
        boolean flagstatus = true;
        if(result.contains("-"))
        {
              flagstatus=false;
              
        }
        assertFalse(flagstatus);
  }
  
  
//Testing of validation for invalid characters in Techpack name for EQEV-3659 (end)

  
  
  
  

}
