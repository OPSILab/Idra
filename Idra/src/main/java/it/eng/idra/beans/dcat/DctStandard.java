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

@Entity
@Table(name = "dcat_standard")
//@IdClass(DCTStandardId.class)
public class DctStandard implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final transient Resource RDFClass = DCTerms.Standard;

  private String uri;

  private String id;
  private transient String nodeID;
  private DcatProperty identifier;
  private DcatProperty title;
  private DcatProperty description;
  private List<DcatProperty> referenceDocumentation;

  public DctStandard() {
  }

  /**
   * Instantiates a new dct standard.
   *
   * @param uri the uri
   * @param identifier the identifier
   * @param title the title
   * @param description the description
   * @param referenceDocumentation the reference documentation
   * @param nodeID the node ID
   */
  public DctStandard(String uri, String identifier, 
      String title, String description,
      List<String> referenceDocumentation,
      String nodeID) {

    super();
    setUri(uri);
    this.nodeID = nodeID;
    setIdentifier(new DcatProperty(DCTerms.identifier, RDFS.Literal, identifier));
    setTitle(new DcatProperty(DCTerms.title, RDFS.Literal, title));
    setDescription(new DcatProperty(DCTerms.description, RDFS.Literal, description));
    setReferenceDocumentation(referenceDocumentation != null
        ? referenceDocumentation.stream()
            .map(item -> new DcatProperty(
                ResourceFactory.createProperty("http://dati.gov.it/onto/dcatapit#referenceDocumentation"), RDFS.Literal,
                item))
            .collect(Collectors.toList())
        : Arrays.asList(
            new DcatProperty(ResourceFactory.createProperty("http://dati.gov.it/onto/dcatapit#referenceDocumentation"),
                RDFS.Literal, "")));

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
      @AttributeOverride(name = "value", 
          column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
  public DcatProperty getIdentifier() {
    return identifier;
  }

  public void setIdentifier(DcatProperty identifier) {
    this.identifier = identifier;
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "title")) })
  public DcatProperty getTitle() {
    return title;
  }

  public void setTitle(DcatProperty title) {
    this.title = title;
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value",
        column = @Column(name = "description")) })
  public DcatProperty getDescription() {
    return description;
  }

  public void setDescription(DcatProperty description) {
    this.description = description;
  }

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_standard_referencedocumentation", joinColumns = {
      @JoinColumn(name = "standard_id", referencedColumnName = "standard_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", 
      column = @Column(name = "referenceDocumentation")) })
  public List<DcatProperty> getReferenceDocumentation() {
    return referenceDocumentation;
  }

  public void setReferenceDocumentation(List<DcatProperty> referenceDocumentation) {
    this.referenceDocumentation = referenceDocumentation;
  }

  //@Id
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

  /**
   * To doc.
   *
   * @param contentType the content type
   * @return the solr input document
   */
  public SolrInputDocument toDoc(CacheContentType contentType) {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", this.id);
    doc.addField("nodeID", this.nodeID);
    doc.addField("content_type", contentType.toString());
    doc.addField("uri", this.uri);
    doc.addField("identifier", this.getIdentifier().getValue());
    doc.addField("title", this.getTitle().getValue());
    doc.addField("description", this.getDescription().getValue());
    doc.addField("referenceDocumentation", 
        this.getReferenceDocumentation().stream().filter(item -> item != null)
        .map(item -> item.getValue()).collect(Collectors.toList()));
    return doc;
  }

  /**
   * Doc to dcat standard.
   *
   * @param doc the doc
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
   * @param array the array
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
      result.add(new DctStandard(obj.optString("uri"),
          obj.optString("identifier"), obj.optString("title"),
          obj.optString("description"),
          (obj.has("referenceDocumentation")) ? obj.getJSONArray("referenceDocumentation")
              .toList().stream().map(item ->
              ((String) item)).collect(Collectors.toList()) : Arrays.asList(""),
          nodeId));
    }

    return result;
  }

  @Override
  public String toString() {
    return "DCTStandard [uri=" + uri 
        + ", id=" + id + ", identifier=" + identifier + ", title=" + title
        + ", description=" + description 
        + ", referenceDocumentation=" + referenceDocumentation + "]";
  }

}
