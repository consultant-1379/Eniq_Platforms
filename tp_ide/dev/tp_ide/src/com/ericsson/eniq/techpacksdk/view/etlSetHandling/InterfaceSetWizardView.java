package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockFactory;
import tableTree.TableTreeComponent;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.common.setWizards.CreateIDirCheckerSet;
import com.ericsson.eniq.common.setWizards.CreateIDirCheckerSetFactory;
import com.ericsson.eniq.common.setWizards.CreateIDiskmanagerSet;
import com.ericsson.eniq.common.setWizards.CreateInterfaceSet;
import com.ericsson.eniq.common.setWizards.CreateInterfaceSetFactory;

@SuppressWarnings("serial")
public class InterfaceSetWizardView extends JPanel {

  private static final Logger LOGGER = Logger.getLogger(InterfaceSetWizardView.class.getName());

  private final SingleFrameApplication application;

  private final DataModelController dataModelController;

  private final boolean editable;

  private final String setName;

  private final String version;

  private final String type;

  private final JTextField elementTypeF;

  private final JComboBox templateVersionCB;

  private final JCheckBox adapterSets;

  private final JCheckBox directoryCheckers;

  private final JCheckBox diskManagers;

  private final JCheckBox schedulings;

  private final JComboBox existingSetsCB;

  private final JLabel existingSets;

  private final JFrame frame;

  private final JFrame fFrame;

  private final TableTreeComponent myTTC;

  private final String dbcon_id = "2";

  private void createInterfacesSets() {

    try {

      dataModelController.getRockFactory().getConnection().setAutoCommit(false);
      dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

      Meta_collection_sets mwhere = new Meta_collection_sets(dataModelController.getEtlRockFactory());      
      
      mwhere.setCollection_set_name(setName);
      mwhere.setVersion_number(version);

      Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
          mwhere);

      Vector tps = mcsf.get();

      Meta_collection_sets etl_tp = null;

      if (tps.size() <= 0) { // etlrep techpack does not exists -> create new

        etl_tp = new Meta_collection_sets(dataModelController.getEtlRockFactory());
        etl_tp.setCollection_set_id(getNextTechPackID(dataModelController.getEtlRockFactory()));
        etl_tp.setCollection_set_name(setName);
        etl_tp.setVersion_number(version);
        etl_tp.setDescription("Interface " + setName + " by TPIDE b");
        etl_tp.setEnabled_flag("Y");
        etl_tp.setType("Interface");

        etl_tp.insertDB(false, false);

      } else {
        etl_tp = (Meta_collection_sets) tps.get(0);
      }
      if (directoryCheckers.isSelected()) {

        CreateIDirCheckerSet cdc = CreateIDirCheckerSetFactory.createIDirCheckerSet(type, etl_tp.getVersion_number(),
            dataModelController.getEtlRockFactory(), etl_tp.getCollection_set_id().longValue(), setName, elementTypeF
                .getText().trim(),dataModelController.getEditInterfaceDataModel().getFormat() );

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cdc.removeSets();
          cdc.create(false);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cdc.create(true);
        }
      }

