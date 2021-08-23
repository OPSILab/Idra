/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.dcat;

import com.google.gson.annotations.SerializedName;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
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

// TODO: Auto-generated Javadoc
/**
 * Represents a DCAT Dataset.
 *
 * @author
 */

@Entity
@Table(name = "dcat_dataset")
@IdClass(DcatDatasetId.class)
public class DcatDataset implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The id. */
  // Custom fields
  private String id;

  /** The node id. */
  @Column(name = "nodeID")
  @SerializedName(value = "nodeID")
  private String nodeId;

  /** The node name. */
  private String nodeName;

  /** The has stored rdf. */
  @Column(name = "hasStoredRDF")
  @SerializedName(value = "hasStoredRDF")
  private boolean hasStoredRdf = false;

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = DCAT.Dataset;

  // DCAT fields

  /** The title. */
  // Mandatory
  private DcatProperty title;

  /** The description. */
  private DcatProperty description;

  /** The distributions. */
  // Recommended
  private List<DcatDistribution> distributions;

  /** The theme. */
  private List<SkosConceptTheme> theme;

  /** The publisher. */
  private FoafAgent publisher;

  /** The contact point. */
  private List<VcardOrganization> contactPoint;

  /** The keywords. */
  private List<String> keywords;

  /** The access rights. */
  // Optional
  private DcatProperty accessRights;

  /** The conforms to. */
  private List<DctStandard> conformsTo;

  /** The documentation. */
  private List<DcatProperty> documentation;

  /** The frequency. */
  private DcatProperty frequency;

  /** The has version. */
  private List<DcatProperty> hasVersion;

  /** The is version of. */
  private List<DcatProperty> isVersionOf;

  /** The landing page. */
  private DcatProperty landingPage;

  /** The language. */
  private List<DcatProperty> language;

  /** The provenance. */
  private List<DcatProperty> provenance;

  /** The related resource. */
  private List<DcatProperty> relatedResource;

  /** The release date. */
  private DcatProperty releaseDate;

  /** The update date. */
  private DcatProperty updateDate;

  /** The identifier. */
  private DcatProperty identifier;

  /** The other identifier. */
  private List<DcatProperty> otherIdentifier;

  /** The sample. */
  private List<DcatProperty> sample;

  /** The source. */
  private List<DcatProperty> source;

  /** The spatial coverage. */
  private DctLocation spatialCoverage;

  /** The temporal coverage. */
  private DctPeriodOfTime temporalCoverage;

  /** The type. */
  private DcatProperty type;

  /** The version. */
  private DcatProperty version;

  /** The version notes. */
  private List<DcatProperty> versionNotes;

  /** The rights holder. */
  private FoafAgent rightsHolder;

  /** The creator. */
  private FoafAgent creator;

  /** The subject. */
  private List<SkosConceptSubject> subject;

  // private String legacyIdentifier;
  // private String seoIdentifier;
  // TODO To be Removed
  // OLD: According to DCAT Specs, the license is at Distribution or Catalog
  // level
  // private DCATProperty licenseTitle;

  /**
   * Instantiates a new dcat dataset.
   */
  public DcatDataset() {
  }

  /**
   * Instantiates a new dcat dataset.
   *
   * @param nodeId           the node ID
   * @param identifier       the identifier
   * @param title            the title
   * @param description      the description
   * @param distributions    the distributions
   * @param theme            the theme
   * @param publisher        the publisher
   * @param contactPoint     the contact point
   * @param keywords         the keywords
   * @param accessRights     the access rights
   * @param conformsTo       the conforms to
   * @param documentation    the documentation
   * @param frequency        the frequency
   * @param hasVersion       the has version
   * @param isVersionOf      the is version of
   * @param landingPage      the landing page
   * @param language         the language
   * @param provenance       the provenance
   * @param releaseDate      the release date
   * @param updateDate       the update date
   * @param otherIdentifier  the other identifier
   * @param sample           the sample
   * @param source           the source
   * @param spatialCoverage  the spatial coverage
   * @param temporalCoverage the temporal coverage
   * @param type             the type
   * @param version          the version
   * @param versionNotes     the version notes
   * @param rightsHolder     the rights holder
   * @param creator          the creator
   * @param subject          the subject
   * @param relatedResource  the related resource
   */
  public DcatDataset(String nodeId, String identifier, String title, String description,
      List<DcatDistribution> distributions, List<SkosConceptTheme> theme, FoafAgent publisher,
      List<VcardOrganization> contactPoint, List<String> keywords, String accessRights,
      List<DctStandard> conformsTo, List<String> documentation, String frequency,
      List<String> hasVersion, List<String> isVersionOf, String landingPage, List<String> language,
      List<String> provenance, String releaseDate, String updateDate, List<String> otherIdentifier,
      List<String> sample, List<String> source, DctLocation spatialCoverage,
      DctPeriodOfTime temporalCoverage, String type, String version, List<String> versionNotes,
      FoafAgent rightsHolder, FoafAgent creator, List<SkosConceptSubject> subject,
      List<String> relatedResource) {

    super();
    setId(CommonUtil.extractSeoIdentifier(title, UUID.randomUUID().toString(), nodeId));
    setNodeId(nodeId);

    try {
      setNodeName(FederationCore.getOdmsCatalogue(Integer.parseInt(nodeId)).getName());
    } catch (NumberFormatException | OdmsCatalogueNotFoundException e) {
      e.printStackTrace();
      setNodeName("");
    }

    setIdentifier(new DcatProperty(DCTerms.identifier, RDFS.Literal, identifier));

    setDistributions(distributions);
    setTitle(new DcatProperty(DCTerms.title, RDFS.Literal, title));
    setDescription(new DcatProperty(DCTerms.description, RDFS.Literal, description));
    setTheme(theme);
    setPublisher(publisher);
    setContactPoint(contactPoint);
    setKeywords(keywords != null && keywords.size() != 0 ? keywords : new ArrayList<String>());
    setAccessRights(new DcatProperty(DCTerms.accessRights, DCTerms.RightsStatement, accessRights));
    setConformsTo(conformsTo);
    setDocumentation(documentation != null
        ? documentation.stream().map(item -> new DcatProperty(FOAF.page, FOAF.Document, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(FOAF.page, FOAF.Document, "")));

    setRelatedResource(relatedResource != null
        ? relatedResource.stream()
            .map(item -> new DcatProperty(DCTerms.relation, RDFS.Resource, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.relation, RDFS.Resource, "")));

    setFrequency(new DcatProperty(DCTerms.accrualPeriodicity, DCTerms.Frequency, frequency));
    setHasVersion(hasVersion != null
        ? hasVersion.stream().map(item -> new DcatProperty(DCTerms.hasVersion, DCAT.Dataset, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.hasVersion, DCAT.Dataset, "")));

    setIsVersionOf(isVersionOf != null
        ? isVersionOf.stream()
            .map(item -> new DcatProperty(DCTerms.isVersionOf, DCAT.Dataset, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.isVersionOf, DCAT.Dataset, "")));

    setLandingPage(new DcatProperty(DCAT.landingPage, FOAF.Document, landingPage));

    setLanguage(language != null
        ? language.stream()
            .map(item -> new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, "")));

    setProvenance(
        provenance != null
            ? provenance.stream()
                .map(
                    item -> new DcatProperty(DCTerms.provenance, DCTerms.ProvenanceStatement, item))
                .collect(Collectors.toList())
            : Arrays.asList(new DcatProperty(DCTerms.provenance, DCTerms.ProvenanceStatement, "")));

    setReleaseDate(new DcatProperty(DCTerms.issued, RDFS.Literal,
        StringUtils.isNotBlank(releaseDate) ? releaseDate : "1970-01-01T00:00:00Z"));
    setUpdateDate(new DcatProperty(DCTerms.modified, RDFS.Literal,
        StringUtils.isNotBlank(updateDate) ? updateDate : "1970-01-01T00:00:00Z"));

    setOtherIdentifier(otherIdentifier != null
        ? otherIdentifier.stream()
            .map(item -> new DcatProperty(
                ResourceFactory.createProperty("http://www.w3.org/ns/adms#identifier"),
                ResourceFactory.createResource("http://www.w3.org/ns/adms#Identifier"), item))
            .collect(Collectors.toList())
        : Arrays.asList(
            new DcatProperty(ResourceFactory.createProperty("http://www.w3.org/ns/adms#identifier"),
                ResourceFactory.createResource("http://www.w3.org/ns/adms#Identifier"), "")));

    setSample(sample != null
        ? sample.stream()
            .map(item -> new DcatProperty(
                ResourceFactory.createProperty("http://www.w3.org/ns/adms#sample"),
                DCAT.Distribution, item))
            .collect(Collectors.toList())
        : Arrays.asList(
            new DcatProperty(ResourceFactory.createProperty("http://www.w3.org/ns/adms#sample"),
                DCAT.Distribution, "")));

    setSource(source != null
        ? source.stream().map(item -> new DcatProperty(DCTerms.source, DCAT.Dataset, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.source, DCAT.Dataset, "")));

    // setSpatialCoverage(spatialCoverage != null ? spatialCoverage
    // : new DCTLocation(DCTerms.spatial.getURI(), "", "", "", nodeID));
    setSpatialCoverage(spatialCoverage);
    // setTemporalCoverage(temporalCoverage != null ? temporalCoverage
    // : new DCTPeriodOfTime(DCTerms.temporal.getURI(), "", "", nodeID));
    setTemporalCoverage(temporalCoverage);
    setType(new DcatProperty(DCTerms.type, SKOS.Concept, type));
    setVersion(new DcatProperty(OWL.versionInfo, RDFS.Literal, version));
    setVersionNotes(versionNotes != null
        ? versionNotes.stream()
            .map(item -> new DcatProperty(
                ResourceFactory.createProperty("http://www.w3.org/ns/adms#versionNotes"),
                RDFS.Literal, item))
            .collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(
            ResourceFactory.createProperty("http://www.w3.org/ns/adms#versionNotes"), RDFS.Literal,
            "")));

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

    // this.setSeoIdentifier(CommonUtil.extractSeoIdentifier(title, identifier));
  }

  /**
   * Instantiates a new dcat dataset.
   *
   * @param id               the id
   * @param nodeId           the node ID
   * @param identifier       the identifier
   * @param title            the title
   * @param description      the description
   * @param distributions    the distributions
   * @param theme            the theme
   * @param publisher        the publisher
   * @param contactPoint     the contact point
   * @param keywords         the keywords
   * @param accessRights     the access rights
   * @param conformsTo       the conforms to
   * @param documentation    the documentation
   * @param frequency        the frequency
   * @param hasVersion       the has version
   * @param isVersionOf      the is version of
   * @param landingPage      the landing page
   * @param language         the language
   * @param provenance       the provenance
   * @param releaseDate      the release date
   * @param updateDate       the update date
   * @param otherIdentifier  the other identifier
   * @param sample           the sample
   * @param source           the source
   * @param spatialCoverage  the spatial coverage
   * @param temporalCoverage the temporal coverage
   * @param type             the type
   * @param version          the version
   * @param versionNotes     the version notes
   * @param rightsHolder     the rights holder
   * @param creator          the creator
   * @param subject          the subject
   * @param relatedResource  the related resource
   * @param hasStoredRdf     the has stored RDF
   */
  public DcatDataset(String id, String nodeId, String identifier, String title, String description,
      List<DcatDistribution> distributions, List<SkosConceptTheme> theme, FoafAgent publisher,
      List<VcardOrganization> contactPoint, List<String> keywords, String accessRights,
      List<DctStandard> conformsTo, List<String> documentation, String frequency,
      List<String> hasVersion, List<String> isVersionOf, String landingPage, List<String> language,
      List<String> provenance, String releaseDate, String updateDate, List<String> otherIdentifier,
      List<String> sample, List<String> source, DctLocation spatialCoverage,
      DctPeriodOfTime temporalCoverage, String type, String version, List<String> versionNotes,
      FoafAgent rightsHolder, FoafAgent creator, List<SkosConceptSubject> subject,
      List<String> relatedResource, boolean hasStoredRdf) {

    this(nodeId, identifier, title, description, distributions, theme, publisher, contactPoint,
        keywords, accessRights, conformsTo, documentation, frequency, hasVersion, isVersionOf,
        landingPage, language, provenance, releaseDate, updateDate, otherIdentifier, sample, source,
        spatialCoverage, temporalCoverage, type, version, versionNotes, rightsHolder, creator,
        subject, relatedResource);

    this.setId(id);
    this.setHasStoredRdf(hasStoredRdf);

  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  // @GeneratedValue(generator = "uuid")
  // @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "dataset_id")
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  @Id
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Gets the node name.
   *
   * @return the node name
   */
  @Transient
  public String getNodeName() {
    return nodeName;
  }

  /**
   * Sets the node name.
   *
   * @param nodeName the new node name
   */
  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  /**
   * Gets the rdf class.
   *
   * @return the rdf class
   */
  @Transient
  public static Resource getRdfClass() {
    return RDFClass;
  }

  /**
   * Gets the checks for stored rdf.
   *
   * @return the checks for stored rdf
   */
  public boolean getHasStoredRdf() {
    return hasStoredRdf;
  }

  /**
   * Sets the checks for stored rdf.
   *
   * @param hasStoredRdf the new checks for stored rdf
   */
  public void setHasStoredRdf(boolean hasStoredRdf) {
    this.hasStoredRdf = hasStoredRdf;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "title", columnDefinition = "LONGTEXT")) })
  public DcatProperty getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  protected void setTitle(DcatProperty title) {
    this.title = title;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "description", columnDefinition = "LONGTEXT")) })
  public DcatProperty getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  protected void setDescription(DcatProperty description) {
    this.description = description;
  }

  /**
   * Gets the identifier.
   *
   * @return the identifier
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
  public DcatProperty getIdentifier() {
    return identifier;
  }

  /**
   * Sets the identifier.
   *
   * @param dcatIdentifier the new identifier
   */
  protected void setIdentifier(DcatProperty dcatIdentifier) {
    this.identifier = dcatIdentifier;
  }

  /**
   * Gets the other identifier.
   *
   * @return the other identifier
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_otherIdentifier", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "otherIdentifier")) })
  public List<DcatProperty> getOtherIdentifier() {
    return otherIdentifier;
  }

  /**
   * Sets the other identifier.
   *
   * @param otherIdentifier the new other identifier
   */
  public void setOtherIdentifier(List<DcatProperty> otherIdentifier) {
    this.otherIdentifier = otherIdentifier;
  }

  /**
   * Gets the release date.
   *
   * @return the release date
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "releaseDate")) })
  public DcatProperty getReleaseDate() {
    return releaseDate;
  }

  /**
   * Sets the release date.
   *
   * @param releaseDate the new release date
   */
  protected void setReleaseDate(DcatProperty releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Gets the update date.
   *
   * @return the update date
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "updateDate")) })
  public DcatProperty getUpdateDate() {
    return updateDate;
  }

  /**
   * Sets the update date.
   *
   * @param modified the new update date
   */
  protected void setUpdateDate(DcatProperty modified) {
    this.updateDate = modified;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "version")) })
  public DcatProperty getVersion() {
    return version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  protected void setVersion(DcatProperty version) {
    this.version = version;
  }

  /**
   * Gets the version notes.
   *
   * @return the version notes
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_versionNotes", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "versionNotes")) })
  public List<DcatProperty> getVersionNotes() {
    return versionNotes;
  }

  /**
   * Sets the version notes.
   *
   * @param versionNotes the new version notes
   */
  protected void setVersionNotes(List<DcatProperty> versionNotes) {
    this.versionNotes = versionNotes;
  }

  /**
   * Gets the landing page.
   *
   * @return the landing page
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "landingPage", length = 65535, columnDefinition = "Text")) })
  public DcatProperty getLandingPage() {
    return landingPage;
  }

  /**
   * Sets the landing page.
   *
   * @param landingPage the new landing page
   */
  protected void setLandingPage(DcatProperty landingPage) {
    this.landingPage = landingPage;
  }

  /**
   * Gets the contact point.
   *
   * @return the contact point
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  // @Fetch(FetchMode.SELECT)
  @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  public List<VcardOrganization> getContactPoint() {
    return contactPoint;
  }

  /**
   * Sets the contact point.
   *
   * @param contactPoint the new contact point
   */
  protected void setContactPoint(List<VcardOrganization> contactPoint) {
    // this.contactPoint = contactPoint != null ? contactPoint
    // : Arrays.asList(new VCardOrganization(DCAT.contactPoint.getURI(), "",
    // "", "", "", "", nodeID));
    //
    this.contactPoint = contactPoint;
  }

  /**
   * Gets the frequency.
   *
   * @return the frequency
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "frequency")) })
  public DcatProperty getFrequency() {
    return frequency;
  }

  /**
   * Sets the frequency.
   *
   * @param frequency the new frequency
   */
  protected void setFrequency(DcatProperty frequency) {
    this.frequency = frequency;
  }

  /**
   * Gets the spatial coverage.
   *
   * @return the spatial coverage
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "spatialCoverage_id")
  public DctLocation getSpatialCoverage() {
    return spatialCoverage;
  }

  /**
   * Sets the spatial coverage.
   *
   * @param spatialCoverage the new spatial coverage
   */
  protected void setSpatialCoverage(DctLocation spatialCoverage) {
    this.spatialCoverage = spatialCoverage;
  }

  /**
   * Gets the temporal coverage.
   *
   * @return the temporal coverage
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "temporalCoverage_id")
  public DctPeriodOfTime getTemporalCoverage() {
    return temporalCoverage;
  }

  /**
   * Sets the temporal coverage.
   *
   * @param temporalCoverage the new temporal coverage
   */
  protected void setTemporalCoverage(DctPeriodOfTime temporalCoverage) {
    this.temporalCoverage = temporalCoverage;
  }

  /**
   * Gets the language.
   *
   * @return the language
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_language", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "language")) })
  public List<DcatProperty> getLanguage() {
    return language;
  }

  /**
   * Sets the language.
   *
   * @param language the new language
   */
  protected void setLanguage(List<DcatProperty> language) {
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

  /**
   * Gets the publisher.
   *
   * @return the publisher
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "publisher_id")
  public FoafAgent getPublisher() {
    return publisher;
  }

  /**
   * Sets the publisher.
   *
   * @param publisher the new publisher
   */
  protected void setPublisher(FoafAgent publisher) {
    // this.publisher = publisher != null ? publisher
    // : new FOAFAgent(DCTerms.publisher.getURI(), "", "", "", "", "", "",
    // nodeID);
    this.publisher = publisher;
  }

  /**
   * Gets the theme.
   *
   * @return the theme
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @Where(clause = "type='1'")
  public List<SkosConceptTheme> getTheme() {
    return theme;
  }

  /**
   * Sets the theme.
   *
   * @param theme the new theme
   */
  protected void setTheme(List<SkosConceptTheme> theme) {
    this.theme = theme;
  }

  /*
   * @ElementCollection
   * 
   * @CollectionTable( name="distribution",
   * joinColumns=@JoinColumn(name="dataset_id") )
   */
  /**
   * Gets the distributions.
   *
   * @return the distributions
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
  public List<DcatDistribution> getDistributions() {
    return this.distributions;

  }

  /**
   * Sets the distributions.
   *
   * @param distributions the new distributions
   */
  protected void setDistributions(List<DcatDistribution> distributions) {
    this.distributions = distributions;
  }

  /**
   * Adds the distribution.
   *
   * @param distribution the distribution
   */
  public void addDistribution(DcatDistribution distribution) {
    if (this.distributions == null) {
      this.distributions = new ArrayList<DcatDistribution>();
    }

    this.distributions.add(distribution);

  }

  /**
   * Gets the keywords.
   *
   * @return the keywords
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_keyword", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  public List<String> getKeywords() {
    return this.keywords;
  }

  /**
   * Sets the keywords.
   *
   * @param keywords the new keywords
   */
  protected void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }

  /*
   * public void toDCATKeywords(ArrayList <String> keywords ){ ArrayList
   * <DCATProperty> keywords = new ArrayList<DCATProperty>(); for (Object k:
   * keywords){ keywords.add(new DCATProperty("dcat:keyword",(String)k)); }
   * setKeywords(keywords); }
   */

  /**
   * Gets the access rights.
   *
   * @return the access rights
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "accessRights")) })
  public DcatProperty getAccessRights() {
    return accessRights;
  }

  /**
   * Sets the access rights.
   *
   * @param accessRights the new access rights
   */
  protected void setAccessRights(DcatProperty accessRights) {
    this.accessRights = accessRights;
  }

  /**
   * Gets the conforms to.
   *
   * @return the conforms to
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  // @Fetch(FetchMode.SELECT)
  @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  public List<DctStandard> getConformsTo() {
    return conformsTo;
  }

  /**
   * Sets the conforms to.
   *
   * @param conformsTo the new conforms to
   */
  protected void setConformsTo(List<DctStandard> conformsTo) {
    this.conformsTo = conformsTo;
  }

  /**
   * Gets the documentation.
   *
   * @return the documentation
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_documentation", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "documentation", columnDefinition = "LONGTEXT")) })
  public List<DcatProperty> getDocumentation() {
    return documentation;
  }

  /**
   * Sets the documentation.
   *
   * @param documentation the new documentation
   */
  protected void setDocumentation(List<DcatProperty> documentation) {
    this.documentation = documentation;
  }

  /**
   * Gets the related resource.
   *
   * @return the related resource
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_relatedResource", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "relatedResource", columnDefinition = "LONGTEXT")) })
  public List<DcatProperty> getRelatedResource() {
    return relatedResource;
  }

  /**
   * Sets the related resource.
   *
   * @param relatedResource the new related resource
   */
  public void setRelatedResource(List<DcatProperty> relatedResource) {
    this.relatedResource = relatedResource;
  }

  /**
   * Gets the checks for version.
   *
   * @return the checks for version
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_hasVersion", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "hasVersion", columnDefinition = "LONGTEXT")) })
  public List<DcatProperty> getHasVersion() {
    return hasVersion;
  }

  /**
   * Sets the checks for version.
   *
   * @param hasVersion the new checks for version
   */
  protected void setHasVersion(List<DcatProperty> hasVersion) {
    this.hasVersion = hasVersion;
  }

  /**
   * Gets the checks if is version of.
   *
   * @return the checks if is version of
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_isVersionOf", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "isVersionOf", columnDefinition = "LONGTEXT")) })
  public List<DcatProperty> getIsVersionOf() {
    return isVersionOf;
  }

  /**
   * Sets the checks if is version of.
   *
   * @param isVersionOf the new checks if is version of
   */
  protected void setIsVersionOf(List<DcatProperty> isVersionOf) {
    this.isVersionOf = isVersionOf;
  }

  /**
   * Gets the provenance.
   *
   * @return the provenance
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_provenance", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "provenance")) })
  public List<DcatProperty> getProvenance() {
    return provenance;
  }

  /**
   * Sets the provenance.
   *
   * @param provenance the new provenance
   */
  protected void setProvenance(List<DcatProperty> provenance) {
    this.provenance = provenance;
  }

  /**
   * Gets the sample.
   *
   * @return the sample
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_sample", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "sample")) })
  public List<DcatProperty> getSample() {
    return sample;
  }

  /**
   * Sets the sample.
   *
   * @param sample the new sample
   */
  protected void setSample(List<DcatProperty> sample) {
    this.sample = sample;
  }

  /**
   * Gets the source.
   *
   * @return the source
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_source", joinColumns = {
      @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "source")) })
  public List<DcatProperty> getSource() {
    return source;
  }

  /**
   * Sets the source.
   *
   * @param source the new source
   */
  protected void setSource(List<DcatProperty> source) {
    this.source = source;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "type")) })
  public DcatProperty getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  protected void setType(DcatProperty type) {
    this.type = type;
  }

  /**
   * Gets the subject.
   *
   * @return the subject
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @Where(clause = "type='2'")
  public List<SkosConceptSubject> getSubject() {
    return subject;
  }

  /**
   * Sets the subject.
   *
   * @param subject the new subject
   */
  public void setSubject(List<SkosConceptSubject> subject) {
    this.subject = subject;
  }

  /**
   * Gets the rights holder.
   *
   * @return the rights holder
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "holder_id")
  public FoafAgent getRightsHolder() {
    return rightsHolder;
  }

  /**
   * Sets the rights holder.
   *
   * @param rightsHolder the new rights holder
   */
  public void setRightsHolder(FoafAgent rightsHolder) {
    this.rightsHolder = rightsHolder;
  }

  /**
   * Gets the creator.
   *
   * @return the creator
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "creator_id")
  public FoafAgent getCreator() {
    return creator;
  }

  /**
   * Sets the creator.
   *
   * @param creator the new creator
   */
  public void setCreator(FoafAgent creator) {
    this.creator = creator;
  }

  /*
   * Defines equality principle for a Dataset based on dcatIdentifier + its own
   * nodeID Alternatively is used otherIdentifier + nodeID
   */

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
    DcatDataset other = (DcatDataset) obj;

    if (this.getIdentifier().getValue().equals(other.getIdentifier().getValue())) {
      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return this.getIdentifier().hashCode();
  }

  /**
   * To doc.
   *
   * @return the solr input document
   */
  public SolrInputDocument toDoc() {

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", id);
    doc.addField("content_type", CacheContentType.dataset.toString());
    doc.addField("nodeID", nodeId);
    doc.addField("hasStoredRDF", hasStoredRdf);

    String descTmp = description.getValue();
    try {
      while (descTmp.getBytes("UTF-8").length >= 32766) {
        descTmp = descTmp.substring(0, (int) Math.ceil(descTmp.length() * (0.9))).trim();
      }
      this.description.setValue(descTmp);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    doc.addField("description", description.getValue());
    doc.addField("title", title.getValue());

    if (theme != null && !theme.isEmpty()) {
      theme.stream().filter(item -> item != null)
          .forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.theme)));
      List<String> datasetThemes = new ArrayList<>();
      for (SkosConceptTheme c : theme) {
        for (SkosPrefLabel p : c.getPrefLabel()) {
          if (StringUtils.isNotBlank(p.getValue()) && FederationCore.isDcatTheme(p.getValue())) {
            datasetThemes.add(p.getValue());
          }
        }
      }

      doc.addField("datasetThemes", datasetThemes);
    }

    doc.addField("accessRights", accessRights.getValue());

    if (conformsTo != null && !conformsTo.isEmpty()) {
      conformsTo.stream().filter(item -> item != null)
          .forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.conformsTo)));
    }

    doc.addField("frequency", frequency.getValue());

    if (hasVersion != null && !hasVersion.isEmpty()) {
      doc.addField("hasVersion", hasVersion.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (documentation != null && !documentation.isEmpty()) {
      doc.addField("documentation", documentation.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (relatedResource != null && !relatedResource.isEmpty()) {
      doc.addField("relatedResource", relatedResource.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (isVersionOf != null && !isVersionOf.isEmpty()) {
      doc.addField("isVersionOf", isVersionOf.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    doc.addField("landingPage", landingPage.getValue());

    if (language != null && !language.isEmpty()) {
      doc.addField("language", language.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (provenance != null && !provenance.isEmpty()) {
      doc.addField("provenance", provenance.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (StringUtils.isNotBlank(releaseDate.getValue())) {
      doc.addField("releaseDate", releaseDate.getValue());
    }
    if (StringUtils.isNotBlank(updateDate.getValue())) {
      doc.addField("updateDate", updateDate.getValue());
    }

    doc.addField("identifier", identifier.getValue());
    if (otherIdentifier != null && !otherIdentifier.isEmpty()) {
      doc.addField("otherIdentifier", otherIdentifier.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (sample != null && !sample.isEmpty()) {
      doc.addField("sample", sample.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (source != null && !source.isEmpty()) {
      doc.addField("source", source.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (spatialCoverage != null) {
      doc.addChildDocument(spatialCoverage.toDoc(CacheContentType.spatialCoverage));
    }

    if (temporalCoverage != null) {
      doc.addChildDocument(temporalCoverage.toDoc(CacheContentType.temporalCoverage));
    }

    doc.addField("type", type.getValue());
    doc.addField("version", version.getValue());

    if (versionNotes != null && !versionNotes.isEmpty()) {
      doc.addField("versionNotes", versionNotes.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (subject != null && !subject.isEmpty()) {
      subject.stream().forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.subject)));
    }

    if (creator != null) {
      doc.addChildDocument(creator.toDoc(CacheContentType.creator));
    }

    if (rightsHolder != null) {
      doc.addChildDocument(rightsHolder.toDoc(CacheContentType.rightsHolder));
    }

    if (publisher != null) {
      doc.addChildDocument(publisher.toDoc(CacheContentType.publisher));
    }

    if (contactPoint != null && !contactPoint.isEmpty()) {
      contactPoint.stream().filter(item -> item != null)
          .forEach(item -> doc.addChildDocument(item.toDoc(CacheContentType.contactPoint)));
    }

    if (distributions != null && !distributions.isEmpty()) {
      distributions.stream().filter(item -> item != null)
          .forEach(item -> doc.addChildDocument(item.toDoc()));

      doc.addField("distributionFormats", distributions.stream().filter(x -> x != null).map(x -> {
        if (x.getFormat() != null && StringUtils.isNotBlank(x.getFormat().getValue())) {
          return x.getFormat().getValue().replaceFirst("\\.", "").toLowerCase();
        } else if (x.getMediaType() != null
            && StringUtils.isNotBlank(x.getMediaType().getValue())) {
          if (x.getMediaType().getValue().contains("/")) {
            return x.getMediaType().getValue().split("/")[1].toLowerCase();
          } else {
            return x.getMediaType().getValue().toLowerCase();
          }
        } else {
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
    if (keywords != null && !keywords.isEmpty()) {
      doc.addField("keywords", keywords);
    }

    // try {
    // if(StringUtils.isNotBlank(seoIdentifier))
    // doc.addField("seoIdentifier",seoIdentifier);
    // else
    // doc.addField("seoIdentifier",
    // CommonUtil.extractSeoIdentifier(title.getValue(), id,nodeID));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    return doc;
  }

  /**
   * Doc to dataset.
   *
   * @param doc the doc
   * @return the dcat dataset
   */
  public static DcatDataset docToDataset(SolrDocument doc) {

    String nodeIdentifier = doc.getFieldValue("nodeID").toString();

    List<SolrDocument> childDocs = doc.getChildDocuments();
    ArrayList<DcatDistribution> distrList = new ArrayList<DcatDistribution>();
    List<SkosConceptTheme> themeList = new ArrayList<SkosConceptTheme>();
    List<SkosConceptSubject> subjectList = new ArrayList<SkosConceptSubject>();
    FoafAgent publisher = null;
    FoafAgent creator = null;
    FoafAgent rightsHolder = null;
    List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();
    List<DctStandard> conformsToList = new ArrayList<DctStandard>();
    DctLocation spatialCoverage = null;
    DctPeriodOfTime temporalCoverage = null;

    if (null != childDocs) {

      for (SolrDocument child : childDocs) {

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type").equals(CacheContentType.conformsTo.toString())) {
          conformsToList.add(DctStandard.docToDcatStandard(child, nodeIdentifier));
        }

        if (child.containsKey("content_type") && child.getFieldValue("content_type")
            .equals(CacheContentType.spatialCoverage.toString())) {
          spatialCoverage = DctLocation.docToDctLocation(child, DCTerms.spatial.getURI(),
              nodeIdentifier);
        }

        if (child.containsKey("content_type") && child.getFieldValue("content_type")
            .equals(CacheContentType.temporalCoverage.toString())) {
          temporalCoverage = DctPeriodOfTime.docToDctPeriodOfTime(child, DCTerms.temporal.getURI(),
              nodeIdentifier);
        }

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type").equals(CacheContentType.creator.toString())) {
          creator = FoafAgent.docToFoafAgent(child, DCTerms.creator.getURI(), nodeIdentifier);
        }

        if (child.containsKey("content_type") && child.getFieldValue("content_type")
            .equals(CacheContentType.rightsHolder.toString())) {
          rightsHolder = FoafAgent.docToFoafAgent(child, DCTerms.rightsHolder.getURI(),
              nodeIdentifier);
        }

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type").equals(CacheContentType.publisher.toString())) {
          publisher = FoafAgent.docToFoafAgent(child, DCTerms.publisher.getURI(), nodeIdentifier);
        }

        if (child.containsKey("content_type") && child.getFieldValue("content_type")
            .equals(CacheContentType.contactPoint.toString())) {
          contactPointList.add(VcardOrganization.docToVcardOrganization(child,
              DCAT.contactPoint.getURI(), nodeIdentifier));
        }

        if (child.containsKey("content_type") && child.getFieldValue("content_type")
            .equals(CacheContentType.distribution.toString())) {
          distrList.add(DcatDistribution.docToDcatDistribution(child));
        }

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type").equals(CacheContentType.theme.toString())) {
          themeList
              .add(SkosConceptTheme.docToSkosConcept(child, DCAT.theme.getURI(), nodeIdentifier));
        }

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type").equals(CacheContentType.subject.toString())) {
          subjectList.add(
              SkosConceptSubject.docToSkosConcept(child, DCTerms.subject.getURI(), nodeIdentifier));
        }
      }
    }

    String datasetIssued = doc.getOrDefault("releaseDate", "").toString();
    if (StringUtils.isNotBlank(datasetIssued)) {
      datasetIssued = CommonUtil.toUtcDate(doc.getFieldValue("releaseDate").toString());
    }

    String datasetModified = doc.getOrDefault("updateDate", "").toString();
    if (StringUtils.isNotBlank(datasetModified)) {
      datasetModified = CommonUtil.toUtcDate(doc.getFieldValue("updateDate").toString());
    }

    DcatDataset d = new DcatDataset(doc.getFieldValue("id").toString(), nodeIdentifier,
        doc.getFieldValue("identifier").toString(), doc.getFieldValue("title").toString(),
        doc.getFieldValue("description").toString(), distrList, themeList, publisher,
        contactPointList, (ArrayList<String>) doc.getFieldValue("keywords"),
        doc.getFieldValue("accessRights").toString(), conformsToList,
        (ArrayList<String>) doc.getFieldValue("documentation"),
        doc.getFieldValue("frequency").toString(),
        (ArrayList<String>) doc.getFieldValue("hasVersion"),
        (ArrayList<String>) doc.getFieldValue("isVersionOf"),
        doc.getFieldValue("landingPage").toString(),
        (ArrayList<String>) doc.getFieldValue("language"),
        (ArrayList<String>) doc.getFieldValue("provenance"), datasetIssued, datasetModified,
        (ArrayList<String>) doc.getFieldValue("otherIdentifier"),
        (ArrayList<String>) doc.getFieldValue("sample"),
        (ArrayList<String>) doc.getFieldValue("source"), spatialCoverage, temporalCoverage,
        doc.getFieldValue("type").toString(), doc.getFieldValue("version").toString(),
        (ArrayList<String>) doc.getFieldValue("versionNotes"), rightsHolder, creator, subjectList,
        (ArrayList<String>) doc.getFieldValue("relatedResource"),
        (Boolean) doc.getFieldValue("hasStoredRDF"));

    return d;

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCATDataset [id=" + id + ", nodeID=" + nodeId + ", title=" + title.getValue()
        + "identifier=" + identifier.getValue() + "]";
  }

}
