package com.ericsson.eniq.etl.ebsHandler.action;

import com.distocraft.dc5000.repository.dwhrep.Measurementtype;

import java.util.Vector;

public class Meastype {

    private String name;

    private final Vector<Group> groups;

    private Measurementtype mtype;

    public Meastype() {
        groups = new Vector<Group>();
    }

    public Vector<Group> getGroups() {
        return groups;
    }

    public Group getLastGroup() {
        if (groups.isEmpty()) {
            return null;
        }
        return groups.lastElement();
    }

    public void addGroup(final Group group) {
        this.groups.add(group);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name.trim();
    }

    public Measurementtype getMtype() {
        return mtype;
    }

    public void setMtype(final Measurementtype mtype) {
        this.mtype = mtype;
    }
}
