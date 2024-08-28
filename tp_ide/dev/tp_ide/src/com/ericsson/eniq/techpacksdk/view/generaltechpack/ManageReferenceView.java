package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.reference.ReferenceTypeColumnTableModel;
import com.ericsson.eniq.techpacksdk.view.reference.ReferenceTypeFactory;
import com.ericsson.eniq.techpacksdk.view.reference.ReferenceTypeParameterModel;

@SuppressWarnings("serial")
public class ManageReferenceView extends JPanel {

  private static final Logger logger = Logger.getLogger(ManageReferenceView.class.getName());

  private final SingleFrameApplication application;

  private final ReferenceTypeFactory treeDataFactory;

  private final TableTreeComponent referenceTypeTableTree;

  private final DataModelController dataModelController;

  private final GeneralTechPackTab parentPanel;

  private final Versioning versioning;

  private final Versioning baseversioning;

  private final boolean editable;

  private boolean saveEnabled = true;

  private boolean cancelEnabled = true;

  private final JFrame frame;

  Vector<String> tableTreeErrors;

  private final ErrorMessageComponent errorMessageComponent;

  private class ReferenceTableTreeListener implements DocumentListener {

	  @Override
    public void changedUpdate(final DocumentEvent e) {
		  logger.info("changedUpdate");
      setScreenMessage(null);
      tableTreeErrors = referenceTypeTableTree.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
      }
      setSaveEnabled(tableTreeErrors.size() == 0);
      setCancelEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);
    }

	  @Override
    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

	  @Override
    public void removeUpdate(final DocumentEvent e) {
      // nothing to do
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
      try {

        // final Vector<Referencetable> referencedtables = new
        // Vector<Referencetable>();
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);

        // Before saving the data to the DB, a series of validation checks
        // are executed. These checks cannot be executed when the data is
        // entered, so they are checked before saving, as here all data
        // models are available.

        // Get all measurement type ranking tables and collect the names of the
        // generated reference types to a list. For example: a ranking table
        // "DC_E_MGW_AAL2APBH"
        // will be stored as "DIM_E_MGW_AAL2APBH_BHTYPE".
        Vector<String> rankList = new Vector<String>();
        Measurementtype mt = new Measurementtype(dataModelController.getRockFactory());
        mt.setVersionid(versioning.getVersionid());
        MeasurementtypeFactory mtF = new MeasurementtypeFactory(dataModelController.getRockFactory(), mt);
        Iterator mtIter = mtF.get().iterator();
        while (mtIter.hasNext()) {
          Measurementtype tmpMT = (Measurementtype) mtIter.next();
          if (tmpMT.getRankingtable().intValue() == 1) {
            rankList.add("DIM_" + tmpMT.getObjectname().replace("DC_", "") + "_BHTYPE");
          }
        }

        // Get all the data from the reference table tree. Loop through all the
        // reference types and execute validation.
        final Vector<Vector<Object>> allData = referenceTypeTableTree.getAllData();
        ReferenceTypeParameterModel parameterData = null;
        ReferenceTypeColumnTableModel   tableData = null;
        for (int i = 0; i < allData.size(); i++) {
          Vector<Object> refTypeData = allData.elementAt(i);

          // Get the parameter model.
          for (int j = 0; j < refTypeData.size(); j++) {
            if (refTypeData.elementAt(j) instanceof ReferenceTypeParameterModel) {
              parameterData = (ReferenceTypeParameterModel) refTypeData.elementAt(j);
            }
            if (refTypeData.elementAt(j) instanceof ReferenceTypeColumnTableModel) {
            	tableData = (ReferenceTypeColumnTableModel) refTypeData.elementAt(j);
              }
          } // for (int j = 0; j < refTypeData.size(); j++)
          
          if(parameterData != null) {
	          // Validate: Before letting the save proceed, it is first checked if
	          // there are reference types defined which would collide with the ones
	          // generated from the base techpack. AGGLEVEL reference type is
	          // checked on the fly in the model, but the BHTYPE reference types for
	          // each ranking table measurement type are checked here.
	          String currentRankTableName = (parameterData).getReferencetable()
	              .getObjectname();
	          for (int k = 0; k < rankList.size(); k++) {
	            if (currentRankTableName.equalsIgnoreCase(rankList.elementAt(k))) {
	              // Match found. Save not possible.
	              String errMessage = "Duplicate reference type name: " + currentRankTableName
	                  + " is already defined in the base techpack. " + "Save is not possible.";
	              throw new Exception(errMessage);
	
	            }
	          } // for (int k = 0; k < rankList.size(); k++)
          } // if(parameterData != null)
          if(tableData != null) {
        	  // eeoidiv 20100407
        	  // HL26166  IDE not ensuring database integrity when changes are made to reference types of TP 
        	  // Check colnumber is unique within table. If colnumber not unique, re-sort
        	  if(!tableData.isOrderOk()) { // TODO: isDuplicate
        		  tableData.reorder();  
        	  } 
          } //if(tableData != null)
          
        } //for (int i = 0; i < allData.size(); i++)

        // Remove the generated reference type data from the database for this
        // techpack.
        dataModelController.getReferenceTypeDataModel().deleteGenerated();

        // final Vector<Vector<Object>> deletedtables =
        // referenceTypeTableTree.getDeletedData();
        // for (final Iterator<Vector<Object>> tablesiter =
        // deletedtables.iterator(); tablesiter.hasNext();) {
        // final Vector<Object> models = tablesiter.next();
        // for (final Iterator<Object> modelIter = models.iterator();
        // modelIter.hasNext();) {
        // final Object model = modelIter.next();
        // if (model instanceof ReferenceTypeParameterModel) {
        // final ReferenceTypeParameterModel referenceTypeParameterModel =
        // (ReferenceTypeParameterModel) model;
        // final Referencetable referencetable =
        // referenceTypeParameterModel.getReferencetable();
        // dataModelController.getReferenceTypeDataModel().deleteGenerated(referencetable);
        // logger.fine(referencetable.getTypename() + " generated deleted");
        // }
        // }
        // }
        //        
        // final Vector<Vector<Object>> insertedtables =
        // referenceTypeTableTree.getNewUnsavedData();
        // for (final Iterator<Vector<Object>> tablesiter =
        // insertedtables.iterator(); tablesiter.hasNext();) {
        // final Vector<Object> models = tablesiter.next();
        // for (final Iterator<Object> modelIter = models.iterator();
        // modelIter.hasNext();) {
        // final Object model = modelIter.next();
        // if (model instanceof ReferenceTypeParameterModel) {
        // final ReferenceTypeParameterModel referenceTypeParameterModel =
        // (ReferenceTypeParameterModel) model;
        // final Referencetable referencetable =
        // referenceTypeParameterModel.getReferencetable();
        // referencedtables.add(referencetable);
        // }
        // }
        // }

        // Save the changes in the reference type tree.
        referenceTypeTableTree.saveChanges();
        
        // create all the remaining data models, in case if Reference Types are dependent on others
        parentPanel.createAllDataModelsIfNotCreated();

        // Create the generated reference type data
        dataModelController.getReferenceTypeDataModel().createGenerated();

        // for (final Iterator<Referencetable> newiter =
        // referencedtables.iterator(); newiter.hasNext();) {
        // final Referencetable referencetable = newiter.next();
        // dataModelController.getReferenceTypeDataModel().createGenerated(referencetable);
        // logger.fine(referencetable.getTypename() + " generated created");
        // }

        dataModelController.rockObjectsModified(dataModelController.getReferenceTypeDataModel());
        
        referenceTypeTableTree.setModelForTree();
        
        (parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
        
        // Commit the changes to the database.
        dataModelController.getRockFactory().getConnection().commit();
        
      } catch (Exception e) {
        // ERROR in save operation. Make a roll back and refresh the
        // contents of the TableTreeComponent.
        try {
          dataModelController.getRockFactory().getConnection().rollback();
          referenceTypeTableTree.setModelForTree();
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
      logger.log(Level.INFO, "References successfully saved");
      TreeState.loadExpansionState(referenceTypeTableTree, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);
      
      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, "References unsuccessfully saved", e);
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
        referenceTypeTableTree.discardChanges();
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(referenceTypeTableTree, treeState);
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
	  logger.info("saving ManageReferenceView");
    final List<List<Object>> list = TreeState.saveExpansionState(referenceTypeTableTree);
    if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
    final Task<Void, Void> saveTask = new SaveTask(list);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));

    return saveTask;
  }
  
  //Collapse all the nodes before save operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {
	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) referenceTypeTableTree.getModel().getRoot();
	  if(root != null){
		  Enumeration<TreePath> e= referenceTypeTableTree.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
			    		referenceTypeTableTree.collapsePath(current);
			    	}
		    	}
		     }
	  }  
  }


  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "cancelEnabled")
  public Task<Void, Void> discard() {
	  logger.info("Discarding changes in ManageReferenceView");
    final List<List<Object>> list = TreeState.saveExpansionState(referenceTypeTableTree);
    if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
    final Task<Void, Void> discardTask = new DiscardTask(list);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    discardTask.setInputBlocker(new BusyIndicatorInputBlocker(discardTask, busyIndicator));

    return discardTask;

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

  public ManageReferenceView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.application = application;
    this.dataModelController = dataModelController;
    this.versioning = versioning;
    this.baseversioning = dataModelController.getTechPackTreeDataModel().getVersionByVersionId(
        this.versioning.getBasedefinition());

    this.parentPanel = parentPanel;
    this.editable = parentPanel.editable;
    this.frame = frame;

    this.treeDataFactory = new ReferenceTypeFactory(application, dataModelController.getReferenceTypeDataModel(),
        editable, versioning, baseversioning);

    this.referenceTypeTableTree = new TableTreeComponent(this.treeDataFactory);
    this.referenceTypeTableTree.addDocumentListener(new ReferenceTableTreeListener());
    this.referenceTypeTableTree.setName("ReferenceTypeTree");

    final JScrollPane scrollPane = new JScrollPane(this.referenceTypeTableTree);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    final JButton cancel = new JButton(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("ReferenceViewCancel");

    final JButton save = new JButton(getAction("save"));
    save.setEnabled(editable);
    save.setName("ReferenceViewSave");

    final JButton close = new JButton(getParentAction("closeDialog"));
    close.setEnabled(true);
    close.setName("ReferenceViewClose");

    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(close);

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

  }

  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

}
