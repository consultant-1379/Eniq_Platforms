package com.ericsson.eniq.techpacksdk.view.measurement;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TTTableModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.DescriptionComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.ParameterPanel;
import tableTreeUtils.RadiobuttonComponent;
import tableTreeUtils.TCTableModel;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TreeMainNode;
import tableTreeUtils.UniverseExtensionComponent;

import com.distocraft.dc5000.repository.dwhrep.Measurementdeltacalcsupport;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.TableComponent;
import com.ericsson.eniq.techpacksdk.common.Constants;


/**
 * Concrete factory class for MeasurementTypes.
 * 
 * @author enaland ejeahei
 * 
 */
public class MeasurementTypeFactory extends TreeDataFactory {

  private static final Logger LOGGER = Logger.getLogger(MeasurementTypeFactory.class.getName());

  private Application application;

  /**
   * The rock factory is the interface towards the DB.
   */
  private RockFactory rockFactory = null;

  private MeasurementTypeDataModel measurementTypeDataModel;

  /**
   * The constructor initiates the renderer and DB interface for this specific
   * type of dialogue.
   * 
   * @param rockFactory
   *          Current RockFactory instance
   * 
   * @param editable
   *          true in case the tree is editable
   * @param forcedNamePrefix
   *          the forced prefix for the tree main node names.
   */
  private MeasurementTypeFactory(final RockFactory rockFactory, final boolean editable, final String forcedNamePrefix) {
    this.rockFactory = rockFactory;
    this.editable = editable;
    this.setForcedNamePrefix(forcedNamePrefix);
    treeCellRenderer = new MeasurementTypeRenderer();
    createTableInfoData(); // Collect info about the different table types
    // used
    this.setMaximumTreeNodeNameLength(Measurementtype.getTypeidColumnSize());
  }

  public MeasurementTypeFactory(final Application application, final MeasurementTypeDataModel measurementTypeDataModel,
      final boolean editable, final String forcedNamePrefix) {
    this(measurementTypeDataModel.getRockFactory(), editable, forcedNamePrefix);
    this.application = application;
    this.measurementTypeDataModel = measurementTypeDataModel;
  }

  /** 
   * Constructor for test.
   */
  protected MeasurementTypeFactory() {
    
  }
  
  private Versioning versioning;

  private Versioning baseversioning;

  private List<Measurementtypeclass> measurementtypeclasses;

  private String[] universeExtensions;

  /**
   * Create a tree cell editor for the specific type of dialogue
   * (MeasurementType).
   * 
   * @param theTree
   * @return treeCellEditor.
   */
  @Override
  public TTEditor getTreeCellEditor(final JTree theTree) {
    if (treeCellEditor == null) {
      treeCellEditor = new MeasurementTypeEditor(theTree);
    }
    return treeCellEditor;
  }

  /**
   * Create the tree model for the tree and initiate it with data from the
   * database.
   * 
   * @return treeModel
   */
  @Override
  public TreeModel createAndGetModel() {

    // Create the root node
    final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    final DefaultTreeModel theModel = new DefaultTreeModel(root);

    measurementTypeDataModel.setCurrentVersioning(versioning);
    measurementTypeDataModel.setBaseVersioning(baseversioning);
    measurementTypeDataModel.refresh();
    universeExtensions = measurementTypeDataModel.getUniverseExtensions();

    measurementtypeclasses = measurementTypeDataModel.getMeasurementtypeclasses();
    final List<MeasurementTypeData> measurements = measurementTypeDataModel.getMeasurements();

    if (measurements != null) {
      LOGGER.finest("MeasurementTypeFactory create MeasurementTypes started");
      // Loop through the measurement types, get their data and add them
      // to the model
      for (final Iterator<MeasurementTypeData> iter = measurements.iterator(); iter.hasNext();) {

        final TreeMainNode mainNode = createMeasurementype(iter.next());
        root.add(mainNode);
      }
      LOGGER.finest("MeasurementTypeFactory create MeasurementTypes finished");
    }
    return theModel;
  }

