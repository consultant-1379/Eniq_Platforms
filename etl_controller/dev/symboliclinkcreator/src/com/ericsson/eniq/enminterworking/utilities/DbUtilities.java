package com.ericsson.eniq.enminterworking.utilities;


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.ericsson.eniq.repository.ETLCServerProperties;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class DbUtilities {

	private static Logger log;
	private static ETLCServerProperties etlcProp ;
	private static RockFactory etlrep = null;
	private static RockFactory rf = null;

	
	// dbType can be either dwh or dwhrep
	public static RockFactory connectTodb(RockFactory etlrep, String dbType) {
//		log = Logger.getLogger("symboliclinkcreator.nat");
//		log.finest("Etlrep    "+etlrep+"   dbtypr"+dbType);
		ResultSet rs = null;
		Connection conn = etlrep.getConnection();
		try {
			Statement s = conn.createStatement();
			rs = s.executeQuery(
					"select username, connection_string, password, driver_name from META_DATABASES where connection_name = '"
							+ dbType + "' and type_name = 'USER'");
			while (rs.next()) {
				if (dbType.equals("dwh")) {
					etlcProp.put("dwhdb_username", rs.getString("username"));
					etlcProp.put("dwhdb_url", rs.getString("connection_string"));
					etlcProp.put("dwhdb_pwd", rs.getString("password"));
					etlcProp.put("dwhdb_drivername", rs.getString("driver_name"));
				} else {
					etlcProp.put("dwhrepdb_username", rs.getString("username"));
					etlcProp.put("dwhrepdb_url", rs.getString("connection_string"));
					etlcProp.put("dwhrepdb_pwd", rs.getString("password"));
					etlcProp.put("dwhrepdb_drivername", rs.getString("driver_name"));
				}
			}
		} catch (SQLException e) {
//			log.warning("Exception at connectTodb "+e.getMessage());
		}

		try {
			if (dbType.equals("dwh")) {
				log.finest("dwhdb_url property value:"+etlcProp.getProperty("dwhdb_url"));
				rf = new RockFactory(etlcProp.getProperty("dwhdb_url"), etlcProp.getProperty("dwhdb_username"),
						etlcProp.getProperty("dwhdb_pwd"), etlcProp.getProperty("dwhdb_drivername"), "NodeAssignment",
						true);
			} else {
				rf = new RockFactory(etlcProp.getProperty("dwhrepdb_url"), etlcProp.getProperty("dwhrepdb_username"),
						etlcProp.getProperty("dwhrepdb_pwd"), etlcProp.getProperty("dwhrepdb_drivername"),
						"NodeAssignment_" + dbType, true);
			}
//			log.finest("Connection Succesful");

		} catch (SQLException | RockException e) {
//			log.warning("Error in connecting to DWHDB in connectTodb method ");
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
//				log.warning("Error in closig DWHDB connection in connectTodb method ");
			}
		}

		return rf;

	}

	public static RockFactory connectToEtlrep() {
//		log = Logger.getLogger("symboliclinkcreator.nat");
		try {
			etlcProp = new ETLCServerProperties("/eniq/sw/conf/ETLCServer.properties");
//			log.warning("connect to etlrep");
		} catch (IOException e) {
			// e.printStackTrace();
//			log.warning("Cannot find the ETLCServerProperties file in connectToEtlrep method ");
		}
		try {
			etlrep = new RockFactory(etlcProp.getProperty(ETLCServerProperties.DBURL),
					etlcProp.getProperty(ETLCServerProperties.DBUSERNAME),
					etlcProp.getProperty(ETLCServerProperties.DBPASSWORD),
					etlcProp.getProperty(ETLCServerProperties.DBDRIVERNAME), "NodeAssignment", true);
		} catch (SQLException | RockException e) {
//			log.warning("some exception while connecting to ETLREP in connectToEtlrep method "+e.getMessage());
			
		}
//		log.finest("returning etlrep object  "+etlrep);
		return etlrep;
	}
	
	

}
