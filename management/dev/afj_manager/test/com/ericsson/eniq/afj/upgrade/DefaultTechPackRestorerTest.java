/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static com.ericsson.eniq.afj.common.PropertyConstants.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.CreateViewsAction;
import com.distocraft.dc5000.dwhm.PartitionAction;
import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.CommonSetGeneratorFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.dwhm.CreateViewsActionFactory;
import com.ericsson.eniq.dwhm.PartitionActionFactory;
import com.ericsson.eniq.dwhm.StorageTimeActionFactory;
import com.ericsson.eniq.dwhm.VersionUpdateActionFactory;
import com.ericsson.eniq.engine.EngineRestarterWrapper;
import com.ericsson.eniq.repository.ActivationCacheWrapper;
import com.ericsson.eniq.techpacksdk.CreateTPDirCheckerSetFactory;
import com.ericsson.eniq.common.setWizards.StatsCreateTPDirCheckerSet;

/**
 * @author eheijun
 * 
 */
public class DefaultTechPackRestorerTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String TEST_TP = "DC_E_TEST";
  
  private static final String TEST_TP_VERSION = TEST_TP + ":((999))";
  
  private static final String BASE_TP_ID = "SOME_BASE_RELEASE_ID";
  
  private static final String RAW_TYPE = "RAW";

  private AFJTechPack mockTechPack;

  private AFJDatabaseHandler mockDatabaseHandler;
  
  private RockFactory mockDbaDwh;
  
  private RockFactory mockDwhrep;

  private RockFactory mockEtlrep;

  private RockFactory mockDwh;

  private Connection mockConnection;
  
  private AFJDatabaseCommonUtils mockDatabaseCommonUtils;
  
  private AFJDelta mockAFJDelta;

  private AFJMeasurementType mockAFJMeasurementType1_1;

  private AFJMeasurementType mockAFJMeasurementType1_2;

  private PropertiesUtility mockPropertiesUtility;

  private Properties mockAfjProperties;

  private Meta_collections mockMetaCollections;

  private Meta_collection_sets mockMetaCollectionSets;

  private Dwhtype mockDwhtype;

  private CommonSetGenerator mockCommonSetGenerator;

  private VersionUpdateAction mockVersionUpdateAction;

  private CreateViewsAction mockCreateViewsAction;

  private StatsCreateTPDirCheckerSet mockCreateTPDirCheckerSet;

  private StorageTimeAction mockStorageTimeAction;

  private PartitionAction mockPartitionAction;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockTechPack = context.mock(AFJTechPack.class);

    mockDatabaseHandler = context.mock(AFJDatabaseHandler.class);
    
    mockDbaDwh = context.mock(RockFactory.class, "rockDbaDwh");

    mockDwhrep = context.mock(RockFactory.class, "rockDwhrep");

    mockEtlrep = context.mock(RockFactory.class, "rockEtlrep");

    mockDwh = context.mock(RockFactory.class, "rockDwh");
    
    mockDatabaseCommonUtils = context.mock(AFJDatabaseCommonUtils.class);
    
    mockAFJDelta = context.mock(AFJDelta.class);
    
    mockAFJMeasurementType1_1 = context.mock(AFJMeasurementType.class, "MT1.1");
    
    mockAFJMeasurementType1_2 = context.mock(AFJMeasurementType.class, "MT1.2");

    mockDwhtype = context.mock(Dwhtype.class);
    
    mockPropertiesUtility = context.mock(PropertiesUtility.class);

    mockAfjProperties = context.mock(Properties.class);

    mockMetaCollections = context.mock(Meta_collections.class);
    
    mockMetaCollectionSets = context.mock(Meta_collection_sets.class);
    
    mockCommonSetGenerator = context.mock(CommonSetGenerator.class);
    
    mockVersionUpdateAction = context.mock(VersionUpdateAction.class);
    
    mockCreateViewsAction = context.mock(CreateViewsAction.class);
    
    mockCreateTPDirCheckerSet = context.mock(StatsCreateTPDirCheckerSet.class);
    
    mockStorageTimeAction = context.mock(StorageTimeAction.class);
    
    mockPartitionAction = context.mock(PartitionAction.class);
    
    AFJDatabaseHandler.setInstance(mockDatabaseHandler);
    AFJDatabaseCommonUtilsFactory.setInstance(mockDatabaseCommonUtils);
    CommonSetGeneratorFactory.setInstance(mockCommonSetGenerator);
    VersionUpdateActionFactory.setInstance(mockVersionUpdateAction);
    CreateViewsActionFactory.setInstance(mockCreateViewsAction);
    CreateTPDirCheckerSetFactory.setInstance(mockCreateTPDirCheckerSet);
    ActivationCacheWrapper.setInitializeAllowed(false);
    StorageTimeActionFactory.setInstance(mockStorageTimeAction);
    PartitionActionFactory.setInstance(mockPartitionAction);
    EngineRestarterWrapper.setExecutionAllowed(false);
    
    PropertiesUtility.setPropertiesUtility(mockPropertiesUtility);
    PropertiesUtility.setAfjProperties(mockAfjProperties);
    
  }
  
  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    AFJDatabaseHandler.setInstance(null);
    AFJDatabaseCommonUtilsFactory.setInstance(null);
    CommonSetGeneratorFactory.setInstance(null);
    VersionUpdateActionFactory.setInstance(null);
    CreateViewsActionFactory.setInstance(null);
    CreateTPDirCheckerSetFactory.setInstance(null);
    ActivationCacheWrapper.setInitializeAllowed(true);
    StorageTimeActionFactory.setInstance(null);
    PartitionActionFactory.setInstance(null);
    
    PropertiesUtility.setPropertiesUtility(null);
    PropertiesUtility.setAfjProperties(null);
  }
  
  
	/**
	 * Test method for
	 * {@link com.ericsson.eniq.afj.upgrade.DefaultTechPackRestorer#restore()}.
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
  @Test
	public void testRestore() throws Exception {
	  
    context.checking(new Expectations() {

      {

        allowing(mockTechPack).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockDatabaseHandler).getDwhrep();
        will(returnValue(mockDwhrep));
        
        allowing(mockDatabaseHandler).getEtlrep();
        will(returnValue(mockEtlrep));
        
        allowing(mockDatabaseHandler).getDwh();
        will(returnValue(mockDwh));
        
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));
        
        allowing(mockDatabaseCommonUtils).getAFJDelta(TEST_TP, mockDwhrep);
        will(returnValue(mockAFJDelta));
        
        allowing(mockDatabaseCommonUtils).getTechpack(TEST_TP, BASE_TP_ID, mockEtlrep);
        will(returnValue(mockMetaCollectionSets));
        
        allowing(mockAFJDelta).getTechPackVersion();
        will(returnValue(TEST_TP_VERSION));
        
        allowing(mockAFJDelta).getMeasurementTypes();
        will(returnValue(new ArrayList<AFJMeasurementType>(Arrays.asList(new AFJMeasurementType[] { mockAFJMeasurementType1_1, mockAFJMeasurementType1_2 }))));
        
        allowing(mockAFJMeasurementType1_1).isTypeNew();
        will(returnValue(false));
        allowing(mockAFJMeasurementType1_1).getTpName();
        will(returnValue(TEST_TP));
        allowing(mockAFJMeasurementType1_1).getTypeName();
        will(returnValue(RAW_TYPE));
        allowing(mockAFJMeasurementType1_1).getTpVersion();
        will(returnValue(BASE_TP_ID));

        allowing(mockAFJMeasurementType1_2).isTypeNew();
        will(returnValue(true));
        allowing(mockAFJMeasurementType1_2).getTpName();
        will(returnValue(TEST_TP));
        allowing(mockAFJMeasurementType1_2).getTypeName();
        will(returnValue(RAW_TYPE));
        allowing(mockAFJMeasurementType1_2).getTpVersion();
        will(returnValue(BASE_TP_ID));
        
        allowing(mockAfjProperties).getProperty(PROP_TEMPLATE_DIR, null);
        will(returnValue("templates"));
        
        allowing(mockAfjProperties).getProperty(PROP_IQLOADER_DIR, null);
        will(returnValue("iqloader"));
        
        allowing(mockAfjProperties).getProperty(PROP_REJECTED_DIR, null);
        will(returnValue("rejected"));
        
        allowing(mockAfjProperties).getProperty(PROP_ETLDATA_DIR, null);
        will(returnValue("etldata"));
        
        allowing(mockAfjProperties).getProperty(PROP_ETLDATA_JOINED_DIR, null);
        will(returnValue("joined"));
        
        allowing(mockAfjProperties).getProperty(PROP_DEF_PARTITIONTYPE, null);
        will(returnValue("templates"));
        
        allowing(mockMetaCollectionSets).getCollection_set_name();
        will(returnValue("TEST_CSN"));
        
        allowing(mockMetaCollectionSets).getVersion_number();
        will(returnValue("((999))"));
        
        allowing(mockMetaCollectionSets).getCollection_set_id();
        will(returnValue(1L));
        
        allowing(mockEtlrep).getConnection();
        will(returnValue(mockConnection));
        
        allowing(mockCommonSetGenerator).removeLoaderAction(with(any(String.class)));
        
        allowing(mockCommonSetGenerator).updateLoaderAction(with(any(String.class)));
        
        allowing(mockDatabaseCommonUtils).removeInterfaceMeasurement(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));

        allowing(mockDatabaseCommonUtils).removeDataItem(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeDefaultTags(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeDataFormat(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeTransformation(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeTransformer(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeMeasurementColumn(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeMeasurementTable(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));

        allowing(mockDatabaseCommonUtils).removeMeasurementCounter(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeMeasurementKey(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeMeasurementType(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockDatabaseCommonUtils).removeMeasurementTypeClass(with(any(List.class)), with(any(Boolean.class)), with(any(RockFactory.class)));
        
        allowing(mockVersionUpdateAction).execute(with(any(String.class)));
        
        allowing(mockDatabaseCommonUtils).getDwhType(with(any(String.class)), with(any(RockFactory.class)));
        will(returnValue(mockDwhtype));
        
        allowing(mockDatabaseCommonUtils).getMeta_collections(with(any(Long.class)), with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
        will(returnValue(new Vector<Meta_collections>(Arrays.asList(new Meta_collections[] { mockMetaCollections }))));
        
        allowing(mockMetaCollections).getCollection_id();
        will(returnValue(2L));
        
        allowing(mockCreateTPDirCheckerSet).removeForAFJ(with(any(String.class)), with(any(Long.class)));
        
        allowing(mockDatabaseHandler).commitTransaction(true);
        
      }
    });
    	final DefaultTechPackRestorer restorer = new DefaultTechPackRestorer(mockTechPack);
    	final boolean result = restorer.restore();
		assertEquals(result, Boolean.valueOf(true));
	}

}
