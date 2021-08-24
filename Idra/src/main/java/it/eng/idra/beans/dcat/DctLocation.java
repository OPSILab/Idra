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
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

// TODO: Auto-generated Javadoc
/**
 * The Class DctLocation.
 */
@Entity
@Table(name = "dcat_location")
public class DctLocation {

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = DCTerms.Location;

  /** The id. */
  private String id;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /** The uri. */
  private String uri;

  /** The geographical identifier. */
  private DcatProperty geographicalIdentifier;

  /** The geographical name. */
  private DcatProperty geographicalName;

  /** The geometry. */
  private DcatProperty geometry;

  /**
   * Instantiates a new dct location.
   */
  public DctLocation() {
  }

  /**
   * Instantiates a new dct location.
   *
   * @param uri                    the uri
   * @param geographicalIdentifier the geographical identifier
   * @param geographicalName       the geographical name
   * @param geometry               the geometry
   * @param nodeId                 the node ID
   */
  public DctLocation(String uri, String geographicalIdentifier, String geographicalName,
      String geometry, String nodeId) {
    super();
    setUri(uri);
    this.nodeId = nodeId;
    setGeographicalIdentifier(new DcatProperty(
        ResourceFactory.createProperty("http://dati.gov.it/onto/dcatapit#geographicalIdentifier"),
        RDFS.Literal, geographicalIdentifier));
    setGeographicalName(new DcatProperty(
        ResourceFactory.createProperty("http://www.w3.org/ns/locn#geographicalName"), SKOS.Concept,
        geographicalName));
    setGeometry(
        new DcatProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry"),
            ResourceFactory.createResource("https://www.w3.org/ns/locn#Geometry"), geometry));

  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "location_id")
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
   * Gets the geographical identifier.
   *
   * @return the geographical identifier
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "geographicalIdentifier")) })
  public DcatProperty getGeographicalIdentifier() {
    return geographicalIdentifier;
  }

  /**
   * Sets the geographical identifier.
   *
   * @param geographicalIdentifier the new geographical identifier
   */
  public void setGeographicalIdentifier(DcatProperty geographicalIdentifier) {
    this.geographicalIdentifier = geographicalIdentifier;
  }

  /**
   * Gets the geographical name.
   *
   * @return the geographical name
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "geographicalName")) })
  public DcatProperty getGeographicalName() {
    return geographicalName;
  }

  /**
   * Sets the geographical name.
   *
   * @param geographicalName the new geographical name
   */
  public void setGeographicalName(DcatProperty geographicalName) {
    this.geographicalName = geographicalName;
  }

  /**
   * Gets the geometry.
   *
   * @return the geometry
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "geometry")) })
  public DcatProperty getGeometry() {
    return geometry;
  }

  /**
   * Sets the geometry.
   *
   * @param geometry the new geometry
   */
  public void setGeometry(DcatProperty geometry) {
    this.geometry = geometry;
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
    doc.addField("geographicalIdentifier",
        this.geographicalIdentifier != null ? this.geographicalIdentifier.getValue() : "");
    doc.addField("geographicalName",
        this.geographicalName != null ? this.geographicalName.getValue() : "");
    doc.addField("geometry", this.geometry != null ? this.geometry.getValue() : "");
    return doc;

  }

  /**
   * Doc to dct location.
   *
   * @param doc    the doc
   * @param uri    the uri
   * @param nodeId the node id
   * @return the dct location
   */
  public static DctLocation docToDctLocation(SolrDocument doc, String uri, String nodeId) {
    DctLocation l = new DctLocation(uri, doc.getFieldValue("geographicalIdentifier").toString(),
        doc.getFieldValue("geographicalName").toString(), doc.getFieldValue("geometry").toString(),
        nodeId);
    l.setId(doc.getFieldValue("id").toString());
    return l;

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCTLocation [uri=" + uri + ", " + "geographicalIdentifier=" + geographicalIdentifier
        + ", geographicalName=" + geographicalName + ", geometry=" + geometry + "]";
  }

}
