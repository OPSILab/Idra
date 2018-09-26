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

import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;

/**
 * Represents a DCAT Distribution
 *
 * @author
 * @version 1.2
 * @since
 */

// @Embeddable
@Table(name = "dcat_distribution")
@Entity
public class DCATDistribution implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Resource RDFClass = DCAT.Distribution;

	private String id;
	private boolean storedRDF;
	private transient String nodeID;

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
	private DCATProperty accessURL;

	// Recommended
	private DCATProperty description;
	private DCATProperty format;
	private DCTLicenseDocument license;

	// Optional
	private DCATProperty byteSize;
	private SPDXChecksum checksum;

	private List<DCATProperty> documentation;
	private DCATProperty downloadURL;
	private List<DCATProperty> language;
	private List<DCTStandard> linkedSchemas;
	private DCATProperty mediaType;
	private DCATProperty releaseDate;
	private DCATProperty updateDate;
	private DCATProperty rights;
	private SKOSConceptStatus status;
	private DCATProperty title;

	// private List<Datalet> datalets;
	private boolean hasDatalets = false;
	private OrionDistributionConfig orionDistributionConfig;
	
	
	public DCATDistribution() {
	}

	/*
	 * DON'T TOUCH - CONSTRUCTOR USED BY WEB SCRAPER
	 */
	public DCATDistribution(String nodeID) {
		this(nodeID, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	public DCATDistribution(String id, String nodeID, String accessURL, String description, String format,
			DCTLicenseDocument license, String byteSize, SPDXChecksum checksum, List<String> documentation,
			String downloadURL, List<String> language, List<DCTStandard> linkedSchemas, String mediaType,
			String releaseDate, String updateDate, String rights, SKOSConceptStatus status, String title,
			boolean hasDatalets) {

		this(nodeID, accessURL, description, format, license, byteSize, checksum, documentation, downloadURL, language,
				linkedSchemas, mediaType, releaseDate, updateDate, rights, status, title);
		this.setId(id);
		this.setHasDatalets(hasDatalets);
		// setDatalets(datalets);
	}

	public DCATDistribution(String nodeID, String accessURL, String description, String format,
			DCTLicenseDocument license, String byteSize, SPDXChecksum checksum, List<String> documentation,
			String downloadURL, List<String> language, List<DCTStandard> linkedSchemas, String mediaType,
			String releaseDate, String updateDate, String rights, SKOSConceptStatus status, String title) {
		super();
		setNodeID(nodeID);
		setAccessURL(new DCATProperty(DCAT.accessURL, RDFS.Resource.getURI(), accessURL));
		setDescription(new DCATProperty(DCTerms.description, RDFS.Literal.getURI(), description));
		setDownloadURL(new DCATProperty(DCAT.downloadURL, RDFS.Resource.getURI(), downloadURL));
		setFormat(new DCATProperty(DCTerms.format, DCTerms.MediaTypeOrExtent.getURI(), format));
		// setLicense(license != null ? license : new
		// DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID));
		setLicense(license);
		setByteSize(new DCATProperty(DCAT.byteSize, RDFS.Literal.getURI(), byteSize));
		// setChecksum(
		// checksum != null ? checksum : new
		// SPDXChecksum("http://spdx.org/rdf/terms#checksum", "", "", nodeID));
		setChecksum(checksum);
		setDocumentation(
				documentation != null
						? documentation.stream().map(item -> new DCATProperty(FOAF.page, FOAF.Document.getURI(), item))
								.collect(Collectors.toList())
						: Arrays.asList(new DCATProperty(FOAF.page, FOAF.Document.getURI(), "")));
		setLanguage(
				language != null
						? language.stream()
								.map(item -> new DCATProperty(DCTerms.language, DCTerms.LinguisticSystem.getURI(),
										item))
								.collect(Collectors.toList())
						: Arrays.asList(new DCATProperty(DCTerms.language, DCTerms.LinguisticSystem.getURI(), "")));
		setLinkedSchemas(linkedSchemas);
		setMediaType(new DCATProperty(DCAT.mediaType, DCTerms.MediaType.getURI(), mediaType));
		setReleaseDate(new DCATProperty(DCTerms.issued, RDFS.Literal.getURI(),
				StringUtils.isNotBlank(releaseDate) ? releaseDate : ""));
		setUpdateDate(new DCATProperty(DCTerms.modified, RDFS.Literal.getURI(),
				StringUtils.isNotBlank(updateDate) ? updateDate : ""));
		setRights(new DCATProperty(DCTerms.rights, DCTerms.RightsStatement.getURI(), rights));
		setStatus(status);
		setTitle(new DCATProperty(DCTerms.title, RDFS.Literal.getURI(), title));
	}

	@Transient
	public static Resource getRDFClass() {
		return RDFClass;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public boolean getStoredRDF() {
		return storedRDF;
	}

	public void setStoredRDF(boolean storedRDF) {
		this.storedRDF = storedRDF;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "title", columnDefinition = "LONGTEXT")) })
	public DCATProperty getTitle() {
		return title;
	}

	public void setTitle(DCATProperty title) {
		this.title = title;
	}

	public void setTitle(String title) {
		setTitle(new DCATProperty(DCTerms.title, RDFS.Literal.getURI(), title));
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "accessURL", length = 65535, columnDefinition = "Text")) })
	public DCATProperty getAccessURL() {
		return accessURL;
	}

	public void setAccessURL(DCATProperty accessURL) {
		this.accessURL = accessURL;
	}

	public void setAccessURL(String accessURL) {
		setAccessURL(new DCATProperty(DCAT.accessURL, RDFS.Resource.getURI(), accessURL));
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "description", columnDefinition = "LONGTEXT")) })
	public DCATProperty getDescription() {
		return description;
	}

	public void setDescription(DCATProperty description) {
		this.description = description;
	}

	public void setDescription(String description) {
		setDescription(new DCATProperty(DCTerms.description, RDFS.Literal.getURI(), description));
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "mediaType")) })
	public DCATProperty getMediaType() {
		return mediaType;
	}

	public void setMediaType(DCATProperty mediaType) {
		this.mediaType = mediaType;
	}

	public void setMediaType(String mediaType) {
		setMediaType(new DCATProperty(DCAT.mediaType, DCTerms.MediaType.getURI(), mediaType));
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "format")) })
	public DCATProperty getFormat() {
		return format;
	}

	public DCATDistribution setFormat(DCATProperty format) {
		if (StringUtils.isBlank(format.getValue())) {
			format.setValue(CommonUtil.extractFormatFromFileExtension(this.getDownloadURL().getValue()));
		}
		this.format = format;
		return this;
	}

	public DCATDistribution setFormat(String format) {
		if (StringUtils.isBlank(format)) {
			format = CommonUtil.extractFormatFromFileExtension(this.getDownloadURL().getValue());
		}
		return setFormat(new DCATProperty(DCTerms.format, DCTerms.MediaTypeOrExtent.getURI(), format));
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "licenseDocument_id")
	public DCTLicenseDocument getLicense() {
		return license;
	}

	public void setLicense(DCTLicenseDocument license) {
		this.license = (license != null) ? license
				: new DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID);
	}

	// public void setLicense(String license) {
	// setLicense(license != null ? new DCTLicenseDocument(DCTerms.license.getURI(),
	// license, "", "", nodeID)
	// : new DCTLicenseDocument(DCTerms.license.getURI(), "", "", "", nodeID));
	// }

	public void setLicense_uri(String uri) {
		if (license == null)
			setLicense(null);
		if (StringUtils.isNotBlank(uri))
			license.setUri(uri);
	}

	public void setLicense_name(String name) {
		if (license == null)
			setLicense(null);
		if (StringUtils.isNotBlank(name))
			license.setName(name);
	}

	public void setLicense_type(String type) {
		if (license == null)
			setLicense(null);
		if (StringUtils.isNotBlank(type))
			license.setType(type);
	}

	public void setLicense_versionInfo(String versionInfo) {
		if (license == null)
			setLicense(null);
		if (StringUtils.isNotBlank(versionInfo))
			license.setVersionInfo(versionInfo);
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "byteSize")) })
	public DCATProperty getByteSize() {
		return byteSize;
	}

	public void setByteSize(DCATProperty byteSize) {
		this.byteSize = byteSize;
	}

	public void setByteSize(String byteSize) {
		setByteSize(new DCATProperty(DCAT.byteSize, RDFS.Literal.getURI(), byteSize));
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "releaseDate", columnDefinition = "varchar(255) default '1970-01-01T00:00:00Z'")) })
	public DCATProperty getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(DCATProperty releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		setReleaseDate(new DCATProperty(DCTerms.issued, RDFS.Literal.getURI(), releaseDate));
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "updateDate", columnDefinition = "varchar(255) default '1970-01-01T00:00:00Z'")) })
	public DCATProperty getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(DCATProperty updateDate) {
		this.updateDate = updateDate;
	}

	public void setUpdateDate(String updateDate) {
		setUpdateDate(new DCATProperty(DCTerms.modified, RDFS.Literal.getURI(), updateDate));
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
			@AttributeOverride(name = "value", column = @Column(name = "documentation", columnDefinition = "LONGTEXT")) })
	public List<DCATProperty> getDocumentation() {
		return documentation;
	}

	public void setDocumentation(List<DCATProperty> documentation) {
		this.documentation = documentation;
	}

	public void setDocumentation(String documentation) {
		setDocumentation(Arrays.asList(new DCATProperty(FOAF.page, FOAF.Document.getURI(), documentation)));
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "downloadURL", length = 65535, columnDefinition = "Text")) })
	public DCATProperty getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(DCATProperty downloadURL) {
		this.downloadURL = downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		setDownloadURL(new DCATProperty(DCAT.downloadURL, RDFS.Resource.getURI(), downloadURL));
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_distribution_language", joinColumns = {
			@JoinColumn(name = "distribution_id", referencedColumnName = "id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "language")) })
	public List<DCATProperty> getLanguage() {
		return language;
	}

	public void setLanguage(List<DCATProperty> language) {
		this.language = language;
	}

	public void setLanguage(String language) {
		setLanguage(Arrays.asList(new DCATProperty(DCTerms.language, DCTerms.LinguisticSystem.getURI(), language)));
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	// @Fetch(FetchMode.SELECT)
	@JoinColumns({ @JoinColumn(name = "distribution_id", referencedColumnName = "id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	public List<DCTStandard> getLinkedSchemas() {
		return linkedSchemas;
	}

	public void setLinkedSchemas(List<DCTStandard> linkedSchemas) {
		this.linkedSchemas = linkedSchemas;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "rights")) })
	public DCATProperty getRights() {
		return rights;
	}

	public void setRights(DCATProperty rights) {
		this.rights = rights;
	}

	public void setRights(String rights) {
		setRights(new DCATProperty(DCTerms.rights, DCTerms.RightsStatement.getURI(), rights));
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumns({ @JoinColumn(name = "status_id", referencedColumnName = "concept_id") })
	@Where(clause="type='3'")
	public SKOSConceptStatus getStatus() {
		return status;
	}

	public void setStatus(SKOSConceptStatus status) {
		this.status = status;
	}

	public void setStatus(String status) {
		setStatus(new SKOSConceptStatus("http://www.w3.org/ns/adms#status", "",
				Arrays.asList(new SKOSPrefLabel("", status, nodeID)), nodeID));
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "checksum_id")
	public SPDXChecksum getChecksum() {
		return checksum;
	}

	public void setChecksum(SPDXChecksum checksum) {
		this.checksum = checksum;
	}

	public void setChecksum(String checksum) {
		setChecksum(new SPDXChecksum("http://spdx.org/rdf/terms#checksum", "checksumAlgorithm_sha1", checksum, nodeID));
	}

	public boolean isHasDatalets() {
		return hasDatalets;
	}

	public void setHasDatalets(boolean hasDatalets) {
		this.hasDatalets = hasDatalets;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "orion_id")
	public OrionDistributionConfig getOrionDistributionConfig() {
		return orionDistributionConfig;
	}

	public void setOrionDistributionConfig(OrionDistributionConfig orionDistributionConfig) {
		this.orionDistributionConfig = orionDistributionConfig;
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
	// Integer.parseInt(x.getTitle().split("_")[1])).collect(Collectors.summarizingInt(Integer::intValue)).getMax()+1;
	// datalet.setTitle("Datalet_"+newID);
	// datalet.setCustomTitle(true);
	// }else {
	// datalet.setCustomTitle(false);
	// }
	//
	// this.datalets.add(datalet);
	//
	// }

	@Transient
	public boolean isRDF() {
		return ((this.format != null
				&& (this.format.getValue().equals("RDF") || this.format.getValue().equals("application/rdf+xml")))

				|| (this.mediaType != null && (this.mediaType.getValue().equals("RDF")
						|| this.mediaType.getValue().equals("application/rdf+xml"))));

	}

	public SolrInputDocument toDoc() {

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id);
		doc.addField("content_type", CacheContentType.distribution.toString());
		doc.addField("nodeID", nodeID);
		doc.addField("storedRDF", storedRDF);
		String desc_tmp = description != null ? description.getValue() : "";
		try {
			while (desc_tmp.getBytes("UTF-8").length >= 32766) {
				desc_tmp = desc_tmp.substring(0, (int) Math.ceil(desc_tmp.length() * (0.9))).trim();
			}
			this.description.setValue(desc_tmp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		doc.addField("accessURL", accessURL.getValue());
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
		if (byteSize != null)
			doc.addField("byteSize", byteSize.getValue());

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
		doc.addField("downloadURL", downloadURL.getValue());
		// doc.addField("language", language.getValue());

		if (documentation != null && !documentation.isEmpty())
			doc.addField("documentation", documentation.stream().filter(item -> item != null)
					.map(item -> item.getValue()).collect(Collectors.toList()));
		if (language != null && !language.isEmpty())
			doc.addField("language", language.stream().filter(item -> item != null).map(item -> item.getValue())
					.collect(Collectors.toList()));

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
		if (mediaType != null)
			doc.addField("mediaType", mediaType.getValue());

		if (releaseDate!=null && StringUtils.isNotBlank(releaseDate.getValue()))
			doc.addField("releaseDate", releaseDate.getValue());
		if (releaseDate!=null && StringUtils.isNotBlank(updateDate.getValue()))
			doc.addField("updateDate", updateDate.getValue());
		if (rights != null)
			doc.addField("rights", rights.getValue());
		if(title!=null)
			doc.addField("title", title.getValue());

		if (status != null) {
			try {
				doc.addField("status", GsonUtil.obj2Json(status, GsonUtil.conceptType));
			} catch (GsonUtilException e) {
				e.printStackTrace();
			}
		}

		return doc;

	}

	public static DCATDistribution docToDCATDistribution(SolrDocument doc) {

		String nodeID = doc.getFieldValue("nodeID").toString();
		String distrib_issued = doc.getOrDefault("releaseDate", "").toString();
		if (StringUtils.isNotBlank(distrib_issued))
			distrib_issued = CommonUtil.toUtcDate(distrib_issued);
		String distrib_modified = doc.getOrDefault("updateDate", "").toString();
		if (StringUtils.isNotBlank(distrib_modified))
			distrib_modified = CommonUtil.toUtcDate(distrib_modified);

		List<SolrDocument> childDocs = doc.getChildDocuments();
		DCTLicenseDocument license = null;
		SPDXChecksum checksum = null;
		List<DCTStandard> linkedSchemas = new ArrayList<DCTStandard>();
		SKOSConceptStatus status = null;
		List<Datalet> datalets = new ArrayList<Datalet>();
		if (null != childDocs) {

			for (SolrDocument child : childDocs) {

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.licenseDocument.toString())) {
					license = DCTLicenseDocument.docToDCTLicenseDocument(child, nodeID);
				}

				// if (child.containsKey("content_type")
				// &&
				// child.getFieldValue("content_type").equals(CacheContentType.linkedSchemas.toString()))
				// {
				// linkedSchemas.add(DCTStandard.docToDCATStandard(child, nodeID));
				// }

				if (child.containsKey("content_type")
						&& child.getFieldValue("content_type").equals(CacheContentType.checksum.toString())) {
					checksum = SPDXChecksum.docToSPDXChecksum(child, "http://spdx.org/rdf/terms#checksum", nodeID);
				}

			}
		}

		if (doc.getFieldValue("checksum") != null) {
			checksum = SPDXChecksum.jsonToSPDXChecksum(new JSONObject(doc.getFieldValue("checksum").toString()),
					"http://spdx.org/rdf/terms#checksum", nodeID);
		}

		if (doc.getFieldValue("license") != null) {
			license = DCTLicenseDocument
					.jsonToDCTLicenseDocument(new JSONObject(doc.getFieldValue("license").toString()), nodeID);
		}

		if (doc.getFieldValue("status") != null) {
			status = SKOSConceptStatus.jsonToSKOSConcept(new JSONObject(doc.getFieldValue("status").toString()),"http://www.w3.org/ns/adms#status", nodeID);
		}

		// try {
		// license = GsonUtil.json2Obj(doc.getFieldValue("license").toString(),
		// GsonUtil.licenseType);
		// } catch (GsonUtilException e) {
		// e.printStackTrace();
		// }

		if (doc.getFieldValue("linkedSchemas") != null)
			linkedSchemas = DCTStandard
					.jsonArrayToDCATStandardList(new JSONArray(doc.getFieldValue("linkedSchemas").toString()), nodeID);

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
		if(doc.getFieldValue("byteSize") !=null) {
			byteSize=doc.getFieldValue("byteSize").toString();
		}
		
		DCATDistribution distr = new DCATDistribution(doc.getFieldValue("id").toString(),
				doc.getFieldValue("nodeID").toString(), doc.getFieldValue("accessURL").toString(),
				doc.getFieldValue("description").toString(), doc.getFieldValue("format").toString(), license,
				byteSize, checksum,
				(ArrayList<String>) doc.getFieldValue("documentation"), doc.getFieldValue("downloadURL").toString(),
				(ArrayList<String>) doc.getFieldValue("language"), linkedSchemas,
				(doc.getFieldValue("mediaType")!=null)?doc.getFieldValue("mediaType").toString():"", distrib_issued, distrib_modified,
				(doc.getFieldValue("rights")!=null)?doc.getFieldValue("rights").toString():"", status, doc.getFieldValue("title").toString(),
				(Boolean) doc.getFieldValue("hasDatalets"));
		// datalets);
		distr.setStoredRDF((Boolean) doc.getFieldValue("storedRDF"));
		return distr;
	}

	@Override
	public String toString() {
		return "DCATDistribution [id=" + id + ", datalets= " + hasDatalets + "]";
	}

}
