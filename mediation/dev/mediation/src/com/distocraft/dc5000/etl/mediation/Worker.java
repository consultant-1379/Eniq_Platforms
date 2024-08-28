package com.distocraft.dc5000.etl.mediation;

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
public abstract class Worker extends Thread {

  /**
   * Error flag. If null, all is fine. If Exception exists, error has occurred.
   */
  protected Exception error = null;

  public abstract void initialize(FileTransferTask task) throws NoFilesException, Exception;

  public abstract boolean isReserved();

  public abstract void kill();

  public abstract void getStatus(StringBuffer sb);

  //public abstract Exception getError();

  /**
   * This function returns the "error flag" which informs if the worker has moved to error state.
   * @return Returns Exception if error has occurred. Otherwise returns null.
   */
  public Exception getError() {
    return this.error;
  }

}
