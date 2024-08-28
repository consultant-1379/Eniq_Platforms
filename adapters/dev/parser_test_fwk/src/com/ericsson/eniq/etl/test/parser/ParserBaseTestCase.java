package com.ericsson.eniq.etl.test.parser;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.Transformer;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.ericsson.eniq.common.INIGet;
import com.ericsson.eniq.common.testutilities.DirectoryHelper;
import com.ericsson.eniq.common.testutilities.ddlutils.DatabaseTestCaseException;
import com.ericsson.eniq.exception.ConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import ssc.rockfactory.RockException;

/**
 * User: eeipca
 * Date: 11/05/12
 * Time: 11:53
 */
public class ParserBaseTestCase {

  //  private static final String viewPath = System.getProperty("cc.base", "/cc_storage/eniq_eeipca_stats120_sidebranch");
  protected static Properties fwkProperties = null;
  private static File fwkConfDir = null;
  private static final String PARSER_TEST_FWK_CONF = "parser_test_fwk.conf";
  public static File getFrameworkConfDir() {
    if (!fwkConfDir.exists()) {
      Assert.fail("Can't find conf directory " + fwkConfDir.getAbsolutePath());
    }
    return fwkConfDir;
  }

  protected static void initDirectories(final File baseDir, final String interfaceName) {
    final File sessionProperties = new File(DirectoryHelper.resolveDirVariable("${ETLDATA_DIR}/session/sessions.properties"));
    DirectoryHelper.createFile(sessionProperties);
    final File engineLogging = new File(baseDir, "engineLogging.properties");
    DirectoryHelper.createFile(engineLogging);
    System.out.println("");

    final String etlDir = DirectoryHelper.resolveDirVariable("${ETLDATA_DIR}");
    final File dataDir = new File(getFrameworkConfDir(), interfaceName);
    final File[] dataFormInfo = dataDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return !(name.startsWith("__") && name.endsWith("__.txt"));
      }
    });

    final INIGet iniReader = new INIGet();
    for (File tagFile : dataFormInfo) {
      try {
        final DFormat dFormat = DataFormatCacheBuilder.parseDFormat(tagFile, iniReader, interfaceName);
        if (dFormat == null) {
          continue;
        }
        final File rawDir = new File(etlDir, dFormat.getFolderName().toLowerCase() + "/raw");
        if (!rawDir.exists() && !rawDir.mkdirs()) {
          throw new DatabaseTestCaseException("Failed to create directory " + rawDir.getPath());
        }
      } catch (IOException e) {
        throw new DatabaseTestCaseException(e);
      }
    }
  }

  protected static void sessionHandlerInit() {
    try {
      SessionHandler.init();
    } catch (NoSuchFieldException e) {
      throw new DatabaseTestCaseException(e);
    } catch (ConfigurationException e) {
      throw new DatabaseTestCaseException(e);
    } catch (IOException e) {
      throw new DatabaseTestCaseException(e);
    }
  }

  private static void extractDataFiles(final File containerFile, final File toDir) {
    try {            
      final JarFile jar = new JarFile(containerFile.getPath());
      final Enumeration entries = jar.entries();
 
      while (entries.hasMoreElements()) {
        final JarEntry file = (JarEntry) entries.nextElement();
        final File f = new File(toDir + File.separator + file.getName());  
        if (file.isDirectory()) {
        	// if its a directory, create it        
          DirectoryHelper.mkdirs(f);
          continue;
        }
        final InputStream is = jar.getInputStream(file); // get the input stream
        final FileOutputStream fos = new FileOutputStream(f);
        try {
          while (is.available() > 0) {  // write contents of 'is' to 'fos'
            fos.write(is.read());
          }
        } finally {
          try {
            fos.close();
          } catch (IOException e) {/*--*/}
          try {
            is.close();
          } catch (IOException e) {/*--*/}
        }
      }
    } catch (IOException e) { 
      e.printStackTrace();
      throw new DatabaseTestCaseException(e);
    } catch (Throwable t) {
      throw new DatabaseTestCaseException(t);
    }
  }

  public static void setup(final String interfaceName,
                           final File baseDir) throws URISyntaxException, IOException, SQLException, RockException {
    
	  DirectoryHelper.mkdirs(baseDir);
    System.setProperty("ETLDATA_DIR", baseDir.getPath() + "/etldata");
    System.setProperty("PMDATA_DIR", baseDir.getPath() + "/pmdata");
    System.setProperty("ARCHIVE_DIR", baseDir.getPath() + "/archive");

    final URL url = ClassLoader.getSystemResource(PARSER_TEST_FWK_CONF);
    if (url == null) {
      Assert.fail("Cant locate file " + PARSER_TEST_FWK_CONF);
    }    
    if ("file".equals(url.getProtocol())) {
      final File localFile = new File(url.toURI().getPath());
      fwkConfDir = localFile.getParentFile();
    } else if ("jar".equals(url.getProtocol())) {
      fwkConfDir = baseDir;

      final String[] details = url.toURI().getSchemeSpecificPart().split("!");
      final String[] detail = details[0].split(":");
	  final File jarFile = new File(detail[1]);
      extractDataFiles(jarFile, fwkConfDir);
    }

    initDirectories(baseDir, interfaceName);
    // Extract the data files to the tmp dir
    final File fwkConfDir = getFrameworkConfDir();
    fwkProperties = new Properties();
    fwkProperties.load(new FileReader(new File(fwkConfDir, PARSER_TEST_FWK_CONF)));
    for (Map.Entry<Object, Object> entry : fwkProperties.entrySet()) {
      System.setProperty(entry.getKey().toString(), entry.getValue().toString());
    }
    System.setProperty("dc5000.config.directory", baseDir.getPath());
    System.setProperty("CONF_DIR", baseDir.getPath());
    copyFile(new File(fwkConfDir, "static.properties"), baseDir);
    StaticProperties.reload();
    sessionHandlerInit();
    final ExecutionSlotProfileHandler slotHandler = new ExecutionSlotProfileHandler(1) {
      @Override
      public int getNumberOfAdapterSlots() {
        return 1;
      }
    };
    Share.instance().add("executionSlotProfileObject", slotHandler);
    initCaches(fwkConfDir, interfaceName);
  }

  public static void copyFile(final File source, final File destDir) {
    try {
      final File destFile = new File(destDir, source.getName());
      FileUtils.copyFile(source, destFile);
    } catch (IOException e) {
      if (!e.getMessage().endsWith(" are the same")) {
        throw new DatabaseTestCaseException(e);
      }
    }
  }

  public static Properties getMdcParserProperties(final String interfaceName, final ParserOutputFormat outputformat) {
    final Properties mdcParserProperties = new Properties();
    mdcParserProperties.setProperty("outDir", "${ETLDATA_DIR}/adapter_tmp/mdc/");
    mdcParserProperties.setProperty("maxFilesPerRun", "5000");
    mdcParserProperties.setProperty("dublicateCheck", "true");
    mdcParserProperties.setProperty("thresholdMethod", "more");
    mdcParserProperties.setProperty("inDir", "${PMDATA_DIR}/eniq_oss_1/mdc/");
    mdcParserProperties.setProperty("interfaceName", interfaceName);
    mdcParserProperties.setProperty("MDCParser.uniqueVectorCounterList", "_pmRes");
    mdcParserProperties.setProperty("MDCParser.UseVector", "true");
    mdcParserProperties.setProperty("ProcessedFiles.fileNameFormat", "A(.{13}).+");
    mdcParserProperties.setProperty("MDCParser.vendorIDMask", ".*,(.+)=.*");
    mdcParserProperties.setProperty("MDCParser.emptyMOIDVendorID", "");
    mdcParserProperties.setProperty("minFileAge", "1");
    mdcParserProperties.setProperty("MDCParser.vectorColumn", "VectorColumn");
    mdcParserProperties.setProperty("baseDir", "${ARCHIVE_DIR}/eniq_oss_1/mdc/");
    mdcParserProperties.setProperty("useZip", "none");
    mdcParserProperties.setProperty("archivePeriod", "168");
    mdcParserProperties.setProperty("loaderDir", "${ETLDATA_DIR}");
    mdcParserProperties.setProperty("parserType", "mdc");
    mdcParserProperties.setProperty("MDCParser.outputFormat", Integer.toString(outputformat.ordinal()));
    mdcParserProperties.setProperty("doubleCheckAction", "move");
    mdcParserProperties.setProperty("MDCParser.VendorPatterns", "");
    mdcParserProperties.setProperty("ProcessedFiles.processedDir", "${ARCHIVE_DIR}/eniq_oss_1/mdc/processed/");
    mdcParserProperties.setProperty("failedAction", "move");
    mdcParserProperties.setProperty("dirThreshold", "0");
    mdcParserProperties.setProperty("MDCParser.createOwnVectorFile", "true");
    mdcParserProperties.setProperty("MDCParser.uniqueVectorIndexName", "DCVECTOR_INDEX");
    mdcParserProperties.setProperty("workers", "3");
    mdcParserProperties.setProperty("MDCParser.HashData", "true");
    mdcParserProperties.setProperty("afterParseAction", "no");
    mdcParserProperties.setProperty("MDCParser.readVendorIDFrom", "data");
    return mdcParserProperties;
  }


  public static void initCaches(final File configDir, final String interfaceName) {
    try {
      DataFormatCacheBuilder.buildCache(configDir, interfaceName);
      final Map<String, Transformer> tformers = new HashMap<String, Transformer>();
      tformers.put("GenericTransformer", new Transformer() {
        @SuppressWarnings("unchecked")
        @Override
        public void transform(final Map data, final Logger clog) throws Exception {
          data.put("DATE_ID", data.get("DATETIME_ID"));
        }

        @Override
        public void addDebugger(final ParserDebugger parserDebugger) {
        }

        @Override
        public List getTransformations() {
          return null;
        }
      });

      final TransformerCache tCache = new TransformerCache();
      tCache.testInitialize(tformers);
    } catch (IOException e) {
      throw new DatabaseTestCaseException(e);
    } catch (Throwable t) {
      throw new DatabaseTestCaseException(t);
    }
  }

  @SuppressWarnings("UnusedDeclaration")
  public enum ParserOutputFormat {
    ASCII, BINARY
  }
}
