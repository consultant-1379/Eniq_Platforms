package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author qatulja
 *
 */
public class ResFileParserLinux extends ResFileParser {
	private double freeMem = 0;
	private double totalMem = 0;

    
    @Override
    public void parseDataFile(final String fin) throws ResourceParserException {
    	
    	file = fin;
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

                } else if (line.equalsIgnoreCase(
                        "[HW START]")) {

                    getMemSize(br);

                } else if (line.equalsIgnoreCase(
                        "[VMSTAT START]")) {
                    getCpuLoad(br);
                } else if (line.equalsIgnoreCase(
                        "[DISKSPACE START]")) {
                    getDiskUsage(br);
                } else if (line.equalsIgnoreCase("[UPTIME START]")) {
                    line = br.readLine();
                    uptime_ = parseUpTime(line);
                }
            }

            br.close();
        } catch (final Exception e) {   
            throw new ResourceParserException("Failed to parse file " + e);
        }
    }


    

    private void getCpuLoad(final BufferedReader br) throws IOException {
		String line;
		line = br.readLine();
		line = br.readLine();
		
		//Identify the header for cpu column
		final String[] headers = line.trim().split("\\s+");
		int idColNo = -1;
		for(int i=0;i<headers.length;i++){
			if("id".equalsIgnoreCase(headers[i].trim())){
				idColNo = i;
			}
		}

		line = br.readLine();
		line = br.readLine();
		
		final String[] s = line.trim().split("\\s+");
		if(idColNo != -1  && s.length > idColNo){
			cpu_.add(s[idColNo]);
		}else{
			logger.debug("Corrupt line in file, Will not be considered for CPU Load");
		}
	}

	private void getMemSize(final BufferedReader br) {
		String line;
		try{
		while (!(line = br.readLine()).startsWith("[HW")) {
		    if (line.startsWith("MemTotal:")) {
		        final String[] s = line.trim().split("\\s+");
				totalMem = Double.parseDouble(s[1].trim());
		    }
		    else if (line.startsWith("MemFree:")) {
		        final String[] s = line.trim().split("\\s+");
				freeMem = Double.parseDouble(s[1].trim());
		    }
		}
		}catch(Exception e){
			logger.error(file +" File is corrupt, Memmory cannot be parsed from file.",e);
		}
	}

    @Override
    public String getUsedMem() {
    	if( freeMem > 0 && totalMem > 0 ) {
			double usedMem_ = 100 - (100 * freeMem / totalMem);
			usedMem_ = convertDouble(usedMem_);
			return Double.toString(usedMem_);
    	}
    	return "";
    }


/*
 * This Method is only for Basic Testing
 * 
 
    public static void main(String[] args) {

        if (args.length == 1) {

            ResFileParserLinux fp_ = new ResFileParserLinux();
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
