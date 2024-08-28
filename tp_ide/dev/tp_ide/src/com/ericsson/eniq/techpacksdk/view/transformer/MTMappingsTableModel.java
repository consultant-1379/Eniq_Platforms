package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssc.rockfactory.RockDBObject;
import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.ericsson.eniq.component.SubTableModel;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.common.gui.MappingTableUtils;

/**
 * Table model for the mappings for common transformations.
 * @author eciacah
 */
@SuppressWarnings("serial")
public class MTMappingsTableModel extends AbstractTableModel implements SubTableModel {
  
  /** Logger */
  private static final Logger LOGGER = Logger.getLogger(MTMappingsTableModel.class.getName());

  /** Type column index */
  private static final int TYPE_COLUMN_IDX = 0;

  /** Enabled/disabled column index */
  private static final int ENABLE_COLUMN_IDX = 1;

  /** Column names, used as headings for the columns. */
  private static final String[] COLUMN_NAMES = { "Transformer", "Enabled" };

  /** Column widths, used to graphically layout the columns. */
  private static final int[] columnWidths = { 100, 80 };

  /** The data that will be shown. This is the list of transformers. */
  private Vector<Transformer> data;

  private final boolean editable = true;

  /** The transformation that is being shown. 
   * The table only shows the transformers for one transformation.
   **/
  private final Transformation transformation;

  /** The mappings for the transformation. */
  private final HashMap<Transformation, ArrayList<String>> mappings;

  private final TransformerDataModel transformerDataModel;

  /** StringBuffer used to calculate the transformer ID */
  private StringBuffer transformerIDsb = new StringBuffer();
  
  /** Transformation utilities  */
  private TransformationUtils tUtils = new TransformationUtils();

  /** Observable class to notify tree model of changes in this table model
   * (when user enables/disables). */
  private ObservableHelper obsHelper;

