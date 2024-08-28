package com.ericsson.eniq.etl.ebsHandler.action;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import junit.framework.TestCase;

public class GroupTest extends TestCase {
    private Group tInstance = null;
    public GroupTest(){
        super("GroupTest");
    }
    @Override @Before
    public void setUp(){
        tInstance = new Group();
    }
    @Override @After
    protected void tearDown() throws Exception {
        tInstance = null;
    }
    @Test
    public void testSetName(){
        final String name = "abc ";
        tInstance.setName(name);
        Assert.assertFalse("There should be no spaces in the name ",
                tInstance.getName().contains(" "));
    }
    @Test
    public void setInit(){
        Assert.assertTrue("Counter list should be empty", tInstance.getCounters().isEmpty());
        Assert.assertTrue("Formula list should be empty", tInstance.getFormulas().isEmpty());
    }
    @Test
    public void testSetCounters(){
        //list is empty, null is returned...
        Assert.assertNull(tInstance.getLastCounter());
        final Counter c = new Counter();
        c.setName("a");
        tInstance.addCounter(c);
        Assert.assertEquals(1, tInstance.getCounters().size());
        final Counter c1 = new Counter();
        c1.setName("b");
        tInstance.addCounter(c1);
        Assert.assertEquals(2, tInstance.getCounters().size());
        Assert.assertEquals(c1, tInstance.getLastCounter());
    }
    @Test
    public void testSetFormulas(){
        //list is empty, null is returned...
        Assert.assertNull(tInstance.getLastFormula());
        final Formula f = new Formula();
        f.setName("a");
        tInstance.addFormula(f);
        Assert.assertEquals(1, tInstance.getFormulas().size());
        final Formula f1 = new Formula();
        f1.setName("b");
        tInstance.addFormula(f1);
        Assert.assertEquals(2, tInstance.getFormulas().size());
        Assert.assertEquals(f1, tInstance.getLastFormula());
    }
}
