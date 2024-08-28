package ssc.rockfactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Measurementtypeclass implements Cloneable, RockDBObject {

  private String TYPECLASSID;

  private String VERSIONID;

  private String DESCRIPTION;

  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {};

  private String[] primaryKeyNames = { "TYPECLASSID" };

  private RockFactory rockFact;

  private boolean newItem;

  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Measurementtypeclass(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;

    this.TYPECLASSID = null;
    this.VERSIONID = null;
    this.DESCRIPTION = null;

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Measurementtypeclass(RockFactory rockFact, String TYPECLASSID) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;

      this.TYPECLASSID = TYPECLASSID;

      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Measurementtypeclass o = (Measurementtypeclass) it.next();

        this.TYPECLASSID = o.getTypeclassid();
        this.VERSIONID = o.getVersionid();
        this.DESCRIPTION = o.getDescription();

        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementtypeclass");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  /**
   * Constructor to select the object according to whereObject from the db NO
   * PRIMARY KEY DEFINED
   * 
   * @param whereObject
   *          the where part is constructed from this object
   * @exception SQLException
   */
  public Measurementtypeclass(RockFactory rockFact, Measurementtypeclass whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Measurementtypeclass o = (Measurementtypeclass) it.next();
        this.TYPECLASSID = o.getTypeclassid();
        this.VERSIONID = o.getVersionid();
        this.DESCRIPTION = o.getDescription();
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementtypeclass");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  public Set gimmeModifiedColumns() {
    return modifiedColumns;
  }

  public void cleanModifiedColumns() {
    modifiedColumns.clear();
  }

  /**
   * Method for getting the table name
   * 
   * @return String name of the corresponding table
   */
  public String getTableName() {
    return "Measurementtypeclass";
  }

  /**
   * Update object contents into database PRIMARY KEY MUST BE DEFINED
   * 
   * @exception SQLException
   */
  public int updateDB() throws SQLException, RockException {
    this.newItem = false;
    return rockFact.updateData(this, true, null);
  }

  /**
   * Update object contents into database PRIMARY KEY MUST BE DEFINED
   * 
   * @param boolean useTimestamp if false, the timestamp is not used to compare
   *        if the data has been changed during the transaction
   * @exception SQLException
   */
  public int updateDB(boolean useTimestamp) throws SQLException, RockException {
    this.newItem = false;
    return rockFact.updateData(this, true, null, useTimestamp);
  }

  /**
   * Update object contents into database NO PRIMARY KEY DEFINED
   * 
   * @param boolean useTimestamp if false, the timestamp is not used to compare
   *        if the data has been changed during the transaction
   * @param <this object type> whereObject the where part is constructed from
   *        this object
   * @exception SQLException
   */
  public int updateDB(boolean useTimestamp, Measurementtypeclass whereObject) throws SQLException, RockException {
    this.newItem = false;
    return rockFact.updateData(this, false, whereObject, useTimestamp);
  }

  /**
   * Insert object contents into database
   * 
   * @exception SQLException
   */
  public int insertDB() throws SQLException, RockException {
    this.newItem = false;
    return rockFact.insertData(this);
  }

  /**
   * Insert object contents into database
   * 
   * @param boolean useTimestamp if false, the timestamp is not used to compare
   *        if the data has been changed during the transaction
   * @param boolean useSequence if false the sequence columns don't get their
   *        values from db sequences
   * @exception SQLException
   */
  public int insertDB(boolean useTimestamp, boolean useSequence) throws SQLException, RockException {
    this.newItem = false;
    return rockFact.insertData(this, useTimestamp, useSequence);
  }

  /**
   * Delete object contents from database PRIMARY KEY MUST BE DEFINED
   * 
   * @exception SQLException
   */
  public int deleteDB() throws SQLException, RockException {
    this.newItem = true;
    return rockFact.deleteData(true, this);
  }

  /**
   * Delete object contents from database NO PRIMARY KEY DEFINED
   * 
   * @param <this object type> whereObject the where part is constructed from
   *        this object
   * @exception SQLException
   */
  public int deleteDB(Measurementtypeclass whereObject) throws SQLException, RockException {
    this.newItem = true;
    return rockFact.deleteData(false, whereObject);
  }

  /**
   * Saves the data into the database
   * 
   * @exception SQLException
   */
  public void saveDB() throws SQLException, RockException {

    if (this.newItem) {
      insertDB();
    } else {
      if (isPrimaryDefined()) {
        rockFact.updateData(this, true, this, true);
      } else {
        throw new RockException("Cannot use rock.Measurementtypeclass.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

  public String getTypeclassid() {
    return this.TYPECLASSID;
  }

  public String getVersionid() {
    return this.VERSIONID;
  }

  public String getDescription() {
    return this.DESCRIPTION;
  }

  public String gettimeStampName() {
    return timeStampName;
  }

  public String[] getcolumnsAndSequences() {
    return columnsAndSequences;
  }

  public String[] getprimaryKeyNames() {
    return primaryKeyNames;
  }

  public RockFactory getRockFactory() {
    return this.rockFact;
  }

  public void removeNulls() {
    if (TYPECLASSID == null)
      TYPECLASSID = new String("");
    if (VERSIONID == null)
      VERSIONID = new String("");
    if (DESCRIPTION == null)
      DESCRIPTION = new String("");
  }

  public void setTypeclassid(String TYPECLASSID) {
    modifiedColumns.add("TYPECLASSID");
    this.TYPECLASSID = TYPECLASSID;
  }

  public void setVersionid(String VERSIONID) {
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }

  public void setDescription(String DESCRIPTION) {
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }

  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */

  public boolean equals(Measurementtypeclass o) {

    if ((((this.TYPECLASSID == null) || (o.TYPECLASSID == null)) && (this.TYPECLASSID != o.TYPECLASSID))
        || (((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
        || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))) {
      return false;
    } else if ((((this.TYPECLASSID != null) && (o.TYPECLASSID != null)) && (this.TYPECLASSID.equals(o.TYPECLASSID) == false))
        || (((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
        || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * to enable a public clone method inherited from Object class (private
   * method)
   */
  public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    } catch (CloneNotSupportedException e) {
    }
    return o;
  }

  /**
   * Is the primakey defined for this table
   */
  public boolean isPrimaryDefined() {
    if (this.primaryKeyNames.length > 0)
      return true;
    else
      return false;
  }

  /**
   * Sets the member variables to Db default values
   */
  public void setDefaults() {

  }

  /**
   * Does the the object exist in the database
   * 
   * @return boolean true if exists else false.
   */
  public boolean existsDB() throws SQLException, RockException {
    RockResultSet results = rockFact.setSelectSQL(false, this);
    Iterator it = rockFact.getData(this, results);
    if (it.hasNext()) {
      results.close();
      return true;
    }
    results.close();
    return false;
  }

}
