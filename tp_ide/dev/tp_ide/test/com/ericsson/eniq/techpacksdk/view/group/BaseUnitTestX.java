package com.ericsson.eniq.techpacksdk.view.group;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public abstract class BaseUnitTestX {

  protected static Mockery context = createMockery();

  private static JUnit4Mockery createMockery() {
    return new JUnit4Mockery();
  }

  static { // we need to mock classes, not just interfaces.
    allowClassesToBeMocked();
  }
  
  /**
   * utility method - allows a unit test class to recreate the mockery context
   * for each unit test in class
   * can help if mocks are colliding with one another
   */
  public void recreateMockeryContext()  {
    context = createMockery();
    allowClassesToBeMocked();    
  }

  /**
   *  we need to mock classes, not just interfaces.
   */
  private static void allowClassesToBeMocked() {
    context.setImposteriser(ClassImposteriser.INSTANCE);
  }
  
  


}
