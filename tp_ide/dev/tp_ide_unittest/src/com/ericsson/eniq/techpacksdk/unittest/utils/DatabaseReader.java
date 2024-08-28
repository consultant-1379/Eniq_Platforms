package com.ericsson.eniq.techpacksdk.unittest.utils;

import com.ericsson.eniq.unittesting.RetrieveDatabaseInformation;

public class DatabaseReader {

  /**
   * @param args
   */
  public static void main(String[] args) {

    // The ENIQ server from where the data is extracted from.
    //OLD String SERVER = "eniq6.lmf.ericsson.se";
    String SERVER = "dcetl-b.lmf.ericsson.se";
    
    // Retrieve the database information
    System.out.println("DatabaseReader: Retreiving DWHREP database information");
    RetrieveDatabaseInformation dwhrepDbInfo = new RetrieveDatabaseInformation(SERVER, "2641", "dwhrep", "dwhrep",
        "dwhrep");

    // Create the SQL files
    System.out.println("DatabaseReader: Creating SQL files...");
    dwhrepDbInfo.createSQLFiles();

//    // Retrieve the database information
//    System.out.println("DatabaseReader: Retrieving ETLREP database information");
//    RetrieveDatabaseInformation etlrepDbInfo = new RetrieveDatabaseInformation(SERVER, "2641", "etlrep", "etlrep",
//        "etlrep");
//
//    // Create the SQL files
//    System.out.println("DatabaseReader: Creating SQL files...");
//    etlrepDbInfo.createSQLFiles();
//
//    // Retrieve the database information.
//    // NOTE: For DWH database, the administrator account (dba) must be used for
//    // retrieval to work properly.
//    System.out.println("DatabaseReader: Retrieving DWH database information");
//    RetrieveDatabaseInformation dwhDbInfo = new RetrieveDatabaseInformation(SERVER, "2640", "dba", "sql", "dwh");
//
//    // Create the SQL files
//    System.out.println("DatabaseReader: Creating SQL files...");
//    dwhDbInfo.createSQLFiles();

    System.out.println("DatabaseReader: Done.");
  }

}
