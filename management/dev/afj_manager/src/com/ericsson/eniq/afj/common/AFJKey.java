/**
 * 
 */
package com.ericsson.eniq.afj.common;

/**
 * @author esunbal
 *
 */
public class AFJKey {

	private String name;
	
	private String dataId;
	
	private Integer nullable;
	
	private Integer uniqueValue;
	
	private Integer dataSize;
	
	private Integer dataScale;
	
	private String dataType;
	
	private String indexes;
	
	private Integer uniqueKey;
	
	private Integer includeSql;
	
	private String colType;
	
	private String univObject;
	
	private int joinable;
	
	private int isElement;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the nullable
	 */
	public Integer getNullable() {
		return nullable;
	}

	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(final Integer nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the uniqueValue
	 */
	public Integer getUniqueValue() {
		return uniqueValue;
	}

	/**
	 * @param uniqueValue the uniqueValue to set
	 */
	public void setUniqueValue(final Integer uniqueValue) {
		this.uniqueValue = uniqueValue;
	}

	/**
	 * @return the dataSize
	 */
	public Integer getDataSize() {
		return dataSize;
	}

	/**
	 * @param dataSize the dataSize to set
	 */
	public void setDataSize(final Integer dataSize) {
		this.dataSize = dataSize;
	}

	/**
	 * @return the dataScale
	 */
	public Integer getDataScale() {
		return dataScale;
	}

	/**
	 * @param dataScale the dataScale to set
	 */
	public void setDataScale(final Integer dataScale) {
		this.dataScale = dataScale;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the indexes
	 */
	public String getIndexes() {
		return indexes;
	}

	/**
	 * @param indexes the indexes to set
	 */
	public void setIndexes(final String indexes) {
		this.indexes = indexes;
	}

	/**
	 * @return the uniqueKey
	 */
	public Integer getUniqueKey() {
		return uniqueKey;
	}

	/**
	 * @param uniqueKey the uniqueKey to set
	 */
	public void setUniqueKey(final Integer uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	/**
	 * @return the includeSql
	 */
	public Integer getIncludeSql() {
		return includeSql;
	}

	/**
	 * @param includeSql the includeSql to set
	 */
	public void setIncludeSql(final Integer includeSql) {
		this.includeSql = includeSql;
	}

	/**
	 * @return the colType
	 */
	public String getColType() {
		return colType;
	}

	/**
	 * @param colType the colType to set
	 */
	public void setColType(final String colType) {
		this.colType = colType;
	}

	/**
	 * @return the univObject
	 */
	public String getUnivObject() {
		return univObject;
	}

	/**
	 * @param univObject the univObject to set
	 */
	public void setUnivObject(final String univObject) {
		this.univObject = univObject;
	}

	/**
	 * @return the joinable
	 */
	public int getJoinable() {
		return joinable;
	}

	/**
	 * @param joinable the joinable to set
	 */
	public void setJoinable(final int joinable) {
		this.joinable = joinable;
	}

	/**
	 * @return the isElement
	 */
	public int getIsElement() {
		return isElement;
	}

	/**
	 * @param isElement the isElement to set
	 */
	public void setIsElement(final int isElement) {
		this.isElement = isElement;
	}

	/**
	 * @return the dataId
	 */
	public String getDataId() {
		return dataId;
	}

	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId(final String dataId) {
		this.dataId = dataId;
	}	
	
}
