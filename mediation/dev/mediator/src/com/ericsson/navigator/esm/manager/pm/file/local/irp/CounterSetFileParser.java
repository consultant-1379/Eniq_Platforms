package com.ericsson.navigator.esm.manager.pm.file.local.irp;

import java.io.FilenameFilter;

public interface CounterSetFileParser {
	
	enum TYPE { IRP, ASN1 };

	FilenameFilter getFilenameFilter();
	void parse(final String fileName) throws Exception; //NOPMD
	TYPE getType();
}
