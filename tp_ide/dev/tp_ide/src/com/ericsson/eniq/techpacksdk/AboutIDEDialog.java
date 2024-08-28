package com.ericsson.eniq.techpacksdk;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import com.ericsson.eniq.techpacksdk.common.Utils;

@SuppressWarnings("serial")
public class AboutIDEDialog extends JDialog {

  public AboutIDEDialog(final SingleFrameApplication application, final ResourceMap resourceMap, final Properties props) {
    super(application.getMainFrame(), true);

    setTitle(resourceMap.getString("AboutIDEDialog.title"));

    final JPanel pane = new JPanel(new GridBagLayout());
    pane.setOpaque(true);

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    pane.add(new JLabel(resourceMap.getString("AboutIDEDialog.heading")), c);

    // 20110216, eanguan, HN65194 :: Added to show the correct current release information of TechPack IDE package.
    c.gridy = 1;
    pane.add(new JLabel(resourceMap.getString("AboutIDEDialog.currentreleaselabel")), c);    
    
    c.gridy = 2;
    pane.add(new JLabel(resourceMap.getString("AboutIDEDialog.copyright")), c);    

    c.gridy = 3;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    final JTextArea ta = new JTextArea(resourceMap.getString("AboutIDEDialog.legal"), 10, 30);
    ta.setEditable(false);
    ta.setLineWrap(true);
    ta.setWrapStyleWord(true);
    pane.add(ta, c);

    c.gridy = 4;
    c.weighty = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTH;
    pane.add(new JButton(application.getContext().getActionMap(this).get("closeaboutdialog")), c);

    setContentPane(pane);

    pack();
    Utils.center(this);
    setVisible(true);

  }

  @Action
  public void closeaboutdialog() {
    this.setVisible(false);
  }

}
