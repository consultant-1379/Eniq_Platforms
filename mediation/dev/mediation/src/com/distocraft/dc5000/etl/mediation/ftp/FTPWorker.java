package com.distocraft.dc5000.etl.mediation.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.pf.file.ExtendedFileFilter;

import com.distocraft.dc5000.etl.mediation.FileTransferTask;
import com.distocraft.dc5000.etl.mediation.NoFilesException;
import com.distocraft.dc5000.etl.mediation.UnrecovableException;
import com.distocraft.dc5000.etl.mediation.Worker;

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
public class FTPWorker extends Worker {

  public static final int FTP_DEFAULT_PORT = 21;

  public Logger log;

  public Logger flog;

  final private Properties conf;

  private boolean live = true;

  private FTPClient ftp = null;

  private String[] fileList = null;

  private ArrayList filteredFiles = null;

  private int fileListIndex = 0;

  private FileTransferTask task = null;

  private int filesTransferred = 0;

  final private String username;

  final private String host;

  final private String password;

  private int portNumber = FTP_DEFAULT_PORT;

  private String remoteDirectory = null;

  final private String removeRemoteFiles;

  final private String useRecursiveFileListing;

  final private String keepFileStructure;

  final private String overwriteDownloadedFiles;

  final private int lookBackHours;

  final private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public class ftpFileEntry {

    public String path = "";

    public FTPFile ftpFile = new FTPFile();
  }

  /**
   * Connects to specified FTP target host.
   * 
   * @param conf
   *          Configuration file
   * @throws Exception
   *           is thrown if connection is failed
   */
  public FTPWorker(Properties conf, Logger log, Logger flog) throws Exception {
    this.conf = conf;
    this.log = log;
    this.flog = flog;

    this.portNumber = Integer.parseInt(getProperty("portNumber", "0"));
    this.portNumber = (this.portNumber > 0) ? this.portNumber : FTP_DEFAULT_PORT;

    this.username = getProperty("userName");
    this.password = getProperty("password");
    this.host = getProperty("hostName");

    this.removeRemoteFiles = getProperty("removeRemoteFiles", "true");

    this.useRecursiveFileListing = getProperty("useRecursiveFileListing", "false");
    this.keepFileStructure = getProperty("keepFileStructure", "true");
    this.overwriteDownloadedFiles = getProperty("overwriteDownloadedFiles", "true");

    this.lookBackHours = Integer.parseInt(getProperty("lookBackHours", "0"));

    this.createFtpConnection();
  }

