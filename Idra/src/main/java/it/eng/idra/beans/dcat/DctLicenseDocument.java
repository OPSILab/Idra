/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
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
import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class DctLicenseDocument.
 */
@Entity
@Table(name = "dcat_licenseDocument")
public class DctLicenseDocument {

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = DCTerms.LicenseDocument;

  /** The id. */
  private String id;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private transient String nodeId;

  /** The uri. */
  private String uri;

  /** The name. */
  private DcatProperty name;

  /** The type. */
  private DcatProperty type;

  /** The version info. */
  private DcatProperty versionInfo;

  /**
   * Instantiates a new dct license document.
   */
  public DctLicenseDocument() {
  }

  /**
   * Instantiates a new dct license document.
   *
   * @param uri         the uri
   * @param name        the name
   * @param type        the type
   * @param versionInfo the version info
   * @param nodeId      the node ID
   */
  public DctLicenseDocument(String uri, String name, String type, String versionInfo,
      String nodeId) {

    setUri(uri);
    this.nodeId = nodeId;
    setName(new DcatProperty(FOAF.name, RDFS.Literal, name));
    setVersionInfo(new DcatProperty(OWL.versionInfo, RDFS.Literal, versionInfo));
    setType(new DcatProperty(DCTerms.type, SKOS.Concept, type));
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "licenseDocument_id")
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
   * Gets the name.
   *
   * @return the name
   */
  @Embedded
  @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "name")) })
  public DcatProperty getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(DcatProperty name) {
    this.name = name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    setName(new DcatProperty(FOAF.name, RDFS.Literal, name));
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
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(String type) {
    setType(new DcatProperty(DCTerms.type, SKOS.Concept, type));
  }

  /**
   * Gets the version info.
   *
   * @return the version info
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "versionInfo")) })
  public DcatProperty getVersionInfo() {
    return versionInfo;
  }

  /**
   * Sets the version info.
   *
   * @param versionInfo the new version info
   */
  public void setVersionInfo(DcatProperty versionInfo) {
    this.versionInfo = versionInfo;
  }

  /**
   * Sets the version info.
   *
   * @param versionInfo the new version info
   */
  public void setVersionInfo(String versionInfo) {
    setVersionInfo(new DcatProperty(OWL.versionInfo, RDFS.Literal, versionInfo));
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
    this.uri = StringUtils.isNotBlank(uri) ? uri : DCTerms.license.getURI();
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
    doc.addField("uri", StringUtils.isNotBlank(this.uri) ? this.uri : "");
    doc.addField("name", this.name != null ? this.name.getValue() : "");
    doc.addField("type", this.type != null ? this.type.getValue() : "");
    doc.addField("versionInfo", this.versionInfo != null ? this.versionInfo.getValue() : "");
    return doc;

  }

  /**
   * Json to DCT license document.
   *
   * @param json   the json
   * @param nodeId the node ID
   * @return the dct license document
   */
  public static DctLicenseDocument jsonToDctLicenseDocument(JSONObject json, String nodeId) {
    return new DctLicenseDocument(json.optString("uri"), json.optString("name"),
        json.optString("type"), json.optString("versionInfo"), nodeId);
  }

  /**
   * Doc to DCT license document.
   *
   * @param doc    the doc
   * @param nodeId the node ID
   * @return the dct license document
   */
  public static DctLicenseDocument docToDctLicenseDocument(SolrDocument doc, String nodeId) {
    return jsonToDctLicenseDocument(new JSONObject(doc.getFieldValue("license").toString()),
        nodeId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCTLicenseDocument [id=" + id + ", uri=" + uri + ", name=" + name + ", type=" + type
        + ", versionInfo=" + versionInfo + "]";
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DctLicenseDocument other = (DctLicenseDocument) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (uri == null) {
      if (other.uri != null) {
        return false;
      }
    } else if (!uri.equals(other.uri)) {
      return false;
    }
    return true;
  }

}
