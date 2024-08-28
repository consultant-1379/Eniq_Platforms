package com.distocraft.dc5000.etl.gui.iface;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;


public class DataFormatTable extends JPanel {

  private JFrame parent;
  
  private Datainterface di;

  private RockFactory dwhrepRock;
  
  private DataFormatTableModel dftm;
  
  private JTable table;

  private TableSorter sorter;

  public DataFormatTable(JFrame parent, Datainterface di,RockFactory dwhrepRock) throws Exception {
    super(new GridBagLayout());

    this.parent = parent;
    this.di = di;
    this.dwhrepRock = dwhrepRock;

    recreate();
  }

  private void recreate() throws Exception {

    this.removeAll();

    dftm = new DataFormatTableModel(di,dwhrepRock);
    sorter = new TableSorter(dftm);
    table = new JTable(sorter);
    //table.getTableHeader().addMouseListener(new DataFormatTableMouseListener());
    table.setDefaultRenderer(Integer.class, new DataFormatTableStatusRenderer());
    table.setDefaultRenderer(String.class, new DataFormatTableStringRenderer());
    //table.addMouseListener(new DataFormatTableMouseListener());
    sorter.setTableHeader(table.getTableHeader());

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
  
  public class DataFormatTableMouseListener extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
      int tablerow = table.rowAtPoint(e.getPoint());

      final int row = (tablerow >= 0) ? sorter.modelIndex(tablerow) : -1;

      if (e.getButton() == MouseEvent.BUTTON3) { // PopUp Menu
        JPopupMenu pop = new JPopupMenu("Actions");

        JMenuItem atp = new JMenuItem("Create a DataFormat", ConfigTool.newIcon);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            DataFormatWindow dfw = new DataFormatWindow(parent,dwhrepRock,di,null);
            if(dfw.committed()) {
              try {
                recreate();
              } catch(Exception e) {
                ErrorDialog ed = new ErrorDialog(parent, "Error", "Failed to create table", e);
              }
            }
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

                Interfacemeasurement im = dftm.getMeasurementAt(right_ix);
                
                im.deleteDB();
                
              }
              
              recreate();
              
            } catch (Exception e) {
              ErrorDialog ed = new ErrorDialog(parent, "Error", "Delete failed", e);
            }
          }
        });
        ListSelectionModel lsm = table.getSelectionModel();

        int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();

        inp.setEnabled(minIndex >= 0 && maxIndex >=0);
        pop.add(inp);

        JMenuItem rf = new JMenuItem("Refresh", ConfigTool.refresh);
        rf.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            try {
              DataFormatTable.this.recreate();
            } catch (Exception e) {
              JOptionPane.showMessageDialog(parent, "Error", "Refresh failed: "+e.getMessage(),JOptionPane.ERROR_MESSAGE);
            }
          }
        });
        pop.add(rf);

        pop.show(e.getComponent(), e.getX(), e.getY());

      } else if (e.getClickCount() >= 2) { // Open DataFormat modif window
          int right_ix = sorter.modelIndex(tablerow);
          Interfacemeasurement ifm = dftm.getMeasurementAt(right_ix);
        
          DataFormatWindow dfw = new DataFormatWindow(parent,dwhrepRock,di,ifm);
          if(dfw.committed()) {
            try {
              DataFormatTable.this.recreate();
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(parent, "Error", "Refresh failed: "+ex.getMessage(),JOptionPane.ERROR_MESSAGE);
            }
          }
      }
    }
  };

}
