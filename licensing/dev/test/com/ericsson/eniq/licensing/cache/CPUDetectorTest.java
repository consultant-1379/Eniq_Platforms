package com.ericsson.eniq.licensing.cache;

import com.ericsson.eniq.common.lwp.LwProcess;
import java.io.BufferedReader;
import java.io.IOException;
import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for CPUDetector class.
 * @author eciacah
 */
public class CPUDetectorTest {
private CPUDetector testInstance;
  
  protected Mockery context = new Mockery();
  {
      // we need to mock classes, not just interfaces.
      context.setImposteriser(ClassImposteriser.INSTANCE);
  }
    
  /** Mock objects */
  Process mockProcess;
  BufferedReader mockBufferedReader;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockProcess = context.mock(Process.class);
    mockBufferedReader = context.mock(BufferedReader.class);
    testInstance = new CPUDetector() {
            
      // Override the following methods for the tests. Allows you to simulate output from the command.
      protected Process executeCommand(final String command) throws IOException {
        return mockProcess;
      }
      
      protected BufferedReader createBufferedReaderForProcess(final Process process) {
        return mockBufferedReader;
      }
    };
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * Positive test case where the output is as we expect it.
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfPhysicalCPUs() throws InterruptedException, IOException {  
    
    final int expectedResult = 5;
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("5"));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfPhysicalCPUs(true);
    Assert.assertTrue("Successful run of getNumberOfPhysicalCPUs should return correct value", result == expectedResult);
  }
  
  /**
   * A line with an error message is in the output.
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfPhysicalCPUsCommandGivesError() throws InterruptedException, IOException {
            
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("error"));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfPhysicalCPUs(true);
    Assert.assertTrue("Error in psrinfo command should return -1 for value", result == -1);
  }
  
  /**
   * A line with an empty string is in the output.
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfPhysicalCPUsCommandGivesEmptyString() throws InterruptedException, IOException {
            
    final int expectedResult = 0;
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue(""));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfPhysicalCPUs(true);
    Assert.assertTrue("Error in psrinfo command should return -1 for value", result == expectedResult);
  }
  
  /**
   * Positive test case where the output is as we expect it.
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfCores() throws InterruptedException, IOException {
    
    final int expectedResult = 3;
    
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("0 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("1 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("2 some other text"));
        
        // No more results:
        oneOf(mockBufferedReader).readLine();
        will(returnValue(null));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfCores(true);
    Assert.assertTrue("Successful run of getNumberOfCores should return correct value", result == expectedResult);
  }
  
  /**
   * A line with an error message is in the output.
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfCoresGivesError() throws InterruptedException, IOException {
    
    final int expectedResult = 2;
    
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("0 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("An error message"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("2 some other text"));
        
        // No more results:
        oneOf(mockBufferedReader).readLine();
        will(returnValue(null));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfCores(true);
    Assert.assertTrue("A line with an error should not be counted as a core by getNumberOfCores", result == expectedResult);
  }
  
  /**
   * Several lines with error messages are in the output. 
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfCoresGivesSeveralErrors() throws InterruptedException, IOException {
    
    final int expectedResult = 1;
    
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("0 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("An error message"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("Another error message"));
        
        // No more results:
        oneOf(mockBufferedReader).readLine();
        will(returnValue(null));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfCores(true);
    Assert.assertTrue("A line with an error should not be counted as a core by getNumberOfCores", result == expectedResult);
  }
  
  /**
   * A line with no characters is in the output. 
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfCoresGivesEmptyString() throws InterruptedException, IOException {
    
    final int expectedResult = 2;
    
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("0 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue(""));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("2 some other text"));
        
        // No more results:
        oneOf(mockBufferedReader).readLine();
        will(returnValue(null));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfCores(true);
    Assert.assertTrue("A line with an error should not be counted as a core by getNumberOfCores", result == expectedResult);
  }
  
  /**
   * A line with only a carriage return character is in the output. 
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfCoresCarriageReturn() throws InterruptedException, IOException {
    
    final int expectedResult = 2;
    
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("0 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("\\r"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("2 some other text"));
        
        // No more results:
        oneOf(mockBufferedReader).readLine();
        will(returnValue(null));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfCores(true);
    Assert.assertTrue("A line with a carriage return should not be counted as a core by getNumberOfCores", result == expectedResult);
  }
  
  /**
   * A line with only a new line character is in the output. 
   * @throws InterruptedException
   * @throws IOException
   */
  @Test
  public void testGetNumberOfCoresNewLine() throws InterruptedException, IOException {
    
    final int expectedResult = 2;
    
    context.checking(new Expectations() {
      {
        oneOf(mockProcess).waitFor();
        will(returnValue(0));
        
        // Command returns a single line with the number 5:
        oneOf(mockBufferedReader).readLine();
        will(returnValue("0 some other text"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("\\n"));
        
        oneOf(mockBufferedReader).readLine();
        will(returnValue("2 some other text"));
        
        // No more results:
        oneOf(mockBufferedReader).readLine();
        will(returnValue(null));
        
        oneOf(mockBufferedReader).close();
      }
    });

    final int result = testInstance.getNumberOfCores(true);
    Assert.assertTrue("A line with a new line should not be counted as a core by getNumberOfCores", result == expectedResult);
  }

  @Test
  public void testGetCoreCountRMIHelper(){
    testInstance.getNumberOfCores(false);
  }
  
}
