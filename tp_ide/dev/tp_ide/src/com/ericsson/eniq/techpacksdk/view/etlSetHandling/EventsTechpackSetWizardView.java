/**------------------------------------------------------------------------
 *
 *
 *      COPYRIGHT (C)                   ERICSSON RADIO SYSTEMS AB, Sweden
 *
 *      The  copyright  to  the document(s) herein  is  the property of
 *      Ericsson Radio Systems AB, Sweden.
 *
 *      The document(s) may be used  and/or copied only with the written
 *      permission from Ericsson Radio Systems AB  or in accordance with
 *      the terms  and conditions  stipulated in the  agreement/contract
 *      under which the document(s) have been supplied.
 *
 *------------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.GridBagConstraints;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockException;
import tableTree.TableTreeComponent;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.ericsson.eniq.common.setWizards.CreateCountSet;
import com.ericsson.eniq.common.setWizards.CreateRestoreSet;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * This class is used for the creation of a set wizard for ENIQ EVENTS techpacks
 * 
 * @author epaujor
 * 
 */
public class EventsTechpackSetWizardView extends TechpackSetWizardView {

  private transient JCheckBox countSets;

  private transient JCheckBox restoreSets;

  /**
   * Creates a set wizard for a ENIQ Events techpack
   * 
   * @param application
   * @param dataModelControl
   * @param myTTC
   * @param frame
   * @param fFrame
   * @param setName
   * @param setVersion
   * @param versionid
   * @param setType
   * @param editable
   */
  public EventsTechpackSetWizardView(final SingleFrameApplication application,
      final DataModelController dataModelControl, final TableTreeComponent myTTC, final JFrame frame,
      final JFrame fFrame, final String setName, final String setVersion, final String versionid, final String setType,
      final boolean editable) {
    super(application, dataModelControl, myTTC, frame, fFrame, setName, setVersion, versionid, setType, editable);
  }

  /**
   * Creates all the check boxes for all the different sets including some extra ones for EVENTS
   * 
   * @param txtPanel
   * @param constraints
   */
  @Override
  protected int createCheckBoxesForSets(final JPanel txtPanel, final GridBagConstraints constraints) {
    final int gridyPosition = super.createCheckBoxesForSets(txtPanel, constraints);

    countSets = new JCheckBox("Count Sets");
    countSets.setName("Count Sets");
    countSets.setSelected(true);
    countSets.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = gridyPosition + 1;
    constraints.weightx = 0;
    txtPanel.add(countSets, constraints);

    restoreSets = new JCheckBox("Restore Sets");
    restoreSets.setName("Restore Sets");
    restoreSets.setSelected(true);
    restoreSets.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = gridyPosition + 2;
    constraints.weightx = 0;
    txtPanel.add(restoreSets, constraints);

    return constraints.gridy;
  }

  @Override
  protected boolean createTPSets(final Vector<String> skiplist, final Meta_collection_sets metaCollectionSet) {
    boolean successful = super.createTPSets(skiplist, metaCollectionSet);
    try {
      dataModelController.getRockFactory().getConnection().setAutoCommit(false);
      dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

      if (countSets.isSelected()) {
        final CreateCountSet createCountSet = new CreateCountSet(setName, setVersion, versionid,
            dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(), (int) metaCollectionSet
                .getCollection_set_id().longValue(), scheduling.isSelected());

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          createCountSet.removeSets(skiplist);
          createCountSet.create(false, skiplist);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          createCountSet.create(true, skiplist);
        }
      }

      if (restoreSets.isSelected()) {
        createRestoreSet(skiplist, metaCollectionSet);
      }

      dataModelController.getRockFactory().getConnection().commit();
      dataModelController.getEtlRockFactory().getConnection().commit();

      myTTC.discardChanges();
    } catch (final Exception e) {
      successful = false ;
      ExceptionHandler.instance().handle(e);
    } finally {
      frame.dispose();
      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);

      } catch (final Exception e) {
    	successful = false ;
        ExceptionHandler.instance().handle(e);
      }
    }
    return successful ;
  }

  /**
   * @param skiplist
   * @param metaCollectionSet
   * @throws SQLException
   * @throws RockException
   */
  protected void createRestoreSet(final Vector<String> skiplist, final Meta_collection_sets metaCollectionSet)
      throws SQLException, RockException {
    final CreateRestoreSet createRestoreSet = new CreateRestoreSet(setName, setVersion, versionid,
        dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(), (int) metaCollectionSet
            .getCollection_set_id().longValue(), scheduling.isSelected());

    if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
      // remove existing ones
      createRestoreSet.removeSets(skiplist);
      createRestoreSet.create(false, skiplist);

    } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
      // if set is allready created, skip it
      createRestoreSet.create(true, skiplist);
    }
  }

  @Override
  protected boolean isAnySetSelected() {
    return super.isAnySetSelected() || countSets.isSelected();
  }

  @Override
  @Action
  public Task create() {
    return super.create();
  }

  @Override
  @Action
  public void discard() {
    super.discard();
  }

  @Override
  @Action
  public void disc() {
    super.disc();
  }

  @Override
  @Action
  public void pick() {
    super.pick();
  }

  @Override
  @Action
  public void discall() {
    super.discall();
  }

  @Override
  @Action
  public void pickall() {
    super.pickall();
  }
}
