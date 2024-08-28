package com.distocraft.dc5000.etl.gui.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.set.SetWindow;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class ScheduleWindow extends JDialog {

  private RockFactory rock;

  private Meta_schedulings sch;

  private boolean commit = false;

  private boolean newSchedule = false;

  private String origName = null;

  private JComboBox set;

  private JTextField name;

  private JComboBox active;

  private JComboBox type;

  private JPanel schviewPanel;

  private ScheduleView schview;

  public ScheduleWindow(JFrame parent, RockFactory rock, Meta_collection_sets tpack, Meta_schedulings psch) {
    super(parent, true);

    this.rock = rock;
    this.sch = psch;

    try {

      if (sch == null) { // new schedule

        sch = new Meta_schedulings(rock);
        sch.setId(getNextScheduleID());
        sch.setCollection_set_id(tpack.getCollection_set_id());
        sch.setVersion_number(tpack.getVersion_number());
        sch.setExecution_type("interval");
        sch.setHold_flag("N");

        newSchedule = true;

        setTitle("New schedule");
      } else {
        setTitle("Schedule " + sch.getId());
      }

      final Container con = getContentPane();
      con.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(2, 4, 2, 2);

      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 1;
      c.gridx = 1;
      c.gridwidth = 2;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      con.add(new JLabel("Schedule"), c);

      c.gridy = 2;
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 3;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Package"), c);

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      JLabel tid = new JLabel(tpack.getCollection_set_name() + " " + tpack.getVersion_number());
      con.add(tid, c);

      // c.gridy = 4 removed

      c.gridx = 1;
      c.gridy = 5;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("ID"), c);

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      JLabel sid = new JLabel(sch.getId().toString());
      con.add(sid, c);

      c.gridx = 1;
      c.gridy = 6;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Name"), c);

      origName = sch.getName();
      
      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      name = new JTextField(sch.getName(), 20);
      con.add(name, c);

      c.gridx = 1;
      c.gridy = 7;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Set"), c);

      Meta_collections wmc = new Meta_collections(rock);
      wmc.setVersion_number(tpack.getVersion_number());
      wmc.setCollection_set_id(tpack.getCollection_set_id());
      Meta_collectionsFactory mcf = new Meta_collectionsFactory(rock, wmc);

      Vector sets = mcf.get();

      Collections.sort(sets, new SetComparator());

      set = new JComboBox(sets);
      set.setRenderer(new ListCellRenderer() {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
          Meta_collections mc = (Meta_collections) value;
          return new JLabel(mc.getCollection_name());
        }
      });
      for (int i = 0; i < sets.size(); i++) {
        Meta_collections mc = (Meta_collections) sets.get(i);
        if (mc.getCollection_id().equals(sch.getCollection_id())) {
          set.setSelectedIndex(i);
          break;
        }
      }
      c.gridx = 2;
      con.add(set, c);

      c.gridx = 1;
      c.gridy = 8;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Active"), c);

      String[] actives = { "true", "false" };
      active = new JComboBox(actives);

      if (sch.getHold_flag().equals("Y"))
        active.setSelectedIndex(1);
      else
        active.setSelectedIndex(0);

      c.gridx = 2;
      con.add(active, c);

      c.gridx = 1;
      c.gridy = 9;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Type"), c);

      c.gridx = 2;
      type = new JComboBox(ConfigTool.SCHEDULE_TYPES);
      type.setSelectedIndex(0);
      for (int i = 0; i < ConfigTool.SCHEDULE_TYPES.length; i++) {
        if (ConfigTool.SCHEDULE_TYPES[i].equals(sch.getExecution_type())) {
          type.setSelectedIndex(i);
          break;
        }
      }
      type.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          String stype = (String) type.getSelectedItem();
          getScheduleView(stype);
          pack();
        }
      });
      con.add(type, c);

      schviewPanel = new JPanel(new GridBagLayout());
      schviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      if (sch.getExecution_type() != null)
        getScheduleView(sch.getExecution_type());

      c.gridx = 1;
      c.gridy = 12;
      c.gridwidth = 2;
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 0;
      c.insets = new Insets(0, 0, 0, 0);
      con.add(schviewPanel, c);

      c.gridy = 13;
      c.gridx = 3;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.insets = new Insets(2, 4, 2, 2);
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 5;
      con.add(Box.createRigidArea(new Dimension(10, 10)), c);

      c.gridy = 14;
      c.gridx = 1;
      JButton discard = new JButton("Discard", ConfigTool.delete);
      discard.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          commit = false;
          setVisible(false);
        }
      });
      con.add(discard, c);

      c.gridx = 2;
      JButton save = new JButton("Save", ConfigTool.check);
      save.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {

          String error = "";

          String xname = name.getText();
          if (xname == null || xname.trim().length() <= 0) {
            error += "Parameter name must be defined\n";
          } else {
            xname = xname.trim();
            for (int i = 0; i < xname.length(); i++) {
              if (Character.isWhitespace(xname.charAt(i))) {
                error += "Parameter name must NOT contain whitespaces\n";
                break;
              }
            }
            
            if(schview != null)
              error += schview.validate();

            if (newSchedule || (!newSchedule && !xname.equals(origName))) {

              try {
                Meta_schedulings where = new Meta_schedulings(ScheduleWindow.this.rock);
                where.setName(xname);
                Meta_schedulingsFactory fac = new Meta_schedulingsFactory(ScheduleWindow.this.rock, where);

                Vector c = fac.get();

                if (c.size() > 0)
                  error += "Name of the schedule must be unique\n";
                c.clear();

              } catch (Exception e) {
                error += "Name validation failed: " + e.getMessage() + "\n";
              }

            }

          }

          if (error.length() > 0) {
            JOptionPane.showMessageDialog(ScheduleWindow.this, error, "Invalid configuration",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
         
          ConfigTool.activateScheduler();
          commit = true;
          setVisible(false);
        }
      });
      con.add(save, c);

      pack();
      setVisible(true);

      if (!commit)
        return;

      sch.setName(name.getText().trim());
      sch.setExecution_type((String) type.getSelectedItem());
      Meta_collections cset = (Meta_collections) set.getSelectedItem();
      sch.setCollection_id(cset.getCollection_id());
      if (active.getSelectedIndex() == 1)
        sch.setHold_flag("Y");
      else
        sch.setHold_flag("N");
      
      // JUST IN CASE
      
      sch.setOs_command(null);
      sch.setScheduling_year(null);
      sch.setScheduling_month(null);
      sch.setScheduling_day(null);
      sch.setScheduling_hour(null);
      sch.setScheduling_min(null);
      sch.setMon_flag(null);
      sch.setTue_flag(null);
      sch.setWed_flag(null);
      sch.setThu_flag(null);
      sch.setFri_flag(null);
      sch.setSat_flag(null);
      sch.setSun_flag(null);
      sch.setStatus(null);
      sch.setLast_execution_time(null);
      sch.setInterval_hour(null);
      sch.setInterval_min(null);
      sch.setPriority(null);
      sch.setTrigger_command(null);
      
      // JUST IN CASE
      
      if(schview != null)
        schview.fill(sch);

      if (!newSchedule) {
        sch.updateDB();
      } else {
        sch.insertDB(false, false);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void getScheduleView(String type) {
    if (type.equals("wait")) {
      schview = new WaitScheduleView(schviewPanel, sch);
    } else if (type.equals("fileExists")) {
      schview = new WaitForFileScheduleView(schviewPanel, sch);
    } else if (type.equals("interval") || type.equals("intervall")) {
      schview = new IntervalScheduleView(schviewPanel, sch);
    } else if (type.equals("weekly")) {
      schview = new WeeklyScheduleView(schviewPanel, sch);
    } else if (type.equals("weeklyinterval")) {
      schview = new WeeklyIntervalScheduleView(schviewPanel, sch);
    } else if (type.equals("monthly")) {
      schview = new MonthlyScheduleView(schviewPanel, sch);
    } else if (type.equals("once")) {
      schview = new OnceScheduleView(schviewPanel, sch);
    } else if (type.equals("onStartup")) {
      schview = new OnstartupScheduleView(schviewPanel, sch);
    } else {
      schview = null;
      schviewPanel.removeAll();
      schviewPanel.invalidate();
      schviewPanel.revalidate();
      schviewPanel.repaint();
    }

  }

  private Long getNextScheduleID() throws Exception {
    Meta_schedulingsFactory fac = new Meta_schedulingsFactory(rock, new Meta_schedulings(rock));
    Vector c = fac.get();
    Enumeration e = c.elements();
    long biggest = -1L;
    while (e.hasMoreElements()) {
      Meta_schedulings s = (Meta_schedulings) e.nextElement();
      if (s.getId().longValue() > biggest)
        biggest = s.getId().longValue();
    }

    return new Long(biggest + 1L);
  }

  public class SetComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      Meta_collections c1 = (Meta_collections) o1;
      Meta_collections c2 = (Meta_collections) o2;

      return c1.getCollection_name().compareToIgnoreCase(c2.getCollection_name());
    }

    public boolean equals(Object obj) {
      return obj == this;
    }

  };

}
