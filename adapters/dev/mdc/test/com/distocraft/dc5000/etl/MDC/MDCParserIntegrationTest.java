package com.distocraft.dc5000.etl.MDC;

import com.distocraft.dc5000.common.DebugAdapterLog;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.parser.Main;
import com.ericsson.eniq.common.testutilities.DirectoryHelper;
import com.ericsson.eniq.etl.test.parser.ParserBaseTestCase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ssc.rockfactory.RockException;
import static com.ericsson.eniq.etl.test.parser.ParserBaseTestCase.ParserOutputFormat.ASCII;

/**
 * Test for different processing instructions below:
 * PEG                all
 * GAUGE              ?
 * PMRESVECTOR        NONE
 * VECTOR             testParse_VECTOR
 * CMVECTOR           testParse_CMVECTOR
 * UNIQUEVECTOR       testParse_UNIQUEVECTOR
 * COMPRESSEDVECTOR   testParse_COMPRESSEDVECTOR
 * <p/>
 * User: eeipca
 * Date: 11/05/12
 * Time: 10:40
 */
public class MDCParserIntegrationTest extends ParserBaseTestCase {
	
  private static final String DC_E_PARSER = "DC_E_PARSER";
  private static final String INTF_DC_E_PARSER = "INTF_DC_E_PARSER";

  private static Properties mdcRanParserProperties = null;

  private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), "MDCParserIntegrationTest");

  @After
  public void after() {
    deleteAfter();
  }


  @Before
  public void before() throws RockException, IOException, SQLException, URISyntaxException {
    DirectoryHelper.delete(TEMP_DIR); 
    setup(INTF_DC_E_PARSER, TEMP_DIR);
    mdcRanParserProperties = getMdcParserProperties(INTF_DC_E_PARSER, ASCII);
    initDirectories(TEMP_DIR, INTF_DC_E_PARSER);
    DirectoryHelper.mkdirs(mdcRanParserProperties.getProperty("baseDir"));
    DirectoryHelper.mkdirs(mdcRanParserProperties.getProperty("inDir"));
    DirectoryHelper.mkdirs(mdcRanParserProperties.getProperty("outDir"));
    sessionHandlerInit();
  }

  private static void deleteAfter() {
    if (Boolean.valueOf(fwkProperties.getProperty("afterclass.delete", "true"))) {
        DirectoryHelper.delete(TEMP_DIR);
    }
  }

  private File getRopFile(final String typeName) {
    final File fwkCfg = getFrameworkConfDir();

    final File dir = new File(fwkCfg, "DATA_FILES/" + typeName.toUpperCase());
    final File[] files = dir.listFiles();
    if (files != null && files.length > 0) {
      return files[0];
    }
    Assert.fail("No files found in " + dir.getPath());
    return null;
  }

