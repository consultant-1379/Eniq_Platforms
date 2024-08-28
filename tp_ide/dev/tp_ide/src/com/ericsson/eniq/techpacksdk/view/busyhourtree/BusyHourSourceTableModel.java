/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.SubTableModel;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

/**
 * @author eheijun
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class BusyHourSourceTableModel extends AbstractTableModel implements SubTableModel {

  private static final int targettechpackColumnIdx = 0;

  private static final int typenameColumnIdx = 1;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] columnNames = { "Current Techpack", "Type Name" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] columnWidths = { 100, 100, 30 };

  private Vector<Busyhoursource> data;

  private List<Busyhoursource> deletedData;

  private List<Busyhoursource> modifiedData;

  private Vector<Busyhoursource> copyOfTheData;

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  private DataModelController dataModelController;

  private final Busyhour busyhour;

  private boolean editable = true;

  /**
   * Constructor.
   * 
   * @param dataModelController
   * @param busyhour
   *          the parent busy hour instance.
   * @param data
   *          busy hour source data vector.
   * @param editable
   */
  public BusyHourSourceTableModel(DataModelController dataModelController, Busyhour busyhour,
      List<Busyhoursource> data, boolean editable) {
    super();
    this.editable = editable;
    this.dataModelController = dataModelController;
    this.rockFactory = dataModelController.getRockFactory();
    this.busyhour = busyhour;
    setData(data);
  }

  private Object getColumnValue(final Busyhoursource busyhoursource, final int col) {

    if (busyhoursource != null) {
      switch (col) {
      case targettechpackColumnIdx:
        return Utils.replaceNull(busyhoursource.getTargetversionid());

      case typenameColumnIdx:
        return Utils.replaceNull(busyhoursource.getTypename());
      default:
        break;
      }
    }
    return null;

  }

  private void setColumnValue(final Busyhoursource busyhoursource, final int col, final Object value) {

    boolean valueChanged = true;
    switch (col) {

    case targettechpackColumnIdx:
      // The targetVersionId is not modified for the bh source, even if the
      // techpack for the measurement type is changed. It must point to the
      // current techpack.
      // busyhoursource.setTargetversionid(Utils.replaceNull((String) value));
      break;

    case typenameColumnIdx:
      busyhoursource.setTypename(Utils.replaceNull(((String) value).toUpperCase()));
      break;

    default:
      // nothing changed
      valueChanged = false;
      break;
    }
    if (valueChanged) {
      if (!modifiedData.contains(busyhoursource)) {
        modifiedData.add(busyhoursource);
      }
    }
  }

  public Class<? extends Object> getColumnClass(final int col) {
    return String.class;
  }

  public String getColumnName(final int col) {
    return columnNames[col].toString();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    if (data != null) {
      return data.size();
    }
    return 0;
  }

  public Object getValueAt(final int rowIndex, final int colIndex) {

    Object result = null;
    if (data != null) {
      if (data.size() > rowIndex) {
        if ((data.elementAt(rowIndex) != null) && (data.elementAt(rowIndex) instanceof Busyhoursource)) {
          final Busyhoursource busyhoursource = (Busyhoursource) data.elementAt(rowIndex);
          result = getColumnValue(busyhoursource, colIndex);
        }
      }
    }
    return result;
  }

  public void setValueAt(final Object value, final int row, final int col) {

    final Busyhoursource busyhoursource = (Busyhoursource) data.elementAt(row);
    if (((String) value).contains("#")) {
      setColumnValue(busyhoursource, 0, ((String) value).substring(0, ((String) value).indexOf("#")));
      setColumnValue(busyhoursource, 1, ((String) value).substring(((String) value).indexOf("#") + 1));
    } else {
      setColumnValue(busyhoursource, col, ((String) value));
    }

    fireTableDataChanged();
  }

  // private void addRow(final Busyhoursource busyhoursource) {
  // data.add(busyhoursource);
  // modifiedData.add(busyhoursource);
  // fireTableRowsInserted(data.size() - 1, data.size() - 1);
  // }
  //
  // private Busyhoursource removeRow(final int row) {
  // final Busyhoursource busyhoursource = data.remove(row);
  // deletedData.add(busyhoursource);
  // fireTableRowsDeleted(row, row);
  // return busyhoursource;
  // }

  public boolean isCellEditable(final int row, final int col) {
    return editable;
  }

  private boolean isDirty() {

    return modifiedData.size() > 0;
  }

  // private void clearDirty() {
  //
  // modifiedData.clear();
  // }

  protected void clear() {

    final int rows = data.size();
    data.clear();
    modifiedData.clear();
    deletedData.clear();
    fireTableRowsDeleted(0, rows);
  }

  public void save() throws Exception {

    // Delete all the busy hour sources in the deleted list from the DB.
    for (int i = deletedData.size() - 1; i >= 0; i--) {

      final Busyhoursource busyhoursource = deletedData.remove(i);

      busyhoursource.deleteDB();
    }

    // Save all the modified busy hour sources to the DB.
    while (isDirty()) {
      final Busyhoursource busyhoursource = modifiedData.remove(0);

      busyhoursource.saveToDB();
    }
  }

  public Vector<Busyhoursource> getData() {

    return data;
  }

  public void setData(final List<Busyhoursource> d) {

    if (this.data == null) {
      this.data = new Vector<Busyhoursource>();
      this.modifiedData = new ArrayList<Busyhoursource>();
      this.deletedData = new ArrayList<Busyhoursource>();
    } else {

      clear();
    }
    for (final Iterator<Busyhoursource> iter = d.iterator(); iter.hasNext();) {
      Busyhoursource b = iter.next();
      data.add(b);
      modifiedData.add(b);
      fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }
  }

  /**
   * Method for setting the preferred column widths
   */
  public void setColumnWidths(final JTable theTable) {
    for (int ind = 0; ind < columnWidths.length; ind++) {
      if (theTable.getColumnModel().getColumnCount() <= ind) {
        final TableColumn col = theTable.getColumnModel().getColumn(ind);
        col.setPreferredWidth(columnWidths[ind]);
      }
    }
  }

  public void startEditing() {

    copyOfTheData = new Vector<Busyhoursource>();
    for (final Iterator<Busyhoursource> it = data.iterator(); it.hasNext();) {
      final Busyhoursource original = it.next();
      final Busyhoursource copy = (Busyhoursource) original.clone();
      copyOfTheData.add(copy);
    }
  }

  public void stopEditing() {

    copyOfTheData = null;
  }

  public void cancelEditing() {

    setData(copyOfTheData);
  }

  public RockDBObject createNew() {

    final Busyhoursource busyhoursource = new Busyhoursource(rockFactory);
    busyhoursource.setVersionid(busyhour.getVersionid());
    busyhoursource.setTargetversionid(busyhour.getTargetversionid());
    busyhoursource.setBhlevel(busyhour.getBhlevel());
    busyhoursource.setBhobject(busyhour.getBhobject());
    busyhoursource.setBhtype(busyhour.getBhtype());
    busyhoursource.setTypename("");
    return busyhoursource;
  }

  public void duplicateRow(final int[] selectedRows, final int times) {

    Object sourceData = null;
    for (int i = 0; i < times; i++) {
      for (int j = 0; j < selectedRows.length; j++) {
        sourceData = data.elementAt(selectedRows[j]);
        insertDataLast(copyOf(sourceData));
      }
    }
  }

  private Object copyOf(final Object toBeCopied) {

    final Busyhoursource orig = (Busyhoursource) toBeCopied;
    final Busyhoursource copy = (Busyhoursource) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  public JPopupMenu getPopUpMenu(final TableComponentListener listener, final Component component) {

    // Show pop-up only in edit mode.
    if (!editable) {
      return null;
    }

    final JPopupMenu menu = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem(TableComponentListener.ADD_ROW);
    menuItem.addActionListener(listener);
    menu.add(menuItem);

    menuItem = new JMenuItem(TableComponentListener.REMOVE_ROW);
    menuItem.setEnabled(false);
    if ((component instanceof JTable) && (((JTable) component).getSelectedRow() > -1)) {
      menuItem.setEnabled(true);
    }
    menuItem.addActionListener(listener);
    menu.add(menuItem);

    menu.addSeparator();
    final JMenu submenu = new JMenu("Advanced");

    menuItem = new JMenuItem(TableComponentListener.ADD_MULTIPLE_ROWS);
    menuItem.addActionListener(listener);
    submenu.add(menuItem);

    menuItem = new JMenuItem(TableComponentListener.DUPLICATE_ROW);
    menuItem.setEnabled(false);
    if ((component instanceof JTable) && (((JTable) component).getSelectedRow() > -1)) {
      menuItem.setEnabled(true);
    }
    menuItem.addActionListener(listener);
    submenu.add(menuItem);

    menu.add(submenu);

    return menu;
  }

  public TableModel getTableModel() {

    return this;
  }

  public void insertDataAtRow(final Object datum, final int index) {

    data.insertElementAt((Busyhoursource) datum, index);
    modifiedData.add((Busyhoursource) datum);
    this.fireTableDataChanged();
  }

  public void insertDataLast(final Object datum) {

    data.insertElementAt((Busyhoursource) datum, data.size());
    modifiedData.add((Busyhoursource) datum);
    this.fireTableDataChanged();
  }

  public void removeSelectedData(final int[] selectedRows) {

    // Remove all selected rows
    for (int i = selectedRows.length - 1; i >= 0; i--) {

      int row = selectedRows[i];

      // Remove the busyhoursource from the data.
      final Busyhoursource busyhoursource = data.remove(row);

      // Add the busyhoursource to the deleted list.
      deletedData.add(busyhoursource);

      // If the row was modified before removal, then remove it from the
      // modified list, so it does not get saved again.
      modifiedData.remove(busyhoursource);
    }

    // Notify listeners about table data change.
    fireTableDataChanged();
  }

  public void removeAllData() {
    for (Busyhoursource bhSource : data) {
      deletedData.add(bhSource);
    }
    modifiedData.clear();
    data.clear();
  }

  public void setColumnEditors(final JTable editTable) {

    // Set editor for source
    final TableColumn bhsourceColumn1 = editTable.getColumnModel().getColumn(targettechpackColumnIdx);
    // bhsourceColumn1.setCellEditor(new
    // BusyHourSourceSelectorCellEditor(dataModelController, this.editable));
    bhsourceColumn1.setCellEditor(new LimitedSizeCellEditor(columnWidths[targettechpackColumnIdx], Busyhoursource
        .getTargetversionidColumnSize(), true));

    // Set editor for source
    final TableColumn bhsourceColumn2 = editTable.getColumnModel().getColumn(typenameColumnIdx);
    bhsourceColumn2.setCellEditor(new BusyHourSourceSelectorCellEditor(dataModelController, this.editable));

  }

  public void setColumnRenderers(final JTable editTable) {

    // Set source renderer
    // final TableColumn bhsourceColumn1 =
    // editTable.getColumnModel().getColumn(targettechpackColumnIdx);
    // bhsourceColumn1.setCellRenderer(new
    // BusyHourSourceSelectorCellRenderer());

    // Set source renderer
    final TableColumn bhsourceColumn2 = editTable.getColumnModel().getColumn(typenameColumnIdx);
    bhsourceColumn2.setCellRenderer(new BusyHourSourceSelectorCellRenderer());

  }

  public void removeFromDB() {

    try {
      for (int i = 0; i < deletedData.size(); i++) {
        deletedData.get(i).deleteDB();
      }
      for (int i = 0; i < data.size(); i++) {
        data.elementAt(i).deleteDB();
      }
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  public Vector<String> validateData() {

    Vector<String> errorStrings = new Vector<String>();
    for (Busyhoursource source : data) {
      if (Utils.replaceNull(source.getTargetversionid()).trim().equals("")) {
        errorStrings.add(columnNames[targettechpackColumnIdx] + " for Busy Hour source is required.");
      }
      if (Utils.replaceNull(source.getTypename()).trim().equals("")) {
        errorStrings.add(columnNames[typenameColumnIdx] + " for Busy Hour source is required.");
      }
    }
    return errorStrings;
  }

  public Object clone() {
    return null;
  }

}
