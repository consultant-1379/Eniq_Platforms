package com.ericsson.eniq.techpacksdk.view.transformer;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * Test for TransformerDataModel.
 * @author eciacah
 */
public class TransformerDataModelTest extends TestCase {
  
  // Mocks:
  private TransformerDataModel transformerDataModelMock;
  private TransformerData transformerDataMock;
  private static String ALL_TRANSFORMER_ID = "DC_E_STN:ALL:mdc";
  private RockFactory rockFactoryMock;
  private TransformerData transformerDataMock2;
  private TransformerData transformerDataMock3;
  private List<TransformerData> transformerDataList;
  private Transformation t1Mock;
  private Vector<Transformation> allTransformations;
  private Transformer transformerMock;
  private HashMap<Transformation, ArrayList<String>> oldMappings;
  private Vector<TableInformation> tableInfos;
  private Map<String, Vector<Transformer>> dfToTransformers;
  private Versioning versioningMock;
  
  public TransformerDataModelTest() {
    super("TransformerDataModelTest");
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
    allTransformations = new Vector<Transformation>();
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
    
    // Dummy map of data formats to Transformers:
    dfToTransformers = new HashMap<String, Vector<Transformer>>();
    // Add mdc:
    Vector<Transformer> mdcTransformers = new Vector<Transformer>();
    mdcTransformers.add(transformerMock);
    mdcTransformers.add(transformerMock);
    mdcTransformers.add(transformerMock);
    dfToTransformers.put("mdc", mdcTransformers);
    // Add ct:
//    Vector<Transformer> ctTransformers = new Vector<Transformer>();
//    mdcTransformers.add(transformerMock);
//    mdcTransformers.add(transformerMock);
//    dfToTransformers.put("ct", ctTransformers);

    versioningMock = createNiceMock(Versioning.class);
    
    // Disable logging to console:
    TransformerDataModel.getLogger().setUseParentHandlers(false);
  }

  @After
  protected void tearDown() throws Exception {
    //
  }
    
