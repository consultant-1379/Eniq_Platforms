package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Component;
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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.gui.set.TechPackTree;
import com.distocraft.dc5000.etl.gui.setwizard.CreateIDirCheckerSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateIDiskmanagerSet;
import com.distocraft.dc5000.etl.gui.setwizard.CreateInterfaceSet;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class InterfaceWizardDialog {

  private JFrame frame;

  private RockFactory dwhrepRock;

  private RockFactory rock;

  private JDialog jd;

  private UI ui;

  private String[] conn_names = new String[0];

  private Long[] conn_ids = new Long[0];

  private JTextField name;

  private JTabbedPane versiontab;

  private JComboBox techPack;

  private JComboBox adapterType;

  private JTextField TpVersion;

  private JComboBox interfaceType;

  private JTextField pmdir;

  private JComboBox dbcon;

  private JTextArea description;

  private JTextField elementType;

  private JButton generate;

  private JButton cancel;

  private JComboBox version;

  private JTextArea techpacks;

  private List vList = null;

  public InterfaceWizardDialog(JFrame frame, RockFactory dwhrepRock, RockFactory rock, UI ui) {
    this.frame = frame;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.ui = ui;

    jd = new JDialog(frame, true);

    try {
      Meta_databases mdb = new Meta_databases(rock);
      Meta_databasesFactory mdf = new Meta_databasesFactory(rock, mdb);
      Vector cons = mdf.get();

      conn_names = new String[cons.size()];
      conn_ids = new Long[cons.size()];

      for (int i = 0; i < cons.size(); i++) {
        Meta_databases md = (Meta_databases) cons.get(i);
        conn_names[i] = md.getConnection_name();
        conn_ids[i] = md.getConnection_id();
      }
    } catch (Exception e) {
      conn_names = new String[0];
      conn_ids = new Long[0];
      ErrorDialog ed = new ErrorDialog(frame, "Error", "Unable to list DB connections", e);
    }

    jd.setTitle("Interface Wizard");

    Container con = jd.getContentPane();
    con.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridx = 1;
    c.gridy = 1;
    con.add(new JLabel("Interface name"), c);

    c.gridx = 2;
    name = new JTextField("INTF_", 20);
    con.add(name, c);

    vList = getVersions();

    Collections.sort(vList);

    versiontab = new JTabbedPane();

    for (int i = 0; i < vList.size(); i++) {

      String version = (String) vList.get(i);

      JPanel cont = new JPanel(new GridBagLayout());
      GridBagConstraints gc = new GridBagConstraints();

      gc.anchor = GridBagConstraints.NORTHWEST;
      gc.fill = GridBagConstraints.NONE;
      gc.insets = new Insets(2, 2, 2, 2);

      if (version.equals("5.0") || version.equals("5.1")) {

        cont.add(Box.createRigidArea(new Dimension(5, 5)), gc);

        gc.gridx = 1;
        gc.gridy = 1;

        cont.add(new JLabel("TechPack"), gc);

        techPack = new TPComboBox(dwhrepRock);
        gc.gridx = 2;
        cont.add(techPack, gc);

        gc.gridx = 1;
        gc.gridy = 4;
        cont.add(new JLabel("Interface Type"), gc);

        gc.gridx = 2;
        gc.gridy = 4;
        interfaceType = new JComboBox(ConfigTool.INTERFACE_TYPES);
        interfaceType.setSelectedIndex(0);
        cont.add(interfaceType, gc);

        gc.gridx = 1;
        gc.gridy = 5;
        cont.add(new JLabel("PMData directory"), gc);

        pmdir = new JTextField("", 20);
        gc.gridx = 2;
        cont.add(pmdir, gc);

        gc.gridx = 1;
        gc.gridy = 6;
        cont.add(new JLabel("DB Connection"), gc);

        dbcon = new JComboBox(conn_ids);
        dbcon.setRenderer(new ListCellRenderer() {

          public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
              boolean cellHasFocus) {
            Long mc = (Long) value;
            for (int i = 0; i < conn_ids.length; i++) {
              if (conn_ids[i].equals(mc))
                return new JLabel(conn_names[i]);
            }

            return new JLabel(String.valueOf(mc));
          }
        });

        for (int ic = 0; ic < conn_names.length; ic++) {
          if (conn_names[ic].equalsIgnoreCase("dwh"))
            dbcon.setSelectedIndex(i);
        }
        gc.gridx = 2;
        cont.add(dbcon, gc);

        gc.gridx = 1;
        gc.gridy = 7;
        cont.add(new JLabel("Adapter type"), gc);

        adapterType = new JComboBox(ConfigTool.ADAPTER_TYPES);
        adapterType.setEditable(true);
        gc.gridx = 2;
        cont.add(adapterType, gc);

        gc.gridx = 1;
        gc.gridy = 8;
        cont.add(new JLabel("Description"), gc);

        description = new JTextArea(4, 30);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        gc.gridx = 2;
        cont.add(description, gc);

      } else { // Version is 5.2 ->

        dbcon.setSelectedIndex(2);
        
        cont.add(Box.createRigidArea(new Dimension(5, 5)), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        cont.add(new JLabel("Interface Type"), gc);

        gc.gridx = 2;
        interfaceType = new JComboBox(ConfigTool.INTERFACE_TYPES);
        interfaceType.setSelectedIndex(0);
        cont.add(interfaceType, gc);

        gc.gridx = 1;
        gc.gridy = 2;
        cont.add(new JLabel("Network element type"), gc);

        elementType = new JTextField(15);
        gc.gridx = 2;
        cont.add(elementType, gc);

        gc.gridx = 1;
        gc.gridy = 3;
        cont.add(new JLabel("Interface Version"), gc);

        TpVersion = new JTextField(15);
        gc.gridx = 2;
        cont.add(TpVersion, gc);

        gc.gridx = 1;
        gc.gridy = 4;
        cont.add(new JLabel("Techpack names"), gc);

        techpacks = new JTextArea(5, 15);
        gc.gridx = 2;
        cont.add(techpacks, gc);

        gc.gridx = 1;
        gc.gridy = 5;
        cont.add(new JLabel("Adapter type"), gc);

        adapterType = new JComboBox(ConfigTool.ADAPTER_TYPES);
        adapterType.setEditable(true);
        gc.gridx = 2;
        cont.add(adapterType, gc);

        gc.gridx = 1;
        gc.gridy = 6;
        cont.add(new JLabel("Description"), gc);

        description = new JTextArea(4, 30);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        gc.gridx = 2;
        cont.add(description, gc);

      }

      gc.gridx = 3;
      gc.gridy = 8;
      cont.add(Box.createRigidArea(new Dimension(5, 5)), gc);

      versiontab.addTab(version, cont);

    } // foreach version

    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 2;
    con.add(versiontab, c);

    c.gridy = 3;
    c.gridx = 3;
    c.weighty = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 4;
    c.gridx = 1;
    c.weighty = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.fill = GridBagConstraints.NONE;
    cancel = new JButton("Discard", ConfigTool.delete);
    cancel.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        jd.setVisible(false);
      }
    });
    con.add(cancel, c);

    c.gridx = 2;
    c.anchor = GridBagConstraints.SOUTHWEST;
    generate = new JButton("Generate", ConfigTool.bulb);
    generate.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        String err = "";

        String sname = name.getText().trim();

        if (sname.length() <= 0)
          err += "Name must be defined\n";

        int ix = sname.indexOf("_");

        if (ix <= 0 || !sname.substring(0, ix + 1).equals("INTF_"))
          err += "Name must begin with INTF_\n";

        if (adapterType.getSelectedIndex() < 0 || ((String) adapterType.getSelectedItem()).trim().length() <= 0)
          err += "Type must be defined\n";

        String vers = versiontab.getTitleAt(versiontab.getSelectedIndex());

        if (vers.equals("5.0") || vers.equals("5.1")) {

          String pmd = pmdir.getText().trim();
          if (pmd.length() <= 0)
            err += "PMData dir must be defined\n";

        } else {

          if (elementType.getText().trim().length() <= 0)
            err += "Element type must be defined\n";

        }

        if (err.length() > 0) {
          JOptionPane.showMessageDialog(jd, err, "Invalid parameters", JOptionPane.ERROR_MESSAGE);
          return;
        }

        WizardWorker ww = new WizardWorker();
        ww.start();
      }
    });
    generate.setEnabled(true);
    con.add(generate, c);

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
      ui.startOperation("Generating interface...");

      name.setEnabled(false);
      techPack.setEnabled(false);
      adapterType.setEnabled(false);
      interfaceType.setEnabled(false);
      pmdir.setEnabled(false);
      dbcon.setEnabled(false);
      description.setEnabled(false);

      generate.setEnabled(false);
      cancel.setEnabled(false);
    }

    public Object construct() {
      try {

        Meta_collection_sets mwhere = new Meta_collection_sets(rock);
        mwhere.setCollection_set_name(name.getText().trim());

        Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(rock, mwhere);
        Vector tps = mcsf.get();

        Meta_collection_sets etl_tp = null;

        Versioning dwh_tp = (Versioning) techPack.getSelectedItem();

        String tpVersion = "";
        String versionID = "";

        
        if (((String) vList.get(versiontab.getSelectedIndex())).equalsIgnoreCase("5.0")
            || ((String) vList.get(versiontab.getSelectedIndex())).equalsIgnoreCase("5.1")) {
        
          if (dwh_tp == null) {
            tpVersion = TpVersion.getText();
            versionID = "";
          } else {
            tpVersion = dwh_tp.getTechpack_version();
            versionID = dwh_tp.getVersionid();
          }

          
        } else {
          //5.2
          tpVersion = TpVersion.getText();
          
        }
        
        
        
        
        if (tps.size() <= 0) { // etlrep techpack does not exists -> create new

          etl_tp = new Meta_collection_sets(rock);
          etl_tp.setCollection_set_id(TechPackTree.getNextTechPackID(rock));
          etl_tp.setCollection_set_name(name.getText().trim());
          etl_tp.setVersion_number(tpVersion);
          etl_tp.setDescription("Interface " + name.getText().trim() + " by ConfigTool b" + ConfigTool.BUILD_NUMBER);
          etl_tp.setEnabled_flag("Y");
          etl_tp.setType("Interface");

          etl_tp.insertDB(false, false);

        } else {
          etl_tp = (Meta_collection_sets) tps.get(0);
        }

        Long dbcon_id = ((Long) dbcon.getSelectedItem());

        CreateIDirCheckerSet cdc = new CreateIDirCheckerSet((String) interfaceType.getSelectedItem(), etl_tp
            .getVersion_number(), rock, etl_tp.getCollection_set_id().longValue(), name.getText().trim(), elementType.getText().trim()
            );
        cdc.create();

        CreateIDiskmanagerSet cdm = new CreateIDiskmanagerSet((String) interfaceType.getSelectedItem(), etl_tp
            .getVersion_number(), rock, etl_tp.getCollection_set_id().longValue(), name.getText().trim(),elementType.getText().trim());
        cdm.create();

        String setName = ((String) adapterType.getSelectedItem()).trim();
        int i = setName.lastIndexOf(".");
        if (i < 0)
          i = 0;
        setName = setName.substring(i + 1);



        if (((String) vList.get(versiontab.getSelectedIndex())).equalsIgnoreCase("5.0")
            || ((String) vList.get(versiontab.getSelectedIndex())).equalsIgnoreCase("5.1")) {


          CreateInterfaceSet cis = new CreateInterfaceSet((String) interfaceType.getSelectedItem(), (String) vList
              .get(versiontab.getSelectedIndex()), tpVersion, versionID, dwhrepRock, rock, etl_tp.getCollection_set_id()
              .longValue(), name.getText().trim(), (String) adapterType.getSelectedItem(), (String) adapterType
              .getSelectedItem(), elementType.getText().trim(), dbcon_id.toString());

          cis.create();

        } else {


    

          CreateInterfaceSet cis = new CreateInterfaceSet((String) interfaceType.getSelectedItem(), (String) vList
              .get(versiontab.getSelectedIndex()), tpVersion, versionID, dwhrepRock, rock, etl_tp.getCollection_set_id()
              .longValue(), name.getText().trim(), (String) adapterType.getSelectedItem(), (String) adapterType
              .getSelectedItem(), elementType.getText().trim(), dbcon_id.toString());

          cis.createTechpacks(techpacks.getText(),(String)adapterType.getSelectedItem());
        }

      } catch (Exception e) {
        return e;
      }

      return null;
    }

    public void finished() {

      Exception e = (Exception) get();

      if (e != null) {
        ErrorDialog ed = new ErrorDialog(frame, "Interface Wizard Error", "Interface Wizard failed exceptionally", e);
      }

      jd.setVisible(false);
      ui.endOperation();
    }

  };

}
