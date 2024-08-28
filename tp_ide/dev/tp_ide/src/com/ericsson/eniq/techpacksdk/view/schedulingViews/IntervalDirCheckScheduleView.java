package com.ericsson.eniq.techpacksdk.view.schedulingViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.TimeSelector;

/**
 * @author eromsza
 */
public class IntervalDirCheckScheduleView implements ScheduleView {

    private final JSpinner hours;

    private final JSpinner minutes;

    private final TimeSelector base;

    private final JCheckBox checkIfEmpty = new JCheckBox();

    private final JTextArea dirs;

    boolean checkIfEmptyAsBoolean = true;

    public IntervalDirCheckScheduleView(final JPanel parent, final Meta_schedulings sch) {
        parent.removeAll();

        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 2;

        parent.add(new JLabel("Occures every"), c);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;

        int origHour = 0;
        if (sch != null && sch.getInterval_hour() != null) {
            origHour = sch.getInterval_hour().intValue();
        }
        final SpinnerModel hsp = new SpinnerNumberModel(origHour, 0, 24, 1);
        hours = new JSpinner(hsp);
        parent.add(hours, c);

        c.gridx = 1;
        parent.add(new JLabel("hours"), c);

        c.gridy = 2;
        c.gridx = 0;

        int origMin = 0;
        if (sch != null && sch.getInterval_min() != null) {
            origMin = sch.getInterval_min().intValue();
        }
        final SpinnerModel msp = new SpinnerNumberModel(origMin, 0, 59, 1);
        minutes = new JSpinner(msp);
        parent.add(minutes, c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        parent.add(new JLabel("minutes"), c);

        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        parent.add(new JLabel("Scheduling base time"), c);

        final Calendar cal = new GregorianCalendar();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (sch != null && sch.getScheduling_hour() != null) {
            hour = sch.getScheduling_hour().intValue();
        }

        int min = cal.get(Calendar.MINUTE);
        if (sch != null && sch.getScheduling_min() != null) {
            min = sch.getScheduling_min().intValue();
        }

        base = new TimeSelector(hour, min);
        c.gridy = 4;
        parent.add(base, c);

        // initialise a checkbox as a source of "true"/"false" values in JTextField
        c.gridy = 5;
        // Set the original value
        if (sch != null && sch.getTrigger_command() != null && sch.getTrigger_command().split(";").length == 2) {
            checkIfEmptyAsBoolean = Boolean.valueOf(sch.getTrigger_command().split(";")[0].trim());
        } else {
            checkIfEmptyAsBoolean = true;
        }
        checkIfEmpty.setText("Check if directories are empty");
        checkIfEmpty.setToolTipText("Check if the provided directories below are all empty (ticked) or any of them contains files (unticked).");
        checkIfEmpty.setSelected(checkIfEmptyAsBoolean);
        checkIfEmpty.setFocusable(false);
        parent.add(checkIfEmpty, c);
        // add a listener for manipulating with the ticked and the unticked states
        checkIfEmpty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                final JCheckBox checkBox = (JCheckBox) event.getSource();
                if (checkBox.isSelected()) {
                    checkIfEmptyAsBoolean = true;
                } else {
                    checkIfEmptyAsBoolean = false;
                }
            }
        });

        c.gridy = 6;
        dirs = new LimitedSizeTextArea(1024, true, 3, 30);
        dirs.setLineWrap(true);
        dirs.setName("dirs");
        dirs.setToolTipText("Comma-separated paths of the directories to check. Wildcard '*' is allowed. The set will be triggered in provided intervals.");

        if (sch != null && sch.getTrigger_command() != null && sch.getTrigger_command().split(";").length == 2) {
            dirs.setText(sch.getTrigger_command().split(";")[1].trim());
        }
        parent.add(dirs, c);

        c.gridx = 2;
        c.gridy = 7;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;
        parent.add(Box.createRigidArea(new Dimension(5, 5)), c);

        parent.invalidate();
        parent.revalidate();
        parent.repaint();
    }

    @Override
    public String validate() {
        String error = "";

        final Long hour = new Long(((Integer) hours.getValue()).longValue());
        final Long min = new Long(((Integer) minutes.getValue()).longValue());

        if (hour.intValue() <= 0 && min.intValue() <= 0) {
            error += "Defined interval must be positive\n";
        }

        if (dirs.getText() == null || dirs.getText().isEmpty()) {
            error += "At least one directory must be defined\n";
        }

        if (dirs.getText().contains("*") && (!dirs.getText().contains(File.separator + "*" + File.separator) ||
                !dirs.getText().endsWith(File.separator + "*"))) {
            error += "Wildcard '*' should be used in the format '../*/..' or '../*' to separate directories\n";
        }

        if (dirs.getText().contains("?")) {
            error += "Wildcard '?' is not supported\n";
        }

        return error;
    }

    @Override
    public void fill(final Meta_schedulings sch) {
        sch.setScheduling_hour(new Long(base.getHour()));
        sch.setScheduling_min(new Long(base.getMin()));

        sch.setInterval_hour(new Long(((Integer) hours.getValue()).longValue()));
        sch.setInterval_min(new Long(((Integer) minutes.getValue()).longValue()));

        /* current date */
        final Date curDate = new Date();
        final GregorianCalendar curCal = new GregorianCalendar();
        curCal.setTime(curDate);

        sch.setScheduling_day(new Long(curCal.get(GregorianCalendar.DAY_OF_MONTH)));
        sch.setScheduling_month(new Long(curCal.get(GregorianCalendar.MONTH)));
        sch.setScheduling_year(new Long(curCal.get(GregorianCalendar.YEAR)));

        sch.setStatus(null);
        sch.setLast_execution_time(null);

        // Set the directory
        sch.setTrigger_command(Boolean.valueOf(checkIfEmptyAsBoolean).toString() + ";" + dirs.getText().trim());
    }

}
