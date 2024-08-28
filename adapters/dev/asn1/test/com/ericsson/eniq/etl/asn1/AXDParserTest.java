package com.ericsson.eniq.etl.asn1;

import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;
import com.distocraft.dc5000.etl.parser.ParseSession;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.etl.parser.Transformer;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ssc.rockfactory.RockFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AXDParserTest {

  private static final File baseTestDir = new File(System.getProperty("java.io.tmpdir"), "axdparser");

  @BeforeClass
  public static void beforeClass() {
    delete(baseTestDir);
    mkdirs(baseTestDir);
  }

  @AfterClass
  public static void afterClass() {
    delete(baseTestDir);
  }

  @Test
  public void testParseRealFile() throws Exception {

    try {
      final Field dfCache = MeasurementFileImpl.class.getDeclaredField("dfCache");
      dfCache.setAccessible(true);
      dfCache.set(null, null);
    } catch (Throwable t) {
      fail("Failed to reset MeasurementFileImpl DataFormatCache instance");
    }

    final List<DItem> al = new ArrayList<DItem>();
    final DItem di1 = new DItem("JVM_TIMEZONE", 1, "JVM_TIMEZONE", "", "int", 0, 0);
    final DItem di2 = new DItem("vendorTag", 2, "vendorTag", "", "varchar", 32, 0);
    final DItem di3 = new DItem("nodeType", 3, "nodeType", "", "varchar", 32, 0);
    al.add(di1);
    al.add(di2);
    al.add(di3);
    final DFormat df2 = new DFormat("tfid", "foldName", "", "foldName", "transformerid_tfid_1");
    df2.setItems(al);
    final Map<String, DFormat> hm = new HashMap<String, DFormat>();
    hm.put("if2_loadTable_1", df2);
    DataFormatCache.testInitialize(hm, null, null, null);
    final TransformerCache transformerCache = new TransformerCache();
    final Field transformers = transformerCache.getClass().getDeclaredField("transformers");
    transformers.setAccessible(true);
    final Map<String, Transformer> tCache = new HashMap<String, Transformer>();
    tCache.put("transformerid_tfid_1", new Transformer() {
      @Override
      public void transform(final Map data, final Logger clog) throws Exception {
      }

      @Override
      public void addDebugger(final ParserDebugger parserDebugger) {
      }

      @Override
      public List getTransformations() {
        return null;
      }
    });
    transformers.set(transformerCache, tCache);

    final File realFile = new File("C:\\cc_storage\\eniq_eeipca_platform_common\\adapters\\dev\\asn1\\doc\\axd_sample\\V2_172.31.248.2_loadTable_1_37.pdr");
    final AXDParser parser = new AXDParser();
    parser.init(null, "techpack", "", "", "worker");
    final File techPackDir = new File(baseTestDir, "out" + File.separator + "techpack");
    mkdirs(techPackDir);
    final Properties prop = new Properties();
    prop.setProperty("baseDir", baseTestDir.getPath());
    prop.setProperty("interfaceName", "if2");
    try {
      MeasurementFileImpl.setTestMode(true);
      final Constructor sourceFileCons = SourceFile.class.getDeclaredConstructor(new Class[]{
        File.class, Properties.class, RockFactory.class, RockFactory.class, ParseSession.class, ParserDebugger.class,
        Logger.class});
      sourceFileCons.setAccessible(true);
      final SourceFile sourceFile = (SourceFile) sourceFileCons.newInstance(realFile, prop, null, null, null, null, null);
      parser.parse(sourceFile, "techpack", "setType", "setName");
      final Field f = parser.getClass().getDeclaredField("MeasurementFile");
      f.setAccessible(true);
      final MeasurementFile mFile = (MeasurementFile) f.get(parser);
      mFile.close();
      final File expectedFile = new File(techPackDir, "foldName_worker");
      assertTrue("Parser output file was not found: " + expectedFile.getPath(), expectedFile.exists());

      final BufferedReader reader = new BufferedReader(new FileReader(expectedFile));
      String line;
      int linesRead = 0;
      while ((line = reader.readLine()) != null) {
        assertEquals("Parsed info not correct", "+0100\tloadTable_1\tAXD301Backbone\t", line);
        linesRead++;
      }
      reader.close();
      assertEquals("Number of parsed records not correct", 12, linesRead);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void mkdirs(final File dir) {
    if (!dir.exists() && !dir.mkdirs()) {
      fail("Failed to create required dir " + dir.getPath());
    }
  }

  private static boolean delete(final File file) {
    if (!file.exists()) {
      return true;
    }
    if (file.isFile()) {
      return file.delete();
    } else {
      final File[] contents = file.listFiles();
      if (contents != null) {
        for (File f : contents) {
          if (!delete(f)) {
            return false;
          }
        }
      }
      return file.delete();
    }
  }
}
