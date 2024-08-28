package measurementType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ssc.rockfactory.*;

public class Measurementkey implements Cloneable, RockDBObject {

  private String TYPEID;

  private String DATANAME;

  private String DESCRIPTION;

  private Integer ISELEMENT;

  private Integer UNIQUEKEY;

  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {};

  private String[] primaryKeyNames = { "TYPEID", "DATANAME" };

  private RockFactory rockFact;

  private boolean newItem;

  private static int runningId = 0;

  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Measurementkey(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;

    this.TYPEID = "typeId: " + runningId;
    this.DATANAME = "The Dataname: " + runningId;
    this.DESCRIPTION = "Hablahii";
    this.ISELEMENT = new Integer(32);
    // this.ISELEMENT = null;
    this.UNIQUEKEY = new Integer(runningId + 1);

    runningId++;

    // this.TYPEID = null;
    // this.DATANAME = null;
    // this.DESCRIPTION = null;
    // this.ISELEMENT = null;
    // this.UNIQUEKEY = null;

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Measurementkey(RockFactory rockFact, String TYPEID, String DATANAME) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;

      this.TYPEID = TYPEID;
      this.DATANAME = DATANAME;

      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Measurementkey o = (Measurementkey) it.next();

        this.TYPEID = o.getTypeid();
        this.DATANAME = o.getDataname();
        this.DESCRIPTION = o.getDescription();
        this.ISELEMENT = o.getIselement();
        this.UNIQUEKEY = o.getUniquekey();

        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementkey");
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
  public Measurementkey(RockFactory rockFact, Measurementkey whereObject) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Measurementkey o = (Measurementkey) it.next();
        this.TYPEID = o.getTypeid();
        this.DATANAME = o.getDataname();
        this.DESCRIPTION = o.getDescription();
        this.ISELEMENT = o.getIselement();
        this.UNIQUEKEY = o.getUniquekey();
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementkey");
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
    return "Measurementkey";
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
  public int updateDB(boolean useTimestamp, Measurementkey whereObject) throws SQLException, RockException {
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
    System.out.println("Measurementkey deleted");
    return rockFact.deleteData(true, this);
  }

  /**
   * Delete object contents from database NO PRIMARY KEY DEFINED
   * 
   * @param <this object type> whereObject the where part is constructed from
   *        this object
   * @exception SQLException
   */
  public int deleteDB(Measurementkey whereObject) throws SQLException, RockException {
    this.newItem = true;
    System.out.println("Measurementkey deleted");
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
        throw new RockException("Cannot use rock.Measurementkey.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

  public String getTypeid() {
    return this.TYPEID;
  }

  public String getDataname() {
    return this.DATANAME;
  }

  public String getDescription() {
    return this.DESCRIPTION;
  }

  public Integer getIselement() {
    return this.ISELEMENT;
  }

  public Integer getUniquekey() {
    return this.UNIQUEKEY;
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
    if (TYPEID == null)
      TYPEID = new String("");
    if (DATANAME == null)
      DATANAME = new String("");
    if (DESCRIPTION == null)
      DESCRIPTION = new String("");
    if (ISELEMENT == null)
      ISELEMENT = new Integer(0);
    if (UNIQUEKEY == null)
      UNIQUEKEY = new Integer(0);
  }

  public void setTypeid(String TYPEID) {
    modifiedColumns.add("TYPEID");
    this.TYPEID = TYPEID;
  }

  public void setDataname(String DATANAME) {
    System.out.println("Modifying Dataname from " + this.DATANAME + " to " + DATANAME);
    modifiedColumns.add("DATANAME");
    this.DATANAME = DATANAME;
  }

  public void setDescription(String DESCRIPTION) {
    System.out.println("Modifying Description from " + this.DESCRIPTION + " to " + DESCRIPTION);
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }

  public void setIselement(Integer ISELEMENT) {
    modifiedColumns.add("ISELEMENT");
    this.ISELEMENT = ISELEMENT;
  }

  public void setUniquekey(Integer UNIQUEKEY) {
    modifiedColumns.add("UNIQUEKEY");
    System.out.println("Modifying unique key from " + this.UNIQUEKEY + " to " + UNIQUEKEY);
    this.UNIQUEKEY = UNIQUEKEY;
  }

  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */

  public boolean equals(Measurementkey o) {

    if ((((this.TYPEID == null) || (o.TYPEID == null)) && (this.TYPEID != o.TYPEID))
        || (((this.DATANAME == null) || (o.DATANAME == null)) && (this.DATANAME != o.DATANAME))
        || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
        || (((this.ISELEMENT == null) || (o.ISELEMENT == null)) && (this.ISELEMENT != o.ISELEMENT))
        || (((this.UNIQUEKEY == null) || (o.UNIQUEKEY == null)) && (this.UNIQUEKEY != o.UNIQUEKEY))) {
      return false;
    } else if ((((this.TYPEID != null) && (o.TYPEID != null)) && (this.TYPEID.equals(o.TYPEID) == false))
        || (((this.DATANAME != null) && (o.DATANAME != null)) && (this.DATANAME.equals(o.DATANAME) == false))
        || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
        || (((this.ISELEMENT != null) && (o.ISELEMENT != null)) && (this.ISELEMENT.equals(o.ISELEMENT) == false))
        || (((this.UNIQUEKEY != null) && (o.UNIQUEKEY != null)) && (this.UNIQUEKEY.equals(o.UNIQUEKEY) == false))) {
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
