package com.ericsson.eniq.techpacksdk;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Useraccount;
import com.distocraft.dc5000.repository.dwhrep.UseraccountFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;


@SuppressWarnings("serial")
public class UserAdminPanel extends JPanel {

  private static final Logger logger = Logger.getLogger(UserAdminPanel.class.getName());

  private final TechPackIDE application;

  private final ResourceMap resourceMap;

  private final RockFactory rock;

  private final JTable utable;

  private final UserTableModel utm;

  private final ArrayList<Useraccount> tobedeleted = new ArrayList<Useraccount>();

  private final JButton savebutton;

  private final JButton discardbutton;

  private final JButton removeuserbutton;

  private final JButton adduserbutton;
  
  //20110311, eanguan, LoginInUserAdminWindow IMP :: Varible for Login JButton
  private final JButton goToLoginButton ;
  
  //20110311, eanguan, LoginInUserAdminWindow IMP :: Varible to store selected user name
  //in table of UserAdminWindow
  private String uNameSelected ;
  

  // Please dont remove this. This constructor is useful for Unit Testing.
  protected UserAdminPanel(final RockFactory rock){
	  application = null;
	  resourceMap = null;
	  this.rock = rock;
	  utm = null;
	  utable = null;
	  savebutton = null;
	  discardbutton= null;
	  removeuserbutton = null;
	  adduserbutton = null;
	  goToLoginButton = null;	  
  }
  
