package com.ericsson.eniq.techpacksdk.view.universeParameters;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class UniverseExtCellEditor extends DefaultCellEditor {

  // private GenericUniverseTableModel<Universeparameters> subTableModel;

  private boolean editable;

  public UniverseExtCellEditor(String[] universeExtensionOptions, boolean editable) {
    // Use a dummy text field, it will be replaced with an expandable sub table
    // component.
    super(new JTextField());

    this.editable = editable;

    // Replace the editor component with an expandable sub table component
    final UniverseExtCellExpandableSubTable textField = new UniverseExtCellExpandableSubTable(this,
        universeExtensionOptions, this.editable);
    this.editorComponent = textField;
    this.clickCountToStart = 1;

    this.delegate = new EditorDelegate() {

      public void setValue(Object value) {
        textField.setText((value != null) ? value.toString() : "");
      }

      public Object getCellEditorValue() {
        return textField.getText();
      }
    };
  }

  @Override
  public boolean stopCellEditing() {
    if (this.editable) {
      return super.stopCellEditing();
    } else {
      return true;
    }
  }

  @Override
  public void cancelCellEditing() {
    if (this.editable) {
      super.cancelCellEditing();
    }
  }
}
