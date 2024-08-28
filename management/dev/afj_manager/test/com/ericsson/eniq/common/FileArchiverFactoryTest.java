/**
 * 
 */
package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;

import com.ericsson.eniq.afj.common.FileArchiver;
import com.ericsson.eniq.afj.common.FileArchiverFactory;
import com.ericsson.eniq.afj.common.MIMFileArchiver;


/**
 * @author eheijun
 *
 */
public class FileArchiverFactoryTest {

  private final Mockery context = new JUnit4Mockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  /**
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
    FileArchiverFactory.setInstance(null);
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.common.FileArchiverFactory#getInstance()}.
   */
  @Test
  public void testGetGivenInstance() {
	  final FileArchiver mockFileArchiver = context.mock(FileArchiver.class);
    FileArchiverFactory.setInstance(mockFileArchiver);
    try {
    	final FileArchiver fileArchiver = FileArchiverFactory.getInstance();
      assertTrue(fileArchiver != null);
      assertTrue(fileArchiver == mockFileArchiver);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.FileArchiverFactory#getInstance()}.
   */
  @Test
  public void testGetDefaultInstance() {
    FileArchiverFactory.setInstance(null);
    try {
    	final FileArchiver fileArchiver = FileArchiverFactory.getInstance();
      assertTrue(fileArchiver != null);
      assertTrue(fileArchiver instanceof MIMFileArchiver);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
