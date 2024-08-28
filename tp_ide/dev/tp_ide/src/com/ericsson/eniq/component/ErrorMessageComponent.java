package com.ericsson.eniq.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Helper class for displaying error message vector
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class ErrorMessageComponent extends JPanel implements ActionListener {

  /**
   * Constants for the button actions used.
   */
  public static final String OPEN = "open";

  public static final String OK = "ok";

  /**
   * Action listener for when this is used in parameter panels
   */
  ActionListener myListener = null;

  /**
   * Action command for when the action listener is triggered
   */
  private String actionCommand;

  /**
   * Caption of the form
   */
  private final String caption;

  /**
   * Stores current model
   */

  private TableModel currentModel;

  private final Vector<String> titles;

  private Vector<Vector<String>> errorMessages;

  /**
   * The edit button for opening the message dialog
   */
  private final JButton openButton;

  /**
   * JTable where messages are shown
   * 
   */
  private JTable messageTable;

  /**
   * Frame for messageTable
   */
  private static JDialog messageDialog;

  private static ImageIcon errorMessageIcon;

  private static ImageIcon okMessageIcon;


  /**
   * Creates dialog for tableitems
   * 
   * @param rows
   *          editable vector
   * @param textFieldWidth
   *          displaysize for display field
   */
  public ErrorMessageComponent(final Application application) {
    super(new GridBagLayout());

    final ResourceMap resourceMap = application.getContext().getResourceMap(getClass());
    
    // Set caption for dialog
    this.caption = resourceMap.getString("ErrorMessageComponent.title");

    // Initialize model
    this.errorMessages = new Vector<Vector<String>>();
    this.titles = new Vector<String>();
    titles.add(resourceMap.getString("ErrorMessageComponent.tabletitle"));
    this.currentModel = new DefaultTableModel(this.errorMessages, this.titles);

    errorMessageIcon = resourceMap.getImageIcon("ErrorMessageComponent.error.icon");
    okMessageIcon = resourceMap.getImageIcon("ErrorMessageComponent.ok.icon");
    
    this.openButton = new JButton();
    this.openButton.setActionCommand(OPEN);
    this.openButton.addActionListener(this);
    this.openButton.setName(caption + "OpenButton");
    // Standard behavior you probably want to turn off
    // This will stop it from drawing an indicator which shows the button has focus.
    openButton.setFocusPainted(false);
    // This will prevent the default button border from being drawn
    openButton.setBorderPainted(false);
    // This will turn off the standard button painting when the user interacts with the button, like
    // when the user presses the button, the buttons usually get darker or something.
    openButton.setContentAreaFilled(false);
    // Remove padding
    openButton.setMargin(new Insets(0,0,0,0));

    GridBagConstraints gbc = new GridBagConstraints();

    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    this.add(openButton, gbc);

  }

  /**
   * This is the listener callback for jtable.
   */
  public void actionPerformed(final ActionEvent e) {

    if (OPEN.equals(e.getActionCommand())) {
      if (openButton.getIcon().equals(errorMessageIcon))	{
      // Create the message frame, set the vector and show the frame.
      createFrame();
      }
    } else if (OK.equals(e.getActionCommand())) {
      // Show done, OK clicked.

      // Store the edited vector, dispose the frame.
      messageDialog.setVisible(false);
      messageDialog.dispose();

      // Fire an event so that the listener knows the value was
      // updated
      if (myListener != null) {
        myListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
      }
    }
  }

  /**
   * Return the component's current value
   */
  public Vector<Vector<String>> getValue() {
    return this.errorMessages;
  }

  /**
   * Set the component's current value
   */
  public void setValue(final List<String> messages) {
    if (messages != null) {
      final Vector<Vector<String>> errorMessages = new Vector<Vector<String>>();
      for (final Iterator<String> iter = messages.iterator(); iter.hasNext(); ) {
        final Vector<String> errorMessage = new Vector<String>();
        errorMessage.add(iter.next());
        errorMessages.add(errorMessage);
      }
      this.errorMessages = errorMessages;
    } else {
      this.errorMessages = new Vector<Vector<String>>();
    }
    this.currentModel = new DefaultTableModel(this.errorMessages, this.titles);
    if (this.errorMessages.size() > 0) {
      openButton.setIcon(errorMessageIcon);
    } else {
      openButton.setIcon(okMessageIcon);
    }
  }

  /**
   * Return itself
   */
  public JComponent getComponent() {
    return this;
  }

  /**
   * Use this method for enabling/disabling the component
   */
  public void setEnabled(final boolean isEnabled) {
    openButton.setEnabled(isEnabled);
  }

  /**
   * Set the action command for this component
   */
  public void setActionCommand(final String command) {
    actionCommand = command;
  }

  /**
   * Set the action listener for this instance
   */
  public void addActionListener(final ActionListener listener) {
    myListener = listener;
  }

  /**
   * Helper method to create the pane and frame.
   * 
   * @return
   */

  private void createFrame() {
    
    // Create dialog
    if (messageDialog != null) {
      messageDialog.dispose();
    }
    messageDialog = new JDialog();
    messageDialog.setAlwaysOnTop(true);
    messageDialog.setTitle(caption);
    messageDialog.setModal(true);

    // Create table
    messageTable = new JTable(currentModel);
    messageTable.setVisible(true);
    messageTable.setEnabled(false);
    messageTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    // Create scroll panel for table
    final JScrollPane editPane = new JScrollPane(messageTable);

    // Create buttons
    final JButton ok = new JButton("OK");
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setName(caption + "OkButton");

    // Layout the contents of the frame
    final GridBagLayout gbl = new GridBagLayout();
    final GridBagConstraints gbc = new GridBagConstraints();
    messageDialog.setLayout(gbl);
    messageDialog.setBackground(Color.white);

    // Place the scroll panel in the layout
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 0;
    gbc.insets = new Insets(0, 0, 0, 0);
    gbl.setConstraints(editPane, gbc);

    messageDialog.add(editPane);

    // Place the buttons in the layout
    gbc.gridwidth = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridy = 1;

    gbl.setConstraints(ok, gbc);
    messageDialog.add(ok);

    gbc.anchor = GridBagConstraints.EAST;

    // And we're done
    messageDialog.pack();
    Utils.center(messageDialog);
    messageDialog.setVisible(true);

  }

}
