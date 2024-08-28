/**
 * 
 */
package com.ericsson.eniq.afj.common;

//import java.util.HashMap;
import java.util.List;
//import java.util.Map;


/**
 * @author eheijun
 *
 */
public class AFJMeasurementType {

	private String typeName;

	private Boolean typeNew;

	private String tpName;

	private String tpVersion;
	
	private String ratio;
	
	private String description;

	//	private String tagName;

	//	private Map<String, List<AFJMeasurementCounter>> tagToCountersMap = new HashMap<String, List<AFJMeasurementCounter>>();

	private List<AFJMeasurementTag> tags; 

	//	List <AFJMeasurementCounter> newCounters;

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

	//	/**
	//	 * @return the newCounters
	//	 */
	//	public List<AFJMeasurementCounter> getNewCounters() {
	//		return newCounters;
	//	}
	//
	//	/**
	//	 * @param newCounters the newCounters to set
	//	 */
	//	public void setNewCounters(final List<AFJMeasurementCounter> newCounters) {
	//		this.newCounters = newCounters;
	//	}

	/**
	 * @return true if new type
	 */
	public Boolean isTypeNew() {
		return typeNew;
	}

	/**
	 * @param typeNew the typeNew to set
	 */
	public void setTypeNew(final Boolean typeNew) {
		this.typeNew = typeNew;
	}

	//	/**
	//	 * @return the tagName
	//	 */
	//	public String getTagName() {
	//		return tagName;
	//	}
	//
	//	/**
	//	 * @param tagName the tagName to set
	//	 */
	//	public void setTagName(String tagName) {
	//		this.tagName = tagName;
	//	}

	//	/**
	//	 * @return the tagToCountersMap
	//	 */
	//	public Map<String, List<AFJMeasurementCounter>> getTagToCountersMap() {
	//		return tagToCountersMap;
	//	}
	//
	//	/**
	//	 * @param tagToCountersMap the tagToCountersMap to set
	//	 */
	//	public void setTagToCountersMap(
	//			Map<String, List<AFJMeasurementCounter>> tagToCountersMap) {
	//		this.tagToCountersMap = tagToCountersMap;
	//	}

	/**
	 * @return the tags
	 */
	public List<AFJMeasurementTag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(final List<AFJMeasurementTag> tags) {
		this.tags = tags;
	}
	
	/**
	 * Add new tag
	 * @param tag
	 */
	public void addTag(final AFJMeasurementTag tag) {
	  this.tags.add(tag);
	}

	/**
	 * @return the tpName
	 */
	public String getTpName() {
		return tpName;
	}

	/**
	 * @param tpName the tpName to set
	 */
	public void setTpName(final String tpName) {
		this.tpName = tpName;
	}

	/**
	 * @return the tpVersion
	 */
	public String getTpVersion() {
		return tpVersion;
	}

	/**
	 * @param tpVersion the tpVersion to set
	 */
	public void setTpVersion(final String tpVersion) {
		this.tpVersion = tpVersion;
	}

	/**
	 * @return the ratio
	 */
	public String getRatio() {
		return ratio;
	}

	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(final String ratio) {
		this.ratio = ratio;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}
