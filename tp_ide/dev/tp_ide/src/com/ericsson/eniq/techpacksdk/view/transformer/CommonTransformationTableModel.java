package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.SingleFrameApplication;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.DescriptionCellEditor;
import tableTree.DescriptionCellRenderer;
import tableTree.GlobalTableTreeConstants;
import tableTree.TTTableModel;
import tableTreeUtils.DescriptionComponent;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Table model for the common transformations.
 * This table gives a "view" of the transformations and shows which transformers
 * they are included in.
 * 
 * @author eciacah
 * 
 */
@SuppressWarnings("serial")
public class CommonTransformationTableModel extends TTTableModel {

  /**
   * The table type/name
   */
  private static final String TABLE_NAME = "Common Transformations";

  /** Column indexes **/
  private static final int SOURCE_IDX = 0;

  private static final int TARGET_IDX = 1;

  private static final int TYPE_IDX = 2;

  private static final int CONFIG_IDX = 3;

  private static final int DESCRIPTION_IDX = 4;

  private static final int MEAS_TYPES_IDX = 5;

  private final TransformerDataModel tformerDataModel;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] COLUMN_NAME = { "Source", "Target", "Type", "Config", "Description",
      "Measurement Types"};

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] myColumnWidths = { 140, 140, 140, 140, 200, 180};

  /**
   * Max number of rows shown before adding scrollbars
   */
  private static final int MAX_ROWS_SHOWN = 25;
  
  /**
   * The transformer ID used by transformations in the ALL section.
   */
  private static final String ALL_ID = "ALL";
  
  /**
   * Transformation utilities.
   */
  private final TransformationUtils tUtils = new TransformationUtils();

  /**
   * Static method that returns the table type and its corresponding column
   * names
   * 
   * @return
   */
  public static TableInformation createTableTypeInfo() {
    return new TableInformation(TABLE_NAME, COLUMN_NAME, myColumnWidths, MAX_ROWS_SHOWN);
  }

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  private final List<TransformerData> tformerDataList;

  private final SingleFrameApplication application;

  private final Vector<Object> allTransformations;

  private MTMappingsTableCellEditor mtMappingCellEditor;

  private final String allTransformerID;

  private final String dataformatType;
  
  /**
   * Constructor.
   * 
   * @param allTransformations
   * @param transformer
   * @param allTransformerID
   * @param dataformatType 
   */
  public CommonTransformationTableModel(SingleFrameApplication application, TransformerDataModel transformerDataModel,
      RockFactory rockFactory, Vector<TableInformation> tableInfos, boolean isTreeEditable,
      List<TransformerData> transformerData, Vector<Object> allTransformations, final String allTransformerID, String dataformatType) {
    super(rockFactory, tableInfos, isTreeEditable);
    this.application = application;
    this.rockFactory = rockFactory;
    this.allTransformations = allTransformations;
    this.tformerDataList = transformerData;
    this.setTableName(TABLE_NAME);
    this.setColumnNames(COLUMN_NAME);
    this.setColumnWidths(myColumnWidths);
    this.tformerDataModel = transformerDataModel;
    this.allTransformerID = allTransformerID;
    this.dataformatType = dataformatType;
  }

  private Object getColumnValue(final Transformation transformation, final int col) {
    if (transformation != null) {
      switch (col) {
      case TYPE_IDX:
        return Utils.replaceNull(transformation.getType());

      case SOURCE_IDX:
        return Utils.replaceNull(transformation.getSource());

      case TARGET_IDX:
        return Utils.replaceNull(transformation.getTarget());

      case CONFIG_IDX:
        return transformation;

      case DESCRIPTION_IDX:
        return Utils.replaceNull(transformation.getDescription());

      case MEAS_TYPES_IDX:
        return transformation;

      default:
        break;
      }
    }
    return null;

  }

  protected void setColumnValue(final Transformation transformation, final int col, Object value) {
    
    Object newValue = value;
    
    switch (col) {
    case TYPE_IDX:
      // transformation.setType((String) value);
      break;

    case SOURCE_IDX:
      transformation.setSource((String) newValue);
      break;

    case TARGET_IDX:
      transformation.setTarget((String) newValue);
      break;

    case CONFIG_IDX:
      String configValue = ((Transformation) newValue).getConfig();                  
      String typeValue = ((Transformation) newValue).getType();      
      transformation.setConfig(configValue);
      transformation.setType(typeValue);
      break;

    case DESCRIPTION_IDX:
      transformation.setDescription((String) newValue);
      break;

    case MEAS_TYPES_IDX:
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
   * Overridden method for setting the value at a certain position in the table.
   * Sets it in the corresponding RockDBObject.
   */
  @Override
  public void setValueAt(final Object value, final int row, final int col) {
    // Get the transformation:
    final Transformation transformation = getTransformationFromData(row);
    // Get the mappings:
    HashMap<Transformation, ArrayList<String>> mappings = tformerDataModel.getMappings(dataformatType);

    // Save old mappings:     
    ArrayList<String> oldTransformers = tUtils.lookUpTransformationInMap(transformation, mappings);    
    
    // If there is a mapping there already, remove it:
    if (oldTransformers != null) {
      mappings.remove((Object)transformation);
    }
    
    setColumnValue(transformation, col, value);
    refreshTable();
    fireTableDataChanged();

    // If old mappings are empty (none were defined), add them:
    if (oldTransformers == null || oldTransformers.size() == 0) {
      if (transformation.getTransformerid().equalsIgnoreCase(ALL_ID)) {
        // Add all transformers (apart from the all transformer):
        for (final Iterator<TransformerData> iterator = tformerDataList.iterator(); iterator.hasNext();) {
          Transformer nextTransformer = iterator.next().getTransformer();
          final String transformerID = nextTransformer.getTransformerid();
          
          if (!transformerID.equalsIgnoreCase(allTransformerID)) {
            ArrayList<String> mappedTs = tUtils.lookUpTransformationInMap(transformation, mappings);   

            if (mappedTs == null) {
              mappedTs = new ArrayList<String>();
              mappedTs.add(transformerID);
              mappings.put(transformation, mappedTs);              
            } else {
              if (!mappedTs.contains(transformerID)) {
                mappedTs.add(transformerID); 
              }
            }
          }
        }
      } else {
        // Add only the transformers to the mapping list, that are in the transformation's transformer ID:
        for (final Iterator<TransformerData> iter2 = tformerDataList.iterator(); iter2.hasNext();) {
          Transformer nextTransformer = iter2.next().getTransformer();
          final String transformerID = nextTransformer.getTransformerid();
          
          // Check if the transformation (shown in the common view) transformer ID contains the transformer:
          if (transformation.getTransformerid().contains(transformerID)) {
            ArrayList<String> mappedTs = tUtils.lookUpTransformationInMap(transformation, mappings);

            if (mappedTs == null) {
              mappedTs = new ArrayList<String>();
              mappedTs.add(transformerID);
              mappings.put(transformation, mappedTs);              
            } else {
              if (!mappedTs.contains(transformerID)) {
                mappedTs.add(transformerID); // this will put in only the enabled transformers
              }
            }
          }
        }
      }
    } else {    
    // The transformation had mappings.
    // Put the transformation (key) back in the mappings HashMap with the old list of transformers (values):
    mappings.put(transformation, oldTransformers);
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
    // Config editor
    TableColumn configColumn = theTable.getColumnModel().getColumn(CONFIG_IDX);
    configColumn.setCellEditor(new ConfigTableCellEditor(isTreeEditable()));

    // Set the cell editor for the description column.
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(DESCRIPTION_IDX);
    descriptionColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));

    final TableColumn mappingsColumn = theTable.getColumnModel().getColumn(MEAS_TYPES_IDX);
    mtMappingCellEditor = new MTMappingsTableCellEditor(allTransformations, tformerDataList, tformerDataModel,
        application, this.isTreeEditable(), allTransformerID, dataformatType);
    mappingsColumn.setCellEditor(mtMappingCellEditor);
  }

  /**
   * Overridden method for setting the column renderer. Not used.
   */
  @Override
  public void setColumnRenderers(final JTable theTable) {
    // Set the renderer for the type
    // Config
    TableColumn configColumn = theTable.getColumnModel().getColumn(CONFIG_IDX);
    ConfigTableCellRenderer configColumnRenderer = new ConfigTableCellRenderer();
    configColumn.setCellRenderer(configColumnRenderer);

    // Set the renderer for the description column
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(DESCRIPTION_IDX);
    descriptionColumn.setCellRenderer(new DescriptionCellRenderer());

    final TableColumn mappingsColumn = theTable.getColumnModel().getColumn(MEAS_TYPES_IDX);
    mappingsColumn.setCellRenderer(new MTMappingsCellRenderer());
  }

  /**
   * Overridden method for creating specifically new Transformations.
   */
  @Override
  public RockDBObject createNew() {
    // Set up a new transformation:
    final Transformation transformation = createNewTransformation();
    transformation.setTransformerid(ALL_ID);
    transformation.setType("fixed");
    transformation.setOrderno(0l);
    transformation.setSource("");
    transformation.setTarget("");
    transformation.setConfig("");
    transformation.setValidateData(false);

    // Get mappings:
    HashMap<Transformation, ArrayList<String>> mappings = tformerDataModel.getMappings(dataformatType);
    
    // Create mappings for the new transformation:
    for (final Iterator<TransformerData> iter2 = tformerDataList.iterator(); iter2.hasNext();) {
      Transformer nextTransformer = iter2.next().getTransformer();
      final String transformerID = nextTransformer.getTransformerid();

      if (!transformerID.equalsIgnoreCase(allTransformerID)) {
        ArrayList<String> mappedTs = tUtils.lookUpTransformationInMap(transformation, mappings);

        if (mappedTs == null) {
          mappedTs = new ArrayList<String>();
          mappedTs.add(transformerID);
          mappings.put(transformation, mappedTs);          
        } else {
          if (!mappedTs.contains(transformerID)) {
            mappedTs.add(transformerID); // put in all transformers for this transformation
          }
        }
      }
    }
    return transformation;
  }

  /**
   * This is a helper method to create a new transformation.
   * @return Transformation.
   */
  protected Transformation createNewTransformation() {
    return new Transformation(rockFactory);
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

    final Transformation transformation = ((Transformation) rockObject);

    // remove from mappings:
    HashMap<Transformation, ArrayList<String>> mappings = tformerDataModel.getMappings(dataformatType);
    mappings.remove((Object)transformation);
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

      if (tformerDataModel.getOldTransformations() == null) {
        tformerDataModel.setOldTransformations(new Vector<Transformation>());
      }

      tformerDataModel.addOldTransformations((Transformation) ((Transformation) currentData).clone());

      ((Transformation) currentData).setOrderno(Long.valueOf(newOrderNumber));
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
    // This only needs to be done for the ordinary transformations (not common ones).
  }

  public boolean isCellEditable(int row, int col) {
    // Always allow the editing for the description column, so that the edit
    // button can be clicked also in the read-only mode. For all the other
    // columns, the editable value depends on the super class
    // implementation.
    if (col == DESCRIPTION_IDX)
      return true;
    else if (col == CONFIG_IDX)
      return true;
    else if (col == MEAS_TYPES_IDX)
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

  /**
   * Renderer class for measurement type mappings column. Sets up a
   * DescriptionComponent to show text and edit button. Edit button colour is
   * overridden if the transformation does not apply for all transformers.
   * 
   * @author ECIACAH
   */
  private class MTMappingsCellRenderer implements TableCellRenderer {

    /**
     * Constructor
     */
    public MTMappingsCellRenderer() {
    }

    /**
     * Method for getting the renderer component. Sets the cell coloring and the
     * text value shown in the description components text box.
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return the renderer component
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

      // The actualColumn represents the column index in the table model, not
      // the view
      int actualColumn = ((TTTableModel) table.getModel()).getTableInfo().getOriginalColumnIndexOfColumnName(
          table.getColumnModel().getColumn(column).getHeaderValue().toString());

      // Create a new description component with the string value and the
      // number of characters in the text field. NOTE: The number of
      // characters does not affect anything when used in the table cell. That
      // is why a zero is used.
      DescriptionComponent retComp = new DescriptionComponent(((Transformation) value).getTransformerid(), 0);

      // The colouring of the cell depends on the row selection, table type
      // filtering and table filtering.

      if (isSelected) {
        retComp.setColors(table.getSelectionBackground(), table.getSelectionForeground(),
            table.getSelectionBackground(), table.getSelectionForeground());
      } else if (((TTTableModel) table.getModel()).isColumnFilteredForTableType(actualColumn)) {
        retComp.setColors(table.getBackground(), table.getForeground(),
            GlobalTableTreeConstants.TABLE_TYPE_FILTERED_CELL_COLOR, table.getForeground());
      } else if (((TTTableModel) table.getModel()).isColumnFiltered(actualColumn)) {
        retComp.setColors(table.getBackground(), table.getForeground(), GlobalTableTreeConstants.FILTERED_CELL_COLOR,
            table.getForeground());
      } else {
        if (!((Transformation) value).getTransformerid().equalsIgnoreCase(ALL_ID)) {
          Color yellow = new Color(255, 253, 112);
          retComp.getEditButton().setBackground(yellow);
        }
      }

      return retComp;
    }
  }

  protected void saveData(Object rockObject) throws SQLException, RockException {
    // No save is done for the common transformation table.
  }

}
