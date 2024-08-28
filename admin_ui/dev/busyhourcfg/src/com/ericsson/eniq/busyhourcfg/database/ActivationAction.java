/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.scheduler.ISchedulerRMI;
import com.distocraft.dc5000.etl.scheduler.SchedulerConnect;
import com.ericsson.eniq.busyhourcfg.config.BusyhourProperties;
import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;
import com.ericsson.eniq.busyhourcfg.database.UpdateResult.ResultType;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DefaultDWHStorageTimeAction;
import com.ericsson.eniq.repository.ETLCServerProperties;


/**
 * @author eheijun
 *
 */
public class ActivationAction implements Callable<UpdateResult> {
  
  private final Logger log = Logger.getLogger(ActivationAction.class.getName());
  
  private final ETLCServerProperties etlcServerProperties;
  
  private final BusyhourProperties busyhourProperties;

  private final TechPack techPack;
  
  /**
   * @param etlcServerProperties
   * @param busyhourProperties
   */
  public ActivationAction(final ETLCServerProperties etlcServerProperties, final BusyhourProperties busyhourProperties, final TechPack techPack) {
    this.etlcServerProperties = etlcServerProperties;
    this.busyhourProperties = busyhourProperties;
    this.techPack = techPack;
  }

  /**
   * Recreate placeholder views of the Techpack
   * @return true if operation was successfull otherwise false
   */
  @Override
  public UpdateResult call() throws Exception {
    try {
      final DatabaseConnector databaseConnector = DatabaseConnectorFactory.getDatabaseConnector(etlcServerProperties, busyhourProperties);
      log.fine("Database connector created successfully");
      try {
        final RockDatabaseSession rockDatabaseSession = (RockDatabaseSession) databaseConnector.createDatabaseSession(DatabaseConnectorFactory.getApplicationName());
        log.fine("Database session created successfully");
        try {
          final DWHStorageTimeAction dwhStorageTimeAction = new DefaultDWHStorageTimeAction(rockDatabaseSession.getDwhRockFactory(), rockDatabaseSession.getDwhrepRockFactory());
          final BusyhourUpdater busyhourUpdater = new RockBusyhourUpdater(rockDatabaseSession, dwhStorageTimeAction);
          busyhourUpdater.recreateViews(techPack);
          ISchedulerRMI sched = SchedulerConnect.connectScheduler();
          sched.reload();
          return new UpdateResult("Recreation of the criteria successfully done.", ResultType.INFO);
        } finally {
          rockDatabaseSession.clear();
          log.fine("Database session cleared successfully");
        }
      } finally {
        DatabaseConnectorFactory.setDatabaseConnector(null);
        log.fine("Database connector cleared successfully");
      }
    } catch (Exception e) {
      final String errormessage = "Recreation of the criteria failed: " + e.getMessage() + ".";
      log.severe(errormessage);
      return new UpdateResult(errormessage, ResultType.ERROR);
    }
  }

}
