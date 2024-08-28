package com.ericsson.eniq.afj.common;

public class AFJTechPack {

	private String techPackName;
	
	private boolean momFilePresent;

	private String fileName;

	private String message;

	private Integer maxCounters;

	private Integer maxMeasTypes;

	private String schemaName;
	
	private String contextPath;
	
	private boolean isNamespaceAware;

	/**
	 * @return the techPackName
	 */
	public String getTechPackName() {
		return techPackName;
	}

	/**
	 * @param techPackName the techPackName to set
	 */
	public void setTechPackName(final String techPackName) {
		this.techPackName = techPackName;
	}

	/**
	 * @return the momFilePresent
	 */
	public boolean isMomFilePresent() {
		return momFilePresent;
	}

	/**
	 * @param momFilePresent the momFilePresent to set
	 */
	public void setMomFilePresent(final boolean momFilePresent) {
		this.momFilePresent = momFilePresent;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}


	/**
	 * @return the maxCounters
	 */
	public Integer getMaxCounters() {
		return maxCounters;
	}


	/**
	 * @param maxCounters the maxCounters to set
	 */
	public void setMaxCounters(final Integer maxCounters) {
		this.maxCounters = maxCounters;
	}


	/**
	 * @return the maxMeasTypes
	 */
	public Integer getMaxMeasTypes() {
		return maxMeasTypes;
	}


	/**
	 * @param maxMeasTypes the maxMeasTypes to set
	 */
	public void setMaxMeasTypes(final Integer maxMeasTypes) {
		this.maxMeasTypes = maxMeasTypes;
	}

	/**
	 * @return
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 */
	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * @param contextPath
	 */
	public void setContextPath(final String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @return
	 */
	public boolean isNamespaceAware() {
		return isNamespaceAware;
	}

	/**
	 * @param isNamespaceAware
	 */
	public void setNamespaceAware(final boolean isNamespaceAware) {
		this.isNamespaceAware = isNamespaceAware;
	}

}
