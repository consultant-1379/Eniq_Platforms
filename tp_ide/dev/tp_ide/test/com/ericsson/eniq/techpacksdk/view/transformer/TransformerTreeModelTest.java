package com.ericsson.eniq.techpacksdk.view.transformer;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.DocumentListener;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.jdesktop.application.SingleFrameApplication;
import org.junit.After;
import org.junit.Before;

import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * Test for TransformerTreeModel.
 * 
 * @author eciacah
 */
public class TransformerTreeModelTest extends TestCase {

  // Mocks:
  private TransformerDataModel transformerDataModelMock;

  private TransformerData transformerDataMock;

  private TransformerData transformerDataMock2;

  private TransformerData transformerDataMock3;

  private List<TransformerData> transformerDataList;

  private Transformation t1Mock;

  private Vector<Object> allTransformations;

  private HashMap<Transformation, ArrayList<String>> mappings;

  private final static String DATA_FORMAT_TYPE = "mdc";

  private SingleFrameApplication applicationMock;

  private Versioning versioningMock;

  private DataModelController dataModelControllerMock;

  private JTree jTreeMock;

  private TransformerTreeModelListener ttmListenerMock;

  private DocumentListener dlMock;

  public TransformerTreeModelTest() {
    super("TransformerTreeModelTest");
  }

  @Before
  protected void setUp() throws Exception {
    versioningMock = createNiceMock(Versioning.class);
    transformerDataModelMock = createNiceMock(TransformerDataModel.class);
    applicationMock = createNiceMock(SingleFrameApplication.class);
    transformerDataMock = createNiceMock(TransformerData.class);
    dataModelControllerMock = createNiceMock(DataModelController.class);
    jTreeMock = createNiceMock(JTree.class);
    ttmListenerMock = createNiceMock(TransformerTreeModelListener.class);
    dlMock = createNiceMock(DocumentListener.class);

    // Disable logging to console:
    TransformerFactory.getLogger().setUseParentHandlers(false);

    transformerDataModelMock = createNiceMock(TransformerDataModel.class);

    transformerDataMock = createNiceMock(TransformerData.class);
    transformerDataMock = createNiceMock(TransformerData.class);
    transformerDataMock2 = createNiceMock(TransformerData.class);
    transformerDataMock3 = createNiceMock(TransformerData.class);

    transformerDataList = new Vector<TransformerData>();

    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);

