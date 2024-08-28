package com.distocraft.dc5000.etl.gui.schedule;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
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
import com.distocraft.dc5000.etl.gui.SystemStructureListener;
import com.distocraft.dc5000.etl.gui.SystemStructureNotificate;
import com.distocraft.dc5000.etl.gui.Tab;
import com.distocraft.dc5000.etl.gui.TechPackSelector;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class ScheduleTab extends JPanel implements Tab, MouseListener, SystemStructureListener {

  private JFrame frame;

  private SystemStructureNotificate ssn;

  private RockFactory rock;
  private RockFactory dwhrepRock;
  private ScheduleTable table = null;

  private JSplitPane split;

  private TechPackSelector tpSel;

  public ScheduleTab(JFrame frame, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.frame = frame;
    this.ssn = ssn;

    disconnected();
  }

  /**
   * @see com.distocraft.dc5000.etl.gui.Tab#connected(ssc.rockfactory.RockFactory)
   */
  public void connected(RockFactory rock, RockFactory dwhrepRock, String connectionID) {
    this.removeAll();

    this.rock = rock;
    this.dwhrepRock = dwhrepRock;
    ssn.addSystemStructureListener(this);

    tpSel = new TechPackSelector(connectionID, rock);
    ssn.addSystemStructureListener(tpSel);
    tpSel.addTreeSelectionListener(new TechPackSelectorListener(rock));
    tpSel.addMouseListener(this);

    split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tpSel, new JPanel());

    this.removeAll();

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

  public void metaDataChange() {
    refresh();
  }

  public void techPackChange() {
    refresh();
  }

  public void setChange() {
    refresh();
  }
  
  private void refresh() {
    tpSel.refresh();
    split.setRightComponent(new JPanel());
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

  public void mouseReleased(MouseEvent e) {

    if (e.getButton() != MouseEvent.BUTTON3) // not left click -> ignore
      return;

    JTree comp = (JTree) e.getComponent();
    final TreePath tp = comp.getClosestPathForLocation(e.getX(), e.getY());

    DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) tp.getLastPathComponent();

    JPopupMenu pop = new JPopupMenu("Actions");

    if (dtm.getUserObject() instanceof String) { // ROOT and TYPE nodes

      JMenuItem rf = new JMenuItem("Refresh Tree", ConfigTool.refresh);
      rf.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          refresh();
        }
      });
      pop.add(rf);

    } else if (table != null) { // TECHPACK node

      JMenuItem atp = new JMenuItem("Create new Schedule", ConfigTool.newIcon);
      atp.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          ScheduleWindow sw = new ScheduleWindow(frame, rock, table.getTechPack(), null);
          table.refresh();
        }
      });
      pop.add(atp);

      JMenuItem rma = new JMenuItem("Remove all Schedules", ConfigTool.delete);
      rma.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          table.removeAllSchedules();
        }
      });
      pop.add(rma);

    } else {
      return;
    }

    pop.show(e.getComponent(), e.getX(), e.getY());

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
        Meta_collection_sets mcs = (Meta_collection_sets) dtm.getUserObject();

        table = new ScheduleTable(mcs, rock, frame);

        split.setRightComponent(table);

      } else {
        split.setRightComponent(new JPanel());
      }

    }
  };

}
