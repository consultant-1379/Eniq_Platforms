package com.ericsson.sim.model.parser;

public class util_prop_parser_test {
	public static void main(String[] args){
		util_prop_parser parser = new util_prop_parser();
		
		try {
			parser.loadFile("C:\\Users\\eandymu\\Documents\\SIM\\week\\config.xml");
			 {int count = parser.getContent("entry");
			 for (int i = 0; i < count; i++) {
				 System.out.print(parser.getAttribute("entry", i, "key", "maxFileTransfersPerRop" ) );
			 }} 
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
	}
}