  /**
   * Gets a filelist using specified mask from specified directory
   * 
   * @param remoteDir
   *          sourcePath to files
   * @param mask
   *          condition used to limit filelist length
   * @throws Exception
   *           is thrown if initialization fails
   */
  public void initialize(final FileTransferTask task) throws NoFilesException, Exception, UnrecovableException {

    try {

      this.task = task;

      if (task == null) {
        this.log.severe("Parameter task was null. Exiting...");
        throw new Exception("Parameter task was null. Exiting...");
      }

      if (task.remoteDir.endsWith(File.separator) == false) {
        task.remoteDir = task.remoteDir + File.separator;
      }

      if (task.localDir.endsWith(File.separator) == false) {
        task.localDir = task.localDir + File.separator;
      }

      // String rpath = task.remoteDir;
      this.remoteDirectory = task.remoteDir;

      if (FileTransferTask.GET.equalsIgnoreCase(task.method)) { // Getting files
        // from Remote
        // Host

        log.fine("Listing files. Mask is " + task.getMask());

        log.fine("GET: Initialize. RemoteDir: " + this.remoteDirectory);

        filteredFiles = filterFilesByMask(this.remoteDirectory, this.useRecursiveFileListing);

        this.log.fine(filteredFiles.size() + " files found after filtering them with filemask.");

        this.log.finest("Printing out the filemask filtered files.");

        Iterator filteredFilesIter = filteredFiles.iterator();
        int counter = 0;

        while (filteredFilesIter.hasNext()) {
          ftpFileEntry currFtpFileEntry = (ftpFileEntry) filteredFilesIter.next();
          this.log.finest("File " + counter);
          this.log.finest("Path: " + currFtpFileEntry.path);
          this.log.finest("FTPFile: " + currFtpFileEntry.ftpFile);
          counter++;
        }

        setFtpRemoteWorkingDirectory(this.remoteDirectory);

        // 60s * 60min * 1000 ms = 3600000 ms = 1 hour.
        final long lbHoursInMillis = 3600000 * lookBackHours; // 

        final Calendar currentTime = Calendar.getInstance();

        if (lbHoursInMillis > 0) {
          final ArrayList approvedFiles = new ArrayList();

          Iterator filelistIter = filteredFiles.iterator();
          ArrayList approvedFileList = new ArrayList();

          while (filelistIter.hasNext()) {
            ftpFileEntry currFtpFileEntry = (ftpFileEntry) filelistIter.next();
            FTPFile currFTPFile = currFtpFileEntry.ftpFile;
            final Calendar currentFileTimestamp = currFTPFile.getTimestamp();

            this.log.finest("Threshold time="
                + dateFormat.format(new Date(currentTime.getTimeInMillis() - lbHoursInMillis)) + " and File timestamp="
                + dateFormat.format(new Date(currentFileTimestamp.getTimeInMillis())));

            if ((currentTime.getTimeInMillis() - lbHoursInMillis) <= currentFileTimestamp.getTimeInMillis()) {
              // File is in specified time range.
              approvedFiles.add(currFtpFileEntry);
              log.finest("File " + currFTPFile.getName() + " is within lookback hours.");
            } else {
              log.finest("File " + currFTPFile.getName() + " not within lookback hours.");
            }

          }

          filteredFiles = approvedFiles;

          this.log.fine(filteredFiles.size() + " files found after filtering them with lookback hours.");

        }

      } else if (FileTransferTask.PUT.equalsIgnoreCase(task.method)) { // Putting
        // files
        // to
        // Remote
        // Host

        log.fine("PUT: Initialize. RemoteDir: " + this.remoteDirectory);

        setFtpRemoteWorkingDirectory(this.remoteDirectory);

        final File f = new File(task.localDir);

        if (!f.canRead()) {
          throw new Exception("Can't read local directory " + task.localDir);
        }

        final ExtendedFileFilter eff = new ExtendedFileFilter();
        eff.addPattern(task.getMask());

        fileList = f.list(eff);

        log.fine("Got fileList. List contains " + fileList.length + " files");

        if (fileList.length > 0) {
          this.task = task;
        } else {
          throw new NoFilesException();
        }

      } else { // Unknown method -> Confused
        throw new Exception("Unknown transfer method " + task.method);
      }
    } catch (NoFilesException e) {
      this.log.log(Level.SEVERE, "NoFilesException occurred.", e);
      throw e;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, "FTPWorker.initialize failed.", e);
      throw new UnrecovableException(e.getMessage());
    }

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

