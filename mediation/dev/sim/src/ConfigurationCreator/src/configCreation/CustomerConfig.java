package configCreation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.protocol.Protocol;

public class CustomerConfig {
	
	public ArrayList<Integer> customerconfig;
	
	public CustomerConfig(){
		customerconfig = new ArrayList<Integer>();
	}

	public void loadFile(String path){
		try{
			FileInputStream fstream = new FileInputStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
			String strLine;
			while ((strLine = br.readLine()) != null) {

				int hash = strLine.trim().hashCode();

				if (hash < 0) {
					hash = hash * -1;
				}

				customerconfig.add(hash);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removeProtocols(){
		ProtocolPool pp = ProtocolPool.getInstance();
		Set<Integer> protocolKeys = new HashSet<Integer>();
		protocolKeys.addAll(pp.getProtocols().keySet());
		
		for (Integer key : protocolKeys) {
		    if(!customerconfig.contains(key)){
		    	pp.removeProtocol(key);
		    }
		}
	}
	
}
