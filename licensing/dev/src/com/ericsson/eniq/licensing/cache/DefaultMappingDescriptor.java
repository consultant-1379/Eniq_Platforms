/**
 ** 
 */
package com.ericsson.eniq.licensing.cache;

/**
 * @author ecarbjo
 *
 */
public class DefaultMappingDescriptor implements MappingDescriptor {

	private static final long serialVersionUID = -1233137765292135581L;
  final private String[] nameArray;
	private String name;
	private MappingType type;
	
	/**
	 * Default constructor
	 * @param names the names
	 * @param type the mapping type of the mapping descriptor
	 */
	public DefaultMappingDescriptor(final String[] names, final MappingType type) {
		if(names == null){
			this.nameArray = null;
		}else{
			this.nameArray = names.clone();			
		}
		this.type = type;
		
		if (names != null && names.length > 0) {
		  name = names[0];
		  for (int i = 1; i<names.length; i++) {
		    name.concat(":" + names[i]);
		  }
		}
	}
	
	/**
	 * Set the type of mapping we want. The name will always be interpreted as a CXC number, and the type attribute will refer to the kind of response we want
	 * @param type the type of mapping that is desired.
	 */
	public void setType(final MappingType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see com.ericsson.eniq.licensing.cache.MappingDescriptor#getType()
	 */
	public MappingType getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see com.ericsson.eniq.licensing.cache.ElementDescriptor#getName()
	 */
	public String getName() {
	  return name;
	}

  /* (non-Javadoc)
   * @see com.ericsson.eniq.licensing.cache.MappingDescriptor#getFeatureNames()
   */
  public String[] getFeatureNames() {
    if (nameArray == null) {
      return null;
    } else { 
      return nameArray.clone();
    }
  }
}
