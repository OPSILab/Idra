/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
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
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class SpdxChecksum.
 */
@Entity
@Table(name = "dcat_checksum")
public class SpdxChecksum {

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = ResourceFactory
      .createResource("http://spdx.org/rdf/terms#Checksum");

  /** The id. */
  private String id;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /** The uri. */
  private String uri;

  /** The algorithm. */
  private DcatProperty algorithm;

  /** The checksum value. */
  private DcatProperty checksumValue;

  /**
   * Instantiates a new spdx checksum.
   */
  public SpdxChecksum() {
  }

  /**
   * Instantiates a new spdx checksum.
   *
   * @param uri           the uri
   * @param algorithm     the algorithm
   * @param checksumValue the checksum value
   * @param nodeId        the node ID
   */
  public SpdxChecksum(String uri, String algorithm, String checksumValue, String nodeId) {
    super();
    this.nodeId = nodeId;
    setUri(uri);
    setAlgorithm(
        new DcatProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#algorithm"),
            ResourceFactory.createResource("http://spdx.org/rdf/terms#checksumAlgorithm_sha1"),
            algorithm));
    setChecksumValue(
        new DcatProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksumValue"),
            RDFS.Literal, checksumValue));
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "checksum_id")
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
   * Gets the uri.
   *
   * @return the uri
   */
  @Transient
  public String getUri() {
    return uri;
  }

  /**
   * Sets the uri.
   *
   * @param uri the new uri
   */
  public void setUri(String uri) {
    this.uri = StringUtils.isNotBlank(uri) ? uri : "http://spdx.org/rdf/terms#checksum";
  }

  /**
   * Gets the algorithm.
   *
   * @return the algorithm
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "algorithm")) })
  public DcatProperty getAlgorithm() {
    return algorithm;
  }

  /**
   * Sets the algorithm.
   *
   * @param algorithm the new algorithm
   */
  public void setAlgorithm(DcatProperty algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Gets the checksum value.
   *
   * @return the checksum value
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "checksumValue")) })
  public DcatProperty getChecksumValue() {
    return checksumValue;
  }

  /**
   * Sets the checksum value.
   *
   * @param checksumValue the new checksum value
   */
  public void setChecksumValue(DcatProperty checksumValue) {
    this.checksumValue = checksumValue;
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
    doc.addField("algorithm", this.algorithm != null ? this.algorithm.getValue() : "");
    doc.addField("checksumValue", this.checksumValue != null ? this.checksumValue.getValue() : "");
    return doc;
  }

  /**
   * Doc to spdx checksum.
   *
   * @param doc    the doc
   * @param uri    the uri
   * @param nodeId the node id
   * @return the spdx checksum
   */
  public static SpdxChecksum docToSpdxChecksum(SolrDocument doc, String uri, String nodeId) {
    SpdxChecksum c = new SpdxChecksum(uri, doc.getFieldValue("algorithm").toString(),
        doc.getFieldValue("checksumValue").toString(), nodeId);
    c.setId(doc.getFieldValue("id").toString());
    return c;

  }

  /**
   * Json to spdx checksum.
   *
   * @param doc    the doc
   * @param uri    the uri
   * @param nodeId the node id
   * @return the spdx checksum
   */
  public static SpdxChecksum jsonToSpdxChecksum(JSONObject doc, String uri, String nodeId) {
    return new SpdxChecksum(uri, doc.has("algorithm") ? doc.getString("algorithm") : "",
        doc.has("checksumValue") ? doc.getString("checksumValue") : "", nodeId);

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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SPDXChecksum [uri=" + uri + ", algorithm=" + algorithm + ", checksumValue="
        + checksumValue + "]";
  }

}
