package com.distocraft.dc5000.etl.mediation.stfp;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pf.file.ExtendedFileFilter;

import com.distocraft.dc5000.etl.mediation.FileTransferTask;
import com.distocraft.dc5000.etl.mediation.NoFilesException;
import com.distocraft.dc5000.etl.mediation.Worker;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;

public class SFTPWorker extends Worker {

  public static final int SFTP_DEFAULT_PORT = 22;

  public Logger log;

  public Logger flog;

  private Properties conf;

  private boolean live = true;

  private Session sftp = null;

  private ChannelSftp csftp = null;

  private String[] fileList = null;

  private int fileListIndex = 0;

  private FileTransferTask task = null;

  private int filesTransferred = 0;

  private String username = null;

  private String password = null;

  private String host = null;

  public SFTPWorker(Properties conf, Logger log, Logger flog) throws Exception {

    this.conf = conf;
    this.log = log;
    this.flog = flog;

    int port = Integer.parseInt(getProperty("portNumber", "0"));
    port = (port > 0) ? port : SFTP_DEFAULT_PORT;

    /*
     String user = getProperty("userName");
     String host = getProperty("hostName");
     final String pass = getProperty("password");
     */

    this.username = getProperty("userName");
    this.password = getProperty("password");
    this.host = getProperty("hostName");

    JSch jsch = new JSch();

    sftp = jsch.getSession(this.username, this.host, 22);

    final Logger tlog = log;

    UserInfo uinfo = new UserInfo() {

      String password = this.password;

      public String getPassphrase() {
        return null;
      }

      public String getPassword() {
        return password;
      }

      public boolean promptPassword(String message) {
        tlog.finer("promptPassword(" + message + ")");
        return true;
      }

      public boolean promptPassphrase(String message) {
        tlog.finer("promptPassphrase(" + message + ")");
        return true;
      }

      public boolean promptYesNo(String message) {
        tlog.finer("promprYesNo(" + message + ")");
        return true;
      }

      public void showMessage(String message) {
        tlog.finer("Via UserInfo: " + message);
      }

    };

    // username and password will be given via UserInfo interface.

    sftp.setUserInfo(uinfo);

    sftp.connect();

    Channel channel = sftp.openChannel("sftp");
    channel.connect();

    csftp = (ChannelSftp) channel;

    log.fine("Connected");

  }

  public void initialize(FileTransferTask task) throws NoFilesException, Exception {

    csftp.cd(task.remoteDir);
    csftp.lcd(task.localDir);

    if (task.method == FileTransferTask.GET) { // Getting files from Remote Host

      log.finer("Listing files");

      Vector vv = csftp.ls(task.getMask());

      if (vv != null) {

        log.finer("Got filelist: " + vv.size() + " entries");

        ArrayList fl = new ArrayList();

        for (int ii = 0; ii < vv.size(); ii++) {

          Object obj = vv.elementAt(ii);
          if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
            com.jcraft.jsch.ChannelSftp.LsEntry lse = (com.jcraft.jsch.ChannelSftp.LsEntry) obj;
            String fname = lse.getFilename();
            if (!fname.equals(".") && !fname.equals(".."))
              fl.add(fname);

          }

        }

        fileList = (String[]) fl.toArray(new String[fl.size()]);

        log.finer("Filtered filelist got " + fileList.length + " entries");

      } else {
        fileList = new String[0];
      }

    } else if (task.method == FileTransferTask.PUT) { // Putting files to Remote Host

      File f = new File(task.localDir);

      if (!f.canRead()) {
        throw new Exception("Can't read local directory " + task.localDir);
      }

      log.finest("Listing files...");
      task.print(log);

      ExtendedFileFilter eff = new ExtendedFileFilter();
      eff.addPattern(task.getMask());

      fileList = f.list(eff);

      log.finer("Filtered filelist got " + fileList.length + " entries");

    } else { // Unknown method -> Confused
      throw new Exception("Unknown transfer method " + task.method);
    }

    log.fine("Got fileList. List contains " + fileList.length + " files");

    if (fileList.length > 0)
      this.task = task;
    else
      throw new NoFilesException();

  }

  public void run() {

    log.fine("Started");

    while (live) {

      synchronized (this) { // WAIT for task to arrive
        try {
          this.wait();
        } catch (InterruptedException ie) {
        }
      }

      if (task == null || fileList == null || fileList.length <= 0)
        continue;

      log.fine("Starting to handle fileList...");

      SftpProgressMonitor monitor = new SftpProgressMonitor() {

        public void init(int op, String src, String dest, long max) {
        }

        public boolean count(long count) {
          return true;
        }

        public void end() {
        }
      };

      for (fileListIndex = 0; fileListIndex < fileList.length; fileListIndex++) {

        String file = fileList[fileListIndex];

        if (FileTransferTask.GET.equals(task.method)) { // DOWNLOAD

          log.finer("Downloading: " + file);

          try {

            log.finest("GET(" + file + "," + file + ")");

            csftp.get(file, ".", monitor, ChannelSftp.OVERWRITE);

            log.finest("RM(" + file + ")");

            csftp.rm(file);

            flog.info("Downloaded file: " + file);

            filesTransferred++;

          } catch (Exception e) {
            log.log(Level.WARNING, "File download failed exceptionally", e);
            this.log.severe("SFTPWorker.run failed because host " + this.host + " did not response anymore.");
            disconnect();
            task = null;
            this.error = e;
            return;

          }

        } else if (FileTransferTask.PUT.equals(task.method)) { // UPLOAD

          log.finer("Uploading: " + file);

          try {
            log.fine("Trying to open localFile: " + file);

            File srcDir = new File(task.localDir);

            File sfile = new File(srcDir, file);

            log.finest("PUT(" + file + ")");

            csftp.put(file, ".", monitor, ChannelSftp.OVERWRITE);

            sfile.delete();

            flog.info("Uploaded file: " + file);

            filesTransferred++;

          } catch (Exception e) {
            log.log(Level.WARNING, "File upload failed exceptionally", e);
            this.log.severe("SFTPWorker.run failed because host " + this.host + " did not response anymore.");
            disconnect();
            task = null;
            this.error = e;
          }

        }

      } // for each file in fileList

      task = null; // Task is ready

    } // main loop

    try {
      log.fine("Closing connection");

      this.disconnect();

    } catch (Exception e) {
      log.log(Level.WARNING, "Error closing connection", e);
    }

    log.fine("Finished");

  }

  public boolean isReserved() {
    return (task != null) ? true : false;
  }

  public void kill() {
    live = false;
  }

  public void getStatus(StringBuffer sb) {

    sb.append("   Worker: ").append(filesTransferred).append(" files transferred. Status: ");
    if (task == null)
      sb.append("idle");
    else
      sb.append("running, ").append(fileList.length - fileListIndex).append("/").append(fileList.length).append(
          " files left.");

  }

  private String getProperty(String pname) throws Exception {
    String p = conf.getProperty(pname);
    if (p == null)
      throw new Exception("Config: Property " + pname + " must be defined");
    return p;
  }

  private String getProperty(String pname, String def) {
    return conf.getProperty(pname, def);
  }

  /**
   * This function closes the SFTP connection to server.
   *
   */
  private void disconnect() {
    csftp.disconnect();
    sftp.disconnect();
    this.log.finest("SFTP connections closed.");
  }

}