    t1Mock = createNiceMock(Transformation.class);
    allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);

    // Set up list of mappings:
    mappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    mappings.put(t1Mock, transformers);

    // Disable logging to console:
    MTMappingsTableCellEditor.getLogger().setUseParentHandlers(false);
  }

  @After
  protected void tearDown() throws Exception {
    //
  }

  /**
   * Tests addAndRemoveFromMappings(). 
   * When we call addAndRemoveFromMappings(), it should delete the transformations 
   * we have marked for removal, and add any new transformations. 
   * This test verifies that this is actually done.
   * 
   * @throws Exception
   */
  public void testAddAndRemoveFromMappings() {

    // A transformation that is marked to be deleted in the common section:
    Transformation deleteTransformation = createNiceMock(Transformation.class);
    final List<Transformation> transformationsToDelete = new ArrayList<Transformation>();
    transformationsToDelete.add(deleteTransformation);

    // A transformation that is marked to be added in the common section:
    Transformation addTransformation = createNiceMock(Transformation.class);
    final List<Transformation> transformationsToAdd = new ArrayList<Transformation>();
    transformationsToAdd.add(addTransformation);

    // Set up a list of "updated" transformations.
    // This is the total list of transformations for a data format type.
    Transformation updatedTransformation = createNiceMock(Transformation.class);
    final Vector<Transformation> updatedTransformations = new Vector<Transformation>();
    updatedTransformations.add(updatedTransformation);

    // Set up a test instance of TransformerTreeModel:
    @SuppressWarnings("serial")
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true) {

      protected List<Transformation> getTransformationsToDelete(Map<Transformation, ArrayList<String>> mappings) {
        return transformationsToDelete;
      }

      protected List<Transformation> getTransformationsToAdd(final Map<Transformation, ArrayList<String>> mappings, final String dataformatType) {
        return transformationsToAdd;
      }

      protected void addOrdered(final List<Transformation> transformationsToAdd, final String dataformatType) {
        // Assume that a call to addOrdered() adds the new transformation:
        updatedTransformations.addAll(transformationsToAdd);
      }

      protected boolean compareTransformations(final Transformation t1, final Transformation t2) {
        return true;
      }
    };

    // Get the transformer data model:
    EasyMock.expect(dataModelControllerMock.getTransformerDataModel()).andReturn(transformerDataModelMock);
    // Then get mappings:
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(mappings);
    // Then get the overall list of transformations that we are adding/removing from:
    EasyMock.expect(transformerDataModelMock.getUpdatedTransformations()).andReturn(updatedTransformations);

    // Set up the scenario where a transformation in the list is the same as the one to delete 
    // (same transformer ID):
    EasyMock.expect(updatedTransformation.getTransformerid()).andReturn("TRANFORMER_ID_FOR_DELETE");
    EasyMock.expect(deleteTransformation.getTransformerid()).andReturn("TRANFORMER_ID_FOR_DELETE");

    replay(dataModelControllerMock);
    replay(transformerDataModelMock);
    replay(updatedTransformation);
    replay(deleteTransformation);

    testInstance.addAndRemoveFromMappings(DATA_FORMAT_TYPE);

    // We should have the transformations that are added, but not the deleted
    // transformations:
    Assert.assertTrue("List of transformations should include the new transformation being added",
        updatedTransformations.contains(addTransformation));
    Assert.assertFalse("List of transformations should not include the deleted transformation",
        updatedTransformations.contains(deleteTransformation));

    EasyMock.verify(dataModelControllerMock);
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(updatedTransformation);
    EasyMock.verify(deleteTransformation);
  }

  /**
   * Tests getTransformationsToAdd().
   * 
   * @throws Exception
   */
  public void testGetTransformationsToAdd() {

    String dataformatType = "dataformatType";
    
    // Transformation:
    final Transformation commonTransformationMock = createNiceMock(Transformation.class);
    final Transformation newTransformationMock = createNiceMock(Transformation.class);
    
    // Mock for the table model (for the common transformations): 
    final TTTableModel commonTableMock = createNiceMock(TTTableModel.class);

    // Set up list of mappings (situation where user has selected some new):
    HashMap<Transformation, ArrayList<String>> newMappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    newMappings.put(commonTransformationMock, transformers);

    // Set up list of "updated" transformations (overall list):
    Transformation updatedTransformation = createNiceMock(Transformation.class);
    final Vector<Transformation> updatedTransformations = new Vector<Transformation>();
    updatedTransformations.add(updatedTransformation);
    
    // The data from the common table model (the transformations/rows from the common table):
    final Vector<Object> dataFromCommonTable = new Vector<Object>();
    dataFromCommonTable.add(commonTransformationMock);

    // Set up commonTableModel (table model for the common section):
    Map<String, TTTableModel> commonTableModels = new HashMap<String, TTTableModel>();
    commonTableModels.put(dataformatType, commonTableMock);
    
    // Test instance of TransformerTreeModel:
    @SuppressWarnings("serial")
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true) {
      
      /** Create special instance of TransformationUtils that will return true for
       * comparing transformations.
       */
      protected TransformationUtils createTransformationUtils() {
        return new TransformationUtils() {
          public boolean checkIsTransformationEqual(Transformation t1, Transformation t2) {
            return true;
          }
        };
      }

      protected Transformation createTransformation(final RockFactory rockFactory) {
        return newTransformationMock;
      }
    };

    // Get the transformer data model:
    EasyMock.expect(dataModelControllerMock.getTransformerDataModel()).andReturn(transformerDataModelMock);
    EasyMock.expect(transformerDataModelMock.getUpdatedTransformations()).andReturn(updatedTransformations);
    EasyMock.expect(transformerDataModelMock.getCommonTableModels()).andReturn(commonTableModels);
    EasyMock.expect(commonTableMock.getData()).andReturn(dataFromCommonTable);
        
    // Set up the situation where the transformation isn't already in the 
    // transformer where we're enabling it:
    EasyMock.expect(updatedTransformation.getTransformerid()).andReturn("Non matching transformer");

    // Transformation should then be added in. 
    // Expect calls to do this: 
    newTransformationMock.setTransformerid("transformer1");
    EasyMock.expectLastCall();

    EasyMock.expect(commonTransformationMock.getConfig()).andReturn("common config");
    newTransformationMock.setConfig("common config");
    EasyMock.expectLastCall();

    EasyMock.expect(commonTransformationMock.getSource()).andReturn("common source");
    newTransformationMock.setSource("common source");
    EasyMock.expectLastCall();

    EasyMock.expect(commonTransformationMock.getTarget()).andReturn("common target");
    newTransformationMock.setTarget("common target");
    EasyMock.expectLastCall();

    EasyMock.expect(commonTransformationMock.getType()).andReturn("common type");
    newTransformationMock.setType("common type");
    EasyMock.expectLastCall();

    // Activate mocks:
    replay(dataModelControllerMock);
    replay(transformerDataModelMock);
    replay(commonTableMock);
    replay(updatedTransformation);
    replay(newTransformationMock);
    replay(commonTransformationMock);
    
    // Call method:
    final List<Transformation> test = testInstance.getTransformationsToAdd(newMappings, dataformatType);

    // If there are mappings for transformations but those transformations don't
    // exist, they should be added.
    // Note: We have only one transformation mock here but it is for 3 transformers,
    // so three transformers should be in the list now:
    Assert.assertTrue("Transformations should be added to the transformers in the mappings", test.size() == 3);

    EasyMock.verify(dataModelControllerMock);
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(commonTableMock);
    EasyMock.verify(updatedTransformation);
    EasyMock.verify(newTransformationMock);
    EasyMock.verify(commonTransformationMock);
  }

  /**
   * Tests getTransformationsToDelete(). Transformation should be added to the
   * deleted list if it is not in the mappings.
   * 
   * @throws Exception
   */
  public void testGetTransformationsToDelete() {

    // Transformation:
    final Transformation commonTransformationMock = createNiceMock(Transformation.class);

    final Transformation newTransformationMock = createNiceMock(Transformation.class);

    // Set up list of mappings:
    HashMap<Transformation, ArrayList<String>> mappings2 = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    mappings2.put(commonTransformationMock, transformers);

    // Set up list of "updated" transformations:
    Transformation updatedTransformation = createNiceMock(Transformation.class);
    final Vector<Transformation> updatedTransformations = new Vector<Transformation>();
    updatedTransformations.add(updatedTransformation);

    // Test instance of TransformerTreeModel:
    @SuppressWarnings("serial")
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true) {

      protected boolean compareTransformations(final Transformation t1, final Transformation t2) {
        return true;
      }

      protected Transformation createTransformation(final RockFactory rockFactory) {
        return newTransformationMock;
      }
    };

    // Get the transformer data model:
    EasyMock.expect(dataModelControllerMock.getTransformerDataModel()).andReturn(transformerDataModelMock);
    EasyMock.expect(transformerDataModelMock.getUpdatedTransformations()).andReturn(updatedTransformations);

    replay(dataModelControllerMock);
    replay(transformerDataModelMock);
    replay(updatedTransformation);
    replay(newTransformationMock);
    replay(commonTransformationMock);

    final List<Transformation> toBeDeletedList = testInstance.getTransformationsToDelete(mappings2);

    // If there are mappings for transformations but those transformations don't
    // exist, they should be added:
    Assert.assertTrue("Transformation should be in the deleted list if it is not in the mappings",
        toBeDeletedList.size() == 1);
    Assert.assertTrue("Transformation should be in the deleted list if it is not in the mappings",
        toBeDeletedList.contains(updatedTransformation));

    EasyMock.verify(dataModelControllerMock);
    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(updatedTransformation);
    EasyMock.verify(newTransformationMock);
    EasyMock.verify(commonTransformationMock);
  }

  /**
   * Test getting common transformations for a specific transformer.
   * Test is: Transformations in result list should be only be for transformer being searched for.
   */
  public void testGetCommonForTransformer() {
    String dataformatType = "testDataFormatType";
    
    // Mock for the table model (for the common transformations): 
    final TTTableModel commonTableMock = createNiceMock(TTTableModel.class);

    // Set up mappings:
    HashMap<Transformation, ArrayList<String>> testMappings = new HashMap<Transformation, ArrayList<String>>();

    // Set up two "common" transformations:
    Transformation t1 = new Transformation();
    t1.setConfig("testConfig1");
    t1.setSource("testConfig1");
    t1.setOrderno(0l);
    t1.setTransformerid("transformer1, transformer2, transformer3");

    Transformation t2 = createTestTransformation("t2config", "t2source", 0, "transformer4, transformer5, transformer6");

    // t1 applies to transformer 1, 2 and 3:
    ArrayList<String> transformerIDsForT1 = new ArrayList<String>();
    transformerIDsForT1.add("transformer1");
    transformerIDsForT1.add("transformer2");
    transformerIDsForT1.add("transformer3");

    // t2 applies to transformer 4, 5 and 6:
    ArrayList<String> transformerIDsForT2 = new ArrayList<String>();
    transformerIDsForT2.add("transformer4");
    transformerIDsForT2.add("transformer5");
    transformerIDsForT2.add("transformer6");

    // Put in entries for mappings:
    testMappings.put(t1, transformerIDsForT1);
    testMappings.put(t2, transformerIDsForT2);
    
    // The data from the common table model (the transformations/rows from the common table):
    final Vector<Object> dataFromCommonTable = new Vector<Object>();
    dataFromCommonTable.add(t1);
    dataFromCommonTable.add(t2);
    
    // Set up commonTableModel (map of data format to common table model):
    Map<String, TTTableModel> commonTableModels = new HashMap<String, TTTableModel>();
    commonTableModels.put(dataformatType, commonTableMock);

    // Set up test instance:
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true);

    // Expectations:
    EasyMock.expect(transformerDataModelMock.getMappings(dataformatType)).andReturn(testMappings);    
    EasyMock.expect(transformerDataModelMock.getCommonTableModels()).andReturn(commonTableModels);
    EasyMock.expect(commonTableMock.getData()).andReturn(dataFromCommonTable);
    
    // Call replay to activate expectations:
    replay(transformerDataModelMock);
    replay(commonTableMock);

    // Call the method:
    List<Transformation> resultList = testInstance.getCommonForTransformer(transformerDataModelMock, dataformatType,
        "transformer1");
    
    // Check results:
    Assert.assertTrue("Only one common transformation should match for transformer1", resultList.size() == 1);
    Assert.assertTrue("Transformations in result list should be only be for transformer being searched for", resultList
        .get(0).equals(t1));
    Assert.assertFalse("Transformations in result list should be only be for transformer being searched for",
        resultList.get(0).equals(t2));

    EasyMock.verify(transformerDataModelMock);
  }

  /**
   * Test getting common transformations for a specific transformer, but can't
   * get mappings (null or empty).
   */
  public void testGetCommonForTransformerUnrecognisedDataformat() {
    String dataformatType = "testDataFormatType";
    
    // Mock for the table model (for the common transformations): 
    final TTTableModel commonTableMock = createNiceMock(TTTableModel.class);

    // Call the method:
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true);

    // Expectations, null mappings returned:
    EasyMock.expect(transformerDataModelMock.getMappings(dataformatType)).andReturn(null);
    // Call replay to activate expectations:
    replay(transformerDataModelMock);

    List<Transformation> resultList = testInstance.getCommonForTransformer(transformerDataModelMock, dataformatType,
        "transformer1");
    Assert.assertNotNull("Result list should not be null even if no mappings are found", resultList);

    EasyMock.reset(transformerDataModelMock);
    
    
    // Set up commonTableModel (map of data format to common table model):
    Map<String, TTTableModel> commonTableModels = new HashMap<String, TTTableModel>();
    commonTableModels.put(dataformatType, commonTableMock);

    // Expectations: just empty mappings returned.
    EasyMock.expect(transformerDataModelMock.getMappings(dataformatType)).andReturn(
        new HashMap<Transformation, ArrayList<String>>());
    
    EasyMock.expect(transformerDataModelMock.getCommonTableModels()).andReturn(commonTableModels);   
    // Also common table data will be an empty vector.
    EasyMock.expect(commonTableMock.getData()).andReturn(new Vector<Object>());
    
    // Call replay to activate expectations:
    replay(transformerDataModelMock);
    replay(commonTableMock);

    resultList = testInstance.getCommonForTransformer(transformerDataModelMock, dataformatType, "transformer1");
    Assert.assertNotNull("Result list should not be null even if no mappings are found", resultList);

    EasyMock.verify(transformerDataModelMock);
  }

  /**
   * Test for getTransformationsForTransformer().
   * Only transformations for the transformer id should be returned in the result list.
   */
  public void testGetTransformationsForTransformer() {

    final List<Transformation> testTransformationsList = new ArrayList<Transformation>();
    final String transformerID = "transformer1";

    // Test transformations:
    Transformation t1 = new Transformation();
    t1.setConfig("testConfig1");
    t1.setSource("testConfig1");
    t1.setOrderno(0l);
    t1.setTransformerid("transformer1");

    Transformation t2 = new Transformation();
    t2.setConfig("testConfig2");
    t2.setSource("testConfig2");
    t2.setOrderno(0l);
    t2.setTransformerid("transformer2");

    // Add test transformations to the list:
    testTransformationsList.add(t1);
    testTransformationsList.add(t2);

    // Set up test instance:
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true);

    // Call the method. Look for transformer1 transformations only:
    List<Transformation> resultList = testInstance.getTransformationsForTransformer(testTransformationsList,
        transformerID);

    // Check results. Only t1 should be returned because it has transformer1 as the transformer ID:
    Assert.assertTrue("Transformations in result list should be only be for transformer being searched for", resultList
        .get(0).equals(t1));
  }
  
  /**
   * This test sets up the following scenario:
   * - Two transformations t1 and t3 exist in the transformer already.
   * - we are adding t2, t4 and t5 by selecting them in the "common" section.
   * The new transformations should be added in the correct order.
   * Order of the updated transformations list - the main list, should be 1,2,3,4,5.
   * Also the number of transformations should be 5. 
   */
  public void testAddSortedTransformations() {
    // Set up "updated" list. This is the whole list of transformations in the tree:    
    final Vector<Transformation> updatedTransformations = setupUpdatedTransformations();
    // For example 1 and 3 exist already.
    Transformation t1 = createTestTransformation("t1config", "t1source", 0, "transformer1");    
    updatedTransformations.add(t1);    
    Transformation t3 = createTestTransformation("t3config", "t3source", 0, "transformer1");    
    updatedTransformations.add(t3);
    
    // Common will have full list of transformations for the transformer:
    final List<Transformation> commonTransformationsForTransformer = setupCommonTransformationsForTransformer();
    Transformation t1common = createTestTransformation("t1config", "t1source", 0, "transformer1,transformer2,transformer6");
    Transformation t2common = createTestTransformation("t2config", "t2source", 0, "transformer1,transformer2,transformer6");
    Transformation t3common = createTestTransformation("t3config", "t3source", 0, "transformer1,transformer2,transformer5");
    Transformation t4common = createTestTransformation("t4config", "t4source", 0, "transformer1,transformer2,transformer6");
    Transformation t5common = createTestTransformation("t5config", "t5source", 0, "transformer1");
    
    commonTransformationsForTransformer.add(t1common);
    commonTransformationsForTransformer.add(t2common);
    commonTransformationsForTransformer.add(t3common);
    commonTransformationsForTransformer.add(t4common);
    commonTransformationsForTransformer.add(t5common);    
    
    // Sorted transformations to be added (2,4 and 5):
    final List<Transformation> sortedTs = setupSortedTransformationsToBeAdded();
    Transformation t2 = createTestTransformation("t2config", "t2source", 0, "transformer1");
    Transformation t4 = createTestTransformation("t4config", "t4source", 0, "transformer1");
    Transformation t5 = createTestTransformation("t5config", "t5source", 0, "transformer1");
    sortedTs.add(t2);
    sortedTs.add(t4);
    sortedTs.add(t5);
    
    // Set up test instance:
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true);

    // Call the method:
    testInstance.addSortedTransformations(updatedTransformations, commonTransformationsForTransformer,
        sortedTs);
    // Verify correct number of transformations are added:
    Assert.assertTrue(updatedTransformations.size() == 5);
    // Verify order is correct:
    Assert.assertTrue(updatedTransformations.get(0).getConfig().equalsIgnoreCase("t1config"));
    Assert.assertTrue(updatedTransformations.get(1).getConfig().equalsIgnoreCase("t2config"));
    Assert.assertTrue(updatedTransformations.get(2).getConfig().equalsIgnoreCase("t3config"));
    Assert.assertTrue(updatedTransformations.get(3).getConfig().equalsIgnoreCase("t4config"));
    Assert.assertTrue(updatedTransformations.get(4).getConfig().equalsIgnoreCase("t5config"));
  }
  
  /**
   * Test adding sorted transformations, no existing transformations are there for the transformer.
   */
  public void testAddSortedNoneExisting() {
    // Set up an empty "updated" list:    
    final Vector<Transformation> updatedTransformations = setupUpdatedTransformations();
    
    // Common will have full list of transformations for the transformer. Adding in 5 new here:
    final List<Transformation> commonTransformationsForTransformer = setupCommonTransformationsForTransformer();
    Transformation t1common = createTestTransformation("t1config", "t1source", 0, "transformer1,transformer2,transformer6");
    Transformation t2common = createTestTransformation("t2config", "t2source", 0, "transformer1,transformer2,transformer6");
    Transformation t3common = createTestTransformation("t3config", "t3source", 0, "transformer1,transformer2,transformer5");
    Transformation t4common = createTestTransformation("t4config", "t4source", 0, "transformer1,transformer2,transformer6");
    Transformation t5common = createTestTransformation("t5config", "t5source", 0, "transformer1");
    
    commonTransformationsForTransformer.add(t1common);
    commonTransformationsForTransformer.add(t2common);
    commonTransformationsForTransformer.add(t3common);
    commonTransformationsForTransformer.add(t4common);
    commonTransformationsForTransformer.add(t5common);    
    
    // Sorted transformations to be added (1,2,3,4 and 5):
    final List<Transformation> sortedTs = setupSortedTransformationsToBeAdded();
    Transformation t1 = createTestTransformation("t1config", "t1source", 0, "transformer1");
    Transformation t2 = createTestTransformation("t2config", "t2source", 0, "transformer1");
    Transformation t3 = createTestTransformation("t3config", "t3source", 0, "transformer1");
    Transformation t4 = createTestTransformation("t4config", "t4source", 0, "transformer1");
    Transformation t5 = createTestTransformation("t5config", "t5source", 0, "transformer1");
    
    sortedTs.add(t1);
    sortedTs.add(t2);
    sortedTs.add(t3);
    sortedTs.add(t4);
    sortedTs.add(t5);
    
    // Set up test instance:
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true);

    // Call the method:
    testInstance.addSortedTransformations(updatedTransformations, commonTransformationsForTransformer,
        sortedTs);
    // Verify correct number of transformations are added:
    Assert.assertTrue(updatedTransformations.size() == 5);
    // Verify order is correct:
    Assert.assertTrue("Error", updatedTransformations.get(0).getConfig().equalsIgnoreCase("t1config"));
    Assert.assertTrue("Error", updatedTransformations.get(1).getConfig().equalsIgnoreCase("t2config"));
    Assert.assertTrue("Error", updatedTransformations.get(2).getConfig().equalsIgnoreCase("t3config"));
    Assert.assertTrue("Error", updatedTransformations.get(3).getConfig().equalsIgnoreCase("t4config"));
    Assert.assertTrue("Error", updatedTransformations.get(4).getConfig().equalsIgnoreCase("t5config"));
  }
  
  /**
   * Test adding sorted transformations, where they should be added in before the existing ones.
   */
  public void testAddSortedBeforeExisting() {
    // Set up an empty "updated" list:    
    final Vector<Transformation> updatedTransformations = setupUpdatedTransformations();
    Transformation t1updated = createTestTransformation("t1config", "t1source", 0, "transformer1");
    Transformation t2updated = createTestTransformation("t2config", "t2source", 0, "transformer1");
    Transformation t3updated = createTestTransformation("t3config", "t3source", 0, "transformer1");
    Transformation t4updated = createTestTransformation("t4config", "t4source", 0, "transformer1");
    Transformation t5updated = createTestTransformation("t5config", "t5source", 0, "transformer1");
    updatedTransformations.add(t1updated);
    updatedTransformations.add(t2updated);
    updatedTransformations.add(t3updated);
    updatedTransformations.add(t4updated);
    updatedTransformations.add(t5updated);
    
    // Common will have full list of transformations for the transformer. Adding in 5 new here:
    final List<Transformation> commonTransformationsForTransformer = setupCommonTransformationsForTransformer();
    Transformation t1commonNew = createTestTransformation("t1configNew", "t1sourceNew", 0, "transformer1,transformer2,transformer6");
    Transformation t2commonNew = createTestTransformation("t2configNew", "t2sourceNew", 0, "transformer1,transformer2,transformer6");
    Transformation t3commonNew = createTestTransformation("t3configNew", "t3sourceNew", 0, "transformer1,transformer2,transformer5");
    Transformation t4commonNew = createTestTransformation("t4configNew", "t4sourceNew", 0, "transformer1,transformer2,transformer6");
    Transformation t5commonNew = createTestTransformation("t5configNew", "t5sourceNew", 0, "transformer1");
    
    Transformation t1common = createTestTransformation("t1config", "t1source", 0, "transformer1,transformer2,transformer6");
    Transformation t2common = createTestTransformation("t2config", "t2source", 0, "transformer1,transformer2,transformer6");
    Transformation t3common = createTestTransformation("t3config", "t3source", 0, "transformer1,transformer2,transformer5");
    Transformation t4common = createTestTransformation("t4config", "t4source", 0, "transformer1,transformer2,transformer6");
    Transformation t5common = createTestTransformation("t5config", "t5source", 0, "transformer1");
    
    commonTransformationsForTransformer.add(t1commonNew);
    commonTransformationsForTransformer.add(t2commonNew);
    commonTransformationsForTransformer.add(t3commonNew);
    commonTransformationsForTransformer.add(t4commonNew);
    commonTransformationsForTransformer.add(t5commonNew);    
    commonTransformationsForTransformer.add(t1common);
    commonTransformationsForTransformer.add(t2common);
    commonTransformationsForTransformer.add(t3common);
    commonTransformationsForTransformer.add(t4common);
    commonTransformationsForTransformer.add(t5common);    
    
    // Sorted transformations to be added (1,2,3,4 and 5):
    final List<Transformation> sortedTs = setupSortedTransformationsToBeAdded();
    Transformation t1New = createTestTransformation("t1configNew", "t1sourceNew", 0, "transformer1");
    Transformation t2New = createTestTransformation("t2configNew", "t2sourceNew", 0, "transformer1");
    Transformation t3New = createTestTransformation("t3configNew", "t3sourceNew", 0, "transformer1");
    Transformation t4New = createTestTransformation("t4configNew", "t4sourceNew", 0, "transformer1");
    Transformation t5New = createTestTransformation("t5configNew", "t5sourceNew", 0, "transformer1");
    
    sortedTs.add(t1New);
    sortedTs.add(t2New);
    sortedTs.add(t3New);
    sortedTs.add(t4New);
    sortedTs.add(t5New);
    
    // Set up test instance:
    TransformerTreeModel testInstance = new TransformerTreeModel(applicationMock, versioningMock,
        dataModelControllerMock, true, jTreeMock, ttmListenerMock, dlMock, true);

    // Call the method:
    testInstance.addSortedTransformations(updatedTransformations, commonTransformationsForTransformer,
        sortedTs);
    // Verify correct number of transformations are added:
    Assert.assertTrue(updatedTransformations.size() == 10);
    // Verify order is correct:
    Assert.assertTrue("Error", updatedTransformations.get(0).getConfig().equalsIgnoreCase("t1configNew"));
    Assert.assertTrue("Error", updatedTransformations.get(1).getConfig().equalsIgnoreCase("t2configNew"));
    Assert.assertTrue("Error", updatedTransformations.get(2).getConfig().equalsIgnoreCase("t3configNew"));
    Assert.assertTrue("Error", updatedTransformations.get(3).getConfig().equalsIgnoreCase("t4configNew"));
    Assert.assertTrue("Error", updatedTransformations.get(4).getConfig().equalsIgnoreCase("t5configNew"));
    
    Assert.assertTrue("Error", updatedTransformations.get(5).getConfig().equalsIgnoreCase("t1config"));
    Assert.assertTrue("Error", updatedTransformations.get(6).getConfig().equalsIgnoreCase("t2config"));
    Assert.assertTrue("Error", updatedTransformations.get(7).getConfig().equalsIgnoreCase("t3config"));
    Assert.assertTrue("Error", updatedTransformations.get(8).getConfig().equalsIgnoreCase("t4config"));
    Assert.assertTrue("Error", updatedTransformations.get(9).getConfig().equalsIgnoreCase("t5config"));
  }

  /**
   * Set up list of transformations. These are the existing transformations (just for one transformer here).
   * @return
   */
  private Vector<Transformation> setupUpdatedTransformations() {
    final Vector<Transformation> updatedTransformations = new Vector<Transformation>();
    return updatedTransformations;
  }
  
  /**
   * Set up the list of common transformations for a transformer (transformer1).
   * @return
   */
  private List<Transformation> setupCommonTransformationsForTransformer() {
    final List<Transformation> common = new ArrayList<Transformation>();
    return common;
  }
  
  /**
   * Set up list of new transformations being added.
   * @return
   */
  private List<Transformation> setupSortedTransformationsToBeAdded() {
    final List<Transformation> sortedNew = new ArrayList<Transformation>();
    return sortedNew;
  }
  
  /**
   * Creates a new test transformation object.
   * @param config
   * @param source
   * @param orderNo
   * @param transformerID
   * @return
   */
  private Transformation createTestTransformation(final String config, final String source, final long orderNo, final String transformerID) {
    Transformation transformation = new Transformation();
    transformation.setConfig(config);
    transformation.setSource(source);
    transformation.setOrderno(orderNo);
    transformation.setTransformerid(transformerID);
    return transformation;
  }

}
