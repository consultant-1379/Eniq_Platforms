package measurementType;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JComboBox;

import com.ericsson.eniq.techpacksdk.common.Constants;


import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;
import tableTreeUtils.DescriptionComponent;
import tableTreeUtils.PairComponent;

/**
 * Concrete class for parameter data for measurement types
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class MeasurementTypeParameterModel extends TTParameterModel {

  /**
   * The RockDBObject representing the measurement type
   */
  private Measurementtype typeObject;

  /**
   * The RockDBObject representing the measurement table
   */
  private Measurementtable tableObject;

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */
  private static final String SIZING_ID = "Sizing";

  private static final String DESCRIPTION = "Description";

  private static final String CLASS = "Class";

  private static final String PLAIN = "Plain";

  private static final String DELTA = "Delta";

  private static final String ELEM_BH = "Elem BH";

  private static final String RANKING = "Ranking";

  private static final String TOTAL_AGG = "Total Agg";
  
  private static final String SON_AGG = Constants.SONAGG;
  
  private static final String SON15AGG = Constants.SON15AGG;

  private static final String TOTAL_AGG_DESC = "Total Agg Desc";

  // Components
  PairComponent sizingIdComp = null;

  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param inType
   * @param inTable
   * @param rockFactory
   * @param isTreeEditable
   */
  public MeasurementTypeParameterModel(Measurementtype inType, Measurementtable inTable, RockFactory rockFactory,
      boolean isTreeEditable) {
    super(rockFactory, isTreeEditable);

    // Set the panel width, because the default width is not suitable.
    setPanelWidth(400);

    typeObject = inType;
    tableObject = inTable;
  }

  protected void setPanelWidth(int width) {
    if (width <= 0) {
      attributePaneWidth = defaultAttributePaneWidth;
    } else {
      attributePaneWidth = width;
    }
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  protected void initializeComponents() {

    /**
     * The component added to the panel. The reference to the component can be
     * used e.g. for creation special relationships between the components in
     * the same panel.
     */
    @SuppressWarnings("unused")
    PairComponent comp = null;

    // Add the components to the panel
    String[] comboItems = { "Small", "Medium", "Large" };
    sizingIdComp = addComboBox(SIZING_ID, comboItems, tableObject.getPartitionplan());
    comp = addCheckBox(TOTAL_AGG, true);
    comp = addCheckBox(SON_AGG, true);
    comp = addCheckBox(SON15AGG, true);
    comp = addCheckBox(RANKING, false);
    comp = addCheckBox(ELEM_BH, true);
    comp = addCheckBox(DELTA, false);
    comp = addCheckBox(PLAIN, true);
    comp = addTextFieldWithLimitedSize(TOTAL_AGG_DESC, typeObject.getTypename(), 10, 25, true);
    comp = addTextField(CLASS, typeObject.getObjecttype(), 4);
    comp = addComponent(DESCRIPTION, new DescriptionComponent(typeObject.getDescription(), 20));

  }

  /**
   * Return the name of the main node of the measurement type. This is what will
   * be displayed as the main node of the subtree.
   * 
   * @return the main node name
   */
  public String getMainNodeName() {
    return typeObject.getTypeid();
  }

  /**
   * Set the name of the main node of the measurement type. This is what will be
   * displayed as the main node of the tree.
   * 
   * @param nodeName
   *          the main node name
   */
  public void setMainNodeName(String nodeName) {
    typeObject.setTypeid(nodeName);
    tableObject.setTypeid(typeObject.getTypeid());
    this.notifyMyObservers("setMainNodeName");
  }

  /**
   * Callback function for updates of the components
   * 
   * PS. Incomplete example, not all the components in the attribute panel have
   * corresponding getters and setters in the Measurementtype-files
   * 
   * @param value
   *          the new value of the component
   * @param identifier
   *          the identifier of the updated component
   */
  public void setValueAt(Object value, String identifier) {
    if (SIZING_ID.equals(identifier)) {
      tableObject.setPartitionplan((String) value);
    } else if (DESCRIPTION.equals(identifier)) {
      typeObject.setDescription((String) value);
    } else if (CLASS.equals(identifier)) {
      typeObject.setObjecttype((String) value);
    } else if (TOTAL_AGG_DESC.equals(identifier)) {
      typeObject.setTypename((String) value);
    }
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  public void saveChanges() {
    try {
      tableObject.saveDB();
      typeObject.saveDB();
    } catch (SQLException e) {
      // Intentionally left blank. Example only.
      e.printStackTrace();
    } catch (RockException e) {
      // Intentionally left blank. Example only.
      e.printStackTrace();
    }
  }

  /**
   * Overridden remove function. Deletes the type and table objects from the DB:
   */
  public void removeFromDB() {
    try {
      typeObject.deleteDB();
      tableObject.deleteDB();
    } catch (SQLException e) {
      // Intentionally left blank. Example only.
      e.printStackTrace();
    } catch (RockException e) {
      // Intentionally left blank. Example only.
      e.printStackTrace();
    }
  }

  /**
   * Call this method to notify all the registered observers that this instance
   * has changed. The String argument can be used to specify the reason as to
   * why we raise the event of the change
   * 
   * to match the examples in the implementing tablemodel classes: argument =
   * "setMainNodeName";
   * 
   * @param argument
   */
  private void notifyMyObservers(String argument) {
    // System.out.println("Notifying the parameter model's observers");
    this.setChanged(); // needs to be done, so that the notifyObservers
    // event goes through to its observers
    this.notifyObservers(argument);
  }

  /**
   * Overridden version of the method. This is an incomplete example which only
   * works for some of the components.
   * 
   * @param identifier
   * @return the value object
   */
  public Object getValueAt(String identifier) {

    // Dummy settings for some fields.
    if (SIZING_ID.equals(identifier)) {
      return tableObject.getPartitionplan();
    } else if (DESCRIPTION.equals(identifier)) {
      return typeObject.getDescription();
    } else if (CLASS.equals(identifier)) {
      return typeObject.getObjecttype();
    } else if (TOTAL_AGG_DESC.equals(identifier)) {
      return typeObject.getTypename();
    } else
      return null;
  }

  /**
   * Overridden version of the method to implement validations for this table
   * model.
   * 
   * @see tableTree.TTTableModel#validateData()
   */
  @Override
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();

    // Sample dummy validation
    if (((JComboBox) sizingIdComp.getComponent()).getSelectedIndex() == 1) {
      System.out.println("isDataValid(): SizingId combo has invalid value: "
          + ((JComboBox) sizingIdComp.getComponent()).getSelectedItem());
      errorStrings.add("SizingId combo has invalid value: "
          + ((JComboBox) sizingIdComp.getComponent()).getSelectedItem());

    }
    return errorStrings;
  }
}
