
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.repository.ETLCServerProperties;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;




/**
 * A servlet for starting universe update application with webstart. The doGet()
 * method of the servlet returns a jnlp file, with which the update application
 * can be started.
 * 
 * The jnlp file is generated from a template on each get request, since a list 
 * available ebs techpacks and their base techpacks must be retrieved from the 
 * server.
 * 
 * @author epiituo
 *
 */
public class WebstartServlet extends HttpServlet{

  private static final long serialVersionUID = 1L;
  
  private static final String TEMPLATE_FILE_NAME = "/JNLP-INF/UNIVERSEUPDATEINSTALLER_TEMPLATE.jnlp";
  private static final String CONF_DIR = "CONF_DIR";
  private static final String ENIQ_SW_CONF = "/eniq/sw/conf";
  private static final String EBSMANAGER_PROPERTIES = "EBSManager.properties";
  private static final String ETLC_SERVER_PROPERTIES = "ETLCServer.properties";
  
  
  //private static final Logger log = Logger.getLogger(WebstartServlet.class);
  
  private static final String TECHPACKS_PROPERTY_PLACEHOLDER = "@@TECHPACKS@@";
  private static final String BASETECHPACKS_PROPERTY_PLACEHOLDER = "@@BASETECHPACKS@@";
  
  private RockFactory etlRockFactory;
  private RockFactory dwhRockFactory;

  
  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    // First, open the database connection.
    try {
    	
      initializeDatabaseConnection();
        
    } catch (IOException e) {
      e.printStackTrace();
      return;
    } catch (SQLException e) {
      e.printStackTrace();
      return;
    } catch (RockException e) {
      e.printStackTrace();
      return;
    }
    
    // Set the header information or the response
    resp.setContentType("text/plain");
    resp.setHeader("Content-Disposition", "attachment; filename=UNIVERSEUPDATEINSTALLER.jnlp");
    
    // Write the body of the response, based on the jnlp template file.
    final PrintWriter out = resp.getWriter();
    
    final URL templateFile = this.getServletContext().getResource(TEMPLATE_FILE_NAME);
    final InputStreamReader isr = new InputStreamReader(templateFile.openStream());
    final BufferedReader templateFileReader = new BufferedReader(isr); 
    
    String line = templateFileReader.readLine();
    while(line != null) {
      if (isTechpacksPropertyPlaceholder(line)) {
        out.println(createTechpacksProperty());
      }
      else if (isBaseTechpacksPropertyPlaceholder(line)) {
        out.println(createBaseTechpacksProperty());
      }
      else {
        out.println(line);
      }
      
      line = templateFileReader.readLine();
    }
    
