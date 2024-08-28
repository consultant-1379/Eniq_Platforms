package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.*;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class EventLoadActionView extends PartitionedLoadActionView {

    private static final String USE_NAMED_PIPE = "useNamedPipe";
    private static final String USE_SNAPPY = "useSnappy";

    private static final String IS_PARALLEL_LOAD_ALLOWED = "isParallelLoadAllowed";

    private static final String TOOLTIP_TEXT = "Version of directory created inside raw directory for storing ETL data files.";
    private final JTextField versionDir;

    private static final String TOOLTIP_FILE_DUP_TEXT = "Decides if duplicate check is carried out on loaded files";
    private transient final JCheckBox fileDuplicateCheck;

    private static final String TOOLTIP_NAMED_PIPE_TEXT = "Decides if named pipe is used for loading";
    private static final String TOOLTIP_SNAPPY_TEXT = "Decides if snappy compression is used for loading with the named pipe";

    private transient final JCheckBox useNamedPipe;
    private transient final JCheckBox useSnappy;

    private static final String TOOLTIP_PARALLEL_LOAD_TEXT = "Decides if parallel loading is allowed for this measurement type";
    private transient final JCheckBox isParallelLoadAllowed;

    public EventLoadActionView(final JPanel parent, final Meta_transfer_actions action) {
        super(parent, action);

        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 5;

        versionDir = new JTextField(20);

        final JLabel versionDirLabel = new JLabel("Directory Version");
        versionDirLabel.setToolTipText(TOOLTIP_TEXT);
        parent.add(versionDirLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy++;
        versionDir.setToolTipText(TOOLTIP_TEXT);
        parent.add(versionDir, c);

        fileDuplicateCheck = new JCheckBox("Load File Duplicate Check");
        fileDuplicateCheck.setToolTipText(TOOLTIP_FILE_DUP_TEXT);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy++;

        c.weightx = 1;
        c.gridx = 1;

        parent.add(fileDuplicateCheck, c);

        useSnappy = new JCheckBox("Use Snappy");
        useSnappy.setToolTipText(TOOLTIP_SNAPPY_TEXT);
        useSnappy.addActionListener(new MyActionListener());
        c.weightx = 1;
        c.gridx = 1;
        c.gridy++;

        parent.add(useSnappy, c);

        useNamedPipe = new JCheckBox("Use Named Pipe");
        useNamedPipe.setToolTipText(TOOLTIP_NAMED_PIPE_TEXT);
        useNamedPipe.addActionListener(new MyActionListener());
        c.weightx = 1;
        c.gridx = 1;
        c.gridy++;

        parent.add(useNamedPipe, c);

        isParallelLoadAllowed = new JCheckBox("Allow Parallel Loading");
        isParallelLoadAllowed.setToolTipText(TOOLTIP_PARALLEL_LOAD_TEXT);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy++;

        parent.add(isParallelLoadAllowed, c);

        if (action != null) {
            final Properties props = stringToProperty(action.getWhere_clause());
            versionDir.setText(props.getProperty("versiondir", ""));

            fileDuplicateCheck.setSelected(Boolean.parseBoolean((props.getProperty("fileDuplicateCheck", "false"))));

            useNamedPipe.setSelected(Boolean.parseBoolean((props.getProperty(USE_NAMED_PIPE, "false"))));
            useSnappy.setSelected(Boolean.parseBoolean((props.getProperty(USE_SNAPPY, "false"))));

            isParallelLoadAllowed.setSelected(Boolean.parseBoolean((props.getProperty(IS_PARALLEL_LOAD_ALLOWED, "false"))));
        }

        parent.invalidate();
        parent.revalidate();
        parent.repaint();
    }

    @Override
    public String getType() {
        return Constants.EVENT_LOADER;
    }

    @Override
    protected Properties getProperties() {
        final Properties p = super.getProperties();
        p.put("versiondir", versionDir.getText().trim());
        p.put("fileDuplicateCheck", String.valueOf(fileDuplicateCheck.isSelected()));

        p.put(USE_NAMED_PIPE, String.valueOf(useNamedPipe.isSelected()));
        p.put(IS_PARALLEL_LOAD_ALLOWED, String.valueOf(isParallelLoadAllowed.isSelected()));
        p.put(USE_SNAPPY, String.valueOf(useSnappy.isSelected()));

        return p;
    }

    @Override
    public String validate() {
        String error = super.validate();

        if (versionDir.getText().trim().length() <= 0) {
            error += "Parameter Directory Version must be defined.\n";
        } else {
            try {
                Integer.parseInt(versionDir.getText().trim());
            } catch (NumberFormatException e) {
                error += "Parameter Directory Version: Invalid Integer.\n";
            }
        }

        return error;
    }

    private class MyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (useNamedPipe.isSelected()) {
                useSnappy.setEnabled(true);
            } else {
                useSnappy.setSelected(false);
                useSnappy.setEnabled(false);
            }

        }
    }
}
