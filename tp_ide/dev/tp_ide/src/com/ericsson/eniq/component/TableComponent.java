package com.ericsson.eniq.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.application.Application;

import com.ericsson.eniq.techpacksdk.common.Utils;

import tableTree.CustomParameterComponent;

/**
 * Helper class for displaying table
 * 
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class TableComponent extends JPanel implements ActionListener, MouseListener, CustomParameterComponent {

  /**
   * Constants for the button actions used.
   */
  public static final String EDIT = "edit";

  public static final String OK = "ok";

  public static final String CANCEL = "cancel";

  public static final String ADDROW = "addrow";

  private Application application;

  private boolean editable = true;

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
  private final SubTableModel currentModel;

  /**
   * the description component's text field width, measured in number of
   * characters
   */
  private static int defaultTextFieldWidth = 12;

  /**
   * The text field for showing the descriptive text
   */
  private final JTextField displayField;

  /**
   * The edit button for opening the editor window
   */
  private final JButton editButton;

  /**
   * JTable where editing is done
   * 
   */
  private JTable editTable;

  /**
   * Dialog for editTable
   */
  private static JDialog editDialog;

  /**
   * UI for error messages
   */
  private ErrorMessageComponent errorMessageComponent;

  private JButton ok;

  private JButton cancel;

  private class TableComponentTableModelListener implements TableModelListener {

    public void tableChanged(final TableModelEvent e) {
      fireTableDataChanged(true);
    }

  }

  /**
   * Creates dialog for table component
   * 
   * @param application
   * @param caption
   * @param currentModel
   * @param textFieldWidth
   */
  public TableComponent(final Application application, final String caption, final SubTableModel currentModel, int textFieldWidth,
      final boolean editable) {
    super(new GridBagLayout());

    this.editable = editable;

    // set application
    this.application = application;

    // Set caption for dialog
    this.caption = caption;

    // Initialize model
    this.currentModel = currentModel;
    // this.currentModel.setData(componentTable.getData());

    // Create the new text field
    this.displayField = new JTextField(getModelAsString(currentModel));

    // If the given text field width is equal or less than zero, then the
    // default value is used.
    if (textFieldWidth <= 0) {
      // textFieldWidth = TableTreeConstants.DEFAULT_TEXTFIELD_WIDTH;
      textFieldWidth = defaultTextFieldWidth;
    }

    // Get the character width from the current font of the text field (of
    // character 'W').
    final int charWidth = displayField.getFontMetrics(displayField.getFont()).charWidth('W');

    // Set the size of the text field to match the width calculated
    // previously (number of characters * character width).
    this.displayField.setPreferredSize(new Dimension(charWidth * textFieldWidth, displayField.getMinimumSize().height));

    // no possibility to edit as text
    this.displayField.setEnabled(false);

    this.editButton = new JButton("...");
    this.editButton.setActionCommand(EDIT);
    this.editButton.addActionListener(this);
    this.editButton.setName(caption + "EditButton");

    GridBagConstraints gbc = new GridBagConstraints();

    if (textFieldWidth > 0) {
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.weightx = 1.0;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridx = 0;
      gbc.gridy = 0;
      this.add(this.displayField, gbc);
    }

    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    this.add(editButton, gbc);

  }

  private static String getModelAsString(final SubTableModel tableModel) {
    String displayValue = "";
    if (tableModel != null) {
      final int rows = tableModel.getRowCount();
      final int cols = tableModel.getColumnCount();
      for (int ind = 0; ind < rows; ind++) {
        if (!displayValue.equals("")) {
          displayValue += ", ";
        }
        String temp = "";
        for (int jnd = 0; jnd < cols; jnd++) {
          if (!temp.equals("")) {
            temp += ", ";
          }
          temp += tableModel.getValueAt(ind, jnd) + "";
        }
        temp = "[" + temp + "]";
        displayValue += temp;
      }
    }
    return displayValue;
  }

  /**
   * This is the listener callback for jtable.
   */
  public void actionPerformed(final ActionEvent e) {

    if (EDIT.equals(e.getActionCommand())) {
      // Starting to edit the cell.
      // Create the edit Dialog, set the editable vector and show the
      // Dialog.
      currentModel.startEditing();
      createDialog();
    } else if (OK.equals(e.getActionCommand())) {
      // Editing done, OK clicked.

      // Store the edited vector, dispose the Dialog.
      editDialog.setVisible(false);
      editDialog.dispose();

      // store the data and notify listeners.
      currentModel.stopEditing();
      displayField.setText(getModelAsString(currentModel));

      // Fire an event so that the listener knows the value was
      // updated
      if (myListener != null) {
        myListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
      }
    } else { // CANCEL
      // Editing done, CANCEL clicked.
      // cancel edited values
      currentModel.cancelEditing();
      // Hide and dispose the Dialog.
      editDialog.setVisible(false);
      editDialog.dispose();
    }
  }

  private void fireTableDataChanged(final boolean newvalue) {
    if (errorMessageComponent != null) {
      final Vector<String> errorStrings = currentModel.validateData();
      ok.setEnabled(newvalue);
      if (errorStrings.size() > 0) {
        ok.setEnabled(false);
        errorMessageComponent.setValue(errorStrings);
      } else {
        errorMessageComponent.setValue(null);
      }
    }
  }

  /**
   * Use this to set the colors of the Component
   * 
   * @param panelBG
   * @param panelFG
   * @param textFieldBG
   * @param textFieldFG
   */
  public void setColors(final Color panelBG, final Color panelFG, final Color textFieldBG, final Color textFieldFG) {
    if (panelBG != null) {
      this.setBackground(panelBG);
    }
    if (panelFG != null) {
      this.setForeground(panelFG);
    }
    if (textFieldBG != null) {
      displayField.setBackground(textFieldBG);
    }
    if (textFieldFG != null) {
      displayField.setForeground(textFieldFG);
    }
  }

  /**
   * Return the component's current value
   */
  public Vector<? extends Object> getValue() {
    return this.currentModel.getData();
  }

  /**
   * Return itself
   */
  public JComponent getComponent() {
    return this;
  }

  /**
   * Use this method for enabling/disabling the component depending on the
   * tree's isEditable value
   */
  public void setEnabled(final boolean isEnabled) {
    this.editButton.setEnabled(isEnabled);
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
   * Helper method to create the pane and Dialog.
   * 
   * @return
   */

  private void createDialog() {
    // Create Dialog
    if (editDialog != null) {
      editDialog.dispose();
    }
    editDialog = new JDialog();
    editDialog.setAlwaysOnTop(true);
    editDialog.setTitle(caption);
    editDialog.setModal(true);

    // Create table
    editTable = new JTable(currentModel.getTableModel());
    editTable.setVisible(true);
    editTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    editTable.addMouseListener(this);
    editTable.getTableHeader().addMouseListener(this);
    editTable.setEnabled(this.editable);

    // Set editor and renderer for the table columns
    currentModel.setColumnEditors(editTable);
    currentModel.setColumnRenderers(editTable);

    editTable.getModel().addTableModelListener(new TableComponentTableModelListener());

    editTable.setRowHeight(22);

    // Create scroll panel for table
    final JScrollPane editPane = new JScrollPane(editTable);

    if (application != null) {
      // Create buttons
      errorMessageComponent = new ErrorMessageComponent(application);
      errorMessageComponent.setValue(null);
    }
    ok = new JButton("Ok");
    cancel = new JButton("Cancel");
    ok.setPreferredSize(cancel.getPreferredSize());
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setName(caption + "OkButton");
    ok.setEnabled(this.editable);
    cancel.setActionCommand(CANCEL);
    cancel.addActionListener(this);
    cancel.setName(caption + "CancelButton");

    // Layout the contents of the Dialog
    final GridBagLayout gbl = new GridBagLayout();
    final GridBagConstraints gbc = new GridBagConstraints();
    editDialog.setLayout(gbl);
    editDialog.setBackground(Color.white);

    // Place the scroll panel in the layout
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 0;
    gbc.insets = new Insets(0, 0, 0, 0);
    gbl.setConstraints(editPane, gbc);

    editDialog.add(editPane);

    // Place the buttons in the layout
    gbc.gridwidth = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridy = 1;

    final JPanel jp = new JPanel();

    if (errorMessageComponent != null) {
      jp.add(errorMessageComponent, gbc);
    }

    jp.add(ok, gbc);
    gbc.anchor = GridBagConstraints.EAST;
    jp.add(cancel, gbc);

    gbl.setConstraints(jp, gbc);
    editDialog.add(jp);

    /*
     * gbl.setConstraints(ok, gbc); editDialog.add(ok);
     * 
     * 
     * gbl.setConstraints(errorMessageComponent, gbc);
     * editDialog.add(errorMessageComponent);
     * 
     * gbc.anchor = GridBagConstraints.EAST;
     * 
     * gbl.setConstraints(cancel, gbc); editDialog.add(cancel);
     */

    /*
     * if (currentModel.getData().size() == 0) { final Object toBeInserted =
     * currentModel.createNew(); currentModel.insertDataLast(toBeInserted); }
     */
    // And we're done
    editDialog.pack();
    Utils.center(editDialog);
    editDialog.setVisible(true);
  }

  public void mouseClicked(final MouseEvent me) {
    if (editable) {
      if (me.getButton() == 3) {
        final TableComponentListener listener = new TableComponentListener(editDialog, currentModel, editTable);
        final JPopupMenu currentmenu = this.currentModel.getPopUpMenu(listener, me.getComponent());
        if (currentmenu != null) {
          currentmenu.show(me.getComponent(), me.getX(), me.getY());
        }
        me.getComponent().repaint();
      }
    }
  }

  public void mouseEntered(final MouseEvent me) {
    // Nothing TO DO
  }

  public void mouseExited(final MouseEvent me) {
    // Nothing TO DO
  }

  public void mousePressed(final MouseEvent me) {
    // Nothing TO DO
  }

  public void mouseReleased(final MouseEvent me) {
    // Nothing TO DO
  }

  /**
   * Used for updating the displayed value in the text field after the model has
   * changed.
   */
  public void updateDisplayField() {
    displayField.setText(getModelAsString(currentModel));
  }

  /**
   * Return this component's edit button
   * 
   * @return the edit button
   */
  public JButton getEditButton() {
    return editButton;
  }

  /**
   * Return this component's text field
   * 
   * @return the text field
   */
  public JTextField getTextField() {
    return displayField;
  }
}
