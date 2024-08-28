package com.ericsson.eniq.techpacksdk;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

@SuppressWarnings("serial")
public class NewServerDialog extends JDialog {

  private final LimitedSizeTextField server;

  private final JButton cancel;

  private final JButton ok;
  
  private final ResourceMap resourceMap;

  public NewServerDialog(final SingleFrameApplication application, final ResourceMap resourceMap) {
    super(application.getMainFrame(), true);

    this.resourceMap = resourceMap;
    
    setTitle(resourceMap.getString("NewServerDialog.title"));

    final JPanel pane = new JPanel(new GridBagLayout());
    pane.setOpaque(true);

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    pane.add(new JLabel(resourceMap.getString("NewServerDialog.hostname")), c);

    c.gridx = 1;
    server = new LimitedSizeTextField(LimitedSizeTextField.SERVERNAMECHARS_STRING, "", 15, 500, true);
    server.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        if (server.getText().length() > 0) {
          final javax.swing.Action a = application.getContext().getActionMap(NewServerDialog.this).get("oknewserverdialog");
          a.actionPerformed(ae);
        }
      }
    });
    pane.add(server, c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    final JPanel bPanel = new JPanel(new GridBagLayout());
    pane.add(bPanel, c);

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.weightx = 0;
    cancel = new JButton(application.getContext().getActionMap(this).get("cancelnewserverdialog"));
    bPanel.add(cancel, c);

    c.gridx = 1;
    ok = new JButton(application.getContext().getActionMap(this).get("oknewserverdialog"));
    ok.setEnabled(false);
    bPanel.add(ok, c);

    setContentPane(pane);

    server.getDocument().addDocumentListener(new ButtonEnabler());

    pack();
    setVisible(true);

  }

  @Action
  public void cancelnewserverdialog() {
    server.setText("");
    setVisible(false);
  }

  @Action
  public void oknewserverdialog() {
    if(server.getText().indexOf(".") <= 0) {
      if(JOptionPane.showConfirmDialog(NewServerDialog.this, resourceMap.getString("NewServerDialog.confirm.message"), resourceMap.getString("NewServerDialog.confirm.title"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
        return;
      }  
    }
    setVisible(false);
  }

  public String getServerName() {
    return server.getText().trim();
  }

  public class ButtonEnabler implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      ok.setEnabled(server.getText().length() > 0);
    }

    public void insertUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

  };

}
