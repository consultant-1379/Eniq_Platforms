package com.ericsson.eniq.enminterworking.automaticNAT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import com.ericsson.eniq.flssymlink.fls.Main;

public class RetainExistingNodes {
	
	 private static Logger log;

	private static String inputFile = null;

	public static void main(String[] args) {

		inputFile = args[0];
		assignNodesAgainstPolicies();

	}

	private static void assignNodesAgainstPolicies() {
		// Read all the existing nodes in the server and
		// add them to the BlockingQueue
		log = Logger.getLogger("symboliclinkcreator.retention");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(inputFile)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] input = line.split("|");
				String nodeFDN = input[1];
				String nodeType = input[2];

				// Adding it to the Blocking Queue
				Main.getInstance().assignNodesQueue.add(new AssignNodesQueueHandler(nodeType, nodeFDN));
			}
		} catch (FileNotFoundException e) {
			log.warning("Exception at assignNodesAgainstPolicies method "+e.getMessage());
		} catch (IOException e) {
			log.warning("Exception at assignNodesAgainstPolicies method "+e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				log.warning("Exception at assignNodesAgainstPolicies method io connection close"+e.getMessage());
			}
		}
	}

}
