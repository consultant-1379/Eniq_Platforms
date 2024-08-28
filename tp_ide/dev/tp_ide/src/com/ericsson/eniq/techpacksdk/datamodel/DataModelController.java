package com.ericsson.eniq.techpacksdk.datamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.techpacksdk.User;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingFactory;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourTreeModel;
import com.ericsson.eniq.techpacksdk.view.dataFormat.DataformatDataModel;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLFactory;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLSetHandlingDataModel;
import com.ericsson.eniq.techpacksdk.view.generalInterface.GeneralInterfaceDataModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.ExternalStatementDataModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.TechPackDependencyDataModel;
import com.ericsson.eniq.techpacksdk.view.group.GroupTypeDataModel;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeDataModel;
import com.ericsson.eniq.techpacksdk.view.newInterface.NewInterfaceDataModel;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackDataModel;
import com.ericsson.eniq.techpacksdk.view.prompts.PromptImplementorDataModel;
import com.ericsson.eniq.techpacksdk.view.reference.ReferenceTypeDataModel;
import com.ericsson.eniq.techpacksdk.view.transformer.TransformerDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseClassDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseComputedObjectDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseConditionDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseFormulasDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseJoinDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseObjectDataModel;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseTablesDataModel;
import com.ericsson.eniq.techpacksdk.view.verification.VerificationConditionDataModel;
import com.ericsson.eniq.techpacksdk.view.verification.VerificationObjectDataModel;

/**
 * The controller class for all the data models for the views in TechPackIDE.
 * 
 */
public class DataModelController {

  /**
   * Used for keeping track of dependencies between the models.
   * 
   */
  private class DataModelDepedencies {

    DataModel dataModel;

    private final List<DataModel> listOfDepedencies = new ArrayList<DataModel>();

    public DataModelDepedencies(final DataModel dataModel, final DataModel depedencyDataModel) {
      this.dataModel = dataModel;
      this.listOfDepedencies.add(depedencyDataModel);
    }

    public DataModel getDataModel() {
      return dataModel;
    }

    public List<DataModel> getDepedensies() {
      return listOfDepedencies;
    }
  }

  private final RockFactory dwhrepRock;

  private final RockFactory etlrepRock;

  private final RockFactory dwhRock;

  private final RockFactory dbadwhRock;

  private final Engine engine;

  private final Scheduler scheduler;

  private final String servername;

  private final User user;

  private final File workingDir;

    private BusyhourTreeModel busyHourTreeModel = null;
  private final TechPackTreeDataModel techPackTreeDataModel;

  private final TechPackDataModel techPackDataModel;

  private final MeasurementTypeDataModel measurementTypeDataModel;

  // private final BusyhourHandlingDataModel busyhourDataModel;

  private final ReferenceTypeDataModel referenceTypeDataModel;

  private final GroupTypeDataModel groupTypeDataModel;

  private final TransformerDataModel transformerDataModel;

  private final PromptImplementorDataModel promptImplementorTypeDataModel;

  private final InterfaceTreeDataModel interfaceTreeDataModel;

  private final NewInterfaceDataModel newInterfaceDataModel;

  private final NewTechPackDataModel newTechPackDataModel;

  private final GeneralInterfaceDataModel editInterfaceDataModel;

  private final GeneralTechPackDataModel editGeneralTechPackDataModel;

  private final DWHTreeDataModel DWHTreeDataModel;

  private final ExternalStatementDataModel externalStatementDataModel;

  private TechPackDependencyDataModel techPackDependencyDataModel;

  private GeneralTechPackDataModel vendorReleasesDataModel;

  private final UniverseObjectDataModel universeObjectDataModel;

  private final UniverseComputedObjectDataModel universeComputedObjectDataModel;

  private final UniverseConditionDataModel universeConditionDataModel;

  private final UniverseTablesDataModel universeTablesDataModel;

  private final UniverseJoinDataModel universeJoinDataModel;

  private final UniverseClassDataModel universeClassDataModel;

  private final UniverseFormulasDataModel universeFormulasDataModel;

  private final VerificationObjectDataModel verificationObjectDataModel;

  private final VerificationConditionDataModel verificationConditionDataModel;

  private final ETLFactory etlFactory;

  private final BusyhourHandlingFactory busyhourHandlingFactory;

  private final ETLSetHandlingDataModel etlSetHandlingDataModel;

  private final BusyhourHandlingDataModel busyhourHandlingDataModel;

  private final DataformatDataModel dataformatDataModel;

