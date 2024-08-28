package com.ericsson.eniq.techpacksdk.datamodel;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.view.generalInterface.ActivateInterface;
import com.ericsson.eniq.techpacksdk.view.newInterface.NewInterfaceDataModel;

@SuppressWarnings("serial")
public class InterfaceTreeDataModel extends DefaultTreeModel implements DataModel {

  RockFactory etlRock;

  String serverName;

  private static final Logger logger = Logger.getLogger(InterfaceTreeDataModel.class.getName());

  DataModelController dataModelController = null;

  public InterfaceTreeDataModel(RockFactory etlRock, final String serverName) {
    super(null);
    this.etlRock = etlRock;
    this.serverName = serverName;
    this.refresh();

  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public void refresh() {
    this.setRoot(createTree());
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  private DefaultMutableTreeNode createTree() {

    Vector<String> interfaceNames = null;

    DefaultMutableTreeNode root = new DefaultMutableTreeNode(serverName);

    interfaceNames = getAllUniqueInterfaces();

    Collections.sort(interfaceNames);

    for (int i = 0; i < interfaceNames.size(); i++) {

      Iterator iter = createVersion(interfaceNames.elementAt(i)).iterator();

      while (iter.hasNext()) {

        DefaultMutableTreeNode t = (DefaultMutableTreeNode) iter.next();
        root.add(t);
      }

    }

    return root;
  }

  private Vector<String> getAllUniqueInterfaces() {

    Vector<String> interfaceName = new Vector<String>();

    try {

      Datainterface m = new Datainterface(etlRock);
      DatainterfaceFactory mF = new DatainterfaceFactory(etlRock, m, true);
      Iterator iter = mF.get().iterator();

      while (iter.hasNext()) {

        Datainterface d = (Datainterface) iter.next();
        if (!interfaceName.contains(d.getInterfacename())) {
          interfaceName.add(d.getInterfacename());
        }
      }

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return interfaceName;
  }

  private Vector<Datainterface> getAllVersions(String interfaceName) {

    Vector<Datainterface> theInterfaces = new Vector<Datainterface>();

    try {

      Datainterface m = new Datainterface(etlRock, true);
      m.setInterfacename(interfaceName);
      DatainterfaceFactory mF = new DatainterfaceFactory(etlRock, m, true);
      theInterfaces = mF.get();

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return theInterfaces;
  }

  private DefaultMutableTreeNode createInterface(String interfaceName) {
    return new DefaultMutableTreeNode(interfaceName);
  }

  private Vector<Object> createVersion(String interfaceName) {

    Vector<Datainterface> vec = getAllVersions(interfaceName);
    Vector<Object> result = new Vector<Object>();

    DefaultMutableTreeNode version = createInterface(interfaceName);

    Iterator<Datainterface> iter = vec.iterator();
    while (iter.hasNext()) {

      Datainterface v = iter.next();

      boolean active = false;
      String locked = null;

      if (v != null && v.getStatus() == 1) {
        active = true;
      }

      if (v != null && v.getLockedby() != null) {
        locked = v.getLockedby().trim();
      }

      // remove double parenthesis from buildnumber
      String show = v.getInterfaceversion();
      show = show.replace("((", "");
      show = show.replace("))", "");

      // Create the child nodes
      DefaultMutableTreeNode parameterNode = new DefaultMutableTreeNode(new DataTreeNode(v.getInterfacename() + ":"
          + show, v, active, locked));

      // Connect the nodes
      version.add(parameterNode);

    }

    result.add(version);

    return result;
  }

  public void save() {

  }

  public boolean validateNew(RockDBObject rObj) {
    return true;
  }

  public boolean validateDel(RockDBObject rObj) {
    return true;
  }

  public boolean validateMod(RockDBObject rObj) {
    return true;
  }

  public boolean newObj(RockDBObject rObj) {
    try {
      rObj.insertDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean delObj(RockDBObject rObj) {
    try {
      rObj.deleteDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean modObj(RockDBObject rObj) {
    RockDBObject[] a = new RockDBObject[1];
    a[0] = rObj;
    return modObj(a);
  }

  public boolean modObj(RockDBObject[] rObj) {

    try {

      etlRock.getConnection().setAutoCommit(false);

      for (int i = 0; i < rObj.length; i++) {
        rObj[i].updateDB();
      }

      dataModelController.rockObjectsModified(this);

      etlRock.getConnection().commit();

    } catch (Exception e) {
      try {
        etlRock.getConnection().rollback();
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        etlRock.getConnection().setAutoCommit(true);
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
    }

    return true;
  }

  public boolean updated(DataModel dataModel) {
    if (dataModel instanceof NewInterfaceDataModel) {
      refresh();
      return true;
    }

    if (dataModel instanceof DWHTreeDataModel) {
      refresh();
      return true;
    }
    return false;
  }

  public void migrate(String name, String version, String level, String rstate) throws Exception {

    Datainterface m = new Datainterface(etlRock, true);
    m.setInterfacename(name);
    m.setInterfaceversion(version);
    DatainterfaceFactory mF = new DatainterfaceFactory(etlRock, m, true);
    Datainterface theInterface = (Datainterface) mF.get().elementAt(0);

    theInterface.setEniq_level(level);
    theInterface.setRstate(rstate);
    theInterface.updateDB();

  }

  public boolean isAnyInterfaceActive(String name) throws Exception {
    Datainterface m = new Datainterface(etlRock, true);
    m.setStatus(1l);
    m.setInterfacename(name);
    DatainterfaceFactory mF = new DatainterfaceFactory(etlRock, m, true);
    return mF.get().size() != 0;
  }

  /**
   * Deactivates an interface.
   * 
   * @param i
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void deactivateInterface(Datainterface i) throws Exception {

    i.setStatus(new Long(0));
    i.saveDB();

    Interfacemeasurement im_cond = new Interfacemeasurement(dataModelController.getRockFactory());

    im_cond.setInterfacename(i.getInterfacename());
    im_cond.setInterfaceversion(i.getInterfaceversion());

    InterfacemeasurementFactory imf = new InterfacemeasurementFactory(dataModelController.getRockFactory(), im_cond);

    Iterator iter = imf.get().iterator();

    while (iter.hasNext()) {

      Interfacemeasurement im = (Interfacemeasurement) iter.next();
      im.deleteDB();
      // im.setStatus(new Long (0));
      // im.saveDB();

    }

    // Disable the meta collection sets, and its meta collections and meta
    // transfer actions.
    //
    // From IDE, the user can only activate the "template" interface (for
    // example: 'INTF_DC_E_CNAXE_OMS_TRAR'). From the command line, the
    // user can activate the actual interface (for example:
    // 'INTF_DC_E_CNAXE_OMS_TRAR-eniq_oss_1'). This means there might be two
    // meta collection sets enabled for "the same interface". All sets will be
    // disabled.

    // Get all the meta collection sets
    Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(), mcs);

    if (mcsF != null && mcsF.size() > 0) {

      // Iterate through all the meta collection sets and check for the match
      // for the template interface name or the real interface name. Interface
      // version is not used as a criteria, since the real interface activated
      // from the command line may have an older version than the template
      // version being de-activated now. All the matching sets are disabled.
      Iterator<Meta_collection_sets> mcsFI = mcsF.get().iterator();
      while (mcsFI.hasNext()) {
        Meta_collection_sets metacs = mcsFI.next();
        if (metacs.getCollection_set_name().equals(i.getInterfacename())
            || metacs.getCollection_set_name().contains(i.getInterfacename() + "-")) {
          // Match found. Disable the meta collection set.
          logger.log(Level.FINE, "Disabling meta collection set: " + metacs.getCollection_set_name()
              + " with interface version:" + metacs.getVersion_number() + ".");
          setEnableFlagForMetaCollectionSet(metacs.getCollection_set_name(), metacs.getVersion_number(), "N");
        } 
      }
    }
  }

  /**
   * Sets the enabled flag for the meta collection set and its meta collections
   * and meta transfer actions.
   * 
   * @param metaCollectionSetName
   * @param interfaceVersion
   * @param enabledFlag
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void setEnableFlagForMetaCollectionSet(String metaCollectionSetName, String interfaceVersion,
      String enabledFlag) throws Exception {
    Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
    mcs.setCollection_set_name(metaCollectionSetName);
    mcs.setVersion_number(interfaceVersion);
    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(), mcs);

    if (mcsF != null && mcsF.size() > 0) {

      Meta_collection_sets metacs = (Meta_collection_sets) mcsF.getElementAt(0);

      Meta_collections mc = new Meta_collections(dataModelController.getEtlRockFactory());
      mc.setCollection_set_id(metacs.getCollection_set_id());
      mc.setVersion_number(interfaceVersion);
      Meta_collectionsFactory mcF = new Meta_collectionsFactory(dataModelController.getEtlRockFactory(), mc);

      Iterator<Meta_collections> mcFI = mcF.get().iterator();
      while (mcFI.hasNext()) {
        Meta_collections metac = mcFI.next();
        metac.setEnabled_flag(enabledFlag);
        metac.saveDB();

        // action
        Meta_transfer_actions mta = new Meta_transfer_actions(dataModelController.getEtlRockFactory());
        mta.setCollection_set_id(metacs.getCollection_set_id());
        mta.setVersion_number(interfaceVersion);
        Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(dataModelController.getEtlRockFactory(),
            mta);

        Iterator<Meta_transfer_actions> mtaFI = mtaF.get().iterator();
        while (mtaFI.hasNext()) {
          Meta_transfer_actions action = mtaFI.next();
          action.setEnabled_flag(enabledFlag);
          action.saveDB();
        }
      }

      metacs.setEnabled_flag(enabledFlag);
      metacs.saveDB();

    }

  }

  /**
   * Activates an interface.
   * 
   * @param i
   * @throws Exception
   */
  public void activateInterface(Datainterface i) throws Exception {

    i.setStatus(new Long(1));
    modObj(i);

    ActivateInterface ai = new ActivateInterface(i.getInterfacename(), i.getInterfaceversion(), dataModelController
        .getRockFactory());

    ai.activateInterface();

    // Enable the meta collection set, and its meta collections and meta
    // transfer actions.
    logger.log(Level.FINE, "Enabling meta collection set: " + i.getInterfacename() + " with interface version:"
        + i.getInterfaceversion() + ".");
    setEnableFlagForMetaCollectionSet(i.getInterfacename(), i.getInterfaceversion(), "Y");

  }

}
