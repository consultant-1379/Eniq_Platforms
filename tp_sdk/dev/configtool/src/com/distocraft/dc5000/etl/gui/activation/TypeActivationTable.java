package com.distocraft.dc5000.etl.gui.activation;

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
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;

/**
 * Copyright Distocraft 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class TypeActivationTable extends JScrollPane {

  private Tpactivation tpactivation;

  private RockFactory dwhrepRockFactory;

  private JFrame frame;

  private JTable table;

  private TypeActivationTableModel typeActivationTableModel;

  private TableSorter sorter;

  public TypeActivationTable(Tpactivation tpactivation, RockFactory dwhrepRockFactory, JFrame frame) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    this.tpactivation = tpactivation;
    this.dwhrepRockFactory = dwhrepRockFactory;
    this.frame = frame;

    refresh();
  }

  public Tpactivation getTpactivation() {
    return this.tpactivation;
  }

  public void refresh() {

    try {

      this.typeActivationTableModel = new TypeActivationTableModel(this.tpactivation, this.dwhrepRockFactory);
      sorter = new TableSorter(this.typeActivationTableModel);
      table = new JTable(sorter);
      sorter.setTableHeader(table.getTableHeader());
      table.addMouseListener(new TypeActivationTableMouseListener());
      table.getTableHeader().addMouseListener(new TypeActivationTableMouseListener());
      table.setDefaultRenderer(String.class, new TypeActivationTableRenderer());

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

  public class TypeActivationTableMouseListener extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
      int tablerow = table.rowAtPoint(e.getPoint());
      final int row = sorter.modelIndex(tablerow);

      if (e.getButton() == MouseEvent.BUTTON3) { // PopUp Menu
        JPopupMenu pop = new JPopupMenu("Actions");

        JMenuItem atp = new JMenuItem("Edit", ConfigTool.draw);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            TypeActivationWindow typeActivationWindow = new TypeActivationWindow(frame,
                TypeActivationTable.this.dwhrepRockFactory, TypeActivationTable.this.typeActivationTableModel
                    .getTypeActivationAt(row));
            refresh();
          }
        });
        pop.add(atp);
        pop.show(e.getComponent(), e.getX(), e.getY());

      } else { // No PopUp Menu

        if (e.getClickCount() >= 2) {
          if (row >= 0) {
            TypeActivationWindow typeActivationWindow = new TypeActivationWindow(frame,
                TypeActivationTable.this.dwhrepRockFactory, TypeActivationTable.this.typeActivationTableModel
                    .getTypeActivationAt(row));
            refresh();
          }
        }
      }
    }

  };

}