  /**
   * Method for collecting info of the different table types
   */
  protected void createTableInfoData() {
    tableInformations.addElement(MeasurementTypeKeyTableModel.createTableTypeInfo());
    tableInformations.addElement(MeasurementTypeCounterTableModel.createTableTypeInfo());
    tableInformations.addElement(MeasurementDeltaCalcSupportTableModel.createTableTypeInfo());
    tableInformations.addElement(MeasurementObjBHTableModel.createTableTypeInfo());
    tableInformations.addElement(MeasurementVectorTableModel.createTableTypeInfo());
  }

  /**
   * Helper method for creating the measurement type node and all its children
   * 
   * @param measurementtype
   * @return
   */
  private TreeMainNode createMeasurementype(final MeasurementTypeData measurementTypeData) {

    final MeasurementtypeExt measurementtypeExt = measurementTypeData.getMeasurementtypeExt();
    final Measurementtype measurementtype = measurementtypeExt.getMeasurementtype();
    final Vector<Object> myKeys = measurementTypeData.getMeasurementkeys();
    final Vector<Object> myCounters = measurementTypeData.getMeasurementcounterExts();
    final Vector<Object> myDcsupports = measurementtypeExt.getDeltaCalcSupport();
    final Vector<Object> myObjectbhs = measurementtypeExt.getObjBHSupport();

    // Create the child node data for all three nodes: parameters, keys and
    // counters
    final Vector<TCTableModel> vectorcounterTableModels = new Vector<TCTableModel>();
    for (final Iterator<Object> iter = myCounters.iterator(); iter.hasNext();) {
      final Object item = iter.next();
      if (item instanceof MeasurementcounterExt) {
        final MeasurementcounterExt measurementcounterExt = (MeasurementcounterExt) item;
        final MeasurementVectorTableModel vectorcounterTableModel = new MeasurementVectorTableModel(rockFactory,
            tableInformations, editable, measurementcounterExt, measurementTypeDataModel.getVendorReleases());
        vectorcounterTableModel.setData(measurementcounterExt.getVectorcounters());
        measurementcounterExt.setVectorCounterTableModel(vectorcounterTableModel);
        vectorcounterTableModels.add(vectorcounterTableModel);
      }
    }
    final TTTableModel keyTableModel = new MeasurementTypeKeyTableModel(application, rockFactory, tableInformations,
        editable, measurementtype, versioning.getTechpack_type());
    final TTTableModel counterTableModel = new MeasurementTypeCounterTableModel(application, rockFactory,
        tableInformations, editable, measurementtypeExt, measurementTypeDataModel.getVendorReleases(), versioning
            .getTechpack_type());
    final TCTableModel dcsupportTableModel = new MeasurementDeltaCalcSupportTableModel(application, rockFactory,
        tableInformations, editable, measurementtype);
    final TCTableModel objectbhTableModel = new MeasurementObjBHTableModel(application, rockFactory, tableInformations,
        editable, measurementtype);
    final TTParameterModel parameterModel = MeasurementTypeParamModelFactory.createMeasurementTypeParamModel(
        application, versioning, measurementtypeclasses, measurementtypeExt, dcsupportTableModel, objectbhTableModel,
        universeExtensions, rockFactory, editable);

    // Set the observer/observable relationships
    parameterModel.addObserver(keyTableModel);
    parameterModel.addObserver(counterTableModel);
    parameterModel.addObserver(dcsupportTableModel);
    parameterModel.addObserver(objectbhTableModel);
    for (final Iterator<TCTableModel> iter = vectorcounterTableModels.iterator(); iter.hasNext();) {
      parameterModel.addObserver(iter.next());
    }

    // Create the main node
    final TreeMainNode mainNode = new TreeMainNode(parameterModel);

    // Create the child nodes
    final DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
    final DefaultMutableTreeNode keyNode = createTableNode(myKeys, keyTableModel);
    final DefaultMutableTreeNode counterNode = createTableNode(myCounters, counterTableModel);
    final DefaultMutableTreeNode dcsupportNode = createTableNode(myDcsupports, dcsupportTableModel);
    final DefaultMutableTreeNode objectbhNode = createTableNode(myObjectbhs, objectbhTableModel);
    final Vector<DefaultMutableTreeNode> vectorcounterNodes = new Vector<DefaultMutableTreeNode>();
    for (final Iterator<TCTableModel> iter = vectorcounterTableModels.iterator(); iter.hasNext();) {
      final TCTableModel vectorcounterTableModel = iter.next();
      final DefaultMutableTreeNode vectorcounterNode = createTableNode(vectorcounterTableModel.getData(),
          vectorcounterTableModel);
      vectorcounterNodes.add(vectorcounterNode);
    }

    // Connect the nodes
    mainNode.add(parameterNode);
    mainNode.add(keyNode);
    mainNode.add(counterNode);
    // DeltaCalcSupport & ObjectBH & VectorCounter are not visible in tree
    // mainNode.add(dcsupportNode);
    // mainNode.add(objectbhNode);
    // for (Iterator<DefaultMutableTreeNode> iter =
    // vectorcounterNodes.iterator(); iter.hasNext(); ) {
    // mainNode.add(iter.next());
    // }

    // Create deletion order
    final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
    for (final Iterator<DefaultMutableTreeNode> iter = vectorcounterNodes.iterator(); iter.hasNext();) {
      nodeOrder.addElement(iter.next());
    }
    nodeOrder.addElement(counterNode);
    nodeOrder.addElement(keyNode);
    nodeOrder.addElement(dcsupportNode);
    nodeOrder.addElement(objectbhNode);
    nodeOrder.addElement(parameterNode);

    setDeletionOrder(mainNode, nodeOrder);

    return mainNode;
  }

