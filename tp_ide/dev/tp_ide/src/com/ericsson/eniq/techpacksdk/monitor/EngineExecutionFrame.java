package com.ericsson.eniq.techpacksdk.monitor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.ericsson.eniq.techpacksdk.datamodel.Engine;

public class EngineExecutionFrame extends JFrame {

  private final Application application;

  private final ResourceMap resourceMap;

  private final Engine eng;

  private final ExecTableModel inexec;
  
  private final ExecTableModel inque;

  private final JButton hide;

  public EngineExecutionFrame(final Application application, final ResourceMap resourceMap, final Engine eng) {
    this.application = application;
    this.resourceMap = resourceMap;
    this.eng = eng;

    setTitle(resourceMap.getString("EngineExecution.title"));
    ImageIcon list = resourceMap.getImageIcon("EngineExecution.icon");
    setIconImage(list.getImage());

    setLayout(new GridBagLayout());

    final String[] ecols = { "Techpack", "Set", "Action", "Started", "Slot" };
    final String[] efields = { "techpackName", "setName", "runningAction", "startTime", "runningSlot" };

    inexec = new ExecTableModel(application, resourceMap, eng, ecols, efields, 0);
    final JTable inexectable = new JTable(inexec);
    inexectable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    inexectable.setDefaultRenderer(String.class, new ExecCellRenderer());
    final JScrollPane inexecscroll = new JScrollPane(inexectable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    final String[] qcols = { "Techpack", "Set", "Created", "Priority" };
    final String[] qfields = { "techpackName", "setName", "creationDate", "priority" };

    inque = new ExecTableModel(application, resourceMap, eng, qcols, qfields, 1);
    final JTable inquetable = new JTable(inque);
    inquetable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    inquetable.setDefaultRenderer(String.class, new ExecCellRenderer());
    final JScrollPane inquescroll = new JScrollPane(inquetable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    hide = new JButton(application.getContext().getActionMap(this).get("hideenginemonitor"));

    addWindowListener(inexec);
    addWindowListener(inque);

    // ----- Layout ------

    final GridBagConstraints c = new GridBagConstraints();

    c.insets = new Insets(2, 2, 2, 2);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 1;
    c.weighty = 0;

    add(new JLabel(resourceMap.getString("EngineExecution.executing.title")), c);

    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1;
    c.gridy = 1;

    add(inexecscroll, c);

    c.fill = GridBagConstraints.NONE;
    c.weighty = 0;
    c.gridy = 2;

    add(new JLabel(resourceMap.getString("EngineExecution.queueing.title")),c);

    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1;
    c.gridy = 3;

    add(inquescroll, c);

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weighty = 0;
    c.gridy = 4;

    add(hide, c);

    pack();

  }

  @Action
  public void hideenginemonitor() {
    setVisible(false);
    inexec.windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  @Action
  public void showenginemonitor() {
    setVisible(true);
    inexec.windowOpened(new WindowEvent(this, WindowEvent.WINDOW_OPENED));
  }

  public class ExecCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
        final boolean hasFocus, final int row, final int column) {
      final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      return c;
    }

  };

}
