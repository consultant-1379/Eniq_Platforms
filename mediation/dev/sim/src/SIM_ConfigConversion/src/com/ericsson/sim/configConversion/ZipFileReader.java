package com.ericsson.sim.configConversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileReader {

	private ZipFile zipFile;
	
	public ZipFileReader(String zipPath) throws IOException{
		zipFile = new ZipFile(zipPath);
	}
	
	public InputStream getFileStream(String fileName) throws IOException{
		ZipEntry entry = zipFile.getEntry(fileName);
		InputStream stream = zipFile.getInputStream(entry);
			
		return stream;
	}
	
	public ArrayList<String> getSERNames(){
		ArrayList<String> sers = new ArrayList<String>();
		
		Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
		String fname;
		while (zipEntries.hasMoreElements()) {
		    fname = ((ZipEntry)zipEntries.nextElement()).getName();
		    if(fname.endsWith(".ser")){
		    	sers.add(fname);
		    }
		}
		System.out.println("returning sernames");
		return sers;
	}
	
}
