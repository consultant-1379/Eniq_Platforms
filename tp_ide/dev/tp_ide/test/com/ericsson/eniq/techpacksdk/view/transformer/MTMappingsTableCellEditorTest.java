package com.ericsson.eniq.techpacksdk.view.transformer;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.junit.After;
import org.junit.Before;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.ericsson.eniq.component.TableComponent;


/**
 * Test for MTMappingsTableCellEditor.
 * @author eciacah
 */
public class MTMappingsTableCellEditorTest extends TestCase {
  
  // Mocks:
  private TransformerDataModel transformerDataModelMock;  
  private SingleFrameApplication applicationMock;
  private TransformerData transformerDataMock;
  private static String ALL_TRANSFORMER_ID = "DC_E_STN:ALL:mdc";
  private static String DATA_FORMAT_TYPE = "mdc";
  
  public MTMappingsTableCellEditorTest() {
    super("MTMappingsTableCellEditorTest");
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
   * Tests setupMappings() where a transformation is enabled for All transformers.
   * Mappings HashMap should have a mapping for all of the transformers (for that transformation).
   * @throws Exception
   */
  public void testSetupMappingsForAll() {        
    // All transformations:
    Transformation t1Mock = createNiceMock(Transformation.class);
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
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableCellEditor testInstance = new MTMappingsTableCellEditor(allTransformations, transformerDataList, 
        transformerDataModelMock, applicationMock, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE);    
    
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(mappings);
    
    // Check transformer ID of the transformation, it is for ALL transformers:
    EasyMock.expect(t1Mock.getTransformerid()).andReturn("ALL");
    
    // Next go through all of the transformers:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock);
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("SPECIFIC");
    
    EasyMock.expect(transformerDataMock2.getTransformer()).andReturn(transformerMock2);
    EasyMock.expect(transformerMock2.getTransformerid()).andReturn("SPECIFIC2");
    
    EasyMock.expect(transformerDataMock3.getTransformer()).andReturn(transformerMock3);
    EasyMock.expect(transformerMock3.getTransformerid()).andReturn("SPECIFIC3");
    
    replay(transformerDataModelMock);
    replay(t1Mock);
    replay(transformerDataMock);
    replay(transformerDataMock2);
    replay(transformerDataMock3);
    replay(transformerMock);
    replay(transformerMock2);  
    replay(transformerMock3);  
            
    testInstance.setupMappings();
        
    Assert.assertTrue("setupMappings() should return the correct number of mappings", mappings.get(t1Mock).size() == 3);
  }
  
  /**
   * Tests setupMappings() where a transformation is enabled only for some transformers.
   * Mappings HashMap should have a mapping for all of the transformer (for that transformation).
   * @throws Exception
   */
  public void testSetupMappings() {        
    // All transformations:
//    Transformation t1Mock = createNiceMock(Transformation.class);
//    Transformation t2Mock = createNiceMock(Transformation.class);
    Transformation t1 = new Transformation();
    t1.setTransformerid("SPECIFIC, SPECIFIC2,");
    Transformation t2 = new Transformation();
    t2.setTransformerid("SPECIFIC2, SPECIFIC3,");
    
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1);
    allTransformations.add(t2);
        
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    // Test list of all transformers:
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    // Assume that there are no mappings and that we are setting them up new:
    HashMap<Transformation, ArrayList<String>> mappings = new HashMap<Transformation, ArrayList<String>>();
    
    // Test instance of MTMappingsTableCellEditor.
    MTMappingsTableCellEditor testInstance = new MTMappingsTableCellEditor(allTransformations, transformerDataList, 
        transformerDataModelMock, applicationMock, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE);    
    
    EasyMock.expect(transformerDataModelMock.getMappings(DATA_FORMAT_TYPE)).andReturn(mappings);
    
    // Check transformer ID of the transformation, it is for specific transformers:
//    EasyMock.expect(t1Mock.getTransformerid()).andReturn("SPECIFIC, SPECIFIC2,");
    
    // Next go through all of the transformers:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock).times(2);
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("SPECIFIC").times(2);
    
    EasyMock.expect(transformerDataMock2.getTransformer()).andReturn(transformerMock2).times(2);
    EasyMock.expect(transformerMock2.getTransformerid()).andReturn("SPECIFIC2").times(2);
    
    EasyMock.expect(transformerDataMock3.getTransformer()).andReturn(transformerMock3).times(2);
    EasyMock.expect(transformerMock3.getTransformerid()).andReturn("SPECIFIC3").times(2);
    
    // Check transformer ID of the transformation, it is for specific transformers:
//    EasyMock.expect(t2Mock.getTransformerid()).andReturn("SPECIFIC2, SPECIFIC3,");
        
    replay(transformerDataModelMock);
//    replay(t1Mock);
//    replay(t2Mock);
    replay(transformerDataMock);
    replay(transformerDataMock2);
    replay(transformerDataMock3);
    replay(transformerMock);
    replay(transformerMock2);  
    replay(transformerMock3);  
            
    testInstance.setupMappings();
        
    Assert.assertTrue("setupMappings() should return the correct number of mappings", mappings.get(t1).size() == 2);
    Assert.assertTrue("setupMappings() should return the correct number of mappings", mappings.get(t2).size() == 2);
  }
  

