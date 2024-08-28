package com.ericsson.eniq.teststubs;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A stub implementation for java.sql.Driver interface. An instance of this 
 * class is used by the RockFactoryStub to simulate database driver.
 * 
 * @author epiituo
 */
public class DriverStub implements Driver {

	private Connection connection;
	
	public DriverStub() {
		this.connection = new ConnectionStub(); 
	}
	
	
	public boolean acceptsURL(String url) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Connection connect(String url, Properties info) throws SQLException {
		return this.connection;
	}

	
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean jdbcCompliant() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
