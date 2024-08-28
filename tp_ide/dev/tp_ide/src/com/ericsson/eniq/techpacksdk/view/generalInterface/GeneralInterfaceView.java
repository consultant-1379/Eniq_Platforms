package com.ericsson.eniq.techpacksdk.view.generalInterface;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLFactory;
import com.ericsson.eniq.techpacksdk.view.generalInterface.GeneralInterfaceDataModel.TPDepedencyTableModel;
import com.ericsson.eniq.techpacksdk.view.generalInterface.GeneralInterfaceDataModel.TPTableModel;



@SuppressWarnings("serial")
public class GeneralInterfaceView extends JPanel {

  private static final Logger logger = Logger.getLogger(GeneralInterfaceView.class.getName());



  String types[] = { "measurement", "reference" };

  private SingleFrameApplication application;

  private DataModelController dataModelController;

  private boolean saveEnabled = false;
  
  private boolean editable = true;

  private boolean tableTPChanged = false;
  private boolean tableTPDChanged = false;
  private GeneralInterfaceTab parentPanel;
  //private JTextField productF;
  private LimitedSizeTextField rStateF;
  private JFrame frame;
  
  private JTable tpT;

  private JTable tppedencyT;

  private DataTreeNode dataTreeNode;

  private LimitedSizeTextArea descriptionA;

  private LimitedSizeTextArea installDescriptionA;
  private String oldDescriptionA;

  private String oldInstallDescriptionA;

  private String oldrStateF;
  
  private ErrorMessageComponent errorMessageComponent;
  
  private class SaveTask extends Task<Void, Void> {

    public SaveTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.info("Save");
         
      dataModelController.getEditInterfaceDataModel().setDescription(descriptionA.getText()); 
      dataModelController.getEditInterfaceDataModel().setInstalldescription(installDescriptionA.getText());
      dataModelController.getEditInterfaceDataModel().setRState(rStateF.getText());
      oldDescriptionA = descriptionA.getText();
      oldInstallDescriptionA = installDescriptionA.getText();
      
      dataModelController.getEditInterfaceDataModel().save();
      
