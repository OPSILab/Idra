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

package it.eng.idra.beans;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class Datalet.
 */
@Entity
@Table(name = "distribution_datalet")
public class Datalet implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The id. */
  @Id
  // @GeneratedValue(generator = "uuid")
  // @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  /** The datalet html. */
  @Column(name = "datalet_html", columnDefinition = "LONGTEXT")
  @SerializedName(value = "datalet_html")
  private String dataletHtml;

  /** The title. */
  @Column(name = "title")
  private String title;

  /** The description. */
  @Column(name = "description")
  private String description;

  // This field is true when the title is created by Federation Manager otherwise
  /** The custom title. */
  // is false
  @Column(name = "customTitle")
  private boolean customTitle;

  /** The register date. */
  @Column(name = "register_date", updatable = false)
  // @Temporal(TemporalType.TIMESTAMP)
  private ZonedDateTime registerDate;

  /** The last seen date. */
  @Column(name = "last_seen_date", updatable = true)
  // @Temporal(TemporalType.TIMESTAMP)
  private ZonedDateTime lastSeenDate;

  /** The views. */
  @Column(name = "views")
  private int views;

  /** The dataset id. */
  @Column(name = "dataset_id")
  @SerializedName(value = "datasetID")
  private String datasetId;

  /** The node id. */
  @Column(name = "nodeID")
  @SerializedName(value = "nodeID")
  private String nodeId;

  /** The distribution id. */
  @Column(name = "distribution_id")
  @SerializedName(value = "distributionID")
  private String distributionId;

  /**
   * Instantiates a new datalet.
   */
  public Datalet() {
    super();
  }

  /**
   * Instantiates a new datalet.
   *
   * @param id             the id
   * @param dataletHtml    the datalet html
   * @param datasetId      the dataset ID
   * @param distributionId the distribution ID
   * @param nodeId         the node ID
   * @param customTitle    the custom title
   * @param registerDate   the register date
   * @param lastSeenDate   the last seen date
   * @param views          the views
   */
  public Datalet(String id, String dataletHtml, String datasetId, String distributionId,
      String nodeId, boolean customTitle, ZonedDateTime registerDate, ZonedDateTime lastSeenDate,
      int views) {
    super();
    this.id = id;
    this.dataletHtml = dataletHtml;
    this.datasetId = datasetId;
    this.datasetId = datasetId;
    this.nodeId = nodeId;
    this.distributionId = distributionId;
    this.customTitle = customTitle;
    this.registerDate = registerDate;
    this.lastSeenDate = lastSeenDate;
    this.views = views;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
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
   * Gets the distribution id.
   *
   * @return the distribution id
   */
  public String getDistributionId() {
    return distributionId;
  }

  /**
   * Sets the distribution id.
   *
   * @param distributionId the new distribution id
   */
  public void setDistributionId(String distributionId) {
    this.distributionId = distributionId;
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
   * Gets the datalet html.
   *
   * @return the datalet html
   */
  public String getDatalet_html() {
    return dataletHtml;
  }

  /**
   * Sets the datalet html.
   *
   * @param dataletHtml the new datalet html
   */
  public void setDatalet_html(String dataletHtml) {
    this.dataletHtml = dataletHtml;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the dataset id.
   *
   * @return the dataset id
   */
  public String getDatasetId() {
    return datasetId;
  }

  /**
   * Sets the dataset id.
   *
   * @param datasetId the new dataset id
   */
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  /**
   * Checks if is custom title.
   *
   * @return true, if is custom title
   */
  public boolean isCustomTitle() {
    return customTitle;
  }

  /**
   * Sets the custom title.
   *
   * @param customTitle the new custom title
   */
  public void setCustomTitle(boolean customTitle) {
    this.customTitle = customTitle;
  }

  /**
   * Gets the register date.
   *
   * @return the register date
   */
  public ZonedDateTime getRegisterDate() {
    return registerDate;
  }

  /**
   * Sets the register date.
   *
   * @param registerDate the new register date
   */
  public void setRegisterDate(ZonedDateTime registerDate) {
    this.registerDate = registerDate;
  }

  /**
   * Gets the last seen date.
   *
   * @return the last seen date
   */
  public ZonedDateTime getLastSeenDate() {
    return lastSeenDate;
  }

  /**
   * Sets the last seen date.
   *
   * @param lastSeenDate the new last seen date
   */
  public void setLastSeenDate(ZonedDateTime lastSeenDate) {
    this.lastSeenDate = lastSeenDate;
  }

  /**
   * Gets the views.
   *
   * @return the views
   */
  public int getViews() {
    return views;
  }

  /**
   * Sets the views.
   *
   * @param views the new views
   */
  public void setViews(int views) {
    this.views = views;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Datalet [id=" + id + ", \ndistID=" + distributionId + ", \ndatasetID=" + datasetId
        + ", \nnodeID=" + nodeId + "]";
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
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    Datalet other = (Datalet) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
