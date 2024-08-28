package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.ComboBoxTableCellEditor;
import tableTree.ComboBoxTableCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the key table.
 *
 * @author enaland ejeahei eheitur
 *
 */
public class ETLSchedulerTableModel extends TTTableModel {

  private static final long serialVersionUID = -5150345618080149223L;

  private static final Logger logger = Logger.getLogger(ETLSchedulerTableModel.class.getName());

  private static final int nameColumnIdx = 0;

  private static final int activeColumnIdx = 1;

  private static final int typeColumnIdx = 2;

  private static final int propertiesColumnIdx = 3;

  private boolean editable;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Name", "Active", "Type", "Parameters" };

  Object[] schedulerTypeItems = { "fileExists", "interval", "timeDirCheck", "monthly", "once", "onStartup", "wait", "weekly",
      "weeklyinterval" };

  /**
   * Column widths, used to layout the table.
   */
  private static final int[] myColumnWidths = { 150, 25, 150, 350 };

  /**
   * The table type/name
   */
  private static final String myTableName = "Schedulings";

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 20;

  private Meta_collections mcol;

  /**
   * Static method that returns the table type and its corresponding column names
   *
   * @return
   */
  public static TableInformation createTableTypeInfo() {
    return new TableInformation(myTableName, myColumnNames, myColumnWidths, maxRowsShown);
  }

  private RockFactory rockFactory;

  /**
   * Constructor. Initializes the column names, widths and table name.
   */
  public ETLSchedulerTableModel(Meta_collections mcol, RockFactory rockFactory,
      Vector<TableInformation> tableInformations, boolean editable) {
    super(rockFactory, tableInformations, editable);
    this.rockFactory = rockFactory;
    this.mcol = mcol;
    this.editable = editable;
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
    this.setTableName(myTableName);
  }

  /**
   * Overridden method for getting the value at a certain position in the table. Gets it from the corresponding
   * RockDBObject.
   */
  public Object getValueAt(int row, int col) {
    Meta_schedulings theScheduling = (Meta_schedulings) displayData.elementAt(row);

    switch (col) {
    case nameColumnIdx:
      return theScheduling.getName();

    case activeColumnIdx:
      return "N".equalsIgnoreCase(theScheduling.getHold_flag());

    case typeColumnIdx:
      return theScheduling.getExecution_type();

    case propertiesColumnIdx:
      return theScheduling;

    default:
      break;
    }

    return data;
  }

  /**
   * Overridden method for getting the value at a certain position in the original table data. Gets it from the
   * corresponding RockDBObject.
   */
  public Object getOriginalValueAt(int row, int col) {
    Meta_schedulings theScheduling = (Meta_schedulings) data.elementAt(row);

    switch (col) {
    case nameColumnIdx:
      return Utils.replaceNull(theScheduling.getName());

    case activeColumnIdx:
      return "N".equalsIgnoreCase(theScheduling.getHold_flag());

    case typeColumnIdx:
      return Utils.replaceNull(theScheduling.getExecution_type());

    case propertiesColumnIdx:
      return theScheduling;

    default:
      logger.fine("getColumnValueAt(): Invalid column index: " + col + ".");
    }

    return null;
  }

  /**
   * Overridden version of this method. Returns the value in a specified column for the given data object. Returns null
   * in case an invalid column index.
   *
   * @return The data object in the cell
   */

  public Object getColumnValueAt(Object dataObject, int col) {
    Meta_schedulings theScheduling = (Meta_schedulings) dataObject;

    switch (col) {
    case nameColumnIdx:
      return theScheduling.getName();

    case activeColumnIdx:
      return "N".equalsIgnoreCase(theScheduling.getHold_flag());

    case typeColumnIdx:
      return Utils.replaceNull(theScheduling.getExecution_type());

    case propertiesColumnIdx:
      return theScheduling;

    default:
      logger.fine("getColumnValueAt(): Invalid column index: " + col + ".");
    }

    return null;
  }

  /**
   * Overridden method for setting the value at a certain position in the table Sets it in the corresponding
   * RockDBObject.
   */
  public void setValueAt(Object value, int row, int col) {
    int index = data.indexOf((Meta_schedulings) displayData.elementAt(row));
    Meta_schedulings theScheduling = (Meta_schedulings) data.elementAt(index);

    switch (col) {
    case nameColumnIdx:
      theScheduling.setName((String) value);
      break;

    case activeColumnIdx:

      if ((Boolean) value) {
        theScheduling.setHold_flag("N");
      } else {
        theScheduling.setHold_flag("Y");
      }
      break;

    case typeColumnIdx:
      // theScheduling.setExecution_type((String) value);
      break;

    case propertiesColumnIdx:
      //fireTableCellUpdated(row, typeColumnIdx);
      fireTableDataChanged();
      break;

    default:
      System.out.println("setValueAt(): Invalid column index: " + col + ".");
    }

    refreshTable();

    if (editable) {
      //fireTableCellUpdated(row, col);
      fireTableDataChanged();
    }

  }