  /**
   * Constructor
   * @param data
   * @param transformation
   * @param mappings
   * @param transformerDataModel
   * @param dataformatType 
   */
  public MTMappingsTableModel(final List<Transformer> data, final Transformation transformation,
      HashMap<Transformation, ArrayList<String>> mappings, TransformerDataModel transformerDataModel, String dataformatType) {
    super();
    setData(data);
    this.transformation = transformation;
    this.mappings = mappings;
    this.transformerDataModel = transformerDataModel;
    
    // Get the TableTreeComponent and set it as an observer of this model.
    // This notifies the ttc of changes in this model.
    TableTreeComponent ttcComp = getTableTreeComp(dataformatType);
    obsHelper = new ObservableHelper(ttcComp);
    
    addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        if (obsHelper != null)
          obsHelper.notifyTableTreeComponent();
      }
    });
  }

  /**
   * Gets the table tree component for the data format type (e.g. mdc, 3gpp etc).
   * There is a separate ttc for each data format type. It corresponds to one node 
   * in the tree.  
   * @param dataformatType  The type as a string.
   * @return ttcComp        The table tree component.
   */
  protected TableTreeComponent getTableTreeComp(String dataformatType) {
    TableTreeComponent ttcComp = (TableTreeComponent) TransformerTreeModel.tableTreeComponentMap.get(dataformatType);
    return ttcComp;
  }
  
  /**
   * Set the table tree component as the observer. 
   * 
   * @param ttcComp The table tree component.
   */
  public void setTableTreeComponentAsObserver(TableTreeComponent ttcComp) {
    if (obsHelper == null)
      obsHelper = new ObservableHelper(ttcComp);
  }

  protected Object getColumnValue(final Transformer transformer, final int col) {
    if (transformer != null) {
      switch (col) {
      case TYPE_COLUMN_IDX:
        final String[] tokens = transformer.getTransformerid().split(":");
        // By default the value is an empty string:
        String transformerValue = "";
        if (tokens.length >= 2) {
          transformerValue = tokens[2];          
        }
        return Utils.replaceNull(transformerValue);
      case ENABLE_COLUMN_IDX:
        // Get the mappings for the transformation:
        ArrayList<String> tformers = tUtils.lookUpTransformationInMap(transformation, mappings);     
                
        if (tformers == null) {
          LOGGER.warning("No mappings found for transformation");
          return Boolean.FALSE;
        }
        if (tformers.contains(transformer.getTransformerid())) {
          return Boolean.TRUE;
        } else {
          return Boolean.FALSE;
        }        
      default:
        break;
      }
    }
    return null;
  }

  /**
   * Set the column value for enabling/disabling transformers.
   * @param transformer
   * @param col
   * @param value
   */
  protected void setColumnValue(final Transformer transformer, final int col, final Object value) {
    switch (col) {
    case TYPE_COLUMN_IDX:
      break;
    case ENABLE_COLUMN_IDX:

      ArrayList<String> tformers = tUtils.lookUpTransformationInMap(transformation, mappings);
      
      if (tformers == null) {
        tformers = new ArrayList<String>();
        mappings.put(transformation, tformers);
      }
           
      // If checked box is enabled:
      if ((Boolean) value == true) {
        if (!tformers.contains(transformer.getTransformerid())) {
          // add the transformer id string:
          tformers.add(transformer.getTransformerid());          
        }                      
      } else {
        // If checked box is disabled:
        final String transformeridToRemove = transformer.getTransformerid();
        // remove the current transformer (for this table)
        tformers.remove(transformeridToRemove);
      }
      
      transformerIDsb.setLength(0);
      for (String tformer : tformers) {
        transformerIDsb.append(tformer);
        transformerIDsb.append(",");
      }
      transformation.setTransformerid(transformerIDsb.toString());
      
      transformerDataModel.newDataCreated = true;
      break;
    default:
      break;
    }
  }

  public Class<? extends Object> getColumnClass(final int col) {
    return String.class;
  }

  public String getColumnName(final int col) {
    return COLUMN_NAMES[col].toString();
  }

  public int getColumnCount() {
    return COLUMN_NAMES.length;
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
        if ((data.elementAt(rowIndex) != null) && (data.elementAt(rowIndex) instanceof Transformer)) {
          final Transformer transformer = (Transformer) data.elementAt(rowIndex);
          result = getColumnValue(transformer, colIndex);
        }
      }
    }
    return result;
  }

  @Override
  public void setValueAt(final Object value, final int row, final int col) {
    final Transformer transformer = (Transformer) data.elementAt(row);
    setColumnValue(transformer, col, value);
    fireTableDataChanged();
  }

  private void addRow(final Transformer transformer) {
    data.add(transformer);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public boolean isCellEditable(final int row, final int col) {
    return editable;
  }

  protected final void clear() {
    data.clear();
  }

  public Vector<Transformer> getData() {
    return data;
  }

  /**
   * Adds rows to the table model.
   * 
   * @param mappings
   */
  public final void setData(final List<Transformer> transformers) {

    if (this.data == null) {
      this.data = new Vector<Transformer>();
    } else {
      clear();
    }
    for (Transformer transformer : transformers) {
      addRow(transformer);
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

  /* (non-Javadoc)
   * @see com.ericsson.eniq.component.SubTableModel#startEditing()
   */
  public void startEditing() {
    // not implemented
  }

  public void stopEditing() {
    // not implemented
  }

  public void cancelEditing() {
    // not implemented
  }

  public RockDBObject createNew() {
    return null;
  }

  public void duplicateRow(final int[] selectedRows, final int times) {
    // not implemented
  }

  @Override
  public JPopupMenu getPopUpMenu(final TableComponentListener listener, final Component component) {
    return new MappingTableUtils().createPopupMenu(listener, component);
  }

  public TableModel getTableModel() {
    return this;
  }

  public void insertDataAtRow(final Object datum, final int index) {
    // not implemented
  }

  public void insertDataLast(final Object datum) {
    // not implemented
  }

  public void removeSelectedData(final int[] selectedRows) {
    // not implemented
  }

  @Override
  public void setColumnEditors(final JTable editTable) {

    final TableColumn colType = editTable.getColumnModel().getColumn(TYPE_COLUMN_IDX);
    colType.setCellEditor(new DefaultCellEditor(new JTextField()));

    final TableColumn colEnable = editTable.getColumnModel().getColumn(ENABLE_COLUMN_IDX);
    colEnable.setCellEditor(new DefaultCellEditor(new JCheckBox()));
  }

  @Override
  public void setColumnRenderers(final JTable editTable) {
    // Use a custom renderer for the editable column.
    final TableColumn colEnable = editTable.getColumnModel().getColumn(ENABLE_COLUMN_IDX);
    colEnable.setCellRenderer(new MappingTableUtils().createCustomCheckBoxRenderer());
  }

  public Vector<String> validateData() {
    return new Vector<String>();
  }

  public static Logger getLogger() {
    return LOGGER;
  }
  
  public HashMap<Transformation, ArrayList<String>> getMappings() {
    return mappings;
  }
  
  /**
   * A helper class used for the observable implementation
   * 
   * @author enaland
   * 
   */
  public class ObservableHelper extends Observable {

    /**
     * Constructor, sets the in-parameter as the observer of this instance
     * 
     * @param theObserver
     */
    public ObservableHelper(Observer theObserver) {
      addObserver(theObserver);
    }

    /**
     * This method should be called by the table model when the tree should be
     * informed that the table data has changed
     */
    public void notifyTableTreeComponent() {
      setChanged();
      notifyObservers();
    }

  }
  
}
