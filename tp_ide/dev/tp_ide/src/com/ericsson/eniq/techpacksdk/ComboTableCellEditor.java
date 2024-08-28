package com.ericsson.eniq.techpacksdk;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import tableTree.GlobalTableTreeConstants;
import tableTreeUtils.LimitedSizeTextField;

/**
 * Table cell editor for combo boxes.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class ComboTableCellEditor extends DefaultCellEditor {

  private int columns;
  private int limit;
  private boolean required;
  
  /**
   * Constructor, creates a combo box based on the given combo items.
   * 
   * @param items
   * @param editable 
   */
  public ComboTableCellEditor(Object[] items, boolean editable) {
    super(new JComboBox(items));
    JComboBox comboBox = (JComboBox) getComponent();
    comboBox.setEditable(editable);
    this.columns = 0; 
    this.limit = 0;
    this.required = false;
  }
  
  /**
   * Constructor, creates a combo box based on the given combo items.
   * 
   * @param items
   * @param editable 
   * @param columns 
   * @param limit
   * @param required
   */
  public ComboTableCellEditor(Object[] items, boolean editable, int columns, int limit, boolean required) {
    this(items, editable);
    this.columns = columns; 
    this.limit = limit;
    this.required = required;
  }

  /**
   * Overridden version of the method. Special implementation is needed for
   * handling possible null values from the database. Returns the combo box
   * table cell editor component.
   * 
   * @param table
   * @param value
   * @param isSelected
   * @param rowIndex
   * @param vColIndex
   * @return the editor component
   */
  public Component getTableCellEditorComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected,
      int rowIndex, int vColIndex) {

    JComboBox comboBox = (JComboBox) this.getComponent();
    comboBox.setSelectedIndex(-1);
    boolean hasValue = false;
    comboBox.setEditor(new LimitedTextSizeComboBoxEditor());
    for (int ind = 0; ind < comboBox.getItemCount(); ind++) {
      if (comboBox.getItemAt(ind).equals(value)) {
        comboBox.setSelectedItem(value);
        hasValue = (!value.toString().equals(""));
        break;
      }
    }
    // If comboBox is editable then show "manually entered" value.
    if ((comboBox.getSelectedIndex() == -1) && (comboBox.isEditable())) {
      comboBox.setSelectedItem(value);
      hasValue = (!value.toString().equals(""));
    }

    // Validate the value. If the combo box is required to have a value, but
    // none is selected, then indicate an error.
    if (required && !hasValue) {
      comboBox.setBackground(GlobalTableTreeConstants.ERROR_BG);
      comboBox.setToolTipText("Empty value is not allowed.");

    } else {
      comboBox.setToolTipText(null);
    }
    return this.getComponent();

  }

  class LimitedTextSizeComboBoxEditor implements ComboBoxEditor {
    
    private LimitedSizeTextField limitedSizeTextField;

    /**
     * @param limit
     * @param required
     */
    public LimitedTextSizeComboBoxEditor() {
      super();
      limitedSizeTextField = new LimitedSizeTextField(LimitedSizeTextField.DATABASECHARS_STRING, "", columns, limit, required);
    }

    public void addActionListener(ActionListener l) {
      limitedSizeTextField.addActionListener(l);
    }

    public Component getEditorComponent() {
      return limitedSizeTextField;
    }

    public Object getItem() {
      return limitedSizeTextField.getText();
    }

    public void removeActionListener(ActionListener l) {
      limitedSizeTextField.removeActionListener(l);
    }

    public void selectAll() {
      limitedSizeTextField.selectAll();
    }

    public void setItem(Object anObject) {
      if (anObject != null) {
        limitedSizeTextField.setText(anObject.toString());
      }
    }
    
  }
}
