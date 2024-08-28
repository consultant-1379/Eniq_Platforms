package com.ericsson.eniq.etl.ebsHandler.action;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import junit.framework.TestCase;

public class MeastypeTest extends TestCase {
    private Meastype tInstance = null;
    public MeastypeTest(){
        super("MeastypeTest");
    }
    @Override @Before
    public void setUp(){
        tInstance = new Meastype();
    }
    @Override @After
    protected void tearDown() throws Exception {
        tInstance = null;
    }
    @Test
    public void setInit(){
        Assert.assertTrue("Group list should be empty", tInstance.getGroups().isEmpty());
    }
    @Test
    public void testSetName(){
        final String name = "abc ";
        tInstance.setName(name);
        Assert.assertFalse("There should be no spaces in the name ",
                tInstance.getName().contains(" "));
    }
    @Test
    public void testAddGroup(){
        //list is empty, null is returned...
        Assert.assertNull(tInstance.getLastGroup());
        final Group g = new Group();
        g.setName("a");
        tInstance.addGroup(g);
        Assert.assertEquals(1, tInstance.getGroups().size());
        final Group g1 = new Group();
        g1.setName("b");
        tInstance.addGroup(g1);
        Assert.assertEquals(2, tInstance.getGroups().size());
        Assert.assertEquals(g1, tInstance.getLastGroup());
    }
    @Test
    public void testSetMtype(){
        final Measurementtype mType = new Measurementtype(null);
        tInstance.setMtype(mType);
        Assert.assertEquals(mType, tInstance.getMtype());
    }
}
