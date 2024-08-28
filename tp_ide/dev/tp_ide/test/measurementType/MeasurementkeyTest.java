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

public class MeasurementkeyTest extends CreateSYBASEDatabase{
	
	
	private static Measurementkey _testInstance;
	private static String MTABLEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT:DAY";
	private static String TYPEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
	private String RELEASEID = "DC_E_TEST:((1))";
	private static String DATANAME = "AtmPort";
	private String DATATYPE = "numeric";
	private Long COLNUMBER = (long) 28;
	private Integer COLSIZE = 18;
	private Integer DATASCALE = 0 ;
	private Integer NULLABLE = 0;
	private Integer UNIQUEKEY = DATASCALE;
	private Long UNIQUEVALUE=(long) 255;
	private String INDEXES ="";
	private String DESCRIPTION = DATANAME;
	private String DATAID = DESCRIPTION;
	 private Integer INCLUDESQL =1;
	  private String[] columnsAndSequences = {};
	
	@BeforeClass
	public static void setUp() throws SQLException, RockException{
		CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new Measurementkey(getRockFactory(Schema.dwhrep));
	}
	
	@Test
	public void testgetTableName() throws Exception{
		
		String actual = _testInstance.getTableName();
		String expected = "Measurementkey";
		
		assertEquals(expected, actual);
		

	}
	
	@Test
	public void testsetIselement() throws Exception{
		
		Integer ISELEMENT = (int) 0;
		_testInstance.setIselement(ISELEMENT);
	
	}
	
	@Test
	public void testsetDataname() throws Exception{
		
		_testInstance.setDataname(DATANAME);
	
	}

	
	@Test
	public void testsetDescription() throws Exception{
		
		_testInstance.setDescription(DESCRIPTION);
	
	}

	
	@Test
	public void testsetTypeid() throws Exception{
		
		_testInstance.setTypeid(TYPEID);
	
	}
	
	@Test
	public void testsetcolumnsAndSequences() throws Exception{
		
		_testInstance.setcolumnsAndSequences(columnsAndSequences);
	
	} 
	@Test
	public void testsetUniquekey() throws Exception{
		
		_testInstance.setUniquekey(UNIQUEKEY);
	
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
	public void testgetIselement() throws Exception{
		
		Integer actual = _testInstance.getIselement();
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
	public void testgetTypeid() throws Exception{
		
		String actual = _testInstance.getTypeid();
		String expected = TYPEID;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
	}

	@Test
	public void testgetDescription() throws Exception{
		
		String  actual = _testInstance.getDescription();
		String  expected = DATANAME;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	
	@Test
	public void testgetUniquekey() throws Exception{
		
		Integer  actual = _testInstance.getUniquekey();
		Integer  expected = 0;
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
