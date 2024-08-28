package configCreation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class SNMPDisableFile {

	public BufferedWriter output;
	
	public SNMPDisableFile(String outputLocation) throws IOException{
		File file = new File(outputLocation + "/conf/DisabledSNMPCounters_template.xml");
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		output = new BufferedWriter(new FileWriter(file));
	}
	
	public void writeLine(String line) throws IOException{
		output.write(line+"\n");
	}
	
	public void close() throws IOException{
		output.flush();
		output.close();
	}
	
}
