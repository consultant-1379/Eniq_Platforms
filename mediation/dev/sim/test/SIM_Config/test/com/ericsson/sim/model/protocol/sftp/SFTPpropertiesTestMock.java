package com.ericsson.sim.model.protocol.sftp;

import java.util.HashMap;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

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
public class SFTPpropertiesTestMock {

	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    // create object to be mocked
    private final SFTPproperties props = context.mock(SFTPproperties.class);
	private final HashMap<String, String> expectedProperties = new HashMap<String, String>();
	
	//expected values
	@Before
	public void addProperties(){
		for(int i = 0; i < 5; i++){
			expectedProperties.put("key" + i, "value" + i);
		}
		
	}
	
	@Test
	public void getsetNameTest(){
		// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
		context.checking(new Expectations() {
			{
				// strict boundary conditions, each method only be called once,
				// input arguments and return values must both be "name"
				// if not test will fail
				exactly(1).of(props).setName(with("name"));
				exactly(1).of(props).getName(); will(returnValue("name"));
			}
		});
		props.setName("name");
		assertEquals(props.getName(), "name");
	}
	
	@Test
	public void addgetPropertyTest(){
		context.checking(new Expectations() {
			{
				exactly(1).of(props).addProperty(with(equal("key")), with(equal("value")));
				exactly(1).of(props).getProperty("key"); will(returnValue("value"));
			}
		});
		
		props.addProperty("key", "value");
		assertEquals(props.getProperty("key"), "value");
	}
	
	@Test
	public void getPropertiesTest(){
		context.checking(new Expectations() {
			{
				for(int i = 0; i < 5; i++){
					exactly(1).of(props).addProperty(with(equal("key" + i)), with(equal("value" + i)));
				}
				exactly(1).of(props).getProperties(); will(returnValue(expectedProperties));
			}
		});
		for(int i = 0; i < 5; i++){
			props.addProperty("key" + i, "value" + i);
		}
		HashMap<String, String> actualProperties = props.getProperties();
		for(int i = 0; i < 5; i++){
			assertEquals(actualProperties.get("key" + i), expectedProperties.get("key" + i));
		}
	}
	
	@Test
	public void containsPropertiesTest() {
		context.checking(new Expectations() {
			{
				allowing(props).addProperty(with(equal("key")), with(equal("value")));
				allowing(props).containsProperty(with(equal("key"))); will(returnValue(true));
			}
		});
		props.addProperty("key", "value");
		assertTrue(props.containsProperty("key"));
	}
	
	@Test
	public void getsetIDTest(){
		context.checking(new Expectations() {
			{
				exactly(1).of(props).setName(with(equal("name")));
				exactly(1).of(props).getName(); will(returnValue("name"));
				exactly(1).of(props).generateID();
				exactly(1).of(props).getID(); will(returnValue("name".hashCode()));
			}
		});
		props.setName("name");
		props.generateID();
		assertEquals(props.getName().hashCode(), props.getID());
	}

	@Test
	public void getExecutionThreadName(){
		context.checking(new Expectations() {
			{
				exactly(1).of(props).addProperty(with(equal("ExecutionThreadName")), with(equal("exec name")));
				exactly(1).of(props).getProperty(with(equal("ExecutionThreadName"))); will(returnValue("exec name"));
				exactly(1).of(props).getExecutionThreadName(); will(returnValue("exec name"));
			}
		});
		props.addProperty("ExecutionThreadName", "exec name");
		assertEquals(props.getExecutionThreadName(), props.getProperty("ExecutionThreadName"));
	}
	
}