  /**
   * Tests refresh().
   * This method sets up the 'transformers' field in TransformerDataModel.
   * @throws Exception
   */
  public void testRefresh() {
    // Test instance of TransformerDataModel:
    TransformerDataModel testInstance = new TransformerDataModel(rockFactoryMock) {
      
      @Override
      public Map<String, Vector<Transformer>> getAllTransformationsDB() {
        return dfToTransformers;
      }
      
      @Override
      public Map<String, Vector<Transformer>> getTransformers(final String versionId) {
        return dfToTransformers;
      } // both of these methods are identical???
      
      @Override
      public Vector<Transformation> getTransformations(final Transformer transformer) {
        return allTransformations;
      }
      
      @Override
      protected boolean getALLTransformations(Vector<Transformer> v, long highestOrderNo, final String key)
      throws SQLException, RockException {
        // Found no transformations (no old ALL transformations exist):
        return false;
      }
    };
    
    testInstance.setCurrentVersioning(versioningMock);
    testInstance.setRefreshNeeded(false);
    
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC").times(2);
    EasyMock.expect(transformerMock.getType()).andReturn("ALL");
    EasyMock.expect(t1Mock.getOrderno()).andReturn(100l).times(2);

    replay(transformerMock);
    replay(t1Mock);
    replay(versioningMock);
            
    // Call refresh:
    testInstance.refresh();
    
    EasyMock.verify(transformerMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(versioningMock);           

    // Purpose of this method is to populate the transformers field:    
    // Check transformers field as the result.
    Assert.assertTrue("Transformers in data model should have correct number of elements", testInstance.getTransformerData("mdc").size() == 2);    
  }
  
  /**
   * Tests refresh().
   * This method sets up the 'transformers' field in TransformerDataModel.
   * Test with old "ALL" transformation found. 
   * @throws Exception
   */
  public void testRefreshALLFound() {
        
    // Test instance of TransformerDataModel:
    TransformerDataModel testInstance = new TransformerDataModel(rockFactoryMock) {
      
      // This is to return true then false from getALLTransformations:
      final boolean[] foundALLTransformations = {true, false};
      int index = 0;

      @Override
      public Map<String, Vector<Transformer>> getAllTransformationsDB() {
        return dfToTransformers;
      }
      
      @Override
      public Map<String, Vector<Transformer>> getTransformers(final String versionId) {
        return dfToTransformers;
      } // both of these methods are identical???
      
      @Override
      public Vector<Transformation> getTransformations(final Transformer transformer) {
        return allTransformations;
      }
      
      @Override
      protected boolean getALLTransformations(Vector<Transformer> v, long highestOrderNo, final String key)
      throws SQLException, RockException {
        // Found ALL transformations:        
        final boolean result = foundALLTransformations[index];
        index++;
        return result;
      }
    };
    
    testInstance.setCurrentVersioning(versioningMock);
    testInstance.setRefreshNeeded(false);    
        
    // refresh() will be called a second time if there are ALL transformations.
    // Do expectations separately otherwise can't guarantee the order.
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("ALL");
    
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("ALL");
    EasyMock.expect(t1Mock.getOrderno()).andReturn(100l).times(4);

    replay(transformerMock);
    replay(t1Mock);  
    replay(versioningMock);
            
    // Call refresh:
    testInstance.refresh();
    
    EasyMock.verify(transformerMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(versioningMock);

    // Purpose of this method is to populate the transformers field:    
    // Check transformers field as the result.
    Assert.assertTrue("Transformers in data model should have correct number of elements", testInstance.getTransformerData("mdc").size() == 2);    
  }
  
  /**
   * Tests getALLTransformations().
   * @throws RockException 
   * @throws SQLException 
   * @throws Exception
   */
  public void testGetALLTransformations() throws SQLException, RockException {
        
    // Test instance of TransformerDataModel:
    TransformerDataModel testInstance = new TransformerDataModel(rockFactoryMock) {

      @Override
      public Map<String, Vector<Transformer>> getAllTransformationsDB() {
        return dfToTransformers;
      }
      
      @Override
      public Map<String, Vector<Transformer>> getTransformers(final String versionId) {
        return dfToTransformers;
      } // both of these methods are identical???
      
      @Override
      public Vector<Transformation> getTransformations(final Transformer transformer) {
        return allTransformations;
      }   
      
      @Override
      protected Transformation createTransformation() {
        return t1Mock;
      }
    };
    
    // The highest order number from the specific transformations for this transformer is 100:
    final long highestOrderNo = 100l;
    
    testInstance.setCurrentVersioning(versioningMock);
    testInstance.setRefreshNeeded(false);    
        
    // refresh() will be called a second time if there are ALL transformations.
    // Do expectations separately otherwise can't guarantee the order.
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("ALL");
    EasyMock.expect(t1Mock.getOrderno()).andReturn(100l);
    
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("SPECIFIC");
    EasyMock.expect(transformerMock.getType()).andReturn("ALL");
    
    EasyMock.expect(t1Mock.clone()).andReturn(t1Mock).times(2);
    
    // The order number used to write to the database should be incremented by one before starting (101): 
    t1Mock.setOrderno(101l);
    EasyMock.expectLastCall();
    
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("TEST_ID");
    
    t1Mock.setTransformerid("TEST_ID");
    EasyMock.expectLastCall();
    
    EasyMock.expect(t1Mock.insertDB()).andReturn(1).times(2);
    
    EasyMock.expect(t1Mock.deleteDB()).andReturn(1);    
                
    
    final String key = "mdc";
    
    Vector<Transformer> mdcTransformers = new Vector<Transformer>();
    mdcTransformers.add(transformerMock);
    mdcTransformers.add(transformerMock);
    mdcTransformers.add(transformerMock);
    
    replay(transformerMock);
    replay(t1Mock);  
    replay(versioningMock);
    
    testInstance.setTransformers(new HashMap<String, Vector<TransformerData>>());
    
    // Call getALLTransformations:
    boolean result = false;
    try {
      result = testInstance.getALLTransformations(mdcTransformers, highestOrderNo, key);
    } catch (SQLException e) {
      fail();
    } catch (RockException e) {
      fail();
    }
    
    EasyMock.verify(transformerMock);
    EasyMock.verify(t1Mock);
    EasyMock.verify(versioningMock);

    Assert.assertTrue("ALL transformer should be added to transformers", testInstance.getTransformerData("mdc").size() == 1);
    Assert.assertTrue("getALLTransformations should return true if transformations are found for ALL transformer", result == true);        
  }
  
}
