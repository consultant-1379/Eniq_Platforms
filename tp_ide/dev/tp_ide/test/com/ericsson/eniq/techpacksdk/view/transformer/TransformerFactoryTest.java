package com.ericsson.eniq.techpacksdk.view.transformer;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.jdesktop.application.SingleFrameApplication;
import org.junit.*;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;
import tableTree.TTTableModel;
import tableTreeUtils.TreeMainNode;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * Test for TransformerFactory.
 * @author eciacah
 */
public class TransformerFactoryTest extends TestCase {
  
  // Mocks:
  private RockFactory rockFactoryMock;  
  private TransformerDataModel transformerDataModelMock;  
  private SingleFrameApplication applicationMock;
  private TransformerTreeModel ttmMock;
  private DataModelController dmcMock;
  private Versioning versioningMock;
  private TransformerData transformerDataMock;

  private boolean editable = true;

  private static final String DATA_FORMAT_TYPE = "mdc";       
  
  public TransformerFactoryTest() {
    super("TransformerFactoryTest");
  }

  @Before
  protected void setUp() throws Exception {
    versioningMock = createNiceMock(Versioning.class);
    rockFactoryMock = createNiceMock(RockFactory.class);
    transformerDataModelMock = createNiceMock(TransformerDataModel.class);
    applicationMock = createNiceMock(SingleFrameApplication.class);
    ttmMock = createNiceMock(TransformerTreeModel.class);
    dmcMock = createNiceMock(DataModelController.class);
    transformerDataMock = createNiceMock(TransformerData.class);
        
    // Disable logging to console:
    TransformerFactory.getLogger().setUseParentHandlers(false);
  }

  @After
  protected void tearDown() throws Exception {
    //
  }
    
  /**
   * createAndGetModel()
   * @throws Exception
   */
  public void testCreateAndGetModel() {

    // All transformations:
    Transformation t1 = createNiceMock(Transformation.class);
    final List<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);

