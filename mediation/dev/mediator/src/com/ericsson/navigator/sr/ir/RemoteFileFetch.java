package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

public class RemoteFileFetch implements IrNode {

	private String rop = "";
	private String pluginDir = "";
	private String protocolType = "";
	private String Offset = "";
	private List<RemoteFileCounterSet> remoteFileCounterSets = new LinkedList<RemoteFileCounterSet>();


	private static final Logger logger = Logger.getLogger("System Registration");

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
		Status result = Status.Success;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		System.out.println(indent + "ROP = " + rop);
		System.out.println(indent + "PluginDir = " + pluginDir);
		System.out.println(indent + "ProtocolType = " + protocolType);
		if(!Offset.equals("") && !Offset.equals(null)){
			System.out.println(indent + "Offset = " + Offset);
		}
		final Iterator<RemoteFileCounterSet> remoteCounterSetIterator = remoteFileCounterSets.iterator();
		while (remoteCounterSetIterator.hasNext() && (Status.Success == result)){
			result = remoteCounterSetIterator.next().srHrWrite(systemName,position + 3);
		}
		System.out.println();
		return Status.Success;
	}

	@Override
	public Status srWrite(final Writer fileWriter, final int position, final String systemName,
			final String dtdPath) {
		Status result = Status.Success;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		try {
			
			final StringBuffer output = new StringBuffer();
			output.append(indent + "<RemoteFileFetch" +
			" pluginDir=\"" + pluginDir + "\"" +
			" ROP=\"" + rop + "\"" +
			" ProtocolType=\"" + protocolType);
			if(Offset.equals("") || Offset.equals(null)){
				output.append("\">\n");
			}else{
				output.append("\"" + 
						" Offset=\""+Offset+ "\">\n");
			}
			
			fileWriter.write(output.toString());
			final Iterator<RemoteFileCounterSet> remoteCounterSetIterator = remoteFileCounterSets.iterator();
			while (remoteCounterSetIterator.hasNext() && (Status.Success == result)){
				result = remoteCounterSetIterator.next().srWrite(fileWriter,position + 3,systemName,dtdPath);
			}
			fileWriter.write(indent + "</RemoteFileFetch>\n");

		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}

	@Override
	public boolean verify(final Ir ir) {
		boolean result = true;

		try {
			Long.parseLong(rop);
		} catch (final NumberFormatException e){
			logger.error("Wrong format of ROP: " + rop);
			result = false;
		}
		if(!Offset.equals("") && !Offset.equals(null)){
			try {
				Long.parseLong(Offset);
			} catch (final NumberFormatException e){
				logger.error("Wrong format of Offset: " + Offset);
				result = false;
			}
		}
		final Iterator<RemoteFileCounterSet> remoteCounterSetIterator = remoteFileCounterSets.iterator();
		while (result && remoteCounterSetIterator.hasNext()){
			result = remoteCounterSetIterator.next().verify(ir);
		}
		return result;
	}

	public void setRemoteFileCounterSets(
			final List<RemoteFileCounterSet> remoteFileCounterSets) {
		this.remoteFileCounterSets = remoteFileCounterSets;
	}

	public void setPluginDir(final String pluginDir) {
		this.pluginDir = pluginDir;
	}

	public void setRop(final String rop) {
		this.rop = rop;
	}

	public void setProtocolType(final String protocolType) {
		this.protocolType = protocolType;
	}
	
	public void setOffset(final String offset) {
		this.Offset = offset;
	}
	
	public String getOffset() {
		return Offset;
	}

	public List<RemoteFileCounterSet> getRemoteFileCounterSets() {
		return remoteFileCounterSets;
	}

	public String getPluginDir() {
		return pluginDir;
	}

	public String getRop() {
		return rop;
	}

	public String getProtocolType() {
		return protocolType;
	}
}
