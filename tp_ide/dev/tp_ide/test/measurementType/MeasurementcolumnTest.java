package measurementType;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Set;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.ericsson.eniq.common.testutilities.UnitDatabaseTestCase.Schema;
import com.ericsson.eniq.techpacksdk.java.test.hsql.common.CreateHSQLDatabase;
import com.ericsson.eniq.techpacksdk.java.test.sybase.common.CreateSYBASEDatabase;

public class MeasurementcolumnTest extends CreateSYBASEDatabase{
	
	
	private static Measurementcolumn _testInstance;
	private static Measurementcolumn _testInstance1;
	private static String MTABLEID = "DC_E_TEST:((1)):DC_E_TEST_ATMPORT:DAY";
	private String RELEASEID = "DC_E_TEST:((1))";
	private static String DATANAME = "pmReceivedAtmCells";
	private static Measurementcolumn _testInstance2;
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
		//DataModelController dataModelController = null;
		_testInstance = new Measurementcolumn(getRockFactory(Schema.dwhrep));
		//_testInstance1 = new Measurementcolumn(getRockFactory(Schema.dwhrep), _testInstance);
		//_testInstance2 = new Measurementcolumn(getRockFactory(Schema.dwhrep), MTABLEID, DATANAME);
		
	}
	
	@Test
	public void testgetTableName() throws Exception{
		
		String actual = _testInstance.getTableName();
		String expected = "Measurementcolumn";
		
		assertEquals(expected, actual);
		

	}
	
	@Test
	public void testsetMtableid() throws Exception{
		
		_testInstance.setMtableid(MTABLEID);
	
	}
	
	@Test
	public void testsetDataname() throws Exception{
		
		_testInstance.setDataname(DATANAME);
	
	}

	@Test
	public void testsetColnumber() throws Exception{
		
		_testInstance.setColnumber(COLNUMBER);
	
	}
	
	@Test
	public void testsetDatatype() throws Exception{
		
		_testInstance.setDatatype(DATATYPE);
	
	}
	
	@Test
	public void testsetDataSize() throws Exception{
		
		_testInstance.setDatasize(COLSIZE);
	
	}
	
	@Test
	public void testsetDatascale() throws Exception{
		
		_testInstance.setDatascale(DATASCALE);
	
	}
	
	@Test
	public void testsetUniquevalue() throws Exception{
		
		_testInstance.setUniquevalue(UNIQUEVALUE);
	
	}
	
	
	@Test
	public void testsetNullable() throws Exception{
		
		_testInstance.setNullable(NULLABLE);
	
	}
	
	@Test
	public void testsetIndexes() throws Exception{
		
		_testInstance.setIndexes(INDEXES);
	
	}
	
	@Test
	public void testsetDescription() throws Exception{
		
		_testInstance.setDescription(DESCRIPTION);
	
	}
	
	@Test
	public void testsetDataid() throws Exception{
		
		_testInstance.setDataid(DATAID);
	
	}
	
	@Test
	public void testsetReleaseid() throws Exception{
		
		_testInstance.setReleaseid(RELEASEID);
	
	}
	
	@Test
	public void testsetUniquekey() throws Exception{
		
		_testInstance.setUniquekey(UNIQUEKEY);
	
	}
	
	
	@Test
	public void testsetIncludesql() throws Exception{
		
		_testInstance.setIncludesql(INCLUDESQL);
	
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
	public void testgetColnumber() throws Exception{
		
		Long actual = _testInstance.getColnumber();
		Long expected =  (long) 28;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	
	}
	
	@Test
	public void testgetDatatype() throws Exception{
		
		String actual = _testInstance.getDatatype();
		String expected =  "numeric";
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetDatasize() throws Exception{
		
		Integer  actual = _testInstance.getDatasize();
		Integer  expected =  18;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetDatascale() throws Exception{
		
		Integer  actual = _testInstance.getDatascale();
		Integer  expected =  0;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetUniquevalue() throws Exception{
		
		Long  actual = _testInstance.getUniquevalue();
		Long  expected = (long) 255;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetNullable() throws Exception{
		
		Integer  actual = _testInstance.getNullable();
		Integer  expected = 0;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetIndexes() throws Exception{
		
		String  actual = _testInstance.getIndexes();
		String  expected = "";
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
	public void testgetDataid() throws Exception{
		
		String  actual = _testInstance.getDataid();
		String  expected = DATAID;
		assertEquals(expected, actual);
		//System.out.println("actaul -"+actual);
	}
	
	@Test
	public void testgetReleaseid() throws Exception{
		
		String  actual = _testInstance.getReleaseid();
		String  expected = RELEASEID;
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
	public void testgetIncludesql() throws Exception{
		
		Integer  actual = _testInstance.getIncludesql();
		Integer  expected = 787;
		//assertEquals(expected, actual);
		System.out.println("actaul -"+actual);
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