      if (FileTransferTask.GET.equals(task.method)) { // DOWNLOAD
        this.log.finest("Executing run and fileList contains " + filteredFiles.size() + " files.");

        if (task == null || filteredFiles == null || filteredFiles.size() <= 0) {
          continue;
        }

        log.fine("Starting to handle fileList...");

        Iterator filteredFilesIter = filteredFiles.iterator();

        while (filteredFilesIter.hasNext()) {
          ftpFileEntry currFtpFileEntry = (ftpFileEntry) filteredFilesIter.next();

          log.fine("Downloading: " + currFtpFileEntry.ftpFile.getName());

          BufferedOutputStream bo = null;

          try {
            log.fine("Trying to open localFile... LocalDir is " + task.localDir + currFtpFileEntry.path);
            File tgtDir = null;
            if (this.keepFileStructure.equalsIgnoreCase("true")) {
              tgtDir = new File(task.localDir + currFtpFileEntry.path);
            } else {
              tgtDir = new File(task.localDir);
            }

            String filename = currFtpFileEntry.ftpFile.getName();

            if (tgtDir.isDirectory() || tgtDir.isFile() || tgtDir.exists()) {
              this.log.finest("tgtDir " + tgtDir.getAbsolutePath() + " already exists.");
            } else {
              boolean mkDirsResult = tgtDir.mkdirs();

              if (mkDirsResult == false) {
                this.log.warning("Could not create directory " + tgtDir.getAbsolutePath() + " with mkdirs() -call.");
                continue;
              }
            }

            File targetFile = new File(tgtDir, filename);
            boolean overwrite = true;

            if (targetFile.exists()) {

              if (!overwriteDownloadedFiles.equalsIgnoreCase("true")) {
                overwrite = false;
              }

              if (overwrite == true) {
                if (!targetFile.delete()) {
                  this.log.warning("Could not delete existing local file " + targetFile.getAbsolutePath());
                  continue;
                }
              }

            }

            if (overwrite == true) {
              bo = new BufferedOutputStream(new FileOutputStream(targetFile));

              setFtpRemoteWorkingDirectory(this.remoteDirectory + currFtpFileEntry.path);

              if (ftp.retrieveFile(filename, bo)) {

                if (this.removeRemoteFiles.equalsIgnoreCase("true")) {
                  log.fine("Trying to delete remoteFile");

                  if (ftp.deleteFile(filename)) {
                    log.finest("Deleted");
                  } else {
                    log.warning("Unable to delete remoteFile " + filename + ":" + ftp.getReplyString());
                    throw new Exception("ftp.deleteFile failed exception");
                  }
                }

                flog.info("Downloaded file: " + filename);

                filesTransferred++;

              } else {
                this.log.warning("Failed to retrieve file " + filename);
              }
            } else {
              this.log.info("Not overwriting existing file " + targetFile.getAbsolutePath());
            }

          } catch (Exception e) {
            log.log(Level.WARNING, "File download failed exceptionally", e);
            this.log.severe("FTPWorker.run failed because host " + this.host + " did not response anymore.");
            disconnect();
            task = null;
            this.error = e;
            return;
          } finally {
            if (bo != null) {
              try {
                bo.close();
              } catch (Exception e) {
                log.log(Level.FINE, "BO close failed", e);
              }
            }
          }

        }

      } else if (FileTransferTask.PUT.equals(task.method)) { // UPLOAD

        this.log.finest("Executing run and fileList contains " + fileList.length + " files.");

        if (task == null || fileList == null || fileList.length <= 0) {
          continue;
        }

        log.fine("Starting to handle fileList...");

        for (fileListIndex = 0; fileListIndex < fileList.length; fileListIndex++) {

          final String file = fileList[fileListIndex];

          log.info("Uploading: " + file);

          BufferedInputStream bi = null;

          try {
            log.fine("Trying to open localFile: " + file);

            final File srcDir = new File(task.localDir);

            final File sfile = new File(srcDir, file);

            if (sfile.canRead()) {

              bi = new BufferedInputStream(new FileInputStream(sfile));

              if (ftp.storeFile(file, bi)) {

                sfile.delete();

                filesTransferred++;

                flog.info("Uploaded file: " + file);

              } else {
                log.warning("Upload of file " + file + " failed: " + ftp.getReplyString());
                throw new Exception("ftp.storeFile failed exception");
              }

            } else {
              log.info("Cant open sourceFile " + file);
            }

          } catch (Exception e) {
            log.log(Level.WARNING, "File upload failed exceptionally", e);
            this.log.severe("FTPWorker.run failed because host " + this.host + " did not response anymore.");
            disconnect();
            task = null;
            this.error = e;
            return;
          } finally {
            if (bi != null) {
              try {
                bi.close();
              } catch (Exception e) {
                log.log(Level.FINE, "BI close failed", e);
              }
            }
          }

        }

      }

