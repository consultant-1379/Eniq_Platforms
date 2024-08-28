/**
 * 
 */
package com.ericsson.eniq.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * @author eheijun
 * 
 */
public class TableComponentListener implements ActionListener {

  public static final String ADD_ROW = "Add Row";

  public static final String REMOVE_ROW = "Remove Row";

  public static final String ADD_MULTIPLE_ROWS = "Add Multiple Rows";

  public static final String DUPLICATE_ROW = "Duplicate Row";

  private final JDialog dialog;

  private final JTable table;

  private SubTableModel model;

  public TableComponentListener(final JDialog dialog, final SubTableModel model, final JTable table) {
    super();
    this.dialog = dialog;
    this.model = model;
    this.table = table;
  }

  public void actionPerformed(final ActionEvent ae) {
    if (ae.getActionCommand().equals(ADD_ROW)) {
      addRow();
    } else if (ae.getActionCommand().equals(REMOVE_ROW)) {
      removeRow(true);
    } else if (ae.getActionCommand().equals(ADD_MULTIPLE_ROWS)) {
      addMultipleRows();
    } else if (ae.getActionCommand().equals(DUPLICATE_ROW)) {
      duplicateRow();
    }
  }

  private void addRow() {
    final int[] selectedRows = table.getSelectedRows();

    if (selectedRows.length > 0) {
      // Selected row, insert before
      final Object toBeInserted = model.createNew();
      model.insertDataAtRow(toBeInserted, selectedRows[selectedRows.length - 1]);
    } else {
      // No rows selected, add last.
      final Object toBeInserted = model.createNew();
      model.insertDataLast(toBeInserted);
    }
  }

  private void removeRow(final boolean isConfirmNeeded) {

      // Remove confirmed. Remove the row(s).
      final int[] selectedRows = table.getSelectedRows();

      if (selectedRows.length > 0) {        
        // Prompt the user for confirmation
        if (!isConfirmNeeded
            || (JOptionPane.showConfirmDialog(dialog, "Are you sure?", "Confirm Remove", JOptionPane.YES_NO_OPTION)) == JOptionPane.YES_OPTION) {

        // Remove from the model
        model.removeSelectedData(selectedRows);

      }
    }
  }

  private void addMultipleRows() {
    final String input = (String) JOptionPane.showInputDialog(dialog, "How many rows you want to add?", "Add Multiple Row",
        JOptionPane.QUESTION_MESSAGE, null, null, 1);
    if (input != null && !input.equals("")) {
      int times = 0;
      try {
        times = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(dialog, "Invalid number of rows.");
      }

      if (times > 0) {

        for (int i = 0; i < times; i++) {
          final Object toBeInserted = model.createNew();
          model.insertDataLast(toBeInserted);
        }
      }

    }
  }

  /**
   * Callback method for adding multiple rows at the same time
   * 
   */
  private void duplicateRow() {
    final int[] selectedRows = table.getSelectedRows();
    final String input = (String) JOptionPane.showInputDialog(dialog, "How many times you want to duplicate the row?",
        "Duplicate Row", JOptionPane.QUESTION_MESSAGE, null, null, 1);
    if (input != null && !input.equals("")) {

      int times = 0;
      try {
        times = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(dialog, "Invalid number of rows.");
      }

      if (times > 0) {
        model.duplicateRow(selectedRows, times);
      }

      // Keep the row selection
      setRowSelection(selectedRows);
    }
  }

  private void setRowSelection(final int[] numbers) {
    for (int i = 0; i < numbers.length; i++) {
      table.addRowSelectionInterval(numbers[i], numbers[i]);
    }
  }

}
