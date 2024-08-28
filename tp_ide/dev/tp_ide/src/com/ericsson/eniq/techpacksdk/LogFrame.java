package com.ericsson.eniq.techpacksdk;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.ericsson.eniq.component.ExceptionHandler;

@SuppressWarnings("serial")
public class LogFrame extends JFrame {

  private static final Logger logger = Logger.getLogger(LogFrame.class.getName());

  private static LogFrame instance = null;

  private final Application application;

  final private JTextPane area;

  final private StyledDocument doc;

  final private JComboBox loglevel;

  final private JButton hide;

  private final Style debug;

  private final Style severe;

  private final Style warning;

  private final Style info;
  
  final LogManager logManager;
  
  final Logger idelogger;
  
  final Logger setWizlogger;

  LogFrame(final Application application, final ResourceMap resourceMap) {

    this.application = application;

    setTitle(resourceMap.getString("LogFrame.title"));
    ImageIcon list = resourceMap.getImageIcon("Log.icon");
    setIconImage(list.getImage());

    setLayout(new GridBagLayout());

    Level l = Level.INFO;
    try {
      l = Level.parse(System.getProperty("TPIDE.LogLevel"));
    } catch (Exception e) {
    }

    logManager = LogManager.getLogManager();
    idelogger = logManager.getLogger("com.ericsson.eniq.techpacksdk");
    setWizlogger = logManager.getLogger("com.ericsson.eniq.common.setWizards");
    
    System.out.println("setWizlogger" + setWizlogger);
    
    if (idelogger != null) {
      idelogger.setLevel(l);
      setWizlogger.setLevel(l);
    }

    final Level[] levels = new Level[8];

    levels[7] = Level.SEVERE;
    levels[6] = Level.WARNING;
    levels[5] = Level.INFO;
    levels[4] = Level.CONFIG;
    levels[3] = Level.FINE;
    levels[2] = Level.FINER;
    levels[1] = Level.FINEST;
    levels[0] = Level.OFF;

    loglevel = new JComboBox(levels);
    loglevel.setSelectedItem(l);
    loglevel.setAction(getAction("changeLogLevel"));
    loglevel.setRenderer(new LevelRenderer());

    area = new JTextPane();
    area.setEditable(false);
    area.setOpaque(true);
    area.setBackground(Color.BLACK);

    doc = area.getStyledDocument();

    final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

    debug = doc.addStyle("debug", def);
    StyleConstants.setForeground(debug, Color.WHITE);

    severe = doc.addStyle("severe", debug);
    StyleConstants.setForeground(severe, Color.RED);
    StyleConstants.setBold(severe, true);

    warning = doc.addStyle("warning", debug);
    StyleConstants.setForeground(warning, Color.YELLOW);

    info = doc.addStyle("info", debug);
    StyleConstants.setForeground(info, Color.GREEN);

    final JScrollPane scp = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scp.setPreferredSize(new Dimension(700, 550));

    hide = new JButton(getAction("hidelog"));

    // ----- Layout ------

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    add(new JLabel(resourceMap.getString("LogFrame.level")), c);

    c.gridx = 1;

    add(loglevel, c);

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 3;
    c.weighty = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;

    add(scp, c);

    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHEAST;

    add(hide, c);

    if (idelogger == null) {
      loglevel.setSelectedIndex(-1);
      loglevel.setEnabled(false);
      area.setText(resourceMap.getString("LogFrame.disabled"));
    }

    pack();

    instance = this;

  }

  public void addMessage(final String msg, final Level level) {

    try {

      Style style = debug;

      if (level.equals(Level.SEVERE)) {
        style = severe;
      } else if (level.equals(Level.WARNING)) {
        style = warning;
      } else if (level.equals(Level.INFO)) {
        style = info;
      }

      doc.insertString(0, msg, style);

      area.setCaretPosition(0);

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

  }

  public static LogFrame getInstance() {
    return instance;
  }

  @Action
  public void hidelog() {
    setVisible(false);
  }

  @Action
  public void changeLogLevel() {
        
    if (idelogger != null && setWizlogger != null) {
      if (loglevel.getSelectedIndex() > -1) {
        final Level oldlevel = idelogger.getLevel();
        final Level newlevel = (Level) loglevel.getSelectedItem();
        if (!oldlevel.equals(newlevel)) {
          idelogger.setLevel(newlevel);
          setWizlogger.setLevel(newlevel);
          logger.severe("Logging level " + oldlevel + " changed to " + newlevel);
        }
      }
    }
  }

  /**
   * Helper function, returns action by name
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
  }

  private final class LevelRenderer extends JLabel implements ListCellRenderer {

    public LevelRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

      final Level lvl = (Level) value;

      setText(lvl.getName());

      Color background;
      Color foreground;

      JList.DropLocation dropLocation = list.getDropLocation();
      if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {

        background = Color.BLUE;
        foreground = Color.WHITE;

      } else if (isSelected) {
        background = Color.RED;
        foreground = Color.WHITE;

      } else {
        background = Color.WHITE;
        foreground = Color.BLACK;
      }

      setBackground(background);
      setForeground(foreground);

      return this;
    }
  }

}
