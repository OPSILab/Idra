/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package it.eng.idra.beans.dcat;

import com.google.gson.annotations.SerializedName;
import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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

// TODO: Auto-generated Javadoc
/**
 * Represents a SKOS Concept, e.g SKOSConceptTheme or SKOSConceptSubject.
 */

@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "dcat_concept")
public class SkosConcept implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The id. */
  private String id;

  /** The name. */
  // Mandatory
  private DcatProperty name;

  // Recommended

  /** The resource uri. */
  private String resourceUri;

  /** The property uri. */
  private String propertyUri;

  /** The pref label. */
  private List<SkosPrefLabel> prefLabel;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /**
   * Instantiates a new skos concept.
   */
  public SkosConcept() {
  }

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = SKOS.Concept;

  /**
   * Instantiates a new skos concept.
   *
   * @param propertyUri the property uri
   * @param resourceUri the resource uri
   * @param prefLabel   the pref label
   * @param nodeId      the node ID
   */
  public SkosConcept(String propertyUri, String resourceUri, List<SkosPrefLabel> prefLabel,
      String nodeId) {

    setNodeId(nodeId);
    setPropertyUri(propertyUri);
    setResourceUri(resourceUri);
    setPrefLabel(prefLabel != null ? prefLabel : Arrays.asList(new SkosPrefLabel()));
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
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

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Gets the rdf class.
   *
   * @return the rdf class
   */
  @Transient
  public static Resource getRdfClass() {
    return RDFClass;
  }

  /**
   * Gets the property uri.
   *
   * @return the property uri
   */
  @Transient
  public String getPropertyUri() {
    return propertyUri;
  }

  /**
   * Sets the property uri.
   *
   * @param propertyUri the new property uri
   */
  public void setPropertyUri(String propertyUri) {
    this.propertyUri = propertyUri;
  }

  /**
   * Gets the resource uri.
   *
   * @return the resource uri
   */
  public String getResourceUri() {
    return resourceUri;
  }

  /**
   * Sets the resource uri.
   *
   * @param resourceUri the new resource uri
   */
  public void setResourceUri(String resourceUri) {
    // this.resourceUri = StringUtils.isBlank(resourceUri) ? "" :
    // resourceUri;
    this.resourceUri = resourceUri;
  }

  /**
   * Gets the pref label.
   *
   * @return the pref label
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  // @Fetch(FetchMode.SELECT)
  @JoinColumns({ @JoinColumn(name = "concept_id", referencedColumnName = "concept_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  public List<SkosPrefLabel> getPrefLabel() {
    return prefLabel;
  }

  /**
   * Sets the pref label.
   *
   * @param prefLabel the new pref label
   */
  public void setPrefLabel(List<SkosPrefLabel> prefLabel) {
    this.prefLabel = prefLabel;
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

  /**
   * Json to skos concept.
   *
   * @param obj         the obj
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the skos concept
   */
  public static SkosConcept jsonToSkosConcept(JSONObject obj, String propertyUri, String nodeId) {

    return new SkosConcept(propertyUri, obj.optString("resourceUri"),
        SkosPrefLabel.jsonArrayToPrefLabelList(obj.getJSONArray("prefLabel"), nodeId), nodeId);
  }

  /**
   * Doc to skos concept.
   *
   * @param doc         the doc
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the skos concept
   */
  public static SkosConcept docToSkosConcept(SolrDocument doc, String propertyUri, String nodeId) {

    return new SkosConcept(propertyUri, (String) doc.getFieldValue("resourceUri"), SkosPrefLabel
        .jsonArrayToPrefLabelList(new JSONArray(doc.getFieldValue("prefLabel").toString()), nodeId),
        nodeId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SKOSConcept [id=" + id + ", name=" + name + ", resourceUri=" + resourceUri
        + ", propertyUri=" + propertyUri + ", prefLabel=" + prefLabel + "]";
  }

}
