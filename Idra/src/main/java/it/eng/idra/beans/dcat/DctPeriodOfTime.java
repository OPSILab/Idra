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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

// TODO: Auto-generated Javadoc
/**
 * The Class DctPeriodOfTime.
 */
@Entity
@Table(name = "dcat_periodOfTime")
public class DctPeriodOfTime {

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = DCTerms.PeriodOfTime;

  /** The id. */
  private String id;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /** The uri. */
  private String uri;

  /** The start date. */
  private DcatProperty startDate;

  /** The end date. */
  private DcatProperty endDate;

  /**
   * Instantiates a new dct period of time.
   */
  public DctPeriodOfTime() {
  }

  /**
   * Instantiates a new dct period of time.
   *
   * @param uri       the uri
   * @param startDate the start date
   * @param endDate   the end date
   * @param nodeId    the node id
   */
  public DctPeriodOfTime(String uri, String startDate, String endDate, String nodeId) {
    super();
    setUri(uri);
    this.nodeId = nodeId;
    setStartDate(new DcatProperty(ResourceFactory.createProperty("http://schema.org#startDate"),
        RDFS.Literal, startDate));
    setEndDate(new DcatProperty(ResourceFactory.createProperty("http://schema.org#endDate"),
        RDFS.Literal, endDate));
  }

  /**
   * Instantiates a new dct period of time.
   *
   * @param uri       the uri
   * @param startDate the start date
   * @param endDate   the end date
   * @param nodeId    the node ID
   */
  public DctPeriodOfTime(String uri, DcatProperty startDate, DcatProperty endDate, String nodeId) {
    super();
    setUri(uri);
    this.nodeId = nodeId;
    setStartDate(startDate);
    setEndDate(endDate);
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "periodOfTime_id")
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
   * Gets the start date.
   *
   * @return the start date
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "startDate")) })
  public DcatProperty getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date.
   *
   * @param startDate the new start date
   */
  public void setStartDate(DcatProperty startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the end date.
   *
   * @return the end date
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "endDate")) })
  public DcatProperty getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date.
   *
   * @param endDate the new end date
   */
  public void setEndDate(DcatProperty endDate) {
    this.endDate = endDate;
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
    doc.addField("startDate", this.startDate != null ? this.startDate.getValue() : "");
    doc.addField("endDate", this.endDate != null ? this.endDate.getValue() : "");
    return doc;
  }

  /**
   * Doc to dct period of time.
   *
   * @param doc    the doc
   * @param uri    the uri
   * @param nodeId the node ID
   * @return the dct period of time
   */
  public static DctPeriodOfTime docToDctPeriodOfTime(SolrDocument doc, String uri, String nodeId) {
    DctPeriodOfTime p = new DctPeriodOfTime(uri, doc.getFieldValue("startDate").toString(),
        doc.getFieldValue("endDate").toString(), nodeId);
    p.setId(doc.getFieldValue("id").toString());
    return p;

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCTPeriodOfTime [id=" + id + ", uri=" + uri + ", startDate=" + startDate + ", endDate="
        + endDate + "]";
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

}
