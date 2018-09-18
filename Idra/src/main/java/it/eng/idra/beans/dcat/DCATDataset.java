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
package it.eng.idra.beans.dcat;

import it.eng.idra.cache.CacheContentType;
import it.eng.idra.management.FederationCore;
import it.eng.idra.utils.CommonUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

/**
 * Represents a DCAT Dataset
 *
 * @author
 * @version 1.2
 * @since
 */

@Entity
@Table(name = "dcat_dataset")
@IdClass(DCATDatasetId.class)
public class DCATDataset implements Serializable {

	private static final long serialVersionUID = 1L;

	// Custom fields
	private String id;
	private String nodeID;
	private boolean hasStoredRDF = false;
	private transient static final Resource RDFClass = DCAT.Dataset;

	// DCAT fields

	// Mandatory
	private DCATProperty title;
	private DCATProperty description;

	// Recommended
	private List<DCATDistribution> distributions;
	private List<SKOSConceptTheme> theme;
	private FOAFAgent publisher;
	private List<VCardOrganization> contactPoint;
	private List<String> keywords;

	// Optional
	private DCATProperty accessRights;
	private List<DCTStandard> conformsTo;
	private List<DCATProperty> documentation;
	private DCATProperty frequency;
	private List<DCATProperty> hasVersion;
	private List<DCATProperty> isVersionOf;
	private DCATProperty landingPage;
	private List<DCATProperty> language;
	private List<DCATProperty> provenance;
	// private DCATProperty relatedResource ?
	private DCATProperty releaseDate;
	private DCATProperty updateDate;
	private DCATProperty identifier;
	private List<DCATProperty> otherIdentifier;
	private List<DCATProperty> sample;
	private List<DCATProperty> source;
	private DCTLocation spatialCoverage;
	private DCTPeriodOfTime temporalCoverage;
	private DCATProperty type;
	private DCATProperty version;
	private List<DCATProperty> versionNotes;

	private FOAFAgent rightsHolder;
	private FOAFAgent creator;
	private List<SKOSConceptSubject> subject;

	private String legacyIdentifier;
	private String seoIdentifier;
	// TODO To be Removed
	// OLD: According to DCAT Specs, the license is at Distribution or Catalog
	// level
	// private DCATProperty licenseTitle;

	public DCATDataset() {
	}

