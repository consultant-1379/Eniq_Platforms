package com.ericsson.navigator.esm.util.log.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

public class ZipFileAppender extends RollingFileAppender {
	
	private static final String classname = ZipFileAppender.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	
	public ZipFileAppender() {
		bufferedIO=true;
	}
	
	@Override
	public synchronized void rollOver() { 
		//doAppend solves synchronization, e.g. there is a risk that a zip 
		//thread can be created and not finished before a rollover runs again

		File target;
		File file;

		if(maxBackupIndex > 0) {
			// Delete the oldest file
	
			// Rename fileName to fileName.<timestamp>
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				return;
			}
			target = new File(fileName + "." + System.currentTimeMillis());
		
			this.closeFile(); // keep windows happy.
		
			file = new File(fileName);
			if(target.exists()){
				System.err.println("Target exists!");
			}
			if(!file.renameTo(target)){
				logger.error(classname +
						".rollover(); Failed to rename alarmlog file: " + file.getAbsolutePath());
			}
			
			try {
				final File f = target;
				new Thread(new Runnable() {
					public void run() {
						try {
							zip(f);
							if (f.exists()) {
								f.delete();
							}
						} catch (final IOException e) {
							logger.error(classname+
									".rollover(); Zip failed for log file: "
									+ f.getAbsolutePath(), e);
						}
					}
				}, "ZipFileAppenderThread").start();

				// Closes also the file (multiple close are safe)
				setFile(fileName, false, bufferedIO, bufferSize);
				
			} catch(final IOException e) {
				logger.error(classname +
						".rollover(); setFile(" + fileName + ", false) call failed", e);
			}
		}
	}

	private synchronized void zip(final File file) throws IOException {
		long startTime=0;
		if (logger.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
		}
		
		final File maxFile = new File(fileName + '.' + maxBackupIndex +".zip");
		if (maxFile.exists()) {
			maxFile.delete();
		}
		
		// Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3, 2}
		for (int i = maxBackupIndex - 1; i >= 1; i--) {
			final File source = new File(fileName + "." + i + ".zip");//NOPMD
			if (source.exists()) {
				File target = new File(fileName + '.' + (i + 1)+ ".zip");//NOPMD
				source.renameTo(target);
			}
		}
		
		final int BUFFER = 8192;
		final byte data[] = new byte[BUFFER];

		final File zipFile = new File(file.getParent(), "alarmlog.log.1.zip");
		final FileOutputStream dest = new FileOutputStream(zipFile);
		final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		final FileInputStream fi = new FileInputStream(file);
		final BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);

		final ZipEntry entry = new ZipEntry(file.getName());
		out.putNextEntry(entry);

		int count;
		while ((count = origin.read(data, 0, BUFFER)) != -1) {
			out.write(data, 0, count);
		}

		out.flush();
		out.close();
		origin.close();
		dest.close();
		
		if (logger.isDebugEnabled()) {
			logger.debug(classname+".zip(); File "+file.getName()+" zipped in " +
					(System.currentTimeMillis() - startTime));
		}		
	}
} 
