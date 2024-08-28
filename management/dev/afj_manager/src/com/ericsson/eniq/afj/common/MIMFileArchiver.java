/**
 * 
 */
package com.ericsson.eniq.afj.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;

/**
 * @author eheijun
 * 
 */
public class MIMFileArchiver implements FileArchiver {

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.common.FileArchiver#backupFile(java.lang.String, java.lang.String)
   */
  @Override
  public File backupFile(final File srcFile, final File dstDir) throws IOException {
    final String fileNameTimeStamp = new SimpleDateFormat("yyyyMMddHHmmssSS").format(System.currentTimeMillis());
    final String archiveFileName = srcFile.getName() + "." + fileNameTimeStamp;
    final File backupFile = new File(dstDir, archiveFileName);
    renameFile(srcFile, backupFile);
    return backupFile;
  }

  /**
   * Renames file
   * @param srcFile
   * @param dstFile
   * @throws IOException
   */
  private static void renameFile(final File srcFile, final File dstFile) throws IOException {

    if (!srcFile.exists()) {
      throw new IOException("backupFile: no such file or directory: " + srcFile.getName());
    }

    if (!srcFile.canWrite()) {
      throw new IOException("backupFile: write protected: " + srcFile.getName());
    }

    if (!dstFile.exists()) {
      if (!dstFile.createNewFile()) {
        throw new IOException("backupFile: problem creating destination: " + dstFile.getName());
      }
    }

    final FileChannel source = new FileInputStream(srcFile).getChannel();
    try {
      final FileChannel destination = new FileOutputStream(dstFile).getChannel();
      try {
        destination.transferFrom(source, 0, source.size());
      } finally {
        destination.close();
      }
    } finally {
      source.close();
    }

    if (!srcFile.delete()) {
      throw new IOException("backupFile: problem deleting source: " + srcFile.getName());
    }

  }
}
