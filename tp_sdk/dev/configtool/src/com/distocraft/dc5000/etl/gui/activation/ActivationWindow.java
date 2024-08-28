package com.distocraft.dc5000.etl.gui.activation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Partitionplan;
import com.distocraft.dc5000.repository.dwhrep.PartitionplanFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

public class ActivationWindow extends JDialog {

  private UI ui;

  private RockFactory dwhrepRockFactory = null;

  private Tpactivation tpactivation = null;

  private JComboBox techPack = null;

  private JComboBox status = null;

  private JComboBox versionId = null;

  private boolean commit = false;

  private GridBagConstraints c = null;

  private JFrame parent = null;

  private ActivationSelector activationSelector = null;

  private TypeActivationTable table = null;

  ActivationWindow(JFrame parent, RockFactory dwhrepRockFactory, Tpactivation tpactivation, UI ui,
      ActivationSelector activationSelector, TypeActivationTable table) {
    super(parent, true);

    this.dwhrepRockFactory = dwhrepRockFactory;
    this.tpactivation = tpactivation;
    this.ui = ui;
    this.parent = parent;
    this.activationSelector = activationSelector;
    this.table = table;

    try {

      if (this.tpactivation == null)
        setTitle("New Activation");
      else
        setTitle("Activation " + this.tpactivation.getTechpack_name());

      final Container con = getContentPane();
      con.setLayout(new GridBagLayout());
      c = new GridBagConstraints();

      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.weighty = 0;
      c.insets = new Insets(2, 4, 2, 2);

      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 1;
      c.gridx = 1;
      con.add(new JLabel("Tech Pack"), c);

      c.gridx = 2;
      if (this.tpactivation == null) {
        Object[] availableTechPacks = getAvailableTechPacks();
        // Creating a new activation.
        if (availableTechPacks.length == 0) {
          JLabel noTechPacksAvailable = new JLabel("No techpacks available");
          con.add(noTechPacksAvailable, c);
        } else {
          techPack = new JComboBox(availableTechPacks);
          // Add the actionlistener which reloads the versionId select element when the techpack is changed.
          techPack.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
              Object[] versionIDs = getTechpackVersionIDs((String) ActivationWindow.this.techPack.getSelectedItem());
              con.remove(versionId);

              versionId = new JComboBox(versionIDs);
              versionId.setSelectedIndex(0);

              c.gridx = 2;
              c.gridy = 3;
              con.add(versionId, c);
              pack();

            }
          });
          con.add(techPack, c);

        }
      } else {
        // Modifying an existing activation.
        con.add(new JLabel(this.tpactivation.getTechpack_name()), c);
      }

      c.gridx = 1;
      c.gridy = 2;
      con.add(new JLabel("Status"), c);

      c.gridx = 2;
      String[] statusOptions = { new String("INACTIVE"), new String("ACTIVE") };
      status = new JComboBox(statusOptions);
      if (this.tpactivation != null && this.tpactivation.getStatus().equalsIgnoreCase("ACTIVE"))
        status.setSelectedIndex(1);
      else
        status.setSelectedIndex(0);

      status.setRenderer(new StatusRenderer());
      con.add(status, c);

      c.gridx = 1;
      c.gridy = 3;
      con.add(new JLabel("VersionID"), c);

      c.gridx = 2;
      Object[] versionIDs = { new String("") };

      if (this.tpactivation != null) {
        versionIDs = getTechpackVersionIDs(this.tpactivation.getTechpack_name());
        versionId = new JComboBox(versionIDs);
        versionId.setSelectedItem(this.tpactivation.getVersionid());
        con.add(versionId, c);
      } else if (this.techPack != null) {
        versionIDs = getTechpackVersionIDs((String) this.techPack.getSelectedItem());
        versionId = new JComboBox(versionIDs);
        con.add(versionId, c);
      } else {
        JLabel noVersionIDAvailable = new JLabel("No VersionID available");
        con.add(noVersionIDAvailable, c);
      }

      c.gridx = 1;
      c.gridy = 4;
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridx = 1;
      c.gridy = 5;
      JButton discard = new JButton("Discard", ConfigTool.delete);
      discard.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          commit = false;
          setVisible(false);
        }
      });
      con.add(discard, c);

      c.gridx = 2;
      JButton save = new JButton("Save", ConfigTool.check);
      save.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {

          String error = "";

          if (ActivationWindow.this.techPack == null && ActivationWindow.this.tpactivation == null)
            error += "Techpack must be defined\n";

          if (error.length() > 0) {
            JOptionPane.showMessageDialog(ActivationWindow.this, error, "Invalid configuration",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          commit = true;
          setVisible(false);
        }
      });
      con.add(save, c);

      c.gridx = 3;
      c.gridy = 7;
      c.insets = new Insets(0, 0, 0, 0);
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      pack();
      setVisible(true);

      if (!commit)
        return;

      ActivationWorker activationWorker = new ActivationWorker();

      activationWorker.start();

    } catch (Exception e) {
      ErrorDialog ed = new ErrorDialog(parent, "Error", "Unable to save DB", e);
    }

  }

  public boolean committed() {
    return commit;
  }

  public class StatusRenderer extends JLabel implements ListCellRenderer {

    private Border noFocusBorder;

    /**
     * Constructs a default renderer object for an item in a list.
     */
    public StatusRenderer() {
      super();

      noFocusBorder = new EmptyBorder(1, 1, 1, 1);

      setOpaque(true);
      setBorder(noFocusBorder);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

      String status = (String) value;

      setComponentOrientation(list.getComponentOrientation());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      setText(status);

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

      return this;
    }

    public void validate() {
    }

    public void revalidate() {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(Rectangle r) {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      if (propertyName == "text")
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

  };

  /**
   * This method loads version id's the techpack has in database table Versioning.
   * @param techpackName Name of the techpack used in getting version id's.
   * @return Returns an array of version id strings.
   */
  private Object[] getTechpackVersionIDs(String techpackName) {
    ArrayList versionIDs = new ArrayList();

    try {
      RockFactory dwhrepRockFactory = this.dwhrepRockFactory;

      Versioning whereVersioning = new Versioning(dwhrepRockFactory);
      whereVersioning.setTechpack_name(techpackName);
      VersioningFactory versioningFactory = new VersioningFactory(dwhrepRockFactory, whereVersioning);
      Vector targetVersionings = versioningFactory.get();
      Iterator targetVersioningsIterator = targetVersionings.iterator();

      while (targetVersioningsIterator.hasNext()) {
        Versioning currentVersioning = (Versioning) targetVersioningsIterator.next();
        versionIDs.add(currentVersioning.getVersionid());
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (RockException e) {
      e.printStackTrace();
    }

    return (versionIDs.toArray());

  }

  /** 
   * This method returns the techpacknames which don't yet have an activation entry in TPActivation table.
   * @return Returns an array of techpackname strings.
   */
  private Object[] getAvailableTechPacks() {
    ArrayList availableTechPacks = new ArrayList();
    ArrayList TpactivationTechPacks = new ArrayList();
    try {
      // First get the techpacks which already has a TPActivation entry.
      RockFactory dwhrepRockFactory = this.dwhrepRockFactory;
      Tpactivation whereTpactivation = new Tpactivation(dwhrepRockFactory);
      TpactivationFactory tpactivationFactory = new TpactivationFactory(dwhrepRockFactory, whereTpactivation);
      Vector allTpactivations = tpactivationFactory.get();
      Iterator allTpactivationsIterator = allTpactivations.iterator();

      while (allTpactivationsIterator.hasNext()) {
        Tpactivation currentTpactivation = (Tpactivation) allTpactivationsIterator.next();
        TpactivationTechPacks.add(currentTpactivation.getTechpack_name());
      }

      // Next iterate through all techpack's in Versioning table.
      Versioning whereVersioning = new Versioning(dwhrepRockFactory);
      VersioningFactory versioningFactory = new VersioningFactory(dwhrepRockFactory, whereVersioning);
      Vector allVersionings = versioningFactory.get();
      Iterator allVersioningsIterator = allVersionings.iterator();

      while (allVersioningsIterator.hasNext()) {
        Versioning currentVersioning = (Versioning) allVersioningsIterator.next();

        if (TpactivationTechPacks.indexOf(currentVersioning.getTechpack_name()) == -1
            && availableTechPacks.indexOf(currentVersioning.getTechpack_name()) == -1) {
          availableTechPacks.add(currentVersioning.getTechpack_name());
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (RockException e) {
      e.printStackTrace();
    }

    return availableTechPacks.toArray();
  }

  /**
   * This method returns the type of the techpack by the given versionid of the techpack.
   * @return Returns the type of the techpack.
   */
  private String getTechpackVersionType(String versionId) {
    String techpackType = "";
    try {
      RockFactory dwhrepRockFactory = this.dwhrepRockFactory;
      Versioning whereVersioning = new Versioning(dwhrepRockFactory);
      whereVersioning.setVersionid(versionId);
      VersioningFactory versioningFactory = new VersioningFactory(dwhrepRockFactory, whereVersioning);
      Vector targetVersionings = versioningFactory.get();
      Iterator targetVersioningsIterator = targetVersionings.iterator();

      if (targetVersioningsIterator.hasNext()) {
        Versioning targetVersioning = (Versioning) targetVersioningsIterator.next();
        techpackType = targetVersioning.getTechpack_type();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (RockException e) {
      e.printStackTrace();
    }

    return techpackType;

  }

  /**
   * This method saves (inserts or updates) the data in TypeActivation-table related to a TPActivation. 
   * @param newActivation is true if the TPActivation is a completely new TPActivation. 
   * newActivation is false if the TPActivation already exists in the database.
   * @param tpactivation Tpactivation of which TypeActivation data is to be updated.
   */
  private void saveTypeActivationData(boolean newActivation, Tpactivation tpactivation) {
    try {
      // This vector holds the TypeActivations to be updated.
      Vector typeActivations = new Vector();

      String targetVersionId = tpactivation.getVersionid();

      // First get the TypeActivations of type Measurement.
      // Get all MeasurementTypes related to this VersionID.
      Measurementtype whereMeasurementType = new Measurementtype(tpactivation.getRockFactory());
      whereMeasurementType.setVersionid(targetVersionId);
      MeasurementtypeFactory measurementtypeFactory = new MeasurementtypeFactory(tpactivation.getRockFactory(),
          whereMeasurementType);
      Vector targetMeasurementTypes = measurementtypeFactory.get();
      Iterator targetMeasurementTypesIterator = targetMeasurementTypes.iterator();

      while (targetMeasurementTypesIterator.hasNext()) {
        Measurementtype targetMeasurementType = (Measurementtype) targetMeasurementTypesIterator.next();
        String targetTypeId = targetMeasurementType.getTypeid();
        String targetTypename = targetMeasurementType.getTypename(); // Typename

        
        if (targetMeasurementType.getJoinable() != null && targetMeasurementType.getJoinable().length() != 0) {
          // Adding new PREV_ table.
          Typeactivation preTypeActivation = new Typeactivation(tpactivation.getRockFactory());
          preTypeActivation.setTypename(targetTypename + "_PREV");
          preTypeActivation.setTablelevel("PLAIN");
          preTypeActivation.setStoragetime(new Long(-1));
          preTypeActivation.setType("Measurement");
          preTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
          preTypeActivation.setStatus(tpactivation.getStatus());
          preTypeActivation.setPartitionplan(null);
          typeActivations.add(preTypeActivation);
        }
        
        Measurementtable whereMeasurementTable = new Measurementtable(tpactivation.getRockFactory());
        whereMeasurementTable.setTypeid(targetTypeId);
        MeasurementtableFactory measurementTableFactory = new MeasurementtableFactory(tpactivation.getRockFactory(),
            whereMeasurementTable);
        Vector targetMeasurementTables = measurementTableFactory.get();
        Iterator targetMeasurementTablesIterator = targetMeasurementTables.iterator();

        while (targetMeasurementTablesIterator.hasNext()) {
          Measurementtable targetMeasurementTable = (Measurementtable) targetMeasurementTablesIterator.next();
          String targetTableLevel = targetMeasurementTable.getTablelevel(); // Tablelevel
          String targetPartitionPlan = targetMeasurementTable.getPartitionplan();

          Partitionplan wherePartitionPlan = new Partitionplan(tpactivation.getRockFactory());
          wherePartitionPlan.setPartitionplan(targetPartitionPlan);
          PartitionplanFactory partitionPlanFactory = new PartitionplanFactory(tpactivation.getRockFactory(),
              wherePartitionPlan);
          Vector targetPartitionPlans = partitionPlanFactory.get();
          Iterator partitionPlanIterator = targetPartitionPlans.iterator();

          Long targetDefaultStorageTime = new Long(-1);
          if (partitionPlanIterator.hasNext()) {
            Partitionplan currentPartitionPlan = (Partitionplan) partitionPlanIterator.next();
            targetDefaultStorageTime = currentPartitionPlan.getDefaultstoragetime(); // StorageTime
          }

          // All the needed data is gathered from tables.
          // Add the Typeactivation of type Measurement to typeActivations-vector to be saved later.
          Typeactivation targetTypeActivation = new Typeactivation(tpactivation.getRockFactory());
          targetTypeActivation.setTypename(targetTypename);
          targetTypeActivation.setTablelevel(targetTableLevel);
          targetTypeActivation.setStoragetime(targetDefaultStorageTime);
          targetTypeActivation.setType("Measurement");
          targetTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
          targetTypeActivation.setStatus(tpactivation.getStatus());
          typeActivations.add(targetTypeActivation);
        }

      }

      // Next get the TypeActivations of type Reference.
      // Get all ReferenceTables related to this VersionID.
      Referencetable whereReferenceTable = new Referencetable(tpactivation.getRockFactory());
      whereReferenceTable.setVersionid(targetVersionId);
      ReferencetableFactory referenceTableFactory = new ReferencetableFactory(tpactivation.getRockFactory(),
          whereReferenceTable);
      Vector targetReferenceTables = referenceTableFactory.get();
      Iterator targetReferenceTablesIterator = targetReferenceTables.iterator();

      while (targetReferenceTablesIterator.hasNext()) {
        Referencetable targetReferenceTable = (Referencetable) targetReferenceTablesIterator.next();
        String typename = targetReferenceTable.getTypename();

        Typeactivation targetTypeActivation = new Typeactivation(tpactivation.getRockFactory());
        targetTypeActivation.setTypename(typename);
        targetTypeActivation.setType("Reference");
        targetTypeActivation.setTablelevel("PLAIN");
        targetTypeActivation.setStoragetime(new Long(-1));
        targetTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
        targetTypeActivation.setStatus(tpactivation.getStatus());
        typeActivations.add(targetTypeActivation);
      }

      // Now the vector typeActivations holds the Typeactivation-objects ready to be saved. Start saving the values.

      if (newActivation) {
        Iterator typeActivationsIterator = typeActivations.iterator();
        while (typeActivationsIterator.hasNext()) {
          Typeactivation targetTypeActivation = (Typeactivation) typeActivationsIterator.next();
          /*
           System.out.println("Techpackname = " + targetTypeActivation.getTechpack_name());
           System.out.println("Status = " + targetTypeActivation.getStatus());
           System.out.println("Typename = " + targetTypeActivation.getTypename());
           System.out.println("Tablelevel = " + targetTypeActivation.getTablelevel());
           System.out.println("Storagetime = " + targetTypeActivation.getStoragetime().toString());
           System.out.println("Type = " + targetTypeActivation.getType());
           System.out.println("-------------------" + targetTypeActivation.getType());
           */
          // Tpactivation is new. Just insert the values.
          targetTypeActivation.insertDB();
        }
      } else {
        // Update the values in TypeActivation table.
        updateTypeActivations(tpactivation.getRockFactory(), typeActivations, tpactivation.getTechpack_name());
      }

      // Reload the changes from the database.
      ConfigTool.reloadConfig();

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (RockException e) {
      e.printStackTrace();
    }

  }

  /**
   * This method updates the TypeActivation-table when a Tpactivation has changed.
   * @param dwhrepRockFactory RockFactory object to handle database actions.
   * @param newTypeActivations New TypeActivations to be saved to database.
   * @param techpackName is the name of the techpack.
   */
  private void updateTypeActivations(RockFactory dwhrepRockFactory, Vector newTypeActivations, String techpackName) {
    try {
      // These two hashmaps contain the keys and objects to compare between new and existing TypeActivations.
      HashMap existingTypeActivationsMap = new HashMap();
      HashMap newTypeActivationsMap = new HashMap();

      Iterator newTypeActivationsIterator = newTypeActivations.iterator();
      while (newTypeActivationsIterator.hasNext()) {
        Typeactivation currentNewTypeActivation = (Typeactivation) newTypeActivationsIterator.next();
        // Create a string that identifies the TypeActivation. 
        // This string is used as a key value when comparing between new and existing TypeActivations.
        // This id string is generated by the primary keys of this object.
        // For example. TECH_PACK_NAME;TYPE_NAME;TABLE_LEVEL
        String idString = currentNewTypeActivation.getTechpack_name() + ";" + currentNewTypeActivation.getTypename()
            + ";" + currentNewTypeActivation.getTablelevel();
        newTypeActivationsMap.put(idString, currentNewTypeActivation);
      }

      Typeactivation whereTypeActivation = new Typeactivation(dwhrepRockFactory);
      whereTypeActivation.setTechpack_name(techpackName);
      TypeactivationFactory typeActivationRockFactory = new TypeactivationFactory(dwhrepRockFactory,
          whereTypeActivation);
      Vector existingTypeActivations = typeActivationRockFactory.get();
      Iterator existingTypeActivationsIterator = existingTypeActivations.iterator();
      while (existingTypeActivationsIterator.hasNext()) {
        Typeactivation currentExistingTypeActivation = (Typeactivation) existingTypeActivationsIterator.next();
        // Create a string that identifies the TypeActivation and add it to the existing TypeActivations map.
        String idString = currentExistingTypeActivation.getTechpack_name() + ";"
            + currentExistingTypeActivation.getTypename() + ";" + currentExistingTypeActivation.getTablelevel();
        existingTypeActivationsMap.put(idString, currentExistingTypeActivation);

      }

      HashMap existingTypeActivationsIdStringsMap = new HashMap();

      // First iterate through the existing TypeActivations and remove the duplicate TypeActivations from the new TypeActivations.
      Set existingTypeActivationsIdStrings = existingTypeActivationsMap.keySet();
      Iterator existingTypeActivationsIdStringsIterator = existingTypeActivationsIdStrings.iterator();
      while (existingTypeActivationsIdStringsIterator.hasNext()) {
        String currentIdString = (String) existingTypeActivationsIdStringsIterator.next();
        if (newTypeActivationsMap.containsKey(currentIdString)) {
          // Remove this from newTypeActivations.
          newTypeActivationsMap.remove(currentIdString);
          existingTypeActivationsIdStringsMap.put(currentIdString, "");
        }
      }

      Set duplicateExistingTypeActivationIdStrings = existingTypeActivationsIdStringsMap.keySet();
      Iterator duplicateExistingTypeActivationIdStringsIterator = duplicateExistingTypeActivationIdStrings.iterator();
      while (duplicateExistingTypeActivationIdStringsIterator.hasNext()) {
        String targetTypeActivationIdString = (String) duplicateExistingTypeActivationIdStringsIterator.next();

        // Only change the status of this TypeActivation to active.
        Typeactivation targetExistingTypeActivation = (Typeactivation) existingTypeActivationsMap
            .get(targetTypeActivationIdString);
        if (targetExistingTypeActivation.getStatus().equalsIgnoreCase("OBSOLETE")) {
          targetExistingTypeActivation.setStatus("ACTIVE");
          targetExistingTypeActivation.updateDB();

        }

        existingTypeActivationsMap.remove(targetTypeActivationIdString);
      }

      // Now the two HashMaps should contain new and obsolete values.
      // New TypeActivations are simply inserted to database.
      Collection newTypeActivationsCollection = newTypeActivationsMap.values();
      newTypeActivationsIterator = newTypeActivationsCollection.iterator();
      while (newTypeActivationsIterator.hasNext()) {
        Typeactivation currentTypeActivation = (Typeactivation) newTypeActivationsIterator.next();
        currentTypeActivation.insertDB();
      }

      // Existing TypeActivations which don't exist in the changed version are marked as obsolete.
      Collection obsoleteTypeActivationsVector = existingTypeActivationsMap.values();
      Iterator obsoleteTypeActivationsIterator = obsoleteTypeActivationsVector.iterator();
      while (obsoleteTypeActivationsIterator.hasNext()) {
        Typeactivation currentTypeActivation = (Typeactivation) obsoleteTypeActivationsIterator.next();
        // Mark the TypeActivation as obsolete and update.
        currentTypeActivation.setStatus("OBSOLETE");
        currentTypeActivation.updateDB();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (RockException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 
   * @author berggren
   *
   */
  public class ActivationWorker extends SwingWorker {

    public ActivationWorker() {
      ui.startOperation("Saving activation...");
    }

    public Object construct() {

      boolean newActivation = false;

      // Start saving the values of this TPActivation to database.
      if (ActivationWindow.this.tpactivation == null) {
        ActivationWindow.this.tpactivation = new Tpactivation(ActivationWindow.this.dwhrepRockFactory);
        newActivation = true;
      }

      ActivationWindow.this.tpactivation.setVersionid((String) ActivationWindow.this.versionId.getSelectedItem());

      if (newActivation == true && ActivationWindow.this.techPack != null) {
        ActivationWindow.this.tpactivation.setTechpack_name((String) ActivationWindow.this.techPack.getSelectedItem());
      }
      ActivationWindow.this.tpactivation.setType(getTechpackVersionType(ActivationWindow.this.tpactivation
          .getVersionid()));

      ActivationWindow.this.tpactivation.setStatus((String) ActivationWindow.this.status.getSelectedItem().toString()
          .toUpperCase());

      try {
        if (newActivation == true) {
          ActivationWindow.this.tpactivation.insertDB(false, false);
        } else {
          ActivationWindow.this.tpactivation.updateDB();
        }

        // Reload the changes from the database.
        ConfigTool.reloadConfig();

      } catch (Exception e) {
        ErrorDialog ed = new ErrorDialog(parent, "Error", "Unable to save DB", e);
      }

      // Update the data in TypeActivation table related to this TPActivation.
      saveTypeActivationData(newActivation, ActivationWindow.this.tpactivation);

      return null;
    }

    public void finished() {
      if (ActivationWindow.this.activationSelector != null)
        ActivationWindow.this.activationSelector.refresh();
      if (ActivationWindow.this.table != null)
        ActivationWindow.this.table.refresh();

      ui.endOperation();
    }

  }

}
