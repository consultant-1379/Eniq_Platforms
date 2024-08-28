package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

public class SnmpCounterSet implements IrNode {

	private static final Logger logger = Logger.getLogger("System Registration");
	private String fileName = null;
	private String rop = ""; 
	
	@Override
	public Status deleteSystem(final String systemName) {
		return Status.Success;
	}

	@Override
	public Status getOperations(final String resource, final String ipAddress,
			final List<Properties> result) {
		return Status.Success;
	}

	@Override
	public Status srHrWrite(final String systemName, final int position) {
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		System.out.println(indent + "fileName = " + fileName);
		System.out.println(indent + "ROP = " + rop);
		System.out.println();
		return Status.Success;
	}

	@Override
	public Status srWrite(final Writer writer, final int position, final String systemName,
			final String dtdPath) {
		Status result = Status.Success;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		try {
			writer.write(indent + "<SnmpCounterSet fileName=\"" + fileName + "\"" + 
					" ROP=\"" + rop + "\"/>\n");
		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}

	@Override
	public boolean verify(final Ir ir) {
		try {
			Long.parseLong(rop);
		} catch (final NumberFormatException e){
			logger.error("Wrong format of ROP: " + rop);
			return false;
		}
		return true;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setRop(final String rop) {
		this.rop = rop;
	}

	public String getRop() {
		return rop;
	}

}
