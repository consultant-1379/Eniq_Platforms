package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.DescriptionCellEditor;
import tableTree.DescriptionCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.ericsson.eniq.techpacksdk.common.Utils;

public class TagTableModel extends TTTableModel {

  private static final long serialVersionUID = -5150345618080149223L;

  private static final Logger logger = Logger.getLogger(TagTableModel.class.getName());

  private static final int tagColumnIdx = 0;

  private static final int descriptionColumnIdx = 1;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Tag", "Description" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 150, 350 };

  /**
   * The table type/name
   */
  private static final String myTableName = "Data Tags";

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 20;

  /**
   * Static method that returns the table type and its corresponding column
   * names
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

  private String dataformatid;

  /**
   * Constructor. Initializes the column names, widths and table name.
   */
  public TagTableModel(String dataformatid, RockFactory rockFactory, Vector<TableInformation> tableInformations,
      boolean editable) {
    super(rockFactory, tableInformations, editable);
    this.dataformatid = dataformatid;
    this.rockFactory = rockFactory;
    this.setColumnNames(myColumnNames);
    this.setTableName(myTableName);
    this.setColumnWidths(myColumnWidths);
  }

  /**
   * Overridden method for getting the value at a certain position in the table.
   * Gets it from the corresponding RockDBObject.
   */
  public Object getValueAt(int row, int col) {
    Defaulttags theAction = (Defaulttags) displayData.elementAt(row);

    switch (col) {
    case tagColumnIdx:
      if (theAction == null) {
        return "";
      }
      return theAction.getTagid();

    case descriptionColumnIdx:
      if (theAction == null) {
        return "";
      }
      return theAction.getDescription();

    default:
      break;
    }

    return data;
  }

  /**
   * Overridden method for getting the order value at a certain position in the
   * table. Gets it from the corresponding RockDBObject.
   */
  public Object getOriginalValueAt(int row, int col) {
    Defaulttags theAction = (Defaulttags) data.elementAt(row);

    switch (col) {
    case tagColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getTagid());

    case descriptionColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDescription());

    default:
      break;
    }

    return data;
  }

  /**
   * Overridden version of this method. Returns the value in a specified column
   * for the given data object. Returns null in case an invalid column index.
   * 
   * @return The data object in the cell
   */
  public Object getColumnValueAt(Object dataObject, int col) {
    Defaulttags theAction = (Defaulttags) dataObject;

    switch (col) {
    case tagColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getTagid());

    case descriptionColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDescription());

    default:
      break;
    }

    return null;
  }

  /**
   * Overridden method for setting the value at a certain position in the table
   * Sets it in the corresponding RockDBObject.
   */
  public void setValueAt(Object value, int row, int col) {
    int index = data.indexOf((Defaulttags) displayData.elementAt(row));
    Defaulttags theAction = (Defaulttags) data.elementAt(index);

    switch (col) {
    case tagColumnIdx:
      theAction.setTagid((String) value);
      break;

    case descriptionColumnIdx:
      theAction.setDescription((String) value);
      break;

    default:
      break;
    }
    // fireTableCellUpdated(row, col);
    refreshTable();
    fireTableDataChanged();
  }

  /**
   * Overridden method for setting the column editor of the Description column.
   */
  public void setColumnEditors(JTable theTable) {

    // Set editor for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(tagColumnIdx);
    LimitedSizeTextTableCellEditor datanameColumnEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[tagColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellEditor(datanameColumnEditor);

    // Description
    TableColumn descColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  public void setColumnRenderers(JTable theTable) {

    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(tagColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[tagColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);

    // Description
    TableColumn descColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descColumn.setCellRenderer(new DescriptionCellRenderer());
  }

  /**
   * Overridden method for creating specifically new Measurementcounters.
   */
  public RockDBObject createNew() {
    Defaulttags newCounter = new Defaulttags(rockFactory);
    newCounter.setDataformatid(dataformatid);
    newCounter.setTagid("NEW TAG");
    newCounter.setDescription("");
    return newCounter;
  }

  /**
   * Overridden version of this method for saving specifically
   * Measurementcounters.
   * 
   * @throws RockException
   * @throws SQLException
   */
  protected void saveData(Object rockObject) throws SQLException, RockException {
    Defaulttags action = ((Defaulttags) rockObject);
    try {
      if (action.gimmeModifiedColumns().size() > 0) {

        action.saveToDB();
        logger.info("save counter " + action.getDataformatid());
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for deleting specifically
   * Measurementcounters.
   * 
   * @throws RockException
   * @throws SQLException
   */
  protected void deleteData(Object rockObject) throws SQLException, RockException {
    ((Defaulttags) rockObject).deleteDB();
  }

  @Override
  protected void finalize() throws Throwable {
    logger.fine("MeasurementTypeCounterTableModel collected?");
    super.finalize();
  }

  @Override
  public Object copyOf(Object toBeCopied) {
    // TODO Auto-generated method stub
    Defaulttags orig = (Defaulttags) toBeCopied;
    Defaulttags copy = (Defaulttags) orig.clone();
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
  public boolean isCellEditable(int row, int col) {
    // Always allow the editing for the description column, so that the edit
    // button can be clicked also in the read-only mode. For all the other
    // columns, the editable value depends on the super class
    // implementation.
    if (col == descriptionColumnIdx)
      return true;
    else
      return super.isCellEditable(row, col);
  }

  @Override
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    Vector<String> dataTagCheckWithinOneMeas = new Vector<String>();
    
    for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      Object obj = iter.next();
      if (obj instanceof Defaulttags) {
    	  
        Defaulttags tra = (Defaulttags) obj;
        
        if (Utils.replaceNull(tra.getTagid()).trim().equals("")) {
          errorStrings.add(" Tag: " + myColumnNames[tagColumnIdx] + " for Action is required");
        } else {
          for (Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
            Object obj2 = iter2.next();
            if (obj2 instanceof Defaulttags) {
              Defaulttags measurementkey2 = (Defaulttags) obj2;
              if (measurementkey2 != tra) {
                if (measurementkey2.getTagid().equals(tra.getTagid())) {
                	final String testString = measurementkey2.getTagid() + measurementkey2.getDataformatid() ;
                	if(dataTagCheckWithinOneMeas.contains(testString)){
                		continue;
                	}else{
                		errorStrings.add(myColumnNames[tagColumnIdx] + "  ( " + tra.getTagid()
                                + " )  is not unique " + " under Data Tags in " + measurementkey2.getDataformatid());
                		dataTagCheckWithinOneMeas.add(testString);
                	}
                }
              }
            }
          }
        }
      }
    }

    return errorStrings;
  }
}
