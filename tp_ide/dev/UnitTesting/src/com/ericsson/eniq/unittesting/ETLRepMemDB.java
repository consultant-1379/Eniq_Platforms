package com.ericsson.eniq.unittesting;

public class ETLRepMemDB extends UnitTestDatabase 
{
//	 ETL Repository Tables Array	
	private String[] etlRepTableNames = {"META_COLLECTION_SETS", "META_COLLECTIONS", "META_CONNECTION_TYPES", "META_DATABASES", "META_EXECUTION_SLOT", "META_EXECUTION_SLOT_PROFILE",
			"META_SCHEDULINGS", "META_SERVERS", "META_TRANSFER_ACTIONS", "META_TRANSFER_BATCHES", "META_VERSIONS" };
    
	
	public ETLRepMemDB ()
	{
		super("etlrep");
	}
	
	
	/**
	 * This returns an array with the names of the ETL Repository Table Names
	 * @return ETL Repository Table Names
	 */
	public String[] getEtlRepTableNames()
	{
		return this.etlRepTableNames;
	}
	
}
