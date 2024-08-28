package com.ericsson.eniq.techpacksdk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class TPIDEHandler extends Handler {

  public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

  private LogFrame logFrame = null;

  public TPIDEHandler() {
    setFormatter(new TPIDEFormatter());
    
    Level l = Level.FINEST;
    try {
      l = Level.parse(System.getProperty("TPIDE.LogLevel"));
    } catch(Exception e) {}
    setLevel(l);
  }

  public void setFrame(final LogFrame logFrame) {
    this.logFrame = logFrame;
  }

  public void publish(final LogRecord record) {
    
    
    if(!isLoggable(record)) {
      return;
    }
    
    
    final String msg = getFormatter().format(record);

    if (logFrame != null) {
      logFrame.addMessage(msg, record.getLevel());
    } else {
      logFrame = LogFrame.getInstance();
      if (logFrame != null) {
        logFrame.addMessage(msg, record.getLevel());
      } else {
        System.err.println(msg);
      }
    }
  }

  public void flush() {

  }

  public void close() {

  }

  public class TPIDEFormatter extends Formatter {

    public String format(final LogRecord record) {
      StringBuffer sb = new StringBuffer();
      sb.append(sdf.format(new Date(record.getMillis()))).append(" ");
      sb.append(record.getLevel()).append(" ");
      String loggerName = record.getLoggerName();
      if(loggerName.indexOf("com.ericsson.eniq.techpacksdk") >= 0) {
          loggerName = loggerName.substring(30);
      }
      if(loggerName.indexOf("com.ericsson.eniq.common.setWizards") >= 0) {
          loggerName = loggerName.substring(36);
      }
      sb.append(loggerName).append(" ");
      sb.append(record.getMessage());
      
      sb.append("\n");

      Throwable exc = record.getThrown();
      while (exc != null) {
        printException(sb, exc);

        exc = exc.getCause();
      }
      
      return sb.toString();
    }

    private void printException(final StringBuffer sb, Throwable t) {
      sb.append("   ");

      sb.append(t.getClass().getName()).append(": ").append(t.getMessage()).append("\n");

      StackTraceElement[] ste = t.getStackTrace();

      for (int j = 0; j < ste.length; j++) {

        sb.append("         ");

        sb.append(ste[j].getClassName()).append(".").append(ste[j].getMethodName());
        sb.append("(");
        if (ste[j].getFileName() == null) {
          sb.append("Unknown Source");
        } else {
          sb.append(ste[j].getFileName());
          sb.append(":");
          sb.append(ste[j].getLineNumber());
        }
        sb.append(")");
        sb.append("\n");
      }

    }
  };

}
