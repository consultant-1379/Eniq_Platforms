package com.ericsson.eniq.unittesting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Arrays;
import java.util.Vector;

import com.ericsson.eniq.common.Constants;
import com.ericsson.eniq.repository.ETLCServerProperties;

public class RetrieveDatabaseInformation
{
	private Connection conn;
		
	private String testDir = "test/UnitTesting/SQLFiles/";
	private String tempDir = "temp/SQLFiles/";
	private String database; // i.e. Dataware House, DWH Repository, ETL Repository
	private String username;
	private String createTableFile;
	private String foreignKeyFile;
	private String insertFile;
	private String dropTableFile;
	private String sysSchema = testDir + "createSysSchema_dwh.sql";
	private String dwhViews = testDir + "createViews_dwh.sql";
	private static  String sybase_db_driver = "";
	
	private PhysicalDatabase sybase;
	
	/**
	 * 
	 * @param serverName
	 * @param portNumber
	 * @param username
	 * @param password
	 * @param eniqDatabase
	 */
	public RetrieveDatabaseInformation(String serverName, String portNumber, String username, String password, String eniqDatabase)
	{		
		getConnection(serverName, portNumber, username, password);
		this.database = eniqDatabase;
		this.username = username;
	}
	
	
	/**
	 * This method needs a few arguments to set up the files correctly.
	 * @param args
	 * server name			= args[0]
	 * portNumber 			= args[1]
	 * username				= args[2]
	 * password				= args[3]
	 * eniqDatabase			= args[4] (etlrep, dwhrep, dwh)
	 * 
	 * e.g. connecting to:
	 * 	jdbc:sybase:Tds:eniq16:2641
	 * 	username: etlrep
	 * 	password: etlrep
	 * 	Eniq Datbase type: ETL Repository
	 * 
	 * 	java RetrieveDatabaseInformation eniq16 2641 etlrep etlrep etlrep
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		
	    String propertiesFile;  
		String conf_dir  = "/eniq/sw/conf";
		           
		if (!conf_dir.endsWith(File.separator)) 
		{                      
		conf_dir += File.separator;  
		}   
		propertiesFile = conf_dir + "ETLCServer.properties";  
		ETLCServerProperties connProps =new ETLCServerProperties(propertiesFile);
		sybase_db_driver=connProps.getProperty(Constants.ENGINE_DB_DRIVERNAME);
		
		if (args.length != 5)
		{
			System.out.println("Please enter in the following values\n" +
					"<Database/ServerName> <PortNumber> <Username> <Password> <ENIQ Database>" +
					"e.g. eniq1 2641 dwhrep dwhrep dwhrep");
		}
		else
		{
			RetrieveDatabaseInformation databaseInfo = new RetrieveDatabaseInformation(args[0], args[1], args[2], args[3], args[4]);
			
			databaseInfo.createSQLFiles();			
		}
		
	}
	
	/**
	 * Creates files that contains the following information for this database
	 * create table statements, primary keys, foreign keys, insert statements, drop table commands 
	 *
	 */
	public void createSQLFiles()
	{
		// setting up the files to write to.
		Vector vec = getTableNames();
		
		// setting up the directories for the files used.
		createTableFile = testDir + "createTables_" + database + ".sql";
		foreignKeyFile = testDir + "foreignKeys_" + database + ".sql";
		
		insertFile = tempDir + "insertTestData_" + database + ".sql";
		dropTableFile = testDir + "dropTables_" + database + ".sql"; 
		
		// creating instances of these files to preform some sanity checking
		File createTable = new File(createTableFile);
		File dropTables = new File(dropTableFile);
		File foreignKeyTables = new File(foreignKeyFile);
		File insertStatements = new File(insertFile);
		File dwhViews = new File(this.dwhViews);
		File testDir = new File(this.testDir);
		File tempDir = new File(this.tempDir);
		
		deleteDir(tempDir);
		
		if(!testDir.exists())
		{
			testDir.mkdirs();
		}
		
		if(!tempDir.exists())
		{
			tempDir.mkdirs();
		}
		
		//removes the files if they already exist on the server
		// this is necessary because if the file isn't deleted then the file will be appended to.
		if (createTable.exists())
		{
			createTable.delete();			
		}
		
		if (dropTables.exists())
		{
			dropTables.delete();
		}
		
		if (foreignKeyTables.exists())
		{
			foreignKeyTables.delete();
		}
		
		if (insertStatements.exists())
		{
			insertStatements.delete();
		}
		
		if (dwhViews.exists())
		{
			dwhViews.delete();
		}
		
/*	
		// creates all the Create table statements along with primary keys
		for (int i = 0; i < vec.size(); i++)
		{
			createTableStatements(vec.elementAt(i).toString());
			createPrimaryKeys(vec.elementAt(i).toString());
		}
		
		
		// adds the drop constraint and drop table statements to the dropTable file
		dropForeignKey(vec);
		createDropTableStatements(vec);
		
		
		// creates the insert statements
		for (int i = 0; i < vec.size(); i++)
		{
			createTableInserts(vec.elementAt(i).toString());
			parseSQLFile(this.testDir, insertStatements);
		}
		
		// the dataware house requires a mockup of the system schema.
		// the dataware house does not need the foreign key file. 
		if (this.database.equals("dwh"))
		{
			// creates the SYS Schema
			createSysSchema();
			createViews();
		}
*/
		// adds all the foreign keys to the foreignKey file
		for (int i = 0; i < vec.size(); i++)
		{
			System.out.println(vec.elementAt(i).toString());
			this.createForeignKeys(vec.elementAt(i).toString());
		}
		
		vec.clear();
		
		// removes all the temporary files in the temp directory.
		deleteDir(tempDir);
		
	}

