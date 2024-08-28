package com.distocraft.dc5000.etl.gui.activation;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;

/**
 * This class represents tablemodel for TypeActivation data.
 * Copyright Distocraft 2006<br>
 * <br>
 * $id$
 *
 * @author berggren
 */
public class TypeActivationTableModel extends AbstractTableModel {

  /**
   * Column names of this table.
   */
  private static final String[] columns = { "Name", "Level", "Storagetime", "Status", "Time" };

  /** 
   * Vector containing typeActivations (rows) of this table.
   */
  private Vector typeActivations = new Vector();

  /**
   * The selected Tpactivation of which this TypeActivationTableModel is related to.
   */
  private Tpactivation tpactivation;

  private RockFactory dwhrepRockFactory;

  /**
   * Constructor of this class. Initializes class variables.
   * @param tpactivation The selected Tpactivation of which this TypeActivationTableModel is related to. 
   * @param rockFactory RockFactory object to handle database queries.
   * @throws Exception
   */
  TypeActivationTableModel(Tpactivation tpactivation, RockFactory dwhrepRockFactory) throws Exception {
    this.tpactivation = tpactivation;
    this.dwhrepRockFactory = dwhrepRockFactory;

    Typeactivation whereTypeActivation = new Typeactivation(this.dwhrepRockFactory);
    whereTypeActivation.setTechpack_name(this.tpactivation.getTechpack_name());
    TypeactivationFactory typeActivationFactory = new TypeactivationFactory(this.dwhrepRockFactory, whereTypeActivation);

    Vector typeActivations = typeActivationFactory.get();
    Iterator typeActivationsIterator = typeActivations.iterator();

    while (typeActivationsIterator.hasNext()) {
      Typeactivation currentTypeactivation = (Typeactivation) typeActivationsIterator.next();
      if (currentTypeactivation.getStatus().equalsIgnoreCase("ACTIVE")
          || currentTypeactivation.getStatus().equalsIgnoreCase("INACTIVE")) {
        addRow(currentTypeactivation);
      }
    }

  }

  /**
   * Returns the Typeactivation at row given as a parameter.
   * @param row Rownumber the Typeactivation is located.
   * @return Returns a Typeactivation object.
   */
  public Typeactivation getTypeActivationAt(int row) {
    return (Typeactivation) this.typeActivations.get(row);
  }

  /**
   * Returns a name of a column.
   * @param col Number of column of which name is to be returned.
   */
  public String getColumnName(int col) {
    return columns[col];
  }

  /**
   * Returns the class of a column.
   * @param col Number of column of which class is to be returned.
   */
  public Class getColumnClass(int col) {
    return String.class;
  }

  /**
   * Returns the value at desired row and columnn in this TypeActivationTableModel.
   * @param row number of target row.
   * @param col number of target column.
   * @return Returns The value of cell defined by parameters row and column. Returns "undefined" if no value is found.
   */
  public Object getValueAt(int row, int col) {
    Typeactivation targetTypeActivation = (Typeactivation) this.typeActivations.get(row);

    if (col == 0) {
      // Typename
      return targetTypeActivation.getTypename();
    } else if (col == 1) {
      // Tablelevel
      return targetTypeActivation.getTablelevel();
    } else if (col == 2) {
      // Storagetime
      return targetTypeActivation.getStoragetime();
    } else if (col == 3) {
      // Status
      return targetTypeActivation.getStatus();
    } else if (col == 4) {
      // Type
      return targetTypeActivation.getType();
    } else {
      return "undefined";
    }
  }

  /**
   * Returns the number of columns in this table.
   * @return Returns the number of columns in this table.
   */
  public int getColumnCount() {
    return columns.length;
  }

  /**
   * Returns the number of rows in this table.
   * @return Returns the number of rows in this table.
   */
  public int getRowCount() {
    return this.typeActivations.size();
  }

  /**
   * This method adds one row to this table.
   * @param typeActivation is the row to be added to this table.
   * @return
   */
  int addRow(Typeactivation typeActivation) {

    if (typeActivation == null)
      return 0;
    this.typeActivations.add(typeActivation);
    fireTableRowsInserted(this.typeActivations.size(), this.typeActivations.size());
    return this.typeActivations.size() - 1;
  }
}
