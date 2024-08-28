package measurementType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ssc.rockfactory.*;

public class Measurementcolumn implements Cloneable, RockDBObject {

  private String MTABLEID;

  private String DATANAME;

  private Long COLNUMBER;

  private String DATATYPE;

  private Integer DATASIZE;

  private Integer DATASCALE;

  private Long UNIQUEVALUE;

  private Integer NULLABLE;

  private String INDEXES;

  private String DESCRIPTION;

  private String DATAID;

  private String RELEASEID;

  private Integer UNIQUEKEY;

  private Integer INCLUDESQL;

  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {};

  private String[] primaryKeyNames = { "MTABLEID", "DATANAME" };

  private RockFactory rockFact;

  private boolean newItem;

  private static int runningId = 0;

  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Measurementcolumn(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;

    this.MTABLEID = "MTableId: " + runningId;
    this.DATANAME = "DB Name: " + runningId;
    this.COLNUMBER = new Long(3);
    this.DATATYPE = "DataType";
    this.DATASIZE = new Integer(42);
    this.DATASCALE = new Integer(2);
    this.UNIQUEVALUE = new Long(11);
    this.NULLABLE = new Integer(33);
    this.INDEXES = "Indexes";
    this.DESCRIPTION = "Description";
    this.DATAID = "DataId";
    this.RELEASEID = "ReleaseId";
    this.UNIQUEKEY = new Integer(21);
    this.INCLUDESQL = new Integer(787);

    runningId++;
    // this.MTABLEID = null;
    // this.DATANAME = null;
    // this.COLNUMBER = null;
    // this.DATATYPE = null;
    // this.DATASIZE = null;
    // this.DATASCALE = null;
    // this.UNIQUEVALUE = null;
    // this.NULLABLE = null;
    // this.INDEXES = null;
    // this.DESCRIPTION = null;
    // this.DATAID = null;
    // this.RELEASEID = null;
    // this.UNIQUEKEY = null;
    // this.INCLUDESQL = null;

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Measurementcolumn(RockFactory rockFact, String MTABLEID, String DATANAME) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;

      this.MTABLEID = MTABLEID;
      this.DATANAME = DATANAME;

      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Measurementcolumn o = (Measurementcolumn) it.next();

        this.MTABLEID = o.getMtableid();
        this.DATANAME = o.getDataname();
        this.COLNUMBER = o.getColnumber();
        this.DATATYPE = o.getDatatype();
        this.DATASIZE = o.getDatasize();
        this.DATASCALE = o.getDatascale();
        this.UNIQUEVALUE = o.getUniquevalue();
        this.NULLABLE = o.getNullable();
        this.INDEXES = o.getIndexes();
        this.DESCRIPTION = o.getDescription();
        this.DATAID = o.getDataid();
        this.RELEASEID = o.getReleaseid();
        this.UNIQUEKEY = o.getUniquekey();
        this.INCLUDESQL = o.getIncludesql();

        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementcolumn");
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
  public Measurementcolumn(RockFactory rockFact, Measurementcolumn whereObject) throws SQLException, RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Measurementcolumn o = (Measurementcolumn) it.next();
        this.MTABLEID = o.getMtableid();
        this.DATANAME = o.getDataname();
        this.COLNUMBER = o.getColnumber();
        this.DATATYPE = o.getDatatype();
        this.DATASIZE = o.getDatasize();
        this.DATASCALE = o.getDatascale();
        this.UNIQUEVALUE = o.getUniquevalue();
        this.NULLABLE = o.getNullable();
        this.INDEXES = o.getIndexes();
        this.DESCRIPTION = o.getDescription();
        this.DATAID = o.getDataid();
        this.RELEASEID = o.getReleaseid();
        this.UNIQUEKEY = o.getUniquekey();
        this.INCLUDESQL = o.getIncludesql();
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Measurementcolumn");
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
    return "Measurementcolumn";
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
  public int updateDB(boolean useTimestamp, Measurementcolumn whereObject) throws SQLException, RockException {
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
  public int deleteDB(Measurementcolumn whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Measurementcolumn.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

  public String getMtableid() {
    return this.MTABLEID;
  }

  public String getDataname() {
    return this.DATANAME;
  }

  public Long getColnumber() {
    return this.COLNUMBER;
  }

  public String getDatatype() {
    return this.DATATYPE;
  }

  public Integer getDatasize() {
    return this.DATASIZE;
  }

  public Integer getDatascale() {
    return this.DATASCALE;
  }

  public Long getUniquevalue() {
    return this.UNIQUEVALUE;
  }

  public Integer getNullable() {
    return this.NULLABLE;
  }

  public String getIndexes() {
    return this.INDEXES;
  }

  public String getDescription() {
    return this.DESCRIPTION;
  }

  public String getDataid() {
    return this.DATAID;
  }

  public String getReleaseid() {
    return this.RELEASEID;
  }

  public Integer getUniquekey() {
    return this.UNIQUEKEY;
  }

  public Integer getIncludesql() {
    return this.INCLUDESQL;
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
    if (DATANAME == null)
      DATANAME = new String("");
    if (COLNUMBER == null)
      COLNUMBER = new Long(0);
    if (DATATYPE == null)
      DATATYPE = new String("");
    if (DATASIZE == null)
      DATASIZE = new Integer(0);
    if (DATASCALE == null)
      DATASCALE = new Integer(0);
    if (UNIQUEVALUE == null)
      UNIQUEVALUE = new Long(0);
    if (NULLABLE == null)
      NULLABLE = new Integer(0);
    if (INDEXES == null)
      INDEXES = new String("");
    if (DESCRIPTION == null)
      DESCRIPTION = new String("");
    if (DATAID == null)
      DATAID = new String("");
    if (RELEASEID == null)
      RELEASEID = new String("");
    if (UNIQUEKEY == null)
      UNIQUEKEY = new Integer(0);
    if (INCLUDESQL == null)
      INCLUDESQL = new Integer(0);
  }

  public void setMtableid(String MTABLEID) {
    modifiedColumns.add("MTABLEID");
    this.MTABLEID = MTABLEID;
  }

  public void setDataname(String DATANAME) {
    modifiedColumns.add("DATANAME");
    this.DATANAME = DATANAME;
  }

  public void setColnumber(Long COLNUMBER) {
    modifiedColumns.add("COLNUMBER");
    this.COLNUMBER = COLNUMBER;
  }

  public void setDatatype(String DATATYPE) {
    modifiedColumns.add("DATATYPE");
    this.DATATYPE = DATATYPE;
  }

  public void setDatasize(Integer DATASIZE) {
    modifiedColumns.add("DATASIZE");
    this.DATASIZE = DATASIZE;
  }

  public void setDatascale(Integer DATASCALE) {
    modifiedColumns.add("DATASCALE");
    this.DATASCALE = DATASCALE;
  }

  public void setUniquevalue(Long UNIQUEVALUE) {
    modifiedColumns.add("UNIQUEVALUE");
    this.UNIQUEVALUE = UNIQUEVALUE;
  }

  public void setNullable(Integer NULLABLE) {
    modifiedColumns.add("NULLABLE");
    this.NULLABLE = NULLABLE;
  }

  public void setIndexes(String INDEXES) {
    modifiedColumns.add("INDEXES");
    this.INDEXES = INDEXES;
  }

  public void setDescription(String DESCRIPTION) {
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }

  public void setDataid(String DATAID) {
    modifiedColumns.add("DATAID");
    this.DATAID = DATAID;
  }

  public void setReleaseid(String RELEASEID) {
    modifiedColumns.add("RELEASEID");
    this.RELEASEID = RELEASEID;
  }

  public void setUniquekey(Integer UNIQUEKEY) {
    modifiedColumns.add("UNIQUEKEY");
    this.UNIQUEKEY = UNIQUEKEY;
  }

  public void setIncludesql(Integer INCLUDESQL) {
    modifiedColumns.add("INCLUDESQL");
    this.INCLUDESQL = INCLUDESQL;
  }

  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs
   * objects field values are equal.
   */

  public boolean equals(Measurementcolumn o) {

    if ((((this.MTABLEID == null) || (o.MTABLEID == null)) && (this.MTABLEID != o.MTABLEID))
        || (((this.DATANAME == null) || (o.DATANAME == null)) && (this.DATANAME != o.DATANAME))
        || (((this.COLNUMBER == null) || (o.COLNUMBER == null)) && (this.COLNUMBER != o.COLNUMBER))
        || (((this.DATATYPE == null) || (o.DATATYPE == null)) && (this.DATATYPE != o.DATATYPE))
        || (((this.DATASIZE == null) || (o.DATASIZE == null)) && (this.DATASIZE != o.DATASIZE))
        || (((this.DATASCALE == null) || (o.DATASCALE == null)) && (this.DATASCALE != o.DATASCALE))
        || (((this.UNIQUEVALUE == null) || (o.UNIQUEVALUE == null)) && (this.UNIQUEVALUE != o.UNIQUEVALUE))
        || (((this.NULLABLE == null) || (o.NULLABLE == null)) && (this.NULLABLE != o.NULLABLE))
        || (((this.INDEXES == null) || (o.INDEXES == null)) && (this.INDEXES != o.INDEXES))
        || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
        || (((this.DATAID == null) || (o.DATAID == null)) && (this.DATAID != o.DATAID))
        || (((this.RELEASEID == null) || (o.RELEASEID == null)) && (this.RELEASEID != o.RELEASEID))
        || (((this.UNIQUEKEY == null) || (o.UNIQUEKEY == null)) && (this.UNIQUEKEY != o.UNIQUEKEY))
        || (((this.INCLUDESQL == null) || (o.INCLUDESQL == null)) && (this.INCLUDESQL != o.INCLUDESQL))) {
      return false;
    } else if ((((this.MTABLEID != null) && (o.MTABLEID != null)) && (this.MTABLEID.equals(o.MTABLEID) == false))
        || (((this.DATANAME != null) && (o.DATANAME != null)) && (this.DATANAME.equals(o.DATANAME) == false))
        || (((this.COLNUMBER != null) && (o.COLNUMBER != null)) && (this.COLNUMBER.equals(o.COLNUMBER) == false))
        || (((this.DATATYPE != null) && (o.DATATYPE != null)) && (this.DATATYPE.equals(o.DATATYPE) == false))
        || (((this.DATASIZE != null) && (o.DATASIZE != null)) && (this.DATASIZE.equals(o.DATASIZE) == false))
        || (((this.DATASCALE != null) && (o.DATASCALE != null)) && (this.DATASCALE.equals(o.DATASCALE) == false))
        || (((this.UNIQUEVALUE != null) && (o.UNIQUEVALUE != null)) && (this.UNIQUEVALUE.equals(o.UNIQUEVALUE) == false))
        || (((this.NULLABLE != null) && (o.NULLABLE != null)) && (this.NULLABLE.equals(o.NULLABLE) == false))
        || (((this.INDEXES != null) && (o.INDEXES != null)) && (this.INDEXES.equals(o.INDEXES) == false))
        || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
        || (((this.DATAID != null) && (o.DATAID != null)) && (this.DATAID.equals(o.DATAID) == false))
        || (((this.RELEASEID != null) && (o.RELEASEID != null)) && (this.RELEASEID.equals(o.RELEASEID) == false))
        || (((this.UNIQUEKEY != null) && (o.UNIQUEKEY != null)) && (this.UNIQUEKEY.equals(o.UNIQUEKEY) == false))
        || (((this.INCLUDESQL != null) && (o.INCLUDESQL != null)) && (this.INCLUDESQL.equals(o.INCLUDESQL) == false))) {
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
