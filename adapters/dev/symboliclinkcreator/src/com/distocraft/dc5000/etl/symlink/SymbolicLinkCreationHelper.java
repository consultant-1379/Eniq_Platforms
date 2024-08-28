/**
 * 
 */
package com.distocraft.dc5000.etl.symlink;

import com.distocraft.dc5000.etl.symlink.SymbolicLinkCreationFailedException;

/**
 * 
 * @author xnagdas
 *
 */

public class SymbolicLinkCreationHelper {
	
	EniqSymbolicLink symLink = null;
	
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
 * @param node
 * @param absoluteOssFilePath
 */
	
	public void createSymbolicLink(final String nodeType, final String absoluteOssFilePath) {
	
		AbstractSymbolicLinkFactory symbolicLinkFactory = null;

		try {
			
			symbolicLinkFactory = getEniqstatsSymbolicLinkFactory();

			if (symbolicLinkFactory != null) {
					symbolicLinkFactory.createSymbolicLink(nodeType,absoluteOssFilePath);
			}
		} catch (final SymbolicLinkCreationFailedException e) {
			System.out.println(e + " Symbolic Link Creation Failed for "+ absoluteOssFilePath);
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
					eniqstatsSymbolicLinkFactoryTemp.initialiseFactory();
						System.out.println(" Stats SymbolicLink Factory successfully initialized");
					eniqstatsSymbolicLinkFactory = eniqstatsSymbolicLinkFactoryTemp;
				}
			}

		}
		return eniqstatsSymbolicLinkFactory;
	}

}


