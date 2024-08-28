package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.awt.Component;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.SingleFrameApplication;

import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;

public class DataFormatTree extends JTree {

  private static final Logger logger = Logger.getLogger(DataFormatTree.class.getName());

  private static final long serialVersionUID = 1L;

  private DataformatTreeModelListener listener = null;

  private TagTreeModel ttm;
  
  //boolean letSaveButtEnable = false;

  private DataformatHandlingView dataformatHandlingViewInstance ;
  
  //private DataModelController dataModelController;
  
  private SingleFrameApplication application;
  
  private GeneralTechPackTab parentPanel;

  public DataFormatTree(JFrame frame, SingleFrameApplication application, GeneralTechPackTab parentPanel, String versionid, DataModelController dataModelController, boolean editable, DocumentListener dl, Component c) {

    //this.dataModelController = dataModelController;
    listener = new DataformatTreeModelListener(frame,application,this,c,editable);
    ttm = new TagTreeModel(versionid, dataModelController, editable, this, listener, dl);
    listener.setTtm(ttm);
    this.setModel(ttm);
    this.addTreeExpansionListener(listener);
    this.setEditable(true);
    this.setCellRenderer(new TreeRenderer());
    this.setCellEditor(new TreeEditor(this));
    this.setRootVisible(false);
    this.setShowsRootHandles(true);
    this.application = application;
    this.parentPanel = parentPanel;
    ttm.addDocumentListener(new ETLErrorTableTreeListener());
    dataformatHandlingViewInstance = (DataformatHandlingView)c;
    
    // Remove some of the auto scrolling in the tree
    this.setAutoscrolls(false);

    this.setScrollsOnExpand(false);

    // Double-click should edit, not expand
    this.setToggleClickCount(0);

  }

  public void addDocumentListener(DocumentListener dl){
    ttm.addDocumentListener(dl);
  }
 
  public void update() {
    List<List<Object>> list = TreeState.saveExpansionState(this);
    ttm.update();
    TreeState.loadExpansionState(this, list);
  }
    
  
  public void save() {
    logger.log(Level.INFO, "save");
    List<List<Object>> list = TreeState.saveExpansionState(this);
    ttm.save();
    TreeState.loadExpansionState(this, list);
  }

  public void discard() {
    logger.log(Level.INFO, "discard");
    List<List<Object>> list = TreeState.saveExpansionState(this);
    ttm.discard();
    TreeState.loadExpansionState(this, list);
    ttm.addDocumentListener(new ETLErrorTableTreeListener());
    
    Vector<String> tableTreeErrors ;
    setScreenMessage(null);
    tableTreeErrors = ttm.validateData();
    if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
        setCancelEnabled(true);
        getParentAction("disableTabs").actionPerformed(null);
    }
  }

  public TagTreeModel getTtm() {
    return ttm;
  }

  public void setTtm(TagTreeModel ttm) {
    this.ttm = ttm;
  }
  
  
  
  
  
  
  
  
  private javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }  
  
  
  private ErrorMessageComponent errorMessageComponent;
  
  Vector<String> tableTreeErrors;
  
 
  private boolean wizardEnabled = true;
  
  public boolean isWizardEnabled() {
    return wizardEnabled;
  }

  public void setWizardEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.wizardEnabled;
    this.wizardEnabled = saveEnabled;
    firePropertyChange("wizardEnabled", oldvalue, saveEnabled);
  }
  
  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }
  
  private class ETLErrorTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {
      
      setScreenMessage(null);
      tableTreeErrors = ttm.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
        // Can open these below commented lines#
        // if want to disable save button on error
//        if(letSaveButtEnable){
//        	dataformatHandlingViewInstance.setSaveEnabled(true);
//        }else{
//        	dataformatHandlingViewInstance.setSaveEnabled(false);//To make it hidden when error comes
//        }
//      }else{
//    	  dataformatHandlingViewInstance.setSaveEnabled(true);
//      }
      }
      //setSaveEnabled(tableTreeErrors.size() == 0);
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
  
  private boolean cancelEnabled = true;
  
  public void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public ErrorMessageComponent getErrorMessageComponent() {
    return errorMessageComponent;
  }

  public void setErrorMessageComponent(ErrorMessageComponent errorMessageComponent) {
    this.errorMessageComponent = errorMessageComponent;
  }
  
}
