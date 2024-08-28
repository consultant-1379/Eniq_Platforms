package com.ericsson.eniq.unittesting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.util.Enumeration;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class UnitTestInstallation
{
	/**
	 * sets up the three databases.
	 * Extracts all the database setup information, inserts and drop information.
	 * Creates XML versions of the databases. 
	 * Extracts necessary files to the appropiate locations on the local file system
	 * @param args
	 */
	public void main(String[] args)
	{
		// extracting the contents of the jars.
		extractJarsContents();
		
		// Extracting all the information from the databases and generating setup files from this.
		extractDatabaseContents();			
		
		// inserting this information into the HSQL Memory database.
		insertDatabaseContents();		
			
		
	}
	
	/**
	 * extracts the contents of the database and generates sql files with this information and parases these sql files into the hsql syntax
	 *
	 */
	private void extractDatabaseContents()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		String dwhRepServer = "";
		String dwhRepPort = "";
		String dwhRepUsername = "";
		String dwhRepPassword = "";
		
		String etlRepServer = "";
		String etlRepPort = "";
		String etlRepUsername = "";
		String etlRepPassword = "";
		
		String dwhServer = "";
		String dwhPort = "";
		String dwhUsername = "";
		String dwhPassword = "";
		
		try 
		{
			System.out.println("================================================================");
			System.out.println("Enter in the following details for the DWH Repository Database");
			System.out.println("Server Name: ");
			dwhRepServer = in.readLine();
					
			System.out.println("Port Number: ");
			dwhRepPort = in.readLine();
						
			System.out.println("Username: ");
			dwhRepUsername = in.readLine();
			
			System.out.println("Password: ");
			dwhRepPassword = in.readLine();
			
			
			System.out.println("================================================================");
			System.out.println("Enter in the following details for the ETL Repository Database");
			System.out.println("Server Name: ");
			etlRepServer = in.readLine();
			
			System.out.println("Port Number: ");
			etlRepPort = in.readLine();
						
			System.out.println("Username: ");
			etlRepUsername = in.readLine();
			
			System.out.println("Password: ");
			etlRepPassword = in.readLine();
			
			
			System.out.println("================================================================");		
			System.out.println("Enter in the following details for the Datawarehouse");
			System.out.println("Server Name: ");
			dwhServer = in.readLine();
			
			System.out.println("Port Number: ");
			dwhPort = in.readLine();
			
			System.out.println("Username: ");
			dwhUsername = in.readLine();
			
			System.out.println("Password: ");
			dwhPassword = in.readLine();
			System.out.println("================================================================");
		} 
			
		catch (IOException e) 
		{

			e.printStackTrace();
		}
	
		
		
		RetrieveDatabaseInformation dwhrepDbInfo = new RetrieveDatabaseInformation(dwhRepServer, dwhRepPort, dwhRepUsername, dwhRepPassword, "dwhrep");
		dwhrepDbInfo.createSQLFiles();
		
		RetrieveDatabaseInformation etlrepDbInfo = new RetrieveDatabaseInformation(etlRepServer, etlRepPort, etlRepUsername, etlRepPassword, "etlrep");
		etlrepDbInfo.createSQLFiles();
		
		RetrieveDatabaseInformation dwhDbInfo = new RetrieveDatabaseInformation(dwhServer, dwhPort, dwhUsername, dwhPassword, "dwh");
		dwhDbInfo.createSQLFiles();
	}
	
	
	/**
	 * inserts all the information contained in the sql files into the hsql database
	 *
	 */
	private void insertDatabaseContents()
	{
		DWHRepMemDB dwhRepDB = new DWHRepMemDB();
		dwhRepDB.createCompleteDB();
		
		ETLRepMemDB etlRepDB = new ETLRepMemDB();
		etlRepDB.createCompleteDB();
		
		DWHMemDB dwhDB = new DWHMemDB();
		dwhDB.createCompleteDB();
		dwhDB.createSystemTableSchema();
		dwhDB.createViews();
	}
	
	/**
	 * extract the files/directories in the Jar to the appropiate locations
	 *
	 */
	private void extractJarsContents()
	{
		// setting up the jar name and the ext directory
		String jarName = "lib/Unit_Testing_Setup.jar";
		String javaHome = System.getenv("JAVA_HOME") + "/jre/lib/ext/";
	
		// destination directories
		String[] coberturaDestinations = {"C:\\"};
		String[] dbunitDestinations = {"lib/", javaHome};
		String[] dwhManagerDestinations = {"lib/", javaHome};
		String[] hsqlDestinations = {"lib/", javaHome};
		String[] junitDestinations = {"lib/", javaHome};
		String[] repositoryDestinations = {"lib/", javaHome};
		String[] sybaseConnectionDestinations = {"lib/", javaHome};
		String[] unittestingDestinations = {"lib/", javaHome};
		
		// jar files used
		String cobertura = "cobertura/";
		String dbunitJar = "dbunit-2.2.jar";
		String dwhmanagerJar = "dwhmanager_5-0-0b223.jar";
		String hsqlJar = "hsqldb.jar";
		String junitJar = "junit.jar";
		String repositoryJar = "repositoryR6.jar";
		String sybaseConnectionJar = "jconn3.jar";
		String unittesting = "unittesting.jar";
		
		try 
		{
			// extracting the files from the jar and adding them to the correct locations
			getFilesFromJar(jarName, cobertura, coberturaDestinations);
			getFilesFromJar(jarName, dbunitJar, dbunitDestinations);
			getFilesFromJar(jarName, dwhmanagerJar, dwhManagerDestinations);
			getFilesFromJar(jarName, hsqlJar, hsqlDestinations);
			getFilesFromJar(jarName, junitJar, junitDestinations);
			getFilesFromJar(jarName, repositoryJar, repositoryDestinations);
			getFilesFromJar(jarName, sybaseConnectionJar, sybaseConnectionDestinations);
			getFilesFromJar(jarName, unittesting, unittestingDestinations);			
		}
		
		catch (FileNotFoundException e) 
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
	 * Extracts specific files contained in the Jar onto the local file system.
	 * @throws FileNotFoundException
	 */
	private void getFilesFromJar(String jarName, String fileName, String[] fileDestination) throws FileNotFoundException
	{
		try 
		{		
			JarFile unittestingJar = new JarFile(jarName);
			
			// adding a folder tag to the end of the fileDestination in case this wasnt entered
			// and makes the directories that are needed if they dont exist
			for (int i = 0; i < fileDestination.length; i++)
			{
				if (!fileDestination[i].endsWith("/"))
				{
					fileDestination[i] += "/";
				}
				
				File fleFileDir = new File(fileDestination[i]);
				
				if(!fleFileDir.exists())
				{
					fleFileDir.mkdirs();
				}
			}			
			
			Enumeration enumJar = unittestingJar.entries();
								
			JarEntry entry = null;
			InputStream in = null;
			OutputStream out = null;
			String tmpFile;
					
			// This loop goes through all the files in the Jar file and then extracts them
			// to the specified folder.
			while (enumJar.hasMoreElements())
			{
				// gets each entry from the jar file
				entry = (JarEntry)enumJar.nextElement();
				tmpFile = entry.getName();				
				
				// checks to see if the filename is a directory i.e. with files underneath it.
				// this will create all the folders and place the files underneath them
				if ( entry.getName().startsWith(fileName))
				{
					// will loop through any destination folders
					for (int i = 0; i < fileDestination.length; i++)
					{
						// if this is a directory then it will create the folders
						if(entry.isDirectory())
						{
							File tmp = new File(fileDestination[i] + entry.getName());
							tmp.mkdirs();
						}
						
						// if its not a directory then it will write the file
						else
						{
							in = unittestingJar.getInputStream( unittestingJar.getEntry(tmpFile) );
							out = new FileOutputStream(fileDestination[i] + tmpFile);						
							
							int fileData;
							
							while ( (fileData = in.read()) != -1)
							{
								out.write(fileData);
							}
						}
					}
				}
				
				// checks to see if this is a file and if the filename is equal to the current file in the jar 
				else if(tmpFile.equals(fileName) && !entry.isDirectory())
				{
					// creates the files for each destination directory needed
					// i.e. lib folder and %JAVA_HOME%/ext folder so ANT can use the jars
					for (int i = 0; i < fileDestination.length; i++)
					{
						in = unittestingJar.getInputStream( unittestingJar.getEntry(tmpFile) );
						out = new FileOutputStream(fileDestination[i] + tmpFile);						
					
						int fileData;
						
						while ( (fileData = in.read()) != -1)
						{
							out.write(fileData);
						}
					}
				}				
			} // end while
		} // end try
		
		catch (IOException e) 
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
