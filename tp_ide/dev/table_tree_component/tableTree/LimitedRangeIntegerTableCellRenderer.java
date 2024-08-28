package tableTree;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 * Table cell renderer for text fields with limited size.
 * 
 * @author eheitur
 * 
 */
public class LimitedRangeIntegerTableCellRenderer implements TableCellRenderer {

  private int minValue;

  private int maxValue;

  // Required parameter is not used in this renderer
  // private boolean required;

  /**
   * @param minValue
   * @param maxValue
   * @param required
   */
  public LimitedRangeIntegerTableCellRenderer(int minValue, int maxValue, boolean required) {
    super();
    this.minValue = minValue;
    this.maxValue = maxValue;
    // this.required = required;
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

    // The actualColumn represents the column index in the table model, not
    // the view
    int actualColumn = ((TTTableModel) table.getModel()).getTableInfo().getOriginalColumnIndexOfColumnName(
        table.getColumnModel().getColumn(column).getHeaderValue().toString());

    // LimitedSizeTextField retComp = new LimitedSizeTextField(columns,
    // limit,
    // required);
    JTextField retComp = new JTextField();
    retComp.setText(String.valueOf(value));

    // The coloring of the cell depends on the row selection, table type
    // filtering and table filtering.
    if (isSelected) {
      // Cell is selected
      retComp.setForeground(table.getSelectionForeground());
      retComp.setBackground(table.getSelectionBackground());
    } else if (((TTTableModel) table.getModel()).isColumnFilteredForTableType(actualColumn)) {
      // Cell is not selected, but the column is filtered in table type
      // level.
      retComp.setForeground(table.getForeground());
      retComp.setBackground(GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR);
    } else if (((TTTableModel) table.getModel()).isColumnFiltered(actualColumn)) {
      // Cell is not selected, but the column is filtered
      retComp.setForeground(table.getForeground());
      retComp.setBackground(GlobalTableTreeConstants.FILTERED_CELL_COLOR);
      // this.setBackground(TableTreeConstants.FILTERED_CELL_BACKGROUND_COLOR);
    } else {
      // Cell is not selected and column is not filtered
      retComp.setForeground(table.getForeground());
      retComp.setBackground(table.getBackground());
    }

    // Validate the value. Note: The required field does not have an effect
    // here, since empty value is not allowed.
    if (((Integer) value < minValue) || ((Integer) value > maxValue)) {
      retComp.setBackground(GlobalTableTreeConstants.ERROR_BG);
      retComp.setToolTipText("The current value: " + value + " is not between the allowed range of " + minValue + "-"
          + maxValue + ".");
    } else {
      retComp.setToolTipText(null);
    }

    return retComp;
  }
}
