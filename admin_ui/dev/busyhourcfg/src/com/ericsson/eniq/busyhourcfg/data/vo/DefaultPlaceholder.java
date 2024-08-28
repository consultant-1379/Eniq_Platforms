/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.data.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ericsson.eniq.busyhourcfg.common.Constants;
import com.ericsson.eniq.busyhourcfg.common.Utils;

/**
 * @author eheijun
 * 
 */
public class DefaultPlaceholder implements Placeholder {

  public final static String RANKBH_TIMELIMITED = "RANKBH_TIMELIMITED";
  
  public final static String RANKBH_SLIDINGWINDOW = "RANKBH_SLIDINGWINDOW";
  
  public final static String RANKBH_TIMECONSISTENT = "RANKBH_TIMECONSISTENT";
  
  public final static String RANKBH_TIMECONSISTENT_SLIDINGWINDOW = "RANKBH_TIMECONSISTENT_SLIDINGWINDOW";
  
  public final static String RANKBH_PEAKROP = "RANKBH_PEAKROP";
  
  private final static Integer WINDOWSIZE = 60;
  
  private final static Integer PEAKROP_WINDOWSIZE = 15;

  private final static Integer SLIDINGWINDOW_OFFSET = 15;

  private static final Integer DEFAULT_OFFSET = 0;

  private String versionid;

  private String bhlevel;

  private String bhtype;

  private String targetversionid;

  private String bhobject;

  private List<Source> sources;

  private List<Source> removedsources;
  
  private String criteria;

  private String where;

  private String description;
  
  private List<Key> keys;
  
  private List<Key> removedkeys;

  private List<MappedType> mappedTypes;

  private String placeholdertype;

  private Boolean enabled;

  private String grouping;

  private String aggregationType;

  private Integer lookback;

  private Integer nThreshold;

  private Integer pThreshold;
  
  private Integer reactivateviews;

  private String oVersionid;

  private String oBhlevel;

  private String oBhtype;

  private String oTargetversionid;

  private String oBhobject;

  private ArrayList<Source> oSources;

  private ArrayList<Key> oKeys;

  private ArrayList<MappedType> oMappedTypes;

  private String oAggregationType;

  private Integer oLookback;

  private Integer oNThreshold;

  private Integer oPThreshold;

  private String oPlaceholdertype;
  private String oDescription;
  private String oWhere;
  private String oCriteria;
  private Boolean oEnabled;
  private String oGrouping;
  private Integer oReactivateviews;
  /**
   * New Placeholder constructor
   * 
   * @param versionid
   * @param bhlevel
   * @param bhtype
   * @param targetversionid
   * @param bhobject
   * @param aggregationType
   * @param lookback
   * @param nThreshold
   * @param pThreshold
   */
  public DefaultPlaceholder(final String versionid, final String bhlevel, final String bhtype,
      final String targetversionid, final String bhobject, final String aggregationType, /*final Integer offset,*/
      final Integer lookback, final Integer nThreshold, final Integer pThreshold, final String placeholdertype,
      final String description, final String where, final String criteria, final Boolean enabled, final String grouping,
      final Integer reactivateviews) {
    this.versionid = versionid;
    this.oVersionid = this.versionid;
    this.bhlevel = bhlevel;
    this.oBhlevel = this.bhlevel;
    this.bhtype = bhtype;
    this.oBhtype = this.bhtype;
    this.targetversionid = targetversionid;
    this.oTargetversionid = this.targetversionid;
    this.bhobject = bhobject;
    this.oBhobject = this.bhobject;
    this.sources = new ArrayList<Source>();
    this.oSources = new ArrayList<Source>(this.sources);
    this.keys = new ArrayList<Key>();
    this.oKeys = new ArrayList<Key>(keys);
    this.mappedTypes = new ArrayList<MappedType>();
    this.oMappedTypes = new ArrayList<MappedType>(this.mappedTypes);
    this.aggregationType = aggregationType;
    this.oAggregationType = this.aggregationType;
    if (this.isTimelimited()) {
      this.lookback = null;
      this.oLookback = this.lookback;
      this.nThreshold = null;
      this.oNThreshold = this.nThreshold;
      this.pThreshold = null;
      this.oPThreshold = this.pThreshold;
    } else if (this.isSlidingwindow() || this.isPeakrop()) {
      this.lookback = null;
      this.oLookback = this.lookback;
      this.nThreshold = null;
      this.oNThreshold = this.nThreshold;
      this.pThreshold = null;
      this.oPThreshold = this.pThreshold;
    } else if (this.isTimelimitedTimeconsistent()) {
      this.lookback = Utils.replaceNull(lookback);
      this.oLookback = Utils.replaceNull(lookback);
      this.nThreshold = Utils.replaceNull(nThreshold);
      this.oNThreshold = Utils.replaceNull(nThreshold);
      this.pThreshold = Utils.replaceNull(pThreshold);
      this.oPThreshold = Utils.replaceNull(pThreshold);
    } else if (this.isSlidingwindowTimeconsistent()) {
      this.lookback = Utils.replaceNull(lookback);
      this.oLookback = Utils.replaceNull(lookback);
      this.nThreshold = Utils.replaceNull(nThreshold);
      this.oNThreshold = Utils.replaceNull(nThreshold);
      this.pThreshold = Utils.replaceNull(pThreshold);
      this.oPThreshold = Utils.replaceNull(pThreshold);
    }
    this.placeholdertype = placeholdertype;
    this.oPlaceholdertype = this.placeholdertype;
    this.description = description;
    this.oDescription = this.description;
    this.where = where;
    this.oWhere = this.where;
    this.criteria = criteria;
    this.oCriteria = this.criteria;
    this.enabled = enabled; 
    this.oEnabled = this.enabled; 
    this.grouping = grouping;
    this.oGrouping = this.grouping;
    this.reactivateviews = reactivateviews;     
    this.oReactivateviews = this.reactivateviews;     
    this.removedsources = new ArrayList<Source>(); 
    this.removedkeys = new ArrayList<Key>(); 
  }


/**
   * @return the versionid
   */
  public String getVersionid() {
    return versionid;
  }

