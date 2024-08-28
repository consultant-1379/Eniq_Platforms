package com.ericsson.eniq.techpacksdk.view.generaltechpack;

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
import java.util.List;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.repository.dwhrep.Supportedvendorrelease;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.ManageTechPackView;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.LicenseTableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.TPDTableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.UETableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.VRTableModel;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackFunctionality;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackInfo;

@SuppressWarnings("serial")
public class ManageGeneralPropertiesView extends JPanel {

  /**
   * Universe extension table edit disabled // 26.6.2008 JTS
   */

  RockFactory rock = null;

  String name, version, product, rstate, licenseName, unvname, unvext, description, basedefinition, baseversion, type,
      deptechpackname, deptechpackversion, installdescription, vendorrelease, fromtechpack, username;

  private static final Logger logger = Logger.getLogger(ManageTechPackView.class.getName());

  private SingleFrameApplication application;

  private DataTreeNode selectedNode;

  private GenericActionTree tpTree;

  private boolean saveEnabled = false;

  // private boolean tableUEChanged = false;

  private boolean tableVRChanged = false;

  private boolean tableTPDChanged = false;

  private boolean tableLicenseChanged = false;

  private GeneralTechPackTab parentPanel;

  public static final Color ERROR_BG = Color.PINK;

  public static final Color WHITE = Color.WHITE;

  private final Color neutralBg;

  private String oldDescriptionA;

  private String oldinstalldescrA;

  private String oldrstateF;

  private LimitedSizeTextArea descriptionA;

  Versioning versioning;

  Supportedvendorrelease vendorreleases;

  NewTechPackFunctionality NewTechPack;

  JTable tppedencyT = new JTable();

  JTable venrelT = new JTable();

  JTable unvextT = new JTable();

  JTable licenceT = new JTable();

  JTable tpT = new JTable();

  JScrollPane scrollPane;

  JTextField nameF = new JTextField("");

  JTextField versionF = new JTextField("");

  JTextField productF = new JTextField("");

  LimitedSizeTextField rstateF;

  JComboBox typeCB = new JComboBox();

  JTextField unvnameF = new JTextField("");

  JTextField unvextF = new JTextField("");

  JComboBox baseCB = new JComboBox();

  JTextField deptpnameF = new JTextField("");

  JTextField deptpverF = new JTextField("");

  JTextArea installdescrA = new JTextArea();

  DataModelController dataModelController;

  Versioning oldversioning;

  JFrame frame;

  // if true handleButton method is skipped...
  private boolean ignoreButtonHandler = false;

  private String setType;

  public void finalize() throws Throwable {
    super.finalize();
  }

  private ErrorMessageComponent errorMessageComponent;
  
  /**
   * Constructor to be used only for test.
   * @param mockNameF 
   * @param mockversionF 
   * @param mockProductF 
   * @param mockRstateF 
   * @param mockOldrstateF 
   * @param mockUnvnameF 
   * @param mockTypeCB 
   * @param mockDescriptionA 
   * @param mockBaseCB 
   * @param mockTppedencyT 
   * @param mockUnvextT 
   * @param mockLicenseTable 
   * @param installdescrAMock 
   * @param vendorReleaseTableMock 
   */
  protected ManageGeneralPropertiesView(DataModelController modelControllerMock, JTextField mockNameF, JTextField mockversionF, JTextField mockProductF, 
      LimitedSizeTextField mockRstateF, String mockOldrstateF, JTextField mockUnvnameF, JComboBox mockTypeCB, 
      LimitedSizeTextArea mockDescriptionA, JComboBox mockBaseCB, JTable mockTppedencyT, JTable mockUnvextT, JTable mockLicenseTable, 
      LimitedSizeTextArea installdescrAMock, JTable vendorReleaseTableMock) {
    nameF = mockNameF;   
    versionF = mockversionF;
    productF = mockProductF;
    rstateF = mockRstateF;
    oldrstateF = mockOldrstateF;
    unvnameF = mockUnvnameF;
    typeCB = mockTypeCB;
    descriptionA = mockDescriptionA;
    baseCB = mockBaseCB;
    tppedencyT = mockTppedencyT;
    unvextT = mockUnvextT;
    licenceT = mockLicenseTable;
    installdescrA = installdescrAMock;
    dataModelController = modelControllerMock;
    venrelT = vendorReleaseTableMock;
            
    neutralBg = null;       
  }

  public ManageGeneralPropertiesView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.rock = dataModelController.getRockFactory();

    this.frame = frame;

    this.dataModelController = dataModelController;

    this.application = application;

    this.parentPanel = parentPanel;

    oldversioning = versioning;

    neutralBg = null;

    setType = versioning.getTechpack_type();

    // ************** Text panel **********************
    int fieldSize = 25;
    int maxwidth = 279;
    Dimension fieldDim = new Dimension(maxwidth, 25);
    Dimension areaDim = new Dimension(maxwidth, 85);
    Dimension tableDim = new Dimension(maxwidth, 85);

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

