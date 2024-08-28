package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.repository.dwhrep.Universejoin;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class UniverseJoinView extends GenericUniverseView {

  private static final Logger logger = Logger.getLogger(UniverseJoinView.class.getName());  

  public UniverseJoinView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(application, dataModelController, versioning, editable, parentPanel, frame);
    
    // ************** Text panel **********************
    int width = 0;
    for (int colWidt : getTableModel().columnWidtArr)
      width += colWidt;
    this.editable = editable;
    Dimension tableDim = new Dimension(width, 300);

    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // Universe join
    JLabel universecondition = new JLabel("Universe join");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(universecondition, c);

    myTable = new UniverseJoinTable(editable);

    GenericUniverseTableModel tm = getDataModel().getTableModel();
    setModel(myTable, tm);

    addTableModelListener(tm, new TableSelectionListener());

    myTable.setBackground(WHITE);

    //myTable.setEnabled(editable);

    if (editable) {
      myTable.addMouseListener(new TableSelectionListener());
      myTable.getTableHeader().addMouseListener(new TableSelectionListener());
    }

    setColumnWidths(myTable);
    setColumnEditors(myTable);
    setColumnRenderers(myTable);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JScrollPane scrollPane = new JScrollPane(myTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    addButtons(txtPanelS);
  }

  @SuppressWarnings("unchecked")
  public GenericUniverseTableModel<Universejoin> getTableModel() {
    return dataModelController.getUniverseJoinDataModel().getTableModel();
  }
  public GenericUniverseDataModel getDataModel() {
    return dataModelController.getUniverseJoinDataModel();
  }

  /**
   * Method for setting the column editor of the Description and IsElement
   * columns.
   */
  public void setColumnEditors(final JTable theTable) {
    
    GenericUniverseTableModel<Universejoin> tm = getTableModel();

    // Set editor for sourcetable
    final TableColumn sourcetableColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.sourcetableColumnIdx);
    LimitedSizeCellEditor sourcetableColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.sourcetableColumnIdx), Universejoin.getSourcetableColumnSize(), true);
    sourcetableColumn.setCellEditor(sourcetableColumnEditor);

    // Set editor for sourcelevel
    final TableColumn sourcelevelColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.sourcelevelColumnIdx);
    LimitedSizeCellEditor sourcelevelColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.sourcelevelColumnIdx), Universejoin.getSourcelevelColumnSize(), false);
    sourcelevelColumn.setCellEditor(sourcelevelColumnEditor);

    // Set editor for sourcecolumn
    final TableColumn sourcecolumnColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.sourcecolumnColumnIdx);
    LimitedSizeCellEditor sourcecolumnColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.sourcecolumnColumnIdx), Universejoin.getSourcecolumnColumnSize(), true);
    sourcecolumnColumn.setCellEditor(sourcecolumnColumnEditor);

    // Set editor for targettable
    final TableColumn targettableColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.targettableColumnIdx);
    LimitedSizeCellEditor targettableColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.targettableColumnIdx), Universejoin.getTargettableColumnSize(), true);
    targettableColumn.setCellEditor(targettableColumnEditor);

    // Set editor for targetlevel
    final TableColumn targetlevelColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.targetlevelColumnIdx);
    LimitedSizeCellEditor targetlevelColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.targetlevelColumnIdx), Universejoin.getTargetlevelColumnSize(), false);
    targetlevelColumn.setCellEditor(targetlevelColumnEditor);

    // Set editor for targetcolumn
    final TableColumn targetcolumnColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.targetcolumnColumnIdx);
    LimitedSizeCellEditor targetcolumnColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.targetcolumnColumnIdx), Universejoin.getTargetcolumnColumnSize(), true);
    targetcolumnColumn.setCellEditor(targetcolumnColumnEditor);

    // Set editor for expression
    final TableColumn expressionColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.expressionColumnIdx);
    LimitedSizeCellEditor expressionColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.expressionColumnIdx), Universejoin.getExpressionColumnSize(), false);
    expressionColumn.setCellEditor(expressionColumnEditor);

    // Set editor for cardinality
    final TableColumn cardinalityColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.cardinalityColumnIdx);
    LimitedSizeCellEditor cardinalityColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.cardinalityColumnIdx), Universejoin.getCardinalityColumnSize(), false);
    cardinalityColumn.setCellEditor(cardinalityColumnEditor);

    // Set editor for context
    final TableColumn contextColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.contextColumnIdx);
    DescriptionCellEditor contextColumnEditor = new DescriptionCellEditor(Universejoin.getContextColumnSize(), false, this.editable);
    contextColumn.setCellEditor(contextColumnEditor);

    // Set editor for excludedcontexts
    final TableColumn excludedcontextsColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.excludedcontextsColumnIdx);
    DescriptionCellEditor excludedcontextsColumnEditor = new DescriptionCellEditor(Universejoin.getExcludedcontextsColumnSize(), false, this.editable);
    excludedcontextsColumn.setCellEditor(excludedcontextsColumnEditor);
    
    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.universeextensionColumnIdx);    
    LimitedSizeCellEditor extensionColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseJoinTableModel.targetlevelColumnIdx), Universejoin.getTargetlevelColumnSize(), false);
    universeextensionColumn.setCellEditor(extensionColumnEditor);
    
  }

  /**
   * Method for setting the column renderer. Used to set a renderer for the
   * description field and isElement combo box.
   */
  public void setColumnRenderers(final JTable theTable) {
        
    // Set renderer for sourcetable
    final TableColumn sourcetableColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.sourcetableColumnIdx);
    LimitedSizeTextTableCellRenderer sourcetableColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getSourcetableColumnSize(), true);
    sourcetableColumn.setCellRenderer(sourcetableColumnRenderer);

    // Set renderer for sourcelevel
    final TableColumn sourcelevelColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.sourcelevelColumnIdx);
    LimitedSizeTextTableCellRenderer sourcelevelColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getSourcelevelColumnSize(), false);
    sourcelevelColumn.setCellRenderer(sourcelevelColumnRenderer);

    // Set renderer for sourcecolumn
    final TableColumn sourcecolumnColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.sourcecolumnColumnIdx);
    LimitedSizeTextTableCellRenderer sourcecolumnColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getSourcecolumnColumnSize(), true);
    sourcecolumnColumn.setCellRenderer(sourcecolumnColumnRenderer);

    // Set renderer for targettable
    final TableColumn targettableColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.targettableColumnIdx);
    LimitedSizeTextTableCellRenderer targettableColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getTargettableColumnSize(), true);
    targettableColumn.setCellRenderer(targettableColumnRenderer);

    // Set renderer for targetlevel
    final TableColumn targetlevelColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.targetlevelColumnIdx);
    LimitedSizeTextTableCellRenderer targetlevelColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getTargetlevelColumnSize(), false);
    targetlevelColumn.setCellRenderer(targetlevelColumnRenderer);

    // Set renderer for targetcolumn
    final TableColumn targetcolumnColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.targetcolumnColumnIdx);
    LimitedSizeTextTableCellRenderer targetcolumnColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getTargetcolumnColumnSize(), true);
    targetcolumnColumn.setCellRenderer(targetcolumnColumnRenderer);

    // Set renderer for expression
    final TableColumn expressionColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.expressionColumnIdx);
    LimitedSizeTextTableCellRenderer expressionColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getExpressionColumnSize(), false);
    expressionColumn.setCellRenderer(expressionColumnRenderer);

    // Set renderer for sourcetable
    final TableColumn cardinalityColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.cardinalityColumnIdx);
    LimitedSizeTextTableCellRenderer cardinalityColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getCardinalityColumnSize(),  false);
    cardinalityColumn.setCellRenderer(cardinalityColumnRenderer);

    // Set renderer for context
    final TableColumn contextColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.contextColumnIdx);
    LimitedSizeTextTableCellRenderer contextColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getContextColumnSize(), false);
    contextColumn.setCellRenderer(contextColumnRenderer);

    // Set renderer for excludedcontexts
    final TableColumn excludedcontextsColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.excludedcontextsColumnIdx);
    LimitedSizeTextTableCellRenderer excludedcontextsColumnRenderer = new LimitedSizeTextTableCellRenderer(Universejoin
        .getExcludedcontextsColumnSize(), false);
    excludedcontextsColumn.setCellRenderer(excludedcontextsColumnRenderer);
    
    // Set renderer for universe extensions:
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseJoinTableModel.universeextensionColumnIdx);
    DefaultTableCellRenderer universeExtColumnRenderer = new DefaultTableCellRenderer();
    universeextensionColumn.setCellRenderer(universeExtColumnRenderer);
  }
  
  @Action
  public void duplicaterowu() {

    logger.log(Level.INFO, "duplicate row in UniverseJoin table");
    for(int i = myTable.getSelectedRows().length-1 ; i >= 0 ; i--){
      getDataModel().getTableModel().duplicateRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }

  @Action(enabledProperty = "saveEnabled")
  public Task save() {

    logger.log(Level.INFO, "Save UniverseTable");
    final Task SaveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    SaveTask.setInputBlocker(new BusyIndicatorInputBlocker(SaveTask, busyIndicator));

    return SaveTask;
  }
  @Action(enabledProperty = "cancelEnabled")
  public Task discard() {
    final Task DiscardTask = new DiscardTask(application);
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

  @Action
  public void addemptyu() {
    logger.fine("ADD " + getDataModel().getTableModel().getRowCount());
    if (myTable.getSelectedRows().length == 0){
      getDataModel().getTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
          myTable.getSelectedRow());
    } else {
    int[] tmp = myTable.getSelectedRows().clone();
    for(int i = myTable.getSelectedRows().length-1 ; i >= 0 ; i--){
      getDataModel().getTableModel().addEmptyNewRow(tmpversioning.getVersionid(), tmp[i]);
    }
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }

  @Action
  public void removeu() {

    logger.log(Level.INFO, "remove row from Universe Join table");
    for(int i = myTable.getSelectedRows().length-1 ; i >= 0 ; i--){
      getDataModel().getTableModel().removeRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }
  
  @Action
  public void moverowupu() {

    logger.log(Level.INFO, "Move row up row in UniverseJoin table");
    for(int i = 0 ; i < myTable.getSelectedRows().length ; i++){
      getDataModel().getTableModel().moveupRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }
  
  @Action
  public void moverowdownu() {

    logger.log(Level.INFO, "Move row down row in UniverseJoin table");
    for(int i = myTable.getSelectedRows().length-1 ; i >= 0 ; i--){
      getDataModel().getTableModel().movedownRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }
  
  /* Just to test. Why aren't the actions coming from superclass with @Action annotation?
  public javax.swing.Action getAction(final String actionName) {
    ApplicationContext ac = application.getContext();
    Object[]keys = application.getContext().getActionMap(this).allKeys();
    
    return application.getContext().getActionMap(this).get(actionName);
  }
  */
  
  private class UniverseJoinTable extends JTable {

    public static final String EMPTY_CELL_VALUE = "";

    private boolean editable;

    public UniverseJoinTable(boolean editable) {
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
        if (column == UniverseJoinTableModel.contextColumnIdx) {
          return true;
        } if (column == UniverseJoinTableModel.excludedcontextsColumnIdx) {
          return true;
        }  else {
          return false;
        }
      }
    }
  }  
  
}
