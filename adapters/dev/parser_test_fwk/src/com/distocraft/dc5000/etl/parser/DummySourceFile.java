package com.distocraft.dc5000.etl.parser;

import java.io.File;
import java.util.Properties;

public class DummySourceFile extends SourceFile {

  public DummySourceFile(final File ropFile, final Properties parserConfig) {
    super(ropFile, parserConfig, null, null, new ParseSession(-1, parserConfig), null, null);
  }
}