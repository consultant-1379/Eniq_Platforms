package ssc.rockfactory.stubs;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_databases;

/**
 * A stub class for overriding the RockFactory's method getData(). In this stub 
 * class, getData() method creates one Meta_collection_sets object with fixed
 * properties, instead of creating data based on an actual SQL query.
 * 
 * More overriding methods may be added to this class, in the case more of 
 * RockFactory's functionality needs to be disabled for unit testing other 
 * classes.
 * 
 * @author epiituo
 */
/**
 * @author epiituo
 *
 */
public class RockFactoryStub extends RockFactory {

	String dbURL;
	String dbType;
	String strUserName;
	String strPassword;
	String driverName;
	String conName;
	
	/**
	 * @param dbURL
	 * @param dbType
	 * @param strUserName
	 * @param strPassword
	 * @param driverName
	 * @param conName
	 * @param autoCommit
	 * @throws SQLException
	 * @throws RockException
	 */
	public RockFactoryStub(String dbURL, String dbType, String strUserName,
			String strPassword, String driverName, String conName,
			boolean autoCommit) throws SQLException, RockException {
		
		super(dbURL, strUserName, strPassword, driverName, conName, autoCommit);
	
		this.dbURL = dbURL;
		this.strUserName = strUserName;
		this.strPassword = strPassword;
		this.driverName = driverName;
		this.conName = conName;
		
		
		
	}
	
	/**
	 * This method overrides RockFactory.getData(Object, RockResultSet) with a
	 * stub implementation.
	 *  
	 * (non-Javadoc)
	 * @see ssc.rockfactory.RockFactory#getData(java.lang.Object, ssc.rockfactory.RockResultSet)
	 */
	@Override
	public Iterator getData(Object dataObj, RockResultSet rockResults)
			throws SQLException, RockException {
		
		Vector<Object> resultVector = new Vector<Object>();
		
		if (dataObj instanceof Meta_databases){
			
			Meta_databases m = new Meta_databases(this);
			m.setConnection_name("dwh");
			m.setDriver_name(this.driverName);
			m.setPassword(this.strPassword);
			m.setUsername(this.strUserName);
			m.setConnection_string(this.dbURL);
			
			m.setDriver_name("com.ericsson.eniq.teststubs.DriverStub");
			resultVector.addElement(m);
			
			m = new Meta_databases(this);
			m.setConnection_name("dwhrep");
			m.setDriver_name(this.driverName);
			m.setPassword(this.strPassword);
			m.setUsername(this.strUserName);
			m.setConnection_string(this.dbURL);
			m.setDriver_name("com.ericsson.eniq.teststubs.DriverStub");
			resultVector.addElement(m);
			
			m = new Meta_databases(this);
			m.setConnection_name("etlrep");
			m.setDriver_name(this.driverName);
			m.setPassword(this.strPassword);
			m.setUsername(this.strUserName);
			m.setConnection_string(this.dbURL);
			m.setDriver_name("com.ericsson.eniq.teststubs.DriverStub");
			resultVector.addElement(m);
			
		} else {
		
		

		
		Meta_collection_sets resultData = new Meta_collection_sets(this);
		resultData.setCollection_set_id(new Long(1));
		resultData.setCollection_set_name("Collection set name");
		resultData.setDescription("Description");
		resultData.setVersion_number("1.0");
		resultData.setEnabled_flag("Y");
		resultData.setType("Type");
		
		resultVector.addElement(resultData);
		
		}
		
		return resultVector.iterator();
	}
	
}
