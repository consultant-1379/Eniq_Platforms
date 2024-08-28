package com.ericsson.eniq.techpacksdk.view.reference;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Application;

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

import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the reference column table.
 * 
 * @author eheijun
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class ReferenceTypeColumnTableModel extends TTTableModel {

  private static final Logger logger = Logger.getLogger(ReferenceTypeColumnTableModel.class.getName());

  private final String techpackType;

  private final Referencetable referencetable;

  private final Application application;

  private final List<Referencecolumn> publiccolumns;

  private boolean isBaseTechPack = false;

  private static final int datanameColumnIdx = 0;

  private static final int datatypeColumnIdx = 1;

  private static final int datasizeColumnIdx = 2;

  private static final int datascaleColumnIdx = 3;

  private static final int uniquekeyColumnIdx = 4;

  private static final int nullableColumnIdx = 5;

  private static final int universeclassColumnIdx = 6;

  private static final int universeobjectColumnIdx = 7;

  private static final int universeconditionColumnIdx = 8;

  private static final int includesqlColumnIdx = 9;

  private static final int includeupdColumnIdx = 10;

  private static final int descriptionColumnIdx = 11;

  /**
   * Column names, used as headings for the columns.
   */
  private String[] myColumnNames = null;

  /**
   * Column widths, used to graphically layout the columns.
   */
  private int[] myColumnWidths = null;

  /**
   * Data Types, used in the drop down field for datatypes.
   */
  private String[] myDataTypes = null;

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  /**
   * Constructor. Initializes the column names, widths and table name.
   */
  public ReferenceTypeColumnTableModel(final Application application, final RockFactory rockFactory,
      final Vector<TableInformation> tableInfos, final boolean isTreeEditable, final Referencetable referencetable,
      final boolean isBaseTechPack, final List<Referencecolumn> publiccolumns, final String techpackType,
      String[] myColumnNames, int[] myColumnWidths, String myTableName, String[] myDataTypes) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.referencetable = referencetable;
    this.publiccolumns = publiccolumns;
    this.isBaseTechPack = isBaseTechPack;
    this.techpackType = techpackType;
    this.myColumnNames = myColumnNames;
    this.myColumnWidths = myColumnWidths;
    this.myDataTypes = myDataTypes;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  protected Object getColumnValue(final Referencecolumn referencecolumn, final int col) {
    if (referencecolumn != null) {
      switch (col) {
      case datanameColumnIdx:
        return Utils.replaceNull(referencecolumn.getDataname());

      case datatypeColumnIdx:
        return Utils.replaceNull(referencecolumn.getDatatype());

      case datasizeColumnIdx:
        return Utils.replaceNull(referencecolumn.getDatasize());

      case datascaleColumnIdx:
        return Utils.replaceNull(referencecolumn.getDatascale());

      case uniquekeyColumnIdx:
        return Utils.integerToBoolean(referencecolumn.getUniquekey());

      case nullableColumnIdx:
        return !(Utils.integerToBoolean(referencecolumn.getNullable()));

      case universeclassColumnIdx:
        return Utils.replaceNull(referencecolumn.getUniverseclass());

      case universeobjectColumnIdx:
        return Utils.replaceNull(referencecolumn.getUniverseobject());

      case universeconditionColumnIdx:
        return Utils.replaceNull(referencecolumn.getUniversecondition());

      case includesqlColumnIdx:
        return Utils.integerToBoolean(referencecolumn.getIncludesql());

      case includeupdColumnIdx:
        return Utils.integerToBoolean(referencecolumn.getIncludeupd());

      case descriptionColumnIdx:
        return Utils.replaceNull(referencecolumn.getDescription());

      default:
        break;
      }
    }
    return null;

  }

  protected void setColumnValue(final Referencecolumn referencecolumn, final int col, final Object value) {
    switch (col) {
    case datanameColumnIdx:
      referencecolumn.setDataname((String) value);
      break;

    case datatypeColumnIdx:
      referencecolumn.setDatatype((String) value);
      break;

    case datasizeColumnIdx:
      referencecolumn.setDatasize((Integer) value);
      break;

    case datascaleColumnIdx:
      referencecolumn.setDatascale((Integer) value);
      break;

    case uniquekeyColumnIdx:
      referencecolumn.setUniquekey(Utils.booleanToInteger((Boolean) value));
      break;

    case nullableColumnIdx:
      if ((Boolean) value) {
        referencecolumn.setNullable(0);
      } else {
        referencecolumn.setNullable(1);
      }
      break;

    case universeclassColumnIdx:
      referencecolumn.setUniverseclass((String) value);
      break;

    case universeobjectColumnIdx:
      referencecolumn.setUniverseobject((String) value);
      break;

    case universeconditionColumnIdx:
      referencecolumn.setUniversecondition((String) value);
      break;

    case includesqlColumnIdx:
      referencecolumn.setIncludesql(Utils.booleanToInteger((Boolean) value));
      break;

    case includeupdColumnIdx:
      referencecolumn.setIncludeupd(Utils.booleanToInteger((Boolean) value));
      break;

    case descriptionColumnIdx:
      referencecolumn.setDescription((String) value);
      break;

    default:
      break;
    }

  }

  /**
   * Overridden method for getting the value at a certain position in the table.
   * Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getValueAt(final int row, final int col) {
    Object result = null;
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Referencecolumn)) {
        final Referencecolumn referencecolumn = (Referencecolumn) displayData.elementAt(row);
        result = getColumnValue(referencecolumn, col);
      }
    }
    return result;
  }

  /**
   * Overridden version of this method. Returns the value in a specified column
   * for the given data object. Returns null in case an invalid column index.
   * 
   * @return The data object in the cell
   */
  @Override
  public Object getColumnValueAt(final Object dataObject, final int col) {
    Object result = null;
    if ((dataObject != null) && (dataObject instanceof Referencecolumn)) {
      final Referencecolumn referencecolumn = (Referencecolumn) dataObject;
      result = getColumnValue(referencecolumn, col);
    }
    return result;
  }

  /**
   * Overridden method for getting the value at a certain position in the
   * original table data. Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getOriginalValueAt(final int row, final int col) {
    Object result = null;
    if (data.size() >= row) {
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Referencecolumn)) {
        final Referencecolumn referencecolumn = (Referencecolumn) data.elementAt(row);
        result = getColumnValue(referencecolumn, col);
      }
    }
    return result;
  }

  /**
   * Overridden method for setting the value at a certain position in the table
   * Sets it in the corresponding RockDBObject.
   */
  @Override
  public void setValueAt(final Object value, final int row, final int col) {
    final int index = data.indexOf(displayData.elementAt(row));
    final Referencecolumn referencecolumn = (Referencecolumn) data.elementAt(index);
    setColumnValue(referencecolumn, col, value);
    // fireTableCellUpdated(row, col);
    refreshTable();
    fireTableDataChanged();
  }

  /**
   * Overridden method for setting the column editor of the Description column.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {

    // Set editor for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellEditor limitedTextEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[datanameColumnIdx], Referencecolumn.getDatanameColumnSize(), true);
    datanameColumn.setCellEditor(limitedTextEditor);

    // Set editor for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellEditor datatypeColumnComboEditor = new ComboBoxTableCellEditor(myDataTypes);
    datatypeColumn.setCellEditor(datatypeColumnComboEditor);

    // Set the cell editor for the description column.
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[datanameColumnIdx], Referencecolumn.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);

    // Set renderer for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellRenderer datatypeComboRenderer = new ComboBoxTableCellRenderer(myDataTypes, true);
    datatypeColumn.setCellRenderer(datatypeComboRenderer);

    // Set the renderer for the description column
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellRenderer(new DescriptionCellRenderer());
  }

  /**
   * Overridden method for creating specifically new Referencecolumns.
   */
  @Override
  public RockDBObject createNew() {
    long latestColNumber;
    String coltype;
    // if (publiccolumns == null) {
    // coltype = "PUBLICCOL";
    // } else {
    // coltype = "COLUMN";
    // }

    coltype = "COLUMN";

    if (data.size() > 0) {
      final Referencecolumn latestsKey = (Referencecolumn) data.get(data.size() - 1);
      latestColNumber = latestsKey.getColnumber();
    } else {
      if (isBaseTechPack) {
        latestColNumber = 0L;
      } else {
        latestColNumber = 100L;
      }
    }
    final Referencecolumn referencecolumn = new Referencecolumn(rockFactory);
    referencecolumn.setTypeid(referencetable.getTypeid());
    referencecolumn.setDataname("");
    referencecolumn.setDatasize(0);
    referencecolumn.setDatascale(0);
    referencecolumn.setNullable(0);
    referencecolumn.setUniquekey(0);
    referencecolumn.setIncludesql(0);
    referencecolumn.setIncludeupd(0);
    referencecolumn.setColnumber(Utils.replaceNull(latestColNumber + 1));
    referencecolumn.setUniquevalue(255L);
    referencecolumn.setIndexes("HG");
    referencecolumn.setColtype(coltype);
    // perform pseudochange
    referencetable.setTypeid(referencetable.getTypeid());
    referencecolumn.setBasedef(0);
    return referencecolumn;
  }

  /**
   * Overridden version of this method for saving specifically Referencecolumns.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {

    final Referencecolumn column = ((Referencecolumn) rockObject);
    try {
      if (column.gimmeModifiedColumns().size() > 0) {
      	//HL60833 
      	// Remove default HG index if Datasize > 255 
      	if(column.getDatasize() > 255) {
      	column.setIndexes("");
      	logger.finest("Typeid " + column.getTypeid() + " " + "DataName" + " " + column.getDataname() + " Not creating index as Datasize > 255: " + column.getDatasize());
      	}
        column.saveToDB();
        logger.info("save key " + column.getDataname() + " of " + column.getTypeid());
      }
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }

  }

  /**
   * Overridden version of this method for deleting specifically
   * Referencecolumns.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    try {
      ((Referencecolumn) rockObject).deleteDB();
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Referencecolumn.
   * 
   * @param currentData
   *          the Referencecolumn to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    if ((currentData != null) && (currentData instanceof Referencecolumn)) {
      ((Referencecolumn) currentData).setColnumber(Long.valueOf(newOrderNumber));
    }
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Referencecolumn.
   * 
   * @param currentData
   *          the Referencecolumn whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    int orderNumber = 101;
    if ((currentData != null) && (currentData instanceof Referencecolumn)) {
      orderNumber = Utils.replaceNull(((Referencecolumn) currentData).getColnumber()).intValue();
    }
    return orderNumber;
  }

  /**
   * Overridden version of this method. Indicates that this table has an order
   * column
   * 
   * @return true
   */
  @Override
  protected boolean dataHasOrder() {
    return true;
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    final Referencecolumn orig = (Referencecolumn) toBeCopied;
    final Referencecolumn copy = (Referencecolumn) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  @Override
  public String getColumnFilterForTableType(final int column) {
    return getTableInfo().getTableTypeColumnFilter(column);
  }

  @Override
  public boolean isColumnFilteredForTableType(final int column) {
    if (column < 0) {
      return false;
    }
    final String filter = getTableInfo().getTableTypeColumnFilter(column);
    return (filter != null && filter != "");
  }

  @Override
  public void update(final Observable sourceObject, final Object sourceArgument) {
    if ((sourceArgument != null) && (((String) sourceArgument).equals(ReferenceTypeParameterModel.TYPE_ID))) {
      final String value = ((ReferenceTypeParameterModel) sourceObject).getReferencetable().getTypeid();

      for (int i = 0; i < data.size(); i++) {
        ((Referencecolumn) data.elementAt(i)).setTypeid(value);
      }
    }
  }

  @Override
  public boolean isCellEditable(final int row, final int col) {
    // Always allow the editing for the description column, so that the edit
    // button can be clicked also in the read-only mode. For all the other
    // columns, the editable value depends on the super class
    // implementation.
    if (col == descriptionColumnIdx) {
      return true;
    } else {
      return super.isCellEditable(row, col);
    }
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      final Object obj = iter.next();
      if (obj instanceof Referencecolumn) {
        final Referencecolumn referencecolumn = (Referencecolumn) obj;
        if (Utils.replaceNull(referencecolumn.getDataname()).trim().equals("")) {
          errorStrings.add(referencetable.getTypename() + ": " + myColumnNames[datanameColumnIdx]
              + " for column is required");
        } else {
          for (final Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
            final Object obj2 = iter2.next();
            if (obj2 instanceof Referencecolumn) {
              final Referencecolumn referencecolumn2 = (Referencecolumn) obj2;
              if (referencecolumn2 != referencecolumn) {
                if (referencecolumn2.getDataname().equals(referencecolumn.getDataname())) {
                  errorStrings.add(referencetable.getTypename() + " Key: " + myColumnNames[datanameColumnIdx] + " ("
                      + referencecolumn.getDataname() + ") is not unique");
                }
              }
            }
          }
        }
        if (Utils.replaceNull(referencecolumn.getDatatype()).trim().equals("")) {
          errorStrings.add(referencetable.getTypename() + ": " + myColumnNames[datatypeColumnIdx]
              + " for column is required");
        }
        final Vector<String> datatypeCheck = Utils.checkDatatype(referencecolumn.getDatatype(), referencecolumn
            .getDatasize(), referencecolumn.getDatascale());
        for (final Iterator<String> iter2 = datatypeCheck.iterator(); iter2.hasNext();) {
          errorStrings.add(referencetable.getTypename() + " " + referencecolumn.getDataname() + " Column: "
              + iter2.next());
        }

        // Column name is not allowed if it is defined in the base techpack as a
        // public column.
        if (publiccolumns != null) {
          final Iterator publicColIter = publiccolumns.iterator();
          // Show erroe for 2="Dynamic" OR  3="Timed Dynamic"
          // 20110830 EANGUAN :: Adding comparison for policy number 4 for History Dynamic (for SON)
          while (publicColIter.hasNext()&& ((referencetable.getUpdate_policy()==2)||(referencetable.getUpdate_policy()==3) 
        		  || (referencetable.getUpdate_policy()==4))) {
            final Referencecolumn pubCol = (Referencecolumn) publicColIter.next();
            if (pubCol.getDataname().equalsIgnoreCase(referencecolumn.getDataname())) {
              errorStrings.add(referencetable.getTypename() + ": Column name '" + referencecolumn.getDataname()
                  + "' is already defined in the base techpack.");
            }
          }
        }
      }
    }

    return errorStrings;
  }
  
  /**
   * Help function for reordering the data. 
   * reorder(int) is private in TTTableModel class.
   */
  public void reorder() {
	final int newStartIndex = getOrderOf(data.elementAt(0));
    Object currentData = null;
    int runningIndex = newStartIndex;

    for (int i = 0; i < data.size(); i++) {
      currentData = data.elementAt(i);
      if (getOrderOf(currentData) != runningIndex) {
        setOrderOf(currentData, runningIndex);
      }
      runningIndex++;
    }
  } // reorder
  
  public boolean isOrderOk() {
	  Vector<String> duplicateCheck = new Vector<String>();
	  for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
	      final Object obj = iter.next();
	      if (obj instanceof Referencecolumn) {
	        final Referencecolumn referencecolumn = (Referencecolumn) obj;
	        if (duplicateCheck.contains(referencecolumn.getColnumber().toString())) {
	            return false;
	          } else {
	        	  duplicateCheck.add(referencecolumn.getColnumber().toString());
	          }
	      } //if (obj instanceof Referencecolumn)
	  } // for
	  return true;
  } // isOrderOk
}
