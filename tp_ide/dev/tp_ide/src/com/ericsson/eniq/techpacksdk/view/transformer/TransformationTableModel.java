package com.ericsson.eniq.techpacksdk.view.transformer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the column table.
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class TransformationTableModel extends TTTableModel {

  private static final Logger logger = Logger.getLogger(TransformationTableModel.class.getName());

  /**
   * The table type/name
   */
  private static final String myTableName = "Transformations";

  private final Transformer transformer;

  private static final int sourceColumnIdx = 0;

  private static final int targetColumnIdx = 1;

  private static final int typeColumnIdx = 2;

  private static final int configColumnIdx = 3;

  private static final int descriptionColumnIdx = 4;

  private TransformerDataModel transformerDataModel;
  
  /** Transformation utilities  */
  private TransformationUtils tUtils = new TransformationUtils();

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] myColumnNames = { "Source", "Target", "Type", "Config", "Description"};

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 140, 140, 140, 140, 200};

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
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  private String dataformatType;

  /**
   * Constructor. Initialises the column names, widths and table name.
   * @param dataformatType 
   * @param transformerTreeModel 
   */
  public TransformationTableModel(TransformerDataModel transformerDataModel, RockFactory rockFactory,
      Vector<TableInformation> tableInfos, boolean isTreeEditable, Transformer transformer, final String dataformatType) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.rockFactory = rockFactory;
    this.transformer = transformer;
    this.setTableName(myTableName);
    this.setColumnNames(myColumnNames);
    this.setColumnWidths(myColumnWidths);
    this.transformerDataModel = transformerDataModel;
    this.dataformatType = dataformatType;
  }

  private Object getColumnValue(final Transformation transformation, final int col) {
    if (transformation != null) {
      switch (col) {
      case typeColumnIdx:
        return Utils.replaceNull(transformation.getType());

      case sourceColumnIdx:
        return Utils.replaceNull(transformation.getSource());

      case targetColumnIdx:
        return Utils.replaceNull(transformation.getTarget());

      case configColumnIdx:
        return transformation;

      case descriptionColumnIdx:
        return Utils.replaceNull(transformation.getDescription());
          
      default:
        break;
      }
    }
    return null;

  }

  protected void setColumnValue(final Transformation transformation, final int col, Object value) {    
    switch (col) {    
    case typeColumnIdx:
      // transformation.setType((String) value);
      break;

    case sourceColumnIdx:
      transformation.setSource((String) value);
      break;

    case targetColumnIdx:
      transformation.setTarget((String) value);
      break;

    case configColumnIdx:
      String configValue = ((Transformation) value).getConfig();
      if (configValue == null) {
        configValue = "";
      }
            
      String typeValue = ((Transformation) value).getType();
      if (typeValue == null) {
        typeValue = "";
      }
      
      transformation.setConfig(configValue);
      transformation.setType(typeValue);
      break;

    case descriptionColumnIdx:
      transformation.setDescription((String) value);
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
      if ((displayData.elementAt(row) != null) && (displayData.elementAt(row) instanceof Transformation)) {
        final Transformation transformation = (Transformation) displayData.elementAt(row);
        result = getColumnValue(transformation, col);
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
    if ((dataObject != null) && (dataObject instanceof Transformation)) {
      final Transformation transformation = (Transformation) dataObject;
      result = getColumnValue(transformation, col);
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
      if ((data.elementAt(row) != null) && (data.elementAt(row) instanceof Transformation)) {
        final Transformation transformation = (Transformation) data.elementAt(row);
        result = getColumnValue(transformation, col);
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
    // Get the transformation:
    final Transformation transformation = getTransformationFromData(row);
    
    final String transformationTransformerID = transformation.getTransformerid();
    
    // Get the mappings:
    HashMap<Transformation, ArrayList<String>> mappings = transformerDataModel.getMappings(dataformatType);    

    // Save old mappings:
    ArrayList<String> oldTransformers = tUtils.lookUpTransformationInMap(transformation, mappings);
    
    // Get the transformations for this transformer:    
    if (tUtils.findNumOccurrencesWithObjects(displayData, transformation) < 2) {
      // remove mappings for this individual transformation (remove old mapping):
      if (oldTransformers != null) {
        oldTransformers.remove(transformationTransformerID);     // this is actually correct if we are deleting the transformation here.
        // If the list is empty, then remove this transformation:
        if (oldTransformers.isEmpty()) {
          mappings.remove(transformation);
        }
      }
    }
    
    // Update the transformation:
    setColumnValue(transformation, col, value);
    refreshTable();
    fireTableDataChanged();
    
    // Get mapping for "updated" transformation:
    ArrayList<String> mappedTs = tUtils.lookUpTransformationInMap(transformation, mappings);

    if (mappedTs == null) {
      // Transformation has changed and is different now:
      mappedTs = new ArrayList<String>();
      mappedTs.add(transformationTransformerID);
      mappings.put(transformation, mappedTs);      
    } else {
      // Otherwise add to what's there already:
      if (!mappedTs.contains(transformationTransformerID)) {
        mappedTs.add(transformationTransformerID); // this will put in only the enabled transformers
      }
    }
  }
  
  /**
   * Gets the transformation from the underlying data (not the display data).
   * @param row
   * @return
   */
  protected Transformation getTransformationFromData(final int row) {
    int index = data.indexOf((Transformation) displayData.elementAt(row));
    final Transformation transformation = (Transformation) data.elementAt(index);
    return transformation;
  }

  /**
   * Overridden method for setting the column editor of the Description column.
   */
  @Override
  public void setColumnEditors(final JTable theTable) {
    // Set the cell editor for type
    /*
     * final TableColumn typeColumn =
     * theTable.getColumnModel().getColumn(typeColumnIdx); final
     * ComboBoxTableCellEditor typeColumnComboEditor = new
     * ComboBoxTableCellEditor(Constants.TRANFORMER_TYPES);
     * typeColumn.setCellEditor(typeColumnComboEditor);
     */

    // Config editor
    TableColumn configColumn = theTable.getColumnModel().getColumn(configColumnIdx);
    configColumn.setCellEditor(new ConfigTableCellEditor(isTreeEditable()));

    // Set the cell editor for the description column.
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set the renderer for the type
    /*
     * final TableColumn typeColumn =
     * theTable.getColumnModel().getColumn(typeColumnIdx); final
     * ComboBoxTableCellRenderer typeColumnComboRenderer = new
     * ComboBoxTableCellRenderer(Constants.TRANFORMER_TYPES);
     * typeColumn.setCellRenderer(typeColumnComboRenderer);
     */

    // Config
    TableColumn configColumn = theTable.getColumnModel().getColumn(configColumnIdx);
    ConfigTableCellRenderer configColumnRenderer = new ConfigTableCellRenderer();    
    configColumn.setCellRenderer(configColumnRenderer);

    // Set the renderer for the description column
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
    descriptionColumn.setCellRenderer(new DescriptionCellRenderer());
  }

  /**
   * Overridden method for creating specifically new Transformations.
   */
  @Override
  public RockDBObject createNew() {
    final Transformation transformation = createNewTransformation();
    transformation.setTransformerid(transformer.getTransformerid());
    transformation.setType("fixed");
    transformation.setOrderno(0l);
    transformation.setSource("");
    transformation.setTarget("");
    
    // Create a new mapping also for this transformation:
    HashMap<Transformation, ArrayList<String>> mappings = transformerDataModel.getMappings(dataformatType);
    ArrayList<String> transformers = tUtils.lookUpTransformationInMap(transformation, mappings);
    final String transformerID = transformation.getTransformerid();
    
    if (transformers == null) {
      ArrayList<String> newValues = new ArrayList<String>();
      newValues.add(transformerID);
      mappings.put(transformation, newValues);
    } else {      
      transformers.add(transformerID);
    }
    
    return transformation;
  }
  
  /**
   * This is a helper method to create a new transformation.
   * @return Transformation.
   */
  protected Transformation createNewTransformation() {
    final Transformation transformation = new Transformation(rockFactory);
    return transformation;
  }

  /**
   * Overridden version of this method for saving specifically Transformations.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void saveData(final Object rockObject) throws SQLException, RockException {

    final Transformation column = ((Transformation) rockObject);
    try {

      transformerDataModel.addUpdatedTransformations(column);
      transformerDataModel.newDataCreated = true;
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }

  }

  /**
   * Overridden version of this method for deleting specifically
   * Transformations.
   * 
   * @throws RockException
   * @throws SQLException
   */
  @Override
  protected void deleteData(final Object rockObject) throws SQLException, RockException {
    transformerDataModel.newDataCreated = true;
    Transformation t1 = ((Transformation) rockObject);
    
    // Remove from mappings also:
    HashMap<Transformation, ArrayList<String>> mappings = transformerDataModel.getMappings(dataformatType);
    ArrayList<String> transformers = tUtils.lookUpTransformationInMap(t1, mappings);
    if (transformers != null) {
      transformers.remove(t1.getTransformerid());      
    }    
    ((Transformation) rockObject).deleteDB();
  }

  /**
   * Overridden version of this method for setting the order number of a
   * Transformation.
   * 
   * @param currentData
   *          the Transformation to be updated
   * @param newOrderNumber
   *          the new value to set the order number of
   */
  @Override
  protected void setOrderOf(final Object currentData, final int newOrderNumber) {
    if ((currentData != null) && (currentData instanceof Transformation)) {

      if (transformerDataModel.getOldTransformations() == null) {
        transformerDataModel.setOldTransformations(new Vector<Transformation>());
      }

      transformerDataModel.addOldTransformations((Transformation) ((Transformation) currentData).clone());

      ((Transformation) currentData).setOrderno(new Long(newOrderNumber));
    }
  }

  /**
   * Overridden version of this method for retrieving the order number of a
   * Transformation.
   * 
   * @param currentData
   *          the Transformation whose order number we're querying.
   */
  @Override
  protected int getOrderOf(final Object currentData) {
    int orderNumber = 101;
    if ((currentData != null) && (currentData instanceof Transformation)) {
      orderNumber = Utils.replaceNull(((Transformation) currentData).getOrderno()).intValue();
    }
    return orderNumber;
  }

  /**
   * Overridden version of this method. Indicates that this table has an order
   * column.
   * 
   * @return true
   */
  @Override
  protected boolean dataHasOrder() {
    return true;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }

  @Override
  public Object copyOf(final Object toBeCopied) {
    return ((Transformation) toBeCopied).clone();
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
    if ((sourceArgument != null) && (((String) sourceArgument).equals(TransformerParameterModel.TRANSFORMER_ID))) {
      final String value = ((TransformerParameterModel) sourceObject).getTransformer().getTransformerid();

      for (int i = 0; i < data.size(); i++) {
        try {
          final Transformation oldtransformation = (Transformation) data.elementAt(i);
          final Transformation newtransformation = (Transformation) oldtransformation.clone();
          newtransformation.setTransformerid(value);
          data.set(i, newtransformation);
          oldtransformation.deleteDB();
        } catch (SQLException e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        } catch (RockException e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }
  }

  public boolean isCellEditable(int row, int col) {
    // Always allow the editing for the description column, so that the edit
    // button can be clicked also in the read-only mode. For all the other
    // columns, the editable value depends on the super class
    // implementation.
    if (col == descriptionColumnIdx)
      return true;
    else if (col == configColumnIdx)
      return true;
    else
      return super.isCellEditable(row, col);
}
  /**
   * Overridden version of the method to implement validations for this table
   * model.
   * 
   * @see tableTree.TTTableModel#validateData()
   */
 @Override
  public Vector<String> validateData() {
  Vector<String> result = new Vector<String>();
    return result;
  }

  public static Logger getLogger() {
    return logger;
  }
  
  /**
   * Sets display data. To be used only for testing.
   */
  protected void setDisplayData(final Vector<Object> displayData) {
    this.displayData = displayData;
  }
 
}
