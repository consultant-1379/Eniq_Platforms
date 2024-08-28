package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SystemStructureNotificate;
import com.distocraft.dc5000.etl.gui.Tab;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.importexport.ETLCExport;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class InterfaceTab extends JPanel implements Tab, TreeSelectionListener, MouseListener {

  private RockFactory rock;

  private RockFactory dwhrepRock;

  private JFrame frame;

  private UI ui;

  private InterfaceTree itree = null;

  private JPanel right;

  private SystemStructureNotificate ssn;

  public InterfaceTab(JFrame frame, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.frame = frame;
    this.ui = ui;
    this.ssn = ssn;
    disconnected();
  }

  /**
   * @see com.distocraft.dc5000.etl.gui.Tab#connected(ssc.rockfactory.RockFactory)
   */
  public void connected(RockFactory rock, RockFactory dwhrepRock, String conID) {

    this.rock = rock;
    this.dwhrepRock = dwhrepRock;
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
    left.add(new JLabel("Interfaces"), lc);

    lc.gridy = 2;
    lc.gridx = 2;
    left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

    lc.gridy = 3;
    lc.gridx = 0;
    lc.gridwidth = 3;
    lc.fill = GridBagConstraints.BOTH;
    lc.weightx = 1;
    lc.weighty = 1;

    itree = new InterfaceTree(conID, dwhrepRock, frame);
    itree.addTreeSelectionListener(this);
    itree.addMouseListener(this);
    left.add(itree, lc);

    right = new JPanel(new GridBagLayout());

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
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

  /**
   * TreeSelectionListener method implementation
   */
  public void valueChanged(TreeSelectionEvent tse) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();

    right.removeAll();

    try {

      if (node instanceof InterfaceTreeNode) { // interface was selected

        InterfaceTreeNode itn = (InterfaceTreeNode) node;

        Datainterface di = itn.getDataInterface();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 6);

        right.add(Box.createRigidArea(new Dimension(10, 5)), c);

        c.gridx = 1;
        c.gridy = 1;
        right.add(new JLabel("Interface"), c);

        c.gridx = 2;
        right.add(new JLabel(di.getInterfacename()), c);

        c.gridx = 1;
        c.gridy = 2;
        right.add(new JLabel("Status"), c);

        c.gridx = 2;
        int stat = di.getStatus().intValue();
        if (stat == 1)
          right.add(new JLabel("enabled"), c);
        else
          right.add(new JLabel("disabled"), c);

        c.gridx = 1;
        c.gridy = 3;
        right.add(new JLabel("Type"), c);

        c.gridx = 2;
        right.add(new JLabel(di.getInterfacetype()), c);

        c.gridx = 1;
        c.gridy = 4;
        right.add(new JLabel("Description"), c);

        c.gridx = 2;
        int row = 4;

        StringBuffer desc = new StringBuffer(di.getDescription());

        while (desc.length() > 70) {

          int lastws = 70;

          for (int i = 0; i < 70; i++) {
            if (Character.isWhitespace(desc.charAt(i))) {
              lastws = i;
            }
          }

          c.gridy = row++;
          right.add(new JLabel(desc.substring(0, lastws)), c);
          desc.delete(0, lastws);

        }

        c.gridy = row++;
        right.add(new JLabel(desc.toString()), c);

        c.gridx = 1;
        c.gridy = 5;
        right.add(new JLabel("Techpacks"), c);

        String techpacks = "";

        // fetch interface measurements
        Interfacetechpacks itp = new Interfacetechpacks(dwhrepRock);
        itp.setInterfacename(di.getInterfacename());
        InterfacetechpacksFactory itpf = new InterfacetechpacksFactory(dwhrepRock, itp);
        Vector itps = itpf.get();

        if (itps != null) {
          Iterator i = itps.iterator();

          while (i.hasNext()) {
            Interfacetechpacks it = (Interfacetechpacks) i.next();
            techpacks += it.getTechpackname() + ", ";

          }
        }

        c.gridx = 2;
        right.add(new JLabel(techpacks), c);

        c.gridx = 3;
        c.gridy = row++;
        right.add(Box.createRigidArea(new Dimension(10, 5)), c);

        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        DataFormatTable dft = new DataFormatTable(frame, di, dwhrepRock);
        right.add(dft, c);

      }

    } catch (Exception e) {
      ErrorDialog ed = new ErrorDialog(frame, "Can't show DataFormat", "Initialization error", e);
    }

    this.invalidate();
    this.validate();
    this.repaint();

  }

  // MouseListener

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

    if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup

      if (tp.getLastPathComponent() instanceof InterfaceTreeNode) { // An
        // interface

        InterfaceTreeNode iftn = (InterfaceTreeNode) tp.getLastPathComponent();

        final Datainterface di = iftn.getDataInterface();

        JPopupMenu pop = new JPopupMenu("Interface");

        JMenuItem ed = new JMenuItem("Edit", ConfigTool.draw);
        ed.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            InterfaceWindow ifw = new InterfaceWindow(frame, dwhrepRock, di);
            if (ifw.committed()) {
              itree.refresh();
              right.removeAll();
              InterfaceTab.this.invalidate();
              InterfaceTab.this.validate();
              InterfaceTab.this.repaint();
            }
          }
        });
        pop.add(ed);

        JMenuItem ri = new JMenuItem("Remove this Interface", ConfigTool.delete);
        ri.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            try {

              // Set ims = di.getInterfaceMeasurements();

              // fetch interface measurements
              Interfacemeasurement aim = new Interfacemeasurement(dwhrepRock);
              aim.setInterfacename(di.getInterfacename());
              InterfacemeasurementFactory aimf = new InterfacemeasurementFactory(dwhrepRock, aim);
              Vector ims = aimf.get();

              if (ims != null) {
                Iterator i = ims.iterator();

                while (i.hasNext()) {
                  Interfacemeasurement im = (Interfacemeasurement) i.next();

                  // remove interface measurement
                  im.deleteDB();
                }
              }

              // fetch interface techpack
              Interfacetechpacks aimt = new Interfacetechpacks(dwhrepRock);
              aimt.setInterfacename(di.getInterfacename());
              InterfacetechpacksFactory aimtf = new InterfacetechpacksFactory(dwhrepRock, aimt);
              Vector imts = aimtf.get();

              if (imts != null) {
                Iterator i = imts.iterator();

                while (i.hasNext()) {
                  Interfacetechpacks imt = (Interfacetechpacks) i.next();

                  // delete interface techpack
                  imt.deleteDB();
                }
              }

              // fetch all sets
              ArrayList removeList = new ArrayList();
              Meta_collections mc = new Meta_collections(rock);
              Meta_collectionsFactory mcF = new Meta_collectionsFactory(rock, mc);
              Iterator iter = mcF.get().iterator();
              while (iter.hasNext()) {

                Meta_collections m = (Meta_collections) iter.next();

                // fetch actions
                Meta_transfer_actions mta = new Meta_transfer_actions(rock);
                mta.setCollection_id(m.getCollection_id());
                mta.setCollection_set_id(m.getCollection_set_id());
                Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);
                Iterator iter2 = mtaF.get().iterator();
                while (iter2.hasNext()) {

                  Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

                  // collect all techpacks that have parser set that have
                  // correct interfaceName.

                  String s = null;
                  Properties p = stringToProperty(a.getAction_contents());
                  if (p != null) {
                    s = (String) p.get("interfaceName");
                  }

                  if (s != null)
                    if (s.equalsIgnoreCase(di.getInterfacename())) {
                      removeList.add(a.getCollection_set_id());
                    }
                }
              }

              Iterator riter = removeList.iterator();
              while (riter.hasNext()) {

                Long collection_set_id = (Long) riter.next();

                // fetch sets
                Meta_collections rmc = new Meta_collections(rock);
                rmc.setCollection_set_id(collection_set_id);
                Meta_collectionsFactory rmcF = new Meta_collectionsFactory(rock, rmc);

                // iterate sets
                Iterator rriter = rmcF.get().iterator();
                while (rriter.hasNext()) {

                  Meta_collections m = (Meta_collections) rriter.next();

                  // fetch actions
                  Meta_transfer_actions mta = new Meta_transfer_actions(rock);
                  mta.setCollection_id(m.getCollection_id());
                  mta.setCollection_set_id(collection_set_id);
                  Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);
                  Iterator iter2 = mtaF.get().iterator();
                  while (iter2.hasNext()) {

                    Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

                    // remove action
                    a.deleteDB();
                  }

                  // fetch schedulings
                  Meta_schedulings ms = new Meta_schedulings(rock);
                  ms.setCollection_id(m.getCollection_id());
                  ms.setCollection_set_id(collection_set_id);
                  Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rock, ms);
                  Iterator iter3 = msF.get().iterator();
                  while (iter3.hasNext()) {

                    Meta_schedulings a = (Meta_schedulings) iter3.next();

                    // remove schedulings
                    a.deleteDB();
                  }

                  // remove set
                  m.deleteDB();

                }

                // fetch techpack
                Meta_collection_sets rmcs = new Meta_collection_sets(rock);
                rmcs.setCollection_set_id(collection_set_id);
                Meta_collection_setsFactory rmcsF = new Meta_collection_setsFactory(rock, rmcs);

                // remove techpack
                ((Meta_collection_sets) rmcsF.getElementAt(0)).deleteDB();

              }

              // delete data interface
              di.deleteDB();

              itree.refresh();

            } catch (Exception e) {
              ErrorDialog ed = new ErrorDialog(frame, "Error", "Failed to remove ", e);
            }
          }
        });
        pop.add(ri);

        JMenuItem mw = new JMenuItem("Version migration", ConfigTool.bulb);
        mw.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            InterfaceMigrationWizardDialog imwd = new InterfaceMigrationWizardDialog(frame, dwhrepRock, di, ui);
            itree.refresh();
          }
        });

        pop.add(mw);

        JMenuItem ii = new JMenuItem("Export interface", ConfigTool.dataExtract);
        ii.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {

            String baseDir = "";

            JFileChooser chooser = new JFileChooser();
            if (ConfigTool.fileDialogLastSelection != null)
              chooser.setCurrentDirectory(ConfigTool.fileDialogLastSelection);

            chooser.setDialogTitle("Choose directory for interface export");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
              baseDir = chooser.getSelectedFile().getAbsolutePath().replaceAll("\\\\", "/");
              ;
              ConfigTool.fileDialogLastSelection = new File(baseDir);
            } else {
              return;
            }

            try {

              StringBuffer sql = new StringBuffer();

              sql.append("\n-- Datainterface\n");

              // fetch datainterfaces
              DatainterfaceFactory aimf = new DatainterfaceFactory(dwhrepRock, di);
              Vector ims = aimf.get();

              if (ims != null) {
                Iterator i = ims.iterator();

                while (i.hasNext()) {
                  Datainterface im = (Datainterface) i.next();

                  sql.append("insert into Datainterface(interfacename,interfacetype,status,description,dataformattype) values ('"
                      + im.getInterfacename() + "','" + im.getInterfacetype() + "','" + im.getStatus() + "','"
                      + im.getDescription()+ "','"
                      + im.getDataformattype()+ "')\n");

                }
              }

              sql.append("\n-- InterfaceTechpacks\n");

              // fetch interface techpack
              Interfacetechpacks aimt = new Interfacetechpacks(dwhrepRock);
              aimt.setInterfacename(di.getInterfacename());
              InterfacetechpacksFactory aimtf = new InterfacetechpacksFactory(dwhrepRock, aimt);
              Vector imts = aimtf.get();

              if (imts != null) {
                Iterator i = imts.iterator();

                while (i.hasNext()) {
                  Interfacetechpacks imt = (Interfacetechpacks) i.next();

                  sql.append("insert into InterfaceTechpacks(interfacename,techpackname) values ('"
                      + imt.getInterfacename() + "','" + imt.getTechpackname() + "')\n");

                }
              }

              // fetch all sets

              String versionNumber = null;
              String tpName = null;

              Meta_collections mc = new Meta_collections(rock);
              Meta_collectionsFactory mcF = new Meta_collectionsFactory(rock, mc);
              Iterator iter = mcF.get().iterator();
              while (iter.hasNext()) {

                Meta_collections m = (Meta_collections) iter.next();

                // fetch actions
                Meta_transfer_actions mta = new Meta_transfer_actions(rock);
                mta.setCollection_id(m.getCollection_id());
                mta.setCollection_set_id(m.getCollection_set_id());
                Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);
                Iterator iter2 = mtaF.get().iterator();
                while (iter2.hasNext()) {

                  Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

                  // collect all techpacks that have parser set that have
                  // correct interfaceName.

                  String s = null;
                  Properties p = stringToProperty(a.getAction_contents());
                  if (p != null) {
                    s = (String) p.get("interfaceName");
                  }

                  if (s != null)
                    if (s.equalsIgnoreCase(di.getInterfacename())) {

                      Meta_collection_sets mcs = new Meta_collection_sets(rock);
                      mcs.setCollection_set_id(a.getCollection_set_id());
                      Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(rock, mcs);
                      Meta_collection_sets mcst = mcsF.getElementAt(0);

                      // if more than one TP pick the one with shortest name
                      if (tpName == null || (mcst.getCollection_set_name().length() < tpName.length())) {
                        versionNumber = a.getVersion_number();
                        tpName = mcst.getCollection_set_name();
                      }
                    }
                }
              }

              String dir = System.getProperty("configtool.templatedir", "");

              if (versionNumber != null && tpName != null) {
                String replaseStr = "#version#=" + versionNumber + ",#techpack#=" + tpName;
                ETLCExport des = new ETLCExport(dir, rock.getConnection());
                des.exportXml(replaseStr, baseDir + "/Tech_Pack_" + di.getInterfacename() + ".xml");
              }

              try {

                File f = new File(baseDir + "/Tech_Pack_" + di.getInterfacename() + ".sql");
                BufferedWriter bf = new BufferedWriter(new FileWriter(f));
                bf.write(sql.toString());
                bf.close();

              } catch (Exception e) {

                System.out.println(e);

              }

            } catch (Exception e) {
              System.out.println(e);
            }

            itree.refresh();
          }
        });

        pop.add(ii);

        pop.show(e.getComponent(), e.getX(), e.getY());

      } else { // ROOT node

        JPopupMenu pop = new JPopupMenu("Host");

        JMenuItem ni = new JMenuItem("Create an Interface", ConfigTool.newIcon);
        ni.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            InterfaceWindow ifw = new InterfaceWindow(frame, dwhrepRock, null);
            if (ifw.committed()) {
              itree.refresh();
              right.removeAll();
              InterfaceTab.this.invalidate();
              InterfaceTab.this.validate();
              InterfaceTab.this.repaint();
            }
          }
        });
        pop.add(ni);

        JMenuItem iw = new JMenuItem("Interface wizard", ConfigTool.bulb);
        iw.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            InterfaceWizardDialog iwd = new InterfaceWizardDialog(frame, dwhrepRock, rock, ui);
            itree.refresh();
            ssn.setChange();
          }
        });
        pop.add(iw);

        JMenuItem rf = new JMenuItem("Refresh Tree", ConfigTool.refresh);
        rf.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            if (itree != null) {
              itree.refresh();
              right.removeAll();
              InterfaceTab.this.invalidate();
              InterfaceTab.this.validate();
              InterfaceTab.this.repaint();
            }
          }
        });
        pop.add(rf);

        pop.show(e.getComponent(), e.getX(), e.getY());

      }

    }

  }

  protected Properties stringToProperty(String str) throws Exception {

    Properties prop = new Properties();

    try {

      if (str != null && str.length() > 0) {
        ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
        prop.load(bais);
        bais.close();
      }

    } catch (Exception e) {

    }

    return prop;

  }

}
