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
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD4;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

// TODO: Auto-generated Javadoc
/**
 * Represents a DCAT VCard, the dataset contacts information.
 *
 * @author
 */

@Entity
@Table(name = "dcat_vcard")
public class VcardOrganization {

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = VCARD4.Organization;

  /** The id. */
  private String id;

  /** The resource uri. */
  private String resourceUri;

  /** The property uri. */
  private String propertyUri;

  /** The fn. */
  private DcatProperty fn;

  /** The node id. */
  @SerializedName(value = "nodeID")
  @Column(name = "nodeID")
  private String nodeId;

  /** The has email. */
  private DcatProperty hasEmail;

  /** The has url. */
  @SerializedName(value = "hasURL")
  private DcatProperty hasUrl;

  /** The has telephone value. */
  private DcatProperty hasTelephoneValue;

  /** The has telephone type. */
  private DcatProperty hasTelephoneType;

  /**
   * Instantiates a new v card organization.
   */
  public VcardOrganization() {
  }

  /**
   * Instantiates a new v card organization.
   *
   * @param propertyUri       the property uri
   * @param resourceUri       the resource uri
   * @param fn                the fn
   * @param hasEmail          the has email
   * @param hasUrl            the has URL
   * @param hasTelephoneValue the has telephone value
   * @param hasTelephoneType  the has telephone type
   * @param nodeId            the node ID
   * 
   */
  public VcardOrganization(String propertyUri, String resourceUri, String fn, String hasEmail,
      String hasUrl, String hasTelephoneValue, String hasTelephoneType, String nodeId) {
    setResourceUri(resourceUri);
    setNodeId(nodeId);
    setPropertyUri(propertyUri);
    setFn(new DcatProperty(VCARD4.fn, RDFS.Literal, fn));
    hasEmail = (hasEmail != null) ? hasEmail.trim() : hasEmail;
    setHasEmail(new DcatProperty(VCARD4.hasEmail, RDFS.Literal, hasEmail));
    setHasUrl(new DcatProperty(VCARD4.hasURL, RDFS.Literal, hasUrl));
    setHasTelephoneValue(new DcatProperty(VCARD4.value, RDFS.Literal, hasTelephoneValue));
    setHasTelephoneType(new DcatProperty(RDF.type, RDFS.Literal, hasTelephoneType));
  }

  /**
   * Instantiates a new v card organization.
   *
   * @param id                the id
   * @param propertyUri       the property uri
   * @param resourceUri       the resource uri
   * @param fn                the fn
   * @param hasEmail          the has email
   * @param hasUrl            the has URL
   * @param hasTelephoneValue the has telephone value
   * @param hasTelephoneType  the has telephone type
   * @param nodeId            the node ID
   */
  public VcardOrganization(String id, String propertyUri, String resourceUri, String fn,
      String hasEmail, String hasUrl, String hasTelephoneValue, String hasTelephoneType,
      String nodeId) {
    this(propertyUri, resourceUri, fn, hasEmail, hasUrl, hasTelephoneValue, hasTelephoneType,
        nodeId);
    this.setId(id);
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
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "vcard_id")
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
    // this.resourceUri = resourceUri != null ? resourceUri : "";
    this.resourceUri = resourceUri;
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
   * @param propertUri the new property uri
   */
  public void setPropertyUri(String propertUri) {
    this.propertyUri = propertUri != null ? propertUri : "";
  }

  /**
   * Gets the fn.
   *
   * @return the fn
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "fn", length = 500)) })
  public DcatProperty getFn() {
    return fn;
  }

  /**
   * Sets the fn.
   *
   * @param fn the new fn
   */
  public void setFn(DcatProperty fn) {
    this.fn = fn;
  }

  /**
   * Gets the checks for email.
   *
   * @return the checks for email
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasEmail")) })
  public DcatProperty getHasEmail() {
    return hasEmail;
  }

  /**
   * Sets the checks for email.
   *
   * @param hasEmail the new checks for email
   */
  public void setHasEmail(DcatProperty hasEmail) {
    this.hasEmail = hasEmail;
  }

  /**
   * Gets the checks for url.
   *
   * @return the checks for url
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasURL")) })
  public DcatProperty getHasUrl() {
    return hasUrl;
  }

  /**
   * Sets the checks for url.
   *
   * @param hasUrl the new checks for url
   */
  public void setHasUrl(DcatProperty hasUrl) {
    this.hasUrl = hasUrl;
  }

  /**
   * Gets the checks for telephone value.
   *
   * @return the checks for telephone value
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "hasTelephoneValue")) })
  public DcatProperty getHasTelephoneValue() {
    return hasTelephoneValue;
  }

  /**
   * Sets the checks for telephone value.
   *
   * @param hasTelephone the new checks for telephone value
   */
  public void setHasTelephoneValue(DcatProperty hasTelephone) {
    this.hasTelephoneValue = hasTelephone;
  }

  /**
   * Gets the checks for telephone type.
   *
   * @return the checks for telephone type
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "hasTelephoneType")) })

  public DcatProperty getHasTelephoneType() {
    return hasTelephoneType;
  }

  /**
   * Sets the checks for telephone type.
   *
   * @param hasTelephoneType the new checks for telephone type
   */
  public void setHasTelephoneType(DcatProperty hasTelephoneType) {
    this.hasTelephoneType = hasTelephoneType;
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
    if (this.getFn().getValue() != null)
      doc.addField("fn", this.getFn().getValue());
    if (this.getHasEmail().getValue() != null)
      doc.addField("hasEmail", this.getHasEmail().getValue());
    if (this.getHasUrl().getValue() != null)
      doc.addField("hasURL", this.getHasUrl().getValue());
    if (this.getHasTelephoneValue().getValue() != null)
      doc.addField("hasTelephoneValue", this.getHasTelephoneValue().getValue());
    if (this.getHasTelephoneType().getValue() != null)
      doc.addField("hasTelephoneType", this.getHasTelephoneType().getValue());
    return doc;
  }

  /**
   * Doc to V card organization.
   *
   * @param doc         the doc
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the v card organization
   */
  public static VcardOrganization docToVcardOrganization(SolrDocument doc, String propertyUri,
      String nodeId) {
    return new VcardOrganization(doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : null,
        propertyUri,
        doc.getFieldValue("resourceUri") != null ? doc.getFieldValue("resourceUri").toString() : null,
        doc.getFieldValue("fn") != null ? doc.getFieldValue("fn").toString() : null,
        doc.getFieldValue("hasEmail") != null ? doc.getFieldValue("hasEmail").toString() : null,
        doc.getFieldValue("hasURL") != null ? doc.getFieldValue("hasURL").toString() : null,
        doc.getFieldValue("hasTelephoneValue") != null ? doc.getFieldValue("hasTelephoneValue").toString() : null,
        doc.getFieldValue("hasTelephoneType") != null ? doc.getFieldValue("hasTelephoneType").toString() : null,
        nodeId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "VCardOrganization [id=" + id + ", fn=" + fn + ", nodeID=" + nodeId + ", hasEmail="
        + hasEmail + ", hasURL=" + hasUrl + ", hasTelephone=" + hasTelephoneValue + "]";
  }

}
