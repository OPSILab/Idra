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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.idra.cache.CacheContentType;

/**
 * Represents a SKOS Concept label in a specific language
 * 
 * @author
 * @version 1.1
 * @since
 */

@Entity
@Table(name = "dcat_concept_prefLabel")
public class SKOSPrefLabel {

	private String id;
	private transient String nodeID;

	private String language;
	private String value;

	public SKOSPrefLabel() {
	}

	private transient static final Resource RDFClass = SKOS.Concept;

	public SKOSPrefLabel(String language, String value, String nodeID) {

		setNodeID(nodeID);
		setLanguage(language);
		setValue(value);
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "prefLabel_id")
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SolrInputDocument toDoc(CacheContentType contentType) {

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("value", this.getValue());
		doc.addField("language", this.getLanguage());
		return doc;
	}

	public static SKOSPrefLabel docToSKOSPrefLabel(SolrDocument doc, String propertyUri, String nodeID) {
		SKOSPrefLabel s = new SKOSPrefLabel((String) doc.getFieldValue("language"), doc.getFieldValue("value").toString(), nodeID);
		s.setId(doc.getFieldValue("id").toString());
		return s;
	}

	public static List<SKOSPrefLabel> jsonArrayToPrefLabelList(JSONArray array, String nodeID) {

		List<SKOSPrefLabel> result = new ArrayList<SKOSPrefLabel>();

		for (int i = 0; i < array.length(); i++) {
			JSONObject prefLabel = array.getJSONObject(i);
			result.add(new SKOSPrefLabel((prefLabel.has("language")) ? prefLabel.getString("language") : "",
					(prefLabel.has("value")) ? prefLabel.getString("value") : "", nodeID));
		}

		return result;
	}

	@Override
	public String toString() {
		return "SKOSPrefLabel [id=" + id + ", language=" + language + ", value=" + value + "]";
	}

}
