package com.distocraft.dc5000.etl.mediation;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

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
public class FileTransferTask {

  public static final String GET = "get";

  public static final String PUT = "put";

  public Properties conf = null;

  public int maskIndex = 0;

  public int calendarField = -1;

  public String method = FileTransferTask.GET;

  public String remoteDir = "";

  public String localDir = "";

  private String mask0 = "";

  private String mask1 = "";

  public Date timestamp = new Date();

  public FileTransferTask(Properties conf) throws Exception {
    this.conf = conf;
    
    method = getMethod(getProperty("method"));
    remoteDir = getProperty("remoteDir");
    localDir = getProperty("localDir");
    mask0 = getProperty("mask0");
    mask1 = conf.getProperty("mask1");
  }

  public FileTransferTask(FileTransferTask ftt) {
    this.method = ftt.method;
    this.remoteDir = ftt.remoteDir;
    this.localDir = ftt.localDir;
    this.maskIndex = ftt.maskIndex;
    this.timestamp = ftt.timestamp;
  }

  public String getMask() {
    if (maskIndex == 0)
      return mask0;
    else if (maskIndex == 1)
      return mask1;
    else
      return null;
  }

  public void setMask(String mask) {
    if (maskIndex == 0)
      mask0 = mask;
    else if (maskIndex == 1)
      mask1 = mask;
  }

  private String getMethod(String val) {
    if(FileTransferTask.PUT.equalsIgnoreCase(val))
      return FileTransferTask.PUT;
    else
      return FileTransferTask.GET;
  }

  public String getProperty(String name) throws Exception {
    String val = conf.getProperty(name, null);

    if (val == null)
      throw new Exception("Parameter " + name + " is undefined");

    return val;

  }
  
  public void print(Logger log) {
    log.fine("FileTransferTask:");
    log.fine("Method: "+method);
    log.fine(localDir+" --> "+remoteDir);
    log.fine("Mask0: "+mask0);
    log.fine("Mask1: "+mask1);
    log.fine("Mask "+maskIndex+": "+getMask());
  }

}
