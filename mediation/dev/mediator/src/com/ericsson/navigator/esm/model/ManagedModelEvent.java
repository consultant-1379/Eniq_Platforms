package com.ericsson.navigator.esm.model;

import java.util.EventObject;

public class ManagedModelEvent<M extends ManagedDataList<?,?>> extends EventObject{

	private static final long serialVersionUID = 1L;
	private final M m_Managed;

	public ManagedModelEvent(final ManagedModel<M> model, final M managed) {
		super(model);
		m_Managed = managed;
	}
	
	public M getManaged(){ 
		return m_Managed;
	}

}
