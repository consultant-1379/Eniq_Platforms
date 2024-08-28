package com.ericsson.eniq.techpacksdk.view.measurement;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
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
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.DescriptionCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the key table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class MeasurementTypeKeyTableModel extends TTTableModel implements Observer {

  private static final Logger logger = Logger.getLogger(MeasurementTypeKeyTableModel.class.getName());

  /**
   * The table type/name
   */
  public static final String myTableName = "Keys";

  private final String techpackType;

  private final Measurementtype measurementtype;

  private final Application application;

  private static final int datanameColumnIdx = 0;

  private static final int datatypeColumnIdx = 1;

  private static final int datasizeColumnIdx = 2;

  private static final int datascaleColumnIdx = 3;

  private static final int uniquekeyColumnIdx = 4;

  private static final int nullableColumnIdx = 5;

  private static final int indexesColumnIdx = 6;

  private static final int univobjectColumnIdx = 7;

  private static final int iselementColumnIdx = 8;

  private static final int includesqlColumnIdx = 9;

  private static final int joinableColumnIdx = 10;
  
  private static final int ropGrpCellColumnIdx = 11;
  
  private static final int descriptionColumnIdx = 12;

  /**
   * Column names, used as headings for the columns. dataname description
   * datatype datasize datascale uniquekey nullable indexes univobject iselement
   * includesql
   */
  private static final String[] myColumnNames = { "Name", "DataType", "DataSize", "DataScale", "DuplicateConstraint",
      "Mandatory", "IQIndex", "UnivObject", "ElementColumn", "SQLInterfaceSupport", "Joinable", "RopGrpCell", "Description" };

  /**
   * Column widths, used to layout the table.
   */
  private static final int[] myColumnWidths = { 120, 120, 60, 60, 60, 60, 120, 120, 60, 60, 60, 60, 200 };

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int maxRowsShown = 25;

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
   * Constructor. Initializes the column names, widths and table name.
   */
  public MeasurementTypeKeyTableModel(final Application application, final RockFactory rockFactory,
      final Vector<TableInformation> tableInfos, final boolean isTreeEditable, final Measurementtype measurementtype,
      final String techpackType) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.measurementtype = measurementtype;
    this.techpackType = techpackType;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final Measurementkey measurementkey, final int col) {
    if (measurementkey != null) {
      switch (col) {
      case datanameColumnIdx:
        return Utils.replaceNull(measurementkey.getDataname());

      case datatypeColumnIdx:
        //return Utils.replaceNull(measurementkey.getDatatype().toLowerCase());
    	  return Utils.replaceNull(measurementkey.getDatatype());

      case datasizeColumnIdx:
        return Utils.replaceNull(measurementkey.getDatasize());

      case datascaleColumnIdx:
        return Utils.replaceNull(measurementkey.getDatascale());

      case uniquekeyColumnIdx:
        return Utils.integerToBoolean(measurementkey.getUniquekey());

      case nullableColumnIdx:
        return !(Utils.integerToBoolean(measurementkey.getNullable()));

      case indexesColumnIdx:
        return Utils.replaceNull(measurementkey.getIndexes());

      case univobjectColumnIdx:
        return Utils.replaceNull(measurementkey.getUnivobject());

      case iselementColumnIdx:
        return Utils.integerToBoolean(measurementkey.getIselement());

      case includesqlColumnIdx:
        return Utils.integerToBoolean(measurementkey.getIncludesql());

      case joinableColumnIdx:
        return Utils.integerToBoolean(measurementkey.getJoinable());
        
      case ropGrpCellColumnIdx:
    	  return Utils.integerToBoolean(measurementkey.getRopgrpcell());
    	  
      case descriptionColumnIdx:
        return Utils.replaceNull(measurementkey.getDescription());

      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Measurementkey measurementkey, final int col, final Object value) {
    if (measurementkey != null) {
      switch (col) {
      case datanameColumnIdx:
        final String originalName = Utils.replaceNull(measurementkey.getDataname()).trim();
        final String originalUniv = Utils.replaceNull(measurementkey.getUnivobject()).trim();
        measurementkey.setDataname(Utils.replaceNull((String) value).trim());
        if (Utils.replaceNull(measurementkey.getUnivobject()).trim().equals("") || originalUniv.equals(originalName)) {
          String temp = measurementkey.getDataname();
          if (temp.length() > Measurementkey.getUnivobjectColumnSize()) {
            temp = temp.substring(0, Measurementkey.getUnivobjectColumnSize());
          }
          measurementkey.setUnivobject(temp);
        }
        //eeoidiv 20100622 HL53524 TechPackIDE: data references copied incorrectly.
        // Always set the dataId to match the dataName when dataName changed.

        // In case the dataId is null, set the dataId to match the dataName.
        if (measurementkey.getDataid() == null) {
          measurementkey.setDataid(measurementkey.getDataname());
        }
        break;

      case datatypeColumnIdx:
        measurementkey.setDatatype(Utils.replaceNull((String) value).trim());
        break;

      case datasizeColumnIdx:
        measurementkey.setDatasize(Utils.replaceNull((Integer) value));
        break;

      case datascaleColumnIdx:
        measurementkey.setDatascale(Utils.replaceNull((Integer) value));
        break;

      case uniquekeyColumnIdx:
        measurementkey.setUniquekey(Utils.booleanToInteger((Boolean) value));
        break;

      case nullableColumnIdx:
        if ((Boolean) value)
          measurementkey.setNullable(0);
        else
          measurementkey.setNullable(1);
        break;

      case indexesColumnIdx:
        measurementkey.setIndexes(Utils.replaceNull((String) value).trim());
        break;

      case univobjectColumnIdx:
        measurementkey.setUnivobject(Utils.replaceNull((String) value).trim());
        break;

      case iselementColumnIdx:
        measurementkey.setIselement(Utils.booleanToInteger((Boolean) value));
        break;

      case includesqlColumnIdx:
        measurementkey.setIncludesql(Utils.booleanToInteger((Boolean) value));
        break;

      case joinableColumnIdx:
        measurementkey.setJoinable(Utils.booleanToInteger((Boolean) value));
        break;              
        
      case ropGrpCellColumnIdx:
    	  measurementkey.setRopgrpcell(Utils.booleanToInteger((Boolean) value));
    	  break;

      case descriptionColumnIdx:
        measurementkey.setDescription(Utils.replaceNull((String) value).trim());
        break;

      default:
        break;
      }
    }
    // perform pseudochange
    measurementtype.setTypename(measurementtype.getTypename());
  }

  /**
   * Overridden method for getting the value at a certain position in the table.
   * Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getValueAt(final int row, final int col) {
    Object result = null;
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Measurementkey)) {
        final Measurementkey measurementkey = (Measurementkey) displayData.elementAt(row);
        result = getColumnValue(measurementkey, col);
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
    if ((dataObject != null) && (dataObject instanceof Measurementkey)) {
      final Measurementkey measurementkey = (Measurementkey) dataObject;
      result = getColumnValue(measurementkey, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Measurementkey)) {
        final Measurementkey measurementkey = (Measurementkey) data.elementAt(row);
        result = getColumnValue(measurementkey, col);
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

    // Get the key object in the specified "row" in the display data vector.
    // Get the index for that object in the real data vector. Set the column
    // value for the object in the data vector at the index.
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Measurementkey)) {
        final int index = data.indexOf(displayData.elementAt(row));
        final Measurementkey measurementkey = (Measurementkey) data.elementAt(index);
        setColumnValue(measurementkey, col, value);
        // fireTableCellUpdated(row, col);
        refreshTable();
        fireTableDataChanged();
      }
    }
  }

  /**
   * Overridden method for setting the column editor of the Description and
   * IsElement columns.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {

    // Set editor for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellEditor datanameColumnEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[datanameColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellEditor(datanameColumnEditor);

    // Set editor for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellEditor datatypeColumnComboEditor = getComboBoxTableCellEditor();
    datatypeColumn.setCellEditor(datatypeColumnComboEditor);

    // // Set editor for univobject
    // final TableColumn univobjectColumn = theTable.getColumnModel()
    // .getColumn(univobjectColumnIdx);
    // LimitedSizeTextTableCellEditor univobjectColumnEditor = new
    // LimitedSizeTextTableCellEditor(
    // myColumnWidths[univobjectColumnIdx], 27, true);
    // univobjectColumn.setCellEditor(univobjectColumnEditor);

    // Set the cell editor for the description column.
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));
  }

  private ComboBoxTableCellEditor getComboBoxTableCellEditor() {
    if (techpackType.equals(Constants.ENIQ_EVENT)) {
      return new ComboBoxTableCellEditor(Constants.EVENTSDATATYPES);
    }
    return new ComboBoxTableCellEditor(Constants.DATATYPES);
  }

  /**
   * Overridden method for setting the column renderer. Used to set a renderer
   * for the description field and isElement combo box.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {

    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[datanameColumnIdx], Measurementkey.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);

    // Set renderer for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellRenderer datatypeComboRenderer = getComboBoxTableCellRender();
    datatypeColumn.setCellRenderer(datatypeComboRenderer);

    // Set renderer for univobject
    final TableColumn univobjectColumn = theTable.getColumnModel().getColumn(univobjectColumnIdx);
    final LimitedSizeTextTableCellRenderer univobjectComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[univobjectColumnIdx], 27, true);
    univobjectColumn.setCellRenderer(univobjectComboRenderer);

    // Set the renderer for the description column
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellRenderer(new DescriptionCellRenderer());
  }

  private ComboBoxTableCellRenderer getComboBoxTableCellRender() {
    if (techpackType.equals(Constants.ENIQ_EVENT)) {
      return new ComboBoxTableCellRenderer(Constants.EVENTSDATATYPES, true);
    }
    return new ComboBoxTableCellRenderer(Constants.DATATYPES, true);
  }

  private long getLatestColNumber() {
    long latestColNumber = 0L;
    if (data.size() > 0) {
      final Measurementkey latestsKey = (Measurementkey) data.get(data.size() - 1);
      if (latestsKey.getColnumber() == null) {
        latestColNumber = 1;
      } else {
        latestColNumber = latestsKey.getColnumber();
      }
    }
    return latestColNumber;
  }

  /**
   * Overridden version of this method for creating specifically
   * Measurementkeys.
   */
  @Override
  public RockDBObject createNew() {
    final Measurementkey measurementkey = new Measurementkey(rockFactory);
    measurementkey.setTypeid(measurementtype.getTypeid());
    measurementkey.setDataname("");
    measurementkey.setDatasize(0);
    measurementkey.setDatascale(0);
    measurementkey.setDescription("");
    measurementkey.setIselement(0);
    measurementkey.setColnumber(getLatestColNumber() + 1);
    measurementkey.setIncludesql(1);
    measurementkey.setUniquevalue(255L);
    measurementkey.setNullable(0);
    measurementkey.setUniquekey(0);
    measurementkey.setJoinable(0);
    measurementkey.setRopgrpcell(0);
    measurementkey.setIndexes("");
    // perform pseudochange
    measurementtype.setTypename(measurementtype.getTypename());
    return measurementkey;
  }

  /**
   * Overridden version of this method for saving specifically Measurementkeys.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {
    final Measurementkey key = ((Measurementkey) rockObject);
    try {
      if (key.gimmeModifiedColumns().size() > 0) {
        //checkMeasurementkey(key);
    	// 20110815 eanguan :: By default to make the DataID name same as TypeID in Key ( User can change 
    	if((key.getDataname() != null) && (!(key.getDataname().trim().equals("")))){
    		if(key.gimmeModifiedColumns().contains("DATANAME")){
    			key.setDataid(key.getDataname());
    		}
    	}		
        key.saveToDB();
        logger.info("save key " + key.getDataname() + " of " + key.getTypeid());
      }
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  
  }

  // private void checkMeasurementkey(Measurementkey key) throws Exception {
  // // TODO get texts from resources
  // if (key.getDataname().trim().equals("")) {
  // throw new Exception("Dataname can not be empty.");
  // }
  // if (key.getDatatype().trim().equals("")) {
  // throw new Exception("Datatype can not be empty.");
  // }
  // }

  /**
   * Overridden version of this method for deleting specifically
   * Measurementkeys.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    final Measurementkey key = ((Measurementkey) rockObject);
    try {
      key.deleteDB();
      // perform pseudochange
      measurementtype.setTypename(measurementtype.getTypename());
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Measurementkey. In this example, we pretend that the UniqueKey field is the
   * order number.
   * 
   * @param currentData
   *          the Measurementkey to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    if ((currentData != null) && (currentData instanceof Measurementkey)) {
      ((Measurementkey) currentData).setColnumber(Long.valueOf(newOrderNumber));
    }
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Measurementkey.
   * 
   * @param currentData
   *          the Measurementkey whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    int orderNumber = 0;
    if ((currentData != null) && (currentData instanceof Measurementkey)) {
      orderNumber = Utils.replaceNull(((Measurementkey) currentData).getColnumber()).intValue();
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
    final Measurementkey orig = (Measurementkey) toBeCopied;
    final Measurementkey copy = (Measurementkey) orig.clone();
    if (!orig.getDataname().equals("")) {
      int count = 0;
      for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
        final Object obj = iter.next();
        if (obj instanceof Measurementkey) {
          final Measurementkey mk = (Measurementkey) obj;
          if (mk.getDataname().equals(copy.getDataname())) {
            count = 1;
          }
          if (mk.getDataname().startsWith(copy.getDataname() + "-")) {
            count++;
          }
        }
      }
      count++;
      copy.setDataname(orig.getDataname() + "-" + count);
      if (orig.getDataname().equals(orig.getUnivobject())) {
        copy.setUnivobject(copy.getDataname());
      }
      // eeoidiv 20100622 HL53524 TechPackIDE: data references copied incorrectly.
      // DataId needs to be same as new name.
      copy.setDataid(copy.getDataname());
    }
    copy.setColnumber(getLatestColNumber() + 1);
    copy.setNewItem(true);
    
    // perform pseudochange
    measurementtype.setTypename(measurementtype.getTypename());

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
    if ((sourceArgument != null) && (((String) sourceArgument).equals(MeasurementTypeParameterModel.TYPE_ID))) {
      final String value = ((MeasurementTypeParameterModel) sourceObject).getMeasurementtypeExt().getMeasurementtype()
          .getTypeid();
      final Vector<Object> copies = new Vector<Object>();
      for (int i = 0; i < data.size(); i++) {
        ((Measurementkey) data.elementAt(i)).setTypeid(value);
        if (data.elementAt(i) instanceof Measurementkey) {
          try {
            final Measurementkey orig = (Measurementkey) data.elementAt(i);
            final Measurementkey copy = (Measurementkey) orig.clone();
            copy.setTypeid(value);
            copies.add(copy);
          } catch (final Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
      for (int i = data.size(); data.size() > 0; i--) {
        if (data.elementAt(i - 1) instanceof Measurementkey) {
          final Measurementkey orig = (Measurementkey) data.elementAt(i - 1);
          markedForDeletion.add(orig);
          data.remove(orig);
        }
      }
      for (int i = 0; i < copies.size(); i++) {
        if (copies.elementAt(i) instanceof Measurementkey) {
          try {
            final Measurementkey copy = (Measurementkey) copies.elementAt(i);
            insertDataLast(copy);
          } catch (final Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
    }
  }

  @Override
  public boolean isCellEditable(final int row, final int col) {
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
    final Vector<String> errorStrings = new Vector<String>();
    int rowcount=0;
    int selectedkey=0;
    for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      final Object obj = iter.next();     
      if (obj instanceof Measurementkey) {
        final Measurementkey measurementkey = (Measurementkey) obj;
  
  		  Boolean chkkey = (Boolean) getOriginalValueAt(rowcount, iselementColumnIdx);
  		  //chkkey will be true when any key is selected as element coulmn.
  		   if(chkkey){
  			   selectedkey=selectedkey+1;  
  		  }
  		   rowcount++;
        if (Utils.replaceNull(measurementkey.getDataname()).trim().equals("")) {
          errorStrings.add(measurementtype.getTypename() + myColumnNames[datanameColumnIdx] + " for key is required");
        } else {
          for (final Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
            final Object obj2 = iter2.next();
            if (obj2 instanceof Measurementkey) {
              final Measurementkey measurementkey2 = (Measurementkey) obj2;
              if (measurementkey2 != measurementkey) {
                if (measurementkey2.getDataname().equals(measurementkey.getDataname())) {
                  errorStrings.add(measurementtype.getTypename() + " Key: " + myColumnNames[datanameColumnIdx] + " ("
                      + measurementkey.getDataname() + ") is not unique");
                }
              }
            }
           
          }
        }
        if (Utils.replaceNull(measurementkey.getDatatype()).trim().equals("")) {
          errorStrings.add(measurementtype.getTypename() + " " + measurementkey.getDataname() + " Key: "
              + myColumnNames[datatypeColumnIdx] + " for key is required");
        }
        final Vector<String> datatypeCheck = Utils.checkDatatype(measurementkey.getDatatype(), measurementkey
            .getDatasize(), measurementkey.getDatascale());
        for (final Iterator<String> iter2 = datatypeCheck.iterator(); iter2.hasNext();) {
          errorStrings
              .add(measurementtype.getTypename() + " " + measurementkey.getDataname() + " Key: " + iter2.next());
        }
      }
    }
    
    //20110624 eanguan :: To disable the Element selection check for EVENTS 
    try{
    	if(!com.ericsson.eniq.common.Utils.getTechPackType(getRockFactory(), measurementtype.getVersionid()).equals(com.ericsson.eniq.common.TechPackType.EVENTS)){
    		if(selectedkey!= 1 && getRowCount()>0){
    	  		errorStrings.add(measurementtype.getTypename() + " one and only one element key should be selected ");
    	    }
    	}
    }catch(final Exception e){
    	String message = " Exception while getting the TechPack Type : " ;
    	message += e.getMessage();
    	logger.warning(message);
    }
    return errorStrings;
  }

}
