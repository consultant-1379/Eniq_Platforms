package com.distocraft.dc5000.etl.engine.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;

public class DWHMVersionUpdateAction extends SQLOperation {

  private final Logger log;
  
  private final RockFactory etlreprock;
  private final RockFactory dwhreprock;
  private final RockFactory dwhrock;

  private final String tpName;
  private String Start_Date;
  
  private String End_Date;
  
  private SimpleDateFormat sdf;
  
  public DWHMVersionUpdateAction(final Meta_versions version, final Long techPackId, final Meta_collections set, final Long transferActionId,
  		final Long transferBatchId, final Long connectId, final RockFactory rockFact, final ConnectionPool connectionPool,
      final Meta_transfer_actions trActions, final Logger clog) throws EngineMetaDataException {

    super(version, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.etlreprock = rockFact;
    this.log = clog;
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {

      final Meta_databases md_cond = new Meta_databases(etlreprock);
      md_cond.setType_name("USER");
      final Meta_databasesFactory md_fact = new Meta_databasesFactory(etlreprock, md_cond);

      RockFactory dwhreprock = null;
      RockFactory dwhrock = null;
      
      for (Meta_databases db : md_fact.get()) {
      
        if (db.getConnection_name().equalsIgnoreCase("dwhrep")) {
          dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db
              .getDriver_name(), "DWHMgr", true);
        } else if (db.getConnection_name().equalsIgnoreCase("dwh")) {
          dwhrock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db.getDriver_name(),
              "DWHMgr", true);
        }

      } // for each Meta_databases

      if (dwhrock == null || dwhreprock == null) {
        throw new Exception("Database (dwh or dwhrep) is not defined in Meta_databases");
      } else {
      	this.dwhreprock = dwhreprock;
      	this.dwhrock = dwhrock;
      }

      final Meta_collection_sets mcs_cond = new Meta_collection_sets(etlreprock);
      mcs_cond.setCollection_set_id(techPackId);
      final Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(etlreprock, mcs_cond);

      final Meta_collection_sets tp = mcs_fact.get().get(0);

      tpName = tp.getCollection_set_name();

      if (tpName == null) {
        throw new Exception("Unable to resolve TP name");
      } 
        
    } catch (Exception e) {
      // Failure -> cleanup connections

      try {
        this.dwhreprock.getConnection().close();
      } catch (Exception se) {
      }

      try {
        this.dwhrock.getConnection().close();
      } catch (Exception ze) {
      }

      throw new EngineMetaDataException("Init error: " + e.getMessage(), e, "constructor");
    }

  }

  private void printLogDDC(String date,String techpackName,String Status)
  {
	  String ddcStringToPrint="";
	  ddcStringToPrint = "DDC:VersionUpdateAction:"+techpackName+":"+Status+":"+date;
	  log.info(ddcStringToPrint);
  }
  
  private void printTotalTimeTaken(long startTime,long endTime)
  {
	  long totalTimeInMillis = endTime-startTime;
	  String totalTimeToPrint="DDC:VersionUpdateAction:"+tpName+":totalTime:" +totalTimeInMillis+" millisec";
	  log.info(totalTimeToPrint);
  }

  public void execute() throws Exception {

    log.fine("Version update executing for techpack " + tpName);
    long startTimeInMillis=System.currentTimeMillis();
    Start_Date = sdf.format(startTimeInMillis);
    printLogDDC(Start_Date,tpName,"START");

    try {

    	final Class<?> c = Class.forName("com.distocraft.dc5000.dwhm.VersionUpdateAction");
			final Class<?>[] parameterTypes = { RockFactory.class, RockFactory.class, String.class, Logger.class };

			final Object args[] = { dwhreprock,dwhrock,tpName,log };

			final Constructor<?> cont = c.getConstructor(parameterTypes);

			final Object action = cont.newInstance(args);

			final Method execute = c.getMethod("execute");

			execute.invoke(action);
    	
			PhysicalTableCache.getCache().revalidate();

			long endTimeInMillis=System.currentTimeMillis();
		    End_Date = sdf.format(endTimeInMillis);
		    printLogDDC(End_Date,tpName,"END");
		    printTotalTimeTaken(startTimeInMillis,endTimeInMillis);


    } finally {
      try {
        dwhreprock.getConnection().close();
      } catch (Exception e) {
      }

      try {
        dwhrock.getConnection().close();
      } catch (Exception e) {
      }
    }

  }

}
