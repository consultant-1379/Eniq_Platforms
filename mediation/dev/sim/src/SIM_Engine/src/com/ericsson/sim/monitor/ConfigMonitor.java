package com.ericsson.sim.monitor;

import com.ericsson.sim.engine.EngineComponent;

public class ConfigMonitor implements EngineComponent{

	protected MonitoringThread mt;
	
	public ConfigMonitor(){
	}

	@Override
	public void startup() {
		mt = new MonitoringThread();
		mt.start();
	}

	@Override
	public void exit() {
		mt.exit();
	}
	
	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