  UserAdminPanel(final TechPackIDE application, final ResourceMap resourceMap, final RockFactory rock, final User user) throws Exception {

    this.application = application;
    this.resourceMap = resourceMap;
    this.rock = rock;

    logger.info("constructed");
        
    setLayout(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.gridwidth = 5;
    c.weightx = 0;
    c.weighty = 0;

    add(new JLabel(resourceMap.getString("UserAdminFrame.header")), c);

    c.gridy = 1;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;

    Useraccount uap = new Useraccount(rock);
    UseraccountFactory uaf = new UseraccountFactory(rock, uap);

    utm = new UserTableModel(uaf.get());

    utable = new JTable(utm);
    
    // setFillsViewportHeight works only with Java 6
    // utable.setFillsViewportHeight(true);
    utable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    utable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateUIState();
        // 20110311, eanguan, LoginInUserAdminWindow IMP :: To get the name from the Row selected
        DefaultListSelectionModel lsm = (DefaultListSelectionModel)e.getSource();
        if(lsm.getMaxSelectionIndex() != -1){
        	uNameSelected = (String)utm.getValueAt(lsm.getMaxSelectionIndex(), 0) ;
        }
      }
    });
    
    final TableColumn userColumn = utable.getColumnModel().getColumn(0);
    userColumn.setCellRenderer(new UsernameRenderer());

    final TableColumn passwdColumn = utable.getColumnModel().getColumn(1);
    passwdColumn.setCellRenderer(new PasswordRenderer());
    passwdColumn.setCellEditor(new DefaultCellEditor(new LimitedSizePasswordField(8, 16, true)));
    utable.setRowHeight(20);

    final TableColumn ruleColumn = utable.getColumnModel().getColumn(2);
    final JComboBox comboBox = new JComboBox();
    comboBox.addItem(User.USER);
    comboBox.addItem(User.RND);
    comboBox.addItem(User.ADMIN);
    comboBox.setRenderer(new RoleComboRenderer(resourceMap));
    
    ruleColumn.setCellEditor(new RoleComboEditor(comboBox));
    ruleColumn.setCellRenderer(new RoleRenderer());

    utable.addMouseListener(new MouseListen());
    utable.getTableHeader().addMouseListener(new MouseListen());

    final JScrollPane scrollPane = new JScrollPane(utable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    add(scrollPane, c);

    c.gridy = 2;
    c.anchor = GridBagConstraints.NORTHEAST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;

    adduserbutton = new JButton(getAction("adduser"));
    add(adduserbutton, c);

    c.gridx = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 0;

    removeuserbutton = new JButton(getAction("removeuser"));
    add(removeuserbutton, c);

    c.gridx = 2;
    
    goToLoginButton = new JButton("gotologinpanel");
    goToLoginButton.setAction(getAction("gotologinpanel"));
    goToLoginButton.setName("gotologinpanel");
    add(goToLoginButton,c);
    
    c.gridx = 3;

    discardbutton = new JButton(getAction("discarduseradmin"));
    add(discardbutton, c);

    c.gridx = 4;
    
    savebutton = new JButton(getAction("saveuseradmin"));
    add(savebutton, c);

    c.gridx = 5;
    
    

//    final JButton quit = new JButton(getAction("quit"));
//    add(quit, c);

    // 20110311, eanguan, LoginInUserAdminWindow IMP :: To select the last row from the table
    utable.setRowSelectionInterval(utm.getRowCount()-1, utm.getRowCount()-1);
    updateUIState();
  }  
  public class MouseListen extends MouseAdapter {

    public void mouseClicked(final MouseEvent me) {
      if (me.getButton() == 3) {

        final JPopupMenu menu = new JPopupMenu();
        final JMenuItem addmi = new JMenuItem(getAction("adduser"));
        menu.add(addmi);
        final JMenuItem delmi = new JMenuItem(getAction("removeuser"));
        menu.add(delmi);

        menu.show(me.getComponent(), me.getX(), me.getY());
        me.getComponent().repaint();
      }
    }
  };
  
  //20110311, eanguan, LoginInUserAdminWindow IMP :: Function to get the selected user name
  protected String getUserName(){
	  return uNameSelected ;
  }

  @Action
  public void adduser() {
	  //20110311, eanguan, LoginInUserAdminWindow IMP :: Sending JTable to NewUserDialog to select the newly added row
	  final NewUserDialog nud = new NewUserDialog(application, resourceMap, utm, utable);
	  nud.pack();
	  nud.setVisible(true);
  }
  
  @Action
  public void removeuser() {
		final int ix = utable.getSelectionModel().getMinSelectionIndex();
		RockFactory etlRock = application.getDataModelController()
				.getTechPackTreeDataModel().getRockFactory();
		int noOfInterfacesLocked = 0;
		int noOfTechpacksLocked = 0;
		try {
			noOfInterfacesLocked = Utils.getNoOfInterfacesLocked(((String) utm
					.getValueAt(ix, 0)), etlRock);

			noOfTechpacksLocked = Utils.getNoOfTechpacksLocked(((String) utm
					.getValueAt(ix, 0)), etlRock);
		} catch (Exception e1) {
			logger
					.info("Failed getting no. of Techpacks and interfaces locked by"
							+ (String) utm.getValueAt(ix, 0));
		}
		int selectedValue = 0;
		if (!((String) utm.getValueAt(ix, 0)).equals("admin")) {
			String desc = "";
			if (noOfInterfacesLocked > 0 || noOfTechpacksLocked > 0) {
				desc = "This deletes the locks on " + noOfTechpacksLocked
						+ " Techpack(s) and " + noOfInterfacesLocked
						+ " Interface(s) Locked by "
						+ ((String) utm.getValueAt(ix, 0)) + ".\n";
			}
			selectedValue = JOptionPane.showConfirmDialog(null, desc
					+ "Are you sure that you want to remove this user?",
					"Remove User?", JOptionPane.YES_NO_OPTION);

			if (selectedValue == JOptionPane.YES_OPTION) {
				if (ix >= 0) {
					final Useraccount ua = utm.removeRow(ix);
					tobedeleted.add(ua);
					logger.info("User " + ua.getName() + " pending deletion.");
				}
				updateUIState();
				utable.setRowSelectionInterval(utm.getRowCount()-1, utm.getRowCount()-1);
			}
		} else {
			JOptionPane.showMessageDialog(application.getMainFrame(),
					"The user admin cannot be deleted.", resourceMap
							.getString("UserAdminFrame.DelLastAdmin.title"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}
	}

  @Action
  public void discarduseradmin() {
    try {
      utm.clear();
      tobedeleted.clear();

      Useraccount uap = new Useraccount(rock);
      UseraccountFactory uaf = new UseraccountFactory(rock, uap);

      Iterator<Useraccount> i = uaf.get().iterator();
      while (i.hasNext()) {
        Useraccount ua = i.next();
        utm.addRow(ua);
      }

      updateUIState();
    } catch (Exception e) {
      logger.log(Level.WARNING, "Discard changes failed", e);
      JOptionPane.showMessageDialog(application.getMainFrame(), "Discard changes failed", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
  
  @Action
  public void saveuseradmin() {
	    try {
	      utm.save();
	      while (tobedeleted.size() > 0) {
				Useraccount ua = null;
				ua = tobedeleted.remove(0);
				ua.deleteDB();
				//added for TR HO26863- Issue : Locks persist on techpacks and interfaces for deleted user
				updateDB(ua);
				logger.info("User " + ua.getName() + " deleted.");
				}
	    } catch (Exception e) {
	      logger.log(Level.WARNING, "Saving users failed", e);
	      JOptionPane.showMessageDialog(application.getMainFrame(), "Failed saving users", "Error",
	          JOptionPane.ERROR_MESSAGE);
	    }
	    updateUIState();
	    utable.setRowSelectionInterval(utm.getRowCount()-1, utm.getRowCount()-1);
  }

  /**
   * Unlocks the techpacks and interfaces which are locked by the users which are going
   * to be deleted.
   * @param ua
   * @throws RockException 
   * @throws SQLException 
   */
  private void updateDB(Useraccount ua) throws SQLException, RockException {
		logger.info("Deleting All the locks for the user " + ua.getName());
		int noOfTechpacksLocked = 0;
		int noOfInterfacesLocked = 0;
		String lockedByName = ua.getName();
		RockFactory etlRock = application.getDataModelController()
				.getTechPackTreeDataModel().getRockFactory();

		noOfTechpacksLocked = unLockTechpack(lockedByName, etlRock);
		noOfInterfacesLocked = unLockDataInterface(lockedByName, etlRock);
		
		logger.info("Number of Techpacks Locked by the user "+ lockedByName + "are :" + noOfTechpacksLocked);
		logger.info("Number of Interfaces Locked by the user "+ lockedByName + "are :" + noOfInterfacesLocked);
		
		// Refresh the techpack dataModel to get updated data.
		application.getDataModelController().getTechPackTreeDataModel()
				.refresh();
		// Refresh the dataInterface dataModel to get updated data.
		application.getDataModelController().getInterfaceTreeDataModel()
				.refresh();

	}
  
  /**
	 * unlocks the techpacks locked by the user which is going to be deleted.
	 * 
	 * @param lockedByName
	 * @param etlRock
	 * @throws SQLException
	 * @throws RockException
	 */
	private int unLockTechpack(String lockedByName, RockFactory etlRock)
			throws SQLException, RockException {
		Iterator vers = null;
		int countOfTPs = 0;
		try {
			Versioning m = new Versioning(etlRock, true);
			m.setLockedby(lockedByName);
			VersioningFactory mF = new VersioningFactory(etlRock, m, true);
			vers = mF.get().iterator();
		} catch (Exception e) {
			logger
					.warning("Exception occured while getting data from Versioning Table for user: "
							+ lockedByName);
		}
		while (vers.hasNext()) {
			Versioning ver = (Versioning) vers.next();
			ver.setLockdate(null);
			ver.setLockedby(null);
			ver.saveDB();
			logger.info("Techpack " + ver.getTechpack_name() + " "
					+ ver.getTechpack_version() + " (" + ver.getVersionid()
					+ ") unlocked.");
			countOfTPs++;
		}

		return countOfTPs;
	}

	/**
	 * unlocks the interfaces locked by the user which is going to be deleted.
	 * 
	 * @param lockedByName
	 * @param etlRock
	 * @throws SQLException
	 * @throws RockException
	 */
	private int unLockDataInterface(String lockedByName, RockFactory etlRock)
			throws SQLException, RockException {
		Iterator dif = null;
		int countOfIFs = 0;
		try {
			Datainterface dI = new Datainterface(etlRock, true);
			dI.setLockedby(lockedByName);
			DatainterfaceFactory dIF = new DatainterfaceFactory(etlRock, dI,
					true);
			dif = dIF.get().iterator();
		} catch (Exception e) {
			logger
					.warning("Exception occured while getting data from Datainterface Table for user: "
							+ lockedByName);
		}
		while (dif.hasNext()) {
			Datainterface temp = (Datainterface) dif.next();
			temp.setLockdate(null);
			temp.setLockedby(null);
			temp.saveDB();
			logger.info("Interface " + temp.getInterfacename() + " "
					+ temp.getInterfaceversion() + ") unlocked.");
			countOfIFs++;
		}
		return countOfIFs;
	}
  
  @Action
  public void quit() {
    if (savebutton.isEnabled()
        && JOptionPane.showConfirmDialog(application.getMainFrame(), resourceMap
            .getString("UserAdminFrame.CloseConfirm.message"), resourceMap
            .getString("UserAdminFrame.CloseConfirm.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {

      saveuseradmin();
    }
    setVisible(false);
  }

  private void updateUIState() {
    savebutton.setEnabled(utm.isDirty() || tobedeleted.size() > 0);
    discardbutton.setEnabled(utm.isDirty() || tobedeleted.size() > 0);
    removeuserbutton.setEnabled(utable.getSelectionModel().getMinSelectionIndex() > -1);
  }

  public class UsernameRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
        final boolean hasFocus, final int row, final int column) {
      final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      comp.setIcon(resourceMap.getImageIcon("User.icon"));
      return comp;
    }
  };

  public class PasswordRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
        final boolean hasFocus, final int row, final int column) {
      final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      String x = "";
      for (int i = 0; i < comp.getText().length(); i++) {
        x += "*";
      }
      comp.setText(x);
      return comp;
    }
  };

  public class RoleRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
        final boolean hasFocus, final int row, final int column) {
      final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (comp.getText().equals(User.ADMIN)) {
        comp.setIcon(resourceMap.getImageIcon("Admin.icon"));
      } else if (comp.getText().equals(User.USER)) {
        comp.setIcon(resourceMap.getImageIcon("User.icon"));
      } else if (comp.getText().equals(User.RND)) {
        comp.setIcon(resourceMap.getImageIcon("RnDUser.icon"));
      } else {
        comp.setIcon(resourceMap.getImageIcon("QuestionMark.icon"));
      }
      return comp;
    }
  };

  public class UserTableModel extends AbstractTableModel {

    private final String[] columnNames = { "Name", "Password", "Role", "Last login" };

    final Vector<Useraccount> data;

    private ArrayList<Useraccount> dirtyList = new ArrayList<Useraccount>();

    public UserTableModel(final Vector<Useraccount> data) {

      this.data = new Vector<Useraccount>();
      for (Iterator<Useraccount> iter = data.iterator(); iter.hasNext();) {
        Useraccount ua = iter.next();
          this.data.add(ua);
      }
    }

    public Class<?> getColumnClass(final int col) {
      return String.class;
    }

    public String getColumnName(final int col) {
      return columnNames[col].toString();
    }

    public int getRowCount() {
      return data.size();
    }

    public int getColumnCount() {
      return columnNames.length;
    }

    public Object getValueAt(final int row, final int col) {
      Useraccount a = data.get(row);
      if (col == 0) {
        return a.getName();
      } else if (col == 1) {
        return a.getPassword();
      } else if (col == 2) {
        return a.getRole();
      } else if (col == 3) {
        return a.getLastlogin();
      } else {
        return "";
      }
    }

    public boolean isCellEditable(final int row, final int col) {
      return (col == 1 || col == 2) ? true : false;
    }

    public void setValueAt(final Object value, final int row, final int col) {
      Useraccount ua = data.get(row);
      if (col == 1) {
        ua.setPassword((String) value);
      } else if (col == 2) {
        ua.setRole((String) value);
      }
      fireTableCellUpdated(row, col);
      if (!dirtyList.contains(ua)) {
        dirtyList.add(ua);
      }
      updateUIState();
    }

    public void addRow(final String user, final String passwd, final String role) throws Exception {
      Useraccount found = null;
      int row = -1;
      for (int i = 0; i < data.size(); i++) {
        Useraccount ua = data.get(i);
        if (ua.getName().equals(user)) {
          found = ua;
          row = i;
          break;
        }
      }

      if (found == null) {

        Useraccount ua = new Useraccount(rock);
        ua.setName(user);
        ua.setPassword(passwd);
        ua.setRole(role);

        ua.saveDB();

        logger.info("User " + user + " (" + role + ") created.");
        
        addRow(ua);

      } else {
        if (JOptionPane.showConfirmDialog(application.getMainFrame(), resourceMap
            .getString("UserAdminFrame.AddAlreadyExists.message"), resourceMap
            .getString("UserAdminFrame.AddAlreadyExists.title"), JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == 0) {

          if (found.getPassword().equals(passwd) && found.getRole().equals(role)) {
            // Tried to make an identical user. Ignore.
          } else {
            // Tried to create an user with existing name -> update
            found.setPassword(passwd);
            found.setRole(role);

            found.updateDB();

            fireTableCellUpdated(row, 1);
            fireTableCellUpdated(row, 2);

          }
        }

      }

    }

    public void addRow(final Useraccount a) {
      data.add(a);
      fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public Useraccount removeRow(final int row) {
      final Useraccount ua = data.remove(row);      
      fireTableRowsDeleted(row, row);
      return ua;
    }

    public boolean isDirty() {
      return dirtyList.size() > 0;
    }

    public void clearDirty() {
      dirtyList.clear();
    }

    public void clear() {
      final int rows = data.size();
      data.clear();
      dirtyList.clear();
      fireTableRowsDeleted(0, rows);
    }

    public void save() throws Exception {
      while (dirtyList.size() > 0) {
        Useraccount ua = dirtyList.remove(0);
        ua.saveDB();
        
        logger.info("User " + ua.getName() + " updated.");

      }
    }

  };

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

}
