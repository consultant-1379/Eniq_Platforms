package com.ericsson.eniq.techpacksdk;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class LimitedSizeTextTableCellRenderer extends LimitedSizeTextField implements TableCellRenderer {
  boolean required = false;
  public LimitedSizeTextTableCellRenderer(int limit, boolean required) {
    super(limit, required);
    this.required = required;
  }

  public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {
    
    String newvalue = "";
    
    if (value != null) {
      newvalue = (String) value;
    }
    
    this.setText(newvalue);
    
    if (required && newvalue.length() == 0) {
      return this;
    }
    
    if (isSelected) {
      // Cell is selected
      
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
      this.setForeground(table.getSelectionForeground());
      this.setBackground(table.getSelectionBackground());
    } else {      
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }

        
    return this;

  }

}
