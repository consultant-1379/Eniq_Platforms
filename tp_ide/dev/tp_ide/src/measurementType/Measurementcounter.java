package measurementType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ssc.rockfactory.*;

public class Measurementcounter implements Cloneable, RockDBObject {

    private String TYPEID;
    private String DATANAME;
    private String DESCRIPTION;
    private String TIMEAGGREGATION;
    private String GROUPAGGREGATION;
    private String COUNTAGGREGATION;
    private Integer YESNOVALUE;

    private String timeStampName = "LAST_UPDATED";

    private String[] columnsAndSequences = {};

    private String[] primaryKeyNames = { "TYPEID", "DATANAME" };

    private RockFactory rockFact;

    private boolean newItem;

    private Set modifiedColumns = new HashSet();

    private static int runningId = 0;

    /**
     * Constructor to initialize all objects to null
     */
    public Measurementcounter(RockFactory rockFact) {
	this.rockFact = rockFact;
	this.newItem = true;

	this.TYPEID = "Type Id: " + runningId;
	this.DATANAME = "DataName: " + runningId;
	this.DESCRIPTION = "Description";
	this.TIMEAGGREGATION = "TimeAggregation";
	this.GROUPAGGREGATION = "GroupAggregation";
	this.COUNTAGGREGATION = "CounterAggregation";
	this.YESNOVALUE = new Integer(runningId);

	runningId++;

	// this.TYPEID = null;
	// this.DATANAME = null;
	// this.DESCRIPTION = null;
	// this.TIMEAGGREGATION = null;
	// this.GROUPAGGREGATION = null;
	// this.COUNTAGGREGATION = null;
	//    
    }

