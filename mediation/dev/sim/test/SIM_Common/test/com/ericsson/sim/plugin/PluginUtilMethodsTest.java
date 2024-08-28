package com.ericsson.sim.plugin;

import static org.junit.Assert.*;

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
public class PluginUtilMethodsTest {

	private Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
      
    private PluginUtilMethods plu; 
    private Node node = context.mock(Node.class);
    private SFTPproperties properties = context.mock(SFTPproperties.class);
    
    private final String CURRENT_FILE_NAME = "currentFilename";
    private final int NODE_ID = 2;
    private final String IP_ADDRESS = "10.0.0.1";
    private final String PREFIX_VALUE = "prefix";
    private final String NODE_NAME = "name";
	
	@Before
	public void setUp() throws Exception { 
		plu = new PluginUtilMethods();
	}
	
	@Test
	public void testReNameFile() {
		context.checking(new Expectations() {
			{
				oneOf(properties).getProperty("fileRenaming");will(returnValue("%H_%F_%N_%P_%I"));
				allowing(node).getID();will(returnValue(NODE_ID));
				allowing(properties).getProperty("filePrefix");will(returnValue(PREFIX_VALUE));
				allowing(node).getName();will(returnValue(NODE_NAME));
				allowing(node).getProperty("IPAddress");will(returnValue(IP_ADDRESS));

			}
		});
		
		String expected = NODE_ID+"_"+CURRENT_FILE_NAME+"_"+NODE_NAME+"_"+PREFIX_VALUE+"_"+IP_ADDRESS+".txt";
		String renamedFile = plu.renameFile(node, properties, CURRENT_FILE_NAME+".txt").toString();
		
		assertEquals(expected, renamedFile);

	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
}




