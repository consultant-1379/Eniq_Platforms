package com.ericsson.sim.model.protocol.snmp;

import java.util.HashMap;
import com.ericsson.sim.model.protocol.snmp.Counter;

// jmock and junit imports
import static org.junit.Assert.*;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//annotation does the same as  putting "mockery.assertIsSatisfied()"
//at the end of each test case
@RunWith(JMock.class)
public class CounterTestMock {

	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    // create mock object
	private final Counter counter = context.mock(Counter.class);
	private static HashMap<String, String> expectedProperties = new HashMap<String, String>();
	
	@Before
	public void addProperties(){
		// generate expected values
		for(int i = 0; i < 3; i++){
			expectedProperties.put("key" + i, "value" + i);
		}
	}
	
	@Test
	public void testAddAndGetProperty() {
		// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
		context.checking(new Expectations() {
			{
				// allowing = method can be called as many times as you want
				// not specifying a return value, jmock will assume a void method
				allowing(counter).addProperty(with(equal("key")), with(equal("value")));
				allowing(counter).getProperty(with(equal("key"))); will(returnValue("value"));
			}
		});
		// running test
		counter.addProperty("key", "value");
		assertEquals("value", counter.getProperty("key"));
	}
	
	@Test
	public void testGetProperties(){
		context.checking(new Expectations() {
			{
				for(int i = 0; i < 3; i++){
					exactly(1).of(counter).addProperty(with(equal("key" + i)), with(equal("value" + i)));
				}
				allowing(counter).getProperties(); will(returnValue(expectedProperties));
			}
		});
		for(int i = 0; i < 3; i++){
			counter.addProperty("key" + i, "value" + i);
		}
		HashMap<String, String> actual = counter.getProperties();
		for(int i = 0; i < 3; i++){
			assertEquals(expectedProperties.get(i), actual.get(i));
		}
	}

}
