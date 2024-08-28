package com.ericsson.eniq.techpacksdk;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.application.ResourceMap;

@SuppressWarnings("serial")
public class StatusBar extends JPanel {

  private JLabel message;

  StatusBar(final String serverName, final User user, final ResourceMap resourceMap) {
    this.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.weighty = 0;
    c.insets = new Insets(1, 1, 1, 1);

    final JLabel server = new JLabel(serverName, resourceMap.getIcon("DataConnection.icon"), SwingConstants.LEFT);
    server.setBorder(BorderFactory.createLoweredBevelBorder());
    add(server, c);

    c.gridx = 1;
    Icon ic = null;
    if (user.getRole().equals(User.ADMIN)) {
      ic = resourceMap.getImageIcon("Admin.icon");
    } else if (user.getRole().equals(User.USER)) {
      ic = resourceMap.getImageIcon("User.icon");
    } else if (user.getRole().equals(User.RND)) {
      ic = resourceMap.getImageIcon("RnDUser.icon");
    } else {
      ic = resourceMap.getImageIcon("QuestionMark.icon");
    }

    final JLabel userlab = new JLabel(user.getName(), ic, SwingConstants.LEFT);
    userlab.setBorder(BorderFactory.createLoweredBevelBorder());
    add(userlab, c);

    c.gridx = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;

    message = new JLabel("");
    message.setBorder(BorderFactory.createLoweredBevelBorder());

    add(message, c);
  }

  public String getMessage() {
    return this.message.getText();
  }

  public void setMessage(String string) {
    this.message.setText(string);
  }

}
