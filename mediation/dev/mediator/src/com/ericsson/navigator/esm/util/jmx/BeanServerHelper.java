package com.ericsson.navigator.esm.util.jmx;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.log4j.Logger;

public abstract class BeanServerHelper {
	
	private static final String classname = BeanServerHelper.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	private static Map<Object, ObjectName> m_ObjectNameMap = new Hashtable<Object, ObjectName>();

	public static void registerMBean(final MBean mBean){
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".registerMBean(); " + mBean.getClass().getName());
		}
	    try {
	    	final StringBuffer name = new StringBuffer(mBean.getClass().getPackage().getName());
	    	name.append(":type=");
	    	name.append(mBean.getClass().getSimpleName());
	    	final ObjectName objectName = addToObjectMap(mBean, name, new ObjectName(name.toString()));
	    	ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, objectName);
		} catch (Exception e) {
			logger.error(classname + ".registerMBean(); Error occured when registering MBean: " + 
					mBean.getClass().getName(), e);
		}
	}

	private static ObjectName addToObjectMap(final Object mBean,
			final StringBuffer name, ObjectName objectName)
			throws MalformedObjectNameException {
		synchronized(m_ObjectNameMap){
			for(int i = 1 ; m_ObjectNameMap.containsValue(objectName) ; ++i){
				objectName = new ObjectName(name.toString()+i);//NOPMD
			}
			m_ObjectNameMap.put(mBean, objectName);
		}
		return objectName;
	}
	
	public static <T extends MDynamicBean> void registerMDynamicBean(final T mBean, final Class<T> c){
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".registerMDynamicBean(); " + mBean.getClass().getName());
		}
	    try {
	    	final StringBuffer name = new StringBuffer(mBean.getClass().getPackage().getName());
	    	name.append(":type=");
	    	name.append(mBean.getClass().getSimpleName());
    		name.append(".");
	    	//Remove not allowed characters in object name
    		name.append(mBean.getInstanceName().replaceAll("[,=:]",".").replaceAll("[ *?]",""));
    		final ObjectName objectName = addToObjectMap(mBean, name, new ObjectName(name.toString()));
    		final Object bean = new StandardMBean(mBean, c);
	    	ManagementFactory.getPlatformMBeanServer().registerMBean(bean, objectName);
		} catch (Exception e) {
			logger.error(classname + ".registerMDynamicBean(); Error occured when registering MBean: " + 
					mBean.getClass().getName(), e);
		}
	}
	
	public static void unRegisterMBean(final MBean mBean){
		unRegisterMBeanObject(mBean);
	}
	
	public static void unRegisterMBean(final MDynamicBean mBean){
		unRegisterMBeanObject(mBean);
	}
	
	private static void unRegisterMBeanObject(final Object mBean){
		if(logger.isDebugEnabled()){
			logger.debug(classname + ".unRegisterMBean(); " + mBean.getClass().getName());
		}
		final ObjectName name = m_ObjectNameMap.remove(mBean);
		if(name == null){
			if(logger.isDebugEnabled()){
				logger.debug(classname + ".unRegisterMBean(); instance name not found for: " + mBean.getClass().getName());
			}
			return;
		}
	    try {
	    	final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	    	mbs.unregisterMBean(name);
		} catch (Exception e) {
			logger.error(classname + ".registerMBean(); Error occured when registering MBean: " + 
					mBean.getClass().getName(), e);
		}
	}
}
