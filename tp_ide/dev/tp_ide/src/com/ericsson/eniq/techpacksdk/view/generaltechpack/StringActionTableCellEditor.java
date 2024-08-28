package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;

public class StringActionTableCellEditor extends ActionTableCellEditor {

  private TableCellEditor editor;

  public StringActionTableCellEditor(TableCellEditor editor) {
    super(editor);
    this.editor = editor;
  }

  protected void editCell(JTable table, int row, int column, boolean editable) {
    // JTextArea textArea = new JTextArea(10, 50);
    int maxTextLenght = 0;
    if (column == 0) {
      // ExternalStament col 0
      maxTextLenght = 255;
    } else {
      // ExternalStament col 2
      maxTextLenght = 2147483647;
    }
    LimitedSizeTextArea textArea = new LimitedSizeTextArea(maxTextLenght, true);
    textArea.setLineWrap(true);

    // JTextArea textArea = new JTextArea(10, 50);
    Object value = table.getValueAt(row, column);
    if (value != null) {
      textArea.setText((String) value);
      textArea.setCaretPosition(0);
    }

    JScrollPane textAreaP = new JScrollPane(textArea);
    Dimension areaDim = new Dimension(500, 300);
    textAreaP.setPreferredSize(areaDim);

    // Show the dialog with a text area.
    int result = JOptionPane.showOptionDialog(table, new JScrollPane(textAreaP), (String) table.getColumnName(column),
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

    // Accept the changes only in edit mode.
    if (editable && result == JOptionPane.OK_OPTION) {
      table.setValueAt(textArea.getText(), row, column);
    } else
      editor.cancelCellEditing();

  }
}
