package com.ericsson.eniq.etl.ebsHandler.action;

import java.util.Vector;

public class Formula {

    private String name;

    private String function;

    private final Vector<String> arguments = new Vector<String>();

    private String measName;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name.trim();
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(final String function) {
        this.function = function.trim();
    }

    public Vector<String> getArguments() {
        return arguments;
    }

    public void addArgument(final String argument) {
        this.arguments.add(argument.trim());
    }

    public String getMeasName() {
        return measName;
    }

    public void setMeasName(final String measName) {
        this.measName = measName.trim();
    }

}
