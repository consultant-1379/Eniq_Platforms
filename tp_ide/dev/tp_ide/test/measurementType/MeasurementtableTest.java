package measurementType;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Set;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;

public class MeasurementtableTest extends CreateSYBASEDatabase{
	
	
	private static Measurementtable _testInstance;
	private static String MTABLEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT:DAY";
	private static String TYPEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT";
	private static String BASETABLENAME = "DC_E_TEST_ATMPORT_RAW";
	private static String PARTITIONPLAN = "medium_day"; 
	private static String TABLELEVEL = "day";
	private String[] columnsAndSequences = {};
	private String DEFAULT_TEMPLATE="template.vm";
	
	@BeforeClass
	public static void setUp() throws SQLException, RockException{
		CreateSYBASEDatabase.createStatsVer(false);
		_testInstance = new Measurementtable(getRockFactory(Schema.dwhrep));
		
	}
	
	@Test
	public void testgetTableName() throws Exception{
		
		String actual = _testInstance.getTableName();
		String expected = "Measurementtable";
		
		assertEquals(expected, actual);
		

	}
	
	@Test
	public void testsetMtableid() throws Exception{
		
		_testInstance.setMtableid(MTABLEID);
	
	}
	
	@Test
	public void testsetTablelevel() throws Exception{
		
		_testInstance.setTablelevel(TABLELEVEL);
	
	}

	@Test
	public void testsetTypeid() throws Exception{
		
		_testInstance.setTypeid(TYPEID);
	
	}
	
	@Test
	public void testsetBasetablename() throws Exception{
		
		_testInstance.setBasetablename(BASETABLENAME);
	
	}
	
	@Test
	public void testsetDefault_template() throws Exception{
		
		_testInstance.setDefault_template(DEFAULT_TEMPLATE);
	
	}
	
	@Test
	public void testsetcolumnsAndSequences() throws Exception{
		
		_testInstance.setcolumnsAndSequences(columnsAndSequences);
	
	}
	
	@Test
	public void testsetPartitionplan() throws Exception{
		
		_testInstance.setPartitionplan(PARTITIONPLAN);
	
	}
	
	@Test
	public void testcleanModifiedColumns() throws Exception{
		
		_testInstance.cleanModifiedColumns();
	
	}

	@Test
	public void testgimmeModifiedColumns() throws Exception{
		
		Set actual = _testInstance.gimmeModifiedColumns();
		//System.out.println("actaul -"+actual);
	
	}
	
	
	@Test
	public void testgetMtableid() throws Exception{
		
		String actual = _testInstance.getMtableid();
		String expected = MTABLEID;
		//System.out.println("actaul -"+actual);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void testgetTablelevel() throws Exception{
		
		String actual = _testInstance.getTablelevel();
		String expected = TABLELEVEL;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetTypeid() throws Exception{
		
		String actual = _testInstance.getTypeid();
		String expected = TYPEID;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	
	@Test
	public void testgetBasetablename() throws Exception{
		
		String actual = _testInstance.getBasetablename();
		String expected = BASETABLENAME;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetDefault_template() throws Exception{
		
		String actual = _testInstance.getDefault_template();
		String expected = DEFAULT_TEMPLATE;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetPartitionplan() throws Exception{
		
		String actual = _testInstance.getPartitionplan();
		String expected = PARTITIONPLAN;
		assertEquals(expected, actual);
	//	System.out.println("actaul -"+actual);
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
	
	@Test
	public void testisPrimaryDefined() throws Exception{
		
		 _testInstance.isPrimaryDefined();
		/*String  expected = "org.hsqldb.jdbcDriver";
		assertEquals(expected, actual.getDriverName());
		*///System.out.println("actaul -"+actual.getDriverName());
	}
	
}
