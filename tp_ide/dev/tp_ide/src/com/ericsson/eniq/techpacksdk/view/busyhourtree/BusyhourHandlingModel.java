package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.util.Vector;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;

import com.distocraft.dc5000.etl.rock.Meta_collections;

/**
 * Concrete class for parameter data for Busy Hours.
 * 
 * @author ejarsav
 * @author eheitur
 * 
 */
public class BusyhourHandlingModel extends TTParameterModel {

  /**
   * The name of the tree main node.
   */
  private String name;

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */

  protected final static int defaultWidth = 900;

  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param inType
   * @param inTable
   */
  public BusyhourHandlingModel(String name, Vector<BusyHourData> removeList, RockFactory rockFactory, boolean editable) {
    super(rockFactory, editable);
    this.attributePaneWidth = defaultWidth;
    this.name = name;
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  @Override
  protected void initializeComponents() {

  }

  /**
   * Return the name of the main node of the measurement type. This is what will
   * be displayed as the main node of the subtree.
   */
  @Override
  public String getMainNodeName() {
    return name;
  }

  /**
   * Set the name of the main node of the busy hour. This is what will be
   * displayed as the main node of the tree.
   */
  @Override
  public void setMainNodeName(String nodeName) {
    this.name = nodeName;
  }

  @Override
  public void setValueAt(Object value, String identifier) {
  }

  @Override
  public void saveChanges() {
  }

  @Override
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
    return null;
  }

  /**
   * @return the type object
   */
  public Meta_collections getTypeObject() {
    return null;
  }

}
