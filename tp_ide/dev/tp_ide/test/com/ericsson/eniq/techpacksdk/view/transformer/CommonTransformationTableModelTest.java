package com.ericsson.eniq.techpacksdk.view.transformer;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.sql.SQLException;
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

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;

/**
 * Test for CommonTransformationTableModel.
 * @author eciacah
 */
public class CommonTransformationTableModelTest extends TestCase {
  
  // Mocks:
  private TransformerDataModel transformerDataModelMock;  
  private SingleFrameApplication applicationMock;
  private TransformerData transformerDataMock;
  private static String ALL_TRANSFORMER_ID = "DC_E_STN:ALL:mdc";
  private static String DATA_FORMAT_TYPE = "mdc";
  
  public CommonTransformationTableModelTest() {
    super("CommonTransformationTableModelTest");
  }

  @Before
  protected void setUp() throws Exception {
    transformerDataModelMock = createNiceMock(TransformerDataModel.class);
    applicationMock = createNiceMock(SingleFrameApplication.class);
    transformerDataMock = createNiceMock(TransformerData.class);    
    // Disable logging to console:
    MTMappingsTableCellEditor.getLogger().setUseParentHandlers(false);
  }

  @After
  protected void tearDown() throws Exception {
    //
  }
    
  /**
   * Tests setValueAt() where a transformation is changed.
   * It is then technically a new transformation. 
   * Situation is where there is no mapping for the "new" transformation so it needs to be created. 
   * @throws Exception
   */
  public void testSetValueAt() {    
    
    RockFactory rockFactoryMock = createNiceMock(RockFactory.class);
    
    // All transformations:
    final Transformation t1Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    // Transformers from each of these TransformerData objects:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> oldMappings = new HashMap<Transformation, ArrayList<String>>();
    
    Vector<TableInformation> tableInfos = new Vector<TableInformation>();       
    
    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    CommonTransformationTableModel testInstance = new CommonTransformationTableModel(applicationMock, transformerDataModelMock,
        rockFactoryMock, tableInfos, true, transformerDataList, allTransformations, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE) {
      
      protected Transformation getTransformationFromData(final int row) {
        return t1Mock;
      }
      
      // Override GUI related calls. This allows us to test the logic of the method in isolation:
      protected void setColumnValue(final Transformation transformation, final int col, final Object value) {
        // override this for test
      }
      
      public void refreshTable() {
        // overridden
      }
      
      public void fireTableDataChanged() {
        // overridden
      }     
    };
            
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    
    EasyMock.expect(t1Mock.getTransformerid()).andReturn("ALL"); // This transformation is enabled for all.
    
    // Go through all of the transformers:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock);
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("SPECIFIC1");
    
    EasyMock.expect(transformerDataMock2.getTransformer()).andReturn(transformerMock2);
    EasyMock.expect(transformerMock2.getTransformerid()).andReturn("SPECIFIC2");
    
    EasyMock.expect(transformerDataMock3.getTransformer()).andReturn(transformerMock3);
    EasyMock.expect(transformerMock3.getTransformerid()).andReturn("SPECIFIC3");

    replay(transformerDataModelMock);
    replay(t1Mock);    
    replay(transformerMock);
    replay(transformerMock2);
    replay(transformerMock3);
    replay(transformerDataMock);
    replay(transformerDataMock2);
    replay(transformerDataMock3);
            
    testInstance.setValueAt((Object) t1Mock, 0, 0);
    
    Assert.assertTrue("Mappings should contain SPECIFIC1 for transformation", oldMappings.get(t1Mock).contains("SPECIFIC1"));
    Assert.assertTrue("Mappings should contain SPECIFIC2 for transformation", oldMappings.get(t1Mock).contains("SPECIFIC2"));
    Assert.assertTrue("Mappings should contain SPECIFIC3 for transformation", oldMappings.get(t1Mock).contains("SPECIFIC3"));
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);    
    EasyMock.verify(transformerMock);
    EasyMock.verify(transformerMock2);
    EasyMock.verify(transformerMock3);
    EasyMock.verify(transformerDataMock);
    EasyMock.verify(transformerDataMock2);
    EasyMock.verify(transformerDataMock3);          
  }
  
  /**
   * Tests setValueAt() where a transformation is changed.
   * It is then technically a new transformation. 
   * Situation is where there is no mapping for the "new" transformation so it needs to be created.
   * Only a subset of transformers are enabled. These should be preserved.
   * @throws Exception
   */
  public void testSetValueAtSubset() {    
    
    RockFactory rockFactoryMock = createNiceMock(RockFactory.class);
    
    // All transformations:
    final Transformation t1Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    // Transformers from each of these TransformerData objects:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> oldMappings = new HashMap<Transformation, ArrayList<String>>();
    
    Vector<TableInformation> tableInfos = new Vector<TableInformation>();
    
    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    CommonTransformationTableModel testInstance = new CommonTransformationTableModel(applicationMock, transformerDataModelMock,
        rockFactoryMock, tableInfos, true, transformerDataList, allTransformations, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE) {
      
      protected Transformation getTransformationFromData(final int row) {
        return t1Mock;
      }
      
      // Override GUI related calls. This allows us to test the logic of the method in isolation:
      protected void setColumnValue(final Transformation transformation, final int col, final Object value) {
        // override this for test
      }
      
      public void refreshTable() {
        // overridden
      }
      
      public void fireTableDataChanged() {
        // overridden
      }     
    };
            
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    
    // This transformation is enabled only for a few transformers (SPECIFIC1 and SPECIFIC2):
    EasyMock.expect(t1Mock.getTransformerid()).andReturn("SPECIFIC1, SPECIFIC2,").anyTimes(); 
    
    // Go through all of the transformers:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock);
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("SPECIFIC1");
    
    EasyMock.expect(transformerDataMock2.getTransformer()).andReturn(transformerMock2);
    EasyMock.expect(transformerMock2.getTransformerid()).andReturn("SPECIFIC2");
    
    EasyMock.expect(transformerDataMock3.getTransformer()).andReturn(transformerMock3);
    EasyMock.expect(transformerMock3.getTransformerid()).andReturn("SPECIFIC3");

    replay(transformerDataModelMock);
    replay(t1Mock);    
    replay(transformerMock);
    replay(transformerMock2);
    replay(transformerMock3);
    replay(transformerDataMock);
    replay(transformerDataMock2);
    replay(transformerDataMock3);
            
    testInstance.setValueAt((Object) t1Mock, 0, 0);
    
    Assert.assertTrue(oldMappings.get(t1Mock).contains("SPECIFIC1"));
    Assert.assertTrue(oldMappings.get(t1Mock).contains("SPECIFIC2"));
    Assert.assertFalse(oldMappings.get(t1Mock).contains("SPECIFIC3"));
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);    
    EasyMock.verify(transformerMock);
    EasyMock.verify(transformerMock2);
    EasyMock.verify(transformerMock3);
    EasyMock.verify(transformerDataMock);
    EasyMock.verify(transformerDataMock2);
    EasyMock.verify(transformerDataMock3);          
  }
  
  /**
   * Tests setValueAt() where a transformation is changed.
   * It is then technically a new transformation. 
   * Situation is where there is are existing mappings for the transformation. 
   * The existing mappings should be put into the HashMap after transformation is updated.
   * No calculating of the mappings needs to be done, they just need to be restored for the new transformation.
   * @throws Exception
   */
  public void testSetValueAtWithMappings() {    
    
    RockFactory rockFactoryMock = createNiceMock(RockFactory.class);
    
    // All transformations:
    final Transformation t1Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    // Mappings, we do have mappings in this situation:
    HashMap<Transformation, ArrayList<String>> oldMappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    oldMappings.put(t1Mock, transformers);
    
    Vector<TableInformation> tableInfos = new Vector<TableInformation>();
    
    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    CommonTransformationTableModel testInstance = new CommonTransformationTableModel(applicationMock, transformerDataModelMock,
        rockFactoryMock, tableInfos, true, transformerDataList, allTransformations, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE) {
      
      protected Transformation getTransformationFromData(final int row) {
        return t1Mock;
      }
      
      // Override GUI related calls. This allows us to test the logic of the method in isolation:
      protected void setColumnValue(final Transformation transformation, final int col, final Object value) {
        // override this for test
      }
      
      public void refreshTable() {
        // overridden
      }
      
      public void fireTableDataChanged() {
        // overridden
      }     
    };
            
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();

    replay(transformerDataModelMock);
            
    testInstance.setValueAt((Object) t1Mock, 0, 0);
    
    Assert.assertTrue("Mappings should contain transformer1 for transformation", oldMappings.get(t1Mock).contains("transformer1"));
    Assert.assertTrue("Mappings should contain transformer2 for transformation", oldMappings.get(t1Mock).contains("transformer2"));
    Assert.assertTrue("Mappings should contain transformer3 for transformation", oldMappings.get(t1Mock).contains("transformer3"));
    
    EasyMock.verify(transformerDataModelMock);       
  }
  
  
  /**
   * Tests the method that actually updates a transformation when
   * the user changes a cell.
   */
  public void testSetColumnValue() {
    RockFactory rockFactoryMock = createNiceMock(RockFactory.class);
    
    // All transformations:
    final Transformation t1Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    // Mappings, we do have mappings in this situation:
    HashMap<Transformation, ArrayList<String>> oldMappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    oldMappings.put(t1Mock, transformers);
    
    Vector<TableInformation> tableInfos = new Vector<TableInformation>();
    
    // Test instance of CommonTransformationTableModel:
    CommonTransformationTableModel testInstance = new CommonTransformationTableModel(applicationMock, transformerDataModelMock,
        rockFactoryMock, tableInfos, true, transformerDataList, allTransformations, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE);
    
    // Set source should be called with "test value" only, no carriage returns or tabs should be included:
    t1Mock.setSource("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);    
    testInstance.setColumnValue(t1Mock, 0, "test value");
    EasyMock.verify(t1Mock);
        
    // Target
    // Set target should be called with "test value" only, no carriage returns or tabs should be included:
    EasyMock.reset(t1Mock);
    t1Mock.setTarget("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);    
    testInstance.setColumnValue(t1Mock, 1, "test value");
    EasyMock.verify(t1Mock);
        
    // Config:
    EasyMock.reset(t1Mock);        
    // Set config should be called with "test value" only, no carriage returns or tabs should be included:       
    EasyMock.expect(t1Mock.getConfig()).andReturn("config value");
    EasyMock.expect(t1Mock.getType()).andReturn("type value");
    
    t1Mock.setConfig("config value");
    EasyMock.expectLastCall();
    t1Mock.setType("type value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);    
    testInstance.setColumnValue(t1Mock, 3, t1Mock);
    EasyMock.verify(t1Mock);   
    
    // Description:
    // Set description should be called with "test value" only, no carriage returns or tabs should be included:
    EasyMock.reset(t1Mock);
    t1Mock.setDescription("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);    
    testInstance.setColumnValue(t1Mock, 4, "test value");
    EasyMock.verify(t1Mock);
  }
  
  
  public void testCreateNew() {    
    
    RockFactory rockFactoryMock = createNiceMock(RockFactory.class);
    
    // All transformations:
    final Transformation t1Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    // Transformers from each of these TransformerData objects:
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    // Mappings, we do have mappings in this situation:
    HashMap<Transformation, ArrayList<String>> oldMappings = new HashMap<Transformation, ArrayList<String>>();
    
    Vector<TableInformation> tableInfos = new Vector<TableInformation>();
    
    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    CommonTransformationTableModel testInstance = new CommonTransformationTableModel(applicationMock, transformerDataModelMock,
        rockFactoryMock, tableInfos, true, transformerDataList, allTransformations, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE) {
      
      protected Transformation createNewTransformation() {
        return t1Mock;
      }
    };
            
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    
    t1Mock.setTransformerid("ALL");
    EasyMock.expectLastCall();
    
    t1Mock.setType("fixed");
    EasyMock.expectLastCall();
    
    t1Mock.setOrderno(0l);
    EasyMock.expectLastCall();
    
    t1Mock.setSource("");
    EasyMock.expectLastCall();
    
    t1Mock.setTarget("");    
    EasyMock.expectLastCall();
    
    // Set validate needs to be false:
    t1Mock.setValidateData(false);
    EasyMock.expectLastCall();    
    
    // Go through all of the transformers:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock);
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("SPECIFIC1");
    
    EasyMock.expect(transformerDataMock2.getTransformer()).andReturn(transformerMock2);
    EasyMock.expect(transformerMock2.getTransformerid()).andReturn("SPECIFIC2");
    
    EasyMock.expect(transformerDataMock3.getTransformer()).andReturn(transformerMock3);
    EasyMock.expect(transformerMock3.getTransformerid()).andReturn("SPECIFIC3");
    
    replay(transformerDataModelMock);
    replay(t1Mock);    
    replay(transformerMock);
    replay(transformerMock2);
    replay(transformerMock3);
    replay(transformerDataMock);
    replay(transformerDataMock2);
    replay(transformerDataMock3);
            
    final RockDBObject result = testInstance.createNew();
    
    Assert.assertNotNull("Newly created transformation should not be null", result);
       
    Assert.assertTrue("The mappings for a new common transformation should contain all of the transformers", 
        oldMappings.get(t1Mock).contains("SPECIFIC1"));
    Assert.assertTrue("The mappings for a new common transformation should contain all of the transformers", 
        oldMappings.get(t1Mock).contains("SPECIFIC2"));
    Assert.assertTrue("The mappings for a new common transformation should contain all of the transformers", 
        oldMappings.get(t1Mock).contains("SPECIFIC3"));
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);    
    EasyMock.verify(transformerMock);
    EasyMock.verify(transformerMock2);
    EasyMock.verify(transformerMock3);
    EasyMock.verify(transformerDataMock);
    EasyMock.verify(transformerDataMock2);
    EasyMock.verify(transformerDataMock3);          
  }
  
  /**
   * Test deleting data. Should remove all mappings for a transformation.
   * @throws RockException 
   * @throws SQLException 
   */
  public void testDeleteData() throws SQLException, RockException {    
    RockFactory rockFactoryMock = createNiceMock(RockFactory.class);
    
    // All transformations:
    final Transformation t1Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    // Mappings, we do have mappings in this situation:
    HashMap<Transformation, ArrayList<String>> oldMappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    oldMappings.put(t1Mock, transformers);
    
    Vector<TableInformation> tableInfos = new Vector<TableInformation>();
    
    // Test instance of CommonTransformationTableModel:
    CommonTransformationTableModel testInstance = new CommonTransformationTableModel(applicationMock, transformerDataModelMock,
        rockFactoryMock, tableInfos, true, transformerDataList, allTransformations, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE);
            
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    
    replay(transformerDataModelMock);
    replay(t1Mock);    
            
    testInstance.deleteData(t1Mock);    
       
    Assert.assertNull("There should be no mappings after a common tranformation is deleted", 
        oldMappings.get(t1Mock));
    
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);            
  }

}


