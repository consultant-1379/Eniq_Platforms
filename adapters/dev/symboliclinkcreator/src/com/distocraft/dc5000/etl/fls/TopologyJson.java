package com.distocraft.dc5000.etl.fls;

public class TopologyJson {
	private String fileName;
	private String dataType;
	private String fileLocation;
	private String fileCreationTimeInOss;
	private int id;
	public TopologyJson(String fileName, String dataType, String fileLocation, String fileCreationTimeInOss,
			int id) {
		super();
		this.fileName = fileName;
		this.dataType = dataType;
		this.fileLocation = fileLocation;
		this.fileCreationTimeInOss = fileCreationTimeInOss;
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public String getFileCreationTimeInOss() {
		return fileCreationTimeInOss;
	}
	public void setFileCreationTimeInOss(String fileCreationTimeInOss) {
		this.fileCreationTimeInOss = fileCreationTimeInOss;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	

}
