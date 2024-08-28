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

public class MeasurementtypeTest extends CreateSYBASEDatabase{
	
	
	
	private static final String COUNTAGGREGATION = "PEG";
	private static final Integer OBJECTVERSION = 1;
	private static final String OBJECTTYPE = null;
	private static final Long STATUS = null;
	private String DESCRIPTION = DATANAME;
	private static Measurementtype _testInstance;
	private static String TYPEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
	private static String TYPECLASSID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
	private static String TYPENAME ="DC_E_TEST_ATMPORT";
	private static String VENDORID ="DC_E_TEST";
	private static String FOLDERNAME = TYPENAME;
	private static String VERSIONID = "DC_E_TEST:((1))";
	private static String OBJECTID = TYPECLASSID;
	private static String OBJECTNAME = TYPENAME;
	private static String SIZING = "medium";
	private static String DATANAME = "pmReceivedAtmCells";
	private String TIMEAGGREGATION = "MAX";
	private final String GROUPAGGREGATION = TIMEAGGREGATION;
    private String[] columnsAndSequences = {};
	
	@BeforeClass
	public static void setUp() throws SQLException, RockException{
		CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new Measurementtype(getRockFactory(Schema.dwhrep));
	}
	
	@Test
	public void testgetTableName() throws Exception{
		
		String actual = _testInstance.getTableName();
		String expected = "Measurementtype";
		
		assertEquals(expected, actual);
		

	}
	
	@Test
	public void testsetTypename() throws Exception{
		
		_testInstance.setTypename(TYPENAME);
	
	}

	@Test
	public void testsetVendorid() throws Exception{
		
		_testInstance.setVendorid(VENDORID);
	
	}
	
	@Test
	public void testsetDescription() throws Exception{
		
		_testInstance.setDescription(DESCRIPTION);
	
	}
	
	@Test
	public void testsetFoldername() throws Exception{
		
		_testInstance.setFoldername(FOLDERNAME);
	
	}
	
	@Test
	public void testsetTypeid() throws Exception{
		
		_testInstance.setTypeid(TYPEID);
	
	}
	
	@Test
	public void testsetTypeclassid() throws Exception{
		
		_testInstance.setTypeclassid(TYPECLASSID);
	
	}
	
	@Test
	public void testsetStatus() throws Exception{
		
		_testInstance.setStatus(STATUS);
	
	}
	
	
	@Test
	public void testsetVersionid() throws Exception{
		
		//Integer YesNoValue = (int) 0;
		_testInstance.setVersionid(VERSIONID);
	
	}
	
	@Test
	public void testgimmeModifiedColumns() throws Exception{
		
		Set actual = _testInstance.gimmeModifiedColumns();
		//System.out.println("actaul -"+actual);
	
	}
	
	
	@Test
	public void testcleanModifiedColumns() throws Exception{
		
		_testInstance.cleanModifiedColumns();
		//System.out.println("actaul -"+actual);
	
	}
	
	@Test
	public void testsetObjectid() throws Exception{
		
		_testInstance.setObjectid(OBJECTID);
		}
	
	@Test
	public void testsetObjectname() throws Exception{
		
		 _testInstance.setObjectname(OBJECTNAME);

	}
	
	
	@Test
	public void testsetObjectversion() throws Exception{
		
		_testInstance.setObjectversion(OBJECTVERSION);
	}
	
	@Test
	public void testsetObjecttype() throws Exception{
		
		 _testInstance.setObjecttype(OBJECTTYPE);
	}
	
	@Test
	public void testgetTypeid() throws Exception{
		
		String actual = _testInstance.getTypeid();
		String expected = TYPEID;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetTypeclassid() throws Exception{
		
		String actual = _testInstance.getTypeclassid();
		String expected = TYPECLASSID;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetVendorid() throws Exception{
		
		String actual = _testInstance.getVendorid();
		String expected = VENDORID;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetVersionid() throws Exception{
		
		String actual = _testInstance.getVendorid();
		String expected = "DC_E_TEST";
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetFoldername() throws Exception{
		
		String actual = _testInstance.getFoldername();
		String expected = FOLDERNAME;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	
	@Test
	public void testgetTypename() throws Exception{
		
		String actual = _testInstance.getTypename();
		String expected = TYPENAME;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}
	
	
	@Test
	public void testgetObjectid() throws Exception{
		
		String actual = _testInstance.getObjectid();
		String expected =  OBJECTID;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	
	}
	
	@Test
	public void testgetObjectname() throws Exception{
		
		String actual = _testInstance.getObjectname();
		String expected =  OBJECTNAME;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	
	}
	
	@Test
	public void testgetObjectversion() throws Exception{
		
		Integer actual = _testInstance.getObjectversion();
		Integer expected =  OBJECTVERSION;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	
	}
	
	@Test
	public void testgetObjecttype() throws Exception{
		
		String actual = _testInstance.getObjecttype();
		String expected =  OBJECTTYPE;
		assertEquals(expected, actual);
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
	public void testgetStatus() throws Exception{
		
		Long  actual = _testInstance.getStatus();
		Long  expected = null;
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
