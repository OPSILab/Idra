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

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

import it.eng.idra.cache.CacheContentType;

@Entity
@Table(name = "dcat_location")
public class DCTLocation {

	private transient static final Resource RDFClass = DCTerms.Location;

	private String id;
	private transient String nodeID;
	private String uri;
	private DCATProperty geographicalIdentifier;
	private DCATProperty geographicalName;
	private DCATProperty geometry;

	public DCTLocation() {
	}

	public DCTLocation(String uri, String geographicalIdentifier, String geographicalName, String geometry,
			String nodeID) {
		super();
		setUri(uri);
		this.nodeID = nodeID;
		setGeographicalIdentifier(new DCATProperty(
				ResourceFactory.createProperty("http://dati.gov.it/onto/dcatapit#geographicalIdentifier"),
				RDFS.Literal.getURI(), geographicalIdentifier));
		setGeographicalName(
				new DCATProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geographicalName"),
						SKOS.Concept.getURI(), geographicalName));
		setGeometry(new DCATProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry"),
				"https://www.w3.org/ns/locn#Geometry", geometry));

	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "location_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "geographicalIdentifier")) })
	public DCATProperty getGeographicalIdentifier() {
		return geographicalIdentifier;
	}

	public void setGeographicalIdentifier(DCATProperty geographicalIdentifier) {
		this.geographicalIdentifier = geographicalIdentifier;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "geographicalName")) })
	public DCATProperty getGeographicalName() {
		return geographicalName;
	}

	public void setGeographicalName(DCATProperty geographicalName) {
		this.geographicalName = geographicalName;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "geometry")) })
	public DCATProperty getGeometry() {
		return geometry;
	}

	public void setGeometry(DCATProperty geometry) {
		this.geometry = geometry;
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
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("geographicalIdentifier",
				this.geographicalIdentifier != null ? this.geographicalIdentifier.getValue() : "");
		doc.addField("geographicalName", this.geographicalName != null ? this.geographicalName.getValue() : "");
		doc.addField("geometry", this.geometry != null ? this.geometry.getValue() : "");
		return doc;

	}

	public static DCTLocation docToDCTLocation(SolrDocument doc, String uri, String nodeID) {
		DCTLocation l = new DCTLocation(uri, doc.getFieldValue("geographicalIdentifier").toString(),
				doc.getFieldValue("geographicalName").toString(), doc.getFieldValue("geometry").toString(), nodeID);
		l.setId(doc.getFieldValue("id").toString());
		return l;

	}

	@Override
	public String toString() {
		return "DCTLocation [uri=" + uri + ", geographicalIdentifier=" + geographicalIdentifier + ", geographicalName="
				+ geographicalName + ", geometry=" + geometry + "]";
	}

}
