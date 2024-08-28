package com.ericsson.navigator.esm.model.pm.file.remote.sftp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.util.Date;

import com.ericsson.navigator.esm.model.pm.file.remote.FileTransferException;
import com.ericsson.navigator.esm.model.pm.file.remote.FileTransferProtocolProvider;
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
import com.sshtools.sftp.SftpClient;

public class SFTPProvider implements FileTransferProtocolProvider { 
	
	private SshConnector connector = null;
	private SshClient client = null;
	private final String privateKeyFilePath;
	private SftpClient sftp = null;

	
	public SFTPProvider(final String privateKeyFilePath){
		this.privateKeyFilePath = privateKeyFilePath;
 	}

	@Override
	public void initialize() throws FileTransferException {
		try {
			connector = SshConnector.getInstance();
			connector.setKnownHosts(new HostKeyVerification() {
				@Override
				public boolean verifyHost(final String hostname, final SshPublicKey key)
						throws SshException {
					return true;
				}
			});
		} catch (SshException e) {
			throw new FileTransferException("SSH initialization error occured", e);
		}
	}

	@Override
	public void connect(final String hostname, final int port, final String user) throws FileTransferException {
		try {
			client = connector.connect(new SocketTransport(hostname, port), user);
			setKeys();
			sftp = new SftpClient(client);
			sftp.setTransferMode(SftpClient.MODE_BINARY);
		} catch (ConnectException e){
			throw new FileTransferException("Failed to connect, " + e.getMessage());
		} catch (IOException e){
			throw new FileTransferException("IO error occured", e);
		} catch (SshException e) {
			throw new FileTransferException("SSH connection error occured", e);
		} catch (SftpStatusException e) {
			throw new FileTransferException("SFTP error", e);
		} catch (ChannelOpenException e) {
			throw new FileTransferException("SSH channel error", e);
		} catch (InvalidPassphraseException e) {
			throw new FileTransferException("SSH channel passphrase error", e);
		}
	}

	private void setKeys() throws FileNotFoundException, IOException,
			InvalidPassphraseException, SshException, FileTransferException {
		final PublicKeyAuthentication pk = new PublicKeyAuthentication();
		final SshKeyPair pair = readKeyPairFromFile();
		pk.setPrivateKey(pair.getPrivateKey());
		pk.setPublicKey(pair.getPublicKey());
		if(client.authenticate(pk) != SshAuthentication.COMPLETE
				&& client.isConnected() && !client.isAuthenticated()){
			throw new FileTransferException("SSH failed to authenticate.");
		}
	}
	
	private SshKeyPair readKeyPairFromFile() throws FileNotFoundException, IOException, InvalidPassphraseException {
		final SshPrivateKeyFile pkfile = SshPrivateKeyFileFactory.parse(
				new FileInputStream(privateKeyFilePath));
		return pkfile.toKeyPair("");
	}

	@Override
	public void cd(final String dir) throws FileTransferException {
		try {
			sftp.cd(dir);
		} catch (SftpStatusException e) {
			throw new FileTransferException("SFTP status error changing directory", e);
		} catch (SshException e) {
			throw new FileTransferException("SSH error changing directory", e);
		}
	}

	@Override
	public void disconnect() throws FileTransferException {
		try {
			if(sftp != null){
				sftp.quit();
			}
		} catch (SshException e) {
			throw new FileTransferException("SSH error disconnecting SFTP session", e);
		}
		if(client != null){
			client.disconnect();
		}
	}

	@Override
	public void get(final String remoteFileName, final String localFileName)
			throws FileTransferException {
		try {
			sftp.get(remoteFileName, localFileName);
		} catch (FileNotFoundException e) {
			throw new FileTransferException("SFTP file not found error", e);
		} catch (SftpStatusException e) {
			throw new FileTransferException("SFTP transfer status error", e);
		} catch (SshException e) {
			throw new FileTransferException("SFTP SSH error", e);
		} catch (TransferCancelledException e) {
			throw new FileTransferException("SFTP transfer cancelled error", e);
		}
	}

	@Override
	public void lcd(final String dir) throws FileTransferException {
		try {
			sftp.lcd(dir);
		} catch (SftpStatusException e) {
			throw new FileTransferException("SFTP error changing local directory", e);
		}
	}

	@Override
	public SftpFile[] ls() throws FileTransferException {
		try {
			return sftp.ls();
		} catch (SftpStatusException e) {
			throw new FileTransferException("SFTP status error listing directory", e);
		} catch (SshException e) {
			throw new FileTransferException("SSH error listing directory", e);
		}
	}
	
	@Override
	public Date creationTime(final String remoteFileName){
		Date creationDate = null;
		try {
			SftpFileAttributes fileAttributes = sftp.stat(remoteFileName);
			long modified = fileAttributes.getModifiedTime().longValue() * 1000;	
			creationDate = new Date(modified);
			
		}  catch (SftpStatusException e) {
			e.printStackTrace();
		} catch (SshException e) {
			e.printStackTrace();
		} 
		
		return creationDate;
	}

}
