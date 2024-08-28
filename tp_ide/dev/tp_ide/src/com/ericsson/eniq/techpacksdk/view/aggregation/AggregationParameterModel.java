package com.ericsson.eniq.techpacksdk.view.aggregation;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;
import tableTreeUtils.DescriptionComponent;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for parameter data for Aggregations
 * 
 * @author eheijun
 * 
 */
public class AggregationParameterModel extends TTParameterModel {

  private static final Logger logger = Logger.getLogger(AggregationParameterModel.class.getName());
  
  /**
   * The RockDBObject representing the tech pack
   */
  private final Versioning versioning;
  
  
  /**
   * The RockDBObject representing the Aggregation
   */
  private final Aggregation aggregation;

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */
  public static final String AGGREGATION = "Aggregation";
  public static final String AGGREGATIONTYPE = "Aggregation Type";
  public static final String AGGREGATIONSCOPE = "Aggregation Scope";
  
  
  private final Application application;


  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param inType
   * @param inTable
   */
  public AggregationParameterModel(Application application, Versioning versioning, Aggregation aggregation, RockFactory rockFactory, boolean isTreeEditable) {
    super(rockFactory, isTreeEditable);
    this.application = application;
    this.versioning = versioning;
    this.aggregation = aggregation;
    // Set the panel width, because the default width is not suitable.
    setPanelWidth(900);
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public Aggregation getAggregation() {
    return aggregation;
  }
  
  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  protected void initializeComponents() {
    addComponent(AGGREGATIONTYPE, new DescriptionComponent(aggregation.getAggregationtype(), 10));
    addComponent(AGGREGATIONSCOPE, new DescriptionComponent(aggregation.getAggregationscope(), 10));
  }

  /**
   * Return the name of the main node of the aggregation. This is what will
   * be displayed as the main node of the subtree.
   */
  public String getMainNodeName() {
    return aggregation.getAggregation();
  }

  /**
   * Set the name of the main node of the aggregation. This is what will be
   * displayed as the main node of the tree.
   */
  public void setMainNodeName(final String nodeName) {
    aggregation.setAggregation(nodeName);
    this.notifyMyObservers(AGGREGATION);
  }
  
  @Override
  public Object getValueAt(final String identifier) {
    if (AGGREGATIONTYPE.equals(identifier)) {
      return aggregation.getAggregationtype();
    } else if (AGGREGATIONSCOPE.equals(identifier)) {
      return aggregation.getAggregationscope();
    } else {
      return null;
    }
  }

  /**
   * Call back function for updates of the components
   * 
   * @param value
   *          the new value of the component
   * @param identifier
   *          the identifier of the updated component
   */
  public void setValueAt(final Object value, final String identifier) {
    if (AGGREGATIONTYPE.equals(identifier)) {
      aggregation.setAggregationtype(Utils.replaceNull((String) value));
    } else if (AGGREGATIONSCOPE.equals(identifier)) {
      aggregation.setAggregationscope(Utils.replaceNull((String) value));
    }
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  public void saveChanges() {
    try {
      if (aggregation.gimmeModifiedColumns().size() > 0) {
        aggregation.saveToDB();
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap()
          .getString("save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }
  }

  /**
   * Overridden remove function. Deletes the type and table objects from the DB:
   */
  public void removeFromDB() {
    try {
      aggregation.deleteDB();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap()
          .getString("delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when removing data", e);
    }
  }

  @Override
  protected void setPanelWidth(final int width) {
    attributePaneWidth = width;
  }
  
  /**
   *  Notify all the registered observers that this instance has changed. 
   *  
   * @param argument
   */
  private void notifyMyObservers(final String changedField){
    this.setChanged(); // needs to be done, so that the notifyObservers event goes through to its observers
    this.notifyObservers(changedField);
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    return errorStrings;
  }

}
