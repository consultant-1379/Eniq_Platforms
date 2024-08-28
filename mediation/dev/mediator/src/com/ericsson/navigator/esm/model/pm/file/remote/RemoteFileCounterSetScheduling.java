package com.ericsson.navigator.esm.model.pm.file.remote;

import java.util.List;

import com.ericsson.navigator.esm.model.pm.DefaultCounterSetScheduling;
import com.ericsson.navigator.esm.model.pm.RegexpCounterSetIdentification;

public class RemoteFileCounterSetScheduling extends DefaultCounterSetScheduling<RegexpCounterSetIdentification> {
	
	private final String pluginDirectory;
	private final int offset;
	private boolean navFirstRun = true;

	public RemoteFileCounterSetScheduling(final String fdn, 
			final int rop, final String pluginDir, final int offset, final List<RegexpCounterSetIdentification> identifications) {
		super(fdn, rop, identifications);
		this.pluginDirectory = pluginDir;
		this.offset = offset;
	}
	
	public String getPluginDirectory(){
		return pluginDirectory;
	}
	
	@Override
	public int getOffset() {
		//Override DefaultCounterSetScheduling to return offset for
		//SFTP Remote File Fetch
		return offset;
	}
	
	@Override
	public boolean equals(final Object obj) {
		final RemoteFileCounterSetScheduling c = (RemoteFileCounterSetScheduling)obj;
		return super.equals(obj) && c.getPluginDirectory().equals(getPluginDirectory()) && c.getOffset()==getOffset();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public boolean isNavFirstRun(){
		return navFirstRun;
	}
	
	public void setNavFirstRun(boolean state){
		navFirstRun = state;
	}
}
