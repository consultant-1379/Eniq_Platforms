package com.distocraft.dc5000.etl.importexport;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Vector;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.ericsson.eniq.repository.ETLCServerProperties;

public class SimpleExport {

  private boolean verbose = false;

  private final Connection con = null;

  private String whereColumn = null;

  private String whereCond = null;

  public SimpleExport(String[] args) throws Exception {
    if (args.length < 2) {
      throw new IllegalArgumentException("");
    }
    
    String confFile = System.getProperty("CONF_DIR")+"/ETLCServer.properties";

    for (int i = 0; i < args.length - 2;) {
      String opt = args[i++];

      if (opt.equals("-c")) {
        confFile = args[i++];
      } else if (opt.equals("-v")) {
        verbose = true;
        System.out.println("Verbose output enabled");
      } else if (opt.equals("-w")) {
        whereColumn = args[i++];
        whereCond = args[i++];
        if (verbose) {
          System.out.println("WHERE " + whereColumn + " LIKE '" + whereCond + "'");
        }
      } else {
        // ignore
      }
    }

    String outFileName = args[args.length - 1];
    String address = args[args.length - 2];

    if (verbose) {
      System.out.println("Output file is " + outFileName);
    }

    if (address.indexOf(".") < 0) {
      throw new IllegalArgumentException("dbname.tableName malformed");
    }

    String dbname = address.substring(0, address.indexOf("."));
    String table = address.substring(address.indexOf(".") + 1);

    if (verbose) {
      System.out.println("Database " + dbname + " table " + table);
    }

    FileOutputStream fos = null;
    Connection con = initializeDBConnection(confFile, dbname);

    try {

      if (verbose) {
        System.out.println("Database connection initialized");
      }

      File out = new File(outFileName);
      if (out.exists()) {
        throw new IllegalArgumentException("Output file already exists");
      }

      fos = new FileOutputStream(out);

      DatabaseConnection connection = new DatabaseConnection(con);

      if (verbose) {
        System.out.println("Querying database...");
      }

      QueryDataSet myDataSet = new QueryDataSet(connection);
      String selClause = "SELECT * FROM " + table;

      if (whereColumn != null) {
        selClause = selClause + " WHERE " + whereColumn + " LIKE '" + whereCond + "'";
      }

      if (verbose) {
        System.out.println("Query: " + selClause);
      }

      myDataSet.addTable(table, selClause);

      FlatXmlDataSet.write(myDataSet, fos);

      if (verbose) {
        System.out.println("Output written. Closing...");
      }

    } finally {

      if (fos != null) {
        fos.close();
      }
      con.close();

    }

  }

  private Connection initializeDBConnection(String confFile, String dbname) throws Exception {

    if (verbose) {
      System.out.println("initializing db " + dbname);
    }

    
      Properties props = new ETLCServerProperties (confFile);
      
      RockFactory rf = new RockFactory(props.getProperty("ENGINE_DB_URL"), props.getProperty("ENGINE_DB_USERNAME"),
          props.getProperty("ENGINE_DB_PASSWORD"), props.getProperty("ENGINE_DB_DRIVERNAME"), "SimpleExport", true);

    if (rf == null) {
      throw new IllegalArgumentException("Unable to initialize DB connection");
    }

      Meta_databases selO = new Meta_databases(rf);
      selO.setConnection_name(dbname);

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rf, selO);

      Vector dbs = mdbf.get();

      if (dbs != null || dbs.size() <= 0) {
        Meta_databases mdb = (Meta_databases) dbs.get(0);

        Class.forName(mdb.getDriver_name());

        rf.getConnection().close();

        return DriverManager.getConnection(mdb.getConnection_string(), mdb.getUsername(), mdb.getPassword());

      } else {
        throw new IllegalArgumentException("No such DB " + dbname);
      }

    

  }

  public static void main(String[] args) {

    try {
      SimpleExport se = new SimpleExport(args);

    } catch (IllegalArgumentException e) {
      if (e.getMessage().length() > 0) {
        System.out.println("Error: " + e.getMessage());
      }
      else {
        System.out.println("Usage: export.sh [options] dbname.tableName outputFileName\n");
        System.out.println("\t-c confFile - Specify the ETLCEngine.properties file.");
        System.out.println("\t-w colName likeCondition - Specify where condition. Ex: -w VERSIONID DC_E%");
        System.out.println("\t-v - Verbose output.");
      }
    } catch (Exception e) {
      System.err.println("Unrecovable error occured.");
      e.printStackTrace();
    }

  }

}
