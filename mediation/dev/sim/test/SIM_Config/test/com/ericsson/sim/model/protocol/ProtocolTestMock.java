package com.ericsson.sim.model.protocol;

import com.ericsson.sim.model.protocol.Protocol;

// jmock and junit imports
import static org.junit.Assert.*;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.integration.junit4.JMock;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.junit.Test;
import org.junit.runner.RunWith;

//annotation does the same as  putting "mockery.assertIsSatisfied()"
//at the end of each test case
@RunWith(JMock.class)
public class ProtocolTestMock {
	
	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    // creating mock object
    private final Protocol protocol = context.mock(Protocol.class);
    
    @Test
    public void getsetNameTest(){
    	context.checking(new Expectations() {
			{
				exactly(1).of(protocol).setName(with(equal("name")));
				exactly(1).of(protocol).getName(); will(returnValue("name"));
			}
		});
    	protocol.setName("name");
    	assertEquals(protocol.getName(), "name");
    }
    
    @Test
    public void addgetIntervalTest(){
    	// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
    	context.checking(new Expectations() {
			{
				exactly(1).of(protocol).addInterval(with(equal(0)));
				exactly(1).of(protocol).getInterval(); will(returnValue(0));
			}
		});
    	// running test
    	protocol.addInterval(0);
    	assertEquals(0, protocol.getInterval());
    }
    
    @Test
    public void removeIntervalTest(){
    	context.checking(new Expectations() {
			{
				exactly(1).of(protocol).addInterval(with(equal(0)));
				exactly(1).of(protocol).removeInterval(with(equal(0)));
				allowing(protocol).getInterval(); will(onConsecutiveCalls(returnValue(0), throwException( new NullPointerException())));
			}
		});
    	protocol.addInterval(0);
    	assertNotNull(protocol);
    	protocol.removeInterval(protocol.getInterval());	
    	try{
    		protocol.getInterval();
    		assertTrue(false);
    	}catch(NullPointerException e){
    		assertTrue(true);
    	}
    }

}
