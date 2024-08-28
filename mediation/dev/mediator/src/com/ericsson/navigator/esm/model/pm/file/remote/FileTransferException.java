package com.ericsson.navigator.esm.model.pm.file.remote;


public class FileTransferException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileTransferException(final String message, final Exception e) {
		super(message, e);
	}

	public FileTransferException(final String message) {
		super(message);
	}
}
