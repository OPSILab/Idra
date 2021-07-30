package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InnerDatasetMetaDefault {

  private String publisher;
  private List<String> theme;
  private String language;
  private String description;
  private String license;
  @SerializedName(value = "records_count")
  private Integer recordsCount;
  private String title;
  @SerializedName(value = "source_domain")
  private String sourceDomain;
  @SerializedName(value = "source_dataset")
  private String sourceDataset;
  private String modified;
  @SerializedName(value = "parent_domain")
  private Object parentDomain;
  @SerializedName(value = "oauth_scope")
  private Object oauthScope;
  private List<String> attributions;
  private String references;
  private List<String> keyword;
  @SerializedName(value = "source_domain_title")
  private String sourceDomainTitle;
  @SerializedName(value = "source_domain_address")
  private String sourceDomainAddress;
  @SerializedName(value = "data_processed")
  private String dataProcessed;
  @SerializedName(value = "metadata_processed")
  private String metadataProcessed;
  @SerializedName(value = "geographic_area_mode")
  private String geographicAreaMode;
  @SerializedName(value = "geographic_area")
  private Object geographicArea;

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public List<String> getTheme() {
    return theme;
  }

  public void setTheme(List<String> theme) {
    this.theme = theme;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public Integer getRecordsCount() {
    return recordsCount;
  }

  public void setRecordsCount(Integer recordsCount) {
    this.recordsCount = recordsCount;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSourceDomain() {
    return sourceDomain;
  }

  public void setSourceDomain(String sourceDomain) {
    this.sourceDomain = sourceDomain;
  }

  public String getSourceDataset() {
    return sourceDataset;
  }

  public void setSourceDataset(String sourceDataset) {
    this.sourceDataset = sourceDataset;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public Object getParentDomain() {
    return parentDomain;
  }

  public void setParentDomain(Object parentDomain) {
    this.parentDomain = parentDomain;
  }

  public Object getOauthScope() {
    return oauthScope;
  }

  public void setOauthScope(Object oauthScope) {
    this.oauthScope = oauthScope;
  }

  public List<String> getAttributions() {
    return attributions;
  }

  public void setAttributions(List<String> attributions) {
    this.attributions = attributions;
  }

  public String getReferences() {
    return references;
  }

  public void setReferences(String references) {
    this.references = references;
  }

  public List<String> getKeyword() {
    return keyword;
  }

  public void setKeyword(List<String> keyword) {
    this.keyword = keyword;
  }

  public String getSourceDomainTitle() {
    return sourceDomainTitle;
  }

  public void setSourceDomainTitle(String sourceDomainTitle) {
    this.sourceDomainTitle = sourceDomainTitle;
  }

  public String getSourceDomainAddress() {
    return sourceDomainAddress;
  }

  public void setSourceDomainAddress(String sourceDomainAddress) {
    this.sourceDomainAddress = sourceDomainAddress;
  }

  public String getDataProcessed() {
    return dataProcessed;
  }

  public void setDataProcessed(String dataProcessed) {
    this.dataProcessed = dataProcessed;
  }

  public String getMetadataProcessed() {
    return metadataProcessed;
  }

  public void setMetadataProcessed(String metadataProcessed) {
    this.metadataProcessed = metadataProcessed;
  }

  public String getGeographicAreaMode() {
    return geographicAreaMode;
  }

  public void setGeographicAreaMode(String geographicAreaMode) {
    this.geographicAreaMode = geographicAreaMode;
  }

  public Object getGeographicArea() {
    return geographicArea;
  }

  public void setGeographicArea(Object geographicArea) {
    this.geographicArea = geographicArea;
  }

}
