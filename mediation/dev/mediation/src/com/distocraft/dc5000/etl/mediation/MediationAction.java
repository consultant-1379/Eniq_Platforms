package com.distocraft.dc5000.etl.mediation;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.mediation.ftp.FTPErrorHandler;
import com.distocraft.dc5000.etl.mediation.ftp.FTPWorker;
import com.distocraft.dc5000.etl.mediation.stfp.SFTPErrorHandler;
import com.distocraft.dc5000.etl.mediation.stfp.SFTPWorker;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * MediationAction is used to transfer files<br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class MediationAction extends TransferActionBase {

  private Logger log = Logger.getLogger("mediator");

  private Logger flog = Logger.getLogger("mediator.file");

  private int maxWorkers = 1;

  private List workers = new ArrayList(5);

  private List tasks = new ArrayList(10);

  private Properties conf = null;

  private ErrorHandler errorHandler;

  public MediationAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact,
      Meta_transfer_actions trActions, SetContext setcontext) throws EngineMetaDataException {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);

    try {

      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);

      String techPack = collSet.getCollection_set_name();
      String set_type = collection.getSettype();
      String set_name = collection.getCollection_name();

      log = Logger.getLogger("etl." + techPack + "." + set_type + "." + set_name + ".mediator");
      flog = Logger.getLogger("file." + techPack + "." + set_type + "." + set_name + ".mediator");

    } catch (Exception e) {
      log.log(Level.WARNING, "Error initializing loggers", e);
    }

    String act_cont = trActions.getAction_contents();

    conf = new Properties();

    if (act_cont != null && act_cont.length() > 0) {

      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
        conf.load(bais);
        bais.close();
        log.finest("Configuration read");
      } catch (Exception e) {
        throw new EngineMetaDataException("Error reading config", new String[] { "" }, e, this, this.getClass()
            .getName());
      }
    }

  }

  public void execute() throws EngineException {

    try {
      FileTransferTask ftt = new FileTransferTask(conf);

      FileTransferTask[] fta = MaskFactory.parseMask(ftt);
      for (int i = 0; i < fta.length; i++) {
        tasks.add(fta[i]);
      }

      log.finer("Starting...");

      while (tasks.size() > 0 || isWorking()) {

        if (tasks.size() > 0) { // Task(s) to perform

          Worker w = null;

          // Seach for idle worker
          for (int i = 0; i < workers.size(); i++) {
            Worker x = (Worker) workers.get(i);
            if (!x.isReserved() && x.getError() == null) {
              w = x;
              break;
            }
          }

          if (w == null && workers.size() < maxWorkers) {
            log.fine("Unable to get free worker. Creating new.");
            w = createWorker();
          }

          if (w != null) {

            log.fine("Delegating work...");

            try {
              w.initialize((FileTransferTask) tasks.remove(0));
            } catch (NoFilesException nfe) {
              continue;
            } catch (UnrecovableException e) {
              throw new Exception("Mediation initialize failed.");
            } catch (Exception e) {
              FileTransferTask[] ftax = errorHandler.initializationError(e);
              if (ftax != null)
                addTasks(ftax);
              continue;
            }

            synchronized (w) {
              w.notifyAll();
            }

          } else {
            log.finer("No workers available.");
          }

          try {
            log.finest("Starting to sleep(3)");
            Thread.sleep(3000);
          } catch (InterruptedException ie) {
          }

        } else { // No more tasks. Just wait tasks to finish.

          try {
            log.finest("Starting to sleep(10)");
            Thread.sleep(10000);
          } catch (InterruptedException ie) {
          }

        }
      } // delegation loop

      log.fine("Delegation end. Killing workers...");

      Exception e = null;

      Iterator i = workers.iterator();
      while (i.hasNext()) {
        Worker w = (Worker) i.next();
        w.kill();
        synchronized (w) {
          w.notifyAll();
        }
        if (e == null && w.getError() != null)
          e = w.getError();
      }

      if (e != null)
        throw e;

    } catch (Exception e) {
      log.log(Level.WARNING, "Mediation failed exceptionally", e);
      throw new EngineException("Exception in Mediation", new String[] { "" }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_SYSTEM);
    }

  }

  private void addTasks(FileTransferTask[] ftt) {
    log.finer("Adding " + ftt.length + " tasks");

    for (int i = 0; i < ftt.length; i++)
      tasks.add(ftt[i]);
  }

  // Connetion FACTORY
  private Worker createWorker() throws Exception {

    String protocol = conf.getProperty("protocol");

    if ("ftp".equalsIgnoreCase(protocol)) {
      if (errorHandler == null)
        errorHandler = new FTPErrorHandler(conf);

      FTPWorker ftpw = new FTPWorker(conf, log, flog);
      ftpw.start();

      workers.add(ftpw);

      return ftpw;

    } else if ("stfp".equalsIgnoreCase(protocol)) {
      if (errorHandler == null)
        errorHandler = new SFTPErrorHandler(conf);

      SFTPWorker sftpw = new SFTPWorker(conf, log, flog);
      sftpw.start();

      workers.add(sftpw);

      return sftpw;

    } else {
      throw new Exception("Unsupported protocol: " + protocol);
    }
  }

  private boolean isWorking() {
    for (int i = 0; i < workers.size(); i++) {
      Worker w = (Worker) workers.get(i);
      if (w.getError() != null && w.isReserved())
        return true;
    }
    return false;
  }

}
