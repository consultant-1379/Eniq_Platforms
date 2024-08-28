package com.ericsson.sim.model.protocol.snmp;


import java.util.ArrayList;
import java.util.HashMap;
import com.ericsson.sim.model.protocol.snmp.CounterSet;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

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
public class SNMPpropertiesTestMock {

	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    // creating mock object
    private final SNMPproperties snmpProperties = context.mock(SNMPproperties.class);
    // cannot add mocked objects to other mocked objects
    private final CounterSet counterSet = new CounterSet(); 
	private final HashMap<String, String> expectedProperties = new HashMap<String, String>();
	private ArrayList<CounterSet> expectedCounterSets = new ArrayList<CounterSet>();
	
	// expected values
	@Before
	public void addPropsCounterSets(){
		for(int i = 0; i < 5; i++){
			expectedProperties.put("key" + i, "value" + i);
			counterSet.setName("name" + i);
			counterSet.addProperty("key" + i, "value" + i);
			expectedCounterSets.add(counterSet);
		}
	}
	
	@Test
	public void testGetAndSetName(){
		context.checking(new Expectations() {
			{
				allowing(snmpProperties).setName(with(equal("name")));
				allowing(snmpProperties).getName(); will(returnValue("name"));
			}
		});
		snmpProperties.setName("name");
		assertEquals(snmpProperties.getName(), "name");
	}
	
	@Test
	public void testAddAndGetProperty() {
		// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
		context.checking(new Expectations() {
			{
				allowing(snmpProperties).addProperty(with(equal("key")), with(equal("value")));
				allowing(snmpProperties).getProperty(with(equal("key"))); will(returnValue("value"));
			}
		});
		// running test
		snmpProperties.addProperty("key", "value");
		assertEquals("value", snmpProperties.getProperty("key"));
	}
	
	@Test
	public void testGetProperties(){
		context.checking(new Expectations() {
			{
				for(int i = 0; i < 5; i++){
					exactly(1).of(snmpProperties).addProperty(with(equal("key" + i)), with(equal("value" + i)));
				}
				exactly(1).of(snmpProperties).getProperties(); will(returnValue(expectedProperties));
			}
		});
		for(int i = 0; i < 5; i++){
			snmpProperties.addProperty("key" + i, "value" + i);
		}
		HashMap<String, String> actualProperties = snmpProperties.getProperties();
		for(int i = 0; i < 5; i++){
			assertEquals(actualProperties.get("key" + i), expectedProperties.get("key" + i));
		}
	}
	
	@Test
	public void testContainsProperty(){
		context.checking(new Expectations() {
			{
				allowing(snmpProperties).addProperty(with(equal("key")), with(equal("value")));
				allowing(snmpProperties).containsProperty(with(equal("key"))); will(returnValue(true));
			}
		});
		snmpProperties.addProperty("key", "value");
		assertTrue(snmpProperties.containsProperty("key"));
	}
	
	@Test
	public void testAddAndGetCounterSets(){
		context.checking(new Expectations() {
			{
				for(int i = 0; i < 5; i++){
					allowing(snmpProperties).addCounterSet(with(any(CounterSet.class)));
				}
				allowing(snmpProperties).getCounterSets(); will(returnValue(expectedCounterSets));
			}
		});
		CounterSet testCs = new CounterSet();
		// expected values
		for(int i = 0; i < 5; i++){
			testCs.setName("name" + i);
			testCs.addProperty("key" + i, "value" + i);
			snmpProperties.addCounterSet(testCs);
		}
		ArrayList<CounterSet> actualCounterSets = snmpProperties.getCounterSets();
		for(int i = 0; i < 5; i++){
			assertEquals(actualCounterSets.get(i), expectedCounterSets.get(i));
		}
	}
	
	@Test
	public void testGetAndSetID(){
		context.checking(new Expectations() {
			{
				allowing(snmpProperties).setName(with(equal("name")));
				allowing(snmpProperties).getName(); will(returnValue("name"));
				allowing(snmpProperties).generateID();
				allowing(snmpProperties).getID(); will(returnValue("name".hashCode()));
			}
		});
		snmpProperties.setName("name");
		snmpProperties.generateID();
		assertEquals(snmpProperties.getName().hashCode(), snmpProperties.getID());
	}

	@Test
	public void testGetExecutionThreadName(){
		context.checking(new Expectations() {
			{
				allowing(snmpProperties).addProperty(with(equal("ExecutionThreadName")), with(equal("exec name")));
				allowing(snmpProperties).getProperty(with(equal("ExecutionThreadName"))); will(returnValue("exec name"));
				allowing(snmpProperties).getExecutionThreadName(); will(returnValue("exec name"));
			}
		});
		snmpProperties.addProperty("ExecutionThreadName", "exec name");
		assertEquals(snmpProperties.getExecutionThreadName(), snmpProperties.getProperty("ExecutionThreadName"));
	}

}
