package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.gui.setwizard.InterfaceMigrationWizard;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Versioning;



public class InterfaceMigrationWizardDialog {

  private JFrame frame;

  private RockFactory dwhrepRock;

  private JDialog jd;

  private UI ui;

  private Datainterface di;

  private JComboBox from_tp;

  private JComboBox to_tp;

  private JButton generate;

  private JButton cancel;

  public InterfaceMigrationWizardDialog(JFrame frame, RockFactory dwhrepRock, Datainterface di, UI ui) {
    this.frame = frame;
    this.dwhrepRock = dwhrepRock;
    this.di = di;
    this.ui = ui;

    jd = new JDialog(frame, true);

    jd.setTitle("Interface Version Migration");

    Container con = jd.getContentPane();
    con.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridx = 1;
    c.gridy = 1;
    con.add(new JLabel("From TechPack"), c);

    c.gridx = 2;
    from_tp = new TPComboBox(dwhrepRock);
    con.add(from_tp, c);

    c.gridy = 2;
    c.gridx = 1;
    con.add(new JLabel("To TechPack"), c);

    c.gridx = 2;
    to_tp = new TPComboBox(dwhrepRock);
    con.add(to_tp, c);

    c.gridy = 8;
    c.gridx = 3;
    c.weighty = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 9;
    c.gridx = 1;
    c.weighty = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.fill = GridBagConstraints.NONE;
    cancel = new JButton("Discard", ConfigTool.delete);
    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        jd.setVisible(false);
      }
    });
    con.add(cancel, c);

    c.gridx = 2;
    c.anchor = GridBagConstraints.SOUTHWEST;
    generate = new JButton("Migrate", ConfigTool.bulb);
    generate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        WizardWorker ww = new WizardWorker();
        ww.start();
      }
    });
    generate.setEnabled(true);
    con.add(generate, c);

    jd.pack();
    jd.setVisible(true);

  }

  public class WizardWorker extends SwingWorker {

    private Exception error = null;

    public WizardWorker() {
      ui.startOperation("Performing migration...");

      from_tp.setEnabled(false);
      to_tp.setEnabled(false);

      generate.setEnabled(false);
      cancel.setEnabled(false);
    }

    public Object construct() {
      try {

        Versioning from = (Versioning) from_tp.getSelectedItem();
        Versioning to = (Versioning) to_tp.getSelectedItem();

        if (from.equals(to))
          throw new Exception("From and To techpacks are the same");

        InterfaceMigrationWizard imw = new InterfaceMigrationWizard(dwhrepRock, di.getInterfacename(),
            from.getVersionid(), to.getVersionid());

        return imw.migrate();

      } catch (Exception e) {
        e.printStackTrace();
        error = e;
        return null;
      }

    }

    public void finished() {

      if (error != null) {

        ErrorDialog ed = new ErrorDialog(frame, "Migration Error", "Migration failed exceptionally", error);

      } else {

        String m_msg = (String) get();

        JOptionPane.showMessageDialog(frame, "Version migration performed:\n " + m_msg, "Successfull",
            JOptionPane.INFORMATION_MESSAGE);

      }

      jd.setVisible(false);
      ui.endOperation();
    }

  };

}
