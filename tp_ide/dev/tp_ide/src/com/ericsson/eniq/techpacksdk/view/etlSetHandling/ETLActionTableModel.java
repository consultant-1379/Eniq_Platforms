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
import tableTree.DescriptionCellEditor;
import tableTree.DescriptionCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the counter table.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class ETLActionTableModel extends TTTableModel {

  private static final long serialVersionUID = -5150345618080149223L;

  private static final Logger logger = Logger.getLogger(ETLActionTableModel.class.getName());

  //20110727 eanguan :: For Remove grouping CR
  Object[] actionTypeItems = { "Aggregation", "AggregationRuleCopy", "AlarmHandler", "AggRuleCacheRefresh",
      "AlarmInterfaceUpdate",
      "AlarmMarkup", "AutomaticAggregation", "AutomaticREAggregati", "CreateDir", "DirectoryDiskmanager",
      "Diskmanager", "Distribute", "DuplicateCheck", "DWHMigrate", "ExecutionProfiler", "GateKeeper", "JDBC Mediation",
      "Join", "JVMMonitor", "Load", "ManualReAggregation", "Mediation", "Parse", "PartitionAction",
      "Partitioned Loader", "PartitionedSQLExec", "RefreshDBLookup", "ReloadDBLookups", "ReloadProperties",
      "ReloadTransformation", "SanityCheck", "SessionLog Loader", "SetTypeTrigger", "SMTP Mediation", "SNMP Poller",
      "SQL Execute", "SQL Extract", "StorageTimeAction", "System Call", "System Monitor", "TableCheck", "TableCleaner",
      "TriggerScheduledSet", "Uncompress", "UnPartitioned Loader", "UpdateDimSession", "UpdateMonitoredTypes",
      "UpdateMonitoring", "UpdatePlan", "VersionUpdate" };

  Object[] dbItems = { "etlrep", "dwhrep", "dwh" };

  private static final int nameColumnIdx = 0;

  private static final int activeColumnIdx = 1;

  private static final int dbColumnIdx = 2;

  private static final int typeColumnIdx = 3;

  private static final int descriptionColumnIdx = 4;

  private static final int propertiesColumnIdx = 5;

  private final String techpackType;
  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Name", "Active", "DB", "Type", "Description", "Properties" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 150, 25, 150, 150, 350, 350 };

  /**
   * The table type/name
   */
  private static final String myTableName = "Actions";

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 20;

  private final Meta_collections mcol;
  
  private final boolean editable;

  /**
   * Static method that returns the table type and its corresponding column names
   * 
   * @return
   */
  public static TableInformation createTableTypeInfo() {
    return new TableInformation(myTableName, myColumnNames, myColumnWidths, maxRowsShown);
  }

  /**
   * Rock factory, used to connect to the database.
   */
  private RockFactory rockFactory = null;

  /**
   * Constructor. Initializes the column names, widths and table name.
   */
  public ETLActionTableModel(Meta_collections mcol, RockFactory rockFactory,
      Vector<TableInformation> tableInformations, boolean editable, final String techpackType) {
    super(rockFactory, tableInformations, editable);
    this.rockFactory = rockFactory;
    this.mcol = mcol;

    this.editable = editable;
    this.techpackType = techpackType;
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
    this.setTableName(myTableName);

  }

  /**
   * Overridden method for getting the value at a certain position in the table. Gets it from the corresponding
   * RockDBObject.
   */
  @Override
  public Object getValueAt(int row, int col) {
    Meta_transfer_actions theAction = (Meta_transfer_actions) displayData.elementAt(row);

    switch (col) {
    case nameColumnIdx:
      return theAction.getTransfer_action_name();

    case activeColumnIdx:
      return "Y".equalsIgnoreCase(theAction.getEnabled_flag());

    case dbColumnIdx:

      if (theAction.getConnection_id() == null) {
        return dbItems[0];
      }

      return dbItems[new Long(theAction.getConnection_id()).intValue()];

    case typeColumnIdx:
      return Utils.replaceNull(theAction.getAction_type());

    case descriptionColumnIdx:

      if (theAction.getDescription() == null) {
        return "";
      }

      return theAction.getDescription();

    case propertiesColumnIdx:

      return theAction;

    default:
      break;
    }

    return data;
  }

  /**
   * Overridden method for getting the order value at a certain position in the table. Gets it from the corresponding
   * RockDBObject.
   */
  @Override
  public Object getOriginalValueAt(int row, int col) {
    Meta_transfer_actions theAction = (Meta_transfer_actions) data.elementAt(row);

    switch (col) {
    case nameColumnIdx:
      return Utils.replaceNull(theAction.getTransfer_action_name());

    case activeColumnIdx:
      return "Y".equalsIgnoreCase(theAction.getEnabled_flag());

    case dbColumnIdx:

      if (theAction.getConnection_id() == null) {
        return dbItems[0];
      }
      return dbItems[new Long(theAction.getConnection_id()).intValue()];

    case typeColumnIdx:
      return Utils.replaceNull(theAction.getAction_type());

    case descriptionColumnIdx:

      return Utils.replaceNull(theAction.getDescription());

    case propertiesColumnIdx:
      return theAction;

    default:
      break;
    }

    return data;
  }

  /**
   * Overridden version of this method. Returns the value in a specified column for the given data object. Returns null
   * in case an invalid column index.
   * 
   * @return The data object in the cell
   */
  @Override
  public Object getColumnValueAt(Object dataObject, int col) {
    Meta_transfer_actions theAction = (Meta_transfer_actions) dataObject;

    switch (col) {
    case nameColumnIdx:
      return Utils.replaceNull(theAction.getTransfer_action_name());

    case activeColumnIdx:
      return "Y".equalsIgnoreCase(theAction.getEnabled_flag());

    case dbColumnIdx:

      if (theAction.getConnection_id() == null) {
        return dbItems[0];
      }

      return dbItems[new Long(theAction.getConnection_id()).intValue()];

    case typeColumnIdx:
      return Utils.replaceNull(theAction.getAction_type());

    case descriptionColumnIdx:
      if (theAction.getDescription() == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDescription());

    case propertiesColumnIdx:
      return theAction;

    default:
      break;
    }

    return null;
  }

  /**
   * Overridden method for setting the value at a certain position in the table Sets it in the corresponding
   * RockDBObject.
   */
  @Override
  public void setValueAt(Object value, int row, int col) {
    int index = data.indexOf(displayData.elementAt(row));
    Meta_transfer_actions theAction = (Meta_transfer_actions) data.elementAt(index);

    switch (col) {
    case nameColumnIdx:
      theAction.setTransfer_action_name((String) value);
      break;

    case activeColumnIdx:
      if ((Boolean) value) {
        theAction.setEnabled_flag("Y");
      } else {
        theAction.setEnabled_flag("N");
      }
      break;

    case dbColumnIdx:

      for (int i = 0; i < dbItems.length; i++) {
        if (((String) dbItems[i]).equalsIgnoreCase((String) value)) {
          theAction.setConnection_id(new Long(i));
          break;
        }
      }

      break;

    case typeColumnIdx:
      // theAction.setAction_type((String) value);
      break;

    case descriptionColumnIdx:
      theAction.setDescription((String) value);
      break;

    case propertiesColumnIdx:
      //theAction = (Meta_transfer_actions) value;
      data.set(index, value);
      theAction.setAction_type(((Meta_transfer_actions) value).getAction_type());
      //fireTableCellUpdated(row, typeColumnIdx);
      fireTableDataChanged();
      break;

    default:
      break;
    }
    if (editable){
      //fireTableCellUpdated(row, col);
      fireTableDataChanged();
    }
    refreshTable();
  }

  /**
   * Overridden method for setting the column editor of the Description column.
   */
  @Override
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
    
    // db
    TableColumn dbColumn = theTable.getColumnModel().getColumn(dbColumnIdx);
    ComboBoxTableCellEditor dbColumnComboEditor = new ComboBoxTableCellEditor(dbItems);
    dbColumn.setCellEditor(dbColumnComboEditor);

    // Description
    TableColumn descColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));

    // Parametrs
    TableColumn propertiesColumn = theTable.getColumnModel().getColumn(propertiesColumnIdx);
    propertiesColumn.setCellEditor(new PropertiesTableCellEditor(editable, techpackType));

  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(JTable theTable) {

    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(nameColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[nameColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);
    
    // db
    TableColumn dbColumn = theTable.getColumnModel().getColumn(dbColumnIdx);
    ComboBoxTableCellRenderer dbComboRenderer = new ComboBoxTableCellRenderer(dbItems);
    dbColumn.setCellRenderer(dbComboRenderer);

    // Description
    TableColumn descColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descColumn.setCellRenderer(new DescriptionCellRenderer());

    // Parametrs
    TableColumn propertiesColumn = theTable.getColumnModel().getColumn(propertiesColumnIdx);
    PropertiesTableCellRenderer propertiedColumnRenderer = new PropertiesTableCellRenderer();
    propertiesColumn.setCellRenderer(propertiedColumnRenderer);

  }

  /**
   * Overridden method for creating specifically new Measurementcounters.
   */
  @Override
  public RockDBObject createNew() {
    Meta_transfer_actions newCounter = new Meta_transfer_actions(rockFactory);

    newCounter.setVersion_number(mcol.getVersion_number());
    newCounter.setCollection_id(mcol.getCollection_id());
    newCounter.setCollection_set_id(mcol.getCollection_set_id());
    newCounter.setTransfer_action_id(-1l);
    newCounter.setAction_type("Aggregation");
    newCounter.setConnection_id(0L);

    if (mcol.getCollection_id()==-1){
      newCounter.setCollection_id(getCollectionMaxID() + 1);
      mcol.setCollection_id(newCounter.getCollection_id());
    }      

    
    if (this.data == null || this.data.size() == 0) {
      newCounter.setOrder_by_no(0L);
    } else {
      newCounter.setOrder_by_no(((Meta_transfer_actions) this.data.lastElement()).getOrder_by_no() + 1);
    }

    newCounter.setEnabled_flag("Y");
    return newCounter;
  }

  private long getActionMaxID() {

    try {
      
      Statement s = rockFactory.getConnection().createStatement();
      ResultSet r = s.executeQuery("select max(transfer_action_id) max from META_TRANSFER_ACTIONS");     
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
  
  /**
   * Overridden version of this method for saving specifically Measurementcounters.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(Object rockObject) throws SQLException, RockException {
    Meta_transfer_actions action = ((Meta_transfer_actions) rockObject);
    try {
      if (action.gimmeModifiedColumns().size() > 0) {
        if (action.getTransfer_action_id()==-1){
          action.setTransfer_action_id(getActionMaxID() + 1);
        }      
        action.saveDB();
        logger.info("save counter " + action.getTransfer_action_name() + " of " + action.getAction_type());
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for deleting specifically Measurementcounters.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(Object rockObject) throws SQLException, RockException {
    ((Meta_transfer_actions) rockObject).deleteDB();
  }

  @Override
  protected void finalize() throws Throwable {
    logger.fine("MeasurementTypeCounterTableModel collected?");
    super.finalize();
  }

  @Override
  public Object copyOf(Object toBeCopied) {
    Meta_transfer_actions copy = (Meta_transfer_actions)((Meta_transfer_actions) toBeCopied).clone();   
    copy.setTransfer_action_id(-1L);
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
   * Overridden version of the method to allow description components to be
   * clicked even though the tree is not editable in the read-only mode.
   * 
   * @param row
   * @param col
   * @return true if the cell is editable
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    // Always allow the editing for the description column, so that the edit
    // button can be clicked also in the read-only mode. For all the other
    // columns, the editable value depends on the super class
    // implementation.
    if (col == descriptionColumnIdx || col == propertiesColumnIdx)
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
        if (obj instanceof Meta_transfer_actions) {
          
          Meta_transfer_actions tra = (Meta_transfer_actions) obj;
          
          if (Utils.replaceNull(tra.getTransfer_action_name()).trim().equals("")) {
            errorStrings.add(mcol.getCollection_name()+ " Key: "+ myColumnNames[nameColumnIdx]
                                                                           + " for Action is required");
            
            
            
          } else {
          
/*
          

          for (Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
            Object obj2 = iter2.next();
            if (obj2 instanceof Meta_transfer_actions) {
              Meta_transfer_actions tra2 = (Meta_transfer_actions) obj2;
              if (tra2 != tra) {
                if (tra2.getTransfer_action_name().equals(tra.getTransfer_action_name())) {
                  errorStrings.add(tra.getTableName() + " Key: "
                      + myColumnNames[nameColumnIdx] +  " (" + tra.getTableName() + ") is not unique");
                }
              }
            }
          }
          */
          }     
          
          
        }
      }
      
      return errorStrings;
    
  }

  /**
   * Overridden version of this method. Indicates that this table has an order column
   * 
   * @return true
   */
  @Override
  protected boolean dataHasOrder() {
    return true;
  }
  
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    if ((currentData != null) && (currentData instanceof Meta_transfer_actions)) {
      ((Meta_transfer_actions) currentData).setOrder_by_no(Long.valueOf(newOrderNumber));
    }
  }

  @Override
  protected int getOrderOf(final Object currentData) {
    int orderNumber = 0;
    if ((currentData != null) && (currentData instanceof Meta_transfer_actions)) {
      orderNumber = Utils.replaceNull(((Meta_transfer_actions) currentData).getOrder_by_no()).intValue();
    }
    return orderNumber;
  }
  
}
