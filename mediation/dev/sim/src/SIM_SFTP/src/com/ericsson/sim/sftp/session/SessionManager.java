package com.ericsson.sim.sftp.session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.maverick.ssh.LicenseManager;

public class SessionManager {
	
	private static SessionManager obj;
	private static int instance;
	protected HashMap<Integer, SftpSession> sessions;
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	private SessionManager(){
		sessions = new HashMap<Integer, SftpSession>();
		loadMaverickLicense();
		obj = this;
	}
	
	public SftpSession getSession(Node node) throws SIMException{
		SftpSession session = null;
		Integer ID = node.getID();
		if(sessions.containsKey(ID)){
			session = sessions.get(ID);
			log.debug(session.isConnected());
			if(session != null && session.isConnected()){
				return session;
			}
		}
		return session;
	}

	public void removeSession(Integer ID){
		sessions.remove(ID);
	}
	
	public void removeSession(Node node){
		sessions.remove(node.getID());
	}
	
	public synchronized SftpSession createSession(Node node) throws SIMException{
		try{
			SftpSession session = new SftpSession(SIMEnvironment.SSHKEYSPATH);
			session.initialize();
			String port = (String) node.getProperty("sftpPort");
			String IPAddress = (String) node.getProperty("IPAddress");
			String UserName = (String) node.getProperty("sftpUserName");
			session.connect(IPAddress, Integer.valueOf(port), UserName);
			
			if(!sessions.containsKey(IPAddress)){
				sessions.put(node.getID(), session);
			}
			
			return session;
		}catch(Exception e){
			throw new SIMException("Failed to create session", e);
		}
	}
	
	protected void loadMaverickLicense(){
		try{
			LicenseManager.addLicense(getFileContent(SIMEnvironment.MAVERICKLICENSE));
		}catch(Exception e){
			log.error("Unable to load license for Maverick. "+ e);
		}
	}
	
	private String getFileContent(final String filePath) throws IOException {
        final StringBuffer result = new StringBuffer();
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String tmpStr = null;
        do {
            tmpStr = bufferedReader.readLine();
            if (tmpStr != null) {
                result.append(tmpStr).append(System.getProperty("line.separator"));
            }
        } while (tmpStr != null);
        
        bufferedReader.close();
        
        return result.toString();
    }
	
	
	public synchronized static SessionManager getInstance(){
		if(instance == 0){
			instance = 1;
			return new SessionManager();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
}
