package com.ericsson.eniq.techpacksdk.view.measurement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTree.TTParameterModel;
import tableTreeUtils.DescriptionComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.RadiobuttonComponent;
import tableTreeUtils.TCTableModel;
import tableTreeUtils.UniverseExtensionComponent;

import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.TableComponent;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourData;

/**
 * Concrete class for parameter data for Measurementtypes
 * 
 * @author eheijun
 * 
 */
public class MeasurementTypeParameterModel extends TTParameterModel {

  private static final Logger logger = Logger.getLogger(MeasurementTypeParameterModel.class.getName());

  protected final static int defaultWidth = 700;

  private int productPlaceholderValueOriginal = 0;

  private int customPlaceholderValueOriginal = 0;

  /**
   * The RockDBObject representing the tech pack
   */
  private final Versioning versioning;

  /**
   * The RockDBObjects representing universe names of tp
   */
  private final String[] universeExtensions;

  /**
   * The RockDBObjects representing the Measurementtypeclass
   */
  private final List<Measurementtypeclass> mtypeclasses;

  /**
   * The RockDBObject representing the Measurementtype
   */
  private final MeasurementtypeExt measurementtypeExt;

  protected final TCTableModel dcsupportTableModel;

  protected final TCTableModel objectbhTableModel;

  // /**
  // * Helper stering for mtype class handling
  // */
  // private String mtypeclassdesc;

  public static final String TYPE_ID = "Type id";

  public static final String TYPENAME = "Type Name";

  /**
   * Identifiers for the components in the panel displaying the parameters.
   */
  protected static final String SIZING_ID = "Sizing";

  protected static final String TABLETYPE = "Table Type";

  protected static final String RANKING = "Ranking Table";

  protected static final String PLAIN = "Plain Table";

  protected static final String CALC = "Calc Table";

  protected static final String VECTOR = "Vector Table";

  protected static final String NONE = "None";

  protected static final String TOTAL_AGG = "Total Aggregation";

  protected static final String ELEM_BH = "Element BH Support";

  protected static final String DELTA_CALC_SUPPORT = "Delta Calc Support";  

  protected static final String OBJ_BH = "Object BH Support";

  // protected static final String VECTOR = "Vector Support";

  protected static final String CLASSIFICATION = "Classification";

  protected static final String UNIVERSE_EXT = "Universe Extensions";

  // protected static final String JOINABLE = "Joinable";

  protected static final String DESCRIPTION = "Description";

  protected static final String DATAFORMATSUPPORT = "Data Format Support";

  protected static final String PRODUCTPLACEHOLDERS = "Product BH Placeholders";

  protected static final String CUSTOMPLACEHOLDERS = "Custom BH Placeholders";

  PairComponent pc2;

  PairComponent compSizing;

  PairComponent compUniverseExt;

  PairComponent compTabletype;

  PairComponent compTotalAgg;
  
  PairComponent compSONAgg;    
  
  PairComponent compSON15MinAgg;

  PairComponent compElemBH;

  PairComponent compObjBH;

  PairComponent compClassification;

  PairComponent compDescription;

  PairComponent productPlaceholders;

  PairComponent customPlaceholders;

  PairComponent compDeltaCalcSupport;

  PairComponent dataFormatSupport;

  PairComponent compObjectBHSupport;

  Application application;

  /**
   * Constructor. Stores the given RockDBObjects.
   * 
   * @param objectbhTableModel
   * 
   * @param inType
   * @param inTable
   */
  public MeasurementTypeParameterModel(final Application application, final Versioning versioning,
      final List<Measurementtypeclass> mtypeclasses, final MeasurementtypeExt measurementtypeExt,
      final TCTableModel dcsupportTableModel, final TCTableModel objectbhTableModel, final String[] universeExtensions,
      final RockFactory rockFactory, final boolean isTreeEditable) {
    super(rockFactory, isTreeEditable);
    this.application = application;
    this.versioning = versioning;
    this.mtypeclasses = mtypeclasses;
    this.measurementtypeExt = measurementtypeExt;
    this.attributePaneWidth = getDefaultWidth();
    this.dcsupportTableModel = dcsupportTableModel;
    this.objectbhTableModel = objectbhTableModel;
    this.universeExtensions = universeExtensions;
  }

  protected int getDefaultWidth() {
    return defaultWidth;
  }

