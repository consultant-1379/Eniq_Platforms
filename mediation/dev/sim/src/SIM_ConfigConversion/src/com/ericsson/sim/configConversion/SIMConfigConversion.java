package com.ericsson.sim.configConversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ericsson.sim.model.parser.XmlParser;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;

public class SIMConfigConversion {

	private String zipPath = "/eniq/data/pmdata_sim/";
	private String confPath = "/eniq/sw/conf/sim/";
	
	private void convert(){
		try{
			System.out.println("Starting the conversion process");
			ZipFileReader zfr = new ZipFileReader(zipPath+"/SIM_Backup.zip");
			InputStream stream = zfr.getFileStream("backup/SystemTopology.xml");
			
			ProtocolPool.getInstance().loadPersistedFile(confPath + "protocols.simc");

			ProtocolPool.getInstance().loadPersistedFile(confPath + "procusProtocols.simc");

			NodePool.getInstance().loadPersistedFile(confPath + "topology.simc");
			
			XmlParser parser = new XmlParser();
			parser.loadFile(stream);
			
			ConversionProcess cp = new ConversionProcess();
			cp.convert(parser, zfr);
			System.out.println("Converting sers");
			SERConversionProcess sers = new SERConversionProcess();
			sers.convert(zfr);
			System.out.println("Converting sers");
			TopologyCSVWriter csv = new TopologyCSVWriter();
			csv.write();
			
		
		}catch(Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}	
		
	}
	
	public void extractMibs(){
		try{
			byte[] buffer = new byte[1024];
			
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath+"/SIM_Backup.zip"));
			ZipEntry ze = zis.getNextEntry();
			
			while(ze!=null){
	    	   String filePath = ze.getName();
	    	   if(filePath.contains("backup/mibs/")){
	    		   
	    		   String filename = filePath.replace("backup/mibs/", "");
	    		   
		           File newFile = new File(confPath + File.separator + "mibs" + File.separator +filename);
		           newFile.getParentFile().mkdirs();
		              
		           FileOutputStream fos = new FileOutputStream(newFile);             
	
		           int len;
		           while ((len = zis.read(buffer)) > 0) {
		        	   fos.write(buffer, 0, len);
		           }
		        		
		           fos.close();   
		           ze = zis.getNextEntry();
	    	   }
	    	}
	    	
	        zis.closeEntry();
	    	zis.close();
		
		
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}


	public static void main(String[] args) {
		SIMConfigConversion scc = new SIMConfigConversion();
		scc.convert();
	}

}