	public DCATDataset(String nodeID, String title, String description, List<DCATDistribution> distributions,
			List<SKOSConceptTheme> theme, FOAFAgent publisher, List<VCardOrganization> contactPoint, List<String> keywords,
			String accessRights, List<DCTStandard> conformsTo, List<String> documentation, String frequency,
			List<String> hasVersion, List<String> isVersionOf, String landingPage, List<String> language,
			List<String> provenance, String releaseDate, String updateDate, String identifier,
			List<String> otherIdentifier, List<String> sample, List<String> source, DCTLocation spatialCoverage,
			DCTPeriodOfTime temporalCoverage, String type, String version, List<String> versionNotes,
			FOAFAgent rightsHolder, FOAFAgent creator, List<SKOSConceptSubject> subject) {

		super();
		 setId(UUID.randomUUID().toString()+ "-" + nodeID);
		//setId(identifier + "-" + nodeID);
		setNodeID(nodeID);
		setDistributions(distributions);

		setTitle(new DCATProperty(DCTerms.title, RDFS.Literal.getURI(), title));
		setDescription(new DCATProperty(DCTerms.description, RDFS.Literal.getURI(), description));
		// setTheme(theme != null ? theme
		// : Arrays.asList(new SKOSConcept(DCAT.theme.getURI(), SKOS.Concept.getURI(),
		// new ArrayList<SKOSPrefLabel>(), nodeID)));
		setTheme(theme);
		// setPublisher(publisher != null ? publisher
		// : new FOAFAgent(DCTerms.publisher.getURI(), "", "", "", "", "", "",
		// nodeID));
		setPublisher(publisher);
		// setContactPoint(contactPoint != null ? contactPoint
		// : Arrays.asList(new VCardOrganization(DCAT.contactPoint.getURI(), "",
		// "", "", "", "", nodeID)));
		setContactPoint(contactPoint);
		setKeywords(keywords != null && keywords.size() != 0 ? keywords : new ArrayList<String>());
		setAccessRights(new DCATProperty(DCTerms.accessRights, DCTerms.RightsStatement.getURI(), accessRights));

		// setConformsTo(conformsTo != null ? conformsTo
		// : Arrays.asList(new DCTStandard(DCTerms.conformsTo.getURI(), "", "",
		// "", "", nodeID)));
		setConformsTo(conformsTo);
		setDocumentation(
				documentation != null
						? documentation.stream().map(item -> new DCATProperty(FOAF.page, FOAF.Document.getURI(), item))
								.collect(Collectors.toList())
						: Arrays.asList(new DCATProperty(FOAF.page, FOAF.Document.getURI(), "")));

		setFrequency(new DCATProperty(DCTerms.accrualPeriodicity, DCTerms.Frequency.getURI(), frequency));
		setHasVersion(hasVersion != null
				? hasVersion.stream().map(item -> new DCATProperty(DCTerms.hasVersion, DCAT.Dataset.getURI(), item))
						.collect(Collectors.toList())
				: Arrays.asList(new DCATProperty(DCTerms.hasVersion, DCAT.Dataset.getURI(), "")));

		setIsVersionOf(isVersionOf != null
				? isVersionOf.stream().map(item -> new DCATProperty(DCTerms.isVersionOf, DCAT.Dataset.getURI(), item))
						.collect(Collectors.toList())
				: Arrays.asList(new DCATProperty(DCTerms.isVersionOf, DCAT.Dataset.getURI(), "")));

		setLandingPage(new DCATProperty(DCAT.landingPage, FOAF.Document.getURI(), landingPage));

		setLanguage(
				language != null
						? language.stream()
								.map(item -> new DCATProperty(DCTerms.language, DCTerms.LinguisticSystem.getURI(),
										item))
								.collect(Collectors.toList())
						: Arrays.asList(new DCATProperty(DCTerms.language, DCTerms.LinguisticSystem.getURI(), "")));

		setProvenance(
				provenance != null
						? provenance.stream()
								.map(item -> new DCATProperty(DCTerms.provenance, DCTerms.ProvenanceStatement.getURI(),
										item))
								.collect(Collectors.toList())
						: Arrays.asList(
								new DCATProperty(DCTerms.provenance, DCTerms.ProvenanceStatement.getURI(), "")));

		setReleaseDate(new DCATProperty(DCTerms.issued, RDFS.Literal.getURI(),
				StringUtils.isNotBlank(releaseDate) ? releaseDate : "1970-01-01T00:00:00Z"));
		setUpdateDate(new DCATProperty(DCTerms.modified, RDFS.Literal.getURI(),
				StringUtils.isNotBlank(updateDate) ? updateDate : "1970-01-01T00:00:00Z"));
		
		setIdentifier(new DCATProperty(DCTerms.identifier, RDFS.Literal.getURI(), identifier));
		
		setOtherIdentifier(otherIdentifier != null
				? otherIdentifier.stream()
						.map(item -> new DCATProperty(
								ResourceFactory.createProperty("http://www.w3.org/ns/adms#identifier"),
								"http://www.w3.org/ns/adms#Identifier", item))
						.collect(Collectors.toList())
				: Arrays.asList(new DCATProperty(ResourceFactory.createProperty("http://www.w3.org/ns/adms#identifier"),
						"http://www.w3.org/ns/adms#Identifier", "")));

		setSample(sample != null
				? sample.stream()
						.map(item -> new DCATProperty(
								ResourceFactory.createProperty("http://www.w3.org/ns/adms#sample"),
								DCAT.Distribution.getURI(), item))
						.collect(Collectors.toList())
				: Arrays.asList(new DCATProperty(ResourceFactory.createProperty("http://www.w3.org/ns/adms#sample"),
						DCAT.Distribution.getURI(), "")));

		setSource(source != null
				? source.stream().map(item -> new DCATProperty(DCTerms.source, DCAT.Dataset.getURI(), item))
						.collect(Collectors.toList())
				: Arrays.asList(new DCATProperty(DCTerms.source, DCAT.Dataset.getURI(), "")));

		// setSpatialCoverage(spatialCoverage != null ? spatialCoverage
		// : new DCTLocation(DCTerms.spatial.getURI(), "", "", "", nodeID));
		setSpatialCoverage(spatialCoverage);
		// setTemporalCoverage(temporalCoverage != null ? temporalCoverage
		// : new DCTPeriodOfTime(DCTerms.temporal.getURI(), "", "", nodeID));
		setTemporalCoverage(temporalCoverage);
		setType(new DCATProperty(DCTerms.type, SKOS.Concept.getURI(), type));
		setVersion(new DCATProperty(OWL.versionInfo, RDFS.Literal.getURI(), version));
		setVersionNotes(versionNotes != null
				? versionNotes.stream()
						.map(item -> new DCATProperty(
								ResourceFactory.createProperty("http://www.w3.org/ns/adms#versionNotes"),
								RDFS.Literal.getURI(), item))
						.collect(Collectors.toList())
				: Arrays.asList(
						new DCATProperty(ResourceFactory.createProperty("http://www.w3.org/ns/adms#versionNotes"),
								RDFS.Literal.getURI(), "")));

		// setRightsHolder(rightsHolder != null ? rightsHolder
		// : new FOAFAgent(DCTerms.rightsHolder.getURI(), "", "", "", "", "",
		// "", nodeID));
		// setCreator(creator != null ? creator : new
		// FOAFAgent(DCTerms.creator.getURI(), "", "", "", "", "", "", nodeID));
		setRightsHolder(rightsHolder);
		setCreator(creator);

		// setSubject(subject != null
		// ? subject.stream().map(item -> new DCATProperty(DCTerms.subject,
		// SKOS.Concept.getURI(), item))
		// .collect(Collectors.toList())
		// : Arrays.asList(new DCATProperty(DCTerms.subject, SKOS.Concept.getURI(),
		// "")));
		setSubject(subject);
		
//		this.setSeoIdentifier(CommonUtil.extractSeoIdentifier(title, identifier));
	}

