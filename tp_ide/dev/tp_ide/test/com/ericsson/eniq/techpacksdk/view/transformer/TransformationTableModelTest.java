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
import org.junit.After;
import org.junit.Before;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;

/**
 * Test for TransformationTableModel.
 * 
 * @author eciacah
 */
public class TransformationTableModelTest extends TestCase {

  // Mocks:
  private TransformerDataModel transformerDataModelMock;

  private TransformerData transformerDataMock;

  private static String ALL_TRANSFORMER_ID = "DC_E_STN:ALL:mdc";

  private RockFactory rockFactoryMock;

  private TransformerData transformerDataMock2;

  private TransformerData transformerDataMock3;

  private List<TransformerData> transformerDataList;

  private Transformation t1Mock;

  private Vector<Object> allTransformations;

  private Transformer transformerMock;

  private HashMap<Transformation, ArrayList<String>> oldMappings;

  private Vector<TableInformation> tableInfos;

  private static String DATA_FORMAT_TYPE = "mdc";

  public TransformationTableModelTest() {
    super("TransformationTableModelTest");
  }

  @Before
  protected void setUp() throws Exception {
    transformerDataModelMock = createNiceMock(TransformerDataModel.class);
    transformerDataMock = createNiceMock(TransformerData.class);

    rockFactoryMock = createNiceMock(RockFactory.class);

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

    transformerMock = createNiceMock(Transformer.class);

    // Set up list of mappings:
    oldMappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    oldMappings.put(t1Mock, transformers);

    tableInfos = new Vector<TableInformation>();

    // Disable logging to console:
    TransformationTableModel.getLogger().setUseParentHandlers(false);
  }

  @After
  protected void tearDown() throws Exception {
    //
  }

  /**
   * Tests setValueAt() (single transformation is changed).
   * 
   * @throws Exception
   */
  public void testSetValueAt() {

    // Set up a transformation and mappings:
    final Transformation testTransformation = createTestTransformation("config1", "source1", 0, "transformer1",
        "target1", "type1");

    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformer1");
    transformers.add("transformer2");
    transformers.add("transformer3");
    mappings.put(testTransformation, transformers);

    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, DATA_FORMAT_TYPE) {

      protected Transformation getTransformationFromData(final int row) {
        return testTransformation;
      }

      // Override GUI related calls. This allows us to test the logic of the
      // method in isolation:
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

    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(mappings).once();
    replay(transformerDataModelMock);

    // Set up dummy display data:
    Vector<Object> displayData = new Vector<Object>();
    Transformation t1 = createTestTransformation("config1", "source1", 0, "transformer1", "target1", "type1");
    Transformation t2 = createTestTransformation("config2", "source2", 1, "transformer1", "target2", "type2");
    Transformation t3 = createTestTransformation("config3", "source3", 2, "transformer1", "target3", "type3");
    Transformation t4 = createTestTransformation("config4", "source4", 3, "transformer1", "target4", "type4");
    displayData.add(t1);
    displayData.add(t2);
    displayData.add(t3);
    displayData.add(t4);
    testInstance.setDisplayData(displayData);

    testInstance.setValueAt((Object) testTransformation, 0, 0);

    // Mappings should be the same.
    // We have removed the old mapping for the transformation (transformer1)
    // then but re added it so all 3 should be there:
    Assert.assertTrue("Mappings should be the same after calling setValueAt", mappings.get(testTransformation)
        .contains("transformer1"));
    Assert.assertTrue("Mappings should be the same after calling setValueAt", mappings.get(testTransformation)
        .contains("transformer2"));
    Assert.assertTrue("Mappings should be the same after calling setValueAt", mappings.get(testTransformation)
        .contains("transformer3"));

    EasyMock.verify(transformerDataModelMock);
  }

  /**
   * Tests setValueAt(), called for a transformation that is the duplicate of
   * another. The mapping should not be removed if the original transformation
   * is still in this transformer (in the display data).
   * 
   * @throws Exception
   */
  public void testSetValueAtEditDuplicate() {

    // Set up a list of transformations in a transformer.
    // One transformation is a duplicate of another (t1Duplicate):
    final Transformation t1 = createTestTransformation("config1", "source1", 0, "transformerID1", "target1", "type1");
    final Transformation t1Duplicate = createTestTransformation("config1", "source1", 1,
        "transformerID1", "target1", "type1");
    final Transformation t2 = createTestTransformation("config2", "source2", 2, "transformerID1", "target2", "type2");
    final Transformation t3 = createTestTransformation("config3", "source3", 3, "transformerID1", "target3", "type3");
    final Transformation t4 = createTestTransformation("config4", "source4", 4, "transformerID1", "target4", "type4");
    
    // Set up display data. Add a duplicated transformation that has been edited:
    Vector<Object> displayData = new Vector<Object>();
    displayData.add(t1);
    displayData.add(t1Duplicate);
    displayData.add(t2);
    displayData.add(t3);
    displayData.add(t4);

    // Set up a list of transformers:
    ArrayList<String> transformers = new ArrayList<String>();
    transformers.add("transformerID1");
    transformers.add("transformerID2");
    transformers.add("transformerID3");

    // Set up mappings:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    mappings.put(t1, transformers);
    mappings.put(t2, transformers);
    mappings.put(t3, transformers);
    mappings.put(t4, transformers);

    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, DATA_FORMAT_TYPE) {

      protected Transformation getTransformationFromData(final int row) {
        return t1Duplicate;
      }

      protected void setColumnValue(final Transformation transformation, final int col, final Object value) {
        // The first transformation is edited so simulate this by setting the
        // values that setColumnValue() would:
        t1Duplicate.setConfig("config1_edited");
        t1Duplicate.setSource("source1_edited");
        t1Duplicate.setTarget("target1_edited");
        t1Duplicate.setType("type1_edited");
      }

      // Override GUI related calls. This allows us to test the logic of the
      // method in isolation:
      public void refreshTable() {
        // overridden
      }

      public void fireTableDataChanged() {
        // overridden
      }
    };

    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(mappings).once();
    replay(transformerDataModelMock);

