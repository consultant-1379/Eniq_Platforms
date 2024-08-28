package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.util.Vector;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;

/**
 * Concrete class for parameter data for Data Formats.
 * 
 * @author ejarsav
 * 
 */
public class DataformatParameterModel extends TTParameterModel {

  //private static final Logger logger = Logger.getLogger(DataformatParameterModel.class.getName());

  protected final static int defaultWidth = 900;

  private String name;

  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param inType
   * @param inTable
   */
  public DataformatParameterModel(String name, RockFactory rockFactory, boolean editable) {
    super(rockFactory, editable);
    this.name = name;
    this.attributePaneWidth = defaultWidth;
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  protected void initializeComponents() {
  }

  /**
   * Return the name of the main node of the measurement type. This is what will
   * be displayed as the main node of the subtree.
   */
  public String getMainNodeName() {
    return name;
  }

  /**
   * Set the name of the main node of the measurement type. This is what will be
   * displayed as the main node of the tree.
   */
  public void setMainNodeName(String nodeName) {
  }

  /**
   * Callback function for updates of the components
   * 
   * @param value
   *          the new value of the component
   * @param identifier
   *          the identifier of the updated component
   */
  public void setValueAt(Object value, String identifier) {
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  public void saveChanges() {
  }

  /**
   * Overridden remove function. Deletes the type and table objects from the DB:
   */
  public void removeFromDB() {
  }

  @Override
  protected void setPanelWidth(int width) {
  }

  @Override
  public Object getValueAt(String identifier) {
    return null;
  }

  @Override
  public Vector<String> validateData() {
    return new Vector<String>();
  }
}
