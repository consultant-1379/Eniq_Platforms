package com.ericsson.navigator.esm.agent.rmi;

import com.ericsson.navigator.esm.util.jmx.MBean;

public interface ManagedSystemAgentInterfaceMBean extends MBean{

	int getSessionCount();
}
