package com.ericsson.eniq.techpacksdk.view.group;

import java.util.Vector;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;

public class GroupTypeParameterModel extends TTParameterModel {

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */
  public static final String TYPE_ID = "Type id";

  GroupTable groupTable;
  /**
   * Constructor. Stores the given RockDBObjects.
   */
  public GroupTypeParameterModel(final RockFactory rockFactory, final GroupTable groupTable,
      final boolean isTreeEditable) {
    super(rockFactory, isTreeEditable);
    this.groupTable = groupTable;
    // Set the panel width, because the default width is not suitable.
    setPanelWidth(900); // NOPMD
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  @Override
  protected void initializeComponents() {
    // Currently no components to initialise
  }

  /**
   * Return the name of the main node of the group type. This is what will be
   * displayed as the main node of the subtree.
   */
  @Override
  public String getMainNodeName() {
    return groupTable.getTypeName();
  }

  /**
   * Set the name of the main node of the group type. This is what will be
   * displayed as the main node of the tree.
   */
  @Override
  public void setMainNodeName(final String nodeName) {
    groupTable.setTypeName(nodeName);
    this.notifyMyObservers(TYPE_ID);
  }

  @Override
  public Object getValueAt(final String identifier) {
    return null;
  }

  /**
   * Callback function for updates of the components
   * 
   * @param value
   *          the new value of the component
   * @param identifier
   *          the identifier of the updated component
   */
  @Override
  public void setValueAt(final Object value, final String identifier) {
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  @Override
  public void saveChanges() {
  }

  /**
   * Overridden remove function. Deletes the type and table objects from the DB:
   */
  @Override
  public void removeFromDB() {
  }

  @Override
  protected void setPanelWidth(final int width) {
    attributePaneWidth = width;
  }

  @Override
  public Vector<String> validateData() {// NOPMD Leave as vector as abstract returns vector
    return new Vector<String>();
  }

  /**
   * Notify all the registered observers that this instance has changed.
   * 
   * @param argument
   */
  private void notifyMyObservers(final String changedField) {
    this.setChanged(); // needs to be done, so that the notifyObservers event
    // goes through to its observers
    this.notifyObservers(changedField);
  }

  public GroupTable getGroupTable() {
    return groupTable;
  }
}
