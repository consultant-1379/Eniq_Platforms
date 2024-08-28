package com.distocraft.dc5000.etl.mediation.jdbc;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class JDBCMediationAction extends TransferActionBase {

  private Logger log = Logger.getLogger("mediator.jdbc");

  private Logger flog = Logger.getLogger("mediator.jdbc.file");

  private Properties conf = null;

  private String selectClause = null;

  public JDBCMediationAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact,
      Meta_transfer_actions trActions, SetContext setcontext) throws EngineMetaDataException {

    try {

      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);

      String techPack = collSet.getCollection_set_name();
      String set_type = collection.getSettype();
      String set_name = collection.getCollection_name();

      log = Logger.getLogger("etl." + techPack + "." + set_type + "." + set_name + ".mediator.jdbc");
      flog = Logger.getLogger("file." + techPack + "." + set_type + "." + set_name + ".mediator.jdbc");

    } catch (Exception e) {
      log.log(Level.WARNING, "Error initializing loggers", e);
    }

    String act_cont = trActions.getAction_contents();

    conf = new Properties();

    if (act_cont != null && act_cont.length() > 0) {

      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
        conf.load(bais);
        bais.close();
        log.finest("Configuration read");
      } catch (Exception e) {
        throw new EngineMetaDataException("Error reading config", new String[] { "" }, e, this, this.getClass()
            .getName());
      }
    }

    selectClause = trActions.getWhere_clause();

  }

  public void execute() throws EngineException {

    String drv = conf.getProperty("jdbc_drv");
    String url = conf.getProperty("jdbc_url");
    String usr = conf.getProperty("db_user");
    String pwd = conf.getProperty("db_pwd");
    String tdr = conf.getProperty("tempDir");
    String dir = conf.getProperty("outdir");
    String hdr = conf.getProperty("write_hdr");
    String fpf = conf.getProperty("fileprefix");

    if (drv == null || url == null || usr == null || pwd == null || tdr == null || dir == null || fpf == null
        || selectClause == null) {
      log.severe("Unsufficient configuration. Exiting...");
      return;
    }

    try {
      Class.forName(drv);
    } catch (ClassNotFoundException cnfe) {
      log.severe("JDBC Driver class \"" + drv + "\" can't be found.");
      return;
    }

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    BufferedWriter bo = null;

    try {
      con = DriverManager.getConnection(url, usr, pwd);

      log.finer("Got connection. Executing statement...");

      stmt = con.createStatement();

      rs = stmt.executeQuery(selectClause);

      if (rs.next()) {

        log.finer("Statement executed. Opening file...");

        File tDir = new File(tdr);

        if (!tDir.isDirectory()) {
          log.info("Temp Directory does not exist. Trying to create...");
          tDir.mkdirs();
        }

        if (!tDir.isDirectory() || !tDir.canWrite()) {
          log.severe("Temp Directory can't be written. exiting...");
          return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

        File tFile = new File(tDir, fpf + "_" + sdf.format(new Date()) + ".txt");
        bo = new BufferedWriter(new FileWriter(tFile));

        log.finer("File opened.");

        String col_delim = conf.getProperty("column_delimiter", "\t");

        ResultSetMetaData rsmd = rs.getMetaData();

        int colCount = rsmd.getColumnCount();

        if ("true".equals(hdr)) {

          log.finer("Writing header line...");

          for (int i = 1; i <= colCount; i++) {
            bo.write(rsmd.getColumnName(i));
            bo.write(col_delim);
          }
          bo.write("\n");

          log.finer("Header line written.");

        }

        log.finer("Writing data...");

        int rows = 0;

        do {

          for (int i = 1; i <= colCount; i++) {

            String tm = rs.getString(i);
            
            if(!rs.wasNull() && tm != null)
              bo.write(rs.getString(i));
            
            bo.write(col_delim);

          }
          bo.write("\n");

          rows++;

        } while (rs.next());

        log.finer(rows + " rows written.");

        flog.info(tFile.getName() + " written.");

        bo.flush();

        bo.close();

        bo = null;

        log.finer("Moving file from temporary dir to output directory");

        File oDir = new File(dir);

        if (!oDir.isDirectory()) {
          log.info("Out Directory does not exist. Trying to create...");
          tDir.mkdirs();
        }

        if (!oDir.isDirectory() || !oDir.canWrite()) {
          log.severe("Out Directory can't be written. exiting...");
          return;
        }

        File oFile = new File(oDir, fpf + "_" + sdf.format(new Date()) + ".txt");

        boolean moveSuccess = tFile.renameTo(oFile);

        if (!moveSuccess) {

          log.finer("renameTo failed. Moving with memory copy");

          log.severe("Parser was forced to move outDir -> loaderDir via memory copy. Check configuration!");
          log.severe("outDir and loaderDir directories should be in same partition or performance is ruined.");

          try {

            InputStream in = new FileInputStream(tFile);
            OutputStream out = new FileOutputStream(oFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
              out.write(buf, 0, len);
            }
            in.close();
            out.close();

            tFile.delete();

            moveSuccess = true;

          } catch (Exception e) {
            log.log(Level.WARNING, "Move with memory copy failed", e);
          }

        }

      } else {
        log.info("Query returned no results.");
      }

    } catch (Exception e) {
      log.log(Level.WARNING, "Mediation failed exceptionally", e);
      throw new EngineException("Exception in Mediation", new String[] { "" }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_SYSTEM);
    } finally {

      if (bo != null) {
        try {
          bo.close();
        } catch (Exception e) {
        }
      }

      if (rs != null) {
        try {
          stmt.close();
        } catch (Exception e) {
        }
      }

      if (stmt != null) {
        try {
          stmt.close();
        } catch (Exception e) {
        }
      }

      if (con != null) {
        try {
          con.close();
        } catch (Exception e) {
        }
      }

    }

  }
}