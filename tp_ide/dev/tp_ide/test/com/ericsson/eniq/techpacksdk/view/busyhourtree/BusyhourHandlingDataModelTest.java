package com.ericsson.eniq.techpacksdk.view.busyhourtree;
import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel.BusyHourSourceTables;

public class BusyhourHandlingDataModelTest extends TestCase {
    private BusyhourHandlingDataModelTester testInstance = null;
    final RockFactory mockedRock = createNiceMock(RockFactory.class);
    private final String versionId = "DC_E_BSS:((10))";
    public BusyhourHandlingDataModelTest(){
        super("BusyhourHandlingDataModelTest");
    }

    @Override
    protected void setUp() throws Exception {
        final DataModelController runDmc = createNiceMock(DataModelController.class);
        replay(runDmc);
        testInstance = new BusyhourHandlingDataModelTester(mockedRock, runDmc);
        final Versioning mockedVer = createNiceMock(Versioning.class);
        mockedVer.getVersionid();
        expectLastCall().andReturn(versionId);
        replay(mockedVer);
        testInstance.setCurrentVersioning(mockedVer);
    }

    public void testGetAllBusyHourSources_db(){
      RockFactory rock = null;
      final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
      final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";    
      final String versionID = "DC_E_BSS:((37))";
      
      final DataModelController runDmc = createNiceMock(DataModelController.class);
      replay(runDmc);
      try {
        rock = new RockFactory(DWHREP_URL, "SA", "", TESTDB_DRIVER, "test", true);
      DatabaseTestUtils.loadSetup(rock, "tpDocTables");
        
        final Versioning where = new Versioning(rock);
        where.setVersionid(versionID);
        VersioningFactory fac;
        
        fac = new VersioningFactory(rock, where);
        final List<Versioning> versions = fac.get();
        System.out.println(versions);
      } catch (SQLException e) {
        fail(e.getMessage());
      } catch (RockException e) {
        fail(e.getMessage());
      } catch (Exception e) {
        fail(e.getMessage());
      }

      final VersioningFactory mockedVer = createNiceMock(VersioningFactory.class);
      
      testInstance = new BusyhourHandlingDataModelTester(rock, runDmc);
      List<BusyHourSourceTables> tables = testInstance.getAllBusyHourSources_db();
      List<String> baseTables = tables.get(0).getBasetables();
      assertEquals(24, baseTables.size());
    }
    
    
    public void testGetBusyhours() throws Exception {
        final RockResultSet rrs = createNiceMock(RockResultSet.class);
        final Iterator iter = createNiceMock(Iterator.class);

        mockedRock.setSelectSQL(eq(false), anyObject());
        expectLastCall().andReturn(rrs);
        mockedRock.getData(anyObject(), eq(rrs));
        expectLastCall().andReturn(iter);

        final int placeHolders = 5;
        int value = 0;
        final List<String> expectedOrder = new ArrayList<String>(placeHolders);
        for(int i=(placeHolders-1);i>=0;i--){
            final Busyhour mockedBh = createNiceMock(Busyhour.class);
            mockedBh.getBhlevel();
            expectLastCall().andReturn("DC_E_BSS_BSCBH");
            expectLastCall().anyTimes();
            mockedBh.getBhtype();
            
            //Change the order of the busyhours.
            if(i == 2){
            	value = 3;
            }if(i == 3){
            	value = 2;
            }else{
            	value = i;
            }
            final String bhType = "PP"+value;
            expectedOrder.add(0, bhType);
            expectLastCall().andReturn(bhType);
            expectLastCall().anyTimes();
            iter.hasNext();
            expectLastCall().andReturn(true);
            iter.next();
            expectLastCall().andReturn(mockedBh);
            replay(mockedBh);
        }
        iter.hasNext();
        expectLastCall().andReturn(false);
        replay(rrs);
        replay(iter);
        replay(mockedRock);
        final List<Busyhour> bhFound = testInstance.getBusyhours(versionId);
        for(int i=0;i<bhFound.size();i++){
            final String actualIndexValue = bhFound.get(i).getBhtype();
            final String expectedIndexValue = expectedOrder.get(i);
            assertEquals("Ordered Busyhour lists isnt in the correct order", expectedIndexValue, actualIndexValue);
        }
    }
    private class BusyhourHandlingDataModelTester extends BusyhourHandlingDataModel{
        public BusyhourHandlingDataModelTester(final RockFactory rockFactory,
                                               final DataModelController dataModelController) {
            super(rockFactory, dataModelController);
        }

        @Override
        public List<Busyhour> getBusyhours(final String versionId) {
            return super.getBusyhours(versionId);
        }
        
        @Override
        public List<BusyHourSourceTables> getAllBusyHourSources_db() {
            return super.getAllBusyHourSources_db();
        }

    }
}