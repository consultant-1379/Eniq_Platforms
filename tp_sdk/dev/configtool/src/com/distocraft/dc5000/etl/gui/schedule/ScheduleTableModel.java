package com.distocraft.dc5000.etl.gui.schedule;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class ScheduleTableModel extends AbstractTableModel {

  private static final String[] columns = { "Name", "Type", "Set", "Description" };

  private Vector schedules = new Vector(10);

  private Vector setNames = new Vector(10);

  private Meta_collection_sets tp;

  private RockFactory rock;

  ScheduleTableModel(Meta_collection_sets tp, RockFactory rock) throws Exception {
    this.tp = tp;
    this.rock = rock;

    Meta_schedulings filt = new Meta_schedulings(rock);
    filt.setCollection_set_id(tp.getCollection_set_id());
    filt.setVersion_number(tp.getVersion_number());
    Meta_schedulingsFactory fact = new Meta_schedulingsFactory(rock, filt);

    Vector actionsVector = fact.get();
    Enumeration e = actionsVector.elements();

    while (e.hasMoreElements()) {
      Meta_schedulings sch = (Meta_schedulings) e.nextElement();
      Meta_collections mc = new Meta_collections(rock, sch.getCollection_id(), sch.getVersion_number(), sch
          .getCollection_set_id());

      addRow(sch, mc.getCollection_name());
    }

  }

  public Meta_schedulings getActionAt(int row) {
    return (Meta_schedulings) schedules.get(row);
  }

  public String getColumnName(int col) {
    return columns[col];
  }

  public Class getColumnClass(int col) {
    return String.class;
  }

  public Object getValueAt(int row, int col) {
    Meta_schedulings sch = (Meta_schedulings) schedules.get(row);

    if (col == 0)
      return sch.getName();
    else if (col == 1)
      return sch.getExecution_type();
    else if (col == 2) {
      String sname = (String) setNames.get(row);
      return sname;
    } else if (col == 3) { // Description
      String typ = sch.getExecution_type();
      if (typ.equals("interval") || typ.equals("intervall")) {
        return "Occurs once every " + sch.getInterval_hour() + " hours " + sch.getInterval_min() + " minutes";
      } else if (typ.equals("wait")) {
        return "Waiting trigger";
      } else if (typ.equals("fileExists")) {
        return "Waiting file " + sch.getTrigger_command();
      } else if (typ.equals("weekly")) {
        StringBuffer sb = new StringBuffer("");
        if ("Y".equals(sch.getMon_flag()))
          sb.append("Mon");
        if ("Y".equals(sch.getTue_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Tue");
        }
        if ("Y".equals(sch.getWed_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Wed");
        }
        if ("Y".equals(sch.getThu_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Thu");
        }
        if ("Y".equals(sch.getFri_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Fri");
        }
        if ("Y".equals(sch.getSat_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Sat");
        }
        if ("Y".equals(sch.getSun_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Sun");
        }
        sb.insert(0, "Every ");
        sb.append(" at ");
        if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_hour()).append(":");
        if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_min());

        return sb.toString();
      } else if (typ.equals("monthly")) {
        int day = sch.getScheduling_day().intValue();
        if (day <= 0) {
          StringBuffer sb = new StringBuffer("Occures last day of month at ");
          if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_hour()).append(":");
          if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_min());
          return sb.toString();
        } else {
          StringBuffer sb = new StringBuffer("Occures ");
          sb.append(sch.getScheduling_day()).append(". day of month at ");
          if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_hour()).append(":");
          if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_min());

          return sb.toString();
        }

      } else if (typ.equals("once")) {
        StringBuffer sb = new StringBuffer("Occures ");
        sb.append(sch.getScheduling_day()).append(".").append(sch.getScheduling_month().intValue()+1);
        sb.append(".").append(sch.getScheduling_year()).append(" at ");
        if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_hour()).append(":");
        if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_min());

        return sb.toString();
      } else if (typ.equals("weeklyinterval")) {
        StringBuffer sb = new StringBuffer("Occures every ");

        if ("Y".equals(sch.getMon_flag()))
          sb.append("Mon");
        if ("Y".equals(sch.getTue_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Tue");
        }
        if ("Y".equals(sch.getWed_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Wed");
        }
        if ("Y".equals(sch.getThu_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Thu");
        }
        if ("Y".equals(sch.getFri_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Fri");
        }
        if ("Y".equals(sch.getSat_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Sat");
        }
        if ("Y".equals(sch.getSun_flag())) {
          if (sb.length() > 0)
            sb.append(",");
          sb.append("Sun");
        }

        // Drop the comma, if it is the first character before the weekdays in info string.
        if (sb.charAt(14) == ',') {
          sb.deleteCharAt(14);
        }

        // Drop the comma, if it is the last character in info string.
        if (sb.charAt(sb.length() - 1) == ',') {
          sb = new StringBuffer(sb.substring(0, sb.length() - 1));
        }

        // Get/parse the interval values from database to these variables from OSCommand.
        String serializedIntervalString = sch.getOs_command();

        Properties intervalProps = new Properties();

        if (serializedIntervalString != null && serializedIntervalString.length() > 0) {

          try {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedIntervalString.getBytes());
            intervalProps.load(bais);
            bais.close();
            Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.ScheduleTableModel").log(Level.FINEST,
                "Interval Properties-object read in view");
          } catch (Exception e) {
            Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.ScheduleTableModel").log(Level.FINEST,
                "Interval Properties-object error in view");
          }
        }

        Integer intervalStartHour = new Integer(intervalProps.getProperty("intervalStartHour"));
        Integer intervalStartMinute = new Integer(intervalProps.getProperty("intervalStartMinute"));
        Integer intervalEndHour = new Integer(intervalProps.getProperty("intervalEndHour"));
        Integer intervalEndMinute = new Integer(intervalProps.getProperty("intervalEndMinute"));

        String intervalStartHourString = new String(intervalStartHour.toString());
        String intervalStartMinuteString = new String(intervalStartMinute.toString());
        String intervalEndHourString = new String(intervalEndHour.toString());
        String intervalEndMinuteString = new String(intervalEndMinute.toString());

        if (intervalStartHour.intValue() < 10 && intervalStartHour.intValue() >= 0) {
          intervalStartHourString = new String("0" + intervalStartHour.toString());
        }
        if (intervalStartMinute.intValue() < 10 && intervalStartMinute.intValue() >= 0) {
          intervalStartMinuteString = new String("0" + intervalStartMinute.toString());
        }
        if (intervalEndHour.intValue() < 10 && intervalEndHour.intValue() >= 0) {
          intervalEndHourString = new String("0" + intervalEndHour.toString());
        }
        if (intervalEndMinute.intValue() < 10 && intervalEndMinute.intValue() >= 0) {
          intervalEndMinuteString = new String("0" + intervalEndMinute.toString());
        }

        sb.append(" from ");
        sb.append(intervalStartHourString + ":" + intervalStartMinuteString);

        sb.append(" to ");
        sb.append(intervalEndHourString + ":" + intervalEndMinuteString);

        sb.append(" every " + sch.getInterval_hour() + " hours and " + sch.getInterval_min() + " minutes");
        return sb.toString();
        
      } else if (typ.equals("onStartup")) {
        return "Executed on ETLC startup";
      } else {
        return "unknown schedule type";
      }
    } else
      return "undefined";
  }

  public int getColumnCount() {
    return columns.length;
  }

  public int getRowCount() {
    return schedules.size();
  }

  int addRow(Meta_schedulings sch, String setName) {

    if (sch == null)
      return 0;
    schedules.add(sch);
    setNames.add(setName);
    fireTableRowsInserted(schedules.size(), schedules.size());
    return schedules.size() - 1;
  }

  public Long getNextActionID() {
    Enumeration e = schedules.elements();
    Long biggest = new Long(-1L);
    while (e.hasMoreElements()) {
      Meta_transfer_actions mta = (Meta_transfer_actions) e.nextElement();

      if (mta.getTransfer_action_id().longValue() > biggest.longValue()) {
        biggest = mta.getTransfer_action_id();
      }
    }

    return new Long(biggest.longValue() + 1L);
  }

}
