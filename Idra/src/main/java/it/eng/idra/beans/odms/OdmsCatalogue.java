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
package it.eng.idra.beans.odms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.sparql.SparqlCatalogueConfiguration;
import it.eng.idra.beans.webscraper.WebScraperSitemap;
import it.eng.idra.scheduler.IdraScheduler;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.JsonRequired;
import it.eng.idra.utils.OdmsCatalogueAdditionalConfigurationDeserializer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.StringUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsCatalogue.
 */
// Represents a federated ODMS Node  
@Entity
@Table(name = "odms")
public class OdmsCatalogue {

  /** The id. */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Expose
  private int id;

  /** The name. */
  @JsonRequired
  @Column(name = "name", unique = true, nullable = false) // TODO: levare unique + mettere url-api
  @Expose
  private String name;

  /** The host. */
  @JsonRequired
  @Column(name = "host", unique = true, nullable = false)
  @Expose
  private String host;

  /** The homepage. */
  @JsonRequired
  @Column(name = "homepage", unique = false, nullable = false)
  @Expose
  private String homepage;

  /** The api key. */
  @Column(name = "api_key", unique = false, nullable = true)
  @SerializedName(value = "APIKey")
  private String apiKey;

  /** The node type. */
  @JsonRequired
  @Column(name = "type", unique = false, nullable = false)
  @Enumerated(EnumType.STRING)
  @Expose
  private OdmsCatalogueType nodeType;

  /** The federation level. */
  @JsonRequired
  @Column(name = "federation_level", unique = false, nullable = false)
  @Enumerated(EnumType.STRING)
  private OdmsCatalogueFederationLevel federationLevel;

  /** The publisher name. */
  @JsonRequired
  @Column(name = "publisher_name", unique = false, nullable = false)
  @Expose
  private String publisherName;

  /** The publisher url. */
  @Column(name = "publisher_url", unique = false, nullable = true)
  @Expose
  private String publisherUrl;

  /** The publisher email. */
  @Column(name = "publisher_email", unique = false, nullable = true)
  @Expose
  private String publisherEmail;

  /** The dataset count. */
  @Column(name = "dataset_count")
  @Expose
  private int datasetCount;

  /** The node state. */
  @Column(name = "state", unique = false, nullable = false)
  @Enumerated(EnumType.STRING)
  @Expose
  private OdmsCatalogueState nodeState;

  /** The register date. */
  @Column(name = "register_date", updatable = true)
  private ZonedDateTime registerDate;

  /** The last update date. */
  @Column(name = "last_update_date")
  @Expose
  private ZonedDateTime lastUpdateDate;

  /** The refresh period. */
  @JsonRequired
  @Column(name = "refresh_period")
  private int refreshPeriod;

  /** The description. */
  @Column(name = "description", columnDefinition = "TEXT")
  @Expose
  private String description;

  // @Column(name = "image", columnDefinition = "LONGTEXT")

  /** The image. */
  @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
  @JoinColumn(name = "image_id")
  @Expose
  private OdmsCatalogueImage image;

  /** The synch lock. */
  @Transient
  private OdmsSynchLock synchLock;

  /** The rdf count. */
  @Column(name = "rdfCount")
  private int rdfCount;

  /** The dataset start. */
  @Column(name = "datasetStart")
  private int datasetStart;

  /** The location. */
  @Column(name = "location", columnDefinition = "LONGTEXT")
  @Expose
  private String location;

  /** The location description. */
  @Column(name = "locationDescription", columnDefinition = "MEDIUMTEXT")
  @Expose
  private String locationDescription;

  /** The message count. */
  @Transient
  private Long messageCount;

  /** The sitemap. */
  @OneToOne(orphanRemoval = true, cascade = { CascadeType.ALL, CascadeType.REMOVE })
  @JoinColumn(name = "sitemap_id")
  private WebScraperSitemap sitemap;

  /** The dump string. */
  @Transient
  private String dumpString;

