package com.ericsson.sim.plugin;

//import bsh.Interpreter;

import com.ericsson.sim.exception.SIMException;

public class PluginLoader {
	
	private static PluginLoader obj;
	private static int instance;
	
	private PluginLoader(){
		obj = this;
	}
	
	public synchronized PluginParent getPlugin(String pluginName) throws SIMException{
				PluginParent plugin = null;
		try{
		if(pluginName.endsWith(".bsh")){
//			Interpreter interpreter = new Interpreter();
//			interpreter.source(pluginName);
//			plugin = (PluginParent) interpreter.getInterface(PluginParent.class);
			
		}else{
			ClassLoader classLoader = PluginLoader.class.getClassLoader();
			Class<?> aClass = classLoader.loadClass(pluginName);
			plugin = (PluginParent) aClass.newInstance();
		}
		}catch(Exception e){
			throw new SIMException("Unable to load plugin: " + pluginName, e);
		}
		
		if(plugin == null){
			throw new SIMException("Unable to load plugin: " + pluginName);
		}
		
		return plugin;
	}
	
	
	
	public static PluginLoader getInstance(){
		if(instance ==0){
			instance = 1;
			return new PluginLoader();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
}