  public MeasurementtypeExt getMeasurementtypeExt() {
    return measurementtypeExt;
  }

  /**
   * List this specific class' parameter components. The identifiers used here
   * will be displayed as labels in the panel. They will also serve as
   * identifiers for update callbacks.
   */
  @Override
  protected void initializeComponents() {

    // Initialize the sizing combo-box
    compSizing = addComboBox(SIZING_ID, getSizingItems(), Utils.replaceNull(measurementtypeExt.getSizing()));

    // Initialize the table type
    final List<String> buttons = initializeTableTypeButtons();
    final int selected = initializeSelectedTableTypeButton();
    compTabletype = addComponent(TABLETYPE, new RadiobuttonComponent(buttons.toArray(new String[buttons.size()]),
        selected));

    initializeCheckBoxes();

    objectbhTableModel.setData(measurementtypeExt.getObjBHSupport());
    compObjectBHSupport = addComponent(OBJ_BH, new TableComponent(application, "ObjectBHSupport", objectbhTableModel,
        12, true));

    dcsupportTableModel.setData(measurementtypeExt.getDeltaCalcSupport());
    compDeltaCalcSupport = addComponent(DELTA_CALC_SUPPORT, new TableComponent(application, "DeltaCalcSupport",
        dcsupportTableModel, 12, true));

    final String mtypeclassdesc = Utils.decodeTypeclassid(Utils.replaceNull(measurementtypeExt.getTypeclassid()),
        this.versioning.getVersionid(), this.versioning.getTechpack_name());
    compClassification = addTextFieldWithLimitedSize(CLASSIFICATION, mtypeclassdesc, 12, Measurementtype
        .getTypeclassidColumnSize(), false);

    compUniverseExt = addComponent(UNIVERSE_EXT, new UniverseExtensionComponent(Utils.replaceNull(measurementtypeExt
        .getUniverseextension()), 12, universeExtensions));

    compDescription = addComponent(DESCRIPTION, new DescriptionComponent(Utils.replaceNull(measurementtypeExt
        .getDescription()), 32));

    productPlaceholders = addTextFieldWithLimitedSize(PRODUCTPLACEHOLDERS, Integer.toString(measurementtypeExt
        .getBHProductPlaceholders()), 12, Busyhourplaceholders.getProductplaceholdersColumnSize(), false);

    // QUICKLY store the original value for later!
    productPlaceholderValueOriginal = measurementtypeExt.getBHProductPlaceholders();

    customPlaceholders = addTextFieldWithLimitedSize(CUSTOMPLACEHOLDERS, Integer.toString(measurementtypeExt
        .getBHCustomPlaceholders()), 12, Busyhourplaceholders.getCustomplaceholdersColumnSize(), false);

    // QUICKLY store the original value for later!
    customPlaceholderValueOriginal = measurementtypeExt.getBHCustomPlaceholders();
    setEnabelesAndDisables(selected);

  }

  protected String[] getSizingItems() {
    return Constants.SIZINGITEMS;
  }

  protected void initializeCheckBoxes() {
    compTotalAgg = addCheckBox(TOTAL_AGG, Utils.replaceNull(measurementtypeExt.getTotalagg()).intValue() == 1);
    compElemBH = addCheckBox(ELEM_BH, Utils.replaceNull(measurementtypeExt.getElementbhsupport()).intValue() == 1);
    dataFormatSupport = addCheckBox(DATAFORMATSUPPORT, Utils.replaceNull(measurementtypeExt.getDataformatsupport())
        .intValue() == 1);
    //To show only in case of Supported TPs (Currently only supported by SON PM TPs)
    for(String tpSubStr : Constants.ROPGRPSUPPORTED_TP){
    	if(measurementtypeExt.getVersionid().contains(tpSubStr)){
    		compSONAgg = addCheckBox(Constants.SONAGG, Utils.replaceNull(measurementtypeExt.getSonAgg()).intValue() == 1); 
    	    compSON15MinAgg = addCheckBox(Constants.SON15AGG, Utils.replaceNull(measurementtypeExt.getSonFifteenMinAgg()).intValue() == 1);
    		break ;
    	}
    }
  }

