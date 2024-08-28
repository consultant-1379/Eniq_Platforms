package measurementType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ssc.rockfactory.*;

public class Measurementtable implements Cloneable, RockDBObject {

  private String MTABLEID;

  private String TABLELEVEL;

  private String TYPEID;

  private String BASETABLENAME;

  private String DEFAULT_TEMPLATE;

  private String PARTITIONPLAN;

  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {};

  private String[] primaryKeyNames = { "MTABLEID" };

  private RockFactory rockFact;

  private boolean newItem;

  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Measurementtable(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;

    this.MTABLEID = "id";
    this.TABLELEVEL = "level";
    this.TYPEID = "Id";
    this.BASETABLENAME = "basetable";
    this.DEFAULT_TEMPLATE = "template";
    this.PARTITIONPLAN = "Medium";

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Measurementtable(RockFactory rockFact, String MTABLEID) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;

      this.MTABLEID = MTABLEID;

      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Measurementtable o = (Measurementtable) it.next();

        this.MTABLEID = o.getMtableid();
        this.TABLELEVEL = o.getTablelevel();
        this.TYPEID = o.getTypeid();
        this.BASETABLENAME = o.getBasetablename();
        this.DEFAULT_TEMPLATE = o.getDefault_template();
        this.PARTITIONPLAN = o.getPartitionplan();

        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementtable");
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
  public Measurementtable(RockFactory rockFact, Measurementtable whereObject) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Measurementtable o = (Measurementtable) it.next();
        this.MTABLEID = o.getMtableid();
        this.TABLELEVEL = o.getTablelevel();
        this.TYPEID = o.getTypeid();
        this.BASETABLENAME = o.getBasetablename();
        this.DEFAULT_TEMPLATE = o.getDefault_template();
        this.PARTITIONPLAN = o.getPartitionplan();
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementtable");
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
    return "Measurementtable";
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
  public int updateDB(boolean useTimestamp, Measurementtable whereObject) throws SQLException, RockException {
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
    System.out.println("Measurementtable deleted");
    return rockFact.deleteData(true, this);
  }

  /**
   * Delete object contents from database NO PRIMARY KEY DEFINED
   * 
   * @param <this object type> whereObject the where part is constructed from
   *        this object
   * @exception SQLException
   */
  public int deleteDB(Measurementtable whereObject) throws SQLException, RockException {
    this.newItem = true;
    System.out.println("Measurementtable deleted");
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
        throw new RockException("Cannot use rock.Measurementtable.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

  public String getMtableid() {
    return this.MTABLEID;
  }

  public String getTablelevel() {
    return this.TABLELEVEL;
  }

  public String getTypeid() {
    return this.TYPEID;
  }

  public String getBasetablename() {
    return this.BASETABLENAME;
  }

  public String getDefault_template() {
    return this.DEFAULT_TEMPLATE;
  }

  public String getPartitionplan() {
    return this.PARTITIONPLAN;
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
    if (MTABLEID == null)
      MTABLEID = new String("");
    if (TABLELEVEL == null)
      TABLELEVEL = new String("");
    if (TYPEID == null)
      TYPEID = new String("");
    if (BASETABLENAME == null)
      BASETABLENAME = new String("");
    if (DEFAULT_TEMPLATE == null)
      DEFAULT_TEMPLATE = new String("");
    if (PARTITIONPLAN == null)
      PARTITIONPLAN = new String("");
  }

  public void setMtableid(String MTABLEID) {
    modifiedColumns.add("MTABLEID");
    this.MTABLEID = MTABLEID;
  }

  public void setTablelevel(String TABLELEVEL) {
    modifiedColumns.add("TABLELEVEL");
    this.TABLELEVEL = TABLELEVEL;
  }

  public void setTypeid(String TYPEID) {
    modifiedColumns.add("TYPEID");
    this.TYPEID = TYPEID;
  }

  public void setBasetablename(String BASETABLENAME) {
    modifiedColumns.add("BASETABLENAME");
    this.BASETABLENAME = BASETABLENAME;
  }

  public void setDefault_template(String DEFAULT_TEMPLATE) {
    modifiedColumns.add("DEFAULT_TEMPLATE");
    this.DEFAULT_TEMPLATE = DEFAULT_TEMPLATE;
  }

  public void setPartitionplan(String PARTITIONPLAN) {
    modifiedColumns.add("PARTITIONPLAN");
    this.PARTITIONPLAN = PARTITIONPLAN;
  }

  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */

  public boolean equals(Measurementtable o) {

    if ((((this.MTABLEID == null) || (o.MTABLEID == null)) && (this.MTABLEID != o.MTABLEID))
        || (((this.TABLELEVEL == null) || (o.TABLELEVEL == null)) && (this.TABLELEVEL != o.TABLELEVEL))
        || (((this.TYPEID == null) || (o.TYPEID == null)) && (this.TYPEID != o.TYPEID))
        || (((this.BASETABLENAME == null) || (o.BASETABLENAME == null)) && (this.BASETABLENAME != o.BASETABLENAME))
        || (((this.DEFAULT_TEMPLATE == null) || (o.DEFAULT_TEMPLATE == null)) && (this.DEFAULT_TEMPLATE != o.DEFAULT_TEMPLATE))
        || (((this.PARTITIONPLAN == null) || (o.PARTITIONPLAN == null)) && (this.PARTITIONPLAN != o.PARTITIONPLAN))) {
      return false;
    } else if ((((this.MTABLEID != null) && (o.MTABLEID != null)) && (this.MTABLEID.equals(o.MTABLEID) == false))
        || (((this.TABLELEVEL != null) && (o.TABLELEVEL != null)) && (this.TABLELEVEL.equals(o.TABLELEVEL) == false))
        || (((this.TYPEID != null) && (o.TYPEID != null)) && (this.TYPEID.equals(o.TYPEID) == false))
        || (((this.BASETABLENAME != null) && (o.BASETABLENAME != null)) && (this.BASETABLENAME.equals(o.BASETABLENAME) == false))
        || (((this.DEFAULT_TEMPLATE != null) && (o.DEFAULT_TEMPLATE != null)) && (this.DEFAULT_TEMPLATE
            .equals(o.DEFAULT_TEMPLATE) == false))
        || (((this.PARTITIONPLAN != null) && (o.PARTITIONPLAN != null)) && (this.PARTITIONPLAN.equals(o.PARTITIONPLAN) == false))) {
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
