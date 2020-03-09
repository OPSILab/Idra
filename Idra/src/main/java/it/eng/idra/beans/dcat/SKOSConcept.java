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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;

/**
 * Represents a SKOS Concept, e.g SKOSConceptTheme or SKOSConceptSubject
 * 
 * @author
 * @version 1.1
 * @since
 */

@Entity
@DiscriminatorColumn(name = "type",discriminatorType=DiscriminatorType.STRING)
@Table(name = "dcat_concept")
public class SKOSConcept implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	
	// Mandatory
	private DCATProperty name;

	// Recommended

	private String resourceUri;
	private String propertyUri;
	private List<SKOSPrefLabel> prefLabel;

	private transient String nodeID;

	public SKOSConcept() {
	}

	private transient static final Resource RDFClass = SKOS.Concept;

	public SKOSConcept(String propertyUri, String resourceUri, List<SKOSPrefLabel> prefLabel, String nodeID) {

		setNodeID(nodeID);
		setPropertyUri(propertyUri);
		setResourceUri(resourceUri);
		setPrefLabel(prefLabel != null ? prefLabel : Arrays.asList(new SKOSPrefLabel()));
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
	@Column(name = "concept_id")
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

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	// @Fetch(FetchMode.SELECT)
	@JoinColumns({ @JoinColumn(name = "concept_id", referencedColumnName = "concept_id"),
			@JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
	public List<SKOSPrefLabel> getPrefLabel() {
		return prefLabel;
	}

	public void setPrefLabel(List<SKOSPrefLabel> prefLabel) {
		this.prefLabel = prefLabel;
	}


	public SolrInputDocument toDoc(CacheContentType contentType) {

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("resourceUri", this.resourceUri);

		if (prefLabel != null && !prefLabel.isEmpty()) {
			try {
				doc.addField("prefLabel", GsonUtil.obj2Json(prefLabel, GsonUtil.prefLabelListType));
			} catch (GsonUtilException e) {
				e.printStackTrace();
			}
		}

		return doc;
	}

	public static SKOSConcept jsonToSKOSConcept(JSONObject obj, String propertyUri, String nodeID) {

		return new SKOSConcept(propertyUri, obj.optString("resourceUri"),
				SKOSPrefLabel.jsonArrayToPrefLabelList(obj.getJSONArray("prefLabel"), nodeID), nodeID);
	}

	public static SKOSConcept docToSKOSConcept(SolrDocument doc, String propertyUri, String nodeID) {

		return new SKOSConcept(propertyUri, (String) doc.getFieldValue("resourceUri"), SKOSPrefLabel
				.jsonArrayToPrefLabelList(new JSONArray(doc.getFieldValue("prefLabel").toString()), nodeID), nodeID);
	}

	@Override
	public String toString() {
		return "SKOSConcept [id=" + id + ", name=" + name + ", resourceUri=" + resourceUri + ", propertyUri="
				+ propertyUri + ", prefLabel=" + prefLabel + "]";
	}

}
