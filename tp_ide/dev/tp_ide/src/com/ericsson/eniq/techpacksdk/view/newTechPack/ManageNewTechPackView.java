package com.ericsson.eniq.techpacksdk.view.newTechPack;

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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
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
import com.ericsson.eniq.component.GenericActionNode;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.User;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel.LicenseTableModel;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel.TPDepedencyTableModel;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel.UETableModel;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel.VRTableModel;

public class ManageNewTechPackView extends JPanel {

  /**
     * 
     */
  private static final long serialVersionUID = -5556558904903405680L;

  DataModelController dataModelController;

  private boolean createEnabled = false;

  // private boolean tableTPChanged = false;
  //
  // private boolean tableUEChanged = false;
  //
  // private boolean tableVRChanged = false;
  //
  // private boolean tableTPDChanged = false;

  String baseTP[] = null;

  String name, version, product, rstate, unvname, unvext, description, basedefinition, type, deptechpackversion,
      installdescription, vendorrelease, fromtechpack, username;

  RockFactory rock;

  private String oldName;

  private String oldVersion;

  private final JTextField setsFromF;

  private static final Logger logger = Logger.getLogger(ManageNewTechPackView.class.getName());

  private SingleFrameApplication application;

  private DataTreeNode selectedNode;

  private final GenericActionTree tpTree;

  public static final Color ERROR_BG = Color.PINK;

  public static final Color WHITE = Color.WHITE;

  private final static Color neutralBg = null;

  private final Vector<String> nobasetypeslist = new Vector<String>();

  boolean editable = true;

  NewTechPackFunctionality NewTechPack;

  JTable tppedencyT = new JTable();

  JTable tpT = new JTable();

  JTable venrelT = new JTable();

  JTable unvextT = new JTable();

  JTable licenceT = new JTable();

  JScrollPane scrollPane;

  LimitedSizeTextField nameF, versionF, productF, rstateF, unvnameF;

  JComboBox typeCB;

  JComboBox baseCB;

  JTextArea descriptionA = new JTextArea();

  JTextArea installdescrA = new JTextArea();

  Versioning versioning;

  Universename universename;

  String tmpVersionid = "";

  String noParenthesisVersionid = "";

  JButton close;

  JButton create;

  JFrame frame;

  JScrollPane tpScrollPane;

  private boolean baseTPEnabled = true;

  private final ErrorMessageComponent errorMessageComponent;

  private boolean ignoreButtonHandler = false;

  private boolean iteratingTree = false;

  private final Versioning ver;
  
  /**
   * Test constructor.
   * @param tmpVersionid
   * @param productTextField 
   * @param universeNameField
   * 
   */
  protected ManageNewTechPackView(final String tmpVersionid, final LimitedSizeTextField versionF, final LimitedSizeTextField rStateTextField,
      final LimitedSizeTextField nameTextField, final LimitedSizeTextField universeNameField, final LimitedSizeTextField productTextField, final JComboBox typeComboBox) {
    this.tmpVersionid = tmpVersionid;
    this.rstateF = rStateTextField;
    this.versionF = versionF;
    this.nameF = nameTextField;
    this.errorMessageComponent = null ;
    this.typeCB = typeComboBox;    
    this.unvnameF = universeNameField;
    this.productF = productTextField;
    this.ver = null ;
    this.tpTree = null ;
    this.setsFromF = null ;
  }

  public ManageNewTechPackView(final Versioning ver, final SingleFrameApplication application,
      final DataModelController dataModelController, final JFrame frame) {
    super(new GridBagLayout());

    this.ver = ver;

    this.rock = dataModelController.getRockFactory();

    this.frame = frame;

    this.dataModelController = dataModelController;

    this.application = application;

    final ComboBoxModel baseModel = new DefaultComboBoxModel(dataModelController.getNewTechPackDataModel()
        .getBaseTechPacks());
    baseCB = new JComboBox();
    baseCB.setModel(baseModel);

    // Create a list of techpack types for which base techpack cannot be
    // defined.
    for (int i = 0; i < Constants.TYPES_NOBASE.length; i++) {
      nobasetypeslist.add(Constants.TYPES_NOBASE[i]);
    }

    // ************** Text panel **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    final int fieldSize = 25;
    final int maxwidth = 279;
    final Dimension fieldDim = new Dimension(maxwidth, 25);
    final Dimension areaDim = new Dimension(maxwidth, 85);
    final Dimension tableDim = new Dimension(maxwidth, 85);

    final JPanel txtPanel = new JPanel(new GridBagLayout());
    final JScrollPane txtPanelS = new JScrollPane(txtPanel);

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // name

    final JLabel name = new JLabel("Name");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(name, c);

    nameF = new LimitedSizeTextField(fieldSize, Versioning.getTechpack_nameColumnSize(), true);
    nameF.setText(dataModelController.getNewTechPackDataModel().getTechpack_name().trim());
    nameF.getDocument().addDocumentListener(new MyOtherDocumentListener());
    nameF.setPreferredSize(fieldDim);
    nameF.setName("Name");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    txtPanel.add(nameF, c);

    // version

    final JLabel version = new JLabel("Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    txtPanel.add(version, c);

    final int maxVersionlenght = Versioning.getVersionidColumnSize() - Versioning.getTechpack_nameColumnSize() - 5;
    // 5 = reduce :(())

    versionF = new LimitedSizeTextField(fieldSize, maxVersionlenght, true);

    String tmpVersionid = dataModelController.getNewTechPackDataModel().getVersionid().trim();
    if (tmpVersionid != "") {
      String removeStr = dataModelController.getNewTechPackDataModel().getTechpack_name();
      removeStr += ":";

      tmpVersionid.replaceFirst(removeStr, "");

      if (tmpVersionid.startsWith("((")) {
        tmpVersionid = tmpVersionid.substring(2);
        tmpVersionid = tmpVersionid.replace("))", "");
      }
    }
    versionF.getDocument().addDocumentListener(new MyOtherDocumentListener());
    versionF.setText(tmpVersionid);
    versionF.setPreferredSize(fieldDim);
    versionF.setName("Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    txtPanel.add(versionF, c);

    // product

    final JLabel product = new JLabel("Product Number");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(product, c);

    productF = new LimitedSizeTextField(fieldSize, Versioning.getProduct_numberColumnSize(), true);
    productF.setText(dataModelController.getNewTechPackDataModel().getProduct_number());
    productF.getDocument().addDocumentListener(new MyDocumentListener());
    productF.setPreferredSize(fieldDim);
    productF.setName("Product");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    txtPanel.add(productF, c);

    // unvname

    final JLabel unvname = new JLabel("Universe name");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;

    txtPanel.add(unvname, c);

    unvnameF = new LimitedSizeTextField(LimitedSizeTextField.WINDOWSFILECHARS_STRING, "", fieldSize, Universename
        .getUniversenameColumnSize(), true);
    unvnameF.setText(dataModelController.getNewTechPackDataModel().getUniversename(
        dataModelController.getNewTechPackDataModel().getVersionid()));
    unvnameF.getDocument().addDocumentListener(new MyDocumentListener());
    unvnameF.setPreferredSize(fieldDim);
    unvnameF.setName("UniverseName");
    unvnameF.setNeutralBg(ERROR_BG);
    unvnameF.setBackground(ERROR_BG);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 6;
    c.weightx = 1;
    txtPanel.add(unvnameF, c);

    // R-State

    final JLabel rstate = new JLabel("R-State");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    txtPanel.add(rstate, c);

    rstateF = new LimitedSizeTextField(fieldSize, Versioning.getTechpack_versionColumnSize(), true);
    rstateF.setText(dataModelController.getNewTechPackDataModel().getTechpack_version());
    rstateF.getDocument().addDocumentListener(new MyDocumentListener());
    rstateF.setPreferredSize(fieldDim);
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
     * licenseF.setText(dataModelController
     * .getNewTechPackDataModel().getLicense_Name());
     * licenseF.setEnabled(editable);
     * licenseF.getDocument().addDocumentListener(new MyDocumentListener());
     * licenseF.setPreferredSize(fieldDim); licenseF.setEditable(true); c.fill =
     * GridBagConstraints.NONE; c.gridx = 1; c.gridy = 4; c.weightx = 1;
     * txtPanel.add(licenseF, c);
     */

    dataModelController.getNewTechPackDataModel().refresh();

    final JLabel license = new JLabel("License Names");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    txtPanel.add(license, c);

    licenceT = new JTable();
    final LicenseTableModel litm = dataModelController.getNewTechPackDataModel().getLicenseTableModel();
    litm.addTableModelListener(new TableLicenseSelectionListener());
    licenceT.setModel(litm);
    licenceT.setBackground(WHITE);
    licenceT.setEnabled(editable);

    if (editable) {
      licenceT.addMouseListener(new TableLicenseSelectionListener());
      licenceT.getTableHeader().addMouseListener(new TableLicenseSelectionListener());
    }

    licenceT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Versioning.getLicensenameColumnSize(), false)));
    licenceT.setToolTipText("");
    licenceT.setName("License");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 4;
    c.weightx = 0;
    final JScrollPane licenseScroll = new JScrollPane(licenceT);
    licenseScroll.setPreferredSize(tableDim);
    txtPanel.add(licenseScroll, c);

