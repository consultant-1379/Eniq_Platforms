/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.config;

import javax.servlet.ServletContext;

import com.ericsson.eniq.busyhourcfg.exceptions.BusyhourConfigurationException;
import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * @author eheijun
 * @copyright Ericsson (c) 2009
 * 
 */
public interface BusyhourConfiguration {

  BusyhourProperties getBusyhourProperties(final ServletContext context, final BusyhourEnvironment environment) throws BusyhourConfigurationException;

  ETLCServerProperties getETLCServerProperties(final ServletContext context, final BusyhourEnvironment environment) throws BusyhourConfigurationException;

}
