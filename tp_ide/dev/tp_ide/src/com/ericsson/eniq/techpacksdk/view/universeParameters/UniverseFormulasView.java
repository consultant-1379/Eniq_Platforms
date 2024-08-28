package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellRenderer;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class UniverseFormulasView extends GenericUniverseView {

  private static final Logger logger = Logger.getLogger(GenericUniverseView.class.getName());

  private UniverseFormulasTable inheritedFormulasTable;

  private String techpackType;

  public UniverseFormulasView(SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {

    super(application, dataModelController, versioning, editable, parentPanel, frame);
    this.editable = editable;
    // Store the tech pack type;
    this.techpackType = versioning.getTechpack_type();

    // Calculate preferred size for tables
    int width = 0;
    for (int colWidt : getTableModel().columnWidtArr) {
      width += colWidt;
    }
    Dimension tableDim = new Dimension(width, 100);

    // Create main panel
    JPanel mainPanel = new JPanel(new GridBagLayout());
    JScrollPane mainPanelS = new JScrollPane(mainPanel);

    // Create grid bag constraints object, and set the initial values.
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // Create the inherited formulas label and table (only for non-base
    // techpacks).
    if (!this.techpackType.equalsIgnoreCase("base")) {
      // Create label
      JLabel inheritedFormulasLabel = new JLabel("Defined in base TechPack:");
      c.fill = GridBagConstraints.NONE;
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = 0;
      mainPanel.add(inheritedFormulasLabel, c);

      // Create table
      this.inheritedFormulasTable = createInheritedFormulasTable();
      inheritedFormulasTable.setName("UniverseFormulasInheritedFormulasTable");

      c.fill = GridBagConstraints.NONE;
      c.gridx = 0;
      c.gridy = 1;
      c.weightx = 0;
      JScrollPane inheritedFormulasTableScrollPane = new JScrollPane(this.inheritedFormulasTable);
      inheritedFormulasTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
      inheritedFormulasTableScrollPane.setPreferredSize(tableDim);
      mainPanel.add(inheritedFormulasTableScrollPane, c);
    }

    // -- Create the formulas label ----------------------------------------
    JLabel formulasLabel = new JLabel("Defined in this TechPack:");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    mainPanel.add(formulasLabel, c);

    // -- Create the formulas table ----------------------------------------
    this.myTable = createFormulasTable();
    this.myTable.setName("UniverseFormulasTable");

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    JScrollPane formulasTableScrollPane = new JScrollPane(this.myTable);
    formulasTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
    formulasTableScrollPane.setPreferredSize(tableDim);
    mainPanel.add(formulasTableScrollPane, c);

    addButtons(mainPanelS);
  }

  private UniverseFormulasTable createInheritedFormulasTable() {
    UniverseFormulasTable result;

    result = new UniverseFormulasTable(false);
    result.setEnabled(true);

    GenericUniverseTableModel<Universeformulas> inheritedFormulasTableModel = null;
    inheritedFormulasTableModel = getInheritedFormulasTableModel();
    setModel(result, inheritedFormulasTableModel);

    setColumnWidths(result);
    setColumnEditors(result);
    setColumnRenderers(result);

    return result;
  }

  private UniverseFormulasTable createFormulasTable() {
    UniverseFormulasTable result = new UniverseFormulasTable(this.editable);

    UniverseFormulasTableModel formulasTableModel = (UniverseFormulasTableModel) getDataModel().getTableModel();
    setModel(result, formulasTableModel);

    addTableModelListener(formulasTableModel, new UniverseFormulasTableSelectionListener());

    setColumnWidths(result);
    setColumnEditors(result);
    setColumnRenderers(result);

    result.setEnabled(true); // The formulas table must be set to enabled,
    // regardless of
    // the editability status of the view, since the formula
    // cell has to be expandable even if it cannot be edited.
    if (this.editable) {
      result.addMouseListener(new TableSelectionListener());
      result.getTableHeader().addMouseListener(new TableSelectionListener());
    }

    return result;
  }

  @Override
  public void refresh() {
    getDataModel().refresh(tmpversioning);

    // Update the inherited formulas table (for non-base techpacks).
    if (!this.techpackType.equalsIgnoreCase("base")) {
      GenericUniverseTableModel<Universeformulas> inheritedFormulasTableModel = null;
      inheritedFormulasTableModel = getInheritedFormulasTableModel();
      setModel(this.inheritedFormulasTable, inheritedFormulasTableModel);
      setColumnWidths(this.inheritedFormulasTable);
      setColumnEditors(this.inheritedFormulasTable);
      setColumnRenderers(this.inheritedFormulasTable);
    }

    // Update the formulas table
    GenericUniverseTableModel<Universeformulas> tableModel = getTableModel();
    addTableModelListener(tableModel, new UniverseFormulasTableSelectionListener());
    setModel(myTable, tableModel);
    setColumnWidths(myTable);
    setColumnEditors(myTable);
    setColumnRenderers(myTable);

    tableChanged = false;
    setSaveEnabled(false);
    setCancelEnabled(false);
    frame.repaint();
    handleButtons();
  }

  @Override
  public GenericUniverseDataModel getDataModel() {
    return this.dataModelController.getUniverseFormulasDataModel();
  }

  @SuppressWarnings("unchecked")
  @Override
  public GenericUniverseTableModel<Universeformulas> getTableModel() {
    return this.dataModelController.getUniverseFormulasDataModel().getTableModel();
  }

  @SuppressWarnings("unchecked")
  public GenericUniverseTableModel<Universeformulas> getInheritedFormulasTableModel() {
    GenericUniverseTableModel<Universeformulas> result = null;

    UniverseFormulasDataModel dataModel = this.dataModelController.getUniverseFormulasDataModel();
    String baseTechpackVersionId = dataModel.versioning.getBasedefinition();

    Versioning baseTechpackVersion = null;
    if (isValidTechPackVersionId(baseTechpackVersionId)) {
      try {
        baseTechpackVersion = new Versioning(dataModel.etlRock, baseTechpackVersionId);
        result = dataModel.createTableModel(baseTechpackVersion);
      } catch (Exception e) {
        logger.warning("Unable to create a table model.");
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
        // Create a table model with empty vector as data
        Vector<Universeformulas> universeFormulasVector = new Vector<Universeformulas>();
        result = new UniverseFormulasTableModel(universeFormulasVector, dataModel.etlRock);
      }
    } else {
      // Create a table model with empty vector as data
      Vector<Universeformulas> universeFormulasVector = new Vector<Universeformulas>();
      result = new UniverseFormulasTableModel(universeFormulasVector, dataModel.etlRock);
    }
    return result;
  }

  private boolean isValidTechPackVersionId(String versionId) {
    return (versionId != null && versionId.trim() != "");
  }

  @Override
  public void setColumnEditors(JTable theTable) {

    GenericUniverseTableModel<Universeformulas> tm = getTableModel();

    TableColumnModel tableColumnModel = theTable.getColumnModel();

    // Name column
    final TableColumn nameColumn = tableColumnModel.getColumn(UniverseFormulasTableModel.NAME_COLUMN_INDEX);
    LimitedSizeCellEditor nameColumnEditor = new LimitedSizeCellEditor(tm
        .getColumnWidt(UniverseFormulasTableModel.NAME_COLUMN_INDEX), Universeformulas.getNameColumnSize(), true);
    nameColumn.setCellEditor(nameColumnEditor);

    // Formula column
    final TableColumn formulaColumn = tableColumnModel.getColumn(UniverseFormulasTableModel.FORMULA_COLUMN_INDEX);
    FormulaCellEditor formulaColumnEditor = new FormulaCellEditor(tm
        .getColumnWidt(UniverseFormulasTableModel.FORMULA_COLUMN_INDEX), Universeformulas.getFormulaColumnSize(), true,
        ((UniverseFormulasTable) theTable).isEditable());
    formulaColumn.setCellEditor(formulaColumnEditor);

    // Object type column
    final TableColumn objectTypeColumn = tableColumnModel
        .getColumn(UniverseFormulasTableModel.OBJECT_TYPE_COLUMN_INDEX);
    JComboBox objectTypeColumnComboBox = new JComboBox(UniverseFormulasDataModel.OBJECT_TYPE_OPTIONS);
    DefaultCellEditor objectTypeColumnEditor = new DefaultCellEditor(objectTypeColumnComboBox);
    objectTypeColumn.setCellEditor(objectTypeColumnEditor);

    // Qualification column
    final TableColumn qualificationColumn = tableColumnModel
        .getColumn(UniverseFormulasTableModel.QUALIFICATION_COLUMN_INDEX);
    JComboBox qualificationColumnComboBox = new JComboBox(UniverseFormulasDataModel.QUALIFICATION_OPTIONS);
    DefaultCellEditor qualificationColumnEditor = new DefaultCellEditor(qualificationColumnComboBox);
    qualificationColumn.setCellEditor(qualificationColumnEditor);

    // Aggregation column
    final TableColumn aggregationColumn = tableColumnModel
        .getColumn(UniverseFormulasTableModel.AGGREGATION_COLUMN_INDEX);
    JComboBox aggregationComboBox = new JComboBox(UniverseFormulasDataModel.AGGREGATION_OPTIONS);
    DefaultCellEditor aggregationColumnEditor = new DefaultCellEditor(aggregationComboBox);
    aggregationColumn.setCellEditor(aggregationColumnEditor);
  }

  @Override
  public void setColumnRenderers(JTable theTable) {
    GenericUniverseTableModel<Universeformulas> tm = getTableModel();

    TableColumnModel tableColumnModel = theTable.getColumnModel();

    // Name column
    final TableColumn nameColumn = tableColumnModel.getColumn(UniverseFormulasTableModel.NAME_COLUMN_INDEX);

    LimitedSizeTextTableCellRenderer nameColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeformulas
        .getNameColumnSize(), true);
    nameColumn.setCellRenderer(nameColumnRenderer);

    // Formula column
    final TableColumn formulaColumn = tableColumnModel.getColumn(UniverseFormulasTableModel.FORMULA_COLUMN_INDEX);

    LimitedSizeTextTableCellRenderer formulaColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeformulas
        .getFormulaColumnSize(), true);
    formulaColumn.setCellRenderer(formulaColumnRenderer);

    // Object type column
    final TableColumn objectTypeColumn = tableColumnModel
        .getColumn(UniverseFormulasTableModel.OBJECT_TYPE_COLUMN_INDEX);
    LimitedSizeCellRenderer objectTypeColumnRenderer = new LimitedSizeCellRenderer(tm
        .getColumnWidt(UniverseFormulasTableModel.OBJECT_TYPE_COLUMN_INDEX), 255, // TODO
        true);
    objectTypeColumn.setCellRenderer(objectTypeColumnRenderer);

    // Qualification column
    final TableColumn qualificationColumn = tableColumnModel
        .getColumn(UniverseFormulasTableModel.QUALIFICATION_COLUMN_INDEX);
    LimitedSizeTextTableCellRenderer qualificationColumnRenderer = new LimitedSizeTextTableCellRenderer(
        Universeformulas.getQualificationColumnSize(), true);
    qualificationColumn.setCellRenderer(qualificationColumnRenderer);

    // Aggregation column
    final TableColumn aggregationColumn = tableColumnModel
        .getColumn(UniverseFormulasTableModel.AGGREGATION_COLUMN_INDEX);
    LimitedSizeTextTableCellRenderer aggregationColumnRenderer = new LimitedSizeTextTableCellRenderer(Universeformulas
        .getAggregationColumnSize(), false);
    aggregationColumn.setCellRenderer(aggregationColumnRenderer);
  }

  @Action
  public void duplicaterowu() {

    logger.log(Level.INFO, "Duplicate row in Universe Formulas table");
    for (int i = myTable.getSelectedRows().length - 1; i >= 0; i--) {
      getDataModel().getTableModel().duplicateRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }

  @Action(enabledProperty = "saveEnabled")
  public Task save() {

    setDataModel(dataModelController.getUniverseFormulasDataModel());

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
    if (myTable.getSelectedRows().length == 0) {
      getDataModel().getTableModel().addEmptyNewRow(tmpversioning.getVersionid(), myTable.getSelectedRow());
    } else {
      int[] tmp = myTable.getSelectedRows().clone();
      for (int i = myTable.getSelectedRows().length - 1; i >= 0; i--) {
        getDataModel().getTableModel().addEmptyNewRow(tmpversioning.getVersionid(), tmp[i]);
      }
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }

  @Action
  public void removeu() {

    logger.log(Level.INFO, "Remove row from Universe Formulas table");
    for (int i = myTable.getSelectedRows().length - 1; i >= 0; i--) {
      getDataModel().getTableModel().removeRow(myTable.getSelectedRows()[i]);
    }
    dataModelController.getUniverseConditionDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowupu() {

    logger.log(Level.INFO, "Move row up row in Universe Formulas table");
    for (int i = 0; i < myTable.getSelectedRows().length; i++) {
      getDataModel().getTableModel().moveupRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowdownu() {

    logger.log(Level.INFO, "Move row down row in Universe Formulas table");
    for (int i = myTable.getSelectedRows().length - 1; i >= 0; i--) {
      getDataModel().getTableModel().movedownRow(myTable.getSelectedRows()[i]);
    }
    getDataModel().getTableModel().fireTableDataChanged();
  }

  private class UniverseFormulasTableSelectionListener extends TableSelectionListener {

    public void tableChanged(TableModelEvent e) {
      if (e != null) {
        UniverseFormulasTableModel tableModel = (UniverseFormulasTableModel) myTable.getModel();

        // Check whether or not qualification has changed from or to "measure",
        // and enable or disable the aggregation cell.
        int firstChangedRowIndex = e.getFirstRow();
        int lastChangedRowIndex = e.getLastRow();
        int changedColumnIndex = e.getColumn();
        // We are only interested in changes to qualification column.
        // Therefore, if all columns are changed, set qualification
        // column as the changed column.
        if (changedColumnIndex == TableModelEvent.ALL_COLUMNS) {
          changedColumnIndex = UniverseFormulasTableModel.QUALIFICATION_COLUMN_INDEX;
        }
        // Ensure that lastChangedRowIndex < number of rows in the table.
        // TODO Should this be needed?
        if (lastChangedRowIndex > tableModel.getRowCount() - 1) {
          lastChangedRowIndex = tableModel.getRowCount() - 1;
        }

        for (int row = firstChangedRowIndex; row <= lastChangedRowIndex; ++row) {
          if (changedColumnIndex == UniverseFormulasTableModel.QUALIFICATION_COLUMN_INDEX) {
            String cellValue = (String) tableModel.getValueAt(row, changedColumnIndex);
            if (cellValue.equals("measure")) {
              // Qualification is set to "measure", enable aggregation
              // cell.
              int column = UniverseFormulasTableModel.AGGREGATION_COLUMN_INDEX;
              String aggregationCellValue = (String) tableModel.getValueAt(row, column);
              if (aggregationCellValue.equals(UniverseFormulasTable.EMPTY_CELL_VALUE)) {
                // Since the aggregation column value was empty, the previous
                // qualification
                // column value was not measurement. Therefore set the default
                // value for the
                // aggregation cell.
                String defaultOption = UniverseFormulasDataModel.AGGREGATION_OPTIONS[0];
                tableModel.setValueAt(defaultOption, row, column);
              } else {
                // Since the aggregation column was nonempty, the previous
                // qualification value
                // was measurement. Therefore the aggregation value should not
                // be changed.
              }
            } else {
              // Qualification is not set to "measure", set aggregation cell's
              // value to empty.
              int column = UniverseFormulasTableModel.AGGREGATION_COLUMN_INDEX;
              tableModel.setValueAt(UniverseFormulasTable.EMPTY_CELL_VALUE, row, column);
            }
          }
        }
      }
      setColumnWidths(myTable);
      super.tableChanged(e);
    }
  }

  private class UniverseFormulasTable extends JTable {

    public static final String EMPTY_CELL_VALUE = "";

    private boolean editable;

    public UniverseFormulasTable(boolean editable) {
      super();
      this.editable = editable;
    }

    public boolean isEditable() {
      return this.editable;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      if (this.editable) {
        if (column == UniverseFormulasTableModel.AGGREGATION_COLUMN_INDEX) {
          int qualificationColumnIndex = UniverseFormulasTableModel.QUALIFICATION_COLUMN_INDEX;
          String qualificationCellValue = (String) this.getModel().getValueAt(row, qualificationColumnIndex);
          return qualificationCellValue.equals("measure");
        } else {
          return true;
        }
      } else {
        if (column == UniverseFormulasTableModel.FORMULA_COLUMN_INDEX) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

}