  /** The dump file path. */
  @Column(name = "dumpFilePath", unique = true, nullable = true)
  private String dumpFilePath;

  /** The dump url. */
  @Column(name = "dumpURL", unique = true, nullable = true)
  @SerializedName(value = "dumpURL")
  private String dumpUrl;

  /** The is active. */
  @Column(name = "isActive", nullable = true)
  @Expose
  private Boolean isActive;

  /** The country. */
  @Column(name = "country", nullable = true)
  @Expose
  private String country;

  /** The category. */
  @Column(name = "category", nullable = true)
  @Expose
  private String category;

  /** The additional config. */
  @OneToOne(orphanRemoval = true, cascade = { CascadeType.ALL, CascadeType.REMOVE })
  @JoinColumn(name = "additionalconfig_id")
  @JsonAdapter(OdmsCatalogueAdditionalConfigurationDeserializer.class)
  private OdmsCatalogueAdditionalConfiguration additionalConfig;

  /** The dcat profile. */
  // @Transient
  private DcatApProfile dcatProfile;

  /** The dcat format. */
  @Transient
  private DcatApFormat dcatFormat;

  /**
   * Instantiates a new odms catalogue.
   */
  public OdmsCatalogue() {
    this.setSynchLock(OdmsSynchLock.NONE);
    this.location = "";
    this.locationDescription = "";
  }

  /**
   * Instantiates a new odms catalogue.
   *
   * @param name                the name
   * @param host                the host
   * @param homepage            the homepage
   * @param apiKey              the API key
   * @param nodeType            the node type
   * @param federationLevel     the federation level
   * @param datasetCount        the dataset count
   * @param nodeState           the node state
   * @param registerDate        the register date
   * @param lastUpdateDate      the last update date
   * @param refreshPeriod       the refresh period
   * @param description         the description
   * @param image               the image
   * @param rdfCount            the rdf count
   * @param location            the location
   * @param locationDescription the location description
   */
  // Throws exception if the type of new object is not allowed
  public OdmsCatalogue(String name, String host, String homepage, String apiKey,
      OdmsCatalogueType nodeType, OdmsCatalogueFederationLevel federationLevel, int datasetCount,
      OdmsCatalogueState nodeState, ZonedDateTime registerDate, ZonedDateTime lastUpdateDate,
      int refreshPeriod, String description, String image, int rdfCount, String location,
      String locationDescription) {

    this.setName(name);
    this.setHost(host);
    this.setHomepage(homepage);
    this.setApiKey(apiKey);
    this.setNodeType(nodeType);
    this.setFederationLevel(federationLevel);
    this.setDatasetCount(datasetCount);
    this.setNodeState(nodeState);
    this.setRegisterDate(registerDate);
    this.setLastUpdateDate(lastUpdateDate);
    this.setRefreshPeriod(refreshPeriod);
    this.setDescription(description);
    this.setImage(new OdmsCatalogueImage(image));
    this.setSynchLock(OdmsSynchLock.NONE);
    this.setRdfCount(rdfCount);
    this.setDatasetStart(0);
    this.setLocation(location);
    this.setLocationDescription(locationDescription);
  }