  /**
   * @param versionid
   *          the versionid to set
   */
  public void setVersionid(final String versionid) {
    this.versionid = versionid;
  }

  /**
   * @return the bhlevel
   */
  public String getBhlevel() {
    return bhlevel;
  }

  /**
   * @param bhlevel
   *          the bhlevel to set
   */
  public void setBhlevel(final String bhlevel) {
    this.bhlevel = bhlevel;
  }

  /**
   * @return the bhtype
   */
  public String getBhtype() {
    return bhtype;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setType(java.lang.String)
   */
  @Override
  public void setBhtype(final String bhtype) {
    this.bhtype = bhtype;
  }

  /**
   * @return the targetversionid
   */
  public String getTargetversionid() {
    return targetversionid;
  }

  /**
   * @param targetversionid
   *          the targetversionid to set
   */
  public void setTargetversionid(final String targetversionid) {
    this.targetversionid = targetversionid;
  }

  /**
   * @return the bhobject
   */
  public String getBhobject() {
    return bhobject;
  }

  /**
   * @param bhobject
   *          the bhobject to set
   */
  public void setBhobject(final String bhobject) {
    this.bhobject = bhobject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getCriteria()
   */
  @Override
  public String getCriteria() {
    return criteria;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setCriteria(java.lang
   * .String)
   */
  public void setCriteria(final String criteria) {
    this.criteria = criteria;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getDescription()
   */
  @Override
  public String getDescription() {
    return description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setDescription(java.lang
   * .String)
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getEnabled()
   */
  @Override
  public Boolean getEnabled() {
    return enabled;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setEnabled(java.lang.
   * Boolean)
   */
  public void setEnabled(final Boolean enabled) {
    this.enabled = enabled;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getKeys()
   */
  @Override
  public List<Key> getKeys() {
    return keys;
  }
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#addSource(java.lang.String
   * )
   */
  @Override
  public void addKey(final String keyName, final String source, final String keyValue) {
    this.keys.add(new DefaultKey(keyName,source,keyValue));
  }
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setKeys(java.util.List)
   */
  public void setKeys(final List<Key> keys) {
    this.keys = keys;
    this.oKeys = new ArrayList<Key>(this.keys);
    Collections.copy(this.oKeys, this.keys);
  }

  @Override
  public void removeKey(final Key key) {
	    if (this.getKeys().remove(key)) {
	        this.getRemovedKeys().add(key);
	      }	
  }
  
  public List<Key> getRemovedKeys() {
	    return removedkeys;  
  }
  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getMappedTypes()
   */
  @Override
  public List<MappedType> getMappedTypes() {
    return mappedTypes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setMappedTypes(java.util
   * .List)
   */
  public void setMappedTypes(final List<MappedType> mappedTypes) {
    this.mappedTypes = mappedTypes;
    this.oMappedTypes = new ArrayList<MappedType>(this.mappedTypes);
    Collections.copy(this.oMappedTypes, this.mappedTypes);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getPlaceholdertype()
   */
  public String getPlaceholdertype() {
    return placeholdertype;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setPlaceholdertype(java
   * .lang.String)
   */
  public void setPlaceholdertype(final String placeholdertype) {
    this.placeholdertype = placeholdertype;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getSources()
   */
  @Override
  public List<Source> getSources() {
    return sources;
  }

  public List<Source> getRemovedSources() {
    return removedsources;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setSources(java.util.
   * List)
   */
  @Override
  public void setSources(final List<Source> sources) {
    this.sources = sources;
    this.oSources = new ArrayList<Source>(this.sources);
    Collections.copy(this.oSources, this.sources);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#addSource(java.lang.String
   * )
   */
  @Override
  public void addSource(final String source) {
    this.sources.add(new DefaultSource(source));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#removeSource(java.lang
   * .String)
   */
  @Override
  public void removeSource(final Source source) {
    if (this.getSources().remove(source)) {
      this.getRemovedSources().add(source);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.PlaceHolder#getWhere()
   */
  @Override
  public String getWhere() {
    return where;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setWhere(java.lang.String
   * )
   */
  public void setWhere(final String where) {
    this.where = where;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getGrouping()
   */
  @Override
  public String getGrouping() {
    return grouping;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setGrouping(java.lang
   * .String)
   */
  @Override
  public void setGrouping(final String grouping) {
    this.grouping = grouping;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getWindowsize()
   */
  @Override
  public Integer getWindowsize() {
	if(this.isPeakrop()){
		return PEAKROP_WINDOWSIZE;
	}
    return WINDOWSIZE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getLookback()
   */
  @Override
  public Integer getLookback() {
    return lookback;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getNThreshold()
   */
  @Override
  public Integer getNThreshold() {
    return nThreshold;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getOffset()
   */
  @Override
  public Integer getOffset() {
    if (this.isSlidingwindow() || (this.isSlidingwindowTimeconsistent() || this.isPeakrop())) {
      return SLIDINGWINDOW_OFFSET;
    } else  {
      return DEFAULT_OFFSET;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getPThreshold()
   */
  @Override
  public Integer getPThreshold() {
    return pThreshold;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setLookback(java.
   * lang.Integer)
   */
  @Override
  public void setLookback(final Integer lookback) {
    this.lookback = lookback;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setNThreshold(java
   * .lang.Integer)
   */
  @Override
  public void setNThreshold(final Integer nThreshold) {
    this.nThreshold = nThreshold;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setPThreshold(java
   * .lang.Integer)
   */
  @Override
  public void setPThreshold(final Integer pThreshold) {
    this.pThreshold = pThreshold;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getAggregationType()
   */
  @Override
  public String getAggregationType() {
    return aggregationType;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setAggregationType(java.lang.String)
   */
  @Override
  public void setAggregationType(final String aggregationType) {
    this.aggregationType = aggregationType;
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#getReactivateviews()
   */
  @Override
  public Integer getReactivateviews() {
    return reactivateviews;
  }
  
  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#setReactivateviews(java.lang.Integer)
   */
  @Override
  public void setReactivateviews(final Integer reactivateviews) {
    this.reactivateviews = reactivateviews;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isCustom()
   */
  @Override
  public Boolean isCustom() {
    return Constants.CUSTOM.equals(placeholdertype);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isProduct()
   */
  @Override
  public Boolean isProduct() {
    return Constants.PRODUCT.equals(placeholdertype);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isTimelimited()
   */
  @Override
  public Boolean isTimelimited() {
    if (aggregationType == null) {
      return false;
    }
    return aggregationType.equals(RANKBH_TIMELIMITED);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isSlidingwindow()
   */
  @Override
  public Boolean isSlidingwindow() {
    if (aggregationType == null) {
      return false;
    }
    return aggregationType.equals(RANKBH_SLIDINGWINDOW);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isTimeconsistent()
   */
  @Override
  public Boolean isTimelimitedTimeconsistent() {
    if (aggregationType == null) {
      return false;
    }
    return aggregationType.equals(RANKBH_TIMECONSISTENT);
  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isTimeconsistentOffset()
   */
  @Override
  public Boolean isSlidingwindowTimeconsistent() {
    if (aggregationType == null) {
      return false;
    }
    return aggregationType.equals(RANKBH_TIMECONSISTENT_SLIDINGWINDOW);
  }
  
  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#isPeakrop()
   */
  @Override
  public Boolean isPeakrop() {
    if (aggregationType == null) {
      return false;
    }
    return aggregationType.equals(RANKBH_PEAKROP);
  }

  /*
   * 
   */
  public Boolean isOffsetDisabled() {
	  if (aggregationType.equals(RANKBH_SLIDINGWINDOW) || aggregationType.equals(RANKBH_TIMECONSISTENT_SLIDINGWINDOW) || aggregationType.equals(RANKBH_PEAKROP)){
		    return false;  
	  }
	    return true;
	  }
  
  /*
   * 
   */
  public Boolean isLookbackDisabled() {
	  if (aggregationType.equals(RANKBH_TIMECONSISTENT) || aggregationType.equals(RANKBH_TIMECONSISTENT_SLIDINGWINDOW)){
		    return false;  
	  }
	    return true;
	  }
  
  /*
   * 
   */
  public Boolean isPthresholdDisabled() {
	  if (aggregationType.equals(RANKBH_TIMECONSISTENT) || aggregationType.equals(RANKBH_TIMECONSISTENT_SLIDINGWINDOW)){
		    return false;  
	  }
	    return true;
	  }
  
  /*
   * 
   */
  public Boolean isNthresholdDisabled() {
	  if (aggregationType.equals(RANKBH_TIMECONSISTENT) || aggregationType.equals(RANKBH_TIMECONSISTENT_SLIDINGWINDOW)){
		    return false;  
	  }
	    return true;
	  }

  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.busyhourcfg.data.vo.Placeholder#clearAllData()
   */
  @Override
  public void clearAllData() {
    if (sources.size() > 0) {
      sources.clear();
    }
    if (keys.size() > 0) {
      keys.clear();
    }
    for (MappedType mappedType : mappedTypes) {
      if (!mappedType.getEnabled().equals(Boolean.valueOf(true))) {
        mappedType.setEnabled(Boolean.valueOf(true));
      }
    }
    if (!this.criteria.equals("")) {
      this.criteria = "";
    }
    if (!this.where.equals("")) {
      this.where = "";
    }
    if (!this.description.equals("")) {
      this.description = "";
    }
    if (this.enabled != null) {
      this.enabled = false;
    }
    if (!this.grouping.equals(Constants.GROUPING_NONE) ) {
      this.grouping = Constants.GROUPING_NONE;
    }
    if (!this.aggregationType.equals(RANKBH_TIMELIMITED)) {
      this.aggregationType = RANKBH_TIMELIMITED;
    }
    if (this.lookback != null) {
      this.lookback = null;
    }
    if (this.nThreshold != null) {
      this.nThreshold = null;
    }
    if (this.pThreshold != null) {
      this.pThreshold = null;
    }
  }

  @Override
  public void undoAllChanges() {
    this.versionid = oVersionid;
    this.bhlevel = oBhlevel;
    this.bhtype = oBhtype;
    this.targetversionid = oTargetversionid;
    this.bhobject = oBhobject;
    this.placeholdertype = oPlaceholdertype;
    this.description = oDescription;
    this.where = oWhere;
    this.criteria = oCriteria;
    this.enabled = oEnabled;
    this.grouping = oGrouping;
    this.reactivateviews = oReactivateviews;
    undoSourceChanges();
    undoKeyChanges();
    undoBusyHourTypeChanges();
    undoMappedTypeChanges();
  }
  
  @Override
  public void undoSourceChanges() {
    this.sources = this.oSources;
    this.oSources = new ArrayList<Source>(this.sources);
    this.removedsources = new ArrayList<Source>(); 
  }
  
  @Override
  public void undoKeyChanges() {
    this.keys = this.oKeys;
    this.oKeys = new ArrayList<Key>(keys);
    this.removedkeys = new ArrayList<Key>(); 
  }
  
  @Override
  public void undoBusyHourTypeChanges() {
    this.aggregationType = oAggregationType;
    this.lookback = oLookback;
    this.nThreshold = oNThreshold;
    this.pThreshold = oPThreshold;
  }
  
  @Override
  public void undoMappedTypeChanges() {
    this.mappedTypes = this.oMappedTypes;
    this.oMappedTypes = new ArrayList<MappedType>(this.mappedTypes);
  }
  
}
