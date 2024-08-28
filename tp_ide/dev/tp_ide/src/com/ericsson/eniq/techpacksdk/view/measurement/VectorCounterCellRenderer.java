package com.ericsson.eniq.techpacksdk.view.measurement;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.application.Application;

import com.ericsson.eniq.component.TableComponent;

import tableTree.GlobalTableTreeConstants;
import tableTree.TTTableModel;
import tableTreeUtils.TCTableModel;

/**
 * This class is a customized table cell renderer for vector counter
 * 
 * @author eheijun
 */
public class VectorCounterCellRenderer implements TableCellRenderer {

  /**
   * Application reference
   */
  private Application application;
  private boolean editable = true;
  
  /**
   * @param application
   */
  public VectorCounterCellRenderer(Application application, boolean editable) {
    super();
    this.application = application;
  }
  
  /**
   * Method for getting the renderer component. Sets the cell coloring and the
   * text value shown in the description components text box.
   * 
   * @param table
   * @param value
   * @param isSelected
   * @param hasFocus
   * @param row
   * @param column
   * @return the renderer component
   */
  public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {

    final TCTableModel vectorcounterTableModel = (TCTableModel) value;
    final TableComponent retComp = new TableComponent(application, "VectorCounter", vectorcounterTableModel, 12, editable);

    // The actualColumn represents the column index in the table model, not
    // the view
    final int actualColumn = ((TTTableModel) table.getModel()).getTableInfo().getOriginalColumnIndexOfColumnName(
        table.getColumnModel().getColumn(column).getHeaderValue().toString());

    // The colouring of the cell depends on the row selection, table type
    // filtering and table filtering.
    if (isSelected) {
      retComp.setColors(table.getSelectionBackground(), table.getSelectionForeground(), table.getSelectionBackground(),
          table.getSelectionForeground());
    } else if (((TTTableModel) table.getModel()).isColumnFilteredForTableType(actualColumn)) {
      retComp.setColors(table.getBackground(), table.getForeground(),
          GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR, table.getForeground());
    } else if (((TTTableModel) table.getModel()).isColumnFiltered(actualColumn)) {
      retComp.setColors(table.getBackground(), table.getForeground(), GlobalTableTreeConstants.FILTERED_CELL_COLOR,
          table.getForeground());
    } else {
      retComp.setColors(table.getBackground(), table.getForeground(), table.getBackground(), table.getForeground());
    }

    return retComp;
  }

}
