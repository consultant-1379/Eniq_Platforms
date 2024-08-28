package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeCounterTableModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeFactory;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeKeyTableModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeParameterModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementcounterExt;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementtypeExt;

@SuppressWarnings("serial")
public class ManageMeasurementView extends JPanel {

  private static final Logger logger = Logger.getLogger(ManageMeasurementView.class.getName());

  private final SingleFrameApplication application;

  private final MeasurementTypeFactory measurementTypeFactory;

  private final TableTreeComponent measurementTypeTableTree;

  private final DataModelController dataModelController;

  private final GeneralTechPackTab parentPanel;

  private final Versioning versioning;

  private final Versioning baseversioning;

  private final boolean editable;

  private boolean saveEnabled = true;

  private boolean cancelEnabled = true;

  private final JFrame frame;

  Vector<String> tableTreeErrors;

  private ErrorMessageComponent errorMessageComponent;

  private class MeasurementTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {

      setScreenMessage(null);
      tableTreeErrors = measurementTypeTableTree.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
      }
      setSaveEnabled(tableTreeErrors.size() == 0);
      setCancelEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);
    }

    public void insertUpdate(final DocumentEvent e) {
      // do nothing
    }

    public void removeUpdate(final DocumentEvent e) {
      // do nothing
    }
  }

  private class SaveTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public SaveTask(List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {

    	// Diabling all tabs
    	parentPanel.disableAllTabs();
    	
      // Before saving the data to the DB, a series of validation checks
      // are executed. These checks cannot be executed when the data is
      // entered, so they are checked before saving, as here all data
      // models are available.

      // Get all data from the table tree component.
      final Vector<Vector<Object>> allData = measurementTypeTableTree.getAllData();

      // For storing the current parameter model and it's key
      Vector<Object> counterTableData = null;
      Vector<Object> keyTableData = null;
      MeasurementTypeParameterModel parameterData = null;

      // Loop through all the data from the table tree component. Get the
      // parameter, counter and key data and execute the validations.
      for (Vector<Object> measTypeData : allData) {

        // Store the parameter, key and counter data objects
        for (Object model : measTypeData) {
          if (model instanceof MeasurementTypeParameterModel) {
            parameterData = (MeasurementTypeParameterModel) model;
          } else if (model instanceof MeasurementTypeCounterTableModel) {
            counterTableData = ((MeasurementTypeCounterTableModel) model).getData();
          } else if (model instanceof MeasurementTypeKeyTableModel) {
            keyTableData = ((MeasurementTypeKeyTableModel) model).getData();
          }
        }

        // Validate: Same data names are not allowed to be defined in both key
        // and counter tables.
        for (Object currentCounter : counterTableData) {
          for (Object key : keyTableData) {
            if (((MeasurementcounterExt) currentCounter).getDataname().equals(((Measurementkey) key).getDataname())) {
              String errMessage = "Duplicate dataname: " + ((MeasurementcounterExt) currentCounter).getDataname()
                  + " in measurement counter and key tables for measurement type: " + parameterData.getMainNodeName()
                  + ". Save is not possible.";
              throw new Exception(errMessage);
            }
          }

        }

        // Validate: Check if the table type is "Ranking Table", then it is not
        // allowed to have any counters defined.
        if (Utils.replaceNull(parameterData.getMeasurementtypeExt().getRankingtable()).intValue() == 1) {
          if (counterTableData != null && counterTableData.size() > 0) {
            String errMessage = parameterData.getMainNodeName()
                + ": Ranking table cannot have any measurement counters defined. Save is not possible.";
            throw new Exception(errMessage);
          }
        }

        // Validate: If the current measurement type is a "Ranking Table" and
        // has "Element BH Support" enabled, the it cannot have
        // "Object BH Support" enabled.
        if ((Utils.replaceNull(parameterData.getMeasurementtypeExt().getRankingtable()).intValue() == 1)
            && (Utils.replaceNull(parameterData.getMeasurementtypeExt().getElementbhsupport()).intValue() == 1)) {
          Vector<Object> myObjBHSups = parameterData.getMeasurementtypeExt().getObjBHSupport();
          if (myObjBHSups != null && myObjBHSups.size() > 0) {
            String errMessage = parameterData.getMainNodeName()
                + ": Ranking table with Element BH Support selected cannot have Object BH Support defined. Save is not possible.";
            throw new Exception(errMessage);

          }
        }

        // Validate: If the current measurement type has "Element BH Support"
        // selected and it is not a Ranking Table, then in the same techpack
        // there must exist a Ranking Table which also has "Element BH Support"
        // selected.
        if ((Utils.replaceNull(parameterData.getMeasurementtypeExt().getElementbhsupport()).intValue() == 1)
            && (Utils.replaceNull(parameterData.getMeasurementtypeExt().getRankingtable()).intValue() == 0)) {

          // Iterate through all data and search for a Ranking Table with
          // Element BH Support.
          boolean found = false;
          for (Vector<Object> currentMeasType : measurementTypeTableTree.getAllData()) {
            for (Object model : currentMeasType) {
              if (model instanceof MeasurementTypeParameterModel) {
                MeasurementTypeParameterModel currentParam = (MeasurementTypeParameterModel) model;

                // Check for match
                if (Utils.replaceNull(currentParam.getMeasurementtypeExt().getRankingtable()).intValue() == 1
                    && Utils.replaceNull(currentParam.getMeasurementtypeExt().getElementbhsupport()).intValue() == 1) {
                  found = true;
                  break;
                }
              }
            }
            if (found)
              break;
          }

          // Check if the match was not found.
          if (!found) {
            String errMessage = parameterData.getMainNodeName()
                + ": Element BH Support is defined, but no measurement type with Ranking Table and Element BH Support defined in the techpack. Save is not possible.";
            throw new Exception(errMessage);
          }
        }

        // Check if the table type is "Ranking Table" and it has been
        // updated then we also mark the father measurement type to be
        // updated...
        // if
        // (Utils.replaceNull(parameterData.getMeasurementtypeExt().getRankingtable()).intValue()
        // == 1
        // && parameterData.getMeasurementtypeExt().isUpdated()) {
        if (parameterData.getMeasurementtypeExt().isUpdated()) {

          // Get all the ObjBBSupports for this measurement type
          Vector<String> mymeasobjsups = new Vector<String>();
          for (Object omob : parameterData.getMeasurementtypeExt().getObjBHSupport()) {
            mymeasobjsups.add(((Measurementobjbhsupport) omob).getObjbhsupport());
          }

          // Retrieve all measobjbhsupports that have the same objbhsupports as
          // the our own
          for (Vector<Object> measTypeData2 : measurementTypeTableTree.getAllData()) {
            for (Object model : measTypeData2) {
              if (model instanceof MeasurementTypeParameterModel) {
                MeasurementTypeParameterModel paramModel = (MeasurementTypeParameterModel) model;
                for (Object omob : ((MeasurementTypeParameterModel) model).getMeasurementtypeExt().getObjBHSupport()) {
                  if (mymeasobjsups.contains(((Measurementobjbhsupport) omob).getObjbhsupport())) {
                    // Touch the data so it will be regenerated
                    paramModel.getMeasurementtypeExt().setDescription(
                        paramModel.getMeasurementtypeExt().getDescription());
                  }
                }
              }
            }
          }
        }
      }

      // Remove generated data for deleted and updated measurement types.
      // Save the changes in the TableTreeComponent. Regenerate the
      // generated data for updated measurement types. Notify the
      // depending model of the change. Commit the changes to the
      // database. Refresh the TableTreeComponent from the database.
      try {

        // Create vectors for both all and modified measurement types
        Vector<MeasurementtypeExt> modifiedtypes = new Vector<MeasurementtypeExt>();
        final Vector<MeasurementtypeExt> alltypes = new Vector<MeasurementtypeExt>();

        // Disable auto commit.
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);

        // Get all deleted data from the TableTreeComponent. Iterate
        // through the data and remove the generated data from the
        // database for deleted measurement types.
        //
        // NOTE: The deleted data vector contains only the deleted
        // measurement types, i.e. the deleted keys or counters are not
        // in the list. The deletion of keys and counters will be shown
        // as updated measurement types.
        for (Vector<Object> models : measurementTypeTableTree.getDeletedData()) {
          for (Object model : models) {
            if (model instanceof MeasurementTypeParameterModel) {
              final MeasurementTypeParameterModel measurementTypeParameterModel = (MeasurementTypeParameterModel) model;
              final MeasurementtypeExt measurementtypeExt = measurementTypeParameterModel.getMeasurementtypeExt();
              dataModelController.getMeasurementTypeDataModel().deleteGenerated(true, measurementtypeExt);
              logger.fine(measurementtypeExt.getTypename() + " generated deleted");
            }
          }
        }

        // Get all data from the TableTreeComponent. Iterate through the data
        // and remove the generated data from the database for those measurement
        // types which were updated. Furthermore, lists of both modified and all
        // measurement types are collected.
        for (Vector<Object> models : measurementTypeTableTree.getAllData()) {
          for (Object model : models) {
            if (model instanceof MeasurementTypeParameterModel) {
              final MeasurementTypeParameterModel measurementTypeParameterModel = (MeasurementTypeParameterModel) model;
              final MeasurementtypeExt measurementtypeExt = measurementTypeParameterModel.getMeasurementtypeExt();

              // Add to list of all measurement types
              alltypes.add(measurementtypeExt);

              // Check if updated. If yes, then remove the generated data.
              if (measurementtypeExt.isUpdated()) {
                if (measurementtypeExt.getOriginal() != null) {
                  dataModelController.getMeasurementTypeDataModel().deleteGenerated(false, measurementtypeExt);
                  logger.fine(measurementtypeExt.getTypename() + " generated deleted");
                }

                // Add to the list of modified measurement types.
                modifiedtypes.add(measurementtypeExt);

              }
            }
          }
        }
		
		//validation for Invalid Characters in Measurement Type for EQEV-2864(start)
        for(Object currentMeasurementType:alltypes)
         {
       	  
       	 if (((MeasurementtypeExt)currentMeasurementType).getTypename().contains("%")) 
       	 {
       		 String errMessage = ((MeasurementtypeExt) currentMeasurementType).getTypename()+":  "
                        +"'%' Character is not allowed.";
                       
                    throw new Exception(errMessage);
         }
       	 
       	 if(((MeasurementtypeExt) currentMeasurementType).getTypename().contains("."))
       	 {
       		 String errMessage = ((MeasurementtypeExt) currentMeasurementType).getTypename()+":  "
                        +"'.' Character is not allowed.";
                        
                    throw new Exception(errMessage);
       	 }
       	 
       	 if(((MeasurementtypeExt) currentMeasurementType).getTypename().contains("-"))
       	 {
       		 String errMessage = ((MeasurementtypeExt) currentMeasurementType).getTypename()+":  "
                        +"'-' Character is not allowed.";
                        
                    throw new Exception(errMessage);
       	 }
       	 if(((MeasurementtypeExt) currentMeasurementType).getTypename().contains(" "))
       	 {
       		 String errMessage = ((MeasurementtypeExt) currentMeasurementType).getTypename()+":  "
                        +" Space is not allowed in Measurement Type Name."; 
                        
                    throw new Exception(errMessage);
       	 }
       	 
         }
        //validation for Invalid Characters in Measurement Type EQEV-2864(end)

        // Save changes in the TableTreeComponent
        measurementTypeTableTree.saveChanges();

        // Reorder the modified types vector (before creating the generated
        // data) so that the "ranking table" measurement types are put to the
        // end. This is due to the problem when generating (busy hour)
        // aggregation rules: When the rank table rules are created and the
        // corresponding aggregation might not exist yet. With the reordering,
        // the rank table busy hour rules are always created last.
        Vector<MeasurementtypeExt> tmpModifiedTypes = new Vector<MeasurementtypeExt>();
        Vector<MeasurementtypeExt> tmpModifiedRankTypes = new Vector<MeasurementtypeExt>();
        for (MeasurementtypeExt measurementtypeExt : modifiedtypes) {
          if (Utils.replaceNull(measurementtypeExt.getRankingtable()).intValue() == 0) {
            tmpModifiedTypes.add(measurementtypeExt);
          } else {
            tmpModifiedRankTypes.add(measurementtypeExt);
          }
        }
        tmpModifiedTypes.addAll(tmpModifiedRankTypes);
        modifiedtypes = tmpModifiedTypes;

        // (Re)create the generated data for the modified measurement types.
        for (MeasurementtypeExt measurementtypeExt : modifiedtypes) {
          dataModelController.getMeasurementTypeDataModel().createGenerated(measurementtypeExt, alltypes);
          logger.fine(measurementtypeExt.getTypename() + " generated created");
        }

        // A special action for measurement types, which are Vector Tables. If
        // the special "DCVECTOR_INDEX" measurement key, does not exist, it will
        // be automatically created.
        createDcVectorIndexKey(measurementTypeTableTree.getAllData(), alltypes);

        // Set the model for the TableTreeComponent. This causes the
        // contents of the tree to be refreshed from the database.
        // 
        // The model needs to be refreshed from DB (even before changes are
        // committed) so that when notifying depending models that this model
        // has changed, the model is really up-to-date. The depending model can
        // then use the model received as a parameter to get to the data
        // (instead of reading from the DB).
        measurementTypeTableTree.setModelForTree();
        
        // To create the components which are not yet created
        parentPanel.createAllDataModelsIfNotCreated();

        // Notify all depending data models that the measurement data
        // model has changed.
        dataModelController.rockObjectsModified(dataModelController.getMeasurementTypeDataModel());

        

        ((GeneralTechPackTab) parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
        
        // Commit the changes to the database.
        dataModelController.getRockFactory().getConnection().commit();


      } catch (Exception e) {
        // ERROR in save operation. Make a roll back and refresh the
        // contents of the TableTreeComponent.
        try {
          dataModelController.getRockFactory().getConnection().rollback();
          measurementTypeTableTree.setModelForTree();
        } catch (Exception ex) {
          throw ex;
        }
        throw e;
      }finally {
          try {
            dataModelController.getRockFactory().getConnection().setAutoCommit(true);
          } catch (Exception e) {
            ExceptionHandler.instance().handle(e);
            e.printStackTrace();
          }
        }
      return null;
    }

    @Override
    protected void succeeded(Void ignored) {
      logger.log(Level.INFO, "Measurementtypes successfully saved");
      TreeState.loadExpansionState(measurementTypeTableTree, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);
      
      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, "Measurementtypes unsuccessfully saved", e);
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

  }

  private class DiscardTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public DiscardTask(List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {
        measurementTypeTableTree.discardChanges();
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(measurementTypeTableTree, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);
    }
  }

  /**
   * Save action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> save() {
	  logger.info("Saving ManageMeasurementView");
    final List<List<Object>> list = TreeState.saveExpansionState((JTree) measurementTypeTableTree);
    
    if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
    
    final Task<Void, Void> saveTask = new SaveTask(list);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));

    return saveTask;
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "cancelEnabled")
  public Task<Void, Void> discard() {
	  logger.info("Discarding changes in ManageMeasurementView");
    final List<List<Object>> list = TreeState.saveExpansionState((JTree) measurementTypeTableTree);
    if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
    
    final Task<Void, Void> discardTask = new DiscardTask(list);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    discardTask.setInputBlocker(new BusyIndicatorInputBlocker(discardTask, busyIndicator));

    return discardTask;
  }
  
  //Collapse all the nodes before save/cancel operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {
	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) measurementTypeTableTree.getModel().getRoot();
	  if(root != null){
		  Enumeration<TreePath> e= measurementTypeTableTree.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
			    		measurementTypeTableTree.collapsePath(current);
			    	}
		    	}
		     }
	  }
}


  /**
   * Helper function, returns action by name
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
  }

  /**
   * Helper function, returns action by name from parent panel
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }

  public void finalize() throws Throwable {
    super.finalize();
  }

  public ManageMeasurementView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.addComponentListener(new MyCompListener());

    this.application = application;
    this.dataModelController = dataModelController;
    this.versioning = versioning;
    this.baseversioning = dataModelController.getTechPackTreeDataModel().getVersionByVersionId(
        this.versioning.getBasedefinition());

    this.parentPanel = parentPanel;
    this.editable = parentPanel.isEditable();
    this.frame = frame;

    final String prefix = versioning.getTechpack_name() + Constants.TYPENAMESEPARATOR;
    this.measurementTypeFactory = new MeasurementTypeFactory(this.application, this.dataModelController
        .getMeasurementTypeDataModel(), this.editable, prefix);
    this.measurementTypeFactory.setVersioning(this.versioning);
    this.measurementTypeFactory.setBaseVersioning(this.baseversioning);

    logger.finest("ManageMeasurementView create tree tree started");
    this.measurementTypeTableTree = new TableTreeComponent(this.measurementTypeFactory);
    this.measurementTypeTableTree.addDocumentListener(new MeasurementTableTreeListener());
    final JScrollPane scrollPane = new JScrollPane(this.measurementTypeTableTree);
    logger.finest("ManageMeasurementView create table tree finished");

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    final JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("MeasurementViewCancel");

    final JButton save = new JButton("Save");
    save.setAction(getAction("save"));
    save.setEnabled(editable);
    save.setName("MeasurementViewSave");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("MeasurementViewClose");

    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    final GridBagConstraints c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    this.add(scrollPane, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

    setSaveEnabled(false);
    setCancelEnabled(false);
    setScreenMessage(null);

  }

  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
	  if(!editable){
		  return;
	  }
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(final boolean cancelEnabled) {
	  if(!editable){
		  return;
	  }
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  private class MyCompListener implements ComponentListener {

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
      if (dataModelController.getMeasurementTypeDataModel().newDataCreated) {
        logger.finest("Measurement type data has been modified. Refreshing the tree.");
        final List<List<Object>> treeState = TreeState.saveExpansionState((JTree) measurementTypeTableTree);
        measurementTypeTableTree.setModelForTree();
        TreeState.loadExpansionState(measurementTypeTableTree, treeState);
        dataModelController.getMeasurementTypeDataModel().newDataCreated = false;
      }
    }
  }

  /**
   * Creates a special measurement key "DCVECTOR_INDEX" for Vector Table
   * measurement types, if it does not exist already.
   * 
   * @param tableTreeData
   *          All the data from the Table Tree Component.
   * @param allmeasurementtypes
   *          All measurement types.
   * @throws Exception
   */
  private void createDcVectorIndexKey(Vector<Vector<Object>> tableTreeData,
      Vector<MeasurementtypeExt> allMeasurementTypes) throws Exception {
    // A special action for measurement types, which are Vector Tables. If
    // the special "DCVECTOR_INDEX" measurement key, does not exist, it will
    // be automatically created.
    //
    // Iterate through all the data from the table tree component.
    Vector<Object> keyData = null;
    MeasurementTypeParameterModel paramData = null;
    MeasurementtypeExt measTypeExt = null;

    for (Vector<Object> allModels : tableTreeData) {

      // Store the parameter data and key data objects
      for (Object model : allModels) {
        if (model instanceof MeasurementTypeParameterModel) {
          paramData = (MeasurementTypeParameterModel) model;
          measTypeExt = paramData.getMeasurementtypeExt();
        } else if (model instanceof MeasurementTypeKeyTableModel) {
          keyData = ((MeasurementTypeKeyTableModel) model).getData();
        }
      }

      // Check if the measurement type is a vector table.
      if (Utils.replaceNull(paramData.getMeasurementtypeExt().getVectorsupport()).intValue() == 1) {
        // This is a vector table.

        // Check if the DCVECTOR_INDEX key already exists.
        boolean found = false;
        for (Object key : keyData) {
          if (((Measurementkey) key).getDataname().equals("DCVECTOR_INDEX")) {
            found = true;
            break;
          }
        }

        // Create the key if not found. The generated data for this
        // measurement type also is removed and regenerated.
        if (!found) {

          logger.info("Creating special measurement key 'DCVECTOR_INDEX' for the vector table: "
              + measTypeExt.getTypename() + ".");

          // Remove the generated data for this measurement type.
          dataModelController.getMeasurementTypeDataModel().deleteGenerated(false, measTypeExt);
          logger.fine(measTypeExt.getTypename() + " generated deleted");

          // The key does not exist yet, so create it.
          Measurementkey newKey = new Measurementkey(dataModelController.getRockFactory(), true);
          newKey.setTypeid(paramData.getMeasurementtypeExt().getTypeid());
          newKey.setDataname("DCVECTOR_INDEX");
          newKey.setDescription("Vector Index");
          newKey.setIselement(0);
          newKey.setUniquekey(1);
          newKey.setColnumber(new Long(keyData.size() + 1));
          newKey.setDatatype("integer");
          newKey.setDatasize(0);
          newKey.setDatascale(0);
          newKey.setUniquevalue(255L);
          newKey.setNullable(1);
          newKey.setIndexes("HG");
          newKey.setIncludesql(1);
          newKey.setUnivobject("DCVECTOR_INDEX");
          newKey.setJoinable(0);
          newKey.setRopgrpcell(0);
          newKey.setDataid("DCVECTOR_INDEX");

          // Save the key to the DB.
          newKey.saveDB();
          logger.fine("Saved DCVECTOR_INDEX key for " + measTypeExt.getTypename());

          // Create the generated data for this measurement type.
          dataModelController.getMeasurementTypeDataModel().createGenerated(measTypeExt, allMeasurementTypes);
          logger.fine(measTypeExt.getTypename() + " generated created");
        }
      }
    }
  }

}
