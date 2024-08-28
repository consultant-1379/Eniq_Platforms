package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public abstract class ActionTableCellEditor implements TableCellEditor, ActionListener {

  // public final Icon DOTDOTDOT_ICON = new
  // ImageIcon(getClass().getResource("edit.ico"));

  private TableCellEditor editor;

  private Component editorComp;

  private Object currentValue;

  // private JButton customEditorButton = new JButton(DOTDOTDOT_ICON);
  private JButton customEditorButton = new JButton("...");

  protected JTable table;

  protected int row, column;

  public ActionTableCellEditor(final TableCellEditor editor) {
    this.editor = editor;
    customEditorButton.addActionListener(this);

    // ui-tweaking
    customEditorButton.setFocusable(false);
    customEditorButton.setFocusPainted(false);
    customEditorButton.setMargin(new Insets(0, 0, 0, 0));
  }

  public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
	  final JPanel panel = new JPanel(new BorderLayout());
    editorComp = editor.getTableCellEditorComponent(table, value, isSelected, row, column);
    panel.add(editorComp);
    panel.add(customEditorButton, BorderLayout.EAST);
    this.table = table;
    this.row = row;
    this.column = column;

    currentValue = editor.getCellEditorValue();

    return panel;
  }

  public Object getCellEditorValue() {
    return editor.getCellEditorValue();
  }

  public boolean isCellEditable(final EventObject anEvent) {
    return editor.isCellEditable(anEvent);
  }

  public boolean shouldSelectCell(final EventObject anEvent) {
    return editor.shouldSelectCell(anEvent);
  }

  public boolean stopCellEditing() {
    if (!currentValue.equals(editor.getCellEditorValue())) {
      currentValue = editor.getCellEditorValue();
      return editor.stopCellEditing();
    } else {
      cancelCellEditing();
      return true;
    }
  }

  public void cancelCellEditing() {
    editor.cancelCellEditing();
  }

  public void addCellEditorListener(final CellEditorListener l) {
    editor.addCellEditorListener(l);
  }

  public void removeCellEditorListener(final CellEditorListener l) {
    editor.removeCellEditorListener(l);
  }

  public final void actionPerformed(final ActionEvent e) {
    editor.cancelCellEditing();
    editCell(table, row, column, editorComp.isEnabled());
  }

  protected abstract void editCell(JTable table, int row, int column, boolean editable);
}
