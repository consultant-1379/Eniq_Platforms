package com.ericsson.eniq.techpacksdk.view.generaltechpack;

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
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.transformer.TransformerTree;

@SuppressWarnings("serial")
public class ManageTransformerView extends JPanel {
	
  private static final Logger logger = Logger.getLogger(ManageTransformerView.class.getName());
  
	private final SingleFrameApplication application;
	
	//private final TransformerFactory treeDataFactory;
    
    private final TransformerTree tft;
    
    // private final TableTreeComponent transformerTableTree;

  private final DataModelController dataModelController;
	
  private final GeneralTechPackTab parentPanel;
  
  private final Versioning versioning;
  
  private final boolean editable;
  
  private ErrorMessageComponent errorMessageComponent;

  Vector<String> tableTreeErrors;

  private boolean saveEnabled = true;
  private boolean cancelEnabled = true;
  
  private JFrame fFrame;

  private class TransformerTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {
      setSaveEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);;
    }

    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

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
    	try{
    		tft.save();
    		((GeneralTechPackTab) parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
    	}catch(Exception e){
    		ExceptionHandler.instance().handle(e);
            e.printStackTrace();
    	}
      return null;
    }
    
    @Override
    protected void succeeded(Void ignored) {
      logger.log(Level.INFO, "Transformers successfully saved");
      //After successful save, Load the tree expansion state of measurement types 
      TreeState.loadExpansionState(tft, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);
      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
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
	        refresh();
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	      return null;
	    }
	    
	    @Override
	    protected void finished() {
	      logger.log(Level.INFO, "Transformers changes successfully discarded");
	      TreeState.loadExpansionState(tft, treeState);
	      setSaveEnabled(false);
	      setCancelEnabled(false);
	      setScreenMessage(null);
	      getParentAction("enableTabs").actionPerformed(null);
	    }	    
  } // End of DiscardTask

  /**
   * Save action
   * 
   * @return
   */

  @Action(enabledProperty = "saveEnabled")
  public Task save() {
	  logger.info("Saving ManageTransformerView");
	  final List<List<Object>> list = TreeState.saveExpansionState(tft);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
	  final Task<Void, Void> saveTask = new SaveTask(list);

	  BusyIndicator busyIndicator = new BusyIndicator();
	  fFrame.setGlassPane(busyIndicator);
	  saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));
	  return saveTask;
  }
  
  //Collapse all the nodes before save operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {
	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) tft.getModel().getRoot();
	  if(root != null){
		  Enumeration<TreePath> e= tft.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
			    		tft.collapsePath(current);
			    	}
		    	}
		    }
	  }
  }


  @Action(enabledProperty = "cancelEnabled")
  public Task<Void,Void> discard() {
	  logger.info("Discarding changes in ManageTransformerView");
	  final List<List<Object>> list = TreeState.saveExpansionState(tft);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();	
	  }
	  final Task<Void, Void> discardTask = new DiscardTask(list);

	  final BusyIndicator busyIndicator = new BusyIndicator();
	  fFrame.setGlassPane(busyIndicator);
	  discardTask.setInputBlocker(new BusyIndicatorInputBlocker(discardTask, busyIndicator));
	  return discardTask;

  }
	
	/**
	 * Helper function, returns action by name
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
   * @param actionName
   * @return
   */
  private javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }
  
  private void refresh() {
	   	setSaveEnabled(false);
	   	setCancelEnabled(false);
	    getParentAction("enableTabs").actionPerformed(null);;
	    tft.discard();
	    setScreenMessage(null);	    
	  }

	public ManageTransformerView(final SingleFrameApplication application,DataModelController dataModelController, Versioning versioning, GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());
    this.fFrame = frame;
    this.application = application;
    this.dataModelController = dataModelController;
    this.versioning = versioning;
    this.parentPanel = parentPanel;
    this.editable = parentPanel.editable;
	    
    this.addComponentListener(new MyCompListener());
    
    /*
		this.treeDataFactory = new TransformerFactory(this.dataModelController.getTransformerDataModel(), editable);

		this.treeDataFactory.setVersioning(this.versioning);
    
		this.transformerTableTree = new TableTreeComponent(this.treeDataFactory);
		this.transformerTableTree.addDocumentListener(new TransformerTableTreeListener());
		
		final JScrollPane scrollPane = new JScrollPane(this.transformerTableTree);
*/
    logger.finest("ManageTransformerView create table tree started");
    tft = new TransformerTree(application, versioning, dataModelController, this.editable, new ETLSetTableTreeListener(),this);
    tft.setName("ManageTransformerViewTree");
    logger.finest("ManageTransformerView create table tree finished");
    final JScrollPane scrollPane = new JScrollPane(tft);

    
    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());
    
    final JButton cancel = new JButton(getAction("discard"));
    cancel.setName("ManageTransformerViewCancel");
    cancel.setEnabled(editable);

    final JButton save = new JButton(getAction("save"));
    save.setName("ManageTransformerViewSave");
    save.setEnabled(editable);

    final JButton close = new JButton(getParentAction("closeDialog"));
    close.setName("ManageTransformerViewClose");
    close.setEnabled(true);
    
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

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
	  if(!this.editable){
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
		if(!this.editable){
			  return;
		  }
		final boolean oldvalue = this.cancelEnabled;
		this.cancelEnabled = cancelEnabled;
		firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
	}
  
  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class ETLSetTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {
    	setScreenMessage(null);
        tableTreeErrors = tft.validateData();
        if (tableTreeErrors.size() > 0) {
          setScreenMessage(tableTreeErrors);
        }
        setSaveEnabled(tableTreeErrors.size() == 0);
        setCancelEnabled(true);
        getParentAction("disableTabs").actionPerformed(null);;
    }

    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

    public void removeUpdate(final DocumentEvent e) {
      // nothing to do
    }
  }
    
  private class MyCompListener implements ComponentListener{

    public void componentHidden(ComponentEvent e) {
      // TODO Auto-generated method stub
      
    }

    public void componentMoved(ComponentEvent e) {
      // TODO Auto-generated method stub
      
    }

    public void componentResized(ComponentEvent e) {
      // TODO Auto-generated method stub
      
    }

    public void componentShown(ComponentEvent e) {
      // TODO Auto-generated method stub
      tft.update();
    }
    
  }

  
}
