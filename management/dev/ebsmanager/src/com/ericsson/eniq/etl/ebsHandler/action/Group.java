package com.ericsson.eniq.etl.ebsHandler.action;

import java.util.Vector;

public class Group {

    private String name;

    private final Vector<Counter> counters;

    private final Vector<Formula> formulas;

    public Group() {
        counters = new Vector<Counter>();
        formulas = new Vector<Formula>();
    }

    public Vector<Counter> getCounters() {
        return counters;
    }

    public Vector<Formula> getFormulas() {
        return formulas;
    }

    public void addCounter(final Counter counter) {
        this.counters.add(counter);
    }

    public void addFormula(final Formula formula) {
        this.formulas.add(formula);
    }

    public Counter getLastCounter() {
        if (counters.isEmpty()) {
            return null;
        }
        return counters.lastElement();
    }

    public Formula getLastFormula() {
        if (formulas.isEmpty()) {
            return null;
        }
        return formulas.lastElement();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name.trim();
    }

}
