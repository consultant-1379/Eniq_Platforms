package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.application.Application;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.ericsson.eniq.component.TableComponent;

/**
 * Table model for the mappings for common transformations.
 * Used in the common transformations table.
 * @author ECIACAH
 */
@SuppressWarnings("serial")
public class MTMappingsTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
  
  /** The table component **/
  private TableComponent theComp = null;

  /** Logger */
  private static final Logger LOGGER = Logger.getLogger(MTMappingsTableCellEditor.class.getName());

  /**
   * The value of the edited data
   */
  private Transformation origTransformation;
  
  private boolean editable;

  /** Application reference **/
  private Application application;
  
  /** The table model for the dialog where you can enable/disable transformers **/
  private MTMappingsTableModel tableModel;
  
  /** List of all transformers **/
  private List<TransformerData> transformerDataList;

  /** List of all transformations **/
  private Vector<Object> allTransformations;
  
  /** TransformerDataModel. This holds the mappings for all of the common transformations **/
  private TransformerDataModel transformerDataModel;

  /** The All transformer ID e.g. DC_E_STN:ALL:mdc **/ 
  private String allTransformerID;

  private String dataformatType;

  /**
   * Constructor to be used only for test.
   * @param allTransformations
   * @param transformerDataList
   * @param tdm
   * @param application
   * @param allTransformerID
   */
  protected MTMappingsTableCellEditor(Vector<Object> allTransformations, List<TransformerData> transformerDataList, 
      TransformerDataModel tdm, Application application, final String allTransformerID, final String dataformatType) {
    this.application = application;
    this.transformerDataList = transformerDataList;   
    this.allTransformations = allTransformations;
    this.transformerDataModel = tdm;
    this.allTransformerID = allTransformerID;
    this.dataformatType = dataformatType;
  }
      
  /**
   * Constructor.
   * @param allTransformations    A list of all of the transformations.
   * @param transformerDataList   A list of all of the transformers.
   * @param tdm                   The transformer data model.
   * @param application           Application object.
   * @param editable              Boolean, true if the tree is editable.
   * @param allTransformerID      The ID string of the ALL transformer. 
   */
  public MTMappingsTableCellEditor(Vector<Object> allTransformations, List<TransformerData> transformerDataList, 
		  TransformerDataModel tdm, Application application, boolean editable, final String allTransformerID, final String dataformatType) {
    this.application = application;
    this.editable = editable;
    this.transformerDataList = transformerDataList;   
    this.allTransformations = allTransformations;
    this.transformerDataModel = tdm;
    this.allTransformerID = allTransformerID;
    this.dataformatType = dataformatType;
    
    // Set up the mappings for the common transformations:
    setupMappings();
  }

  /**
   * Sets up the mappings for the common transformations.
   */
  protected final void setupMappings() {

    LOGGER.fine("MTMappingsTableCellEditor, setupMappings(): entering");

    HashMap<Transformation, ArrayList<String>> mappings = transformerDataModel.getMappings(dataformatType);

    for (final Iterator<Object> iter = allTransformations.iterator(); iter.hasNext();) {
      Transformation tfm1 = (Transformation) iter.next();
      final String transformationTransformerID = tfm1.getTransformerid();

      if (transformationTransformerID.equalsIgnoreCase("ALL")) {
        // All of the transformers need to be added to the mapping (added into
        // the values for this transformation key):
        for (final Iterator<TransformerData> iter2 = transformerDataList.iterator(); iter2.hasNext();) {
          Transformer nextTransformer = iter2.next().getTransformer();
          final String transformerID = nextTransformer.getTransformerid();
          if (!transformerID.equalsIgnoreCase(allTransformerID)) {

            ArrayList<String> mappedTs = (ArrayList<String>) mappings.get(tfm1);

            if (mappedTs == null) {
              mappedTs = new ArrayList<String>();
              mappedTs.add(transformerID);
              mappings.put(tfm1, mappedTs);
            } else {
              if (!mappedTs.contains(transformerID)) {
                mappedTs.add(transformerID); // put in all transformers for this transformation
              }
            }
          }
        }
      } else {
        // Specific:
        for (final Iterator<TransformerData> iter2 = transformerDataList.iterator(); iter2.hasNext();) {
          Transformer nextTransformer = iter2.next().getTransformer(); // go through all transformers
          final String transformerID = nextTransformer.getTransformerid();
          
          if (transformationTransformerID.contains(transformerID + ",")) {
            ArrayList<String> mappedTs = (ArrayList<String>) mappings.get(tfm1);
            
            if (mappedTs == null) {
              mappedTs = new ArrayList<String>();
              mappedTs.add(transformerID);
              mappings.put(tfm1, mappedTs);
            } else {
              if (!mappedTs.contains(transformerID)) {
                mappedTs.add(transformerID); // this will put in only the enabled transformers                
              }
            }                        
          }                              
        }
      }
    }
    LOGGER.fine("MTMappingsTableCellEditor, setupMappings(): exiting");
  }

  /**
   * This returns the value to the edited cell when fireEditingStopped has been
   * called.
   */
  public Object getCellEditorValue() {
    return origTransformation;
  }

  /**
   * Creates the edit table for enabling/disabling transformers for a common transformation.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
    LOGGER.fine("MTMappingsTableCellEditor, getTableCellEditorComponent() entering");
    
    if (value instanceof Transformation) {    
    Transformation tfm = (Transformation) value;

    Vector<Transformer> transformers = new Vector<Transformer>();

    // Add all specific transformers:
    for (final Iterator<TransformerData> iter = transformerDataList.iterator(); iter.hasNext();) {
      Transformer tempTransfomer = iter.next().getTransformer();
      if (!tempTransfomer.getTransformerid().equalsIgnoreCase(allTransformerID)) {
        transformers.add(tempTransfomer);
      }
    }    

    tableModel = createMappingsTableModel(tfm, transformers);

    theComp = createTableComponent(application, "Transformers", tableModel, 150, editable);

    // Add the action lister, so that we can catch the description action
    // when the user updates the cell value.
    theComp.addActionListener(this);

    // Set the component enabled based on the tree enabled status.
    theComp.setEnabled(true);
    
    LOGGER.fine("MTMappingsTableCellEditor, getTableCellEditorComponent() exiting");
    }
    return theComp;
  }

  /**
   * Protected creator method to create new TableComponent.
   * @param application
   * @param title
   * @param tableModel
   * @param fieldWidth
   * @param editable
   * @return
   */
  protected TableComponent createTableComponent(final Application application, final String title, final MTMappingsTableModel tableModel, 
      final int fieldWidth, final boolean editable) {
    return new TableComponent(application, title, tableModel, fieldWidth, editable);
  }

  /**
   * Protected creator method to create the mappings table model.
   * @param tfm
   * @param transformers
   */
  protected MTMappingsTableModel createMappingsTableModel(Transformation tfm, Vector<Transformer> transformers) {
    return new MTMappingsTableModel(transformers, tfm, transformerDataModel.getMappings(dataformatType), transformerDataModel, this.dataformatType);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
  }

  /**
   * Gets the logger object.
   * @return logger.
   */
  public static Logger getLogger() {
    return LOGGER;
  }

}
