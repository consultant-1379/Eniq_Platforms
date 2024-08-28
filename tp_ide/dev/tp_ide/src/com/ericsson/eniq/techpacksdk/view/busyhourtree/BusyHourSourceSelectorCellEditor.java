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

import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel.BusyHourSourceTables;

@SuppressWarnings("serial")
public class BusyHourSourceSelectorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  // private static final Logger logger =
  // Logger.getLogger(BusyHourSourceCellEditor.class.getName());

  private DataModelController dataModelController;

  private boolean editable;

  // The editor will show the same components inside the cell as the renderer.
  // Create a panel and a text field and an edit button.
  private JPanel panel = new JPanel(new GridBagLayout());

  private JTextField textField = new JTextField("dummy");

  private JButton editButton = new JButton("...");

  private JComboBox targetTechPackCB;

  String oldSourcetechpack = "";

  String oldBasetable = "";

  /**
   * Constants for the button and text field actions used.
   */
  public static final String EDITTEXT = "edittext";

  public static final String TARGETTPCHANGED = "targettpchanged";

  public static final String TYPENAMECHANGED = "typenamechanged";

  public static final String EDIT = "edit";

  public static final String OK = "ok";

  public static final String CANCEL = "cancel";

  /**
   * The value of the edited data
   */
  private String originalTypeName;

  private String originalTargetTP;

  private String modifiedTypeName;

  private String modifiedTargetTP;

  /**
   * The edit frame that is displayed when the edit action is triggered.
   */
  protected JDialog editorDialog = null;

  private JComboBox typeNameCB;

  private List<BusyHourSourceTables> allsources;

  /**
   * The edit pane contained within the edit frame. Used to set and get the
   * data.
   */
  // private JEditorPane editPane = null;
  /**
   * Constructor. Initiates the text field and the edit button in the table
   * cell.
   */
  public BusyHourSourceSelectorCellEditor(DataModelController dataModelController, boolean editable) {

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
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 2;
    gbc.gridy = 0;
    textField.addActionListener(this);
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
    return originalTargetTP + "#" + originalTypeName;
  }

  /**
   * This returns the panel as the editor to the table. This is also where we
   * get the initial value for the text field.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {

    // Store the cell value for when we start editing
    originalTypeName = (String) table.getModel().getValueAt(row, 1);
    modifiedTypeName = originalTypeName;
    originalTargetTP = (String) table.getModel().getValueAt(row, 0);
    modifiedTargetTP = originalTargetTP;
    textField.setText(originalTypeName);

    return panel;
  }

  /**
   * This is the listener callback for the three buttons (the panels edit
   * button, and the dialog's OK and CANCEL buttons) and the text field editing
   * done action.
   */
  public void actionPerformed(ActionEvent e) {

    try {

      if (e.getActionCommand().equals(EDITTEXT)) {
        // Editing done, enter pressed
        // Store the data, hide and dispose the frame.
        modifiedTypeName = textField.getText();
        originalTypeName = modifiedTypeName;
        originalTargetTP = modifiedTargetTP;
        if (editorDialog != null) {
          editorDialog.setVisible(false);
          editorDialog.dispose();
          editorDialog = null;
        }
        fireEditingStopped();
      } else if (e.getActionCommand().equals(TARGETTPCHANGED) || e.getActionCommand().equals(EDITTEXT)) {
        if (!targetTechPackCB.getSelectedItem().equals(oldSourcetechpack)) {
          oldSourcetechpack = (String) targetTechPackCB.getSelectedItem();
          modifiedTypeName = "";
          String[] techpackBasetables;
          int selectedInd = targetTechPackCB.getSelectedIndex();
          if (selectedInd > -1) {
            BusyHourSourceTables selecteditem = allsources.get(selectedInd);
            techpackBasetables = new String[selecteditem.getBasetables().size()];
            int cnt2 = 0;
            for (Iterator<String> iter = selecteditem.getBasetables().iterator(); iter.hasNext();) {
              techpackBasetables[cnt2] = iter.next();
              cnt2++;
            }
          } else {
            techpackBasetables = new String[0];
          }
          typeNameCB.removeAllItems();
          ComboBoxModel model = new DefaultComboBoxModel(techpackBasetables);
          typeNameCB.setModel(model);
          typeNameCB.revalidate();
          typeNameCB.setSelectedIndex(-1);
        }
      } else if (e.getActionCommand().equals(TYPENAMECHANGED)) {
        if (typeNameCB.getSelectedIndex() < 0) {
          modifiedTypeName = "";
          modifiedTargetTP = "";
          oldBasetable = "";
        } else if (!typeNameCB.getSelectedItem().equals(oldBasetable)) {
          modifiedTypeName = (String) typeNameCB.getSelectedItem();
          modifiedTargetTP = (String) targetTechPackCB.getSelectedItem();
          oldBasetable = modifiedTypeName;
        }
      } else if (e.getActionCommand().equals(EDIT)) {
        // Starting to edit the cell.
        // Create the edit frame and show the frame.
        editorDialog = createDialog();
        editorDialog.setVisible(true);
        oldBasetable = modifiedTypeName;
      } else if (OK.equals(e.getActionCommand())) {
        // Editing done, OK clicked.
        // Store the data, hide and dispose the frame.
        modifiedTypeName = (String) typeNameCB.getSelectedItem();
        modifiedTargetTP = (String) targetTechPackCB.getSelectedItem();

        originalTypeName = modifiedTypeName;
        originalTargetTP = modifiedTargetTP;
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
    thisDialog.setTitle("Base Table");
    thisDialog.setName("BusyHourSourceSelectorCellEditorBaseTableDialog");
    thisDialog.setModal(true);

    // Layout the contents of the frame
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    thisDialog.setLayout(gbl);

    // textfield
    // place the chooser to layout

    // Add: From TechPack Label
    JLabel fromTechPackLabel = new JLabel("From TechPack");
    gbc.gridx = 0;
    gbc.gridy = 0;
    // gbc.weighty = 0;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(30, 30, 30, 0);
    gbl.setConstraints(fromTechPackLabel, gbc);
    thisDialog.add(fromTechPackLabel);

    allsources = dataModelController.getBusyhourHandlingDataModel().getAllBusyHourSources();
    String[] installedtechpacks = new String[allsources.size()];
    int cnt = 0;
    String currentSelectedTp = originalTargetTP;

    for (Iterator<BusyHourSourceTables> iter = allsources.iterator(); iter.hasNext();) {
      BusyHourSourceTables item = iter.next();
      installedtechpacks[cnt] = item.getVersionid();
      /*
       * Vector<String> basetables = item.getBasetables(); for (Iterator<String>
       * iter2 = basetables.iterator(); iter2.hasNext();) { String item2 =
       * iter2.next(); if (item2 != null && item2.equals(originalBasetable)) {
       * currentSelectedTp = item.getVersionid(); } }
       */
      cnt++;
    }

    // Add: Target TechPack ComboBox
    targetTechPackCB = new JComboBox(installedtechpacks);
    targetTechPackCB.setActionCommand(TARGETTPCHANGED);
    targetTechPackCB.setEnabled(editable);
    targetTechPackCB.setName("BusyHourSourceSelectorCellEditorBaseTableTargetTechPackComboBox");

    int selectedInd = -1;
    if (currentSelectedTp != null) {
      for (int ind = 0; ind < targetTechPackCB.getItemCount(); ind++) {
        if (targetTechPackCB.getItemAt(ind) != null
            && ((String) targetTechPackCB.getItemAt(ind)).equalsIgnoreCase(currentSelectedTp)) {
          selectedInd = ind;
        }
      }
    }
    targetTechPackCB.setSelectedIndex(selectedInd);
    oldSourcetechpack = (String) targetTechPackCB.getSelectedItem();

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.insets = new Insets(30, 10, 30, 30);
    gbl.setConstraints(targetTechPackCB, gbc);
    thisDialog.add(targetTechPackCB);

    // Add: TypeName Label
    JLabel typeNameLabel = new JLabel("Type Name");
    gbc.gridx = 0;
    gbc.gridy = 1;
    // gbc.weighty = 0;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(30, 30, 30, 0);
    gbl.setConstraints(typeNameLabel, gbc);
    thisDialog.add(typeNameLabel);

    // Add: TypeName ComboBox
    String[] techpackBasetables;
    if (selectedInd > -1) {
      BusyHourSourceTables selecteditem = allsources.get(selectedInd);
      techpackBasetables = new String[selecteditem.getBasetables().size()];
      int cnt2 = 0;
      for (Iterator<String> iter = selecteditem.getBasetables().iterator(); iter.hasNext();) {
        techpackBasetables[cnt2] = iter.next();
        cnt2++;
      }
    } else {
      techpackBasetables = new String[0];
    }
    typeNameCB = new JComboBox();
    ComboBoxModel model = new DefaultComboBoxModel(techpackBasetables);
    typeNameCB.setModel(model);
    typeNameCB.revalidate();
    typeNameCB.setActionCommand(TYPENAMECHANGED);
    typeNameCB.setEnabled(editable);
    typeNameCB.setName("BusyHourSourceSelectorCellEditorBaseTableTypeNameComboBox");

    if (originalTypeName != null) {
      selectedInd = -1;
      for (int ind = 0; ind < typeNameCB.getItemCount(); ind++) {
        if (typeNameCB.getItemAt(ind) != null && typeNameCB.getItemAt(ind).equals(originalTypeName)) {
          selectedInd = ind;
        }
      }
    }

    typeNameCB.setSelectedIndex(selectedInd);
    targetTechPackCB.addActionListener(this);
    typeNameCB.addActionListener(this);

    gbc.fill = GridBagConstraints.NONE;
    gbc.gridy = 1;
    gbc.gridx = 1;
    // gbc.weightx = 1;
    // gbc.weighty = 1;
    gbc.gridwidth = 3;
    gbc.insets = new Insets(30, 10, 30, 30);
    gbl.setConstraints(typeNameCB, gbc);
    thisDialog.add(typeNameCB);

    // Create buttons
    JButton ok = new JButton("Save");
    JButton cancel = new JButton("Cancel");

    ok.setPreferredSize(cancel.getPreferredSize());
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setEnabled(editable);
    ok.setName("BusyHourSourceSelectorCellEditorBaseTableOkButton");

    cancel.setActionCommand(CANCEL);
    cancel.addActionListener(this);
    cancel.setName("BusyHourSourceSelectorCellEditorBaseTableCancelButton");

    // Place the buttons in the layout
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.weighty = 0;
    gbc.weightx = 0;
    gbc.gridy = 3;
    gbc.gridx = 1;
    gbl.setConstraints(ok, gbc);
    thisDialog.add(ok);

    gbc.gridy = 3;
    gbc.gridx = 2;
    gbl.setConstraints(cancel, gbc);
    thisDialog.add(cancel);

    // And we're done
    thisDialog.pack();
    Utils.center(thisDialog);
    return thisDialog;

  }

}
