package com.distocraft.dc5000.etl.mediation.ftp;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.mediation.ErrorHandler;
import com.distocraft.dc5000.etl.mediation.FileTransferTask;
import com.distocraft.dc5000.etl.mediation.MaskFactory;

/**
 * TODO intro <br>
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class FTPErrorHandler implements ErrorHandler {

  private Properties conf;

  private Logger log;

  public FTPErrorHandler(Properties conf) {
    this.conf = conf;

    log = Logger.getLogger("FTPErrorHandler");
  }

  public FileTransferTask[] initializationError(Exception e) {
    log.finer("Handling initializationError");
    
    try {

      if (e instanceof FTPException) {

        FTPException ftpe = (FTPException) e;
        
        // Too Many files on listing
        if (ftpe.getFTPError().startsWith("550") && ftpe.getFTPError().indexOf("Argument") > 0) {

          FileTransferTask task = ftpe.getTask();

          return MaskFactory.getNext(task);

        } else if(ftpe.getFTPError().startsWith("550") && ftpe.getFTPError().indexOf("No such file") > 0) {
          log.finest("No files matching mask");
          return null;
        } else {
          log.info("Error: "+ftpe.getFTPError());
          log.log(Level.WARNING, "Unrecovable error. Ignoring task.", e);
          return null;
        }

      } else {
        log.log(Level.WARNING, "Unrecovable exception", e);
        return null;
      }

    } catch (Exception ex) {
      log.log(Level.WARNING,"ErrorHandler failed",ex);
      return null;
    }

  }

}