    // type

    final JLabel type = new JLabel("Type");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    txtPanel.add(type, c);

    if (dataModelController.getUser().authorize(User.DO_PRODUCTTP)) {
      typeCB = new JComboBox(Utils.getAllSupportedTechpacks());
      typeCB.setSelectedItem(dataModelController.getNewTechPackDataModel().getTechpack_type());

      if (dataModelController.getNewTechPackDataModel().getBaseTechPacks().length == 0) {
        typeCB = new JComboBox(Constants.TYPES_NOBASE);
        baseTPEnabled = false;
      } else if (typeCB.getSelectedItem().toString().equals(Constants.BASE_TECHPACK)) {
        baseTPEnabled = false;
      } else {
        typeCB = new JComboBox(Utils.getAllSupportedTechpacks());
      }
    } else {
      typeCB = new JComboBox(Constants.CUSTOMTECHPACKTYPES);
      typeCB.setSelectedIndex(0);
      // Product number is not mandatory for custom techpacks.
      productF.setBackground(WHITE);
      unvnameF.setBackground(WHITE);

    }

    typeCB.setBackground(WHITE);
    typeCB.setEditable(false);
    typeCB.addMouseListener(new TableTypeSelectionListener());
    typeCB.addActionListener(new TableTypeSelectionListener());

    typeCB.setPreferredSize(fieldDim);
    typeCB.setName("Type");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 5;
    c.weightx = 1;
    txtPanel.add(typeCB, c);

    // unvext

    final JLabel unvext = new JLabel("Universe extension");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;
    txtPanel.add(unvext, c);

    unvextT = new JTable();
    final UETableModel uetm = dataModelController.getNewTechPackDataModel().getUnvExtTableModel();
    uetm.addTableModelListener(new TableUnvExtSelectionListener());
    unvextT.setModel(uetm);
    unvextT.setBackground(WHITE);
    unvextT.setEnabled(editable);
    // unvextT.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Commented out, as
    // cols really small when doing New... when existing data in table.
    if (editable) {
      unvextT.addMouseListener(new TableUnvExtSelectionListener());
      unvextT.getTableHeader().addMouseListener(new TableUnvExtSelectionListener());
    }
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
    final JScrollPane unvextscroll = new JScrollPane(unvextT);
    unvextscroll.setPreferredSize(tableDim);
    txtPanel.add(unvextscroll, c);

    // description