    /**
     * Constructor for primary selection from database PRIMARY KEY MUST BE
     * DEFINED
     * 
     * @params primarykeys
     * @exception SQLException
     */
    public Measurementcounter(RockFactory rockFact, String TYPEID,
	    String DATANAME) throws SQLException, RockException {
	try {
	    this.rockFact = rockFact;

	    this.TYPEID = TYPEID;
	    this.DATANAME = DATANAME;

	    RockResultSet results = rockFact.setSelectSQL(true, this);
	    Iterator it = rockFact.getData(this, results);
	    if (it.hasNext()) {
		Measurementcounter o = (Measurementcounter) it.next();

		this.TYPEID = o.getTypeid();
		this.DATANAME = o.getDataname();
		this.DESCRIPTION = o.getDescription();
		this.TIMEAGGREGATION = o.getTimeaggregation();
		this.GROUPAGGREGATION = o.getGroupaggregation();
		this.COUNTAGGREGATION = o.getCountaggregation();
		this.YESNOVALUE = o.getYesNoValue();

		results.close();
		this.newItem = false;
	    } else {
		results.close();
		throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA
			+ "Measurementcounter");
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
     *                the where part is constructed from this object
     * @exception SQLException
     */
    public Measurementcounter(RockFactory rockFact,
	    Measurementcounter whereObject) throws SQLException, RockException {
	try {
	    this.rockFact = rockFact;
	    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
	    Iterator it = rockFact.getData(whereObject, results);
	    if (it.hasNext()) {
		Measurementcounter o = (Measurementcounter) it.next();
		this.TYPEID = o.getTypeid();
		this.DATANAME = o.getDataname();
		this.DESCRIPTION = o.getDescription();
		this.TIMEAGGREGATION = o.getTimeaggregation();
		this.GROUPAGGREGATION = o.getGroupaggregation();
		this.COUNTAGGREGATION = o.getCountaggregation();
		this.YESNOVALUE = o.getYesNoValue();
		results.close();
		this.newItem = false;
	    } else {
		results.close();
		throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA
			+ "Measurementcounter");
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
	return "Measurementcounter";
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
     * @param boolean
     *                useTimestamp if false, the timestamp is not used to
     *                compare if the data has been changed during the
     *                transaction
     * @exception SQLException
     */
    public int updateDB(boolean useTimestamp) throws SQLException,
	    RockException {
	this.newItem = false;
	return rockFact.updateData(this, true, null, useTimestamp);
    }

    /**
     * Update object contents into database NO PRIMARY KEY DEFINED
     * 
     * @param boolean
     *                useTimestamp if false, the timestamp is not used to
     *                compare if the data has been changed during the
     *                transaction
     * @param <this
     *                object type> whereObject the where part is constructed
     *                from this object
     * @exception SQLException
     */
    public int updateDB(boolean useTimestamp, Measurementcounter whereObject)
	    throws SQLException, RockException {
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
     * @param boolean
     *                useTimestamp if false, the timestamp is not used to
     *                compare if the data has been changed during the
     *                transaction
     * @param boolean
     *                useSequence if false the sequence columns don't get their
     *                values from db sequences
     * @exception SQLException
     */
    public int insertDB(boolean useTimestamp, boolean useSequence)
	    throws SQLException, RockException {
	this.newItem = false;
	return rockFact.insertData(this, useTimestamp, useSequence);
    }

    /**
     * Delete object contents from database PRIMARY KEY MUST BE DEFINED
     * 
     * @exception SQLException
     */
    public int deleteDB() throws SQLException, RockException {
	System.out.println("Measurementcounter deleted");
	this.newItem = true;
	return rockFact.deleteData(true, this);
    }

    /**
     * Delete object contents from database NO PRIMARY KEY DEFINED
     * 
     * @param <this
     *                object type> whereObject the where part is constructed
     *                from this object
     * @exception SQLException
     */
    public int deleteDB(Measurementcounter whereObject) throws SQLException,
	    RockException {
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
		throw new RockException(
			"Cannot use rock.Measurementcounter.saveDB(), no primary key defined");
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

    public String getTimeaggregation() {
	return this.TIMEAGGREGATION;
    }

    public Integer getYesNoValue() {
	return this.YESNOVALUE;
    }

    public String getGroupaggregation() {
	return this.GROUPAGGREGATION;
    }

    public String getCountaggregation() {
	return this.COUNTAGGREGATION;
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
	if (TIMEAGGREGATION == null)
	    TIMEAGGREGATION = new String("");
	if (GROUPAGGREGATION == null)
	    GROUPAGGREGATION = new String("");
	if (COUNTAGGREGATION == null)
	    COUNTAGGREGATION = new String("");
	if (YESNOVALUE == null)
	    YESNOVALUE = new Integer(0);
    }

    public void setTypeid(String TYPEID) {
	modifiedColumns.add("TYPEID");
	this.TYPEID = TYPEID;
    }

    public void setDataname(String DATANAME) {
	modifiedColumns.add("DATANAME");
	this.DATANAME = DATANAME;
    }

    public void setDescription(String DESCRIPTION) {
	System.out.println("Modifying Description from " + this.DESCRIPTION
		+ " to " + DESCRIPTION);
	modifiedColumns.add("DESCRIPTION");
	this.DESCRIPTION = DESCRIPTION;
    }

    public void setTimeaggregation(String TIMEAGGREGATION) {
	modifiedColumns.add("TIMEAGGREGATION");
	this.TIMEAGGREGATION = TIMEAGGREGATION;
    }

    public void setGroupaggregation(String GROUPAGGREGATION) {
	modifiedColumns.add("GROUPAGGREGATION");
	this.GROUPAGGREGATION = GROUPAGGREGATION;
    }

    public void setCountaggregation(String COUNTAGGREGATION) {
	modifiedColumns.add("COUNTAGGREGATION");
	this.COUNTAGGREGATION = COUNTAGGREGATION;
    }

    public void setYesNoValue(Integer YESNOVALUE) {
	modifiedColumns.add("YESNOVALUE");
	System.out.println("Modifying YesNoValue from " + this.YESNOVALUE
		+ " to " + YESNOVALUE);
	this.YESNOVALUE = YESNOVALUE;
    }

    public void setcolumnsAndSequences(String[] newColsAndSeqs) {
	this.columnsAndSequences = newColsAndSeqs;
    }

    /**
     * equals method test wheather the objects field values and and the
     * parametrs objects field values are equal.
     */

    public boolean equals(Measurementcounter o) {

	if ((((this.TYPEID == null) || (o.TYPEID == null)) && (this.TYPEID != o.TYPEID))
		|| (((this.DATANAME == null) || (o.DATANAME == null)) && (this.DATANAME != o.DATANAME))
		|| (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
		|| (((this.TIMEAGGREGATION == null) || (o.TIMEAGGREGATION == null)) && (this.TIMEAGGREGATION != o.TIMEAGGREGATION))
		|| (((this.GROUPAGGREGATION == null) || (o.GROUPAGGREGATION == null)) && (this.GROUPAGGREGATION != o.GROUPAGGREGATION))
		|| (((this.COUNTAGGREGATION == null) || (o.COUNTAGGREGATION == null)) && (this.COUNTAGGREGATION != o.COUNTAGGREGATION))
		|| (((this.YESNOVALUE == null) || (o.YESNOVALUE == null)) && (this.YESNOVALUE != o.YESNOVALUE))) {
	    return false;
	} else if ((((this.TYPEID != null) && (o.TYPEID != null)) && (this.TYPEID
		.equals(o.TYPEID) == false))
		|| (((this.DATANAME != null) && (o.DATANAME != null)) && (this.DATANAME
			.equals(o.DATANAME) == false))
		|| (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION
			.equals(o.DESCRIPTION) == false))
		|| (((this.TIMEAGGREGATION != null) && (o.TIMEAGGREGATION != null)) && (this.TIMEAGGREGATION
			.equals(o.TIMEAGGREGATION) == false))
		|| (((this.GROUPAGGREGATION != null) && (o.GROUPAGGREGATION != null)) && (this.GROUPAGGREGATION
			.equals(o.GROUPAGGREGATION) == false))
		|| (((this.COUNTAGGREGATION != null) && (o.COUNTAGGREGATION != null)) && (this.COUNTAGGREGATION
			.equals(o.COUNTAGGREGATION) == false))
		|| (((this.YESNOVALUE != null) && (o.YESNOVALUE != null)) && (this.YESNOVALUE
			.equals(o.YESNOVALUE) == false))

	) {
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
