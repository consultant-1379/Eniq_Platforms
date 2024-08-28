package com.ericsson.sim.model.properties;


import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import com.ericsson.sim.model.properties.RuntimeProperties;

//jmock and junit imports
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
public class RuntimePropertiesTestMock {

	// set context of object to be mocked
	private Mockery context = new Mockery() {{
		// jmock can only mock interfaces, need setImposteriser to allow 
		// mocking of objects of classes
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    // create objects to be mocked
    private RuntimeProperties runtimeProperties = context.mock(RuntimeProperties.class);
    private final String filePath = Paths.get(".").toAbsolutePath().normalize().toString(); 
    private HashMap<String, String> expectedProps = new HashMap<String, String>();
   
	@Test
    public void getInstanceTest(){
		// jmock forces you to declare any methods that will be used on the 
		// mocked objects. You must specify how many times each method will be called,
		// and if there is a return value, what it will be
		context.checking(new Expectations() {
			{
				allowing(runtimeProperties); RuntimeProperties.getInstance(); 
			}
		});
		// running test
    	assertNotNull(RuntimeProperties.getInstance());	
    }
	
	@Test
	public void addAndGetProperty(){
		context.checking(new Expectations() {
			{
				exactly(1).of(runtimeProperties).addProperty(with(equal("key")), with(equal("value")));
				exactly(1).of(runtimeProperties).getProperty(with(equal("key"))); will(returnValue("value"));
			}
		});
		runtimeProperties.addProperty("key", "value");
		assertEquals(runtimeProperties.getProperty("key"), "value");
	}
	
	@Test
    public void writePersistedFileTest() throws Exception{
        final File path = new File(filePath + "/config");
    	context.checking(new Expectations() {
			{
				allowing(runtimeProperties).writePersistedFile(with(equal(filePath + "/config")));
			}
		});
    	//assertTrue(path.exists());
    	runtimeProperties = RuntimeProperties.getInstance();
    	runtimeProperties.writePersistedFile(filePath + "/config");
    	assertTrue((new File(filePath+"/config/properties.simc")).exists());
    }
	
	@SuppressWarnings("static-access")
	@Test
	public void loadPersistedFileTest() throws Exception {
		for(int i = 0; i < 5; i++){
    		expectedProps.put("key" + i, "value" + i);
    	}
		context.checking(new Expectations() {
			{
				allowing(runtimeProperties).writePersistedFile(with(equal(filePath + "/config")));
				allowing(runtimeProperties).loadPersistedFile(with(equal(filePath + "/config")));
				for(int i = 0; i < 5; i++){
					allowing(runtimeProperties).getProperty(with(equal("key" + i))); will(returnValue("value" + i));
				}
			}
		});
		runtimeProperties.getInstance().writePersistedFile(filePath + "/config");
		runtimeProperties.getInstance().loadPersistedFile(filePath + "/config/properties.simc");
		HashMap<String, String> actualProps = new HashMap<String, String>();
		for(int i = 0; i < 5; i++){
			actualProps.put("key" + i, runtimeProperties.getProperty("key" + i));
		}
		for(int i = 0; i < 5; i++){
			assertEquals(actualProps.get("key" + i), expectedProps.get("key" + i));
		}
	}
	
}







