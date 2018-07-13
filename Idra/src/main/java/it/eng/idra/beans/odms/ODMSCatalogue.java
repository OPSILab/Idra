/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.webscraper.WebScraperSitemap;
import it.eng.idra.utils.JsonRequired;

// Represents a federated ODMS Node  
@Entity
@Table(name = "odms")
public class ODMSCatalogue {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@JsonRequired
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@JsonRequired
	@Column(name = "host", unique = true, nullable = false)
	private String host;

	@Column(name = "api_key", unique = false, nullable = true)
	private String APIKey;

	@JsonRequired
	@Column(name = "type", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private ODMSCatalogueType nodeType;

	@JsonRequired
	@Column(name = "federation_level", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private ODMSCatalogueFederationLevel federationLevel;

	@JsonRequired
	@Column(name = "publisher_name", unique = false, nullable = false)
	private String publisherName;

	@Column(name = "publisher_url", unique = false, nullable = true)
	private String publisherUrl;

	@Column(name = "publisher_email", unique = false, nullable = true)
	private String publisherEmail;

	@Column(name = "dataset_count")
	private int datasetCount;

	@Column(name = "state", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private ODMSCatalogueState nodeState;

	@Column(name = "register_date", updatable = true)
	// @Temporal(TemporalType.TIMESTAMP)
	private ZonedDateTime registerDate;

	@Column(name = "last_update_date")
	// @Temporal(TemporalType.TIMESTAMP)
	private ZonedDateTime lastUpdateDate;

	@JsonRequired
	@Column(name = "refresh_period")
	private int refreshPeriod;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	// @Column(name = "image", columnDefinition = "LONGTEXT")

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "image_id")
	private ODMSCatalogueImage image;

	@Transient
	private ODMSSynchLock synchLock;

	@Column(name = "rdfCount")
	private int rdfCount;

	@Column(name = "datasetStart")
	private int datasetStart;

	@Column(name = "location", columnDefinition = "LONGTEXT")
	private String location;

	@Column(name = "locationDescription", columnDefinition = "MEDIUMTEXT")
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
	private Boolean isActive;

	@Column(name = "country", nullable = true)
	private String country;

	@Column(name = "category", nullable = true)
	private String category;

	// @Transient
	private DCATAPProfile dcatProfile;

	public ODMSCatalogue() {
		this.setSynchLock(ODMSSynchLock.NONE);
		this.location = "";
		this.locationDescription = "";
	}

	// Throws exception if the type of new object is not allowed
	public ODMSCatalogue(String name, String host, String APIKey, ODMSCatalogueType nodeType,
			ODMSCatalogueFederationLevel federationLevel, int datasetCount, ODMSCatalogueState nodeState,
			ZonedDateTime registerDate, ZonedDateTime lastUpdateDate, int refreshPeriod, String description,
			String image, int rdfCount, String location, String locationDescription) {

		this.setName(name);
		this.setHost(host);
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
	public ODMSCatalogue(int id, String name, String host, String APIKey, ODMSCatalogueType nodeType,
			ODMSCatalogueFederationLevel integrationLevel, int datasetCount, ODMSCatalogueState nodeState,
			ZonedDateTime registerDate, ZonedDateTime lastUpdateDate, int refresh_period, String description,
			String image, int rdfCount, String location, String locationDescription) {

		this.setId(id);
		this.setName(name);
		this.setHost(host);
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

	public ODMSCatalogue(int id, String name, String host, String APIKey, ODMSCatalogueType nodeType,
			ODMSCatalogueFederationLevel integrationLevel, int datasetCount, ODMSCatalogueState nodeState,
			ZonedDateTime registerDate, ZonedDateTime lastUpdateDate, int refresh_period, String description,
			String image, int rdfCount, int startDataset, String location, String locationDescription) {

		this.setId(id);
		this.setName(name);
		this.setHost(host);
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
				|| federationLevel.equals(ODMSCatalogueFederationLevel.LEVEL_3);
	}

	@PrePersist
	protected void onCreate() {
		lastUpdateDate = registerDate = ZonedDateTime.now(ZoneOffset.UTC);
	}

	@PreUpdate
	protected void onUpdate() {
		lastUpdateDate = ZonedDateTime.now(ZoneOffset.UTC);
	}

	@Override
	public String toString() {
		return "ODMSCatalogue [id=" + id + ", name=" + name + ", synchLock=" + synchLock + ", host=" + host + ", nodeType="
				+ nodeType + ", federationLevel=" + federationLevel + ", nodeState=" + nodeState + ", datasetStart="
				+ datasetStart + "messageCount=" + messageCount + "]";
	}

}
