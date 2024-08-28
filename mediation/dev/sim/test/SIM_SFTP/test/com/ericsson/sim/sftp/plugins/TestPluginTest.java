package com.ericsson.sim.sftp.plugins;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;



@RunWith(JMock.class)
public class TestPluginTest {
	private TestPlugin classUnderTest;
	
	
	Mockery context = new Mockery(){
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	private final Node node = context.mock(Node.class);
	private final SFTPproperties properties = context.mock(SFTPproperties.class);

	@Before
	public void setUp() throws Exception {
		classUnderTest = new TestPlugin();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		final Map<String, String> list = new HashMap<>();
		list.put("key", "value");
		context.checking(new Expectations(){
			{
				allowing(node).getID();will(returnValue(1));
				allowing(node).getName();will(returnValue("name"));
				allowing(properties).getID();will(returnValue(5));
				allowing(properties).getName();will(returnValue("name"));
				allowing(properties).getProperties().keySet();will(returnValue(list));
				allowing(properties).getProperty("key");will(returnValue("value"));
			}
		});
		node.getID();
		classUnderTest.execute(node, properties);
	}

}
