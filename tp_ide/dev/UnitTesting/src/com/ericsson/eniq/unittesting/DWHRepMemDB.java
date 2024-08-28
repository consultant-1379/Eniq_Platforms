package com.ericsson.eniq.unittesting;

public class DWHRepMemDB extends UnitTestDatabase
{
//	 DWH Repository Tables Array
	private String[] dwhRepTableNames = {"MEASUREMENTKEY", "AGGREGATIONRULE", "AGGREGATION", "MEASUREMENTCOLUMN", "MEASUREMENTCOUNTER", "MEASUREMENTTABLE", "MEASUREMENTTYPE",
            "MEASUREMENTTYPECLASS", "REFERENCECOLUMN", "REFERENCETABLE", "EXTERNALSTATEMENT", "DEFAULTTAGS", "INTERFACEMEASUREMENT", "DATAITEM",
            "DATAFORMAT", "TRANSFORMATION", "TRANSFORMER", "PROMPT", "PROMPTOPTION", "PROMPTIMPLEMENTOR", "VERSIONING", "INTERFACETECHPACKS",
            "DATAINTERFACE", "EXTERNALSTATEMENTSTATUS", "DWHCOLUMN", "ALARMREPORTPARAMETER", "ALARMREPORT", "ALARMINTERFACE", "DWHPARTITION",
            "DWHTYPE", "DWHTECHPACKS", "TYPEACTIVATION", "TPACTIVATION", "PARTITIONPLAN", "INFOMESSAGES", "CONFIGURATION"};
	
	
	public DWHRepMemDB()
	{
		super("dwhrep");
	}
	
	
	/**
	 * This returns an array with the names of the DWH Repository Table Names
	 * @return DWH Repository Table Names
	 */
	public String[] getDwhRepTableNames()
	{
		return this.dwhRepTableNames;
	}
	
	
	
}