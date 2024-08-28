package com.ericsson.eniq.flssymlink.fls;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.ericsson.eniq.flssymlink.StaticProperties;

/**
 * This class compares two files and creates a cache of the ENM server details
 * in the cache memory.
 *  
 */
class CacheENMServerDetails {

	static CacheENMServerDetails cacheinstance;
	static HashMap<String, ENMServerDetails> det;

	/**
	 * This function returns the ENM server details in the form of HashMap.
	 * 
	 * @return Returns the HashMap<String,ENMServerDetails> which contains the
	 *         details of the ENM server.
	 */
	static HashMap<String, ENMServerDetails> getInstance(Logger log) throws IOException {
		cacheinstance = new CacheENMServerDetails();
		det = new HashMap<String, ENMServerDetails>();

		List<String> ip = new ArrayList<String>();
		List<String> oss_id = new ArrayList<String>();

		String ref = StaticProperties.getProperty("OSS_REF_NAME_FILE_PATH", "/eniq/sw/conf/.oss_ref_name_file");
		String enm = StaticProperties.getProperty("ENM_FILE_PATH", "/eniq/sw/conf/enmserverdetail");


		
		Process pOSS = Runtime.getRuntime().exec("cat " + ref );
		BufferedReader inputOSS = new BufferedReader(new InputStreamReader(pOSS.getInputStream()));
		
		Process pENM = Runtime.getRuntime().exec("cat " +enm );
		BufferedReader inputENM = new BufferedReader(new InputStreamReader(pENM.getInputStream()));
		
		try {
			String line;

			while ((line = inputOSS.readLine()) != null) {
				String[] odd = line.split("\\s+");
				oss_id.add(odd[0]);
				ip.add(odd[1]);
			}

			String line1;
			while ((line1 = inputENM.readLine()) != null) {
				String[] odd = line1.split("\\s+");

				for (int j = 0; j < ip.size(); j++) {
					if (odd[0].equals(ip.get(j))) {
						ENMServerDetails element = new ENMServerDetails();

						element.setIp(odd[0]);
						element.setHost(odd[1]);
						element.setType(odd[2]);
						element.setUsername(odd[3]);
						element.setPassword(odd[4]);
						element.setHostname(odd[5]);
						det.put(oss_id.get(j), element);

					}
				}

			}
		} catch (FileNotFoundException e) {
			log.info("Files .oss_ref_name_file or enmserverdetail not found Exception and exception is:"+e.getMessage());
		}
		return det;
	}

}

