package ssc.rockfactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Meta_collection_sets implements Cloneable, RockDBObject {

  private Long COLLECTION_SET_ID;

  private String COLLECTION_SET_NAME;

  private String DESCRIPTION;

  private String VERSION_NUMBER;

  private String ENABLED_FLAG;

  private String TYPE;

  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {};

  private String[] primaryKeyNames = { "COLLECTION_SET_ID", "VERSION_NUMBER" };

  private RockFactory rockFact;

  private boolean newItem;

  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_collection_sets(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;

    this.COLLECTION_SET_ID = null;
    this.COLLECTION_SET_NAME = null;
    this.DESCRIPTION = null;
    this.VERSION_NUMBER = null;
    this.ENABLED_FLAG = null;
    this.TYPE = null;

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_collection_sets(RockFactory rockFact, Long COLLECTION_SET_ID, String VERSION_NUMBER) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

      this.COLLECTION_SET_ID = COLLECTION_SET_ID;
      this.VERSION_NUMBER = VERSION_NUMBER;

      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_collection_sets o = (Meta_collection_sets) it.next();

        this.COLLECTION_SET_ID = o.getCollection_set_id();
        this.COLLECTION_SET_NAME = o.getCollection_set_name();
        this.DESCRIPTION = o.getDescription();
        this.VERSION_NUMBER = o.getVersion_number();
        this.ENABLED_FLAG = o.getEnabled_flag();
        this.TYPE = o.getType();

        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_collection_sets");
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
  public Meta_collection_sets(RockFactory rockFact, Meta_collection_sets whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_collection_sets o = (Meta_collection_sets) it.next();
        this.COLLECTION_SET_ID = o.getCollection_set_id();
        this.COLLECTION_SET_NAME = o.getCollection_set_name();
        this.DESCRIPTION = o.getDescription();
        this.VERSION_NUMBER = o.getVersion_number();
        this.ENABLED_FLAG = o.getEnabled_flag();
        this.TYPE = o.getType();
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_collection_sets");
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
    return "Meta_collection_sets";
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
  public int updateDB(boolean useTimestamp, Meta_collection_sets whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_collection_sets whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_collection_sets.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

  public Long getCollection_set_id() {
    return this.COLLECTION_SET_ID;
  }

  public String getCollection_set_name() {
    return this.COLLECTION_SET_NAME;
  }

  public String getDescription() {
    return this.DESCRIPTION;
  }

  public String getVersion_number() {
    return this.VERSION_NUMBER;
  }

  public String getEnabled_flag() {
    return this.ENABLED_FLAG;
  }

  public String getType() {
    return this.TYPE;
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
    if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long(0);
    if (COLLECTION_SET_NAME == null)
      COLLECTION_SET_NAME = new String("");
    if (DESCRIPTION == null)
      DESCRIPTION = new String("");
    if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String("");
    if (ENABLED_FLAG == null)
      ENABLED_FLAG = new String("");
    if (TYPE == null)
      TYPE = new String("");
  }

  public void setCollection_set_id(Long COLLECTION_SET_ID) {
    modifiedColumns.add("COLLECTION_SET_ID");
    this.COLLECTION_SET_ID = COLLECTION_SET_ID;
  }

  public void setCollection_set_name(String COLLECTION_SET_NAME) {
    modifiedColumns.add("COLLECTION_SET_NAME");
    this.COLLECTION_SET_NAME = COLLECTION_SET_NAME;
  }

  public void setDescription(String DESCRIPTION) {
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }

  public void setVersion_number(String VERSION_NUMBER) {
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }

  public void setEnabled_flag(String ENABLED_FLAG) {
    modifiedColumns.add("ENABLED_FLAG");
    this.ENABLED_FLAG = ENABLED_FLAG;
  }

  public void setType(String TYPE) {
    modifiedColumns.add("TYPE");
    this.TYPE = TYPE;
  }

  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */

  public boolean equals(Meta_collection_sets o) {

    if ((((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
        || (((this.COLLECTION_SET_NAME == null) || (o.COLLECTION_SET_NAME == null)) && (this.COLLECTION_SET_NAME != o.COLLECTION_SET_NAME))
        || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
        || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
        || (((this.ENABLED_FLAG == null) || (o.ENABLED_FLAG == null)) && (this.ENABLED_FLAG != o.ENABLED_FLAG))
        || (((this.TYPE == null) || (o.TYPE == null)) && (this.TYPE != o.TYPE))) {
      return false;
    } else if ((((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID
        .equals(o.COLLECTION_SET_ID) == false))
        || (((this.COLLECTION_SET_NAME != null) && (o.COLLECTION_SET_NAME != null)) && (this.COLLECTION_SET_NAME
            .equals(o.COLLECTION_SET_NAME) == false))
        || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
        || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER
            .equals(o.VERSION_NUMBER) == false))
        || (((this.ENABLED_FLAG != null) && (o.ENABLED_FLAG != null)) && (this.ENABLED_FLAG.equals(o.ENABLED_FLAG) == false))
        || (((this.TYPE != null) && (o.TYPE != null)) && (this.TYPE.equals(o.TYPE) == false))) {
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
