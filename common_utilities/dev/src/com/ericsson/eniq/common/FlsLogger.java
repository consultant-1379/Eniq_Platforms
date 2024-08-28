/**
 * Log Handler class for symboliclinkcreator module
 */
package com.ericsson.eniq.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.distocraft.dc5000.common.ConsoleLogFormatter;

/**
 * Log handler for symboliclinkcreator module
 * @author xarjsin
 */
public class FlsLogger extends Handler {
	
	private static final DateFormat form = new SimpleDateFormat("yyyy_MM_dd");
	private boolean deb = false;
	private String logdir;
	private Writer symLog;
	private String currentDate;

	/**
	 * @throws IOException, SecurityException 
	 * 
	 */
	public FlsLogger() throws IOException, SecurityException {
		final String plogdir = System.getProperty("LOG_DIR");
	    if (plogdir == null) {
	      throw new IOException("System property \"LOG_DIR\" not defined");
	    }
	    logdir = plogdir + File.separator + "symboliclinkcreator";
	    setLevel(Level.ALL);
	    setFormatter(new ConsoleLogFormatter());
	    final String xdeb = System.getProperty("FlsLogger.debug");
	    if (xdeb != null && xdeb.length() > 0) {
	      deb = true;
	    }
	}

	/** 
	 * Close the output log stream
	 * 
	 */
	@Override
	public void close() throws SecurityException {
		if (deb) {
		      System.err.println("FL.close()");
		    }
		try{
			symLog.flush();
			symLog.close();
		}
		catch(Exception e){
			System.err.println("Failed to close log file");
		}
	}

	/**
	 *  Does nothing because publish will handle flush after writing
	 */
	@Override
	public void flush() {
		if (deb) {
		      System.err.println("FL.flush()");
		    }
	}

	/** 
	 * Publish a LogRecord
	 */
	@Override
	public synchronized void publish(final LogRecord record) {
		
		String type = record.getLoggerName();
		final String toDate = form.format(new Date(record.getMillis()));
		
		if(deb){
			System.err.println("FL.publish(" + record.getLoggerName() + ")");
		}
		// Determine that level is loggable and filter passes
		if (!isLoggable(record)) {
			return;
		}
		
		if (symLog == null || !currentDate.equals(toDate)) {
			rotate(type,toDate);
		}
		try {
			symLog.write(getFormatter().format(record));
			symLog.flush();
			if (deb) {
				System.err.println("Written: " + record.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param type
	 * @param timestamp
	 */
	private void rotate(final String type, final String timestamp) {
		
		final File dir = new File(logdir);
		if (deb) {
			System.err.println("FlsLogger.rotate("+ timestamp + ")");
		}
		try{
			if(symLog != null) {
				symLog.close();
			}
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			// create a file handle for the new logfile for this date stamp.
			File logFile = null;
			if(type.contains(".")){
				logFile = new File(dir, type.substring(0,type.indexOf(".")) + "-" + timestamp + ".log");
			} else {				
				logFile = new File(dir, type + "-" + timestamp + ".log");
			}
			
			if (deb) {
				System.err.println("FlsLogger: FileName is " + logFile.getCanonicalPath());
			}
			
			symLog = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(logFile, true)));
			currentDate = timestamp;
			symLog.write(getFormatter().getHead(this));
		}
		catch (Exception e) {
			System.err.println("FlsLogger: LogRotation failed");
			e.printStackTrace();
		}
	}
}
