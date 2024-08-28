/**
 * 
 */
package com.ericsson.eniq.flssymlink.symlink;

import java.util.logging.Logger;

/**
 * 
 * @author xnagdas
 *
 */

public class SymbolicLinkCreationHelper {
	
	EniqSymbolicLink symLink = null;
	Logger log;
	
	/**
	 * To create symbolic links for files collected
	 */
	private static EniqSymbolicLinkFactory eniqstatsSymbolicLinkFactory;

	/**
	 * Only instance of the class
	 */
	private static volatile SymbolicLinkCreationHelper instance = null;

	
	/**
	 * getter method for the instance of the class
	 */
	public static SymbolicLinkCreationHelper getInstance() {
		if (instance == null) {
			synchronized (SymbolicLinkCreationHelper.class) {
				if (instance == null) {
					instance = new SymbolicLinkCreationHelper();
				}
			}
		}
		return instance;
	}

/**
 * 	
 * @param nodeType
 * @param absoluteOssFilePath
 * @param log
 */	
public void createSymbolicLink(final String nodeType, final String absoluteOssFilePath, Logger log) {
	
		AbstractSymbolicLinkFactory symbolicLinkFactory = null;
		this.log = log;

		try {
			
			symbolicLinkFactory = getEniqstatsSymbolicLinkFactory();

			if (symbolicLinkFactory != null) {
					symbolicLinkFactory.createSymbolicLink(nodeType,absoluteOssFilePath);
			}
		} catch (Exception e) {
			log.warning(e + " Symbolic Link Creation Failed for "+ absoluteOssFilePath);
		}

	}	
	
/**
 * 
 * @return
 */
private EniqSymbolicLinkFactory getEniqstatsSymbolicLinkFactory() {
		if (eniqstatsSymbolicLinkFactory == null) {
			synchronized (EniqSymbolicLinkFactory.class) {
				if (eniqstatsSymbolicLinkFactory == null) {
					final EniqSymbolicLinkFactory eniqstatsSymbolicLinkFactoryTemp = new EniqSymbolicLinkFactory();
					eniqstatsSymbolicLinkFactoryTemp.initialiseFactory(log);
						//log.info(" SymbolicLink Factory successfully initialized");
					eniqstatsSymbolicLinkFactory = eniqstatsSymbolicLinkFactoryTemp;
				}
			}

		}
		return eniqstatsSymbolicLinkFactory;
	}

}


