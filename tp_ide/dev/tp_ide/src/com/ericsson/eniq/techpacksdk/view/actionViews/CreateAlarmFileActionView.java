package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * View for the action that creates the alarm file.
 * 
 * @author eciacah
 */
public class CreateAlarmFileActionView implements ActionView {

  // Property keys:
  protected static final String FILENAME_PROPERTY_KEY = "filename";

  protected static final String DIRECTORY_PROPERTY_KEY = "directory";

  private static Logger logger = Logger.getLogger(CreateAlarmFileActionView.class.getName());

  // The directory text field:
  private final JTextField directory;

  // The filename text field:
  private final JTextField filename;

  /**
   * Constructor for test only. Sets up mock objects for the text fields so it
   * is possible to mock values being read and written to them.
   * 
   * @param directoryMock
   * @param filenameMock
   */
  protected CreateAlarmFileActionView(final JTextField directoryMock, final JTextField filenameMock) {
    this.directory = directoryMock;
    this.filename = filenameMock;
  }

  /**
   * Default constructor for CreateAlarmFileActionView.
   * @param parent  
   * @param action
   */
  public CreateAlarmFileActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    // Add directory text field:
    directory = new JTextField(20);
    directory.setToolTipText("Path of file's directory.");

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    final JLabel l_directory = new JLabel("Directory");
    l_directory.setToolTipText("Path of the file's directory.");
    parent.add(l_directory, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(directory, c);
    
    // Filename text field:
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;

    filename = new JTextField(20);
    filename.setToolTipText("File name.");

    final JLabel l_filename = new JLabel("File name");
    l_filename.setToolTipText("File name");
    parent.add(l_filename, c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(filename, c);

    // Set the values in the textboxes from database:
    setTextboxValues(action);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  /**
   * Sets the textbox values for directory and filename.
   * 
   * @param action
   *          A Meta_transfer_actions object. Used to get the data from the
   *          database.
   */
  protected final void setTextboxValues(final Meta_transfer_actions action) {
    if (action != null) {
      try {
        // Get the action contents:
        final String actionContents = action.getAction_contents();
        // Convert to properties:
        final Properties props = Utils.stringToProperty(actionContents);
        // Set the text fields:
        directory.setText(props.getProperty(DIRECTORY_PROPERTY_KEY, ""));
        filename.setText(props.getProperty(FILENAME_PROPERTY_KEY, ""));
      } catch (Exception exc) {
        logger.warning("Error setting values in CreateFileActionView");
      }
    } else {
      logger.warning("Error setting text box values in CreateFileActionView: Meta_transfer_actions was not defined");
    }
  }

  public String getType() {
    return "CreateFile";
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.view.actionViews.ActionView#validate()
   */
  public String validate() {
    String error = "";

    if (directory.getText() == null) {
      error += "Parameter Directory must be defined\n";
    } else if (directory.getText().length() <= 0) {
      error += "Parameter Directory must be defined\n";
    }

    if (filename.getText() == null) {
      error += "Parameter filename must be defined\n";
    } else if (filename.getText().length() <= 0) {
      error += "Parameter filename must be defined\n";
    }

    return error;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.view.actionViews.ActionView#getContent()
   */
  public String getContent() {

    final Properties p = new Properties();
    p.put(DIRECTORY_PROPERTY_KEY, directory.getText().trim());
    p.put(FILENAME_PROPERTY_KEY, filename.getText().trim());

    String propertyString = "";
    try {      
      propertyString = Utils.propertyToString(p);
    } catch (Exception e) {
      logger.warning("Error getting properties string: " + e.toString());
    }
    return propertyString;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.view.actionViews.ActionView#getWhere()
   */
  public String getWhere() {
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.techpacksdk.view.actionViews.ActionView#isChanged()
   */
  public boolean isChanged() {
    return true;
  }

  /**
   * Gets the instance of logger. Can be used by tests to disable logging for
   * unit tests.
   * 
   * @return logger An instance of java.util.logging.Logger.
   */
  protected Logger getLogger() {
    return logger;
  }

}
