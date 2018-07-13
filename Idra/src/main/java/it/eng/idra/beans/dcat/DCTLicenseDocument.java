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

import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONObject;

import it.eng.idra.cache.CacheContentType;

@Entity
@Table(name = "dcat_licenseDocument")
public class DCTLicenseDocument {

	private transient static final Resource RDFClass = DCTerms.LicenseDocument;

	private String id;
	private transient String nodeID;

	private String uri;
	private DCATProperty name;
	private DCATProperty type;
	private DCATProperty versionInfo;

	public DCTLicenseDocument() {
	}

	public DCTLicenseDocument(String uri, String name, String type, String versionInfo, String nodeID) {

		setUri(uri);
		this.nodeID = nodeID;
		setName(new DCATProperty(FOAF.name, RDFS.Literal.getURI(), name));
		setVersionInfo(new DCATProperty(OWL.versionInfo, RDFS.Literal.getURI(), versionInfo));
		setType(new DCATProperty(DCTerms.type, SKOS.Concept.getURI(), type));
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "licenseDocument_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "name")) })
	public DCATProperty getName() {
		return name;
	}

	public void setName(DCATProperty name) {
		this.name = name;
	}

	public void setName(String name) {
		setName(new DCATProperty(FOAF.name, RDFS.Literal.getURI(), name));
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "type")) })
	public DCATProperty getType() {
		return type;
	}

	public void setType(DCATProperty type) {
		this.type = type;
	}

	public void setType(String type) {
		setType(new DCATProperty(DCTerms.type, SKOS.Concept.getURI(), type));
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "versionInfo")) })
	public DCATProperty getVersionInfo() {
		return versionInfo;
	}

	public void setVersionInfo(DCATProperty versionInfo) {
		this.versionInfo = versionInfo;
	}

	public void setVersionInfo(String versionInfo) {
		setVersionInfo(new DCATProperty(OWL.versionInfo, RDFS.Literal.getURI(), versionInfo));
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = StringUtils.isNotBlank(uri) ? uri : DCTerms.license.getURI();
	}

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

	public SolrInputDocument toDoc(CacheContentType contentType) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", UUID.randomUUID().toString());
		doc.addField("content_type", contentType.toString());
		doc.addField("uri", StringUtils.isNotBlank(this.uri) ? this.uri : "");
		doc.addField("name", this.name != null ? this.name.getValue() : "");
		doc.addField("type", this.type != null ? this.type.getValue() : "");
		doc.addField("versionInfo", this.versionInfo != null ? this.versionInfo.getValue() : "");
		return doc;

	}

	public static DCTLicenseDocument jsonToDCTLicenseDocument(JSONObject json, String nodeID) {
		return new DCTLicenseDocument(json.optString("uri"), json.optString("name"), json.optString("type"),
				json.optString("versionInfo"), nodeID);
	}

	public static DCTLicenseDocument docToDCTLicenseDocument(SolrDocument doc, String nodeID) {
		return new DCTLicenseDocument(doc.getFieldValue("uri").toString(), doc.getFieldValue("name").toString(),
				doc.getFieldValue("type").toString(), doc.getFieldValue("versionInfo").toString(), nodeID);
	}

	@Override
	public String toString() {
		return "DCTLicenseDocument [id=" + id + ", uri=" + uri + ", name=" + name + ", type=" + type + ", versionInfo="
				+ versionInfo + "]";
	}

}