      task = null; // Task is ready

    } // main loop

    disconnect();

    log.fine("Finished");

  }

  /**
   * This function disconnects the FTPClient from the server.
   */
  private void disconnect() {
    try {
      ftp.disconnect();
      this.log.finest("Disconnected from the host " + this.host);
    } catch (Exception e) {
      log.log(Level.WARNING, "Error closing connection", e);
    }
  }

  public boolean isReserved() {

    if (task == null) {
      return false;
    }

    return true;
  }

  public void kill() {
    live = false;
  }

  public void getStatus(final StringBuffer sb) {

    sb.append("   Worker: ").append(filesTransferred).append(" files transferred. Status: ");
    if (task == null) {
      sb.append("idle");
    } else {
      sb.append("running, ").append(fileList.length - fileListIndex).append("/").append(fileList.length).append(
          " files left.");
    }

  }

  private String getProperty(final String pname) throws Exception {
    final String p = conf.getProperty(pname);
    if (p == null) {
      throw new Exception("Config: Property " + pname + " must be defined");
    }
    return p;
  }

  private String getProperty(final String pname, final String def) {
    return conf.getProperty(pname, def);
  }

  /**
   * This function reconnects the FTPClient to remote server.
   * 
   * @return Returns true if the reconnection is succesful. Otherwise returns
   *         false.
   */
  private boolean createFtpConnection() throws Exception {

    try {
      ftp = new FTPClient();

      ftp.enterLocalPassiveMode();

      log.fine("Connecting...");

      ftp.connect(host, this.portNumber);

      String rep = ftp.getReplyString();

      log.finer(rep);

      if (!rep.startsWith("220")) {
        throw new Exception("Connection error: " + rep);
      }

      ftp.login(this.username, this.password);

      rep = ftp.getReplyString();

      log.finer(rep);

      if (!rep.startsWith("230")) {
        throw new Exception("Login error: " + rep);
      }

      log.info("Connected");

    } catch (Exception e) {
      throw new Exception("Failed to create connection to server " + host, e);
    }

    return true;
  }

  /**
   * This function sets the working directory to the FTPClient.
   * 
   * @param remotePath
   *          Path on the remote server.
   * @throws Exception
   */
  private void setFtpRemoteWorkingDirectory(final String remotePath) throws UnrecovableException {
    this.log.finer("Changing workingDir to " + remotePath);

    try {
      if (!this.ftp.changeWorkingDirectory(remotePath) || !this.ftp.getReplyString().startsWith("250")) {
        throw new UnrecovableException("Unable to change to remoteDirectory: " + remotePath);
      }

      this.log.finer("Changing workingDir replyString: " + this.ftp.getReplyString());

    } catch (Exception e) {
      throw new UnrecovableException(e.getMessage());
    }
  }

  /**
   * This function gets a list of remote files and filters them with the given
   * filemask.
   * 
   * @param remoteDirectory
   *          Directory where the files are downloaded from.
   * @param useRecursiveFileListing
   *          String containing "true" if the files are downloaded from the
   *          remote directory and all it's subdirectories. Otherwise only the
   *          files located in remote directory are downloaded.
   * @return Returns an ArrayList containing the filtered files.
   */
  private ArrayList filterFilesByMask(String remoteDirectory, String useRecursiveFileListing) throws Exception,
      UnrecovableException {

    log.fine("Iterating at RemoteDir: " + remoteDirectory);
    ArrayList filteredFiles = new ArrayList();
    setFtpRemoteWorkingDirectory(remoteDirectory);

    FTPFile[] dirEntries = ftp.listFiles();

    String rep = ftp.getReplyString();

    if (!rep.startsWith("226")) {
      // Directory listing failed. Too many files?
      this.log.warning("FTP ReplyString: " + rep);

      if (rep.startsWith("550") && !rep.contains("Bad directory components")) {
        this.log.fine("No files found with filemask " + task.getMask() + " from directory " + remoteDirectory);
      } else if (rep.startsWith("550") && rep.contains("Bad directory components")) {
        throw new FTPException("Cannot read remote directory " + remoteDirectory, rep, task);
      } else {
        throw new FTPException("Listing files failed while doing listFiles.", rep, task);
      }
    }

    ArrayList filteredFilesInRemoteDir = new ArrayList();

    String[] filteredFilenames = ftp.listNames(task.getMask());

    rep = ftp.getReplyString();

    if (!rep.startsWith("226")) {
      // Directory listing failed. Too many files?
      this.log.warning("FTP ReplyString: " + rep);

      if (rep.startsWith("550") && !rep.contains("Bad directory components")) {
        this.log.fine("No files found with filemask " + task.getMask() + " from directory " + remoteDirectory);
      } else if (rep.startsWith("550") && rep.contains("Bad directory components")) {
        throw new FTPException("Cannot read remote directory " + remoteDirectory, rep, task);
      } else {
        throw new FTPException("Listing files failed while doing listNames.", rep, task);
      }

    }

    if (filteredFilenames != null) {
      log.finer("RemoteDir " + remoteDirectory + " has " + filteredFilenames.length
          + " files by filemask filtering with mask " + task.getMask());

      for (int i = 0; i < filteredFilenames.length; i++) {
        filteredFilesInRemoteDir.add(filteredFilenames[i]);
      }

    }

    if (dirEntries != null) {
      // First check if there are any subdirectories.
      for (int i = 0; i < dirEntries.length; i++) {
        FTPFile currEntry = (FTPFile) dirEntries[i];

        if (this.useRecursiveFileListing.equals("true")) {
          if (currEntry.isDirectory()) {
            if (currEntry.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION) == true
                || currEntry.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION) == true) {

              String subdirPath = "";
              if (remoteDirectory.endsWith(File.separator) == false) {
                subdirPath = remoteDirectory + File.separator + currEntry.getName();
              } else {
                subdirPath = remoteDirectory + currEntry.getName();
              }

              log.finest("Going into subdirectory " + subdirPath);
              // Found a subdirectory. Go and get the files from there.
              filteredFiles.addAll(filterFilesByMask(subdirPath, useRecursiveFileListing));
              // Set back the class variable remoteDirectory after iterating a
              // subdirectory.
              setFtpRemoteWorkingDirectory(remoteDirectory);
            } else {
              this.log.fine("Skipping directory " + remoteDirectory + currEntry.getName()
                  + " because user has not enough permissions.");
            }
          }
        }

        if (currEntry.isFile()) {
          if (currEntry.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION) == true
              || currEntry.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION) == true) {
            if (filteredFilesInRemoteDir.contains(currEntry.getName())) {
              // This file has survived the file mask filtering.
              ftpFileEntry currFileEntry = new ftpFileEntry();

              // Remove the start of the remotedirectory in case where we are
              // iterating at a remote subdirectory.
              String remoteSubDirPath = remoteDirectory.substring(this.remoteDirectory.length(), remoteDirectory
                  .length());

              if (remoteSubDirPath == null) {
                remoteSubDirPath = "";
              }

              this.log.finest("remoteSubDirPath = " + remoteSubDirPath);
              currFileEntry.path = remoteSubDirPath;
              currFileEntry.ftpFile = currEntry;
              this.log
                  .finest("File " + currFileEntry.ftpFile.getName() + " has been added to the filtered files list.");
              filteredFiles.add(currFileEntry);
            }
          } else {
            this.log.fine("Skipping file " + remoteDirectory + currEntry.getName()
                + " because user has not enough permissions.");
          }
        }

      }
    }

    return filteredFiles;

  }
}