  private final List<DataModelDepedencies> listOfDataModels;

  private final ResourceMap resourceMap;

  /**
   * Constructor.
   * 
   * @param application
   * @param dwhrepRock
   * @param etlrepRock
   * @param dwhRock
   * @param dbadwhRock
   * @param user
   * @param workingDir
   * @param engServerName
   * @param schServerName
   * @param serverName
   * @param rmiport
   * @param engRef
   * @param schRef
   * @throws Exception
   */
  public DataModelController(final Application application, final RockFactory dwhrepRock, final RockFactory etlrepRock,
      final RockFactory dwhRock, final RockFactory dbadwhRock, final User user, final File workingDir,
      final String engServerName, final String schServerName, final String serverName, final int rmiport, final String engRef, final String schRef) throws Exception {

    this.dwhrepRock = dwhrepRock;
    this.etlrepRock = etlrepRock;
    this.dwhRock = dwhRock;
    this.dbadwhRock = dbadwhRock;
    this.engine = initEngine(engServerName, rmiport, engRef);
    this.scheduler = initScheduler(schServerName, rmiport, schRef);
    this.user = user;
    this.workingDir = workingDir;
    this.servername = serverName;
    this.resourceMap = application.getContext().getResourceMap();

    // Create a list for data model dependencies.
    listOfDataModels = new ArrayList<DataModelDepedencies>();

    //
    // TECHPACK
    //

    // Create the techpack tree model and set the reference to this controller.
    techPackTreeDataModel = new TechPackTreeDataModel(dwhrepRock, serverName);
    techPackTreeDataModel.setDataModelController(this);

    // Create the techpack data model and set the reference to this controller.
    techPackDataModel = new TechPackDataModel(dwhrepRock);
    techPackDataModel.setDataModelController(this);

    // Create the new techpack model and set the reference to this controller.
    newTechPackDataModel = new NewTechPackDataModel(dwhrepRock, serverName);
    newTechPackDataModel.setDataModelController(this);

    // Create the edit techpack model.
    editGeneralTechPackDataModel = new GeneralTechPackDataModel(dwhrepRock);

    // Create the models for the techpack views.
    // busyhourDataModel = new BusyhourHandlingDataModel(dwhrepRock, this);
    busyhourHandlingDataModel = new BusyhourHandlingDataModel(dwhrepRock, this);
    busyhourHandlingFactory = new BusyhourHandlingFactory(application, busyhourHandlingDataModel);
    dataformatDataModel = new DataformatDataModel(dwhrepRock, this);
    etlSetHandlingDataModel = new ETLSetHandlingDataModel(etlrepRock);
    etlFactory = new ETLFactory(etlSetHandlingDataModel);
    externalStatementDataModel = new ExternalStatementDataModel(dwhrepRock);
    externalStatementDataModel.setDataModelController(this);
    measurementTypeDataModel = new MeasurementTypeDataModel(dwhrepRock);
    promptImplementorTypeDataModel = new PromptImplementorDataModel(dwhrepRock);
    referenceTypeDataModel = new ReferenceTypeDataModel(dwhrepRock);
    transformerDataModel = new TransformerDataModel(dwhrepRock);
    universeClassDataModel = new UniverseClassDataModel(dwhrepRock);
    universeClassDataModel.setDataModelController(this);
    universeComputedObjectDataModel = new UniverseComputedObjectDataModel(dwhrepRock);
    universeComputedObjectDataModel.setDataModelController(this);
    universeConditionDataModel = new UniverseConditionDataModel(dwhrepRock);
    universeConditionDataModel.setDataModelController(this);
    universeFormulasDataModel = new UniverseFormulasDataModel(dwhrepRock);
    universeFormulasDataModel.setDataModelController(this);
    universeJoinDataModel = new UniverseJoinDataModel(dwhrepRock);
    universeJoinDataModel.setDataModelController(this);
    universeObjectDataModel = new UniverseObjectDataModel(dwhrepRock);
    universeObjectDataModel.setDataModelController(this);
    universeTablesDataModel = new UniverseTablesDataModel(dwhrepRock);
    universeTablesDataModel.setDataModelController(this);
    verificationConditionDataModel = new VerificationConditionDataModel(dwhrepRock);
    verificationConditionDataModel.setDataModelController(this);
    verificationObjectDataModel = new VerificationObjectDataModel(dwhrepRock);
    verificationObjectDataModel.setDataModelController(this);
    groupTypeDataModel = new GroupTypeDataModel(dwhrepRock);

    // Create the dependencies between the data models.
    listOfDataModels.add(new DataModelDepedencies(busyhourHandlingDataModel, measurementTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(busyhourHandlingDataModel, techPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(dataformatDataModel, dataformatDataModel));
    listOfDataModels.add(new DataModelDepedencies(dataformatDataModel, measurementTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(dataformatDataModel, referenceTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(editGeneralTechPackDataModel, measurementTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(etlSetHandlingDataModel, busyhourHandlingDataModel));
    listOfDataModels.add(new DataModelDepedencies(etlSetHandlingDataModel, newTechPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(measurementTypeDataModel, busyhourHandlingDataModel));
    listOfDataModels.add(new DataModelDepedencies(measurementTypeDataModel, editGeneralTechPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(measurementTypeDataModel, techPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(measurementTypeDataModel, universeComputedObjectDataModel));
    listOfDataModels.add(new DataModelDepedencies(measurementTypeDataModel, universeConditionDataModel));
    listOfDataModels.add(new DataModelDepedencies(promptImplementorTypeDataModel, techPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(referenceTypeDataModel, measurementTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(referenceTypeDataModel, techPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(techPackDataModel, techPackTreeDataModel));
    listOfDataModels.add(new DataModelDepedencies(techPackTreeDataModel, newTechPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(transformerDataModel, dataformatDataModel));
    listOfDataModels.add(new DataModelDepedencies(transformerDataModel, techPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(universeComputedObjectDataModel, measurementTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(universeComputedObjectDataModel, universeFormulasDataModel));
    listOfDataModels.add(new DataModelDepedencies(universeConditionDataModel, measurementTypeDataModel));
    listOfDataModels.add(new DataModelDepedencies(universeFormulasDataModel, universeComputedObjectDataModel));

    //  
    // DWH
    //

    // Create the DWH tree model.
    DWHTreeDataModel = new DWHTreeDataModel(dwhrepRock);

    // Create the dependencies between the data models.
    listOfDataModels.add(new DataModelDepedencies(DWHTreeDataModel, newTechPackDataModel));
    listOfDataModels.add(new DataModelDepedencies(DWHTreeDataModel, techPackDataModel));

    //
    // INTERFACE
    //

    // Create the interface tree model and set the reference to this controller.
    interfaceTreeDataModel = new InterfaceTreeDataModel(dwhrepRock, serverName);
    interfaceTreeDataModel.setDataModelController(this);

    // Create the new interface model and set the reference to this controller.
    newInterfaceDataModel = new NewInterfaceDataModel(dwhrepRock, serverName, resourceMap);
    newInterfaceDataModel.setDataModelController(this);

    // Create the edit interface model and set the reference to this controller.
    editInterfaceDataModel = new GeneralInterfaceDataModel(dwhrepRock, serverName, resourceMap);
    editInterfaceDataModel.setDataModelController(this);

    // Create the dependencies between the data models.
    listOfDataModels.add(new DataModelDepedencies(interfaceTreeDataModel, DWHTreeDataModel));
    listOfDataModels.add(new DataModelDepedencies(interfaceTreeDataModel, newInterfaceDataModel));

  }

    protected Engine initEngine(final String serverName, final int rmiport, final String engRef){
        return new Engine(serverName, rmiport, engRef);
    }
    protected Scheduler initScheduler(final String serverName, final int rmiport, final String schRef){
        return new Scheduler(serverName, rmiport, schRef);
    }

  public RockFactory getRockFactory() {
    return dwhrepRock;
  }

  public RockFactory getEtlRockFactory() {
    return etlrepRock;
  }

  public RockFactory getDwhRockFactory() {
    return dwhRock;
  }

  public RockFactory getDbaDwhRockFactory() {
    return dbadwhRock;
  }

  public Engine getEngine() {
    return engine;
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  public void setAndWaitActiveExecutionProfile(final String profileName) {
    try {
      if (engine != null) {
        engine.setAndWaitActiveExecutionProfile(profileName);
      }
    } catch (Exception e) {
      new Exception("Could not change active profile in engine");
    }
  }

  public void schedulerReload() throws Exception {
    try {
      if (scheduler != null) {
        if (scheduler != null) {
          scheduler.reload();
        }
      }
    } catch (Exception e) {
      new Exception("Could not reload the scheduler");
    }
  }

  public String getUserName() {
    return user.getName();
  }

  public User getUser() {
    return user;
  }

  public ResourceMap getResourceMap() {
    return resourceMap;
  }

  public File getWorkingDir() {
    return this.workingDir;
  }

  public String getServerName() {
    return this.servername;
  }


    public void setBusyHourTreeModel(final BusyhourTreeModel model){
        busyHourTreeModel = model;
    }
    public BusyhourTreeModel getBusyhourTreeModel(){
        return busyHourTreeModel;
    }
  public InterfaceTreeDataModel getInterfaceTreeDataModel() {
    return interfaceTreeDataModel;
  }

  public TechPackTreeDataModel getTechPackTreeDataModel() {
    return techPackTreeDataModel;
  }

  public DWHTreeDataModel getDWHTreeDataModel() {
    return DWHTreeDataModel;
  }

  public TechPackDataModel getTechPackDataModel() {
    return techPackDataModel;
  }

  public MeasurementTypeDataModel getMeasurementTypeDataModel() {
    return measurementTypeDataModel;
  }

  public ReferenceTypeDataModel getReferenceTypeDataModel() {
    return referenceTypeDataModel;
  }

  public GroupTypeDataModel getGroupTypeDataModel() {
    return groupTypeDataModel;
  }

  public TransformerDataModel getTransformerDataModel() {
    return transformerDataModel;
  }

  public PromptImplementorDataModel getPromptImplementorTypeDataModel() {
    return promptImplementorTypeDataModel;
  }

  public UniverseTablesDataModel getUniverseTablesDataModel() {
    return universeTablesDataModel;
  }

  public UniverseObjectDataModel getUniverseObjectDataModel() {
    return universeObjectDataModel;
  }

  public UniverseComputedObjectDataModel getUniverseComputedObjectDataModel() {
    return universeComputedObjectDataModel;
  }

  public UniverseConditionDataModel getUniverseConditionDataModel() {
    return universeConditionDataModel;
  }

  public UniverseJoinDataModel getUniverseJoinDataModel() {
    return universeJoinDataModel;
  }

  public UniverseClassDataModel getUniverseClassDataModel() {
    return universeClassDataModel;
  }

  public UniverseFormulasDataModel getUniverseFormulasDataModel() {
    return universeFormulasDataModel;
  }

  public NewInterfaceDataModel getNewInterfaceDataModel() {
    return newInterfaceDataModel;
  }

  public ExternalStatementDataModel getExternalStatementDataModel() {
    return externalStatementDataModel;
  }

  public TechPackDependencyDataModel getTechPackDependencyDataModel() {
    return techPackDependencyDataModel;
  }

  public GeneralTechPackDataModel getVendorReleasesDataModel() {
    return vendorReleasesDataModel;
  }

  public NewTechPackDataModel getNewTechPackDataModel() {
    return newTechPackDataModel;
  }

  public GeneralInterfaceDataModel getEditInterfaceDataModel() {
    return editInterfaceDataModel;
  }

  public GeneralTechPackDataModel getEditGeneralTechPackDataModel() {
    return editGeneralTechPackDataModel;
  }

  public VerificationObjectDataModel getVerificationObjectDataModel() {
    return verificationObjectDataModel;
  }

  public VerificationConditionDataModel getVerificationConditionDataModel() {
    return verificationConditionDataModel;
  }

  public ETLFactory getETLFactory() {
    return etlFactory;
  }

  public BusyhourHandlingFactory getBusyhourHandlingFactory() {
    return busyhourHandlingFactory;
  }

  public ETLSetHandlingDataModel getETLSetHandlingDataModel() {
    return etlSetHandlingDataModel;
  }

  public BusyhourHandlingDataModel getBusyhourHandlingDataModel() {
    return busyhourHandlingDataModel;
  }

  public DataformatDataModel getDataformatDataModel() {
    return dataformatDataModel;
  }

  /**
   * Notify all depending data models that the data model has changed. The
   * updated() method in the depending models will be called.
   * 
   * @param dm
   *          The changed data model.
   * @throws Exception
   */
  public void rockObjectsModified(final DataModel dm) throws Exception {

    // loop all dataModels
    for (int dmi = 0; dmi < listOfDataModels.size(); dmi++) {
      // check if dataModel is interested in given dataModel dm
      final DataModelDepedencies dmd = (listOfDataModels.get(dmi));
      for (int i = 0; i < dmd.getDepedensies().size(); i++) {
        final DataModel ddm = dmd.getDepedensies().get(i);
        if (ddm != null && ddm.getClass().equals(dm.getClass())) {
          dmd.getDataModel().updated(dm);
        }
      }
    }
  }

  public void migrate(final String versionid) throws Exception {

  }

}