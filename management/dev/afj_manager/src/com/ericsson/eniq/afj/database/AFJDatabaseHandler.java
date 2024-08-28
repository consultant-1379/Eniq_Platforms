/**
 * 
 */
package com.ericsson.eniq.afj.database;

import static com.ericsson.eniq.afj.common.PropertyConstants.DEFAULT_DWHDBA_TYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.DEFAULT_DWH_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.DEFAULT_DWH_TYPE;




import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DWHDBA_TYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DWH_NAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DWH_REDIRECT;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DWH_TYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ENGINE_DB_DRIVERNAME;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ENGINE_DB_PASSWORD;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ENGINE_DB_URL;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_ENGINE_DB_USERNAME;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.exception.AFJException;

/**
 * Singleton class to manage the database connections for dwhrep, etlrep and dwh.
 * 
 * @author esunbal
 * 
 */
public class AFJDatabaseHandler {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  private RockFactory etlrep;

  private RockFactory dwhrep;

  private RockFactory dwh;

  private RockFactory dbadwh;

  private static AFJDatabaseHandler afjDatabase;

  private static final boolean autoCommit = false;

  private static final String connectionString = "AFJManager";

  private AFJDatabaseHandler() {

  }

  public static synchronized AFJDatabaseHandler getInstance() {
    if (afjDatabase == null) {
      afjDatabase = new AFJDatabaseHandler();
    }
    return afjDatabase;
  }

  /**
   * Setter for the databaseHandler. used only for junit testing
   * 
   * @param databaseHandler
   */
  public static synchronized void setInstance(final AFJDatabaseHandler databaseHandler) {
    afjDatabase = databaseHandler;
  }

  /**
   * @param type
   * @param etlRepRock
   * @param name
   * @return
   * @throws SQLException
   * @throws RockException
   * @throws Exception
   */
  protected RockFactory getRockFactory(final String type, final RockFactory etlRepRock, final String name)
      throws SQLException, RockException, Exception {
    RockFactory result = null;
    final Meta_databases md_cond = new Meta_databases(etlRepRock);
    md_cond.setType_name(type);
    md_cond.setConnection_name(name);
    final Meta_databasesFactory md_fact = new Meta_databasesFactory(etlRepRock, md_cond);
    @SuppressWarnings("unchecked")
    final Vector<Meta_databases> dbs = md_fact.get();
    if (dbs.isEmpty()) {
      throw new Exception("Could not find in Meta_database (" + type + "|" + name + ")");
    }
    for (Meta_databases db : dbs) {      
        final String dbUrl = PropertiesUtility.getProperty(PROP_DWH_REDIRECT, db.getConnection_string());
        result = new RockFactory(dbUrl, db.getUsername(), db.getPassword(), db.getDriver_name(), connectionString,
            autoCommit);
    }
    return result;
  }

  /**
   * Method to return a rockfactory for dwh.
   * 
   * @return
   * @throws AFJException
   */
  public RockFactory getDwh() throws AFJException {
    if (dwh == null) {
      getEtlrep();
      try {
        final String dwhType = PropertiesUtility.getProperty(PROP_DWH_TYPE, DEFAULT_DWH_TYPE);
        final String dwhName = PropertiesUtility.getProperty(PROP_DWH_NAME, DEFAULT_DWH_NAME);
        dwh = getRockFactory(dwhType, etlrep, dwhName);
        log.info("Connected to dwhdb");
      } catch (SQLException e) {
        throw new AFJException(e.getMessage());
      } catch (RockException e) {
        throw new AFJException(e.getMessage());
      } catch (Exception e) {
        throw new AFJException(e.getMessage());
      }
    }
    return dwh;
  }

  /**
   * Method to return a rockfactory object of dwh with dba as user
   * 
   * @return
   * @throws AFJException
   */
  public RockFactory getDbaDwh() throws AFJException {
    if (dbadwh == null) {
      getEtlrep();
      try {
        final String dwhType = PropertiesUtility.getProperty(PROP_DWHDBA_TYPE, DEFAULT_DWHDBA_TYPE);
        final String dwhName = PropertiesUtility.getProperty(PROP_DWH_NAME, DEFAULT_DWH_NAME);
        dbadwh = getRockFactory(dwhType, etlrep, dwhName);
        log.info("Connected to dwhdb as DBA");
      } catch (SQLException e) {
        throw new AFJException(e.getMessage());
      } catch (RockException e) {
        throw new AFJException(e.getMessage());
      } catch (Exception e) {
        throw new AFJException(e.getMessage());
      }
    }
    return dbadwh;
  }

  /**
   * Method to return a rockfactory object of dwhrep.
   * 
   * @return
   * @throws AFJException
   */
  public RockFactory getDwhrep() throws AFJException {
    if (dwhrep == null) {
      getEtlrep();
      try {
        final String dwhrepType = "USER";
        final String dwhrepName = "dwhrep";
        dwhrep = getRockFactory(dwhrepType, etlrep, dwhrepName);
        log.info("Connected to dwhrep");
      } catch (Exception e) {
        throw new AFJException("Unable to initialize database connection for dwhrep:" + e.getMessage());
      }
    }
    return dwhrep;
  }

