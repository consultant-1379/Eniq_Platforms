package com.distocraft.dc5000.etl.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.importexport.ETLCImport;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class InstallTab extends JPanel implements Tab {

  private JFrame frame;

  private RockFactory rock;

  private JSplitPane split;

  private JTextField fileName;

  private JCheckBox sets;

  private JCheckBox schedules;

  private JButton insert;

  private JTextArea log;

  private Connection rockConn;

  private SystemStructureNotificate ssn;

  public InstallTab(JFrame frame, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.frame = frame;
    this.ssn = ssn;

    disconnected();
  }

  /**
   * @see com.distocraft.dc5000.etl.gui.Tab#connected(ssc.rockfactory.RockFactory)
   */
  public void connected(RockFactory rock, RockFactory dwhrepRock, String connectionID) {

    rockConn = rock.getConnection();
    this.removeAll();

    GridBagConstraints rc = new GridBagConstraints();
    rc.fill = GridBagConstraints.NONE;
    rc.anchor = GridBagConstraints.NORTHWEST;
    rc.weightx = 0;
    rc.weighty = 0;

    this.add(Box.createRigidArea(new Dimension(30, 10)), rc);

    rc.gridx = 1;
    rc.gridy = 1;
    rc.weightx = 1;
    rc.fill = GridBagConstraints.HORIZONTAL;
    rc.insets = new Insets(2, 2, 5, 2);
    this.add(new JLabel("Select installation file"), rc);

    rc.gridy = 2;
    this.add(Box.createRigidArea(new Dimension(10, 10)));

    rc.gridy = 3;
    rc.fill = GridBagConstraints.BOTH;
    fileName = new JTextField("", 25);
    fileName.setEditable(false);
    this.add(fileName, rc);

    rc.gridx = 2;
    rc.weightx = 0;
    rc.fill = GridBagConstraints.NONE;
    JButton choose = new JButton(ConfigTool.openDoc);
    choose.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        final JFileChooser fc = new JFileChooser();
        if (ConfigTool.fileDialogLastSelection != null)
          fc.setCurrentDirectory(ConfigTool.fileDialogLastSelection);
        fc.setDialogTitle("Choose installation file");
        fc.setFileFilter(new FileFilter() {

          public boolean accept(File f) {
            if (f.canRead() && (f.getName().endsWith(".xml") || f.isDirectory()))
              return true;
            else
              return false;
          }

          public String getDescription() {
            return "Installation files (.xml)";
          }
        });

        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
          File sel = fc.getSelectedFile();

          if (sel != null) {
            ConfigTool.fileDialogLastSelection = sel.getParentFile();
            fileName.setText(sel.getPath());
            insert.setEnabled(true);
            log.append("File selected: " + sel.getPath() + "\n");
          } else {
            insert.setEnabled(false);
            fileName.setText("");
          }

        }
      }
    });
    this.add(choose, rc);

    rc.gridx = 1;
    rc.gridy = 4;
    this.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    rc.gridy = 5;
    sets = new JCheckBox("Install sets", true);
    sets.addItemListener(new ItemListener() {

      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
          schedules.setEnabled(false);
          schedules.setSelected(false);
        } else if (e.getStateChange() == ItemEvent.SELECTED) {
          schedules.setEnabled(true);
        }
      }
    });
    this.add(sets, rc);

    rc.gridy = 6;
    schedules = new JCheckBox("Install schedules", true);
    this.add(schedules, rc);

    rc.gridy = 7;
    rc.fill = GridBagConstraints.NONE;
    this.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    insert = new JButton("Install", ConfigTool.dataStore);
    insert.setEnabled(false);
    insert.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        InstallWorker iw = new InstallWorker();
        iw.start();
      }
    });

    rc.gridy = 8;
    this.add(insert, rc);

    rc.gridy = 9;
    this.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    log = new JTextArea(10, 40);
    log.setEditable(false);
    JScrollPane logScroll = 
      new JScrollPane(log,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    log.setText("--- Installation log ---\nReady to instal. Select installation file.\n");
    rc.gridy = 10;
    rc.gridwidth = 2;
    rc.weightx = 1;
    rc.weighty = 1;
    rc.fill = GridBagConstraints.BOTH;
    this.add(logScroll, rc);

    rc.gridy = 11;
    rc.gridx = 5;
    rc.weightx = 0;
    rc.weighty = 0;
    rc.gridwidth = 1;
    rc.fill = GridBagConstraints.BOTH;
    this.add(Box.createRigidArea(new Dimension(30, 10)), rc);

    this.invalidate();
    this.validate();
    this.repaint();

  }

  /**
   * @see com.distocraft.dc5000.etl.gui.Tab#disconnected()
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

  public class InstallWorker extends SwingWorker {

    public InstallWorker() {
      log.append("Installing:");
      if(sets.isSelected())
        log.append(" sets");
      if(schedules.isSelected())
        log.append(" schedules");
      log.append("...\n");
    }

    public Object construct() {
      try {
        
        String dir = System.getProperty("configtool.templatedir", "");

        File file = new File(fileName.getText());

        if (file.isFile() && file.canRead()) {

          ETLCImport imp = new ETLCImport(dir, rockConn);
          imp.doImport(fileName.getText(), sets.isSelected(), schedules.isSelected());

          ConfigTool.activateScheduler();

        }

      } catch (Exception e) {
        return e;
      }

      return null;
    }

    public void finished() {

      Exception e = (Exception) get();

      if (e != null) {
        ErrorDialog ed = new ErrorDialog(frame, "Set Installer Error", "Installer failed exceptionally", e);
        log.append("Installation failed: " + e.getMessage() + "\n");
      } else {
        log.append("Successfully installed.\n");
      }

      ssn.setChange();

    }

  };

}