	public DCATDataset(String nodeID, String title, String description, List<DCATDistribution> distributions,
			List<SKOSConceptTheme> theme, FOAFAgent publisher, List<VCardOrganization> contactPoint, List<String> keywords,
			String accessRights, List<DCTStandard> conformsTo, List<String> documentation, String frequency,
			List<String> hasVersion, List<String> isVersionOf, String landingPage, List<String> language,
			List<String> provenance, String releaseDate, String updateDate, String identifier,
			List<String> otherIdentifier, List<String> sample, List<String> source, DCTLocation spatialCoverage,
			DCTPeriodOfTime temporalCoverage, String type, String version, List<String> versionNotes,
			FOAFAgent rightsHolder, FOAFAgent creator, List<SKOSConceptSubject> subject, String legacyIdentifier) {

		this(nodeID, title, description, distributions, theme, publisher, contactPoint, keywords, accessRights,
				conformsTo, documentation, frequency, hasVersion, isVersionOf, landingPage, language, provenance,
				releaseDate, updateDate, identifier, otherIdentifier, sample, source, spatialCoverage, temporalCoverage,
				type, version, versionNotes, rightsHolder, creator, subject);

		this.setLegacyIdentifier(legacyIdentifier);
		this.setSeoIdentifier(CommonUtil.extractSeoIdentifier(title, getId(),getNodeID()));

	}