    // TreeMainNode:
    final TreeMainNode treeMainNode = createNiceMock(TreeMainNode.class);
    final DefaultMutableTreeNode rootNodeMock = createNiceMock(DefaultMutableTreeNode.class);

    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);
    
    // Test instance of TransformerFactory.
    // Override creator methods to return mocks. 
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable, ttmMock,
        dmcMock) {

      @Override
      protected List<Object> createCommonTransformations(final List<TransformerData> transformerData) {
        return allTransformations;
      }

      @Override
      protected TreeMainNode createTransformer(final TransformerData transformerData,
          Vector<Object> allTransformations, final List<TransformerData> allTransformerData) {
        return treeMainNode;
      }
      
      @Override
      protected DefaultMutableTreeNode createTreeNodeObject(final String treeNodeName) {
        return rootNodeMock;
      }
    };    
    
    EasyMock.reset(transformerDataModelMock);

    // Data format type is mdc for test here:
    final String dataFormatType = "mdc";
    testInstance.setDataformatType(dataFormatType);

    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    
    // Expect call to get mappings from the transformer data model:
    EasyMock.expect(transformerDataModelMock.getMappings(dataFormatType)).andReturn( new HashMap<Transformation, ArrayList<String>>());
    // Expect call to get the transformers:
    EasyMock.expect(transformerDataModelMock.getTransformerData(dataFormatType)).andReturn(transformerDataList);
    
    rootNodeMock.add(treeMainNode);
    expectLastCall().once();
        
    replay(transformerDataModelMock);
    replay(rootNodeMock);
    
    TreeModel treeModel = testInstance.createAndGetModel();
    
    verify(transformerDataModelMock);
    verify(rootNodeMock);
    
    Assert.assertNotNull(treeModel);
  }
  
  /**
   * createAndGetModel(), exception creating the transformers. 
   * @throws Exception
   */
  public void testCreateAndGetWithException() {

    // All transformations:
    Transformation t1 = createNiceMock(Transformation.class);
    final List<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);

    final DefaultMutableTreeNode rootNodeMock = createNiceMock(DefaultMutableTreeNode.class);

    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);
    
    // Test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable, ttmMock,
        dmcMock) {

      @Override
      protected List<Object> createCommonTransformations(final List<TransformerData> transformerData) {
        return allTransformations;
      }

      @Override
      protected TreeMainNode createTransformer(final TransformerData transformerData,
          Vector<Object> allTransformations, final List<TransformerData> allTransformerData) throws Exception {
        throw new Exception("Failed to create transformer");
      }
      
      @Override
      protected DefaultMutableTreeNode createTreeNodeObject(final String treeNodeName) {
        return rootNodeMock;
      }
    };
    
    EasyMock.reset(transformerDataModelMock);

    // Data format type is mdc for test here:
    final String dataFormatType = "mdc";
    testInstance.setDataformatType(dataFormatType);

    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    
    // Expect call to get mappings from the transformer data model:
    EasyMock.expect(transformerDataModelMock.getMappings(dataFormatType)).andReturn( new HashMap<Transformation, ArrayList<String>>());
    // Expect call to get the transformers:
    EasyMock.expect(transformerDataModelMock.getTransformerData(dataFormatType)).andReturn(transformerDataList);
           
    replay(transformerDataModelMock);
    
    TreeModel treeModel = testInstance.createAndGetModel();
    
    verify(transformerDataModelMock);
    
    Assert.assertNotNull(treeModel);
  }
  
  /**
   * createAndGetModel(), exception getting common transformations.
   * @throws Exception
   */
  public void testCreateAndGetModelCommonExc() {

    // All transformations:
    Transformation t1 = createNiceMock(Transformation.class);
    final List<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);

    // TreeMainNode:
    final TreeMainNode treeMainNode = createNiceMock(TreeMainNode.class);
    final DefaultMutableTreeNode rootNodeMock = createNiceMock(DefaultMutableTreeNode.class);

    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);
    
    // Test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable, ttmMock,
        dmcMock) {

      @Override
      protected List<Object> createCommonTransformations(final List<TransformerData> transformerData) throws Exception {
        throw new Exception("Error getting common transformations");
      }

      @Override
      protected TreeMainNode createTransformer(final TransformerData transformerData,
          Vector<Object> allTransformations, final List<TransformerData> allTransformerData) {
        return treeMainNode;
      }
      
      @Override
      protected DefaultMutableTreeNode createTreeNodeObject(final String treeNodeName) {
        return rootNodeMock;
      }
    };
    
    EasyMock.reset(transformerDataModelMock);

    // Data format type is mdc for test here:
    final String dataFormatType = "mdc";
    testInstance.setDataformatType(dataFormatType);

    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    
    // Expect call to get mappings from the transformer data model:
    EasyMock.expect(transformerDataModelMock.getMappings(dataFormatType)).andReturn( new HashMap<Transformation, ArrayList<String>>());
    // Expect call to get the transformers:
    EasyMock.expect(transformerDataModelMock.getTransformerData(dataFormatType)).andReturn(transformerDataList);
        
    replay(transformerDataModelMock);

    TreeModel treeModel = testInstance.createAndGetModel();
    
    verify(transformerDataModelMock);
    
    Assert.assertNotNull(treeModel);
  }
    
  /**
   * Test createCommonTransformations().
   * Should return correct number of transformations.
   */
  public void testCreateCommonTransformations() {

    // Set up a dummy list of all transformations:
    Transformation t1 = createNiceMock(Transformation.class);        
    final List<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);
    
    // Test list of transformers that we will iterate through to check:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);        
    
    final Transformation commonTransformation = createNiceMock(Transformation.class);
    
    final Transformer transformerMock = createNiceMock(Transformer.class);

    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);
    
    // Test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable, ttmMock,
        dmcMock) {
      
      @Override
      protected List<Object> getAllTransformations(final List<TransformerData> transformerData) {
        return allTransformations;
      }
      
      @Override
      protected Transformation createCommonTransformation(final Transformation transformation, final List<Transformer> transformerMatches) {
        return commonTransformation;
      }
    };    
    EasyMock.reset(transformerDataModelMock);

    // Data format type is mdc for test here:
    final String dataFormatType = "mdc";
    testInstance.setDataformatType(dataFormatType);
    
    // Set up the list of transformations that we get back from the dummy transformer:
    final Vector<Object> tdTransformations = new Vector<Object>();
    tdTransformations.add(t1);
    
    // We need to get all of the transformations from the transformer:
    EasyMock.expect(transformerDataMock.getTransformations()).andReturn(tdTransformations);
    
    // If transformer has transformation in its transformations, add it to a list of transformers.
    // This gives us a transformation, and a list of corresponding transformers. 
    // This is then passed to createCommonTransformation() 
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock);    
    replay(transformerDataMock);

    // Call the method being tested:
    List<Object> resultList = null;
    try {
    resultList = testInstance.createCommonTransformations(transformerDataList);
    } catch (Exception exc) {
      fail("createCommonTransformations() should not throw an exception");
    }
    
    verify(transformerDataMock);
    
    Assert.assertNotNull("resultList should not be null", resultList);
    Assert.assertTrue("1 common transformation should be found", resultList.size() == 1);
  }
  
  
  /**
   * Common transformations may be enabled for all transformations, or just a few transformers.
   * Test for situation when a transformation is enabled for just a few.
   */
  public void testCreateCommonTransformation() {

    // Set up a dummy list of all transformations:
    Transformation t1 = createNiceMock(Transformation.class);
    final List<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    
    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);

    // Test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable,
        ttmMock, dmcMock);    
    EasyMock.reset(transformerDataModelMock);
    
    // Data format type is mdc for test here:
    final String dataFormatType = "mdc";
    testInstance.setDataformatType(dataFormatType);
        

    // Test list of transformers that we will iterate through to check:
    final List<Transformer> transformerMatches = new Vector<Transformer>();
    transformerMatches.add(transformerMock);
    
    EasyMock.expect(transformerDataModelMock.getTransformerData(dataFormatType)).andReturn(transformerDataList);
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("testID");   
    
    EasyMock.expect(t1.getSource()).andReturn("Source");
    EasyMock.expect(t1.getConfig()).andReturn("Config");
    EasyMock.expect(t1.getType()).andReturn("Type");
    EasyMock.expect(t1.getTarget()).andReturn("Target");
    
    replay(transformerDataModelMock);
    replay(t1);
    replay(transformerMock);
    
    Transformation result = null;
    try {
      result = testInstance.createCommonTransformation(t1, transformerMatches);
    } catch (Exception exc) {
      fail("createCommonTransformation() should not throw an exception");
    }
    
    verify(t1);
    verify(transformerMock);
    
    Assert.assertNotNull("Common transformation should not be null", result);  
  }
  
  /**
   * Common transformations may be enabled for all transformations, or just a few transformers.
   * Test for situation when a transformation is enabled for all.
   */
  public void testCreateCommonTransformationWithALL() {

    // Set up a dummy list of all transformations:
    Transformation t1 = createNiceMock(Transformation.class);
    final List<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);
       
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock);  
    
    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);

    // Test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable,
        ttmMock, dmcMock);    
    EasyMock.reset(transformerDataModelMock);
    
    // Data format type is mdc for test here:
    final String dataFormatType = "mdc";
    testInstance.setDataformatType(dataFormatType);
        
    // Test list of transformers that we will iterate through to check:
    final List<Transformer> transformerMatches = new Vector<Transformer>();
    transformerMatches.add(transformerMock);
    
    EasyMock.expect(transformerDataModelMock.getTransformerData(dataFormatType)).andReturn(transformerDataList);
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);      
    
    EasyMock.expect(t1.getSource()).andReturn("Source");
    EasyMock.expect(t1.getConfig()).andReturn("Config");
    EasyMock.expect(t1.getType()).andReturn("Type");
    EasyMock.expect(t1.getTarget()).andReturn("Target");
 
    replay(transformerDataModelMock);
    replay(t1);
    replay(transformerMock);
    
    Transformation result = null;
    try {
      result = testInstance.createCommonTransformation(t1, transformerMatches);
    } catch (Exception exc) {
      fail("createCommonTransformation() should not throw an exception");
    }
    
    verify(t1);
    verify(transformerMock);
    
    Assert.assertNotNull("Common transformation should not be null", result);  
  }
  
  /**
   * getAllTransformations. Should return a list of all of the transformations from the transformers. 
   */
  public void testGetAllTransformations() {
    
    final int expectedNoTransformations = 3;
    
    final String allTransformerID = "DC_E_TEST_TECH_PACK:ALL:mdc";
    
    // Set up a dummy list of all transformations. We expect 3 transformations in the list of all transformations:
    Transformation t1 = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);
    allTransformations.add(t1);
    allTransformations.add(t1);
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);

    // Set up test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable,
        ttmMock, dmcMock);    
    EasyMock.reset(transformerDataModelMock);
    
    // Both data format type and versioning are set in the object after instantiation:
    testInstance.setDataformatType(DATA_FORMAT_TYPE);
    testInstance.setVersioning(versioningMock);
    
    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock);
    
    EasyMock.expect(versioningMock.getVersionid()).andReturn("DC_E_TEST_TECH_PACK");
    
    // First transformer is the All transformer:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock).once();
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(allTransformerID);
    
    // Second transformer is a specific transformer, has the 3 transformations, so total will be 3:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock).once();
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("DC_E_TEST_TECH_PACK:SPECIFIC_TYPE:mdc");
    EasyMock.expect(transformerDataMock.getTransformations()).andReturn(allTransformations).once();   
    
    replay(versioningMock);
    replay(transformerDataMock);
    replay(transformerMock);    
    
    List<Object> resultList = null;
    try {
      resultList = testInstance.getAllTransformations(transformerDataList);
    } catch (Exception exc) {
      fail("getAllTransformations() should not throw an exception");
    }
    
    verify(versioningMock);
    verify(transformerDataMock);
    verify(transformerMock);
    
    Assert.assertNotNull("All transformations list should not be null", resultList);
    Assert.assertTrue("All transformations list should have correct length", resultList.size() == expectedNoTransformations);    
  }
  
  
  /**
   * getAllTransformations. Should return an empty list of transformations. 
   */
  public void testGetAllTransformationsNoneFound() {    
    final String allTransformerID = "DC_E_TEST_TECH_PACK:ALL:mdc";
    
    // Set up a dummy empty list of all transformations. We expect no transformations at all in the list of all transformations:
    final Vector<Object> allTransformations = new Vector<Object>();
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);

    // Set up test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable,
        ttmMock, dmcMock);    
    EasyMock.reset(transformerDataModelMock);
    
    // Both data format type and versioning are set in the object after instantiation:
    testInstance.setDataformatType(DATA_FORMAT_TYPE);
    testInstance.setVersioning(versioningMock);
    
    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock);
    
    EasyMock.expect(versioningMock.getVersionid()).andReturn("DC_E_TEST_TECH_PACK");
    
    // First transformer is the All transformer:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock).once();
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(allTransformerID);
    
    // Second transformer is a specific transformer, but has no transformations:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock).once();
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("DC_E_TEST_TECH_PACK:SPECIFIC_TYPE:mdc");
    EasyMock.expect(transformerDataMock.getTransformations()).andReturn(allTransformations).once();   
    
    replay(versioningMock);
    replay(transformerDataMock);
    replay(transformerMock);    
    
    List<Object> resultList = null;
    try {
      resultList = testInstance.getAllTransformations(transformerDataList);
    } catch (Exception exc) {
      System.out.println(exc.toString());
      fail("getAllTransformations() should not throw an exception");
    }
    
    verify(versioningMock);
    verify(transformerDataMock);
    verify(transformerMock);
    
    final int expectedNoTransformations = 0;
    Assert.assertNotNull("All transformations list should not be null even if empty", resultList);
    Assert.assertTrue("All transformations list should have correct length", resultList.size() == expectedNoTransformations);    
  }
  
  /**
   * Test creating a transformer in the tree.
   * This method creates a single transformer. Called by createAndGetModel() which creates all of the 
   * transformers in a loop. createAndGetModel() handles any exceptions that might be thrown by this 
   * method.
   */
  public void testCreateTransformer() {
    final String allTransformerID = "DC_E_TEST_TECH_PACK:ALL:mdc";
    
    // Set up a dummy list of all transformations.
    Transformation t1 = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);
    allTransformations.add(t1);
    allTransformations.add(t1);
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    
    final TTParameterModel paramModelMock = createNiceMock(TTParameterModel.class);
    final TTTableModel commonModelMock = createNiceMock(TTTableModel.class);
    final TTTableModel specificModelMock = createNiceMock(TTTableModel.class);
    final TreeMainNode treeMainNodeMock = createNiceMock(TreeMainNode.class);
    final DefaultMutableTreeNode parameterNodeMock = createNiceMock(DefaultMutableTreeNode.class);
    final DefaultMutableTreeNode transformationNodeMock = createNiceMock(DefaultMutableTreeNode.class);
    // Mock for the table model (for the common transformations): 
    final TTTableModel commonTableMock = createNiceMock(TTTableModel.class);
    
    // Expectations for constructor:
    EasyMock.expect(transformerDataModelMock.getRockFactory()).andReturn(rockFactoryMock);
    replay(transformerDataModelMock);

    // Set up test instance of TransformerFactory:
    TransformerFactory testInstance = new TransformerFactory(applicationMock, transformerDataModelMock, editable,
        ttmMock, dmcMock) {
      
      protected TTParameterModel createParameterModel(final TransformerData transformerData, final Transformer transformer, final String name) {
        return paramModelMock;
      }
      
      protected TTTableModel createCommonTableModel(Vector<Object> allTransformations,
          final List<TransformerData> allTransformerData) {
        return commonModelMock;        
      }
            
      protected TTTableModel createSpecificTableModel(final List<TransformerData> allTransformerData,
          final Transformer transformer) {
        return specificModelMock;
      }
      
      protected TreeMainNode createTreeMainNode(final TTParameterModel parameterModel) {
        return treeMainNodeMock;
      }
      
      protected DefaultMutableTreeNode createParameterNode(
          TTParameterModel parameterModel) {
        return parameterNodeMock;
      }
      
      protected DefaultMutableTreeNode createTableNode(Vector<Object> rockData,
          TTTableModel tableModel, final boolean enableDuplicateRows) {
        return transformationNodeMock;
      }
    };
    EasyMock.reset(transformerDataModelMock);
    
    // Both data format type and versioning are set in the object after instantiation:
    testInstance.setDataformatType(DATA_FORMAT_TYPE);
    testInstance.setVersioning(versioningMock);
    
    // Test list of transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock);
    
    // Set up commonTableModel (map of data format to common table model):
    Map<String, TTTableModel> commonTableModels = new HashMap<String, TTTableModel>();
    commonTableModels.put(DATA_FORMAT_TYPE, commonTableMock);
        
    // Expectations:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock).once();
    EasyMock.expect(transformerDataMock.getTransformations()).andReturn(allTransformations).once();
    
    EasyMock.expect(transformerDataModelMock.getCommonTableModels()).andReturn(commonTableModels); 
    
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(allTransformerID);
    
    EasyMock.expect(versioningMock.getVersionid()).andReturn("DC_E_TEST_TECH_PACK");
    
    paramModelMock.addObserver(commonModelMock);
    EasyMock.expectLastCall();
    
    treeMainNodeMock.add(parameterNodeMock);
    EasyMock.expectLastCall();
    
    EasyMock.expect(transformerMock.getTransformerid()).andReturn(allTransformerID);
    EasyMock.expect(versioningMock.getVersionid()).andReturn("DC_E_TEST_TECH_PACK");               
    
    replay(versioningMock);
    replay(transformerDataMock);
    replay(transformerDataModelMock);
    replay(transformerMock);
    replay(paramModelMock); 
    replay(treeMainNodeMock);
    replay(parameterNodeMock); 
    
    TreeMainNode mainNode = null;
    try {
      mainNode = testInstance.createTransformer(transformerDataMock, allTransformations, transformerDataList);
      // The node should be created ok:
      Assert.assertNotNull("createTransformer() should return a new tree node for a transformer", mainNode);
      // commonTableModels should be updated with the table model:
      Assert.assertNotNull("createTransformer() should store the common table model", 
          commonTableModels.get(DATA_FORMAT_TYPE) == commonTableMock);
    } catch (Exception exc) {
      System.out.println(exc.toString());
      fail("createTransformer() should not throw an exception");
    }
    
    verify(versioningMock);
    verify(transformerDataMock);
    verify(transformerMock);
    verify(paramModelMock); 
    verify(treeMainNodeMock);
    verify(parameterNodeMock);     
    
    Assert.assertNotNull("TreeMainNode should not be null", mainNode);
  }

}

