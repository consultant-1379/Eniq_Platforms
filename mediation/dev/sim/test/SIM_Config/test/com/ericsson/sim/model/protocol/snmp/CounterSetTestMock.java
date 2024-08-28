package com.ericsson.sim.model.protocol.snmp;

import java.util.ArrayList;
import java.util.HashMap;
import com.ericsson.sim.model.protocol.snmp.Counter;
import com.ericsson.sim.model.protocol.snmp.CounterSet;

// jmock and junit imports
import static org.junit.Assert.*;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

// annotation does the same as  putting "mockery.assertIsSatisfied()"
// at the end of each test case
@RunWith(JMock.class)
public class CounterSetTestMock {

	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    // creating mock object
    private final CounterSet counterSet = context.mock(CounterSet.class);
    // cannot add mocked objects to other mocked objects
	private final Counter counter = new Counter();
	private static HashMap<String, String> expectedProperties = new HashMap<String, String>();
	private static HashMap<String, String> actualProperties = new HashMap<String, String>();
	private static ArrayList<Counter> expectedCounters = new ArrayList<Counter>();
	private static ArrayList<Counter> actualCounters = new ArrayList<Counter>();
	
	@Before
	public void addProperties(){
		// generate expected values
		for(int i = 0; i < 3; i++){
			expectedProperties.put("key" + i, "value" + i);
			expectedCounters.add(new Counter());
		}
	}
	
	@Test
	public void testGetAndSetName(){
		// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
		context.checking(new Expectations() {
			{
				// allowing = method can be called as many times as you want
				// not specifying a return value, jmock will assume a void method
				allowing(counterSet).setName(with(equal("name")));
				// example of a method with a return value
				exactly(1).of(counterSet).getName(); will(returnValue("name"));
			}
		});
		// running test
		counterSet.setName("name");
		assertEquals(counterSet.getName(), "name");
	}
	
	@Test
	public void testAddAndGetProperty() {
		context.checking(new Expectations() {
			{
				// input arguments can be specific values
				// if input arguments are not equal to "key" and "value", test will fail
				allowing(counterSet).addProperty(with(equal("key")), with(equal("value")));
				allowing(counterSet).getProperty(with(equal("key"))); will(returnValue("value"));
			}
		});
		counterSet.addProperty("key", "value");
		assertEquals("value", counterSet.getProperty("key"));
	}

	@Test
	public void testGetProperties(){
		context.checking(new Expectations() {
			{
				for(int i = 0; i < 3; i++){
					exactly(1).of(counterSet).addProperty(with(equal("key" + i)), with(equal("value" + i)));
				}
				
				allowing(counterSet).getProperties(); will(returnValue(expectedProperties));
			}
		});
		
		for(int i = 0; i < 3; i++){
			counterSet.addProperty("key" + i, "value" + i);
		}		
		actualProperties = counterSet.getProperties();
		for(int i = 0; i < 3; i++){
			assertEquals(expectedProperties.get("key" + i), actualProperties.get("key" + i));
		}
	}
	
	@Test
	public void testAddAndGetCounter(){
		context.checking(new Expectations() {
			{
				// input arguments can also be just generic instances of a class
				// However returning with(any(.class) will always return null
				allowing(counterSet).addCounter(with(any(Counter.class)));
				allowing(counterSet).getCounters(); will(returnValue(expectedCounters));
			}
		});
		for(int i = 0; i < 3; i++){
			counter.addProperty("key" + i, "value" + i);
			counterSet.addCounter(counter);
		}
		actualCounters = counterSet.getCounters();
		for(int i = 0; i < 3; i++){
			assertEquals(actualCounters.get(i), expectedCounters.get(i));
		}
	}
}