	public DCATDataset(String nodeID, String title, String description, List<DCATDistribution> distributions,
			List<SKOSConceptTheme> theme, FOAFAgent publisher, List<VCardOrganization> contactPoint, List<String> keywords,
			String accessRights, List<DCTStandard> conformsTo, List<String> documentation, String frequency,
			List<String> hasVersion, List<String> isVersionOf, String landingPage, List<String> language,
			List<String> provenance, String releaseDate, String updateDate, String identifier,
			List<String> otherIdentifier, List<String> sample, List<String> source, DCTLocation spatialCoverage,
			DCTPeriodOfTime temporalCoverage, String type, String version, List<String> versionNotes,
			FOAFAgent rightsHolder, FOAFAgent creator, List<SKOSConceptSubject> subject, String legacyIdentifier,String seoID) {

		this(nodeID, title, description, distributions, theme, publisher, contactPoint, keywords, accessRights,
				conformsTo, documentation, frequency, hasVersion, isVersionOf, landingPage, language, provenance,
				releaseDate, updateDate, identifier, otherIdentifier, sample, source, spatialCoverage, temporalCoverage,
				type, version, versionNotes, rightsHolder, creator, subject);

		this.setLegacyIdentifier(legacyIdentifier);
		this.setSeoIdentifier(seoID);

	}
	/*
	 * @Id
	 * 
	 * @GeneratedValue(generator="increment")
	 * 
	 * @GenericGenerator(name="increment", strategy = "increment")
	 * 
	 * @Id //@GeneratedValue(generator = "uuid") //@GenericGenerator(name = "uuid",
	 * strategy = "uuid2")
	 * 
	 * @Column(name = "dataset_id") /*public String getId() { String id = null; if
	 * (identifier != null) id = identifier.getValue();
	 * 
	 * if( id != null && !id.equals("") ) return identifier.getValue() + nodeID;
	 * else if (otherIdentifier != null) return otherIdentifier.getValue() + nodeID;
	 * else return null; }
	 * 
	 * @Field public void setId(String id) { String value = null; if (identifier !=
	 * null) value = identifier.getValue(); if( value == null || value.equals("") )
	 * identifier.setValue(id); else if (otherIdentifier != null)
	 * identifier.setValue(otherIdentifier.getValue());
	 * 
	 * }
	 */

	@Id
	// @GeneratedValue(generator = "uuid")
	// @GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "dataset_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Id
	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	@Transient
	public static Resource getRDFClass() {
		return RDFClass;
	}

	public boolean getHasStoredRDF() {
		return hasStoredRDF;
	}