    out.close();
    try {
    	this.etlRockFactory.getConnection().close();
    	this.dwhRockFactory.getConnection().close();
    } catch (SQLException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
    }

    
  }
  
  private boolean isTechpacksPropertyPlaceholder(final String line) {
    return line.trim().equals(TECHPACKS_PROPERTY_PLACEHOLDER);
  }

  private boolean isBaseTechpacksPropertyPlaceholder(final String line) {
    return line.trim().equals(BASETECHPACKS_PROPERTY_PLACEHOLDER);
  }
  
  /**
   * Creates the property tag that defines ebs techpacks in the jnlp file.
   * 
   * @return
   * @throws IOException
   */
  private String createTechpacksProperty() throws IOException {
    final StringBuffer techpacksProperty = new StringBuffer();
    techpacksProperty.append("<property name=\"jnlp.univerupdateinstaller.techpacks\" value=\"");
    
    final String[] techpackIds = getVersionIds();
    for(int i=0; i<techpackIds.length; ++i) {
      techpacksProperty.append(techpackIds[i]);
      techpacksProperty.append(",");
    }
    // Remove the last comma
    if(techpackIds.length > 0) {
      techpacksProperty.deleteCharAt(techpacksProperty.length() - 1);
    }
    
    techpacksProperty.append("\"/>");
    return techpacksProperty.toString();
  }
  
  /**
   * Creates the property tag that defines base techpacks in the jnlp file.
   * 
   * @return
   */
  private String createBaseTechpacksProperty() {
    final StringBuffer baseTechpacksProperty = new StringBuffer();
    baseTechpacksProperty.append("<property name=\"jnlp.univerupdateinstaller.basetechpacks\" value=\"");
    
    // Get the techpack names
    String[] techpackIds;
    try {
      techpackIds = getVersionIds();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    
    for(int i=0; i<techpackIds.length; ++i) {
      final String baseTechpackId = getBaseTechpackVersionId(techpackIds[i]);

      baseTechpacksProperty.append(baseTechpackId);
      baseTechpacksProperty.append(",");
    }
    // Remove the last comma
    if(techpackIds.length > 0) {
      baseTechpacksProperty.deleteCharAt(baseTechpacksProperty.length() - 1);
    }
    
    baseTechpacksProperty.append("\"/>");
    return baseTechpacksProperty.toString();
  }
  
  /**
   * Returns the list of ebs techpacks available on the server.
   * 
   * @return
   * @throws IOException
   */
  private String[] getVersionIds() throws IOException {
    // Load the ebs manager properties
    final Properties properties = getEBSManagerProperties();
    // Get the ebsDirs string, and use it to parse the techpack names.
    final String ebsDirs = properties.getProperty("ebsDirs");
    final String[] ebsDirsTokens = ebsDirs.split(",");
    String[] techpackVersionIds = new String[ebsDirsTokens.length];
    for(int i=0; i<ebsDirsTokens.length; ++i) {
      final int techpackNameEndIndex = ebsDirsTokens[i].indexOf("=");
      final String techpackName = ebsDirsTokens[i].substring(0, techpackNameEndIndex);
      techpackVersionIds[i] = getVersionIdForName(techpackName);
    }
    
    return techpackVersionIds;
  }
  
  /**
   * Returns the version ID for the techpack name given as parameter. The ID is
   * retrieved from the TPActivations table in the database.
   * 
   * @param techpackName
   * @return versionId
   */
  private String getVersionIdForName(final String techpackName) {
    final Tpactivation tpActivationQuery = new Tpactivation(this.dwhRockFactory);
    tpActivationQuery.setTechpack_name(techpackName);
    TpactivationFactory tpF;
    try {
      tpF = new TpactivationFactory(this.dwhRockFactory, tpActivationQuery);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
    final Vector<Tpactivation> tpActivationResult = tpF.get();
    if(tpActivationResult.size() == 1) {
      return tpActivationResult.firstElement().getVersionid();
    }
    else if(tpActivationResult.size() < 1) {
      System.err.println("No version ID was found for " + techpackName);
      return null;
    }
    else { //tpActivationResult.size() > 1
      System.err.println("More than one version ID was found for " + techpackName);
      return null;
    }
  }

  /**
   * Returns the base techpack id for a techpack id, given as a parameter.
   * 
   * @param techpackName
   * @return
   */
  private String getBaseTechpackVersionId(final String techpackVersionId) {

    final Versioning versioningQuery = new Versioning(this.dwhRockFactory);
    versioningQuery.setVersionid(techpackVersionId);
    VersioningFactory vF;
    try {
      vF = new VersioningFactory(this.dwhRockFactory, versioningQuery);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
    return vF.get().firstElement().getBasedefinition();
  }
  
  /**
   * Initializes database connection.
   * 
   * @throws SQLException
   * @throws RockException
   */
  private void initializeDatabaseConnection() throws IOException, SQLException, RockException {
    // First,get the information required to connect to the etl database.
    final Properties etlcServerProperties = getETLCServerProperties(); 
    final String etlDbUrl = etlcServerProperties.getProperty("ENGINE_DB_URL");
    final String etlDbUser = etlcServerProperties.getProperty("ENGINE_DB_USERNAME");
    final String etlDbPassword = etlcServerProperties.getProperty("ENGINE_DB_PASSWORD");
    final String etlDbDriver = etlcServerProperties.getProperty("ENGINE_DB_DRIVERNAME");
    
    // Connect to the etl database
    this.etlRockFactory = new RockFactory(etlDbUrl, etlDbUser, etlDbPassword, etlDbDriver, "WebstartServlet", true);
    
    final Meta_databases mdbQuery = new Meta_databases(this.etlRockFactory);
    mdbQuery.setConnection_name("dwhrep");
    mdbQuery.setType_name("USER");
    final Meta_databasesFactory mdbf = new Meta_databasesFactory(this.etlRockFactory, mdbQuery);
    final Meta_databases mdbResult = (Meta_databases) mdbf.get().firstElement();
    
    // Get the information required to connect to the dwh database.
    final String dwhDbUrl = mdbResult.getConnection_string();
    final String dwhDbUser = mdbResult.getUsername();
    final String dwhDbPassword = mdbResult.getPassword();
    final String dwhDbDriver = mdbResult.getDriver_name();
    
    // Connect to dwh database
    this.dwhRockFactory = new RockFactory(dwhDbUrl, dwhDbUser, dwhDbPassword, dwhDbDriver, "WebstartServlet", true);
  }
  
  private Properties getETLCServerProperties() throws IOException {
    String etlcServerPropertyFile = System.getProperty(CONF_DIR, ENIQ_SW_CONF);
    if (!etlcServerPropertyFile.endsWith(File.separator)) {
      etlcServerPropertyFile += File.separator;
    }
    etlcServerPropertyFile += ETLC_SERVER_PROPERTIES;
    final Properties etlcServerProperties = new ETLCServerProperties(etlcServerPropertyFile);
    return etlcServerProperties;
  }
  
  private Properties getEBSManagerProperties() throws IOException {
    String ebsManagerPropertyFile = System.getProperty(CONF_DIR, ENIQ_SW_CONF);
    if (!ebsManagerPropertyFile.endsWith(File.separator)) {
      ebsManagerPropertyFile += File.separator;
    }
    ebsManagerPropertyFile += EBSMANAGER_PROPERTIES;
    final Properties ebsManagerProperties = new ETLCServerProperties(ebsManagerPropertyFile);
    return ebsManagerProperties;
  }
  
}
