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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD4;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

import it.eng.idra.cache.CacheContentType;

/**
 * Represents a DCAT VCard, the dataset contacts information
 *
 * @author
 * @version 1.1
 * @since
 */

@Entity
@Table(name = "dcat_vcard")
public class VCardOrganization {

	private static transient final Resource RDFClass = VCARD4.Organization;

	private String id;

	private String resourceUri;
	private String propertyUri;
	private DCATProperty fn;
	private String nodeID;
	private DCATProperty hasEmail;
	private DCATProperty hasURL;
	private DCATProperty hasTelephoneValue;
	private DCATProperty hasTelephoneType;

	public VCardOrganization() {
	}

	public VCardOrganization(String propertyUri, String resourceUri, String fn, String hasEmail, String hasURL,
			String hasTelephoneValue, String hasTelephoneType, String nodeID) {
		setResourceUri(resourceUri);
		setNodeID(nodeID);
		setPropertyUri(propertyUri);
		setFn(new DCATProperty(VCARD4.fn, RDFS.Literal.getURI(), fn));
		hasEmail = (hasEmail != null) ? hasEmail.trim() : hasEmail;
		setHasEmail(new DCATProperty(VCARD4.hasEmail, RDFS.Literal.getURI(), hasEmail));
		setHasURL(new DCATProperty(VCARD4.hasURL, RDFS.Literal.getURI(), hasURL));
		setHasTelephoneValue(new DCATProperty(VCARD4.value, RDFS.Literal.getURI(), hasTelephoneValue));
		setHasTelephoneType(new DCATProperty(RDF.type, RDFS.Literal.getURI(), hasTelephoneType));
	}

	public VCardOrganization(String id, String propertyUri, String resourceUri, String fn, String hasEmail,
			String hasURL, String hasTelephoneValue, String hasTelephoneType, String nodeID) {
		this(propertyUri, resourceUri, fn, hasEmail, hasURL, hasTelephoneValue, hasTelephoneType, nodeID);
		this.setId(id);
	}

	@Transient
	public static Resource getRDFClass() {
		return RDFClass;
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "vcard_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResourceUri() {
		return resourceUri;
	}

	public void setResourceUri(String resourceUri) {
		// this.resourceUri = resourceUri != null ? resourceUri : "";
		this.resourceUri = resourceUri;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	@Transient
	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String propertUri) {
		this.propertyUri = propertUri != null ? propertUri : "";
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "fn",length=500)) })
	public DCATProperty getFn() {
		return fn;
	}

	public void setFn(DCATProperty fn) {
		this.fn = fn;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasEmail")) })
	public DCATProperty getHasEmail() {
		return hasEmail;
	}

	public void setHasEmail(DCATProperty hasEmail) {
		this.hasEmail = hasEmail;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasURL")) })
	public DCATProperty getHasURL() {
		return hasURL;
	}

	public void setHasURL(DCATProperty hasURL) {
		this.hasURL = hasURL;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasTelephoneValue")) })
	public DCATProperty getHasTelephoneValue() {
		return hasTelephoneValue;
	}

	public void setHasTelephoneValue(DCATProperty hasTelephone) {
		this.hasTelephoneValue = hasTelephone;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasTelephoneType")) })

	public DCATProperty getHasTelephoneType() {
		return hasTelephoneType;
	}

	public void setHasTelephoneType(DCATProperty hasTelephoneType) {
		this.hasTelephoneType = hasTelephoneType;
	}

	public SolrInputDocument toDoc(CacheContentType contentType) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("resourceUri", this.resourceUri);
		doc.addField("fn", this.getFn().getValue());
		doc.addField("hasEmail", this.getHasEmail().getValue());
		doc.addField("hasURL", this.getHasURL().getValue());
		doc.addField("hasTelephoneValue", this.getHasTelephoneValue().getValue());
		doc.addField("hasTelephoneType", this.getHasTelephoneType().getValue());
		return doc;
	}

	public static VCardOrganization docToVCardOrganization(SolrDocument doc, String propertyUri, String nodeID) {
		return new VCardOrganization((String) doc.getFieldValue("id"), propertyUri,
				(String) doc.getFieldValue("resourceUri"), doc.getFieldValue("fn").toString(),
				doc.getFieldValue("hasEmail").toString(), doc.getFieldValue("hasURL").toString(),
				doc.getFieldValue("hasTelephoneValue").toString(), doc.getFieldValue("hasTelephoneType").toString(),
				nodeID);
	}

	@Override
	public String toString() {
		return "VCardOrganization [id=" + id + ", fn=" + fn + ", nodeID=" + nodeID + ", hasEmail=" + hasEmail
				+ ", hasURL=" + hasURL + ", hasTelephone=" + hasTelephoneValue + "]";
	}

}
