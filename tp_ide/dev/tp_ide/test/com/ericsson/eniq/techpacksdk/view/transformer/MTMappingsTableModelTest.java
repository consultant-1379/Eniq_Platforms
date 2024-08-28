package com.ericsson.eniq.techpacksdk.view.transformer;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.jdesktop.application.SingleFrameApplication;
import org.junit.After;
import org.junit.Before;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;

/**
 * Test for MTMappingsTableModel.
 * @author eciacah
 */
public class MTMappingsTableModelTest extends TestCase {
  
  // Mocks:
  private TransformerDataModel transformerDataModelMock;  
  private SingleFrameApplication applicationMock;
  private TransformerData transformerDataMock;
  private static String ALL_TRANSFORMER_ID = "DC_E_STN:ALL:mdc";
  private TableTreeComponent ttcMock;       
  
  public MTMappingsTableModelTest() {
    super("MTMappingsTableModelTest");
  }

  @Before
  protected void setUp() throws Exception {
    transformerDataModelMock = createNiceMock(TransformerDataModel.class);
    applicationMock = createNiceMock(SingleFrameApplication.class);
    transformerDataMock = createNiceMock(TransformerData.class); 
    ttcMock = createNiceMock(TableTreeComponent.class);
    
    // Disable logging to console:
    MTMappingsTableModel.getLogger().setUseParentHandlers(false);
  }

  @After
  protected void tearDown() throws Exception {
    //
  }
    
  /**
   * Tests getColumnValue(), transformer ID.
   * @throws Exception
   */
  public void testGetColumnValue() {        
    // Transformation mock:
    Transformation t1Mock = createNiceMock(Transformation.class);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    
    
    // Test list of all transformers:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    final List<Transformer> transformerList = new Vector<Transformer>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerList.add(transformerMock);
    transformerList.add(transformerMock2);
    transformerList.add(transformerMock3);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableModel testInstance = createTestInstance(t1Mock, ttcMock, transformerList, mappings);
    
    final String testTransformerID = "DC_E_STN:mdc:E1";
    
    // Next go through all of the transformers:
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(testTransformerID);
    
    replay(transformerDataModelMock);
    replay(t1Mock);
    replay(transformerMock);
                
    final String result = (String) testInstance.getColumnValue(transformerMock, 0);
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(transformerMock);
        
    Assert.assertTrue("Transformer value should be correct", result.equals("E1"));
  }

  /**
   * Helper method to create a test instance of MTMappingsTableModel.
   * @param t1Mock
   * @param ttcMock
   * @param transformerList
   * @param mappings
   * @param dataformatType
   * @return
   */
  protected MTMappingsTableModel createTestInstance(Transformation t1Mock, final TableTreeComponent ttcMock,
      final List<Transformer> transformerList, HashMap<Transformation, ArrayList<String>> mappings) {
    @SuppressWarnings("serial")
    
    final String dataformatType = "mdc";
    MTMappingsTableModel testInstance = new MTMappingsTableModel(transformerList, t1Mock, mappings, transformerDataModelMock, dataformatType) {
      protected TableTreeComponent getTableTreeComp(String dataformatType) {
        return ttcMock;
      }
    };
    return testInstance;
  }
  
  /**
   * Tests getColumnValue(), transformer ID with incorrect value.
   * @throws Exception
   */
  public void testGetColumnValueIncorrectData() {        
    // Transformation mock:
    Transformation t1Mock = createNiceMock(Transformation.class);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    final List<Transformer> transformerList = new Vector<Transformer>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerList.add(transformerMock);
    transformerList.add(transformerMock2);
    transformerList.add(transformerMock3);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    
    // Test instance of MTMappingsTableCellEditor:
    MTMappingsTableModel testInstance = createTestInstance(t1Mock, ttcMock, transformerList, mappings);
    
    // Incorrect value without ':'
    final String testTransformerID = "TEST_VALUE";
    
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(testTransformerID);
    
    replay(transformerDataModelMock);
    replay(t1Mock);
    replay(transformerMock);
            
    final String result = (String) testInstance.getColumnValue(transformerMock, 0);
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(transformerMock);
    
    Assert.assertTrue("Transformer value should be empty string if transformer ID is corrupted", result.equals(""));
  }
  
