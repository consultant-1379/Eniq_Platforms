package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.application.Application;

import com.ericsson.eniq.component.TableComponent;

/**
 * This class is a customized table cell renderer for busyhour rank keys
 * 
 * @author eheijun
 */
public class BusyHourRankkeysCellRenderer implements TableCellRenderer {

  private Application application;

  private boolean editable = true;

  /**
   * @param application
   */
  public BusyHourRankkeysCellRenderer(Application application, boolean editable) {
    super();
    this.editable = editable;
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
  public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
      final boolean hasFocus, final int row, final int column) {

    final BusyHourRankkeysTableModel busyHourRankkeysTableModel = (BusyHourRankkeysTableModel) value;
    final TableComponent retComp = new TableComponent(application, "Busy Hour RANK Keys", busyHourRankkeysTableModel, 550,
        editable);

    // The colouring of the cell depends on the row selection, table type
    // filtering and table filtering.
    if (isSelected) {
      // retComp.setColors(table.getSelectionBackground(),
      // table.getSelectionForeground(),
      // table.getSelectionBackground(),table.getSelectionForeground());
    } else {
      retComp.setColors(table.getBackground(), table.getForeground(), table.getBackground(), table.getForeground());
    }

    return retComp;
  }

}
