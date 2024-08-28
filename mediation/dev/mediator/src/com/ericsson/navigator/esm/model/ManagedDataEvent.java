package com.ericsson.navigator.esm.model;

import java.io.Serializable;

public class ManagedDataEvent<D extends ManagedData<?,D>> implements Serializable{

	private static final long serialVersionUID = 1L;

	public enum Action {INITIAL, ADD, UPDATE, DELETE, INITIAL_START, INITIAL_END};
	
	private final Action action;
	private final D data;
	private boolean stateChanged = false;
	
	/**
	 * Creates an new AlarmEvent with a specified action.
	 * 
	 * @param alarm The alarm that caused this event
	 * @param action INITIAL, ADD, UPDATE or DELETE 
	 */
	public ManagedDataEvent(final D data, final Action action){
		this.data = data;
		this.action = action;
	}
	
	public ManagedDataEvent(final D data, final Action action, final boolean stateChanged){
		this(data, action);
		this.stateChanged = stateChanged;
	}
	
	public boolean stateChanged(){
		return stateChanged;
	}
	
	public Action getAction(){
		return action;
	}
	
	public D getData(){
		return data;
	}

}