  /**
   * Method to return a rockfactory object of etlrep.
   * 
   * @return
   * @throws AFJException
   */
  public RockFactory getEtlrep() throws AFJException {
    if (etlrep == null) {
      final String databaseUsername = PropertiesUtility
          .getProperty(PROP_ENGINE_DB_USERNAME);
      final String databasePassword = PropertiesUtility
          .getProperty(PROP_ENGINE_DB_PASSWORD);
      final String databaseUrl = PropertiesUtility.getProperty(PROP_ENGINE_DB_URL);
      final String databaseDriver = PropertiesUtility.getProperty(PROP_ENGINE_DB_DRIVERNAME);

      try {
        etlrep = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver, connectionString,
            autoCommit);
        log.info("Connected to etlrep");
      } catch (Exception e) {
        throw new AFJException(
            "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.", e);
      }
    }
    return etlrep;
  }

  /**
   * Method to commit all the transactions.
   * 
   * @param commit
   * @throws AFJException
   */
  public void commitTransaction(final boolean commit) throws AFJException {
    try {
      if (dwhrep != null) {
        if (commit) {
          dwhrep.commit();
        } else {
          dwhrep.rollback();
        }
      }

      if (etlrep != null) {
        if (commit) {
          etlrep.commit();
        } else {
          etlrep.rollback();
        }
      }

      if (dwh != null) {
        if (commit) {
          dwh.commit();
        } else {
          dwh.rollback();
        }
      }

      if (dbadwh != null) {
        if (commit) {
          dbadwh.commit();
        } else {
          dbadwh.rollback();
        }
      }
    } catch (SQLException se) {
      throw new AFJException("SQLException in committing the transaction. Message:" + se.getMessage() + " Exception:"
          + se.toString());
    }
  }

  /**
   * Method to close the open db connections
   * 
   * @throws AFJException
   */
  public void closeConnections() throws AFJException {
    if (dwhrep != null) {
      closeConnection(dwhrep.getConnection());
      dwhrep = null;
    }
    if (etlrep != null) {
      closeConnection(etlrep.getConnection());
      etlrep = null;
    }
    if (dwh != null) {
      closeConnection(dwh.getConnection());
      dwh = null;
    }
    if (dbadwh != null) {
      closeConnection(dbadwh.getConnection());
      dbadwh = null;
    }
  }

  /**
   * Method to close a specific open connection.
   * 
   * @param con
   * @throws AFJException
   */
  private void closeConnection(final Connection con) throws AFJException {
    final boolean connOpen;
    try {
      connOpen = !con.isClosed();
    } catch (SQLException e) {
      throw new AFJException("Error in checking whether connection is open:" + e.toString());
    }
    if (connOpen) {
      try {
        final String dbUrl = con.getMetaData().getURL();
        con.close();
        log.info("Connection to " + dbUrl + " closed.");
      } catch (SQLException e) {
        log.info("Unable to close connection on Connection.close() : " + e.toString());
        throw new AFJException("Unable to close connection on Connection.close()");
      }
    }
  }

  // /**
  // * Method to commit transactions and close the connection.
  // * @param commit
  // * @throws AFJException
  // */
  // public void close(final boolean commit) throws AFJException{
  // if (dwhrep != null) {
  // closeConnection(dwhrep.getConnection(), commit);
  // dwhrep = null;
  // }
  // if (etlrep != null) {
  // closeConnection(etlrep.getConnection(), commit);
  // etlrep = null;
  // }
  // if (dwh != null) {
  // closeConnection(dwh.getConnection(), commit);
  // dwh = null;
  // }
  // if (dbadwh != null) {
  // closeConnection(dbadwh.getConnection(), commit);
  // dbadwh = null;
  // }
  // }

  // /**
  // * Method to close the connection.
  // * @param conn
  // * @param commit
  // * @throws AFJException
  // */
  // private void closeConnection(final Connection conn, final boolean commit) throws AFJException{
  // final boolean connOpen;
  // try {
  // connOpen = !conn.isClosed();
  // } catch (SQLException e) {
  // throw new AFJException("Error in checking whether connection is open:"+e.toString());
  // }
  // if (connOpen) {
  // try {
  // if (commit) {
  // log.info("Comitting transaction");
  // conn.commit();
  // } else {
  // log.info("Rolling back transaction");
  // conn.rollback();
  // }
  // } catch (SQLException e) {
  // throw new AFJException("SQL exception in closing connection:"+e.toString());
  // }
  // try {
  // final String dbUrl = conn.getMetaData().getURL();
  // conn.close();
  // log.info("Connection to " + dbUrl + " closed.");
  // } catch (SQLException e) {
  // log.info("Ignoring error on Connection.close() : " + e.toString());
  // }
  // }
  // }

  // public List<AFJMeasurementType> populateDefaultTagsCacheOld(String techpackName, RockFactory dwhrep)throws
  // AFJException{
  //
  // List<AFJMeasurementType> returnList = new ArrayList<AFJMeasurementType>();
  // try {
  // Defaulttags where = new Defaulttags(dwhrep);
  // // Defaulttags defaultTags = new Defaulttags(dwhrep,where);
  // final DefaulttagsFactory factory = new DefaulttagsFactory(dwhrep, where);
  // final Vector<Defaulttags> defaultTagsList = factory.get();
  // if(defaultTagsList.isEmpty()){
  // throw new AFJException("There is no default tags found for "+techpackName);
  // }
  // System.out.println(defaultTagsList.size());
  // AFJMeasurementType measType = null;
  // for(Defaulttags dt:defaultTagsList){
  // if(dt.getDataformatid().startsWith(techpackName)){
  // measType = new AFJMeasurementType();
  // System.out.println("TagId:"+dt.getTagid());
  // measType.setTagName(dt.getTagid());
  // measType.setTypeName(dt.getDataformatid().substring(0, dt.getDataformatid().lastIndexOf(":")));
  // measType.setTypeNew(false);
  // returnList.add(measType);
  // }
  // }
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (RockException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // return returnList;
  //
  // }

  // public Map<String, List<String>> populateDefaultTagsCacheOld(String techpackName, RockFactory dwhrep)throws
  // AFJException{
  //
  // Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
  // List<String> counterList = null;
  //
  // try {
  // Defaulttags where = new Defaulttags(dwhrep);
  // // Defaulttags defaultTags = new Defaulttags(dwhrep,where);
  // final DefaulttagsFactory factory = new DefaulttagsFactory(dwhrep, where);
  // final Vector<Defaulttags> defaultTagsList = factory.get();
  // if(defaultTagsList.isEmpty()){
  // throw new AFJException("There is no default tags found for "+techpackName);
  // }
  // System.out.println(defaultTagsList.size());
  // for(Defaulttags dt:defaultTagsList){
  // if(dt.getDataformatid().startsWith(techpackName)){
  // System.out.println("TagId:"+dt.getTagid());
  // counterList = getMeasCounters(dt.getDataformatid().substring(0, dt.getDataformatid().lastIndexOf(":")));
  // returnMap.put(dt.getTagid(), counterList);
  // }
  // }
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (RockException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // return returnMap;
  //
  // }

  // public List<AFJMeasurementCounter> getMeasCounters(String queryTypeId) throws AFJException{
  // List<AFJMeasurementCounter> measCounterList = new ArrayList<AFJMeasurementCounter>();
  // getDwhrep();
  // Measurementcounter measCounter = new Measurementcounter(dwhrep);
  // measCounter.setTypeid(queryTypeId);
  //
  // try {
  // MeasurementcounterFactory factory = new MeasurementcounterFactory(dwhrep, measCounter);
  // List<Measurementcounter> dbMeasCounterList = factory.get();
  // if(dbMeasCounterList.isEmpty()){
  // throw new AFJException("No matching record found in MeasurementCounter table for typeId:"+queryTypeId);
  // }
  // AFJMeasurementCounter afjMeasCounter = null;
  // for(Measurementcounter mc: dbMeasCounterList){
  // afjMeasCounter = new AFJMeasurementCounter();
  // afjMeasCounter.setCounterName(mc.getDataname());
  // measCounterList.add(afjMeasCounter);
  // }
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (RockException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  //
  // return measCounterList;
  // }

  // public List<String> getMeasCounters(String queryTypeId) throws AFJException{
  // List<String> measCounterList = new ArrayList<String>();
  // getDwhrep();
  // Measurementcounter measCounter = new Measurementcounter(dwhrep);
  // measCounter.setTypeid(queryTypeId);
  //
  // try {
  // MeasurementcounterFactory factory = new MeasurementcounterFactory(dwhrep, measCounter);
  // List<Measurementcounter> dbMeasCounterList = factory.get();
  // if(dbMeasCounterList.isEmpty()){
  // throw new AFJException("No matching record found in MeasurementCounter table for typeId:"+queryTypeId);
  // }
  // for(Measurementcounter mc: dbMeasCounterList){
  // measCounterList.add(mc.getDataname());
  // }
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // } catch (RockException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  //
  // return measCounterList;
  // }

  // public void populateDefaultTagsCache(String techpackName, RockFactory dwhrep)throws AFJException{
  // String searchSql = "select tagid from defaulttags where dataformatid like '"+techpackName+"%'";
  // System.out.println("Search Query:"+searchSql);
  // Connection connection = dwhrep.getConnection();
  // Statement statement = null;
  // ResultSet rs = null;
  // try {
  // statement = connection.createStatement();
  // rs = statement.executeQuery(searchSql);
  // while(rs.next()){
  // System.out.println(rs.getString(1));
  // }
  //
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // finally{
  // try {
  // rs.close();
  // statement.close();
  // connection.close();
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // }

}
