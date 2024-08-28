package com.distocraft.dc5000.etl.gui.schedule;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.TableSorter;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class ScheduleTable extends JScrollPane {

  private Meta_collection_sets techPack;

  private RockFactory rock;

  private JFrame frame;

  private JTable table;

  private ScheduleTableModel stm;

  private TableSorter sorter;

  public ScheduleTable(Meta_collection_sets techPack, RockFactory rock, JFrame frame) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    this.techPack = techPack;
    this.rock = rock;
    this.frame = frame;

    refresh();
  }

  public Meta_collection_sets getTechPack() {
    return techPack;
  }

  public void refresh() {

    try {

      stm = new ScheduleTableModel(techPack, rock);
      sorter = new TableSorter(stm);
      table = new JTable(sorter);
      sorter.setTableHeader(table.getTableHeader());
      table.addMouseListener(new ScheduleTableMouseListener());
      table.getTableHeader().addMouseListener(new ScheduleTableMouseListener());
      table.setDefaultRenderer(String.class, new ScheduleTableRenderer());

      setViewportView(table);

    } catch (Exception e) {
      e.printStackTrace();

      JPanel canvas = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();
      c.weightx = 0;
      c.weighty = 0.5;
      c.fill = GridBagConstraints.VERTICAL;
      c.anchor = GridBagConstraints.NORTHWEST;
      canvas.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridx = 1;
      c.gridy = 1;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      canvas.add(new JLabel(e.getMessage()), c);

      c.gridx = 2;
      c.gridy = 2;
      c.weightx = 0;
      c.weighty = 0.5;
      c.fill = GridBagConstraints.VERTICAL;
      canvas.add(Box.createRigidArea(new Dimension(5, 5)), c);

      setViewportView(canvas);
    }
  }

  void removeAllSchedules() {
    try {
      for (int i = 0; i < stm.getRowCount(); i++) {
        Meta_schedulings ms = stm.getActionAt(i);
        ms.deleteDB();
      }
      refresh();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public class ScheduleTableMouseListener extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
      int tablerow = table.rowAtPoint(e.getPoint());
      final int row = sorter.modelIndex(tablerow);
      if (e.getButton() == MouseEvent.BUTTON3) { // PopUp Menu
        JPopupMenu pop = new JPopupMenu("Actions");

        JMenuItem atp = new JMenuItem("Create new Schedule", ConfigTool.newIcon);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            ScheduleWindow sw = new ScheduleWindow(frame, rock, techPack, null);
            refresh();
          }
        });
        pop.add(atp);

        if (row >= 0) {

          JMenuItem inp = new JMenuItem("Delete this Schedule", ConfigTool.delete);
          inp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
              try {
                Meta_schedulings ms = stm.getActionAt(row);
                ms.deleteDB();
                refresh();
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });
          pop.add(inp);

        }

        pop.show(e.getComponent(), e.getX(), e.getY());

      } else { // No PopUp Menu
        if (e.getClickCount() >= 2) {
          if (row >= 0) {
            ScheduleWindow sw = new ScheduleWindow(frame, rock, techPack, stm.getActionAt(row));
            refresh();
          }
        }
      }
    }

  };

}
