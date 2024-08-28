package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.SubTableModel;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class BusyHourRankkeysTableModel extends AbstractTableModel implements SubTableModel {

  private static final int keynameColumnIdx = 0;

  private static final int keyvalueSourceColumnIdx = 1;

  private static final int keyvalueColumnIdx = 2;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] columnNames = { "Key Name", "Source", "Key Value" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] columnWidths = { 75, 200, 75 };

  private Vector<Busyhourrankkeys> data;

  private List<Busyhourrankkeys> deletedData;

  private List<Busyhourrankkeys> modifiedData;

  private Vector<Busyhourrankkeys> copyOfTheData;

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  private final Busyhour busyhour;

  private boolean editable;

  private List<Busyhoursource> busyhourSource;

  private String[] busyhourSourceRows;

  /**
   * @param data
   */
  public BusyHourRankkeysTableModel(List<Busyhoursource> busyhourSource, RockFactory rockFactory, Busyhour busyhour,
      List<Busyhourrankkeys> data, boolean editable) {
    super();
    this.busyhourSource = busyhourSource;
    this.editable = editable;
    this.rockFactory = rockFactory;
    this.busyhour = busyhour;
    setData(data);
  }

  public void setBusyhourSource(List<Busyhoursource> busyhourSource) {
    this.busyhourSource = busyhourSource;

    busyhourSourceRows = new String[busyhourSource.size()];
    int i = 0;
    for (Busyhoursource source : busyhourSource) {
      busyhourSourceRows[i] = source.getTypename();
      i++;
    }

  }

  private Object getColumnValue(final Busyhourrankkeys busyhourrankkeys, final int col) {
    if (busyhourrankkeys != null) {
      switch (col) {
      case keynameColumnIdx:
        return Utils.replaceNull(busyhourrankkeys.getKeyname());
      case keyvalueSourceColumnIdx:
        return Utils.replaceNull(busyhourrankkeys.getKeyvalue().substring(0,
            Utils.getLarger(0, Utils.replaceNull(busyhourrankkeys.getKeyvalue().indexOf(".")))));
      case keyvalueColumnIdx:
        return Utils.replaceNull(busyhourrankkeys.getKeyvalue().substring(
            Utils.replaceNull(busyhourrankkeys.getKeyvalue().indexOf(".") + 1)));
      default:
        break;
      }
    }
    return null;

  }

  private void setColumnValue(final Busyhourrankkeys busyhourrankkeys, final int col, final Object value) {
    boolean valueChanged = true;
    String sourceTableName = "";
    switch (col) {

    case keynameColumnIdx:
      busyhourrankkeys.setKeyname(Utils.replaceNull((String) value));
      break;
    case keyvalueSourceColumnIdx:
      // The source table is stored to the beginning of the key, for example:
      // DC_E_CPP_VCLTP_COUNT.OSS_ID.
      String key = busyhourrankkeys.getKeyvalue().substring(
          Utils.replaceNull(busyhourrankkeys.getKeyvalue().indexOf(".") + 1));
      if (Utils.replaceNull((String) value).trim().length() > 0) {
        key = Utils.replaceNull((String) value) + "." + key;
      }
      busyhourrankkeys.setKeyvalue(key);
      break;
    case keyvalueColumnIdx:
      // Get the key value. The existing source table from the old key value is
      // added to the beginning, for example: DC_E_CPP_VCLTP_COUNT.OSS_ID)
      String keyValue = Utils.replaceNull((String) value);
      sourceTableName = busyhourrankkeys.getKeyvalue().substring(0,
          Utils.getLarger(0, Utils.replaceNull(busyhourrankkeys.getKeyvalue().indexOf("."))));
      if (Utils.replaceNull((String) sourceTableName).trim().length() > 0) {
        keyValue = sourceTableName + "." + keyValue;
      }
      busyhourrankkeys.setKeyvalue(keyValue);
      break;
    default:
      // nothing changed
      valueChanged = false;
      break;
    }
    if (valueChanged) {
      if (!modifiedData.contains(busyhourrankkeys)) {
        modifiedData.add(busyhourrankkeys);
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

  @Override
  public Object getValueAt(final int rowIndex, final int colIndex) {
    Object result = null;
    if (data != null) {
      if (data.size() > rowIndex) {
        if ((data.elementAt(rowIndex) != null) && (data.elementAt(rowIndex) instanceof Busyhourrankkeys)) {
          final Busyhourrankkeys Busyhourrankkeys = (Busyhourrankkeys) data.elementAt(rowIndex);
          result = getColumnValue(Busyhourrankkeys, colIndex);
        }
      }
    }
    return result;
  }

  @Override
  public void setValueAt(final Object value, final int row, final int col) {
    final Busyhourrankkeys Busyhourrankkeys = (Busyhourrankkeys) data.elementAt(row);
    setColumnValue(Busyhourrankkeys, col, value);
    fireTableDataChanged();
  }

  private void addRow(final Busyhourrankkeys Busyhourrankkeys) {
    data.add(Busyhourrankkeys);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  private Busyhourrankkeys removeRow(final int row) {
    final Busyhourrankkeys Busyhourrankkeys = data.remove(row);
    fireTableRowsDeleted(row, row);
    return Busyhourrankkeys;
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public boolean isDirty() {
    return modifiedData.size() > 0;
  }
  
  public void clearDirty() {
    modifiedData.clear();
  }

  public void clear() {
    final int rows = data.size();
    data.clear();
    modifiedData.clear();
    deletedData.clear();
    fireTableRowsDeleted(0, rows);
  }

  public void save() throws Exception {
    for (Busyhourrankkeys key : deletedData) {
      key.deleteDB();
    }

    // 20110901 EANGUAN :: Adding the logic to insert the keys into the database
    // if user duplicate a key and delete the original one. Then view part of keys ( data )
    // should be read for any changes and accordingly inserted into DB
    ArrayList<String> keyNameList = new ArrayList<String>();
    
    while (isDirty()) {
      final Busyhourrankkeys busyhourrankkeys = modifiedData.remove(0);
      if (!deletedData.contains(busyhourrankkeys)) {
    	  keyNameList.add(busyhourrankkeys.getKeyname());
    	  busyhourrankkeys.saveToDB();
      }
    }
    int i = 0 ;
    while(i < data.size()){
    	final Busyhourrankkeys busyhourrankkeys = data.get(i++);
        if (!keyNameList.contains(busyhourrankkeys.getKeyname())) {
          busyhourrankkeys.saveToDB();
        }
    }
  }

  public Vector<Busyhourrankkeys> getData() {
    return data;
  }

  public void setData(final List<Busyhourrankkeys> rankKeys) {

    if (this.data == null) {
      this.data = new Vector<Busyhourrankkeys>();
      this.modifiedData = new ArrayList<Busyhourrankkeys>();
      if (deletedData == null)
        this.deletedData = new ArrayList<Busyhourrankkeys>();
    } else {
      clear();
    }
    long ord = 0;
    for (Busyhourrankkeys key : rankKeys) {

      key.setOrdernbr(ord++);
      addRow(key);
      modifiedData.add(key);
    }
  }

  /**
   * @param rankKeys
   */
  public void setDirtyData(final List<Busyhourrankkeys> rankKeys) {

    // If the data is null, create empty lists for holding the data.
    if (this.data == null) {

      this.data = new Vector<Busyhourrankkeys>();
      this.modifiedData = new ArrayList<Busyhourrankkeys>();
      this.deletedData = new ArrayList<Busyhourrankkeys>();
    }

    // Check if there is existing data. If there is no data, then add the keys
    // to the data and modified data. If there is data, then the new keys are
    // first compared to the existing keys.
    if (this.data.isEmpty()) {
      for (Busyhourrankkeys key : rankKeys) {

        addRow(key);
        modifiedData.add(key);
      }
    } else {
      // The data is not empty.

      // Create a vector for holding the temporary data when comparing the
      // existing data against the new data.
      Vector<Busyhourrankkeys> tmpdata = new Vector<Busyhourrankkeys>();

      // Iterate through the new rank keys and collect them to the temporary
      // data vector. If the key already exists in the data, it is removed from
      // the data vector.
      for (Busyhourrankkeys key : rankKeys) {

        boolean found = false;

        // Iterate through the existing rank keys.
        for (final Iterator<Busyhourrankkeys> iter2 = this.data.iterator(); iter2.hasNext();) {
          Busyhourrankkeys key2 = iter2.next();

          // If the primary key fields match (ignoring the case), then remove
          // the key from the data vector and add it to the temporary vector.
          if ((key.getVersionid().equals(key2.getVersionid()) && key.getBhlevel().equals(key2.getBhlevel())
              && key.getBhtype().equals(key2.getBhtype()) && key.getKeyname().equalsIgnoreCase(key2.getKeyname())
              && key.getTargetversionid().equals(key2.getTargetversionid()) && key.getBhobject().equals(
              key2.getBhobject()))) {

            found = true;
            tmpdata.add(key2);
            iter2.remove();
            break;
          }
        }

        // If no match, then add the new key to the temporary vector.
        if (!found) {
          tmpdata.add(key);
        }
      }

      // Clear the data.
      // NOTE: Existing data cannot be cleared, since it would destroy manual
      // modifications made by the user.
      // clear();

      // Add the collected data to the data and to the modified data.
      for (Busyhourrankkeys key : tmpdata) {
        addRow(key);
        modifiedData.add(key);
      }
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
    // Take a copy of the data to be able to revert back to old data if editing
    // is cancelled.
    copyOfTheData = new Vector<Busyhourrankkeys>();
    for (Busyhourrankkeys original : data) {
      copyOfTheData.add((Busyhourrankkeys) original.clone());
    }
  }

  public void stopEditing() {
    // Clear the copy of the data.
    copyOfTheData = null;
  }

  public void cancelEditing() {
    // Revert to the copy of the data.
    setData(copyOfTheData);
  }

  public RockDBObject createNew() {
    final Busyhourrankkeys busyhourrankkeys = new Busyhourrankkeys(rockFactory);
    busyhourrankkeys.setVersionid(busyhour.getVersionid());
    busyhourrankkeys.setTargetversionid(busyhour.getTargetversionid());
    busyhourrankkeys.setBhlevel(busyhour.getBhlevel());
    busyhourrankkeys.setBhobject(busyhour.getBhobject());
    busyhourrankkeys.setBhtype(busyhour.getBhtype());

    String keyname = "";
    String keyvalue = "";

    if (busyhourSourceRows.length > 0) {
      keyvalue = busyhourSourceRows[0] + ".";
    }

    busyhourrankkeys.setKeyname(keyname);
    busyhourrankkeys.setKeyvalue(keyvalue);
    busyhourrankkeys.setOrdernbr(0L);
    return busyhourrankkeys;
  }

  public void duplicateRow(final int[] selectedRows, final int times) {
    Object rankkeyData = null;
    for (int i = 0; i < times; i++) {
      for (int j = 0; j < selectedRows.length; j++) {
        rankkeyData = data.elementAt(selectedRows[j]);
        insertDataLast(copyOf(rankkeyData));
      }
    }
  }

  private Object copyOf(final Object toBeCopied) {
    final Busyhourrankkeys orig = (Busyhourrankkeys) toBeCopied;
    final Busyhourrankkeys copy = (Busyhourrankkeys) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  public JPopupMenu getPopUpMenu(final TableComponentListener listener, final Component component) {
    final JPopupMenu menu = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem(TableComponentListener.ADD_ROW);
    menuItem.addActionListener(listener);
    menu.add(menuItem);

    menuItem = new JMenuItem(TableComponentListener.REMOVE_ROW);
    menuItem.setEnabled(component instanceof JTable);
    menuItem.addActionListener(listener);
    menu.add(menuItem);

    menu.addSeparator();
    final JMenu submenu = new JMenu("Advanced");

    menuItem = new JMenuItem(TableComponentListener.ADD_MULTIPLE_ROWS);
    menuItem.addActionListener(listener);
    submenu.add(menuItem);

    menuItem = new JMenuItem(TableComponentListener.DUPLICATE_ROW);
    menuItem.setEnabled(false);
    if (component instanceof JTable) {
      if (((JTable) component).getSelectedRow() > -1) {
        menuItem.setEnabled(true);
      }
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
    data.insertElementAt((Busyhourrankkeys) datum, index);
    this.fireTableDataChanged();
  }

  public void insertDataLast(final Object datum) {
    data.insertElementAt((Busyhourrankkeys) datum, data.size());
    this.fireTableDataChanged();
  }

  public void removeSelectedData(final int[] selectedRows) {
    for (int i = selectedRows.length - 1; i >= 0; i--) {
      final Busyhourrankkeys del = removeRow(selectedRows[i]);
      deletedData.add(del);
    }
  }

  public void removeAllData() {
    for (Busyhourrankkeys bhKey : data) {
      deletedData.add(bhKey);
    }
    modifiedData.clear();
    data.clear();
  }

  public void setColumnEditors(final JTable editTable) {

    final TableColumn targetTPColumn2 = editTable.getColumnModel().getColumn(keynameColumnIdx);
    final LimitedSizeCellEditor targetTPColumnEditor2 = new LimitedSizeCellEditor(columnWidths[keynameColumnIdx],
        Busyhour.getTargetversionidColumnSize(), true);
    targetTPColumn2.setCellEditor(targetTPColumnEditor2);

    final TableColumn targetTPColumn3 = editTable.getColumnModel().getColumn(keyvalueSourceColumnIdx);
    JComboBox comboBox2 = new JComboBox(busyhourSourceRows);
    comboBox2.setMaximumRowCount(10);
    comboBox2.setEditable(editable);
    if (busyhourSource.size() > 0) {
      comboBox2.setSelectedIndex(0);
    }
    targetTPColumn3.setCellEditor(new DefaultCellEditor(comboBox2));

    final TableColumn targetTPColumn4 = editTable.getColumnModel().getColumn(keyvalueColumnIdx);
    final LimitedSizeCellEditor targetTPColumnEditor4 = new LimitedSizeCellEditor(columnWidths[keyvalueColumnIdx],
        Busyhour.getTargetversionidColumnSize(), true);
    targetTPColumn4.setCellEditor(targetTPColumnEditor4);
  }

  public void setColumnRenderers(final JTable editTable) {
    //
  }

  public void removeFromDB() {
    try {
      for (Busyhourrankkeys key : deletedData) {
        key.deleteDB();
      }
      for (Busyhourrankkeys key : data) {
        key.deleteDB();
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

    for (int i = 0; i < data.size(); i++) {
      for (int ii = i + 1; ii < data.size(); ii++) {
        if (data.get(i).getKeyname().equals(data.get(ii).getKeyname())) {
          errorStrings.add("Keyname " + data.get(ii).getKeyname() + " is duplicate in Rank Keys");
        }
      }
    }

    for (Busyhourrankkeys key : data) {

      if (Utils.replaceNull(key.getKeyname()).trim().equals("")) {
        errorStrings.add(columnNames[keynameColumnIdx] + " for Busy Hour Keys is required");
      }

      String keyValue = Utils.replaceNull(key.getKeyvalue());

      // The key value must not be empty. If there is a source table defined, it
      // is also checked that the key part also exists.
      if (keyValue.equals("")
          || (keyValue.contains(".") && (keyValue.substring(keyValue.indexOf(".") + 1).length() == 0))) {
        errorStrings.add(columnNames[keyvalueColumnIdx] + " for Busy Hour Keys is required");
      }

      // It is not allowed to have the source selected if the key value is
      // fixed, i.e. surrounded with single quotes. For example:
      // DC_E_CPP_VCLTP_COUNT.'XYZ' is not a valid rank key.
      if (keyValue.contains(".")) {
        // Get the key and check if it is a fixed value.
        String keyPart = keyValue.substring(keyValue.indexOf(".") + 1);
        if (keyPart.startsWith("'") && keyPart.endsWith("'")) {
          errorStrings.add(columnNames[keyvalueSourceColumnIdx] + " cannot be set with hard coded key value: " + keyPart);

        }
      }
    }
    return errorStrings;
  }

  public Object clone() {
    return null;
  }

}
