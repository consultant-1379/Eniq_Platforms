package com.ericsson.navigator.sr.ir;

import java.io.Writer;
import java.util.List;
import java.util.Properties;

import com.ericsson.navigator.sr.util.Status;

public interface IrNode {
	boolean verify(Ir ir);
	Status srHrWrite(String systemName, int position);
	Status srWrite(Writer file, int position, String systemName, String dtdPath);
	Status deleteSystem(String systemName);
	Status getOperations(String resource, String ipAddress, List<Properties> result);
}




	
