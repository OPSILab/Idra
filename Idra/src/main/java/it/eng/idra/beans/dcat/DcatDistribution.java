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

package it.eng.idra.beans.dcat;

import com.google.gson.annotations.SerializedName;
import it.eng.idra.beans.DistributionAdditionalConfiguration;
import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a DCAT Distribution.
 *
 * @author
 */

// @Embeddable
@Table(name = "dcat_distribution")
@Entity
public class DcatDistribution implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Resource RDFClass = DCAT.Distribution;

  private String id;
  
  @SerializedName(value = "storedRDF")
  @Column(name = "storedRDF")
  private boolean storedRdf;
  
  @SerializedName(value = "nodeID")
  //@Column(name = "nodeID")
  private transient String nodeId;

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  // DCAT FIELDS
  // Mandatory
  @SerializedName(value = "accessURL")
  private DcatProperty accessUrl;

  // Recommended
  private DcatProperty description;
  private DcatProperty format;
  private DctLicenseDocument license;

  // Optional
  private DcatProperty byteSize;
  private SpdxChecksum checksum;

  private List<DcatProperty> documentation;
  
  @SerializedName(value = "downloadURL")
  private DcatProperty downloadUrl;
  
  private List<DcatProperty> language;
  private List<DctStandard> linkedSchemas;
  private DcatProperty mediaType;
  private DcatProperty releaseDate;
  private DcatProperty updateDate;
  private DcatProperty rights;
  private SkosConceptStatus status;
  private DcatProperty title;

  // private List<Datalet> datalets;
  private boolean hasDatalets = false;
  private DistributionAdditionalConfiguration distributionAdditionalConfig;

  public DcatDistribution() {
  }

  /**
   * Instantiates a new dcat distribution.
   *
   * @param nodeId the node ID
   */
  /*
   * DON'T TOUCH - CONSTRUCTOR USED BY WEB SCRAPER
   */
  public DcatDistribution(String nodeId) {
    this(nodeId, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, null);
  }

  /**
   * Instantiates a new dcat distribution.
   *
   * @param id the id
   * @param nodeId the node ID
   * @param accessUrl the access URL
   * @param description the description
   * @param format the format
   * @param license the license
   * @param byteSize the byte size
   * @param checksum the checksum
   * @param documentation the documentation
   * @param downloadUrl the download URL
   * @param language the language
   * @param linkedSchemas the linked schemas
   * @param mediaType the media type
   * @param releaseDate the release date
   * @param updateDate the update date
   * @param rights the rights
   * @param status the status
   * @param title the title
   * @param hasDatalets the has datalets
   */
  public DcatDistribution(String id, 
      String nodeId, String accessUrl, 
      String description, String format,
      DctLicenseDocument license, String byteSize, 
      SpdxChecksum checksum, List<String> documentation,
      String downloadUrl,
      List<String> language, 
      List<DctStandard> linkedSchemas, String mediaType, String releaseDate,
      String updateDate, String rights, 
      SkosConceptStatus status, String title, boolean hasDatalets) {

    this(nodeId, accessUrl, description, format, 
        license, byteSize, checksum, documentation, downloadUrl, language,
        linkedSchemas, mediaType, releaseDate, updateDate, rights, status, title);
    this.setId(id);
    this.setHasDatalets(hasDatalets);
    // setDatalets(datalets);
  }

  /**
   * Instantiates a new dcat distribution.
   *
   * @param nodeId the node ID
   * @param accessUrl the access URL
   * @param description the description
   * @param format the format
   * @param license the license
   * @param byteSize the byte size
   * @param checksum the checksum
   * @param documentation the documentation
   * @param downloadUrl the download URL
   * @param language the language
   * @param linkedSchemas the linked schemas
   * @param mediaType the media type
   * @param releaseDate the release date
   * @param updateDate the update date
   * @param rights the rights
   * @param status the status
   * @param title the title
   */
  public DcatDistribution(String nodeId, 
      String accessUrl, String description, String format,
      DctLicenseDocument license, String byteSize, 
      SpdxChecksum checksum, List<String> documentation,
      String downloadUrl, List<String> language, 
      List<DctStandard> linkedSchemas, String mediaType, String releaseDate,
      String updateDate, String rights, SkosConceptStatus status, String title) {
    super();
    setNodeId(nodeId);
    setAccessUrl(new DcatProperty(DCAT.accessURL, RDFS.Resource, accessUrl));
    setDescription(new DcatProperty(DCTerms.description, RDFS.Literal, description));
    setDownloadUrl(new DcatProperty(DCAT.downloadURL, RDFS.Resource, downloadUrl));
    setFormat(new DcatProperty(DCTerms.format, DCTerms.MediaTypeOrExtent, format));
    // setLicense(license != null ? license : new
    // DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID));
    setLicense(license);
    setByteSize(new DcatProperty(DCAT.byteSize, RDFS.Literal, byteSize));
    // setChecksum(
    // checksum != null ? checksum : new
    // SPDXChecksum("http://spdx.org/rdf/terms#checksum", "", "", nodeID));
    setChecksum(checksum);
    setDocumentation(
        documentation != null ? documentation.stream()
            .map(item -> new DcatProperty(FOAF.page, FOAF.Document, item))
            .collect(Collectors.toList()) 
            : Arrays.asList(new DcatProperty(FOAF.page, FOAF.Document, "")));
    setLanguage(language != null ? language.stream()
        .map(item -> new DcatProperty(DCTerms.language, 
            DCTerms.LinguisticSystem, item)).collect(Collectors.toList())
        : Arrays.asList(new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, "")));
    setLinkedSchemas(linkedSchemas);
    setMediaType(new DcatProperty(DCAT.mediaType, DCTerms.MediaType, mediaType));
    setReleaseDate(
        new DcatProperty(DCTerms.issued, RDFS.Literal, 
            StringUtils.isNotBlank(releaseDate) ? releaseDate : ""));
    setUpdateDate(
        new DcatProperty(DCTerms.modified, RDFS.Literal, 
            StringUtils.isNotBlank(updateDate) ? updateDate : ""));
    setRights(new DcatProperty(DCTerms.rights, DCTerms.RightsStatement, rights));
    setStatus(status);
    setTitle(new DcatProperty(DCTerms.title, RDFS.Literal, title));
  }

  @Transient
  public static Resource getRdfClass() {
    return RDFClass;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public boolean getStoredRdf() {
    return storedRdf;
  }

  public void setStoredRdf(boolean storedRdf) {
    this.storedRdf = storedRdf;
  }

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "title", columnDefinition = "LONGTEXT")) })
  public DcatProperty getTitle() {
    return title;
  }

  public void setTitle(DcatProperty title) {
    this.title = title;
  }

  public void setTitle(String title) {
    setTitle(new DcatProperty(DCTerms.title, RDFS.Literal, title));
  }

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "accessURL", length = 65535, columnDefinition = "Text")) })
  public DcatProperty getAccessUrl() {
    return accessUrl;
  }

  public void setAccessUrl(DcatProperty accessUrl) {
    this.accessUrl = accessUrl;
  }

  public void setAccessUrl(String accessUrl) {
    setAccessUrl(new DcatProperty(DCAT.accessURL, RDFS.Resource, accessUrl));
  }

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "description", columnDefinition = "LONGTEXT")) })
  public DcatProperty getDescription() {
    return description;
  }

  public void setDescription(DcatProperty description) {
    this.description = description;
  }

  public void setDescription(String description) {
    setDescription(new DcatProperty(DCTerms.description, RDFS.Literal, description));
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "mediaType")) })
  public DcatProperty getMediaType() {
    return mediaType;
  }

  public void setMediaType(DcatProperty mediaType) {
    this.mediaType = mediaType;
  }

  public void setMediaType(String mediaType) {
    setMediaType(new DcatProperty(DCAT.mediaType, DCTerms.MediaType, mediaType));
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "format")) })
  public DcatProperty getFormat() {
    return format;
  }

  /**
   * Sets the format.
   *
   * @param format the format
   * @return the dcat distribution
   */
  public DcatDistribution setFormat(DcatProperty format) {
    if (StringUtils.isBlank(format.getValue())) {
      format.setValue(CommonUtil.extractFormatFromFileExtension(this.getDownloadUrl().getValue()));
    }
    this.format = format;
    return this;
  }

  /**
   * Sets the format.
   *
   * @param format the format
   * @return the dcat distribution
   */
  public DcatDistribution setFormat(String format) {
    if (StringUtils.isBlank(format)) {
      format = CommonUtil.extractFormatFromFileExtension(this.getDownloadUrl().getValue());
    }
    return setFormat(new DcatProperty(DCTerms.format, DCTerms.MediaTypeOrExtent, format));
  }

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "licenseDocument_id")
  public DctLicenseDocument getLicense() {
    return license;
  }

  public void setLicense(DctLicenseDocument license) {
    this.license = (license != null) ? license 
        : new DctLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeId);
  }

  // public void setLicense(String license) {
  // setLicense(license != null ? new DCTLicenseDocument(DCTerms.license.getURI(),
  // license, "", "", nodeID)
  // : new DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID));
  // }

  /**
   * Sets the license uri.
   *
   * @param uri the new license uri
   */
  public void setLicense_uri(String uri) {
    if (license == null) {
      setLicense(null);
    }
    if (StringUtils.isNotBlank(uri)) {
      license.setUri(uri);
    }
  }

  /**
   * Sets the license name.
   *
   * @param name the new license name
   */
  public void setLicense_name(String name) {
    if (license == null) {
      setLicense(null);
    }
    if (StringUtils.isNotBlank(name)) {
      license.setName(name);
    }
  }

  /**
   * Sets the license type.
   *
   * @param type the new license type
   */
  public void setLicense_type(String type) {
    if (license == null) {
      setLicense(null);
    }
    if (StringUtils.isNotBlank(type)) {
      license.setType(type);
    }
  }

  /**
   * Sets the license version info.
   *
   * @param versionInfo the new license version info
   */
  public void setLicense_versionInfo(String versionInfo) {
    if (license == null) {
      setLicense(null);
    }
    if (StringUtils.isNotBlank(versionInfo)) {
      license.setVersionInfo(versionInfo);
    }
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "byteSize")) })
  public DcatProperty getByteSize() {
    return byteSize;
  }

  public void setByteSize(DcatProperty byteSize) {
    this.byteSize = byteSize;
  }

  public void setByteSize(String byteSize) {
    setByteSize(new DcatProperty(DCAT.byteSize, RDFS.Literal, byteSize));
  }

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "releaseDate", 
          columnDefinition = "varchar(255) default '1970-01-01T00:00:00Z'")) })
  public DcatProperty getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(DcatProperty releaseDate) {
    this.releaseDate = releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    setReleaseDate(new DcatProperty(DCTerms.issued, RDFS.Literal, releaseDate));
  }

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "updateDate", 
          columnDefinition = "varchar(255) default '1970-01-01T00:00:00Z'")) })
  public DcatProperty getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(DcatProperty updateDate) {
    this.updateDate = updateDate;
  }

  public void setUpdateDate(String updateDate) {
    setUpdateDate(new DcatProperty(DCTerms.modified, RDFS.Literal, updateDate));
  }

  // @AttributeOverrides({ @AttributeOverride(name = "value", column =
  // @Column(name = "documentation")) })
  // public List<DCATProperty> getDocumentation() {
  // return documentation;
  // }

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_distribution_documentation", joinColumns = {
      @JoinColumn(name = "distribution_id", referencedColumnName = "id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "documentation", columnDefinition = "LONGTEXT")) })
  public List<DcatProperty> getDocumentation() {
    return documentation;
  }

  public void setDocumentation(List<DcatProperty> documentation) {
    this.documentation = documentation;
  }

  public void setDocumentation(String documentation) {
    setDocumentation(Arrays.asList(new DcatProperty(FOAF.page, FOAF.Document, documentation)));
  }

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "downloadURL", length = 65535, columnDefinition = "Text")) })
  public DcatProperty getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(DcatProperty downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    setDownloadUrl(new DcatProperty(DCAT.downloadURL, RDFS.Resource, downloadUrl));
  }

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_distribution_language", joinColumns = {
      @JoinColumn(name = "distribution_id", referencedColumnName = "id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "language")) })
  public List<DcatProperty> getLanguage() {
    return language;
  }

  public void setLanguage(List<DcatProperty> language) {
    this.language = language;
  }

  public void setLanguage(String language) {
    setLanguage(Arrays.asList(
        new DcatProperty(DCTerms.language, DCTerms.LinguisticSystem, language)));
  }

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  // @Fetch(FetchMode.SELECT)
  @JoinColumns({ @JoinColumn(name = "distribution_id", referencedColumnName = "id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  public List<DctStandard> getLinkedSchemas() {
    return linkedSchemas;
  }

  public void setLinkedSchemas(List<DctStandard> linkedSchemas) {
    this.linkedSchemas = linkedSchemas;
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "rights")) })
  public DcatProperty getRights() {
    return rights;
  }

  public void setRights(DcatProperty rights) {
    this.rights = rights;
  }

  public void setRights(String rights) {
    setRights(new DcatProperty(DCTerms.rights, DCTerms.RightsStatement, rights));
  }

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumns({ @JoinColumn(name = "status_id", referencedColumnName = "concept_id") })
  @Where(clause = "type='3'")
  public SkosConceptStatus getStatus() {
    return status;
  }

  public void setStatus(SkosConceptStatus status) {
    this.status = status;
  }

  public void setStatus(String status) {
    setStatus(new SkosConceptStatus("http://www.w3.org/ns/adms#status", "",
        Arrays.asList(new SkosPrefLabel("", status, nodeId)), nodeId));
  }

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "checksum_id")
  public SpdxChecksum getChecksum() {
    return checksum;
  }

  public void setChecksum(SpdxChecksum checksum) {
    this.checksum = checksum;
  }

  public void setChecksum(String checksum) {
    setChecksum(new SpdxChecksum("http://spdx.org/rdf/terms#checksum", "checksumAlgorithm_sha1", checksum, nodeId));
  }

  public boolean isHasDatalets() {
    return hasDatalets;
  }

  public void setHasDatalets(boolean hasDatalets) {
    this.hasDatalets = hasDatalets;
  }

  @OneToOne(orphanRemoval = true, cascade = { CascadeType.ALL, CascadeType.REMOVE })
  @JoinColumn(name = "distribution_additionalconfig_id")
  public DistributionAdditionalConfiguration getDistributionAdditionalConfig() {
    return distributionAdditionalConfig;
  }

  public void setDistributionAdditionalConfig(
      DistributionAdditionalConfiguration distributionAdditionalConfig) {
    this.distributionAdditionalConfig = distributionAdditionalConfig;
  }

  // @LazyCollection(LazyCollectionOption.FALSE)
  // @OneToMany(cascade = { CascadeType.ALL },orphanRemoval=true)
  // @JoinColumns({@JoinColumn(name = "distribution_id",
  // referencedColumnName="id"),
  // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID")})
  // public List<Datalet> getDatalets() {
  // return datalets;
  // }
  //
  // public void setDatalets(List<Datalet> datalets) {
  // this.datalets = datalets;
  // }
  //
  // public void addDatalet(Datalet datalet) {
  // if (this.datalets == null)
  // this.datalets = new ArrayList<Datalet>();
  //
  //
  // if(StringUtils.isBlank(datalet.getTitle())) {
  // Integer newID =1;
  // List<Datalet> tmp = this.datalets.stream().filter(x->
  // x.isCustomTitle()).collect(Collectors.toList());
  // if(tmp.size()!=0)
  // newID = tmp.stream().map(x ->
  // Integer.parseInt(x.getTitle().split("_")[1]))
  // .collect(Collectors.summarizingInt(Integer::intValue)).getMax()+1;
  // datalet.setTitle("Datalet_"+newID);
  // datalet.setCustomTitle(true);
  // }else {
  // datalet.setCustomTitle(false);
  // }
  //
  // this.datalets.add(datalet);
  //
  // }

  /**
   * Checks if is rdf.
   *
   * @return true, if is rdf
   */
  @Transient
  public boolean isRdf() {
    return ((this.format != null
        && (this.format.getValue().equals("RDF") 
            || this.format.getValue().equals("application/rdf+xml")))

        || (this.mediaType != null
            && (this.mediaType.getValue().equals("RDF") 
                || this.mediaType.getValue().equals("application/rdf+xml"))));

  }

  /**
   * To doc.
   *
   * @return the solr input document
   */
  public SolrInputDocument toDoc() {

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", id);
    doc.addField("content_type", CacheContentType.distribution.toString());
    doc.addField("nodeID", nodeId);
    doc.addField("storedRDF", storedRdf);
    String descTmp = description != null 
        ? description.getValue() : "";
    try {
      while (descTmp.getBytes("UTF-8").length >= 32766) {
        descTmp = descTmp.substring(0, (int) Math.ceil(descTmp.length() * (0.9))).trim();
      }
      this.description.setValue(descTmp);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    doc.addField("accessURL", accessUrl.getValue());
    doc.addField("description", description.getValue());
    doc.addField("format", format.getValue());

    // if (license != null)
    // doc.addChildDocument(license.toDoc(CacheContentType.licenseDocument));

    if (license != null) {
      try {
        doc.addField("license", GsonUtil.obj2Json(license, GsonUtil.licenseType));
      } catch (GsonUtilException e) {
        e.printStackTrace();
      }
    }
    // if (license != null)
    // doc.addChildDocument(license.toDoc(CacheContentType.licenseDocument));
    if (byteSize != null) {
      doc.addField("byteSize", byteSize.getValue());
    }

    if (checksum != null) {
      try {
        doc.addField("checksum", GsonUtil.obj2Json(checksum, GsonUtil.checksumType));
        // doc.addField("checksum",checksum.toDoc(CacheContentType.checksum));
      } catch (GsonUtilException e) {
        e.printStackTrace();
      }
    }

    // if (datalets != null && !datalets.isEmpty()) {
    // try {
    // for (Datalet datalet : datalets)
    // doc.addField("datalets", GsonUtil.obj2Json(datalet, GsonUtil.dataletType));
    // } catch (GsonUtilException e) {
    // e.printStackTrace();
    // }
    // }

    doc.addField("hasDatalets", hasDatalets);

    // doc.addField("documentation", documentation.getValue());
    doc.addField("downloadURL", downloadUrl.getValue());
    // doc.addField("language", language.getValue());

    if (documentation != null && !documentation.isEmpty()) {
      doc.addField("documentation", documentation.stream()
          .filter(item -> item != null).map(item -> item.getValue())
          .collect(Collectors.toList()));
    }
    if (language != null && !language.isEmpty()) {
      doc.addField("language",
          language.stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (linkedSchemas != null && !linkedSchemas.isEmpty()) {
      try {
        doc.addField("linkedSchemas", GsonUtil.obj2Json(linkedSchemas, GsonUtil.stdListType));
      } catch (GsonUtilException e) {
        e.printStackTrace();
      }
    }
    // if (linkedSchemas != null && !linkedSchemas.isEmpty())
    // linkedSchemas.stream().filter(item -> item != null)
    // .forEach(item ->
    // doc.addChildDocument(item.toDoc(CacheContentType.linkedSchemas)));
    if (mediaType != null) {
      doc.addField("mediaType", mediaType.getValue());
    }

    if (releaseDate != null && StringUtils.isNotBlank(releaseDate.getValue())) {
      doc.addField("releaseDate", releaseDate.getValue());
    }
    if (releaseDate != null && StringUtils.isNotBlank(updateDate.getValue())) {
      doc.addField("updateDate", updateDate.getValue());
    }
    if (rights != null) {
      doc.addField("rights", rights.getValue());
    }
    if (title != null) {
      doc.addField("title", title.getValue());
    }

    if (status != null) {
      try {
        doc.addField("status", GsonUtil.obj2Json(status, GsonUtil.conceptType));
      } catch (GsonUtilException e) {
        e.printStackTrace();
      }
    }

    return doc;

  }

  /**
   * Doc to DCAT distribution.
   *
   * @param doc the doc
   * @return the dcat distribution
   */
  public static DcatDistribution docToDcatDistribution(SolrDocument doc) {

    String nodeIdentifier = doc.getFieldValue("nodeID").toString();
    String distribIssued = doc.getOrDefault("releaseDate", "").toString();
    if (StringUtils.isNotBlank(distribIssued)) {
      distribIssued = CommonUtil.toUtcDate(distribIssued);
    }
    String distribModified = doc.getOrDefault("updateDate", "").toString();
    if (StringUtils.isNotBlank(distribModified)) {
      distribModified = CommonUtil.toUtcDate(distribModified);
    }

    List<SolrDocument> childDocs = doc.getChildDocuments();
    DctLicenseDocument license = null;
    SpdxChecksum checksum = null;
    List<DctStandard> linkedSchemas = new ArrayList<DctStandard>();
    SkosConceptStatus status = null;
    // List<Datalet> datalets = new ArrayList<Datalet>();
    if (null != childDocs) {

      for (SolrDocument child : childDocs) {

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type")
            .equals(CacheContentType.licenseDocument.toString())) {
          license = DctLicenseDocument.docToDctLicenseDocument(child, nodeIdentifier);
        }

        // if (child.containsKey("content_type")
        // &&
        // child.getFieldValue("content_type").equals(CacheContentType.linkedSchemas.toString()))
        // {
        // linkedSchemas.add(DCTStandard.docToDCATStandard(child, nodeID));
        // }

        if (child.containsKey("content_type")
            && child.getFieldValue("content_type").equals(CacheContentType.checksum.toString())) {
          checksum = SpdxChecksum.docToSpdxChecksum(child, "http://spdx.org/rdf/terms#checksum", nodeIdentifier);
        }

      }
    }

    if (doc.getFieldValue("checksum") != null) {
      checksum = SpdxChecksum.jsonToSpdxChecksum(
          new JSONObject(doc.getFieldValue("checksum").toString()),
          "http://spdx.org/rdf/terms#checksum", nodeIdentifier);
    }

    if (doc.getFieldValue("license") != null) {
      license = DctLicenseDocument.jsonToDctLicenseDocument(
          new JSONObject(doc.getFieldValue("license").toString()),
          nodeIdentifier);
    }

    if (doc.getFieldValue("status") != null) {
      status = SkosConceptStatus.jsonToSkosConcept(
          new JSONObject(doc.getFieldValue("status").toString()),
          "http://www.w3.org/ns/adms#status", nodeIdentifier);
    }

    // try {
    // license = GsonUtil.json2Obj(doc.getFieldValue("license").toString(),
    // GsonUtil.licenseType);
    // } catch (GsonUtilException e) {
    // e.printStackTrace();
    // }

    if (doc.getFieldValue("linkedSchemas") != null) {
      linkedSchemas = DctStandard
          .jsonArrayToDcatStandardList(new JSONArray(
              doc.getFieldValue("linkedSchemas").toString()), nodeIdentifier);
    }

    // try {
    // linkedSchemas =
    // GsonUtil.json2Obj(doc.getFieldValue("linkedSchemas").toString(),
    // GsonUtil.stdListType);
    // } catch (GsonUtilException e) {
    // e.printStackTrace();
    // }

    // if (doc.getFieldValue("datalets") != null) {
    // try {
    // List<String> dataletsString = (List<String>) doc.getFieldValue("datalets");
    // for (String s : dataletsString)
    // datalets.add(GsonUtil.json2Obj(s, GsonUtil.dataletType));
    // } catch (GsonUtilException e) {
    // e.printStackTrace();
    // }
    // }
    String byteSize = "";
    if (doc.getFieldValue("byteSize") != null) {
      byteSize = doc.getFieldValue("byteSize").toString();
    }

    DcatDistribution distr = new DcatDistribution(doc.getFieldValue("id").toString(),
        doc.getFieldValue("nodeID").toString(), doc.getFieldValue("accessURL").toString(),
        doc.getFieldValue("description").toString(),
        doc.getFieldValue("format").toString(), license, byteSize,
        checksum, (ArrayList<String>) doc.getFieldValue("documentation"),
        doc.getFieldValue("downloadURL").toString(),
        (ArrayList<String>) doc.getFieldValue("language"), linkedSchemas,
        (doc.getFieldValue("mediaType") != null) 
        ? doc.getFieldValue("mediaType").toString() : "", distribIssued,
        distribModified, (doc.getFieldValue("rights") != null) 
        ? doc.getFieldValue("rights").toString() : "", status,
        doc.getFieldValue("title").toString(), (Boolean) doc.getFieldValue("hasDatalets"));
    // datalets);
    distr.setStoredRdf((Boolean) doc.getFieldValue("storedRDF"));
    return distr;
  }

  @Override
  public String toString() {
    return "DCATDistribution [id=" + id + ", datalets= " + hasDatalets + "]";
  }

}
