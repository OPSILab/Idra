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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.idra.cache.CacheContentType;

@Entity
@Table(name = "dcat_standard")
//@IdClass(DCTStandardId.class)
public class DCTStandard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient static final Resource RDFClass = DCTerms.Standard;

	private String uri;

	private String id;
	private transient String nodeID;
	private DCATProperty identifier;
	private DCATProperty title;
	private DCATProperty description;
	private List<DCATProperty> referenceDocumentation;

	public DCTStandard() {
	}

	public DCTStandard(String uri, String identifier, String title, String description,
			List<String> referenceDocumentation, String nodeID) {

		super();
		setUri(uri);
		this.nodeID = nodeID;
		setIdentifier(new DCATProperty(DCTerms.identifier, RDFS.Literal.getURI(), identifier));
		setTitle(new DCATProperty(DCTerms.title, RDFS.Literal.getURI(), title));
		setDescription(new DCATProperty(DCTerms.description, RDFS.Literal.getURI(), description));
		setReferenceDocumentation(
				referenceDocumentation != null
						? referenceDocumentation.stream()
								.map(item -> new DCATProperty(
										ResourceFactory.createProperty(
												"http://dati.gov.it/onto/dcatapit#referenceDocumentation"),
										RDFS.Literal.getURI(), item))
								.collect(Collectors.toList())
						: Arrays.asList(new DCATProperty(
								ResourceFactory
										.createProperty("http://dati.gov.it/onto/dcatapit#referenceDocumentation"),
								RDFS.Literal.getURI(), "")));

	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "standard_id")
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
	@AttributeOverrides({
			@AttributeOverride(name = "value", column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
	public DCATProperty getIdentifier() {
		return identifier;
	}

	public void setIdentifier(DCATProperty identifier) {
		this.identifier = identifier;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "title")) })
	public DCATProperty getTitle() {
		return title;
	}

	public void setTitle(DCATProperty title) {
		this.title = title;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "description")) })
	public DCATProperty getDescription() {
		return description;
	}

	public void setDescription(DCATProperty description) {
		this.description = description;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "dcat_standard_referencedocumentation", joinColumns = { @JoinColumn(name = "standard_id",referencedColumnName="standard_id"),
			@JoinColumn(name = "nodeID", referencedColumnName="nodeID") })
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "referenceDocumentation")) })
	public List<DCATProperty> getReferenceDocumentation() {
		return referenceDocumentation;
	}

	public void setReferenceDocumentation(List<DCATProperty> referenceDocumentation) {
		this.referenceDocumentation = referenceDocumentation;
	}

//	@Id
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
		doc.addField("uri", this.uri);
		doc.addField("identifier", this.getIdentifier().getValue());
		doc.addField("title", this.getTitle().getValue());
		doc.addField("description", this.getDescription().getValue());
		doc.addField("referenceDocumentation", this.getReferenceDocumentation().stream().filter(item -> item != null)
				.map(item -> item.getValue()).collect(Collectors.toList()));
		return doc;
	}

	public static DCTStandard docToDCATStandard(SolrDocument doc, String nodeID) {
		String uri = DCTerms.conformsTo.getURI();
		if (doc.containsKey("uri"))
			uri = doc.getFieldValue("uri").toString();
		DCTStandard s= new DCTStandard(uri, doc.getFieldValue("identifier").toString(), doc.getFieldValue("title").toString(),
				doc.getFieldValue("description").toString(),
				(ArrayList<String>) doc.getFieldValue("referenceDocumentation"), nodeID);
		s.setId(doc.getFieldValue("id").toString());
		return s;

	}

	public static List<DCTStandard> jsonArrayToDCATStandardList(JSONArray array, String nodeID) {
		String uri = DCTerms.conformsTo.getURI();
		List<DCTStandard> result = new ArrayList<DCTStandard>();

		for (int i = 0; i < array.length(); i++) {

			JSONObject obj = array.getJSONObject(i);
			if (obj.has("uri"))
				uri = obj.get("uri").toString();
			result.add(
					new DCTStandard(obj.optString("uri"), obj.optString("identifier"), obj.optString("title"),
							obj.optString("description"),
							(obj.has("referenceDocumentation"))
									? obj.getJSONArray("referenceDocumentation").toList().stream()
											.map(item -> ((String) item)).collect(Collectors.toList())
									: Arrays.asList(""),
							nodeID));
		}

		return result;
	}

	@Override
	public String toString() {
		return "DCTStandard [uri=" + uri + ", id=" + id + ", identifier=" + identifier + ", title=" + title
				+ ", description=" + description + ", referenceDocumentation=" + referenceDocumentation + "]";
	}

}
