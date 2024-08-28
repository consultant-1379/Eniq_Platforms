package com.ericsson.eniq.techpacksdk.view.transformer;

//import java.util.logging.Logger;

import java.util.Vector;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;
import tableTreeUtils.DescriptionComponent;

import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for parameter data for Transformers
 * 
 * @author enaland ejeahei
 * 
 */
public class TransformerParameterModel extends TTParameterModel {

  // private static final Logger logger =
  // Logger.getLogger(TransformerParameterModel.class.getName());

  protected final static int defaultWidth = 760;

  /**
   * The RockDBObject representing the tech pack
   */
  private final Versioning versioning;

  /**
   * The RockDBObject representing the Transformer
   */
  private final Transformer transformer;

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */
  public static final String TRANSFORMER_ID = "Transformer id";

  private static final String DESCRIPTION = "Description";

  /**
   * String, which will be visible at the tree
   */
  private String name = "";

  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param inType
   * @param inTable
   */
  public TransformerParameterModel(String name, Versioning versioning, Transformer transformer,
      RockFactory rockFactory, boolean isTreeEditable) {
    super(rockFactory, isTreeEditable);
    this.name = name;
    this.versioning = versioning;
    this.transformer = transformer;
    this.attributePaneWidth = defaultWidth;
  }

  public Transformer getTransformer() {
    return transformer;
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  protected void initializeComponents() {
    addComponent(DESCRIPTION, new DescriptionComponent(Utils.replaceNull(transformer.getDescription()), 20));
  }

  /**
   * Return the name of the main node of the transformer. This is what will be
   * displayed as the main node of the subtree.
   */
  public String getMainNodeName() {
    String value = transformer.getTransformerid();
    if (value.startsWith(this.versioning.getVersionid()))
      value = value.substring(this.versioning.getVersionid().length());

    value = value.replaceFirst(":", "");
    if (value.contains(":"))
      value = value.substring(0, value.indexOf(":"));
    return value;
  }

  /**
   * Set the name of the main node of the transformer. This is what will be
   * displayed as the main node of the tree.
   */
  public void setMainNodeName(final String nodeName) {
    final String transformerid = this.versioning.getVersionid() + ":" + nodeName;
    transformer.setTransformerid(transformerid);
    this.notifyMyObservers(TRANSFORMER_ID);
  }

  @Override
  public Object getValueAt(final String identifier) {
    if (DESCRIPTION.equals(identifier)) {
      return transformer.getDescription();
    } else {
      return null;
    }
  }

  /**
   * Callback function for updates of the components
   * 
   * @param value
   *          the new value of the component
   * @param identifier
   *          the identifier of the updated component
   */
  public void setValueAt(final Object value, final String identifier) {
    if (DESCRIPTION.equals(identifier)) {
      transformer.setDescription((String) value);
    } else {
      // TODO error
    }
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  public void saveChanges() {
    try {
      if (transformer.gimmeModifiedColumns().size() > 0) {
        transformer.saveToDB();
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  /**
   * Overridden remove function. Deletes the type and table objects from the DB:
   */
  public void removeFromDB() {
    try {
      transformer.deleteDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  @Override
  protected void setPanelWidth(final int width) {
    attributePaneWidth = width;
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

  @Override
  public Vector<String> validateData() {
    return new Vector<String>();
  }

}
