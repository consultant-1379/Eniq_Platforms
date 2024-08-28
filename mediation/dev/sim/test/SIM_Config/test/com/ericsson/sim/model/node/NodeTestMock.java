package com.ericsson.sim.model.node;


import java.util.ArrayList;
import com.ericsson.sim.model.node.Node;

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
public class NodeTestMock {

	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    // creating mock object
    private final Node node = context.mock(Node.class);
    private ArrayList<Integer> protocols = new ArrayList<Integer>();
    
    // expected values
    @Before
    public void addProtocols(){
    	for(int i = 0; i < 5; i++){
    		protocols.add(i);
    	}
    }
    
	@Test
	public void getsetNametest() {
		// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
		context.checking(new Expectations() {
			{
				// strict boundary conditions, each method only be called once,
				// input arguments and return values must both be "name"
				// if not test will fail
				exactly(1).of(node).setName(with(equal("name")));
				exactly(1).of(node).getName(); will(returnValue("name"));
			}
		});
		// running test
		node.setName("name");
		assertEquals("name",node.getName());
	}
	
	@Test
	public void addgetPropertyTest(){
		context.checking(new Expectations() {
			{
				exactly(1).of(node).addProperty(with(equal("key")), with(equal("value")));
				exactly(1).of(node).getProperty("key"); will(returnValue("value"));
			}
		});
		
		node.addProperty("key", "value");
		assertEquals(node.getProperty("key"), "value");
	}
	
	@Test
	public void addgetProtocolsTest(){
		context.checking(new Expectations() {
			{
				for(int i = 0; i < 5; i++){
					exactly(1).of(node).addProtocol(with(i));
				}
				exactly(1).of(node).getProtocols(); will(returnValue(protocols));
			}
		});
		
		for(int i = 0; i < 5; i++){
			node.addProtocol(i);
		}
		protocols = node.getProtocols();
		for(int i : protocols){
			assertEquals(i, protocols.get(i), 0.1);
		}
	}
	
	@Test
	public void updateFirstROPTest(){
		context.checking(new Expectations() {
			{
				for(int i = 1; i <= 2; i++){
					allowing(node).updateFirstRop(with(equal(i)));
				}
				allowing(node).addProperty(with(equal("firstRop")), with(any(String.class)));
				allowing(node).getProperty(with(equal("firstRop"))); will(onConsecutiveCalls(returnValue(""+1), returnValue(""+1+","+2)));
			}
		});
		node.addProperty("firstRop", null);
		node.updateFirstRop(1);
		assertEquals(node.getProperty("firstRop"), ""+1);
		node.updateFirstRop(2);
		assertEquals(node.getProperty("firstRop"), ""+1+","+2);
	}
	
	@Test
	public void getsetIDTest(){
		context.checking(new Expectations() {
			{
				exactly(1).of(node).setName(with(equal("name")));
				exactly(1).of(node).addProperty(with(equal("uniqueID")), with(equal("1")));
				exactly(1).of(node).addProperty(with(equal("IPAddress")), with(equal("2")));
				exactly(1).of(node).generateID(); 
				exactly(1).of(node).getName(); will(returnValue("name"));
				exactly(1).of(node).getProperty(with(equal("IPAddress"))); will(returnValue("2"));
				exactly(1).of(node).getProperty(with(equal("uniqueID"))); will(returnValue("1"));
				exactly(1).of(node).getID(); will(returnValue(("name:2:1").hashCode()));
			}
		});
		node.setName("name");
		node.addProperty("uniqueID", "1");
		node.addProperty("IPAddress", "2");
		node.generateID();
		String idstring = node.getName() + ":" + node.getProperty("IPAddress") + ":" + node.getProperty("uniqueID");
		assertEquals(idstring.hashCode(), node.getID(), 1);
	}

}









