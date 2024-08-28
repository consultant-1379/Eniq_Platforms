package com.ericsson.navigator.esm.util;

public class Hash {
	private static final long FNV_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV_64_PRIME = 0x100000001b3L;

	//for PMD
	private Hash(){		
	}
	
	/**
	 * Hash algorithm used by ESM to generate the unique ID. String.HashCode()
	 * is not good enough since it provides only a 32 bit integer with a bad
	 * spread. So, be aware not to use Collections that use the hashCode function.
	 * 
	 * FNV hashes are designed to be fast while maintaining a low collision
	 * rate. The FNV speed allows one to quickly hash lots of data while
	 * maintaining a reasonable collision rate.
	 * 
	 * @see http://www.isthe.com/chongo/tech/comp/fnv/
	 * @see http://en.wikipedia.org/wiki/Fowler_Noll_Vo_hash
	 */
	public static long fnv_64_1a(final String k) {
		long rv = FNV_64_INIT;
		final int len = k.length();
		for (int i = 0; i < len; i++) {
			rv ^= k.charAt(i);
			rv *= FNV_64_PRIME;
		}
		return rv & Long.MAX_VALUE;
	}	
}
