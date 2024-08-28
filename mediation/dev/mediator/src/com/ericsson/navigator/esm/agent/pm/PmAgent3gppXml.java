package com.ericsson.navigator.esm.agent.pm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger; 

import com.ericsson.navigator.esm.agent.rmi.AgentInterface;
import com.ericsson.navigator.esm.model.pm.Counter;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.Counter.CounterType;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class PmAgent3gppXml extends AbstractPmAgent {

	private static final String classname = PmAgent3gppXml.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	
	private static final String DIRECTORY = System.getProperty("PMIRPOUTPUT", "/nav/var/esm/pm/output/3gpp");
	private static final String FILEPATH = DIRECTORY+File.separator;
	private static final String XMLSUFFIX = ".xml";
	private static final String PENDINGSUFFIX = ".pending";

	static final DateFormat formatterToXML = new SimpleDateFormat("yyyyMMddHHmmss");

	public PmAgent3gppXml(final AgentInterface agentInterface) {
		super(agentInterface);
	}
	
	public void process(final CounterSet counterSet) {
		final long startTime = System.currentTimeMillis();
		final File file = getUniqueFile(counterSet);

		process(counterSet, file);
		
		if (logger.isDebugEnabled()) {
			logger.debug(classname+".writeToFile; Created counterset file for "+
					counterSet.getManagedObjectInstance()+
					". Time "+(System.currentTimeMillis()-startTime));				
		}
	}
	
	private File getUniqueFile(final CounterSet counterSet) {
		String moid = counterSet.getMoid();
		final Date endTime = counterSet.getEndTime();

		if (moid == null || endTime == null) {
			return null;
		}
		if(moid.contains("/")){ // Replace "/" in the moid with "_" - TR HM47798
			moid = moid.replaceAll("/","_");
		}
		String fileName = null;
		for (int i=0; i<100; i++) { //Iterate max 100 times to find unique name
			fileName = FILEPATH + "PM_" + moid + "_" + 
				formatterToXML.format(endTime) + "_" + 
				String.valueOf(System.currentTimeMillis());
			final File file = new File(fileName + XMLSUFFIX); //NOPMD
			if (! file.exists()) {
				return file;
			}
		}
		return null;
	}

	private void process(final CounterSet counterSet, final File file) {
		if (counterSet == null || file == null) {
			return;
		}
		
		final File pendingFile = new File(file.getAbsolutePath()+PENDINGSUFFIX);

		PrintWriter writer = null; 
		List<BigDecimal> counterValues = null;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(pendingFile)));

			final Date endTime = counterSet.getEndTime();
	
			writer.println("<?xml version=\"1.0\"?>");
			writer.println("<?xml-stylesheet type=\"text/xsl\" href=\"MeasDataCollection.xsl\" ?>");
			writer.println("<!DOCTYPE MeasDataCollection SYSTEM \"MeasDataCollection.dtd\" >");
			writer.println("<mdc xmlns:HTML=\"http://www.w3.org/TR/REC-xml\">");
			writer.println("<mfh>");
			writer.println("<ffv>1</ffv>");
			writer.println("<sn>System=ESMPM</sn>");
			writer.println("<st></st>");
			writer.println("<vn>Ericsson</vn>");
			writer.println("<cbt>" + formatterToXML.format(new Date(System.currentTimeMillis())) + "</cbt>");
			writer.println("</mfh>");
			writer.println("<md>");
			writer.println("<neid>");
			writer.println("<neun>User friendly name not defined</neun>");
			writer.println("<nedn>"+counterSet.getManagedObjectInstance()+"</nedn>");
			writer.println("</neid>");
			writer.println("<mi>");
	
			//TODO DataPoller can include many MO instances in the same file
			//A loop starts here in the original
			writer.println("<mts>" + formatterToXML.format(endTime) + "</mts>");
			writer.println("<gp>" + counterSet.getGranularityPeriod() + "</gp>");
			
			counterValues = new ArrayList<BigDecimal>(); // to guarantee order
			for (Entry<String, Counter> counter: counterSet.getCounters().entrySet()) {
				if((counter.getValue().getType() == CounterType.KPI) || (counter.getValue().getType() == CounterType.INDEX)){
					//Skipping, Navigator KPIs and Indexes will not be written in 3GPP
					continue;
				}
				writer.println("<mt>" + counter.getKey() + "</mt>");
				counterValues.add(counter.getValue().getValue());
			}
			
			writer.println("<mv>");
			writer.println("<moid>" + counterSet.getMoid() + "</moid>");
			
			for (BigDecimal counter: counterValues) {
				writer.println("<r>" + (counter == null ? "NULL" : counter) + "</r>");
			}
			
			writer.println("</mv>");
			writer.println("</mi>");
			writer.println("</md>");
			writer.println("<mff><ts>0</ts></mff>");
			writer.println("</mdc>");
		
		} catch (IOException e) {
			logger.error(classname+".writeToFile; Cannot create writer for file "+pendingFile.getName(), e);
			return;
		}finally{
			counterValues = null;
			writer.close();
		}
		//TODO Check why original does some normalized stuff...
		if (!(pendingFile.renameTo(file))) {
			logger.warn(classname+".writeToFile; Output file "+pendingFile.getName()+"could not be renamed");
			pendingFile.delete();
		}	
	}

	@Override
	protected String getAgentId() {
		return classname;
	}

	@Override
	public String getComponentName() {
		return PmAgent3gppXml.class.getSimpleName();
	}
}