  /**
   * Overridden version of the corresponding TreeDataFactory method. Used for
   * creating new empty nodes when the user inserts nodes in a tree.
   */
  @Override
  public TreeMainNode createEmptyNode() {
    // Create new type and table objects
    final MeasurementTypeData emptyMeasurementType = new MeasurementTypeData(versioning, rockFactory);
    final MeasurementtypeExt measurementtypeExt = emptyMeasurementType.getMeasurementtypeExt();
    final Measurementtype measurementtype = measurementtypeExt.getMeasurementtype();
    // Get default values for delta calc support
    measurementtypeExt.setDeltaCalcSupport(measurementTypeDataModel
        .getDeltaCalcSupportsForMeasurementtype(measurementtype));
    final Vector<Object> myKeys = emptyMeasurementType.getMeasurementkeys();
    final Vector<Object> myCounters = emptyMeasurementType.getMeasurementcounterExts();
    final Vector<Object> myDcsupports = measurementtypeExt.getDeltaCalcSupport();
    final Vector<Object> myObjectbhs = measurementtypeExt.getObjBHSupport();

    // Create the empty data sets for the child nodes
    final MeasurementcounterExt emptyMeasurementcounterExt = new MeasurementcounterExt(rockFactory);
    final MeasurementVectorTableModel vectorcounterTableModel = new MeasurementVectorTableModel(rockFactory,
        tableInformations, editable, emptyMeasurementcounterExt, measurementTypeDataModel.getVendorReleases());
    vectorcounterTableModel.setData(emptyMeasurementcounterExt.getVectorcounters());
    emptyMeasurementcounterExt.setVectorCounterTableModel(vectorcounterTableModel);
    final TTTableModel keyTableModel = new MeasurementTypeKeyTableModel(application, rockFactory, tableInformations,
        editable, measurementtype, versioning.getTechpack_type());
    final TTTableModel counterTableModel = new MeasurementTypeCounterTableModel(application, rockFactory,
        tableInformations, editable, measurementtypeExt, measurementTypeDataModel.getVendorReleases(), versioning
            .getTechpack_type());
    final TCTableModel dcsupportTableModel = new MeasurementDeltaCalcSupportTableModel(application, rockFactory,
        tableInformations, editable, measurementtype);
    final TCTableModel objectbhTableModel = new MeasurementObjBHTableModel(application, rockFactory, tableInformations,
        editable, measurementtype);
    final TTParameterModel compositeData = MeasurementTypeParamModelFactory.createMeasurementTypeParamModel(
        application, versioning, measurementtypeclasses, emptyMeasurementType.getMeasurementtypeExt(),
        dcsupportTableModel, objectbhTableModel, universeExtensions, rockFactory, editable);

    // Set the observer/observable relationships
    compositeData.addObserver(keyTableModel);
    compositeData.addObserver(counterTableModel);
    compositeData.addObserver(dcsupportTableModel);
    compositeData.addObserver(objectbhTableModel);
    compositeData.addObserver(vectorcounterTableModel);

    // Create the main node and all the child nodes
    final TreeMainNode mainNode = new TreeMainNode(compositeData);
    final DefaultMutableTreeNode parameterNode = createParameterNode(compositeData);
    final DefaultMutableTreeNode keyNode = createTableNode(myKeys, keyTableModel);
    final DefaultMutableTreeNode counterNode = createTableNode(myCounters, counterTableModel);
    final DefaultMutableTreeNode dcsupportNode = createTableNode(myDcsupports, dcsupportTableModel);
    final DefaultMutableTreeNode objectbhNode = createTableNode(myObjectbhs, objectbhTableModel);
    final DefaultMutableTreeNode vectorcounterNode = createTableNode(vectorcounterTableModel.getData(),
        vectorcounterTableModel);

    // Connect all the nodes
    mainNode.add(parameterNode);
    mainNode.add(keyNode);
    mainNode.add(counterNode);
    // DeltaCalcSupport & ObjectBH are not visible in tree
    // mainNode.add(dcsupportNode);
    // mainNode.add(objectbhNode);

    // Create deletion order
    final Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
    nodeOrder.addElement(vectorcounterNode);
    nodeOrder.addElement(counterNode);
    nodeOrder.addElement(keyNode);
    nodeOrder.addElement(dcsupportNode);
    nodeOrder.addElement(objectbhNode);
    nodeOrder.addElement(parameterNode);

    setDeletionOrder(mainNode, nodeOrder);

    return mainNode;
  }

