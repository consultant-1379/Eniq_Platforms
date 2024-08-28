package com.ericsson.eniq.techpacksdk.view.newInterface;

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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import com.ericsson.eniq.techpacksdk.datamodel.InterfaceTreeDataModel;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.GenericActionNode;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementtypeExt;
import com.ericsson.eniq.techpacksdk.view.newInterface.NewInterfaceDataModel.TPDepedencyTableModel;
import com.ericsson.eniq.techpacksdk.view.newInterface.NewInterfaceDataModel.TPTableModel;

@SuppressWarnings("serial")
public class ManageNewInterfaceView extends JPanel {

  String types[] = { "measurement", "reference" };

  private static final Logger logger = Logger.getLogger(ManageNewInterfaceView.class.getName());

  private Application application;

  private DataTreeNode selectedNode;

  private final GenericActionTree tpTree;

  private boolean saveEnabled = false;

  private JTable tppedencyT = new JTable();

  private JTable tpT = new JTable();

  private final LimitedSizeTextField nameF;

  // private LimitedSizeTextField productF;

  private final LimitedSizeTextField rStateF;

  private final LimitedSizeTextField iVersion;

  private final JTextField setsFromF;
  
  //20100715, eeoidiv,CR 26/109 18-FCP 103 8147/12:Removing parser dependency from ENIQ platform
  //new parser can be added without changing the platform code
  private JComboBox formatCB = new JComboBox(Utils.getParserFormats());

  private JComboBox typeCB = new JComboBox(types);

  private JTextArea descriptionA = new JTextArea();

  private JTextArea installDescriptionA = new JTextArea();

  private final DataModelController dataModelController;

  private final JFrame frame;

  private final int fieldSize;

  private String oldName;

  private String oldVersion;

  private boolean iteratingTree = false;
  //EQEV-2862 Autolock Interface Starts
  InterfaceTreeDataModel interfaceTreeDataModelUnlock;
  //EQEV-2862 Autolock Interface ends

  private final ErrorMessageComponent errorMessageComponent;

