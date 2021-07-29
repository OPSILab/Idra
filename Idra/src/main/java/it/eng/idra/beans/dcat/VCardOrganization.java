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

/**
 * Represents a DCAT VCard, the dataset contacts information.
 *
 * @author
 */

@Entity
@Table(name = "dcat_vcard")
public class VCardOrganization {

  private static final transient Resource RDFClass = VCARD4.Organization;

  private String id;

  private String resourceUri;
  private String propertyUri;
  private DcatProperty fn;
  private String nodeID;
  private DcatProperty hasEmail;
  private DcatProperty hasURL;
  private DcatProperty hasTelephoneValue;
  private DcatProperty hasTelephoneType;

  public VCardOrganization() {
  }

  /**
   * Instantiates a new v card organization.
   *
   * @param propertyUri the property uri
   * @param resourceUri the resource uri
   * @param fn the fn
   * @param hasEmail the has email
   * @param hasURL the has URL
   * @param hasTelephoneValue the has telephone value
   * @param hasTelephoneType the has telephone type
   * @param nodeID the node ID
   */
  public VCardOrganization(String propertyUri, 
      String resourceUri, 
      String fn, String hasEmail,
      String hasURL,
      String hasTelephoneValue, 
      String hasTelephoneType,
      String nodeID) {
    setResourceUri(resourceUri);
    setNodeID(nodeID);
    setPropertyUri(propertyUri);
    setFn(new DcatProperty(VCARD4.fn, RDFS.Literal, fn));
    hasEmail = (hasEmail != null) ? hasEmail.trim() : hasEmail;
    setHasEmail(new DcatProperty(VCARD4.hasEmail, RDFS.Literal, hasEmail));
    setHasURL(new DcatProperty(VCARD4.hasURL, RDFS.Literal, hasURL));
    setHasTelephoneValue(new DcatProperty(VCARD4.value, RDFS.Literal, hasTelephoneValue));
    setHasTelephoneType(new DcatProperty(RDF.type, RDFS.Literal, hasTelephoneType));
  }

  /**
   * Instantiates a new v card organization.
   *
   * @param id the id
   * @param propertyUri the property uri
   * @param resourceUri the resource uri
   * @param fn the fn
   * @param hasEmail the has email
   * @param hasURL the has URL
   * @param hasTelephoneValue the has telephone value
   * @param hasTelephoneType the has telephone type
   * @param nodeID the node ID
   */
  public VCardOrganization(String id, String propertyUri, 
      String resourceUri, String fn, String hasEmail, String hasURL,
      String hasTelephoneValue, String hasTelephoneType, String nodeID) {
    this(propertyUri, resourceUri, fn, hasEmail,
        hasURL, hasTelephoneValue, hasTelephoneType, nodeID);
    this.setId(id);
  }

  @Transient
  public static Resource getRDFClass() {
    return RDFClass;
  }

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "vcard_id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResourceUri() {
    return resourceUri;
  }

  public void setResourceUri(String resourceUri) {
    // this.resourceUri = resourceUri != null ? resourceUri : "";
    this.resourceUri = resourceUri;
  }

  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  @Transient
  public String getPropertyUri() {
    return propertyUri;
  }

  public void setPropertyUri(String propertUri) {
    this.propertyUri = propertUri != null ? propertUri : "";
  }

  @Embedded
  @AttributeOverrides({ 
      @AttributeOverride(name = "value", column = @Column(name = "fn", length = 500)) })
  public DcatProperty getFn() {
    return fn;
  }

  public void setFn(DcatProperty fn) {
    this.fn = fn;
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasEmail")) })
  public DcatProperty getHasEmail() {
    return hasEmail;
  }

  public void setHasEmail(DcatProperty hasEmail) {
    this.hasEmail = hasEmail;
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "hasURL")) })
  public DcatProperty getHasURL() {
    return hasURL;
  }

  public void setHasURL(DcatProperty hasURL) {
    this.hasURL = hasURL;
  }

  @Embedded
  @AttributeOverrides({ 
      @AttributeOverride(name = "value", column = @Column(name = "hasTelephoneValue")) })
  public DcatProperty getHasTelephoneValue() {
    return hasTelephoneValue;
  }

  public void setHasTelephoneValue(DcatProperty hasTelephone) {
    this.hasTelephoneValue = hasTelephone;
  }

  @Embedded
  @AttributeOverrides({ 
      @AttributeOverride(name = "value", column = @Column(name = "hasTelephoneType")) })

  public DcatProperty getHasTelephoneType() {
    return hasTelephoneType;
  }

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
    doc.addField("id", this.id);
    doc.addField("nodeID", this.nodeID);
    doc.addField("content_type", contentType.toString());
    doc.addField("resourceUri", this.resourceUri);
    doc.addField("fn", this.getFn().getValue());
    doc.addField("hasEmail", this.getHasEmail().getValue());
    doc.addField("hasURL", this.getHasURL().getValue());
    doc.addField("hasTelephoneValue", this.getHasTelephoneValue().getValue());
    doc.addField("hasTelephoneType", this.getHasTelephoneType().getValue());
    return doc;
  }

  /**
   * Doc to V card organization.
   *
   * @param doc the doc
   * @param propertyUri the property uri
   * @param nodeId the node ID
   * @return the v card organization
   */
  public static VCardOrganization docToVCardOrganization(SolrDocument doc,
      String propertyUri, String nodeId) {
    return new VCardOrganization((String) doc.getFieldValue("id"), propertyUri,
        (String) doc.getFieldValue("resourceUri"), doc.getFieldValue("fn").toString(),
        doc.getFieldValue("hasEmail").toString(), doc.getFieldValue("hasURL").toString(),
        doc.getFieldValue("hasTelephoneValue").toString(), 
        doc.getFieldValue("hasTelephoneType").toString(), nodeId);
  }

  @Override
  public String toString() {
    return "VCardOrganization [id=" + id + ", fn=" 
        + fn + ", nodeID=" + nodeID + ", hasEmail=" + hasEmail + ", hasURL="
        + hasURL + ", hasTelephone=" + hasTelephoneValue + "]";
  }

}