  /**
   * Set deletion order, e.g. tables have to be deleted before the
   * parameterpanel due to references in the data. Nodes are deleted in the
   * order that they are in the vector sent to
   * mainNode.setDeletionOrder(vector). Nodes that aren't included in the vector
   * will be deleted last
   */
  private void setDeletionOrder(final TreeMainNode parentNode, final Vector<DefaultMutableTreeNode> nodes) {
    parentNode.setDeletionOrder(nodes);
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }

  public Versioning getBaseVersioning() {
    return baseversioning;
  }

  public void setBaseVersioning(final Versioning baseversioning) {
    this.baseversioning = baseversioning;
  }

  /**
   * Duplicates an existing tree node data to the new tree node
   */
  @Override
  public void duplicateExistingNode(final TreeMainNode existingNode, final TreeMainNode newNode) {
    // First we need to get the new type id from the new node. This value is
    // needed when cloning the old measurement key and counter table rock
    // objects.
    String newTypeId = null;
    for (int i = 0; i < newNode.getChildCount(); i++) {
      final Object child = ((DefaultMutableTreeNode) newNode.getChildAt(i).getChildAt(0)).getUserObject();
      if (child instanceof ParameterPanel) {
        final MeasurementTypeParameterModel model = (MeasurementTypeParameterModel) ((ParameterPanel) child).getModel();
        newTypeId = (model.getMeasurementtypeExt().getMeasurementtype()).getTypeid();
      }
    }

    // Iterate through all the children.
    for (int i = 0; i < existingNode.getChildCount(); i++) {
      // Get both old and new children. The children must be in the same
      // order under both nodes.
      final Object oldChild = ((DefaultMutableTreeNode) existingNode.getChildAt(i).getChildAt(0)).getUserObject();
      final Object newChild = ((DefaultMutableTreeNode) newNode.getChildAt(i).getChildAt(0)).getUserObject();

      // Copy the data from old child to the new based on the type of the
      // child.
      if (oldChild instanceof ParameterPanel) {
        // The node is a parameter panel

        // Get the old and new models
        final MeasurementTypeParameterModel oldParameterModel = (MeasurementTypeParameterModel) ((ParameterPanel) oldChild)
            .getModel();
        final MeasurementTypeParameterModel newParameterModel = getNewMeasurementTypeParameterModel(newChild,
                oldParameterModel);



        // Get the values from old node.
        final String oldSizing = (String) oldParameterModel.getValueAt(MeasurementTypeParameterModel.SIZING_ID);
        final String oldTableType = (String) oldParameterModel.getValueAt(MeasurementTypeParameterModel.TABLETYPE);
        final int oldAggregation = (Integer) oldParameterModel.getValueAt(MeasurementTypeParameterModel.TOTAL_AGG);
        final int oldSonAggregation = (Integer) oldParameterModel.getValueAt(Constants.SONAGG);
        final int oldSon15MinAggregation = (Integer) oldParameterModel.getValueAt(Constants.SON15AGG);
        final int oldElemBH = (Integer) oldParameterModel.getValueAt(MeasurementTypeParameterModel.ELEM_BH);
        final int oldDataFormatsupport = (Integer) oldParameterModel
            .getValueAt(MeasurementTypeParameterModel.DATAFORMATSUPPORT);
        final Vector<Object> oldObjectBH = (Vector<Object>) oldParameterModel
            .getValueAt(MeasurementTypeParameterModel.OBJ_BH);
        final Vector<Object> oldDelta = (Vector<Object>) oldParameterModel
            .getValueAt(MeasurementTypeParameterModel.DELTA_CALC_SUPPORT);
        final String oldClassification = (String) oldParameterModel
            .getValueAt(MeasurementTypeParameterModel.CLASSIFICATION);
        final String oldUniverseExt = (String) oldParameterModel.getValueAt(MeasurementTypeParameterModel.UNIVERSE_EXT);
        final String oldDescription = (String) oldParameterModel.getValueAt(MeasurementTypeParameterModel.DESCRIPTION);
        String pph = (String) oldParameterModel.getValueAt(MeasurementTypeParameterModel.PRODUCTPLACEHOLDERS);
        String cph = (String) oldParameterModel.getValueAt(MeasurementTypeParameterModel.CUSTOMPLACEHOLDERS);

        // Set the values in the new node
        //
        // NOTE: If setValueAt is used, then the model is updated, but
        // the combo box in the panel will show the old selection.
        // newParameterModel.setValueAt(priority,
        // PromptImplementorParameterModel.PRIORITY);
        //

        // Update ComboBox: Sizing
        int ind = -1;
        String[] sizingItems = oldParameterModel.getSizingItems();
        for (int j = 0; j < sizingItems.length; j++) {
          if (sizingItems[j].equals(oldSizing)) {
            ind = j;
            break;
          }
        }
        PairComponent comp = (PairComponent) ((ParameterPanel) newChild).getComponent(0);
        ((JComboBox) comp.getComponent()).setSelectedIndex(ind);

        // Update RadioButtonComponent: table type
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(1);
        final RadiobuttonComponent radioComp = (RadiobuttonComponent) comp.getComponent();
        radioComp.setSelectedButton(oldTableType);
        newParameterModel.setValueAt(oldTableType, MeasurementTypeParameterModel.TABLETYPE);

        // Update CheckBox: total aggregation
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(2);
        ((JCheckBox) comp.getComponent()).setSelected(oldAggregation == 1);
        newParameterModel.setValueAt(oldAggregation == 1, MeasurementTypeParameterModel.TOTAL_AGG);

        // Update CheckBox: element bh support
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(3);
        ((JCheckBox) comp.getComponent()).setSelected(oldElemBH == 1);
        newParameterModel.setValueAt(oldElemBH == 1, MeasurementTypeParameterModel.ELEM_BH);

        // Update CheckBox: data format support
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(4);
        ((JCheckBox) comp.getComponent()).setSelected(oldDataFormatsupport == 1);
        newParameterModel.setValueAt(oldDataFormatsupport == 1, MeasurementTypeParameterModel.DATAFORMATSUPPORT);

        int index = 4;
        if (oldParameterModel instanceof EventMeasurementTypeParameterModel) {
          final int oldOneMinAgg = (Integer) oldParameterModel
              .getValueAt(EventMeasurementTypeParameterModel.ONE_MIN_AGG);
          final int oldFifteenMinAgg = (Integer) oldParameterModel
              .getValueAt(EventMeasurementTypeParameterModel.FIFTEEN_MIN_AGG);
          final int oldLoadFileDupCheck = (Integer) oldParameterModel
              .getValueAt(EventMeasurementTypeParameterModel.LOAD_FILE_DUP_CHECK);

          comp = (PairComponent) ((ParameterPanel) newChild).getComponent(5);
          ((JCheckBox) comp.getComponent()).setSelected(oldOneMinAgg == 1);
          newParameterModel.setValueAt(oldOneMinAgg == 1, EventMeasurementTypeParameterModel.ONE_MIN_AGG);

          comp = (PairComponent) ((ParameterPanel) newChild).getComponent(6);
          ((JCheckBox) comp.getComponent()).setSelected(oldFifteenMinAgg == 1);
          newParameterModel.setValueAt(oldFifteenMinAgg == 1, EventMeasurementTypeParameterModel.FIFTEEN_MIN_AGG);

          comp = (PairComponent) ((ParameterPanel) newChild).getComponent(7);
          ((JCheckBox) comp.getComponent()).setSelected(oldLoadFileDupCheck == 1);
          newParameterModel
              .setValueAt(oldLoadFileDupCheck == 1, EventMeasurementTypeParameterModel.LOAD_FILE_DUP_CHECK);
          index = 7;
          
        }else{
        	// Update CheckBox: SONAGG aggregation
            comp = (PairComponent) ((ParameterPanel) newChild).getComponent(5);
            if(comp.getComponent() instanceof JCheckBox){
            	((JCheckBox)comp.getComponent()).setSelected(oldSonAggregation == 1);
            	newParameterModel.setValueAt(oldSonAggregation == 1, Constants.SONAGG);
            	index = 5;
            }
                    
            
            
            // Update CheckBox: SON15AGG aggregation
            comp = (PairComponent) ((ParameterPanel) newChild).getComponent(6);
            if(comp.getComponent() instanceof JCheckBox){
            	((JCheckBox) comp.getComponent()).setSelected(oldSon15MinAggregation == 1);
                newParameterModel.setValueAt(oldSon15MinAggregation == 1, Constants.SON15AGG);
                index = 6 ;
            } 
            
        }
        
        
        // Update TableComponent: object bh support
        //
        // TODO: copying works, but the GUI is not updated before
        // save!!!!!!
        //
        //
        // comp = (PairComponent) ((ParameterPanel) newChild)
        // .getComponent(5);
        // ((JTextField) ((TableComponent) comp.getComponent())
        // .getTextField()).setText(oldObjectBH.toString());
        //
        // ((MeasurementObjBHTableModel)
        // newParameterModel.objectbhTableModel)
        // .setData(oldParameterModel.getMeasurementtypeExt()
        // .getDeltaCalcSupport());
        // comp = (PairComponent) ((ParameterPanel) newChild)
        // .getComponent(5);
        // ((JTextField) ((TableComponent) comp.getComponent())
        // .getTextField()).setText(oldObjectBH.toString());

        // Copy the data in the table model
        Measurementobjbhsupport oldObj = null;
        final MeasurementObjBHTableModel oldTableModel1 = (MeasurementObjBHTableModel) oldParameterModel.objectbhTableModel;
        final MeasurementObjBHTableModel newTableModel1 = (MeasurementObjBHTableModel) newParameterModel.objectbhTableModel;
        final Vector<Object> oldData = oldTableModel1.getData();

        for (int j = 0; j < oldData.size(); j++) {
          oldObj = (Measurementobjbhsupport) oldData.elementAt(j);
          // Get the old rock object and clone it
          final Measurementobjbhsupport newObj = (Measurementobjbhsupport) oldTableModel1.copyOf(oldObj);
          // Modify the typeId of the clone to match the new
          newObj.setTypeid(newTypeId);
          // Insert the clone to the new table
          newTableModel1.insertDataLast(newObj);
        }

        // Update the visible value in the components text field after
        // the model has changed.
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 1);
        ((TableComponent) comp.getComponent()).updateDisplayField();

        // Update DescriptionComponent: delta calc support
        Measurementdeltacalcsupport oldObj2 = null;
        final MeasurementDeltaCalcSupportTableModel oldTableModel2 = (MeasurementDeltaCalcSupportTableModel) oldParameterModel.dcsupportTableModel;
        final MeasurementDeltaCalcSupportTableModel newTableModel2 = (MeasurementDeltaCalcSupportTableModel) newParameterModel.dcsupportTableModel;
        
        /*
         * 20110721 eanguan :: Removing the copying of old Dela Calc Support Data again to Newly created Node
         * No need to copy the old data as it is already present in new node.
        final Vector<Object> oldData2 = oldTableModel2.getData();
        for (int j = 0; j < oldData2.size(); j++) {
          oldObj2 = (Measurementdeltacalcsupport) oldData2.elementAt(j);
          // Get the old rock object and clone it
          final Measurementdeltacalcsupport newObj2 = (Measurementdeltacalcsupport) oldTableModel2.copyOf(oldObj2);
          // Modify the typeId of the clone to match the new
          newObj2.setTypeid(newTypeId);
          // Insert the clone to the new table
          newTableModel2.insertDataLast(newObj2);
        }
        */
        
        // Update the visible value in the components text field after
        // the model has changed.
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 2);
        ((TableComponent) comp.getComponent()).updateDisplayField();

        // Update TextField: classification
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 3);
        ((JTextField) comp.getComponent()).setText(oldClassification);
        newParameterModel.setValueAt(oldClassification, MeasurementTypeParameterModel.CLASSIFICATION);

