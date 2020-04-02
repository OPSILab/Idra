/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.eng.idra.beans.odms;

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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.sparql.SparqlCatalogueConfiguration;
import it.eng.idra.beans.webscraper.WebScraperSitemap;
import it.eng.idra.scheduler.IdraScheduler;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.JsonRequired;
import it.eng.idra.utils.ODMSCatalogueAdditionalConfigurationDeserializer;

// Represents a federated ODMS Node  
@Entity
@Table(name = "odms")
public class ODMSCatalogue {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	private int id;

	@JsonRequired
	@Column(name = "name", unique = true, nullable = false) //TODO: levare unique + mettere url-api
	@Expose
	private String name;

	@JsonRequired
	@Column(name = "host", unique = true, nullable = false)
	@Expose
	private String host;
	
	@JsonRequired
	@Column(name = "homepage", unique = false, nullable = false)
	@Expose
	private String homepage;

	@Column(name = "api_key", unique = false, nullable = true)
	private String APIKey;

	@JsonRequired
	@Column(name = "type", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	@Expose
	private ODMSCatalogueType nodeType;

	@JsonRequired
	@Column(name = "federation_level", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private ODMSCatalogueFederationLevel federationLevel;

	@JsonRequired
	@Column(name = "publisher_name", unique = false, nullable = false)
	@Expose
	private String publisherName;

	@Column(name = "publisher_url", unique = false, nullable = true)
	@Expose
	private String publisherUrl;

	@Column(name = "publisher_email", unique = false, nullable = true)
	@Expose
	private String publisherEmail;

	@Column(name = "dataset_count")
	@Expose
	private int datasetCount;

	@Column(name = "state", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	@Expose
	private ODMSCatalogueState nodeState;

	@Column(name = "register_date", updatable = true)
	private ZonedDateTime registerDate;

	@Column(name = "last_update_date")
	@Expose
	private ZonedDateTime lastUpdateDate;

	@JsonRequired
	@Column(name = "refresh_period")
	private int refreshPeriod;

	@Column(name = "description", columnDefinition = "TEXT")
	@Expose
	private String description;

	// @Column(name = "image", columnDefinition = "LONGTEXT")

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "image_id")
	@Expose
	private ODMSCatalogueImage image;

	@Transient
	private ODMSSynchLock synchLock;

	@Column(name = "rdfCount")
	private int rdfCount;

	@Column(name = "datasetStart")
	private int datasetStart;

	@Column(name = "location", columnDefinition = "LONGTEXT")
	@Expose
	private String location;

	@Column(name = "locationDescription", columnDefinition = "MEDIUMTEXT")
	@Expose
	private String locationDescription;

	@Transient
	private Long messageCount;

	@OneToOne(orphanRemoval = true, cascade = { CascadeType.ALL, CascadeType.REMOVE })
	@JoinColumn(name = "sitemap_id")
	private WebScraperSitemap sitemap;

	@Transient
	private String dumpString;

	@Column(name = "dumpFilePath", unique = true, nullable = true)
	private String dumpFilePath;

	@Column(name = "dumpURL", unique = true, nullable = true)
	private String dumpURL;

	@Column(name = "isActive", nullable = true)
	@Expose
	private Boolean isActive;

	@Column(name = "country", nullable = true)
	@Expose
	private String country;

	@Column(name = "category", nullable = true)
	@Expose
	private String category;

	@OneToOne(orphanRemoval = true, cascade = { CascadeType.ALL, CascadeType.REMOVE })
	@JoinColumn(name = "additionalconfig_id")
	@JsonAdapter(ODMSCatalogueAdditionalConfigurationDeserializer.class)
	private ODMSCatalogueAdditionalConfiguration additionalConfig;
	
	// @Transient
	private DCATAPProfile dcatProfile;

	@Transient
	private DCATAPFormat dcatFormat;
	
	
	public ODMSCatalogue() {
		this.setSynchLock(ODMSSynchLock.NONE);
		this.location = "";
		this.locationDescription = "";
	}

	// Throws exception if the type of new object is not allowed
	public ODMSCatalogue(String name, String host,String homepage, String APIKey, ODMSCatalogueType nodeType,
			ODMSCatalogueFederationLevel federationLevel, int datasetCount, ODMSCatalogueState nodeState,
			ZonedDateTime registerDate, ZonedDateTime lastUpdateDate, int refreshPeriod, String description,
			String image, int rdfCount, String location, String locationDescription) {

		this.setName(name);
		this.setHost(host);
		this.setHomepage(homepage);
		this.setAPIKey(APIKey);
		this.setNodeType(nodeType);
		this.setFederationLevel(federationLevel);
		this.setDatasetCount(datasetCount);
		this.setNodeState(nodeState);
		this.setRegisterDate(registerDate);
		this.setLastUpdateDate(lastUpdateDate);
		this.setRefreshPeriod(refreshPeriod);
		this.setDescription(description);
		this.setImage(new ODMSCatalogueImage(image));
		this.setSynchLock(ODMSSynchLock.NONE);
		this.setRdfCount(rdfCount);
		this.setDatasetStart(0);
		this.setLocation(location);
		this.setLocationDescription(locationDescription);
	}

	// Throws exception if the type of new object is not allowed
	public ODMSCatalogue(int id, String name, String host,String homepage, String APIKey, ODMSCatalogueType nodeType,
			ODMSCatalogueFederationLevel integrationLevel, int datasetCount, ODMSCatalogueState nodeState,
			ZonedDateTime registerDate, ZonedDateTime lastUpdateDate, int refresh_period, String description,
			String image, int rdfCount, String location, String locationDescription) {

		this.setId(id);
		this.setName(name);
		this.setHost(host);
		this.setHomepage(homepage);
		this.setAPIKey(APIKey);
		this.setNodeType(nodeType);
		this.setFederationLevel(integrationLevel);
		this.setDatasetCount(datasetCount);
		this.setNodeState(nodeState);
		this.setRegisterDate(registerDate);
		this.setLastUpdateDate(lastUpdateDate);
		this.setRefreshPeriod(refresh_period);
		this.setDescription(description);
		this.setImage(new ODMSCatalogueImage(image));
		this.setSynchLock(ODMSSynchLock.NONE);
		this.setRdfCount(rdfCount);
		this.setDatasetStart(0);
		this.setLocation(location);
		this.setLocationDescription(locationDescription);
	}

	public ODMSCatalogue(int id, String name, String host,String homepage, String APIKey, ODMSCatalogueType nodeType,
			ODMSCatalogueFederationLevel integrationLevel, int datasetCount, ODMSCatalogueState nodeState,
			ZonedDateTime registerDate, ZonedDateTime lastUpdateDate, int refresh_period, String description,
			String image, int rdfCount, int startDataset, String location, String locationDescription) {

		this.setId(id);
		this.setName(name);
		this.setHost(host);
		this.setHomepage(homepage);
		this.setAPIKey(APIKey);
		this.setNodeType(nodeType);
		this.setFederationLevel(integrationLevel);
		this.setDatasetCount(datasetCount);
		this.setNodeState(nodeState);
		this.setRegisterDate(registerDate);
		this.setLastUpdateDate(lastUpdateDate);
		this.setRefreshPeriod(refresh_period);
		this.setDescription(description);
		this.setImage(new ODMSCatalogueImage(image));
		this.setSynchLock(ODMSSynchLock.NONE);
		this.setRdfCount(rdfCount);
		this.setDatasetStart(startDataset);
		this.setLocation(location);
		this.setLocationDescription(locationDescription);
	}

	public ODMSCatalogue(int id) {
		this.setId(id);
	}

	public int getDatasetStart() {
		return datasetStart;
	}

	public void setDatasetStart(int i) {
		this.datasetStart = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getAPIKey() {
		return APIKey;
	}

	public void setAPIKey(String aPIKey) {
		APIKey = aPIKey;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPublisherUrl() {
		return publisherUrl;
	}

	public void setPublisherUrl(String publisherUrl) {
		this.publisherUrl = publisherUrl;
	}

	public String getPublisherEmail() {
		return publisherEmail;
	}

	public void setPublisherEmail(String publisherEmail) {
		this.publisherEmail = publisherEmail;
	}

	public int getDatasetCount() {
		return datasetCount;
	}

	public void setDatasetCount(int datasetCount) {
		this.datasetCount = datasetCount;
	}

	public int getRdfCount() {
		return rdfCount;
	}

	public void setRdfCount(int rdfCount) {

		this.rdfCount = rdfCount;
	}

	public ZonedDateTime getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(ZonedDateTime registerDate) {
		this.registerDate = registerDate;
	}

	public ODMSCatalogueType getNodeType() {
		return nodeType;
	}

	public void setNodeType(ODMSCatalogueType nodeType) {
		this.nodeType = nodeType;
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public ODMSCatalogueFederationLevel getFederationLevel() {
		return federationLevel;
	}

	public void setFederationLevel(ODMSCatalogueFederationLevel integrationLevel) {
		this.federationLevel = integrationLevel;
	}

	public ODMSCatalogueState getNodeState() {
		return nodeState;
	}

	public void setNodeState(ODMSCatalogueState nodeState) {
		this.nodeState = nodeState;
	}

	public int getRefreshPeriod() {
		return refreshPeriod;
	}

	public void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
		result = prime * result + ((registerDate == null) ? 0 : registerDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ODMSCatalogue other = (ODMSCatalogue) obj;
		if (id != other.id)
			if (nodeType != other.getNodeType() || !host.equals(other.getHost()))
				return false;
		/*
		 * if (registerDate == null) { if (other.registerDate != null) return false; }
		 * else if (!registerDate.equals(other.registerDate)) return false;
		 */
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ODMSCatalogueImage getImage() {
		return image;
	}

	public void setImage(ODMSCatalogueImage image) {
		this.image = image;
	}

	public ODMSSynchLock getSynchLock() {
		return synchLock;
	}

	public void setSynchLock(ODMSSynchLock synchLock) {
		this.synchLock = synchLock;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}

	public Long getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(Long messageCount) {
		this.messageCount = messageCount;
	}

	public WebScraperSitemap getSitemap() {
		return sitemap;
	}

	public void setSitemap(WebScraperSitemap sitemap) {
		this.sitemap = sitemap;
	}

	public String getDumpString() {
		return dumpString;
	}

	public void setDumpString(String dumpString) {
		this.dumpString = dumpString;
	}

	public String getDumpURL() {
		return dumpURL;
	}

	public void setDumpURL(String dumpURL) {
		this.dumpURL = dumpURL;
	}

	public String getDumpFilePath() {
		return dumpFilePath;
	}

	public void setDumpFilePath(String dumpFilePath) {
		this.dumpFilePath = dumpFilePath;
	}

	public DCATAPProfile getDCATProfile() {
		return dcatProfile;
	}

	public void setDCATProfile(DCATAPProfile profile) {
		this.dcatProfile = profile;
	}

	public DCATAPFormat getDcatFormat() {
		return dcatFormat;
	}

	public void setDcatFormat(DCATAPFormat dcatFormat) {
		this.dcatFormat = dcatFormat;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ODMSCatalogueAdditionalConfiguration getAdditionalConfig() {
		return additionalConfig;
	}

	public void setAdditionalConfig(ODMSCatalogueAdditionalConfiguration orionConfig) {
		this.additionalConfig = orionConfig;
	}

	public boolean isOnline() {
		return this.getNodeState().equals(ODMSCatalogueState.ONLINE);
	}

	public boolean isOffline() {
		return this.getNodeState().equals(ODMSCatalogueState.OFFLINE);
	}

	public boolean isFederating() {
		return this.synchLock.equals(ODMSSynchLock.FIRST);
	}

	public boolean isSynching() {
		return this.synchLock.equals(ODMSSynchLock.PERIODIC);
	}

	public boolean isUnlocked() {
		return this.synchLock.equals(ODMSSynchLock.NONE);
	}

	public boolean isCacheable() {
		return federationLevel.equals(ODMSCatalogueFederationLevel.LEVEL_2)
				|| federationLevel.equals(ODMSCatalogueFederationLevel.LEVEL_3) 
				|| federationLevel.equals(ODMSCatalogueFederationLevel.LEVEL_4);
	}

	@PrePersist
	protected void onCreate() {
		lastUpdateDate = registerDate = ZonedDateTime.now(ZoneOffset.UTC);
	}

	@PreUpdate
	protected void onUpdate() {
		lastUpdateDate = ZonedDateTime.now(ZoneOffset.UTC);
	}

	@PostPersist
	protected void postCreate() {
		if(this.nodeType.equals(ODMSCatalogueType.ORION)) {
			OrionCatalogueConfiguration cf = (OrionCatalogueConfiguration) this.additionalConfig;
			if(cf.isAuthenticated()) {
				try {
					IdraScheduler.getSingletonInstance().startOAUTHTokenSynchJob(this);
				} catch (SchedulerNotInitialisedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		
		
	}

	@PostUpdate
	protected void postUpdate() {
		
	}
	
	@PostRemove
	protected void postDelete() {
		if(this.nodeType.equals(ODMSCatalogueType.ORION)) {
			OrionCatalogueConfiguration cf = (OrionCatalogueConfiguration) this.additionalConfig;
			if(cf.isAuthenticated()) {
				try {
					IdraScheduler.getSingletonInstance().deleteJob("synchToken_"+Integer.toString(this.id));
				} catch (SchedulerNotInitialisedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(StringUtils.isNotBlank(cf.getOrionDatasetFilePath())) {
				CommonUtil.deleteFile(cf.getOrionDatasetFilePath());
			}
			
		}else if(this.nodeType.equals(ODMSCatalogueType.SPARQL)) {
			SparqlCatalogueConfiguration cf = (SparqlCatalogueConfiguration) this.additionalConfig;
			if(StringUtils.isNotBlank(cf.getSparqlDatasetFilePath())) {
				CommonUtil.deleteFile(cf.getSparqlDatasetFilePath());
			}
		}else if(this.nodeType.equals(ODMSCatalogueType.DCATDUMP)) {
			if(StringUtils.isNotBlank(this.dumpFilePath)) {
				CommonUtil.deleteFile(this.dumpFilePath);
			}
		}
	}
	
	@Override
	public String toString() {
		return "ODMSCatalogue [id=" + id + ", name=" + name + ", synchLock=" + synchLock + ", host=" + host + ", nodeType="
				+ nodeType + ", federationLevel=" + federationLevel + ", nodeState=" + nodeState + ", datasetStart="
				+ datasetStart + "messageCount=" + messageCount + "]";
	}

}
