package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.unzipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.ParserException;


public class PmFileUnzipper implements CounterSetFileParser {

	public static final Logger logger = Logger.getLogger("PmFileUnzipper");
	private static final String classname = PmFileUnzipper.class.getName();
	
	@Override
	public File parseFile(String fdn, String filePath,
			CounterSetCallback callback, boolean doLookup)
			throws ParserException {
		
		File inputFile = new File(filePath);
		String outputFileName = inputFile.getAbsolutePath().replace(".gz", "");
		File outputFile = new File(outputFileName);
		
		GZIPInputStream gzipInputStream = null; 
		OutputStream out = null;
		try{
			gzipInputStream = new GZIPInputStream(new FileInputStream(inputFile)); 
			out = new FileOutputStream(outputFile);
	
			byte[] buf = new byte[102400]; //size can be changed according to programmer's need.
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) { 
				out.write(buf, 0, len);
			}
		
		}catch(Exception e){
			logger.error(classname+": Could not unzip input file "+inputFile.getName()+e);
			throw new ParserException("Could not unzip input file");
		}finally{
			try{
				out.close();
				gzipInputStream.close();
				inputFile.delete();
			}catch(Exception e){
				logger.error(classname+": Failed to close datastream");
			}
			
		}
		return outputFile;
	}

	@Override
	public String getDescription() {
		return "Mediator PM file unzipper";
	}

	@Override
	public String getContactInformation() {
		return "ENIQ product development team";
	}

	@Override
	public String getDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

}
