package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import tableTree.ComboBoxTableCellEditor;

import com.distocraft.dc5000.repository.dwhrep.Universeobject;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.ComboTableCellRenderer;
import com.ericsson.eniq.techpacksdk.GenericPanel;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class UniverseObjectView extends GenericPanel {

  private static final Logger logger = Logger.getLogger(UniverseConditionView.class.getName());

  public static final Color WHITE = Color.WHITE;

  private SingleFrameApplication application;

  private UniverseTabs parentPanel;

  private boolean saveEnabled = false;

  private boolean cancelEnabled = false;

  private boolean tableOChanged = false;

  private boolean tableCChanged = false;

  boolean editable = true;

  private JTable universeObjectT = new JTable();

  private JTable universeCompT = new JTable();

  private DataModelController dataModelController;

  private JFrame frame;

  private String[] extensions = null;

  private String[] selectOptions = null;

  private UniverseComputedObjectDataModel computedObjectdataModel;

  Versioning tmpversioning;

  /**
   * For error handling
   */
  Vector<String> errors = new Vector<String>();

  /**
   * For error handling
   */
  private ErrorMessageComponent errorMessageComponent;

  public UniverseObjectView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;
    this.editable = editable;
    this.dataModelController = dataModelController;

    this.application = application;

    this.parentPanel = parentPanel;

    tmpversioning = versioning;

    computedObjectdataModel = dataModelController.getUniverseComputedObjectDataModel();

    // Get the universe extensions
    this.extensions = computedObjectdataModel.getUniverseExtensions(tmpversioning.getVersionid());

    // Get the select options
    this.selectOptions = computedObjectdataModel.getUniverseFormulas(tmpversioning.getVersionid());
    Arrays.sort(this.selectOptions);

    // ************** Text panel **********************
    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // Universe Object table
    int width = 0;
    for (int colWidt : UniverseObjectTableModel.myColumnWidths)
      width += colWidt;

    Dimension tableDim = new Dimension(width, 200);

    JLabel universeobject = new JLabel("Universe Object");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(universeobject, c);

    universeObjectT = new UniverseObjectTable(editable);

    UniverseObjectTableModel otm = dataModelController.getUniverseObjectDataModel().getTableModel();
    setModel(universeObjectT, otm);

    addTableModelListener(otm, new TableOSelectionListener());

    universeObjectT.setBackground(WHITE);

    // universeObjectT.setEnabled(editable);

    if (editable) {
      universeObjectT.addMouseListener(new TableOSelectionListener());
      universeObjectT.getTableHeader().addMouseListener(new TableOSelectionListener());
    }

    setColumnWidths(universeObjectT);

    setColumnEditors(universeObjectT);
    setColumnRenderers(universeObjectT);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JScrollPane scrollPane = new JScrollPane(universeObjectT);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // Universe Computed object table
    width = 0;
    for (int colWidt : UniverseComputedObjectTableModel.myColumnWidths)
      width += colWidt;

    tableDim = new Dimension(width, 200);

    JLabel universecompobject = new JLabel("Universe Computed Object");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(universecompobject, c);

    universeCompT = new UniverseComputedObjectTable(editable);

    UniverseComputedObjectTableModel ctm = dataModelController.getUniverseComputedObjectDataModel().getTableModel();
    setModel(universeCompT, ctm);

    addTableModelListener(ctm, new TableCSelectionListener());

    // Add a listener that observes changes in class name and object name
    // fields.
    // This allows updating the key values of Universeparameters objects,
    // that
    // have these fields' values as their primary keys.
    addTableModelListener(ctm, new ParametersKeyValuesChangeListener(universeCompT));

    universeCompT.setBackground(WHITE);
    // universeCompT.setEnabled(editable);

    if (editable) {
      universeCompT.addMouseListener(new TableCSelectionListener());
      universeCompT.getTableHeader().addMouseListener(new TableCSelectionListener());
    }

    setComputedColumnWidths(universeCompT);

    setComputedColumnEditors(universeCompT);
    setComputedColumnRenderers(universeCompT);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    scrollPane = new JScrollPane(universeCompT);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // ************** buttons **********************

    JButton cancel;
    JButton save;

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    cancel = new JButton("Discard");
    cancel.setActionCommand("discard");
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Discard");

    save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("UniverseObjectClose");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    buttonPanel.add(errorMessageComponent);

    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    // ************** right & left panels, left panel contains
    // **********************

    JPanel lpanel = new JPanel(new BorderLayout());
    JPanel rpanel = new JPanel(new BorderLayout());
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

    this.addComponentListener(new MyCompListener());

    frame.repaint();
  }

  private class MyCompListener implements ComponentListener {

    public void componentHidden(ComponentEvent e) {
      // No action
    }

    public void componentMoved(ComponentEvent e) {
      // No action
    }

    public void componentResized(ComponentEvent e) {
      // No action
    }

    public void componentShown(ComponentEvent e) {
      if (computedObjectdataModel.dataRefreshed) {
        refresh();
        computedObjectdataModel.dataRefreshed = false;
      }

    }

  }

  /**
   * Method for setting the column editor of the Description and IsElement
   * columns.
   */
  public void setColumnEditors(final JTable theTable) {

    // Set editor for Classname
    final TableColumn classnameColumn = theTable.getColumnModel()
        .getColumn(UniverseObjectTableModel.classnameColumnIdx);
    LimitedSizeCellEditor classnameColumnEditor = new LimitedSizeCellEditor(
        UniverseObjectTableModel.myColumnWidths[UniverseObjectTableModel.classnameColumnIdx], Universeobject
            .getClassnameColumnSize(), true);
    classnameColumn.setCellEditor(classnameColumnEditor);

    // Set editor for object name
    final TableColumn objectnameColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.objectnameColumnIdx);
    LimitedSizeCellEditor objectnameColumnEditor = new LimitedSizeCellEditor(
        UniverseObjectTableModel.myColumnWidths[UniverseObjectTableModel.objectnameColumnIdx], Universeobject
            .getObjectnameColumnSize(), true);
    objectnameColumn.setCellEditor(objectnameColumnEditor);

    // Set editor for description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.descriptionColumnIdx);
    DescriptionCellEditor descriptionColumnEditor = new DescriptionCellEditor(
        Universeobject.getDescriptionColumnSize(), false, this.editable);
    objectnameColumn.setCellEditor(objectnameColumnEditor);
    descriptionColumn.setCellEditor(descriptionColumnEditor);

    // Set editor for objecttype
    final TableColumn objecttypeColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.objecttypeColumnIdx);
    final ComboBoxTableCellEditor objecttypeColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.UNIVERSEOBJECTTYPES);
    objecttypeColumn.setCellEditor(objecttypeColumnComboEditor);

    // Set editor for qualification
    final TableColumn qualificationColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.qualificationColumnIdx);
    final ComboBoxTableCellEditor qualificationColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.UNIVERSEQUALIFICATIONTYPES);
    qualificationColumn.setCellEditor(qualificationColumnComboEditor);

    // Set editor for aggregation
    final TableColumn aggregationColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.aggregationColumnIdx);
    final ComboBoxTableCellEditor aggregationColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.AGGREGATION_FORMULAS);
    aggregationColumn.setCellEditor(aggregationColumnComboEditor);

    // Set editor for select
    final TableColumn selectColumn = theTable.getColumnModel().getColumn(UniverseObjectTableModel.selectColumnIdx);
    DescriptionCellEditor selectColumnEditor = new DescriptionCellEditor(Universeobject.getObjselectColumnSize(),
        false, this.editable);
    objectnameColumn.setCellEditor(objectnameColumnEditor);
    selectColumn.setCellEditor(selectColumnEditor);

    // Set editor for where
    final TableColumn whereColumn = theTable.getColumnModel().getColumn(UniverseObjectTableModel.whereColumnIdx);
    DescriptionCellEditor whereColumnEditor = new DescriptionCellEditor(Universeobject.getObjwhereColumnSize(), false,
        this.editable);
    objectnameColumn.setCellEditor(objectnameColumnEditor);
    whereColumn.setCellEditor(whereColumnEditor);

    // Set editor for prompt
    final TableColumn promptColumn = theTable.getColumnModel().getColumn(UniverseObjectTableModel.promptColumnIdx);
    LimitedSizeCellEditor promptColumnEditor = new LimitedSizeCellEditor(
        UniverseObjectTableModel.myColumnWidths[UniverseObjectTableModel.promptColumnIdx], Universeobject
            .getPrompthierarchyColumnSize(), false);
    promptColumn.setCellEditor(promptColumnEditor);

    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.universeextensionColumnIdx);
    // final ComboBoxTableCellEditor universeextensionColumnComboEditor =
    // new
    // ComboBoxTableCellEditor(extensions);
    // universeextensionColumn.setCellEditor(universeextensionColumnComboEditor);
    UniverseExtCellEditor univExtEditor = new UniverseExtCellEditor(this.extensions, true);
    universeextensionColumn.setCellEditor(univExtEditor);

  }

  /**
   * Method for setting the column renderer. Used to set a renderer for the
   * description field and isElement combo box.
   */
  public void setColumnRenderers(final JTable theTable) {

    // Set renderer for Classname
    final TableColumn classnameColumn = theTable.getColumnModel()
        .getColumn(UniverseObjectTableModel.classnameColumnIdx);
    LimitedSizeTextTableCellRenderer classnameColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getClassnameColumnSize(), true);
    classnameColumn.setCellRenderer(classnameColumnRenderer);

    // Set renderer for objectname
    final TableColumn objectnameColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.objectnameColumnIdx);
    LimitedSizeTextTableCellRenderer objectnameColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getObjectnameColumnSize(), true);
    objectnameColumn.setCellRenderer(objectnameColumnRenderer);

    // Set renderer for Classname
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.descriptionColumnIdx);
    LimitedSizeTextTableCellRenderer descriptionColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getDescriptionColumnSize(), false);
    descriptionColumn.setCellRenderer(descriptionColumnRenderer);

    // Set editor for objecttype
    final TableColumn objecttypeColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.objecttypeColumnIdx);
    final ComboTableCellRenderer objecttypeColumnComboRenderer = new ComboTableCellRenderer(
        Constants.UNIVERSEOBJECTTYPES);
    objecttypeColumn.setCellRenderer(objecttypeColumnComboRenderer);
    theTable.setVerifyInputWhenFocusTarget(true);

    // Set editor for qualification
    final TableColumn qualificationColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.qualificationColumnIdx);
    final ComboTableCellRenderer qualificationColumnComboRenderer = new ComboTableCellRenderer(
        Constants.UNIVERSEQUALIFICATIONTYPES);
    qualificationColumn.setCellRenderer(qualificationColumnComboRenderer);
    theTable.setVerifyInputWhenFocusTarget(true);

    // Set editor for aggregation
    final TableColumn aggregationColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.aggregationColumnIdx);
    final ComboTableCellRenderer aggregationColumnComboRenderer = new ComboTableCellRenderer(
        Constants.AGGREGATION_FORMULAS);
    aggregationColumn.setCellRenderer(aggregationColumnComboRenderer);
    theTable.setVerifyInputWhenFocusTarget(true);

    // Set renderer for select
    final TableColumn selectColumn = theTable.getColumnModel().getColumn(UniverseObjectTableModel.selectColumnIdx);
    LimitedSizeTextTableCellRenderer selectColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getObjselectColumnSize(), false);
    selectColumn.setCellRenderer(selectColumnRenderer);

    // Set renderer for where
    final TableColumn whereColumn = theTable.getColumnModel().getColumn(UniverseObjectTableModel.whereColumnIdx);
    LimitedSizeTextTableCellRenderer whereColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getObjwhereColumnSize(), false);
    whereColumn.setCellRenderer(whereColumnRenderer);

    // Set renderer for prompt
    final TableColumn promptColumn = theTable.getColumnModel().getColumn(UniverseObjectTableModel.promptColumnIdx);
    LimitedSizeTextTableCellRenderer promptColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getPrompthierarchyColumnSize(), false);
    promptColumn.setCellRenderer(promptColumnRenderer);

    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseObjectTableModel.universeextensionColumnIdx);
    // final ComboTableCellRenderer universeextensionColumnComboRenderer =
    // new
    // ComboTableCellRenderer(extensions);
    DefaultTableCellRenderer universeExtColumnRenderer = new DefaultTableCellRenderer();
    universeextensionColumn.setCellRenderer(universeExtColumnRenderer);

    theTable.setVerifyInputWhenFocusTarget(true);

  }

  /**
   * Method for setting the column editor of the Description and IsElement
   * columns.
   */
  public void setComputedColumnEditors(final JTable theTable) {

    // Set editor for Classname
    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.classnameColumnIdx);
    LimitedSizeCellEditor classnameColumnEditor = new LimitedSizeCellEditor(
        UniverseComputedObjectTableModel.myColumnWidths[UniverseComputedObjectTableModel.classnameColumnIdx],
        Universeobject.getClassnameColumnSize(), true);
    classnameColumn.setCellEditor(classnameColumnEditor);

    // Set editor for object name
    final TableColumn objectnameColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.objectnameColumnIdx);
    LimitedSizeCellEditor objectnameColumnEditor = new LimitedSizeCellEditor(
        UniverseComputedObjectTableModel.myColumnWidths[UniverseComputedObjectTableModel.objectnameColumnIdx],
        Universeobject.getObjectnameColumnSize(), true);
    objectnameColumn.setCellEditor(objectnameColumnEditor);

    // Set editor for description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.descriptionColumnIdx);
    DescriptionCellEditor descriptionColumnEditor = new DescriptionCellEditor(
        Universeobject.getDescriptionColumnSize(), false, this.editable);
    objectnameColumn.setCellEditor(objectnameColumnEditor);
    descriptionColumn.setCellEditor(descriptionColumnEditor);

    // Set editor for objecttype
    final TableColumn objecttypeColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.objecttypeColumnIdx);
    final ComboBoxTableCellEditor objecttypeColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.UNIVERSEOBJECTTYPES);
    objecttypeColumn.setCellEditor(objecttypeColumnComboEditor);

    // Set editor for qualification
    final TableColumn qualificationColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.qualificationColumnIdx);
    final ComboBoxTableCellEditor qualificationColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.UNIVERSEQUALIFICATIONTYPES);
    qualificationColumn.setCellEditor(qualificationColumnComboEditor);

    // Set editor for aggregation
    // final TableColumn aggregationColumn =
    // theTable.getColumnModel().getColumn(
    // UniverseComputedObjectTableModel.aggregationColumnIdx);
    // final ComboBoxTableCellEditor aggregationColumnComboEditor = new
    // ComboBoxTableCellEditor(
    // Constants.AGGREGATION_FORMULAS);
    // aggregationColumn.setCellEditor(aggregationColumnComboEditor);

    // Set editor for select
    final TableColumn selectColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.selectColumnIdx);
    ComboBoxTableCellEditor selectColumnEditor = new ComboBoxTableCellEditor(this.selectOptions);
    selectColumn.setCellEditor(selectColumnEditor);

    // Set editor for parameter
    final TableColumn parametersColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.parametersColumnIdx);
    ParameterCellEditor parametersEditor = new ParameterCellEditor(dataModelController, tmpversioning.getVersionid(),
        true);
    parametersColumn.setCellEditor(parametersEditor);

    // Set editor for where
    final TableColumn whereColumn = theTable.getColumnModel()
        .getColumn(UniverseComputedObjectTableModel.whereColumnIdx);
    DescriptionCellEditor whereColumnEditor = new DescriptionCellEditor(Universeobject.getObjwhereColumnSize(), false,
        this.editable);
    whereColumn.setCellEditor(whereColumnEditor);

    // Set editor for prompt
    final TableColumn promptColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.promptColumnIdx);
    LimitedSizeCellEditor promptColumnEditor = new LimitedSizeCellEditor(
        UniverseComputedObjectTableModel.myColumnWidths[UniverseComputedObjectTableModel.promptColumnIdx],
        Universeobject.getPrompthierarchyColumnSize(), false);
    promptColumn.setCellEditor(promptColumnEditor);

    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.universeextensionColumnIdx);
    // final ComboBoxTableCellEditor universeextensionColumnComboEditor =
    // new
    // ComboBoxTableCellEditor(extensions);
    // universeextensionColumn.setCellEditor(universeextensionColumnComboEditor);
    UniverseExtCellEditor univExtEditor = new UniverseExtCellEditor(this.extensions, true);
    universeextensionColumn.setCellEditor(univExtEditor);

  }

  /**
   * Method for setting the column renderer. Used to set a renderer for the
   * description field and isElement combo box.
   */
  public void setComputedColumnRenderers(final JTable theTable) {

    // Set renderer for Classname
    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.classnameColumnIdx);
    LimitedSizeTextTableCellRenderer classnameColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getClassnameColumnSize(), true);
    classnameColumn.setCellRenderer(classnameColumnRenderer);

    // Set renderer for objectname
    final TableColumn objectnameColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.objectnameColumnIdx);
    LimitedSizeTextTableCellRenderer objectnameColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getObjectnameColumnSize(), true);
    objectnameColumn.setCellRenderer(objectnameColumnRenderer);

    // Set renderer for Description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.descriptionColumnIdx);
    LimitedSizeTextTableCellRenderer descriptionColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getDescriptionColumnSize(), false);
    descriptionColumn.setCellRenderer(descriptionColumnRenderer);

    // Set renderer for objecttype
    final TableColumn objecttypeColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.objecttypeColumnIdx);
    final ComboTableCellRenderer objecttypeColumnComboRenderer = new ComboTableCellRenderer(
        Constants.UNIVERSEOBJECTTYPES);
    objecttypeColumn.setCellRenderer(objecttypeColumnComboRenderer);
    theTable.setVerifyInputWhenFocusTarget(true);

    // Set renderer for qualification
    final TableColumn qualificationColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.qualificationColumnIdx);
    final ComboTableCellRenderer qualificationColumnComboRenderer = new ComboTableCellRenderer(
        Constants.UNIVERSEQUALIFICATIONTYPES);
    qualificationColumn.setCellRenderer(qualificationColumnComboRenderer);
    theTable.setVerifyInputWhenFocusTarget(true);

    // Set renderer for aggregation
    // final TableColumn aggregationColumn =
    // theTable.getColumnModel().getColumn(
    // UniverseComputedObjectTableModel.aggregationColumnIdx);
    // final ComboTableCellRenderer aggregationColumnComboRenderer = new
    // ComboTableCellRenderer(
    // Constants.AGGREGATION_FORMULAS);
    // aggregationColumn.setCellRenderer(aggregationColumnComboRenderer);
    // theTable.setVerifyInputWhenFocusTarget(true);

    // Set renderer for select
    final TableColumn selectColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.selectColumnIdx);
    ComboTableCellRenderer selectColumnRenderer = new ComboTableCellRenderer(this.selectOptions);
    selectColumn.setCellRenderer(selectColumnRenderer);

    // Set renderer for parameters column
    TableColumn parametersColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.parametersColumnIdx);
    DefaultTableCellRenderer parametersColumnRenderer = new DefaultTableCellRenderer();
    parametersColumn.setCellRenderer(parametersColumnRenderer);

    // Set renderer for where
    final TableColumn whereColumn = theTable.getColumnModel()
        .getColumn(UniverseComputedObjectTableModel.whereColumnIdx);
    LimitedSizeTextTableCellRenderer whereColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getObjwhereColumnSize(), false);
    whereColumn.setCellRenderer(whereColumnRenderer);

    // Set renderer for prompt
    final TableColumn promptColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.promptColumnIdx);
    LimitedSizeTextTableCellRenderer promptColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeobject
        .getPrompthierarchyColumnSize(), false);
    promptColumn.setCellRenderer(promptColumnRenderer);

    // Set renderer for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseComputedObjectTableModel.universeextensionColumnIdx);
    // final ComboTableCellRenderer universeextensionColumnComboRenderer =
    // new
    // ComboTableCellRenderer(extensions);
    // universeextensionColumn.setCellRenderer(universeextensionColumnComboRenderer);
    DefaultTableCellRenderer universeExtColumnRenderer = new DefaultTableCellRenderer();
    universeextensionColumn.setCellRenderer(universeExtColumnRenderer);

    theTable.setVerifyInputWhenFocusTarget(true);

  }

  private void setColumnWidths(final JTable theTable) {

    for (int i = 0; i < UniverseObjectTableModel.myColumnWidths.length; i++) {
      int widt = UniverseObjectTableModel.myColumnWidths[i];

      TableColumn column = theTable.getColumnModel().getColumn(i);

      column.setPreferredWidth(widt);
    }
  }

  private void setComputedColumnWidths(final JTable theTable) {

    for (int i = 0; i < UniverseComputedObjectTableModel.myColumnWidths.length; i++) {
      int widt = UniverseComputedObjectTableModel.myColumnWidths[i];

      TableColumn column = theTable.getColumnModel().getColumn(i);

      column.setPreferredWidth(widt);
    }
  }

  /**
   * Perform refresh to the view
   * 
   */
  public void refresh() {
    dataModelController.getUniverseObjectDataModel().refresh(tmpversioning);
    dataModelController.getUniverseComputedObjectDataModel().refresh(tmpversioning);

    // Get the universe extensions
    this.extensions = computedObjectdataModel.getUniverseExtensions(tmpversioning.getVersionid());

    // Get the select options
    this.selectOptions = computedObjectdataModel.getUniverseFormulas(tmpversioning.getVersionid());
    Arrays.sort(this.selectOptions);

    UniverseObjectTableModel utm = dataModelController.getUniverseObjectDataModel().getTableModel();
    addTableModelListener(utm, new TableOSelectionListener());
    setModel(universeObjectT, utm);
    setColumnEditors(universeObjectT);
    setColumnRenderers(universeObjectT);
    setColumnWidths(universeObjectT);
    tableOChanged = false;

    UniverseComputedObjectTableModel ctm = dataModelController.getUniverseComputedObjectDataModel().getTableModel();
    setModel(universeCompT, ctm);
    addTableModelListener(ctm, new TableCSelectionListener());
    addTableModelListener(ctm, new ParametersKeyValuesChangeListener(universeCompT));
    setComputedColumnEditors(universeCompT);
    setComputedColumnRenderers(universeCompT);
    setColumnWidths(universeCompT);
    tableCChanged = false;

    setSaveEnabled(false);
    setCancelEnabled(false);
    // frame.repaint();
    handleButtons();
  }

  protected String getVersionId() {
    if (tmpversioning != null) {
      return this.tmpversioning.getVersionid();
    } else {
      return null;
    }
  }

  protected UniverseComputedObjectDataModel getDataModel() {
    return this.dataModelController.getUniverseComputedObjectDataModel();
  }

  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> save() {

    logger.log(Level.INFO, "Save UniverseTable");
    final Task<Void, Void> SaveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    SaveTask.setInputBlocker(new BusyIndicatorInputBlocker(SaveTask, busyIndicator));

    return SaveTask;
  }

  private class SaveTask extends Task<Void, Void> {

    public SaveTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "Save UniverseObject and UniverseComputedObject");

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getUniverseObjectDataModel().save();
        dataModelController.getUniverseComputedObjectDataModel().save();
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {

        logger.warning("Error saving UniverseObject and UniverseComputedObject" + e);

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

      tableOChanged = false;
      tableCChanged = false;
      setSaveEnabled(false);
      setCancelEnabled(false);
      getParentAction("enableTabs").actionPerformed(null);
      return null;
    }
  }

  private class DiscardTask extends Task<Void, Void> {

    public DiscardTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.info("Discard Edit UniverseObject");
      getParentAction("enableTabs").actionPerformed(null);
      return null;
    }
  }

  @Action(enabledProperty = "cancelEnabled")
  public Task<Void, Void> discard() {
    final Task<Void, Void> DiscardTask = new DiscardTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    DiscardTask.setInputBlocker(new BusyIndicatorInputBlocker(DiscardTask, busyIndicator));

    return DiscardTask;
  }

  @Action
  public void close() {
    logger.log(Level.INFO, "close");
    frame.dispose();
  }

  // Universe table mouse menu
  @Action
  public void addemptyo() {
    logger.fine("ADD " + dataModelController.getUniverseObjectDataModel().getTableModel().getRowCount());
    if (universeObjectT.getSelectedRows().length == 0) {
      dataModelController.getUniverseObjectDataModel().getTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
          universeObjectT.getSelectedRow());
    } else {
      int[] tmp = universeObjectT.getSelectedRows().clone();
      for (int i = universeObjectT.getSelectedRows().length - 1; i >= 0; i--) {
        dataModelController.getUniverseObjectDataModel().getTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
            tmp[i]);
      }
    }
    dataModelController.getUniverseObjectDataModel().getTableModel().fireTableDataChanged();
  }

  // Universe table mouse menu
  @Action
  public void addemptyc() {
    logger.fine("ADD " + dataModelController.getUniverseComputedObjectDataModel().getTableModel().getRowCount());

    // if (universeCompT.getSelectedRows().length == 0) {
    // dataModelController.getUniverseComputedObjectDataModel()
    // .getTableModel().addEmptyNewRow(
    // tmpversioning.getVersionid(),
    // universeCompT.getSelectedRow());
    // } else {
    // int[] tmp = universeCompT.getSelectedRows().clone();
    // for (int i = universeCompT.getSelectedRows().length - 1; i >= 0; i--)
    // {
    // dataModelController.getUniverseComputedObjectDataModel()
    // .getTableModel().addEmptyNewRow(
    // tmpversioning.getVersionid(), tmp[i]);
    // }
    // }

    // Add a new row to the model
    dataModelController.getUniverseComputedObjectDataModel().getTableModel().addEmptyNewRow(
        tmpversioning.getVersionid());

    // No need to fire as it will be done in the model.
    // dataModelController.getUniverseComputedObjectDataModel()
    // .getTableModel().fireTableDataChanged();
  }

  @Action
  public void removeo() {

    for (int i = universeObjectT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseObjectDataModel().getTableModel().removeRow(universeObjectT.getSelectedRows()[i]);
    }
    // No need to fire as it will be done in the model.
    //
    // dataModelController.getUniverseObjectDataModel().getTableModel()
    // .fireTableDataChanged();
  }

  @Action
  public void removec() {

    for (int i = universeCompT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseComputedObjectDataModel().getTableModel().removeRow(
          universeCompT.getSelectedRows()[i]);
    }
    // No need to fire as it will be done in the model.
    // dataModelController.getUniverseComputedObjectDataModel()
    // .getTableModel().fireTableDataChanged();
  }

  @Action
  public void duplicaterowo() {

    for (int i = universeObjectT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseObjectDataModel().getTableModel().duplicateRow(
          universeObjectT.getSelectedRows()[i]);
    }
    // No need to fire as it will be done in the model.
    // dataModelController.getUniverseObjectDataModel().getTableModel()
    // .fireTableDataChanged();
  }

  @Action
  public void duplicaterowc() {

    for (int i = universeCompT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseComputedObjectDataModel().getTableModel().duplicateRow(
          universeCompT.getSelectedRows()[i]);
    }
    // No need to fire as it will be done in the model.
    // dataModelController.getUniverseComputedObjectDataModel()
    // .getTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowupo() {

    for (int i = 0; i < universeObjectT.getSelectedRows().length; i++) {
      dataModelController.getUniverseObjectDataModel().getTableModel().moveupRow(universeObjectT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseObjectDataModel().getTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowupc() {

    for (int i = 0; i < universeCompT.getSelectedRows().length; i++) {
      dataModelController.getUniverseComputedObjectDataModel().getTableModel().moveupRow(
          universeCompT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseComputedObjectDataModel().getTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowdowno() {

    for (int i = universeObjectT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseObjectDataModel().getTableModel()
          .movedownRow(universeObjectT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseObjectDataModel().getTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowdownc() {

    for (int i = universeCompT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseComputedObjectDataModel().getTableModel().movedownRow(
          universeCompT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseComputedObjectDataModel().getTableModel().fireTableDataChanged();
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
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

  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  // ************************************************
  // Universe Class popup menu
  // ************************************************
  private JPopupMenu createOPopup(MouseEvent e) {
    JPopupMenu popupUC;
    JMenuItem miUC;
    popupUC = new JPopupMenu();

    int selected = universeObjectT.getSelectedRow();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removeo"));
        miUC.setText("Remove row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate rows");
        miUC.setAction(getAction("duplicaterowo"));
        miUC.setText("Duplicate row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move up row");
        miUC.setAction(getAction("moverowupo"));
        miUC.setText("Move up row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move down row");
        miUC.setAction(getAction("moverowdowno"));
        miUC.setText("Move down row");
        popupUC.add(miUC);
      }

      miUC = new JMenuItem("Add Empty");
      miUC.setText("Add empty row");
      miUC.setAction(getAction("addemptyo"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);

    } else {

      miUC = new JMenuItem("Add Empty");
      miUC.setAction(getAction("addemptyo"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);
    }

    popupUC.setOpaque(true);
    popupUC.setLightWeightPopupEnabled(true);

    return popupUC;
  }

  // ************************************************
  // Universe Class popup menu
  // ************************************************
  private JPopupMenu createCPopup(MouseEvent e) {
    JPopupMenu popupUC;
    JMenuItem miUC;
    popupUC = new JPopupMenu();

    int selected = universeCompT.getSelectedRow();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removec"));
        miUC.setText("Remove row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate rows");
        miUC.setAction(getAction("duplicaterowc"));
        miUC.setText("Duplicate row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move up row");
        miUC.setAction(getAction("moverowupc"));
        miUC.setText("Move up row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move down row");
        miUC.setAction(getAction("moverowdownc"));
        miUC.setText("Move down row");
        popupUC.add(miUC);

      }

      miUC = new JMenuItem("Add Empty");
      miUC.setText("Add empty row");
      miUC.setAction(getAction("addemptyc"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);

    } else {

      miUC = new JMenuItem("Add Empty");
      miUC.setAction(getAction("addemptyc"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);
    }

    popupUC.setOpaque(true);
    popupUC.setLightWeightPopupEnabled(true);

    return popupUC;
  }

  private void displayOMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createOPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private void displayCMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createCPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(boolean cancelEnabled) {
    boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  private void handleButtons() {
    boolean saveIsEnabled = false;
    setScreenMessage(null);

    try {
      UniverseObjectDataModel model = dataModelController.getUniverseObjectDataModel();
      errors = model.validateData();

      UniverseComputedObjectDataModel cmodel = dataModelController.getUniverseComputedObjectDataModel();
      errors.addAll(cmodel.validateData());

    } catch (Exception e) {
      errors.clear();
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    if (errors.size() > 0) {
      setScreenMessage(errors);
    }

    boolean hasErrors = errors.size() > 0;
    if (tableOChanged || tableCChanged) {
      saveIsEnabled = hasErrors ? false : true;
      getParentAction("disableTabs").actionPerformed(null);
      this.setCancelEnabled(true);
    } else {
      this.setCancelEnabled(false);
    }
    this.setSaveEnabled(saveIsEnabled);
  }

  /**
   * Error handling, sets the message for errorMessageComponent
   * 
   * @param message
   */
  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class TableOSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableOChanged = true;
      handleButtons();
      setColumnRenderers(universeObjectT);
      setColumnEditors(universeObjectT);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayOMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayOMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayOMenu(e);
    }
  }

  private class TableCSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableCChanged = true;
      handleButtons();
      setComputedColumnRenderers(universeCompT);
      setComputedColumnEditors(universeCompT);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayCMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayCMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayCMenu(e);
    }
  }

  private class ParametersKeyValuesChangeListener implements TableModelListener {

    JTable table;

    TableRows previousValues;

    public ParametersKeyValuesChangeListener(JTable table) {
      this.table = table;
      refreshPreviousValues();
    }

    public void tableChanged(TableModelEvent e) {

      if (e == null) {
        return;
      }

      int firstRow = e.getFirstRow();
      int lastRow = e.getLastRow();
      int column = e.getColumn();
      int type = e.getType();

      switch (type) {
      case TableModelEvent.UPDATE:
        switch (column) {
        case UniverseComputedObjectTableModel.classnameColumnIdx:
        case UniverseComputedObjectTableModel.objectnameColumnIdx:
        case UniverseComputedObjectTableModel.universeextensionColumnIdx:
          // Class name, Object name, or UniverseExtension column was
          // changed.
          if (table.getRowCount() == previousValues.getNumberOfRows()) {
            int rowsChanged = lastRow - firstRow + 1;
            for (int i = 0; i < rowsChanged; ++i) {
              int rowIndex = firstRow + i;
              int classNameColIndex = UniverseComputedObjectTableModel.classnameColumnIdx;
              int objectNameColIndex = UniverseComputedObjectTableModel.objectnameColumnIdx;
              int univExtColIndex = UniverseComputedObjectTableModel.universeextensionColumnIdx;
              String currentClassName = (String) table.getValueAt(rowIndex, classNameColIndex);
              String previousClassName = previousValues.getValueAt(rowIndex, classNameColIndex);
              String currentObjectName = (String) table.getValueAt(rowIndex, objectNameColIndex);
              String previousObjectName = previousValues.getValueAt(rowIndex, objectNameColIndex);
              String currentUnivExt = (String) table.getValueAt(rowIndex, univExtColIndex);
              String previousUnivExt = previousValues.getValueAt(rowIndex, univExtColIndex);

              changeKeysForParameters(previousClassName, previousObjectName, previousUnivExt, currentClassName,
                  currentObjectName, currentUnivExt);
            }
          } else {
            logger.warning("The number of rows in computed objects table "
                + "seems to have changed, without all columns changed! " + "This should not be possible.");
          }
          break;

        default:

          break;
        }
        break;
      case TableModelEvent.DELETE:
        // Rows were removed.
        int deletedRows = lastRow - firstRow + 1;
        for (int i = 0; i < deletedRows; ++i) {
          int rowIndex = firstRow + i;
          String prevClassName = previousValues.getValueAt(rowIndex,
              UniverseComputedObjectTableModel.classnameColumnIdx);
          String prevObjectName = previousValues.getValueAt(rowIndex,
              UniverseComputedObjectTableModel.objectnameColumnIdx);
          String prevUnivExt = previousValues.getValueAt(rowIndex,
              UniverseComputedObjectTableModel.universeextensionColumnIdx);
          removeParametersWithKeys(prevClassName, prevObjectName, prevUnivExt);
        }
        break;

      default:

        break;
      }
      // Finally, update the previous values
      refreshPreviousValues();
    }

    private void refreshPreviousValues() {
      int numberOfColumns = this.table.getColumnCount();
      int numberOfRows = this.table.getRowCount();

      this.previousValues = new TableRows(numberOfColumns);

      for (int rowIndex = 0; rowIndex < numberOfRows; ++rowIndex) {
        previousValues.addRow(rowIndex);
        TableRow row = previousValues.getRow(rowIndex);
        for (int colIndex = 0; colIndex < numberOfColumns; ++colIndex) {
          String cellValue = (String) table.getValueAt(rowIndex, colIndex);
          row.setValueAt(colIndex, cellValue);
        }
      }
    }

    /**
     * Update the universe parameter object with the new key values from the
     * universe computed object.
     * 
     * @param prevClassName
     * @param prevObjectName
     * @param prevUnivExt
     * @param newClassName
     * @param newObjectName
     * @param newUnivExt
     */
    private void changeKeysForParameters(String prevClassName, String prevObjectName, String prevUnivExt,
        String newClassName, String newObjectName, String newUnivExt) {
      String versionId = getVersionId();
      UniverseComputedObjectTableModel tableModel = getDataModel().getTableModel();
      Vector<Universeparameters> universeParameters = tableModel.getUniverseParameters(versionId, prevClassName,
          prevObjectName, prevUnivExt);
      Iterator<Universeparameters> uPIterator = universeParameters.iterator();
      while (uPIterator.hasNext()) {
        Universeparameters parameters = uPIterator.next();
        parameters.setClassname(newClassName);
        parameters.setObjectname(newObjectName);
        parameters.setUniverseextension(newUnivExt);
      }
    }

    private void removeParametersWithKeys(String className, String objectName, String univExt) {
      String versionId = getVersionId();
      UniverseComputedObjectTableModel tableModel = getDataModel().getTableModel();
      Vector<Universeparameters> universeParameters = tableModel.getUniverseParameters(versionId, className,
          objectName, univExt);

      // TODO: What is happening here???

    }

    private class TableRow {

      private String[] cellValues;

      public TableRow(int columns) {
        this.cellValues = new String[columns];
      }

      public String getValueAt(int column) {
        return this.cellValues[column];
      }

      public void setValueAt(int column, String value) {
        this.cellValues[column] = value;
      }
    }

    private class TableRows {

      private Vector<TableRow> tableRows;

      private int numberOfColumns;

      public TableRows(int numberOfColumns) {
        this.tableRows = new Vector<TableRow>();
        this.numberOfColumns = numberOfColumns;
      }

      public void addRow(int index) {
        this.tableRows.insertElementAt(new TableRow(this.numberOfColumns), index);
      }

      public TableRow getRow(int index) {
        return this.tableRows.elementAt(index);
      }

      public String getValueAt(int rowIndex, int columnIndex) {
        TableRow row = this.getRow(rowIndex);
        return row.getValueAt(columnIndex);
      }

      public void setValueAt(int rowIndex, int columnIndex, String value) {
        TableRow row = this.getRow(rowIndex);
        row.setValueAt(columnIndex, value);
      }

      public int getNumberOfRows() {
        return this.tableRows.size();
      }

      public int getNumberOfColumns() {
        return this.numberOfColumns;
      }
    }
  }

  private class UniverseComputedObjectTable extends JTable {

    public static final String EMPTY_CELL_VALUE = "";

    private boolean editable;

    public UniverseComputedObjectTable(boolean editable) {
      super();
      this.editable = editable;
    }

    public boolean isEditable() {
      return this.editable;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      if (this.editable) {
        return true;
      } else {
        if (column == UniverseComputedObjectTableModel.descriptionColumnIdx) {
          return true;
        }
        if (column == UniverseComputedObjectTableModel.whereColumnIdx) {
          return true;
        }
        if (column == UniverseComputedObjectTableModel.parametersColumnIdx) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

  private class UniverseObjectTable extends JTable {

    public static final String EMPTY_CELL_VALUE = "";

    private boolean editable;

    public UniverseObjectTable(boolean editable) {
      super();
      this.editable = editable;
    }

    public boolean isEditable() {
      return this.editable;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      if (this.editable) {
        return true;
      } else {
        if (column == UniverseObjectTableModel.descriptionColumnIdx) {
          return true;
        }
        if (column == UniverseObjectTableModel.whereColumnIdx) {
          return true;
        }
        if (column == UniverseObjectTableModel.selectColumnIdx) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

}
