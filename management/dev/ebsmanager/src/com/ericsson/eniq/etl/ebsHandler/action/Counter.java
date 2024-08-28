package com.ericsson.eniq.etl.ebsHandler.action;

public class Counter {

    private String name;

    private String desc;

    private String size;

    private String aggregation;

    private String type;

    public Counter() {

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name.replaceAll("\\.", "_").trim();

    }

    public String getSize() {
        return size;
    }

    public void setSize(final String size) {
        this.size = size.trim();
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(final String aggregation) {
        this.aggregation = aggregation.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type.trim();
    }

}
