package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.IntervalDirCheckScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.IntervalScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.MonthlyScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.OnceScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.OnstartupScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.ScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.WaitForFileScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.WaitScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.WeeklyIntervalScheduleView;
import com.ericsson.eniq.techpacksdk.view.schedulingViews.WeeklyScheduleView;

public class SchedulePropertiesTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(SchedulePropertiesTableCellEditor.class.getName());

  Object[] schedulerTypeItems = { "fileExists", "interval", "timeDirCheck", "monthly", "once", "onStartup", "wait", "weekly",
      "weeklyinterval" };

  // The editor will show the same components inside the cell as the renderer.
  // Create a panel and a text field and an edit button.

  private JPanel panel = new JPanel(new GridBagLayout());

  private JTextField textField = new JTextField("dummy");

  private JButton editButton = new JButton("...");

  private JComboBox aType;

  private ScheduleView aView;

  private JPanel p;

  private String setName;

  private String techpackName;

  private boolean editable;

  private boolean active = false;

  private JFrame thisFrame;

  /**
   * Constants for the button and text field actions used.
   */
  public static final String EDITTEXT = "edittext";

  public static final String EDIT = "edit";

  public static final String OK = "ok";

  public static final String CANCEL = "cancel";

  public static final String STYPECHANGED = "stypechanged";

  /**
   * The value of the edited data
   */
  private Meta_schedulings ms;

  /**
   * The edit frame that is displayed when the edit action is triggered.
   */
  protected JFrame editFrame = null;

  /**
   * The edit pane contained within the edit frame. Used to set and get the
   * data.
   */
  // private JEditorPane editPane = null;
  /**
   * Constructor. Initiates the text field and the edit button in the table
   * cell.
   */
  public SchedulePropertiesTableCellEditor(boolean editable) {

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

    // Text field
    textField.setBorder(null);
    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
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
    gbc.gridx = 1;
    gbc.gridy = 0;
    // gbc.insets = new Insets(2, 2, 2, 2);
    panel.add(editButton, gbc);
  }

  /**
   * This returns the value to the edited cell when fireEditingStopped has been
   * called.
   */
  public Object getCellEditorValue() {
    return ms;
  }

  /**
   * This returns the panel as the editor to the table. This is also where we
   * get the initial value for the text field.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {

    if (active) {
      editFrame.setVisible(false);
      active = false;
      editFrame.dispose();
    }

    // Store the cell value for when we start editing
    ms = (Meta_schedulings) value;
    textField.setText(Utils.getSChedulingDescription(ms));
    return panel;
  }

  /**
   * This is the listener callback for the three buttons (the panels edit
   * button, and the dialog's OK and CANCEL buttons) and the text field editing
   * done action.
   */
  public void actionPerformed(ActionEvent e) {

    try {

      if (STYPECHANGED.equals(e.getActionCommand())) {

        // Scheduler type has been changed
        aView = getScheduleView(p, (String) aType.getSelectedItem(), ms);

      } else if (EDIT.equals(e.getActionCommand())) {
        // Starting to edit the cell.
        // Create the edit frame, set the text and show the frame.
        if (!active) {
          editFrame = createFrame();
        }
        if(editFrame == null){
        	active = false;
        }else{
        	editFrame.setVisible(true);
            active = true;
        }
      } else if (EDITTEXT.equals(e.getActionCommand())) {
        // Text box editing done. Store data.
        aView.fill(ms);
        fireEditingStopped();
      } else if (OK.equals(e.getActionCommand())) {
        // Editing done, OK clicked.
        // Store the data, hide and dispose the frame.
        ms.setExecution_type((String) aType.getSelectedItem());
        aView.fill(ms);
        editFrame.setVisible(false);
        active = false;
        editFrame.dispose();
        fireEditingStopped();
      } else { // CANCEL
        // Editing done, CANCEL clicked.
        // Hide and dispose the frame.
        editFrame.setVisible(false);
        active = false;
        editFrame.dispose();
        fireEditingCanceled();
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

    try {

      Meta_collections mc = new Meta_collections(ms.getRockFactory());
      mc.setCollection_id(ms.getCollection_id());
      Meta_collectionsFactory mcF = new Meta_collectionsFactory(ms.getRockFactory(), mc);
      techpackName = ((Meta_collections) mcF.getElementAt(0)).getCollection_name();

      Meta_collection_sets mcs = new Meta_collection_sets(ms.getRockFactory());
      mcs.setCollection_set_id(ms.getCollection_set_id());
      Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(ms.getRockFactory(), mcs);
      setName = ((Meta_collection_sets) mcsF.getElementAt(0)).getCollection_set_name();

    } catch(Exception e){
    	JOptionPane.showMessageDialog(null,
    		    "Please save Parameter Type first to edit properties of this Scheduling.",
    		    "Warning!!!!",
    		    JOptionPane.WARNING_MESSAGE);
    	logger.warning(e.getMessage());
    	return null;
    }

    // Create frame
    thisFrame = new JFrame("Edit Scheduling: " + techpackName + "/" + setName + "/" + ms.getName());
    thisFrame.setAlwaysOnTop(true);

    // Layout the contents of the frame
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    thisFrame.setLayout(gbl);
    // thisFrame.setBackground(Color.white);

    p = new JPanel();
    p.setLayout(gbl);

    aView = getScheduleView(p, ms.getExecution_type(), ms);
    p.setEnabled(editable);

    // Create buttons
    JButton ok = new JButton("Save");
    JButton cancel = new JButton("Cancel");
    ok.setPreferredSize(cancel.getPreferredSize());
    ok.setActionCommand(OK);
    ok.addActionListener(this);
    ok.setEnabled(editable);

    cancel.setActionCommand(CANCEL);
    cancel.addActionListener(this);

    // place the chooser to layout

    JLabel name = new JLabel("Scheduler Type");
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    gbl.setConstraints(name, gbc);
    thisFrame.add(name);

    aType = new JComboBox(schedulerTypeItems);
    aType.setActionCommand(STYPECHANGED);
    aType.addActionListener(this);
    aType.setSelectedItem(ms.getExecution_type());

    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbl.setConstraints(aType, gbc);
    thisFrame.add(aType);

    // Place the pane in the layout
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridy = 2;
    gbc.insets = new Insets(2, 2, 2, 2);
    gbl.setConstraints(p, gbc);

    thisFrame.add(p);

    JPanel bPanel = new JPanel();
    bPanel.add(ok);
    bPanel.add(cancel);

    // Place the buttons in the layout
    gbc.gridwidth = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridy = 3;

    gbl.setConstraints(bPanel, gbc);
    thisFrame.add(bPanel);

    // And we're done
    thisFrame.pack();
    return thisFrame;
  }

  private ScheduleView getScheduleView(JPanel schviewPanel, String type, Meta_schedulings sch) {

    if (editFrame != null) {
      editFrame.setSize(400, 400);
    }

    ScheduleView schview;

    if (type == null) {
      schview = null;
      schviewPanel.removeAll();
      schviewPanel.invalidate();
      schviewPanel.revalidate();
      schviewPanel.repaint();
      return schview;
    }

    if (type.equals("wait")) {
      schview = (ScheduleView) new WaitScheduleView(schviewPanel, sch);
    } else if (type.equals("fileExists")) {
      schview = (ScheduleView) new WaitForFileScheduleView(schviewPanel, sch);
    } else if (type.equals("interval") || type.equals("intervall")) {
      schview = (ScheduleView) new IntervalScheduleView(schviewPanel, sch);
    } else if (type.equals("timeDirCheck")) {
      schview = (ScheduleView) new IntervalDirCheckScheduleView(schviewPanel, sch);
    } else if (type.equals("weekly")) {
      schview = (ScheduleView) new WeeklyScheduleView(schviewPanel, sch);
    } else if (type.equals("weeklyinterval")) {
      if (editFrame != null) {
        editFrame.setSize(400, 600);
      }
      schview = (ScheduleView) new WeeklyIntervalScheduleView(schviewPanel, sch);
    } else if (type.equals("monthly")) {
      schview = (ScheduleView) new MonthlyScheduleView(schviewPanel, sch);
    } else if (type.equals("once")) {
      schview = (ScheduleView) new OnceScheduleView(schviewPanel, sch);
    } else if (type.equals("onStartup")) {
      schview = (ScheduleView) new OnstartupScheduleView(schviewPanel, sch);
    } else {
      schview = null;
      schviewPanel.removeAll();
      schviewPanel.invalidate();
      schviewPanel.revalidate();
      schviewPanel.repaint();
    }

    return schview;

  }

}