  /**
   * Overridden method for setting the column editor of the Description and IsElement columns.
   */
  public void setColumnEditors(JTable theTable) {

    // Set editor for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(nameColumnIdx);
    LimitedSizeTextTableCellEditor datanameColumnEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[nameColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellEditor(datanameColumnEditor);

    // Set editor for type
    final TableColumn typeColumn= theTable.getColumnModel().getColumn(typeColumnIdx);
    JTextField jtf = new JTextField();
    jtf.setEnabled(false);
    DefaultCellEditor  typeColumnEditor = new DefaultCellEditor(jtf);
    typeColumn.setCellEditor(typeColumnEditor);

    // parameters
    TableColumn parameterColumn = theTable.getColumnModel().getColumn(this.propertiesColumnIdx);
    SchedulePropertiesTableCellEditor parameterColumnComboEditor = new SchedulePropertiesTableCellEditor(this
        .isTreeEditable());
    parameterColumn.setCellEditor(parameterColumnComboEditor);

  }

  /**
   * Overridden method for setting the column renderer. Used to set a renderer for the description field and isElement
   * combo box.
   */
  public void setColumnRenderers(JTable theTable) {

    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(nameColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[nameColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);

    // parameters
    TableColumn parameterColumn = theTable.getColumnModel().getColumn(this.propertiesColumnIdx);
    SchedulerPropertiesTableCellRenderer parameterRenderer = new SchedulerPropertiesTableCellRenderer();
    parameterColumn.setCellRenderer(parameterRenderer);

  }

  /**
   * Overridden version of this method for creating specifically Measurementkeys.
   */
  public RockDBObject createNew() {
    Meta_schedulings newScheduling = new Meta_schedulings(rockFactory);

    newScheduling.setVersion_number(mcol.getVersion_number());
    newScheduling.setCollection_id(mcol.getCollection_id());
    newScheduling.setCollection_set_id(mcol.getCollection_set_id());
    newScheduling.setId(-1l);
    newScheduling.setHold_flag("N");
    newScheduling.setExecution_type("wait");
    newScheduling.setName("");

    if (mcol.getCollection_id() == -1) {
      newScheduling.setCollection_id(getCollectionMaxID() + 1);
      mcol.setCollection_id(newScheduling.getCollection_id());
    }

    return newScheduling;
  }

  private long getSchedulingMaxID() {

    try {

      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(id) max from META_SCHEDULINGS");
      r.next();
      Long m = r.getLong("max");
      r.close();
      s.close();
      return m;

    } catch (Exception e) {
      logger.warning("Error while retrieving max transferActionID " + e.getMessage());
    }

    return 0;
  }

  /**
   * Overridden version of this method for saving specifically Measurementkeys.
   *
   * @throws RockException
   * @throws SQLException
   */
  protected void saveData(Object rockObject) throws SQLException, RockException {

    Meta_schedulings scheduling = ((Meta_schedulings) rockObject);
    try {
      if (scheduling.gimmeModifiedColumns().size() > 0) {
        if (scheduling.getId() == -1) {
          scheduling.setId(getSchedulingMaxID() + 1);
        }
        scheduling.saveDB();
        logger.info("save counter " + scheduling.getName() + " of " + scheduling.getExecution_type());
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for deleting specifically Measurementkeys.
   *
   * @throws RockException
   * @throws SQLException
   */
  protected void deleteData(Object rockObject) throws SQLException, RockException {
    ((Meta_schedulings) rockObject).deleteDB();
  }

  @Override
  public Object copyOf(Object toBeCopied) {
    Meta_schedulings copy = (Meta_schedulings) ((Meta_schedulings) toBeCopied).clone();
    copy.setId(-1L);
    copy.setNewItem(true);

    return copy;
  }

  @Override
  public String getColumnFilterForTableType(int column) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isColumnFilteredForTableType(int column) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void update(Observable sourceObject, Object sourceArgument) {
    // TODO Auto-generated method stub

  }

  /**
   * Overridden version of the method to allow description components to be clicked even though the tree is not editable
   * in the read-only mode.
   *
   * @param row
   * @param col
   * @return true if the cell is editable
   */
  public boolean isCellEditable(int row, int col) {
    // Always allow the editing for the description column, so that the edit
    // button can be clicked also in the read-only mode. For all the other
    // columns, the editable value depends on the super class
    // implementation.
    if (col == propertiesColumnIdx)
      return true;
    else
      return super.isCellEditable(row, col);
  }

  @Override
  public Vector<String> validateData() {

    // TODO Auto-generated method stub

    Vector<String> errorStrings = new Vector<String>();
    for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      Object obj = iter.next();
      if (obj instanceof Meta_schedulings) {

        Meta_schedulings tra = (Meta_schedulings) obj;

        if (Utils.replaceNull(tra.getName()).trim().equals("")) {
          errorStrings.add(mcol.getCollection_name() + " Key: " + myColumnNames[nameColumnIdx]
              + " for Scheduling is required");

        } else {

          for (Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
            Object obj2 = iter2.next();
            if (obj2 instanceof Meta_schedulings) {
              Meta_schedulings tra2 = (Meta_schedulings) obj2;
              if (tra2 != tra) {
                if (tra2.getName().equals(tra.getName())) {
                  errorStrings.add(mcol.getCollection_name() + " Key: " + myColumnNames[nameColumnIdx] + " ("
                      + tra.getTableName() + ") is not unique");
                }
              }
            }
          }
        }
      }
    }
    return errorStrings;
  }

  private long getCollectionMaxID() {

    try {

      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(collection_id) max from META_collections");
      r.next();
      Long m = r.getLong("max");
      r.close();
      s.close();
      return m;

    } catch (Exception e) {
      logger.warning("Error while retrieving max transferActionID " + e.getMessage());
    }

    return 0;
  }

}
