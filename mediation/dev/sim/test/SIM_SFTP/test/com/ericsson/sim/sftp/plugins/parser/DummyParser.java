package com.ericsson.sim.sftp.plugins.parser;

import java.io.File;

import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.sftp.plugins.parser.ParserParent;

public class DummyParser extends ParserParent{
	public void parseFile(Node node, SFTPproperties properties,
			File dataFile) throws SIMException {
		//Do Nothing
	}
}
  