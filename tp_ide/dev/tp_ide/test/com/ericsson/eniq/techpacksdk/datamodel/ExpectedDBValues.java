package com.ericsson.eniq.techpacksdk.datamodel;

import java.util.Vector;

//import com

public class ExpectedDBValues {
	
	public ExpectedDBValues(){
		
	}
	
	
	
	Vector<String> getExpTPNameFromVersioning(){
		
		Vector<String> expVersioningTps = new Vector<String>();
		expVersioningTps.addElement("DC_E_CPP");
		expVersioningTps.addElement("DC_E_MGW");
		expVersioningTps.addElement("DC_E_TEST");
		//System.out.println("expVersion= "+expVersioningTps);
		return expVersioningTps;
	}

	
	Vector<String> getVerIdFromTPActivation(){
		
		Vector<String> expActiveTps = new Vector<String>();
		expActiveTps.addElement("DC_E_TEST:((1))");
		//System.out.println("expVersion= "+expActiveTps);
		return expActiveTps;
	}
	
	Vector<String> getInterfaceNameFromDataInterface(){
		
		Vector<String> expActiveTps = new Vector<String>();
		expActiveTps.addElement("INTF_DC_E_MGW");
		//System.out.println("expVersion= "+expActiveTps);
		return expActiveTps;
	}

}
