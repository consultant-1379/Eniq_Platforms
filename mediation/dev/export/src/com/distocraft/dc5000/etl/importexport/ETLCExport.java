package com.distocraft.dc5000.etl.importexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;


/**
 * Exports tables from DB to a XML-file. Usage: url userName password dbDriverName outputfile
 * replaceString - contains tag and value pairs ( tag = value) delimited by comma ',' . Tags are
 * searched from sqlSelect and sqlClause and replaced by value before execution. (tag = value, tag1 =
 * value1, tag2 = value2) properties (ETLCExport.properties) property file contains table list:
 * contains tables that are read from db. tables = table1,table2,table3 for each element in table
 * list there is two sub parameters (sqlSelect and sqlClause) table1.sqlSelect = select * from
 * table1 table1.sqlClause = where a = #tag1# table2.sqlSelect = select * from table2
 * table2.sqlClause = where a = #tag2# table3.sqlSelect = select * from table3 table3.sqlClause =
 * where a = #tag3# table4.sqlSelect = select * from table4 table4.sqlClause = where a = b
 * 
 * @author savinen Copyright Distocraft 2005 $id$
 */
public class ETLCExport {

  public String dbDriverClass = "";

  public String dbConn = "";

  private ArrayList tableList;

  private ArrayList sqlSelectList;

  private ArrayList sqlClauseList;

  private IDatabaseConnection connection;

  /**
   * @param url
   * @param userName
   * @param password
   * @param dbDriverName
   */
  public ETLCExport(String url, String userName, String password, String dbDriverName) {
    String dbUsername = userName;
    String dbPassword = password;
    this.dbDriverClass = dbDriverName;
    this.dbConn = url;

    try {

      // database connection
      Class driverClass = Class.forName(dbDriverClass);
      Connection jdbcConnection = DriverManager.getConnection(dbConn, dbUsername, dbPassword);
      connection = new DatabaseConnection(jdbcConnection);

    } catch (Exception e) {

      Logger.getLogger("export.ETLCExport").log(Level.SEVERE, "ERROR while creating connection: ", e);

    }

    // read properties
    initProperties(null);

  }

  public ETLCExport(String propDir, Connection jdbcConnection) {

    try {

      connection = new DatabaseConnection(jdbcConnection);

    } catch (Exception e) {

      Logger.getLogger("export.ETLCExport").log(Level.SEVERE, "ERROR while creating connection: ", e);

    }

    // read properties
    initProperties(null);

  }

  public ETLCExport(Properties prop, Connection jdbcConnection) {

    try {

      connection = new DatabaseConnection(jdbcConnection);

    } catch (Exception e) {

      Logger.getLogger("export.ETLCExport").log(Level.SEVERE, "ERROR while creating connection: ", e);

    }

    // read properties
    initProperties(prop);

  }

