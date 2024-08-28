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
import tableTree.DescriptionCellRenderer;
import tableTree.LimitedSizeTextTableCellEditor;
import tableTree.LimitedSizeTextTableCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the counter table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class MeasurementTypeCounterTableModel extends TTTableModel implements Observer {

  private static final Logger logger = Logger.getLogger(MeasurementTypeCounterTableModel.class.getName());

  /**
   * The table type/name
   */
  private static final String myTableName = "Counters";

  private final String techpackType;

  private final MeasurementtypeExt measurementtypeExt;

  private final String[] vendorReleases;

  private static final int datanameColumnIdx = 0;

  private static final int datatypeColumnIdx = 1;

  private static final int datasizeColumnIdx = 2;

  private static final int datascaleColumnIdx = 3;

  private static final int timeaggregationColumnIdx = 4;

  private static final int groupaggregationColumnIdx = 5;

  private static final int countertypeColumnIdx = 6;

  private static final int vectorCounterColumnIdx = 7;

  private static final int univobjectColumnIdx = 8;

  private static final int univclassColumnIdx = 9;

  private static final int includesqlColumnIdx = 10;

  private static final int descriptionColumnIdx = 11;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Name", "DataType", "DataSize", "DataScale",
      "TimeAggregationFormula", "OtherAggregationFormulas", "CounterType", "VC", "UnivObject", "UnivClass",
      "SQLInterfaceSupport", "Description" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 120, 120, 60, 60, 120, 120, 120, 40, 90, 90, 60, 200 };

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
  private final RockFactory rockFactory;

  private final Application application;

  /**
   * Constructor. Initializes the column names, widths and table name.
   * 
   * @param vendorReleases
   */
  public MeasurementTypeCounterTableModel(final Application application, final RockFactory rockFactory,
      final Vector<TableInformation> tableInfos, final boolean isTreeEditable,
      final MeasurementtypeExt measurementtypeExt, final String[] vendorReleases, final String techpackType) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.measurementtypeExt = measurementtypeExt;
    this.vendorReleases = vendorReleases;
    this.techpackType = techpackType;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
  }

  private Object getColumnValue(final MeasurementcounterExt measurementcounterExt, final int col) {
    switch (col) {
    case datanameColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getDataname());

    case datatypeColumnIdx:
    	//Fix for TR HT13131 starts here
    	 //return Utils.replaceNull(measurementcounterExt.getDatatype().toLowerCase());
    	return Utils.replaceNull(measurementcounterExt.getDatatype());
    	//Fix for TR HT13131 ends here
    case datasizeColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getDatasize());

    case datascaleColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getDatascale());

    case timeaggregationColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getTimeaggregation()).toUpperCase();

    case groupaggregationColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getGroupaggregation()).toUpperCase();

    case countertypeColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getCounterprocess());

    case univobjectColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getUnivobject());

    case univclassColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getUnivclass());

    case includesqlColumnIdx:
      return Utils.integerToBoolean(measurementcounterExt.getIncludesql());

    case descriptionColumnIdx:
      return Utils.replaceNull(measurementcounterExt.getDescription());

    case vectorCounterColumnIdx:
      return measurementcounterExt.getVectorCounterTableModel();

    default:
      break;
    }
    return null;

  }

  private void setColumnValue(final MeasurementcounterExt measurementcounterExt, final int col, final Object value) {
    switch (col) {
    case datanameColumnIdx:
      final String originalName = Utils.replaceNull(measurementcounterExt.getDataname()).trim();
      final String originalUniv = Utils.replaceNull(measurementcounterExt.getUnivobject()).trim();

      measurementcounterExt.setDataname(Utils.replaceNull((String) value).trim());

      // If the univobject value is the same as the old dataname, then also
      // update the univbject value to be the same as the new dataname. Empty
      // univobject is not changed.
      //
      // if
      // (Utils.replaceNull(measurementcounterExt.getUnivobject()).trim().equals("")
      // || originalUniv.equals(originalName)) {
      if (originalUniv.equals(originalName)) {
        String temp = measurementcounterExt.getDataname();
        if (temp.length() > Measurementcounter.getUnivobjectColumnSize()) {
          temp = temp.substring(0, Measurementcounter.getUnivobjectColumnSize());
        }
        measurementcounterExt.setUnivobject(temp);
      }
      
      // In case the dataId is null, set the dataId to match the dataName.
      if (measurementcounterExt.getDataid() == null) {
        measurementcounterExt.setDataid(measurementcounterExt.getDataname());
      }
      break;

    case datatypeColumnIdx:
      measurementcounterExt.setDatatype(Utils.replaceNull((String) value));
      break;

    case datasizeColumnIdx:
      measurementcounterExt.setDatasize(Utils.replaceNull((Integer) value));
      break;

    case datascaleColumnIdx:
      measurementcounterExt.setDatascale(Utils.replaceNull((Integer) value));
      break;

    case timeaggregationColumnIdx:
      measurementcounterExt.setTimeaggregation(Utils.replaceNull((String) value));
      break;

    case groupaggregationColumnIdx:
      measurementcounterExt.setGroupaggregation(Utils.replaceNull((String) value));
      break;

    case countertypeColumnIdx:
      final String counterType = Utils.replaceNull((String) value);
      measurementcounterExt.setCounterprocess(counterType);
      if ((counterType.equals("VECTOR")) || (counterType.equals("UNIQUEVECTOR"))) {
        measurementcounterExt.setCountertype("VECTOR");
        measurementcounterExt.setCountaggregation("GAUGE");
      } else {
        measurementcounterExt.setCountertype(counterType);

        // Generate the counter aggregation value.
        String countaggregation = "";
        if (Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()).intValue() == 1) {
          final Vector<Object> deltacalcsupport = measurementtypeExt.getDeltaCalcSupport();
          countaggregation = Utils.decodeMeasurementDeltaCalcSupport(deltacalcsupport);
        }
        if (countaggregation.length() > Measurementcounter.getCountaggregationColumnSize()) {
          logger.severe("Generated countaggregation length too long.");
        }
        measurementcounterExt.setCountaggregation(countaggregation);
      }
      break;

    case univobjectColumnIdx:
      measurementcounterExt.setUnivobject(Utils.replaceNull((String) value));
      break;

    case univclassColumnIdx:
      measurementcounterExt.setUnivclass(Utils.replaceNull((String) value));
      break;

    case includesqlColumnIdx:
      measurementcounterExt.setIncludesql(Utils.booleanToInteger((Boolean) value));
      break;

    case descriptionColumnIdx:
      measurementcounterExt.setDescription(Utils.replaceNull((String) value));
      break;

    case vectorCounterColumnIdx:
      // model edited directly, no need to set data
      // measurementcounterExt.getVectorCounterTableModel().setData(((Vector<
      // Object>)
      // value));
      break;

    default:
      break;
    }
    // perform pseudochange
    measurementtypeExt.setTypename(measurementtypeExt.getTypename());

  }

  /**
   * Overridden method for getting the value at a certain position in the table.
   * Gets it from the corresponding RockDBObject.
   */
  @Override
  public Object getValueAt(final int row, final int col) {
    Object result = null;
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof MeasurementcounterExt)) {
        final MeasurementcounterExt measurementcounter = (MeasurementcounterExt) displayData.elementAt(row);
        result = getColumnValue(measurementcounter, col);
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
    if ((dataObject != null) && (dataObject instanceof MeasurementcounterExt)) {
      final MeasurementcounterExt measurementcounter = (MeasurementcounterExt) dataObject;
      result = getColumnValue(measurementcounter, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof MeasurementcounterExt)) {
        final MeasurementcounterExt measurementcounter = (MeasurementcounterExt) data.elementAt(row);
        result = getColumnValue(measurementcounter, col);
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

    // Get the counter object in the specified "row" in the display data vector.
    // Get the index for that object in the real data vector. Set the column
    // value for the object in the data vector at the index.
    if (displayData.size() >= row) {
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof MeasurementcounterExt)) {
        final int index = data.indexOf(displayData.elementAt(row));
        final MeasurementcounterExt measurementcounter = (MeasurementcounterExt) data.elementAt(index);
        setColumnValue(measurementcounter, col, value);
        // fireTableCellUpdated(row, col);
        refreshTable();
        fireTableDataChanged();
      }
    }
  }

  /**
   * Overridden method for setting the column editor of the Description column.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {

    // Set editor for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellEditor limitedTextEditor = new LimitedSizeTextTableCellEditor(
        myColumnWidths[datanameColumnIdx], Measurementcounter.getDatanameColumnSize(), true);
    datanameColumn.setCellEditor(limitedTextEditor);

    // Set editor for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellEditor datatypeColumnComboEditor = getComboBoxTableCellEditor();
    datatypeColumn.setCellEditor(datatypeColumnComboEditor);

    // Set editor for timeaggregations
    final TableColumn timeaggregationColumn = theTable.getColumnModel().getColumn(timeaggregationColumnIdx);
    final ComboBoxTableCellEditor timeaggregationColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.AGGREGATION_FORMULAS);
    timeaggregationColumn.setCellEditor(timeaggregationColumnComboEditor);

    // Set editor for groupaggregations
    final TableColumn groupaggregationColumn = theTable.getColumnModel().getColumn(groupaggregationColumnIdx);
    final ComboBoxTableCellEditor groupaggregationColumnComboEditor = new ComboBoxTableCellEditor(
        Constants.AGGREGATION_FORMULAS);
    groupaggregationColumn.setCellEditor(groupaggregationColumnComboEditor);

    // Set editor for countertypes
    final TableColumn countertypeColumn = theTable.getColumnModel().getColumn(countertypeColumnIdx);
    final ComboBoxTableCellEditor countertypeColumnComboEditor = new ComboBoxTableCellEditor(Constants.COUNTER_TYPES);
    countertypeColumn.setCellEditor(countertypeColumnComboEditor);

    // Set the cell editor for the description column.
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));

    final TableColumn vectorCounterColumn = theTable.getColumnModel().getColumn(vectorCounterColumnIdx);
    vectorCounterColumn.setCellEditor(new VectorCounterCellEditor(application, this.isTreeEditable()));
  }

  private ComboBoxTableCellEditor getComboBoxTableCellEditor() {
    if (techpackType.equals(Constants.ENIQ_EVENT)) {
      return new ComboBoxTableCellEditor(Constants.EVENTSDATATYPES);
    }
    return new ComboBoxTableCellEditor(Constants.DATATYPES);
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {

    // Set renderer for dataname
    final TableColumn datanameColumn = theTable.getColumnModel().getColumn(datanameColumnIdx);
    final LimitedSizeTextTableCellRenderer datanameComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[datanameColumnIdx], Measurementcounter.getDatanameColumnSize(), true);
    datanameColumn.setCellRenderer(datanameComboRenderer);

    // Set renderer for datatypes
    final TableColumn datatypeColumn = theTable.getColumnModel().getColumn(datatypeColumnIdx);
    final ComboBoxTableCellRenderer datatypeComboRenderer = getComboBoxTableCellRender();
    datatypeColumn.setCellRenderer(datatypeComboRenderer);

    // Set renderer for timeaggregations
    final TableColumn timeaggregationColumn = theTable.getColumnModel().getColumn(timeaggregationColumnIdx);
    final ComboBoxTableCellRenderer timeaggregationComboRenderer = new ComboBoxTableCellRenderer(
        Constants.AGGREGATION_FORMULAS);
    timeaggregationColumn.setCellRenderer(timeaggregationComboRenderer);

    // Set renderer for groupaggregations
    final TableColumn groupaggregationColumn = theTable.getColumnModel().getColumn(groupaggregationColumnIdx);
    final ComboBoxTableCellRenderer groupaggregationComboRenderer = new ComboBoxTableCellRenderer(
        Constants.AGGREGATION_FORMULAS);
    groupaggregationColumn.setCellRenderer(groupaggregationComboRenderer);

    // Set renderer for countertypes
    final TableColumn countertypeColumn = theTable.getColumnModel().getColumn(countertypeColumnIdx);
    final ComboBoxTableCellRenderer countertypeComboRenderer = new ComboBoxTableCellRenderer(Constants.COUNTER_TYPES);
    countertypeColumn.setCellRenderer(countertypeComboRenderer);

    // Set renderer for univobject
    final TableColumn univobjectColumn = theTable.getColumnModel().getColumn(univobjectColumnIdx);
    final LimitedSizeTextTableCellRenderer univobjectComboRenderer = new LimitedSizeTextTableCellRenderer(
        myColumnWidths[univobjectColumnIdx], 27, false);
    univobjectColumn.setCellRenderer(univobjectComboRenderer);

    // Set the renderer for the description column
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellRenderer(new DescriptionCellRenderer());

    // Set VectorCounter renderer
    final TableColumn vectorCounterColumn = theTable.getColumnModel().getColumn(vectorCounterColumnIdx);
    vectorCounterColumn.setCellRenderer(new VectorCounterCellRenderer(application, true));

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
      final MeasurementcounterExt latestsKey = (MeasurementcounterExt) data.get(data.size() - 1);
      if (Utils.replaceNull(latestsKey.getColnumber()) == null) {
        latestColNumber = 1;
      } else {
        latestColNumber = Utils.replaceNull(latestsKey.getColnumber());
      }
    }
    return latestColNumber;
  }

  /**
   * Overridden method for creating specifically new Measurementcounters.
   */
  @Override
  public RockDBObject createNew() {
    final MeasurementcounterExt measurementcounterExt = new MeasurementcounterExt(rockFactory);
    measurementcounterExt.setTypeid(measurementtypeExt.getTypeid());
    measurementcounterExt.setDataname("");
    measurementcounterExt.setDatasize(0);
    measurementcounterExt.setDatascale(0);
    measurementcounterExt.setDescription("");
    measurementcounterExt.setColnumber(getLatestColNumber() + 1);
    measurementcounterExt.setIncludesql(1);
    measurementcounterExt.setUnivobject("");
    measurementcounterExt.setUnivclass("");
    final MeasurementVectorTableModel vectorcounterTableModel = new MeasurementVectorTableModel(rockFactory,
        tableInformations, this.isTreeEditable(), measurementcounterExt, vendorReleases);
    vectorcounterTableModel.setData(measurementcounterExt.getVectorcounters());
    measurementcounterExt.setVectorCounterTableModel(vectorcounterTableModel);
    // perform pseudochange
    measurementtypeExt.setTypename(measurementtypeExt.getTypename());
    return measurementcounterExt;
  }

  /**
   * Overridden version of this method for saving specifically
   * Measurementcounters.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {
    final MeasurementcounterExt counter = ((MeasurementcounterExt) rockObject);
    try {
      if (counter.gimmeModifiedColumns().size() > 0) {
    	// 20110815 eanguan :: By default to make the DataID name same as TypeID in Counter ( User can change dataid later )
      	if((counter.getDataname() != null) && (!(counter.getDataname().trim().equals("")))){
      		if(counter.gimmeModifiedColumns().contains("DATANAME")){
      			counter.setDataid(counter.getDataname());
      		}
      	}
        counter.saveToDB();
        logger.info("save counter " + counter.getDataname() + " of " + counter.getTypeid());
      }
      counter.getVectorCounterTableModel().saveChanges();
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
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
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    final MeasurementcounterExt counter = ((MeasurementcounterExt) rockObject);
    try {
      counter.getVectorCounterTableModel().removeFromDB();
      counter.deleteDB();
      // perform pseudochange
      measurementtypeExt.setTypename(measurementtypeExt.getTypename());
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.severe(e.getMessage());
    }
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Measurementcounter.
   * 
   * @param currentData
   *          the Measurementcounter to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    if ((currentData != null) && (currentData instanceof MeasurementcounterExt)) {
      ((MeasurementcounterExt) currentData).setColnumber(Long.valueOf(newOrderNumber));
    }
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Measurementcounter.
   * 
   * @param currentData
   *          the Measurementcounter whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    int orderNumber = 101;
    if ((currentData != null) && (currentData instanceof MeasurementcounterExt)) {
      orderNumber = Utils.replaceNull(((MeasurementcounterExt) currentData).getColnumber()).intValue();
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
    final MeasurementcounterExt orig = (MeasurementcounterExt) toBeCopied;
    final MeasurementcounterExt copy = (MeasurementcounterExt) orig.getClone();
    if (!orig.getDataname().equals("")) {
      int count = 0;
      for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
        final Object obj = iter.next();
        if (obj instanceof MeasurementcounterExt) {
          final MeasurementcounterExt mc = (MeasurementcounterExt) obj;
          if (mc.getDataname().equals(copy.getDataname())) {
            count = 1;
          }
          if (mc.getDataname().startsWith(copy.getDataname() + "-")) {
            count++;
          }
        }
      }
      count++;
      copy.setDataname(orig.getDataname() + "-" + count);
      if (orig.getDataname().equals(orig.getUnivobject())) {
        copy.setUnivobject(copy.getDataname());
      }
      // eeoidiv 20100521 HL53524 TechPackIDE: data references copied incorrectly.
      // DataId needs to be same as new name.
      copy.setDataid(copy.getDataname());
    }
    copy.setColnumber(getLatestColNumber() + 1);
    final MeasurementVectorTableModel vectorcounterTableModel = new MeasurementVectorTableModel(rockFactory,
        tableInformations, this.isTreeEditable(), copy, vendorReleases);
    vectorcounterTableModel.setData(copy.getVectorcounters());
    copy.setVectorCounterTableModel(vectorcounterTableModel);
    copy.setNewItem(true);
    
    // perform pseudochange
    measurementtypeExt.setTypename(measurementtypeExt.getTypename());

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
      final Vector<Object> temp = new Vector<Object>();
      for (int i = 0; i < data.size(); i++) {
        if (data.elementAt(i) instanceof MeasurementcounterExt) {
          try {
            final MeasurementcounterExt original = (MeasurementcounterExt) data.elementAt(i);
            final MeasurementcounterExt copy = (MeasurementcounterExt) original.getClone();
            copy.setTypeid(value);
            temp.add(copy);
          } catch (final Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
      for (int i = data.size(); data.size() > 0; i--) {
        if (data.elementAt(i - 1) instanceof MeasurementcounterExt) {
          final MeasurementcounterExt orig = (MeasurementcounterExt) data.elementAt(i - 1);
          markedForDeletion.add(orig);
          data.remove(orig);
        }
      }
      for (int i = 0; i < temp.size(); i++) {
        if (temp.elementAt(i) instanceof MeasurementcounterExt) {
          try {
            final MeasurementcounterExt copy = (MeasurementcounterExt) temp.elementAt(i);
            insertDataLast(copy);
          } catch (final Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
    }
    if ((sourceArgument != null)
        && (((String) sourceArgument).equals(MeasurementTypeParameterModel.DELTA_CALC_SUPPORT))) {
      for (int i = 0; i < data.size(); i++) {
        if (data.elementAt(i) instanceof MeasurementcounterExt) {
          try {
            final MeasurementcounterExt original = (MeasurementcounterExt) data.elementAt(i);
            String countaggregation = original.getCountaggregation();
            if (Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()).intValue() == 1) {
              final Vector<Object> deltecalcsupport = measurementtypeExt.getDeltaCalcSupport();
              countaggregation = Utils.decodeMeasurementDeltaCalcSupport(deltecalcsupport);

              /*
               * String hasVrSupport = ""; String notVrSupport = ""; for
               * (Iterator<Object> iter = deltecalcsupport.iterator();
               * iter.hasNext();) { Object tmp = iter.next(); if (tmp instanceof
               * Measurementdeltacalcsupport) { Measurementdeltacalcsupport
               * measurementdeltacalcsupport = (Measurementdeltacalcsupport)
               * tmp; if (measurementdeltacalcsupport
               * .getDeltacalcsupport().intValue() == 1) { if
               * (hasVrSupport.length() != 0) { hasVrSupport += ","; }
               * hasVrSupport += measurementdeltacalcsupport.getVendorrelease();
               * } else { if (notVrSupport.length() != 0) { notVrSupport += ",";
               * } notVrSupport +=
               * measurementdeltacalcsupport.getVendorrelease(); } } } if
               * (hasVrSupport.length() != 0) { if (notVrSupport.length() != 0)
               * { countaggregation = hasVrSupport + ";PEG" + "/" + notVrSupport
               * + ";GAUGE"; } else { countaggregation = "PEG"; } } else { if
               * (notVrSupport.length() != 0) { countaggregation = "GAUGE"; }
               * else { countaggregation = "PEG"; } }
               */
            }
            if (countaggregation.length() > Measurementcounter.getCountaggregationColumnSize()) {
              logger.severe("Generated countaggregation length too long.");
            }
            original.setCountaggregation(countaggregation);
          } catch (final Exception e) {
            logger.severe(e.getMessage());
          }
        }
      }
    }

  }

  @Override
  public boolean isCellEditable(final int row, final int col) {
    // Always allow the editing for the description and vector counter columns,
    // so that the edit button can be clicked also in the read-only mode. For
    // all the other columns, the editable value depends on the super class
    // implementation.
    if (col == descriptionColumnIdx || col == vectorCounterColumnIdx)
      return true;
    else
      return super.isCellEditable(row, col);
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();

    // Iterate through the data for the table
    for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
      final Object obj = iter.next();

      if (obj instanceof MeasurementcounterExt) {
        final MeasurementcounterExt measurementcounterExt = (MeasurementcounterExt) obj;

        // Check for an empty type name. If it is not empty, then check for type
        // name duplicates
        if (Utils.replaceNull(measurementcounterExt.getDataname()).trim().equals("")) {
          errorStrings.add(measurementtypeExt.getTypename() + myColumnNames[datanameColumnIdx]
              + " for counter is required");
        } else {
          for (final Iterator<Object> iter2 = data.iterator(); iter2.hasNext();) {
            final Object obj2 = iter2.next();
            if (obj2 instanceof MeasurementcounterExt) {
              final MeasurementcounterExt measurementcounterExt2 = (MeasurementcounterExt) obj2;
              if (measurementcounterExt2 != measurementcounterExt) {
                if (measurementcounterExt2.getDataname().equals(measurementcounterExt.getDataname())) {
                  errorStrings.add(measurementtypeExt.getTypename() + ": Counter " + myColumnNames[datanameColumnIdx]
                      + " (" + measurementcounterExt.getDataname() + ") is not unique");
                }
              }
            }
          }
        }

        // Check for an empty data type
        if (Utils.replaceNull(measurementcounterExt.getDatatype()).trim().equals("")) {
          errorStrings.add(measurementtypeExt.getTypename() + " " + measurementcounterExt.getDataname() + " Counter: "
              + myColumnNames[datatypeColumnIdx] + " for counter is required");
        }

		//validation for invalid characters in measurement type tab for adding counters for EQEV-2864...(start)
        if (Utils.replaceNull(measurementcounterExt.getDataname()).contains("%")) {
            errorStrings.add(measurementtypeExt.getTypename() +"  "+measurementcounterExt.getDataname()+ " Counter: '%' Character is not allowed."); 
            
            		
          }
        if (Utils.replaceNull(measurementcounterExt.getDataname()).contains(".")) {
            errorStrings.add(measurementtypeExt.getTypename() +"  "+measurementcounterExt.getDataname()+ " Counter: '.' Character is not allowed."); 
            		
          }
        if (Utils.replaceNull(measurementcounterExt.getDataname()).contains("-")) {
            errorStrings.add(measurementtypeExt.getTypename() +"  "+measurementcounterExt.getDataname()+ " Counter: '-' Character is not allowed."); 
            		
          }
        if (Utils.replaceNull(measurementcounterExt.getDataname()).contains(" ")) {
            errorStrings.add(measurementtypeExt.getTypename() +"  "+measurementcounterExt.getDataname()+ " Counter: Space is not allowed in counter name."); 
            		
          }
		//validation for invalid characters in measurement type tab for adding counters for EQEV-2864...(end)
		
		
        // Check for a valid data type (against data scale and data size)
        final Vector<String> datatypeCheck = Utils.checkDatatype(measurementcounterExt.getDatatype(),
            measurementcounterExt.getDatasize(), measurementcounterExt.getDatascale());
        for (final Iterator<String> iter2 = datatypeCheck.iterator(); iter2.hasNext();) {
          errorStrings.add(measurementtypeExt.getTypename() + " " + measurementcounterExt.getDataname() + " Counter: "
              + iter2.next());
        }

        // Check that if counterType is PEG or GAUGE, then vectorCounters cannot
        // exist
        final Vector<Object> vectorCounters = measurementcounterExt.getVectorcounters();
        if (vectorCounters != null && vectorCounters.size() > 0) {
          // One or more vectorcounters exist
          final String counterType = measurementcounterExt.getCountertype();
          if ("PEG".equals(counterType) || "GAUGE".equals(counterType)) {
            // Vectorcounters are not allowed for PEG or GAUGE, so an error
            // message is added.
            errorStrings.add(measurementtypeExt.getTypename() + " " + measurementcounterExt.getDataname()
                + " Counter : PEG or GAUGE countertypes can NOT have vectorcounters(VC)! "
                + "Remove vectorcounters or change countertype to something else.");
          }
        }
      }
    }

    return errorStrings;
  }

}