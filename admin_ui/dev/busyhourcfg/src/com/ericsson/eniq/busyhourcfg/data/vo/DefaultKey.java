/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

/**
 * @author eheijun
 * 
 */
public class DefaultKey implements Key {

  private String source;

  private final String originalSource;

  private String keyValue;

  private final String originalKeyValue;

  private String keyName;

  private final String originalKeyName;

  private boolean modified;

  public DefaultKey(final String keyName, final String source, final String keyValue) {
    super();
    this.source = source;
    this.originalSource = source;
    this.keyName = keyName;
    this.originalKeyName = keyName;
    this.keyValue = keyValue;
    this.originalKeyValue = keyValue;
    modified = false;
  }

  @Override
  public String getKeyName() {
    return keyName;
  }

  @Override
  public String getKeyValue() {
    return keyValue;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public void setKeyName(final String keyName) {
    if (this.keyName != null && !this.keyName.equals(keyName)) {
      this.keyName = keyName;
      this.modified = true;
    }
  }

  @Override
  public void setKeyValue(final String keyValue) {
    if (this.keyValue != null && !this.keyValue.equals(keyValue)) {
      this.keyValue = keyValue;
      this.modified = true;
    }
  }

  @Override
  public void setSource(final String source) {
    if (this.source != null && !this.source.equals(source)) {
      this.source = source;
      this.modified = true;
    }
  }

  @Override
  public Boolean isModified() {
    return modified;
  }

  @Override
  public Boolean isNew() {
    return originalSource.equals("") && originalKeyName.equals("") && this.originalKeyValue.equals("");
  }
}
