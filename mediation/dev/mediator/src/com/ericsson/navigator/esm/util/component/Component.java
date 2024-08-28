package com.ericsson.navigator.esm.util.component;

public interface Component {

	void initialize() throws ComponentInitializationException;
	void shutdown() throws ComponentShutdownException;
	String getComponentName();
}