      tableTPChanged = false;
      tableTPDChanged = false;   
      setSaveEnabled(false);
      getParentAction("enableTabs").actionPerformed(null);
      return null;
    }
  }
  
  /**
   * Save action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task save() {
    final Task saveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));

    return saveTask;
  }

  private class DiscardTask extends Task<Void, Void> {

    public DiscardTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.info("Discard");
         
      dataModelController.getEditInterfaceDataModel().refresh();
      
      descriptionA.setText(dataModelController.getEditInterfaceDataModel().getDescription());
      installDescriptionA.setText(dataModelController.getEditInterfaceDataModel().getInstalldescription());
      
      rStateF.setText(dataModelController.getEditInterfaceDataModel().getRState());
      

      
      TPTableModel tm = dataModelController.getEditInterfaceDataModel().getTPTableModel();
      tm.addTableModelListener(new TableTPSelectionListener());
      tpT.setModel(tm);
     
      TPDepedencyTableModel tmd = dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel();
      tmd.addTableModelListener(new TableTPDSelectionListener());
      tppedencyT.setModel(tmd);
          
      tpT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Interfacetechpacks.getTechpacknameColumnSize(), false)));
      
      tpT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Interfacetechpacks.getTechpackversionColumnSize(), false)));
      
      tppedencyT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));
      
      tppedencyT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

      tableTPChanged = false;
      tableTPDChanged = false;  
      
      ((AbstractTableModel)tpT.getModel()).fireTableRowsUpdated(-1, -1);
      ((AbstractTableModel)tppedencyT.getModel()).fireTableRowsUpdated(-1, -1);
      
      setSaveEnabled(false);
              
      try {
        getParentAction("enabledTabs").actionPerformed(null);
      } catch (Exception e){
         // hack
      }   
      
      frame.repaint();    
      return null;
    }
  }
  
  /**
   * DiscardTask action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task discard() {
      
    final Task DiscardTask = new DiscardTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
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
  
  public GeneralInterfaceView(final SingleFrameApplication application, DataModelController dataModelController,
      DataTreeNode dataTreeNode, boolean editable, GeneralInterfaceTab parentPanel,  JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;
    this.application = application;
    this.dataModelController = dataModelController;
    this.editable = editable;
    this.dataTreeNode = dataTreeNode;
    this.parentPanel = parentPanel;
    
    Dimension fieldDim = new Dimension(250, 25);
    Dimension areaDim = new Dimension(250, 150);
    Dimension tableDim = new Dimension(350, 150);

    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // name

    JLabel name = new JLabel("Name");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(name, c);

    JTextField nameF = new JTextField(dataModelController.getEditInterfaceDataModel().getInterfacename());
    nameF.setPreferredSize(fieldDim);
    nameF.setEditable(false);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    txtPanel.add(nameF, c);

    // rState

    JLabel rstate = new JLabel("R-State");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    txtPanel.add(rstate, c);
 
    rStateF = new LimitedSizeTextField(22,Datainterface.getRstateColumnSize(), false);
    rStateF.setText(dataModelController.getEditInterfaceDataModel().getRState().trim());   
    rStateF.setEditable(this.editable);
    rStateF.getDocument().addDocumentListener(new MyDocumentListener());
    rStateF.setName("RState");
   
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    txtPanel.add(rStateF, c);
    
    // product
/*
    JLabel product = new JLabel("Product Number");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    txtPanel.add(product, c);

    productF = new JTextField(dataModelController.getEditInterfaceDataModel().getProductnumber());
    productF.setPreferredSize(fieldDim);
    productF.setEditable(true);
    productF.getDocument().addDocumentListener(new MyDocumentListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    txtPanel.add(productF, c);
*/
    // version

    JLabel version = new JLabel("Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(version, c);

    
    // remove double parenthesis from buildnumber
    String show = dataModelController.getEditInterfaceDataModel().getInterfaceversion();
    show = show.replace("((", "");
    show = show.replace("))", "");
    
    JTextField rstateF = new JTextField(show);
    rstateF.setPreferredSize(fieldDim);
    rstateF.setEditable(false);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    txtPanel.add(rstateF, c);

    // description

    JLabel description = new JLabel("Description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    txtPanel.add(description, c);

    descriptionA = new LimitedSizeTextArea(Datainterface.getDescriptionColumnSize(), false);
    descriptionA.setText(dataModelController.getEditInterfaceDataModel().getDescription().trim());
    oldDescriptionA = dataModelController.getEditInterfaceDataModel().getDescription().trim();
    oldrStateF = dataModelController.getEditInterfaceDataModel().getRState().trim();
    descriptionA.getDocument().addDocumentListener(new MyDocumentListener());
    
    JScrollPane descriptionP = new JScrollPane(descriptionA);
    descriptionP.setPreferredSize(areaDim);
    descriptionA.setEditable(this.editable);
    descriptionA.setName("Description");

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 3;
    c.weightx = 1;
    txtPanel.add(descriptionP, c);

    // / format

    JLabel format = new JLabel("Format");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    txtPanel.add(format, c);
    
    // 20100715, eeoidiv,CR 26/109 18-FCP 103 8147/12:Removing parser dependency from ENIQ platform
    // new parser can be added without changing the platform code
    JComboBox formatCB = new JComboBox(Utils.getParserFormats());
    formatCB.setPreferredSize(fieldDim);
    formatCB.setEditable(false);
    formatCB.setEnabled(false);
    formatCB.setSelectedItem(dataModelController.getEditInterfaceDataModel().getFormat());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 4;
    c.weightx = 1;
    txtPanel.add(formatCB, c);

    // Type
    JLabel type = new JLabel("Type");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    txtPanel.add(type, c);

    JComboBox typeCB = new JComboBox(types);
    typeCB.setPreferredSize(fieldDim);
    typeCB.setEditable(false);
    typeCB.setEnabled(false);
    typeCB.setSelectedItem(dataModelController.getEditInterfaceDataModel().getDataformattype());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 5;
    c.weightx = 1;
    txtPanel.add(typeCB, c);

    // TechPack

    JLabel tp = new JLabel("TechPack");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    txtPanel.add(tp, c);

    tpT = new JTable();
    TPTableModel tm = dataModelController.getEditInterfaceDataModel().getTPTableModel();
    tm.addTableModelListener(new TableTPSelectionListener());
    tpT.setModel(tm);
    tpT.setEnabled(editable);
    tpT.setName("TechPack");
    
    if (editable) {
      tpT.addMouseListener(new TableTPSelectionListener());
      tpT.getTableHeader().addMouseListener(new TableTPSelectionListener());
    }
    
    tpT.setToolTipText("");
    
    tpT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Interfacetechpacks.getTechpacknameColumnSize(), false)));
    
    tpT.getColumnModel().getColumn(1).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Interfacetechpacks.getTechpackversionColumnSize(), false)));
       
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;

    JScrollPane scrollPane = new JScrollPane(tpT);
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // TechPack Depedency

    JLabel tppedency = new JLabel("TechPack Depedency");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 8;
    c.weightx = 0;
    txtPanel.add(tppedency, c);

    tppedencyT = new JTable();
    TPDepedencyTableModel tmd = dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel();
    tmd.addTableModelListener(new TableTPDSelectionListener());
    tppedencyT.setModel(tmd);
    tppedencyT.setEnabled(editable);
    tppedencyT.setName("tpdependency");
    
    if (editable) {
      tppedencyT.addMouseListener(new TableTPDSelectionListener());
      tppedencyT.getTableHeader().addMouseListener(new TableTPDSelectionListener());
    }
    
    tppedencyT.setToolTipText("");
    
    tppedencyT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));
    
    tppedencyT.getColumnModel().getColumn(1).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));
    
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 9;
    c.weightx = 0;

    JScrollPane scrollPane2 = new JScrollPane(tppedencyT);
    scrollPane2.setPreferredSize(tableDim);
    txtPanel.add(scrollPane2, c);

    // Install Description

    JLabel installDescription = new JLabel("Install Description");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 10;
    c.weightx = 0;
    txtPanel.add(installDescription, c);

    installDescriptionA = new LimitedSizeTextArea(Datainterface.getInstalldescriptionColumnSize(), false);
    installDescriptionA.setText(dataModelController.getEditInterfaceDataModel().getInstalldescription().trim());
    oldInstallDescriptionA = dataModelController.getEditInterfaceDataModel().getInstalldescription().trim();
    installDescriptionA.getDocument().addDocumentListener(new MyDocumentListener());

    JScrollPane installDescriptionP = new JScrollPane(installDescriptionA);
    installDescriptionP.setPreferredSize(areaDim);
    installDescriptionA.setEditable(this.editable);
    installDescriptionA.setName("InstallDescription");

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 11;
    c.weightx = 0;
    txtPanel.add(installDescriptionP, c);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());
    
    JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setEnabled(false);

    JButton save = new JButton("Save");
    save.setAction(getAction("save"));
    save.setEnabled(false);
   
    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);
    
    c.gridheight = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    this.add(txtPanelS, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

    setScreenMessage(null);
  }

  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  @Action
  public void addemptytp() {
    logger.fine("ADD " + dataModelController.getEditInterfaceDataModel().getTPTableModel().getRowCount());
    
    Interfacetechpacks itp = new Interfacetechpacks(dataModelController.getRockFactory());
    itp.setTechpackname("");
    itp.setTechpackversion("");
    itp.setInterfacename(((Datainterface)dataTreeNode.getRockDBObject()).getInterfacename());
    itp.setInterfaceversion(((Datainterface)dataTreeNode.getRockDBObject()).getInterfaceversion());
   
    dataModelController.getEditInterfaceDataModel().getTPTableModel().addNewRow(itp);
    dataModelController.getEditInterfaceDataModel().getTPTableModel().fireTableDataChanged();
  }

  @Action
  public void addtp() {
    logger.log(Level.INFO, "addtp");

    String s = (String) JOptionPane.showInputDialog(this, "Select Tech pack", "TechPacks",
        JOptionPane.PLAIN_MESSAGE, null, dataModelController.getEditInterfaceDataModel().getAllTechPacks(), "");

    if ((s != null) && (s.length() > 0)) {

      String split[] = s.split(":");
      Interfacetechpacks itp = new Interfacetechpacks(dataModelController.getRockFactory());
      itp.setTechpackname(split[0]);
      itp.setTechpackversion(split[1]);
      itp.setInterfacename(((Datainterface)dataTreeNode.getRockDBObject()).getInterfacename());
      itp.setInterfaceversion(((Datainterface)dataTreeNode.getRockDBObject()).getInterfaceversion());
      
      dataModelController.getEditInterfaceDataModel().getTPTableModel().addNewRow(itp);
      dataModelController.getEditInterfaceDataModel().getTPTableModel().fireTableDataChanged();
    }

  }

  @Action
  public void removetp() {
    logger.log(Level.INFO, "remove");

    dataModelController.getEditInterfaceDataModel().getTPTableModel().removeRow(tpT.getSelectedRow());
    dataModelController.getEditInterfaceDataModel().getTPTableModel().fireTableDataChanged();

  }

  @Action
  public void addemptytpd() {
    System.out.println("ADD "
        + dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().getRowCount());
    Interfacedependency itp = new Interfacedependency(dataModelController.getRockFactory());
    itp.setTechpackname("");
    itp.setTechpackversion("");  
    itp.setInterfacename(((Datainterface)dataTreeNode.getRockDBObject()).getInterfacename());
    itp.setInterfaceversion(((Datainterface)dataTreeNode.getRockDBObject()).getInterfaceversion());
    dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().addNewRow(itp);
    dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().fireTableDataChanged();
  }

  @Action
  public void addtpd() {
    logger.log(Level.INFO, "addtp");

    String s = (String) JOptionPane.showInputDialog(this, "Select Tech pack", "Customized Dialog",
        JOptionPane.PLAIN_MESSAGE, null, dataModelController.getEditInterfaceDataModel().getAllTechPacks(), "");

    if ((s != null) && (s.length() > 0)) {
      List l = new ArrayList();
      String split[] = s.split(":");      
      Interfacedependency itp = new Interfacedependency(dataModelController.getRockFactory());
      itp.setTechpackname(split[0]);
      itp.setTechpackversion(split[1]);  
      itp.setInterfacename(((Datainterface)dataTreeNode.getRockDBObject()).getInterfacename());
      itp.setInterfaceversion(((Datainterface)dataTreeNode.getRockDBObject()).getInterfaceversion());
      dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().addNewRow(itp);
      dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().fireTableDataChanged();
    }

  }

  @Action
  public void removetpd() {
    logger.log(Level.INFO, "remove");

    dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().removeRow(tppedencyT.getSelectedRow());
    dataModelController.getEditInterfaceDataModel().getTPDepedencyTableModel().fireTableDataChanged();

  }

  private JPopupMenu createTPDPopup(MouseEvent e) {
    JPopupMenu popup;
    JMenuItem mi;
    popup = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (tppedencyT.getSelectedRow() > -1) {
        mi = new JMenuItem("Remove");
        mi.setAction(getAction("removetpd"));
        popup.add(mi);
      }

      mi = new JMenuItem("Add Empty");
      mi.setAction(getAction("addemptytpd"));
      popup.add(mi);

      mi = new JMenuItem("Add Techpack");
      mi.setAction(getAction("addtpd"));
      popup.add(mi);

    } else {

      mi = new JMenuItem("Add Empty");
      mi.setAction(getAction("addemptytpd"));
      popup.add(mi);

      mi = new JMenuItem("Add Techpack");
      mi.setAction(getAction("addtpd"));
      popup.add(mi);

    }

    popup.setOpaque(true);
    popup.setLightWeightPopupEnabled(true);

    return popup;
  }

  private JPopupMenu createTPPopup(MouseEvent e) {
    JPopupMenu popup;
    JMenuItem mi;
    popup = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (tpT.getSelectedRow() > -1) {
        mi = new JMenuItem("Remove");
        mi.setAction(getAction("removetp"));
        popup.add(mi);
      }

      mi = new JMenuItem("Add Empty");
      mi.setAction(getAction("addemptytp"));
      popup.add(mi);

      mi = new JMenuItem("Add Techpack");
      mi.setAction(getAction("addtp"));
      popup.add(mi);

    } else {

      mi = new JMenuItem("Add Empty");
      mi.setAction(getAction("addemptytp"));
      popup.add(mi);

      mi = new JMenuItem("Add Techpack");
      mi.setAction(getAction("addtp"));
      popup.add(mi);

    }

    popup.setOpaque(true);
    popup.setLightWeightPopupEnabled(true);

    return popup;
  }

  private void displayTPMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createTPPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private void displayTPDMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createTPDPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private class TableTPSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      // TODO Auto-generated method stub
    	
    	if (e!=null){
    	    handleButtons();
    		return;
    	} 
    	
      tableTPChanged = true;
      
      tpT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Interfacetechpacks.getTechpacknameColumnSize(), false)));
      
      tpT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Interfacetechpacks.getTechpackversionColumnSize(), false)));
           
      handleButtons();
    }

    public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
        handleButtons();
    }

    public void mouseClicked(MouseEvent e) {
      // TODO Auto-generated method stub
      displayTPMenu(e);
    }

    public void mouseEntered(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {
      // TODO Auto-generated method stub
      displayTPMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      // TODO Auto-generated method stub
      displayTPMenu(e);
    }
  }

  private class TableTPDSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      // TODO Auto-generated method stub
    	
    	if (e!=null){
    	    handleButtons();
    		return;
    	} 
    	
      tableTPDChanged = true;
      
      tppedencyT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));
      
      tppedencyT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

      handleButtons();
    }

    public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
        handleButtons();
    }

    public void mouseClicked(MouseEvent e) {
      // TODO Auto-generated method stub
      displayTPDMenu(e);
    }

    public void mouseEntered(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {
      // TODO Auto-generated method stub
      displayTPDMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      // TODO Auto-generated method stub
      displayTPDMenu(e);
    }
  }

  private JPanel getSetHandlingTab() {

    JPanel panel = new JPanel();

    ETLFactory etlf = dataModelController.getETLFactory();
    etlf.setSetName(((Datainterface) dataTreeNode.getRockDBObject()).getInterfacename());
    etlf.setEditable(this.editable);
    TableTreeComponent myTTC = new TableTreeComponent(etlf);
    JScrollPane scrollPane = new JScrollPane(myTTC);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }
  
  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }
    
  private void handleButtons(){
    
    // RState check
    boolean rstateCorrect = false;
    rstateCorrect = Utils.patternMatch(rStateF.getText(), Constants.RSTATEPATTERN);

    checkRstate();
    
    Vector<String> messages = new Vector<String>();
        
    // check techpack duplicates
    Vector<String> v1 = new Vector<String>();
    boolean tpduplicate = false;
    for (int i = 0 ; i < tpT.getModel().getRowCount() ; i++){
      if (v1.contains((String)tpT.getModel().getValueAt(i, 0))){
        tpduplicate = true;
        messages.add("Techpack " + (String)tpT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      if (!Utils.patternMatch((String)tpT.getModel().getValueAt(i, 1), Constants.RSTATEPATTERN)){
        tpduplicate = true;
        messages.add("Rstate in Techpack " + (String)tpT.getModel().getValueAt(i, 0) +"/"+ (String)tpT.getModel().getValueAt(i, 1) + " Not correct");    
      }
      v1.add((String)tpT.getModel().getValueAt(i, 0));
    }


    // check techpack depedency duplicates
    Vector<String> v2 = new Vector<String>();
    boolean tpdduplicate = false;
    for (int i = 0 ; i < tppedencyT.getModel().getRowCount() ; i++){
      if (v2.contains((String)tppedencyT.getModel().getValueAt(i, 0))){
        tpdduplicate = true;
        messages.add("Techpack depedency" + (String)tppedencyT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      if (!Utils.patternMatch((String)tppedencyT.getModel().getValueAt(i, 1), Constants.RSTATEPATTERN)){
        tpdduplicate = true;
        messages.add("Rstate in Techpack depedency " + (String)tppedencyT.getModel().getValueAt(i, 0) +"/"+ (String)tppedencyT.getModel().getValueAt(i, 1) + " Not correct");    
      }
      v2.add((String)tppedencyT.getModel().getValueAt(i, 0));
    }
    
    if (!tpduplicate
        && !tpdduplicate
        && rstateCorrect
        && (tableTPChanged || tableTPDChanged || !this.oldDescriptionA.equalsIgnoreCase(this.descriptionA.getText())
            || !this.oldrStateF.equalsIgnoreCase(this.rStateF.getText()) || !this.oldInstallDescriptionA
            .equalsIgnoreCase(this.installDescriptionA.getText()))) {
      getParentAction("disableTabs").actionPerformed(null);
      errorMessageComponent.setValue(null);
      this.setSaveEnabled(true);
    } else {
      this.setSaveEnabled(false);
      errorMessageComponent.setValue(messages);
      getParentAction("enableTabs").actionPerformed(null);

    } 
  }
  
  private class MyDocumentListener implements DocumentListener{

    public void changedUpdate(DocumentEvent e) {
      // TODO Auto-generated method stub
      handleButtons();
    }

    public void insertUpdate(DocumentEvent e) {
      // TODO Auto-generated method stub
      handleButtons();
    }

    public void removeUpdate(DocumentEvent e) {
      // TODO Auto-generated method stub
      handleButtons();
    }   
  }
  
  private void checkRstate() {
    
    boolean saveIsEnabled = isSaveEnabled();
    
    // RState check
    boolean rstateCorrect = false;
    rstateCorrect = Utils.patternMatch(rStateF.getText(), Constants.RSTATEPATTERN);
   
        if (!rstateCorrect) {
          rStateF.setNeutralBg(Color.PINK);
          saveIsEnabled = false;
        } else {
          rStateF.setNeutralBg(Color.WHITE);
        }      
  
    
    this.setSaveEnabled(saveIsEnabled);   
  }
  
}
