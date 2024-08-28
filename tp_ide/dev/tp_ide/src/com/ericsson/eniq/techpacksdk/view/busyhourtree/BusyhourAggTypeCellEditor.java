package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.ericsson.eniq.techpacksdk.LimitedRangeIntegerField;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class BusyhourAggTypeCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  // private static final Logger logger =
  // Logger.getLogger(RankTableCellEditor.class.getName());

  private boolean editable;

  // The editor will show the same components inside the cell as the renderer.
  // Create a panel and a text field and an edit button.
  private JPanel panel = new JPanel(new GridBagLayout());

  private JTextField textField = new JTextField("dummy");

  private JButton editButton = new JButton("...");

  private JComboBox aggregationTypeCB;

  // private JTextField r11 = new JTextField();
  private LimitedRangeIntegerField r21 = new LimitedRangeIntegerField(0,0,false);

  private LimitedRangeIntegerField r31 = new LimitedRangeIntegerField(0,0,false);

  private LimitedRangeIntegerField r41 = new LimitedRangeIntegerField(0,0,false);

  private LimitedRangeIntegerField r51 = new LimitedRangeIntegerField(0,0,false);

  String oldAggregationType = "";

  String curAggregationType = "";

  //Constraints for limits set...
  private static final int LOOKBACK_LIMIT = 90;
  
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
  private Busyhour originalBusyhour;

  private Busyhour modifiedBusyhour;

  /**
   * The edit frame that is displayed when the edit action is triggered.
   */
  protected JDialog editorDialog = null;

  /**
   * The edit pane contained within the edit frame. Used to set and get the
   * data.
   */
  // private JEditorPane editPane = null;
  /**
   * Constructor. Initiates the text field and the edit button in the table
   * cell.
   */
  public BusyhourAggTypeCellEditor(DataModelController dataModelController, boolean editable) {

    GridBagConstraints gbc = null;

    this.editable = editable;

    // Configure the JPanel
    panel.setBackground(Color.white);

    // Configure the edit button and text box action commands and listeners.
    editButton.setActionCommand(EDIT);
    editButton.addActionListener(this);
    editButton.setName("Editbutton");

    textField.setActionCommand(EDITTEXT);
    textField.addActionListener(this);
    textField.setEnabled(editable);

    // Configure the layout and add the text field and the button to the
    // panel.

    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;

    // Text field
    textField.setBorder(null);
    textField.addActionListener(this);

    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 2;
    gbc.gridy = 0;
    panel.add(textField, gbc);

    // Edit button
    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 3;
    gbc.gridy = 0;
    panel.add(editButton, gbc);
  }

  /**
   * This returns the value to the edited cell when fireEditingStopped has been
   * called.
   */
  public Object getCellEditorValue() {
    return originalBusyhour;
  }

  /**
   * This returns the panel as the editor to the table. This is also where we
   * get the initial value for the text field.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {

    // Store the cell value for when we start editing
    originalBusyhour = (Busyhour) value;
    modifiedBusyhour = (Busyhour) originalBusyhour.clone();
    textField.setText(Utils.showAggtypeGUI(originalBusyhour.getAggregationtype()));
    curAggregationType = originalBusyhour.getAggregationtype();

    return panel;
  }

  /**
   * This is the listener callback for the three buttons (the panels edit
   * button, and the dialog's OK and CANCEL buttons) and the text field editing
   * done action.
   */
  public void actionPerformed(ActionEvent e) {

    try {

      if (e.getActionCommand().equals("edittext")) {
        // Editing done, enter pressed.
        // Store the data, hide and dispose the frame.

        modifiedBusyhour.setAggregationtype(textField.getText());
        originalBusyhour = modifiedBusyhour;
        if (editorDialog != null) {
          editorDialog.setVisible(false);
          editorDialog.dispose();
          editorDialog = null;

        }
        fireEditingStopped();
      } else if (e.getActionCommand().equals(TYPECHANGED)) {

        setVisibility();

      } else if (e.getActionCommand().equals(EDIT)) {
        // Starting to edit the cell.
        // Create the edit frame and show the frame.
        editorDialog = createDialog();
        editorDialog.setVisible(true);
        oldAggregationType = modifiedBusyhour.getAggregationtype();
        curAggregationType = modifiedBusyhour.getAggregationtype();
      } else if (OK.equals(e.getActionCommand())) {
        // Editing done, OK clicked.
        // Store the data, hide and dispose the frame.
        modifiedBusyhour.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[aggregationTypeCB.getSelectedIndex()]);
        // modifiedBusyhour.setWindowsize(Utils.stringToInteger(r11.getText()));
        if(curAggregationType.equals(Constants.BH_TYPE_PR)){
        	modifiedBusyhour.setWindowsize(15);
        }else{
        	modifiedBusyhour.setWindowsize(60);
        }
        
        //If either Slidingwindow or Slidingwindow + TimeConsistant is selected set the offset to 15 minutes.
        if(curAggregationType.equals(Constants.BH_TYPE_SW) || curAggregationType.equals(Constants.BH_TYPE_SW_TC)){
          modifiedBusyhour.setOffset(Constants.SLIDING_WINDOW_OFFSET);
        }else if(curAggregationType.equals(Constants.BH_TYPE_PR)){ // If Peakrop selected set the offset to 15 minutes
        	modifiedBusyhour.setOffset(Constants.PEAKROP_OFFSET);
        }
        else{
          modifiedBusyhour.setOffset(0);
        }
        
        modifiedBusyhour.setLookback(Utils.stringToInteger(r31.getText()));
        modifiedBusyhour.setP_threshold(Utils.stringToInteger(r41.getText()));
        modifiedBusyhour.setN_threshold(Utils.stringToInteger(r51.getText()));
        modifiedBusyhour.setReactivateviews(1);
        originalBusyhour = modifiedBusyhour;
        editorDialog.setVisible(false);
        editorDialog.dispose();
        editorDialog = null;
        fireEditingStopped();
      } else { // CANCEL
        if (editorDialog != null) {
          editorDialog.setVisible(false);
          editorDialog.dispose();
          editorDialog = null;
        }
        fireEditingCanceled();
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void setVisibility() {

    if (!aggregationTypeCB.getSelectedItem().equals(oldAggregationType)) {

      if (!aggregationTypeCB.getSelectedItem().equals(curAggregationType)) {
        // type has been changed -> clear parameters
        r21.setText("");
        r31.setText("");
        r41.setText("");
        r51.setText("");
        curAggregationType = (String) aggregationTypeCB.getSelectedItem();
      }

      if (aggregationTypeCB.getSelectedIndex() == 0) {
        // r11.setEnabled(false);
        // r11.setText("");
        r21.setText("");
        r21.setEnabled(false);

        r31.setText("");
        r31.setEnabled(false);
        
        r41.setText("");
        r41.setEnabled(false);

        r51.setText("");
        r51.setEnabled(false);
      }
      if (aggregationTypeCB.getSelectedIndex() == 1) {
        // r11.setEnabled(true);
        r21.setEnabled(true);

        r31.setText("");
        r31.setEnabled(false);
        
        r41.setText("");
        r41.setEnabled(false);

        r51.setText("");
        r51.setEnabled(false);

      }
      if (aggregationTypeCB.getSelectedIndex() == 2) {
        // r11.setEnabled(false);
        // r11.setText("");
    	  
        r21.setText("");
        r21.setEnabled(false);

        r31.setEnabled(true);
        r41.setEnabled(true);
        r51.setEnabled(true);
      }
      if (aggregationTypeCB.getSelectedIndex() == 3) {
        // r11.setEnabled(true);
        r21.setEnabled(true);
        r31.setEnabled(true);
        r41.setEnabled(true);
        r51.setEnabled(true);
      }
      if (aggregationTypeCB.getSelectedIndex() == 4) {
          
          r21.setEnabled(true);

          r31.setText("");
          r31.setEnabled(false);
          
          r41.setText("");
          r41.setEnabled(false);

          r51.setText("");
          r51.setEnabled(false);

        }
      }
  }

  /**
   * Helper method to create the pane and frame.
   * 
   * @return
   */
  protected JDialog createDialog() {

    // Create frame
    JDialog thisDialog = new JDialog();
    thisDialog.setAlwaysOnTop(true);
    thisDialog.setTitle("Busy Hour Type");
    thisDialog.setModal(true);
    thisDialog.setName("BusyhourAggTypeCellEditor");

    // Layout the contents of the frame
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    thisDialog.setLayout(gbl);

    // textfield
    // place the chooser to layout

    JLabel name = new JLabel("Busy Hour Type");
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 0;
    gbc.insets = new Insets(10, 30, 10, 30);
    gbl.setConstraints(name, gbc);
    thisDialog.add(name);

    aggregationTypeCB = new JComboBox(Constants.BH_AGGREGATION_TYPPES_SHOW);
    aggregationTypeCB.setActionCommand(TYPECHANGED);
    aggregationTypeCB.addActionListener(this);
    aggregationTypeCB.setSelectedItem(Utils.showAggtypeGUI(modifiedBusyhour.getAggregationtype()));
    aggregationTypeCB.setName("BusyhourAggTypeCellEditorAggregationType");
    aggregationTypeCB.setEnabled(editable);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 0;
    gbl.setConstraints(aggregationTypeCB, gbc);
    thisDialog.add(aggregationTypeCB);

    curAggregationType = (String) aggregationTypeCB.getSelectedItem();


    JLabel r3 = new JLabel("Lookback");
    r3.setToolTipText("Set loockback value (max: "+LOOKBACK_LIMIT+" days");
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.weighty = 0;
    gbl.setConstraints(r3, gbc);
    thisDialog.add(r3);

    r31 = new LimitedRangeIntegerField(5, 1, LOOKBACK_LIMIT, true);
    r31.setToolTipText("Set loockback value (max: "+LOOKBACK_LIMIT+" days");
    r31.setText("" + Utils.replaceNull(modifiedBusyhour.getLookback()));
    r31.setName("lookback");
    r31.setEnabled(editable);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 4;
    gbc.gridx = 1;
    gbc.weighty = 0;
    gbl.setConstraints(r31, gbc);
    thisDialog.add(r31);

    JLabel r4 = new JLabel("P Threshold");
    r4.setToolTipText("Set positive threshold value");
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 5;
    gbc.gridx = 0;
    gbc.weighty = 0;
    gbl.setConstraints(r4, gbc);
    thisDialog.add(r4);

    r41 = new LimitedRangeIntegerField(5, 0, Integer.MAX_VALUE, true);
    r41.setToolTipText("Set positive threshold value");
    r41.setText("" + Utils.replaceNull(modifiedBusyhour.getP_threshold()));
    r41.setName("pthreshold");
    r41.setEnabled(editable);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 5;
    gbc.gridx = 1;
    gbc.weighty = 0;
    gbl.setConstraints(r41, gbc);
    thisDialog.add(r41);

    JLabel r5 = new JLabel("N Threshold");
    r5.setToolTipText("Set negative threshold value");
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 6;
    gbc.gridx = 0;
    gbc.weighty = 0;
    gbl.setConstraints(r5, gbc);
    thisDialog.add(r5);

    r51 = new LimitedRangeIntegerField(5, 0, Integer.MAX_VALUE, true);
    r51.setToolTipText("Set negative threshold value");
    r51.setText("" + Utils.replaceNull(modifiedBusyhour.getN_threshold()));
    r51.setName("nthreshold");
    r51.setEnabled(editable);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 6;
    gbc.gridx = 1;
    gbc.weighty = 0;
    gbl.setConstraints(r51, gbc);
    thisDialog.add(r51);

    // Create buttons
    JButton ok = new JButton("Save");
    JButton cancel = new JButton("Cancel");

    ok.setPreferredSize(cancel.getPreferredSize());
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setEnabled(editable);
    ok.setName("BusyhourAggTypeCellEditorSaveButton");

    cancel.setActionCommand(CANCEL);
    cancel.addActionListener(this);
    cancel.setName("BusyhourAggTypeCellEditorCancelButton");
    
    // Place the buttons in the layout
      
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
         
    gbc.gridy = 0;
    gbc.gridx = 0;
    gbl.setConstraints(ok, gbc);
    buttonPanel.add(ok);

    gbc.gridy = 1;
    gbc.gridx = 0;
    gbl.setConstraints(cancel, gbc);
    buttonPanel.add(cancel);
      
    gbc.gridwidth = 1;
    gbc.weighty = 0;
    gbc.weightx = 2;
    gbc.gridy = 7;
    gbc.gridx = 1;
    gbl.setConstraints(buttonPanel, gbc);
    thisDialog.add(buttonPanel);
    
    setVisibility();

    // And we're done
    thisDialog.pack();
    Utils.center(thisDialog);
    return thisDialog;

  }

}
