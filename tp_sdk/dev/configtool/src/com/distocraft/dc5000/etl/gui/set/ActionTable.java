package com.distocraft.dc5000.etl.gui.set;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.TableSorter;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class ActionTable extends JPanel {

  private JTable table;

  private TableSorter sorter;

  private Meta_collections set;

  private Meta_collection_sets tpmeta;

  private RockFactory rock;

  private TechPackTree tptree;

  private JFrame frame;

  private ActionTableModel atm;

  ActionTable(Meta_collections set, Meta_collection_sets tpmeta, RockFactory rock, TechPackTree tptree, JFrame frame)
      throws Exception {
    super(new GridBagLayout());

    this.set = set;
    this.tpmeta = tpmeta;
    this.rock = rock;
    this.tptree = tptree;
    this.frame = frame;

    recreate();

  }

  private void recreate() throws Exception {

    this.removeAll();

    atm = new ActionTableModel(set, rock);
    sorter = new TableSorter(atm);
    table = new JTable(sorter);
    sorter.setTableHeader(table.getTableHeader());
    table.getTableHeader().addMouseListener(new ActionTableMouseListener());
    table.addMouseListener(new ActionTableMouseListener());
    table.setDefaultRenderer(Boolean.class, new ActionTableBooleanRenderer());
    table.setDefaultRenderer(Long.class, new ActionTableLongRenderer());
    table.setDefaultRenderer(String.class, new ActionTableStringRenderer());
    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

    JScrollPane scroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;

    sorter.setSortingStatus(0, TableSorter.ASCENDING);

    this.add(scroll, c);

    this.invalidate();
    this.revalidate();
    this.repaint();

  }

  public class ActionTableMouseListener extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
      int tablerow = table.rowAtPoint(e.getPoint());

      final int row = (tablerow >= 0) ? sorter.modelIndex(tablerow) : -1;

      if (e.getButton() == MouseEvent.BUTTON3) { // PopUp Menu
        JPopupMenu pop = new JPopupMenu("Actions");

        JMenuItem atp = new JMenuItem("Create an Action", ConfigTool.newIcon);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            ActionWindow aw = new ActionWindow(frame, set, tpmeta, rock, null, atm.getNextOrderNumber());
            if (aw.committed())
              atm.addRow(aw.getAction());
          }
        });
        pop.add(atp);

        JMenuItem inp = new JMenuItem("Delete selected", ConfigTool.delete);
        inp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            ListSelectionModel lsm = table.getSelectionModel();

            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();

            if (minIndex < 0 || maxIndex < 0)
              return;

            try {

              for (int ix = minIndex; ix <= maxIndex; ix++) {

                int right_ix = sorter.modelIndex(ix);

                Meta_transfer_actions act = atm.getActionAt(right_ix);

                act.deleteDB();

              }

              recreate();
              
            } catch (Exception e) {
              ErrorDialog ed = new ErrorDialog(frame, "Error", "Unable to delete action", e);
            }
          }
        });
        ListSelectionModel lsm = table.getSelectionModel();

        int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();

        inp.setEnabled(minIndex >= 0 && maxIndex >=0);
        pop.add(inp);

        JMenuItem cut = new JMenuItem("Copy Action", ConfigTool.copy);
        cut.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {

            ListSelectionModel lsm = table.getSelectionModel();

            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();

            if (minIndex < 0 || maxIndex < 0)
              return;

            Meta_transfer_actions[] mta = new Meta_transfer_actions[maxIndex - minIndex + 1];

            int i = 0;
            for (int ix = minIndex; ix <= maxIndex; ix++) {

              int right_ix = sorter.modelIndex(ix);

              mta[i++] = atm.getActionAt(right_ix);
            }

            tptree.setActionClipboard(mta);

          }
        });
        cut.setEnabled(row >= 0);
        pop.add(cut);

        JMenuItem pas = new JMenuItem("Paste Action", ConfigTool.paste);
        pas.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            try {

              Meta_transfer_actions[] pasted = tptree.getActionClipboard();

              long pix = atm.getNextOrderNumber().longValue();

              for (int i = 0; i < pasted.length; i++) {

                Meta_transfer_actions actnew = new Meta_transfer_actions(rock);

                actnew.setVersion_number(set.getVersion_number());
                actnew.setTransfer_action_id(getNextActionID(rock));
                actnew.setCollection_id(set.getCollection_id());
                actnew.setCollection_set_id(set.getCollection_set_id());

                actnew.setAction_type(pasted[i].getAction_type());
                actnew.setTransfer_action_name(pasted[i].getTransfer_action_name());
                actnew.setOrder_by_no(new Long(pix++));
                actnew.setDescription(pasted[i].getDescription());
                actnew.setWhere_clause(pasted[i].getWhere_clause());
                actnew.setAction_contents(pasted[i].getAction_contents());
                actnew.setEnabled_flag(pasted[i].getEnabled_flag());
                actnew.setConnection_id(pasted[i].getConnection_id());

                actnew.insertDB(false, false);

              }

              ActionTable.this.recreate();

            } catch (Exception e) {
              ErrorDialog ed = new ErrorDialog(frame, "Error", "Unable to paste action", e);
            }
          }
        });
        pas.setEnabled(tptree.getActionClipboard() != null);
        pop.add(pas);

        JMenuItem rf = new JMenuItem("Refresh", ConfigTool.refresh);
        rf.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            try {
              ActionTable.this.recreate();
            } catch (Exception e) {
              ErrorDialog ed = new ErrorDialog(frame, "Error", "Refresh failed", e);
            }
          }
        });
        pop.add(rf);

        pop.show(e.getComponent(), e.getX(), e.getY());

      } else { // No PopUp Menu
        if (e.getClickCount() >= 2) {
          Meta_transfer_actions action = atm.getActionAt(row);
          ActionWindow aw = new ActionWindow(frame, set, tpmeta, rock, action, new Long(-1));
          atm.fireTableRowsUpdated(row, row);
        }
      }
    }
  };

  private Long getNextActionID(RockFactory rock) throws Exception {

    Meta_transfer_actions whre = new Meta_transfer_actions(rock);
    Meta_transfer_actionsFactory afact = new Meta_transfer_actionsFactory(rock, whre);

    Enumeration e = afact.get().elements();
    Long biggest = new Long(-1L);
    while (e.hasMoreElements()) {
      Meta_transfer_actions mta = (Meta_transfer_actions) e.nextElement();
      Long cid = mta.getTransfer_action_id();
      if (cid.longValue() > biggest.longValue())
        biggest = cid;
    }

    return new Long(biggest.longValue() + 1);
  }

}
