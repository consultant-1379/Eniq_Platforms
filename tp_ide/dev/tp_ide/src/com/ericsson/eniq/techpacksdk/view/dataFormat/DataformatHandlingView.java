package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
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

import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;

@SuppressWarnings("serial")
public class DataformatHandlingView extends JPanel {

  private static final Logger logger = Logger.getLogger(DataformatHandlingView.class.getName());

  private SingleFrameApplication application;

  //private MeasurementTypeFactory treeDataFactory;

  private GeneralTechPackTab parentPanel;

  private boolean saveEnabled = true;

  private DataFormatTree dft;

  private JFrame frame;

  
  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  //private DataModelController dataModelController;

  boolean editable = true;

  private class SaveTask extends Task<Void, Void> {

	  //Changing for making View/Edit operation fast
	  private final List<List<Object>> treeState;

	  public SaveTask(List<List<Object>> treeState) {
	      super(application);
	      this.treeState = treeState;
	  }

    @Override
    protected Void doInBackground() throws Exception {
      // Create other panels if not created
      parentPanel.createAllDataModelsIfNotCreated();
      try{
    	  dft.save();
      }catch(Exception e){
    	  ExceptionHandler.instance().handle(e);
          e.printStackTrace();
      }      
      return null;
    }
    
    @Override
    protected void succeeded(Void ignored) {
    	logger.log(Level.INFO, "DataFormats successfully saved");
        //After successful save, Load the tree expansion state of measurement types 
        TreeState.loadExpansionState(dft, treeState);
        setSaveEnabled(false);       
        getParentAction("enableTabs").actionPerformed(null);
        //Fire an event telling any listeners that we have changed something here!!
        application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
    }
    
  }
  
  /**
   * Save action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> save() {
	  logger.info("Saving DataformatHandlingView");
	  // Save the current tree expansion state before save operation is triggered.this state is loaded after save 
	  final List<List<Object>> list = TreeState.saveExpansionState(dft);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
	  
	  final Task<Void, Void> saveTask = new SaveTask(list);

	  BusyIndicator busyIndicator = new BusyIndicator();
	  frame.setGlassPane(busyIndicator);
	  saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));
	  return saveTask;
  }
  
  //Collapse all the nodes before save operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {
	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) dft.getModel().getRoot();
	  if(root != null){
			Enumeration<TreePath> e= dft.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
			    		dft.collapsePath(current);
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
	      try {
	    	  dft.discard();
	        } catch (Exception e) {
	          ExceptionHandler.instance().handle(e);
	          e.printStackTrace();
	        }
	        return null;
	    }
	    
	    @Override
	    protected void finished() {
	      logger.log(Level.INFO, "DataFormats changes successfully discarded");
	      TreeState.loadExpansionState(dft, treeState);
	      setSaveEnabled(false);    
	      getParentAction("enableTabs").actionPerformed(null);
	    }
  }
  
  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> discard() {
	  logger.info("Discarding changes in DataformatHandlingView");
	  final List<List<Object>> list = TreeState.saveExpansionState(dft);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
	  
	  final Task<Void, Void> DiscardTask = new DiscardTask(list);
    
	  BusyIndicator busyIndicator = new BusyIndicator();
	  frame.setGlassPane(busyIndicator);
	  DiscardTask.setInputBlocker(new BusyIndicatorInputBlocker(DiscardTask, busyIndicator));
	  return DiscardTask;
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

  public void finalize () throws Throwable {
    super.finalize();
  }
  
  public DataformatHandlingView(SingleFrameApplication application, DataModelController dataModelController,
      String setName, String setVersion, String versionid, String setType, int type, boolean editable,
      GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.application = application;
    //this.dataModelController = dataModelController;
    this.editable = editable;
    this.parentPanel = parentPanel;
    this.frame = frame;

    this.addComponentListener(new MyCompListener());
    logger.finest("DataformatHandlingView create table tree started");
    dft = new DataFormatTree(frame, application, parentPanel, versionid, dataModelController, this.editable, new ETLSetTableTreeListener(), this);
    JScrollPane scrollPane = new JScrollPane(dft);
    logger.finest("DataformatHandlingView create table tree finished");
    // ************** buttons **********************

    dft.setName("DataFormatTree");
    dft.setErrorMessageComponent(new ErrorMessageComponent(application));
    dft.getErrorMessageComponent().setValue(new Vector<String>());
    
    JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setName("DataFormatCancel");
    cancel.setEnabled(editable);

    JButton save = new JButton("Save");
    save.setAction(getAction("save"));
    save.setName("DataFormatSave");
    save.setEnabled(editable);

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setName("DataFormatClose");
    closeDialog.setEnabled(true);

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(dft.getErrorMessageComponent());
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

  }

  private class ETLSetTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {
      setSaveEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);
    }

    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

    public void removeUpdate(final DocumentEvent e) {
      // nothing to do
    }
  }

  private class MyCompListener implements ComponentListener {

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {

    }

    public void componentResized(ComponentEvent e) {

    }

    public void componentShown(ComponentEvent e) {
      dft.update();
    }

  }

}
