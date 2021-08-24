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

package it.eng.idra.beans.statistics;

import com.google.gson.annotations.SerializedName;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsStatistics.
 */
@SqlResultSetMapping(name = "ODMSStatisticsResult", classes = {
    @ConstructorResult(targetClass = OdmsStatisticsResult.class, columns = {
        @ColumnResult(name = "added", type = Integer.class),
        @ColumnResult(name = "deleted", type = Integer.class),
        @ColumnResult(name = "updated", type = Integer.class),
        @ColumnResult(name = "added_RDF", type = Integer.class),
        @ColumnResult(name = "deleted_RDF", type = Integer.class),
        @ColumnResult(name = "updated_RDF", type = Integer.class) }) })

@Entity
@Table(name = "odms_statistics")
public class OdmsStatistics {

  /** The id. */
  private int id;

  /** The node id. */
  @SerializedName(value = "nodeID")
  private int nodeId;

  /** The name. */
  private String name;

  /** The day. */
  private int day;

  /** The month. */
  private int month;

  /** The year. */
  private int year;

  /** The added datasets. */
  @SerializedName(value = "added_datasets")
  private int addedDatasets;

  /** The updated datasets. */
  @SerializedName(value = "updated_datasets")
  private int updatedDatasets;

  /** The deleted datasets. */
  @SerializedName(value = "deleted_datasets")
  private int deletedDatasets;

  /** The added rdf. */
  @SerializedName(value = "added_RDF")
  private int addedRdf;

  /** The updated rdf. */
  @SerializedName(value = "updated_RDF")
  private int updatedRdf;

  /** The deleted rdf. */
  @SerializedName(value = "deleted_RDF")
  private int deletedRdf;

  /**
   * Instantiates a new odms statistics.
   */
  public OdmsStatistics() {

  }

  /**
   * Instantiates a new odms statistics.
   *
   * @param nodeId          the node ID
   * @param name            the name
   * @param day             the day
   * @param month           the month
   * @param year            the year
   * @param addedDatasets   the added datasets
   * @param updatedDatasets the updated datasets
   * @param deletedDatasets the deleted datasets
   * @param addedRdf        the added RDF
   * @param updatedRdf      the updated RDF
   * @param deletedRdf      the deleted RDF
   */
  public OdmsStatistics(int nodeId, String name, int day, int month, int year, int addedDatasets,
      int updatedDatasets, int deletedDatasets, int addedRdf, int updatedRdf, int deletedRdf) {
    super();
    this.nodeId = nodeId;
    this.name = name;
    this.day = day;
    this.month = month;
    this.year = year;
    this.addedDatasets = addedDatasets;
    this.updatedDatasets = updatedDatasets;
    this.deletedDatasets = deletedDatasets;
    this.addedRdf = addedRdf;
    this.updatedRdf = updatedRdf;
    this.deletedRdf = deletedRdf;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  @Column(name = "nodeID")
  public int getNodeId() {
    return nodeId;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  @Column(name = "name")
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the day.
   *
   * @return the day
   */
  @Column(name = "day")
  public int getDay() {
    return day;
  }

  /**
   * Sets the day.
   *
   * @param day the new day
   */
  public void setDay(int day) {
    this.day = day;
  }

  /**
   * Gets the month.
   *
   * @return the month
   */
  @Column(name = "month")
  public int getMonth() {
    return month;
  }

  /**
   * Sets the month.
   *
   * @param month the new month
   */
  public void setMonth(int month) {
    this.month = month;
  }

  /**
   * Gets the year.
   *
   * @return the year
   */
  @Column(name = "year")
  public int getYear() {
    return year;
  }

  /**
   * Sets the year.
   *
   * @param year the new year
   */
  public void setYear(int year) {
    this.year = year;
  }

  /**
   * Gets the added datasets.
   *
   * @return the added datasets
   */
  @Column(name = "added_datasets")
  public int getAddedDatasets() {
    return addedDatasets;
  }

  /**
   * Sets the added datasets.
   *
   * @param addedDatasets the new added datasets
   */
  public void setAddedDatasets(int addedDatasets) {
    this.addedDatasets = addedDatasets;
  }

  /**
   * Inc added datasets.
   *
   * @param addedDatasets the added datasets
   */
  public void incAddedDatasets(int addedDatasets) {
    this.addedDatasets += addedDatasets;
  }

  /**
   * Gets the updated datasets.
   *
   * @return the updated datasets
   */
  @Column(name = "updated_datasets")
  public int getUpdatedDatasets() {
    return updatedDatasets;
  }

  /**
   * Sets the updated datasets.
   *
   * @param updatedDatasets the new updated datasets
   */
  public void setUpdatedDatasets(int updatedDatasets) {
    this.updatedDatasets = updatedDatasets;
  }

  /**
   * Inc updated datasets.
   *
   * @param updatedDatasets the updated datasets
   */
  public void incUpdatedDatasets(int updatedDatasets) {
    this.updatedDatasets += updatedDatasets;
  }

  /**
   * Gets the deleted datasets.
   *
   * @return the deleted datasets
   */
  @Column(name = "deleted_datasets")
  public int getDeletedDatasets() {
    return deletedDatasets;
  }

  /**
   * Sets the deleted datasets.
   *
   * @param deletedDatasets the new deleted datasets
   */
  public void setDeletedDatasets(int deletedDatasets) {
    this.deletedDatasets = deletedDatasets;
  }

  /**
   * Inc deleted datasets.
   *
   * @param deletedDatasets the deleted datasets
   */
  public void incDeletedDatasets(int deletedDatasets) {
    this.deletedDatasets += deletedDatasets;
  }

  /**
   * Gets the added rdf.
   *
   * @return the added rdf
   */
  @Column(name = "added_RDF")
  public int getAddedRdf() {
    return addedRdf;
  }

  /**
   * Sets the added rdf.
   *
   * @param addedRdf the new added rdf
   */
  public void setAddedRdf(int addedRdf) {
    this.addedRdf = addedRdf;
  }

  /**
   * Gets the updated rdf.
   *
   * @return the updated rdf
   */
  @Column(name = "updated_RDF")
  public int getUpdatedRdf() {
    return updatedRdf;
  }

  /**
   * Inc added rdf.
   *
   * @param addedRdf the added rdf
   */
  public void incAddedRdf(int addedRdf) {
    this.addedRdf += addedRdf;
  }

  /**
   * Sets the updated rdf.
   *
   * @param updatedRdf the new updated rdf
   */
  public void setUpdatedRdf(int updatedRdf) {
    this.updatedRdf = updatedRdf;
  }

  /**
   * Gets the deleted rdf.
   *
   * @return the deleted rdf
   */
  @Column(name = "deleted_RDF")
  public int getDeletedRdf() {
    return deletedRdf;
  }

  /**
   * Inc updated rdf.
   *
   * @param updatedRdf the updated rdf
   */
  public void incUpdatedRdf(int updatedRdf) {
    this.updatedRdf += updatedRdf;
  }

  /**
   * Sets the deleted rdf.
   *
   * @param deletedRdf the new deleted rdf
   */
  public void setDeletedRdf(int deletedRdf) {
    this.deletedRdf = deletedRdf;
  }

  /**
   * Inc deleted rdf.
   *
   * @param deletedRdf the deleted rdf
   */
  public void incDeletedRdf(int deletedRdf) {
    this.deletedRdf += deletedRdf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ODMSStatistics [id=" + id + ", nodeID=" + nodeId + ", day=" + day + ", month=" + month
        + ", year=" + year + "]";
  }

}
