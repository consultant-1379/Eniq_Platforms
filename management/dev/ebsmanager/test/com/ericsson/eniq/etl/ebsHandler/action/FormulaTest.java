package com.ericsson.eniq.etl.ebsHandler.action;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import junit.framework.TestCase;

public class FormulaTest extends TestCase {
    private Formula tInstance = null;
    public FormulaTest(){
        super("FormulaTest");
    }
    @Override @Before
    protected void setUp() throws Exception {
        tInstance = new Formula();
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
    public void testSetFunction(){
        final String func = " 123 ";
        tInstance.setFunction(func);
        Assert.assertFalse("There should be no spaces in the function name ",
                tInstance.getFunction().contains(" "));
    }
    @Test
    public void testAddArgument(){
        final String arg = " 123 ";
        Assert.assertTrue("Arg list should be empty ", tInstance.getArguments().isEmpty());
        tInstance.addArgument(arg);
        Assert.assertEquals("Arg list shouldnt be empty", 1, tInstance.getArguments().size());
        final String toCheck = tInstance.getArguments().get(0);
        Assert.assertFalse("There should be no spaces in the arguement ",
                toCheck.contains(" "));
    }
    @Test
    public void testSetMeasName(){
        final String name = " 123 ";
        tInstance.setMeasName(name);
        Assert.assertFalse("There should be no spaces in the measname ",
                tInstance.getMeasName().contains(" "));
    }
}
