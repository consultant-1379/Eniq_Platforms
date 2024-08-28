package com.ericsson.eniq.component;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * A singleton exception handler which can be used to implement to show a pop-up
 * when a predefined (hard coded) exception received. In case a non-predefined
 * exception is received, the message is logged.
 * 
 * @author eheitur
 * 
 */
public class ExceptionHandler {

  private static long lastException1 = 0;

  private static long lastException2 = 0;

  private final long exceptionLookupTime = 5000;

  private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());

  private static ExceptionHandler singletonExceptionHandler = null;

  /**
   * In the first call of this method new share is created and returned. After
   * the first call the same share is returned.
   * 
   * @return ExceptionHandler
   */
  public synchronized static ExceptionHandler instance() {

    if (singletonExceptionHandler == null) {
      singletonExceptionHandler = new ExceptionHandler();
    }

    return singletonExceptionHandler;

  }

  public synchronized void handle(final Exception se) {

    if (lastException1 + exceptionLookupTime <= System.currentTimeMillis()) {
      if (se instanceof SQLException && se.getMessage().contains("JZ0C0: Connection is already closed")) {
        JOptionPane.showMessageDialog(null, "Connection is down. Please restart the TechPackIDE application.");
        lastException1 = System.currentTimeMillis();
        return;
      }
    }

    if (lastException2 + exceptionLookupTime <= System.currentTimeMillis()) {
      if (se instanceof SQLException && ((SQLException) se).getErrorCode() == -210) {
        JOptionPane.showMessageDialog(null, "Database has a table locked." + "\n"
            + "Please restart the TechPackIDE application." + "\n"
            + "If this does not resolve the problem, then contact a database administrator.");
        lastException2 = System.currentTimeMillis();
        return;
      }
    }

    // Unexpected exception. Log the message.
    LOGGER.warning("Unexpected exception: " + se.getMessage());
    LOGGER.log(Level.WARNING,"Exception details",se);

  }

}