	/**
	 * Delets the directory and all its contents including subfolders
	 * @param dir
	 * @return
	 */
	private static boolean deleteDir(File dir)
	{
		// checks to see if dir is a directory
		// if it is it will go through all the children and delete them
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				
				if (!success)
				{
					return false;
				}
			}
		}
		
		// after all the children are deleted the directory/file is deleted
		return dir.delete();
	}
	
	
	
	/**
	 * Creates a connection to the database
	 * @param database
	 * @param portNumber
	 * @param username
	 * @param password
	 * 
	 */
	private void getConnection(String database, String portNumber, String username, String password) 
	{
		
		// information needed for connecting to the database.
		
			    String databaseType = "Sybase";
				String url = "jdbc:sybase:Tds:";
			    
				// checks to see if a port number exists. if it does the : will be needed 
				if (!portNumber.equals("") || !portNumber.equals(null))
				{
					database = database + ":";
				}
				
			    sybase = new PhysicalDatabase(sybase_db_driver, databaseType, database, url, portNumber, username, password);
				 
			    this.conn = sybase.getConnection();

	}

	/**
	 * Retreives the names of the tables in the specified database
	 * @param database
	 */
	private Vector getTableNames()
	{
		
		String strQuery = "";
		
		// the dwh is very different to the repository databases.
		// it will access this information differnetly 
		if (this.database.equals("dwh"))
		{
			strQuery = "Select distinct st.table_name " + 
			"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
			"    WHERE st.table_type = 'BASE' " +
			"    AND up.user_name = 'dc' " + 
			"    ORDER BY st.table_name; ";
		}
		else
		{
			strQuery = " select distinct tname " + 
						  " from sys.syscolumns " +
						  " where creator = '" + this.username + "'" +
						  " order by tname; ";
		}
				
		try 
		{
			Vector<String> vec = new Vector<String>(); // stores the table names
			
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(strQuery);
			
			// gets the names of the tables from the query and inserts these into the vector
			while (rs.next())
			{
				if (this.database.equals("dwh"))
					vec.add(rs.getString("table_name").trim());
				else
					vec.add(rs.getString("tname").trim());				
			}
			
			rs.close();
			stmt.close();
			
			return vec;
		
		}
		
		catch (SQLException e) 
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Error Code: " + e.getErrorCode() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
		
		return null;
		
	}

	/**
	 * Retreives the names of the views in the specified database
	 * @param database
	 */
	private Vector getViewNames()
	{
		
		String strQuery = "Select distinct st.table_name " + 
		"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
		"    WHERE st.table_type = 'VIEW' " +
		"    AND up.user_name = 'dc' ";
		
		try 
		{
			Vector<String> vec = new Vector<String>(); // stores the table names
			
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(strQuery);
			
			// gets the names of the tables from the query and inserts these into the vector
			while (rs.next())
			{
					vec.add(rs.getString("table_name").trim());
			}
			
			rs.close();
			stmt.close();
			
			return vec;
		
		}
		
		catch (SQLException e) 
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Error Code: " + e.getErrorCode() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
		
		return null;
		
	}
	/**
	 * generates the create table statements for each table in the specified database
	 * @param tableName
	 */
	private void createTableStatements(String tableName)
	{
		System.out.println("Create Table -  Creating: " + tableName);
	    try 
	    {
	    	// setting up the file for writing
	    	File createTableFile = new File(this.createTableFile);
	    	BufferedWriter bw = null;
	    	
	    	// if the file doesnt exist create all the directories and the bufferedwriter will create a new file
			if (!createTableFile.isFile())
			{
				bw = new BufferedWriter(new FileWriter(createTableFile));
			}
			else
			{		
				// if it already exists then append to the existing file.
				bw = new BufferedWriter(new FileWriter(createTableFile, true));
			}
	    	
			PrintWriter pw = new PrintWriter(bw);
			
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String strQuery = "";
			
			// the dwh is very different to the repository databases.
			// it will access this information differnetly 
			if ( this.database.equals("dwh"))
			{
				// this query will extract the column types and their lengths, rename datetime to timestamp and change any numeric value to 31 if it is greater than 31
				strQuery =  " BEGIN " +
				"    DECLARE tablenameParam varchar(255); " +
				"    SET tablenameParam = '" + tableName + "'; " +
				"     " +
				"    SELECT coldef " +
				"    FROM ( " +
				"    SELECT sc.column_name || ' ' || sd.domain_name || '(' || sc.width || ')' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_name = '" + tableName + "' AND (sd.domain_name = 'varchar' OR sd.domain_name = 'char') " +
				"    AND st.table_type = 'BASE' " +
				"    AND up.user_name = 'dc' " +
				"    UNION ALL " +
				"    SELECT sc.column_name || ' ' || sd.domain_name ||  '(' || IF(sc.width > 31) THEN 31 ELSE sc.width ENDIF|| ')' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_name = '" + tableName + "' AND sd.domain_name = 'numeric' " +
				"    AND st.table_type = 'BASE' " +
				"    AND up.user_name = 'dc' " +
				"    UNION ALL " +
				"    SELECT sc.column_name || ' ' || 'int' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_name = '" + tableName + "' AND (sd.domain_name = 'unsigned int' OR sd.domain_name = 'smallint' OR sd.domain_name = 'tinyint') " +
				"    AND st.table_type = 'BASE' " +
				"    AND up.user_name = 'dc' " +
				"    UNION ALL " +
				"    SELECT sc.column_name || ' ' || 'bigint' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_name = '" + tableName + "' AND sd.domain_name = 'unsigned bigint' " +
				"    AND st.table_type = 'BASE' " +
				"    AND up.user_name = 'dc' " +
				"    UNION ALL " +
				"    SELECT sc.column_name || ' ' ||  'timestamp' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_name = '" + tableName + "' AND (sd.domain_name = 'datetime' OR sd.domain_name = 'small datetime') " +
				"    AND st.table_type = 'BASE' " +
				"    AND up.user_name = 'dc' " +
				"    UNION ALL " +
				"    SELECT sc.column_name || ' ' || sd.domain_name ||  ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_name = '" + tableName + "' AND sd.domain_name <> 'unsigned bigint' AND sd.domain_name <> 'varchar' AND sd.domain_name <> 'char' AND sd.domain_name <> 'numeric' AND sd.domain_name <> 'unsigned int' AND sd.domain_name <> 'unsigned bigint' AND sd.domain_name <> 'smallint' AND sd.domain_name <> 'tinyint' AND sd.domain_name <> 'datetime' AND sd.domain_name <> 'small datetime' " +
				"    AND st.table_type = 'BASE' " +
				"    AND up.user_name = 'dc')as dertable " +
				"    ORDER BY column_id; " +
				" END; ";
			}
			else 
			{
				strQuery =  " BEGIN " +
				" DECLARE tablenameParam varchar(255); " +
				" SET tablenameParam = '" + tableName + "'; " +
				" " +
				" SELECT coldef " +
				" FROM ( " +
				" SELECT sc.column_name || ' ' || sd.domain_name || '(' || sc.width || ')' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				" FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				" WHERE st.table_name = '" + tableName + "' AND (sd.domain_name = 'varchar' OR sd.domain_name = 'char') " +
				" UNION ALL " +
				" SELECT sc.column_name || ' ' || sd.domain_name ||  '(' || IF(sc.width > 31) THEN 31 ELSE sc.width ENDIF|| ')' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				" FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id)  JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				" WHERE st.table_name = '" + tableName + "' AND sd.domain_name = 'numeric' " +
				" UNION ALL " +
				" SELECT sc.column_name || ' ' || 'int' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				" FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id)  JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				" WHERE st.table_name = '" + tableName + "' AND (sd.domain_name = 'unsigned int' OR sd.domain_name = 'smallint' OR sd.domain_name = 'tinyint') " +
				" UNION ALL " +
				" SELECT sc.column_name || ' ' || 'bigint' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				" FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				" WHERE st.table_name = '" + tableName + "' AND sd.domain_name = 'unsigned bigint' " +
				" UNION ALL " +
				" SELECT sc.column_name || ' ' ||  'timestamp' || ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				" FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id)  JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				" WHERE st.table_name = '" + tableName + "' AND (sd.domain_name = 'datetime' OR sd.domain_name = 'small datetime') " +
				" UNION ALL " +
				" SELECT sc.column_name || ' ' || sd.domain_name ||  ' ' || IF(sc.nulls='N') THEN 'not null' ENDIF || ',' AS coldef, sc.column_id " +
				" FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id)  JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				" WHERE st.table_name = '" + tableName + "' AND sd.domain_name <> 'unsigned bigint' AND sd.domain_name <> 'varchar' AND sd.domain_name <> 'char' AND sd.domain_name <> 'numeric' AND sd.domain_name <> 'unsigned int' AND sd.domain_name <> 'unsigned bigint' AND sd.domain_name <> 'smallint' AND sd.domain_name <> 'tinyint' AND sd.domain_name <> 'datetime' AND sd.domain_name <> 'small datetime' " +
				" )as dertable " +
				" ORDER BY column_id; " +
				"  END; ";
			}
			
			ResultSet rs = stmt.executeQuery(strQuery);			
	
			String line = "create table " + tableName + " (";
			
			// appending to the file
			// Writing the name of each table above its statements
			pw.write("\n");
			pw.write("\n--/***************************");
			pw.write("\n--  " + tableName);
			pw.write("\n--/***************************");
			pw.write("\n");
			pw.write(line);
			while (rs.next())
			{
				line = "\t" + rs.getString("coldef");
				
				// checks to see if this is the last row in the table
				if(rs.next() == false)
				{	
					line = line.replace(",", "");
				}
				
				rs.previous(); // has to be brought back as the next() method in the if will bring it forward one row.
				
				//System.out.println(line);
				pw.write("\n" + line);
			}			
			
			pw.write("\n);");
			
			pw.close();
			bw.close();
			stmt.close();

			System.out.println("Create Table -  Created: " + tableName);
		}
	    
	    catch (Exception e) 
	    {
	    	System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
	}

	/**
	 * Adds the primary key alter statements to the create table SQL file
	 * @param tableName
	 */
	private void createPrimaryKeys(String tableName)
	{
		System.out.println("Create Primary Keys -  Creating Key for: " + tableName);
		
		// this query finds the name of the primary key columns in the table
		String sqlQuery = " Select cname" +
						  " From sys.sysColumns" +
						  " Where in_primary_key = 'Y'" +
						  " and tname = '" + tableName + "'";
		
		try 
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlQuery);
			Vector<String> vec = new Vector<String>();
			
			String primaryKey = "";
			
			// adds the primary keys to a vector for further processing
			while( rs.next() )
			{
				vec.add(rs.getString("cname").trim());
			}
			
			// if the vector has more than one element then they need to be formatted correctly
			if (vec.size() > 1);
			{
				for (int i = 0; i < vec.size(); i++ )
				{
					primaryKey += vec.elementAt(i).toString();
					
					if (i != (vec.size() -1))
					{
						primaryKey = primaryKey + ", ";
					}
				}				
			}
			
			String alterStatement = "";
			boolean hasPrimaryKeys = false;
			
			if ( vec.size() > 0)
			{
				// this is the string that will be added to the file.
				alterStatement = "\n alter table " + tableName + "\n" + 
										"       add primary key (" + primaryKey + ");";
				hasPrimaryKeys = true;
			}
			
			vec.clear(); // clearing the vector
			
			// setting up the file for writing
	    	File createTableFile = new File(this.createTableFile);
	    	BufferedWriter bw = null;
	    	try 
	    	{
		    	// if the file doesnt exist the bufferedwriter will create a new file
				if (!createTableFile.isFile())
				{
					bw = new BufferedWriter(new FileWriter(createTableFile));					
				}
				else
				{		
					// if it already exists then append to the existing file.
					bw = new BufferedWriter(new FileWriter(createTableFile, true));
				}
		    	
				PrintWriter pw = new PrintWriter(bw);
				
				pw.write("\n" + alterStatement + "\n");
				
				pw.close();
				bw.close();
				rs.close();
				stmt.close();
				
				if (hasPrimaryKeys)
					System.out.println("Create Primary Keys -  Created Key for: " + tableName);
				else
					System.out.println("Create Primary Keys -  No Primary Keys Created for: " + tableName);
		    }
		    	
	    	catch (IOException e) 
	    	{
	    		System.out.println( "============================================" );
				e.printStackTrace();
				System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
				System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
				System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
				System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
				System.out.println(" Message: " + e.getMessage() );
				System.out.println( "============================================" );
			}
		} 
		
		catch (SQLException e) 
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Error Code: " + e.getErrorCode() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
		
	}

	/**
	 * This adds the foreign keys to the create table sql file
	 * @param tableName
	 */
	private void createForeignKeys(String tableName)
	{
		System.out.println("Create Foreign Keys -  Creating Key for: " + tableName);
		
		String strQuery = " select primary_tname, role, columns " +
						  " from sys.sysforeignkeys " +
						  " where foreign_tname = '" + tableName + "'; ";
		
		try
		{			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strQuery);
			
			String primaryKeyTable = "";
			String constraintName = "";
			int keyCounter = 1; // used to create completely unique foreign key names as some tables can be connected more than once to the same tables.
			
			while (rs.next())
			{
				String columns = "";
				String foreignKey = "";
				String primaryKey = "";
				
				primaryKeyTable = rs.getString("primary_tname").trim();
				constraintName = rs.getString("role").trim();
				columns = rs.getString("columns").trim();
								
				constraintName = "FK_" + tableName + "_TO_" + primaryKeyTable + "_" + keyCounter;
				
				// if there are more than one foreign key then the columns have to be extracted and formatted differently
				if( columns.indexOf(",") > 1)
				{
					String temp = "";
					String line = columns;
					
					int offset = 1;
					
					// splits up the primary and foreign keys and adds them to the appropiate variables
					while (offset != 0)
					{
						// checks to see if theres a comma left in the line. if so it splits up the parts seperated by the comma
						if (line.contains(","))
						{
							offset = 1;
							temp = line.substring(0, line.indexOf(","));
							line = line.substring(temp.length() + offset);
						}
						// if not this is the last one so the loop can end after this.
						else
						{
							offset = 0;
							temp = line;
						}
						
						// adding the foreign and primary key components to the correct variables.
						foreignKey += temp.substring(0, temp.indexOf(" IS ")) + ", ";
						primaryKey += temp.substring(temp.indexOf(" IS ") + 4, temp.length()) + ", ";
						
					}
				}
				
				// if there is only one foreign key then just assign the value to the correct variables. 
				else
				{
					foreignKey = columns.substring(0, columns.indexOf(" IS "));
					primaryKey = columns.substring(columns.indexOf(" IS ") + 4, columns.length());
				}
				
				// this removes the last comma from the foreign key
				if (foreignKey.indexOf(",") > -1)
				{
					foreignKey = foreignKey.substring(0, foreignKey.lastIndexOf(","));
				}
				
				// this removes the last comma from the primary key
				if (primaryKey.indexOf(",") > -1)
				{
					primaryKey = primaryKey.substring(0, primaryKey.lastIndexOf(","));
				}
				
				// this is the format the alter statement for the foreign key should take. 
				String alterTableStatement = " \nalter table " + tableName + "\n" +
											 "       add constraint " + constraintName + " foreign key " + "(" + foreignKey + ")" + "\n" +
											 "       references " + primaryKeyTable + " (" + primaryKey + ")" +  "\n" +
											 "       on delete restrict on update restrict;";
				
				// setting up the file for writing
		    	File foreignKeyFile = new File(this.foreignKeyFile);
		    	BufferedWriter bw = null;
		    	
		    	try 
		    	{
			    	// if the file doesnt exist the bufferedwriter will create a new file
					if (!foreignKeyFile.isFile())
					{
						bw = new BufferedWriter(new FileWriter(foreignKeyFile));					
					}
					else
					{		
						// if it already exists then append to the existing file.
						bw = new BufferedWriter(new FileWriter(foreignKeyFile, true));
					}
			    	
					PrintWriter pw = new PrintWriter(bw);
					
					pw.write("\n" + alterTableStatement + "\n");
					
					pw.close();
					bw.close();
					keyCounter ++;
			    }
			    	
		    	catch (IOException e) 
		    	{
		    		System.out.println( "============================================" );
					e.printStackTrace();
					System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
					System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
					System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
					System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
					System.out.println(" Message: " + e.getMessage() );
					System.out.println( "============================================" );
				}
			}
			
			rs.close();
			stmt.close();
			
			System.out.println("Create Foreign Keys -  Created Key for: " + tableName);
		
		}
		
		catch (SQLException e) 
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Error Code: " + e.getErrorCode() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
	}
		
		

	
	/**
	 * Creates a file containg a mockup of the SYS Schema used in Sybase IQ
	 *
	 */
	private void createSysSchema()
	{
		System.out.println("Create System Schema for DWH-  Creating Schema");
		
		// contents of the sql file mockup of the system schema
		String strSysSchema = " create schema SYS authorization DBA \n" +
							  " --/*************************** \n" +
							  "    SYSCOLUMNS \n" +
							  " --/*************************** \n" +
							  " create view SYSCOLUMNS as \n" +
							  " select sc.TABLE_NAME TNAME, sc.COLUMN_NAME CNAME, sc.ordinal_position COLNO, sc.data_type COLTYPE, sc.is_nullable NULLS, column_size length, sc.column_def DEFAULT_VALUE, sc.remarks REMARKS \n" +
							  " from information_schema.SYSTEM_COLUMNS sc \n" +
							  " \n " +
							  " --/*************************** \n" +
							  "    SYSINDEXES \n" +
							  " --/*************************** \n" +
							  " --SYSINDEXES \n" +
							  " -- Insert the creator from the userpermissions table or one of the other system tables. \n" +
							  " create view SYSINDEXES as \n" +
							  " select si.index_name INAME, si.table_name TNAME, si.type INDEXTYPE, si.column_name COLNAMES, si.asc_or_desc INTERVAL \n" +
							  " from information_schema.SYSTEM_INDEXINFO si \n" +
							  " \n " +
							  " --/*************************** \n" +
							  "    SYSINDEX \n" +
							  " --/*************************** \n" +
							  " create view SYSINDEX as \n" +
							  " select si.index_name INAME, si.type INDEXTYPE \n" +
							  " from information_schema.SYSTEM_INDEXINFO si \n" +
							  " \n " +
							  " --/*************************** \n" +
							  "    SYSTABLE \n" +
							  " --/*************************** \n" +
							  " create view SYSTABLE as \n" +
							  " select st.table_name TABLE_NAME, st.table_type TABLE_TYPE, st.remarks REMARKS \n" +
							  " from information_schema.SYSTEM_TABLES st; \n";
		
		// creating the file
		File fleSysSchema = new File(this.sysSchema);
		BufferedWriter bw = null;
		
		try
		{
			bw = new BufferedWriter(new FileWriter(fleSysSchema));
			
			// writing to the file
			PrintWriter pw = new PrintWriter(bw);
			pw.write(strSysSchema);
			
			pw.close();
			bw.close();
			
			System.out.println("Create System Schema for DWH-  Created Schema");
		}	
		catch (Exception e) 
	    {
	    	System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
	}
	
	/**
	 * retreives the inserts in the database
	 * @param tableName
	 */
	private void createTableInserts(String tableName)
	{
		System.out.println("Create Table Inserts -  Creating Inserts for: " + tableName);
		
		try 
		{
			File insertFile = new File(this.insertFile);
			BufferedWriter bw = null;
			
			// if the file doesnt exist create all the directories and the bufferedwriter will create a new file
			if (!insertFile.isFile())
			{
				bw = new BufferedWriter(new FileWriter(insertFile));
			}
			else
			{		
				// if it already exists then append to the existing file.
				bw = new BufferedWriter(new FileWriter(insertFile, true));
			}
	    	
			PrintWriter pw = new PrintWriter(bw);
			
			Statement stmtColumnNames = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String strColumnNameQuery = "";
			String colName = "";
			String colType = "";
			
			if (this.database.equals("dwh"))
			{
				strColumnNameQuery = "Select sc.column_name, sc.column_id, sd.domain_name" + 
				"    FROM sys.syscolumn sc JOIN sys.systable st ON (sc.table_id = st.table_id) Join sys.sysuserperm up ON (st.creator = up.user_id) JOIN sys.sysdomain sd ON (sc.domain_id = sd.domain_id)  " +
				"    WHERE st.table_type = 'BASE' " +
				"    AND st.table_name = '" + tableName + "'" +
				"    AND up.user_name = 'dc' " + 
				"    ORDER BY sc.column_id; ";
				
				colName = "column_name";
				colType = "domain_name";
			}
			
			else
			{
				strColumnNameQuery = " select cname, colno, coltype " +
			    " from sys.syscolumns " +
			    " where tname = '" + tableName + "' " + 
			    " Order by colno ";
				
				colName = "cname";
				colType = "coltype";
			}
			// gets the name of the column specific to that table and their ordinal_position
			
						
			ResultSet rsColumnNames = stmtColumnNames.executeQuery(strColumnNameQuery);
			
			String columnNames = "";
			String type = "";
			
			Vector<String> valueContsraint = new Vector<String>();
						
			// getting the column names from the resultset
			while (rsColumnNames.next())
			{				
				if (!rsColumnNames.next())
				{
					rsColumnNames.previous();
					columnNames += rsColumnNames.getString(colName).trim();
				}
				else
				{
					rsColumnNames.previous();
					columnNames += rsColumnNames.getString(colName).trim() + ", ";
				}
				
				type = rsColumnNames.getString(colType).trim();
				
				// if this is one of the following it will need an inverted comma
				if (type.equals("varchar") || type.equals("datetime") || type.equals("timestamp") || type.equals("char") || type.equals("date") || type.equals("time") || type.equals("character") ) 
				{
					valueContsraint.add("'");
				}
				else
				{
					valueContsraint.add("");
				}
			}
			
			rsColumnNames.close();
			stmtColumnNames.close();
			
			String strSelectQuery = "";
			
			// the dwh requires that the username dc is in front of the table name to access it. 
			if (this.database.equals("dwh"))
			{
				strSelectQuery = " Select " + columnNames +
								 " from dc." + tableName;
			}
			else
			{
				strSelectQuery = " Select " + columnNames +
				 				 " from " + tableName;
			}
			
			Statement stmtInserts = conn.createStatement();
			ResultSet rsTableInserts = stmtInserts.executeQuery(strSelectQuery);
			
			String temp = "";
			String line = "";
			String strInsertInto = "insert into " + tableName + "( " + columnNames + ") values ("; // line for the insertion statement
			
			// loggin info
			boolean hasRows = false;
			
			
					
			int bufferflush = 0;
			
			if (!rsTableInserts.next())
				System.out.println("Create Table Inserts -  Nothing to insert for " + tableName);
				
			else
			{
				hasRows = true;
			
				// retreives the information from the database
				do{
					
					
	
					line = strInsertInto;
					
					// this puts the appropiate brackets on the value from the database.
					for (int i = 0; i < valueContsraint.size(); i ++)
					{					
						temp = rsTableInserts.getString(i+1);	// gets the information from the specific column
						
						
						// if the value isnt null then it will put the brackets on the value (
						if (temp != null)
						{
							// temp has to be converted into a char array to remove any return values if they exist to prevent problems with the parsing.
							if ( temp.indexOf("\n") > -1)
							{							 
								char[] parseTemp = temp.toCharArray();
								String charTemp = "";
								
								for (int j = 0; j < parseTemp.length; j++)
								{
									if (parseTemp[j] == '\r')
									{
										parseTemp[j] = ' ';
									}
									
									if (parseTemp[j] == '\n')
									{
										parseTemp[j] = ' ';
									}
																
									charTemp += parseTemp[j];
								}
								
								temp = charTemp;						
							}
						
							// checks to see if there are apostrophe's contained within the values contained in the fields.
							// adds another apostrophe so that HSQL will disregard this.
							if (temp.contains("'"))
							{
								temp = temp.replace("'", "''");
							}
							//System.out.println("Line for Statement: " + temp);
							line = line + valueContsraint.elementAt(i) + temp + valueContsraint.elementAt(i);
						}
						
						else  // value is null so no delimiters are needed
						{
							line = line + temp;
						}
						
						// if this is the last value then it doesnt need the comma at the end. 
						if (i != (valueContsraint.size()- 1))
						{
							line = line + ", ";
						}
						
					}
					
					line = line + ");"; // ending the insert
					
					// writing this to the file
					pw.write(line);
					pw.write("\n");
					
					if ( (bufferflush%10000) == 0)
					{
						bw.flush();
						pw.flush();
					}
				} while (rsTableInserts.next());
			}
			
			if (hasRows)
				System.out.println("Create Table Inserts -  Created Inserts for: " + tableName);
			
			rsTableInserts.close();
			stmtInserts.close();
			bw.close();
			pw.close();
		}
		
		catch (SQLException e) 
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Error Code: " + e.getErrorCode() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
		
		catch(Exception e)
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );	
		}		
	}

	/**
	 * Parses any file from Sybase IQ SQL Syntax to HSQL SQL Syntax.
	 * @param String destinationDir, File fileName
	 * @return File parsefFile
	 */
	private void parseSQLFile(String destinationDir, File fileName)
	{
		// checks the file extension
		if (fileName.getName().endsWith(".sql"))
		{
			File dir = new File(destinationDir);
			BufferedReader input = null;
			File parsedFile = new File(destinationDir + fileName .getName());
			BufferedWriter bw = null;
			PrintWriter pw = null;
			
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			
			try 
			{
				bw = new BufferedWriter(new FileWriter(parsedFile));
				pw = new PrintWriter(bw);
			}
			
			catch (FileNotFoundException e1) 
			{
				e1.printStackTrace();
			}
			
			catch(Exception e)
			{
				System.out.println( "============================================" );
				e.printStackTrace();
				System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
				System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
				System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
				System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
				System.out.println(" Message: " + e.getMessage() );
				System.out.println( "============================================" );	
			}		
			try
			{
				input = new BufferedReader( new FileReader(fileName) );
				String line = null; 
				
				// this checks these values within an insert statement i.e. metadata
				String[] sqlSybaseIQInsert = {"'datetime'", "'numeric'", "insert into MeasurementColumn", "insert into ReferenceColumn", "numeric("};
				String[] sqlHSQLInsert = {"'timestamp'", "'numeric'", "", "", ""};
				
				// sybase iq specific indexes have to be removed as they dont exist in hsql
				String[] sybaseIndexes = {"'CMP'", "'DATE'", "'DTTM'", "'HG'", "'HNG'", "'LF'", "'TIME'", "'WD'"};
				
				// Reading the File into a String Buffer. This will be used to separate the SQL Statements
				while ( (line = input.readLine() ) != null )
				{					
					// this checks to see if this is not a comment and part of an insert statement. 
					if (!line.startsWith("--"))
					{
						// Checks if the line starts with a sybase IQ comment
						// changes it to a HSQL Comment
						if (line.startsWith("/*"))
						{
							line = "--" + line;
						}						
						
						// checks to see if a value exists in this line and needs to be updated.
						for (int i = 0; i < sqlSybaseIQInsert.length; i++)
						{							
							if (line.indexOf(sqlSybaseIQInsert[i]) > -1)
							{
								// checks to see if datetime is in the string. This will change it to timestamp
								if (i == 0)
								{	
									line = line.replaceAll(sqlSybaseIQInsert[i], sqlHSQLInsert[i]);
								}
								
								// checks to see if numeric is in the string. it will reduce any value over 31 to 31 to comply with HSQL syntax.
								if (i == 1)
								{	
									String currentTestLine = line;
									String finalLine = "";
									String temp = "";	
									int offset = 4;
									
									while (currentTestLine.indexOf(sqlSybaseIQInsert[i]) > -1)
									{
										// extracts the value of the numeric									
										String value = currentTestLine.substring(currentTestLine.indexOf(sqlSybaseIQInsert[i]) + (sqlSybaseIQInsert[i].length() + 2), currentTestLine.indexOf(sqlSybaseIQInsert[i]) + (sqlSybaseIQInsert[i].length() + 4));
	
										// checks to see if the value is a 2 digit number. if so it checks its value
										if (value.indexOf(",") < 1)
										{	
											Integer newValue = new Integer(value);
											
											// if this is greater than 31 it brings it changes it to 31
											if (newValue > 31)
											{
												newValue = 31;
												
												// updates the current line with the changes
												currentTestLine = currentTestLine.replace( sqlSybaseIQInsert[i] + "," + value, sqlSybaseIQInsert[i] + "," + newValue );
																						
												offset = 4; // offset is set to 4 so that the exact amount of information is contained in the string.
											}
										}
											
										else
										{
											offset = 3; // offset is set to 3 so that the exact amount of information is contained in the string
										}
										
										// used to prevent outOfBounds index error.
										int endOfInput = currentTestLine.indexOf(sqlSybaseIQInsert[i]) + (sqlSybaseIQInsert[i].length() + offset);
										
										if (endOfInput >= currentTestLine.length())
										{
											endOfInput = currentTestLine.length();
										}
										
										// temporly stores the piece of the current line that has already been processed
										temp = currentTestLine.substring(0, endOfInput);
										
										// sets the current line to the piece of the line that hasnt been processed.
										currentTestLine = currentTestLine.substring(endOfInput);
										finalLine += temp;	// this is the line that will be used to put the modified sql into the file. 
									}
									
									finalLine += currentTestLine; // inserts the piece of the current line that didnt need any processing.#
									line = finalLine;
								}
								
								// goes through all the possible indexes that are sybase IQ specific and removes them. 
								if ( i == 2 || i == 3)
								{
									for (int j = 0; j < sybaseIndexes.length; j++)
									{
										if (line.indexOf(sybaseIndexes[j]) > 1)
										{
											line = line.replace(sybaseIndexes[j], "''");
										}										
									} // end for
								} // end if
								
								// checks to see if numeric( is withing a line and makes sure that its the correct value. 
								if ( i == 4 )
								{
									String currentTestLine = line;
									String finalLine = "";
									String temp = "";	
									int offset = 4;
									
									while (currentTestLine.indexOf(sqlSybaseIQInsert[i]) > -1)
									{
										// extracts the value of the numeric									
										String value = currentTestLine.substring(currentTestLine.indexOf(sqlSybaseIQInsert[i]) + (sqlSybaseIQInsert[i].length()), currentTestLine.indexOf(sqlSybaseIQInsert[i]) + (sqlSybaseIQInsert[i].length() + 2));
	
										// checks to see if the value is a 2 digit number. if so it checks its value
										if (value.indexOf("(") < 1)
										{				
											Integer newValue = new Integer(value);
											
											// if this is greater than 31 it brings it changes it to 31
											if (newValue > 31)
											{
												newValue = 31;
												
												// updates the current line with the changes
												currentTestLine = currentTestLine.replace( sqlSybaseIQInsert[i] + value, sqlSybaseIQInsert[i] + newValue );
																						
												offset = 4; // offset is set to 4 so that the exact amount of information is contained in the string.
											}
										}
											
										else
										{
											offset = 3; // offset is set to 3 so that the exact amount of information is contained in the string
										}
										
										// used to prevent outOfBounds index error.
										int endOfInput = currentTestLine.indexOf(sqlSybaseIQInsert[i]) + (sqlSybaseIQInsert[i].length() + offset);
										
										if (endOfInput >= currentTestLine.length())
										{
											endOfInput = currentTestLine.length();
										}
										
										// temporly stores the piece of the current line that has already been processed
										temp = currentTestLine.substring(0, endOfInput);
										
										// sets the current line to the piece of the line that hasnt been processed.
										currentTestLine = currentTestLine.substring(endOfInput);
										finalLine += temp;	// this is the line that will be used to put the modified sql into the file. 
									}
									
									finalLine += currentTestLine; // inserts the piece of the current line that didnt need any processing.#
									line = finalLine;
								}
							}// end if (line.indexOf(sqlSybaseIQInsert[i]) > -1)
						}
					} // end if (!line.startsWith("--"))
					
					pw.write(line);
					pw.write("\n");
					
				} // end while ( (line = input.readLine() ) != null )
				
				
				pw.close();
				bw.close();
				input.close();
			}
			
			catch( FileNotFoundException e )
			{
				System.out.println( "============================================" );
				e.printStackTrace();
				System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
				System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
				System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
				System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
				System.out.println(" Message: " + e.getMessage() );
				System.out.println( "============================================" );
			}
			
			catch ( IOException e )
			{
				System.out.println( "============================================" );
				e.printStackTrace();
				System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
				System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
				System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
				System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
				System.out.println(" Message: " + e.getMessage() );
				System.out.println( "============================================" );
			}
			
			finally
			{
				try
				{
					if ( input != null )
					{
						input.close();
						bw.close();
					}
				}
				
				catch( IOException e )
				{
					System.out.println( "============================================" );
					e.printStackTrace();
					System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
					System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
					System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
					System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
					System.out.println(" Message: " + e.getMessage() );
					System.out.println( "============================================" );
				}
			}
			
		} // end check for SQL file extension
		
		else
		{
			System.out.println(fileName.getName() + " is an invalid file. Only *.sql files will be parsed.");
		}		
	} // end parseFile

	/**
	 * Creates a file that contains the drop tables commands for this database
	 * @param vecTableNames
	 */
	private void createDropTableStatements(Vector vecTableNames)
	{
		System.out.println("Create Drop Table -  Creating Drop Tables.");
		
		String dropTables = "";
		
		try
		{
			// setting up the file, this file should already exist so it will be appended to
			File dropTableFile = new File(this.dropTableFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(dropTableFile, true));
			PrintWriter pw = new PrintWriter(bw);
			
			// gets the names of all the tables and addes these to the file in the following format
			for (int i = 0; i < vecTableNames.size(); i++)
			{
				dropTables = "drop table " + vecTableNames.elementAt(i) + ";";	
				pw.write(dropTables + "\n");
			}
			
			System.out.println("Create Drop Table -  Created Drop Tables.");
			
			
			if (this.database.equals("dwh"))
			{
				String dropView = "drop view ";
				String dropSchema = "drop view ";
				
				// dropping the system schema views
				System.out.println("Create Drop Table -  Creating Drop Tables for DWH System Tables.");
				pw.write(dropSchema + "SYSCOLUMNS; \n");
				pw.write(dropSchema + "SYSINDEXES; \n");
				pw.write(dropSchema + "SYSINDEX; \n");
				pw.write(dropSchema + "SYSTABLE; \n");
				System.out.println("Create Drop Table -  Created Drop Tables for DWH System Tables.");
				
				//dropping the Views
				Vector vecViewNames = getViewNames();
				
				System.out.println("Create Drop Table -  Creating Drop Views.");
				for (int i = 0; i < vecViewNames.size(); i++)
				{
					pw.write(dropView + vecViewNames.elementAt(i).toString() + "; \n");
				}				
				System.out.println("Create Drop Table -  Created Drop Views.");
			}
			
			bw.close();
			pw.close();
		}
		
		catch (Exception e) 
	    {
	    	System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
				
	}

	/**
	 * Creates a file that contains the drop foreign key statements for this database
	 * @param vecTableNames
	 */
	private void dropForeignKey(Vector vecTableNames)
	{
		System.out.println("Create Drop Foreign Keys -  Creating Drop Foreign Keys.");
		
		try
		{	
			// setting up the file for writing to
			File dropTableFile = new File(this.dropTableFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(dropTableFile, true));;
			PrintWriter pw = new PrintWriter(bw);
			
			// this loop retreives the information relating to the foreign key and then inserts the
			// alter statement into the file
			for (int i = 0; i < vecTableNames.size(); i++)
			{
				String strQuery = " select role " +
				  " from sys.sysforeignkeys " +
				  " where foreign_tname = '" + vecTableNames.elementAt(i) + "'; ";
	
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(strQuery);
				
				String constraintName = "";
				String dropConstraint = "";
				
				// after the info has been retreived the alter table statement is created
				while(rs.next())
				{
					constraintName = rs.getString("role").trim();
					
					if (constraintName != null)
					{
						dropConstraint = " alter table " + vecTableNames.elementAt(i) + "\n" + 
										 " \tdrop constraint " + constraintName + "; \n";
						pw.write(dropConstraint + "\n");
					}
				}
			}
			
			bw.close();
			pw.close();
			
			System.out.println("Create Drop Foreign Keys -  Created Drop Foreign Keys.");
		}
		
		catch (SQLException e) 
		{
			System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n\n" );
			System.out.println( "Error Code: " + e.getErrorCode() + "\n\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
		catch (Exception e) 
	    {
	    	System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}				
	}
	
	public void createViews()
	{
		System.out.println("Create Drop Foreign Keys -  Creating Drop Views.");
		
		String sqlQuery = "Select viewtext from sysviews;";
		
		try 
		{
			// setting up the file for writing
	    	File createDWHViews = new File(this.dwhViews);
	    	BufferedWriter bw = null;
	    	
	    	// if the file doesnt exist create all the directories and the bufferedwriter will create a new file
			if (!createDWHViews.isFile())
			{
				bw = new BufferedWriter(new FileWriter(createDWHViews));
			}
				    	
			PrintWriter pw = new PrintWriter(bw);
			
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery(sqlQuery);
			String line = "";
			
			while (rs.next())
			{
				line = rs.getString("viewtext");
				
				pw.write("\n" + line + ";\n");
			}			
			
			pw.close();
			bw.close();
			stmt.close();
			
			System.out.println("Create Drop Foreign Keys -  Created Drop Views.");
		}
		
		catch (Exception e) 
	    {
	    	System.out.println( "============================================" );
			e.printStackTrace();
			System.out.println( "This is the cause of the problem: " + e.getCause() + "\n" );
			System.out.println( "This is the initial cause of the problem: " + e.initCause( e.getCause()) + "\n" );
			System.out.println( "Localised Message: " + e.getLocalizedMessage() + "\n" );
			System.out.println( "Fill In Stack Trace: " + e.fillInStackTrace() );
			System.out.println(" Message: " + e.getMessage() );
			System.out.println( "============================================" );
		}
	}
	
}