      if (diskManagers.isSelected()) {

        CreateIDiskmanagerSet cdm = new CreateIDiskmanagerSet(type, etl_tp.getVersion_number(), dataModelController
            .getEtlRockFactory(), etl_tp.getCollection_set_id().longValue(), setName, elementTypeF.getText().trim());

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cdm.removeSets(schedulings.isSelected());
          cdm.create(false, schedulings.isSelected());

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cdm.create(true, schedulings.isSelected());
        }
      }

      if (adapterSets.isSelected()) {

        CreateInterfaceSet cis = CreateInterfaceSetFactory.createInterfaceSet(dataModelController
            .getEditInterfaceDataModel().getInterfacetype(), (String) templateVersionCB.getSelectedItem(), version, "",
            dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(), etl_tp
                .getCollection_set_id().longValue(), setName, version, dataModelController.getEditInterfaceDataModel()
                .getFormat(), dataModelController.getEditInterfaceDataModel().getFormat(), elementTypeF.getText()
                .trim(), dbcon_id, application.getContext().getResourceMap());
        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cis.removeTechpacks(dataModelController.getEditInterfaceDataModel().getTechPacks(), dataModelController
              .getEditInterfaceDataModel().getFormat(), schedulings.isSelected());
          cis.createTechpacks(false, dataModelController.getEditInterfaceDataModel().getTechPacks(),
              dataModelController.getEditInterfaceDataModel().getFormat(), schedulings.isSelected());

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cis.createTechpacks(true, dataModelController.getEditInterfaceDataModel().getTechPacks(), dataModelController
              .getEditInterfaceDataModel().getFormat(), schedulings.isSelected());

        }

      }

      dataModelController.getRockFactory().getConnection().commit();
      dataModelController.getEtlRockFactory().getConnection().commit();

      myTTC.discardChanges();

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {

      frame.dispose();

      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }
  }

  private class WizardTask extends Task<Void, Void> {

    public WizardTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {
      fFrame.setEnabled(false);
      createInterfacesSets();
      fFrame.setEnabled(true);
      return null;
    }
  }

  /**
   * create action
   * 
   * @return
   */
  @Action
  public Task create() {
    final Task WizardTask = new WizardTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    WizardTask.setInputBlocker(new BusyIndicatorInputBlocker(WizardTask, busyIndicator));

    return WizardTask;
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action
  public void discard() {
    LOGGER.log(Level.INFO, "cancel");
    frame.dispose();
  }

  /**
   * Helper function, returns action by name
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
  }

  public InterfaceSetWizardView(final SingleFrameApplication application, DataModelController dataModelController,
      TableTreeComponent myTTC, JFrame frame, JFrame fFrame, String setName, String version, String type,
      boolean editable) {
    super(new GridBagLayout());

    String templateVersions[] = { "5.2" };
    String existingSet[] = { "Overwrite", "Skip" };
    this.application = application;
    this.dataModelController = dataModelController;
    this.editable = editable;
    this.setName = setName;
    this.version = version;
    this.type = type;
    this.frame = frame;
    this.fFrame = fFrame;
    this.myTTC = myTTC;

    Dimension fieldDim = new Dimension(250, 25);
    // Dimension areaDim = new Dimension(250, 150);
    // Dimension tableDim = new Dimension(350, 150);

    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // template

    JLabel templateVersion = new JLabel("Template Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(templateVersion, c);

    templateVersionCB = new JComboBox(templateVersions);
    templateVersionCB.setSelectedIndex(0);
    templateVersionCB.setPreferredSize(fieldDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    txtPanel.add(templateVersionCB, c);

    // Element type

    JLabel elementType = new JLabel("Element Type");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(elementType, c);

    elementTypeF = new JTextField();
    elementTypeF.setPreferredSize(fieldDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    txtPanel.add(elementTypeF, c);

    // Generate

    JLabel generate = new JLabel("Generate");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    txtPanel.add(generate, c);

    adapterSets = new JCheckBox("Adapter Sets");
    adapterSets.setSelected(true);
    adapterSets.addActionListener(new MyActionListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    txtPanel.add(adapterSets, c);

    directoryCheckers = new JCheckBox("Directory Checkers");
    directoryCheckers.setSelected(true);
    directoryCheckers.addActionListener(new MyActionListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    txtPanel.add(directoryCheckers, c);

    diskManagers = new JCheckBox("Disk Managers");
    diskManagers.setSelected(true);
    diskManagers.addActionListener(new MyActionListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    txtPanel.add(diskManagers, c);

    schedulings = new JCheckBox("Schedulings");
    schedulings.setSelected(true);
    schedulings.addActionListener(new MyActionListener());
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 7;
    c.weightx = 0;
    txtPanel.add(schedulings, c);

    // handle existing sets

    existingSets = new JLabel("Existing Sets");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 8;
    c.weightx = 0;
    txtPanel.add(existingSets, c);

    existingSetsCB = new JComboBox(existingSet);
    existingSetsCB.setPreferredSize(fieldDim);
    existingSetsCB.setSelectedItem(dataModelController.getEditInterfaceDataModel().getFormat());
    existingSetsCB.setName("InterfaceSetWizardViewExistinSetsComboBox");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 8;
    c.weightx = 1;
    txtPanel.add(existingSetsCB, c);

    // ************** buttons **********************

    JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("InterfaceSetWizardViewDiscard");

    JButton save = new JButton("Create");
    save.setAction(getAction("create"));
    save.setEnabled(editable);
    save.setName("InterfaceSetWizardViewCreate");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(save);
    buttonPanel.add(cancel);

    c.gridheight = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    this.add(txtPanelS, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

  }

  public static Long getNextTechPackID(RockFactory rock) throws Exception {

    Meta_collection_sets csw = new Meta_collection_sets(rock);
    Meta_collection_setsFactory csf = new Meta_collection_setsFactory(rock, csw);

    long largest = -1;
    Vector dbVec = csf.get();
    for (int i = 0; i < dbVec.size(); i++) {
      Meta_collection_sets mc = (Meta_collection_sets) dbVec.elementAt(i);
      if (largest < mc.getCollection_set_id().longValue()) {
        largest = mc.getCollection_set_id().longValue();
      }
    }

    LOGGER.fine("new TechPackID " + (largest + 1));

    return Long.valueOf(largest + 1);

  }

  private class MyActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub

      if (!(adapterSets.isSelected() || directoryCheckers.isSelected() || diskManagers.isSelected())) {
        schedulings.setSelected(false);
        schedulings.setEnabled(false);
      } else {
        schedulings.setEnabled(true);
      }

    }
  }
}