  /**
   * Instantiates a new odms catalogue.
   *
   * @param id                  the id
   * @param name                the name
   * @param host                the host
   * @param homepage            the homepage
   * @param apiKey              the API key
   * @param nodeType            the node type
   * @param integrationLevel    the integration level
   * @param datasetCount        the dataset count
   * @param nodeState           the node state
   * @param registerDate        the register date
   * @param lastUpdateDate      the last update date
   * @param refreshPeriod       the refresh period
   * @param description         the description
   * @param image               the image
   * @param rdfCount            the rdf count
   * @param location            the location
   * @param locationDescription the location description
   */
  // Throws exception if the type of new object is not allowed
  public OdmsCatalogue(int id, String name, String host, String homepage, String apiKey,
      OdmsCatalogueType nodeType, OdmsCatalogueFederationLevel integrationLevel, int datasetCount,
      OdmsCatalogueState nodeState, ZonedDateTime registerDate, ZonedDateTime lastUpdateDate,
      int refreshPeriod, String description, String image, int rdfCount, String location,
      String locationDescription) {

    this.setId(id);
    this.setName(name);
    this.setHost(host);
    this.setHomepage(homepage);
    this.setApiKey(apiKey);
    this.setNodeType(nodeType);
    this.setFederationLevel(integrationLevel);
    this.setDatasetCount(datasetCount);
    this.setNodeState(nodeState);
    this.setRegisterDate(registerDate);
    this.setLastUpdateDate(lastUpdateDate);
    this.setRefreshPeriod(refreshPeriod);
    this.setDescription(description);
    this.setImage(new OdmsCatalogueImage(image));
    this.setSynchLock(OdmsSynchLock.NONE);
    this.setRdfCount(rdfCount);
    this.setDatasetStart(0);
    this.setLocation(location);
    this.setLocationDescription(locationDescription);
  }

  /**
   * Instantiates a new odms catalogue.
   *
   * @param id                  the id
   * @param name                the name
   * @param host                the host
   * @param homepage            the homepage
   * @param apiKey              the API key
   * @param nodeType            the node type
   * @param integrationLevel    the integration level
   * @param datasetCount        the dataset count
   * @param nodeState           the node state
   * @param registerDate        the register date
   * @param lastUpdateDate      the last update date
   * @param refreshPeriod       the refresh period
   * @param description         the description
   * @param image               the image
   * @param rdfCount            the rdf count
   * @param startDataset        the start dataset
   * @param location            the location
   * @param locationDescription the location description
   */
  public OdmsCatalogue(int id, String name, String host, String homepage, String apiKey,
      OdmsCatalogueType nodeType, OdmsCatalogueFederationLevel integrationLevel, int datasetCount,
      OdmsCatalogueState nodeState, ZonedDateTime registerDate, ZonedDateTime lastUpdateDate,
      int refreshPeriod, String description, String image, int rdfCount, int startDataset,
      String location, String locationDescription) {

    this.setId(id);
    this.setName(name);
    this.setHost(host);
    this.setHomepage(homepage);
    this.setApiKey(apiKey);
    this.setNodeType(nodeType);
    this.setFederationLevel(integrationLevel);
    this.setDatasetCount(datasetCount);
    this.setNodeState(nodeState);
    this.setRegisterDate(registerDate);
    this.setLastUpdateDate(lastUpdateDate);
    this.setRefreshPeriod(refreshPeriod);
    this.setDescription(description);
    this.setImage(new OdmsCatalogueImage(image));
    this.setSynchLock(OdmsSynchLock.NONE);
    this.setRdfCount(rdfCount);
    this.setDatasetStart(startDataset);
    this.setLocation(location);
    this.setLocationDescription(locationDescription);
  }

  /**
   * Instantiates a new odms catalogue.
   *
   * @param id the id
   */
  public OdmsCatalogue(int id) {
    this.setId(id);
  }

  /**
   * Gets the dataset start.
   *
   * @return the dataset start
   */
  public int getDatasetStart() {
    return datasetStart;
  }