    nameF = new JTextField(versioning.getTechpack_name());
    nameF.setPreferredSize(fieldDim);
    nameF.setBackground(neutralBg);
    nameF.setEditable(false);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    nameF.setName("TechPackName");
    txtPanel.add(nameF, c);

    // version

    JLabel version = new JLabel("Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    txtPanel.add(version, c);

    String tmpVersionid = versioning.getVersionid();
    String removeStr = versioning.getTechpack_name();

    if (tmpVersionid != "") {

      removeStr += ":";
      tmpVersionid = tmpVersionid.replace(removeStr, "");

      if (tmpVersionid.startsWith("((")) {
        tmpVersionid = tmpVersionid.substring(2);
        tmpVersionid = tmpVersionid.replace("))", "");
      }
      versionF.setText(tmpVersionid);
    }

    versionF.setPreferredSize(fieldDim);
    versionF.setBackground(neutralBg);
    versionF.setEditable(false);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    versionF.setName("Version");
    txtPanel.add(versionF, c);

    // product

    JLabel product = new JLabel("Product Number");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(product, c);

    productF = new JTextField(versioning.getProduct_number());
    productF.setPreferredSize(fieldDim);
    productF.setBackground(neutralBg);
    productF.setEditable(false);
    productF.setName("Product");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    txtPanel.add(productF, c);

    // rstate

    JLabel rstate = new JLabel("R-state");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    txtPanel.add(rstate, c);

    rstateF = new LimitedSizeTextField(fieldSize, Versioning.getTechpack_versionColumnSize(), true);
    rstateF.setText(versioning.getTechpack_version());
    rstateF.setEnabled(editable);

    oldrstateF = (versioning.getTechpack_version());
    rstateF.getDocument().addDocumentListener(new MyOtherDocumentListener());

    rstateF.setPreferredSize(fieldDim);
    rstateF.setEditable(true);
    rstateF.setName("Rstate");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 3;
    c.weightx = 1;
    txtPanel.add(rstateF, c);

    // license

    /*
     * JLabel license = new JLabel("License Name"); c.fill =
     * GridBagConstraints.NONE; c.gridx = 0; c.gridy = 4; c.weightx = 0;
     * txtPanel.add(license, c);
     * 
     * licenseF = new LimitedSizeTextField(fieldSize,
     * Versioning.getLicensenameColumnSize(), false);
     * licenseF.setText(versioning.getLicensename());
     * licenseF.setEnabled(editable); oldlicenseF = licenseF.getText();
     * licenseF.getDocument().addDocumentListener(new
     * MyOtherDocumentListener()); licenseF.setPreferredSize(fieldDim);
     * licenseF.setEditable(true); c.fill = GridBagConstraints.NONE; c.gridx =
     * 1; c.gridy = 4; c.weightx = 1; txtPanel.add(licenseF, c);
     */

    JLabel license = new JLabel("License Names");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    txtPanel.add(license, c);

    licenceT = new JTable();
    LicenseTableModel litm = dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel();
    litm.addTableModelListener(new TableLicenseSelectionListener());
    licenceT.setModel(litm);
    licenceT.setBackground(WHITE);
    licenceT.setEnabled(editable);

    if (!(setType.equalsIgnoreCase("base") || setType.equalsIgnoreCase("topology")) && editable) {
      licenceT.addMouseListener(new TableLicenseSelectionListener());
      licenceT.getTableHeader().addMouseListener(new TableLicenseSelectionListener());
    }

    licenceT.getColumnModel().getColumn(0).setCellEditor(
            new DefaultCellEditor(new LimitedSizeTextField(25, Versioning.getLicensenameColumnSize(), false)));
    licenceT.setToolTipText("");
    licenceT.setName("License");

    if (setType.equalsIgnoreCase("base")) {
      licenceT.setBackground(neutralBg);
      licenceT.setEnabled(false);
    }

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 4;
    c.weightx = 0;
    JScrollPane licenseScroll = new JScrollPane(licenceT);
    licenseScroll.setPreferredSize(tableDim);
    txtPanel.add(licenseScroll, c);

    // type

    JLabel type = new JLabel("Type");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    txtPanel.add(type, c);

    typeCB.addItem(setType);
    typeCB.setBackground(neutralBg);
    typeCB.setEditable(false);
    typeCB.setPreferredSize(fieldDim);
    typeCB.setName("Type");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 5;
    c.weightx = 1;
    txtPanel.add(typeCB, c);

    // unvname

    JLabel unvname = new JLabel("Universe name");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    txtPanel.add(unvname, c);

    unvnameF.setText(dataModelController.getEditGeneralTechPackDataModel().getUniversename(versioning.getVersionid()));
    unvnameF.setBackground(neutralBg);
    unvnameF.setEditable(false);
    unvnameF.setPreferredSize(fieldDim);
    unvnameF.setName("UniverseName");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 6;
    c.weightx = 1;
    txtPanel.add(unvnameF, c);

    // unvext

    JLabel unvext = new JLabel("Universe extension");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;
    txtPanel.add(unvext, c);

    unvextT = new JTable();

    UETableModel uetm = dataModelController.getEditGeneralTechPackDataModel().getUnvExtTableModel();
    // uetm.addTableModelListener(new TableUnvExtSelectionListener());
    unvextT.setModel(uetm);
    // unvextT.setBackground(WHITE);
    unvextT.setBackground(neutralBg);
    unvextT.setEnabled(false);
    // unvextT.setEnabled(editable);
    // if (editable) {
    // unvextT.addMouseListener(new TableUnvExtSelectionListener());
    // unvextT.getTableHeader().addMouseListener(new
    // TableUnvExtSelectionListener());
    // }
    unvextT.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    unvextT.getColumnModel().getColumn(0).setCellEditor(
            new DefaultCellEditor(new LimitedSizeTextField(25, Universename.getUniverseextensionColumnSize(), false)));
    unvextT.getColumnModel().getColumn(0).setPreferredWidth(115);
    unvextT.getColumnModel().getColumn(1).setCellEditor(
            new DefaultCellEditor(new LimitedSizeTextField(35, Universename.getUniverseextensionnameColumnSize(), false)));
    unvextT.getColumnModel().getColumn(1).setPreferredWidth((maxwidth - 115));
    unvextT.setToolTipText("");
    unvextT.setName("UniverseExtension");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 7;
    c.weightx = 0;
    JScrollPane unvextscroll = new JScrollPane(unvextT);
    unvextscroll.setPreferredSize(tableDim);
    txtPanel.add(unvextscroll, c);

    // description

    JLabel description = new JLabel("Description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 8;
    c.weightx = 0;
    txtPanel.add(description, c);

    descriptionA = new LimitedSizeTextArea(Versioning.getDescriptionColumnSize(), false);
    descriptionA.setText(versioning.getDescription());
    oldDescriptionA = versioning.getDescription();
    descriptionA.getDocument().addDocumentListener(new MyOtherDocumentListener());
    JScrollPane descriptionP = new JScrollPane(descriptionA);
    descriptionP.setPreferredSize(areaDim);
    descriptionA.setName("Description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 8;
    c.weightx = 1;
    txtPanel.add(descriptionP, c);

    // venrel

    JLabel venrel = new JLabel("Vendor release");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 9;
    c.weightx = 0;
    txtPanel.add(venrel, c);

    venrelT = new JTable();
    venrelT.setName("venrelT");
    venrelT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    VRTableModel vrtm = dataModelController.getEditGeneralTechPackDataModel().getVRTableModel();
    vrtm.addTableModelListener(new TableVRSelectionListener());
    venrelT.setModel(vrtm);
    venrelT.setEnabled(editable);
    if (!setType.equalsIgnoreCase("base") && editable) {
      venrelT.addMouseListener(new TableVRSelectionListener());
      venrelT.getTableHeader().addMouseListener(new TableVRSelectionListener());
    }

    JTextField tf = new JTextField();
    tf.setEditable(false);
    venrelT.getColumnModel().getColumn(0)
    // .setCellEditor(new DefaultCellEditor(new LimitedSizeTextField(25,
    // Supportedvendorrelease.getVendorreleaseColumnSize(),false)));
        .setCellEditor(new DefaultCellEditor(tf));
    venrelT.setToolTipText("");
    venrelT.setName("VendorRelease");

    if (setType.equalsIgnoreCase("base")) {
      venrelT.setBackground(neutralBg);
      venrelT.setEnabled(false);
    }

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 9;
    c.weightx = 0;
    JScrollPane scroll = new JScrollPane(venrelT);
    scroll.setPreferredSize(tableDim);
    txtPanel.add(scroll, c);

    // basedef

    JLabel basedef = new JLabel("Base definition");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 10;
    c.weightx = 0;
    txtPanel.add(basedef, c);

    baseCB.addItem(versioning.getBasedefinition());
    baseCB.setBackground(neutralBg);
    baseCB.setEditable(false);
    baseCB.setPreferredSize(fieldDim);
    baseCB.setName("BaseDefinition");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 10;
    c.weightx = 1;
    txtPanel.add(baseCB, c);

    // TechPack Dependency

    JLabel tppedency = new JLabel("TechPack Depedency");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = 11;
    c.weightx = 0;
    txtPanel.add(tppedency, c);

    tppedencyT = new JTable();
    TPDTableModel tpdtm = dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel();
    tpdtm.addTableModelListener(new TableTPDSelectionListener());
    tppedencyT.setModel(tpdtm);
    tppedencyT.setBackground(WHITE);
    tppedencyT.setEnabled(editable);
    if (!setType.equalsIgnoreCase("base") && editable) {
      tppedencyT.addMouseListener(new TableTPDSelectionListener());
      tppedencyT.getTableHeader().addMouseListener(new TableTPDSelectionListener());
    }
    tppedencyT.getColumnModel().getColumn(0).setCellEditor(
            new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));
    tppedencyT.getColumnModel().getColumn(1).setCellEditor(
            new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));
    tppedencyT.setToolTipText("");
    tppedencyT.setName("TechPackDependency");

    if (setType.equalsIgnoreCase("base")) {
      tppedencyT.setBackground(neutralBg);
      tppedencyT.setEnabled(false);
    }

    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 11;
    c.weightx = 0;
    JScrollPane scrollPane = new JScrollPane(tppedencyT);
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // installdescr
    JLabel installdescr = new JLabel("Install description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 12;
    c.weightx = 0;
    txtPanel.add(installdescr, c);

    installdescrA = new LimitedSizeTextArea(Versioning.getInstalldescriptionColumnSize(), false);
    installdescrA.setName("InstallDescription");
    installdescrA.setText(versioning.getInstalldescription());
    oldinstalldescrA = (versioning.getInstalldescription());
    installdescrA.getDocument().addDocumentListener(new MyOtherDocumentListener());
    installdescrA.setName("InstallDescription");
    JScrollPane installdescrP = new JScrollPane(installdescrA);
    installdescrP.setPreferredSize(areaDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 12;
    c.weightx = 1;
    txtPanel.add(installdescrP, c);

    // View mode
    if (editable == false) {
      installdescrA.setEditable(false);
      tppedencyT.setEnabled(false);
      venrelT.setEnabled(false);
      unvextT.setEnabled(false);
      rstate.setEnabled(false);
      descriptionA.setEnabled(false);
    }

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    JButton cancel;
    JButton save;

    cancel = new JButton("Discard");
    cancel.setActionCommand("discard");
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Discard");
    cancel.setName("ManageGeneralPropertiesDiscard");

    save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");
    save.setName("ManageGeneralPropertiesSave");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("ManageGeneralPropertiesClose");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    // ************** right & left panels, left panel contains tab
    // **************

    JPanel lpanel = new JPanel(new BorderLayout());
    JPanel rpanel = new JPanel(new BorderLayout());
    lpanel.add(txtPanelS);

    // ****** Inner panel panel with right and left panels ******

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    innerPanel.add(lpanel, BorderLayout.LINE_START);
    innerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    innerPanel.add(rpanel, BorderLayout.LINE_START);

    // *** Main panel with inner panel and button panel ***
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

    // creating NewTechPackFunctionality object
    NewTechPack = new NewTechPackFunctionality();

    setScreenMessage(null);
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
      
      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);

        NewTechPackInfo techpack = createNewTechPackInfo();

        NewTechPack.updateTechPack(techpack, dataModelController.getRockFactory());

        // JOptionPane.showMessageDialog(null, "Tech Pack " + name + " " +
        // version + " properties updated");

        dataModelController.getRockFactory().getConnection().commit();
        dataModelController.rockObjectsModified(dataModelController.getEditGeneralTechPackDataModel());

        ((GeneralTechPackTab) parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {
        try {
          dataModelController.getRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }

      tableVRChanged = false;
      tableTPDChanged = false;
      tableLicenseChanged = false;
      // tableUEChanged = false;
      setSaveEnabled(false);

      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();

      // Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);

      return null;
    }
  }
  
  /**
   * Creates a NewTechPackInfo object.
   * @return techpack 
   */
  protected NewTechPackInfo createNewTechPackInfo() {
    NewTechPackInfo techpack = null;
  
    try {
      List<Object> venrellist;
      List<String> tpdeplist;
      List<List<String>> unvextlist;
      username = dataModelController.getUserName();
  
      name = nameF.getText();
  
      version = versionF.getText();
      version = version.toUpperCase();
      version = "((" + version + "))";
  
      product = productF.getText();
      product = product.toUpperCase();
  
      rstate = rstateF.getText();
      rstate = rstate.toUpperCase();
  
      licenseName = "";
      LicenseTableModel lic = (LicenseTableModel) licenceT.getModel();
      int tmprows1 = lic.getRowCount();
      for (int i = 0; i < tmprows1; i++) {
        if (i == 0) {
          licenseName += lic.getValueAt(i, 0).trim();
        } else {
          licenseName += "," + lic.getValueAt(i, 0).trim();
        }
      }
  
      oldrstateF = rstateF.getText();
      oldrstateF = oldrstateF.toUpperCase();
  
      unvname = unvnameF.getText();
  
      type = (String) typeCB.getSelectedItem();
  
      UETableModel mtmUnvExt = (UETableModel) unvextT.getModel();
      unvextlist = new Vector<List<String>>();
      int tmprows = mtmUnvExt.getRowCount();
  
      for (int i = 0; i < tmprows; i++) {
        // (Universe Extension, Universe Extension Name) E.g. "a","Standard"
        List<String> unvextlistrow = new Vector<String>();
        unvextlistrow.add(0, (String) mtmUnvExt.getValueAt(i, 0));
        unvextlistrow.add(1, (String) mtmUnvExt.getValueAt(i, 1));
        unvextlist.add(unvextlistrow);
      }
  
      description = descriptionA.getText();
      oldDescriptionA = descriptionA.getText();
  
      VRTableModel mtm = (VRTableModel) venrelT.getModel();
      venrellist = new Vector<Object>();
      int rows = mtm.getRowCount();
      for (int i = 0; i < rows; i++) {
        venrellist.add(mtm.getValueAt(i, 0));
      }
  
      basedefinition = (String) baseCB.getSelectedItem();
  
      TPDTableModel mtmTPD = (TPDTableModel) tppedencyT.getModel();
      tpdeplist = new Vector<String>();
  
      if (mtmTPD != null) {
  
        int rowCount = mtmTPD.getRowCount();
  
        for (int d = 0; d < rowCount; d++) {
          tpdeplist.add((String) mtmTPD.getValueAt(d, 0));
          tpdeplist.add((String) mtmTPD.getValueAt(d, 1));
        }
      }
  
      installdescription = installdescrA.getText();
      oldinstalldescrA = installdescrA.getText();
  
      techpack = new NewTechPackInfo(name, version, product, rstate, licenseName, type, unvname, unvextlist,
          description, venrellist, basedefinition, tpdeplist, installdescription, fromtechpack, username);
    } catch (Exception exc) {
      logger.log(Level.WARNING, "Failed to create new tech pack info");
    }
    return techpack;
  }

  private class DiscardTask extends Task<Void, Void> {

    public DiscardTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      ignoreButtonHandler = true;

      logger.info("Discard Edit Tech Pack");

      dataModelController.getEditGeneralTechPackDataModel().refresh(oldversioning);
      rstateF.setText(oldversioning.getTechpack_version());

      LicenseTableModel lic = dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel();
      licenceT.setModel(lic);

      errorMessageComponent.setValue(null);

      descriptionA.setText(oldversioning.getDescription());

      UETableModel ue = dataModelController.getEditGeneralTechPackDataModel().getUnvExtTableModel();
      // ue.addTableModelListener(new TableUnvExtSelectionListener());
      unvextT.setModel(ue);

      unvextT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Universename.getUniverseextensionColumnSize(), false)));
      unvextT.getColumnModel().getColumn(0)
              .setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(35, Universename.getUniverseextensionnameColumnSize(),
                  false)));

      VRTableModel vr = dataModelController.getEditGeneralTechPackDataModel().getVRTableModel();
      vr.addTableModelListener(new TableVRSelectionListener());
      venrelT.setModel(vr);

      JTextField jt = new JTextField();
      jt.setEditable(false);
      venrelT.getColumnModel().getColumn(0)
      // .setCellEditor(new DefaultCellEditor(new LimitedSizeTextField(25,
      // Supportedvendorrelease.getVendorreleaseColumnSize(),false)));
          .setCellEditor(new DefaultCellEditor(jt));

      TPDTableModel tpd = dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel();
      tpd.addTableModelListener(new TableTPDSelectionListener());
      tppedencyT.setModel(tpd);

      tppedencyT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));

      tppedencyT.getColumnModel().getColumn(1).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

      installdescrA.setText(oldversioning.getInstalldescription());

      // /tableUEChanged = false;
      tableVRChanged = false;
      tableTPDChanged = false;
      tableLicenseChanged = false;
      setSaveEnabled(false);

      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();

      ignoreButtonHandler = false;

      return null;
    }
  }

  /**
   * DiscardTask action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> discard() {
    final Task<Void, Void> DiscardTask = new DiscardTask(application);
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
   * Save action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> save() {
    final Task<Void, Void> saveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));

    return saveTask;
  }

  @Action
  public void close() {
    logger.log(Level.INFO, "close");
    frame.dispose();
  }

  // Vendor release mouse menu
  @Action
  public void addemptyvr() {
    logger.fine("ADD " + dataModelController.getEditGeneralTechPackDataModel().getVRTableModel().getRowCount());

    String s = (String) JOptionPane.showInputDialog(this, "Add Vendor Release", "Vendor Release",
        JOptionPane.PLAIN_MESSAGE, null, null, "");

    if ((s != null) && (s.length() > 0)) {
      dataModelController.getEditGeneralTechPackDataModel().getVRTableModel().addNewRow(s);
      dataModelController.getEditGeneralTechPackDataModel().getVRTableModel().fireTableDataChanged();
    }

  }

  @Action
  public void removevr() {
    logger.log(Level.INFO, "remove");
    dataModelController.getEditGeneralTechPackDataModel().getVRTableModel().removeRow(venrelT.getSelectedRow());
    dataModelController.getEditGeneralTechPackDataModel().getVRTableModel().fireTableDataChanged();
  }

  // Tech Pack Dependencies mouse menu

  @Action
  public void addtpd() {
    logger.log(Level.INFO, "addtpd");

    String s = (String) JOptionPane.showInputDialog(this, "Select Tech pack", "TechPacks", JOptionPane.PLAIN_MESSAGE,
        null, dataModelController.getEditGeneralTechPackDataModel().getTechPacks(), "");

    if ((s != null) && (s.length() > 0)) {

      String split[] = s.split(":");

      Techpackdependency tpd = new Techpackdependency(dataModelController.getRockFactory());
      tpd.setTechpackname(split[0]);
      tpd.setVersion(split[1]);
      dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().addNewRow(tpd);
      dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().fireTableDataChanged();
    }
  }

  @Action
  public void addemptytpd() {
    logger
        .fine("ADD " + dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().getRowCount());
    dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().addEmptyNewRow();
    dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().fireTableDataChanged();
  }

  @Action
  public void removetpd() {

    logger.log(Level.INFO, "remove");
    dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().removeRow(
        tppedencyT.getSelectedRow());
    dataModelController.getEditGeneralTechPackDataModel().getTPDepedencyTableModel().fireTableDataChanged();
  }

  @Action
  public void addemptyli() {
    logger.fine("ADD " + dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel().getRowCount());
    dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel().addEmptyNewRow();
    dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel().fireTableDataChanged();
  }

  @Action
  public void removeli() {

    logger.log(Level.INFO, "remove");
    dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel().removeRow(licenceT.getSelectedRow());
    dataModelController.getEditGeneralTechPackDataModel().getLicenseTableModel().fireTableDataChanged();
  }

  // Universe Extension mouse menu
  /*
   * @Action public void addemptyue() { logger.fine("ADD " +
   * dataModelController.
   * getEditGeneralTechPackDataModel().getUnvExtTableModel().getRowCount());
   * dataModelController
   * .getEditGeneralTechPackDataModel().getUnvExtTableModel().addEmptyNewRow();
   * dataModelController
   * .getEditGeneralTechPackDataModel().getUnvExtTableModel().
   * fireTableDataChanged(); }
   * 
   * @Action public void removeue() {
   * 
   * logger.log(Level.INFO, "remove");
   * dataModelController.getEditGeneralTechPackDataModel
   * ().getUnvExtTableModel().removeRow(unvextT.getSelectedRow());
   * dataModelController
   * .getEditGeneralTechPackDataModel().getUnvExtTableModel().
   * fireTableDataChanged(); }
   */
  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  public DataTreeNode getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(DataTreeNode selectedNode) {
    DataTreeNode exSelectedNode = this.selectedNode;
    this.selectedNode = selectedNode;
    firePropertyChange("selectedNode", exSelectedNode, selectedNode);
    tpTree.setSelected(selectedNode);

  }

  // ************************************************
  // Vendor release table popup menu
  // ************************************************
  private JPopupMenu createVRPopup(MouseEvent e) {
    JPopupMenu popupVR;
    JMenuItem miVR;
    popupVR = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (venrelT.getSelectedRow() > -1) {
        miVR = new JMenuItem("Remove");
        miVR.setAction(getAction("removevr"));
        popupVR.add(miVR);
      }

      miVR = new JMenuItem("Add Empty");
      miVR.setAction(getAction("addemptyvr"));
      popupVR.add(miVR);

    } else {
      if (!(typeCB.getSelectedItem().toString().equalsIgnoreCase("Topology")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("BASE")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("EVENT") || typeCB.getSelectedItem().toString()
          .equalsIgnoreCase("SYSTEM"))) {

        miVR = new JMenuItem("Add Empty");
        miVR.setAction(getAction("addemptyvr"));
        popupVR.add(miVR);
      }
    }

    popupVR.setOpaque(true);
    popupVR.setLightWeightPopupEnabled(true);

    return popupVR;
  }

  private void displayVRMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createVRPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private class TableVRSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableVRChanged = true;
      handleButtons();

      JTextField jt = new JTextField();
      jt.setEditable(false);

      venrelT.getColumnModel().getColumn(0)
      // .setCellEditor(new DefaultCellEditor(new LimitedSizeTextField(25,
      // Supportedvendorrelease.getVendorreleaseColumnSize(),false)));
          .setCellEditor(new DefaultCellEditor(jt));

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayVRMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayVRMenu(e);
    }

    public void mouseReleased(MouseEvent e) {

      displayVRMenu(e);
    }
  }

  // ************************************************
  // Universe extension table popup menu
  // ************************************************
  /*
   * private JPopupMenu createUnvExtPopup(MouseEvent e) { JPopupMenu
   * popupUnvExt; JMenuItem miUnvExt; popupUnvExt = new JPopupMenu();
   * 
   * if (e.getSource() instanceof JTable) { if (unvextT.getSelectedRow() > -1) {
   * miUnvExt = new JMenuItem("Remove");
   * miUnvExt.setAction(getAction("removeue")); popupUnvExt.add(miUnvExt); }
   * 
   * miUnvExt = new JMenuItem("Add Empty");
   * miUnvExt.setAction(getAction("addemptyue")); popupUnvExt.add(miUnvExt); }
   * else {
   * 
   * if (!(typeCB.getSelectedItem().toString().equalsIgnoreCase("Topology") ||
   * typeCB.getSelectedItem().toString().equalsIgnoreCase("BASE") ||
   * typeCB.getSelectedItem().toString().equalsIgnoreCase("EVENT") ||
   * typeCB.getSelectedItem().toString() .equalsIgnoreCase("SYSTEM"))) {
   * 
   * miUnvExt = new JMenuItem("Add Empty");
   * miUnvExt.setAction(getAction("addemptyue")); popupUnvExt.add(miUnvExt); } }
   * 
   * popupUnvExt.setOpaque(true); popupUnvExt.setLightWeightPopupEnabled(true);
   * 
   * return popupUnvExt; }
   * 
   * private void displayUnvExtMenu(MouseEvent e) {
   * 
   * if (e.isPopupTrigger()) { createUnvExtPopup(e).show(e.getComponent(),
   * e.getX(), e.getY()); } }
   * 
   * private class TableUnvExtSelectionListener implements TableModelListener,
   * ActionListener, MouseListener {
   * 
   * public void tableChanged(TableModelEvent e) { tableUEChanged = true;
   * handleButtons();
   * 
   * unvextT.getColumnModel().getColumn(0).setCellEditor( new
   * DefaultCellEditor(new LimitedSizeTextField(25,
   * Universename.getUniverseextensionColumnSize(), false)));
   * 
   * 
   * }
   * 
   * public void actionPerformed(ActionEvent e) {
   * 
   * }
   * 
   * public void mouseClicked(MouseEvent e) { displayUnvExtMenu(e); }
   * 
   * public void mouseEntered(MouseEvent e) {
   * 
   * }
   * 
   * public void mouseExited(MouseEvent e) {
   * 
   * }
   * 
   * public void mousePressed(MouseEvent e) { displayUnvExtMenu(e); }
   * 
   * public void mouseReleased(MouseEvent e) {
   * 
   * displayUnvExtMenu(e); } }
   */
  private void handleButtons() {

    if (ignoreButtonHandler) {
      return;
    }

    // RState check
    boolean rstateCorrect = false;
    rstateCorrect = Utils.patternMatch(rstateF.getText(), Constants.RSTATEPATTERN);
    checkRstate();

    Vector<String> messages = new Vector<String>();

    // check licence duplicates
    Vector<String> v1 = new Vector<String>();
    boolean licduplicate = false;
    for (int i = 0; i < licenceT.getModel().getRowCount(); i++) {
      if (v1.contains(licenceT.getModel().getValueAt(i, 0))) {
        licduplicate = true;
        messages.add("Licence " + licenceT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      v1.add((String) licenceT.getModel().getValueAt(i, 0));
    }

    // check techpack depedency duplicates
    Vector<String> v2 = new Vector<String>();
    boolean tpdduplicate = false;
    for (int i = 0; i < tppedencyT.getModel().getRowCount(); i++) {
      if (v2.contains((String) tppedencyT.getModel().getValueAt(i, 0))) {
        tpdduplicate = true;
        messages.add("Techpack " + (String) tppedencyT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      if (!Utils.patternMatch((String) tppedencyT.getModel().getValueAt(i, 1), Constants.RSTATEPATTERN)) {
        tpdduplicate = true;
        messages.add("Rstate in Techpack " + (String) tppedencyT.getModel().getValueAt(i, 0) + "/"
            + (String) tppedencyT.getModel().getValueAt(i, 1) + " Not correct");
      }
      v2.add((String) tppedencyT.getModel().getValueAt(i, 0));
    }

    // check vendor release duplicates
    Vector<String> v3 = new Vector<String>();
    boolean venrelduplicate = false;
    for (int i = 0; i < venrelT.getModel().getRowCount(); i++) {
      if (v3.contains(venrelT.getModel().getValueAt(i, 0))) {
        licduplicate = true;
        messages.add("Vendor realease " + venrelT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      v3.add((String) venrelT.getModel().getValueAt(i, 0));
    }

    // check universe duplicates
    Vector<String> v4 = new Vector<String>();
    boolean unvduplicate = false;
    for (int i = 0; i < unvextT.getModel().getRowCount(); i++) {
      if (v4.contains(unvextT.getModel().getValueAt(i, 0))) {
        licduplicate = true;
        messages.add("Universe " + unvextT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      v4.add((String) unvextT.getModel().getValueAt(i, 0));
    }

    if (!tpdduplicate
        && !venrelduplicate
        && !unvduplicate
        && !licduplicate
        && rstateCorrect
        && (tableLicenseChanged || tableTPDChanged || tableVRChanged
            || !this.oldDescriptionA.equalsIgnoreCase(this.descriptionA.getText())
            || !(this.oldinstalldescrA == null || this.oldinstalldescrA.equalsIgnoreCase(this.installdescrA.getText())) || !this.oldrstateF
            .equalsIgnoreCase(this.rstateF.getText()))) {

      this.setSaveEnabled(true);
      errorMessageComponent.setValue(null);
      getParentAction("disableTabs").actionPerformed(null);
    } else {
      errorMessageComponent.setValue(messages);
      this.setSaveEnabled(false);
      getParentAction("enableTabs").actionPerformed(null);
    }

  }

  private class MyOtherDocumentListener implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      handleButtons();
    }

    public void insertUpdate(DocumentEvent e) {
      handleButtons();
    }

    public void removeUpdate(DocumentEvent e) {
      handleButtons();
    }
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  // ************************************************
  // Techpack Dependencies popup menu
  // ************************************************
  private JPopupMenu createTPDPopup(MouseEvent e) {
    JPopupMenu popupTPD;
    JMenuItem miTPD;
    popupTPD = new JPopupMenu();

    if ((typeCB.getSelectedItem().toString().equalsIgnoreCase("BASE"))) {
      return popupTPD;
    }

    if (e.getSource() instanceof JTable) {
      if (tppedencyT.getSelectedRow() > -1) {
        miTPD = new JMenuItem("Remove Techpack Dependency");
        miTPD.setAction(getAction("removetpd"));
        popupTPD.add(miTPD);
      }

      miTPD = new JMenuItem("Add Empty");
      miTPD.setAction(getAction("addemptytpd"));
      popupTPD.add(miTPD);

      miTPD = new JMenuItem("Add Techpack Dependency");
      miTPD.setAction(getAction("addtpd"));
      popupTPD.add(miTPD);

    } else {

      miTPD = new JMenuItem("Add Empty");
      miTPD.setAction(getAction("addemptytpd"));
      popupTPD.add(miTPD);

      miTPD = new JMenuItem("Add Techpack Dependency");
      miTPD.setAction(getAction("addtpd"));
      popupTPD.add(miTPD);

    }

    popupTPD.setOpaque(true);
    popupTPD.setLightWeightPopupEnabled(true);

    return popupTPD;

  }

  private void displayTPDMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createTPDPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private class TableTPDSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableTPDChanged = true;
      handleButtons();

      tppedencyT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));

      tppedencyT.getColumnModel().getColumn(1).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayTPDMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayTPDMenu(e);
    }

    public void mouseReleased(MouseEvent e) {

      displayTPDMenu(e);
    }
  }

  private class TableLicenseSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableLicenseChanged = true;
      handleButtons();
      licenceT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Versioning.getLicensenameColumnSize(), false)));

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayLicenseMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayLicenseMenu(e);
    }

    public void mouseReleased(MouseEvent e) {

      displayLicenseMenu(e);
    }
  }

  private void displayLicenseMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createLicensePopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  // ************************************************
  // License name table popup menu
  // ************************************************
  private JPopupMenu createLicensePopup(MouseEvent e) {

    JPopupMenu popupUnvExt;
    JMenuItem miUnvExt;
    popupUnvExt = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (licenceT.getSelectedRow() > -1) {
        miUnvExt = new JMenuItem("Remove License");
        miUnvExt.setAction(getAction("removeli"));
        popupUnvExt.add(miUnvExt);
      }

      miUnvExt = new JMenuItem("Add License");
      miUnvExt.setAction(getAction("addemptyli"));
      popupUnvExt.add(miUnvExt);

    } else {

      miUnvExt = new JMenuItem("Add License");
      miUnvExt.setAction(getAction("addemptyli"));
      popupUnvExt.add(miUnvExt);

    }

    popupUnvExt.setOpaque(true);
    popupUnvExt.setLightWeightPopupEnabled(true);

    return popupUnvExt;
  }

  private void checkRstate() {

    boolean saveIsEnabled = isSaveEnabled();

    // RState check
    boolean rstateCorrect = false;
    rstateCorrect = Utils.patternMatch(rstateF.getText(), Constants.RSTATEPATTERN);

    if (!rstateCorrect) {
      rstateF.setNeutralBg(Color.PINK);
      saveIsEnabled = false;
    } else {
      rstateF.setNeutralBg(Color.WHITE);
    }

    this.setSaveEnabled(saveIsEnabled);
  }

  
}