        // Update universe extensions
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 4);
        (((UniverseExtensionComponent) comp.getComponent()).getTextField()).setText(oldUniverseExt);
        newParameterModel.setValueAt(oldUniverseExt, MeasurementTypeParameterModel.UNIVERSE_EXT);

        // Update DescriptionComponent: Description
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 5);
        (((DescriptionComponent) comp.getComponent()).getTextField()).setText(oldDescription);
        newParameterModel.setValueAt(oldDescription, MeasurementTypeParameterModel.DESCRIPTION);

        // Update Product placeholders
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 6);
        ((JTextField) comp.getComponent()).setText(pph);

        newParameterModel.setValueAt(pph, MeasurementTypeParameterModel.PRODUCTPLACEHOLDERS);

        // Update Custom placeholders
        comp = (PairComponent) ((ParameterPanel) newChild).getComponent(index + 7);
        ((JTextField) comp.getComponent()).setText(cph);

        newParameterModel.setValueAt(cph, MeasurementTypeParameterModel.CUSTOMPLACEHOLDERS);

      } else if (oldChild instanceof TableContainer) {
        // The node is a table

        // Get the table models for the old and new table
        final TTTableModel oldTableModel = ((TableContainer) oldChild).getTableModel();
        final TTTableModel newTableModel = ((TableContainer) newChild).getTableModel();

        // Copy all the rock objects in the table to the new table.

        final Vector<Object> data = oldTableModel.getData();
        for (int j = 0; j < data.size(); j++) {
          if (oldTableModel instanceof MeasurementTypeKeyTableModel) {

            final Measurementkey oldObj = (Measurementkey) data.elementAt(j);

            // Get the old rock object and clone it
            final Measurementkey newObj = copyMeasKey(newTypeId, oldTableModel, newTableModel, oldObj);
            
            // Insert the clone to the new table
            newTableModel.insertDataLast(newObj);

          } else if (oldTableModel instanceof MeasurementTypeCounterTableModel) {
            final MeasurementcounterExt oldObj = (MeasurementcounterExt) data.elementAt(j);

            // Get the old rock object and clone it
            final MeasurementcounterExt newObj = copyMeasCounter(newTypeId, oldTableModel, newTableModel, oldObj);

            // Insert the clone to the new table
            newTableModel.insertDataLast(newObj);
          }
        }

        // Refresh table so that it is correctly drawn after copying the
        // data
        ((TableContainer) newChild).tuneSize();
      }
    }
  }
  
  /**
   * Get new MeasurementTypeParameterModel based on the oldParameterModel
   * 
   * @param newChild
   * @param oldParameterModel
   * @return
   */
  private MeasurementTypeParameterModel getNewMeasurementTypeParameterModel(final Object newChild,
      MeasurementTypeParameterModel oldParameterModel) {
    MeasurementTypeParameterModel newParameterModel;
    if (oldParameterModel instanceof EventMeasurementTypeParameterModel) {
      newParameterModel = (EventMeasurementTypeParameterModel) ((ParameterPanel) newChild).getModel();
    } else {
      newParameterModel = (StatsMeasurementTypeParameterModel) ((ParameterPanel) newChild).getModel();
    }
    return newParameterModel;
  }

  protected Measurementkey copyMeasKey(String newTypeId, TTTableModel oldTableModel, TTTableModel newTableModel,
	      Measurementkey oldObj) {
	    // Get the old rock object and clone it
	    Measurementkey newObj = (Measurementkey) oldTableModel.copyOf(oldObj);
	    // Modify the typeId of the clone to match the new
	    newObj.setTypeid(newTypeId);
	    // Change the data name and universe object back to
	    // original after cloning, since the cloning produces a
	    // different name with a running number suffix
	    final String oldDataName = oldObj.getDataname();
	    if (oldDataName == null) {
	      newObj.setDataname("");
	    } else {
	      newObj.setDataname(oldDataName);
	    }

	    final String oldDataId = oldObj.getDataid();
	    if (oldDataId == null) {
	      newObj.setDataid("");
	    } else {
	      newObj.setDataid(oldDataId);    // ok to duplicate the data id when we duplicate the entire measurement type
	    }

	    final String oldUnivobject = oldObj.getUnivobject();
	    if (oldDataName == null) {
	      newObj.setUnivobject("");
	    } else {
	      newObj.setUnivobject(oldUnivobject);
	    }

	    // Change the col number back to the original, since the
	    // copyOf steps the number.
	    final Long oldColnumber = oldObj.getColnumber();
	    if (oldColnumber == null) {
	      newObj.setColnumber(new Long (0));
	    } else {
	      newObj.setColnumber(oldColnumber);
	    }

	    return newObj;
	  }

	  protected MeasurementcounterExt copyMeasCounter(String newTypeId, TTTableModel oldTableModel, TTTableModel newTableModel,
	      MeasurementcounterExt oldObj) {
	    // Get the old rock object and clone it
	    MeasurementcounterExt newObj = (MeasurementcounterExt) oldTableModel.copyOf(oldObj);
	    // Modify the typeId of the clone to match the new
	    newObj.setTypeid(newTypeId);
	    // Change the data name and universe object back to
	    // original after cloning, since the cloning produces a
	    // different name with a running number suffix
	    final String oldDataName = oldObj.getDataname();
	    if (oldDataName == null) {
	      newObj.setDataname("");
	    } else {
	      newObj.setDataname(oldDataName);
	    }

	    final String oldDataId = oldObj.getDataid();
	    if (oldDataId == null) {
	      newObj.setDataid("");
	    } else {
	      newObj.setDataid(oldDataId);    // ok to duplicate the data id when we duplicate the entire measurement type
	    }

	    final String oldUnivobject = oldObj.getUnivobject();
	    if (oldDataName == null) {
	      newObj.setUnivobject("");
	    } else {
	      newObj.setUnivobject(oldUnivobject);
	    }

	 // Change the col number back to the original, since the
	    // copyOf steps the number.
	    final Long oldColnumber = oldObj.getColnumber();
	    if (oldColnumber == null) {
	      newObj.setColnumber(new Long (0));
	    } else {
	      newObj.setColnumber(oldColnumber);
	    }

	    return newObj;
	  }

}
