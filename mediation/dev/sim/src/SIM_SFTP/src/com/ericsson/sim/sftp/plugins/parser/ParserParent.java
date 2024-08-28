package com.ericsson.sim.sftp.plugins.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.sftp.serialization.appended.FileProperties;

public class ParserParent {

	public String firstDataLine = null;
	public String header;
	public ArrayList<String> data;
	
	public void parseFile(Node node, SFTPproperties properties, File file) throws SIMException{};
	
	
	public boolean isValidFile(File file){
		if(file.length() > 0){
			return true;
		}
		return false;
	}

public ArrayList<String> findNewData(BufferedReader br, FileProperties props) throws SIMException{
		ArrayList<String> data = new ArrayList<String>();
		try{
			boolean newdata = false;
			
			String lastline = props.getLastParsedLine();
		
			if(lastline.equals("")){ //if no last line has been set then all data is new
				newdata = true;  
			}  
			
			for(String line; (line = br.readLine()) != null; ) { 
				
				if(!line.trim().equals("")){ //only process line with data on it
			        if(newdata){ //add to arraylist if data is new  
			        	data.add(line);
			        }else{
			        	if(line.equals(lastline)){ //test if the current line matches the last line
			        		newdata = true; //anything after this in the file is new data
			        	}
			        } 
				}
		    }
		
		}catch(Exception e){
			throw new SIMException(e);
		}
		

		
		return data;
	}
	
	public void processData(File dataFile, SFTPproperties properties, FileProperties props) throws SIMException{
		try{
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			data = new ArrayList<String>();
			String dateFormat = (String) properties.getProperty("dateFormatInFileName");
			String delimitor = (String) properties.getProperty("delimitor");

			String line = br.readLine();
			//Test if the first line is a header. If not then its the first line of data
			if(isHeader(line, dateFormat, delimitor)){
				header = line;
				line = br.readLine();
				firstDataLine = line;
			}else{
				firstDataLine = line;
			}
			
			//Get the last parsed line
			boolean newdata = false;
			String lastline = props.getLastParsedLine();
			if(lastline.equals("")){ //if no last line has been set then all data is new
				newdata = true;
			}
			
			//Process the data to find the new data
			while (line != null) {
				if (!line.trim().equals("")) {
					if(newdata){ //add to arraylist if data is new
			        	data.add(line);
			        }else{
			        	if(line.equals(lastline)){ //test if the current line matches the last line
			        		newdata = true; //anything after this in the file is new data
			        	}
			        }
				}
				line = br.readLine();
			}
			br.close();
			
		}catch(Exception e){
			throw new SIMException("Error while processing data. ", e);
		}
	}
	
	public boolean isHeader(String dataline, String dateFormat, String delimitor){
		try{
			Calendar calender = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			calender.setTime(format.parse(dataline.split(delimitor)[0]));
		}catch(Exception e){
			return true;
		}
		
		return false;
	}

	public int getMaximumColumnCount(String delimitor){
		int columnCount = 0;
		if(header != null){
			return header.split(delimitor).length;
		}
		
		for(String line : data){
			int count = line.split(delimitor).length;
			if(count > columnCount){
				columnCount = count;
			}
		}
		
		return columnCount;	
	}
	

	
	public void writeNewFile(File newFile, String delimitor, int columnCount) throws SIMException{
		try {
			
			File dir = newFile.getParentFile();
			if(!dir.exists()){
				dir.mkdirs();
			}
			newFile.createNewFile();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
			
			if(header != null){
				out.write(header+"\n");
			}
			for(String line : data){
				if(line.split(delimitor).length == columnCount){
					out.write(line+"\n");
				}
			}
			
			out.flush();
			out.close();
			
		} catch (IOException e) {
			throw new SIMException("Unable to create new file: ", e);
		}
	}
	
	
	public void moveFile(SFTPproperties props, File sourceFile) throws SIMException{
		try{
			Path sourcePath = sourceFile.toPath();
			
			String dest = (String) props.getProperty("DestinationDir");
			File destDir = new File(dest);
			if(!destDir.exists()){
				destDir.mkdirs();
			}
			Path destPath = Paths.get(destDir.toPath()+"/"+sourceFile.getName());
			
			if(sourcePath.toFile().exists()){
				if(!destPath.toFile().exists()){
					Files.move(sourcePath, destPath, REPLACE_EXISTING);
				}
			}
			
			sourcePath.toFile().delete();
		}catch(Exception e){
			throw new SIMException("Unable to move file " + sourceFile.getName() + " to destination");
		}
		
	}
	
	public void moveFile(String destination, File sourceFile) throws SIMException{
		try{
			Path sourcePath = sourceFile.toPath();
			
			File destDir = new File(destination);
			if(!destDir.exists()){
				destDir.mkdirs();
			}
			Path destPath = Paths.get(destDir.toPath()+"/"+sourceFile.getName());
			
			if(sourcePath.toFile().exists()){
				if(!destPath.toFile().exists()){
					Files.move(sourcePath, destPath, REPLACE_EXISTING);
				}
			}
			
			sourcePath.toFile().delete();
		}catch(Exception e){
			throw new SIMException("Unable to move file " + sourceFile.getName() + " to destination");
		}
		
	}

	
}
