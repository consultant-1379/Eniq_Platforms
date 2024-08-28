package com.ericsson.eniq.techpacksdk;

import javax.swing.DefaultCellEditor;


/**
 * Table cell editor for text fields with limited size.
 * 
 */
public class LimitedSizeCellEditor extends DefaultCellEditor {

  private boolean required = false;

  private static final long serialVersionUID = 8599483547945333667L;

  /**
   * Constructor, creates a limited size text field. NOTE: The required field
   * has no real effect here, because it is not possible to empty the cell value
   * for an integer. The cell editing is always canceled if a null or an empty
   * value is given.
   * 
   * @param colums
   * @param limit
   * @param required
   */
  public LimitedSizeCellEditor(int colums, int limit, boolean required) {
    super(new LimitedSizeTextField(LimitedSizeTextField.DATABASECHARS_STRING, "", colums, limit, required));
    this.required = required;
  }

  /**
   * Overridden version of the method to be able to set the old value back in
   * case the value is required and a null or an empty value is entered.
   */
  @Override
  public boolean stopCellEditing() {
    boolean retVal = super.stopCellEditing();
    // If the value is required, the check if the edited value is either
    // null or empty. If yes, then cancel the cell editing because it is not
    // allowed to set that value for this field.
    if (required) {
      if (this.getCellEditorValue().equals("") || this.getCellEditorValue() == null) {
        super.cancelCellEditing();
      }
    }
    
    return retVal;
  }
}
