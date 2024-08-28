package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
//import java.beans.PropertyChangeListener;
//import java.sql.SQLException;
import java.util.Enumeration;
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
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

//import ssc.rockfactory.RockException;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
//import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourTree;

@SuppressWarnings("serial")
public class ManageBusyHourView extends JPanel {

  public final static int TECHPACK = 1;

  public final static int INTERFACE = 2;

  public final static int MAINTENANCE = 3;

  private static final Logger logger = Logger.getLogger(ManageBusyHourView.class.getName());

  private SingleFrameApplication application;

  private BusyhourTree bht;

  private Object parentPanel;
  
  //private Versioning versioning;

  private boolean saveEnabled = true;

  private JFrame fFrame;

  private DataModelController dataModelController;

  boolean editable = true;

  private ErrorMessageComponent errorMessageComponent;

  List<String> tableTreeErrors;

  private boolean cancelEnabled = true;

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

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  private void setScreenMessage(final List<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class SaveTask extends Task<Void, Void> {
	  
	  private final List<List<Object>> treeState;
	  public SaveTask(List<List<Object>> treeState) {
	      super(application);
	      this.treeState = treeState;
	  }

    @Override
    protected Void doInBackground() throws Exception {
      logger.log(Level.INFO, " Saving ManageBusyHourView");
      try{
    	  bht.save();
    	  ((GeneralTechPackTab) parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_BUSYHOUR);
    	  dataModelController.getBusyhourHandlingDataModel().refresh();
      }catch(Exception e){
    	  ExceptionHandler.instance().handle(e);
          e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void succeeded(final Void ignored) {
      TreeState.loadExpansionState(bht, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      getParentAction("enableTabs").actionPerformed(null);
      
      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
      
      logger.log(Level.INFO, "ManageBusyHourView saved successfully.");
    }

    @Override
    protected void failed(final Throwable e) {
      logger.log(Level.SEVERE, "ManageBusyHourView unsuccessfully saved", e);
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

  }

  /**
   * Save action
   * 
   * @return
   */

  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> save() {
	  logger.info("Saving ManageBusyHourView");
	  final List<List<Object>> list = TreeState.saveExpansionState(bht);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
	  final Task<Void, Void> saveTask = new SaveTask(list);
	  
	  final BusyIndicator busyIndicator = new BusyIndicator();
	  fFrame.setGlassPane(busyIndicator);
	  saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));
	  return saveTask;
  }
  
  //Collapse all the nodes before save operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {	
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) bht.getModel().getRoot();
		if(root != null){
			Enumeration<TreePath> e= bht.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
			    		bht.collapsePath(current);
			    	}
		    	}
		     }
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
      logger.log(Level.INFO, "discard");
      
      try {
    	  bht.discard();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }      
      return null;
    }
    
    @Override
    protected void finished() {
      logger.log(Level.INFO, "BusyHour changes successfully discarded");
      TreeState.loadExpansionState(bht, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);
    }
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "cancelEnabled")
  public Task<Void, Void> discard() {
	  logger.info("Discarding changes to ManageBusyHourView");
	  final List<List<Object>> list = TreeState.saveExpansionState(bht);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
	  final Task<Void, Void> DiscardTask = new DiscardTask(list);

	  final BusyIndicator busyIndicator = new BusyIndicator();
	  fFrame.setGlassPane(busyIndicator);
	  DiscardTask.setInputBlocker(new BusyIndicatorInputBlocker(DiscardTask, busyIndicator));
	  return DiscardTask;
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

  protected void finalize() throws Throwable {
    super.finalize();
  }

  //This is used for testing.
  public ManageBusyHourView(final Versioning versioning, final DataModelController dataModelController){
    //this.versioning = versioning;
    this.dataModelController = dataModelController;
  }
  
  public ManageBusyHourView(final SingleFrameApplication application, final DataModelController dataModelController,
      final Versioning versioning, final boolean editable, final Object parentPanel, final JFrame frame) {
    super(new GridBagLayout());

    this.fFrame = frame;
    this.application = application;
    //this.versioning = versioning;
    
    this.dataModelController = dataModelController;
    this.editable = editable;
    this.parentPanel = parentPanel;
    this.addComponentListener(new MyCompListener());
		// leave the tree as editable for add/remove target techpacks (sub tree editing is disabled always)
    bht = new BusyhourTree(frame, application, (GeneralTechPackTab) parentPanel, versioning, dataModelController,
        this.editable, new BusyhourTableTreeListener());
    bht.setName("BusyHourTree");
    final JScrollPane scrollPane = new JScrollPane(bht);
 
      
    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    final JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("BusyHourViewCancel");

    final JButton save = new JButton("Save");
    save.setAction(getAction("save"));
    save.setEnabled(editable);
    save.setName("BusyHourViewSave");
    
    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("BusyHourViewClose");

    
    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    // buttonPanel.add(wizard);
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

  private class MyCompListener implements ComponentListener {

    public void componentHidden(final ComponentEvent e) {
    }

    public void componentMoved(final ComponentEvent e) {
    }

    public void componentResized(final ComponentEvent e) {
    }

    public void componentShown(final ComponentEvent e) {
      // If the data has been marked as updated, then refresh the tree.
      if (dataModelController.getBusyhourHandlingDataModel().dataUpdated) {
        logger.finest("Busy hour data has been modified. Refreshing the tree.");
        final List<List<Object>> treeState = TreeState.saveExpansionState(bht);
        bht.update();
        TreeState.loadExpansionState(bht, treeState);
        dataModelController.getBusyhourHandlingDataModel().dataUpdated = false;
      }
    }
  }

  private class BusyhourTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {
      setScreenMessage(null);
      tableTreeErrors = bht.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
      }
      setSaveEnabled(tableTreeErrors.size() == 0);
      setCancelEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);
    }

    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

    public void removeUpdate(final DocumentEvent e) {
      // nothing to do
    }
  }
}
