package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for ETLCImport.
 * @author eciacah
 *
 */
public class ETLCImportTest {	
	
	protected Mockery context = new Mockery();
	{
		context.setImposteriser(ClassImposteriser.INSTANCE);
	}
	
	private ETLCImport etlcImport;
	
	Statement mockStatement;
	ResultSet mockRSet;

	@Before
	public void setUp() throws Exception {
		mockStatement = context.mock(Statement.class);
		mockRSet = context.mock(ResultSet.class);
		
		etlcImport = new ETLCImport() {
			protected Statement createStatement() throws SQLException {
				return mockStatement;
			}
			
			/** Override pauseBeforeREtry to do nothing **/
			protected void pauseBeforeRetry() {
				// Do nothing
			}
		};
		
	}

	@After
	public void tearDown() throws Exception {
		context.assertIsSatisfied();
	}

	@Test
	public void testReOrder() throws SQLException {
		final String tableName = "tableName";
		final String colName = "colName";
		final String colValueFromXML = "1000";
		final int currentIDValueFromDb = 1;
		
		context.checking(new Expectations() {
		      {
		        oneOf(mockStatement).executeQuery("select max(colName) as colName from tableName");
		        will(returnValue(mockRSet));
		        
		        oneOf(mockRSet).next();
		        will(returnValue(true));
		        
		        oneOf(mockRSet).getInt(colName);
		        will(returnValue(currentIDValueFromDb));
		        
		        oneOf(mockRSet).close();		        		        
		        oneOf(mockStatement).close();	        
		      }
		    });
				
		final String maxValue = etlcImport.reOrder(tableName, colName, colValueFromXML);
		assertTrue("Max value returned should be ID value from database plus one", maxValue.equalsIgnoreCase(Integer.toString(2)));
		assertFalse("Max value returned should not be the XML value", maxValue.equalsIgnoreCase(colValueFromXML));
	}
	
	@Test
	public void testReOrderNothingInResultSet() throws SQLException {
		final String tableName = "tableName";
		final String colName = "colName";
		final String colValueFromXML = "12345";
		
		context.checking(new Expectations() {
		      {
		        oneOf(mockStatement).executeQuery("select max(colName) as colName from tableName");
		        will(returnValue(mockRSet));
		        
		        oneOf(mockRSet).next();
		        will(returnValue(false));
		        
		        oneOf(mockRSet).close();	
		        oneOf(mockStatement).close();        
		      }
		    });
				
		final String maxValue = etlcImport.reOrder(tableName, colName, colValueFromXML);
		assertTrue("Max value returned for ID should be 0 if result set is empty", maxValue.equalsIgnoreCase("0"));
	}
	
	@Test
	public void testReOrderResultSetThrowsException() throws SQLException {
		final String tableName = "tableName";
		final String colName = "colName";
		final String colValueFromXML = "999";
		
		context.checking(new Expectations() {
			{
				exactly(3).of(mockStatement).executeQuery("select max(colName) as colName from tableName");
				will(returnValue(mockRSet));

				exactly(3).of(mockRSet).next();
				will(throwException(new SQLException("Error occurred calling next() on ResultSet")));

				exactly(3).of(mockRSet).close();
				exactly(3).of(mockStatement).close();
			}
		});				
				
		final String maxValue = etlcImport.reOrder(tableName, colName, colValueFromXML);
		assertTrue("Max value returned should be XML value if checking result set throws an error", maxValue.equalsIgnoreCase(colValueFromXML));
	}
	
	@Test
	public void testReOrderResultSetExceptionThenRecovers() throws SQLException {
		final String tableName = "tableName";
		final String colName = "colName";
		final String colValueFromXML = "999";
		final int currentIDValueFromDb = 1;
		
		context.checking(new Expectations() {
			{
				exactly(3).of(mockStatement).executeQuery("select max(colName) as colName from tableName");
				will(returnValue(mockRSet));
				
				// Getting the result set throws an exception twice:
				exactly(2).of(mockRSet).next();
				will(throwException(new SQLException("Error occurred calling next() on ResultSet")));
				
				// On the third time it works ok:
				oneOf(mockRSet).next();
				will(returnValue(true));
				
				oneOf(mockRSet).getInt(colName);
				will(returnValue(currentIDValueFromDb));

				exactly(3).of(mockRSet).close();
				exactly(3).of(mockStatement).close();
			}
		});				
				
		final String maxValue = etlcImport.reOrder(tableName, colName, colValueFromXML);
		assertTrue("Max value returned should be ID value from database plus one", maxValue.equalsIgnoreCase(Integer.toString(2)));
		assertFalse("Max value returned should not be the XML value", maxValue.equalsIgnoreCase(colValueFromXML));
	}
	
	@Test
	public void testReOrderExecuteQueryThrowsException() throws SQLException {
		final String tableName = "tableName";
		final String colName = "colName";
		final String colValueFromXML = "1";		
		
		context.checking(new Expectations() {
			{
				exactly(3).of(mockStatement).executeQuery("select max(colName) as colName from tableName");
				will(throwException(new SQLException("Error occurred calling executeQuery()")));				
				
				exactly(3).of(mockStatement).close();
			}
		});
				
		final String maxValue = etlcImport.reOrder(tableName, colName, colValueFromXML);
		assertTrue("Max value returned should be XML value if executeQuery() throws an error", maxValue.equalsIgnoreCase(colValueFromXML));
	}
	
	@Test
	public void testReOrderExecuteQueryExceptionThenRecovers() throws SQLException {
		final String tableName = "tableName";
		final String colName = "colName";
		final String colValueFromXML = "1";
		final int currentIDValueFromDb = 1;
		
		context.checking(new Expectations() {
			{
				// executeQuery() fails twice:
				exactly(2).of(mockStatement).executeQuery("select max(colName) as colName from tableName");
				will(throwException(new SQLException("Error occurred calling executeQuery()")));			
								
				// Then executeQuery() works ok:
				oneOf(mockStatement).executeQuery("select max(colName) as colName from tableName");
				will(returnValue(mockRSet));
				
				oneOf(mockRSet).next();
				will(returnValue(true));
				
				// We expect the correct value to be returned as per usual:
				oneOf(mockRSet).getInt(colName);
				will(returnValue(currentIDValueFromDb));
								
				exactly(3).of(mockStatement).close();
				oneOf(mockRSet).close();
			}
		});
				
		final String maxValue = etlcImport.reOrder(tableName, colName, colValueFromXML);
		assertTrue("Max value returned should be ID value from database plus one", maxValue.equalsIgnoreCase(Integer.toString(2)));
		assertFalse("Max value returned should not be the XML value", maxValue.equalsIgnoreCase(colValueFromXML));
	}

}
