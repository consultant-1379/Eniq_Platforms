package com.distocraft.dc5000.repository.cache;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;


/**
 * Parser and loader specific cache implementation for accessing DataFormats
 * 
 * @author lemminkainen
 * 
 */
public class DataFormatCache {

  private static final Logger LOG = Logger.getLogger("etlengine.repository.DataFormatCache");

  private static String defaultSchema = "dwhrep.";
  static {
    defaultSchema = System.getProperty(defaultSchema, defaultSchema);
  }

  /**
   * Used for testing only....
   * @param newSchema The DWHREP schema name, defaults to 'dwhrep.'
   */
  public static void resetSchema(final String newSchema){
    defaultSchema = newSchema;
    System.setProperty("dwhrep.", newSchema);

    GET_INTERFACES = _get_interfaces_sql(newSchema);
    GET_ITEMS = _get_items_sql(newSchema);
  }

  private static String _get_interfaces_sql(final String __schema){
    return "select di.interfacename, im.tagid, im.dataformatid, df.foldername, im.transformerid"
      + " from datainterface di, interfacemeasurement im, dataformat df"
      + " where di.interfacename = im.interfacename and im.dataformatid = df.dataformatid"
      + " and di.status = 1 and im.status = 1"
      + " and df.versionid in (select versionid from "+__schema+"tpactivation where status = 'ACTIVE')"
      + " ORDER BY im.dataformatid";
  }

  private static String _get_items_sql(final String __schema){
    return " SELECT di.dataname, di.colnumber, di.dataid, di.process_instruction, di.dataformatid, di.datatype, di.datasize, di.datascale,"
                      + " COALESCE("
                      + " (SELECT 1 FROM MeasurementCounter mc WHERE di.dataname = mc.dataname AND df.typeid = mc.typeid),"
                      + " (SELECT 1 FROM ReferenceColumn rc WHERE di.dataname = rc.dataname AND df.typeid = rc.typeid AND uniquekey = 0),"
                      + " 0) AS is_counter "
                      + " FROM "+__schema+"dataformat df "
                      + " JOIN "+__schema+"dataitem di ON df.dataformatid = di.dataformatid"
					  + " WHERE df.versionid in (select versionid from "+defaultSchema+"tpactivation where status = 'ACTIVE')";
  }

  // HM33299 etogust
  // Take only dataformats from active techpacks in order to guarantee that 
  // dataformats/dataitems can only be used from active TPs
  private static String GET_INTERFACES = _get_interfaces_sql(defaultSchema);

  private static String GET_ITEMS = _get_items_sql(defaultSchema);
  
  private static final String GET_NON_PARSER = "SELECT dataformatid FROM DataFormat WHERE dataformatid NOT IN (SELECT dataformatid FROM InterfaceMeasurement)"
      + "and VERSIONID in (select VERSIONID from TPActivation)";

  private String dburl = null;
  private String dbusr = null;
  private String dbpwd = null;

  
  private Map<String, DFormat> it_map = null;

  private Map<String, List<DFormat>> id_map = null;

  private Set<String> if_names = null;

  private Map<String, DFormat> folder_map = null;
  
  private Map<String, DFormat> tag_map = null;

  private static DataFormatCache dfc = null;

  private DataFormatCache() {
  }

  public static void initialize(final RockFactory rock, final String dburl, final String dbusr, final String dbpwd) {
    initialize(rock.getDriverName(), dburl, dbusr, dbpwd);
  }
  public static void initialize(final String driver, final String dburl, final String dbusr, final String dbpwd) {

    dfc = new DataFormatCache();

    try {

      LOG.fine("Initializing...");

      Class.forName(driver);

      dfc.dburl = dburl;
      dfc.dbusr = dbusr;
      dfc.dbpwd = dbpwd;

      LOG.config("Repository: " + dfc.dburl);

      dfc.revalidate();

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }

  
  public static void initialize(final RockFactory rock) {

    dfc = new DataFormatCache();

    try {

      LOG.fine("Initializing...");

      final Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwhrep");
      selO.setType_name("USER");

      final Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      final List<Meta_databases> dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        LOG.severe("dwhrep database not correctly defined in etlrep.Meta_databases.");
      }

      final Meta_databases dwhrep = dbs.get(0);

      Class.forName(dwhrep.getDriver_name());

      dfc.dburl = dwhrep.getConnection_string();
      dfc.dbusr = dwhrep.getUsername();
      dfc.dbpwd = dwhrep.getPassword();

      LOG.config("Repository: " + dfc.dburl);

      dfc.revalidate();
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Fatal initialization error", e);
    }
  }

  public static DataFormatCache getCache() {
    return dfc;
  }

  /**
   * Method to init this cache. Testing purposes only.
   */
  public static void testInitialize(final Map<String, DFormat> it_map, final Map<String, List<DFormat>> id_map,
      final Set<String> if_names, final Map<String, DFormat> folder_map) {

    dfc = new DataFormatCache();

    dfc.it_map = it_map;
    dfc.id_map = id_map;
    dfc.if_names = if_names;
    dfc.folder_map = folder_map;

  }

  public DFormat getFormatWithTagID(final String interfaceName, final String tagID) {
    return it_map.get(interfaceName + "_" + tagID);
  }

  public DFormat getFormatWithTagIDWithoutInterface(final String tagID) {
	    return tag_map.get(tagID);
	  }
  
  public List<DFormat> getFormatWithFormatID(final String interfaceName, final String dataFormatID) {
    return id_map.get(interfaceName + "_" + dataFormatID);
  }
  