    // Set up the display data:
    testInstance.setDisplayData(displayData);

    /**
     * Call setValueAt. 
     * This will do the following:
     * 1. Remove the mapping for the transformation being edited. 
     * 2. Change the values in the transformation (simulated by setColumnValue() above). 
     * 3. Put in a new mapping for the "new" transformation (new because it's edited).
     */
    testInstance.setValueAt((Object) t1Duplicate, 0, 0);

    // The mapping for the original transformation should not be removed.
    Assert.assertNotNull("Mappings should not be null for original transformation", mappings.get(t1));
    Assert.assertTrue("Mappings should be the same after calling setValueAt", mappings.get(t1).contains("transformerID1"));
    Assert.assertTrue("Mappings should be created for new edited transformation", mappings.get(t1Duplicate).contains("transformerID1"));

    EasyMock.verify(transformerDataModelMock);
  }

  /**
   * Tests setValueAt() (single transformation is changed). No mappings exist
   * for the transformation.
   * 
   * @throws Exception
   */
  public void testSetValueAtNoMappings() {

    // Test with no mappings existing for the existing transformation:
    oldMappings.clear();
    
    // Set up a test transformation:
    final Transformation t1 = createTestTransformation("config1", "source1", 0, "transformerID1", "target1", "type1");

    // Test instance of CommonTransformationTableModel:
    @SuppressWarnings("serial")
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, DATA_FORMAT_TYPE) {

      protected Transformation getTransformationFromData(final int row) {
        return t1;
      }

      // Override GUI related calls. This allows us to test the logic of the
      // method in isolation:
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

    // Set up dummy display data:
    Vector<Object> displayData = new Vector<Object>();
    displayData.add(t1);
    testInstance.setDisplayData(displayData);
    
    // Call setValueAt:
    testInstance.setValueAt((Object) t1, 0, 0);

    // Mappings should have "transformer1" only for the updated transformation:
    Assert.assertTrue("Updated transformation should have correct mapping",
        oldMappings.get(t1).contains("transformerID1"));

    EasyMock.verify(transformerDataModelMock);
  }

  /**
   * Tests the method that actually updates a transformation when the user
   * changes a cell.
   */
  public void testSetColumnValue() {

    // Set up test instance of TransformationTableModel:
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, "");

    // Set source should be called with "test value" only, no carriage returns
    // or tabs should be included:
    t1Mock.setSource("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 0, "test value");
    EasyMock.verify(t1Mock);

    // Target
    // Set target should be called with "test value" only, no carriage returns
    // or tabs should be included:
    EasyMock.reset(t1Mock);
    t1Mock.setTarget("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 1, "test value");
    EasyMock.verify(t1Mock);

    // Config:
    EasyMock.reset(t1Mock);
    // Set config should be called with "test value" only, no carriage returns
    // or tabs should be included:
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
    // Set description should be called with "test value" only, no carriage
    // returns or tabs should be included:
    EasyMock.reset(t1Mock);
    t1Mock.setDescription("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 4, "test value");
    EasyMock.verify(t1Mock);
  }

  /**
   * Tests the method that actually updates a transformation when the user
   * changes a cell. Some of the existing values are null.
   */
  public void testSetColumnValueWithNullValues() {

    // Set up test instance of TransformationTableModel:
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, "");

    // Set source should be called with "test value" only, no carriage returns
    // or tabs should be included:
    t1Mock.setSource("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 0, "test value");
    EasyMock.verify(t1Mock);

    // Target
    // Set target should be called with "test value" only, no carriage returns
    // or tabs should be included:
    EasyMock.reset(t1Mock);
    t1Mock.setTarget("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 1, "test value");
    EasyMock.verify(t1Mock);

    // Config:
    EasyMock.reset(t1Mock);
    // Set config should be called with "test value" only, no carriage returns
    // or tabs should be included:
    EasyMock.expect(t1Mock.getConfig()).andReturn(null);
    EasyMock.expect(t1Mock.getType()).andReturn(null);

    t1Mock.setConfig("");
    EasyMock.expectLastCall();
    t1Mock.setType("");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 3, t1Mock);
    EasyMock.verify(t1Mock);

    // Description:
    // Set description should be called with "test value" only, no carriage
    // returns or tabs should be included:
    EasyMock.reset(t1Mock);
    t1Mock.setDescription("test value");
    EasyMock.expectLastCall();
    // Activate the mock:
    replay(t1Mock);
    testInstance.setColumnValue(t1Mock, 4, "test value");
    EasyMock.verify(t1Mock);
  }

  /**
   * Tests creating a new transformation in one of the specific transformers.
   */
  public void testCreateNew() {
    // Set up test instance of TransformationTableModel:
    @SuppressWarnings("serial")
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, DATA_FORMAT_TYPE) {

      protected Transformation createNewTransformation() {
        return t1Mock;
      }
    };

    // Test with no mappings existing. This is a new transformation:
    oldMappings.clear();

    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    EasyMock.expect(t1Mock.getTransformerid()).andReturn("transformer1").once();

    replay(transformerDataModelMock);
    replay(t1Mock);

    final RockDBObject result = testInstance.createNew();

    Assert.assertNotNull("Newly created transformation should not be null", result);
    Assert.assertTrue("The mapping for a new common transformation should have the correct transformer", oldMappings
        .get(t1Mock).contains("transformer1"));
    Assert.assertTrue("New transformation should have 1 new mapping", oldMappings.get(t1Mock).size() == 1);

    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
  }

  /**
   * Tests creating a new transformation in one of the specific transformers.
   * Mappings already exist for the same transformation in other transformers.
   * So we should have 4 mappings after this is done.
   */
  public void testCreateNewWithMappings() {
    // Set up test instance of TransformationTableModel:
    @SuppressWarnings("serial")
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, DATA_FORMAT_TYPE) {

      protected Transformation createNewTransformation() {
        return t1Mock;
      }
    };

    // The transformer ID that the new transformation is going into:
    final String newTransformerID = "transformer_NEW";

    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    EasyMock.expect(t1Mock.getTransformerid()).andReturn(newTransformerID).once();

    replay(transformerDataModelMock);
    replay(t1Mock);

    final RockDBObject result = testInstance.createNew();

    Assert.assertNotNull("Newly created transformation should not be null", result);
    Assert.assertTrue("The mapping for a new common transformation should have the correct transformer", oldMappings
        .get(t1Mock).contains(newTransformerID));
    Assert.assertTrue("The mapping for a new common transformation should have the correct transformer", oldMappings
        .get(t1Mock).contains("transformer1"));
    Assert.assertTrue("The mapping for a new common transformation should have the correct transformer", oldMappings
        .get(t1Mock).contains("transformer2"));
    Assert.assertTrue("The mapping for a new common transformation should have the correct transformer", oldMappings
        .get(t1Mock).contains("transformer3"));
    Assert.assertTrue("New transformation should have 1 new mapping", oldMappings.get(t1Mock).size() == 4);

    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
  }

  /**
   * Test deleting data. Should remove the mapping for an individual
   * transformation.
   * 
   * @throws RockException
   * @throws SQLException
   */
  public void testDeleteData() throws SQLException, RockException {
    // Set up test instance of TransformationTableModel:
    TransformationTableModel testInstance = new TransformationTableModel(transformerDataModelMock, rockFactoryMock,
        tableInfos, true, transformerMock, DATA_FORMAT_TYPE);

    // Test deleting the transformation (t1Mock) from transformer1.
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(oldMappings).once();
    EasyMock.expect(t1Mock.getTransformerid()).andReturn("transformer1").once();

    replay(transformerDataModelMock);
    replay(t1Mock);

    testInstance.deleteData(t1Mock);

    // After this is done, transformer1 should not be in the mappings, but
    // transformer2 and transformer3 should be still there.
    Assert.assertFalse("After deleting a transformation, mapping should not be there for its transformer.", oldMappings
        .get(t1Mock).contains("transformer1"));
    Assert.assertTrue("After deleting a transformation, mappings for other transformer should still be there.",
        oldMappings.get(t1Mock).contains("transformer2"));
    Assert.assertTrue("After deleting a transformation, mappings for other transformer should still be there.",
        oldMappings.get(t1Mock).contains("transformer3"));

    EasyMock.verify(transformerDataModelMock);
    EasyMock.verify(t1Mock);
  }

  /**
   * Creates a new test transformation object.
   * 
   * @param config
   * @param source
   * @param orderNo
   * @param transformerID
   * @return
   */
  private Transformation createTestTransformation(final String config, final String source, final long orderNo,
      final String transformerID, final String target, final String type) {
    Transformation transformation = new Transformation();
    transformation.setConfig(config);
    transformation.setSource(source);
    transformation.setTarget(target);
    transformation.setType(type);
    transformation.setOrderno(orderNo);
    transformation.setTransformerid(transformerID);
    return transformation;
  }

}
