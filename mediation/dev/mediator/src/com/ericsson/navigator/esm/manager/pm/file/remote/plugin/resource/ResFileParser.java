package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.resource;

import java.io.BufferedReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.Logger;

public abstract class ResFileParser {
	protected static Logger logger = Logger.getLogger(ResFileParser.class.getName());  
    protected Date starttime = null;
    protected Date endtime = null;
    protected String uptime_ = "";
    protected String memsize = "";
    protected String hostname = "";
    protected List<String> cpu_ = new Vector<String>();
    protected List<String> mem_ = new Vector<String>();
    protected Map<String,String> disk_ = new HashMap<String, String>();
    protected SimpleDateFormat df_ = new SimpleDateFormat("yyyyMMddHHmm");
    protected int uptimeCount = 0;
    protected String file= "";

    //public ResFileParser() {}

    public abstract void parseDataFile(String fin) throws ResourceParserException;

    public Date getTimestamp() {
        return endtime;
    }

    public String getSystemUptime() {
        return uptime_;
    }

    public String getGranularity() {
    	if(endtime != null && starttime != null){
    		final long period = (endtime.getTime() - starttime.getTime())/1000;
    		return Long.toString(period);
    	}
    	return "";
    }

    public String getMaxCpu() {
        return getMax(cpu_, true);
    }

    public String getAvgCpu() {
        return getAverage(cpu_, true);
    }

    public abstract String getUsedMem();
    

    public Map<String,String> getFilesystems() {
        return disk_;
    }

    
    public StringBuffer getResultPrintout() {
		final StringBuffer resourceData = new StringBuffer();

		resourceData.append(" Timestamp     : " + getTimestamp());
		resourceData.append(" Period        : " + getGranularity());
		resourceData.append(" Uptime  (min) : " + getSystemUptime());
		resourceData.append(" CPU Max  (%)  : " + getMaxCpu());
		resourceData.append(" CPU Avg  (%)  : " + getAvgCpu());
		resourceData.append(" Mem Usage (%) : " + getUsedMem());

        final Iterator<?> it = disk_.keySet().iterator();
        resourceData.append(" Filesystems:");
        while (it.hasNext()) {
            final String key_ = (String) it.next();

            String newKey = key_.replaceAll("/", "_");
            if(newKey.trim().equals("_")){
            	newKey= newKey.concat("root");
            }

           final  String val_ = disk_.get(key_);
           resourceData.append("  " + newKey+ " : " + val_);

        }
        return resourceData;
    }

    protected String getMax(final List<String> v, final boolean invert) {

		double max = 0.0d;
        for (int i = 0; i < v.size(); i++) {

            final String val = v.get(i);
			double currVal = Double.parseDouble(val);
            if (invert) {
                currVal = 100 - currVal;
            }
            if (currVal > max) {
                max = currVal;
            }
        }
		max = convertDouble(max);
		return Double.toString(max);
    }

    protected String getMin(final List<String> v) {

		double min = Double.parseDouble(v.get(0));
        for (int i = 1; i < v.size(); i++) {

            final String val = v.get(i);
			final double currVal = Double.parseDouble(val);
            if (currVal < min) {
                min = currVal;
            }
        }
		min = convertDouble(min);
		return Double.toString(min);
    }

    protected String getAverage(final List<String> v, final boolean invert) {

		double acc = 0.0d;
		double averageVal = 0.0d;
        for (int i = 0; i < v.size(); i++) {

            final String val = v.get(i);
			double currVal = Double.parseDouble(val);
            if (invert) {
                currVal = 100 - currVal;
            }
            acc += currVal;
        }
		averageVal = convertDouble(acc / v.size());
		return Double.toString(averageVal);
    }
    
	protected String parseUpTime(String uptimeStr){
		uptimeCount = 0;	
		boolean onlyHrMinFlag = false;  // to Check if block has only Hour and Mins 
		//Remove the part before "up"
		uptimeStr = uptimeStr.substring(uptimeStr.indexOf("up") + 3);
		final String[] uptimeParts = uptimeStr.trim().split(",");
		//Loop only two times since only first two part are sufficient for uptime
		for(int i=0; i<2;i++){
			if(uptimeParts[i].contains("day")){
				//Helper call to get Days with Hour & Mins count if exist
				onlyHrMinFlag = parseUpTimeHelper(onlyHrMinFlag, uptimeParts, i);
			}else if(uptimeParts[i].contains("min")){
				final String[] minPart = uptimeParts[i].trim().split("\\s+");
				uptimeCount += Integer.parseInt(minPart[0]); //Mins count
			}else if(uptimeParts[i].contains("hr") || uptimeParts[i].contains("hour")){
				final String[] hrPart = uptimeParts[i].trim().split("\\s+");
				uptimeCount += Integer.parseInt(hrPart[0]) * 60; //Hour count
			}
			//Check if Block has only Hours and Mins
			if(uptimeParts[i].contains(":") && !onlyHrMinFlag){
				final String[] hrMinPart = uptimeParts[i].trim().split(":");
				uptimeCount += Integer.parseInt(hrMinPart[0]) * 60; //Hour count 
				uptimeCount += Integer.parseInt(hrMinPart[1]); //Mins count
				break;
			}
		}
		return Integer.toString(uptimeCount);
	}

	/** 
	 * Helper for parseUpTime
	 * @param onlyHrMinFlag
	 * @param uptimeParts
	 * @param i
	 * @return
	 */
	private boolean parseUpTimeHelper(boolean onlyHrMinFlag,
			final String[] uptimeParts, final int i) {
		final String[] dayPart = uptimeParts[i].trim().split("\\s+");
		uptimeCount += Integer.parseInt(dayPart[0]) * 1440; //Day count in Min
		//Check if Part has Hours & Mins in addition of Days
		if(dayPart.length>2 && dayPart[2].contains(":") ){
			final String[] hrMinPart = dayPart[2].trim().split(":");
			uptimeCount += Integer.parseInt(hrMinPart[0]) * 60; //Hr count in Min
			uptimeCount += Integer.parseInt(hrMinPart[1]); 
			onlyHrMinFlag = true;
		} else if(dayPart[1].contains(":")){
			final String[] hrMinPart = dayPart[1].trim().split(":");
			uptimeCount += Integer.parseInt(hrMinPart[0]) * 60; //Hr count in Min
			uptimeCount += Integer.parseInt(hrMinPart[1]); 
			onlyHrMinFlag = true;
		}
		return onlyHrMinFlag;
	}

	public double convertDouble(double value) {
		final DecimalFormat df = new DecimalFormat("#.###");
		value = Double.valueOf(df.format(value));
		return value;
	}
	
    protected void getDiskUsage(final BufferedReader br){
    	String line;
    	String newLine = "";
    	try{
    		line = br.readLine();
    		while (!(line = br.readLine()).startsWith("[DISK")) {
    		newLine += " "+line;
    		final String[] s = newLine.trim().split("\\s+");
    		   if(s.length == 6){
    			   disk_.put(s[5], s[4]);
    			   newLine = "";
    		   }else if (s.length > 6){
    			   logger.debug("Corrupt line in Resource Indicator File.");
    			   newLine = "";
    		   }
    		}
    	}catch(Exception e){
    		logger.error(file +" File is corrupt, Disk Usage cannot be parsed from file.",e);
    	}
    }

}
