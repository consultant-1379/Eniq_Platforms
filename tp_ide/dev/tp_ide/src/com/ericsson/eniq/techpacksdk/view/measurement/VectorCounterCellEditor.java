package com.ericsson.eniq.techpacksdk.view.measurement;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.application.Application;

import com.ericsson.eniq.component.TableComponent;

import tableTreeUtils.TCTableModel;

/**
 * This class is a customized table cell editor for vector counter
 * 
 * @author eheijun
 */
@SuppressWarnings("serial")
public class VectorCounterCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
  
  /**
   * Application reference
   */
  private Application application;

  /**
   * The table component
   */
  private TableComponent theComp = null;

  /**
   * Current value of the cell
   */
  private TCTableModel vectorcounterTableModel;

  /**
   * Is the tree editable
   */
  private final boolean treeEditable;

  /**
   * Constructor.
   * 
   * @param vectorcounterTableModel 
   * 
   * @param editable
   *          true if the tree is editable.
   */
  public VectorCounterCellEditor(Application application, boolean editable) {
    this.application = application;
    this.treeEditable = editable;
  }

  /**
   * This returns the table component as the editor to the table. This is also
   * where we get the initial value for the components text field.
   * 
   * @param table
   * @param value
   * @param isSelected
   * @param row
   * @param col
   * @return the editor component
   */
  public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
      final int row, final int col) {

    vectorcounterTableModel = (TCTableModel) value;
    theComp = new TableComponent(application, "VectorCounter", vectorcounterTableModel, 0, treeEditable);

    // Add the action lister, so that we can catch the description action
    // when the user updates the cell value.
    theComp.addActionListener(this);

    // Set the component enabled based on the tree enabled status.
    // theComp.setEnabled(this.treeEditable);

    return theComp;
  }

  /**
   * Overridden version of the method. Returns the current text value.
   * 
   * @return the current text value
   */
  public Object getCellEditorValue() {
    return vectorcounterTableModel.getData();
  }

  /**
   * Overridden version of the method. The description action is caught so that
   * the editing can be stopped when the used updates the value in the cell.
   * 
   * @param e
   *          the action event
   */
  public void actionPerformed(final ActionEvent e) {
    fireEditingStopped();
  }

}