  /**
   * Tests getColumnValue(), no mappings for enabled/disabled column.
   * @throws Exception
   */
  public void testGetColumnValueEnabledColumnNoMappings() {        
    // Transformation mock:
    Transformation t1Mock = createNiceMock(Transformation.class);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    final List<Transformer> transformerList = new Vector<Transformer>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerList.add(transformerMock);
    transformerList.add(transformerMock2);
    transformerList.add(transformerMock3);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableModel testInstance = createTestInstance(t1Mock, ttcMock, transformerList, mappings);
    
    replay(transformerDataModelMock);
    replay(t1Mock);
    replay(transformerMock);
    
    final Boolean result = (Boolean) testInstance.getColumnValue(transformerMock, 1);
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(transformerMock);
                    
    Assert.assertTrue("Enabled value should be false if there are no mappings found", result == false);
  }
  
  /**
   * Tests getColumnValue(), mapping found for enabled/disabled column.
   * @throws Exception
   */
  public void testGetColumnValueEnabledColumn() {        
    // Transformation mock:
    Transformation t1Mock = createNiceMock(Transformation.class);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    final List<Transformer> transformerList = new Vector<Transformer>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerList.add(transformerMock);
    transformerList.add(transformerMock2);
    transformerList.add(transformerMock3);
    
    // Mappings:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    final String testTransformerID = "TEST_VALUE";
    transformers.add(testTransformerID);
    mappings.put(t1Mock, transformers);
    
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(testTransformerID);
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableModel testInstance = createTestInstance(t1Mock, ttcMock, transformerList, mappings); 
    
    replay(transformerDataModelMock);
    replay(t1Mock);
    replay(transformerMock);
    
    final Boolean result = (Boolean) testInstance.getColumnValue(transformerMock, 1);
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(transformerMock);
                    
    Assert.assertTrue("Enabled value should be true if there is a mapping", result == true);
  }
  
  /**
   * Tests getColumnValue(), mapping found for enabled/disabled column.
   * @throws Exception
   */
  public void testSetColumnValueEnabled() {        
    // Transformation mock:
    Transformation t1Mock = createNiceMock(Transformation.class);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    final List<Transformer> transformerList = new Vector<Transformer>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerList.add(transformerMock);
    
    // Mappings:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();

    final String testTransformerID = "TEST_VALUE";
    
    // Transformer will have a mapping:
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(testTransformerID).times(2);
    
    t1Mock.setTransformerid("TEST_VALUE,");
    EasyMock.expectLastCall();
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableModel testInstance = createTestInstance(t1Mock, ttcMock, transformerList, mappings); 
        
    // User ticks and enables the box:
    Boolean selection = true;

    replay(transformerMock);
    replay(transformerDataModelMock);
    replay(t1Mock);
    
    testInstance.setColumnValue(transformerMock, 1, selection);
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(transformerMock);

    Assert.assertTrue("Mappings should contain transformer ID after it is enabled", testInstance.getMappings().get(t1Mock).contains(testTransformerID));
  }
  
  /**
   * Tests getColumnValue(), mapping found for enabled/disabled column.
   * @throws Exception
   */
  public void testSetColumnValueDisabled() {        
    // Transformation mock:
    Transformation t1Mock = createNiceMock(Transformation.class);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    final List<Transformer> transformerList = new Vector<Transformer>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerList.add(transformerMock);
    
    // Mappings:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();            
    final String testTransformerID = "TEST_VALUE";
    
    // Transformer will have a mapping:
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(testTransformerID);
    
    // No transformers will be enabled (list is empty):
    t1Mock.setTransformerid("");
    EasyMock.expectLastCall();
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableModel testInstance = createTestInstance(t1Mock, ttcMock, transformerList, mappings); 
        
    // User ticks and disables the box:
    Boolean selection = false;

    replay(transformerMock);
    replay(transformerDataModelMock);
    replay(t1Mock);
    
    testInstance.setColumnValue(transformerMock, 1, selection);
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(transformerMock);

    Assert.assertTrue("Mappings should contain no transformers after disabling", testInstance.getMappings().get(t1Mock).size() == 0);
  }
 
}



