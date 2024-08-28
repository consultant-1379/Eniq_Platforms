package com.ericsson.eniq.techpacksdk;

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
 * @author eeoidiv Copied version of the LicenceLogger class.
 *         Changed and adapted for the techpackide.
 */
public class TPIDELogFileHandler extends Handler {

	// The data format of the logs.
	private static final DateFormat form = new SimpleDateFormat("yyyy_MM_dd");
	
	// The dir where the logs should be placed.
	private String logdir = null;
	
	// debug flag.
	private boolean debugMode = false;

	// the string describing the licensing subsystem (used for dirnames and log entries.)
	private String subSystemString = "techpackide";
	
	private Writer currentLog;
	private String currentDate;
	private int logFailureCount = 0;
	
	/**
	 * Default constructor.
	 * @throws IOException
	 * @throws SecurityException
	 */
	public TPIDELogFileHandler() throws IOException, SecurityException {	
		// get the logdir from the system property LOG_DIR
		logdir = System.getProperty("LOG_DIR");
		if (logdir == null) {
		  logdir = File.separator + "eniq";
		  logdir += File.separator + "sw";
		  logdir += File.separator + "log";
		}

		// Define our logdir as a licensemanager subdir in the LOG_DIR
		logdir = logdir + File.separator + subSystemString;

		// set the logging level to Level.ALL to include all log messages. 
		setLevel(Level.ALL);
		
		// set the formatter to a an instance of the com.distrocraft.dc5000.common.ConsoleLogFormatter.
		setFormatter(new ConsoleLogFormatter());

		// get the debug mode from the system properties.
		String systemDebugFlag = System.getProperty("TPIDELogFileHandler.debug");
		if (systemDebugFlag != null && systemDebugFlag.length() > 0)
			debugMode = true;
	}

	/**
	 * Does nothing because publish will handle flush after writing
	 */
	public synchronized void flush() {
		if (debugMode)
			System.err.println("TPIDELogFileHandler.flush()");
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	public synchronized void close() {
		if (debugMode)
			System.err.println("TPIDELogFileHandler.close()");

		// close the output log stream.
		try {
			currentLog.flush();
			currentLog.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Publish a LogRecord
	 */
	public synchronized void publish(LogRecord record) {

		if (debugMode)
			System.err.println("TPIDELogFileHandler.publish(" + record.getLoggerName() + ")");

		// Determine that level is loggable and filter passes
		if (!isLoggable(record)) {
			return;
		}

		// get the current date stamp
		Date dateNow = new Date(record.getMillis());
		String dateStamp = form.format(dateNow);

		// check if we need to rotate the logs.
		if (currentLog == null || !currentDate.equals(dateStamp))
			rotate(dateStamp);

		// write the log entry to the writer and flush the data.
		try {
			if (currentLog != null) {
				currentLog.write(getFormatter().format(record));
				currentLog.flush();

				if (debugMode)
					System.err.println("Written: " + record.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logFailureCount++;
		}
	}

	
	/**
	 * Rotate the log onto a new one for this date.
	 * @param dateStamp the new date stamp to use.
	 */
	private void rotate(String dateStamp) {

		if (debugMode)
			System.err.println("TPIDELogFileHandler.rotate("+ dateStamp+ ")");

		try {
			// close the old file if a log file is already active.
			if (currentLog != null) {
				currentLog.close();
			}
			
			// make the log dir if it doesn't exist.
			File dir = new File(logdir);
			if (!dir.exists())
				dir.mkdirs();

			// create a file handle for the new logfile for this date stamp.
			File logFile = new File(dir, subSystemString + "-" + dateStamp + ".log");

			if (debugMode)
				System.err.println("TPIDELogFileHandler: FileName is " + logFile.getCanonicalPath());

			// open the streams and set the current date stamp
			if(logFailureCount>3) { //TODO: Don't hardcode max failures. 
				return;
			}
			currentLog = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(logFile, true)));
			if (currentLog != null) {
				currentDate = dateStamp;
				// write the header to the log file.
				currentLog.write(getFormatter().getHead(this));
			}
		} catch (Exception e) {
			System.err.println("TPIDELogFileHandler: LogRotation failed");
			e.printStackTrace();
			logFailureCount++;
		}
	}
} // end TPIDELogFileHandler