  public void testGetTableCellEditorComponent() {
    
    final MTMappingsTableModel mtMappingsTableModelMock = createNiceMock(MTMappingsTableModel.class);
    final JTable tableMock = createNiceMock(JTable.class);
    final TableComponent tableComponentMock = createNiceMock(TableComponent.class);
    
    // All transformations:
    Transformation t1Mock = createNiceMock(Transformation.class);
    Transformation t2Mock = createNiceMock(Transformation.class);
    final Vector<Object> allTransformations = new Vector<Object>();
    allTransformations.add(t1Mock);
    allTransformations.add(t2Mock);
    
    transformerDataMock = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock2 = createNiceMock(TransformerData.class);
    TransformerData transformerDataMock3 = createNiceMock(TransformerData.class);
    
    final List<TransformerData> transformerDataList = new Vector<TransformerData>();
    // Set up 3 transformers in the list of TransformerData objects:
    transformerDataList.add(transformerDataMock);
    transformerDataList.add(transformerDataMock2);
    transformerDataList.add(transformerDataMock3);
    
    final Transformer transformerMock = createNiceMock(Transformer.class);
    final Transformer transformerMock2 = createNiceMock(Transformer.class);
    final Transformer transformerMock3 = createNiceMock(Transformer.class);
    
    // Next go through all of the transformers:
    EasyMock.expect(transformerDataMock.getTransformer()).andReturn(transformerMock);
    EasyMock.expect(transformerMock.getTransformerid()).andReturn("SPECIFIC");
    
    EasyMock.expect(transformerDataMock2.getTransformer()).andReturn(transformerMock2);
    EasyMock.expect(transformerMock2.getTransformerid()).andReturn("SPECIFIC2");
    
    EasyMock.expect(transformerDataMock3.getTransformer()).andReturn(transformerMock3);
    EasyMock.expect(transformerMock3.getTransformerid()).andReturn("SPECIFIC3");
    
    tableComponentMock.addActionListener((ActionListener)EasyMock.anyObject());
    EasyMock.expectLastCall();
    tableComponentMock.setEnabled(true);
    EasyMock.expectLastCall();
    
    // Test instance of MTMappingsTableCellEditor.
    @SuppressWarnings("serial")
    MTMappingsTableCellEditor testInstance = new MTMappingsTableCellEditor(allTransformations, transformerDataList, 
        transformerDataModelMock, applicationMock, ALL_TRANSFORMER_ID, DATA_FORMAT_TYPE) {
     
      @Override
      protected MTMappingsTableModel createMappingsTableModel(Transformation tfm, Vector<Transformer> transformers) {
        return mtMappingsTableModelMock;
      }
      
      @Override
      protected TableComponent createTableComponent(final Application application, final String title, final MTMappingsTableModel tableModel, 
          final int fieldWidth, final boolean editable) {
        return tableComponentMock;
      }          
    };
    
    replay(transformerDataModelMock);
    replay(t1Mock);
    replay(t2Mock);
    replay(transformerDataMock);
    replay(transformerDataMock2);
    replay(transformerDataMock3);
    replay(transformerMock);
    replay(transformerMock2);  
    replay(transformerMock3); 
    
    testInstance.getTableCellEditorComponent(tableMock, t1Mock, true, 0, 0);
  }
}


