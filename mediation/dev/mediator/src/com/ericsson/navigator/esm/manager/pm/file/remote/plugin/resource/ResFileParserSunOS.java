package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ResFileParserSunOS extends ResFileParser {

	@Override
    public void parseDataFile(final String fin) throws ResourceParserException {
        String line = "";
        try {
            final BufferedReader br = new BufferedReader(new FileReader(fin));
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("[ENDTIME START]")) {
                    line = br.readLine();
                    endtime = df_.parse(line.trim());
                    line = br.readLine();
                } else if (line.equalsIgnoreCase("[STARTTIME START]")) {
                    line = br.readLine();
                    starttime = df_.parse(line.trim());
                    line = br.readLine();
                } else if (line.equalsIgnoreCase("[HW START]")) {
                    getMemSize(br);
                } else if (line.equalsIgnoreCase("[VMSTAT START]")) {
                    getCpuLoad(br);
                } else if (line.equalsIgnoreCase("[DISKSPACE START]")) {
                    getDiskUsage(br);
                } else if (line.equalsIgnoreCase("[UPTIME START]")) {
                    line = br.readLine();
                    uptime_ = parseUpTime(line);
                }
            }

            br.close();
        } catch (final Exception e) {
            throw new ResourceParserException("Failed to Parse " + e);
        }
    }

 	private void getCpuLoad(final BufferedReader br) throws IOException {
		String line;
		line = br.readLine();
		line = br.readLine();
		
		//Identify the header and memory column
		final String[] headers = line.trim().split("\\s+");
		int idColNo = -1;
		int freeColNo = -1;
		for(int i=0;i<headers.length;i++){
			if("id".equalsIgnoreCase(headers[i].trim())){
				idColNo = i;
			}
			if("free".equalsIgnoreCase(headers[i].trim())){
				freeColNo = i;
			}
		}
		
		line = br.readLine();
		line = br.readLine();
		final String[] s = line.trim().split("\\s+");
		if(idColNo != -1  && s.length > idColNo){
			cpu_.add(s[idColNo]);
		}else{
			logger.debug("Corrupt line in file, Will not be considered for CPU Load.");
		}
		if(freeColNo != -1  && s.length > freeColNo){
			mem_.add(s[freeColNo]);
		}else{
			logger.debug("Corrupt line in file, Will not be considered for parsing Memory.");
		}
	}

	private void getMemSize(final BufferedReader br) throws IOException {
		String line;
		try{
		while (!(line = br.readLine()).startsWith("[HW")) {
		    if (line.startsWith("Memory size")) {
		       final String[] s = line.trim().split(":");
		        memsize = s[1].substring(0, s[1].indexOf("Megabytes")).trim();
		    } else if (line.startsWith("SunOS")) {
		        hostname = line;
		    }
		}
		}catch(Exception e){
			logger.error(file +" File is corrupt, Memmory cannot be parsed from file.",e);
		}
	}

    @Override
    public String getUsedMem() {

    if( memsize.length() > 0 ) {
			final double minFreeMem = Double.parseDouble(getMin(mem_)) / 1024;
			final double installedMem = Double.parseDouble(memsize);
			final double usedMem = installedMem - minFreeMem;
			double memUsage = (100 * usedMem / installedMem);
			memUsage = convertDouble(memUsage);
			return Double.toString(memUsage);
        }
        return "";
    }

/*
* This Method is only for Basic Testing
*
    public static void main(final String[] args) {

        if (args.length == 1) {
            final ResFileParserSunOS fp_ = new ResFileParserSunOS();
            try {
                fp_.parseDataFile(args[0]);
            } catch (Exception e) {
                System.err.println("File parser exception: " + e.getMessage());
            }
//            fp_.getResultPrintout();    
        StringBuffer sb = fp_.getResultPrintout();
        System.out.println(sb.toString());
        } else {
            System.err.println("Argument: <datafile>");
        }
    }
*/
}
