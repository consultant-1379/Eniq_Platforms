package com.ericsson.eniq.unittesting;

public class PhysicalDatabase extends UnitTestDatabase
{	
	public PhysicalDatabase(String driver, String databaseType, String databaseName, String url, String portNumber, String username, String password )
	{
		super(driver, databaseType, databaseName, url, portNumber, username, password);		
	}
}
