package com.ericsson.sim.config.configNodes;

public class SIMCLI {

	public static void main(String[] args) {
		if(args[0].equals("import")){
			new NodeConfiguration(args[1]);
		}else if(args[0].equals("export")){
			new TopologyToCSV(args[1]);
		}

	}

}
