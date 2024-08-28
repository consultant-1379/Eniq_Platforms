package com.distocraft.dc5000.etl.mediation.ftp;

import com.distocraft.dc5000.etl.mediation.FileTransferTask;

/**
 * TODO intro <br> 
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class FTPException extends Exception {
  
  private String ftperror;
  private FileTransferTask task;
  
  public FTPException(String message, String ftperror, FileTransferTask task) {
    super(message);
    this.ftperror = ftperror;
    this.task = task;
  }

  public String getFTPError() {
    return ftperror;
  }
  
  public FileTransferTask getTask() {
    return task;
  }
  
}
