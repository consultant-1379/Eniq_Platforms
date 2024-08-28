package com.ericsson.eniq.techpacksdk.view.transformer;

import static com.ericsson.eniq.techpacksdk.common.Constants.ALARM;
import static com.ericsson.eniq.techpacksdk.common.Constants.BITMAPLOOKUP;
import static com.ericsson.eniq.techpacksdk.common.Constants.CALCULATION;
import static com.ericsson.eniq.techpacksdk.common.Constants.CONDITION;
import static com.ericsson.eniq.techpacksdk.common.Constants.CURRENTTIME;
import static com.ericsson.eniq.techpacksdk.common.Constants.DATABASELOOKUP;
import static com.ericsson.eniq.techpacksdk.common.Constants.DATEFORMAT;
import static com.ericsson.eniq.techpacksdk.common.Constants.DEFAULTTIMEHANDLER;
import static com.ericsson.eniq.techpacksdk.common.Constants.DSTPARAMETERS;
import static com.ericsson.eniq.techpacksdk.common.Constants.FIELDTOKENIZER;
import static com.ericsson.eniq.techpacksdk.common.Constants.FIXED;
import static com.ericsson.eniq.techpacksdk.common.Constants.LOOKUP;
import static com.ericsson.eniq.techpacksdk.common.Constants.POSTAPPENDER;
import static com.ericsson.eniq.techpacksdk.common.Constants.PREAPPENDER;
import static com.ericsson.eniq.techpacksdk.common.Constants.PROPERTYTOKENIZER;
import static com.ericsson.eniq.techpacksdk.common.Constants.RADIXCONVERTER;
import static com.ericsson.eniq.techpacksdk.common.Constants.REDUCEDATE;
import static com.ericsson.eniq.techpacksdk.common.Constants.ROPTIME;
import static com.ericsson.eniq.techpacksdk.common.Constants.ROUNDTIME;
import static com.ericsson.eniq.techpacksdk.common.Constants.SWITCH;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.view.transformationViews.AlarmView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.BitmapLookupView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.CalculationView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.ConditionView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.CurrentTimeView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.DatabaseLookupView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.DateformatView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.DefaultTimeHandlerView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.DstParametersView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.FieldTokenizerView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.FixedView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.GenericTransformationView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.LookupView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.PostAppenderView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.PreAppenderView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.PropertyTokenizerView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.ROPTimeView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.RadixConverterView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.ReduceDateView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.RoundtimeView;
import com.ericsson.eniq.techpacksdk.view.transformationViews.SwitchView;

