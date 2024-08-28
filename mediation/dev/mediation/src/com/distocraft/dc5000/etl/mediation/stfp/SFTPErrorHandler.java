package com.distocraft.dc5000.etl.mediation.stfp;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.mediation.ErrorHandler;
import com.distocraft.dc5000.etl.mediation.FileTransferTask;

public class SFTPErrorHandler implements ErrorHandler {

  private Properties conf;

  private Logger log;

  public SFTPErrorHandler(Properties conf) {
    this.conf = conf;

    log = Logger.getLogger("SFTPErrorHandler");
  }

  public FileTransferTask[] initializationError(Exception e) {
    log.log(Level.WARNING, "Unrecovable exception", e);
    return null;
  }

}
