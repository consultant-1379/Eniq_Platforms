package com.distocraft.dc5000.etl.gui.dwhmanagement;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.SystemStructureNotificate;
import com.distocraft.dc5000.etl.gui.Tab;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhcolumn;
import com.distocraft.dc5000.repository.dwhrep.DwhcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhpartition;
import com.distocraft.dc5000.repository.dwhrep.DwhpartitionFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.DwhtechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Externalstatementstatus;
import com.distocraft.dc5000.repository.dwhrep.ExternalstatementstatusFactory;

//import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
//import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
//import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;

/**
 * This class contains the tab for the DWH in ConfigTool. Copyright Distocraft
 * 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class DwhManagementTab extends JPanel implements Tab, MouseListener {

  private Logger log;

  private RockFactory dwhrepRockFactory;

  private UI ui;

  private JFrame frame;

  private JSplitPane split;

  public DwhTechPackSelector dwhTechPackSelector;

  // public TypeActivationTable table = null;

  private RockFactory rockFactory = null;

  private String connectionId = null;

  /**
   * Constructor of this tab.
   * 
   * @param frame
   * @param rock
   * @param ssn
   * @param ui
   */
  public DwhManagementTab(JFrame frame, RockFactory rock, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.log = Logger.getLogger("DwhManagementTab");

    this.ui = ui;
    this.frame = frame;
    this.rockFactory = rock;

    this.disconnected();
  }

  /**
   * Just a wrapper to refresh the whole tab.
   * 
   */
  public void refresh() {
    connected(this.rockFactory, this.dwhrepRockFactory, this.connectionId);
  }

  /**
   * This method shows the contents of the tab when the configtool is
   * succesfully connected.
   * 
   * @param rockFactory
   *          Rockfactory to etlrep.
   * @param dwhrepRock
   *          Rockfactory to dwhrep.
   * @param connectionId
   *          Id of the connection.
   */
  public void connected(RockFactory rockFactory, RockFactory dwhrepRock, String connectionId) {

    this.dwhrepRockFactory = dwhrepRock;
    this.rockFactory = rockFactory;
    this.connectionId = connectionId;

    this.removeAll();

    JPanel left = new JPanel(new GridBagLayout());

    GridBagConstraints lc = new GridBagConstraints();
    lc.fill = GridBagConstraints.NONE;
    lc.anchor = GridBagConstraints.NORTHWEST;
    lc.weightx = 0;
    lc.weighty = 0;
    left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

    lc.gridx = 1;
    lc.gridy = 1;
    left.add(new JLabel("Available techpacks"), lc);

    lc.gridy = 2;
    lc.gridx = 2;
    left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

    lc.gridy = 3;
    lc.gridx = 0;
    lc.gridwidth = 3;
    lc.fill = GridBagConstraints.BOTH;
    lc.weightx = 1;
    lc.weighty = 1;

    this.dwhTechPackSelector = new DwhTechPackSelector(connectionId, this.dwhrepRockFactory, null);
    this.dwhTechPackSelector.addMouseListener(this);
    this.dwhTechPackSelector.addTreeSelectionListener(new DwhTechPackSelectorListener(this.dwhrepRockFactory));
    left.add(this.dwhTechPackSelector, lc);

    JPanel right = new JPanel(new GridBagLayout());

    GridBagConstraints rc = new GridBagConstraints();
    rc.fill = GridBagConstraints.NONE;
    rc.anchor = GridBagConstraints.NORTHWEST;
    rc.weightx = 0;
    rc.weighty = 0;

    right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    rc.gridx = 1;
    rc.gridy = 1;
    rc.insets = new Insets(2, 2, 5, 2);
    right.add(new JLabel("Status of techpack"), rc);
    rc.gridy = 10;
    rc.gridx = 5;
    rc.weightx = 0;
    rc.weighty = 0;
    rc.gridwidth = 1;
    rc.fill = GridBagConstraints.NONE;
    right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    this.add(split, c);

    this.invalidate();
    this.validate();
    this.repaint();
  }

  /**
   * This method removes all components from the container and shows the
   * notification for connection.
   */
  public void disconnected() {
    this.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.VERTICAL;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weighty = 0.5;

    this.add(Box.createRigidArea(new Dimension(20, 20)), c);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 1;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    this.add(new JLabel("Disconnected"), c);

    c.gridy = 2;
    this.add(new JLabel("Select Connection-Connect to connect"), c);

    c.fill = GridBagConstraints.VERTICAL;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weighty = 0.5;
    c.gridx = 2;
    c.gridy = 3;
    this.add(Box.createRigidArea(new Dimension(20, 20)), c);

    this.invalidate();
    this.validate();
    this.repaint();

  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    JTree comp = (JTree) e.getComponent();
    final TreePath tp = comp.getClosestPathForLocation(e.getX(), e.getY());

    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();

    // The selected object is the top level connection, where a new
    // Tpactiovation can be created.
    if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup
      if (!(dmtn.getUserObject() instanceof Dwhtechpacks)) {

        JPopupMenu pop = new JPopupMenu("DWH TechPack");
        JMenuItem rf = new JMenuItem("Refresh tree", ConfigTool.refresh);
        rf.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {

            if (dwhTechPackSelector != null)
              dwhTechPackSelector.refresh();

          }
        });
        pop.add(rf);
        pop.show(e.getComponent(), e.getX(), e.getY());
      } else {
        // The selected object is an existing DWHTechPack.
        final Dwhtechpacks selectedDWHTechPack = (Dwhtechpacks) dmtn.getUserObject();

        if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup

          JPopupMenu pop = new JPopupMenu("DWH TechPack");

          JMenuItem rmd = new JMenuItem("Remove this DWH TechPack", ConfigTool.delete);
          rmd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

              // Check if the selected techpack has a CUSTOM techpack related to
              // it.
              String techpackName = selectedDWHTechPack.getTechpack_name();
              String customTechpackName = "CUSTOM_" + techpackName;

              Dwhtechpacks whereDwhTechpack = new Dwhtechpacks(dwhrepRockFactory);
              whereDwhTechpack.setTechpack_name(customTechpackName);
              try {
                DwhtechpacksFactory dwhTechpackFactory = new DwhtechpacksFactory(dwhrepRockFactory, whereDwhTechpack);

                Vector customTechpacks = dwhTechpackFactory.get();
                Iterator customTechpacksIterator = customTechpacks.iterator();

                int confirmResult = 0;
                if (customTechpacksIterator.hasNext()) {
                  // The CUSTOM_ techpack exists. Notify the user to delete it
                  // first.

                  confirmResult = JOptionPane
                      .showConfirmDialog(DwhManagementTab.this.frame,
                          "This techpack has a related custom techpack named " + customTechpackName
                              + " and you should delete it before deleting this techpack.\n"
                              + "Are you sure you want to remove " + techpackName
                              + " and all data related to it from DWH?", "Confirm delete", JOptionPane.YES_NO_OPTION);

                } else {
                  // No custom techpack exists for this techpack. Ask
                  // confirmation anyway.
                  confirmResult = JOptionPane.showConfirmDialog(DwhManagementTab.this.frame,
                      "Are you sure you want to remove " + techpackName + " and all data related to it from DWH?",
                      "Confirm delete", JOptionPane.YES_NO_OPTION);

                }

                if (confirmResult == 0) {
                  RemoveDwhTechPackWorker removeDWHTechPackWorker = new RemoveDwhTechPackWorker(selectedDWHTechPack);
                  removeDWHTechPackWorker.start();
                }

              } catch (Exception e) {
                // Some error happened. Show dialog.
                JOptionPane.showConfirmDialog(DwhManagementTab.this.frame,
                    "Failed to load from DWHTechPacks table. Exception: " + e.getStackTrace(), "Error",
                    JOptionPane.OK_OPTION);
              }

            }
          });

          pop.add(rmd);

          JMenuItem rf = new JMenuItem("Refresh tree", ConfigTool.refresh);
          rf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

              if (dwhTechPackSelector != null)
                dwhTechPackSelector.refresh();

            }
          });
          pop.add(rf);

          pop.show(e.getComponent(), e.getX(), e.getY());

        }
        return;
      }
    }
  }

  public class DwhTechPackSelectorListener implements TreeSelectionListener {

    public DwhTechPackSelectorListener(RockFactory dwhrepRockFactory) {
    }

    public void valueChanged(TreeSelectionEvent tse) {

      if (tse == null) {
        split.setRightComponent(new JPanel());
        return;

      }
      TreePath tp = tse.getPath();

      if (tp == null) {
        split.setRightComponent(new JPanel());
        return;
      }

      DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) tp.getLastPathComponent();

      if (dtm.getUserObject() instanceof Dwhtechpacks) {
        Dwhtechpacks dwhTechPack = (Dwhtechpacks) dtm.getUserObject();

        JPanel right = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 6);

        right.add(Box.createRigidArea(new Dimension(10, 5)), c);

        c.gridx = 1;
        c.gridy = 1;
        right.add(new JLabel("Techpack name"), c);

        c.gridx = 2;
        right.add(new JLabel(dwhTechPack.getTechpack_name()), c);

        c.gridx = 1;
        c.gridy = 2;
        right.add(new JLabel("Version ID"), c);

        c.gridx = 2;
        right.add(new JLabel(dwhTechPack.getVersionid()), c);

        c.gridx = 1;
        c.gridy = 3;
        right.add(new JLabel("Creation date"), c);

        c.gridx = 2;
        right.add(new JLabel(dwhTechPack.getCreationdate().toString()), c);

        right.add(Box.createRigidArea(new Dimension(10, 5)), c);

        c.gridx = 1;
        c.gridy = 4;
        right.add(Box.createRigidArea(new Dimension(10, 20)), c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);

        /*
         * table = new TypeActivationTable(tpactivation, this.dwhrepRockFactory,
         * frame); right.add(table, c);
         */

        try {
          split.setRightComponent(right);

        } catch (Exception e) {
          e.printStackTrace();
        }

      } else {
        split.setRightComponent(new JPanel());
      }

      /*
       * if (ActivationTab.this.table == null) { split.setRightComponent(new
       * JPanel()); }
       */

    }
  };

  public class RemoveDwhTechPackWorker extends SwingWorker {

    Dwhtechpacks selectedDwhTechPack = null;

    public RemoveDwhTechPackWorker(Dwhtechpacks selectedDwhTechPack) {
      ui.startOperation("Removing DWH TechPack...");
      this.selectedDwhTechPack = selectedDwhTechPack;
    }

    public Object construct() {

      try {
        // Remove all related data before deleting the DwhTechPack itself.
        deleteDwhTechPackRelatedData(selectedDwhTechPack);
        this.selectedDwhTechPack.deleteDB();
        DwhManagementTab.this.log.finest("Deletion of DWHTechPack was succesful.");

      } catch (Exception e) {
        new ErrorDialog(DwhManagementTab.this.frame, "Error", "DWH removal failed", e);
      }

      ConfigTool.reloadConfig();
      return null;
    }

    public void finished() {

      DwhManagementTab.this.refresh();

      ui.endOperation();
    }

  }

  /**
   * This function deletes all data related to a specific DWHTechPack.
   * 
   * @param targetDwhTechPack
   *          An instance of Dwhtechpacks. The related data to this instance
   *          will be deleted.
   */
  private void deleteDwhTechPackRelatedData(Dwhtechpacks targetDwhTechPack) {
    try {
      // First get the connection to DWH schema.
      Meta_databases whereMetaDatabases = new Meta_databases(this.rockFactory);
      whereMetaDatabases.setConnection_name("dwh");
      whereMetaDatabases.setType_name("USER");

      Meta_databasesFactory metaDatabasesFactory = new Meta_databasesFactory(this.rockFactory, whereMetaDatabases);
      Vector databases = metaDatabasesFactory.get();
      Iterator databasesIterator = databases.iterator();

      RockFactory dwhRockFactory = null;

      if (databasesIterator.hasNext()) {
        Meta_databases targetMetaDatabase = (Meta_databases) databasesIterator.next();

        String url = targetMetaDatabase.getConnection_string();

        if (url.indexOf("dwhdb") > 0) {
          String f_url = url.substring(0, url.indexOf("dwhdb"));
          f_url += ConfigTool.serverHost;
          f_url += url.substring(url.indexOf("dwhdb") + 5);
          url = f_url;
        }

        dwhRockFactory = new RockFactory(url, targetMetaDatabase.getUsername(), targetMetaDatabase.getPassword(),
            targetMetaDatabase.getDriver_name(), targetMetaDatabase.getConnection_name(), true);
        DwhManagementTab.this.log.finest("Connection to DWH schema was succesful.");
      } else {
        DwhManagementTab.this.log.severe("Connection to DWH schema failed.");
        return;
      }

      if (dwhRockFactory == null) {
        // TODO: Error logging and quit.
        DwhManagementTab.this.log
            .severe("DwhManagementTab.deleteDwhTechPackRelatedData: dwhRockFactory is null. Exiting function call.");
        return;
      }

      // Iterate through database tables and delete from bottom to top.
      Dwhtype whereDwhType = new Dwhtype(this.dwhrepRockFactory);
      whereDwhType.setTechpack_name(targetDwhTechPack.getTechpack_name());
      DwhtypeFactory dwhTypeFactory = new DwhtypeFactory(this.dwhrepRockFactory, whereDwhType);
      Vector dwhTypes = dwhTypeFactory.get();
      Iterator dwhTypesIterator = dwhTypes.iterator();

      while (dwhTypesIterator.hasNext()) {
        Dwhtype currentDwhType = (Dwhtype) dwhTypesIterator.next();

        // Get the tables to delete from DWH schema.
        Dwhpartition whereDwhPartition = new Dwhpartition(this.dwhrepRockFactory);
        whereDwhPartition.setStorageid(currentDwhType.getStorageid());
        DwhpartitionFactory dwhPartitionFactory = new DwhpartitionFactory(this.dwhrepRockFactory, whereDwhPartition);
        Vector dwhPartitions = dwhPartitionFactory.get();
        Iterator dwhPartitionsIterator = dwhPartitions.iterator();

        while (dwhPartitionsIterator.hasNext()) {
          Dwhpartition currentDwhPartition = (Dwhpartition) dwhPartitionsIterator.next();
          String tableName = currentDwhPartition.getTablename();

          // Drop the table from DWH schema.
          String query = "drop table " + tableName + ";";
          try {
            dwhRockFactory.executeSql(query);

          } catch (SQLException e) {
            new ErrorDialog(DwhManagementTab.this.frame, "Error", "Table " + tableName + " not found in DWH.", e);
          }
          DwhManagementTab.this.log.finest("Executed SQL " + query + " in DWH schema.");

          // Drop the DWHPartition entry itself.
          DwhManagementTab.this.log.finest("Deleting DWHPartition " + currentDwhPartition.getTablename());
          currentDwhPartition.deleteDB();

        }

        // Start the deletion of DWHColumns.
        Dwhcolumn whereDwhColumn = new Dwhcolumn(this.dwhrepRockFactory);
        whereDwhColumn.setStorageid(currentDwhType.getStorageid());
        DwhcolumnFactory dwhColumnFactory = new DwhcolumnFactory(this.dwhrepRockFactory, whereDwhColumn);
        Vector dwhColumns = dwhColumnFactory.get();
        Iterator dwhColumnsIterator = dwhColumns.iterator();

        while (dwhColumnsIterator.hasNext()) {
          Dwhcolumn currentDwhColumn = (Dwhcolumn) dwhColumnsIterator.next();
          // Delete the DWHColumn entries.
          DwhManagementTab.this.log.finest("Deleting DWHColumn " + currentDwhColumn.getStorageid() + " "
              + currentDwhColumn.getDataname());
          currentDwhColumn.deleteDB();
        }

        // Drop the DWHType entry itself.
        DwhManagementTab.this.log.finest("Deleting DWHType " + currentDwhType.getStorageid());
        currentDwhType.deleteDB();
      }

      // Start deleting the ExternalStatementStatus entries.
      Externalstatementstatus whereExternalStatementStatus = new Externalstatementstatus(this.dwhrepRockFactory);
      whereExternalStatementStatus.setTechpack_name(targetDwhTechPack.getTechpack_name());
      ExternalstatementstatusFactory externalStatementStatusFactory = new ExternalstatementstatusFactory(
          this.dwhrepRockFactory, whereExternalStatementStatus);
      Vector externalStatementStatus = externalStatementStatusFactory.get();
      Iterator externalStatementStatusIterator = externalStatementStatus.iterator();

      while (externalStatementStatusIterator.hasNext()) {
        Externalstatementstatus currentExternalStatementStatus = (Externalstatementstatus) externalStatementStatusIterator
            .next();
        // Delete the ExternalStatementStatus entry.
        DwhManagementTab.this.log.finest("Deleting ExternalStatementStatus "
            + currentExternalStatementStatus.getTechpack_name() + " "
            + currentExternalStatementStatus.getStatementname());
        currentExternalStatementStatus.deleteDB();
      }

    } catch (Exception e) {
      new ErrorDialog(DwhManagementTab.this.frame, "Error", "DWH Techpack related data removal failed", e);
    }

  }

}
