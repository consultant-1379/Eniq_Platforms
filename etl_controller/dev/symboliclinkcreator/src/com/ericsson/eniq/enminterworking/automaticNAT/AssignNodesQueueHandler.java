package com.ericsson.eniq.enminterworking.automaticNAT;

public class AssignNodesQueueHandler implements Runnable{

	private String node_type = null;
	private String node_fdn = null;
	
	
	public  AssignNodesQueueHandler(String node_type , String node_fdn) {
		super();
		this.node_type = node_type;
		this.node_fdn = node_fdn;
	}
	
	@Override
	public void run() {
		
		//Call AssignNodes constructor
		new AssignNodes(node_type, node_fdn);
	}

}
