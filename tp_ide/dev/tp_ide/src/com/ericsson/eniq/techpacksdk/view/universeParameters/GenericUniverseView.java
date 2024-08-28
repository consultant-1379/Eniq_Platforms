package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockDBObject;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.GenericPanel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public abstract class GenericUniverseView extends GenericPanel {

  private static final Logger logger = Logger.getLogger(GenericUniverseView.class.getName());

  public static final Color WHITE = Color.WHITE;

  public SingleFrameApplication application;

  public UniverseTabs parentPanel;

  public boolean saveEnabled = false;

  public boolean cancelEnabled = false;

  public boolean tableChanged = false;

  public boolean editable = true;

  public JTable myTable = new JTable();

  public DataModelController dataModelController;

  public JFrame frame;

  private DataModel datamodel;

  Versioning tmpversioning;

  /**
   * For error handling
   */
  Vector<String> errors = new Vector<String>();

  /**
   * For error handling
   */
  private ErrorMessageComponent errorMessageComponent;
  

  public GenericUniverseView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;

    this.dataModelController = dataModelController;

    this.application = application;

    this.editable = editable;

    this.parentPanel = parentPanel;

    tmpversioning = versioning;

  }

  /**
   * Adds buttons common to all universeViews
   * 
   * @param textPanel
   */
  protected void addButtons(JScrollPane textPanel) {

    JButton cancel;
    JButton save;

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    cancel = new JButton("Discard");
    cancel.setActionCommand("discard");
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Discard");

    save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("UniverseConditionClose");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    buttonPanel.add(errorMessageComponent);

    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    // ************** right & left panels, left panel contains
    // **********************

    JPanel lpanel = new JPanel(new BorderLayout());
    JPanel rpanel = new JPanel(new BorderLayout());
    lpanel.add(textPanel);

    // ************** Inner panel panel with right and left panels
    // **********************

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    innerPanel.add(lpanel, BorderLayout.LINE_START);
    innerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    innerPanel.add(rpanel, BorderLayout.LINE_START);

    // ************** Main panel with inner panel and button panel
    // **********************

    // insert both panels in main view

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(innerPanel, gbc);

    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 1;

    this.add(buttonPanel, gbc);

    frame.repaint();
  }

  /**
   * Method for setting the column editor of the Description and IsElement
   * columns.
   */
  abstract public void setColumnEditors(final JTable theTable);

  abstract public GenericUniverseTableModel<? extends RockDBObject> getTableModel();

  /**
   * Method for setting the column renderer. Used to set a renderer for the
   * description field and isElement combo box.
   */
  abstract public void setColumnRenderers(final JTable theTable);

  protected void setColumnWidths(final JTable theTable) {

    for (int i = 0; i < getTableModel().columnWidtArr.length; i++) {

      int widt = getTableModel().columnWidtArr[i];

      TableColumn column = theTable.getColumnModel().getColumn(i);

      column.setPreferredWidth(widt);

    }
  }

  /**
   * Perform refresh to the view
   * 
   */
  public void refresh() {
    getDataModel().refresh(tmpversioning);
    GenericUniverseTableModel utm = getTableModel();

    addTableModelListener(utm, new TableSelectionListener());
    setModel(myTable, utm);

    setColumnEditors(myTable);
    setColumnRenderers(myTable);

    tableChanged = false;
    setSaveEnabled(false);
    setCancelEnabled(false);
    frame.repaint();
    handleButtons();
  }

  abstract public GenericUniverseDataModel getDataModel();

  public class SaveTask extends Task<Void, Void> {

    public SaveTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "Save UniverseCondition");

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        getDataModel().save();
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {

        logger.warning("Error while saving " + e);

        try {
          dataModelController.getRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }

        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }

      tableChanged = false;
      setSaveEnabled(false);
      setCancelEnabled(false);
      getParentAction("enableTabs").actionPerformed(null);

      if (datamodel != null) {
        dataModelController.rockObjectsModified(datamodel);
        datamodel = null;
      }

      return null;
    }
  }

  public void setDataModel(DataModel datamodel) {
    this.datamodel = datamodel;
  }

  public class DiscardTask extends Task<Void, Void> {

    public DiscardTask(Application app) {
      super(app);
    }

    @Override
    public Void doInBackground() throws Exception {
      logger.info("Discard Edit");
      getParentAction("enableTabs").actionPerformed(null);
      return null;
    }
  }

  public javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  /**
   * Helper function, returns action by name from parent panel
   * 
   * @param actionName
   * @return
   */
  public javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  // ************************************************
  // popup menu
  // ************************************************
  private JPopupMenu createUCPopup(MouseEvent e) {
    JPopupMenu popupUC;
    JMenuItem miUC;
    popupUC = new JPopupMenu();

    int selected = myTable.getSelectedRow();
    // Object O = getAction("addemptyu");

    // Object o = e.getSource();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removeu"));
        miUC.setText("Remove row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate rows");
        miUC.setAction(getAction("duplicaterowu"));
        miUC.setText("Duplicate row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move up row");
        miUC.setAction(getAction("moverowupu"));
        miUC.setText("Move up row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move down row");
        miUC.setAction(getAction("moverowdownu"));
        miUC.setText("Move down row");
        popupUC.add(miUC);

      }

      miUC = new JMenuItem("Add Empty");
      miUC.setText("Add empty row");
      miUC.setAction(getAction("addemptyu"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);

    } else {

      miUC = new JMenuItem("Add Empty");
      miUC.setAction(getAction("addemptyu"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);
    }

    popupUC.setOpaque(true);
    popupUC.setLightWeightPopupEnabled(true);

    return popupUC;
  }

  private void displayUMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createUCPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(boolean cancelEnabled) {
    boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  @SuppressWarnings("unchecked")
  public void handleButtons() {
    boolean saveIsEnabled = false;
    setScreenMessage(null);

    try {
      GenericUniverseDataModel model = getDataModel();
      errors = model.validateData();
    } catch (Exception e) {
      errors.clear();
      e.printStackTrace();
    }

    if (errors.size() > 0) {
      setScreenMessage(errors);
    }

    boolean hasErrors = errors.size() > 0;
    if (tableChanged) {
      saveIsEnabled = hasErrors ? false : true;
      getParentAction("disableTabs").actionPerformed(null);
      this.setCancelEnabled(true);
    } else {
      this.setCancelEnabled(false);
    }
    this.setSaveEnabled(saveIsEnabled);
  }

  /**
   * Error handling, sets the message for errorMessageComponent
   * 
   * @param message
   */
  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  public class TableSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableChanged = true;
      handleButtons();
      setColumnRenderers(myTable);
      setColumnEditors(myTable);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayUMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayUMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayUMenu(e);
    }
  }
}
