package com.ericsson.eniq.techpacksdk.view.generalInterface;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLSetHandlingView;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;

public class GeneralInterfaceTab extends JPanel {

  String types[] = { "measurement", "reference" };

  private static final Logger logger = Logger.getLogger(GeneralInterfaceTab.class.getName());

  JFrame frame;

  private SingleFrameApplication application;

  boolean editable = true;

  DataModelController dataModelController;

  JTable tpT;

  JTable tppedencyT;

  DataTreeNode dataTreeNode;
  
  private JTabbedPane tabbedPanel;
  //eqev-4605 STARTS
  public static boolean editOpened;
  //EQEV-4605 ENDS
  
  
  public GeneralInterfaceTab(SingleFrameApplication application, DataModelController dataModelController, JFrame frame,
      boolean editable, DataTreeNode dataTreeNode) {
    super(new GridBagLayout());

    this.frame = frame;
    this.editable = editable;
    this.application = application;
    this.dataModelController = dataModelController;
    this.dataTreeNode = dataTreeNode; 
    
    tabbedPanel = new JTabbedPane();

    tabbedPanel.setTabPlacement(JTabbedPane.TOP);
    
    tabbedPanel.addTab("General", null, new GeneralInterfaceView(application,dataModelController,dataTreeNode,this.editable,this,frame));
    tabbedPanel.addTab("Sets/Actions/Schedulings", null, new ETLSetHandlingView(application,dataModelController,((Datainterface)dataTreeNode.getRockDBObject()).getInterfacename(),((Datainterface)dataTreeNode.getRockDBObject()).getInterfaceversion(),((Datainterface)dataTreeNode.getRockDBObject()).getInterfaceversion(),((Datainterface)dataTreeNode.getRockDBObject()).getInterfacetype(), ETLSetHandlingView.INTERFACE,this.editable, this, frame));
    
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(tabbedPanel, gbc);
  }
  
  /**
   * close dialog action
   * 
   * @return
   */
  @Action
  public void closeDialog() {
   //EQEV-4605 starts
	  String frameName = this.frame.getName();
	  if (frameName.equalsIgnoreCase("EditTechPackWindow")){
		  this.setEditIsOpened(false);
		  }
		  //EQEV-4605 ends
    this.frame.dispose();
  }
  
  /**
   * disableTabs
   * 
   * @return
   */
  @Action
  public void disableTabs() {
    for (int i = 0 ; i < tabbedPanel.getComponentCount() ; i++){
      tabbedPanel.setEnabledAt(i, false);
    }   
  }
  
  /**
   * enableTabs
   * 
   * @return
   */
  @Action
  public void enableTabs() {
    for (int i = 0 ; i < tabbedPanel.getComponentCount() ; i++){
      tabbedPanel.setEnabledAt(i, true);
    }
  }
  //EQEV-4605 starts
  public static void  setEditIsOpened(boolean edit)
  {
	   editOpened = edit;
  }

  public static boolean  getEditIsOpened()
  {
      return editOpened;
  }
  
  //EQEV-4605 ends
}
