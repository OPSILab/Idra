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

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONObject;

import it.eng.idra.cache.CacheContentType;

@Entity
@Table(name = "dcat_checksum")
public class SPDXChecksum {

	private transient static final Resource RDFClass = ResourceFactory
			.createResource("http://spdx.org/rdf/terms#Checksum");

	private String id;
	private transient String nodeID;
	private String uri;
	private DCATProperty algorithm;
	private DCATProperty checksumValue;

	public SPDXChecksum() {
	}

	public SPDXChecksum(String uri, String algorithm, String checksumValue, String nodeID) {
		super();
		this.nodeID = nodeID;
		setUri(uri);
		setAlgorithm(new DCATProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#algorithm"),
				"http://spdx.org/rdf/terms#checksumAlgorithm_sha1", algorithm));
		setChecksumValue(new DCATProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksumValue"),
				RDFS.Literal.getURI(), checksumValue));
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "checksum_id")
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
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = StringUtils.isNotBlank(uri) ? uri : "http://spdx.org/rdf/terms#checksum";
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "algorithm")) })
	public DCATProperty getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(DCATProperty algorithm) {
		this.algorithm = algorithm;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "checksumValue")) })
	public DCATProperty getChecksumValue() {
		return checksumValue;
	}

	public void setChecksumValue(DCATProperty checksumValue) {
		this.checksumValue = checksumValue;
	}

	public SolrInputDocument toDoc(CacheContentType contentType) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("algorithm", this.algorithm != null ? this.algorithm.getValue() : "");
		doc.addField("checksumValue", this.checksumValue != null ? this.checksumValue.getValue() : "");
		return doc;
	}

	public static SPDXChecksum docToSPDXChecksum(SolrDocument doc, String uri, String nodeID) {
		SPDXChecksum c = new SPDXChecksum(uri, doc.getFieldValue("algorithm").toString(),
				doc.getFieldValue("checksumValue").toString(), nodeID);
		c.setId(doc.getFieldValue("id").toString());
		return c;

	}

	public static SPDXChecksum jsonToSPDXChecksum(JSONObject doc, String uri, String nodeID) {
		return new SPDXChecksum(uri, doc.has("algorithm") ? doc.getString("algorithm") : "",
				doc.has("checksumValue") ? doc.getString("checksumValue") : "", nodeID);

	}

	@Transient
	public static Resource getRDFClass() {
		return RDFClass;
	}

	@Override
	public String toString() {
		return "SPDXChecksum [uri=" + uri + ", algorithm=" + algorithm + ", checksumValue=" + checksumValue + "]";
	}

}
