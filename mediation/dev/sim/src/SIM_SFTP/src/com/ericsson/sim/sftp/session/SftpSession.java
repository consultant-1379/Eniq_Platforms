package com.ericsson.sim.sftp.session;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.exception.SIMException;
import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpFileAttributes;
import com.maverick.sftp.SftpStatusException;
import com.maverick.sftp.TransferCancelledException;
import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.HostKeyVerification;
import com.maverick.ssh.PublicKeyAuthentication;
import com.maverick.ssh.SshAuthentication;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshConnector;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshKeyPair;
import com.maverick.ssh.components.SshPublicKey;
import com.sshtools.net.SocketTransport;
import com.sshtools.publickey.InvalidPassphraseException;
import com.sshtools.publickey.SshPrivateKeyFile;
import com.sshtools.publickey.SshPrivateKeyFileFactory;
import com.sshtools.sftp.DirectoryOperation;
import com.sshtools.sftp.SftpClient;


public class SftpSession {

	private String SshKeyPath;
	protected SshConnector connector = null;
	protected SshClient client = null;
	protected SftpClient sftp = null;
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	public SftpSession(final String SshKeyPath){
		this.SshKeyPath = SshKeyPath;
	}

	public void initialize() throws SIMException {
		try {
			connector = SshConnector.getInstance();
			connector.setKnownHosts(new HostKeyVerification() {
				@Override
				public boolean verifyHost(final String hostname, final SshPublicKey key)
						throws SshException {
					return true;
				}
			});
		} catch (Exception e) {
			throw new SIMException("SSH initialization error occured", e);
		}
	}

	public void connect(final String hostname, final int port, final String user) throws SIMException {
		try {
			client = connector.connect(new SocketTransport(hostname, port), user);
			setKeys();
			sftp = new SftpClient(client);
			sftp.setTransferMode(SftpClient.MODE_BINARY);
			sftp.setRegularExpressionSyntax(SftpClient.GlobSyntax);
		} catch (ConnectException e){
			throw new SIMException("Failed to connect", e);
		} catch (IOException e){
			throw new SIMException("IO error occured", e);
		} catch (SshException e) {
			throw new SIMException("SSH connection error occured", e);
		} catch (SftpStatusException e) {
			throw new SIMException(SftpStatusException.getStatusText(e.getStatus()), e);
		} catch (ChannelOpenException e) {
			throw new SIMException("SSH channel error", e);
		} catch (InvalidPassphraseException e) {
			throw new SIMException("SSH channel passphrase error", e);
		}
	}

	private void setKeys() throws FileNotFoundException, IOException,
			InvalidPassphraseException, SshException, SIMException {
		final PublicKeyAuthentication pk = new PublicKeyAuthentication();
		final SshKeyPair pair = readKeyPairFromFile();
		pk.setPrivateKey(pair.getPrivateKey());
		pk.setPublicKey(pair.getPublicKey());
		if(client.authenticate(pk) != SshAuthentication.COMPLETE
				&& client.isConnected() && !client.isAuthenticated()){
			throw new SIMException("SSH failed to authenticate.");
		}
	}
	
	private SshKeyPair readKeyPairFromFile() throws FileNotFoundException, IOException, InvalidPassphraseException {
		final SshPrivateKeyFile pkfile = SshPrivateKeyFileFactory.parse(
				new FileInputStream(SshKeyPath));
		return pkfile.toKeyPair("");
	}

	public void cd(final String dir) throws SIMException {
		try {
			sftp.cd(dir);
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP status error changing directory", e);
		} catch (SshException e) {
			throw new SIMException("SSH error changing directory", e);
		}
	}

	public void disconnect() throws SIMException {
		try {
			if(sftp != null){
				sftp.quit();
			}
		} catch (SshException e) {
			throw new SIMException("SSH error disconnecting SFTP session", e);
		}
		if(client != null){
			client.disconnect();
		}
	}

	public void get(final String remoteFileName, final String localFileName)
			throws SIMException, SshException {
		try {
			sftp.get(remoteFileName, localFileName, true);
		} catch (FileNotFoundException e) {
			throw new SIMException("SFTP file not found error", e);
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP transfer status error", e);
		} catch (TransferCancelledException e) {
			throw new SIMException("SFTP transfer cancelled error", e);
		}
	}
	
	public SftpFile[] getFiles(String path) throws SIMException{
		try {
			return sftp.getFiles(path);
		} catch (FileNotFoundException e) {
			throw new SIMException("SFTP file not found error", e);
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP transfer status error", e);
		} catch (SshException e) {
			throw new SIMException("SFTP SSH error", e);
		} catch (TransferCancelledException e) {
			throw new SIMException("SFTP transfer cancelled error", e);
		}
	}

	public void lcd(final String dir) throws SIMException {
		try {
			sftp.lcd(dir);
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP error changing local directory", e);
		}
	}

	public SftpFile[] ls() throws SIMException {
		try {
			return sftp.ls();
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP status error listing directory", e);
		} catch (SshException e) {
			throw new SIMException("SSH error listing directory", e);
		}
	}
	
	public SftpFile[] ls(String path) throws SIMException {
		try {
			return sftp.ls(path);
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP status error listing directory", e);
		} catch (SshException e) {
			throw new SIMException("SSH error listing directory", e);
		}
	} 
	
	
	public Date getLastModifiedTime(final String remoteFileName) throws SIMException{
		Date creationDate = null;
		try {
			SftpFileAttributes fileAttributes = sftp.stat(remoteFileName);
			creationDate = fileAttributes.getModifiedDateTime();
			
		}  catch (SftpStatusException e) {
			throw new SIMException(e);
		} catch (SshException e) {
			throw new SIMException(e);
		} 
		
		return creationDate;
	}
	
	public DirectoryOperation copyRemoteDirectory(String remoteDir, String localDir, boolean recurse, boolean sync, boolean commit) throws SIMException{
		try {
			return sftp.copyRemoteDirectory(remoteDir, localDir, recurse, sync, commit, null);
		} catch (FileNotFoundException e) {
			throw new SIMException("Unable to find files", e);
		} catch (SftpStatusException e) {
			throw new SIMException("SFTP status error listing directory", e);
		} catch (SshException e) {
			throw new SIMException("SSH error listing directory", e);
		} catch (TransferCancelledException e) {
			throw new SIMException("Transfer cancellation error", e);
		}
	}
	
	
	public boolean isConnected(){
		log.debug(sftp.isClosed());
		log.debug(client.isConnected());
		return client.isConnected();
	}
	
}
