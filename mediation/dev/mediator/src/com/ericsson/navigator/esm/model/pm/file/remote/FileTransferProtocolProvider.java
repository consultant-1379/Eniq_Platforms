package com.ericsson.navigator.esm.model.pm.file.remote;

import java.util.Date;

import com.maverick.sftp.SftpFile;

public interface FileTransferProtocolProvider {

	void initialize() throws FileTransferException;

	void connect(String hostname, int port, String user) throws FileTransferException;

	void cd(String remoteDirectory) throws FileTransferException;

	void disconnect() throws FileTransferException;

	SftpFile[] ls() throws FileTransferException;

	void lcd(String inputDirectory) throws FileTransferException;

	void get(String remoteFileName, String localFileName) throws FileTransferException;
	
	Date creationTime(String remoteFileName);
}
