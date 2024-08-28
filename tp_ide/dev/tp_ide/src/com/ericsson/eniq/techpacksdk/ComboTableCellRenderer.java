package com.ericsson.eniq.techpacksdk;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import tableTree.GlobalTableTreeConstants;

/**
 * Table cell renderer for combo boxes.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class ComboTableCellRenderer extends JComboBox implements TableCellRenderer {

  private boolean required;

  // denied
  protected ComboTableCellRenderer() {
  }

  public ComboTableCellRenderer(Object[] items) {
    super(items);
    this.required = false;
    this.setEditable(false);
  }

  /**
   * Constructor for initializing the combo box with items and required flag.
   * 
   * @param items
   * @param required
   */
  public ComboTableCellRenderer(Object[] items, boolean required) {
    this(items);
    this.required = required;
  }

  /**
   * Constructor for initializing the combo box with items, required flag and editable flag.
   * 
   * @param items
   * @param required
   */
  public ComboTableCellRenderer(Object[] items, boolean required, boolean editable) {
    this(items, required);
    this.setEditable(editable);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {

    if (isSelected) {
      // Cell is selected
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
      this.setForeground(table.getSelectionForeground());
      this.setBackground(table.getSelectionBackground());
    } else {
      // Cell is not selected 
      setForeground(table.getForeground());
      setBackground(table.getBackground());

      this.setForeground(table.getForeground());
      this.setBackground(table.getBackground());

    }

    // Set the selected item in the list. The value can be null in the
    // database, so the selection match against the combo box items is
    // checked first.
    boolean hasValue = false;
    this.setSelectedIndex(-1);
    for (int ind = 0; ind < this.getItemCount(); ind++) {
      if (this.getItemAt(ind).equals(value)) {
        this.setSelectedItem(value);
        hasValue = (!value.toString().equals(""));
        break;
      }
    }
    // If comboBox is editable then show "manually entered" value.
    if ((this.getSelectedIndex() == -1) && (this.isEditable())) {
      this.setSelectedItem(value);
      hasValue = (!value.toString().equals(""));
    }

    // Validate the value. If the combo box is required to have a value, but
    // none is selected, then indicate an error.
    if (required && !hasValue) {
        setForeground(GlobalTableTreeConstants.ERROR_BG);
        setBackground(GlobalTableTreeConstants.ERROR_BG);
        this.setForeground(GlobalTableTreeConstants.ERROR_BG);
        this.setBackground(GlobalTableTreeConstants.ERROR_BG);
        this.setToolTipText("Empty value is not allowed.");

    } else {
        this.setToolTipText(null);
    }
    
    return this;

  }

}
