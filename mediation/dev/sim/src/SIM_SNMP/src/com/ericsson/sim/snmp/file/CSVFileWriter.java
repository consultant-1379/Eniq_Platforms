package com.ericsson.sim.snmp.file;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;
import com.ericsson.sim.snmp.data.SNMPData;

public class CSVFileWriter {

	private Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SNMPproperties protocol;
	private SNMPData data;

	public CSVFileWriter(Node node, Protocol protocol, SNMPData data) {
		this.node = node;
		this.protocol = (SNMPproperties) protocol;
		this.data = data;
	}

	public void writeFile(){
		try{
			if(data.getData().size() > 0){
				//Make destination directory
				File destination = new File(protocol.getProperty("DestinationDir"));
				if(!destination.exists()){
					destination.mkdirs();
				}
				File outputFile = new File(destination, generateFileName());
				
				PrintWriter writer = new PrintWriter(outputFile);
				writer.println("ROP Time,Variable Counter ID," + data.getHeader());
				
				for(String line : data.getData()){
					writer.println(data.getTimeOfRop() + "," + line);
				}
				
				writer.close();
				log.info("CSV file written to: " + outputFile.getAbsolutePath());
			}else{
				log.info("CSV file not written as request resulted in zero data");
			}
			
		}catch(Exception e){
			log.error("Unable to create file. " , e);
		}
	}
	
	
	
	private String generateFileName() throws ParseException{
		//<TimeStamp>_<Granularity>_<Nodename>_<Measurement>.csv		
		StringBuffer filename = new StringBuffer();
		filename.append(node.getProperty("IPAddress"));

		String id = String.valueOf(node.getID());
		id.replace("-", "");
		filename.append("-"+id);
		
		Calendar cal = Calendar.getInstance();
		DateFormat RopTimedateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmm");
		cal.setTime(RopTimedateFormat.parse(data.getTimeOfRop()));

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmm");
		filename.append("_" + dateFormat.format(cal.getTime()));
		
		filename.append("_" + data.getROP());
		
		filename.append("_" + node.getName());
		
		filename.append("_" + data.getMeasurement()+".csv");
				
		return filename.toString();		
	
	}

	
}
