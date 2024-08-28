package com.distocraft.dc5000.etl.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
class StatusBar extends JPanel {

  private JLabel message;
  private JLabel host;
  private JProgressBar progress;

  private StatusBarThread thread = null;

  StatusBar() {
    // this.setBorder(BorderFactory.createRaisedBevelBorder());

    this.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    message = new JLabel("");
    message.setBorder(BorderFactory.createLoweredBevelBorder());

    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 1;
    c.ipadx = 2;
    c.ipady = 1;
    c.insets = new Insets(1, 1, 1, 1);

    this.add(message, c);

    host = new JLabel(ConfigTool.unplug);
    host.setBorder(BorderFactory.createLoweredBevelBorder());

    c.weightx = 0;
    c.gridx = 2;
    c.insets = new Insets(1, 1, 1, 1);

    this.add(host, c);

    c.gridx = 3;
    c.fill = GridBagConstraints.VERTICAL;
    c.weighty = 1;
    c.ipadx = 0;
    c.ipady = 0;
    c.insets = new Insets(1, 1, 1, 1);

    progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
    progress.setBorder(BorderFactory.createLoweredBevelBorder());

    this.add(progress, c);
    
    this.thread = new StatusBarThread();
    this.thread.start();

  }

  public void startOperation(String msg) {
    message.setText(msg);
    thread.live = true;
    synchronized (thread) {
      thread.notifyAll();
    }
  }

  public void endOperation() {
    message.setText("");
    thread.live = false;
  }

  public void setHostName(String name) {
    if (name != null) {
      host.setText(name);
      host.setIcon(ConfigTool.dataConnection);
    } else {
      host.setText("");
      host.setIcon(ConfigTool.unplug);
    }
  }

  public class StatusBarThread extends Thread {

    public boolean live = false;
    public int progInt = 0;

    public void run() {
      while (true) {
        
        try {

          while (live) {

            progInt += 2;

            if (progInt >= 100)
              progInt = 0;

            javax.swing.SwingUtilities.invokeLater(new Runnable() {

              public void run() {
                progress.setValue(progInt);
              }
            });
            
            Thread.sleep(1000);

          } // progress thread
          
          progInt = 0;
          
          javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
              progress.setValue(progInt);
            }
          });
          
          synchronized(thread) {
            thread.wait();
          }
          
        } catch (Exception e) {}

      }
    }

  };

}
