package com.distocraft.dc5000.etl.gui.set;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.MetaSelector;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.gui.setwizard.CreateAggregatorSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateDWHMSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateLoaderSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateTPDirCheckerSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateTPDiskmanagerSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateTopologyLoaderSet;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;
import com.distocraft.dc5000.repository.dwhrep.Versioning;


/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class SetWizardDialog implements TreeSelectionListener {

  private JFrame frame;

  private RockFactory rock;
  private RockFactory dwhrepRock;

  private Meta_collection_sets tp;

  private JDialog jd;

  private UI ui;

  private MetaSelector metaTree;

  private JCheckBox loadSet;
  
  private JCheckBox DWHMSet;

  private JCheckBox aggregateSet;

  private JCheckBox dirCheck;

  private JCheckBox diskmanager;

  private JCheckBox topologyLoader;

  private JCheckBox scheduling;

  private JButton generate;

  private JComboBox version;
  
  private List vList = null;
  
  public SetWizardDialog(JFrame frame, String connID,RockFactory dwhrepRock, RockFactory rock, Meta_collection_sets tp, UI ui) {
    this.frame = frame;
    this.rock = rock;
    this.dwhrepRock = dwhrepRock;
    this.tp = tp;
    this.ui = ui;

    jd = new JDialog(frame, true);

    jd.setTitle("Set Wizard");

    Container con = jd.getContentPane();
    con.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    con.add(new JLabel("Select metadata instance for generation"), c);

    metaTree = new MetaSelector(dwhrepRock,connID, tp);
    metaTree.addTreeSelectionListener(this);
    c.gridy = 2;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    con.add(metaTree, c);

    c.gridy = 3;
    c.weighty = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 4;
    con.add(new JLabel("Select Version"), c);

    c.gridx = 1;
    c.gridy = 5;
    vList = getVersions();
    version = new JComboBox(vList.toArray());
    version.setSelectedIndex(0);
    con.add(version, c);

    
    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Generate"), c);

    c.gridx = 1;
    c.gridy = 7;
    c.insets = new Insets(1, 2, 0, 2);
    DWHMSet = new JCheckBox("DWH sets");
    DWHMSet.setSelected(true);
    DWHMSet.setEnabled(false);
    con.add(DWHMSet, c);

    c.gridx = 1;
    c.gridy = 8;
    c.insets = new Insets(1, 2, 0, 2);
    loadSet = new JCheckBox("Load sets");
    loadSet.setSelected(true);
    loadSet.setEnabled(false);
    con.add(loadSet, c);

    
    c.gridy = 9;
    aggregateSet = new JCheckBox("Aggregation sets");
    aggregateSet.setSelected(true);
    aggregateSet.setEnabled(false);
    con.add(aggregateSet, c);

    c.gridy = 10;
    c.insets = new Insets(1, 2, 0, 2);
    dirCheck = new JCheckBox("Directory Checker sets");
    dirCheck.setSelected(true);
    dirCheck.setEnabled(false);
    con.add(dirCheck, c);

    c.gridy = 11;
    diskmanager = new JCheckBox("Diskmanager sets");
    diskmanager.setSelected(true);
    diskmanager.setEnabled(false);
    con.add(diskmanager, c);

    c.gridy = 12;
    topologyLoader = new JCheckBox("TopologyLoader sets");
    topologyLoader.setSelected(true);
    topologyLoader.setEnabled(false);
    con.add(topologyLoader, c);

    c.gridy = 13;
    scheduling = new JCheckBox("Schedulings");
    scheduling.setSelected(true);
    scheduling.setEnabled(false);
    con.add(scheduling, c);

    c.gridy = 14;
    c.insets = new Insets(2, 2, 2, 2);
    c.fill = GridBagConstraints.BOTH;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 15;
    c.weighty = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.SOUTH;
    c.fill = GridBagConstraints.NONE;
    generate = new JButton("Generate", ConfigTool.bulb);
    generate.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        WizardWorker ww = new WizardWorker();
        ww.start();
      }
    });
    generate.setEnabled(false);
    con.add(generate, c);

    c.gridx = 2;
    c.gridy = 16;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    jd.pack();
    jd.setVisible(true);

  }

  private List getVersions() {

    List result = null;

    try {
      ClassLoader cl = this.getClass().getClassLoader();
      InputStreamReader isr = new InputStreamReader(cl.getResourceAsStream("templateVersion.properties"));
      BufferedReader br = new BufferedReader(isr);

      if (br != null) {

        result = new ArrayList();
        String line = "";
        while ((line = br.readLine()) != null) {

          try {
            if (!line.equalsIgnoreCase("")) {
              String[] splitted = line.split("=");
              result.add(splitted[0].trim());
            }

          } catch (Exception e) {

          }
        }
        br.close();
      }

    } catch (Exception e) {
      System.out.println(e);
    }

    return result;
  }

  public class WizardWorker extends SwingWorker {

    public WizardWorker() {
      ui.startOperation("Generating sets...");
      metaTree.setEnabled(false);
      DWHMSet.setEnabled(false);
      loadSet.setEnabled(false);
      aggregateSet.setEnabled(false);
      dirCheck.setEnabled(false);
      diskmanager.setEnabled(false);
      topologyLoader.setEnabled(false);
      scheduling.setEnabled(false);
      generate.setEnabled(false);
      version.setEnabled(false);
    }

    public Object construct() {


      try {

        Versioning vers = metaTree.getSelectedMeta();

        if (vers == null) // A meta was NOT selected
          return null;

        // Try to find weather specified tp exists or do we need to create it

        Meta_collection_sets mwhere = new Meta_collection_sets(rock);
        mwhere.setCollection_set_name(vers.getTechpack_name());
        mwhere.setVersion_number(vers.getTechpack_version());

        Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(rock, mwhere);
        Vector tps = mcsf.get();

        Meta_collection_sets techPack = null;

        if (tps.size() <= 0) { // not exists -> create
          techPack = new Meta_collection_sets(rock);
          techPack.setCollection_set_id(TechPackTree.getNextTechPackID(rock));
          techPack.setCollection_set_name(vers.getTechpack_name());
          techPack.setVersion_number(vers.getTechpack_version());
          
          if(vers.getTechpack_type().equalsIgnoreCase("custom")) {
            techPack.setType("Custompack");
            techPack.setDescription("CustomPack " + vers.getTechpack_name() + " " + vers.getTechpack_version()+ " by Configtool b"+ConfigTool.BUILD_NUMBER);
          } else if(vers.getTechpack_type().equalsIgnoreCase("system")) {
            throw new Exception("Wizard cannot be used with SYSTEM techpacks.");
          } else {
            techPack.setType("Techpack");
            techPack.setDescription("TechPack " + vers.getTechpack_name() + " " + vers.getTechpack_version()+ " by Configtool b"+ConfigTool.BUILD_NUMBER);
          }
          
          techPack.setEnabled_flag("Y");

          techPack.insertDB(false, false);

        } else {
          techPack = (Meta_collection_sets) tps.get(0);
        }

        if (DWHMSet.isSelected()) {
          CreateDWHMSet cls = new CreateDWHMSet(vers, rock, techPack.getCollection_set_id()
              .longValue(), techPack.getCollection_set_name(), scheduling.isSelected());
          cls.create();
        }
       
        if (loadSet.isSelected()) {
          CreateLoaderSet cls = new CreateLoaderSet((String)vList.get(version.getSelectedIndex()), vers, dwhrepRock, rock, (int) techPack.getCollection_set_id()
              .longValue(), techPack.getCollection_set_name(), scheduling.isSelected());
          cls.create();
        }

        if (aggregateSet.isSelected()) {
          CreateAggregatorSet cas = new CreateAggregatorSet((String)vList.get(version.getSelectedIndex()), vers, dwhrepRock, rock, (int) techPack.getCollection_set_id()
              .longValue(), scheduling.isSelected());
          cas.create();
        }

        if (dirCheck.isSelected()) {
          CreateTPDirCheckerSet cdc = new CreateTPDirCheckerSet(vers, dwhrepRock, rock, techPack.getCollection_set_id()
              .longValue(), techPack.getCollection_set_name());
          cdc.create(topologyLoader.isSelected());
        }

        if (diskmanager.isSelected()) {
          CreateTPDiskmanagerSet cdm = new CreateTPDiskmanagerSet(vers, rock, techPack.getCollection_set_id()
              .longValue(), techPack.getCollection_set_name());
          cdm.create();
        }

        if (topologyLoader.isSelected()) {
          CreateTopologyLoaderSet tls = new CreateTopologyLoaderSet((String)vList.get(version.getSelectedIndex()),vers, dwhrepRock, rock, (int) techPack
              .getCollection_set_id().longValue(), techPack.getCollection_set_name() , scheduling.isSelected());
          tls.create();

        }

        
        ConfigTool.activateScheduler();
        
        
      } catch (Exception e) {
        return e;
      }      

      return null;
    }

    public void finished() {

      Exception e = (Exception) get();

      if (e != null) {
        ErrorDialog ed = new ErrorDialog(frame, "Set Wizard Error", "Set Wizard failed exceptionally", e);
      }

      jd.setVisible(false);
      ui.endOperation();
    }

  };

  public void valueChanged(TreeSelectionEvent e) {
    TreePath tp = e.getPath();
    if (tp != null && ((DefaultMutableTreeNode) tp.getLastPathComponent()).getUserObject() instanceof Versioning) {
      generate.setEnabled(true);
      loadSet.setEnabled(true);
      DWHMSet.setEnabled(true);
      aggregateSet.setEnabled(true);
      dirCheck.setEnabled(true);
      diskmanager.setEnabled(true);
      topologyLoader.setEnabled(true);
      scheduling.setEnabled(true);
      version.setEnabled(true);
    } else {
      generate.setEnabled(false);
      loadSet.setEnabled(false);
      DWHMSet.setEnabled(false);
      aggregateSet.setEnabled(false);
      dirCheck.setEnabled(false);
      diskmanager.setEnabled(false);
      topologyLoader.setEnabled(false);
      scheduling.setEnabled(false);
      version.setEnabled(false);
    }
  }

}