//  @Test
//  public void testParse_COMPRESSEDVECTOR() throws Exception {
//    final File ropFile = getRopFile("COMPRESSEDVECTOR");
//    final Map<String, Integer> linesPerRop = new HashMap<String, Integer>();
//    linesPerRop.put("dc_e_parser_compressedvector", -1);
//    linesPerRop.put("dc_e_parser_compressedvector_v", -1);
//
//    final Map<String, Integer> countersPerRop = new HashMap<String, Integer>();
//    countersPerRop.put("dc_e_parser_compressedvector", -1);
//    countersPerRop.put("dc_e_parser_compressedvector_v", -1);
//
//    testParse(ropFile, linesPerRop, countersPerRop);
//  }

  @Test
  public void testParse_CMVECTOR() throws Exception {;
    final File ropFile = getRopFile("CMVECTOR");
    final Map<String, Integer> linesPerRop = new HashMap<String, Integer>();
    linesPerRop.put("dc_e_parser_cmvector", 2);
    linesPerRop.put("dc_e_parser_cmvector_v", 2);

    final Map<String, Integer> countersPerRop = new HashMap<String, Integer>();
    countersPerRop.put("dc_e_parser_cmvector", 0);
    countersPerRop.put("dc_e_parser_cmvector_v", 0);
    testParse(ropFile, linesPerRop, countersPerRop);
  }

  @Test
  public void testParse_UNIQUEVECTOR() throws Exception {
    final File ropFile = getRopFile("UNIQUEVECTOR");
    final Map<String, Integer> linesPerRop = new HashMap<String, Integer>();
    linesPerRop.put("dc_e_parser_uniquevector", 2);
    linesPerRop.put("dc_e_parser_uniquevector_v_pmres", 2);

    final Map<String, Integer> countersPerRop = new HashMap<String, Integer>();
    countersPerRop.put("dc_e_parser_uniquevector", 0);
    countersPerRop.put("dc_e_parser_uniquevector_v_pmres", 0);

    testParse(ropFile, linesPerRop, countersPerRop);
  }

  @Test
  public void testParse_VECTOR() throws Exception {
    final File ropFile = getRopFile("VECTOR");
    final Map<String, Integer> linesPerRop = new HashMap<String, Integer>();
    linesPerRop.put("dc_e_parser_vector", 2);
    linesPerRop.put("dc_e_parser_vector_v", 2);

    final Map<String, Integer> countersPerRop = new HashMap<String, Integer>();
    countersPerRop.put("dc_e_parser_vector", 0);
    countersPerRop.put("dc_e_parser_vector_v", 0);

    testParse(ropFile, linesPerRop, countersPerRop);
  }

  private void testParse(final File ropFile, final Map<String, Integer> linesPerFile, final Map<String, Integer> countersPerFile) throws Exception {
    final EngineCom engineCom = new EngineCom();
    final Main mainParserObject = new Main(mdcRanParserProperties, DC_E_PARSER, "", "",
          null, null, engineCom);
    final MDCParser mdcParser = new MDCParser();
    mdcParser.init(mainParserObject, DC_E_PARSER, "", "", "test-parser-worker");

    final File destDir = new File(DirectoryHelper.resolveDirVariable(mdcRanParserProperties.getProperty("inDir")));
    copyFile(ropFile, destDir);

    DebugAdapterLog.clear();
    final Map parserdMeasurementTypes = mainParserObject.parse();

    Assert.assertTrue("No data parsed!", parserdMeasurementTypes.containsKey("parsedMeastypes"));
    final Set<String> parsedTypes = (Set<String>) parserdMeasurementTypes.get("parsedMeastypes");   
   
    
    for (String key : linesPerFile.keySet()) {
    	if(parsedTypes.iterator().hasNext()){
    		//Added to avoid case sensitive
    		if(parsedTypes.contains(key.toUpperCase()))
    			Assert.assertTrue("Measurement Type '" + key + "' not parsed?!", parsedTypes.contains(key.toUpperCase()));
    		else
    			Assert.assertTrue("Measurement Type '" + key + "' not parsed?!", parsedTypes.contains(key));
    	}
    		
    }

    for (String type : parsedTypes) {
      final File loadFileDir = new File(DirectoryHelper.resolveDirVariable(mdcRanParserProperties.get("loaderDir") + "/" + type.toLowerCase() + "/raw"));
      final File[] generatedFiles = loadFileDir.listFiles();
      Assert.assertNotNull(generatedFiles);
      Assert.assertEquals("Wrong number of files generated for type " + type + " !", 1, generatedFiles.length);
      final BufferedReader reader = new BufferedReader(new FileReader(generatedFiles[0]));
      int lineCount = 0;
      while (reader.readLine() != null) {
        lineCount++;
      }
      final int expectedLineCount = linesPerFile.get(type.toLowerCase());
      Assert.assertEquals("Wrong number of rows generated to file " + generatedFiles[0].getName(), expectedLineCount, lineCount);
      final Map<String, String> data = DebugAdapterLog.ROP_COUNTERVOLUME_INFO.get(type.toUpperCase());
      final int sessionLogRowCount = Integer.valueOf(data.get("rowCount"));
      final int sessionLogCounterVolume = Integer.valueOf(data.get("counterVolume"));

      final int expectedCounterCount = countersPerFile.get(type.toLowerCase());
      Assert.assertEquals("Wrong number of rows sent to adapter session log for " + type, expectedLineCount, sessionLogRowCount);
      Assert.assertEquals("Wrong number of counters sent to adapter session log for " + type, expectedCounterCount, sessionLogCounterVolume);
    }
  }
}