	public void setHasStoredRDF(boolean hasStoredRDF) {
		this.hasStoredRDF = hasStoredRDF;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "title", columnDefinition = "LONGTEXT")) })
	public DCATProperty getTitle() {
		return title;
	}

	protected void setTitle(DCATProperty title) {
		this.title = title;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "description", columnDefinition = "LONGTEXT")) })
	public DCATProperty getDescription() {
		return description;
	}

	protected void setDescription(DCATProperty description) {
		this.description = description;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
	public DCATProperty getIdentifier() {
		return identifier;
	}

	protected void setIdentifier(DCATProperty dcat_identifier) {
		this.identifier = dcat_identifier;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_otherIdentifier", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "otherIdentifier")) })
	public List<DCATProperty> getOtherIdentifier() {
		return otherIdentifier;
	}

	public void setOtherIdentifier(List<DCATProperty> otherIdentifier) {
		this.otherIdentifier = otherIdentifier;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "releaseDate")) })
	public DCATProperty getReleaseDate() {
		return releaseDate;
	}

	protected void setReleaseDate(DCATProperty releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "updateDate")) })
	public DCATProperty getUpdateDate() {
		return updateDate;
	}

	protected void setUpdateDate(DCATProperty modified) {
		this.updateDate = modified;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "version")) })
	public DCATProperty getVersion() {
		return version;
	}

	protected void setVersion(DCATProperty version) {
		this.version = version;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_versionNotes", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "versionNotes")) })
	public List<DCATProperty> getVersionNotes() {
		return versionNotes;
	}

	protected void setVersionNotes(List<DCATProperty> versionNotes) {
		this.versionNotes = versionNotes;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "landingPage", length = 65535, columnDefinition = "Text")) })
	public DCATProperty getLandingPage() {
		return landingPage;
	}

	protected void setLandingPage(DCATProperty landingPage) {
		this.landingPage = landingPage;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	// @Fetch(FetchMode.SELECT)
	@JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	public List<VCardOrganization> getContactPoint() {
		return contactPoint;
	}

	protected void setContactPoint(List<VCardOrganization> contactPoint) {
		// this.contactPoint = contactPoint != null ? contactPoint
		// : Arrays.asList(new VCardOrganization(DCAT.contactPoint.getURI(), "",
		// "", "", "", "", nodeID));
		//
		this.contactPoint = contactPoint;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "frequency")) })
	public DCATProperty getFrequency() {
		return frequency;
	}

	protected void setFrequency(DCATProperty frequency) {
		this.frequency = frequency;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "spatialCoverage_id")
	public DCTLocation getSpatialCoverage() {
		return spatialCoverage;
	}

	protected void setSpatialCoverage(DCTLocation spatialCoverage) {
		this.spatialCoverage = spatialCoverage;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "temporalCoverage_id")
	public DCTPeriodOfTime getTemporalCoverage() {
		return temporalCoverage;
	}

	protected void setTemporalCoverage(DCTPeriodOfTime temporalCoverage) {
		this.temporalCoverage = temporalCoverage;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_language", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "language")) })
	public List<DCATProperty> getLanguage() {
		return language;
	}

	protected void setLanguage(List<DCATProperty> language) {
		this.language = language;
	}

	/*
	 * OLD: MOVED AT DISTRIBUTION AND CATALOG LEVEL
	 */
	// @Embedded
	// @AttributeOverrides({
	// @AttributeOverride(name = "value", column = @Column(name =
	// "licenseTitle", columnDefinition = "LONGTEXT")) })
	// public DCATProperty getLicenseTitle() {
	// return licenseTitle;
	// }
	//
	// public void setLicenseTitle(DCATProperty licenseTitle) {
	// this.licenseTitle = licenseTitle;
	// }

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "publisher_id")
	public FOAFAgent getPublisher() {
		return publisher;
	}

	protected void setPublisher(FOAFAgent publisher) {
		// this.publisher = publisher != null ? publisher
		// : new FOAFAgent(DCTerms.publisher.getURI(), "", "", "", "", "", "",
		// nodeID);
		this.publisher = publisher;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	@Where(clause="type='1'")
	public List<SKOSConceptTheme> getTheme() {
		return theme;
	}

	protected void setTheme(List<SKOSConceptTheme> theme) {
		this.theme = theme;
	}

	/*
	 * @ElementCollection
	 * 
	 * @CollectionTable( name="distribution",
	 * joinColumns=@JoinColumn(name="dataset_id") )
	 */
	/*
	 * @OneToMany(fetch = FetchType.LAZY, cascade = {
	 * CascadeType.ALL,CascadeType.PERSIST,CascadeType.MERGE }, mappedBy = "owner" )
	 */
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	// @Fetch(FetchMode.SELECT)
	@JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	public List<DCATDistribution> getDistributions() {
		return this.distributions;

	}

	protected void setDistributions(List<DCATDistribution> distributions) {
		this.distributions = distributions;
	}

	public void addDistribution(DCATDistribution distribution) {
		if (this.distributions == null)
			this.distributions = new ArrayList<DCATDistribution>();

		this.distributions.add(distribution);

	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_keyword", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	public List<String> getKeywords() {
		return this.keywords;
	}

	protected void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	/*
	 * public void toDCATKeywords(ArrayList <String> keywords ){ ArrayList
	 * <DCATProperty> keywords = new ArrayList<DCATProperty>(); for (Object k:
	 * keywords){ keywords.add(new DCATProperty("dcat:keyword",(String)k)); }
	 * setKeywords(keywords); }
	 */

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "accessRights")) })
	public DCATProperty getAccessRights() {
		return accessRights;
	}

	protected void setAccessRights(DCATProperty accessRights) {
		this.accessRights = accessRights;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	// @Fetch(FetchMode.SELECT)
	@JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	public List<DCTStandard> getConformsTo() {
		return conformsTo;
	}

	protected void setConformsTo(List<DCTStandard> conformsTo) {
		this.conformsTo = conformsTo;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_documentation", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "documentation", columnDefinition = "LONGTEXT")) })
	public List<DCATProperty> getDocumentation() {
		return documentation;
	}

	protected void setDocumentation(List<DCATProperty> documentation) {
		this.documentation = documentation;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_hasVersion", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "hasVersion", columnDefinition = "LONGTEXT")) })
	public List<DCATProperty> getHasVersion() {
		return hasVersion;
	}

	protected void setHasVersion(List<DCATProperty> hasVersion) {
		this.hasVersion = hasVersion;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_isVersionOf", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "isVersionOf", columnDefinition = "LONGTEXT")) })
	public List<DCATProperty> getIsVersionOf() {
		return isVersionOf;
	}

	protected void setIsVersionOf(List<DCATProperty> isVersionOf) {
		this.isVersionOf = isVersionOf;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_provenance", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "provenance")) })
	public List<DCATProperty> getProvenance() {
		return provenance;
	}

	protected void setProvenance(List<DCATProperty> provenance) {
		this.provenance = provenance;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_sample", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "sample")) })
	public List<DCATProperty> getSample() {
		return sample;
	}

	protected void setSample(List<DCATProperty> sample) {
		this.sample = sample;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_source", joinColumns = { @JoinColumn(name = "dataset_id",referencedColumnName="dataset_id"),	@JoinColumn(name = "nodeID",referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "source")) })
	public List<DCATProperty> getSource() {
		return source;
	}

	protected void setSource(List<DCATProperty> source) {
		this.source = source;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "type")) })
	public DCATProperty getType() {
		return type;
	}

	protected void setType(DCATProperty type) {
		this.type = type;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	@Where(clause="type='2'")
	public List<SKOSConceptSubject> getSubject() {
		return subject;
	}

	public void setSubject(List<SKOSConceptSubject> subject) {
		this.subject = subject;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "holder_id")
	public FOAFAgent getRightsHolder() {
		return rightsHolder;
	}

	public void setRightsHolder(FOAFAgent rightsHolder) {
		this.rightsHolder = rightsHolder;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "creator_id")
	public FOAFAgent getCreator() {
		return creator;
	}

	public void setCreator(FOAFAgent creator) {
		this.creator = creator;
	}

	
	public String getLegacyIdentifier() {
		return legacyIdentifier;
	}

	public void setLegacyIdentifier(String legacyIdentifier) {
		this.legacyIdentifier = legacyIdentifier;
	}

	public String getSeoIdentifier() {
		return seoIdentifier;
	}

	public void setSeoIdentifier(String seoIdentifier) {
		this.seoIdentifier = seoIdentifier;
	}
	
	/*
	 * Defines equality principle for a Dataset based on dcatIdentifier + its own
	 * nodeID Alternatively is used otherIdentifier + nodeID
	 */

	

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DCATDataset other = (DCATDataset) obj;

//		if (this.getId().equals(other.getId())) {
		if (this.getLegacyIdentifier().equals(other.getLegacyIdentifier())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getLegacyIdentifier().hashCode();
	}

	public SolrInputDocument toDoc() {

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id);
		doc.addField("content_type", CacheContentType.dataset.toString());
		doc.addField("nodeID", nodeID);
		doc.addField("hasStoredRDF", hasStoredRDF);

		try {
			doc.addField("legacyIdentifier",
					(StringUtils.isNotBlank(legacyIdentifier) ? legacyIdentifier
							: StringUtils.isNotBlank(otherIdentifier.get(0).getValue().toString())
									? otherIdentifier.get(0).getValue().toString()
									: ""));
		} catch (Exception e) {
			doc.addField("legacyIdentifier", "");
		}
		
		String desc_tmp = description.getValue();
		try {
			while (desc_tmp.getBytes("UTF-8").length >= 32766) {
				desc_tmp = desc_tmp.substring(0, (int) Math.ceil(desc_tmp.length() * (0.9))).trim();
			}
			this.description.setValue(desc_tmp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		doc.addField("description", description.getValue());
		doc.addField("title", title.getValue());

		if (theme != null && !theme.isEmpty()) {
			theme.stream().filter(item -> item != null)
					.forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.theme)));
			List<String> datasetThemes = new ArrayList<>();
			for(SKOSConceptTheme c : theme) {
				for(SKOSPrefLabel p : c.getPrefLabel()) {
					if(StringUtils.isNotBlank(p.getValue()) && FederationCore.isDcatTheme(p.getValue())) {
						datasetThemes.add(p.getValue());
					}
				}
			}
			
			doc.addField("datasetThemes", datasetThemes);
		}

		doc.addField("accessRights", accessRights.getValue());

		if (conformsTo != null && !conformsTo.isEmpty())
			conformsTo.stream().filter(item -> item != null)
					.forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.conformsTo)));

		doc.addField("frequency", frequency.getValue());

		if (hasVersion != null && !hasVersion.isEmpty())
			doc.addField("hasVersion", hasVersion.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

		if (documentation != null && !documentation.isEmpty())
			doc.addField("documentation", documentation.stream().filter(item -> item != null)
					.map(item -> item.getValue()).collect(Collectors.toList()));

		if (isVersionOf != null && !isVersionOf.isEmpty())
			doc.addField("isVersionOf", isVersionOf.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

		doc.addField("landingPage", landingPage.getValue());

		if (language != null && !language.isEmpty())
			doc.addField("language", language.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

		if (provenance != null && !provenance.isEmpty())
			doc.addField("provenance", provenance.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

		if (StringUtils.isNotBlank(releaseDate.getValue()))
			doc.addField("releaseDate", releaseDate.getValue());
		if (StringUtils.isNotBlank(updateDate.getValue()))
			doc.addField("updateDate", updateDate.getValue());

		doc.addField("identifier", identifier.getValue());
		if (otherIdentifier != null && !otherIdentifier.isEmpty())
			doc.addField("otherIdentifier", otherIdentifier.stream().filter(item -> item != null)
					.map(item -> item.getValue()).collect(Collectors.toList()));

		if (sample != null && !sample.isEmpty())
			doc.addField("sample", sample.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

		if (source != null && !source.isEmpty())
			doc.addField("source", source.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

		if (spatialCoverage != null)
			doc.addChildDocument(spatialCoverage.toDoc(CacheContentType.spatialCoverage));

		if (temporalCoverage != null)
			doc.addChildDocument(temporalCoverage.toDoc(CacheContentType.temporalCoverage));

		doc.addField("type", type.getValue());
		doc.addField("version", version.getValue());

		if (versionNotes != null && !versionNotes.isEmpty())
			doc.addField("versionNotes", versionNotes.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));
	
		if (subject != null && !subject.isEmpty())
			subject.stream().forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.subject)));

		if (creator != null)
			doc.addChildDocument(creator.toDoc(CacheContentType.creator));

		if (rightsHolder != null)
			doc.addChildDocument(rightsHolder.toDoc(CacheContentType.rightsHolder));

		if (publisher != null)
			doc.addChildDocument(publisher.toDoc(CacheContentType.publisher));

		if (contactPoint != null && !contactPoint.isEmpty()) {
			contactPoint.stream().filter(item -> item != null)
					.forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.contactPoint)));
		}

		if (distributions != null && !distributions.isEmpty()) {
			distributions.stream().filter(item -> item != null).forEach(item -> doc.addChildDocument(item.toDoc()));
								
			doc.addField("distributionFormats", distributions.stream().filter(x -> x!=null)
					.map(x -> { 
						if(x.getFormat() != null && StringUtils.isNotBlank(x.getFormat().getValue())) { 
							return x.getFormat().getValue().replaceFirst("\\.", "").toLowerCase();
						}else if(x.getMediaType() != null && StringUtils.isNotBlank(x.getMediaType().getValue())) {
							if(x.getMediaType().getValue().contains("/")) {
								return x.getMediaType().getValue().split("/")[1].toLowerCase();	
							}else {
								return x.getMediaType().getValue().toLowerCase();
							}
						}else {
							return null;
						}
					}).distinct().collect(Collectors.toList()));
			
			doc.addField("distributionLicenses",
					distributions.stream()
							.filter(x -> x != null && x.getLicense() != null && x.getLicense().getName() != null
									&& StringUtils.isNotBlank(x.getLicense().getName().getValue()))
							.map(x -> x.getLicense().getName().getValue().toLowerCase()).distinct()
							.collect(Collectors.toList()));
		}
		if (keywords != null && !keywords.isEmpty())
			doc.addField("keywords", keywords);
		
		try {
			if(StringUtils.isNotBlank(seoIdentifier))
				doc.addField("seoIdentifier",seoIdentifier);
			else
				doc.addField("seoIdentifier", CommonUtil.extractSeoIdentifier(title.getValue(), id,nodeID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc;
	}

	public static DCATDataset docToDataset(SolrDocument doc) {

		String nodeID = doc.getFieldValue("nodeID").toString();

		List<SolrDocument> childDocs = doc.getChildDocuments();
		ArrayList<DCATDistribution> distrList = new ArrayList<DCATDistribution>();
		List<SKOSConceptTheme> themeList = new ArrayList<SKOSConceptTheme>();
		List<SKOSConceptSubject> subjectList = new ArrayList<SKOSConceptSubject>();
		FOAFAgent publisher = null, creator = null, rightsHolder = null;
		List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
		List<DCTStandard> conformsToList = new ArrayList<DCTStandard>();
		DCTLocation spatialCoverage = null;
		DCTPeriodOfTime temporalCoverage = null;

		if (null != childDocs) {

			for (SolrDocument child : childDocs) {

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.conformsTo.toString())) {
					conformsToList.add(DCTStandard.docToDCATStandard(child, nodeID));
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.spatialCoverage.toString())) {
					spatialCoverage = DCTLocation.docToDCTLocation(child, DCTerms.spatial.getURI(), nodeID);
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.temporalCoverage.toString())) {
					temporalCoverage = DCTPeriodOfTime.docToDCTPeriodOfTime(child, DCTerms.temporal.getURI(), nodeID);
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.creator.toString())) {
					creator = FOAFAgent.docToFOAFAgent(child, DCTerms.creator.getURI(), nodeID);
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.rightsHolder.toString())) {
					rightsHolder = FOAFAgent.docToFOAFAgent(child, DCTerms.rightsHolder.getURI(), nodeID);
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.publisher.toString())) {
					publisher = FOAFAgent.docToFOAFAgent(child, DCTerms.publisher.getURI(), nodeID);
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.contactPoint.toString())) {
					contactPointList
							.add(VCardOrganization.docToVCardOrganization(child, DCAT.contactPoint.getURI(), nodeID));
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.distribution.toString())) {
					distrList.add(DCATDistribution.docToDCATDistribution(child));
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.theme.toString())) {
					themeList.add(SKOSConceptTheme.docToSKOSConcept(child, DCAT.theme.getURI(), nodeID));
				}

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.subject.toString())) {
					subjectList.add(SKOSConceptSubject.docToSKOSConcept(child, DCTerms.subject.getURI(), nodeID));
				}
			}
		}

		String dataset_issued = doc.getOrDefault("releaseDate", "").toString();
		if (StringUtils.isNotBlank(dataset_issued))
			dataset_issued = CommonUtil.toUtcDate(doc.getFieldValue("releaseDate").toString());

		String dataset_modified = doc.getOrDefault("updateDate", "").toString();
		if (StringUtils.isNotBlank(dataset_modified))
			dataset_modified = CommonUtil.toUtcDate(doc.getFieldValue("updateDate").toString());

		DCATDataset d= new DCATDataset(nodeID, doc.getFieldValue("title").toString(),
				doc.getFieldValue("description").toString(), distrList, themeList, publisher, contactPointList,
				(ArrayList<String>) doc.getFieldValue("keywords"), doc.getFieldValue("accessRights").toString(),
				conformsToList, (ArrayList<String>) doc.getFieldValue("documentation"),
				doc.getFieldValue("frequency").toString(), (ArrayList<String>) doc.getFieldValue("hasVersion"),
				(ArrayList<String>) doc.getFieldValue("isVersionOf"), doc.getFieldValue("landingPage").toString(),
				(ArrayList<String>) doc.getFieldValue("language"), (ArrayList<String>) doc.getFieldValue("provenance"),
				dataset_issued, dataset_modified, doc.getFieldValue("identifier").toString(),
				(ArrayList<String>) doc.getFieldValue("otherIdentifier"),
				(ArrayList<String>) doc.getFieldValue("sample"), (ArrayList<String>) doc.getFieldValue("source"),
				spatialCoverage, temporalCoverage, doc.getFieldValue("type").toString(),
				doc.getFieldValue("version").toString(), (ArrayList<String>) doc.getFieldValue("versionNotes"),
				rightsHolder, creator, subjectList, doc.getFieldValue("legacyIdentifier").toString(),doc.getFieldValue("seoIdentifier").toString());
	
		d.setId(doc.getFieldValue("id").toString());
		d.setHasStoredRDF((Boolean) doc.getFieldValue("hasStoredRDF"));
		return d;

	}
	
	@Override
	public String toString() {
		return "DCATDataset [id=" + id + ", nodeID=" + nodeID + ", title=" + title.getValue() + "identifier="
				+ identifier.getValue() + "]";
	}

}
