/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.List;

/**
 * @author eheijun
 * 
 */
public interface Placeholder {
  
  /**
   * Getter for placeholdertype
   * @return Placeholdertype
   */
  String getPlaceholdertype();
  
  /**
   * Setter for
   * @param placeholdertype
   */
  void setPlaceholdertype(String placeholdertype);

  /**
   * Getter for description
   * @return
   */
  String getDescription();
  
  /**
   * Setter for description
   * @param description
   */
  void setDescription(String description);

  /**
   * Getter for sources
   * @return
   */
  List<Source> getSources();
  
  /**
   * Setter for sources
   * @param sources
   */
  void setSources(List<Source> sources);

  /**
   * Add new source for placeholder
   * @param string
   */
  void addSource(String string);

  /**
   * Remove selected source from placeholder
   * @param string
   */
  void removeSource(Source source);

  /**
   * Getter for where
   * @return
   */
  String getWhere();
  
  /**
   * Setter for where
   * @param where
   */
  void setWhere(String where);

  /**
   * Getter for criteria
   * @return
   */
  String getCriteria();
  
  /**
   * Setter for criteria
   * @param criteria
   */
  void setCriteria(String criteria);

  /**
   * Getter for keys
   * @return
   */
  List<Key> getKeys();
  
  /**
   * Add new key for placeholder
   * @param keyName
   * @param source
   * @param keyValue
   */
  void addKey(String keyName, String source, String keyValue);

  /**
   * Remove selected key from placeholder
   * @param key
   */
  void removeKey(Key key);
  
  /**
   * Get list of removed keys
   * @return
   */
  List<Key> getRemovedKeys();
  
  /**
   * Setter for keys
   * @param keys
   */
  void setKeys(List<Key> keys);

  /**
   * Getter for mapped types
   * @return
   */
  List<MappedType> getMappedTypes();
  
  /**
   * Setter for mapped types
   * @param mappedTypes
   */
  void setMappedTypes(List<MappedType> mappedTypes);

  /**
   * Getter for enabled
   * @return
   */
  Boolean getEnabled();
  
  /**
   * Setter for enabled
   * @param enabled
   */
  void setEnabled(Boolean enabled);

  /**
   * Getter for bhtype
   * @return
   */
  String getBhtype();
  
  /**
   * Setter for bhtype
   * @param type
   */
  void setBhtype(String type);

  /**
   * Getter for grouping
   * @return
   */
  String getGrouping();
  
  /**
   * Setter for grouping
   * @param grouping
   */
  void setGrouping(String grouping);
  
  /**
   * Getter for aggregation type
   * @return
   */
  String getAggregationType();
  
  /**
   * Setter for aggregation type
   * @param aggregationtype
   */
  void setAggregationType(String aggregationtype);
  
  /**
   * Getter for windowsize
   * @return
   */
  Integer getWindowsize();

  /**
   * Getter for offset
   * @return
   */
  Integer getOffset();
  
  /**
   * Getter for lookback 
   * @return
   */
  Integer getLookback();
  
  /**
   * Setter for lookback
   * @param lookback
   */
  void setLookback(Integer lookback);
  
  /**
   * Getter for positive threshold
   * @return
   */
  Integer getPThreshold();
  
  /**
   * Setter for positive threshold 
   * @param pThreshold
   */
  void setPThreshold(Integer pThreshold);
  
  /**
   * Getter for negative threshold
   * @return
   */
  Integer getNThreshold();
  
  /**
   * Setter for negative threshold 
   * @param nThreshold
   */
  void setNThreshold(Integer nThreshold);
  
  /**
   * Getter for reactivateviews
   * @return
   */
  Integer getReactivateviews();
  
  /**
   * Setter for reactivate views
   * @return
   */
  void setReactivateviews(Integer reactivateviews);
  
  /**
   * Checks if placeholder type is product
   * @return
   */
  Boolean isProduct();
  
  /**
   * Checks if placeholder type is custom
   * @return
   */
  Boolean isCustom();
  
  /**
   * Checks if busy hour type is timelimited
   * @return
   */
  Boolean isTimelimited();
  
  /**
   * Checks if busy hour type is slidingwindow
   * @return
   */
  Boolean isSlidingwindow();
  
  /**
   * Checks if busy hour type is timelimited+timeconsistent 
   * @return
   */
  Boolean isTimelimitedTimeconsistent();
  
  /**
   * Checks if busy hour type is slidingwindow+timeconsistent 
   * @return
   */
  Boolean isSlidingwindowTimeconsistent();
  
  /**
   * Checks if busy hour type is peakrop
   * @return
   */
  Boolean isPeakrop();
  
  /**
   * Clears all data from object
   */
  void clearAllData();

  /**
   * Undo all changes
   */
  void undoAllChanges();

  /**
   * Undo source changes
   */
  void undoSourceChanges();

  /**
   * Undo key changes
   */
  void undoKeyChanges();

  /**
   * Undo all busy hour type changes
   */
  void undoBusyHourTypeChanges();

  /**
   * Undo mapped type changes 
   */
  void undoMappedTypeChanges();
  
}
