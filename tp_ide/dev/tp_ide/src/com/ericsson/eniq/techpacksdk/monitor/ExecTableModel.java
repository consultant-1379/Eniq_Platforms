package com.ericsson.eniq.techpacksdk.monitor;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingworker.SwingWorker;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.ericsson.eniq.techpacksdk.datamodel.Engine;

@SuppressWarnings("serial")
public class ExecTableModel extends AbstractTableModel implements WindowListener {

  private final Logger log = Logger.getLogger(ExecTableModel.class.getName());

  private final String[] columnNames;

  private final String[] inputFields;

  public static final int POLL_PERIOD = 3000;

  private final Engine eng;

  private List<String[]> data = new ArrayList<String[]>();

  private boolean visible = false;

  private final int method;

  ExecTableModel(final Application application, final ResourceMap resourceMap, final Engine eng,
      final String[] columnNames, final String[] inputFields, final int method) {
    this.eng = eng;
    this.columnNames = columnNames;
    this.inputFields = inputFields;
    this.method = method;

    final Timer timer = new Timer();

    final TimerTask task = new TimerTask() {

      public void run() {
        (new GetStatus()).execute();
      }
    };

    timer.schedule(task, 0, POLL_PERIOD);
  }

  public Class<?> getColumnClass(final int col) {
    return String.class;
  }

  public String getColumnName(final int col) {
    return columnNames[col].toString();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    return data.size();
  }

  public boolean isCellEditable(final int row, final int col) {
    return false;
  }

  public Object getValueAt(final int row, final int col) {
    final String[] dr = data.get(row);
    return dr[col];
  }

  private void updateVals(final List<Map<String, String>> cur) throws Exception {

    final boolean[] updated = new boolean[data.size()];
    for (int i = 0; i < updated.length; i++) {
      updated[i] = false;
    }

    final Iterator<Map<String, String>> i = cur.iterator();

    while (i.hasNext()) {
      final Map<String, String> map = i.next();

      final String[] newvals = parse(map);

      boolean found = false;
      for (int row = 0; row < data.size(); row++) {
        final String[] vals = data.get(row);
        if (vals[0].equals(newvals[0]) && vals[1].equals(newvals[1])) {
          // Row is already visible
          updated[row] = true;

          if (!vals.equals(newvals)) {
            data.set(row, newvals);
            fireTableRowsUpdated(row, row);
          }

          found = true;
          break;
        }

      }

      if (!found) {
        data.add(newvals);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
      }

    }

    for (int ix = updated.length - 1; ix >= 0; ix--) {
      if (!updated[ix]) {
        data.remove(ix);
        fireTableRowsDeleted(ix, ix);
      }
    }

  }

  private String[] parse(final Map<String, String> arg) {
    final String[] ret = new String[inputFields.length];
    for (int i = 0; i < inputFields.length; i++) {
      
      ret[i] = arg.get(inputFields[i]);
    }
    return ret;
  }

  public class GetStatus extends SwingWorker<List<Map<String, String>>, Void> {

    // In Background thread
    @Override
    protected List<Map<String, String>> doInBackground() throws InterruptedException {
      try {
        if (visible) {
          if (method == 0) {
            return eng.getRunningSets();
          } else if (method == 1) {
            return eng.getQueuedSets();
          }
        }
      } catch (Exception e) {
        log.log(Level.INFO, "Engine connection failed", e);
      }      
      return null;
    }

    // In EDT
    @Override
    protected void done() {
      try {

        final List<Map<String, String>> result = get();

        if (result != null) {
          log.fine("--------" + method + ": " + result.size() + " sets running ---------------");

          final Iterator<Map<String, String>> i = result.iterator();
          while (i.hasNext()) {
            final Map<String, String> set = i.next();
            log.fine(set.get("techpackName") + " " + set.get("setName") + " " + set.get("runningAction") + " "
                + set.get("runningSlot"));

          }

          updateVals(result);
        }

      } catch (Exception e) {
        log.log(Level.WARNING, "Update failed", e);
      }

    }

  }

  public void windowActivated(final WindowEvent e) {

  }

  public void windowDeactivated(final WindowEvent e) {

  }

  public void windowOpened(final WindowEvent e) {
    visible = true;
  }

  public void windowClosing(final WindowEvent e) {
    visible = false;
  }

  public void windowClosed(final WindowEvent e) {
    visible = false;
  }

  public void windowDeiconified(final WindowEvent e) {
    visible = true;
  }

  public void windowIconified(final WindowEvent e) {
    visible = false;
  }

}
