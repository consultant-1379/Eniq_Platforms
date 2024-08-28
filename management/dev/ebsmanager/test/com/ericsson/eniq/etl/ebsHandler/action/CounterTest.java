package com.ericsson.eniq.etl.ebsHandler.action;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import junit.framework.TestCase;

public class CounterTest extends TestCase {
    private Counter tInstance = null;
    public CounterTest(){
        super("CounterTest");
    }
    @Before
    public void setUp(){
        tInstance = new Counter();
    }
    @After
    public void tearDown(){
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
    public void testSetSize(){
        final String size = " numeric(18)";
        tInstance.setSize(size);
        Assert.assertFalse("There should be no spaces in the size ",
                tInstance.getSize().contains(" "));
    }
    @Test
    public void testSetAggregation(){
        final String aggregation = " peg ";
        tInstance.setAggregation(aggregation);
        Assert.assertFalse("There should be no spaces in the aggregation ",
                tInstance.getAggregation().contains(" "));
    }
    @Test
    public void testSetType(){
        final String type = " we ";
        tInstance.setType(type);
        Assert.assertFalse("There should be no spaces in the type ",
                tInstance.getType().contains(" "));
    }
}
