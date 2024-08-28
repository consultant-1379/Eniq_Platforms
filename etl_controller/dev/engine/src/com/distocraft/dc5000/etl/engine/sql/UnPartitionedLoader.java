package com.distocraft.dc5000.etl.engine.sql;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.ericsson.eniq.common.CommonUtils;
import com.ericsson.eniq.exception.ConfigurationException;
import com.ericsson.eniq.exception.FileException;

/**
 * TODO intro <br>
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * <td><b>Default</b></td>
 * </tr>
 *  <tr>
 * <td>Table name</td>
 * <td>tablename</td>
 * <td>Defines the tablename of the loaded data.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Techpack</td>
 * <td>techpack</td>
 * <td>Defines the teckpack of the loaded data.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>checkpoint</td>
 * <td>Parameter in loadTable command. This parameter is read first from static properties (PartitionedLoader.checkpoint) then from normal action parameters and if still no value default is used.</td>
 * <td>OFF</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>notify_rows</td>
 * <td>Parameter in loadTable command. This parameter is read first from static properties (PartitionedLoader.notify_rows) then from normal action parameters and if still no value default is used.</td>
 * <td>100000</td>
 * </tr>
 * <tr>
 * <td>Pattern</td>
 * <td>pattern</td>
 * <td>Defines RegExp mask to filter files from input directory (raw dir).</td>
 * <td>.+\.txt</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>dateformat</td>
 * <td>Dafines the dateformat (simpleDateFormat) to parse the date (datadate) found at the end of the loadDatafile.</td>
 * <td>yyyy-MM-dd</td>
 * </tr>
 * <tr>
 * <td>Directory</td>
 * <td>dir</td>
 * <td>Defines the input directory in etldata/'meastype'/.</td>
 * <td>raw</td>
 * </tr>
 * <td>Load template</td>
 * <td>Action_contents column</td>
 * <td>Defines the velocity template for the actual sql clause.</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table> <br>
 * <br>

 * @author lemminkainen
 */
public class UnPartitionedLoader extends Loader {

  private static final String DEFAULT_DATA_FILE_MASK = ".+\\.txt";


  public UnPartitionedLoader(final Meta_versions version, final Long collectionSetId, final Meta_collections collection,
      final Long transferActionId, final Long transferBatchId, final Long connectId, final RockFactory rockFact, final ConnectionPool connectionPool,
      final Meta_transfer_actions trActions, final SetContext sctx, final Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions, sctx, clog);

    final String where = this.getTrActions().getWhere_clause();

    whereProps = TransferActionBase.stringToProperties(where);

    if (whereProps == null) {
      whereProps = new Properties();
      whereProps.setProperty("where", where);
    }

    if (!whereProps.contains("checkpoint") || whereProps.getProperty("checkpoint").equalsIgnoreCase("")) {
      whereProps.setProperty("checkpoint", StaticProperties.getProperty("UnPartitionedLoader.checkpoint", "OFF"));
    }

    if (!whereProps.contains("notify_rows") || whereProps.getProperty("notify_rows").equalsIgnoreCase("")) {
      whereProps.setProperty("notify_rows", StaticProperties.getProperty("UnPartitionedLoader.notify_rows", "100000"));
    }

    notifyTypeName = this.tablename;
    
  }

  public void initializeLoggers() {

    final String logname = log.getName() + ".UnPartitionedLoader";

    log = Logger.getLogger(logname);

    final String logname_pfx = logname.substring(logname.indexOf("."));

    fileLog = Logger.getLogger("file." + logname_pfx + ".UnPartitionedLoader");
    sqlLog = Logger.getLogger("sql." + logname_pfx + ".UnPartitionedLoader");
    sqlErrLog = Logger.getLogger("sqlerror." + logname_pfx + ".UnPartitionedLoader");

  }
  
  /**
   * @see com.distocraft.dc5000.etl.engine.sql.Loader#getTableToFileMap()
   */
  protected Map<String, List<String>> getTableToFileMap() throws ConfigurationException, FileException {
    final String tableName = whereProps.getProperty("tablename");
    if (tableName == null || tableName.length() <= 0) {
    	throw new ConfigurationException("ETL set parameters","tablename",ConfigurationException.Reason.MISSING);
    }
    final Map<String, List<String>> fileMap = new HashMap<String, List<String>>();

    final String refilter = whereProps.getProperty("pattern", DEFAULT_DATA_FILE_MASK);

    // create a filter that filters out unwanted files
    final FilenameFilter filter = new FilenameFilter() {

      public boolean accept(final File dir, final String name) {
        return name.matches(refilter);
      }
    };

    // check files that need to be loaded
    final String directory = TransferActionBase.resolveSysProperties(whereProps.getProperty("dir"));
    final File _dir = new File(directory);
    final int mountPoints = CommonUtils.getNumOfDirectories(log);
    final List<File> searchDirectories = expandEtlPaths(_dir);
    if(searchDirectories.isEmpty()){
      log.fine("Nothing found for " +_dir.getPath() +" with "+mountPoints+" mount point(s)");
    }

    for (File sdir : searchDirectories) {
      final File[] files = sdir.listFiles(filter);
      final List<String> dirFiles = new ArrayList<String>();
      if (files != null) {
        log.info("Found " + files.length + " files in " + sdir.getPath());
        for (File file : files) {
          dirFiles.add(file.toString());
        }
      }
      if (!dirFiles.isEmpty()) {
        if(fileMap.containsKey(tableName)){
          final List<String> sofar = fileMap.get(tableName);
          sofar.addAll(dirFiles);
        } else {
          fileMap.put(tableName, dirFiles);
        }
      }
    }
    return fileMap;
  }

  protected void fillVelocityContext(final VelocityContext context) {
    final String dateStr = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    context.put("DATE", dateStr);
    context.put("MEASTYPE", whereProps.getProperty("tablename"));
    context.put("CHECKPOINT", whereProps.getProperty("checkpoint"));
    context.put("NOTIFY_ROWS", whereProps.getProperty("notify_rows"));
    context.put("TECHPACK", whereProps.getProperty("techpack"));
  }

  protected void updateSessionLog() {
  }  
}
