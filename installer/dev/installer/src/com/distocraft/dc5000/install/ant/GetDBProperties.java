package com.distocraft.dc5000.install.ant;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;

/**
 * This is a custom made ANT task that gets the database properties like
 * database url, database username, database password and database driver. This
 * task gets one parameter called <i>name</i> and the ANT properties created by
 * this task are:<br/>
 * <ul>
 * <li><i>name</i>DatabaseUrl</li>
 * <li><i>name</i>DatabaseUsername</li>
 * <li><i>name</i>DatabasePassword</li>
 * <li><i>name</i>DatabaseDriver</li>
 * <li><i>name</i>DatabaseDBAUsername</li>
 * <li><i>name</i>DatabaseDBAPassword</li>
 * </ul>
 * 
 * @author berggren
 */
public class GetDBProperties extends CommonTask {

	private String name = new String("");

	private String type = new String("USER");

	private String propertiesFilepath = new String("");

	/**
	 * This function starts the checking of the installation file.
	 */
	public void execute() throws BuildException {

		Connection con = null;

		try {

			Map<String, String> etlrepDatabaseConnectionDetails = getDatabaseConnectionDetails();

			if (this.name.equalsIgnoreCase("etlrep")
					&& this.type.equalsIgnoreCase("user")) {
				// Set the connection details directly from
				// ETLCServer.properties file.
				getProject().setNewProperty(
						this.name + "DatabaseUrl",
						etlrepDatabaseConnectionDetails
								.get("etlrepDatabaseUrl").toString());
				getProject().setNewProperty(
						this.name + "DatabaseUsername",
						etlrepDatabaseConnectionDetails.get(
								"etlrepDatabaseUsername").toString());
				getProject().setNewProperty(
						this.name + "DatabasePassword",
						etlrepDatabaseConnectionDetails.get(
								"etlrepDatabasePassword").toString());
				getProject().setNewProperty(
						this.name + "DatabaseDriver",
						etlrepDatabaseConnectionDetails.get(
								"etlrepDatabaseDriver").toString());

				return;
			}

			// Connect actually to database

			// Create the connection to the etlrep.
			con = createETLRepConnection(etlrepDatabaseConnectionDetails);

			// Set the database connection properties to ANT task properties.
			this.setDBProperties(con);

		} catch (BuildException be) {
			throw be;
		} catch (Exception e) {
			throw new BuildException("Exceptional failure", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception ex) {
				}
			}
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private Connection createETLRepConnection(
			Map<String, String> etlrepDatabaseConnectionDetails)
			throws BuildException {
		try {
			Driver driver = (Driver) Class.forName(
					etlrepDatabaseConnectionDetails.get("etlrepDatabaseDriver")
							.toString()).newInstance();

			Properties p = new Properties();
			p.put("user",
					etlrepDatabaseConnectionDetails.get(
							"etlrepDatabaseUsername").toString());
			p.put("password",
					etlrepDatabaseConnectionDetails.get(
							"etlrepDatabasePassword").toString());
			p.put("REMOTEPWD", ",,CON=PLAT_INST");

			Connection con = driver.connect(etlrepDatabaseConnectionDetails
					.get("etlrepDatabaseUrl").toString(), p);

			// This should never happen...
			if (con == null) {
				throw new Exception(
						"DB driver initialized null connection object");
			}
			return con;

		} catch (Exception e) {
			throw new BuildException("Database connecting to etlrep failed", e);
		}
	}

	/**
	 * This function sets the database connection details to ANT task
	 * properties. If etlrep database is requested, database properties are
	 * retrieved from ETLCServer.properties.
	 */
	private void setDBProperties(Connection con) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("SELECT CONNECTION_STRING, USERNAME, PASSWORD, DRIVER_NAME FROM META_DATABASES WHERE CONNECTION_NAME=? AND TYPE_NAME=?");
			ps.setString(1, this.name);
			ps.setString(2, this.type);

			rs = ps.executeQuery();

			if (rs.next()) {

				getProject().setNewProperty(this.name + "DatabaseUrl",
						rs.getString("CONNECTION_STRING"));
				getProject().setNewProperty(this.name + "DatabaseUsername",
						rs.getString("USERNAME"));
				getProject().setNewProperty(this.name + "DatabasePassword",
						rs.getString("PASSWORD"));
				getProject().setNewProperty(this.name + "DatabaseDriver",
						rs.getString("DRIVER_NAME"));

				// Separate DBA properties, as ant property override is not
				// supported.
				if (getType().equalsIgnoreCase("DBA")) {
					getProject().setNewProperty(
							this.name + "DatabaseDBAUsername",
							rs.getString("USERNAME"));
					getProject().setNewProperty(
							this.name + "DatabaseDBAPassword",
							rs.getString("PASSWORD"));
				}

			} else {
				throw new BuildException("No such database \"" + this.name
						+ "\" of type \"" + type + "\"");
			}

		} catch (BuildException be) {
			throw be;
		} catch (Exception e) {
			throw new BuildException(
					"Failed to load DB properties from etlrep.", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
