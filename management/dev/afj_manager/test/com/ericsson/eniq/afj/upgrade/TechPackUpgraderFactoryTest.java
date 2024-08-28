/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class TechPackUpgraderFactoryTest {
	
	private static Properties afjProperties;
	private static File AFJManagerProperties;
	private static File ETLCServerProperties;
	private static File StaticProperties;	

  private final Mockery context = new JUnit4Mockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  
  @BeforeClass
  public static void init() throws Exception{		
	  final String homeDir = System.getProperty("user.dir");
	  /* Create property file for database connection details */
	  AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
	  System.setProperty("CONF_DIR",System.getProperty("user.dir"));
	  AFJManagerProperties.deleteOnExit();
	  
	  String afjBasePath = "afjBasePath=" + homeDir; 
	  afjBasePath = afjBasePath.replace("\\", "/");	  
	  
	  System.out.println("afjBasePath:"+afjBasePath);
	  try {
		  final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
		  pw.write("stn.name=DC_E_STN\n");			  
		  pw.write("afj.supported.tps=DC_E_STN,DC_E_BSS\n");	
		  pw.write("bss.name=DC_E_BSS\n");
		  pw.write("ims.name=DC_E_IMS\n");	
		  pw.write("hss.name=DC_E_HSS\n");	
		  pw.write("mtas.name=DC_E_MTAS\n");	
		  pw.write(afjBasePath);
		  pw.close();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }

	  afjProperties = new Properties();
	  final FileInputStream fis = new FileInputStream(AFJManagerProperties);
	  afjProperties.load(fis);
	  fis.close();	 

	  /* Create property file for database connection details */
	  ETLCServerProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");		  
	  ETLCServerProperties.deleteOnExit();
	  try {
		  final PrintWriter pw = new PrintWriter(new FileWriter(ETLCServerProperties));
		  pw.write("ENGINE_DB_URL = jdbc:hsqldb:mem:testdb\n");
		  pw.write("ENGINE_DB_USERNAME = sa\n");
		  pw.write("ENGINE_DB_PASSWORD = \n");
		  pw.write("ENGINE_DB_DRIVERNAME = org.hsqldb.jdbcDriver\n");
		  pw.close();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	  
		/* Create property file for database connection details */
		StaticProperties = new File(System.getProperty("user.dir"), "static.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		StaticProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(StaticProperties));
			pw.write("sybaseiq.option.public.DML_Options5=0");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
  /**
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
    TechPackUpgraderFactory.setUpgrader(null);
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory#getUpgrader(String)throws AFJException}.
   */
  @Test
  public void testGetMockInstance() throws AFJException{
	final String tpName = "XYZ";
	final TechPackUpgrader mockTechPackUpgrader = context.mock(TechPackUpgrader.class);
    TechPackUpgraderFactory.setUpgrader(mockTechPackUpgrader);
    final TechPackUpgrader techPackUpgrader = TechPackUpgraderFactory.getUpgrader(tpName);
    assertTrue(techPackUpgrader == mockTechPackUpgrader); 
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory#getUpgrader(String)throws AFJException}.
   */
  @Test
  public void testGetSTNInstance() throws AFJException{
	final String tpName = "DC_E_STN";
    TechPackUpgraderFactory.setUpgrader(null);
    final TechPackUpgrader techPackUpgrader = TechPackUpgraderFactory.getUpgrader(tpName);
    assertTrue(techPackUpgrader != null); 
    assertTrue(techPackUpgrader instanceof STNUpgrader); 
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory#getUpgrader(String) throws AFJException}.
   */
  @Test
  public void testGetBSSInstance() throws AFJException{
	final String tpName = "DC_E_BSS";
    TechPackUpgraderFactory.setUpgrader(null);
    final TechPackUpgrader techPackUpgrader = TechPackUpgraderFactory.getUpgrader(tpName);
    assertTrue(techPackUpgrader != null); 
    assertTrue(techPackUpgrader instanceof BSSUpgrader); 
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory#getUpgrader(String) throws AFJException}.
   */
  @Test
  public void testGetIMSInstance() throws AFJException{
	final String tpName = "DC_E_IMS";
    TechPackUpgraderFactory.setUpgrader(null);
    final TechPackUpgrader techPackUpgrader = TechPackUpgraderFactory.getUpgrader(tpName);
    assertTrue(techPackUpgrader != null); 
    assertTrue(techPackUpgrader instanceof IMSUpgrader); 
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory#getUpgrader(String) throws AFJException}.
   */
  @Test
  public void testGetHSSInstance() throws AFJException{
	final String tpName = "DC_E_HSS";
    TechPackUpgraderFactory.setUpgrader(null);
    final TechPackUpgrader techPackUpgrader = TechPackUpgraderFactory.getUpgrader(tpName);
    assertTrue(techPackUpgrader != null); 
    assertTrue(techPackUpgrader instanceof HSSUpgrader); 
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory#getUpgrader(String) throws AFJException}.
   */
  @Test
  public void testGetMTASInstance() throws AFJException{
	final String tpName = "DC_E_MTAS";
    TechPackUpgraderFactory.setUpgrader(null);
    final TechPackUpgrader techPackUpgrader = TechPackUpgraderFactory.getUpgrader(tpName);
    assertTrue(techPackUpgrader != null); 
    assertTrue(techPackUpgrader instanceof MTASUpgrader); 
  }

}
