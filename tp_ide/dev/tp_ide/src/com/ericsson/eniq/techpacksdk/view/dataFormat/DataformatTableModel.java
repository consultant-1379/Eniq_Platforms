package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the counter table.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class DataformatTableModel extends TTTableModel {

  private static final long serialVersionUID = -5150345618080149223L;

  private static final Logger logger = Logger.getLogger(DataformatTableModel.class.getName());

  private static final int dataNameColumnIdx = 0;

  private static final int dataIDColumnIdx = 1;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Data Name", "Data ID" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 150, 150 };

  /**
   * The table type/name
   */
  private static final String myTableName = "Data Formats";

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

  /**
   * Constructor. Initializes the column names, widths and table name.
   */
  public DataformatTableModel(RockFactory rockFactory, Vector<TableInformation> tableInformations, boolean editable) {
    super(rockFactory, tableInformations, editable);
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
    Dataitem theAction = (Dataitem) displayData.elementAt(row);

    switch (col) {
    case dataNameColumnIdx:
      if (theAction == null) {
        return "";
      }
      return theAction.getDataname();

    case dataIDColumnIdx:
      if (theAction == null) {
        return "";
      }
      return theAction.getDataid();

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
    Dataitem theAction = (Dataitem) data.elementAt(row);

    switch (col) {
    case dataNameColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDataname());

    case dataIDColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDataid());

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
    Dataitem theAction = (Dataitem) dataObject;

    switch (col) {
    case dataNameColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDataname());

    case dataIDColumnIdx:
      if (theAction == null) {
        return "";
      }
      return Utils.replaceNull(theAction.getDataid());

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
    int index = data.indexOf((Dataitem) displayData.elementAt(row));
    Dataitem theAction = (Dataitem) data.elementAt(index);

    switch (col) {
    case dataNameColumnIdx:
      // theAction.setDataname((String) value);
      break;

    case dataIDColumnIdx:
      theAction.setDataid((String) value);
      break;

    default:
      break;
    }
    // fireTableCellUpdated(row, col);
    refreshTable();
    fireTableDataChanged();
  }

  /**
   * Overridden version of this method for saving specifically Dataitems. In
   * addition of saving the data item information to the DB, all the dataIds are
   * updated in applicable measurement and reference tables.
   * 
   * @throws RockException
   * @throws SQLException
   */
  protected void saveData(Object rockObject) throws SQLException, RockException {
    Dataitem di = ((Dataitem) rockObject);

    try {

      // If the dataItem was modified the save the changes to the DB and update
      // the dataId values for all applicable measurement and reference tables.
      if (di.isModified()) {

        Dataformat d = new Dataformat(rockFactory);
        d.setDataformatid(di.getDataformatid());
        com.distocraft.dc5000.repository.dwhrep.DataformatFactory dF = new com.distocraft.dc5000.repository.dwhrep.DataformatFactory(
            rockFactory, d);
        Dataformat dfor = dF.getElementAt(0);

        // Reference column

        Referencecolumn rcCond = new Referencecolumn(rockFactory);
        rcCond.setTypeid(dfor.getTypeid());
        rcCond.setDataname(di.getDataname());
        ReferencecolumnFactory rcF = new ReferencecolumnFactory(rockFactory, rcCond);
        Iterator<Referencecolumn> rColIter = rcF.get().iterator();
        while (rColIter.hasNext()) {
          Referencecolumn rCol = (Referencecolumn) rColIter.next();
          rCol.setDataid(di.getDataid());
          rCol.saveDB();
          logger.finest("DataId updated for reference type: " + rCol.getTypeid() + ", column: " + rCol.getDataname());
        }

        // Measurement key

        Measurementkey mk = new Measurementkey(rockFactory);
        mk.setTypeid(dfor.getTypeid());
        mk.setDataname(di.getDataname());
        MeasurementkeyFactory mkF = new MeasurementkeyFactory(rockFactory, mk);
        Iterator<Measurementkey> mKeyIter = mkF.get().iterator();
        while (mKeyIter.hasNext()) {
          Measurementkey mKey = (Measurementkey) mKeyIter.next();
          mKey.setDataid(di.getDataid());
          mKey.saveDB();
          logger.finest("DataId updated for measurement type: " + mKey.getTypeid() + ", key: " + mKey.getDataname());
        }

        // Measurement counter

        Measurementcounter mCounterCond = new Measurementcounter(rockFactory);
        mCounterCond.setTypeid(dfor.getTypeid());
        mCounterCond.setDataname(di.getDataname());
        MeasurementcounterFactory mcF = new MeasurementcounterFactory(rockFactory, mCounterCond);
        Iterator<Measurementcounter> mCounterIter = mcF.get().iterator();
        while (mCounterIter.hasNext()) {
          Measurementcounter mCounter = (Measurementcounter) mCounterIter.next();
          mCounter.setDataid(di.getDataid());
          mCounter.saveDB();
          logger.finest("DataId updated for measurement type: " + mCounter.getTypeid() + ", key: "
              + mCounter.getDataname());
        }

        // Measurement column
        //
        // Find all measurement columns with the matching data name. Check if
        // the
        // mTableId starts with the data format typeId. If yes,
        // the update the dataId. This way, all the different table levels are
        // covered.
        Measurementcolumn mColCond = new Measurementcolumn(rockFactory);
        mColCond.setDataname(di.getDataname());
        MeasurementcolumnFactory mColF = new MeasurementcolumnFactory(rockFactory, mColCond);
        Iterator<Measurementcolumn> mColIter = mColF.get().iterator();
        while (mColIter.hasNext()) {

          Measurementcolumn mColumn = (Measurementcolumn) mColIter.next();
          if (mColumn.getMtableid().startsWith(dfor.getTypeid())) {
            mColumn.setDataid(di.getDataid());
            mColumn.saveDB();
            logger.finest("DataId updated for measurement table: " + mColumn.getMtableid() + ", column: "
                + mColumn.getDataname());
          }
        }

        // Save the dataitem to the DB.
        di.saveToDB();
        // Clear the modified columns, because otherwise the same information
        // will always be saved, even if it was not changed again.
        di.cleanModifiedColumns();
        logger.fine("Saved data format: " + di.getDataformatid());
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for deleting specifically Dataitems.
   * 
   * @throws RockException
   * @throws SQLException
   */
  protected void deleteData(Object rockObject) throws SQLException, RockException {
    ((Dataitem) rockObject).deleteDB();
  }

  @Override
  protected void finalize() throws Throwable {
    logger.fine("MeasurementTypeCounterTableModel collected?");
    super.finalize();
  }

  @Override
  public Object copyOf(Object toBeCopied) {
    return toBeCopied;
  }

  @Override
  public RockDBObject createNew() {
    return null;
  }

  @Override
  public String getColumnFilterForTableType(int column) {
    return null;
  }

  @Override
  public boolean isColumnFilteredForTableType(int column) {
    return false;
  }

  @Override
  public void update(Observable sourceObject, Object sourceArgument) {
  }

  public boolean isTableRowAddRemoveAllowed() {
    // Adding or removing is not allowed.
    return false;
  }

  @Override
  public Vector<String> validateData() {
    return new Vector<String>();
  }

}
