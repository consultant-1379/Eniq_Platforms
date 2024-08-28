package ttctests;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tableTreeUtils.TableInformation;

/**
 * Test for TableInformation.
 * @author ECIACAH
 *
 */
public class TableInformationTest extends TestCase {

  private static final String TABLE_TYPE = "tableType";
  private TableInformation testInstance;
  private Properties properties = new Properties();

  @Before
  public void setUp() throws Exception {

    final String[] defaultColumnNames = new String[] {"column1", "column2", "column3"};
    final int[] columnWidths= new int[] {100, 100, 100};
    
    properties.setProperty("table." + TABLE_TYPE + ".column.order", "Source\\:Target\\:Type\\:Config\\:Description\\:Order \\No\\:");
        
    testInstance = new TableInformation(TABLE_TYPE, defaultColumnNames, columnWidths, 50) {
      
      @Override
      protected synchronized Properties readProps() {
        return properties;      
      }
      
      @Override
      public synchronized void setColumnNamesInOrderToProperties(String[] names) {
        
      }
      
      @Override
      public synchronized void setColumnWidths(String[] colNames, int[] widths) {
        
      }
      
      public void resetColumns() {
        properties.setProperty("table." + TABLE_TYPE + ".column.order", "column1:column2:column3");
      }
    };
  }

  @After
  public void tearDown() throws Exception {
    // 
  }

  @Test
  public void testGetColumnNamesInOrderFromProperties() {    
    final String[] columnNamesFromProperties = testInstance.getColumnNamesInOrderFromProperties();
    Assert.assertTrue("Column names should be updated if properties do not match", columnNamesFromProperties[0].equals("column1"));
    Assert.assertTrue("Column names should be updated if properties do not match", columnNamesFromProperties[1].equals("column2"));
    Assert.assertTrue("Column names should be updated if properties do not match", columnNamesFromProperties[2].equals("column3"));
  }



}
