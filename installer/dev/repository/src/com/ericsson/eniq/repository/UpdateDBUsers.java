package com.ericsson.eniq.repository;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;

public class UpdateDBUsers {

  private UpdateDBUsers() {

  }

  public static int updateMetaDatabases(final String user, final String oldPassword,
      final String newPassword) throws IOException, SQLException, RockException {

    int numberOfRowsupdated = 0;
    if (user == null || oldPassword == null || newPassword == null) {
      return numberOfRowsupdated;
    }

    final List<Meta_databases> databases = DBUsersGet.getMetaDatabases("ALL", "ALL");
    for (Meta_databases m : databases) {
      if (oldPassword.equals(m.getPassword()) && user.equalsIgnoreCase(m.getUsername())) {
        final RockFactory etlRep = getRockFactoryObject();
        final Meta_databases metaDatabaseObject = getNewMetaDataBaseObject(etlRep);
        final Meta_databases whereMdb = getNewMetaDataBaseObject(etlRep);
        metaDatabaseObject.setPassword(newPassword);
        whereMdb.setUsername(user);
        whereMdb.setPassword(oldPassword);
        numberOfRowsupdated = metaDatabaseObject.updateDB(false, whereMdb);
        break;
      }
    }
    return numberOfRowsupdated;
  }

  /**
   * For bypassing PMD.
   */
  private static Meta_databases getNewMetaDataBaseObject(final RockFactory etlRep) {
    return new Meta_databases(etlRep);
  }

  /**
   * @return
   * @throws IOException
   * @throws RockException
   * @throws SQLException
   * @throws Exception
   */
  private static RockFactory getRockFactoryObject() throws IOException, SQLException, RockException {
    String engUser = null;
    String engPass = null;
    String url = null;
    String driver = null;
    Properties props = null;

    final String confDir = System.getProperty("CONF_DIR", "/eniq/sw/conf");

    props = new ETLCServerProperties(confDir + File.separator + "ETLCServer.properties");

    engUser = props.getProperty(ETLCServerProperties.DBUSERNAME);
    engPass = props.getProperty(ETLCServerProperties.DBPASSWORD);
    url = props.getProperty(ETLCServerProperties.DBURL);
    driver = props.getProperty(ETLCServerProperties.DBDRIVERNAME);

    return new RockFactory(url, engUser, engPass, driver, "DBUsersScript", true);

  }

}