    final JLabel description = new JLabel("Description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 8;
    c.weightx = 0;
    txtPanel.add(description, c);

    descriptionA = new LimitedSizeTextArea(Versioning.getDescriptionColumnSize(), false);
    descriptionA.setText(dataModelController.getNewTechPackDataModel().getDescription().trim());
    descriptionA.getDocument().addDocumentListener(new MyDocumentListener());
    descriptionA.setName("Description");
    final JScrollPane descriptionP = new JScrollPane(descriptionA);
    descriptionP.setPreferredSize(areaDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 8;
    c.weightx = 1;
    txtPanel.add(descriptionP, c);

    // SetFrom

    final JLabel setsFrom = new JLabel("Sets From");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 9;
    c.weightx = 0;
    txtPanel.add(setsFrom, c);

    final JPanel jp = new JPanel();

    setsFromF = new JTextField("");
    setsFromF.setPreferredSize(fieldDim);
    setsFromF.setEditable(false);
    setsFromF.setName("SetsFrom");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 9;
    c.weightx = 1;
    jp.add(setsFromF, c);

    final JButton b = new JButton("Clear");
    b.addActionListener(new MyButtonActionListener());
    b.setName("Clear");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 2;
    c.gridy = 9;
    c.weightx = 1;
    jp.add(b, c);

    c.gridx = 1;
    c.gridy = 9;
    c.weightx = 1;
    txtPanel.add(jp, c);

    // venrel

    final JLabel venrel = new JLabel("Vendor release");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 10;
    c.weightx = 0;
    txtPanel.add(venrel, c);

    venrelT = new JTable();
    final VRTableModel vrtm = dataModelController.getNewTechPackDataModel().getVRTableModel();
    vrtm.addTableModelListener(new TableVRSelectionListener());
    venrelT.setModel(vrtm);
    venrelT.setBackground(WHITE);
    venrelT.setEnabled(editable);
    if (editable) {
      venrelT.addMouseListener(new TableVRSelectionListener());
      venrelT.getTableHeader().addMouseListener(new TableVRSelectionListener());
    }
    venrelT.getColumnModel().getColumn(0)
        .setCellEditor(
            new DefaultCellEditor(new LimitedSizeTextField(25, Supportedvendorrelease.getVendorreleaseColumnSize(),
                false)));
    venrelT.setToolTipText("");
    venrelT.setName("VendorRelease");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 10;
    c.weightx = 0;
    final JScrollPane scroll = new JScrollPane(venrelT);
    scroll.setPreferredSize(tableDim);
    txtPanel.add(scroll, c);

    // basedef

    final JLabel basedef = new JLabel("Base definition");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 11;
    c.weightx = 0;
    txtPanel.add(basedef, c);

    if (baseTPEnabled) {
      baseTP = (dataModelController.getNewTechPackDataModel().getBaseTechPacks());
      baseCB = new JComboBox(baseTP);
      baseCB.setSelectedIndex(baseCB.getItemCount() - 1);
      baseCB.setBackground(WHITE);
    } else {
      final String empty[] = { "" };
      baseCB = new JComboBox(empty);
      baseCB.setEditable(false);
      baseCB.setBackground(neutralBg);
    }

    baseCB.setPreferredSize(fieldDim);
    baseCB.setName("BaseDefinition");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 11;
    c.weightx = 1;
    txtPanel.add(baseCB, c);

    // TechPack Depedency

    final JLabel tppedency = new JLabel("TechPack Dependencies");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 12;
    c.weightx = 0;
    txtPanel.add(tppedency, c);

    tppedencyT = new JTable();
    final TPDepedencyTableModel tpdtm = dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel();
    tpdtm.addTableModelListener(new TableTPDSelectionListener());
    tppedencyT.setModel(tpdtm);
    tppedencyT.setBackground(WHITE);
    tppedencyT.setEnabled(editable);
    if (editable) {
      tppedencyT.addMouseListener(new TableTPDSelectionListener());
      tppedencyT.getTableHeader().addMouseListener(new TableTPDSelectionListener());
    }
    tppedencyT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));
    tppedencyT.getColumnModel().getColumn(1).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));
    tppedencyT.setToolTipText("");
    tppedencyT.setName("TechPackDependency");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 12;
    c.weightx = 0;
    final JScrollPane scrollPane = new JScrollPane(tppedencyT);
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // installdescr
    final JLabel installdescr = new JLabel("Install description");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 13;
    c.weightx = 0;
    txtPanel.add(installdescr, c);
    installdescrA = new LimitedSizeTextArea(Versioning.getInstalldescriptionColumnSize(), false);
    installdescrA.setText(dataModelController.getNewTechPackDataModel().getInstalldescription());
    installdescrA.getDocument().addDocumentListener(new MyDocumentListener());
    installdescrA.setName("InstallDescription");
    final JScrollPane installdescrP = new JScrollPane(installdescrA);
    installdescrP.setPreferredSize(areaDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 13;
    c.weightx = 1;
    txtPanel.add(installdescrP, c);

    // ************** buttons **********************

    close = new JButton("Close");
    close.setName("CloseCreateNewTechpack");
    close.setActionCommand("close");
    close.setAction(getAction("close"));
    close.setToolTipText("Close");
    close.setName("Close");

    final JButton create = new JButton("Create");
    create.setName("CreateNewTechpack");
    create.setAction(getAction("create"));
    create.setActionCommand("create");
    create.setToolTipText("Create");
    create.setEnabled(false);

    final JButton cancel = new JButton("Cancel");

    cancel.setName("CancelNewTechpack");
    // cancel = new JButton("Discard");
    cancel.setActionCommand("discard");
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Cancel");

    // ************** TPTree panel **********************

    tpTree = new GenericActionTree(dataModelController.getTechPackTreeDataModel());

    tpTree.setCellRenderer(new NewTechPackRenderer(baseTPEnabled, dataModelController));
    tpTree.addTreeSelectionListener(new SelectionListener());
    tpTree.setToolTipText("");
    tpTree.setName("NewTechPackTree");

    // if there is techpack selected we will select the same techpack here
    // also...
    if (this.ver != null) {

      iteratingTree = true;
      for (int i = 1; i < tpTree.getRowCount(); i++) {
        tpTree.expandRow(i);
      }
      TreePath selected = null;
      for (int i = 1; i < tpTree.getRowCount(); i++) {
        tpTree.setSelectionRow(i);
        final GenericActionNode current = tpTree.getSelected();
        if (current instanceof DataTreeNode) {
          final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
          if (((Versioning) node.getRockDBObject()).getVersionid().equals(this.ver.getVersionid())) {
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

    tpScrollPane = new JScrollPane(tpTree);

    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(cancel);
    buttonPanel.add(create);
    buttonPanel.add(close);

    // ************** TP chooser panel**********************

    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(tpScrollPane);

    // *** right & left panels, left panel contains tab ****

    final JPanel lpanel = new JPanel(new BorderLayout());
    final JPanel rpanel = new JPanel(new BorderLayout());
    rpanel.add(panel);
    lpanel.add(txtPanelS);

    // *** Inner panel panel with right and left panel *****

    final JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    innerPanel.add(lpanel, BorderLayout.LINE_START);
    innerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    innerPanel.add(rpanel, BorderLayout.LINE_START);

    // *** Main panel with inner panel and button pane *****
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

  @Action(enabledProperty = "createEnabled")
  public Task<Void, Void> create() {

    final Task<Void, Void> CreateTask = new CreateTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    name = getTechPackName();
    
    version = versionF.getText().trim();
    version = version.toUpperCase();
    version = "((" + version + "))";

    product = productF.getText().trim();
    product = product.toUpperCase();

    type = (String) typeCB.getSelectedItem();

    unvname = unvnameF.getText().trim();
    unvname = unvname.toUpperCase();

    if (tpTree.isSelectionEmpty()) {
      fromtechpack = "";
    } else {
      if (tpTree.getSelected() != null) {
        fromtechpack = ((Versioning) ((DataTreeNode) tpTree.getSelected()).getRockDBObject()).getVersionid();
      } else {
        fromtechpack = "";
      }
    }
    final String tmpVersionid = name + ":" + version;

    if (name.equals("")) {
      JOptionPane.showMessageDialog(null, "Please enter Tech Pack name.");
      return null;
    } else if (version.equals("(())")) {
      JOptionPane.showMessageDialog(null, "Please enter Tech Pack version.");
      return null;
    } else if ((tmpVersionid == fromtechpack) && (fromtechpack == "null")) {
      JOptionPane.showMessageDialog(null, "Tech Pack already exist in system. Please rename Tech Pack.");
      return null;
    } else if (product.equals("") && !(typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CUSTOM_TECHPACK))) {
      JOptionPane.showMessageDialog(null, "Please enter Tech Pack product number.");
      return null;
    } else if (unvname.equals("")
        && (!(typeCB.getSelectedItem().toString().equalsIgnoreCase("BASE")
            || typeCB.getSelectedItem().toString().equalsIgnoreCase("Topology")
            || typeCB.getSelectedItem().toString().equalsIgnoreCase("EVENT")
            || typeCB.getSelectedItem().toString().equalsIgnoreCase("SYSTEM")
            || typeCB.getSelectedItem().toString().equalsIgnoreCase("CUSTOM") || typeCB.getSelectedItem().toString()
            .equalsIgnoreCase(Constants.ENIQ_EVENT)))) {
      JOptionPane.showMessageDialog(null, "Please enter Tech Pack universe name.");
      return null;
    } else if (!(typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK)
        || typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.SYSTEM_TECHPACK)
        || typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.TOPOLOGY_TECHPACK) || typeCB.getSelectedItem().toString()
        .equalsIgnoreCase(Constants.EVENT_TECHPACK))
        && baseCB.getSelectedItem().toString().equals("")) {
      JOptionPane.showMessageDialog(null, "Please enter Tech Pack Base definiton version.");
      return null;
    }

    frame.setGlassPane(busyIndicator);
    CreateTask.setInputBlocker(new BusyIndicatorInputBlocker(CreateTask, busyIndicator));

    return CreateTask;
  }

  /**
   * Get the tech pack name.
   */
  protected String getTechPackName() {
    return nameF.getText().trim();
  }

  private class CreateTask extends Task<Void, Void> {

    public CreateTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "Creating a new techpack.");

      List<Object> venrellist;
      List<String> tpdeplist;
      List<List> unvextlist;

      final String tmpversion = version;

      boolean success = true;
      try {

        // Disable autocommit
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

        // Get the username of the current user.
        username = dataModelController.getUserName();

        // Get the techpack R-State (trimmed and uppercase)
        rstate = rstateF.getText().trim().toUpperCase();

        // Get the licenses as one comma separated string.
        String licenseName = "";
        final LicenseTableModel lic = (LicenseTableModel) licenceT.getModel();
        final int tmprows1 = lic.getRowCount();
        for (int i = 0; i < tmprows1; i++) {
          if (i == 0) {
            licenseName += lic.getValueAt(i, 0);
          } else {
            licenseName += "," + lic.getValueAt(i, 0);
          }
        }

        // Get a list of the universe names and extension.
        final UETableModel mtmUnvExt = (UETableModel) unvextT.getModel();
        unvextlist = new Vector<List>();
        final int tmprows = mtmUnvExt.getRowCount();
        for (int i = 0; i < tmprows; i++) {
          // (Universe Extension, Universe Extension Name) E.g. "a","Standard"
          final List<String> unvextlistrow = new Vector<String>();
          unvextlistrow.add(0, Utils.replaceNull((String) mtmUnvExt.getValueAt(i, 0)));
          unvextlistrow.add(1, Utils.replaceNull((String) mtmUnvExt.getValueAt(i, 1)));
          unvextlist.add(unvextlistrow);
        }

        // Get the description (trimmed)
        description = descriptionA.getText().trim();

        // Get a list of vendor releases.
        final VRTableModel mtmVenRel = (VRTableModel) venrelT.getModel();
        venrellist = new Vector<Object>();
        final int rows = mtmVenRel.getRowCount();
        for (int v = 0; v < rows; v++) {
          venrellist.add(mtmVenRel.getValueAt(v, 0));
        }

        // Set the selected base definition.
        // NOTE: Will not be set for techpacks without base.
        if (!nobasetypeslist.contains(type)) {
          basedefinition = baseCB.getSelectedItem().toString();
        }

        // Get a list of techpack dependencies.
        final TPDepedencyTableModel mtmTPD = dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel();
        tpdeplist = new Vector<String>();

        if (mtmTPD != null) {
          final int rowCount = mtmTPD.getRowCount();
          for (int r = 0; r < rowCount; r++) {
            tpdeplist.add((String) dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().getValueAt(
                r, 0));
            tpdeplist.add((String) dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().getValueAt(
                r, 1));
          }
        }

        // Get the install description (trimmed)
        installdescription = installdescrA.getText().trim();

        // Create a new techpack info object
        final NewTechPackInfo techpack = new NewTechPackInfo(name, version, product, rstate, licenseName, type,
            unvname, unvextlist, description, venrellist, basedefinition, tpdeplist, installdescription, fromtechpack,
            username);
      //Validation for invalid characters in Techpack name for EQEV-3659 (start)
        if(techpack.getName().contains("%"))        
        {
        	String errMessage = techpack.getName() +":  "
                    +" '%' character is not allowed."; 
                    
                throw new Exception(errMessage);
        }
        if(techpack.getName().contains("."))        
        {
        	String errMessage = techpack.getName() +":  "
                    +" '.' character is not allowed."; 
                    
                throw new Exception(errMessage);
        }
        if(techpack.getName().contains("-"))        
        {
        	String errMessage = techpack.getName() +":  "
                    +" '-' character is not allowed."; 
                    
                throw new Exception(errMessage);
        }
        if(techpack.getName().contains(" "))        
        {
        	String errMessage = techpack.getName() +":  "
                    +" Space is not allowed in Techpack Name."; 
                    
                throw new Exception(errMessage);
        }
      //Validation for invalid characters in Techpack name for EQEV-3659 (end)
        

        // Create the new techpack information to the database.
        NewTechPack.createTechPack(techpack, dataModelController.getRockFactory());

        // Copy the sets from the old techpack, if needed.
        if (setsFromF.getText().length() > 0) {
        	dataModelController.getETLSetHandlingDataModel().copySets(oldName, oldVersion, name, tmpversion, type, dataModelController.getRockFactory());
        }

        // Regenerate the busy hour aggregations and aggregation rules, because
        // there might be invalid information in the copied aggregations due to
        // busy hour improvements.
        final String newVersionId = techpack.getName() + ":" + techpack.getVersion();
        dataModelController.getBusyhourHandlingDataModel().regenerateBhAggregationsForTechpack(newVersionId);
      
        
        // Commit the changes to the database.
        dataModelController.getRockFactory().getConnection().commit();
        dataModelController.getEtlRockFactory().getConnection().commit();

        // tableVRChanged = false;
        // tableTPDChanged = false;
        // tableUEChanged = false;
        setCreateEnabled(false);

      } catch (final Exception e) {
    	//Validation for invalid characters in Techpack name for EQEV-3659 (start)
          JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
        	        "save.error.caption"), JOptionPane.ERROR_MESSAGE); 
        //Validation for invalid characters in Techpack name for EQEV-3659 (end)
        try {
          success = false;
          dataModelController.getRockFactory().getConnection().rollback();
          dataModelController.getEtlRockFactory().getConnection().rollback();
        
        } catch (final Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {
        if (success) {
          logger.log(Level.INFO, "Create finished.");
          frame.dispose();
        } else {
          logger.log(Level.SEVERE, "Create failed!");
          JOptionPane.showMessageDialog(null, "Creation of new techpack failed.");
        }

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
          dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);
        } catch (final Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
      return null;
    }
  }

  /**
   * DiscardTask action
   * 
   * @return
   */
  @Action
  public void discard() {

    ignoreButtonHandler = true;

    logger.info("Cancel New Tech Pack");

    try {

      tpTree.setSelectionRow(0);

      nameF.setText("");

      versionF.setText("");
      versionF.setBackground(ERROR_BG);
      versionF.getDocument().addDocumentListener(new MyOtherDocumentListener());

      productF.setText("");
      rstateF.setText("");

      errorMessageComponent.setValue(null);

      unvnameF.setText("");
      if (typeCB.getSelectedItem() == Constants.CUSTOM_TECHPACK) {
        productF.setBackground(WHITE);
        unvnameF.setBackground(WHITE);
      }

      if (typeCB.getSelectedItem() == Constants.PM_TECHPACK || typeCB.getSelectedItem() == Constants.CM_TECHPACK) {
        unvnameF.setBackground(ERROR_BG);
      }

      descriptionA.setText("");
      installdescrA.setText("");
      setsFromF.setText("");

      dataModelController.getNewTechPackDataModel().refresh();

      final UETableModel ue = dataModelController.getNewTechPackDataModel().getUnvExtTableModel();
      ue.addTableModelListener(new TableUnvExtSelectionListener());
      unvextT.setModel(ue);

      final VRTableModel vr = dataModelController.getNewTechPackDataModel().getVRTableModel();
      vr.addTableModelListener(new TableVRSelectionListener());
      venrelT.setModel(vr);

      final LicenseTableModel lic = dataModelController.getNewTechPackDataModel().getLicenseTableModel();
      lic.addTableModelListener(new TableLicenseSelectionListener());
      licenceT.setModel(lic);

      final TPDepedencyTableModel tpd = dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel();
      tpd.addTableModelListener(new TableTPDSelectionListener());
      tppedencyT.setModel(tpd);

      // tableUEChanged = false;
      // tableVRChanged = false;
      // tableTPDChanged = false;

      setSaveEnabled(false);
      frame.repaint();

    } finally {
    }

    ignoreButtonHandler = false;
  }

  @Action
  public void close() {
	  
	  if (tpTree.getSelected() != null){
      // EQEV-2862 - Starts
      try {
            if (tpTree.getSelected() instanceof DataTreeNode) {
                  final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
                  final Versioning v = (Versioning) node.getRockDBObject();
                  v.setLockedby("");
                  v.setLockdate(null);
                  dataModelController.getTechPackTreeDataModel().modObj(v);
            }
            dataModelController.getTechPackTreeDataModel().refresh();
      } catch (final Exception e) {
            logger.log(Level.SEVERE, "Error unlocking Tech Pack", e);
      }
      // EQEV-2862 - End

    logger.log(Level.INFO, "New Tech Pack close");
    frame.dispose();
	  }
  }

  // Vendor release mouse menu
  @Action
  public void addemptyvr() {
    logger.fine("ADD " + dataModelController.getNewTechPackDataModel().getVRTableModel().getRowCount());
    dataModelController.getNewTechPackDataModel().getVRTableModel().addEmptyNewRow();
    dataModelController.getNewTechPackDataModel().getVRTableModel().fireTableDataChanged();
  }

  @Action
  public void removevr() {
    logger.log(Level.INFO, "remove");
    dataModelController.getNewTechPackDataModel().getVRTableModel().removeRow(venrelT.getSelectedRow());
    dataModelController.getNewTechPackDataModel().getVRTableModel().fireTableDataChanged();
  }

  // Tech Pack Dependencies mouse menu
  @Action
  public void addemptytpd() {
    logger.fine("ADD " + dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().getRowCount());
    dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().addEmptyNewRow();
    dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().fireTableDataChanged();
  }

  @Action
  public void addtpd() {
    logger.log(Level.INFO, "addtpd");

    final String s = (String) JOptionPane.showInputDialog(this, "Select Tech pack", "TechPacks",
        JOptionPane.PLAIN_MESSAGE, null, dataModelController.getNewTechPackDataModel().getTechPacks(), "");

    if ((s != null) && (s.length() > 0)) {

      final String split[] = s.split(":");

      final Techpackdependency tpd = new Techpackdependency(dataModelController.getRockFactory());
      tpd.setTechpackname(split[0]);
      tpd.setVersion(split[1]);
      dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().addNewRow(tpd);
      dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().fireTableDataChanged();
    }
  }

  @Action
  public void removetpd() {

    logger.log(Level.INFO, "remove");
    dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().removeRow(tppedencyT.getSelectedRow());
    dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel().fireTableDataChanged();
  }

  // Universe Extension mouse menu
  @Action
  public void addemptyue() {
    logger.fine("ADD " + dataModelController.getNewTechPackDataModel().getUnvExtTableModel().getRowCount());
    dataModelController.getNewTechPackDataModel().getUnvExtTableModel().addEmptyNewRow();
    dataModelController.getNewTechPackDataModel().getUnvExtTableModel().fireTableDataChanged();
  }

  @Action
  public void removeue() {

    logger.log(Level.INFO, "remove");
    dataModelController.getNewTechPackDataModel().getUnvExtTableModel().removeRow(unvextT.getSelectedRow());
    dataModelController.getNewTechPackDataModel().getUnvExtTableModel().fireTableDataChanged();
  }

  // License name mouse menu
  @Action
  public void addemptyli() {
    logger.fine("ADD " + dataModelController.getNewTechPackDataModel().getLicenseTableModel().getRowCount());
    dataModelController.getNewTechPackDataModel().getLicenseTableModel().addEmptyNewRow();
    dataModelController.getNewTechPackDataModel().getLicenseTableModel().fireTableDataChanged();
  }

  @Action
  public void removeli() {

    logger.log(Level.INFO, "remove");
    dataModelController.getNewTechPackDataModel().getLicenseTableModel().removeRow(licenceT.getSelectedRow());
    dataModelController.getNewTechPackDataModel().getLicenseTableModel().fireTableDataChanged();
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  private class SelectionListener implements TreeSelectionListener {

    @Override
    public void valueChanged(final TreeSelectionEvent e) {

      if (iteratingTree) {

        final TreePath t = e.getPath();
        final Object pointed = t.getLastPathComponent();
        if (pointed instanceof DefaultMutableTreeNode) {
          final DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
          final Object tmp = node.getUserObject();
          if (tmp instanceof DataTreeNode) {
            setSelectedNode((DataTreeNode) tmp);
          }
        }
        return;
      }

      rock = dataModelController.getNewTechPackDataModel().getRockFactory();

      // boolean error = false;
      // int fieldSize = 25;

      licenceT.removeAll();
      venrelT.removeAll();
      unvextT.removeAll();
      tppedencyT.removeAll();

      final TreePath t = e.getPath();
      final Object pointed = t.getLastPathComponent();
      if (pointed instanceof DefaultMutableTreeNode) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
        final Object tmp = node.getUserObject();

        if (tmp instanceof DataTreeNode) {

          final Versioning v = (Versioning) ((DataTreeNode) tmp).getRockDBObject();

          if (((DataTreeNode) tmp).locked != null
              && (((DataTreeNode) tmp).locked.length() > 0 && !((DataTreeNode) tmp).locked.equals(dataModelController
                  .getUserName()))) {
            return;
          }

          if (v.getEniq_level() == null || v.getEniq_level().equalsIgnoreCase("1.0")) {
            return;
          }

          // If there is no base techpack defined, check if the current techpack
          // type is allowed to be used (without a base techpack).
          if (!baseTPEnabled && !nobasetypeslist.contains(v.getTechpack_type())) {
            return;
          }

          setSelectedNode((DataTreeNode) tmp);
          dataModelController.getNewTechPackDataModel().refresh((Versioning) ((DataTreeNode) tmp).getRockDBObject());

          tpT.setModel(dataModelController.getNewTechPackDataModel().getTPTableModel());

          nameF.setText(((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getTechpack_name());
          nameF.getDocument().addDocumentListener(new MyOtherDocumentListener());

          tmpVersionid = ((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getVersionid();

          boolean parenthesisRemoved = false;

          if (tmpVersionid != "") {
            String removeStr = (((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getTechpack_name());
            removeStr += ":";

            tmpVersionid = tmpVersionid.replaceFirst(removeStr, "");

            if (tmpVersionid.startsWith("((")) { // REMOVE THIS IF WHEN ALL TP
              // ARE :((versio))
              tmpVersionid = tmpVersionid.substring(2);
              tmpVersionid = tmpVersionid.replace("))", "");
              parenthesisRemoved = true;
            }
          }
          versionF.setText(tmpVersionid);
          versionF.getDocument().addDocumentListener(new MyOtherDocumentListener());

          if (tmpVersionid == ((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getVersionid()) {
            if (parenthesisRemoved) {
              versionF.setNeutralBg(neutralBg);
            } else {
              versionF.setNeutralBg(ERROR_BG);
            }
          } else {
            versionF.setNeutralBg(ERROR_BG);
          }

          productF.setText(((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getProduct_number());
          rstateF.setText(((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getTechpack_version());

          final LicenseTableModel lic = dataModelController.getNewTechPackDataModel().getLicenseTableModel();
          lic.addTableModelListener(new TableLicenseSelectionListener());
          licenceT.setModel(lic);

          licenceT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Supportedvendorrelease.getVendorreleaseColumnSize(),
                  false)));

          // 20110615 eanguan :: For restricting the user to Copy Evnets TP to Stats or Vice versa
          final String tpType =  ((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getTechpack_type();
          
          if(tpType.equals("ENIQ_EVENT")){
        	  boolean isEvent = false ;
        	  for(int index =0; index < typeCB.getItemCount(); index++){
        		  if(((String)typeCB.getItemAt(index)).equals("ENIQ_EVENT")){
        			  isEvent = true;
        			  break;
        		  }
        	  }
        	  if(!isEvent){
        		  typeCB.addItem("ENIQ_EVENT");
        	  }
        	  typeCB.setSelectedItem("ENIQ_EVENT");
        	  typeCB.setEnabled(false);
          }else{
        	  typeCB.setSelectedItem(tpType);
        	  typeCB.removeItem("ENIQ_EVENT");
        	  typeCB.setEnabled(true);     	  
          }
          typeCB.revalidate();          

          if (dataModelController.getNewTechPackDataModel().getTechpack_type() != typeCB.getSelectedItem()) {
            handleButtons();
          }

          descriptionA.setText(((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getDescription());

          final VRTableModel vrtm = dataModelController.getNewTechPackDataModel().getVRTableModel();
          vrtm.addTableModelListener(new TableVRSelectionListener());
          venrelT.setModel(vrtm);

          venrelT.getColumnModel().getColumn(0).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Supportedvendorrelease.getVendorreleaseColumnSize(),
                  false)));

          final UETableModel ue = dataModelController.getNewTechPackDataModel().getUnvExtTableModel();
          ue.addTableModelListener(new TableUnvExtSelectionListener());
          unvextT.setModel(ue);

          unvextT.getColumnModel().getColumn(0)
              .setCellEditor(
                  new DefaultCellEditor(new LimitedSizeTextField(25, Universename.getUniverseextensionColumnSize(),
                      false)));
          unvextT.getColumnModel().getColumn(1).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Universename.getUniverseextensionnameColumnSize(),
                  false)));

          final TPDepedencyTableModel tpd = dataModelController.getNewTechPackDataModel().getTPDepedencyTableModel();
          tpd.addTableModelListener(new TableTPDSelectionListener());
          tppedencyT.setModel(tpd);

          tppedencyT.getColumnModel().getColumn(0)
              .setCellEditor(
                  new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(),
                      false)));

          tppedencyT.getColumnModel().getColumn(1).setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

          if ((typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK))) {

            unvnameF.setText("");
            unvnameF.setEnabled(false);
            unvnameF.setNeutralBg(neutralBg);
            typeCB.setBackground(WHITE);
            unvextT.setEnabled(false);
            tppedencyT.setEnabled(false);
            licenceT.setEnabled(false);

          } else if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.TOPOLOGY_TECHPACK)) {
            unvnameF.setText("");
            unvnameF.setEnabled(false);
            unvnameF.setNeutralBg(neutralBg);
            typeCB.setBackground(WHITE);
            unvextT.setEnabled(false);
            venrelT.setEnabled(false);
            tppedencyT.setEnabled(true);
            licenceT.setEnabled(true);

          } else {
            licenceT.setEnabled(true);
            unvnameF.setEnabled(true);
            unvnameF.setText(dataModelController.getNewTechPackDataModel().getUniversename(
                ((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getVersionid()));

            unvnameF.setNeutralBg(WHITE);

            // Product number is not mandatory for custom techpacks.
            if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CUSTOM_TECHPACK)) {
              productF.setNeutralBg(WHITE);
              unvnameF.setNeutralBg(WHITE);
            }

            /*
             * if
             * (!typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CUSTOM_TECHPACK))
             * { if (unvnameF.getText().equals("")) {
             * unvnameF.setNeutralBg(ERROR_BG); } }
             */

            unvextT.setEnabled(true);
            tppedencyT.setEnabled(true);

            // Set the default base techpack to be the last one in the list,
            // i.e. the latest.
            if (baseTPEnabled && baseCB.getItemCount() > 0) {
              baseCB.setSelectedIndex(getBaseTechpackIndex());
            } else {
              baseCB.setSelectedItem(null);
            }
            final UETableModel uetm = dataModelController.getNewTechPackDataModel().getUnvExtTableModel();
            uetm.addTableModelListener(new TableUnvExtSelectionListener());
            unvextT.setModel(uetm);

            unvextT.getColumnModel().getColumn(0).setCellEditor(
                new DefaultCellEditor(
                    new LimitedSizeTextField(25, Universename.getUniverseextensionColumnSize(), false)));

            final TPDepedencyTableModel tpdtm = dataModelController.getNewTechPackDataModel()
                .getTPDepedencyTableModel();
            tpdtm.addTableModelListener(new TableTPDSelectionListener());
            tppedencyT.setModel(tpdtm);

            tppedencyT.getColumnModel().getColumn(0).setCellEditor(
                new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(),
                    false)));

            tppedencyT.getColumnModel().getColumn(1).setCellEditor(
                new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

          }

          repaint();
          validate();

          installdescrA.setText(((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getInstalldescription());

          if (!tmpVersionid.equals(((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getVersionid())) {

            // if (error == false) {
            // tableTPChanged = true;
            // }
            handleButtons();
          }

          oldName = nameF.getText();
          oldVersion = ((Versioning) ((DataTreeNode) tmp).getRockDBObject()).getVersionid();
          oldVersion = oldVersion.substring(oldVersion.lastIndexOf(":") + 1);

          String show = oldVersion;
          show = show.replace("((", "");
          show = show.replace("))", "");

          setsFromF.setText(oldName + ":" + show);

        } else {
          setSelectedNode(null);
        }
      } else {
        setSelectedNode(null);
      }
      // System.out.println("Selected " + getSelectedNode());
    }
  }

  private int getBaseTechpackIndex() {
    boolean foundBase = false;
    int baseTpIndex = 0;

    if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.ENIQ_EVENT)) {
      if (this.ver != null) {
        for (String s : baseTP) {
          if (s.equals(this.ver.getBasedefinition())) {
            foundBase = true;
            break;
          }
          baseTpIndex++;
        }
      }
    }
    // Use the latest BASE TP if base cannot be found for ENIQ_EVENT techpack or
    // if techpack is not ENIQ_EVENT
    if (!foundBase) {
      baseTpIndex = baseCB.getItemCount() - 1;
    }
    return baseTpIndex;
  }

  // ************************************************
  // Techpack Dependencies popup menu
  // ************************************************
  private JPopupMenu createTPDPopup(final MouseEvent e) {
    JPopupMenu popupTPD;
    JMenuItem miTPD;
    popupTPD = new JPopupMenu();

    if ((typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK))) {
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

  private void displayTPDMenu(final MouseEvent e) {

    if (e.isPopupTrigger()) {
      createTPDPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private class TableTPDSelectionListener implements TableModelListener, ActionListener, MouseListener {

    @Override
    public void tableChanged(final TableModelEvent e) {
      // tableTPDChanged = true;
      handleButtons();

      tppedencyT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getTechpacknameColumnSize(), false)));

      tppedencyT.getColumnModel().getColumn(1).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Techpackdependency.getVersionColumnSize(), false)));

    }

    @Override
    public void actionPerformed(final ActionEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {
      displayTPDMenu(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {

      displayTPDMenu(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

      displayTPDMenu(e);
    }
  }

  public DataTreeNode getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(final DataTreeNode selectedNode) {
    final DataTreeNode exSelectedNode = this.selectedNode;
    this.selectedNode = selectedNode;
    firePropertyChange("selectedNode", exSelectedNode, selectedNode);
    tpTree.setSelected(selectedNode);
  }

  // ************************************************
  // Vendor release table popup menu
  // ************************************************
  private JPopupMenu createVRPopup(final MouseEvent e) {
    JPopupMenu popupVR;
    JMenuItem miVR;
    popupVR = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (venrelT.getSelectedRow() > -1) {
        miVR = new JMenuItem("Remove Vendor Release");
        miVR.setAction(getAction("removevr"));
        popupVR.add(miVR);
      }

      miVR = new JMenuItem("Add Vendor Release");
      miVR.setAction(getAction("addemptyvr"));
      popupVR.add(miVR);

    } else {

      if (!(typeCB.getSelectedItem().toString().equalsIgnoreCase("Topology")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("BASE")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("EVENT")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("SYSTEM") || typeCB.getSelectedItem().toString()
          .equalsIgnoreCase(Constants.ENIQ_EVENT))) {
        miVR = new JMenuItem("Add Vendor Release");
        miVR.setAction(getAction("addemptyvr"));
        popupVR.add(miVR);
      }
    }

    popupVR.setOpaque(true);
    popupVR.setLightWeightPopupEnabled(true);

    return popupVR;
  }

  private void displayVRMenu(final MouseEvent e) {

    if (e.isPopupTrigger()) {
      createVRPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private class TableVRSelectionListener implements TableModelListener, ActionListener, MouseListener {

    @Override
    public void tableChanged(final TableModelEvent e) {
      // tableVRChanged = true;
      handleButtons();

      venrelT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(
              new LimitedSizeTextField(25, Supportedvendorrelease.getVendorreleaseColumnSize(), false)));

    }

    @Override
    public void actionPerformed(final ActionEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {
      displayVRMenu(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
      displayVRMenu(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

      displayVRMenu(e);
    }
  }

  // ************************************************
  // Universe extension table popup menu
  // ************************************************
  private JPopupMenu createLicensePopup(final MouseEvent e) {

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

  // ************************************************
  // Universe extension table popup menu
  // ************************************************
  private JPopupMenu createUnvExtPopup(final MouseEvent e) {
    JPopupMenu popupUnvExt;
    JMenuItem miUnvExt;
    popupUnvExt = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (unvextT.getSelectedRow() > -1) {
        miUnvExt = new JMenuItem("Remove Universe Extension");
        miUnvExt.setAction(getAction("removeue"));
        popupUnvExt.add(miUnvExt);
      }

      miUnvExt = new JMenuItem("Add Universe Extension");
      miUnvExt.setAction(getAction("addemptyue"));
      popupUnvExt.add(miUnvExt);

    } else {
      if (!(typeCB.getSelectedItem().toString().equalsIgnoreCase("Topology")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("BASE")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("EVENT")
          || typeCB.getSelectedItem().toString().equalsIgnoreCase("SYSTEM") || typeCB.getSelectedItem().toString()
          .equalsIgnoreCase(Constants.ENIQ_EVENT))) {

        miUnvExt = new JMenuItem("Add Universe Extension");
        miUnvExt.setAction(getAction("addemptyue"));
        popupUnvExt.add(miUnvExt);
      }
    }

    popupUnvExt.setOpaque(true);
    popupUnvExt.setLightWeightPopupEnabled(true);

    return popupUnvExt;
  }

  private void displayLicenseMenu(final MouseEvent e) {

    if (e.isPopupTrigger()) {
      createLicensePopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private void displayUnvExtMenu(final MouseEvent e) {

    if (e.isPopupTrigger()) {
      createUnvExtPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private class TableLicenseSelectionListener implements TableModelListener, ActionListener, MouseListener {

    @Override
    public void tableChanged(final TableModelEvent e) {
      // tableUEChanged = true;
      handleButtons();
      licenceT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Versioning.getLicensenameColumnSize(), false)));

    }

    @Override
    public void actionPerformed(final ActionEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {
      displayLicenseMenu(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
      displayLicenseMenu(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

      displayLicenseMenu(e);
    }
  }

  private class TableUnvExtSelectionListener implements TableModelListener, ActionListener, MouseListener {

    @Override
    public void tableChanged(final TableModelEvent e) {
      // tableUEChanged = true;
      handleButtons();
      unvextT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(25, Universename.getUniverseextensionColumnSize(), false)));
      unvextT.getColumnModel().getColumn(1)
          .setCellEditor(
              new DefaultCellEditor(new LimitedSizeTextField(25, Universename.getUniverseextensionnameColumnSize(),
                  false)));

    }

    @Override
    public void actionPerformed(final ActionEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {
      displayUnvExtMenu(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
      displayUnvExtMenu(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

      displayUnvExtMenu(e);
    }
  }

  public boolean isCreateEnabled() {
    return createEnabled;
  }

  public void setCreateEnabled(final boolean createEnabled) {
    final boolean oldvalue = this.isCreateEnabled();
    this.createEnabled = createEnabled;
    firePropertyChange("createEnabled", oldvalue, createEnabled);
  }

  private void handleButtons() {

    if (ignoreButtonHandler) {
      return;
    }

    boolean createIsEnabled = false;

    boolean versionIdAlreadyExist = false;

    versionIdAlreadyExist = getVersionIds();

    // RState check
    boolean rstateCorrect = false;
    rstateCorrect = Utils.patternMatch(rstateF.getText(), Constants.RSTATEPATTERN);

    final Vector<String> messages = new Vector<String>();
    //eeoidiv,20110110,HN49966, Changed that SYSTEM techpack can have a licence, e.g. DWH_MONITOR has ENIQ starter licence.
    if ((typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK))
        && licenceT.getModel().getRowCount() > 0) {
      messages.add("There cant be any Licenses defined in " + typeCB.getSelectedItem().toString()
          + " techpack, please switch to other techpack type and remove it from table.");
    }
    if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK) && unvnameF.getText().length() > 0) {
      messages.add("There cant be a universe name defined defined in " + typeCB.getSelectedItem().toString()
          + " techpack, please switch to other techpack type and remove it from filed.");
    }
    if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK) && unvextT.getModel().getRowCount() > 0) {
      messages.add("There cant be any universe extensions defined in " + typeCB.getSelectedItem().toString()
          + " techpack, please switch to other techpack type and remove it from table.");
    }
    if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK) && venrelT.getModel().getRowCount() > 0) {
      messages.add("There cant be any vendor releases defined in " + typeCB.getSelectedItem().toString()
          + " techpack, please switch to other techpack type and remove it from table.");
    }
    if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK) && baseCB.getSelectedIndex() > 0) {
      messages.add("There cant be a base definition defined in " + typeCB.getSelectedItem().toString()
          + " techpack, please switch to other techpack type and remove it from field.");
    }
    if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK) && tppedencyT.getModel().getRowCount() > 0) {
      messages.add("There cant be any techpack depedencys defined in " + typeCB.getSelectedItem().toString()
          + " techpack, please switch to other techpack type and remove it from table.");
    }

    // Check license duplicates
    final Vector<String> v1 = new Vector<String>();
    boolean licduplicate = false;
    for (int i = 0; i < licenceT.getModel().getRowCount(); i++) {
      if (v1.contains(licenceT.getModel().getValueAt(i, 0))) {
        licduplicate = true;
        messages.add("Licence " + licenceT.getModel().getValueAt(i, 0) + " Not correct");
      }

      v1.add((String) licenceT.getModel().getValueAt(i, 0));
    }

    // Check techpack dependency duplicates
    final Vector<String> v2 = new Vector<String>();
    boolean tpdduplicate = false;
    for (int i = 0; i < tppedencyT.getModel().getRowCount(); i++) {
      if (v2.contains(tppedencyT.getModel().getValueAt(i, 0))) {
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

    // Check vendor release duplicates
    final Vector<String> v3 = new Vector<String>();
    final boolean venrelduplicate = false;
    for (int i = 0; i < venrelT.getModel().getRowCount(); i++) {
      if (v3.contains(venrelT.getModel().getValueAt(i, 0))) {
        licduplicate = true;
        messages.add("Vendor realease " + venrelT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      v3.add((String) venrelT.getModel().getValueAt(i, 0));
    }

    // Check universe duplicates
    final Vector<String> v4 = new Vector<String>();
    final boolean unvduplicate = false;
    for (int i = 0; i < unvextT.getModel().getRowCount(); i++) {
      if (v4.contains(unvextT.getModel().getValueAt(i, 0))) {
        licduplicate = true;
        messages.add("Universe " + unvextT.getModel().getValueAt(i, 0) + " Not Unique");
      }
      v4.add((String) unvextT.getModel().getValueAt(i, 0));
    }

    if (tmpVersionid.equalsIgnoreCase("")) {
      tmpVersionid = "none";
    }

    // Validate r-state
    if (rstateCorrect) {
      rstateF.setBackground(WHITE);
      rstateF.setNeutralBg(WHITE);
    } else {
      rstateF.setBackground(ERROR_BG);
      rstateF.setNeutralBg(ERROR_BG);
    }
    
    // eeoidiv 20100514 HL82032:TechPackIDE: Can't generate universe for a system tech pack. 
    // Allow Universe name to be entered for SYSTEM techpacks, but not mandatory.
    // Also allow universe name for EVENT tech packs.
        
    // Check if settings are correct for creating a universe (no duplicate fields or error messages):
    final boolean universeSettingsOk = checkUniverseSettings(messages, rstateCorrect, unvduplicate, venrelduplicate, 
        tpdduplicate, licduplicate, versionIdAlreadyExist);
    
    // Get the tech pack type the user has selected from the combo box:
    final String techPackType = typeCB.getSelectedItem().toString();

    // Check if we can create the tech pack for Custom/System/Event tech packs:
    if (isCreateEnabledCustomSystemEvent(universeSettingsOk, techPackType)) {      
      createIsEnabled = true;
      versionF.setBackground(WHITE);
      productF.setBackground(WHITE);
      unvnameF.setNeutralBg(WHITE);
      unvnameF.setBackground(WHITE);
      unvnameF.setEnabled(true);
      errorMessageComponent.setValue(null);
    } else if (isCreateEnabledPMCM(universeSettingsOk, techPackType)) {
      // Check if we can create the tech pack for PM/CM tech packs:
      createIsEnabled = true;
      versionF.setBackground(WHITE);

      unvnameF.setNeutralBg(WHITE);
      unvnameF.setEnabled(true);
      errorMessageComponent.setValue(null);

    } else if (isCreateEnabledBaseSystemTopologyEniqEvent(universeSettingsOk, techPackType)) {      
      // Check if we can create the tech pack for Base/System/Topology tech packs:
      createIsEnabled = true;
      versionF.setBackground(WHITE);

      unvnameF.setText("");
      unvnameF.setNeutralBg(WHITE);
      unvnameF.setEnabled(false);
      errorMessageComponent.setValue(null);

    } else if (typeCB.getSelectedItem().equals(Constants.BASE_TECHPACK) && (dataModelController.getNewTechPackDataModel().getTechpack_type() != typeCB.getSelectedItem())) {
      if (versionIdAlreadyExist == true) {
        versionF.setBackground(ERROR_BG);
        versionF.setNeutralBg(ERROR_BG);
      }

      errorMessageComponent.setValue(messages);
      createIsEnabled = false;

    } else {
      createIsEnabled = false;

      if (versionIdAlreadyExist == true) {
        versionF.setBackground(ERROR_BG);
        versionF.setNeutralBg(ERROR_BG);
      }

      errorMessageComponent.setValue(messages);

      // Enable the universe name text field for CM, PM, and custom tech packs (these are always enabled):
      if ((typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CM_TECHPACK))
    		  || (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.PM_TECHPACK))
    		  || (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CUSTOM_TECHPACK))) {

        unvnameF.setEnabled(true);

        if (unvnameF.getText().toString().equals("")) {
          unvnameF.setNeutralBg(ERROR_BG);
        } else {

          unvnameF.setNeutralBg(WHITE);
        }
        unvnameF.setEnabled(true);
      }
    }

    // Disable the universe name text field for topology tech packs and base tech packs:
    if ((typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.TOPOLOGY_TECHPACK))
        || (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK))
        || (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.ENIQ_EVENT))) {
    	
      unvnameF.setText("");
      unvnameF.setNeutralBg(neutralBg);
      unvnameF.setBackground(neutralBg);
      unvnameF.setEnabled(false);
    }
    this.setCreateEnabled(createIsEnabled);
  }
    
  /**
   * Checks if a techpack can be created for a Custom, System or Event tech
   * pack.
   * 
   * @param universeSettingsOk  True if the settings are ok.
   * @param techPackType        The type of tech pack selected from the combo box.
   * @return create True if the tech pack can be created.
   */
  protected boolean isCreateEnabledCustomSystemEvent(final boolean universeSettingsOk, final String techPackType) {
    boolean create = false;

    create = (!tmpVersionid.equalsIgnoreCase(versionF.getText().toString()))
        && !rstateF.getText().equals("")
        && !versionF.getText().equals("")
        && !nameF.getText().equals("")
        && universeSettingsOk
        && ((techPackType.equalsIgnoreCase(Constants.CUSTOM_TECHPACK))
            || (techPackType.equalsIgnoreCase(Constants.SYSTEM_TECHPACK)) || (techPackType
            .equalsIgnoreCase(Constants.EVENT_TECHPACK)));

    return create;
  }


  /**
   * Checks if a universe can be created for a PM or CM tech pack.
   * 
   * @param universeSettingsOk  True if the settings are ok.
   * @param techPackType        The type of tech pack selected from the combo box.
   * @return create True if the tech pack can be created.
   */
  protected boolean isCreateEnabledPMCM(final boolean universeSettingsOk, final String techPackType) {
    boolean create = false;

    create = (!tmpVersionid.equalsIgnoreCase(versionF.getText().toString()))
        && !unvnameF.getText().equals("")
        && !productF.getText().equals("")
        && !rstateF.getText().equals("")
        && !versionF.getText().equals("")
        && !nameF.getText().equals("")
        && universeSettingsOk
        && ((techPackType.equalsIgnoreCase(Constants.PM_TECHPACK))
        		|| (techPackType.toString().equalsIgnoreCase(Constants.CM_TECHPACK)));

    return create;
  }

  /**
   * Checks if a universe can be created for a Base, System, or Topology tech pack.
   * 
   * @param universeSettingsOk  True if the settings are ok.
   * @param techPackType        The type of tech pack selected from the combo box.
   * @return create True if the tech pack can be created.
   */
  protected boolean isCreateEnabledBaseSystemTopologyEniqEvent(final boolean universeSettingsOk, final String techPackType) {
    boolean create = false;

    create = (!tmpVersionid.equals(versionF.getText().toString()))
        && !productF.getText().equals("")
        && !rstateF.getText().equals("")
        && !versionF.getText().equals("")
        && !nameF.getText().equals("")
        && universeSettingsOk
        && ((techPackType.equalsIgnoreCase(Constants.BASE_TECHPACK))
            || (techPackType.equalsIgnoreCase(Constants.SYSTEM_TECHPACK))
            || (techPackType.equalsIgnoreCase(Constants.TOPOLOGY_TECHPACK))
            || (techPackType.equalsIgnoreCase(Constants.ENIQ_EVENT)));

    return create;
  }
  
  /**
   * Checks if settings are correct for creating a universe.
   * @param messages              Error messages.
   * @param rstateCorrect         True if R-state is correct.
   * @param unvduplicate          True if universe is duplicate.
   * @param venrelduplicate       True if vendor release is duplicate.
   * @param tpdduplicate          True if tech pack is duplicate.
   * @param licduplicate          True if license is duplicate.
   * @param versionIdAlreadyExist True if version ID already exists.
   * @return settingsOk True if there are no duplicate fields or error messages, and r state is correct.
   */
  private boolean checkUniverseSettings(Vector<String> messages, boolean rstateCorrect, boolean unvduplicate, boolean venrelduplicate, 
      boolean tpdduplicate, boolean licduplicate, boolean versionIdAlreadyExist) {
    
    boolean settingsOk = false;
    settingsOk = (messages.size() == 0) 
    && rstateCorrect 
    && !unvduplicate 
    && !venrelduplicate 
    && !tpdduplicate 
    && !licduplicate
    && (versionIdAlreadyExist == false);
    return settingsOk;
  }
  
  private class TableTypeSelectionListener implements ActionListener, MouseListener {

    public void tableChanged(final TableModelEvent e) {
      // tableTPChanged = true;
      handleButtons();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {

      // tableTPChanged = true;

      // Enable or disable the base techpack combo box, if the base should
      // be defined for this type or not.
      if (nobasetypeslist.contains(typeCB.getSelectedItem().toString())) {
        // No base can be defined for this techpack. Disable the combo box.
        baseCB.removeAllItems();
        baseCB.revalidate();
        baseCB.setEnabled(false);
        baseCB.setBackground(neutralBg);
      } else {
        // Base must be defined for this techpack. Enable the combo box.
        baseCB.setEnabled(true);
        baseCB.setBackground(WHITE);
        final ComboBoxModel model = new DefaultComboBoxModel(dataModelController.getNewTechPackDataModel()
            .getBaseTechPacks());
        baseCB.setModel(model);
        baseCB.revalidate();

        // Set the default base techpack to be the last one in the list,
        // i.e. the latest.
        if (getSelectedNode() != null) {
          baseCB.setSelectedIndex(baseCB.getItemCount() - 1);
        } else {
          // No from techpack selected
          baseCB.setSelectedIndex(0);
        }

      }

      if ((typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.BASE_TECHPACK))) {

        unvnameF.setEnabled(false);
        unvextT.setEnabled(false);
        unvextT.removeAll();
        tppedencyT.setEnabled(false);
        tppedencyT.removeAll();
        venrelT.setEnabled(false);
        unvextT.setBackground(neutralBg);
        venrelT.removeAll();
        tppedencyT.setBackground(neutralBg);
        venrelT.setBackground(neutralBg);
        unvnameF.setBackground(neutralBg);

        licenceT.setEnabled(false);
        licenceT.removeAll();
        licenceT.setBackground(neutralBg);

      } else if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.SYSTEM_TECHPACK)) {

        unvnameF.setEnabled(false);
        unvextT.setEnabled(false);
        unvextT.removeAll();
        tppedencyT.setEnabled(true);
        venrelT.setEnabled(false);
        venrelT.removeAll();
        unvextT.setBackground(neutralBg);
        tppedencyT.setBackground(WHITE);
        venrelT.setBackground(neutralBg);
        unvnameF.setBackground(neutralBg);

        licenceT.setEnabled(false);
        licenceT.removeAll();
        licenceT.setBackground(neutralBg);

      } else if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.TOPOLOGY_TECHPACK)) {

        unvnameF.setEnabled(false);
        unvextT.setEnabled(false);
        unvextT.removeAll();
        tppedencyT.setEnabled(true);
        venrelT.setEnabled(false);
        venrelT.removeAll();
        licenceT.setEnabled(false);
        licenceT.setBackground(WHITE);
        unvextT.setBackground(neutralBg);
        tppedencyT.setBackground(WHITE);
        venrelT.setBackground(neutralBg);
        unvnameF.setBackground(neutralBg);

      } else if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CUSTOM_TECHPACK)) {

        unvnameF.setEnabled(true);
        unvextT.setEnabled(true);
        venrelT.setEnabled(true);
        tppedencyT.setEnabled(true);
        licenceT.setEnabled(false);

        licenceT.setBackground(WHITE);
        unvextT.setBackground(WHITE);
        venrelT.setBackground(WHITE);
        tppedencyT.setBackground(WHITE);
        unvnameF.setBackground(WHITE);

      } else {

        unvnameF.setEnabled(true);
        unvextT.setEnabled(true);
        venrelT.setEnabled(true);
        tppedencyT.setEnabled(true);
        licenceT.setEnabled(false);

        licenceT.setBackground(WHITE);
        unvextT.setBackground(WHITE);
        venrelT.setBackground(WHITE);
        tppedencyT.setBackground(WHITE);
        unvnameF.setBackground(WHITE);

        if (unvnameF.getText().equals("")) {
          unvnameF.setBackground(ERROR_BG);
        }
      }

      // Product number is not mandatory for custom techpacks.
      if (typeCB.getSelectedItem().toString().equalsIgnoreCase(Constants.CUSTOM_TECHPACK)) {
        productF.setBackground(WHITE);
      } else if (productF.getText().equals("")) {
        productF.setBackground(ERROR_BG);
      }

      handleButtons();

      repaint();
      validate();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }
  }

  public boolean getVersionIds() {

    name = getTechPackName();
    version = versionF.getText().trim();
    version = version.toUpperCase();

    // TODO Should migrate convert all parenthesis away?

    final String noParenthesisVersionid = name + ":" + version;
    final String chkVersionid = name + ":((" + version + "))";

    List<String> versionIdList = null;
    String tmp = "";

    versionIdList = (dataModelController.getNewTechPackDataModel().getVersionIdList());

    for (int i = 0; i < versionIdList.size(); i++) {
      tmp = versionIdList.get(i).toString();

      if (tmp.equalsIgnoreCase(chkVersionid) || (tmp.equalsIgnoreCase(noParenthesisVersionid))) {
        return true;
      }
    }
    return false;
  }

  private void checkVersion() {

    boolean saveIsEnabled = isCreateEnabled();

    final List<String> v = dataModelController.getNewTechPackDataModel().getVersionIdList();
    if (v != null) {

      final String name = nameF.getText();
      final String version = "((" + versionF.getText() + "))";

      if (v.contains(name + ":" + version)) {
        versionF.setNeutralBg(ERROR_BG);
        saveIsEnabled = false;
      } else if (version.equals("(())") && !name.equals("")) {
        nameF.setNeutralBg(WHITE);
        versionF.setNeutralBg(ERROR_BG);
        saveIsEnabled = false;
      } else if (version.equals("(())") && name.equals("")) {
        nameF.setNeutralBg(ERROR_BG);
        versionF.setNeutralBg(ERROR_BG);
        saveIsEnabled = false;
      } else {
        versionF.setNeutralBg(WHITE);
        versionF.setBackground(WHITE);
        nameF.setNeutralBg(WHITE);
        saveIsEnabled = true;
      }
    }

    this.setCreateEnabled(saveIsEnabled);
  }

  private class MyOtherDocumentListener implements DocumentListener {

    @Override
    public void changedUpdate(final DocumentEvent e) {
      checkVersion();
      handleButtons();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
      checkVersion();
      handleButtons();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
      checkVersion();
      handleButtons();
    }
  }

  private class MyDocumentListener implements DocumentListener {

    @Override
    public void changedUpdate(final DocumentEvent e) {
      handleButtons();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
      handleButtons();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
      handleButtons();
    }
  }

  private boolean saveEnabled = false;

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  private class MyButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(final ActionEvent e) {
      setsFromF.setText("");
    }
  }
}