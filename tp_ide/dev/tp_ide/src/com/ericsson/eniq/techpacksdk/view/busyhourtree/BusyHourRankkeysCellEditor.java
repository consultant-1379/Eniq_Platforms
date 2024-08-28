package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.application.Application;

import com.ericsson.eniq.component.TableComponent;

/**
 * This class is a customized table cell editor for busyhour rank keys
 * 
 * @author eheijun
 */
@SuppressWarnings("serial")
public class BusyHourRankkeysCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  private Application application;

  /**
   * The table component
   */
  private TableComponent theComp = null;

  /**
   * Current text value of the cell
   */
  private String currentValue = "";

  /**
   * Current value of the cell
   */
  private BusyHourRankkeysTableModel busyHourRankkeysTableModel;

  /**
   * Is the tree editable
   */
  private final boolean tableEditable;

  /**
   * Constructor.
   * 
   * @param editable
   *          true if the tree is editable.
   */
  public BusyHourRankkeysCellEditor(Application application, boolean editable) {
    this.application = application;
    this.tableEditable = editable;
  }

  /**
   * This returns the description component as the editor to the table. This is
   * also where we get the initial value for the components text field.
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

    busyHourRankkeysTableModel = (BusyHourRankkeysTableModel) value;
    theComp = new TableComponent(application, "Busy Hour RANK Keys", busyHourRankkeysTableModel, 150, tableEditable);
    currentValue = theComp.getTextField().getText();

    // Add the action lister, so that we can catch the description action
    // when the user updates the cell value.
    theComp.addActionListener(this);

    // Set the component enabled based on the tree enabled status.
    theComp.setEnabled(true);

    return theComp;
  }

  /**
   * Overridden version of the method. Returns the current text value.
   * 
   * @return the current text value
   */
  public Object getCellEditorValue() {
    return busyHourRankkeysTableModel.getData();
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

  /**
   * Overridden version of the method. In addition to passing on the editing
   * stopped action, the current text value is stored from the components
   * textField.
   */
  protected void fireEditingStopped() {
    if (!(currentValue.equals(theComp.getTextField().getText()))) {
      currentValue = theComp.getTextField().getText();
      super.fireEditingStopped();
    } else if (currentValue.equals(theComp.getTextField().getText())) {
      fireEditingCanceled();
    }
  }
}
