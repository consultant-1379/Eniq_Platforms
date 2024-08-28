package com.ericsson.eniq.techpacksdk;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

//import tableTreeUtils.LimitedSizeTextField;
//import tableTreeUtils.LimitedSizeTextField.RequiredWatch;

/**
 * Table cell renderer for text fields with limited size.
 * 
 * @author eheitur
 * 
 */
public class LimitedSizeCellRenderer implements TableCellRenderer {

  // private int columns;
  private int limit;

  private boolean required;

  /**
   * @param columns
   * @param limit
   * @param required
   */
  public LimitedSizeCellRenderer(int columns, int limit, boolean required) {
    super();
    // this.columns = columns;
    this.limit = limit;
    this.required = required;
  }

  /**
   * Method for getting the renderer component. Creates component and sets the
   * text value shown.
   * 
   * @param table
   * @param value
   * @param isSelected
   * @param hasFocus
   * @param row
   * @param column
   * @return the renderer component
   */
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {

    String text = "";
    if (value != null) {
      text = (String) value;
    }

    LimitedSizeTextField retComp = new LimitedSizeTextField(limit, required);

    retComp.setText(text);

    // The coloring of the cell depends on the row selection, table type
    // filtering and table filtering.
    if (isSelected) {
      // Cell is selected
      retComp.setForeground(table.getSelectionForeground());
      retComp.setBackground(table.getSelectionBackground());
    } else {
      // Cell is not selected
      retComp.setForeground(table.getForeground());
      retComp.setBackground(table.getBackground());
    }

    // Validate the length
    retComp.setText(retComp.getText());

    return retComp;
  }
}
