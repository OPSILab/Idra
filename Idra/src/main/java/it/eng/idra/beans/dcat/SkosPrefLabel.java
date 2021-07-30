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
import it.eng.idra.cache.CacheContentType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a SKOS Concept label in a specific language.
 * 
 * @author
 */

@Entity
@Table(name = "dcat_concept_prefLabel")
public class SkosPrefLabel {

  private String id;
  
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  private String language;
  private String value;

  public SkosPrefLabel() {
  }

  private static final transient Resource RDFClass = SKOS.Concept;

  /**
   * Instantiates a new skos pref label.
   *
   * @param language the language
   * @param value the value
   * @param nodeId the node ID
   */
  public SkosPrefLabel(String language, String value, String nodeId) {

    setNodeId(nodeId);
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

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  @Transient
  public static Resource getRdfClass() {
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

  /**
   * To doc.
   *
   * @param contentType the content type
   * @return the solr input document
   */
  public SolrInputDocument toDoc(CacheContentType contentType) {

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", this.id);
    doc.addField("nodeID", this.nodeId);
    doc.addField("content_type", contentType.toString());
    doc.addField("value", this.getValue());
    doc.addField("language", this.getLanguage());
    return doc;
  }

  /**
   * Doc to skos pref label.
   *
   * @param doc the doc
   * @param propertyUri the property uri
   * @param nodeId the node id
   * @return the skos pref label
   */
  public static SkosPrefLabel docToSkosPrefLabel(SolrDocument doc, 
      String propertyUri, String nodeId) {
    SkosPrefLabel s = new SkosPrefLabel((String) 
        doc.getFieldValue("language"), doc.getFieldValue("value").toString(),
        nodeId);
    s.setId(doc.getFieldValue("id").toString());
    return s;
  }

  /**
   * Json array to pref label list.
   *
   * @param array the array
   * @param nodeId the node ID
   * @return the list
   */
  public static List<SkosPrefLabel> jsonArrayToPrefLabelList(JSONArray array, String nodeId) {

    List<SkosPrefLabel> result = new ArrayList<SkosPrefLabel>();

    for (int i = 0; i < array.length(); i++) {
      JSONObject prefLabel = array.getJSONObject(i);
      result.add(new SkosPrefLabel((prefLabel.has("language")) 
          ? prefLabel.getString("language") : "",
          (prefLabel.has("value")) ? prefLabel.getString("value") : "", nodeId));
    }

    return result;
  }

  @Override
  public String toString() {
    return "SKOSPrefLabel [id=" + id + ", language=" + language + ", value=" + value + "]";
  }

}
