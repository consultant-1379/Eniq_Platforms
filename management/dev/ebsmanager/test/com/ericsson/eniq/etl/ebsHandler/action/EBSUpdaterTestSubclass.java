package com.ericsson.eniq.etl.ebsHandler.action;

import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.ericsson.eniq.common.setWizards.SetCreator;
import com.ericsson.eniq.etl.ebsHandler.setCreators.stubs.CreateAggregatorSetStub;
import com.ericsson.eniq.etl.ebsHandler.setCreators.stubs.CreateLoaderSetStub;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import ssc.rockfactory.RockFactory;

/**
 * A subclass for testing the class EBSUpdateAction. This class overrides the
 * superclasses methods that create dependencies to other classes. These classes
 * include:
 * <p/>
 * RockFactory,
 * CreateAggregatorSet, and
 * CreateLoaderSet.
 * <p/>
 * The instantiation of these classes in EBSUpdateAction is replaced by
 * instantiation of corresponding stub classes in this subclass. Also, the
 * technology package creation code has been overridden with a mock version.
 *
 * @author epiituo
 */
public class EBSUpdaterTestSubclass extends EBSUpdater {

  private final Vector<CreateAggregatorSetStub> createAggregatorSetStubs;
  private final Vector<CreateLoaderSetStub> createLoaderSetStubs;
  protected boolean schedulerReset = false;
  protected boolean engineReset = false;

  /**
   * @param properties .
   * @param rockFact   .
   * @param logger     .
   * @throws Exception .
   */
  public EBSUpdaterTestSubclass(final Properties properties, final RockFactory rockFact, final Logger logger) throws Exception {

    super(properties, rockFact, logger);

    this.createAggregatorSetStubs = new Vector<CreateAggregatorSetStub>();
    this.createLoaderSetStubs = new Vector<CreateLoaderSetStub>();

  }

  @Override
  protected void resetScheduler() {
    schedulerReset = true;
  }

  @Override
  protected void resetEngine() {
    engineReset = true;
  }

  @Override
  protected String getLockUserColumnReturnName() {
    return "@p0";
  }

  @Override
  protected String getLockUserSPCommand(final String lockType, final String user) {
    return "{call " + lockType + "_user('" + user + "')}";
  }

  @Override
  protected String getDropConnectionsSPCommand(final String user) {
    return "{call drop_user_connections('" + user + "')}";
  }

  @Override
  protected VersionUpdateAction getVersionUpdateAction(final String tpName) throws Exception {
    return new VersionUpdateAction(dwhRepRockFactory, dwhRockFactory, tpName, log) {
      @Override
      protected String getTableSearchFilter(final String tableName) {
        return "select count(table_name) as tableCount from INFORMATION_SCHEMA.SYSTEM_TABLES where table_name = '" + tableName + "'";
      }
    };
  }/*
	 * (non-Javadoc)
	 * @see com.distocraft.dc5000.etl.engine.system.EBSUpdateAction#getTechPack(ssc.rockfactory.RockFactory, java.lang.Long)
	 */

  @Override
  protected Meta_collection_sets getTechPack(final RockFactory etlRepRock, final Long techPackId) {
    final Meta_collection_sets result = new Meta_collection_sets(etlRepRock);
    result.setCollection_set_name("Techpack");
    result.setVersion_number("1.0");
    result.setCollection_set_id((long) 5);
    return result;
  }

  /*
    * (non-Javadoc)
    * @see com.distocraft.dc5000.etl.engine.system.EBSUpdateAction#getLoaderSetCreator(java.lang.String, java.lang.String, java.lang.String, java.lang.String, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, int, java.lang.String, boolean)
    */
  @Override
  protected SetCreator getLoaderSetCreator(final String templateDir, final String setName, final String setVersion,
                                           final String versionId, final RockFactory dwhRepRock, final RockFactory etlRepRock,
                                           final int techPackId, final String techPackName, final boolean schedulings)
    throws Exception {

    final CreateLoaderSetStub result = new CreateLoaderSetStub(templateDir,
      setName, setVersion, versionId,
      dwhRepRock, etlRepRock, techPackName);

    // Store the created CreateLoaderSetStub object, for logging purposes
    this.createLoaderSetStubs.addElement(result);

    return result;
  }

  /*
    * (non-Javadoc)
    * @see com.distocraft.dc5000.etl.engine.system.EBSUpdateAction#getAggregatorSetCreator(java.lang.String, java.lang.String, java.lang.String, java.lang.String, ssc.rockfactory.RockFactory, ssc.rockfactory.RockFactory, int, boolean)
    */
  @Override
  protected SetCreator getAggregatorSetCreator(final String templateDir, final String setName, final String setVersion,
                                               final String versionId, final RockFactory dwhRepRock, final RockFactory etlRepRock,
                                               final int techPackId, final boolean schedulings)
    throws Exception {

    final CreateAggregatorSetStub result = new CreateAggregatorSetStub(
      templateDir, setName,
      setVersion, versionId,
      dwhRepRock, etlRepRock);

    this.createAggregatorSetStubs.addElement(result);

    return result;
  }

  /*
    * Returns an array of the CreateAggregatorSetStubs that have been created
    * by this object. This enables observing the method calls made to the
    * created stubs.
    */
  public CreateAggregatorSetStub[] getCreatedCreateAggregatorSetStubs() {
    final int objectsCreated = this.createAggregatorSetStubs.size();
    final CreateAggregatorSetStub[] result =
      new CreateAggregatorSetStub[objectsCreated];
    this.createAggregatorSetStubs.toArray(result);
    return result;
  }

  /*
    * Returns an array of the CreateLoaderSetStubs that have been created
    * by this object. This enables observing the method calls made to the
    * created stubs.
    */
  public CreateLoaderSetStub[] getCreatedCreateLoaderSetStubs() {
    final int objectsCreated = this.createLoaderSetStubs.size();
    final CreateLoaderSetStub[] result = new CreateLoaderSetStub[objectsCreated];
    this.createLoaderSetStubs.toArray(result);
    return result;
  }

}
