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
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
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
@Table(name = "dcat_periodoftime")
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

  // new
  /** The beginning. */
  private DcatProperty beginning;

  /** The end. */
  private DcatProperty end;

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
   * @param beginning the beginning
   * @param end       the end
   */
  public DctPeriodOfTime(String uri, String startDate, String endDate, String nodeId, String beginning, String end) {
    super();
    setUri(uri);
    setNodeId(nodeId);
    /*
     * setStartDate(new
     * DcatProperty(ResourceFactory.createProperty("http://schema.org#startDate"),
     * RDFS.Literal, startDate));
     * setEndDate(new
     * DcatProperty(ResourceFactory.createProperty("http://schema.org#endDate"),
     * RDFS.Literal, endDate));
     */
    setStartDate(new DcatProperty(DCAT.startDate, DCTerms.PeriodOfTime, startDate));
    setEndDate(new DcatProperty(DCAT.endDate, DCTerms.PeriodOfTime, endDate));
    // **New Fields Mapping**
    setBeginning(new DcatProperty(ResourceFactory.createProperty("https://www.w3.org/2006/time#hasBeginning"),
        RDFS.Literal, beginning));
    setEnd(new DcatProperty(ResourceFactory.createProperty("https://www.w3.org/2006/time#hasEnd"), RDFS.Literal,
        end));
  }

  /**
   * Instantiates a new dct period of time.
   *
   * @param uri       the uri
   * @param startDate the start date
   * @param endDate   the end date
   * @param nodeId    the node ID
   */
  public DctPeriodOfTime(String uri, DcatProperty startDate, DcatProperty endDate, String nodeId,
      DcatProperty beginning, DcatProperty end) {
    super();
    setUri(uri);
    setNodeId(nodeId);
    setStartDate(startDate);
    setEndDate(endDate);
    setBeginning(beginning);
    setEnd(end);
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

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "beginning")) })
  public DcatProperty getBeginning() {
    return beginning;
  }

  public void setBeginning(DcatProperty beginning) {
    this.beginning = beginning;
  }

  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "end")) })
  public DcatProperty getEnd() {
    return end;
  }

  public void setEnd(DcatProperty end) {
    this.end = end;
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
    if (this.id != null)
      doc.addField("id", this.id);
    if (this.nodeId != null)
      doc.addField("nodeID", this.nodeId);
    if (contentType.toString() != null)
      doc.addField("content_type", contentType.toString());
    doc.addField("startDate", this.startDate != null ? this.startDate.getValue() : "");
    doc.addField("endDate", this.endDate != null ? this.endDate.getValue() : "");
    doc.addField("beginning", this.beginning != null ? this.beginning.getValue() : "");
    doc.addField("end", this.end != null ? this.end.getValue() : "");
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
    DctPeriodOfTime p = new DctPeriodOfTime(uri,
        doc.getFieldValue("startDate") != null ? doc.getFieldValue("startDate").toString() : null,
        doc.getFieldValue("endDate") != null ? doc.getFieldValue("endDate").toString() : null, nodeId,
        doc.getFieldValue("beginning") != null ? doc.getFieldValue("beginning").toString() : null,
        doc.getFieldValue("end") != null ? doc.getFieldValue("end").toString() : null);
    p.setId(doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : "");
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
        + endDate + ", beginning=" + beginning + ", end=" + end + "]";
  }

}
