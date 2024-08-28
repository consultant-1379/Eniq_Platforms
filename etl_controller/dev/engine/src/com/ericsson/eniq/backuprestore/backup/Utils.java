package com.ericsson.eniq.backuprestore.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import com.distocraft.dc5000.repository.cache.BackupConfigurationCache;

/**
 * Implementation for the common methods for topology and aggregation backup
 * 
 * @author xhussho
 *
 */

public class Utils {

	public static String backupDir = "/eniq/flex_data_bkup";
	public static String savePath = "/var/tmp/table_info";
	public static String FilePath = "/var/tmp/tablebackedup.txt";
	public static String Script = "/eniq/sw/bin/backupdata.bsh";
	private static List<String> DIMtblList = new ArrayList<String>();
	private static List<String> finalDIMtblList = new ArrayList<String>();
	Logger log = null;
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;

	public Utils(final Logger parentlog, Connection dwh_conn) {
		conn = dwh_conn;
		log = Logger.getLogger(parentlog.getName() + ".Utils");

	}

	public boolean checkData(String tableName) {
		boolean check = false;
		try {
			final String sql = "Select count(*) from " + tableName;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String count = rs.getString(1);
				if (!(Integer.parseInt(count) == 0)) {
					check = true;
				}
			}
		} catch (SQLException e) {
			if (!e.getSQLState().equals("42S02"))
				log.warning("SQL Exception caught while fetching data from " + tableName + e.getMessage());
		} catch (Exception e) {
			log.warning("Exception caught while checking data " + e.getMessage());
		}
		finally{
			try{
				if(rs != null)
				rs.close();
			}
			catch(Exception e){
				log.warning("Could not close ResultSet" + e.getMessage());
			}
			try{
				if(stmt != null)
				stmt.close();
			}
			catch(Exception e){
				log.warning("Could not close Statement" + e.getMessage());
			}
			
		}
		return check;
	}

	/**
	 * Method for checking for which all DIM MO's topology 2 weeks backup is
	 * enabled.
	 * 
	 * @author xhussho
	 * @throws IOException
	 */

	public List<String> preCheck(){
		String DIMTable = null;
		DIMtblList = BackupConfigurationCache.getBackupTypeNames();
		for (String s : DIMtblList) {		
			if (s.contains("_CURRENT_DC")) {
				DIMTable = s.replaceAll("_CURRENT_DC", "");
				log.log(Level.FINEST, "Checking if data available in the table : " + DIMTable);
				boolean chk = (checkData(DIMTable));
				

				if (chk) {
					if ((!finalDIMtblList.contains(DIMTable))) {
						finalDIMtblList.add(DIMTable);
					}
				}
			}
		}
		log.log(Level.INFO, "Number of Topology Tables with data : " + finalDIMtblList.size());
		FileWriter writer = null;
		try {
			writer = new FileWriter(savePath);
			for (String str : finalDIMtblList) {
				writer.write(str);
				writer.write("\n");
			}
		} catch (IOException e) {
			log.warning("Error while writing data into Dim File" + e.getMessage());
		}
		finally{
		try {
			if(writer != null)
			writer.close();
		} catch (IOException e) {
			log.warning("Could not close Writer" + e.getMessage());
		}
		}
		return finalDIMtblList;
	}

	/**
	 * Method for creating directories for topology backup if already not
	 * created
	 * 
	 * @author xhussho
	 * @param dimTableList
	 * @throws IOException
	 */

	public void createDir(List<String> dimTableList){

		for (String mo : dimTableList)

		{
			String backupDirfull = backupDir + File.separator + mo;
			if (!new File(backupDirfull).exists()) {
				boolean result = false;

				try {
					result = new File(backupDirfull).mkdirs();

				} catch (SecurityException se) {

					log.log(Level.WARNING, "Exception while creating directories", se);
				}
				if (result) {
					log.log(Level.FINE, "Directory created for : " + mo);

				} else {
					log.log(Level.WARNING, "Error while creating directory for : " + mo);
				}
			}
		}
	}

	// }

	/**
	 * Method for zipping the backed up files
	 * 
	 * @author xhussho
	 */

	public void gzipIt(List<String> ziplist) {

		GZIPOutputStream gzos = null;
		FileInputStream in = null;
		for (String s : ziplist) {
			String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			String OUTPUT_GZIP_FILE = backupDir + File.separator + s + File.separator + s + "_" + timeStamp + ".gz";
			String SOURCE_FILE = backupDir + File.separator + s + File.separator + s + "_" + timeStamp + ".txt";
			File file = new File(SOURCE_FILE);
			// String SOURCE_FILE = s + ".txt";
			byte[] buffer = new byte[1024];

			try {

				gzos = new GZIPOutputStream(new FileOutputStream(OUTPUT_GZIP_FILE));

				in = new FileInputStream(SOURCE_FILE);
				
				int len;
				if(file.exists()){
				while ((len = in.read(buffer)) > 0) {
					gzos.write(buffer, 0, len);

				}		
				}else{
					log.log(Level.INFO, "File does not exist " + s);
				}
			} catch (IOException ex) {
				log.log(Level.WARNING, "Exception while zipping the files for " + s + ex.getMessage());
				ex.printStackTrace();
			} finally {
				try {
					in.close();
					gzos.finish();
					gzos.close();
				} catch (IOException e) {
					log.warning("Could not close GZIPOutputStream" + e.getMessage());
				}

			}
			//Deletion of previous day files
			try{
				file.delete();
			}catch(Exception e){
				log.log(Level.WARNING, "Exception caught while deleting files " + e.getMessage());
			}
		}
	}
	
	public static String compression(String filename)
	{
		String gzip = filename +".gz";
		byte[] buffer = new byte[1024];
        String def=null;
	     try{

	    	GZIPOutputStream gzos =
	    		new GZIPOutputStream(new FileOutputStream(gzip)){
	    		{
	    			this.def.setLevel(Deflater.BEST_COMPRESSION);
	    		}
	    	};

	        FileInputStream in =
	            new FileInputStream(filename);

	        int len;
	        while ((len = in.read(buffer)) > 0) {
	        	gzos.write(buffer, 0, len);
	        }

	        in.close();
	        gzos.finish();
	    	gzos.close();
	    	return gzip;
	    	

	    }catch(IOException ex){
	    	return def;
	    }
	     
	}
		

}
