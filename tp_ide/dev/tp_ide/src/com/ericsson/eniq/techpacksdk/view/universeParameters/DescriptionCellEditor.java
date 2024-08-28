package com.ericsson.eniq.techpacksdk.view.universeParameters;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class DescriptionCellEditor extends DefaultCellEditor implements ActionListener {

  private boolean editable;

  public DescriptionCellEditor(int limit, boolean required, boolean editable) {
    // Use a dummy textfield, it will be replaced with a ExpandableTextField
    // component.
    super(new JTextField());

    this.editable = editable;

    // Replace the editor component with a ExpandableTextField component
    int buttonWidthInColumns = 2;
    final ExpandableTextField textField = new ExpandableTextField(buttonWidthInColumns, limit, required, editable);
    textField.addActionListener(this);
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
//IDE #660 & 663 Save and Cancel buttons not active after Busy Hour criteria modified by Edit window ,11/09/09, ejohabd
  public void actionPerformed(final ActionEvent e) {
	   fireEditingStopped();
	  }
}
