package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel.BusyHourRankTables;


@SuppressWarnings("serial")
public class RankTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  // private static final Logger logger =
  // Logger.getLogger(RankTableCellEditor.class.getName());
  
  private DataModelController dataModelController;

  private boolean editable;

  // The editor will show the same components inside the cell as the renderer.
  // Create a panel and a text field and an edit button.
  private JPanel panel = new JPanel(new GridBagLayout());

  private JTextField textField = new JTextField("dummy");

  private JButton editButton = new JButton("...");

  private JComboBox targetTechPackCB;

  String oldTargetversionid = "";
  
  String oldRanktable = "";

  /**
   * Constants for the button and text field actions used.
   */
  public static final String EDITTEXT = "edittext";

  public static final String TYPECHANGED = "typechanged";

  public static final String RANKTABLECHANGED = "ranktablechanged";

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

  private JComboBox ranktableCB;

  private List<BusyHourRankTables> allranktables;

  /**
   * The edit pane contained within the edit frame. Used to set and get the
   * data.
   */
  // private JEditorPane editPane = null;
  /**
   * Constructor. Initiates the text field and the edit button in the table
   * cell.
   */
  public RankTableCellEditor(DataModelController dataModelController, boolean editable) {

    GridBagConstraints gbc = null;

    this.dataModelController = dataModelController;
    this.editable = editable;

    // Configure the JPanel
    panel.setBackground(Color.white);

    // Configure the edit button and text box action commands and listeners.
    editButton.setActionCommand(EDIT);
    editButton.addActionListener(this);

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
    textField.setText((String) originalBusyhour.getBhlevel());

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

        modifiedBusyhour.setBhlevel(textField.getText());
        originalBusyhour = modifiedBusyhour;
        if(editorDialog != null){
        editorDialog.setVisible(false);
        editorDialog.dispose();
        editorDialog = null;
        }
        fireEditingStopped();
      } else if (e.getActionCommand().equals(TYPECHANGED)) {
        if (!targetTechPackCB.getSelectedItem().equals(oldTargetversionid)) {
          modifiedBusyhour.setTargetversionid((String) targetTechPackCB.getSelectedItem());
          modifiedBusyhour.setBhlevel("");
          oldTargetversionid = modifiedBusyhour.getTargetversionid();
          String[] techpackRanktables;
          int selectedInd = targetTechPackCB.getSelectedIndex();
          if (selectedInd > -1) {
            BusyHourRankTables selecteditem = allranktables.get(selectedInd);
            techpackRanktables = new String[selecteditem.getRanktables().size()];
            int cnt2 = 0;
            for (Iterator<String> iter = selecteditem.getRanktables().iterator(); iter.hasNext(); ) {
              techpackRanktables[cnt2] = iter.next();
              cnt2++;
            }
          } else {
            techpackRanktables = new String[0];
          }
          ranktableCB.removeAllItems();
          ComboBoxModel model = new DefaultComboBoxModel(techpackRanktables);
          ranktableCB.setModel(model);
          ranktableCB.revalidate();
          ranktableCB.setSelectedIndex(-1);
        }
      } else if (e.getActionCommand().equals(RANKTABLECHANGED)) {
        if (ranktableCB.getSelectedIndex() < 0) {
          modifiedBusyhour.setBhlevel("");
          oldRanktable = modifiedBusyhour.getBhlevel();
        } else if (!ranktableCB.getSelectedItem().equals(oldRanktable)) {
          modifiedBusyhour.setBhlevel((String) ranktableCB.getSelectedItem());
          oldRanktable = modifiedBusyhour.getBhlevel();
        }
      } else if (e.getActionCommand().equals(EDIT)) {
        // Starting to edit the cell.
        // Create the edit frame and show the frame.
        editorDialog = createDialog();
        editorDialog.setVisible(true);
        oldTargetversionid = modifiedBusyhour.getTargetversionid();
        oldRanktable = modifiedBusyhour.getBhlevel();
      } else if (OK.equals(e.getActionCommand())) {
        // Editing done, OK clicked.
        // Store the data, hide and dispose the frame.
        modifiedBusyhour.setTargetversionid((String) targetTechPackCB.getSelectedItem());
        modifiedBusyhour.setBhlevel((String) ranktableCB.getSelectedItem());
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
  protected JDialog createDialog() {

    // Create frame
    JDialog thisDialog = new JDialog();
    thisDialog.setAlwaysOnTop(true);
    thisDialog.setTitle("Rank Table");
    thisDialog.setModal(true);
    thisDialog.setName("RankTableCellEditorDialog");

    // Layout the contents of the frame
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    thisDialog.setLayout(gbl);

    // textfield
    // place the chooser to layout

    JLabel name = new JLabel("Target TechPack");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 0;
    gbc.insets = new Insets(30, 30, 30, 0);
    gbl.setConstraints(name, gbc);
    thisDialog.add(name);

    allranktables = dataModelController.getBusyhourHandlingDataModel().getAllBusyHourRankTables();
    String[] techpacks = new String[allranktables.size()];
    int cnt = 0;
    for (Iterator<BusyHourRankTables> iter = allranktables.iterator(); iter.hasNext(); ) {
      BusyHourRankTables item = iter.next();
      techpacks[cnt] = item.getVersionid();
      cnt++;
    }
    
    targetTechPackCB = new JComboBox(techpacks);
    targetTechPackCB.setActionCommand(TYPECHANGED);
    targetTechPackCB.setEnabled(true);
    targetTechPackCB.setName("RankTableCellEditorTargetTechPackComboBox");

    int selectedInd = -1;
    for (int ind = 0; ind < targetTechPackCB.getItemCount(); ind++) {
      if (targetTechPackCB.getItemAt(ind).equals(originalBusyhour.getTargetversionid())) {
        selectedInd = ind;
      }
    }
    targetTechPackCB.setSelectedIndex(selectedInd);
    
    gbc.insets = new Insets(30, 10, 30, 30);
    gbc.gridx = 1;
    gbc.gridy = 0;

    gbl.setConstraints(targetTechPackCB, gbc);
    thisDialog.add(targetTechPackCB);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    String[] techpackRanktables;
    if (selectedInd > -1) {
      BusyHourRankTables selecteditem = allranktables.get(selectedInd);
      techpackRanktables = new String[selecteditem.getRanktables().size()];
      int cnt2 = 0;
      for (Iterator<String> iter = selecteditem.getRanktables().iterator(); iter.hasNext(); ) {
        techpackRanktables[cnt2] = iter.next();
        cnt2++;
      }
    } else {
      techpackRanktables = new String[0];
    }
    ranktableCB = new JComboBox();
    ComboBoxModel model = new DefaultComboBoxModel(techpackRanktables);
    ranktableCB.setModel(model);
    ranktableCB.revalidate();
    ranktableCB.setActionCommand(RANKTABLECHANGED);
    ranktableCB.setEnabled(editable);
    ranktableCB.setName("RankTableCellEditorComboBox");
    
    selectedInd = -1;
    for (int ind = 0; ind < ranktableCB.getItemCount(); ind++) {
      if (ranktableCB.getItemAt(ind).equals(originalBusyhour.getBhlevel())) {
        selectedInd = ind;
      }
    }
    ranktableCB.setSelectedIndex(selectedInd);
    
    targetTechPackCB.addActionListener(this);
    ranktableCB.addActionListener(this);
    
    
    JLabel rname = new JLabel("Rank Table");
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weighty = 0;
    gbc.insets = new Insets(30, 30, 30, 0);
    gbl.setConstraints(rname, gbc);
    thisDialog.add(rname);
    
    // Place the pane in the layout
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 2;
    gbc.gridx = 1;
    gbl.setConstraints(ranktableCB, gbc);
    thisDialog.add(ranktableCB);

    // Create buttons
    JButton ok = new JButton("Save");
    JButton cancel = new JButton("Cancel");

    ok.setPreferredSize(cancel.getPreferredSize());
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setEnabled(editable);
    ok.setName("RankTableCellEditorSaveButton");

    cancel.setActionCommand(CANCEL);
    cancel.addActionListener(this);
    cancel.setName("RankTableCellEditorCancelButton");
    // Place the buttons in the layout
    gbc.fill = GridBagConstraints.NONE;

    gbc.gridwidth = 1;
    gbc.weighty = 0;
    gbc.weightx = 0;
    gbc.gridy = 3;
    gbc.gridx = 2;
    gbl.setConstraints(ok, gbc);
    thisDialog.add(ok);

    gbc.gridy = 3;
    gbc.gridx = 3;
    gbl.setConstraints(cancel, gbc);
    thisDialog.add(cancel);

    // And we're done
    thisDialog.pack();
    Utils.center(thisDialog);
    return thisDialog;

  }

}
