package com.distocraft.dc5000.etl.gui.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.distocraft.dc5000.common.ServicenamesHelper;

/**
 * @author eheijun
 * 
 */
public class ServiceNames { // NOPMD by eheijun on 03/06/11 08:43

	private static final Log log = LogFactory.getLog(ServiceNames.class);

	public static final String SPECIAL_CHARECTER1 = "#";

	public static final String SPECIAL_CHARECTER2 = "~";

	public static final String DELIMITER = "::";

	public static final String EXECUTION_CONTEXT = "ec";

	private final Set<String> hosts;

	private final Map<String, String> hostToServiceMap = new HashMap<String, String>();

	private final Set<String> services = new HashSet<String>();

	private final Set<String> ecs = new HashSet<String>();

	public ServiceNames() {
		this.hosts = new HashSet<String>();
	}

	public ServiceNames(final String filepath) { // NOPMD by eheijun on 03/06/11 08:43
		this();
		final File file = new File(filepath);
		try {
			if (file.exists()) {
				final FileReader fr = new FileReader(file);
				final BufferedReader br = new BufferedReader(fr);
				try {
					String line = null;
					StringTokenizer tokenizer = null;
					while ((line = br.readLine()) != null) {
						log.debug("Services Line as " + line);
						if (!(line.contains(SPECIAL_CHARECTER1) || line.contains(SPECIAL_CHARECTER2))) {
							tokenizer = new StringTokenizer(line, DELIMITER); // NOPMD by eheijun on 03/06/11 08:43
							if (tokenizer.hasMoreTokens()) {
								tokenizer.nextToken(); // IP, skip it
								if (tokenizer.hasMoreTokens()) {
									final String hostName = tokenizer.nextToken();
									if (tokenizer.hasMoreTokens()) {
										final String serviceName = tokenizer.nextToken();
										if (ServicenamesHelper.validateServiceNames(serviceName)) {
											if (!serviceName.toLowerCase().contains("wifi")) {
												hostToServiceMap.put(hostName, serviceName);
												services.add(serviceName);
												if (serviceName.startsWith(EXECUTION_CONTEXT)) {
													ecs.add(serviceName);
												}// service name
												hosts.add(hostName); // host name
											}
										}
									}
								}
							}
						}
					}
				} finally {
					br.close();
				}
			}
		} catch (final Exception e) {
			log.error("Exception ", e);
		}
	}

	/**
	 * Return all hosts from service_names configuration file
	 * 
	 * @return
	 */
	public Set<String> getHosts() {
		return hosts;
	}

	/**
	 * Return all services from service_names configuration file
	 * 
	 * @return
	 */
	public Set<String> getServices() {
		return services;
	}

	/**
	 * Return EC hosts from service_names configuration file
	 * 
	 * @return
	 */
	public Set<String> getECs() {
		return ecs;
	}

	/**
	 * Return hostNametoServiceNameMap
	 * 
	 * @return
	 */
	public Map<String, String> getHostToServiceMap() {
		return hostToServiceMap;
	}

	/**
	 * Returns first serviceName for given host
	 * 
	 * @param hostName
	 * @return if serviceName does not exists then returns null
	 */
	public String getFirstServiceName(final String hostName) {
		if (hostToServiceMap.containsKey(hostName)) {
			return hostToServiceMap.get(hostName);
		}
		return null;
	}

}
