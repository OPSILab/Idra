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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

import it.eng.idra.cache.CacheContentType;

/**
 * Represents a DCAT Agent, the Dataset publisher
 *
 * @author
 * @version 1.1
 * @since
 */

@Entity
@Table(name = "dcat_agent")
public class FOAFAgent {

	private String id;

	// Mandatory
	private DCATProperty name;

	// Recommended

	private String resourceUri;
	private String propertyUri;
	private DCATProperty mbox;
	private DCATProperty homepage;
	private DCATProperty type;
	private DCATProperty identifier;
	private transient String nodeID;

	public FOAFAgent() {
	}

	private transient static final Resource RDFClass = FOAF.Agent;

	// public FOAFAgent(String name, String mbox, String homepage, String type,
	// String nodeID) {
	//
	// setNodeID(nodeID);
	//
	// setName(new DCATProperty(FOAF.name, RDFS.Literal.getURI(), name));
	// setMbox(new DCATProperty(FOAF.mbox, RDFS.Literal.getURI(), mbox));
	// setHomepage(new DCATProperty(FOAF.homepage, RDFS.Literal.getURI(),
	// homepage));
	// setType(new DCATProperty(DCTerms.type, SKOS.Concept.getURI(), type));
	//
	// }

	public FOAFAgent(String propertyUri, String resourceUri, String name, String mbox, String homepage, String type,
			String identifier, String nodeID) {

		setNodeID(nodeID);
		setPropertyUri(propertyUri);
		setResourceUri(resourceUri);
		setName(new DCATProperty(FOAF.name, RDFS.Literal.getURI(), name));
		setMbox(new DCATProperty(FOAF.mbox, RDFS.Literal.getURI(), mbox));
		setHomepage(new DCATProperty(FOAF.homepage, RDFS.Literal.getURI(), homepage));
		setType(new DCATProperty(DCTerms.type, SKOS.Concept.getURI(), type));
		setIdentifier(new DCATProperty(DCTerms.identifier, RDFS.Literal.getURI(), identifier));

	}

	/*
	 * @Id
	 * 
	 * @Column(name = "publisher_id")
	 * 
	 * @GeneratedValue(generator="increment")
	 * 
	 * @GenericGenerator(name="increment", strategy = "increment")
	 */
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "agent_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Transient
	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public String getResourceUri() {
		return resourceUri;
	}

	public void setResourceUri(String resourceUri) {
		// this.resourceUri = StringUtils.isBlank(resourceUri) ? "" :
		// resourceUri;
		this.resourceUri = resourceUri;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "name")) })
	public DCATProperty getName() {
		return name;
	}

	public void setName(DCATProperty name) {
		if (name != null)
			this.name = name;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "mbox")) })
	public DCATProperty getMbox() {
		return mbox;
	}

	public void setMbox(DCATProperty mbox) {
		this.mbox = mbox;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "homepage")) })
	public DCATProperty getHomepage() {
		return homepage;
	}

	public void setHomepage(DCATProperty homepage) {
		this.homepage = homepage;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "type")) })
	public DCATProperty getType() {
		return type;
	}

	public void setType(DCATProperty type) {
		this.type = type;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
	public DCATProperty getIdentifier() {
		return identifier;
	}

	public void setIdentifier(DCATProperty identifier) {
		this.identifier = identifier;
	}

	public SolrInputDocument toDoc(CacheContentType contentType) {

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("resourceUri", this.resourceUri);
		doc.addField("identifier", this.getIdentifier().getValue());
		doc.addField("name", this.getName().getValue());
		doc.addField("mbox", this.getMbox().getValue());
		doc.addField("homepage", this.getHomepage().getValue());
		doc.addField("type", this.getType().getValue());
		
		return doc;
	}

	public static FOAFAgent docToFOAFAgent(SolrDocument doc, String propertyUri, String nodeID) {
		FOAFAgent f = new FOAFAgent(propertyUri, (String) doc.getFieldValue("resourceUri"),
				doc.getFieldValue("name").toString(), doc.getFieldValue("mbox").toString(),
				doc.getFieldValue("homepage").toString(), doc.getFieldValue("type").toString(),
				doc.getFieldValue("identifier").toString(), nodeID);
		f.setId(doc.getFieldValue("id").toString());
		return f;
	}

	@Override
	public String toString() {
		return "FOAFAgent [id=" + id + ", name=" + name + ", propertyUri=" + propertyUri + ", mbox=" + mbox
				+ ", homepage=" + homepage + ", type=" + type + ", identifier=" + identifier + ", nodeID=" + nodeID
				+ "]";
	}

}
