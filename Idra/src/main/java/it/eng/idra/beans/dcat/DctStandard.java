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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

// TODO: Auto-generated Javadoc
/**
 * The Class DctStandard.
 */
@Entity
@Table(name = "dcat_standard")
//@IdClass(DCTStandardId.class)
public class DctStandard implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = DCTerms.Standard;

  /** The uri. */
  private String uri;

  /** The id. */
  private String id;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /** The identifier. */
  private DcatProperty identifier;

  /** The title. */
  private DcatProperty title;

  /** The description. */
  private DcatProperty description;

  /** The reference documentation. */
  private List<DcatProperty> referenceDocumentation;

  /**
   * Instantiates a new dct standard.
   */
  public DctStandard() {
  }

  /**
   * Instantiates a new dct standard.
   *
   * @param uri                    the uri
   * @param identifier             the identifier
   * @param title                  the title
   * @param description            the description
   * @param referenceDocumentation the reference documentation
   * @param nodeId                 the node ID
   */
  public DctStandard(String uri, String identifier, String title, String description,
      List<String> referenceDocumentation, String nodeId) {

    super();
    setUri(uri);
    this.nodeId = nodeId;
    setIdentifier(new DcatProperty(DCTerms.identifier, RDFS.Literal, identifier));
    setTitle(new DcatProperty(DCTerms.title, RDFS.Literal, title));
    setDescription(new DcatProperty(DCTerms.description, RDFS.Literal, description));
    setReferenceDocumentation(
        referenceDocumentation != null
            ? referenceDocumentation.stream()
                .map(item -> new DcatProperty(
                    ResourceFactory
                        .createProperty("http://dati.gov.it/onto/dcatapit#referenceDocumentation"),
                    RDFS.Literal, item))
                .collect(Collectors.toList())
            : Arrays
                .asList(new DcatProperty(
                    ResourceFactory
                        .createProperty("http://dati.gov.it/onto/dcatapit#referenceDocumentation"),
                    RDFS.Literal, "")));

  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "standard_id")
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
   * Gets the uri.
   *
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets the uri.
   *
   * @param uri the new uri
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Gets the identifier.
   *
   * @return the identifier
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", 
          column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
  public DcatProperty getIdentifier() {
    return identifier;
  }

  /**
   * Sets the identifier.
   *
   * @param identifier the new identifier
   */
  public void setIdentifier(DcatProperty identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "title")) })
  public DcatProperty getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(DcatProperty title) {
    this.title = title;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "description")) })
  public DcatProperty getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(DcatProperty description) {
    this.description = description;
  }

  /**
   * Gets the reference documentation.
   *
   * @return the reference documentation
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_standard_referencedocumentation", joinColumns = {
      @JoinColumn(name = "standard_id", referencedColumnName = "standard_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "referenceDocumentation")) })
  public List<DcatProperty> getReferenceDocumentation() {
    return referenceDocumentation;
  }

  /**
   * Sets the reference documentation.
   *
   * @param referenceDocumentation the new reference documentation
   */
  public void setReferenceDocumentation(List<DcatProperty> referenceDocumentation) {
    this.referenceDocumentation = referenceDocumentation;
  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  // @Id
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
    doc.addField("uri", this.uri);
    doc.addField("identifier", this.getIdentifier().getValue());
    doc.addField("title", this.getTitle().getValue());
    doc.addField("description", this.getDescription().getValue());
    doc.addField("referenceDocumentation", this.getReferenceDocumentation().stream()
        .filter(item -> item != null).map(item -> item.getValue()).collect(Collectors.toList()));
    return doc;
  }

  /**
   * Doc to dcat standard.
   *
   * @param doc    the doc
   * @param nodeId the node id
   * @return the dct standard
   */
  public static DctStandard docToDcatStandard(SolrDocument doc, String nodeId) {
    String uri = DCTerms.conformsTo.getURI();
    if (doc.containsKey("uri")) {
      uri = doc.getFieldValue("uri").toString();
    }
    DctStandard s = new DctStandard(uri, doc.getFieldValue("identifier").toString(),
        doc.getFieldValue("title").toString(), doc.getFieldValue("description").toString(),
        (ArrayList<String>) doc.getFieldValue("referenceDocumentation"), nodeId);
    s.setId(doc.getFieldValue("id").toString());
    return s;

  }

  /**
   * Json array to dcat standard list.
   *
   * @param array  the array
   * @param nodeId the node id
   * @return the list
   */
  public static List<DctStandard> jsonArrayToDcatStandardList(JSONArray array, String nodeId) {
    String uri = DCTerms.conformsTo.getURI();
    List<DctStandard> result = new ArrayList<DctStandard>();

    for (int i = 0; i < array.length(); i++) {

      JSONObject obj = array.getJSONObject(i);
      if (obj.has("uri")) {
        uri = obj.get("uri").toString();
      }
      result.add(new DctStandard(obj.optString("uri"), obj.optString("identifier"),
          obj.optString("title"), obj.optString("description"),
          (obj.has("referenceDocumentation")) ? obj.getJSONArray("referenceDocumentation").toList()
              .stream().map(item -> ((String) item)).collect(Collectors.toList())
              : Arrays.asList(""),
          nodeId));
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCTStandard [uri=" + uri + ", id=" + id + ", identifier=" + identifier + ", title="
        + title + ", description=" + description + ", referenceDocumentation="
        + referenceDocumentation + "]";
  }

}
