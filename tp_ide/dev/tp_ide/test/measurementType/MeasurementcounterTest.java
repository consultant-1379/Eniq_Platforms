package measurementType;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Set;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;
import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;

public class MeasurementcounterTest extends CreateSYBASEDatabase{
	
	
	
	private static final String COUNTAGGREGATION = "PEG";
	private String DESCRIPTION = DATANAME;
	private static Measurementcounter _testInstance;
	private static String TYPEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
	private static String DATANAME = "pmReceivedAtmCells";
	private String TIMEAGGREGATION = "MAX";
	private final String GROUPAGGREGATION = TIMEAGGREGATION;
    private String[] columnsAndSequences = {};
	
	@BeforeClass
	public static void setUp() throws SQLException, RockException{
		CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new Measurementcounter(getRockFactory(Schema.dwhrep));
	}
	
	@Test
	public void testgetTableName() throws Exception{
		
		String actual = _testInstance.getTableName();
		String expected = "Measurementcounter";
		
		assertEquals(expected, actual);
		

	}
	
	@Test
	public void testsetDataname() throws Exception{
		
		_testInstance.setDataname(DATANAME);
	
	}

	@Test
	public void testsetTimeaggregation() throws Exception{
		
		_testInstance.setTimeaggregation(TIMEAGGREGATION);
	
	}
	
	@Test
	public void testsetDescription() throws Exception{
		
		_testInstance.setDescription(DESCRIPTION);
	
	}
	
	@Test
	public void testsetGroupaggregation() throws Exception{
		
		_testInstance.setGroupaggregation(GROUPAGGREGATION);
	
	}
	
	@Test
	public void testsetTypeid() throws Exception{
		
		_testInstance.setTypeid(TYPEID);
	
	}
	
	@Test
	public void testsetCountaggregation() throws Exception{
		
		_testInstance.setCountaggregation(COUNTAGGREGATION);
	
	}
	
	
	@Test
	public void testsetYesNoValue() throws Exception{
		
		Integer YesNoValue = (int) 0;
		_testInstance.setYesNoValue(YesNoValue);
	
	}
	
	@Test
	public void testgimmeModifiedColumns() throws Exception{
		
		Set actual = _testInstance.gimmeModifiedColumns();
		//System.out.println("actaul -"+actual);
	
	}
	
	
	@Test
	public void testgetYesNoValue() throws Exception{
		
		Integer actual = _testInstance.getYesNoValue();
		Integer expected = (int) 0;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetDataname() throws Exception{
		
		String actual = _testInstance.getDataname();
		String expected = DATANAME;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	
	@Test
	public void testgetTimeaggregation() throws Exception{
		
		String actual = _testInstance.getTimeaggregation();
		String expected = TIMEAGGREGATION;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetCountaggregation() throws Exception{
		
		String actual = _testInstance.getCountaggregation();
		String expected = COUNTAGGREGATION;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetTypeid() throws Exception{
		
		String actual = _testInstance.getTypeid();
		String expected = TYPEID;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetGroupaggregation() throws Exception{
		
		String actual = _testInstance.getGroupaggregation();
		String expected =  GROUPAGGREGATION;
		//assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	
	}
	
	
	@Test
	public void testgetDescription() throws Exception{
		
		String  actual = _testInstance.getDescription();
		String  expected = DATANAME;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}


	@Test
	public void testgettimeStampName() throws Exception{
		
		String  actual = _testInstance.gettimeStampName();
		String  expected = "LAST_UPDATED";
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetcolumnsAndSequences() throws Exception{
		
		String[]  actual = _testInstance.getcolumnsAndSequences();
		/*String  expected = "ReleaseId";
		//assertEquals(expected, actual);
		System.out.println("actaul -"+actual.toString());*/
	}
	
	@Test
	public void testgetprimaryKeyNames() throws Exception{
		
		String[]  actual = _testInstance.getprimaryKeyNames();
		/*String  expected = "ReleaseId";
		//assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);*/	
	}
	
	
	@Test
	public void testgetRockFactory() throws Exception{
		
		RockFactory  actual = _testInstance.getRockFactory();

		//assertEquals(expected, actual.getDriverName());
		//System.out.println("actaul -"+actual.getDriverName());
	}
	
	
	@Test
	public void testremoveNulls() throws Exception{
		
		 _testInstance.removeNulls();
		/*String  expected = "org.hsqldb.jdbcDriver";
		assertEquals(expected, actual.getDriverName());
		*///System.out.println("actaul -"+actual.getDriverName());
	}
}