public class ConfigTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  // private static final Logger logger =
  // Logger.getLogger(ConfigTableCellEditor.class.getName());

  // The editor will show the same components inside the cell as the renderer.
  // Create a panel and a text field and an edit button.

  private JPanel panel = new JPanel(new GridBagLayout());

  private JTextField textField = new JTextField("dummy");

  private JButton editButton = new JButton("...");

  private GenericTransformationView tView;

  private JPanel p;

  private JComboBox tTypeCB;

  private String oldType = "";

  private boolean active = false;

  /**
   * Constants for the button and text field actions used.
   */
  public static final String EDITTEXT = "edittext";

  public static final String TYPECHANGED = "typechanged";

  public static final String EDIT = "edit";

  public static final String OK = "ok";

  public static final String CANCEL = "cancel";

  /**
   * The value of the edited data
   */
  private Transformation origTransformation;

  private Transformation transformation;

  /**
   * The edit frame that is displayed when the edit action is triggered. The
   * frame is static so that only one frame can be opened for the table row at a
   * time.
   */
  protected static JFrame editFrame = null;

  private boolean editable;

  /**
   * The edit pane contained within the edit frame. Used to set and get the
   * data.
   */
  // private JEditorPane editPane = null;
  /**
   * Constructor. Initiates the text field and the edit button in the table
   * cell.
   */
  public ConfigTableCellEditor(boolean editable) {

    GridBagConstraints gbc = null;

    this.editable = editable;

    // Configure the JPanel
    panel.setBackground(Color.white);

    // Configure the edit button and text box action commands and listeners.
    editButton.setActionCommand(EDIT);
    editButton.addActionListener(this);

    textField.setActionCommand(EDITTEXT);
    textField.addActionListener(this);
    textField.setEnabled(false);

    // Configure the layout and add the text field and the button to the
    // panel.

    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;

    // Text field
    textField.setBorder(null);

    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 2;
    gbc.gridy = 0;
    // gbc.insets = new Insets(2, 2, 2, 2);
    panel.add(textField, gbc);

    // Edit button
    // editButton.setBorderPainted(false);
    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    // gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 3;
    gbc.gridy = 0;
    // gbc.insets = new Insets(2, 2, 2, 2);
    panel.add(editButton, gbc);
  }

  /**
   * This returns the value to the edited cell when fireEditingStopped has been
   * called.
   */
  public Object getCellEditorValue() {
    return origTransformation;
  }

  /**
   * This returns the panel as the editor to the table. This is also where we
   * get the initial value for the text field.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {

    if (active) {
      active = false;
    }

    // Store the cell value for when we start editing
    origTransformation = (Transformation) value;
    transformation = (Transformation) origTransformation.clone();
    textField.setText((String) origTransformation.getConfig());

    return panel;
  }

  /**
   * This is the listener callback for the three buttons (the panels edit
   * button, and the dialog's OK and CANCEL buttons) and the text field editing
   * done action.
   */
  public void actionPerformed(ActionEvent e) {

    try {

      if (e.getActionCommand().equals(TYPECHANGED)) {
        if (!tTypeCB.getSelectedItem().equals(oldType)) {

          if (active && oldType.trim().length() > 0) {
            transformation.setConfig("");
          }

          transformation.setType((String) tTypeCB.getSelectedItem());
          tView = createTransformationView(p, transformation);
          oldType = (String) tTypeCB.getSelectedItem();
          if (editFrame != null)
            editFrame.pack();

        }
      } else if (e.getActionCommand().equals(EDIT)) {
        // Starting to edit the cell.
        // Create the edit frame and show the frame.
        editFrame = createFrame();
        editFrame.setVisible(true);
        oldType = transformation.getType().toLowerCase();
        active = true;
      } else if (OK.equals(e.getActionCommand())) {
        // Editing done, OK clicked.
        // Store the data, hide and dispose the frame.
        String error = tView.validate();
        if (!error.equalsIgnoreCase("")) {

          JOptionPane.showMessageDialog(null, error);
          return;

        }
        transformation.setType((String) tTypeCB.getSelectedItem());
        transformation.setConfig(tView.getContent());
        origTransformation = transformation;
        editFrame.setVisible(false);
        editFrame.dispose();
        editFrame = null;
        fireEditingStopped();
      } else { // CANCEL
        if (editFrame != null) {
          editFrame.setVisible(false);
          editFrame.dispose();
          editFrame = null;
        }
        fireEditingStopped();
      }

    } catch (Exception ex) {
      ExceptionHandler.instance().handle(ex);
      ex.printStackTrace();
    }
  }

  /**
   * Helper method to create the pane and frame.
   *
   * @return
   */
  protected JFrame createFrame() {

    // This is done so that only one frame can be visible at
    // any one time
    if (editFrame != null) {
      editFrame.dispose();
    }

    // Create frame
    JFrame thisFrame = new JFrame("Edit config of Transformation: " + transformation.getTransformerid());

    // Layout the contents of the frame
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    thisFrame.setLayout(gbl);

    p = new JPanel();
    p.setLayout(gbl);

    tView = createTransformationView(p, transformation);
    p.setEnabled(editable);

    // textfield
    // place the chooser to layout

    JLabel name = new JLabel("Transformation Type");
    gbc.gridx = 0;
    gbc.gridy = 0;
    // gbc.weightx = 1;
    gbc.weighty = 0;
    gbc.insets = new Insets(30, 30, 30, 0);
    gbl.setConstraints(name, gbc);
    thisFrame.add(name);

    tTypeCB = new JComboBox(Constants.TRANFORMER_TYPES);
    tTypeCB.setActionCommand(TYPECHANGED);
    tTypeCB.addActionListener(this);
    tTypeCB.setSelectedItem(transformation.getType().toLowerCase());
    tTypeCB.setEnabled(editable);

    gbc.insets = new Insets(30, 10, 30, 30);
    gbc.gridx = 1;
    gbc.gridy = 0;

    gbl.setConstraints(tTypeCB, gbc);
    thisFrame.add(tTypeCB);

    // Place the pane in the layout
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0, 30, 30, 30);
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridwidth = 4;
    gbl.setConstraints(p, gbc);
    JScrollPane sp = new JScrollPane(p);
    gbl.setConstraints(sp, gbc);
    thisFrame.add(sp);

    // Create buttons
    JButton ok = new JButton("Save");
    JButton cancel = new JButton("Cancel");

    ok.setPreferredSize(cancel.getPreferredSize());
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setEnabled(editable);

    cancel.setActionCommand(CANCEL);
    cancel.addActionListener(this);

    // Place the buttons in the layout
    gbc.fill = GridBagConstraints.NONE;

    gbc.gridwidth = 1;
    gbc.weighty = 0;
    gbc.weightx = 0;
    gbc.gridy = 3;
    gbc.gridx = 2;
    gbl.setConstraints(ok, gbc);
    thisFrame.add(ok);

    gbc.gridy = 3;
    gbc.gridx = 3;
    gbl.setConstraints(cancel, gbc);
    thisFrame.add(cancel);

    // And we're done
    thisFrame.pack();
    return thisFrame;

  }

  /**
   * Returns calculation type spesific view
   *
   * @param parent
   * @param transfromation
   * @return
   */
  private GenericTransformationView createTransformationView(JPanel parent, Transformation transfromation) {

    if (transformation.getType().equalsIgnoreCase(CALCULATION))
      return new CalculationView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(CURRENTTIME))
      return new CurrentTimeView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(FIXED))
      return new FixedView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(LOOKUP))
      return new LookupView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(CONDITION))
      return new ConditionView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(POSTAPPENDER))
      return new PostAppenderView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(PROPERTYTOKENIZER))
      return new PropertyTokenizerView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(PREAPPENDER))
      return new PreAppenderView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(DSTPARAMETERS))
      return new DstParametersView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(DATABASELOOKUP))
      return new DatabaseLookupView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(DATEFORMAT))
      return new DateformatView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(DEFAULTTIMEHANDLER))
      return new DefaultTimeHandlerView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(ALARM))
      return new AlarmView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(BITMAPLOOKUP))
      return new BitmapLookupView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(REDUCEDATE))
      return new ReduceDateView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(FIELDTOKENIZER))
      return new FieldTokenizerView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(ROUNDTIME))
      return new RoundtimeView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(SWITCH))
      return new SwitchView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(RADIXCONVERTER))
      return new RadixConverterView(parent, transfromation);

    if (transformation.getType().equalsIgnoreCase(ROPTIME))
        return new ROPTimeView(parent, transfromation);

    return new GenericTransformationView(parent, transfromation);
  }
}
