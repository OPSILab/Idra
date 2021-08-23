/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class InnerDatasetMetaDefault.
 */
public class InnerDatasetMetaDefault {

  /** The publisher. */
  private String publisher;

  /** The theme. */
  private List<String> theme;

  /** The language. */
  private String language;

  /** The description. */
  private String description;

  /** The license. */
  private String license;

  /** The records count. */
  @SerializedName(value = "records_count")
  private Integer recordsCount;

  /** The title. */
  private String title;

  /** The source domain. */
  @SerializedName(value = "source_domain")
  private String sourceDomain;

  /** The source dataset. */
  @SerializedName(value = "source_dataset")
  private String sourceDataset;

  /** The modified. */
  private String modified;

  /** The parent domain. */
  @SerializedName(value = "parent_domain")
  private Object parentDomain;

  /** The oauth scope. */
  @SerializedName(value = "oauth_scope")
  private Object oauthScope;

  /** The attributions. */
  private List<String> attributions;

  /** The references. */
  private String references;

  /** The keyword. */
  private List<String> keyword;

  /** The source domain title. */
  @SerializedName(value = "source_domain_title")
  private String sourceDomainTitle;

  /** The source domain address. */
  @SerializedName(value = "source_domain_address")
  private String sourceDomainAddress;

  /** The data processed. */
  @SerializedName(value = "data_processed")
  private String dataProcessed;

  /** The metadata processed. */
  @SerializedName(value = "metadata_processed")
  private String metadataProcessed;

  /** The geographic area mode. */
  @SerializedName(value = "geographic_area_mode")
  private String geographicAreaMode;

  /** The geographic area. */
  @SerializedName(value = "geographic_area")
  private Object geographicArea;

  /**
   * Gets the publisher.
   *
   * @return the publisher
   */
  public String getPublisher() {
    return publisher;
  }

  /**
   * Sets the publisher.
   *
   * @param publisher the new publisher
   */
  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  /**
   * Gets the theme.
   *
   * @return the theme
   */
  public List<String> getTheme() {
    return theme;
  }

  /**
   * Sets the theme.
   *
   * @param theme the new theme
   */
  public void setTheme(List<String> theme) {
    this.theme = theme;
  }

  /**
   * Gets the language.
   *
   * @return the language
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Sets the language.
   *
   * @param language the new language
   */
  public void setLanguage(String language) {
    this.language = language;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the license.
   *
   * @return the license
   */
  public String getLicense() {
    return license;
  }

  /**
   * Sets the license.
   *
   * @param license the new license
   */
  public void setLicense(String license) {
    this.license = license;
  }

  /**
   * Gets the records count.
   *
   * @return the records count
   */
  public Integer getRecordsCount() {
    return recordsCount;
  }

  /**
   * Sets the records count.
   *
   * @param recordsCount the new records count
   */
  public void setRecordsCount(Integer recordsCount) {
    this.recordsCount = recordsCount;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the source domain.
   *
   * @return the source domain
   */
  public String getSourceDomain() {
    return sourceDomain;
  }

  /**
   * Sets the source domain.
   *
   * @param sourceDomain the new source domain
   */
  public void setSourceDomain(String sourceDomain) {
    this.sourceDomain = sourceDomain;
  }

  /**
   * Gets the source dataset.
   *
   * @return the source dataset
   */
  public String getSourceDataset() {
    return sourceDataset;
  }

  /**
   * Sets the source dataset.
   *
   * @param sourceDataset the new source dataset
   */
  public void setSourceDataset(String sourceDataset) {
    this.sourceDataset = sourceDataset;
  }

  /**
   * Gets the modified.
   *
   * @return the modified
   */
  public String getModified() {
    return modified;
  }

  /**
   * Sets the modified.
   *
   * @param modified the new modified
   */
  public void setModified(String modified) {
    this.modified = modified;
  }

  /**
   * Gets the parent domain.
   *
   * @return the parent domain
   */
  public Object getParentDomain() {
    return parentDomain;
  }

  /**
   * Sets the parent domain.
   *
   * @param parentDomain the new parent domain
   */
  public void setParentDomain(Object parentDomain) {
    this.parentDomain = parentDomain;
  }

  /**
   * Gets the oauth scope.
   *
   * @return the oauth scope
   */
  public Object getOauthScope() {
    return oauthScope;
  }

  /**
   * Sets the oauth scope.
   *
   * @param oauthScope the new oauth scope
   */
  public void setOauthScope(Object oauthScope) {
    this.oauthScope = oauthScope;
  }

  /**
   * Gets the attributions.
   *
   * @return the attributions
   */
  public List<String> getAttributions() {
    return attributions;
  }

  /**
   * Sets the attributions.
   *
   * @param attributions the new attributions
   */
  public void setAttributions(List<String> attributions) {
    this.attributions = attributions;
  }

  /**
   * Gets the references.
   *
   * @return the references
   */
  public String getReferences() {
    return references;
  }

  /**
   * Sets the references.
   *
   * @param references the new references
   */
  public void setReferences(String references) {
    this.references = references;
  }

  /**
   * Gets the keyword.
   *
   * @return the keyword
   */
  public List<String> getKeyword() {
    return keyword;
  }

  /**
   * Sets the keyword.
   *
   * @param keyword the new keyword
   */
  public void setKeyword(List<String> keyword) {
    this.keyword = keyword;
  }

  /**
   * Gets the source domain title.
   *
   * @return the source domain title
   */
  public String getSourceDomainTitle() {
    return sourceDomainTitle;
  }

  /**
   * Sets the source domain title.
   *
   * @param sourceDomainTitle the new source domain title
   */
  public void setSourceDomainTitle(String sourceDomainTitle) {
    this.sourceDomainTitle = sourceDomainTitle;
  }

  /**
   * Gets the source domain address.
   *
   * @return the source domain address
   */
  public String getSourceDomainAddress() {
    return sourceDomainAddress;
  }

  /**
   * Sets the source domain address.
   *
   * @param sourceDomainAddress the new source domain address
   */
  public void setSourceDomainAddress(String sourceDomainAddress) {
    this.sourceDomainAddress = sourceDomainAddress;
  }

  /**
   * Gets the data processed.
   *
   * @return the data processed
   */
  public String getDataProcessed() {
    return dataProcessed;
  }

  /**
   * Sets the data processed.
   *
   * @param dataProcessed the new data processed
   */
  public void setDataProcessed(String dataProcessed) {
    this.dataProcessed = dataProcessed;
  }

  /**
   * Gets the metadata processed.
   *
   * @return the metadata processed
   */
  public String getMetadataProcessed() {
    return metadataProcessed;
  }

  /**
   * Sets the metadata processed.
   *
   * @param metadataProcessed the new metadata processed
   */
  public void setMetadataProcessed(String metadataProcessed) {
    this.metadataProcessed = metadataProcessed;
  }

  /**
   * Gets the geographic area mode.
   *
   * @return the geographic area mode
   */
  public String getGeographicAreaMode() {
    return geographicAreaMode;
  }

  /**
   * Sets the geographic area mode.
   *
   * @param geographicAreaMode the new geographic area mode
   */
  public void setGeographicAreaMode(String geographicAreaMode) {
    this.geographicAreaMode = geographicAreaMode;
  }

  /**
   * Gets the geographic area.
   *
   * @return the geographic area
   */
  public Object getGeographicArea() {
    return geographicArea;
  }

  /**
   * Sets the geographic area.
   *
   * @param geographicArea the new geographic area
   */
  public void setGeographicArea(Object geographicArea) {
    this.geographicArea = geographicArea;
  }

}