  /**
   * Read properties file. dbDriverClass dbConn dbUsername dbPassword tables for each element in
   * table: sqlSelect sqlClause
   * 
   * @param fileName
   */
  public void initProperties(Properties prop) {
    tableList = new ArrayList();
    sqlSelectList = new ArrayList();
    sqlClauseList = new ArrayList();

    Properties dbProp = new Properties();

    InputStream is = null;

    try {

      // if given property is null read from stream (jar)
      if (prop == null) {

        ClassLoader cl = this.getClass().getClassLoader();
        is = cl.getResourceAsStream("ETLCExport.properties");
        dbProp.load(is);

      } else {

        dbProp = prop;

      }

      String tableString = dbProp.getProperty("tables").trim();
      StringTokenizer token = new StringTokenizer(tableString, ",");

      while (token.hasMoreElements()) {
        String table = (String) token.nextElement();
        String sqlSelect = dbProp.getProperty(table + ".sqlSelect").trim();
        String sqlClause = dbProp.getProperty(table + ".sqlClause").trim();

        // insert into a array
        this.tableList.add(table);
        this.sqlSelectList.add(sqlSelect);
        this.sqlClauseList.add(sqlClause);
      }

    } catch (Exception e) {
      Logger.getLogger("export.readProperties").log(Level.SEVERE,
          "ERROR reading properties file: ETLCExport.properties", e);

    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
        }
      }

    }
  }

  /**
   * Exports DB tables and columns defined in property file into a xml file. sqlClauses (see. init)
   * can contain tags that can be replaced with certain value. replaceInClause contains these
   * tag/value pairs (tag=value).
   * 
   * @param replaceInClause
   * @param outputFileName
   */
  public void exportXml(String replaceInClause, String outputFileName) {

    FileOutputStream out = null;

    try {

      // outputfile
      File outputFile = new File(outputFileName);

      // create dataset
      QueryDataSet myDataSet = new QueryDataSet(connection);

      // loop all tables
      for (int i = 0; i < tableList.size(); i++) {

        String sqlSelect = (String) sqlSelectList.get(i);
        String tableName = (String) tableList.get(i);
        String sqlClause = (String) sqlClauseList.get(i);

        // replace possible tags

        if (replaceInClause == null) {
          replaceInClause = "";
        }
        StringTokenizer token = new StringTokenizer(replaceInClause, ",");
        while (token.hasMoreElements()) {
          String tmp = (String) token.nextElement();
          String tag = tmp.substring(0, tmp.indexOf("="));
          String value = tmp.substring(tmp.indexOf("=") + 1);

          sqlClause = sqlClause.replaceAll(tag, value);
        }

        // create statement
        String sqlStatement = sqlSelect + " " + tableName + " " + sqlClause;

        // execute statement
        Logger.getLogger("export.exportXml").log(Level.FINER, "Using sql statement: " + sqlStatement);
        myDataSet.addTable((String) tableList.get(i), sqlStatement);
        Logger.getLogger("export.exportXml").log(Level.INFO, "EXPORTING: " + (String) tableList.get(i));

      }

      // write result into a file in XML

      out = new FileOutputStream(outputFile);

      FlatXmlDataSet.write(myDataSet, out);

      // System.out.println("All tables exported succesfully.");
      Logger.getLogger("export.exportXml").log(Level.INFO, "All tables exported succesfully.");

    } catch (Exception e) {
      Logger.getLogger("export.exportXml").log(Level.SEVERE, "ERROR in exporting XML file " + outputFileName + ": ", e);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Exception e) {
          Logger.getLogger("export.write").log(Level.SEVERE, "ERROR closing outputfile ", e);
        }
      }
    }

  }

  public StringWriter exportXml(String replaceInClause) {

    StringWriter sw = new StringWriter();

    try {

      // create dataset
      QueryDataSet myDataSet = new QueryDataSet(connection);

      // loop all tables
      for (int i = 0; i < tableList.size(); i++) {

        String sqlSelect = (String) sqlSelectList.get(i);
        String tableName = (String) tableList.get(i);
        String sqlClause = (String) sqlClauseList.get(i);

        // replace possible tags

        if (replaceInClause == null) {
          replaceInClause = "";
        }
        StringTokenizer token = new StringTokenizer(replaceInClause, ",");
        while (token.hasMoreElements()) {
          String tmp = (String) token.nextElement();
          String tag = tmp.substring(0, tmp.indexOf("="));
          String value = tmp.substring(tmp.indexOf("=") + 1);

          sqlClause = sqlClause.replaceAll(tag, value);
        }

        // create statement
        String sqlStatement = sqlSelect + " " + tableName + " " + sqlClause;

        // execute statement
        Logger.getLogger("export.exportXml").log(Level.FINER, "Using sql statement: " + sqlStatement);
        myDataSet.addTable((String) tableList.get(i), sqlStatement);
        Logger.getLogger("export.exportXml").log(Level.INFO, "EXPORTING: " + (String) tableList.get(i));

      }

      // write result into a stringWriter
      FlatXmlDataSet.write(myDataSet, sw);
      // System.out.println("All tables exported succesfully.");
      Logger.getLogger("export.exportXml").log(Level.INFO, "All tables exported succesfully.");

    } catch (Exception e) {
      Logger.getLogger("export.exportXml").log(Level.SEVERE, "ERROR in exporting XML file ", e);
    }

    return sw;
  }

  private void closeCon() {
    try {
      // close connection
      connection.close();
    } catch (Exception e) {

      Logger.getLogger("export.closeCon").log(Level.SEVERE, "ERROR while closing connection: ", e);

    }
  }

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) {

    if (args.length == 6) {

      ETLCExport des = new ETLCExport(args[0], args[1], args[2], args[3]);
      des.exportXml(args[5], args[4]);
      // close connection
      des.closeCon();
    } else {
      String tmp = "";
      for (int i = 0; i < args.length; i++) {
        tmp += args[i];
      }

      System.out.println("Wrong number of attributes in " + tmp + "\n Usage: \n\t" + "url\t" + "userName\t"
          + "password\t" + "dbDriverName\t" + "outputfile\t" + "replaceString\n");
    }

  }

}