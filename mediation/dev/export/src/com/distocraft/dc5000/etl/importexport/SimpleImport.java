package com.distocraft.dc5000.etl.importexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.Vector;

import org.dbunit.database.DatabaseConnection;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.ericsson.eniq.repository.ETLCServerProperties;

public class SimpleImport extends DefaultHandler {

  private boolean verbose = false;

  private Connection con = null;

  public SimpleImport(String[] args) throws Exception {
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
      } else {
        // ignore
      }
    }

    String inFileName = args[args.length - 1];
    String dbname = args[args.length - 2];

    if (verbose) {
      System.out.println("Input file is " + inFileName);
    }

    if (verbose) {
      System.out.println("Database " + dbname);
    }

    con = initializeDBConnection(confFile, dbname);

    if (verbose) {
      System.out.println("Database connection initialized");
    }

    File inf = new File(inFileName);
    if (!inf.canRead()) {
      throw new IllegalArgumentException("Can't read inputfile");
    }

    DatabaseConnection connection = new DatabaseConnection(con);

    if (verbose) {
      System.out.println("Querying database...");
    }

    con.setAutoCommit(false);

    BufferedReader br = new BufferedReader(new FileReader(inf));

    try {

      XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
      xmlReader.setContentHandler(this);
      xmlReader.setErrorHandler(this);
      xmlReader.setEntityResolver(this);
      xmlReader.parse(new InputSource(br));

      con.commit();
      
      if (verbose) {
        System.out.println("Changes successfully committed");
      }
      
    } catch (Exception e) {
      con.rollback();
      throw e;
    } finally {
      if (verbose) {
        System.out.println("Closing...");
      }
      br.close();
      con.close();
    }

  }

  /**
   * Event handlers
   */
  @Override
  public void startDocument() {
  }

  @Override
  public void endDocument() throws SAXException {
    if (verbose) {
      System.out.println("Document parsed");
    }
  }

  /**
   * Handle element start
   */
  @Override
  public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException {

    if (!qName.equalsIgnoreCase("dataset")) {
      if (verbose) {
        System.out.println("Reading element " + qName);
      }

      if (atts.getLength() <= 0) {
        return;
      }
      PreparedStatement ps = null;
      try {
        StringBuffer st = new StringBuffer("INSERT INTO ").append(qName).append(" (");
        StringBuffer en = new StringBuffer(") VALUES (");

        for (int i = 0; i < atts.getLength(); i++) {
          st.append(atts.getQName(i));
          en.append("'").append(atts.getValue(i)).append("'");

          if ((i + 1) < atts.getLength()) {
            st.append(",");
            en.append(",");
          }
        }

        st.append(en).append(")");

        if (verbose) {
          System.out.println(st.toString());
        }

        ps = con.prepareStatement(st.toString());
        int res = ps.executeUpdate();
        ps.close();

        if (verbose) {
          System.out.println(res + " rows affected");
        }

      } catch (Exception e) {
        e.printStackTrace();
        throw new SAXException(e.getMessage());
      }finally {
        try {
          if (ps != null) {
            ps.close();
          }
        } catch (final Exception e) {
          System.out.println("Cleanup error - " + e.toString());
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String name, String qName) throws SAXException {
  }

  @Override
  public void characters(char ch[], int start, int length) {
  }

  private Connection initializeDBConnection(String confFile, String dbname) throws Exception {

    System.out.println("initializing db " + dbname);


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
      SimpleImport se = new SimpleImport(args);

    } catch (IllegalArgumentException e) {
      if (e.getMessage().length() > 0) {
        System.out.println("Error: " + e.getMessage());
      }
      else {
        System.out.println("Usage: import.sh [options] dbname inputFileName\n");
        System.out.println("\t-c confFile - Specify the ETLCEngine.properties file.");
        System.out.println("\t-v - Verbose output.");
      }
    } catch (Exception e) {
      System.err.println("Unrecovable error occured.");
      e.printStackTrace();
    }

  }

}