  public Vector<String> getInterfaceNames() {
    final Vector<String> v = new Vector<String>();
    v.addAll(if_names);
    Collections.sort(v);

    return v;
  }
  
  /**
   * This method returns the data format object (DFormat) of the measurement type who's load file would be put into directory folderName
   * 
   * @param folderName  String with directory name of a load file (not the path).
   * @return  The DFormat that maps to folderName
   */
  public DFormat getFormatWithFolderName(final String folderName){
    DFormat dFormat = folder_map.get(folderName);
    final String CURRENT_DC = "_CURRENT_DC";
    if(dFormat == null && folderName.endsWith(CURRENT_DC)) {
      final String shortName = folderName.substring(0, folderName.length() - CURRENT_DC.length());
      dFormat = folder_map.get(shortName);
    }
    
    final String PREV = "_PREV";
    if(dFormat == null && folderName.endsWith(PREV)) {
      final String shortName_1 = folderName.substring(0, folderName.length() - PREV.length());
      dFormat = folder_map.get(shortName_1);
    }
    
    return dFormat;
  }

  public boolean isAnInterface(final String ifname) {
    return if_names.contains(ifname);
  }

  public void revalidate() throws Exception {

    LOG.fine("Revalidating...");


    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;


    try {
      con = DriverManager.getConnection(dburl, dbusr, dbpwd);
	  
      LOG.finer("Querying interfaces");
      ps = con.prepareStatement(GET_INTERFACES);

       rs = ps.executeQuery();
    
      final Set<String> if_names2 = new HashSet<String>();
      final Map<String, DFormat> it_map2 = new HashMap<String, DFormat>();
      final Map<String, List<DFormat>> id_map2 = new HashMap<String, List<DFormat>>();
      final Map<String, DFormat> folder_map2 = new HashMap<String, DFormat>();
      final Map<String, DFormat> tag_map2 = new HashMap<String, DFormat>();

      while (rs.next()) {
        final DFormat df = new DFormat(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));

         LOG.finest("Interface " + df.getInterfaceName() + " dataformat " + df.getDataFormatID() + " folder " + df.getFolderName() + " tagid    "+df.getTagID());



        if_names2.add(df.getInterfaceName());

        it_map2.put(df.getInterfaceName() + "_" + df.getTagID(), df);

        List<DFormat> dfs = id_map2.get(df.getInterfaceName() + "_" + df.getDataFormatID());
        if (dfs == null) {
          dfs = new ArrayList<DFormat>();
          id_map2.put(df.getInterfaceName() + "_" + df.getDataFormatID(), dfs);
        }

        dfs.add(df);
        
        folder_map2.put(df.getFolderName(), df);
        
        tag_map2.put(df.getTagID(), df);

      }

      LOG.finer("Found " + folder_map2.size() + " interfaces");

      rs.close();
      rs = null;
      ps.close();
      ps = null;

      LOG.finer("Querying items");



      ps = con.prepareStatement(GET_ITEMS);
      rs = ps.executeQuery();

      //TR HL94231 - EEIKBE. 
      //comment out rs.next() as it's reading the first result, 
      //then in while loop, it's discarded. Therefore the first 
      //result from query never gets put into cache.
      //rs.next();
      


      final Map<String, List<DItem>> map = new HashMap<String, List<DItem>>();
      while (rs.next()) {
        List<DItem> list = map.get(rs.getString(5));
        if (list == null) {
          list = new ArrayList<DItem>();
        }
          
        final DItem di = new DItem(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getInt(9));
        list.add(di);
        map.put(rs.getString(5), list);
      }

      for (DFormat df : it_map2.values()) {
        final List<DItem> list = map.get(df.getDataFormatID());
        if (list != null) {
          Collections.sort(list);
          LOG.finer("Dataformat " + df.getDataFormatID() + ": " + list.size() + " items");
          df.setItems(list);

        }

      }



      // Populate foldermap (the one that loader uses) for types that do not have ENIQ parser
     
      LOG.finer("Querying Non-Parser ones");

 
      ps = con.prepareStatement(GET_NON_PARSER);
      rs = ps.executeQuery();
      while (rs.next()) {
        final String dataformatID = rs.getString(1);
        
        final List<DItem> list = map.get(dataformatID);
        if (list != null) {
          final int index = dataformatID.indexOf(":")+1;
          final String folderName = dataformatID.substring(dataformatID.indexOf(":",index)+1, dataformatID.lastIndexOf(":"));
      
          LOG.finest("Found folderName \"" + folderName + "\"");
    
          if(!folder_map2.containsKey(folderName)) {
            Collections.sort(list);
            final DFormat df = new DFormat("Folder:" + folderName, "", folderName, folderName, null);
            df.setItems(list);
            folder_map2.put(folderName, df);

            LOG.finer("Foldername " + df.getFolderName() + ": " + list.size() + " items");
          }
        }       
      }

      if_names = if_names2;
      it_map = it_map2;
      id_map = id_map2;
      folder_map = folder_map2;
      tag_map = tag_map2;
      
      LOG.info("Revalidation succesfully performed. " + if_names.size() + " formats and " + folder_map2.size() + " folders found");

    } catch (Exception e) {
      LOG.log(Level.WARNING, "Refresh failed", e);
    } finally {

      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          LOG.log(Level.WARNING, "Cleanup error", e);
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          LOG.log(Level.WARNING, "Cleanup error", e);
        }
      }

      if (con != null) {
        try {
          con.close();
        } catch (Exception e) {
          LOG.log(Level.WARNING, "Cleanup error", e);
        }
      }

    }
  }
}
