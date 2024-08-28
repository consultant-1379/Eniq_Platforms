package com.ericsson.eniq.techpacksdk.view.reference;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;
import tableTreeUtils.DescriptionComponent;

import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class for parameter data for Referencetables
 * 
 * @author eheijun
 * 
 */
public class ReferenceTypeParameterModel extends TTParameterModel {

  private static final Logger logger = Logger.getLogger(ReferenceTypeParameterModel.class.getName());

  /**
   * The RockDBObject representing the tech pack
   */
  private final Versioning versioning;

  /**
   * The RockDBObject representing the Referencetable
   */
  private final Referencetable referencetable;

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */
  public static final String TYPE_ID = "Type id";

  public static final String TYPENAME = "Type Name";

  protected static final String UPDATEMETHOD = "Update Method";

  // private static final String TYPE = "Type";
  protected static final String DESCRIPTION = "Description";

  protected static final String DATAFORMATSUPPORT = "Data Format Support";

  // private JComboBox updatemethod;

  private final Application application;

  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param inType
   * @param inTable
   */
  public ReferenceTypeParameterModel(Application application, Versioning versioning, Referencetable referencetable,
      RockFactory rockFactory, boolean isTreeEditable) {
    super(rockFactory, isTreeEditable);
    this.application = application;
    this.versioning = versioning;
    this.referencetable = referencetable;
    // Set the panel width, because the default width is not suitable.
    setPanelWidth(900);
  }

  public Referencetable getReferencetable() {
    return referencetable;
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  protected void initializeComponents() {
    // final PairComponent pc =
    addComboBox(UPDATEMETHOD, Constants.UPDATE_METHODS_TEXT, getUpdateMethodText(Utils.replaceNull(referencetable
        .getUpdate_policy())));
    // updatemethod = (JComboBox) pc.getComponent();
    // addComboBox(TYPE, Constants.TABLE_TYPES,
    // Utils.replaceNull(referencetable.getTable_type()).toString());
    addCheckBox(DATAFORMATSUPPORT, Utils.replaceNull(referencetable.getDataformatsupport()).intValue() == 1);
    addComponent(DESCRIPTION, new DescriptionComponent(Utils.replaceNull(referencetable.getDescription()), 20));
  }

  /**
   * Return the name of the main node of the reference type. This is what will
   * be displayed as the main node of the subtree.
   */
  public String getMainNodeName() {
    return referencetable.getTypename();
  }

  /**
   * Set the name of the main node of the reference type. This is what will be
   * displayed as the main node of the tree.
   */
  public void setMainNodeName(final String nodeName) {
    final String typename = Utils.replaceNull(nodeName);
    final String typeid = this.versioning.getVersionid() + ":" + typename;
    if (referencetable.isNew()) {
      referencetable.setTypeid(typeid);
      referencetable.setTypename(typename);
      referencetable.setObjectid(typeid);
      referencetable.setObjectname(typename);
      referencetable.setDataformatsupport(1);
      referencetable.setBasedef(0);
    } else {
      JOptionPane.showMessageDialog(null, "Name can not be changed.");
    }
    this.notifyMyObservers(TYPE_ID);
  }

  private String getUpdateMethodText(final Long updateMethod) {
    final long up = updateMethod;
    int ind = 0;
    for (int jnd = 0; jnd < Constants.UPDATE_METHODS.length; jnd++) {
      if (Constants.UPDATE_METHODS[jnd] == (up)) {
        ind = jnd;
        break;
      }
    }
    return Constants.UPDATE_METHODS_TEXT[ind];
  }

  @Override
  public Object getValueAt(final String identifier) {
    if (UPDATEMETHOD.equals(identifier)) {
      return getUpdateMethodText(Utils.replaceNull(referencetable.getUpdate_policy()));
      // } else if (TYPE.equals(identifier)) {
      // return referencetable.getTable_type();
    } else if (DATAFORMATSUPPORT.equals(identifier)) {
      return referencetable.getDataformatsupport();
    } else if (DESCRIPTION.equals(identifier)) {
      return referencetable.getDescription();
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

    if (UPDATEMETHOD.equals(identifier)) {
      int ind = 0;
      for (int jnd = 0; jnd < Constants.UPDATE_METHODS_TEXT.length; jnd++) {
        if (Constants.UPDATE_METHODS_TEXT[jnd].equals((String) value)) {
          ind = jnd;
          break;
        }
      }
      referencetable.setUpdate_policy(Constants.UPDATE_METHODS[ind]);
      // } else if (TYPE.equals(identifier)) {
      // referencetable.setTable_type((String) value);
    } else if (DATAFORMATSUPPORT.equals(identifier)) {
      referencetable.setDataformatsupport(Utils.booleanToInteger((Boolean) value));
    } else if (DESCRIPTION.equals(identifier)) {
      referencetable.setDescription((String) value);
    }
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  public void saveChanges() {
    try {
      if (referencetable.gimmeModifiedColumns().size() > 0) {
        referencetable.saveToDB();
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }
  }

  /**
   * Overridden remove function. Deletes the type and table objects from the DB:
   */
  public void removeFromDB() {
    try {
      referencetable.deleteDB();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "delete.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when removing data", e);
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
    final Vector<String> errorStrings = new Vector<String>();
    if (Utils.replaceNull(referencetable.getTypename()).trim().equals("")) {
      errorStrings.add(TYPENAME + " is required");
    }

    // Check that the user does not try to define the hardcoded base techpack
    // reference table for AGGLEVEL,SELECT_(TPNAME)_AGGLEVEL.
    if (isSelect_AGGLEVEL(referencetable.getTypename())) {
        errorStrings.add(referencetable.getTypename()+" reference type cannot be used as it is defined in the base techpack.");
    }

    return errorStrings;
  } // validateData
  
  /**
   * Don't want to allow user to define a ReferenceType SELECT_(TPNAME)_AGGLEVEL
   * as this is already defined in the base TP.
   * 20100816, eeoidiv, changed approved by ekatkil.
   * @param typeName
   * @return
   */
  protected boolean isSelect_AGGLEVEL(final String typeName) {
	  boolean result = false;
	  // Create the type name by replacing the (TPNAME) in the base reference type
	  // typeName. For example: "SELECT_(TPNAME)_AGGLEVEL" for DC_E_MGW techpack
	  // "SELECT_E_MGW_AGGLEVEL".
	  final String tpName = this.versioning.getTechpack_name();
	  final String SELECT_TPNAME_AGGLEVEL = "SELECT_"+tpName+"_AGGLEVEL";
	  if(typeName.equals(SELECT_TPNAME_AGGLEVEL)) {
		  return true;
	  }
	  return false;
  } // isSelectAGGLEVEL
  
}
