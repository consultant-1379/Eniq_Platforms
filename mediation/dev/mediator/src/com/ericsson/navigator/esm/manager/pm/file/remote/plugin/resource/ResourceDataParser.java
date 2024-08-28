package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.resource;

import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.ParserException;

import java.io.File;
import java.util.*;

public class ResourceDataParser implements CounterSetFileParser {

    private CounterSetCallback parseCallback;
    private String moid;
    private Date endTime;
    private final Map<String, String> counters = new HashMap<String, String>();
    private ResFileParser fp = null;

//    public ResourceDataParser() {
//    }

    public File parseFile(final String fdn, final String filePath, final CounterSetCallback callback, boolean doLookup) throws ParserException {
        try {
            parseCallback = callback;
            parseCallback.debug(" - Parsing " + filePath + " for FDN:" + fdn);
            parse(filePath, fdn);
        } catch (final Exception e) {
            throw new ParserException("Error parsing Counter Set file: ", e);
        }
		return null;
    }

    private void parse(final String filePath, final String fdn) {

        try {
            if( filePath.contains("SunOS")) {
                parseCallback.debug(" - Using SunOS parser");
                fp = new ResFileParserSunOS();
            }
            else if( filePath.contains("Linux")) {
                parseCallback.debug(" - Using Linux parser");
                fp = new ResFileParserLinux();
            }
            else {
                parseCallback.error(" - Unknown system type in file [" + filePath + "] - Parser aborted");
                return;
            }

            fp.parseDataFile(filePath);

            endTime = fp.getTimestamp();
            moid = fdn.concat(",Component=ResourceIndicator");
            
            counters.put("CpuLoad", fp.getAvgCpu());
            counters.put("MaxCpuLoad", fp.getMaxCpu());
            counters.put("MemUsage", fp.getUsedMem());
            counters.put("Uptime", fp.getSystemUptime());
            parseCallback.pushCounterSet(moid, endTime, counters);
            parseCallback.debug("Pushing counterset for: " + fdn + " with moid: " + moid);
            counters.clear();
            final Iterator<?> it = fp.getFilesystems().keySet().iterator();
            while (it.hasNext()) {
                final String key_ = (String) it.next();
                moid = fdn.concat(",FileSystem="+key_);
                //Added to replace "/" 
                String newKey_ = key_.replaceAll("/", "_");
                if(newKey_.trim().equals("_")){
                	newKey_= newKey_.concat("root");
                }

                String val_ = fp.getFilesystems().get(key_);
                val_ = val_.substring(0, val_.lastIndexOf("%"));
                counters.put("DiskUsage"+newKey_, val_);
                parseCallback.pushCounterSet(moid, endTime, counters);
                parseCallback.debug("Pushing counterset for: " + fdn + " with moid: " + moid);
                counters.clear();
            }
        } catch(ResourceParserException e ) {
            parseCallback.error("File parser exception for " + fdn, e);
            return;
        }
    }

    public String getDescription() {
        return "Parser for resource measurements data.";
    }

    public String getContactInformation() {
        return "Navigator, ESM product development team";
    }

	@Override
	public String getDirectory() {
		// TODO Auto-generated method stub
		return null;
	}
}

