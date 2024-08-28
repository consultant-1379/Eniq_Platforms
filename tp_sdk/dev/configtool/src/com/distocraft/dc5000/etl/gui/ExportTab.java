package com.distocraft.dc5000.etl.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Caret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.importexport.ETLCExport;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class ExportTab extends JPanel implements Tab {

  private JFrame frame;

  private SystemStructureNotificate ssn;

  private RockFactory rock;

  private JSplitPane split;

  private JTextField fileName;

  private JButton insert;

  private JTextArea log;

  private TechPackSelector tps;

  private Meta_collection_sets mcs = null;

  private Connection rockConn;

  private String outputFileName = "";
  
  private JScrollPane logScroll;
  
  public ExportTab(JFrame frame, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.frame = frame;
    this.ssn = ssn;

    disconnected();
  }

  /**
   * @see com.distocraft.dc5000.etl.gui.Tab#connected(ssc.rockfactory.RockFactory)
   */
  public void connected(RockFactory rock, RockFactory dwhrepRock, String connectionID) {
    this.rock = rock;
    rockConn = rock.getConnection();
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
    left.add(new JLabel("Packages available at " + connectionID), lc);

    lc.gridy = 2;
    lc.gridx = 2;
    left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

    lc.gridy = 3;
    lc.gridx = 0;
    lc.gridwidth = 3;
    lc.fill = GridBagConstraints.BOTH;
    lc.weightx = 1;
    lc.weighty = 1;

    tps = new TechPackSelector(connectionID, rock);
    tps.addTreeSelectionListener(new TechPackSelectorListener(rock));

    ssn.addSystemStructureListener(tps);
    left.add(tps, lc);

    // RIGHT PANEL STARTS HEE

    JPanel right = new JPanel(new GridBagLayout());

    GridBagConstraints rc = new GridBagConstraints();
    rc.fill = GridBagConstraints.NONE;
    rc.anchor = GridBagConstraints.NORTHWEST;
    rc.weightx = 0;
    rc.weighty = 0;

    right.add(Box.createRigidArea(new Dimension(30, 10)), rc);

    rc.gridx = 1;
    rc.gridy = 1;
    rc.insets = new Insets(2, 2, 5, 2);
    right.add(new JLabel("Export package into installation file"), rc);

    rc.gridy = 2;
    right.add(Box.createRigidArea(new Dimension(10, 10)));

    rc.gridy = 3;
    rc.weightx = 1;
    rc.fill = GridBagConstraints.BOTH;
    fileName = new JTextField("", 25);
    fileName.setEditable(false);
    right.add(fileName, rc);

    rc.gridx = 2;
    rc.weightx = 0;
    rc.fill = GridBagConstraints.NONE;
    JButton choose = new JButton(ConfigTool.openDoc);
    choose.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        final JFileChooser fc = new JFileChooser();
        if(ConfigTool.fileDialogLastSelection != null)
          fc.setCurrentDirectory(ConfigTool.fileDialogLastSelection);
        fc.setDialogTitle("Choose installation filename for export");
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
            ConfigTool.fileDialogLastSelection = sel.getParentFile();
            log.append("File selected: " + sel.getPath() + "\n");
          } else {
            fileName.setText("");
          }
          
          if(fileName.getText().length() > 0 && mcs != null)
            insert.setEnabled(true);
          else
            insert.setEnabled(false);

        }
      }
    });
    right.add(choose, rc);

    rc.gridx = 1;
    rc.gridy = 4;
    rc.fill = GridBagConstraints.NONE;
    right.add(Box.createRigidArea(new Dimension(10, 10)));

    // rc.gridy = 5;
    // rc.gridy = 6;

    rc.gridy = 7;
    rc.fill = GridBagConstraints.NONE;
    right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    insert = new JButton("Export", ConfigTool.dataExtract);
    insert.setEnabled(false);
    insert.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {

        BuildWorker bw = new BuildWorker();
        bw.start();

      }
    });

    rc.gridy = 8;
    right.add(insert, rc);

    rc.gridy = 9;
    right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    log = new JTextArea(10, 40);
    log.setEditable(false);
 
    

    
     logScroll = 
      new JScrollPane(log,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    logScroll.setAutoscrolls(true);
    log.setText("--- Export log ---\nSelect package and export file.\n");
    rc.gridy = 10;
    rc.gridwidth = 2;
    rc.weightx = 1;
    rc.weighty = 1;
    rc.fill = GridBagConstraints.BOTH;
    right.add(logScroll, rc);

    rc.gridy = 11;
    rc.gridx = 5;
    rc.weightx = 0;
    rc.weighty = 1;
    rc.gridwidth = 0;
    rc.fill = GridBagConstraints.BOTH;
    right.add(Box.createRigidArea(new Dimension(30, 10)), rc);

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    this.add(split, c);

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

  public void mousePressed(MouseEvent e) {
  }

  public class TechPackSelectorListener implements TreeSelectionListener {

    private RockFactory rock;

    public TechPackSelectorListener(RockFactory rock) {
      this.rock = rock;
    }

    public void valueChanged(TreeSelectionEvent tse) {
      TreePath tp = tse.getPath();
      if (tp == null)
        return;

      DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) tp.getLastPathComponent();

      if (dtm.getUserObject() instanceof Meta_collection_sets) {
        mcs = (Meta_collection_sets) dtm.getUserObject();
      } else
        mcs = null;
      
      if(fileName.getText().length() > 0 && mcs != null)
        insert.setEnabled(true);
      else
        insert.setEnabled(false);

    }
  };

  public class BuildWorker extends SwingWorker {

    public BuildWorker() {
    }

    public Object construct() {
      try {

        String dir = System.getProperty("configtool.templatedir", "");

        log.append("Exporting "+mcs.getCollection_set_name()+"...\n");

        String replaseStr = "#version#=" + mcs.getVersion_number() + ",#techpack#=" + mcs.getCollection_set_name();

        ETLCExport des = new ETLCExport(dir, rockConn);
        des.exportXml(replaseStr, fileName.getText());
        
        log.setCaretPosition( log.getDocument().getLength() );

      } catch (Exception e) {
        return e;
      }

      return null;
    }

    public void finished() {

      Exception e = (Exception) get();

      if (e != null) {
        ErrorDialog ed = new ErrorDialog(frame, "Export Error", "Exporter exceptionally", e);
        log.append("Export failed: "+e.getMessage()+"\n");
      } else {
        log.append("Successfully exported\n");
      }
      
    }

  };

}
