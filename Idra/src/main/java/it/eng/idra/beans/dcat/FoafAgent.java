/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2025 Engineering Ingegneria Informatica S.p.A.
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
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * Represents a DCAT Agent, the Dataset publisher.
 *
 * @author
 */

@Entity
@Table(name = "dcat_agent")
public class FoafAgent implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The id. */
  private String id;

  /** The name. */
  // Mandatory
  // private DcatProperty name; //check, need to be list u dcat-apv3
  private List<DcatProperty> name;

  // Recommended

  /** The resource uri. */
  private String resourceUri;

  /** The property uri. */
  private String propertyUri;

  /** The mbox. */
  private DcatProperty mbox;

  /** The homepage. */
  private DcatProperty homepage;

  /** The type. */
  private DcatProperty type;

  /** The identifier. */
  private DcatProperty identifier;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /**
   * Instantiates a new foaf agent.
   */
  public FoafAgent() {
  }

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = FOAF.Agent;

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

  /**
   * Instantiates a new foaf agent.
   *
   * @param propertyUri the property uri
   * @param resourceUri the resource uri
   * @param names       the names
   * @param mbox        the mbox
   * @param homepage    the homepage
   * @param type        the type
   * @param identifier  the identifier
   * @param nodeId      the node ID
   */
  public FoafAgent(String propertyUri, String resourceUri, List<String> names, String mbox,
      String homepage, String type, String identifier, String nodeId) {

    setNodeId(nodeId);
    setPropertyUri(propertyUri);
    setResourceUri(resourceUri);
    // setName(new DcatProperty(FOAF.name, RDFS.Literal, name));
    setName(names == null ? Arrays.asList(new DcatProperty(FOAF.name, RDFS.Literal, ""))
        : names.stream()
            .map(name -> new DcatProperty(FOAF.name, RDFS.Literal, name))
            .collect(Collectors.toList()));
    setMbox(new DcatProperty(FOAF.mbox, RDFS.Literal, mbox));
    setHomepage(new DcatProperty(FOAF.homepage, RDFS.Literal, homepage));
    setType(new DcatProperty(DCTerms.type, SKOS.Concept, type));
    setIdentifier(new DcatProperty(DCTerms.identifier, RDFS.Literal, identifier));

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
  @Column(name = "agent_id")
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
  @Column(name = "nodeID")
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

  // @Embedded
  // @AttributeOverrides({ @AttributeOverride(name = "value", column =
  // @Column(name = "name")) })
  // public DcatProperty getName() {
  // return name;
  // }

  // public void setName(DcatProperty name) {
  // if (name != null) {
  // this.name = name;
  // }
  // }

  /**
   * Gets the list of names.
   *
   * @return the list of names
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "dcat_agent_name", joinColumns = {
      @JoinColumn(name = "agent_id", referencedColumnName = "agent_id"),
      @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "name")) })
  public List<DcatProperty> getName() {
    return name;
  }

  /**
   * Sets the list of names.
   *
   * @param names the new list of names
   */
  public void setName(List<DcatProperty> names) {
    if (names != null) {
      this.name = names;
    }
  }

  /**
   * Gets the mbox.
   *
   * @return the mbox
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "mbox")) })
  public DcatProperty getMbox() {
    return mbox;
  }

  /**
   * Sets the mbox.
   *
   * @param mbox the new mbox
   */
  public void setMbox(DcatProperty mbox) {
    this.mbox = mbox;
  }

  /**
   * Gets the homepage.
   *
   * @return the homepage
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "homepage")) })
  public DcatProperty getHomepage() {
    return homepage;
  }

  /**
   * Sets the homepage.
   *
   * @param homepage the new homepage
   */
  public void setHomepage(DcatProperty homepage) {
    this.homepage = homepage;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "type")) })
  public DcatProperty getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(DcatProperty type) {
    this.type = type;
  }

  /**
   * Gets the identifier.
   *
   * @return the identifier
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "identifier", columnDefinition = "LONGTEXT")) })
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
   * To doc.
   *
   * @param contentType the content type
   * @return the solr input document
   */
  public SolrInputDocument toDoc(CacheContentType contentType) {

    SolrInputDocument doc = new SolrInputDocument();
    if (this.id != null)
      doc.addField("id", this.id);
    if (this.nodeId != null)
      doc.addField("nodeID", this.nodeId);
    if (contentType.toString() != null)
      doc.addField("content_type", contentType.toString());
    if (this.resourceUri != null)
      doc.addField("resourceUri", this.resourceUri);
    if (this.getIdentifier().getValue() != null)
      doc.addField("identifier", this.getIdentifier().getValue());
    // doc.addField("name", this.getName().getValue());
    if (this.getName() != null && !this.getName().isEmpty()) {
      doc.addField("nameList", this.getName().stream().filter(item -> item != null)
          .map(item -> item.getValue()).collect(Collectors.toList()));
    }

    if (this.getMbox().getValue() != null)
      doc.addField("mbox", this.getMbox().getValue());
    if (this.getHomepage().getValue() != null)
      doc.addField("homepage", this.getHomepage().getValue());
    if (this.getType().getValue() != null)
      doc.addField("type", this.getType().getValue());

    return doc;
  }

  /**
   * Doc to foaf agent.
   *
   * @param doc         the doc
   * @param propertyUri the property uri
   * @param nodeId      the node id
   * @return the foaf agent
   */
  public static FoafAgent docToFoafAgent(SolrDocument doc, String propertyUri, String nodeId) {
    // Extract name field as a list of strings
    FoafAgent f = new FoafAgent(propertyUri,
        doc.getFieldValue("resourceUri") != null ? doc.getFieldValue("resourceUri").toString() : null,
        (ArrayList<String>) doc.getFieldValue("nameList"),
        doc.getFieldValue("mbox") != null ? doc.getFieldValue("mbox").toString() : null,
        doc.getFieldValue("homepage") != null ? doc.getFieldValue("homepage").toString() : null,
        doc.getFieldValue("type") != null ? doc.getFieldValue("type").toString() : null,
        doc.getFieldValue("identifier") != null ? doc.getFieldValue("identifier").toString() : null, nodeId);
    f.setId(doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : "");
    return f;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FOAFAgent [id=" + id + ", nameList=" + name + ", propertyUri=" + propertyUri + ", mbox="
        + mbox + ", homepage=" + homepage + ", type=" + type + ", identifier=" + identifier
        + ", nodeID=" + nodeId + "]";
  }

}