  public ManageNewInterfaceView(Datainterface di, Application application, DataModelController dataModelController,
      JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;

    this.dataModelController = dataModelController;

    this.application = application;

    // ************** Text panel **********************

    fieldSize = 25;
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

    nameF = new LimitedSizeTextField(fieldSize, Datainterface.getInterfacenameColumnSize(), true);
    nameF.setName("Name");
    nameF.getDocument().addDocumentListener(new MyDocumentListener());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    txtPanel.add(nameF, c);

    // rstate

    JLabel rstate = new JLabel("R-State");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    txtPanel.add(rstate, c);

    rStateF = new LimitedSizeTextField(fieldSize, Datainterface.getRstateColumnSize(), true);
    rStateF.setName("Rstate");
    rStateF.getDocument().addDocumentListener(new MyDocumentListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    txtPanel.add(rStateF, c);

    // / product
    /*
     * JLabel product = new JLabel("Product Number"); c.fill =
     * GridBagConstraints.NONE; c.gridx = 0; c.gridy = 1; c.weightx = 0;
     * txtPanel.add(product, c);
     * 
     * productF = new LimitedSizeTextField(fieldSize,
     * Datainterface.getProductnumberColumnSize(), true);
     * productF.getDocument().addDocumentListener(new MyDocumentListener());
     * c.fill = GridBagConstraints.NONE; c.gridx = 1; c.gridy = 1; c.weightx =
     * 1; txtPanel.add(productF, c);
     */
    // version
    JLabel version = new JLabel("Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(version, c);

    iVersion = new LimitedSizeTextField(fieldSize, Datainterface.getInterfaceversionColumnSize(), true);
    iVersion.setName("Version");
    // rstateF.getDocument().addDocumentListener(new MyDocumentListener());
    // iVersion.getDocument().addDocumentListener(new
    // MyOtherDocumentListener());
    iVersion.getDocument().addDocumentListener(new MyDocumentListener());

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    txtPanel.add(iVersion, c);

    // description

    JLabel description = new JLabel("Description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    txtPanel.add(description, c);

    descriptionA = new LimitedSizeTextArea(Interfacetechpacks.getInterfacenameColumnSize(), false);
    JScrollPane descriptionP = new JScrollPane(descriptionA);
    descriptionP.setPreferredSize(areaDim);

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
    formatCB = new JComboBox(Utils.getParserFormats());
    formatCB.setPreferredSize(fieldDim);
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

    typeCB = new JComboBox(types);
    typeCB.setPreferredSize(fieldDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 5;
    c.weightx = 1;
    txtPanel.add(typeCB, c);

    // SetFrom

    JLabel setsFrom = new JLabel("Sets From");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    txtPanel.add(setsFrom, c);

    JPanel jp = new JPanel();

    setsFromF = new JTextField("");
    setsFromF.setPreferredSize(fieldDim);
    setsFromF.setEditable(false);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 6;
    c.weightx = 1;
    jp.add(setsFromF, c);

    JButton b = new JButton("Clear");
    b.addActionListener(new MyButtonActionListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 2;
    c.gridy = 6;
    c.weightx = 1;
    jp.add(b, c);

    c.gridx = 1;
    c.gridy = 6;
    c.weightx = 1;
    txtPanel.add(jp, c);

    // TechPack

    JLabel tp = new JLabel("TechPack");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;
    txtPanel.add(tp, c);

    tpT = new JTable();
    TPTableModel tm = dataModelController.getNewInterfaceDataModel().getTPTableModel();
    tm.addTableModelListener(new TableTPSelectionListener());
    tpT.setModel(tm);
    tpT.addMouseListener(new TableTPSelectionListener());
    tpT.getTableHeader().addMouseListener(new TableTPSelectionListener());

    tpT.setToolTipText("");

    tpT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(
            new LimitedSizeTextField(fieldSize, Interfacetechpacks.getTechpacknameColumnSize(), false)));
    tpT.getColumnModel().getColumn(1).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Interfacetechpacks.getTechpackversionColumnSize(),
            false)));

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 8;
    c.weightx = 0;

    JScrollPane scrollPane = new JScrollPane(tpT);
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // TechPack Depedency

    JLabel tppedency = new JLabel("TechPack Depedency");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 9;
    c.weightx = 0;
    txtPanel.add(tppedency, c);

    tppedencyT = new JTable();

    TPDepedencyTableModel tmd = dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel();
    tmd.addTableModelListener(new TableTPDSelectionListener());
    tppedencyT.setModel(tmd);
    tppedencyT.addMouseListener(new TableTPDSelectionListener());
    tppedencyT.getTableHeader().addMouseListener(new TableTPDSelectionListener());

    tppedencyT.setToolTipText("");

    tppedencyT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(
            new LimitedSizeTextField(fieldSize, Techpackdependency.getTechpacknameColumnSize(), false)));

    tppedencyT.getColumnModel().getColumn(1).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Techpackdependency.getVersionColumnSize(), false)));

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 10;
    c.weightx = 0;

    JScrollPane scrollPane2 = new JScrollPane(tppedencyT);
    scrollPane2.setPreferredSize(tableDim);
    txtPanel.add(scrollPane2, c);

    // Install Description

    JLabel installDescription = new JLabel("Install Description");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 11;
    c.weightx = 0;
    txtPanel.add(installDescription, c);

    installDescriptionA = new LimitedSizeTextArea(Datainterface.getInstalldescriptionColumnSize(), false);
    JScrollPane installDescriptionP = new JScrollPane(installDescriptionA);
    installDescriptionP.setPreferredSize(areaDim);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 12;
    c.weightx = 0;
    txtPanel.add(installDescriptionP, c);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    JButton cancel;
    JButton create;
    JButton clear;

    clear = new JButton("Cancel");
    clear.setName("CancelCreateNewInterface");
    // clear.setActionCommand("cancel");
    clear.setAction(getAction("discard"));
    clear.setToolTipText("Cancel");

    cancel = new JButton("closeDialog");
    cancel.setName("CloseCreateNewInterface");
    // cancel.setActionCommand("closeDialog");
    cancel.setAction(getAction("closeDialog"));
    cancel.setToolTipText("closeDialog");

    create = new JButton("Create");
    create.setName("CreateNewInterface");
    // create.setActionCommand("create");
    create.setAction(getAction("create"));
    create.setToolTipText("Create");

    // ************** TPTree panel **********************

    tpTree = new GenericActionTree(dataModelController.getInterfaceTreeDataModel());

    tpTree.setCellRenderer(new NewInterfaceRenderer(dataModelController));
    tpTree.addTreeSelectionListener(new SelectionListener());
    tpTree.setToolTipText("");
    tpTree.setName("NewInterfaceTree");

    // if there is interface selected we will select the same interface here

    if (di != null) {

      iteratingTree = true;
      for (int i = 1; i < tpTree.getRowCount(); i++) {
        tpTree.expandRow(i);
      }
      TreePath selected = null;
      for (int i = 1; i < tpTree.getRowCount(); i++) {
        tpTree.setSelectionRow(i);
        GenericActionNode current = tpTree.getSelected();
        if (current instanceof DataTreeNode) {
          DataTreeNode node = (DataTreeNode) tpTree.getSelected();
          if (((Datainterface) node.getRockDBObject()).getInterfacename().equals(di.getInterfacename())
              && ((Datainterface) node.getRockDBObject()).getInterfaceversion().equals(di.getInterfaceversion())) {
            selected = tpTree.getPathForRow(i);
            break;
          }
        }
      }
      for (int i = 1; i < tpTree.getRowCount(); i++) {
        tpTree.collapseRow(i);
      }

      iteratingTree = false;
      tpTree.expandPath(selected);
      tpTree.setSelectionPath(selected);
    }

    final JScrollPane tpScrollPane = new JScrollPane(tpTree);

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(clear);
    buttonPanel.add(create);
    buttonPanel.add(cancel);

    // ************** TP chooser panel**********************

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(tpScrollPane);

    // ************** right & left panels, left panel contains
    // tab**********************

    JPanel lpanel = new JPanel(new BorderLayout());
    JPanel rpanel = new JPanel(new BorderLayout());
    rpanel.add(panel);
    lpanel.add(txtPanelS);

    // ************** Inner panel panel with right and left panels
    // **********************

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    innerPanel.add(lpanel, BorderLayout.LINE_START);
    innerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    innerPanel.add(rpanel, BorderLayout.LINE_START);

    // ************** Main panel with inner panel and button panel
    // **********************

    // insert both panels in main view

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(innerPanel, gbc);

    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 1;

    this.add(buttonPanel, gbc);
    setScreenMessage(null);

    // If there was an interface selected, set the type and format values for
    // the new interface.
    if (di != null) {

      // Check if the type is a valid interface type. In migrated
      // interfaces, the type might be something else. If the type is
      // valid, then it is used, if not, then the first one in the list is
      // used.
      if (isValidInterfaceType(di.getInterfacetype())) {
        // System.out.println("ManageNewInterfaceView(): DEBUG: Setting typeCB = "
        // + di.getInterfacetype() + " and formatCB = "
        // + di.getDataformattype());
        typeCB.setSelectedItem(di.getInterfacetype());
      } else {
        // System.out.println("ManageNewInterfaceView(): DEBUG: Setting DEFAULT type "
        // + (String)typeCB.getItemAt(0) + " instead of type = " +
        // di.getInterfacetype()
        // + " and formatCB = " + di.getDataformattype());
        typeCB.setSelectedIndex(0);
      }

      // Set the data format type
      formatCB.setSelectedItem(getDataFormat(di));

    }
  }

  private String getDataFormat(Datainterface di) {
    String dataformat = "";
    
    String parserformat = application.getContext().getResourceMap()
        .getString(di.getInterfacename() + ".parserformat");
    if (parserformat == null) {
      dataformat = di.getDataformattype();
    } else {
      dataformat = parserformat;
    }
    return dataformat;
  }

  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class SaveTask extends Task<Void, Void> {

    public SaveTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {
       
      logger.log(Level.INFO, "create");
      final String tmpType= "ManageNewInterfaceView";
      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

        dataModelController.getNewInterfaceDataModel().setInterfacename(nameF.getText());
        dataModelController.getNewInterfaceDataModel().setInterfacetype((String) typeCB.getSelectedItem());

        String version = iVersion.getText();
        version = version.replace("((", "");
        version = version.replace("))", "");

        dataModelController.getNewInterfaceDataModel().setInterfaceversion("((" + version + "))");
        dataModelController.getNewInterfaceDataModel().setStatus(new Long(0));
        dataModelController.getNewInterfaceDataModel().setDescription(descriptionA.getText());
        dataModelController.getNewInterfaceDataModel().setDataformattype((String) formatCB.getSelectedItem());
        // dataModelController.getNewInterfaceDataModel().setProductnumber(productF.getText());
        dataModelController.getNewInterfaceDataModel().setRState(rStateF.getText());
        dataModelController.getNewInterfaceDataModel().setEniqLevel("2.0");
        dataModelController.getNewInterfaceDataModel().setInstalldescription(installDescriptionA.getText());
        //Validation for invalid characters in interface name for EQEV-3659 (start)
        if(dataModelController.getNewInterfaceDataModel().getInterfacename().contains("%"))
        {
            String errMessage = dataModelController.getNewInterfaceDataModel().getInterfacename()+":  "
                    +" '%' Character is not allowed."; 
                    
                throw new Exception(errMessage);
        }
        if(dataModelController.getNewInterfaceDataModel().getInterfacename().contains("."))
        {
            String errMessage = dataModelController.getNewInterfaceDataModel().getInterfacename()+":  "
                    +" '.' Character is not allowed."; 
                    
                throw new Exception(errMessage);
        }
        if(dataModelController.getNewInterfaceDataModel().getInterfacename().contains("-"))
        {
            String errMessage = dataModelController.getNewInterfaceDataModel().getInterfacename()+":  "
                    +" '-' Character is not allowed."; 
                    
                throw new Exception(errMessage);
        }
        if(dataModelController.getNewInterfaceDataModel().getInterfacename().contains(" "))
        {
            String errMessage = dataModelController.getNewInterfaceDataModel().getInterfacename()+":  "
                    +" Space is not allowed in Interface Name."; 
                    
                throw new Exception(errMessage);
        }
      //Validation for invalid characters in interface name for EQEV-3659 (end)

        dataModelController.getNewInterfaceDataModel().save();
        
        if (setsFromF.getText().length() > 0) {
          dataModelController.getETLSetHandlingDataModel().copySets(oldName, oldVersion, nameF.getText(),
              "((" + version + "))", tmpType, dataModelController.getRockFactory());
        }

        dataModelController.getRockFactory().getConnection().commit();
        dataModelController.getEtlRockFactory().getConnection().commit();

      } catch (Exception e) {

        logger.warning("Error creating Interfaces " + e);
		//Validation for invalid characters in interface name for EQEV-3659(START)
        JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
        "save.error.caption"), JOptionPane.ERROR_MESSAGE);  
        //Validation for invalid characters in interface name for EQEV-3659(END)


        try {
          dataModelController.getRockFactory().getConnection().rollback();
          dataModelController.getEtlRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }

        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
          dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }

      frame.dispose();

      return null;
    }
  }

  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> create() {

    final Task<Void, Void> SaveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    SaveTask.setInputBlocker(new BusyIndicatorInputBlocker(SaveTask, busyIndicator));

    return SaveTask;
  }

  @Action
  public void discard() {

    tpTree.setSelectionRow(0);

    nameF.setText("");
    rStateF.setText("");
    typeCB.setSelectedIndex(0);
    formatCB.setSelectedIndex(0);
    iVersion.setText("");
    descriptionA.setText("");
    setsFromF.setText("");
    installDescriptionA.setText("");

    errorMessageComponent.setValue(null); // null sets messages to new Vector<String>()

    dataModelController.getNewInterfaceDataModel().newInterface(); // IDE #666 Interface creation fails, if interface made from scratch were selected from list [and Cancel pressed]. 2009-07-15, eeoidiv
    dataModelController.getNewInterfaceDataModel().refresh();

    TPDepedencyTableModel tmd = dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel();
    tmd.addTableModelListener(new TableTPDSelectionListener());
    tppedencyT.setModel(tmd);

    TPTableModel tm = dataModelController.getNewInterfaceDataModel().getTPTableModel();
    tm.addTableModelListener(new TableTPSelectionListener());
    tpT.setModel(tm);

    setSaveEnabled(false);
    frame.repaint();
  }

  @Action
  public void closeDialog() {
  
    logger.log(Level.INFO, "closeDialog");
	//EQEV-2862 Autolock Interface starts
    
    try {
    	interfaceTreeDataModelUnlock= dataModelController.getInterfaceTreeDataModel();
        GenericActionNode current = tpTree.getSelected();
        if (current instanceof DataTreeNode) {
          DataTreeNode node = (DataTreeNode) current;
          Datainterface i = (Datainterface) node.getRockDBObject();
          i.setLockedby("");
          i.setLockdate(null);
          interfaceTreeDataModelUnlock.modObj(i);
        }
        interfaceTreeDataModelUnlock.refresh();
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
      
    //EQEV-2862 Autolock Interface ends
    frame.dispose();
  }

  @Action
  public void addemptytp() {
    logger.fine("ADD " + dataModelController.getNewInterfaceDataModel().getTPTableModel().getRowCount());

    Interfacetechpacks itp = new Interfacetechpacks(dataModelController.getRockFactory());
    itp.setTechpackname("");
    itp.setTechpackversion("");
    dataModelController.getNewInterfaceDataModel().getTPTableModel().addNewRow(itp);
    dataModelController.getNewInterfaceDataModel().getTPTableModel().fireTableDataChanged();
  }

  @Action
  public void addtp() {
    logger.log(Level.INFO, "addtp");

    String s = (String) JOptionPane.showInputDialog(this, "Select Tech pack", "TechPacks", JOptionPane.PLAIN_MESSAGE,
        null, dataModelController.getNewInterfaceDataModel().getTechPacks(), "");

    if ((s != null) && (s.length() > 0)) {

      String split[] = s.split(":");
      Interfacetechpacks itp = new Interfacetechpacks(dataModelController.getRockFactory());
      itp.setTechpackname(split[0]);
      itp.setTechpackversion(split[1]);
      dataModelController.getNewInterfaceDataModel().getTPTableModel().addNewRow(itp);
      dataModelController.getNewInterfaceDataModel().getTPTableModel().fireTableDataChanged();
    }

  }

  @Action
  public void removetp() {
    logger.log(Level.INFO, "remove");

    dataModelController.getNewInterfaceDataModel().getTPTableModel().removeRow(tpT.getSelectedRow());
    dataModelController.getNewInterfaceDataModel().getTPTableModel().fireTableDataChanged();

  }

  @Action
  public void addemptytpd() {
    logger.fine("ADD " + dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().getRowCount());
    dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().addEmptyNewRow();
    dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().fireTableDataChanged();
  }

  @Action
  public void addtpd() {
    logger.log(Level.INFO, "addtp");

    String s = (String) JOptionPane.showInputDialog(this, "Select Tech pack", "Customized Dialog",
        JOptionPane.PLAIN_MESSAGE, null, dataModelController.getNewInterfaceDataModel().getTechPacks(), "");

    if ((s != null) && (s.length() > 0)) {
      // List l = new ArrayList();
      String split[] = s.split(":");
      Interfacedependency itp = new Interfacedependency(dataModelController.getRockFactory());
      itp.setTechpackname(split[0]);
      itp.setTechpackversion(split[1]);
      dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().addNewRow(itp);
      dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().fireTableDataChanged();
    }

  }

  @Action
  public void removetpd() {
    logger.log(Level.INFO, "remove");

    dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().removeRow(tppedencyT.getSelectedRow());
    dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel().fireTableDataChanged();

  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(final Application application) {
    this.application = application;
  }

  private void handleButtons() {
    boolean saveIsEnabled = false;

    // RState check
    boolean rstateCorrect = false;
    rstateCorrect = Utils.patternMatch(rStateF.getText(), Constants.RSTATEPATTERN);

    Vector<String> messages = new Vector<String>();

    // check techpack duplicates
    Vector<String> v1 = new Vector<String>();
    boolean tpduplicate = false;
    for (int i = 0; i < tpT.getModel().getRowCount(); i++) {
      if (v1.contains(tpT.getModel().getValueAt(i, 0))) {
        tpduplicate = true;
        messages.add("Techpack " + (String) tpT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      if (!Utils.patternMatch((String) tpT.getModel().getValueAt(i, 1), Constants.RSTATEPATTERN)) {
        tpduplicate = true;
        messages.add("Rstate in Techpack " + (String) tpT.getModel().getValueAt(i, 0) + "/"
            + (String) tpT.getModel().getValueAt(i, 1) + " Not correct");
      }
      v1.add((String) tpT.getModel().getValueAt(i, 0));
    }

    // check techpack depedency duplicates
    Vector<String> v2 = new Vector<String>();
    boolean tpdduplicate = false;
    for (int i = 0; i < tppedencyT.getModel().getRowCount(); i++) {
      if (v2.contains(tppedencyT.getModel().getValueAt(i, 0))) {
        tpdduplicate = true;
        messages.add("Techpack depedency " + (String) tppedencyT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      if (!Utils.patternMatch((String) tppedencyT.getModel().getValueAt(i, 1), Constants.RSTATEPATTERN)) {
        tpdduplicate = true;
        messages.add("Rstate in Techpack depedency " + (String) tppedencyT.getModel().getValueAt(i, 0) + "/"
            + (String) tppedencyT.getModel().getValueAt(i, 1) + " Not correct");
      }
      v2.add((String) tppedencyT.getModel().getValueAt(i, 0));
    }

    if (!tpduplicate && !tpdduplicate && this.nameF.getText().length() > 0 && this.iVersion.getText().length() > 0
        && this.rStateF.getText().length() > 0 && rstateCorrect) {
      saveIsEnabled = true;
      errorMessageComponent.setValue(null);
    } else {
      saveIsEnabled = false;
      errorMessageComponent.setValue(messages);
    }

    this.setSaveEnabled(saveIsEnabled);
  }

  private void checkVersion() {

    boolean saveIsEnabled = isSaveEnabled();

    Vector<String> v = dataModelController.getNewInterfaceDataModel().getAllInterfaceVersions(nameF.getText());
    if (v != null) {
      if (iVersion.getText().trim().length() > 0) {
        // migrated interface version is N/A without parenthesis
        String version = iVersion.getText();
        String parenthesisVersion = "((" + iVersion.getText() + "))";
        if (v.contains(version) || v.contains(parenthesisVersion)) {
          iVersion.setNeutralBg(Color.PINK);
          saveIsEnabled = false;
        } else {
          iVersion.setNeutralBg(Color.WHITE);
        }
      } else {
        iVersion.setNeutralBg(Color.PINK);
        saveIsEnabled = false;
      } // if (iVersion.getText().trim().length() > 0)
    } else {
      // Reset, when v is null, otherwise might be left pink from previous case.
      iVersion.setNeutralBg(Color.WHITE); // IDE #666 Interface creation fails, if interface made from scratch were selected from list [and Cancel pressed]. 2009-07-22, eeoidiv
    } // if (v != null)

    this.setSaveEnabled(saveIsEnabled);
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

  // private class MyOtherDocumentListener implements DocumentListener {
  //
  // public void changedUpdate(DocumentEvent e) {
  // handleButtons();
  // checkVersion();
  // }
  //
  // public void insertUpdate(DocumentEvent e) {
  // handleButtons();
  // checkVersion();
  // }
  //
  // public void removeUpdate(DocumentEvent e) {
  // handleButtons();
  // checkVersion();
  // }
  // }

  private class MyDocumentListener implements DocumentListener {

    @Override
    public void changedUpdate(DocumentEvent e) {
      handleButtons();
      checkVersion();
      checkRstate();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      handleButtons();
      checkVersion();
      checkRstate();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      handleButtons();
      checkVersion();
      checkRstate();
    }
  }

  private class SelectionListener implements TreeSelectionListener {

    @Override
    public void valueChanged(TreeSelectionEvent e) {

      if (iteratingTree) {

        TreePath t = e.getPath();
        Object pointed = t.getLastPathComponent();
        if (pointed instanceof DefaultMutableTreeNode) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
          Object tmp = node.getUserObject();
          if (tmp instanceof DataTreeNode) {
            setSelectedNode((DataTreeNode) tmp);
          }
        }
        return;
      }

      TreePath t = e.getPath();
      Object pointed = t.getLastPathComponent();
      if (pointed instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
        Object tmp = node.getUserObject();
        if (tmp instanceof DataTreeNode) {

          Datainterface di = (Datainterface) ((DataTreeNode) tmp).getRockDBObject();

          if (((DataTreeNode) tmp).locked != null && ((DataTreeNode) tmp).locked.length() > 0
              && !((DataTreeNode) tmp).locked.equals(dataModelController.getUserName())) {
            return;
          }

          if (di.getEniq_level() == null || di.getEniq_level().equalsIgnoreCase("1.0")) {
            return;
          }

          setSelectedNode((DataTreeNode) tmp);

          dataModelController.getNewInterfaceDataModel()
              .refresh((Datainterface) ((DataTreeNode) tmp).getRockDBObject());

          TPDepedencyTableModel tmd = dataModelController.getNewInterfaceDataModel().getTPDepedencyTableModel();
          tmd.addTableModelListener(new TableTPDSelectionListener());
          tppedencyT.setModel(tmd);

          tppedencyT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Techpackdependency.getTechpacknameColumnSize(),
                  false)));
          tppedencyT.getColumnModel().getColumn(1).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Techpackdependency.getVersionColumnSize(),
                  false)));

          TPTableModel tm = dataModelController.getNewInterfaceDataModel().getTPTableModel();
          tm.addTableModelListener(new TableTPSelectionListener());
          tpT.setModel(tm);

          tpT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Interfacetechpacks.getTechpacknameColumnSize(),
                  false)));
          tpT.getColumnModel().getColumn(1).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Interfacetechpacks
                  .getTechpackversionColumnSize(), false)));

          nameF.setText(dataModelController.getNewInterfaceDataModel().getInterfacename());
          // productF.setText(dataModelController.getNewInterfaceDataModel().getProductnumber());
          rStateF.setText(dataModelController.getNewInterfaceDataModel().getRState());

          String show = dataModelController.getNewInterfaceDataModel().getInterfaceversion();
          show = show.replace("((", "");
          show = show.replace("))", "");

          iVersion.setText(show);
          checkVersion();
          checkRstate();

          descriptionA.setText(dataModelController.getNewInterfaceDataModel().getDescription());
          installDescriptionA.setText(dataModelController.getNewInterfaceDataModel().getInstalldescription());

          // formatCB.setSelectedItem(dataModelController.getNewInterfaceDataModel().getDataformattype());
          // typeCB.setSelectedItem(dataModelController.getNewInterfaceDataModel().getInterfacetype());

          // Check if the type is a valid interface type. In migrated
          // interfaces, the type might be something else. If the type is
          // valid, then it is used, if not, then the first one in the list is
          // used.
          if (isValidInterfaceType(di.getInterfacetype())) {
            // System.out.println("valueChanged(): DEBUG: Setting typeCB = " +
            // di.getInterfacetype() + " and formatCB = "
            // + di.getDataformattype());
            typeCB.setSelectedItem(di.getInterfacetype());
          } else {
            // System.out.println("valueChanged(): DEBUG: Setting DEFAULT type "
            // + (String)typeCB.getItemAt(0) + " instead of type = " +
            // di.getInterfacetype()
            // + " and formatCB = " + di.getDataformattype());
            typeCB.setSelectedIndex(0);
          }
          formatCB.setSelectedItem(getDataFormat(di));

          oldName = nameF.getText();
          oldVersion = dataModelController.getNewInterfaceDataModel().getInterfaceversion();

          setsFromF.setText(oldName + ":" + show);

        } else {
          setSelectedNode(null);
        }
      } else {
        setSelectedNode(null);
      }
      if (getSelectedNode() != null) {
        logger.fine("Selected " + getSelectedNode());
      }
    }
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

    @Override
    public void tableChanged(TableModelEvent e) {

      handleButtons();

      tpT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Interfacetechpacks.getTechpacknameColumnSize(),
              false)));
      tpT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Interfacetechpacks.getTechpackversionColumnSize(),
              false)));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      displayTPMenu(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      displayTPMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      displayTPMenu(e);
    }
  }

  private class TableTPDSelectionListener implements TableModelListener, ActionListener, MouseListener {

    @Override
    public void tableChanged(TableModelEvent e) {

      handleButtons();

      tppedencyT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Techpackdependency.getTechpacknameColumnSize(),
              false)));

      tppedencyT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(fieldSize, Techpackdependency.getVersionColumnSize(), false)));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      displayTPDMenu(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      displayTPDMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      displayTPDMenu(e);
    }
  }

  public DataTreeNode getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(DataTreeNode selectedNode) {
    DataTreeNode exSelectedNode = this.selectedNode;
    this.selectedNode = selectedNode;
    firePropertyChange("selectedNode", exSelectedNode, selectedNode);
    tpTree.setSelected(selectedNode);

    // Enable/disable the the format and type fields, if new/copy interface will
    // be created. If a copy is created, initialise the type and format fields
    // to match the old one, since the fields cannot be changed.
    if (selectedNode != null) {
      // Copy of an existing interface

      // NOTE: Type value is allowed to be changed, since copying a migrated
      // techpack the original type value is the same as the format, so the
      // initial value is always set to measurement.
      //
      //typeCB.setEnabled(false);

      formatCB.setEnabled(false);

      // Set the type and format values to match the selected old interface.
      // System.out.println("setSelectedNode(): DEBUG: Copy interface: Setting typeCB = "
      // + ((Datainterface) selectedNode.getRockDBObject()).getInterfacetype() +
      // " and formatCB = "
      // + ((Datainterface)
      // selectedNode.getRockDBObject()).getDataformattype());
      typeCB.setSelectedItem(((Datainterface) selectedNode.getRockDBObject()).getInterfacetype());
      formatCB.setSelectedItem(getDataFormat(((Datainterface) selectedNode.getRockDBObject())));

    } else {
      // New interface
      // System.out.println("setSelectedNode(): DEBUG: New interface: Setting typeCB = "
      // + dataModelController.getNewInterfaceDataModel().getInterfacetype() +
      // " and formatCB = "
      // + dataModelController.getNewInterfaceDataModel().getDataformattype());
      typeCB.setSelectedItem(dataModelController.getNewInterfaceDataModel().getInterfacetype());
      formatCB.setSelectedItem(dataModelController.getNewInterfaceDataModel().getDataformattype());

      typeCB.setEnabled(true);
      formatCB.setEnabled(true);
    }

  }

  private class MyButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      setsFromF.setText("");
    }
  }

  /**
   * Returns true in case the interface type is listed in the interface types of
   * the class.
   * 
   * @param interfaceType
   * @return true in case the type is a valid interface type-
   */
  private boolean isValidInterfaceType(String interfaceType) {
    for (int i = 0; i < types.length; i++) {
      if (types[i].equalsIgnoreCase(interfaceType)) {
        return true;
      }
    }
    return false;
  }
}
