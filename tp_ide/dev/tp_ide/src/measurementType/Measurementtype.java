package measurementType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ssc.rockfactory.*;

public class Measurementtype implements Cloneable, RockDBObject {

  private String TYPEID;

  private String TYPECLASSID;

  private String TYPENAME;

  private String VENDORID;

  private String FOLDERNAME;

  private String DESCRIPTION;

  private Long STATUS;

  private String VERSIONID;

  private String OBJECTID;

  private String OBJECTNAME;

  private Integer OBJECTVERSION;

  private String OBJECTTYPE;

  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {};

  private String[] primaryKeyNames = { "TYPEID" };

  private RockFactory rockFact;

  private boolean newItem;

  private Set modifiedColumns = new HashSet();

  private static int runningId = 1;

  /**
   * Constructor to initialize all objects to null
   */
  public Measurementtype(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;

    this.TYPEID = "Type id: " + runningId;
    this.TYPECLASSID = "Class id 255";
    this.TYPENAME = "A type name: " + runningId;
    ;
    this.VENDORID = "A vendor id";
    this.FOLDERNAME = "A folder";
    this.DESCRIPTION = "This is the description of this measurement type";
    this.STATUS = new Long(4);
    this.VERSIONID = "Version id";
    this.OBJECTID = "An object id";
    this.OBJECTNAME = "An object name";
    this.OBJECTVERSION = new Integer(5);
    this.OBJECTTYPE = "This type";

    runningId++;
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Measurementtype(RockFactory rockFact, String TYPEID) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;

      this.TYPEID = TYPEID;

      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Measurementtype o = (Measurementtype) it.next();

        this.TYPEID = o.getTypeid();
        this.TYPECLASSID = o.getTypeclassid();
        this.TYPENAME = o.getTypename();
        this.VENDORID = o.getVendorid();
        this.FOLDERNAME = o.getFoldername();
        this.DESCRIPTION = o.getDescription();
        this.STATUS = o.getStatus();
        this.VERSIONID = o.getVersionid();
        this.OBJECTID = o.getObjectid();
        this.OBJECTNAME = o.getObjectname();
        this.OBJECTVERSION = o.getObjectversion();
        this.OBJECTTYPE = o.getObjecttype();

        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementtype");
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
  public Measurementtype(RockFactory rockFact, Measurementtype whereObject) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Measurementtype o = (Measurementtype) it.next();
        this.TYPEID = o.getTypeid();
        this.TYPECLASSID = o.getTypeclassid();
        this.TYPENAME = o.getTypename();
        this.VENDORID = o.getVendorid();
        this.FOLDERNAME = o.getFoldername();
        this.DESCRIPTION = o.getDescription();
        this.STATUS = o.getStatus();
        this.VERSIONID = o.getVersionid();
        this.OBJECTID = o.getObjectid();
        this.OBJECTNAME = o.getObjectname();
        this.OBJECTVERSION = o.getObjectversion();
        this.OBJECTTYPE = o.getObjecttype();
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementtype");
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
    return "Measurementtype";
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
  public int updateDB(boolean useTimestamp, Measurementtype whereObject) throws SQLException, RockException {
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
    System.out.println("Measurementtype deleted!");
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
  public int deleteDB(Measurementtype whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Measurementtype.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

  public String getTypeid() {
    return this.TYPEID;
  }

  public String getTypeclassid() {
    return this.TYPECLASSID;
  }

  public String getTypename() {
    return this.TYPENAME;
  }

  public String getVendorid() {
    return this.VENDORID;
  }

  public String getFoldername() {
    return this.FOLDERNAME;
  }

  public String getDescription() {
    return this.DESCRIPTION;
  }

  public Long getStatus() {
    return this.STATUS;
  }

  public String getVersionid() {
    return this.VERSIONID;
  }

  public String getObjectid() {
    return this.OBJECTID;
  }

  public String getObjectname() {
    return this.OBJECTNAME;
  }

  public Integer getObjectversion() {
    return this.OBJECTVERSION;
  }

  public String getObjecttype() {
    return this.OBJECTTYPE;
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
    if (TYPECLASSID == null)
      TYPECLASSID = new String("");
    if (TYPENAME == null)
      TYPENAME = new String("");
    if (VENDORID == null)
      VENDORID = new String("");
    if (FOLDERNAME == null)
      FOLDERNAME = new String("");
    if (DESCRIPTION == null)
      DESCRIPTION = new String("");
    if (STATUS == null)
      STATUS = new Long(0);
    if (VERSIONID == null)
      VERSIONID = new String("");
    if (OBJECTID == null)
      OBJECTID = new String("");
    if (OBJECTNAME == null)
      OBJECTNAME = new String("");
    if (OBJECTVERSION == null)
      OBJECTVERSION = new Integer(0);
    if (OBJECTTYPE == null)
      OBJECTTYPE = new String("");
  }

  public void setTypeid(String TYPEID) {
    System.out.println("New type id " + TYPEID);
    modifiedColumns.add("TYPEID");
    this.TYPEID = TYPEID;
  }

  public void setTypeclassid(String TYPECLASSID) {
    modifiedColumns.add("TYPECLASSID");
    this.TYPECLASSID = TYPECLASSID;
  }

  public void setTypename(String TYPENAME) {
    modifiedColumns.add("TYPENAME");
    this.TYPENAME = TYPENAME;
  }

  public void setVendorid(String VENDORID) {
    modifiedColumns.add("VENDORID");
    this.VENDORID = VENDORID;
  }

  public void setFoldername(String FOLDERNAME) {
    modifiedColumns.add("FOLDERNAME");
    this.FOLDERNAME = FOLDERNAME;
  }

  public void setDescription(String DESCRIPTION) {
    System.out.println("Description modified, new value: " + DESCRIPTION);
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }

  public void setStatus(Long STATUS) {
    modifiedColumns.add("STATUS");
    this.STATUS = STATUS;
  }

  public void setVersionid(String VERSIONID) {
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }

  public void setObjectid(String OBJECTID) {
    modifiedColumns.add("OBJECTID");
    this.OBJECTID = OBJECTID;
  }

  public void setObjectname(String OBJECTNAME) {
    modifiedColumns.add("OBJECTNAME");
    this.OBJECTNAME = OBJECTNAME;
  }

  public void setObjectversion(Integer OBJECTVERSION) {
    modifiedColumns.add("OBJECTVERSION");
    this.OBJECTVERSION = OBJECTVERSION;
  }

  public void setObjecttype(String OBJECTTYPE) {
    modifiedColumns.add("OBJECTTYPE");
    this.OBJECTTYPE = OBJECTTYPE;
  }

  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */

  public boolean equals(Measurementtype o) {

    if ((((this.TYPEID == null) || (o.TYPEID == null)) && (this.TYPEID != o.TYPEID))
        || (((this.TYPECLASSID == null) || (o.TYPECLASSID == null)) && (this.TYPECLASSID != o.TYPECLASSID))
        || (((this.TYPENAME == null) || (o.TYPENAME == null)) && (this.TYPENAME != o.TYPENAME))
        || (((this.VENDORID == null) || (o.VENDORID == null)) && (this.VENDORID != o.VENDORID))
        || (((this.FOLDERNAME == null) || (o.FOLDERNAME == null)) && (this.FOLDERNAME != o.FOLDERNAME))
        || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
        || (((this.STATUS == null) || (o.STATUS == null)) && (this.STATUS != o.STATUS))
        || (((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
        || (((this.OBJECTID == null) || (o.OBJECTID == null)) && (this.OBJECTID != o.OBJECTID))
        || (((this.OBJECTNAME == null) || (o.OBJECTNAME == null)) && (this.OBJECTNAME != o.OBJECTNAME))
        || (((this.OBJECTVERSION == null) || (o.OBJECTVERSION == null)) && (this.OBJECTVERSION != o.OBJECTVERSION))
        || (((this.OBJECTTYPE == null) || (o.OBJECTTYPE == null)) && (this.OBJECTTYPE != o.OBJECTTYPE))) {
      return false;
    } else if ((((this.TYPEID != null) && (o.TYPEID != null)) && (this.TYPEID.equals(o.TYPEID) == false))
        || (((this.TYPECLASSID != null) && (o.TYPECLASSID != null)) && (this.TYPECLASSID.equals(o.TYPECLASSID) == false))
        || (((this.TYPENAME != null) && (o.TYPENAME != null)) && (this.TYPENAME.equals(o.TYPENAME) == false))
        || (((this.VENDORID != null) && (o.VENDORID != null)) && (this.VENDORID.equals(o.VENDORID) == false))
        || (((this.FOLDERNAME != null) && (o.FOLDERNAME != null)) && (this.FOLDERNAME.equals(o.FOLDERNAME) == false))
        || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
        || (((this.STATUS != null) && (o.STATUS != null)) && (this.STATUS.equals(o.STATUS) == false))
        || (((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
        || (((this.OBJECTID != null) && (o.OBJECTID != null)) && (this.OBJECTID.equals(o.OBJECTID) == false))
        || (((this.OBJECTNAME != null) && (o.OBJECTNAME != null)) && (this.OBJECTNAME.equals(o.OBJECTNAME) == false))
        || (((this.OBJECTVERSION != null) && (o.OBJECTVERSION != null)) && (this.OBJECTVERSION.equals(o.OBJECTVERSION) == false))
        || (((this.OBJECTTYPE != null) && (o.OBJECTTYPE != null)) && (this.OBJECTTYPE.equals(o.OBJECTTYPE) == false))) {
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