  protected int initializeSelectedTableTypeButton() {
    int selected = 0;

    if (Utils.replaceNull(measurementtypeExt.getRankingtable()).intValue() == 1) {
      selected = 1;
    } else if (Utils.replaceNull(measurementtypeExt.getPlaintable()).intValue() == 1) {
      selected = 2;
    } else if (Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()).intValue() == 1) {
      selected = 3;
    } else if (Utils.replaceNull(measurementtypeExt.getVectorsupport()).intValue() == 1) {
      selected = 4;
    }
    return selected;
  }

  protected List<String> initializeTableTypeButtons() {
    final List<String> listOfButtons = new ArrayList<String>();

    listOfButtons.add(NONE);
    listOfButtons.add(RANKING);
    listOfButtons.add(PLAIN);
    listOfButtons.add(CALC);
    listOfButtons.add(VECTOR);

    return listOfButtons;
  }

  /**
   * @param selected
   */
  protected void setEnabelesAndDisables(final int selected) {

    measurementtypeExt.setUseplaceholders(false);

    customPlaceholders.getComponent().setEnabled(false);
    customPlaceholders.setVisible(false);
    productPlaceholders.getComponent().setEnabled(false);
    productPlaceholders.setVisible(false);

    compDeltaCalcSupport.getComponent().setEnabled(false);
    switch (selected) {
    case 0:
      break;
    case 1:
      // ranking table
      productPlaceholders.setVisible(true);
      customPlaceholders.setVisible(true);
      if (!Utils.isCustomTP(versioning.getTechpack_type())) {
        productPlaceholders.getComponent().setEnabled(true);
        customPlaceholders.getComponent().setEnabled(true);
      }

      measurementtypeExt.setUseplaceholders(true);

      break;
    case 2:
      break;
    case 3:
      compDeltaCalcSupport.getComponent().setEnabled(true);
      break;
    case 4:
      break;
    default:
      break;
    }

  }

  /**
   * Return the name of the main node of the measurement type. This is what will
   * be displayed as the main node of the subtree.
   */
  @Override
  public String getMainNodeName() {
    return measurementtypeExt.getTypename();
  }

  /**
   * Set the name of the main node of the measurement type. This is what will be
   * displayed as the main node of the tree.
   */
  @Override
  public void setMainNodeName(final String nodeName) {
    final String typename = Utils.replaceNull(nodeName);
    final String typeid = Utils.encodeTypeid(typename, this.versioning.getVersionid());
    measurementtypeExt.setTypeid(typeid);
    measurementtypeExt.setTypename(typename);
    measurementtypeExt.setObjectid(typeid);
    measurementtypeExt.setObjectname(typename);
    measurementtypeExt.setFoldername(typename);
    // String bhobj =
    // typename.substring(measurementtypeExt.getVendorid().length() + 1);
    measurementtypeExt.setBHLevel(typename);
    this.notifyMyObservers(TYPE_ID);
  }

  /**
   * Returns previous values for text fields
   */
  @Override
  public Object getValueAt(final String identifier) {
    if (SIZING_ID.equals(identifier)) {
      return measurementtypeExt.getSizing();
    } else if (TABLETYPE.equals(identifier)) {
      return getTableType();
    } else if (TOTAL_AGG.equals(identifier)) {
      return measurementtypeExt.getTotalagg();
    } else if (Constants.SONAGG.equals(identifier)) {
      return Utils.replaceNull(measurementtypeExt.getSonAgg());    
    }else if (Constants.SON15AGG.equals(identifier)) {      
    	return Utils.replaceNull(measurementtypeExt.getSonFifteenMinAgg());
    } else if (RANKING.equals(identifier)) {
      return measurementtypeExt.getRankingtable();
    } else if (ELEM_BH.equals(identifier)) {
      return measurementtypeExt.getElementbhsupport();
    } else if (OBJ_BH.equals(identifier)) {
      return measurementtypeExt.getObjBHSupport();
    } else if (DELTA_CALC_SUPPORT.equals(identifier)) {
      return measurementtypeExt.getDeltaCalcSupport();
    } else if (PLAIN.equals(identifier)) {
      return measurementtypeExt.getPlaintable();
    } else if (CLASSIFICATION.equals(identifier)) {
      return Utils.decodeTypeclassid(measurementtypeExt.getTypeclassid(), this.versioning.getVersionid(),
          this.versioning.getTechpack_name());
    } else if (UNIVERSE_EXT.equals(identifier)) {
      return measurementtypeExt.getUniverseextension();
    } else if (DATAFORMATSUPPORT.equals(identifier)) {
      return measurementtypeExt.getDataformatsupport();
    } else if (DESCRIPTION.equals(identifier)) {
      return measurementtypeExt.getDescription();
    } else if (PRODUCTPLACEHOLDERS.equals(identifier)) {
      return Integer.toString(measurementtypeExt.getBHProductPlaceholders());
    } else if (CUSTOMPLACEHOLDERS.equals(identifier)) {
      return Integer.toString(measurementtypeExt.getBHCustomPlaceholders());
    } else {
      return null;
    }
  }

  protected Object getTableType() {
    if (Utils.replaceNull(measurementtypeExt.getRankingtable()) == 1) {
      return RANKING;
    } else if (Utils.replaceNull(measurementtypeExt.getPlaintable()) == 1) {
      return PLAIN;
    } else if (Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()) == 1) {
      return CALC;
    } else if (Utils.replaceNull(measurementtypeExt.getVectorsupport()) == 1) {
      return VECTOR;
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
  @Override
  @SuppressWarnings("unchecked")
  public void setValueAt(final Object value, final String identifier) {
    if (SIZING_ID.equals(identifier)) {
      measurementtypeExt.setSizing((String) value);
    } else if (TABLETYPE.equals(identifier)) {
      final int selected = setTableType(value);
      setEnabelesAndDisables(selected);
    } else if (TOTAL_AGG.equals(identifier)) {
      measurementtypeExt.setTotalagg(Utils.booleanToInteger((Boolean) value));
      // } else if (RANKING.equals(identifier)) {
      // measurementtypeExt.setRankingtable(Utils.booleanToInteger((Boolean)
      // value));
    } else if (Constants.SONAGG.equals(identifier)) {    	
      measurementtypeExt.setSonAgg(Utils.booleanToInteger((Boolean) value));        
    }else if(Constants.SON15AGG.equals(identifier)){    	   
    	measurementtypeExt.setSonFifteenMinAgg(Utils.booleanToInteger((Boolean) value));    	
    } else if (ELEM_BH.equals(identifier)) {
      measurementtypeExt.setElementbhsupport(Utils.booleanToInteger((Boolean) value));
    } else if (OBJ_BH.equals(identifier)) {
      measurementtypeExt.setObjBHSupport((Vector<Object>) value);
    } else if (DELTA_CALC_SUPPORT.equals(identifier)) {
      measurementtypeExt.setDeltaCalcSupport((Vector<Object>) value);
      this.notifyMyObservers(DELTA_CALC_SUPPORT);
      // } else if (PLAIN.equals(identifier)) {
      // measurementtypeExt.setPlaintable(Utils.booleanToInteger((Boolean)
      // value));
    } else if (CLASSIFICATION.equals(identifier)) {
      final String typeclassid = Utils.encodeTypeclassid(Utils.replaceNull((String) value), this.versioning
          .getVersionid(), this.versioning.getTechpack_name());
      measurementtypeExt.setTypeclassid(typeclassid);
    } else if (UNIVERSE_EXT.equals(identifier)) {
      measurementtypeExt.setUniverseextension(Utils.replaceNull((String) value));
    } else if (DATAFORMATSUPPORT.equals(identifier)) {
      measurementtypeExt.setDataformatsupport(Utils.booleanToInteger((Boolean) value));
    } else if (DESCRIPTION.equals(identifier)) {
      measurementtypeExt.setDescription(Utils.replaceNull((String) value));
    } else if (PRODUCTPLACEHOLDERS.equals(identifier)) {
      measurementtypeExt.setBHProductPlaceholders(Integer.parseInt((String) value));
    } else if (CUSTOMPLACEHOLDERS.equals(identifier)) {
      measurementtypeExt.setBHCustomPlaceholders(Integer.parseInt((String) value));
    }
  }

  protected int setTableType(final Object value) {
    int selected = 0;
    measurementtypeExt.setRankingtable(0);
    measurementtypeExt.setPlaintable(0);
    measurementtypeExt.setDeltacalcsupport(0);
    measurementtypeExt.setVectorsupport(0);
    if (value == null) {
      selected = 0;
    } else if (value.equals(RANKING)) {
      measurementtypeExt.setRankingtable(1);
      selected = 1;
    } else if (value.equals(PLAIN)) {
      measurementtypeExt.setPlaintable(1);
      selected = 2;
    } else if (value.equals(CALC)) {
      measurementtypeExt.setDeltacalcsupport(1);
      selected = 3;
    } else if (value.equals(VECTOR)) {
      measurementtypeExt.setVectorsupport(1);
      selected = 4;
    }
    return selected;
  }

  /**
   * Overridden save function. Saves the type and table objects in the DB.
   */
  @Override
  public void saveChanges() {
    try {
      if (measurementtypeExt.gimmeModifiedColumns().size() > 0) {
        boolean exists = false;
        for (final Iterator<Measurementtypeclass> iter = mtypeclasses.iterator(); iter.hasNext();) {
          final Measurementtypeclass mtypeclass = iter.next();
          if (mtypeclass.getTypeclassid().equalsIgnoreCase(measurementtypeExt.getTypeclassid())) {
            exists = true;
            break;
          }
        }
        if (!exists) {
          final String classdescription = Utils.decodeTypeclassid(measurementtypeExt.getTypeclassid(), this.versioning
              .getVersionid(), this.versioning.getTechpack_name());
          final Measurementtypeclass newMtypeclass = new Measurementtypeclass(rockFactory, true);
          newMtypeclass.setTypeclassid(measurementtypeExt.getTypeclassid());
          newMtypeclass.setVersionid(this.versioning.getVersionid());
          newMtypeclass.setDescription(classdescription);
          newMtypeclass.saveToDB();
          mtypeclasses.add(newMtypeclass);
          logger.info("save mtypeclass " + newMtypeclass.getTypeclassid());
        }

        measurementtypeExt.saveToDB();
        logger.info("save mtype " + measurementtypeExt.getTypename());
      }
      dcsupportTableModel.saveChanges();
      objectbhTableModel.saveChanges();
    } catch (final Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
      logger.log(Level.SEVERE, "Fatal error when saving data", e);
    }
  }

  /**
   * Overridden remove function. Deletes the type object from the DB
   */
  @Override
  public void removeFromDB() {
    try {
      dcsupportTableModel.removeFromDB();
      objectbhTableModel.removeFromDB();
      measurementtypeExt.deleteDB();
    } catch (final Exception e) {
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
    this.setChanged(); // needs to be done, so that the notifyObservers
    // event goes through to its observers
    this.notifyObservers(changedField);
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    if (Utils.replaceNull(measurementtypeExt.getTypename()).trim().equals("")) {
      errorStrings.add(TYPENAME + " is required");
    }
    // Classification can be empty
    // LimitedSizeTextField tmp = (LimitedSizeTextField)
    // compClassification.getComponent();
    // String tmpstr = tmp.getText().trim();
    // if (tmpstr.equals("")) {
    // errorStrings.add(measurementtypeExt.getTypename() + ": " +
    // CLASSIFICATION + " is required");
    // }

    // Sizing cannot be empty (except for base techpack)
    if (!versioning.getTechpack_type().equalsIgnoreCase("base")) {
      if (Utils.replaceNull(measurementtypeExt.getSizing()).trim().equals("")) {
        errorStrings.add(measurementtypeExt.getTypename() + ": " + SIZING_ID + " is required");
      }
    }

    // If 'Calc Table' is selected, then 'Total Aggregation' must also be
    // selected
    if (Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()).intValue() == 1
        && Utils.replaceNull(measurementtypeExt.getTotalagg()).intValue() == 0) {
    	errorStrings.add(measurementtypeExt.getTypename() + ": If " + CALC + " is selected, then " + TOTAL_AGG
    	          + " is also required.");
    }
    
    // If 'SON15MINAGG' is selected, then 'CALC table' must not be selected
    if (Utils.replaceNull(measurementtypeExt.getSonFifteenMinAgg()).intValue() == 1
        && Utils.replaceNull(measurementtypeExt.getDeltacalcsupport()).intValue() == 1) {
       errorStrings.add(measurementtypeExt.getTypename() + ": If " + Constants.SON15AGG + " is selected, then " + CALC + " can not be selected.");
     }

    // If table type is "Ranking Table" and "Element BH Support" is selected,
    // then the "Object BH Support" is not allowed to be defined.
    if (Utils.replaceNull(measurementtypeExt.getRankingtable()).intValue() == 1
        && Utils.replaceNull(measurementtypeExt.getElementbhsupport()).intValue() == 1) {
      if (measurementtypeExt.getObjBHSupport() != null && measurementtypeExt.getObjBHSupport().size() > 0) {
        errorStrings.add(measurementtypeExt.getTypename() + ": If " + RANKING + " and " + ELEM_BH
            + " are selected, then " + OBJ_BH + " cannot be defined.");
      }
    }

    // If table type is "Ranking Table", then the "Object BH Support" can have
    // only one value defined.
    if (Utils.replaceNull(measurementtypeExt.getRankingtable()).intValue() == 1) {
      if (measurementtypeExt.getObjBHSupport() != null && measurementtypeExt.getObjBHSupport().size() > 1) {
        errorStrings.add(measurementtypeExt.getTypename() + ": If " + RANKING + " is selected, then " + OBJ_BH
            + " cannot have more than one value defined.");
      }
    }

    // If either of the placeholder sizes have reduced then an additional check
    // must be done to
    // see if the placeholder can be removed.
    // Check if there are any enabled placeholders above the new number.
    int customPlaceholderValueNew = Utils.replaceNull(measurementtypeExt.getBHCustomPlaceholders()).intValue();
    int productPlaceholderValueNew = Utils.replaceNull(measurementtypeExt.getBHProductPlaceholders()).intValue();

    if (productPlaceholderValueNew < productPlaceholderValueOriginal && productPlaceholderValueNew >= 0) {
      TechPackIDE tpIDE = (TechPackIDE) this.application;
      // ProductPlaceholders
      List<BusyHourData> bhData = tpIDE.getDataModelController().getBusyhourHandlingDataModel()
          .getBusyHourDataByPlaceholderType(measurementtypeExt.getVersionid(), measurementtypeExt.getTypename(),
              Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX);
      if (bhData != null && bhData.size() > 0) {
        for (int i = productPlaceholderValueNew; i < bhData.size(); i++) {
          BusyHourData bd = bhData.get(i);
          if (bd.getBusyhour().getEnable() > 0) {
            errorStrings.add(measurementtypeExt.getTypename() + ": Number of " + PRODUCTPLACEHOLDERS
                + " cannot be reduced to " + productPlaceholderValueNew
                + " as there are enabled busy hours more than that number.");
            break;
          } // end if
        } // end for
      } // end if
    }// end if (main if statement)

    if (customPlaceholderValueNew < customPlaceholderValueOriginal && customPlaceholderValueNew >= 0) {
      // CustomPlaceholders
      TechPackIDE tpIDE = (TechPackIDE) this.application;
      List<BusyHourData> bhData = tpIDE.getDataModelController().getBusyhourHandlingDataModel()
          .getBusyHourDataByPlaceholderType(measurementtypeExt.getVersionid(), measurementtypeExt.getTypename(),
              Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX);
      if (bhData != null && bhData.size() > 0) {
        for (int i = customPlaceholderValueNew; i < bhData.size(); i++) {
          BusyHourData bd = bhData.get(i);
          if (bd.getBusyhour().getEnable() > 0) {
            errorStrings.add(measurementtypeExt.getTypename() + ": Number of " + CUSTOMPLACEHOLDERS
                + " cannot be reduced to " + customPlaceholderValueNew
                + " as there are enabled busy hours more than that number.");
            break;
          } // end if
        } // end for
      } // end if
    } // end if (main if statement)

    // The total number of product + custom place holders must not exceed the
    // maximum allowed number of place holders.
    int numberOfPlaceHolders = customPlaceholderValueNew + productPlaceholderValueNew;
    if (numberOfPlaceHolders > Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS)
      errorStrings.add(measurementtypeExt.getTypename() + ": Total number of busy hour place holders "
          + numberOfPlaceHolders + " exceeds the maximum allowed number "
          + Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS + ".");
    // Number of product placeholders cannot be less than zero.
    if (productPlaceholderValueNew < 0) {
      errorStrings.add(measurementtypeExt.getTypename() + ": Number of " + PRODUCTPLACEHOLDERS
          + " cannot be less than 0.");
    }
    // Number of custom placeholders cannot be less than zero.
    if (customPlaceholderValueNew < 0) {
      errorStrings.add(measurementtypeExt.getTypename() + ": Number of " + CUSTOMPLACEHOLDERS
          + " cannot be less than 0.");
    }

    return errorStrings;
  }
}