  /**
   * Sets the dataset start.
   *
   * @param i the new dataset start
   */
  public void setDatasetStart(int i) {
    this.datasetStart = i;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the host.
   *
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets the host.
   *
   * @param host the new host
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * Gets the homepage.
   *
   * @return the homepage
   */
  public String getHomepage() {
    return homepage;
  }

  /**
   * Sets the homepage.
   *
   * @param homepage the new homepage
   */
  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  /**
   * Gets the api key.
   *
   * @return the api key
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * Sets the api key.
   *
   * @param apiKey the new api key
   */
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Gets the publisher name.
   *
   * @return the publisher name
   */
  public String getPublisherName() {
    return publisherName;
  }

  /**
   * Sets the publisher name.
   *
   * @param publisherName the new publisher name
   */
  public void setPublisherName(String publisherName) {
    this.publisherName = publisherName;
  }

  /**
   * Gets the publisher url.
   *
   * @return the publisher url
   */
  public String getPublisherUrl() {
    return publisherUrl;
  }

  /**
   * Sets the publisher url.
   *
   * @param publisherUrl the new publisher url
   */
  public void setPublisherUrl(String publisherUrl) {
    this.publisherUrl = publisherUrl;
  }

  /**
   * Gets the publisher email.
   *
   * @return the publisher email
   */
  public String getPublisherEmail() {
    return publisherEmail;
  }

  /**
   * Sets the publisher email.
   *
   * @param publisherEmail the new publisher email
   */
  public void setPublisherEmail(String publisherEmail) {
    this.publisherEmail = publisherEmail;
  }

  /**
   * Gets the dataset count.
   *
   * @return the dataset count
   */
  public int getDatasetCount() {
    return datasetCount;
  }

  /**
   * Sets the dataset count.
   *
   * @param datasetCount the new dataset count
   */
  public void setDatasetCount(int datasetCount) {
    this.datasetCount = datasetCount;
  }

  /**
   * Gets the rdf count.
   *
   * @return the rdf count
   */
  public int getRdfCount() {
    return rdfCount;
  }

  /**
   * Sets the rdf count.
   *
   * @param rdfCount the new rdf count
   */
  public void setRdfCount(int rdfCount) {

    this.rdfCount = rdfCount;
  }

  /**
   * Gets the register date.
   *
   * @return the register date
   */
  public ZonedDateTime getRegisterDate() {
    return registerDate;
  }

  /**
   * Sets the register date.
   *
   * @param registerDate the new register date
   */
  public void setRegisterDate(ZonedDateTime registerDate) {
    this.registerDate = registerDate;
  }

  /**
   * Gets the node type.
   *
   * @return the node type
   */
  public OdmsCatalogueType getNodeType() {
    return nodeType;
  }

  /**
   * Sets the node type.
   *
   * @param nodeType the new node type
   */
  public void setNodeType(OdmsCatalogueType nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * Gets the last update date.
   *
   * @return the last update date
   */
  public ZonedDateTime getLastUpdateDate() {
    return lastUpdateDate;
  }

  /**
   * Sets the last update date.
   *
   * @param lastUpdateDate the new last update date
   */
  public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
  }

  /**
   * Gets the federation level.
   *
   * @return the federation level
   */
  public OdmsCatalogueFederationLevel getFederationLevel() {
    return federationLevel;
  }

  /**
   * Sets the federation level.
   *
   * @param integrationLevel the new federation level
   */
  public void setFederationLevel(OdmsCatalogueFederationLevel integrationLevel) {
    this.federationLevel = integrationLevel;
  }

  /**
   * Gets the node state.
   *
   * @return the node state
   */
  public OdmsCatalogueState getNodeState() {
    return nodeState;
  }

  /**
   * Sets the node state.
   *
   * @param nodeState the new node state
   */
  public void setNodeState(OdmsCatalogueState nodeState) {
    this.nodeState = nodeState;
  }

  /**
   * Gets the refresh period.
   *
   * @return the refresh period
   */
  public int getRefreshPeriod() {
    return refreshPeriod;
  }

  /**
   * Sets the refresh period.
   *
   * @param refreshPeriod the new refresh period
   */
  public void setRefreshPeriod(int refreshPeriod) {
    this.refreshPeriod = refreshPeriod;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Checks if is active.
   *
   * @return the boolean
   */
  public Boolean isActive() {
    return isActive;
  }

  /**
   * Sets the active.
   *
   * @param isActive the new active
   */
  public void setActive(Boolean isActive) {
    this.isActive = isActive;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
    result = prime * result + ((registerDate == null) ? 0 : registerDate.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    OdmsCatalogue other = (OdmsCatalogue) obj;
    if (id != other.id) {
      if (nodeType != other.getNodeType() || !host.equals(other.getHost())) {
        return false;
      }
    }
    /*
     * if (registerDate == null) { if (other.registerDate != null) return false; }
     * else if (!registerDate.equals(other.registerDate)) return false;
     */
    return true;
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
   * Gets the image.
   *
   * @return the image
   */
  public OdmsCatalogueImage getImage() {
    return image;
  }

  /**
   * Sets the image.
   *
   * @param image the new image
   */
  public void setImage(OdmsCatalogueImage image) {
    this.image = image;
  }

  /**
   * Gets the synch lock.
   *
   * @return the synch lock
   */
  public OdmsSynchLock getSynchLock() {
    return synchLock;
  }

  /**
   * Sets the synch lock.
   *
   * @param synchLock the new synch lock
   */
  public void setSynchLock(OdmsSynchLock synchLock) {
    this.synchLock = synchLock;
  }

  /**
   * Gets the location.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the location.
   *
   * @param location the new location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Gets the location description.
   *
   * @return the location description
   */
  public String getLocationDescription() {
    return locationDescription;
  }

  /**
   * Sets the location description.
   *
   * @param locationDescription the new location description
   */
  public void setLocationDescription(String locationDescription) {
    this.locationDescription = locationDescription;
  }

  /**
   * Gets the message count.
   *
   * @return the message count
   */
  public Long getMessageCount() {
    return messageCount;
  }

  /**
   * Sets the message count.
   *
   * @param messageCount the new message count
   */
  public void setMessageCount(Long messageCount) {
    this.messageCount = messageCount;
  }

  /**
   * Gets the sitemap.
   *
   * @return the sitemap
   */
  public WebScraperSitemap getSitemap() {
    return sitemap;
  }

  /**
   * Sets the sitemap.
   *
   * @param sitemap the new sitemap
   */
  public void setSitemap(WebScraperSitemap sitemap) {
    this.sitemap = sitemap;
  }

  /**
   * Gets the dump string.
   *
   * @return the dump string
   */
  public String getDumpString() {
    return dumpString;
  }

  /**
   * Sets the dump string.
   *
   * @param dumpString the new dump string
   */
  public void setDumpString(String dumpString) {
    this.dumpString = dumpString;
  }

  /**
   * Gets the dump url.
   *
   * @return the dump url
   */
  public String getDumpUrl() {
    return dumpUrl;
  }

  /**
   * Sets the dump url.
   *
   * @param dumpUrl the new dump url
   */
  public void setDumpUrl(String dumpUrl) {
    this.dumpUrl = dumpUrl;
  }

  /**
   * Gets the dump file path.
   *
   * @return the dump file path
   */
  public String getDumpFilePath() {
    return dumpFilePath;
  }

  /**
   * Sets the dump file path.
   *
   * @param dumpFilePath the new dump file path
   */
  public void setDumpFilePath(String dumpFilePath) {
    this.dumpFilePath = dumpFilePath;
  }

  /**
   * Gets the dcat profile.
   *
   * @return the dcat profile
   */
  public DcatApProfile getDcatProfile() {
    return dcatProfile;
  }

  /**
   * Sets the dcat profile.
   *
   * @param profile the new dcat profile
   */
  public void setDcatProfile(DcatApProfile profile) {
    this.dcatProfile = profile;
  }

  /**
   * Gets the dcat format.
   *
   * @return the dcat format
   */
  public DcatApFormat getDcatFormat() {
    return dcatFormat;
  }

  /**
   * Sets the dcat format.
   *
   * @param dcatFormat the new dcat format
   */
  public void setDcatFormat(DcatApFormat dcatFormat) {
    this.dcatFormat = dcatFormat;
  }

  /**
   * Gets the country.
   *
   * @return the country
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country the new country
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Gets the category.
   *
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the category.
   *
   * @param category the new category
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Gets the additional config.
   *
   * @return the additional config
   */
  public OdmsCatalogueAdditionalConfiguration getAdditionalConfig() {
    return additionalConfig;
  }

  /**
   * Sets the additional config.
   *
   * @param orionConfig the new additional config
   */
  public void setAdditionalConfig(OdmsCatalogueAdditionalConfiguration orionConfig) {
    this.additionalConfig = orionConfig;
  }

  /**
   * Checks if is online.
   *
   * @return true, if is online
   */
  public boolean isOnline() {
    return this.getNodeState().equals(OdmsCatalogueState.ONLINE);
  }

  /**
   * Checks if is offline.
   *
   * @return true, if is offline
   */
  public boolean isOffline() {
    return this.getNodeState().equals(OdmsCatalogueState.OFFLINE);
  }

  /**
   * Checks if is federating.
   *
   * @return true, if is federating
   */
  public boolean isFederating() {
    return this.synchLock.equals(OdmsSynchLock.FIRST);
  }

  /**
   * Checks if is synching.
   *
   * @return true, if is synching
   */
  public boolean isSynching() {
    return this.synchLock.equals(OdmsSynchLock.PERIODIC);
  }

  /**
   * Checks if is unlocked.
   *
   * @return true, if is unlocked
   */
  public boolean isUnlocked() {
    return this.synchLock.equals(OdmsSynchLock.NONE);
  }

  /**
   * Checks if is cacheable.
   *
   * @return true, if is cacheable
   */
  public boolean isCacheable() {
    return federationLevel.equals(OdmsCatalogueFederationLevel.LEVEL_2)
        || federationLevel.equals(OdmsCatalogueFederationLevel.LEVEL_3)
        || federationLevel.equals(OdmsCatalogueFederationLevel.LEVEL_4);
  }

  /**
   * On create.
   */
  @PrePersist
  protected void onCreate() {
    lastUpdateDate = registerDate = ZonedDateTime.now(ZoneOffset.UTC);
  }

  /**
   * On update.
   */
  @PreUpdate
  protected void onUpdate() {
    lastUpdateDate = ZonedDateTime.now(ZoneOffset.UTC);
  }

  /**
   * Post create.
   */
  @PostPersist
  protected void postCreate() {
    if (this.nodeType.equals(OdmsCatalogueType.ORION)) {
      OrionCatalogueConfiguration cf = (OrionCatalogueConfiguration) this.additionalConfig;
      if (cf.isAuthenticated()) {
        try {
          IdraScheduler.getSingletonInstance().startOauthTokenSynchJob(this);
        } catch (SchedulerNotInitialisedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * Post update.
   */
  @PostUpdate
  protected void postUpdate() {

  }

  /**
   * Post delete.
   */
  @PostRemove
  protected void postDelete() {
    if (this.nodeType.equals(OdmsCatalogueType.ORION)) {
      OrionCatalogueConfiguration cf = (OrionCatalogueConfiguration) this.additionalConfig;
      if (cf.isAuthenticated()) {
        try {
          IdraScheduler.getSingletonInstance().deleteJob("synchToken_" + Integer.toString(this.id));
        } catch (SchedulerNotInitialisedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      if (StringUtils.isNotBlank(cf.getOrionDatasetFilePath())) {
        CommonUtil.deleteFile(cf.getOrionDatasetFilePath());
      }

    } else if (this.nodeType.equals(OdmsCatalogueType.SPARQL)) {
      SparqlCatalogueConfiguration cf = (SparqlCatalogueConfiguration) this.additionalConfig;
      if (StringUtils.isNotBlank(cf.getSparqlDatasetFilePath())) {
        CommonUtil.deleteFile(cf.getSparqlDatasetFilePath());
      }
    } else if (this.nodeType.equals(OdmsCatalogueType.DCATDUMP)) {
      if (StringUtils.isNotBlank(this.dumpFilePath)) {
        CommonUtil.deleteFile(this.dumpFilePath);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ODMSCatalogue [id=" + id + ", name=" + name + ", synchLock=" + synchLock + ", host="
        + host + ", nodeType=" + nodeType + ", federationLevel=" + federationLevel + ", nodeState="
        + nodeState + ", datasetStart=" + datasetStart + "messageCount=" + messageCount + "]";
  }

}
